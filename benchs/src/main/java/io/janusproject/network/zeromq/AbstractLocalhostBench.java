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

import io.janusproject.benchmarking.BenchRun;
import io.janusproject.benchmarking.CsvBench;
import io.janusproject.kernel.Network;
import io.janusproject.kernel.Scopes;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.SpaceID;
import io.sarl.util.OpenEventSpaceSpecification;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;

/** Abstract implementation of a benchmarking tool for the ZeroMQ layer on a single host.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public abstract class AbstractLocalhostBench extends CsvBench<BenchRun> {

	/** Sender. */
	@Inject
	protected Network networkSource;

	/** Receiver. */
	@Inject
	protected Network networkTarget;

	/** Identifier of the space. */
	protected SpaceID spaceId;
	
	/** Scope. */
	protected Scope<?> scope;
	
	/** Context identifier. */
	protected UUID contextId;
	
	/** Identifier of the space. */
	protected UUID id;

	/** Default event to send. */
	protected Event defaultEvent;
	
	/**
	 * @param directory is the directory that shold contains the CSV file.
	 * @throws IOException
	 */
	public AbstractLocalhostBench(File directory) throws IOException {
		super(directory,
				"Order", //$NON-NLS-1$
				"Name", //$NON-NLS-1$
				"Global Duration (ns)", //$NON-NLS-1$
				"Operation Duration (ns)", //$NON-NLS-1$
				"Operation Standard Deviation", //$NON-NLS-1$
				"OS Load Average"); //$NON-NLS-1$
	}

	@Override
	protected Collection<BenchRun> determineRuns(String benchFunctionName) {
		return Collections.singleton(new BenchRun(benchFunctionName));
	}
	
	/** Replies the injection module to use.
	 * 
	 * @return the module.
	 */
	protected abstract Module getInjectionModule();

	@Override
	public void initialize() throws Exception {
		super.initialize();
		
		System.setProperty(ZeroMQConfig.PUB_URI, Constants.LOCALHOST_SOURCE_PEER);
		Injector injector = Guice.createInjector(getInjectionModule());
		
		this.networkSource = injector.getInstance(Network.class);

		System.setProperty(ZeroMQConfig.PUB_URI, Constants.LOCALHOST_TARGET_PEER);
		injector = Guice.createInjector(
				new BenchmarkingModule(
						JavaBinaryEventSerializer.class,
						PlainTextEncrypter.class));
		this.networkTarget = injector.getInstance(Network.class);

		this.scope = Scopes.nullScope();
		this.contextId = UUID.randomUUID();
		this.id = UUID.randomUUID();
		this.spaceId = new SpaceID(
				this.contextId,
				this.id,
				OpenEventSpaceSpecification.class);
		this.defaultEvent = NoEvent.INSTANCE;

		this.networkTarget.startAsync();
		this.networkSource.startAsync();
		
		this.networkSource.awaitRunning();
		this.networkTarget.awaitRunning();
		
		this.networkSource.connectPeer(Constants.LOCALHOST_TARGET_PEER);
		
		setNumberOfCalls(1); // Call the "bench" functions once time
		setNumberOfRuns(2); // Generate multi rows in the CSV
	}

	@Override
	public void onEndDeclaredBenchFunction() throws Exception {
		int i=0;
		for(BenchRun r : getTerminatedRuns()) {
			writeRecord(
				i,
				r.getName(),
				r.getRunDuration(),
				r.getCallAverageDuration(),
				r.getCallStandardDeviation(),
				getSystemLoadAverage());
			++i;
		}
		super.onEndDeclaredBenchFunction();
	}
	
	@Override
	public void dispose() throws Exception {
		this.networkSource.stopAsync();
		this.networkTarget.stopAsync();
		this.networkSource = this.networkTarget = null;
		this.defaultEvent = null;
		this.spaceId = null;
		this.scope = null;
		this.contextId = this.id = null;
		super.dispose();
	}
	
}