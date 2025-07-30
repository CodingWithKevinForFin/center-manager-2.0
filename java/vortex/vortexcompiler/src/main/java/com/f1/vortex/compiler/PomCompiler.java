package com.f1.vortex.compiler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarOutputStream;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.f1.base.Legible;
import com.f1.utils.AH;
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.EnvironmentDump;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SearchPath;
import com.f1.utils.concurrent.SimpleExecutor;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.tar.TarEntry;
import com.f1.utils.tar.TarOutputStream;

public class PomCompiler {

	/*
	* Required Arguments are: source_dir target_file maven_settings_file tmp_dir ADDITIONAL_OPTIONS
	* 
	* source_dir - the directory containing the pom.xml file 
	* target_file - final gz file, be sure to include the tar.gz in the file name
	* maven_settings_file - this is usually located at ${HOME}/.m2/settings.xml
	* tmp_dir - where temporary files can be placed during the build process
	* 
	* ADDITIONAL_OPTIONS
	* 
	* Switches:
	*    -javadoc - Produce javadoc
	*    -autocode - Autocode f1 auto codable classes (--autocode supported for legacy)
	*    -out <src>> - Name of the jar to produce inside the zip (default is out.jar)
	*    -quiet - Don't log as much
	*    
	* Instructions:
	*    -copy_files <src> <tgt> - Copy files from the source_dir/<src> path into the <tgt> directory of the file tar.gz file.  This can be repeated for multiple files/dirs
	*    -copy_abs_files <src> <tgt> - Copy files from the absolute <src> path into the <tgt> directory of the file tar.gz file.  This can be repeated for multiple files/dirs
	*    -co <option> - Pass the option into the compiler, use repeated -co options to pass in multi-argument options
	*    -prepare_jsp_pattern <regex> - All files in the source matching the regex  will be passed through the f1 jsp engine for coding to classes
	*/
	public static void main(String arguments[]) throws IOException {

		//this is what to run:
		///home/rcooke/p4base/dev/java/vortex/vortexcompiler  /home/rcooke/compiler.tar.gz /home/deploy/.m2/settings.xml /tmp/vortexcompiler  -co -g:lines,vars -autocode
		try {
			IOH.log.setLevel(Level.OFF);
			if (arguments.length < 4) {
				System.err.println("usage: input_project_pom_file dest output_file_name maven_settings_file tmp_scratch_disk options...");
				System.exit(1);
			}

			List<String> compilerOptions = new ArrayList<String>();
			final String source = arguments[0];
			final String target = arguments[1];
			final String mavenSettings = arguments[2];
			final String tmpPath = arguments[3];

			final File srcDir = new File(source);
			final File srcPomFile = new File(srcDir, "pom.xml");
			final File tmpDir = new File(tmpPath);
			final File autocodeDir = new File(tmpDir, "autocode");
			final File javadocDir = new File(tmpDir, "javadoc");
			final File localRepoDir = new File(tmpDir, "repo");
			final File targetDir = new File(tmpDir, "classes");
			final File targetJunitDir = new File(tmpDir, "test-classes");
			final File junitReportTmpFile = new File(tmpDir, "test_results.txt");
			String testScript = "/home/build/tester/scripts/test.sh  ";
			File targetJar = new File(tmpDir, "out.jar");
			final File targetJunitJar = new File(tmpDir, "junit-out.jar");
			String junitReportFile = null;
			final File autocodeJar = new File(tmpDir, "autocode.jar");
			IOH.ensureEmptyDir(tmpDir);
			List<Tuple2<String, File>> copyFiles = new ArrayList<Tuple2<String, File>>();
			copyFiles.add(new Tuple2<String, File>("scripts", new File(srcDir, "src/main/scripts")));
			copyFiles.add(new Tuple2<String, File>("config", new File(srcDir, "src/main/config")));
			copyFiles.add(new Tuple2<String, File>("data", new File(srcDir, "src/main/data")));
			boolean autocodeEnabled = false;
			boolean onlyLocalResources = false;
			File javadoc = null;
			boolean verbose = true;
			List<String> jspPatterns = new ArrayList<String>();
			List<File> rscPaths = new ArrayList<File>();
			for (int i = 4; i < arguments.length; i++) {
				String arg = arguments[i];
				if ("-quiet".equals(arg)) {
					verbose = false;
				} else if ("-javadoc".equals(arg)) {
					javadoc = new File(arguments[++i]);
				} else if ("-autocode".equals(arg) || "--autocode".equals(arg)) {
					autocodeEnabled = true;
				} else if ("-out".equals(arg)) {
					targetJar = new File(tmpDir, arguments[++i]);
				} else if ("-copy_files".equals(arg)) {
					String s = arguments[++i];
					String t = arguments[++i];
					copyFiles.add(new Tuple2<String, File>(s, new File(source, t)));
				} else if ("-copy_abs_files".equals(arg)) {
					String s = arguments[++i];
					String t = arguments[++i];
					copyFiles.add(new Tuple2<String, File>(s, new File(t)));
				} else if ("-jsp_pattern".equals(arg)) {
					jspPatterns.add(arguments[++i]);
				} else if ("-junit_script".equals(arg)) {
					testScript = arguments[++i];
				} else if ("-junit".equals(arg)) {
					junitReportFile = arguments[++i];
				} else if ("-only_bundle_local".equals(arg)) {
					onlyLocalResources = true;
				} else if ("-compiler_option".equals(arg) || "-co".equals(arg)) {//legacy
					compilerOptions.add(arguments[++i]);
				} else if ("-resource_path".equals(arg)) {
					rscPaths.add(new File(arguments[++i]));
				} else {
					System.out.println("Unknown option: " + arg);
					System.exit(1);
				}
			}
			File outFile = new File(target);
			System.out.println("outjar=" + targetJar);
			IOH.ensureDir(localRepoDir);

			Duration d = new Duration();

			///////////////////
			//PROCESS POM FILES
			MavenManager pm = new MavenManager(verbose);
			pm.setMavenSettingsFile(new File(mavenSettings));
			pm.setLocalRepo(localRepoDir);
			PomInstance pom = pm.addLocalPom(srcPomFile);
			pm.resolve();
			//			final List<PomDependency> externalDependencies = new ArrayList<PomDependency>();
			final Collection<PomDependency> sink = pom.getAllDependencies(false);
			List<File> srcPaths = new ArrayList<File>(sink.size());
			List<File> tstPaths = new ArrayList<File>(sink.size());
			{
				final String projPath = pom.getDirectoryPath();
				addIfExists(projPath, pm.getSourcePath(), srcPaths);
				addIfExists(projPath, pm.getResourcesPath(), rscPaths);
				if (junitReportFile != null)
					addIfExists(projPath, pm.getTestPath(), tstPaths);
			}

			for (PomDependency dep : sink)
				if (dep.getTarget() != null) {
					final String projPath = dep.getTarget().getDirectoryPath();
					addIfExists(projPath, pm.getSourcePath(), srcPaths);
					addIfExists(projPath, pm.getResourcesPath(), rscPaths);
					if (junitReportFile != null)
						addIfExists(projPath, pm.getTestPath(), tstPaths);
					//				} else {
					//					externalDependencies.add(dep);
				}
			Map<Tuple2<String, String>, PomInstance> remoteDependencies = new LinkedHashMap<Tuple2<String, String>, PomInstance>();
			pm.resolveRemoteDependencies(pom, remoteDependencies, false, false);

			//			if (onlyLocalResources) {
			//				for (PomDependency i : pom.getDependencies(false)){
			//					pm.resolveRemoteDependencies(pom, remoteDependencies, false);
			//				}
			//			}

			List<File> sourceFiles = new SearchPath(srcPaths).search("*.java",
					SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);

			System.out.println("Compiling '" + pom.getName() + "' from " + sourceFiles.size() + " file(s) across " + srcPaths.size() + " path(s). Referencing " + rscPaths.size()
					+ " resource(s) and " + remoteDependencies.size() + " repo lib(s)");

			//System.out.println(SH.join(SH.NEWLINE, srcPaths));
			//System.out.println("#### Resource paths: ");
			//System.out.println(SH.join(SH.NEWLINE, rscPaths));
			//System.out.println("#### Remote files: ");
			//System.out.println(SH.join(SH.NEWLINE, remoteDependencies));
			List<File> dependencies = new ArrayList<File>();
			for (PomInstance dp : remoteDependencies.values())
				dependencies.add(dp.getLocalJarFile());

			IOH.deleteForce(targetDir);
			IOH.ensureDir(targetDir);
			if (onlyLocalResources) {
				Map<Tuple2<String, String>, PomInstance> toRemove = new LinkedHashMap<Tuple2<String, String>, PomInstance>();
				for (PomDependency i : pom.getAllDependencies(false))
					if (i.getVersion().endsWith("-SNAPSHOT"))
						pm.resolveRemoteDependencies(i.getTarget(), toRemove, false, false);
				for (PomInstance t : toRemove.values()) {
					PomInstance removed = remoteDependencies.remove(t.getGroupAndArtifactId());
					if (removed != null)
						System.out.println("Option -only_bundle_local set, skipping child's dependency: " + removed.getGroupAndArtifactId() + " ==> " + removed.getLocalJarFile());
				}
				System.out.println("Option -only_bundle_local set, bundling local files");
				final String projPath = pom.getDirectoryPath();
				List<File> localSrcPaths = new ArrayList<File>();
				List<File> localRscPaths = new ArrayList<File>();
				addIfExists(projPath, pm.getSourcePath(), localSrcPaths);
				addIfExists(projPath, pm.getResourcesPath(), localRscPaths);
				List<File> localSourceFiles = new SearchPath(localSrcPaths).search("*.java",
						SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
				compile(arguments, compilerOptions, dependencies, sourceFiles, rscPaths, targetDir, targetJar);
				IOH.deleteForce(targetDir);
				IOH.ensureDir(targetDir);
				dependencies.add(targetJar.getAbsoluteFile());
				compile(arguments, compilerOptions, dependencies, localSourceFiles, localRscPaths, targetDir, targetJar);
				//				remoteDependencies.clear();
				//				getLocalExternalDependencies(pm, pom, remoteDependencies, 0);
			} else {
				compile(arguments, compilerOptions, dependencies, sourceFiles, rscPaths, targetDir, targetJar);
			}

			////////////
			//UNIT TESTSJSP
			if (junitReportFile != null) {
				runTests(targetJunitDir, junitReportTmpFile, testScript, targetJar, targetJunitJar, junitReportFile, outFile, pm, pom);
			}

			boolean hasJsps = false;
			////////////
			//AUTOCODE && JSP
			if (autocodeEnabled || !jspPatterns.isEmpty()) {
				IOH.deleteForce(autocodeDir);
				IOH.ensureDir(autocodeDir);

				if (autocodeEnabled) {
					StringBuilder cp = new StringBuilder();
					for (File dp : dependencies)
						cp.append(File.pathSeparator).append(IOH.getFullPath(dp));
					String exec = EH.getJavaExecutableCommand(OH.EMPTY_STRING_ARRAY, PreAutocoder.class.getName(), AH.a(IOH.getFullPath(targetJar), IOH.getFullPath(autocodeDir)));
					exec = SH.replaceAll(exec, "-classpath ", "-classpath " + IOH.getFullPath(targetJar) + cp + File.pathSeparator);
					System.out.println("Pre-autocoding: " + exec);
					String[] parts = SH.splitContinous(' ', exec);
					Tuple3<Process, byte[], byte[]> execResult = EH.exec(SimpleExecutor.DEFAULT, parts);
					if (execResult.getA().exitValue() != 0) {
						System.err.println("Could not autocode:" + SH.NEWLINE + new String(execResult.getB()) + SH.NEWLINE + SH.NEWLINE + new String(execResult.getC()));
						System.exit(1);
					}
				}

				if (!jspPatterns.isEmpty()) {
					String[] a = AH.a(IOH.getFullPath(targetJar), IOH.getFullPath(autocodeDir));
					a = AH.appendList(a, jspPatterns);
					String exec = EH.getJavaExecutableCommand(OH.EMPTY_STRING_ARRAY, PreJspCoder.class.getName(), a);
					exec = SH.replaceAll(exec, "-classpath ", "-classpath " + IOH.getFullPath(targetJar) + File.pathSeparator);
					System.out.println("Processing Jsps (" + jspPatterns + ") " + exec);
					String[] parts = SH.splitContinous(' ', exec);
					Tuple3<Process, byte[], byte[]> execResult = EH.exec(SimpleExecutor.DEFAULT, parts);
					int exitValue = execResult.getA().exitValue();
					if (exitValue == 0) {
						hasJsps = true;
					} else if (exitValue == 99) {
						hasJsps = false;
					} else {
						System.err.println("Could not compile jsps:" + SH.NEWLINE + new String(execResult.getB()) + SH.NEWLINE + SH.NEWLINE + new String(execResult.getC()));
						System.exit(1);
					}
					if (verbose)
						System.out.println("Jsp output:" + SH.NEWLINE + new String(execResult.getB()) + SH.NEWLINE + SH.NEWLINE + new String(execResult.getC()));
				}

				File[] listFiles = new File(autocodeDir, "class").listFiles();
				if (listFiles.length > 0) {
					JarOutputStream autocodeOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(autocodeJar), 1024 * 1024 * 10));
					addEntries("", autocodeOut, listFiles);
					autocodeOut.close();
				} else
					IOH.delete(autocodeJar);
			}

			////////////
			//JAVADOC
			if (javadoc != null) {
				IOH.deleteForce(javadocDir);
				IOH.ensureDir(javadocDir);
				System.out.println("Generating Javadoc to: " + IOH.getFullPath(javadoc));
				String cmd = "javadoc -d " + IOH.getFullPath(javadocDir) + " -sourcepath " + SH.join(File.pathSeparator, srcPaths) + " -subpackages com";//TODO: should not just be com package
				//System.out.println(cmd);
				Tuple3<Process, byte[], byte[]> execResult = EH.exec(SimpleExecutor.DEFAULT, SH.split(' ', cmd));
				if (execResult.getA().exitValue() != 0) {
					System.err.println("Could not generate javadoc:" + SH.NEWLINE + new String(execResult.getB()) + SH.NEWLINE + SH.NEWLINE + new String(execResult.getC()));
					System.exit(1);
				}
				JarOutputStream javadocOut = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(javadoc), 1024 * 1024 * 10));
				addEntries("", javadocOut, javadocDir.listFiles());
				javadocOut.close();
			}

			//FINAL ZIP

			TarOutputStream zipout = new TarOutputStream(new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(outFile), 1024 * 1024 * 10)));
			if (junitReportFile != null) {
				zipout.putNextEntry(new TarEntry(junitReportFile, false).setModTime(System.currentTimeMillis()));
				zipout.write(IOH.readData(junitReportTmpFile));
				zipout.closeEntry();
			}
			zipout.putNextEntry(new TarEntry("lib", true).setModTime(System.currentTimeMillis()));
			zipout.closeEntry();
			for (PomInstance dp : remoteDependencies.values())
				if (dp.getLocalJarFile() != null)
					addEntries("lib", zipout, dp.getLocalJarFile());
			addEntries("lib", zipout, targetJar);
			if (autocodeEnabled || hasJsps && autocodeJar.exists()) {
				addEntries("lib", zipout, autocodeJar);
			}
			//			addEntries("", zipout, scriptsDir);
			//			addEntries("", zipout, configDir);
			for (Tuple2<String, File> cf : copyFiles) {
				String prefix = cf.getA();
				File copyFile = cf.getB();
				System.out.println("Copying: " + IOH.getFullPath(copyFile) + " ==> " + prefix);
				if (IOH.isDirectory(copyFile)) {
					for (File f : copyFile.listFiles())
						addEntries(prefix, zipout, f);
				} else if (IOH.isFile(copyFile)) {
					addEntries(prefix, zipout, copyFile);
				}
			}
			zipout.close();
			System.out.println("Generated '" + IOH.getFullPath(outFile) + "' in " + d.stamp(1));
		} catch (RuntimeException e) {
			System.err.println("Compiler error: ");
			if (e instanceof Legible) {
				System.err.println(((Legible) e).toLegibleString());
			}
			System.err.println(SH.printStackTrace(e));
			EH.systemExit(1);
		}
	}
	//	private static void getLocalExternalDependencies(MavenManager pm, PomInstance pom, Map<Tuple2<String, String>, PomInstance> remoteDependencies, int depth) throws IOException {
	//		for (PomDependency i : pom.getDependencies(false)) {
	//			if (i.getTarget() == null) {
	//				File localPomFile;
	//				localPomFile = pm.getRemotePom(pom, i, ".pom");
	//				PomInstance pom2 = pm.getRemotePom(localPomFile);
	//				if (pom2 != null && pom2.getLocalJarFile() != null) {
	//					if (remoteDependencies.put(pom2.getGroupAndArtifactId(), pom2) == null)
	//						getLocalExternalDependencies(pm, pom2, remoteDependencies, depth + 1);
	//				}
	//			}
	//		}
	//		//		if (pom.getParent() != null)
	//		//			getLocalExternalDependencies(pm, pom.getParent(), remoteDependencies, depth);
	//	}
	private static void runTests(final File targetJunitDir, final File junitReportTmpFile, String testScript, File targetJar, final File targetJunitJar, String junitReportFile,
			File outFile, MavenManager pm, PomInstance pom) throws IOException {
		//		final List<PomDependency> externalDependencies = new ArrayList<PomDependency>();
		final Collection<PomDependency> sink = pom.getAllDependencies(true);
		List<File> rscPaths = new ArrayList<File>(sink.size());
		List<File> srcPaths = new ArrayList<File>(sink.size());
		List<File> tstPaths = new ArrayList<File>(sink.size());
		{
			final String projPath = pom.getDirectoryPath();
			addIfExists(projPath, pm.getSourcePath(), srcPaths);
			addIfExists(projPath, pm.getResourcesPath(), rscPaths);
			if (junitReportFile != null)
				addIfExists(projPath, pm.getTestPath(), tstPaths);
		}
		for (PomDependency dep : sink) {
			System.out.println("Adding Dependency: " + dep.getKey());
			if (dep.getTarget() != null) {
				final String projPath = dep.getTarget().getDirectoryPath();
				addIfExists(projPath, pm.getSourcePath(), srcPaths);
				addIfExists(projPath, pm.getResourcesPath(), rscPaths);
				if (junitReportFile != null)
					addIfExists(projPath, pm.getTestPath(), tstPaths);
				//			} else {
				//				externalDependencies.add(dep);
			}
		}
		Map<Tuple2<String, String>, PomInstance> remoteDependencies = new LinkedHashMap<Tuple2<String, String>, PomInstance>();
		pm.resolveRemoteDependencies(pom, remoteDependencies, true, false);
		List<File> testDependencies = new ArrayList<File>();
		for (PomInstance dp : remoteDependencies.values())
			testDependencies.add(dp.getLocalJarFile());
		List<File> testFiles = new SearchPath(tstPaths).search("*.java", SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
		//		List<File> testDependencies = new ArrayList<File>(dependencies);
		testDependencies.add(targetJar);
		System.out.println("Preparing JUnit Test Jar '" + IOH.getFullPath(targetJunitJar) + " with " + testFiles.size() + " test file(s)");
		IOH.deleteForce(targetJunitDir);
		IOH.ensureDir(targetJunitDir);
		for (File s : tstPaths) {
			String root = IOH.getFullPath(s);
			List<File> sqlTests = new SearchPath(s).search("*.*", SearchPath.OPTION_CONTINUE_AFTER_FIRST_FIND | SearchPath.OPTION_IS_PATTERN | SearchPath.OPTION_RECURSE);
			for (File src : sqlTests) {
				if (src.getName().endsWith(".java"))
					continue;
				String relativePath = SH.stripPrefix(IOH.getFullPath(src), root, true);

				File dst = new File(IOH.getFullPath(targetJunitDir) + "/" + relativePath);
				IOH.ensureDir(dst.getParentFile());
				IOH.copy(src, dst, 1024, false);
			}
		}
		compile(OH.EMPTY_STRING_ARRAY, Collections.EMPTY_LIST, testDependencies, testFiles, Collections.EMPTY_LIST, targetJunitDir, targetJunitJar);

		StringBuilder testClasses = new StringBuilder();
		testClasses.append(testScript).append(' ');
		testClasses.append(IOH.getFullPath(targetJunitJar));
		testClasses.append(File.pathSeparator);
		testClasses.append(IOH.getFullPath(targetJar));
		testClasses.append(" -header ").append("host=").append(EH.getLocalHost());
		testClasses.append(" -header ").append("target=").append(outFile.getName());
		testClasses.append(" -out ");
		testClasses.append(IOH.getFullPath(junitReportTmpFile));
		//				testClasses.append(IOH.getFullPath(junitReportFile));
		for (File f : testFiles) {
			String className = parseClassName(f);
			if (className == null)
				continue;
			testClasses.append(" -test ");
			testClasses.append(' ').append(className);
		}
		System.out.println("Test Command on " + testFiles.size() + " file(s): " + testClasses);
		Tuple3<Process, byte[], byte[]> execResult = EH.exec(SimpleExecutor.DEFAULT, SH.splitContinous(' ', testClasses.toString()));
		if (execResult.getA().exitValue() != 0) {
			System.out.println("### One ore more Tests Failed ###");
			System.out.println(new String(execResult.getC()));
			System.out.println(IOH.readText(junitReportTmpFile));
			//					System.exit(1);
		} else {
			System.out.println("All Tests Passed.");
		}
	}
	private static void compile(String[] arguments, List<String> compilerOptions, List<File> dependencies, List<File> soureFiles, List<File> rscPaths, File targetDir,
			File targetJar) throws IOException {
		///////////
		//COMPILE
		List<String> fullPathSourceFiles = new ArrayList<String>();
		for (File file : soureFiles)
			fullPathSourceFiles.add(IOH.getFullPath(file));

		List<String> args = new ArrayList<String>(compilerOptions);
		if (targetDir != null) {
			args.add("-d");
			String targetDirFullPath = IOH.getFullPath(targetDir);
			args.add(targetDirFullPath);
		}
		if (!dependencies.isEmpty()) {
			args.add("-cp");
			StringBuilder cp = new StringBuilder();
			for (File dp : dependencies)
				cp.append(File.pathSeparator).append(IOH.getFullPath(dp));
			args.add(cp.substring(1));
		}

		args.addAll(fullPathSourceFiles);
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			StringBuilder sb = new StringBuilder();
			EnvironmentDump.dump(PomCompiler.class, arguments, null, sb);
			System.err.println("env: " + sb);
			throw new RuntimeException("compiler not available");
		}
		ByteArrayOutputStream stdout = new ByteArrayOutputStream();
		ByteArrayOutputStream stderr = new ByteArrayOutputStream();
		//System.out.println("Compiling " + soureFiles.size() + " java file(s)...");
		int result = compiler.run(null, stdout, stderr, args.toArray(new String[args.size()]));
		if (result != 0) {
			final String stdoutStr = new String(stdout.toByteArray());
			final String stderrStr = new String(stderr.toByteArray());
			System.err.println(stderrStr);
			System.out.println(stdoutStr);
			System.exit(result);
		}

		//JAR FILE - CLASSES
		if (targetDir != null) {
			////////////
			//JAR FILE 
			JarOutputStream jarout = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(targetJar), 1024 * 1024 * 10));
			addEntries("", jarout, targetDir.listFiles());
			for (File rscPath : rscPaths) {
				if (rscPath.isDirectory())
					addEntries("", jarout, rscPath.listFiles());
				else if (rscPath.isFile())
					addEntries("", jarout, rscPath);
			}
			jarout.close();
		}
	}
	private static void addIfExists(final String projPath, String sourcePath, List<File> sink) {
		final File srcPath = new File(projPath, sourcePath);
		if (srcPath.isDirectory())
			sink.add(srcPath);
	}
	static private void addEntries(final String prefix, ZipOutputStream sink, File... sources) throws IOException {
		for (File source : sources) {
			if (source.isFile()) {
				String fileName = prefix.length() > 0 && !prefix.endsWith("/") ? (prefix + "/" + source.getName()) : (prefix + source.getName());
				//System.out.println("Writting: " + fileName);
				ZipEntry entry = new ZipEntry(fileName);
				entry.setTime(source.lastModified());
				sink.putNextEntry(entry);
				sink.write(IOH.readData(source));
				sink.closeEntry();
				sink.flush();
			} else if (source.isDirectory()) {
				String pref = prefix;
				if (prefix.length() != 0)
					pref += "/" + source.getName();
				else
					pref = source.getName();
				for (File child : source.listFiles()) {
					addEntries(pref, sink, child);
				}
			}
		}
	}
	static private void addEntries(String prefix, TarOutputStream sink, File... sources) throws IOException {
		for (File source : sources) {
			if (source.isFile()) {
				TarEntry entry = new TarEntry(prefix.length() > 0 && !prefix.endsWith("/") ? (prefix + "/" + source.getName()) : (prefix + source.getName()), false);
				entry.setModTime(source.lastModified());
				entry.setIsModeExecutableByOwner(source.canExecute());
				entry.setIsModeWriteableByOwner(source.canWrite());
				entry.setIsModeReadableByOwner(source.canRead());
				sink.putNextEntry(entry);
				sink.write(IOH.readData(source));
				sink.closeEntry();
			} else if (source.isDirectory()) {
				TarEntry entry = new TarEntry(prefix.length() > 0 && !prefix.endsWith("/") ? (prefix + "/" + source.getName()) : (prefix + source.getName()), true);
				entry.setModTime(source.lastModified());
				sink.putNextEntry(entry);
				sink.closeEntry();
				if (prefix.length() != 0)
					prefix += "/" + source.getName();
				else
					prefix = source.getName();
				for (File child : source.listFiles()) {
					addEntries(prefix, sink, child);
				}
			}
		}
	}
	private static String parseClassName(File f) {
		try {
			String[] lines = SH.splitLines(IOH.readText(f));
			String pck = null;
			String clz = null;
			for (String line : lines) {
				line = SH.trim(line);
				if (pck == null) {
					pck = SH.strip(line, "package", ";", false);
					if (pck == line)
						pck = null;
				} else {
					if (line.indexOf(" interface ") != -1)
						return null;
					int i = line.indexOf(" class ");
					if (i != -1) {
						clz = line.substring(i + " class ".length());
						clz = SH.trim(clz);
						clz = SH.beforeFirst(clz, ' ', clz);
						clz = SH.beforeFirst(clz, '\t', clz);
						clz = SH.beforeFirst(clz, '<', clz);
						break;
					}
				}
			}
			if (pck == null)
				throw new RuntimeException("Expecting package declaration in file: " + IOH.getFullPath(f));
			if (clz == null)
				throw new RuntimeException("Expecting class declaration in file: " + IOH.getFullPath(f));
			return SH.trim(pck) + "." + SH.trim(clz);
		} catch (IOException e) {
			throw new RuntimeException("Error parsing classname for file: " + IOH.getFullPath(f), e);
		}
	}
}
