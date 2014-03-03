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

import io.janusproject.kernel.DistributedSpace;
import io.janusproject.kernel.Network;
import io.janusproject.repository.JanusConfig;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.SpaceID;
import io.sarl.util.AddressScope;
import io.sarl.util.OpenEventSpaceSpecification;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.arakhne.afc.vmutil.locale.Locale;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/** Abstract implementation of a ZeroMQ message replier.
 * <p>
 * CLI parameters are: <ul>
 * <li><code>-help</code>: show the help.</li>
 * <li><code>-java</code>: Use the Java serializer.</li>
 * <li><code>-gson</code>: Use the GSon serializer (default).</li>
 * <li><code>-aes</code>: Use the encrypter with AES algorithm (default).</li>
 * <li><code>-plain</code>: Do not use encrypter.</li>
 * </ul> 
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class Replier {

	/** Network connection. */
	private final Network network;
	
	private DSpace dspace = null;
	
	/**
	 * @param module
	 */
	private Replier(Module module) {
		Injector injector = Guice.createInjector(module);
		this.network = injector.getInstance(Network.class);
	}
	
	private static Inet4Address getPrimaryAdapter() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if (interfaces!=null) {
				NetworkInterface inter;
				while (interfaces.hasMoreElements()) {
					inter = interfaces.nextElement();
					Enumeration<InetAddress> e = inter.getInetAddresses();
					while (e.hasMoreElements()) {
						InetAddress a = e.nextElement();
						if (a instanceof Inet4Address) return (Inet4Address)a;
					}
				}
			}
		}
		catch(SocketException _) {
			//
		}
		return null;
	}
	
	private void start() throws Exception {
		this.dspace = new DSpace();
		this.network.startAsync();
		this.network.awaitTerminated();
		this.network.register(this.dspace);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CommandLineParser parser = new BasicParser();

		Options options = new Options();
		options.addOption("h", "help", false, "Help"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		options.addOption("j", "java", false, "Serializer is Java"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		options.addOption("g", "gson", false, "Serializer is Gson"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		options.addOption("a", "aes", false, "Encrypter is AES"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
		options.addOption("p", "plain", false, "No encrypter"); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		Class<? extends EventSerializer> serialType = GsonEventSerializer.class;
		Class<? extends EventEncrypter> encrypType = AESEventEncrypter.class;
		
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption('h')) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Replier.class.getName()+" [OPTIONS]", options); //$NON-NLS-1$
			System.exit(0);
			return;
		}

		if (cmd.hasOption('j')) {
			serialType = JavaBinaryEventSerializer.class;
		}
		else if (cmd.hasOption('g')) {
			serialType = GsonEventSerializer.class;
		}
		
		if (cmd.hasOption('a')) {
			encrypType = AESEventEncrypter.class;
		}
		else if (cmd.hasOption('p')) {
			encrypType = PlainTextEncrypter.class;
		}

		String pubUri = "tcp://"+getPrimaryAdapter().getHostAddress()+":"+ZMQConstants.REPLIER_PORT; //$NON-NLS-1$ //$NON-NLS-2$
		System.err.println("PUB SOCKET:"+pubUri); //$NON-NLS-1$
		System.setProperty(ZeroMQConfig.PUB_URI, pubUri);

		Replier r = new Replier(new BenchmarkingModule(serialType, encrypType));
		
		r.start();
	}
	
	/**
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 2.0.0
	 */
	public class DSpace implements DistributedSpace {
		
		private final SpaceID spaceId;
		private final Address address;
		
		/**
		 */
		public DSpace() {
			this.spaceId = new SpaceID(
					UUID.fromString(JanusConfig.DEFAULT_JANUS_CONTEXT_ID),
					UUID.fromString(JanusConfig.DEFAULT_JANUS_SPACE_ID),
					OpenEventSpaceSpecification.class);
			this.address = new Address(
					this.spaceId,
					UUID.fromString(ZMQConstants.REPLIER_ADDRESS));
		}

		@Override
		public SpaceID getID() {
			return this.spaceId;
		}

		@SuppressWarnings({ "unchecked", "synthetic-access" })
		@Override
		public void recv(Scope<?> scope, Event envelope) {
			try {
				if (scope==null || ((Scope<Address>)scope).matches(this.address)) {
					Address src = envelope.getSource();
					System.out.println(Locale.getString("LOG_MESSAGE", src, envelope));  //$NON-NLS-1$
					envelope.setSource(this.address);
					
					Replier.this.network.publish(this.spaceId,
							AddressScope.getScope(src),
							envelope);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	} /* class DSpace */
	
}