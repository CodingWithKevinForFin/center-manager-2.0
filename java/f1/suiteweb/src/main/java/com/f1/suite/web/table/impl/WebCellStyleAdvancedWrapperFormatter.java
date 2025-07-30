package com.f1.suite.web.table.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.suite.web.table.WebCellFormatter;
import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.structs.ComparableComparator;

public class WebCellStyleAdvancedWrapperFormatter implements WebCellFormatter {

	private WebCellFormatter inner;
	final private String[] styleKeys;
	private String stylePrefix;
	private final boolean skipText;
	private String bg;
	private boolean isPercent;
	private int bgIndex;
	private boolean hasSeperateSortValue;
	private int styleStart;

	public WebCellStyleAdvancedWrapperFormatter(WebCellFormatter inner, boolean hasSeperateSortValue, String stylePrefix, String... styleKeys) {
		this.hasSeperateSortValue = hasSeperateSortValue;
		if (inner instanceof PercentWebCellFormatter) {
			Map<String, String> parts = SH.splitToMap(new LinkedHashMap<String, String>(), '|', '=', stylePrefix);
			this.bg = parts.remove("_bg");
			stylePrefix = SH.joinMap('|', '=', parts);
			this.isPercent = true;
			this.bgIndex = AH.indexOf("_bg", styleKeys);
		} else
			bgIndex = -1;
		this.inner = inner;
		this.stylePrefix = stylePrefix == null || stylePrefix.length() == 0 ? null : stylePrefix;
		this.styleKeys = styleKeys;
		skipText = stylePrefix.indexOf("_fg=|") != -1 || stylePrefix.endsWith("_fg=");
		this.styleStart = hasSeperateSortValue ? 2 : 1;
	}

	public void lock() {
		inner.lock();
	}

	public boolean isLocked() {
		return inner.isLocked();
	}

	public StringBuilder formatCellToText(Object data, StringBuilder sb) {
		return inner.formatCellToText(toData(data), sb);
	}

	public void formatCellToHtml(Object data, StringBuilder sb, StringBuilder cellStyle) {
		if (this.styleKeys.length > 0) {
			Object[] array = (Object[]) data;
			int len = sb.length();
			if (isPercent) {
				Object bg = this.bg;
				if (bgIndex != -1)
					bg = array[bgIndex + styleStart];
				((PercentWebCellFormatter) inner).formatCellToHtml(array[0], sb, cellStyle, bg == null ? null : bg.toString());
			} else
				inner.formatCellToHtml(array[0], sb, cellStyle);
			boolean needsComma = false;
			if (stylePrefix != null)
				cellStyle.append(stylePrefix).append('|');
			Object bg = this.bg;
			for (int i = 0; i < this.styleKeys.length; i++) {
				if (i == bgIndex)
					continue;
				Object value = array[i + styleStart];
				if (value == null)
					continue;
				if (needsComma)
					cellStyle.append('|');
				cellStyle.append(this.styleKeys[i]).append('=').append(value);
				needsComma = true;
			}
		} else {
			if (isPercent)
				((PercentWebCellFormatter) inner).formatCellToHtml(toData(data), sb, cellStyle, bg);
			else
				inner.formatCellToHtml(toData(data), sb, cellStyle);
			if (stylePrefix != null)
				cellStyle.append(stylePrefix);
		}
		int len = cellStyle.length();
		if (cellStyle.length() > 0 && cellStyle.charAt(len - 1) == '|')
			cellStyle.setLength(len - 1);
	}
	public StringBuilder formatCellToExcel(Object data, StringBuilder sb) {
		return inner.formatCellToExcel(toData(data), sb);
	}

	public Comparable getOrdinalValue(Object data) {
		if (hasSeperateSortValue)
			return (Comparable) (((Object[]) data)[1]);
		return inner.getOrdinalValue(toData(data));
	}

	private Object toData(Object data) {
		return data == null || !data.getClass().isArray() ? data : ((Object[]) data)[0];
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
		return inner.setDefaultWidth(i);
	}

	public String formatCellForSearch(Object data) {
		return inner.formatCellForSearch(toData(data));
	}

	public StringBuilder formatCellForSearch(Object data, StringBuilder sb) {
		return inner.formatCellForSearch(toData(data), sb);
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
