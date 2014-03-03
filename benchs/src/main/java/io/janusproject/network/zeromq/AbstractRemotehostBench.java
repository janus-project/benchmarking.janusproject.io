/*
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on http://www.janusproject.io
 * 
 * Copyright (C) 2014 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.janusproject.network.zeromq;

import io.janusproject.benchmarking.CsvBench;
import io.janusproject.benchmarking.PropertyBench;
import io.janusproject.kernel.DistributedSpace;
import io.janusproject.kernel.Network;
import io.janusproject.repository.JanusConfig;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.SpaceID;
import io.sarl.util.OpenEventSpaceSpecification;
import io.sarl.util.Scopes;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import org.arakhne.afc.vmutil.locale.Locale;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/** Abstract implementation of a benchmarking tool for the ZeroMQ layer on two hosts.
 * <p>
 * It is assumed that the {@link Replier} is launched with the correct command-line
 * parameters.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
@PropertyBench(names={"REMOTE_HOST"})
public abstract class AbstractRemotehostBench extends CsvBench<RemoteBenchRun> {

	/** Sender. */
	@Inject
	private Network network;

	/** Identifier of the space. */
	private SpaceID spaceId;

	/**
	 * @param directory - the directory that shold contains the CSV file.
	 * @param title - the title of the benchmarks.
	 * @throws IOException
	 */
	public AbstractRemotehostBench(File directory, String title) throws IOException {
		super(directory, title,
				Locale.getString("COLUMN_ROW_INDEX"), //$NON-NLS-1$
				Locale.getString("COLUMN_DURATION"), //$NON-NLS-1$
				Locale.getString("COLUMN_STANDARD_DEVIATION"), //$NON-NLS-1$
				Locale.getString("COLUMN_OS_LOAD_AVERAGE")); //$NON-NLS-1$
	}

	@Override
	protected Collection<RemoteBenchRun> determineRuns(String benchFunctionName) {
		return Collections.singleton(new RemoteBenchRun(benchFunctionName));
	}

	/** Replies the injection module to use.
	 * 
	 * @return the module.
	 */
	protected abstract Module getInjectionModule();

	@Override
	public void initialize() throws Exception {
		super.initialize();

		System.setProperty(ZeroMQConfig.PUB_URI, ZMQConstants.LOCALHOST_SOURCE_PEER);
		Injector injector = Guice.createInjector(getInjectionModule());

		this.network = injector.getInstance(Network.class);

		this.spaceId = new SpaceID(
				UUID.fromString(JanusConfig.DEFAULT_JANUS_CONTEXT_ID),
				UUID.fromString(JanusConfig.DEFAULT_JANUS_SPACE_ID),
				OpenEventSpaceSpecification.class);

		this.network.startAsync();

		this.network.awaitRunning();

		this.network.connectPeer(System.getProperty("REMOTE_HOST")); //$NON-NLS-1$

		this.network.register(new DSpace());

		setNumberOfCalls(1); // Call the "bench" functions once time
		setNumberOfRuns(200); // Generate multi rows in the CSV
	}

	@Override
	public void onEndRunOnMany(int nbRuns, RemoteBenchRun run) throws Exception {
		// Wait until all the messages were arrived
		long startTime = System.currentTimeMillis();
		while ((System.currentTimeMillis()-startTime)<=ZMQConstants.NETWORK_TIMEOUT
				&& run.isWaitingEvent()) {
			Thread.yield();
		}
		super.onEndRunOnMany(nbRuns, run);
	}

	@Override
	public void onEndDeclaredBenchFunction() throws Exception {
		int i=0;
		for(RemoteBenchRun r : getTerminatedRuns()) {
			writeRecord(
					i,
					r.getCallAverageDuration(),
					r.getCallStandardDeviation(),
					getSystemLoadAverage());
			++i;
		}
		super.onEndDeclaredBenchFunction();
	}

	@Override
	public void dispose() throws Exception {
		this.network.stopAsync();
		this.network = null;
		this.spaceId = null;
		super.dispose();
	}

	/** Send a message over the network.
	 * 
	 * @throws Exception
	 */
	protected void send() throws Exception {
		getCurrentRun().incrementEventCounter();
		Event event = new RemoteBenchEvent();
		this.network.publish(this.spaceId, Scopes.allParticipants(), event);
	}

	private void onReceived(long receivingTime, RemoteBenchEvent event) {
		event.setArrivalTime(receivingTime);
		getCurrentRun().decrementEventCounter(event);
	}

	/**
	 * @throws Exception
	 */
	public void benchPublish() throws Exception {
		send();
	}

	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 2.0.0
	 */
	public class DSpace implements DistributedSpace {

		private final UUID address;

		/**
		 */
		public DSpace() {
			this.address = UUID.fromString(ZMQConstants.SENDER_ADDRESS);
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public SpaceID getID() {
			return AbstractRemotehostBench.this.spaceId;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void recv(Scope<?> scope, Event envelope) {
			Address src = envelope.getSource();
			if (src!=null && !this.address.equals(src.getUUID())) {
				onReceived(System.nanoTime(), (RemoteBenchEvent)envelope);
			}
		}

	} /* class DSpace */

}