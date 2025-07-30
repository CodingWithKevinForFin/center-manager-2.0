/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table;

import java.util.Comparator;

import com.f1.base.Lockable;

public interface WebCellFormatter extends Lockable {

	public StringBuilder formatCellToText(Object data, StringBuilder sb);

	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder cellStyle);

	public StringBuilder formatCellToExcel(Object data, StringBuilder sb);

	public Comparable getOrdinalValue(Object data);

	//indicates that no string builder will be necessary to do the search
	public boolean isString();

	public Comparator getComparator();

	public String getDefaultColumnCssClass();

	public String formatCellToExcel(Object data);
	public String formatCellToText(Object o);
	public String formatCellToHtml(Object data);

	public int getDefaultWidth();
	public boolean getDefaultClickable();
	public boolean getDefaultOneClick();

	public WebCellFormatter setDefaultWidth(int i);

	public String formatCellForSearch(Object data);
	public StringBuilder formatCellForSearch(Object data, StringBuilder sb);

	public String getDefaultHeaderStyle();
}
