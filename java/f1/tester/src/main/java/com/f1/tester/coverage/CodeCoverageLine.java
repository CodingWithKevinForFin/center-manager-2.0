package com.f1.tester.coverage;

import com.f1.utils.EmptyCollection;

public class CodeCoverageLine extends CodeCoverageItem implements Comparable<CodeCoverageLine> {

	private CodeCoverageMethod method;
	private long locationUid;

	public CodeCoverageLine(int lineNumber) {
		super(lineNumber);
		this.status = MISSED;
	}

	public void setMethod(CodeCoverageMethod method) {
		this.method = method;
	}

	public long getLocationUid() {
		return locationUid;
	}

	public void setLocationUid(long locationUid_) {
		locationUid = locationUid_;
	}

	public CodeCoverageMethod getMethod() {
		return method;
	}

	public void setCovered(boolean covered) {
		byte t = covered ? COVERED : MISSED;
		if (t == status)
			return;
		status = t;
		method.clearStatus();
	}

	@Override
	CodeCoverageItem getParent() {
		return method;
	}

	@Override
	Iterable<CodeCoverageItem> getChildren() {
		return EmptyCollection.INSTANCE;
	}

	@Override
	public CodeCoverageClass getCodeCoverageClass() {
		return getMethod().getCodeCoverageClass();
	}

	@Override
	public int compareTo(CodeCoverageLine o) {
		return getLineNumber() - o.getLineNumber();
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass() && compareTo((CodeCoverageLine) o) == 0;
	}

}
