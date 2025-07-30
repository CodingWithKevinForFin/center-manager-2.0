package com.f1.utils.sql;


public interface SqlPlanListener {

	public void onStart(String query);
	public void onStep(String step, String msg);
	public void onEnd(Object result);
	public void onEndWithError(Exception e);
}
