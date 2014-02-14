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
import java.lang.reflect.Constructor;

/** Run the benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public class BenchLauncher {

	/**
	 * @param args
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		File output = new File(args[0]);
		float progression = Float.parseFloat(args[1]);
		float progressionWindow = Float.parseFloat(args[2]);
		float progressionPerClass = progressionWindow / (args.length - 3);
		for(int i=3; i<args.length; ++i) {
			Class<?> type = Class.forName(args[i]);
			if (Bench.class.isAssignableFrom(type)) {
				Class<? extends Bench<?>> benchType = (Class<? extends Bench<?>>)type; 
				Constructor<? extends Bench<?>> cons = benchType.getConstructor(File.class);
				Bench<?> bench = cons.newInstance(output);
				bench.runBenchs(progression, progressionPerClass);
				bench = null;
				for(int j=0; j<6; ++j) {
					System.gc();
				}
			}
			progression += progressionPerClass;
		}
		System.exit(0);
	}
	
}