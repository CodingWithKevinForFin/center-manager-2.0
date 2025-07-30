package com.f1.tester.coverage;

import java.util.Map;
import java.util.TreeMap;
import com.f1.utils.CH;

public class CodeCoverageResults {

	private final Map<String, CodeCoverageClass> classes = new TreeMap<String, CodeCoverageClass>();
	private String sourcePath;

	public CodeCoverageResults() {
	}

	public void addClass(CodeCoverageClass clazz) {
		clazz.setGroup(this);
		CH.putOrThrow(classes, clazz.getName(), clazz);
	}

	public CodeCoverageClass getClass(String className) {
		return classes.get(className);
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public Map<String, CodeCoverageClass> getClasses() {
		return classes;
	}

	public void reset() {
		classes.clear();
	}

}
