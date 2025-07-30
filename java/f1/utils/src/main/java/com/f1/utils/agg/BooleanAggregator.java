/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.agg;

public class BooleanAggregator {
	private int countTrue;
	private int countFalse;
	private int countNull;

	public void add(boolean b) {
		if (b)
			countTrue++;
		else
			countFalse++;
	}

	public void add(Boolean b) {
		if (b == null)
			countNull++;
		else if (b)
			countTrue++;
		else
			countFalse++;
	}

	public int getCountTrue() {
		return countTrue;
	}

	public int getCountFalse() {
		return countFalse;
	}

	public int getCountNull() {
		return countNull;
	}

	public boolean hasTrue() {
		return countTrue > 0;
	}

	public boolean hasFalse() {
		return countFalse > 0;
	}

	public boolean hasBoth() {
		return hasTrue() && hasFalse();
	}

	public boolean hasNull() {
		return countNull > 0;
	}

	public int getCount() {
		return countTrue + countFalse + countNull;
	}

}
