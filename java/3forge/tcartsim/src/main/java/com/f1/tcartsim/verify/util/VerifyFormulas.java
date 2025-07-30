/**
 * 
 */
package com.f1.tcartsim.verify.util;

/**
 * @author george
 * 
 */
public class VerifyFormulas {

	public static double vwap(double notionalValue, long filledSize) {
		return notionalValue / filledSize;
	}
	public static double value(long size, double px) {
		return (double) size * px;
	}

	public static double avgPx(double value, long size) {
		if (size == 0) {
			return 0d;
		}
		return value / (double) size;
	}

	public static double percentFilled(long totalFilled, long totalTarget) {
		double pFilled = 0;
		if (totalTarget == 0) {
			pFilled = 0;
		} else {
			pFilled = (double) totalFilled * 100.0d / (double) totalTarget;
		}
		return pFilled;
	}

	public static double avgFillSize(long totalFilled, long nFills) {
		double avgFillSize = 0;
		if (nFills == 0) {
			avgFillSize = 0;
		} else {
			avgFillSize = (double) totalFilled / (double) nFills;
		}
		return avgFillSize;
	}

	public static long leaves(long totalDistributed, long totalChildCanceled, long totalChildFilled) {
		long leaves = totalDistributed - totalChildCanceled - totalChildFilled;
		if (leaves < 0)
			return 0;
		return leaves;
	}

	public static long open(long size, long canceledSize, long filledSize) {
		long open = size - canceledSize - filledSize;
		if (open < 0)
			return 0;
		return open;
	}
}
