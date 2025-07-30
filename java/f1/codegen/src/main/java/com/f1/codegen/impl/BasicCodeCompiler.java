/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.f1.codegen.ClassPathModifier;
import com.f1.codegen.CodeCompiler;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class BasicCodeCompiler implements CodeCompiler {
	public static final String SOURCE_FILEEXT = ".java";
	public static final String CLASS_FILEEXT = ".class";
	public static final String SRC_DIR = "src";
	public static final String DST_DIR = "class";

	final private static Logger log = Logger.getLogger(BasicCodeCompiler.class.getName());
	private static final String PROPERTY_CLASSPATH = "java.class.path";

	final private List<String> compilerClassPath = new ArrayList<String>();
	final private File dstDirectory;
	final private File srcDirectory;
	final private File baseDirectory;
	final private JavaCompiler compiler;

	public BasicCodeCompiler(String basePath) throws IOException {
		this.baseDirectory = new File(basePath);
		this.srcDirectory = new File(this.baseDirectory, SRC_DIR);
		this.dstDirectory = new File(this.baseDirectory, DST_DIR);
		IOH.ensureDir(baseDirectory);
		IOH.ensureDir(srcDirectory);
		IOH.ensureDir(dstDirectory);
		compilerClassPath.addAll(SH.splitToList(File.pathSeparator, System.getProperty(PROPERTY_CLASSPATH)));
		compilerClassPath.add(srcDirectory.toString());
		compilerClassPath.add(dstDirectory.toString());
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new RuntimeException("System Java Compiler not found");
		LH.fine(log, "classpath: ", compilerClassPath);
		LH.fine(log, "src/class: ", IOH.getFullPath(srcDirectory), "   ", IOH.getFullPath(dstDirectory));
		ClassPathModifier.addFile(getDestinationDirectory());
	}

	@Override
	public void addToClassPath(String cp) {
		compilerClassPath.add(cp);
	}

	@Override
	public List<Boolean> compile(List<Tuple2<String, String>> classAndCode) throws IOException {
		int size = classAndCode.size();
		List<File> srcFiles = new ArrayList<File>(size);
		List<File> dstFiles = new ArrayList<File>(size);
		List<Boolean> results = new ArrayList<Boolean>(size);
		int targetCount = 0;
		for (int i = 0; i < size; i++) {
			String className = classAndCode.get(i).getA();
			String code = classAndCode.get(i).getB();
			File srcFile = getSourceFile(className);
			File dstFile = getClassFile(className);
			boolean result = false;
			dstFiles.add(dstFile);
			srcFiles.add(srcFile);
			IOH.ensureDir(srcFile.getParentFile());
			try {
				if (dstFile.exists()) {
					final String origFile = IOH.readText(srcFile);
					if (origFile != null && origFile.equals(code))
						result = Boolean.TRUE;
					else
						IOH.delete(dstFile);
				}
				if (!result)
					IOH.writeText(srcFile, code);
			} catch (final Exception e) {
				throw new RuntimeException("Error with '" + srcFile + "' in preparing compilation of " + className, e);
			}
			if (!result)
				targetCount++;
			results.add(result);
		}
		if (targetCount == 0)
			return results;
		if (log.isLoggable(Level.INFO))
			LH.info(log, "going to compile ", targetCount, " file(s)");
		final String a[] = new String[4 + targetCount];
		a[0] = "-d";
		a[1] = dstDirectory.toString();
		a[2] = "-classpath";
		a[3] = SH.join(File.pathSeparatorChar, compilerClassPath);
		for (int i = 0, n = 4; i < size; i++)
			if (!results.get(i))
				a[n++] = srcFiles.get(i).toString();
		try {
			ByteArrayOutputStream stdout = new ByteArrayOutputStream();
			ByteArrayOutputStream stderr = new ByteArrayOutputStream();

			int result = compiler.run(null, stdout, stderr, a);
			final String stdoutStr = new String(stdout.toByteArray());
			final String stderrStr = new String(stderr.toByteArray());
			if (result != 0) {
				LH.severe(log, "compiling failed for some classes:, return code: ", result, " , stdout: ", stdoutStr, ", stderr: ", stderrStr);
				throw new RuntimeException("Compiling failed, with error code " + result);
			}
			for (int i = 0; i < size; i++)
				if (!dstFiles.get(i).exists())
					LH.severe(log, "File not found: ", dstFiles.get(i));
				else
					results.set(i, true);

			if (log.isLoggable(Level.INFO))
				LH.info(log, "compiled ", targetCount, " class(es)");
			if (log.isLoggable(Level.FINE)) {
				if (SH.is(stdoutStr))
					LH.fine(log, "out: ", stdoutStr);
				if (SH.is(stderrStr))
					LH.fine(log, "err: ", stderrStr);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error compiling " + targetCount + " file(s)", e);
		}
		return results;

	}

	@Override
	public List<String> getCompilerClassPath() {
		return compilerClassPath;
	}

	@Override
	public File getDestinationDirectory() {
		return dstDirectory;
	}

	@Override
	public File getSourceDirectory() {
		return srcDirectory;
	}

	@Override
	public File getBaseDirectory() {
		return baseDirectory;
	}

	@Override
	public boolean compile(String className, String code) throws IOException {
		ArrayList<Tuple2<String, String>> l = new ArrayList<Tuple2<String, String>>(1);
		l.add(new Tuple2<String, String>(className, code));
		return compile(l).get(0);

	}

	@Override
	public File getSourceFile(String className) {
		String dir = className.replace('.', File.separatorChar);
		return new File(srcDirectory, dir + SOURCE_FILEEXT);
	}
	@Override
	public File getClassFile(String className) {
		String dir = className.replace('.', File.separatorChar);
		return new File(dstDirectory, dir + CLASS_FILEEXT);
	}

}
