package com.f1.tester.coverage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;

public class CodeCoverageClass extends CodeCoverageItem {
	private final List<CodeCoverageMethod> methods = new ArrayList<CodeCoverageMethod>();
	private final String name;
	private final String sourceCodePath;
	private final IntKeyMap<CodeCoverageLine> lines = new IntKeyMap<CodeCoverageLine>();
	private CodeCoverageResults group;
	private String[] sourceCode;

	public CodeCoverageClass(String name, String sourceCodePath, int lineNumber) {
		super(lineNumber);
		this.methods.addAll(methods);
		this.name = name;
		this.sourceCodePath = sourceCodePath;
	}

	public void addMethod(CodeCoverageMethod method) {
		method.setCodeCoverageClass(this);
		this.methods.add(method);
		for (CodeCoverageLine line : method.getLines())
			lines.put(line.getLineNumber(), line);
	}

	public List<CodeCoverageMethod> getMethods() {
		return methods;
	}

	public String getName() {
		return this.name;
	}

	@Override
	CodeCoverageItem getParent() {
		return null;
	}

	@Override
	Iterable<? extends CodeCoverageItem> getChildren() {
		return methods;
	}

	public CodeCoverageLine getLineAt(int line) {
		return lines.get(line);
	}

	public void setGroup(CodeCoverageResults group) {
		this.group = group;
	}

	public CodeCoverageResults getGroup() {
		return group;
	}

	public String[] getSourceCode(int start, int end) {
		final String[] sc = getSourceCode();
		if (sc == null)
			return null;
		final String[] r = new String[end - start + 1];
		for (int i = 0; i < r.length; i++)
			r[i] = sc[i + start - 1];
		return r;
	}

	public String[] getSourceCode() {
		if (sourceCode == null) {
			File f = getSourceFile();
			if (!f.exists())
				sourceCode = OH.EMPTY_STRING_ARRAY;
			else
				try {
					sourceCode = SH.split('\n', SH.replaceAll(IOH.readText(f), '\r', ""));
				} catch (IOException e) {
					throw OH.toRuntime(e);
				}
		}
		return sourceCode == OH.EMPTY_STRING_ARRAY ? null : sourceCode;
	}

	@Override
	public CodeCoverageClass getCodeCoverageClass() {
		return this;
	}

	public File getSourceFile() {
		File f = new File(group.getSourcePath(), sourceCodePath);
		return f;
	}

}
