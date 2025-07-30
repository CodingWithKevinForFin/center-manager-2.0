package com.f1.tester.coverage;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import com.f1.utils.structs.Tuple2;

public class CodeCoverageMethod extends CodeCoverageItem {
	private final SortedMap<Integer, CodeCoverageLine> lines = new TreeMap<Integer, CodeCoverageLine>();
	private CodeCoverageClass ccClass;
	private final String name;

	public CodeCoverageMethod(String name, int lineNumber) {
		super(lineNumber);
		this.name = name;
	}

	public boolean addLine(CodeCoverageLine line) {
		line.setMethod(this);
		if (lines.containsKey(line.getLineNumber()))
			return false;
		lines.put(line.getLineNumber(), line);
		return true;
	}

	public String getName() {
		return name;
	}

	public Collection<CodeCoverageLine> getLines() {
		return lines.values();
	}

	@Override
	public CodeCoverageClass getCodeCoverageClass() {
		return ccClass;
	}

	@Override
	CodeCoverageItem getParent() {
		return this.ccClass;
	}

	@Override
	Iterable<? extends CodeCoverageItem> getChildren() {
		return lines.values();
	}

	public void setCodeCoverageClass(CodeCoverageClass ccClass) {
		this.ccClass = ccClass;
	}

	public void getSpans(List<Tuple2<CodeCoverageItem, CodeCoverageItem>> spans) {
		if (isCovered())
			return;
		if (isMissed()) {
			spans.add(new Tuple2<CodeCoverageItem, CodeCoverageItem>(this, lines.get(lines.size() - 1)));
			return;
		}
		CodeCoverageLine spanStart = null, spanEnd = null;
		for (CodeCoverageLine l : getLines()) {
			if (l.isCovered()) {
				if (spanStart != null) {
					spans.add(new Tuple2<CodeCoverageItem, CodeCoverageItem>(spanStart, spanEnd));
					spanStart = spanEnd = null;
				}
			} else {
				if (spanStart == null)
					spanStart = l;
				spanEnd = l;
			}
		}
		if (spanStart != null)
			spans.add(new Tuple2<CodeCoverageItem, CodeCoverageItem>(spanStart, spanEnd));
	}
}
