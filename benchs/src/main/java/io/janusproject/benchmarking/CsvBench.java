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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/** This abstract class describes a bench for the Janus kernel with
 * all the each run result is stored in a line of a CSV file.
 * 
 * @param <R> is the type of the runs
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
public abstract class CsvBench<R extends BenchRun> extends Bench<R> {

	private final Object[] headers;
	private final String title;
	private BufferedWriter writer;

	/**
	 * @param directory is the directory that shold contains the CSV file.
	 * @param title - title of the benchmarks. 
	 * @param headers - headers of the columns in the CSV file.
	 * @throws IOException
	 */
	public CsvBench(File directory, String title, Object... headers) throws IOException {
		super(directory);
		this.headers = headers;
		this.title = title;
		this.writer = null;
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() throws Exception {
		if (this.writer!=null) {
			this.writer.close();
			this.writer = null;
		}
		super.dispose();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStartDeclaredBenchFunction(String benchFunctionName) throws Exception {
		if (this.writer!=null) {
			this.writer.close();
		}
		File csvFile = new File(getOutputDirectory(), benchFunctionName+".csv"); //$NON-NLS-1$
		csvFile.getParentFile().mkdirs();
		this.writer = new BufferedWriter(new FileWriter(csvFile));
		setRunRanges(determineRuns(benchFunctionName));
		Object[] headers = getCsvHeader();
		if (headers!=null && headers.length>0) {
			writeHeader(headers);
		}
	}
	
	/** Replies the headers of the CSV, column by column.
	 * 
	 * @return the headers of the columns.
	 */
	protected Object[] getCsvHeader() {
		return this.headers;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void logError(Logger logger, String benchFunctionName, Throwable e) {
		logger.log(Level.SEVERE, benchFunctionName.toLowerCase()+e.toString(), e);
		File errorFile = new File(getOutputDirectory(), benchFunctionName+".log"); //$NON-NLS-1$
		errorFile.getParentFile().mkdirs();
		try(PrintWriter s = new PrintWriter(new FileWriter(errorFile))) {
			s.println(e.toString());
			e.printStackTrace(s);
		}
		catch(Throwable _) {
			//
		}
	}

	/** Determine the different runs to execute.
	 * 
	 * @param benchFunctionName is the name of the bench function
	 * @return the iterator on the description of the benchs to run.
	 */
	protected abstract Collection<R> determineRuns(String benchFunctionName);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEndDeclaredBenchFunction() throws Exception {
		if (this.writer!=null) {
			this.writer.close();
			this.writer = null;
		}
		super.onEndDeclaredBenchFunction();
	}
	
	/** Write a record line in the CSV.
	 * @param columns
	 * @throws IOException
	 */
	protected void writeRecord(Object... columns) throws IOException {
		assert(this.writer!=null);
		for(int i=0; i<columns.length; ++i) {
			if (i>0) this.writer.write("\t"); //$NON-NLS-1$
			if (columns[i]!=null)
				this.writer.write(columns[i].toString());
		}
		this.writer.write("\n"); //$NON-NLS-1$
		this.writer.flush();
	}
	
	/** Write a header line in the CSV.
	 * @param columns
	 * @throws IOException
	 */
	private void writeHeader(Object... columns) throws IOException {
		assert(this.writer!=null);
		if (this.title!=null && !"".equals(this.title)) { //$NON-NLS-1$
			this.writer.write("#------------ "); //$NON-NLS-1$
			this.writer.write(this.title);
			this.writer.write(" ------------\n"); //$NON-NLS-1$
		}
		for(int i=0; i<columns.length; ++i) {
			if (i>0) this.writer.write("\t"); //$NON-NLS-1$
			else this.writer.write("#"); //$NON-NLS-1$
			if (columns[i]!=null)
				this.writer.write(columns[i].toString());
		}
		this.writer.write("\n"); //$NON-NLS-1$
		this.writer.flush();
	}

}