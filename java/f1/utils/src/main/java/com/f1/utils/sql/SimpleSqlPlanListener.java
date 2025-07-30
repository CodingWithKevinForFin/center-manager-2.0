package com.f1.utils.sql;

import com.f1.utils.OH;
import com.f1.utils.SH;

public class SimpleSqlPlanListener implements SqlPlanListener {

	private long start = System.nanoTime();

	@Override
	public void onStep(String step, String msg) {
		System.out.println(elapsed() + step + ": " + msg);
	}

	@Override
	public void onStart(String query) {
		System.out.println(elapsed() + "START: " + query);
	}

	private String elapsed() {
		return ((System.nanoTime() - start) / 1000L) + " MICROS: ";
	}

	@Override
	public void onEnd(Object result) {
		System.out.println(elapsed() + "END: " + OH.getSimpleClassName(result));
	}

	@Override
	public void onEndWithError(Exception e) {
		System.out.println(elapsed() + "ERROR: " + SH.printStackTrace(e));
	}

}
