/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.table.impl;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.f1.base.Lockable;
import com.f1.base.LockedException;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.ComparableComparator;

public class BasicWebCellFormatter implements WebCellFormatter, Lockable {

	public static final int DEFAULT_DEFAULT_WIDTH = 100;
	public static final boolean DEFAULT_DEFAULT_CLICKABLE = false;
	public static final boolean DEFAULT_DEFAULT_ONECLICK = false;
	public static final String DEFAULT_CSS_CLASS = "cell_text";

	protected String nullValue = "";
	private String defaultColumnCssClass = DEFAULT_CSS_CLASS;
	private Comparator comparator = ComparableComparator.INSTANCE;
	private int defaultWidth = DEFAULT_DEFAULT_WIDTH;
	private boolean defaultClickable = DEFAULT_DEFAULT_CLICKABLE;
	private boolean defaultOneClick = DEFAULT_DEFAULT_ONECLICK;
	private boolean locked;
	private Map<Object, String> conditionalCellStyles = null;
	private String conditionalDefault;
	private String defaultHeaderStyle;

	public BasicWebCellFormatter setNullValue(String nullValue) {
		LockedException.assertNotLocked(this);
		this.nullValue = nullValue;
		return this;
	}
	public BasicWebCellFormatter setCssClass(String cssClass) {
		LockedException.assertNotLocked(this);
		this.defaultColumnCssClass = cssClass;
		return this;
	}
	public BasicWebCellFormatter setHeaderStyle(String headerStyle) {
		LockedException.assertNotLocked(this);
		this.defaultHeaderStyle = headerStyle;
		return this;
	}

	@Override
	public StringBuilder formatCellToText(Object o, StringBuilder sb) {
		return (null == o) ? sb.append(nullValue) : s(o, sb);
	}
	@Override
	public String formatCellToText(Object o) {
		return (null == o) ? nullValue : s(o);
	}

	@Override
	public Comparable getOrdinalValue(Object row) {
		return row instanceof Comparable || row == null ? (Comparable) row : formatCellToText(row);
	}

	@Override
	public boolean isString() {
		return true;
	}

	@Override
	public String formatCellToHtml(Object o) {
		return (null == o) ? nullValue : o instanceof String ? WebHelper.escapeHtmlIncludeBackslash((String) o, new StringBuilder()).toString() : s(o);
	}

	@Override
	public Comparator getComparator() {
		return comparator;
	}

	public BasicWebCellFormatter setComparator(Comparator comparator) {
		LockedException.assertNotLocked(this);
		this.comparator = comparator;
		return this;
	}

	@Override
	public String getDefaultColumnCssClass() {
		return defaultColumnCssClass;
	}

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (null == value)
			sb.append(nullValue);
		else if (value instanceof String)
			WebHelper.escapeHtmlIncludeBackslash((String) value, sb);
		else
			s(value, sb);
		if (conditionalCellStyles != null) {
			String style = conditionalCellStyles.get(value);
			if (style != null)
				cellStyle.append(style);
			else if (conditionalDefault != null)
				cellStyle.append(conditionalDefault);
		} else if (conditionalDefault != null)
			cellStyle.append(conditionalDefault);
	}
	protected StringBuilder s(Object o, StringBuilder sb) {
		return SH.s(o, sb);
	}
	protected String s(Object o) {
		if (o != null && o.getClass().isArray()) {
			StringBuilder sb = SH.getSimpleName(o.getClass().getComponentType(), new StringBuilder());
			sb.append('[').append(Array.getLength(o)).append(']');
			return sb.toString();
		}
		return SH.s(o);
	}

	@Override
	public StringBuilder formatCellToExcel(Object value, StringBuilder sb) {
		return formatCellToText(value, sb);
	}
	@Override
	public String formatCellToExcel(Object data) {
		return formatCellToText(data);
	}
	@Override
	public int getDefaultWidth() {
		return defaultWidth;
	}

	public BasicWebCellFormatter setDefaultWidth(int defaultWidth) {
		LockedException.assertNotLocked(this);
		this.defaultWidth = defaultWidth;
		return this;
	}
	@Override
	public boolean getDefaultClickable() {
		return defaultClickable;
	}

	public BasicWebCellFormatter setDefaultClickable(boolean defaultClickable) {
		LockedException.assertNotLocked(this);
		this.defaultClickable = defaultClickable;
		return this;
	}
	public BasicWebCellFormatter setDefaultOneClick(boolean defaultOneClick) {
		LockedException.assertNotLocked(this);
		this.defaultOneClick = defaultOneClick;
		return this;
	}
	public void lock() {
		this.locked = true;
	}
	public BasicWebCellFormatter lockFormatter() {
		lock();
		return this;
	}

	public boolean isLocked() {
		return this.locked;
	}

	public BasicWebCellFormatter addConditionalString(String style, Object... values) {
		LockedException.assertNotLocked(this);
		if (this.conditionalCellStyles == null)
			this.conditionalCellStyles = new HashMap<Object, String>();
		for (Object value : values)
			CH.putOrThrow(this.conditionalCellStyles, value, style);
		return this;
	}
	public BasicWebCellFormatter addCssClass(String cssClass) {
		LockedException.assertNotLocked(this);
		if (SH.isnt(this.defaultColumnCssClass))
			this.defaultColumnCssClass = cssClass;
		else
			this.defaultColumnCssClass += " " + cssClass;
		return this;
	}
	public BasicWebCellFormatter addConditionalDefault(String string) {
		LockedException.assertNotLocked(this);
		this.conditionalDefault = string;
		return this;
	}
	@Override
	public String formatCellForSearch(Object data) {
		return formatCellToText(data);
	}
	@Override
	public StringBuilder formatCellForSearch(Object data, StringBuilder sb) {
		return formatCellToText(data, sb);
	}
	@Override
	public String getDefaultHeaderStyle() {
		return this.defaultHeaderStyle;
	}
	@Override
	public boolean getDefaultOneClick() {
		// TODO Auto-generated method stub
		return defaultOneClick;
	}
}
