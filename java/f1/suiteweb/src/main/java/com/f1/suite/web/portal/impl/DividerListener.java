package com.f1.suite.web.portal.impl;

public interface DividerListener {

	void onDividerMovingStarted(DividerPortlet dividerPortlet, double currentOffset);
	void onDividerMoving(DividerPortlet dividerPortlet, double currentOffset);
	void onDividerRestored(DividerPortlet dividerPortlet);

	void onDividerMoved(DividerPortlet dividerPortlet, double currentOffset);
	void onDividerDblClick();

}
