package com.f1.suite.web.portal.impl;

public interface RootPortletDialogListener {

	public void onDialogClickoutside(RootPortletDialog dialog);

	public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b);

	public void onDialogMoved(RootPortletDialog rootPortletDialog);

	public void onDialogClosed(RootPortletDialog rootPortletDialog);

	public void onUserCloseDialog(RootPortletDialog rootPortletDialog);
}
