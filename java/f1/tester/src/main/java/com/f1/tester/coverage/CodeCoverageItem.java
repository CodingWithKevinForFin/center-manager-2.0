package com.f1.tester.coverage;

public abstract class CodeCoverageItem {
	public static final byte UNKNOWN = 0;
	public static final byte COVERED = 1;
	public static final byte MISSED = 2;
	public static final byte PARTIAL = 3;

	protected byte status = UNKNOWN;
	private final int lineNumber;

	public CodeCoverageItem(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public byte getStatus() {
		if (status == UNKNOWN)
			status = calcStatus(getChildren(), status);
		return status;
	}

	protected void clearStatus() {
		if (status == UNKNOWN)
			return;
		status = UNKNOWN;
		final CodeCoverageItem p = getParent();
		if (p != null)
			p.clearStatus();
	}

	abstract CodeCoverageItem getParent();

	abstract Iterable<? extends CodeCoverageItem> getChildren();

	private byte calcStatus(Iterable<? extends CodeCoverageItem> items, byte status) {
		byte s;
		for (CodeCoverageItem i : items)
			switch (s = i.getStatus()) {
				case UNKNOWN :
					throw new IllegalStateException(i.toString());
				case PARTIAL :
					return PARTIAL;
				case COVERED :
				case MISSED :
					if (status == UNKNOWN)
						status = s;
					else if (status != s)
						return PARTIAL;
			}

		if (status == UNKNOWN)
			return COVERED;
		return status;

	}

	public boolean isMissed() {
		return getStatus() == MISSED;
	}

	public boolean isCovered() {
		return getStatus() == COVERED;
	}

	public boolean isPartial() {
		return getStatus() == PARTIAL;
	}

	public final int getLineNumber() {
		return lineNumber;
	}

	public abstract CodeCoverageClass getCodeCoverageClass();

}
