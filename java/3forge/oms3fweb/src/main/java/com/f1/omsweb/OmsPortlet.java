package com.f1.omsweb;

public interface OmsPortlet {

	public void onOrder(WebOmsOrder order);
	public void onExecution(WebOmsExecution execution);
}
