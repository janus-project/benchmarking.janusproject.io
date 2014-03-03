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



/** Abstract implementation of a benchmarking tool for the ZeroMQ layer.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
interface ZMQConstants {

	/** URI of the source peer on localhost.
	 */
	public final String LOCALHOST_SOURCE_PEER = "tcp://localhost:29118"; //$NON-NLS-1$
	
	/** URI of the target peer on localhost.
	 */
	public final String LOCALHOST_TARGET_PEER = "tcp://localhost:19118"; //$NON-NLS-1$;
	
	/** Address of the sender.
	 */
	public final String SENDER_ADDRESS = "04376c83-6fb8-4b7c-9196-94c32a58bdeb"; //$NON-NLS-1$

	/** Address of the replier.
	 */
	public final String REPLIER_ADDRESS = "47a209b0-39a7-4fe9-b9fb-07a82a6e48f9"; //$NON-NLS-1$

	/** Port of the replier.
	 */
	public final int REPLIER_PORT = 24316;

	/** Timeout used to assumed that messages were lost over the network.
	 */
	public final long NETWORK_TIMEOUT = 5*60*1000;
	
}