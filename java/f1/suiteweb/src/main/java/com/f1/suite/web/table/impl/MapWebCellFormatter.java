package com.f1.suite.web.table.impl;

import java.util.HashMap;
import java.util.Map;

import com.f1.base.LockedException;
import com.f1.suite.web.table.WebCellEnumFormatter;
import com.f1.utils.CH;
import com.f1.utils.ConstFormatter;
import com.f1.utils.Formatter;
import com.f1.utils.ToStringFormatter;

public class MapWebCellFormatter<K> extends BasicWebCellFormatter implements WebCellEnumFormatter<K> {

	private class EnumEntry {
		public K key;
		public Object value;
		public String text;
		public String html;
		public String css;

		public EnumEntry(K key, Object value, String text, String html, String css) {
			this.key = key;
			this.value = value;
			this.text = text;
			this.html = html;
			this.css = css;
		}
	}

	private Formatter defaultValue = new ToStringFormatter(null, null, "");
	private Formatter formatter;
	final private Map<K, EnumEntry> map;
	final private Map<K, String> textMap;

	public MapWebCellFormatter(Formatter textFormatter) {
		this.map = new HashMap<K, EnumEntry>();
		this.textMap = new HashMap<K, String>();
		this.formatter = textFormatter;
	}

	public MapWebCellFormatter<K> addEntry(K key, Object value, String style) {
		LockedException.assertNotLocked(this);
		addEntry(key, value);
		setStyle(key, style);
		return this;
	}
	public MapWebCellFormatter<K> addEntry(K key, Object value, String style, String html) {
		LockedException.assertNotLocked(this);
		addEntry(key, value);
		setStyle(key, style);
		setHtml(key, html);
		return this;
	}
	public MapWebCellFormatter<K> addEntry(K key, Object value) {
		LockedException.assertNotLocked(this);
		final String text = format(value);
		final String html = text;
		final String css = null;
		CH.putOrThrow(this.textMap, key, text);
		CH.putOrThrow(this.map, key, new EnumEntry(key, value, text, html, css));
		return this;
	}
	private String format(Object value) {
		return formatter == null ? null : formatter.format(value);
	}

	@Override
	public MapWebCellFormatter<K> setNullValue(String value) {
		LockedException.assertNotLocked(this);
		defaultValue = new ConstFormatter(value);
		return this;
	}
	public MapWebCellFormatter<K> setDefaultValue(Formatter value) {
		LockedException.assertNotLocked(this);
		defaultValue = value;
		return this;
	}

	public MapWebCellFormatter<K> setStyle(K key, String style) {
		LockedException.assertNotLocked(this);
		EnumEntry entry = CH.getOrThrow(map, key);
		entry.css = style;
		return this;
	}
	public MapWebCellFormatter<K> setHtml(K key, String html) {
		LockedException.assertNotLocked(this);
		EnumEntry entry = CH.getOrThrow(map, key);
		entry.html = html;
		return this;
	}

	@Override
	public StringBuilder formatCellToText(Object o, StringBuilder sb) {
		formatToText(o, sb);
		return sb;
	}

	@Override
	public String formatCellToText(Object o) {
		EnumEntry entry = map.get(o);
		if (entry == null)
			return null;
		else
			return entry.text;
	}
	private StringBuilder formatToText(Object o, StringBuilder sb) {
		EnumEntry entry = map.get(o);
		if (entry == null)
			super.formatCellToText(null, sb);
		else
			sb.append(entry.text);
		return sb;
	}

	@Override
	public void formatCellToHtml(Object o, StringBuilder sb, StringBuilder cellStyle) {
		EnumEntry entry = map.get(o);
		if (entry == null)
			super.formatCellToText(null, sb);
		else {
			sb.append(entry.html);
			if (entry.css != null)
				cellStyle.append(entry.css);
		}
	}

	@Override
	public String formatCellToHtml(Object data) {
		EnumEntry entry = map.get(data);
		if (entry == null)
			return super.formatCellToHtml(data);
		else
			return entry.html;
	}

	@Override
	public Map<K, String> getEnumValuesAsText() {
		return textMap;
	}

}
