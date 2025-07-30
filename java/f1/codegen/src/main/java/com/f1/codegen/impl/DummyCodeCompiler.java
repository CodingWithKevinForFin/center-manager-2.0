package com.f1.codegen.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.codegen.ClassPathModifier;
import com.f1.codegen.CodeCompiler;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class DummyCodeCompiler implements CodeCompiler {

	public static final String SOURCE_FILEEXT = ".java";
	public static final String CLASS_FILEEXT = ".class";
	public static final String SRC_DIR = "src";
	public static final String DST_DIR = "class";

	final private static Logger log = Logger.getLogger(DummyCodeCompiler.class.getName());
	private static final String PROPERTY_CLASSPATH = "java.class.path";

	final private List<String> compilerClassPath = new ArrayList<String>();
	final private File dstDirectory;
	final private File srcDirectory;
	final private File baseDirectory;

	public DummyCodeCompiler(String basePath) throws IOException {
		this.baseDirectory = new File(basePath);
		this.srcDirectory = new File(this.baseDirectory, SRC_DIR);
		this.dstDirectory = new File(this.baseDirectory, DST_DIR);
		compilerClassPath.addAll(SH.splitToList(File.pathSeparator, System.getProperty(PROPERTY_CLASSPATH)));
		if (srcDirectory.isDirectory())
			compilerClassPath.add(srcDirectory.toString());
		if (dstDirectory.isDirectory())
			compilerClassPath.add(dstDirectory.toString());
		LH.info(log, "classpath: ", compilerClassPath);
		LH.info(log, "src/class: ", IOH.getFullPath(srcDirectory), "   ", IOH.getFullPath(dstDirectory));
		if (IOH.isDirectory(getDestinationDirectory()))
			ClassPathModifier.addFile(getDestinationDirectory());
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
	public void addToClassPath(String cp) {
		compilerClassPath.add(cp);
	}

	@Override
	public List<Boolean> compile(List<Tuple2<String, String>> classNamesAndcode) throws IOException {
		List<Boolean> r = new ArrayList<Boolean>(classNamesAndcode.size());
		for (int i = classNamesAndcode.size(); i > 0; i--)
			r.add(Boolean.TRUE);
		return r;
	}

	@Override
	public List<String> getCompilerClassPath() {
		return compilerClassPath;
	}

	@Override
	public boolean compile(String className, String code) throws IOException {
		return true;
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
