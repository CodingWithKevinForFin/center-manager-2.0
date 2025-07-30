/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.f1.utils.structs.Tuple2;

public interface CodeCompiler {

	public File getDestinationDirectory();

	public File getSourceDirectory();

	public File getBaseDirectory();

	public void addToClassPath(String cp);

	public List<Boolean> compile(List<Tuple2<String, String>> classNamesAndcode) throws IOException;

	public List<String> getCompilerClassPath();

	public boolean compile(String className, String code) throws IOException;

	public File getSourceFile(String fullClassName);

	public File getClassFile(String className);

}
