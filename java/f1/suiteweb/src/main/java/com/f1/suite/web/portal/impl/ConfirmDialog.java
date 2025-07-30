package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;

// implemented by alert and confirm dialogs
public interface ConfirmDialog {
	public static final byte TYPE_MESSAGE = 1;
	public static final byte TYPE_YES_NO = 2;
	public static final byte TYPE_OK_CANCEL = 3;
	public static final byte TYPE_WAIT_WITH_CANCEL = 4;
	public static final byte TYPE_ALERT = 5;
	public static final byte TYPE_USER_ALERT = 6;
	public static final byte TYPE_OK_CUSTOM = -1;
	public static final String ID_YES = "y";
	public static final String ID_NO = "n";
	public static final String ID_CLOSE = "close";

	public ConfirmDialog addButton(String id, String buttonText);
	public ConfirmDialog addButton(String id, String buttonText, int location);
	public void clearButtons();
	public void fireButton(String id);
	public void fireYesButton();
	public String getCallback();
	public Object getCorrelationData();
	public Object getInputFieldValue();
	public FormPortletField<? extends Object> getInputField();
	public ConfirmDialog setCallback(String string);
	public ConfirmDialog setCorrelationData(Object correlationData);
	public void setDetails(String details);
	public void setFollowupDialog(String id, String string, Portlet portlet);
	public ConfirmDialog setInputFieldValue(Object value);
	public ConfirmDialog setStyle(byte style);
	public ConfirmDialog setText(String text);
	public ConfirmDialog updateButton(String id, String buttonText);
	public void addDialogListener(ConfirmDialogListener cfl);
	// avoid conflict with abstract portlet's close()
	public void closeDialog();
}
