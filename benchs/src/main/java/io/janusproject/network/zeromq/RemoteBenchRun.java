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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Implementation of a run for the
 * benchmaking with remote hosts.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
class RemoteBenchRun extends BenchRun {

	private final List<RemoteBenchEvent> events = new ArrayList<>(100);
	private final AtomicInteger awaitingMessages = new AtomicInteger();
	
	private double averageDuration = Double.NaN;
	private double standardDeviation = Double.NaN;

	/**
	 * @param name
	 */
	public RemoteBenchRun(String name) {
		super(name);
		setTimeScalingFactor(.5f);
	}
	
	/** Increment the number of events that are emitted during the run.
	 */
	public void incrementEventCounter() {
		this.awaitingMessages.incrementAndGet();
	}
	
	/** Decrement the number of events that are emitted during the run.
	 * 
	 * @param event
	 */
	public void decrementEventCounter(RemoteBenchEvent event) {
		this.events.add(event);
		this.awaitingMessages.decrementAndGet();
	}
	
	/** Replies if an event is still waited.
	 * 
	 * @return <code>true</code> if an event is waited, <code>false</code>
	 * if all the events were received.
	 */
	public boolean isWaitingEvent() {
		return this.awaitingMessages.get()>0;
	}
	
	private void compute() {
		long total = 0;
		double average = 0;
		double stdDev = 0f;
		if (!this.events.isEmpty()) {
			for(RemoteBenchEvent event : this.events) {
				total += (event.getArrivalTime() - event.getSendingTime()) * getTimeScalingFactor() + getTimeIncrement();
			}
			average = total / this.events.size();
			total = 0;
			for(RemoteBenchEvent event : this.events) {
				float x = (event.getArrivalTime() - event.getSendingTime()) * getTimeScalingFactor() + getTimeIncrement();
				total += (x-average)*(x-average);
			}
			stdDev = Math.sqrt(total / this.events.size());
		}
		this.averageDuration = average;
		this.standardDeviation = stdDev;
	}
	
	@Override
	public long getCallAverageDuration() {
		if (Double.isNaN(this.averageDuration)) {
			compute();
		}
		return (long)this.averageDuration;
	}
	
	@Override
	public double getCallStandardDeviation() {
		if (Double.isNaN(this.standardDeviation)) {
			compute();
		}
		return (long)this.standardDeviation;
	}
		
}