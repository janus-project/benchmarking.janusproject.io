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

import java.io.File;
import java.io.IOException;

import com.google.inject.Module;

/** Benchmarking of the ZeroMQ layer:
 * <ul>
 * <li>Serialization: Gson.</li>
 * <li>Encrypting: None.</li>
 * <li>Source on host A</li>
 * <li>Target on host A</li>
 * <li>Receiver is not replying.</li>
 * </ul>
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.0.0
 */
public class GsonPlainLocalhostMonodirBench extends AbstractLocalhostBench {

	/**
	 * @param directory
	 * @throws IOException
	 */
	public GsonPlainLocalhostMonodirBench(File directory) throws IOException {
		super(directory);
	}

	@Override
	protected Module getInjectionModule() {
		return new BenchmarkingModule(
				GsonEventSerializer.class,
				PlainTextEncrypter.class);
	}

	/**
	 * @throws Exception
	 */
	public void benchPublish() throws Exception {
		this.networkSource.publish(this.spaceId, this.scope, this.defaultEvent);
	}
	
}