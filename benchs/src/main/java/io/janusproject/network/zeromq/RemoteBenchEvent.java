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

import io.sarl.lang.core.Event;

import java.io.Serializable;

/** Implementation of an event that is
 * going over the network during the benchmarking process.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
class RemoteBenchEvent extends Event implements Serializable {

	private static final long serialVersionUID = -3831445504743195115L;
	
	private final long sendingTime;
	
	private transient long arrivalTime;

	/**
	 */
	public RemoteBenchEvent() {
		this.sendingTime = System.nanoTime();
	}
	
	/** Replies the nano time at which this message was sent.
	 * 
	 * @return the sending time.
	 */
	public long getSendingTime() {
		return this.sendingTime;
	}
	
	/** Replies the nano time at which this message was arrived.
	 * 
	 * @return the sending time.
	 */
	public long getArrivalTime() {
		return this.arrivalTime;
	}

	/** Set the time of arrival of the message.
	 * 
	 * @param time
	 */
	public void setArrivalTime(long time) {
		this.arrivalTime = time;
	}

}