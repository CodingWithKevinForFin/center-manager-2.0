package com.f1.suite.web.portal.impl;

import java.util.List;

public interface WebColumnEditConfig {

	public static final byte EDIT_DISABLED = 0;
	public static final byte EDIT_READONLY = 1;
	public static final byte EDIT_TEXTFIELD = 2;
	public static final byte EDIT_SELECT = 3;
	public static final byte EDIT_COMBOBOX = 4;
	public static final byte EDIT_DATERANGE_FIELD = 5;
	public static final byte EDIT_DATE_FIELD = 6;
	public static final byte EDIT_CHECKBOX = 7;
	public static final byte EDIT_NUMERIC = 8;
	public static final byte EDIT_MASKED = 9;

	public String getEditId();
	public String getColumnId();
	String getEditOptionFormula();
	List<String> getEditSelectOptions();
	byte getEditType();
	int getEnableLastNDays();
	boolean getDisableFutureDays();
}
