package com.f1.suite.web.table.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.suite.web.table.WebCellFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.ComparableComparator;

public class WebCellStyleWrapperFormatter implements WebCellFormatter {

	WebCellFormatter inner;
	private String style;
	private String bg;
	private boolean isPercent;
	private boolean hasSeperateSortValue;

	public WebCellStyleWrapperFormatter(WebCellFormatter inner, boolean hasSeperateSortValue, String style) {
		this.hasSeperateSortValue = hasSeperateSortValue;
		this.inner = inner;
		this.style = style;
		if (inner instanceof PercentWebCellFormatter) {
			Map<String, String> parts = SH.splitToMap(new LinkedHashMap<String, String>(), '|', '=', style);
			this.bg = parts.remove("_bg");
			this.style = SH.joinMap('|', '=', parts);
			this.isPercent = true;
		}
	}

	public void lock() {
		inner.lock();
	}

	public String getStyle() {
		return this.style;
	}

	public boolean isLocked() {
		return inner.isLocked();
	}

	public StringBuilder formatCellToText(Object data, StringBuilder sb) {
		return inner.formatCellToText(toData(data), sb);
	}

	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder cellStyle) {
		if (isPercent)
			((PercentWebCellFormatter) inner).formatCellToHtml(toData(data), sb, cellStyle, bg);
		else
			inner.formatCellToHtml(toData(data), sb, cellStyle);
		cellStyle.append(style);
	}

	public StringBuilder formatCellToExcel(Object data, StringBuilder sb) {
		return inner.formatCellToExcel(toData(data), sb);
	}

	public Comparable getOrdinalValue(Object data) {
		if (hasSeperateSortValue)
			return (Comparable) (((Object[]) data)[1]);
		return inner.getOrdinalValue(data);
	}

	public boolean isString() {
		return inner.isString();
	}

	public Comparator getComparator() {
		if (hasSeperateSortValue)
			return ComparableComparator.INSTANCE;
		return inner.getComparator();
	}

	public String getDefaultColumnCssClass() {
		return inner.getDefaultColumnCssClass();
	}

	public String formatCellToExcel(Object data) {
		return inner.formatCellToExcel(toData(data));
	}

	public String formatCellToText(Object o) {
		return inner.formatCellToText(toData(o));
	}

	public String formatCellToHtml(Object data) {
		return inner.formatCellToHtml(toData(data));
	}

	public int getDefaultWidth() {
		return inner.getDefaultWidth();
	}

	public boolean getDefaultClickable() {
		return inner.getDefaultClickable();
	}

	public WebCellFormatter setDefaultWidth(int i) {
		inner.setDefaultWidth(i);
		return this;
	}

	public String formatCellForSearch(Object data) {
		return inner.formatCellForSearch(toData(data));
	}

	public StringBuilder formatCellForSearch(Object data, StringBuilder sb) {
		return inner.formatCellForSearch(toData(data), sb);
	}

	private Object toData(Object data) {
		if (hasSeperateSortValue)
			return ((Object[]) data)[0];
		return data;
	}

	public WebCellFormatter getInner() {
		return this.inner;
	}

	@Override
	public String getDefaultHeaderStyle() {
		return this.inner.getDefaultHeaderStyle();
	}

	public void setFormattter(WebCellFormatter cf) {
		this.inner = cf;
	}

	@Override
	public boolean getDefaultOneClick() {
		// TODO Auto-generated method stub
		return inner.getDefaultOneClick();
	}

}
