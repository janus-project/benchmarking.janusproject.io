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
package io.janusproject;

import io.janusproject.benchmarking.Bench;
import io.janusproject.benchmarking.BenchConstants;
import io.janusproject.benchmarking.BenchLauncher;
import io.janusproject.benchmarking.PropertyBench;
import io.janusproject.benchmarking.jei.JanusExperienceIndex;
import io.janusproject.network.zeromq.GsonAesLocalhostMonodirBench;
import io.janusproject.network.zeromq.GsonAesRemotehostBench;
import io.janusproject.network.zeromq.GsonPlainLocalhostMonodirBench;
import io.janusproject.network.zeromq.GsonPlainRemotehostBench;
import io.janusproject.network.zeromq.JavaAesLocalhostMonodirBench;
import io.janusproject.network.zeromq.JavaAesRemotehostBench;
import io.janusproject.network.zeromq.JavaPlainLocalhostMonodirBench;
import io.janusproject.network.zeromq.JavaPlainRemotehostBench;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.arakhne.afc.vmutil.VMCommandLine;
import org.arakhne.afc.vmutil.locale.Locale;

/** Run the benchs.
 * 
 * @author $Author: sgalland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.5
 */
@SuppressWarnings("unchecked")
public class Benchs {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		VMCommandLine.saveVMParametersIfNotSet(BenchLauncher.class, args);
		run(
				//
				// ZeroMQ
				//
				JavaPlainLocalhostMonodirBench.class,
				JavaAesLocalhostMonodirBench.class,
				GsonPlainLocalhostMonodirBench.class,
				GsonAesLocalhostMonodirBench.class,
				JavaPlainRemotehostBench.class,
				JavaAesRemotehostBench.class,
				GsonPlainRemotehostBench.class,
				GsonAesRemotehostBench.class
				);
		System.exit(0);
	}

	private static List<Class<? extends Bench<?>>> selectBenchs(Class<? extends Bench<?>>... benchs) {
		List<Class<? extends Bench<?>>> benchsToRun = new ArrayList<>();
		SelectionGUI gui = new SelectionGUI(benchs);
		gui.setVisible(true);
		if (gui.isJei()) {
			benchsToRun = null;
		}
		else if (gui.isBenchmark()) {
			gui.getSelectionBenchs(benchsToRun);
		}
		gui.dispose();
		return benchsToRun;
	}

	private static void run(Class<? extends Bench<?>>... benchs) throws Exception {
		List<Class<? extends Bench<?>>> benchsToRun = selectBenchs(benchs);
		if (benchsToRun==null) {
			JanusExperienceIndex.main(new String[0]);
		}
		else if (!benchsToRun.isEmpty()) {
			// Enter additional parameters
			Properties properties = new Properties();
			for(Class<?> benchType : benchsToRun) {
				while (benchType!=null && !Object.class.equals(benchType)) {
					for(Annotation a : benchType.getDeclaredAnnotations()) {
						if (PropertyBench.class.isAssignableFrom(a.annotationType())) {
							for(String name : ((PropertyBench)a).names()) {
								if (!properties.containsKey(name)) {
									TextInputGUI g = new TextInputGUI(name);
									g.setVisible(true);
									String value = g.getValue();
									if (value==null) {
										return; // exit from the main
									}
									properties.setProperty(name, value);
									g.dispose();
								}
							}
							break;
						}
					}
					benchType = benchType.getSuperclass();
				}
			}
			
			// Create the temp directory
			String tmpDir;
			tmpDir = System.getenv("TEMP"); //$NON-NLS-1$
			if (tmpDir==null || "".equals(tmpDir)) { //$NON-NLS-1$
				tmpDir = System.getenv("TMP"); //$NON-NLS-1$
			}
			if (tmpDir==null || "".equals(tmpDir)) { //$NON-NLS-1$
				tmpDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
			}
			File output;
			if (tmpDir==null || "".equals(tmpDir)) { //$NON-NLS-1$
				output = null;
			}
			else {
				output = new File(tmpDir);
			}

			output = new File(output, "janusBenchmarks"); //$NON-NLS-1$

			JFileChooser fileChooser = new JFileChooser(output);
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				output = fileChooser.getSelectedFile();
			}
			else {
				return; // exit from the main
			}
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd-HHmmss"); //$NON-NLS-1$
			output = new File(output, fmt.format(new Date()));
			System.out.println(output.getAbsolutePath());
			output.mkdirs();

			boolean isAssertEnabled = false;
			assert((isAssertEnabled = true)==true);
			ExecutorService serv = null;
			if (!isAssertEnabled)
				serv = Executors.newFixedThreadPool(2);
			
			float percentagePerBench = 100f / benchsToRun.size();
			float progression = 0f;
			
			// Ensure that most of the unused objects are removed from memory
			System.gc();
			System.gc();
			System.gc();
						
			for(Class<? extends Bench<?>> benchType : benchsToRun) {
				if (isAssertEnabled) {
					launchForDebug(output, benchType, properties, progression, percentagePerBench);
				}
				else {
					launchInSubProcess(output, benchType, serv, properties, progression, percentagePerBench);
				}
				progression += percentagePerBench;
			}

			System.out.println(output.getAbsolutePath());
		}
	}
	
	private static void launchForDebug(File output, Class<? extends Bench<?>> benchType, Properties benchProperties, float progression, float percentage) throws Exception {
		if (benchProperties!=null) {
			System.getProperties().putAll(benchProperties);
		}
		BenchLauncher.main(
				new String[] {
						output.getAbsolutePath(),
						Float.toString(progression),
						Float.toString(percentage),
						benchType.getCanonicalName()
				});
	}
	
	private static void launchInSubProcess(File output, Class<? extends Bench<?>> benchType, ExecutorService service, Properties benchProperties, float progression, float progressionWindow) throws Exception {
		Process process = null;
		try {
			List<String> cmd = new ArrayList<>();
			cmd.add(VMCommandLine.getVMBinary());
			if (benchProperties!=null) {
				for(Entry<Object,Object> entry : benchProperties.entrySet()) {
					cmd.add("-D"+entry.getKey()+"="+entry.getValue()); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			cmd.add("-Xmx"+BenchConstants.MAX_MEMORY+"m"); //$NON-NLS-1$ //$NON-NLS-2$
			cmd.add("-classpath"); //$NON-NLS-1$
			cmd.add(System.getProperty("java.class.path")); //$NON-NLS-1$
			cmd.add(BenchLauncher.class.getCanonicalName());
			cmd.add(output.getAbsolutePath());
			cmd.add(Float.toString(progression));
			cmd.add(Float.toString(progressionWindow));
			cmd.add(benchType.getCanonicalName());
			for(int i=0; i<cmd.size(); ++i) {
				if (i>0) System.out.print(' ');
				System.out.print(cmd.get(i));
			}
			System.out.print("\n"); //$NON-NLS-1$
			String[] array = new String[cmd.size()];
			cmd.toArray(array);
			cmd.clear();
			cmd = null;
			System.gc();
			System.gc();
			System.gc();
			process = Runtime.getRuntime().exec(
					array,
					null,
					null);
			OutputRunner r1 = new OutputRunner(process.getInputStream(), System.out);
			OutputRunner r2 = new OutputRunner(process.getErrorStream(), System.err);
			service.submit(r1);
			service.submit(r2);
			process.waitFor();
			r1.stop();
			r2.stop();
			process = null;
		}
		catch(Throwable e) {
			System.err.println(Locale.getString(Benchs.class, "KILLING")); //$NON-NLS-1$
			e.printStackTrace();
			if (process!=null) {
				process.destroy();
			}
		}
	}
	
	private static class OutputRunner implements Runnable {
	
		private volatile boolean stop = false; 
		private final PrintStream os;
		private final InputStream is;
		
		public OutputRunner(InputStream is, PrintStream os) {
			this.is = is;
			this.os = os;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			try {
				String s;
				byte[] buffer = new byte[2048];
				Thread.yield();
				int nb = this.is.read(buffer);
				while (nb>0 && !this.stop) {
					s = new String(buffer, 0, nb);
					this.os.print(s);
					Thread.yield();
					nb = this.is.read(buffer);
				}
				this.is.close();
			}
			catch(IOException _) {
				//
			}
		}
		
		/** Stop the process.
		 */
		public void stop() {
			this.stop = true;
		}
		
	}
	
	private static class TextInputGUI extends JDialog implements ActionListener {
		
		private static final long serialVersionUID = -4124681578914766500L;
		
		private final JTextField value;
		private final AtomicBoolean isOk = new AtomicBoolean(false);
		
		/**
		 * @param name - name of the property to enter.
		 */
		public TextInputGUI(String name) {
			super((Window)null, Locale.getString(Benchs.class, "INPUT_PROPERTY", name)); //$NON-NLS-1$
			setPreferredSize(new Dimension(600, 200));
			setLayout(new BorderLayout());
			setModal(true);
			this.value = new JTextField();
			add(BorderLayout.CENTER, this.value);

			JPanel btPanel = new JPanel();
			add(BorderLayout.SOUTH, btPanel);
			
			JButton bt = new JButton(Locale.getString(Benchs.class, "SAVE")); //$NON-NLS-1$
			bt.setDefaultCapable(true);
			btPanel.add(bt);
			bt.setActionCommand("SAVE"); //$NON-NLS-1$
			bt.addActionListener(this);

			bt = new JButton(Locale.getString(Benchs.class, "CANCEL")); //$NON-NLS-1$
			btPanel.add(bt);
			bt.setActionCommand("CANCEL"); //$NON-NLS-1$
			bt.addActionListener(this);

			pack();
		}

		/** Replies the entered value.
		 * 
		 * @return the value, or <code>null</code> if no value was entered.
		 */
		public String getValue() {
			if (this.isOk.get()) {
				return this.value.getText();
			}
			return null;
		}
		
		/** {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ("SAVE".equals(e.getActionCommand())) { //$NON-NLS-1$
				this.isOk.set(true);
				setVisible(false);
			}
			else if ("CANCEL".equals(e.getActionCommand())) { //$NON-NLS-1$
				this.isOk.set(false);
				setVisible(false);
			}
		}
		
	}
	

	/** Select the benchs.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class SelectionGUI extends JDialog implements ActionListener {

		private static final long serialVersionUID = 5475408361451706102L;

		private DefaultListModel<Class<? extends Bench<?>>> model;
		private JList<Class<? extends Bench<?>>> list;
		private boolean isBench = false;
		private boolean isJei = false;

		/**
		 * @param benchs
		 */
		public SelectionGUI(Class<? extends Bench<?>>... benchs) {
			super((Window)null, Locale.getString(Benchs.class, "SELECT_BENCHS")); //$NON-NLS-1$
			setPreferredSize(new Dimension(600, 600));
			setLayout(new BorderLayout());
			setModal(true);
			this.model = new DefaultListModel<>();
			this.list = new JList<>(this.model);
			add(BorderLayout.CENTER, new JScrollPane(this.list));
			this.list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			this.list.setCellRenderer(new SelectionRenderer());

			for(Class<? extends Bench<?>> benchType : benchs) {
				addInModel(benchType);
			}

			JPanel btPanel = new JPanel();
			add(BorderLayout.SOUTH, btPanel);
			
			JButton bt = new JButton(Locale.getString(Benchs.class, "LAUNCH_BENCHS")); //$NON-NLS-1$
			btPanel.add(bt);
			bt.setActionCommand("BENCH"); //$NON-NLS-1$
			bt.addActionListener(this);

			bt = new JButton(Locale.getString(Benchs.class, "COMPUTE_JEI")); //$NON-NLS-1$
			btPanel.add(bt);
			bt.setActionCommand("JEI"); //$NON-NLS-1$
			bt.addActionListener(this);

			pack();
		}

		/** Replies if the benchmarcks should be computed
		 * 
		 * @return <code>true</code> if the user decided to compute the benchmarks.
		 */
		public boolean isBenchmark() {
			return this.isBench;
		}

		/** Replies if the JEI should be computed
		 * 
		 * @return <code>true</code> if the user decided to compute the JEI.
		 */
		public boolean isJei() {
			return this.isJei;
		}

		/**
		 * @param list
		 * @return the number of added benchs.
		 */
		public int getSelectionBenchs(List<Class<? extends Bench<?>>> list) {
			int nb = 0;
			for(int i : this.list.getSelectedIndices()) {
				list.add(this.model.get(i));
				++nb;
			}
			return nb;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void dispose() {
			this.model.clear();
			this.model = null;
			this.list = null;
			super.dispose();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			if ("JEI".equals(e.getActionCommand())) { //$NON-NLS-1$
				this.isJei = true;
				setVisible(false);
			}
			else if ("BENCH".equals(e.getActionCommand())) { //$NON-NLS-1$
				this.isBench = true;
				setVisible(false);
			}
		}

		private void addInModel(Class<? extends Bench<?>> data) {
			assert(data!=null);
			int f = 0;
			int l = this.model.size()-1;
			int c;
			Class<? extends Bench<?>> d;
			int cmpR;
			while (l>=f) {
				c = (f+l)/2;
				d = this.model.get(c);
				cmpR = compare(data, d);
				if (cmpR==0) return;
				if (cmpR<0) {
					l = c-1;
				}
				else {
					f = c+1;
				}
			}
			this.model.add(f, data);
		}

		@SuppressWarnings("synthetic-access")
		public static int compare(Class<?> c1, Class<?> c2) {
			if (c1==c2) return 0;
			if (c1==null) return Integer.MAX_VALUE;
			if (c2==null) return Integer.MIN_VALUE;
			String n1 = toUIName(c1);
			String n2 = toUIName(c2);
			return n1.compareToIgnoreCase(n2);
		}

	}

	/** Select the benchs.
	 * 
	 * @author $Author: sgalland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 0.5
	 */
	private static class SelectionRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -5166019436245423924L;

		/**
		 */
		public SelectionRenderer() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			label.setText(toUIName((Class<?>)value));
			return label;
		}

	}

	private static String toUIName(Class<?> type) {
		StringBuilder b = new StringBuilder();
		b.append(type.getPackage().getName().substring(type.getPackage().getName().lastIndexOf('.')+1));
		b.append(" | "); //$NON-NLS-1$
		b.append(type.getSimpleName());
		return b.toString();
	}

}