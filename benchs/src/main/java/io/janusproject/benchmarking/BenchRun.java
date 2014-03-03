/*
 * $Id$
 * 
 * Janus platform is an open-source multiagent platform.
 * More details on http://www.janusproject.io
 * 
 * Copyright (C) 2012-2014 Sebastian RODRIGUEZ, Nicolas GAUD, Stéphane GALLAND.
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
package io.janusproject.benchmarking;

/** This class describes a run of a bench.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchRun {

	private String name;
	private long durationPerCall = -1;
	private long runDuration = -1;
	private double callStandardDeviation = 0;
	private float timeScalingFactor = 1f;
	private long timeIncrement = 0;
	
	/**
	 * @param name
	 */
	public BenchRun(String name) {
		this.name = name;
	}
	
	/** Replies the name of the bench.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/** Set the name of the bench.
	 * 
	 * @param name is the name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/** Replies the duration of the bench run with
	 * all the tests.
	 * @return the nano time, or {@code -1} if the
	 * duration is unknown.
	 */
	public long getRunDuration() {
		return this.runDuration;
	}
	
	/** Replies the average duration of one test of 
	 * the bench run.
	 * @return the nano time, or {@code -1} if the
	 * duration is unknown.
	 */
	public long getCallAverageDuration() {
		return this.durationPerCall;
	}
	
	/** Replies the standard deviation of one unit operation.
	 * 
	 * @return the standard deviation.
	 */
	public double getCallStandardDeviation() {
		return this.callStandardDeviation;
	}

	/** Set the duration of the bench.
	 * 
	 * @param runDuration is the duration of the complete run.
	 * @param callDuration is the average duration of one call in the run.
	 * @param callStandardDeviation is the standard deviation of the calls.
	 */
	void setDurations(long runDuration, long callDuration, double callStandardDeviation) {
		this.runDuration = runDuration;
		this.durationPerCall = callDuration;
		this.callStandardDeviation = callStandardDeviation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return (this.name==null || this.name.isEmpty()) ? super.toString() : this.name;
	}
	
	/** Replies the factor to apply to the measured time after all the tests
	 * were run.
	 * 
	 * @return the time scaling factor.
	 */
	public float getTimeScalingFactor() {
		return this.timeScalingFactor;
	}
	
	/** Set the factor to apply to the measured time after all the tests
	 * were run.
	 * 
	 * @param factor is the time scaling factor.
	 */
	public void setTimeScalingFactor(float factor) {
		this.timeScalingFactor = factor;
	}

	/** Replies the a time to add to the measured duration, after its scaling.
	 * 
	 * @return the time scaling factor.
	 */
	public long getTimeIncrement() {
		return this.timeIncrement;
	}
	
	/** Set the a time to add to the measured duration, after its scaling.
	 * 
	 * @param increment is the time scaling factor.
	 */
	public void setTimeIncrement(long increment) {
		this.timeIncrement = increment;
	}

}