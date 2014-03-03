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

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.arakhne.afc.vmutil.locale.Locale;

/** This abstract class describes a bench for the Janus kernel.
 * <p>
 * The subclasses of {@code Bench} must implements functions with
 * the prefix "bench" in their name. The function {@link #runBenchs(float, float)}
 * retreives there functions with reflection mechanism and run them.
 * The function {@link #onStartBenchCalls(BenchRun)} and {@link #onEndBenchCalls(BenchRun)} are invoked
 * before and after each invocation of a "bench" function.
 * 
 * @param <R> is the type of the runs
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class Bench<R extends BenchRun> {

	private static final String PREFIX = "bench"; //$NON-NLS-1$
	
	private final File outputDirectory;
	private int nbCalls = BenchConstants.DEFAULT_TEST_NUMBER;
	private int nbRuns = BenchConstants.DEFAULT_RUN_NUMBER;
	
	private Collection<R> ranges = Collections.emptyList();
	
	private final List<R> runs = new LinkedList<>();
	
	private R currentRun = null;
	
	private final OperatingSystemMXBean osBean;
	
	/**
	 * @param directory is the directory that shold contains the CSV file.
	 */
	protected Bench(File directory) {
		this.outputDirectory = new File(directory, getClass().getSimpleName());
		this.osBean = ManagementFactory.getOperatingSystemMXBean();
	}

	/** Replies the name of the directory in which the benchmark results are written.
	 *  
	 * @return the output directory of the benchmarks.
	 */
	public File getOutputDirectory() {
		return this.outputDirectory;
	}
	/** Change the ranges to be considered.
	 * 
	 * @param ranges
	 */
	@SuppressWarnings("unchecked")
	protected void setRunRanges(R... ranges) {
		this.ranges = ranges == null ? Collections.<R>emptyList() : Arrays.asList(ranges);
	}
	
	/** Change the ranges to be considered.
	 * 
	 * @param ranges
	 */
	protected void setRunRanges(Collection<R> ranges) {
		this.ranges = ranges == null ? Collections.<R>emptyList() : ranges;
	}

	/** Replies the run ranges.
	 * 
	 * @return the ranges.
	 */
	protected Collection<R> getRunRanges() {
		return this.ranges;
	}
	
	/**
	 * Returns the system load average for the last minute. The system load
	 * average is the sum of the number of runnable entities queued to the
	 * available processors and the number of runnable entities running on
	 * the available processors averaged over a period of time. The way in
	 * which the load average is calculated is operating system specific but
	 * is typically a damped time-dependent average.
	 * <p>
	 * If the load average is not available, a zero value is returned.
	 * <p>
	 * This method is designed to provide a hint about the system load and
	 * may be queried frequently. The load average may be unavailable on
	 * some platform where it is expensive to implement this method.
	 * 
	 * @return the system load average.
	 */
	public double getSystemLoadAverage() {
		return Math.max(0, this.osBean.getSystemLoadAverage());
	}
	
	/**
	 * Returns the number of processors available to the Java virtual machine.
     * This method is equivalent to the {@link Runtime#availableProcessors()}
     * method.
     * <p> This value may change during a particular invocation of
     * the virtual machine.
     *
     * @return  the number of processors available to the virtual
     *          machine; never smaller than one.
	 */
	public double getAvailableProcessors() {
		return this.osBean.getAvailableProcessors();
	}

	// Invoked by reflection
	@SuppressWarnings("unused")
	private void doIddle() {
		//
	}
	
	/** Replies the current run.
	 * 
	 * @return the current run.
	 */
	public R getCurrentRun() {
		return this.currentRun;
	}
	
	private void setCurrentRun(R run) {
		this.currentRun = run;
	}
	
	/** Set the number of calls to the benchmarking function.
	 * Each benchmarking function is invoked the number of times
	 * given by the parameter. This enable us to obtain
	 * better time computation.
	 * 
	 * @param nbCalls
	 */
	protected void setNumberOfCalls(int nbCalls) {
		if (nbCalls>0) {
			this.nbCalls = nbCalls;
		}
	}
	
	/** Set the number of calls to the benchmarking function.
	 * Each benchmarking function is invoked the number of times
	 * given by the parameter. This enable us to obtain
	 * better time consumption computation.
	 * 
	 * @return the number of tests to run for each benchmark.
	 */
	protected int getNumberOfCalls() {
		return this.nbCalls;
	}

	/** Replies the number of times the bench was initialized, run and
	 * disposed.
	 * 
	 * @return the number of runs
	 */
	protected int getNumberOfRuns() {
		return this.nbRuns;
	}

	/** Set the number of times the bench was initialized, run and
	 * disposed.
	 * 
	 * @param nbRuns
	 */
	protected void setNumberOfRuns(int nbRuns) {
		if (nbRuns>0) {
			this.nbRuns = nbRuns;
		}
	}
	
	/** Invoked when the bench class is starting.
	 * @throws Exception
	 */
	public void initialize() throws Exception {
		//
	}
	
	/** Invoked when the bench class should be disposed.
	 * @throws Exception
	 */
	public void dispose() throws Exception {
		//
	}
	
	/** Invoked to initialize the benchmarking for a function.
	 * 
	 * @param benchFunctionName is the name of the bench function
	 * @throws Exception
	 */
	public abstract void onStartDeclaredBenchFunction(String benchFunctionName) throws Exception;

	/** Invoked to initialize one run of a bench.
	 * 
	 * @param run is the description of the run of the bench to execute. 
	 * @throws Exception
	 */
	public void onStartBenchCalls(R run) throws Exception {
		//
	}

	/** Invoked to initialize a bench when it is multi-run-based.
	 * 
	 * @param run is the description of the run of the bench to execute. 
	 * @throws Exception
	 */
	public void onStartRunInMany(R run) throws Exception {
		//
	}

	/** Invoked to close one run of a bench.
	 * 
	 * @param run is the description of the bench to shut down.
	 * @throws Exception
	 */
	public void onEndBenchCalls(R run) throws Exception {
		this.runs.add(run);
	}
	
	/** Invoked to close a bench when it is multirun.
	 * 
	 * @param nbRuns is the number of runs.
	 * @param run is the description of the bench to shut down.
	 * @throws Exception
	 */
	public void onEndRunOnMany(int nbRuns, R run) throws Exception {
		this.runs.add(run);
	}

	/** Invoked to close a group of benchs.
	 * 
	 * @throws Exception
	 */
	public void onEndDeclaredBenchFunction() throws Exception {
		//
	}
	
	/** Replies the terminated runs.
	 * 
	 * @return the terminated runs.
	 */
	protected List<R> getTerminatedRuns() {
		return this.runs;
	}
	
	private static String formatPercentage(float v) {
		StringBuilder sb = new StringBuilder();
		sb.append((int)v);
		while (sb.length()<3) {
			sb.insert(0, ' ');
		}
		return sb.toString();
	}

	/** Run all the benchs of the class.
	 * 
	 * @param taskStart is the first percentage dedicated to this task.
	 * @param taskSize is the amount of the progression dedicated to this run
	 * @throws Exception
	 */
	public final void runBenchs(float taskStart, float taskSize) throws Exception {
		// Retreive the hierarchy of classes.
		List<Class<?>> classes = new LinkedList<>();
		{
			Class<?> type = getClass();
			while (type!=null && !type.equals(Bench.class)) {
				classes.add(0, type);
				type = type.getSuperclass();
			}
		}
		
		Logger logger = Logger.getAnonymousLogger();
		
		initialize();
		
		List<Method> benchMethods = new ArrayList<>(); 
		for(Class<?> type : classes) {
			for(Method method : type.getDeclaredMethods()) {
				if (method.getName().startsWith(PREFIX)) {
					benchMethods.add(method);
				}
			}
		}
		
		float taskStep = taskSize / benchMethods.size();
		float taskValue = taskStart;
		String task = formatPercentage(taskValue);
		
		
		// Run the "bench" functions for each class
		for(Method method : benchMethods) {
			String groupName = method.getName().substring(PREFIX.length());
			try {
				long startTime, endTime;
				int nbTests = getNumberOfCalls();
				int nbRuns = getNumberOfRuns();
				
				logger.info(Locale.getString("START_GROUP", task, getClass().getSimpleName(), groupName)); //$NON-NLS-1$
				this.runs.clear();
				this.ranges = Collections.emptyList();
				onStartDeclaredBenchFunction(groupName);
				Collection<R> runIterator = getRunRanges();
				
				float subTaskValue = taskValue;
				float subTaskStep = taskStep / runIterator.size();
				
				for(R run : runIterator) {
					setCurrentRun(run);
					long runDuration = 0;
					if (nbRuns>1) {
						onStartRunInMany(run);
					}
					long[] measurements = new long[nbRuns];
					
					float subsubTaskValue = subTaskValue;
					float subsubTaskStep = subTaskStep / nbRuns;
					task = formatPercentage(subsubTaskValue);
					
					for(int idxRun=0; idxRun<nbRuns; ++idxRun) {
						
						logger.info(Locale.getString("PREPARE_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString(), BenchConstants.INITIALIZATION_WAITING_TIME/1000)); //$NON-NLS-1$
						onStartBenchCalls(run);
						if (BenchConstants.INITIALIZATION_WAITING_TIME>0) {
							Thread.sleep(BenchConstants.INITIALIZATION_WAITING_TIME);
						}
						logger.info(Locale.getString("START_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString())); //$NON-NLS-1$
						startTime = System.nanoTime();
						for(int i=0; i<nbTests; ++i) {
							method.invoke(this);
						}
						endTime = System.nanoTime();
						measurements[idxRun] = Math.max(0,
								(long)(run.getTimeScalingFactor()*(endTime - startTime))
								+run.getTimeIncrement());
						runDuration += measurements[idxRun];
						measurements[idxRun] = measurements[idxRun] / nbTests;
						if (nbRuns==1) {
							run.setDurations(
									runDuration,
									measurements[idxRun],
									0);
						}
						onEndBenchCalls(run);
						subsubTaskValue += subsubTaskStep;
						task = formatPercentage(subsubTaskValue);
						logger.info(Locale.getString("END_BENCH", task, idxRun+1, nbRuns, getClass().getSimpleName(), run.toString())); //$NON-NLS-1$
					}
					{
						double runAverage = (double)runDuration/nbRuns;
						double testAverage = 0.;
						if (measurements.length>0) {
							for(long x : measurements) {
								testAverage += x;
							}
							testAverage /= measurements.length;
						}
						double stdDev = 0;
						if (measurements.length>0) {
							for(long x : measurements) {
								stdDev += (x-testAverage)*(x-testAverage);
							}
							stdDev /= measurements.length;
							stdDev = Math.sqrt(stdDev);
						}
						run.setDurations(
								(long)runAverage,
								(long)testAverage,
								stdDev);
					}
					if (nbRuns>1) {
						onEndRunOnMany(nbRuns, run);
					}
					setCurrentRun(null);
					run = null;
					for(int i=0; i<6; ++i) {
						System.gc();
					}
					subTaskValue += subTaskStep;
				}
			}
			catch(Throwable e) {
				logError(logger, groupName, e);
			}

			onEndDeclaredBenchFunction();
			this.runs.clear();
			this.ranges = Collections.emptyList();

			taskValue += taskStep;
			task = formatPercentage(taskValue);
			logger.info(Locale.getString("END_GROUP", task, getClass().getSimpleName(), groupName)); //$NON-NLS-1$
			
		}
		
		dispose();
	}
	
	/** Log an error.
	 * 
	 * @param logger the default logger.
	 * @param benchFunctionName is the name of the group of benchs that have failed.
	 * @param e is the error to log.
	 */
	public abstract void logError(Logger logger, String benchFunctionName, Throwable e);
	
}