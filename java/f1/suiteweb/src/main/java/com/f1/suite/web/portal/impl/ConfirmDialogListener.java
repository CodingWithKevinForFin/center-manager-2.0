package com.f1.suite.web.portal.impl;

public interface ConfirmDialogListener {

	//return true if okay to close
	public boolean onButton(ConfirmDialog source, String id);

}
