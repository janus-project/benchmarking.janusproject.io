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

/** Constants for the benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchConstants {

	/** Default number of tests in the same run
	 * when only one run should be executed.
	 */
	public static final int DEFAULT_TEST_NUMBER = 100000;

	/** Default number of runs.
	 */
	public static final int DEFAULT_RUN_NUMBER = 10;

	/** Time to wait after initialization (in ms).
	 */
	public static final int INITIALIZATION_WAITING_TIME = 1000;

	/** Max memory in the virtual machine.
	 */
	public static final int MAX_MEMORY = 1024;

//	/**
//	 *  Delay for the insertion of a message into the black hole mailbox.
//	 */
//	public static final int MAILBOX_INSERTION_DELAY = 0;
//	
//	/**
//	 *  Delay for the read of a message from the black hole mailbox.
//	 */
//	public static final int MAILBOX_READ_DELAY = 0;
//
//	/** Number of runs when a bench must be reset
//	 * between each benchmarked operation.
//	 */
//	public static final int RESETTING_RUN_NUMBER = 5000;
//
//	/** Number of runs when the bench should measure
//	 * number of operations per second.
//	 */
//	public static final int PER_SECOND_BENCH_RUN_NUMBER = 5;
//
//	/** Sleeping duration when the bench should measure
//	 * number of operations per second (in ms).
//	 */
//	public static final int PER_SECOND_BENCH_SLEEPING_DURATION = 2000;
//	
//	/** First point of the benchmarking intervals for messages.
//	 */
//	public static final int MESSAGE_INTERVAL_START = 0;
//
//	/** Points of the benchmarking intervals for messages.
//	 */
//	public static final int[] MESSAGE_INTERVAL_SEGMENTS = new int[] {
//		100, 10,				// [1; 100]
//		1000, 50,				// ]100; 1000]
//		10000, 500,				// ]1000; 10000]
//	};
//
//	/** First point of the benchmarking intervals for light agents.
//	 */
//	public static final int LIGHT_AGENT_INTERVAL_START = 0;
//
//	/** Points of the benchmarking intervals for light agents.
//	 */
//	public static final int[] LIGHT_AGENT_INTERVAL_SEGMENTS = new int[] {
//		100, 10,				// [1; 100]
//		1000, 100,				// ]100; 1000]
//		10000, 1000,			// ]1000; 10000]
//		100000, 5000			// ]10000; 100000]
//	};
//
//	/** First point of the benchmarking intervals for heavy agents.
//	 */
//	public static final int HEAVY_AGENT_INTERVAL_START = 0;
//
//	/** Points of the benchmarking intervals for heavy agents.
//	 */
//	public static final int[] HEAVY_AGENT_INTERVAL_SEGMENTS = new int[] {
//		100, 20,				// [1; 100]
//		1000, 100				// ]100; 1000]
//	};
//	
//	/** First point of the benchmarking intervals for one heavy and many light agents.
//	 */
//	public static final int HEAVY_LIGHT_AGENT_INTERVAL_START = 0;
//
//	/** Points of the benchmarking intervals for one heavy and many light agents.
//	 */
//	public static final int[] HEAVY_LIGHT_AGENT_INTERVAL_SEGMENTS = new int[] {
//		100, 10,				// [1; 100]
//		1000, 100,				// ]100; 1000]
//		10000, 1000,			// ]1000; 10000]
//		100000, 5000			// ]10000; 100000]
//	};

}