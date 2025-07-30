/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tags;

import java.util.Map;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.tag.IfTag;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;

public class TimeFormatterTag extends AbstractFormatterTag {
	public static final String TIME = "T";
	public static final String DATE = "D";
	public static final String DATETIME = "DT";
	public static final String DATETIME_LONG = "DTL";
	public static final Map<String, Integer> types = CH.m(TIME, LocaleFormatter.TIME, DATE, LocaleFormatter.DATE, DATETIME, LocaleFormatter.DATETIME, DATETIME_LONG,
			LocaleFormatter.DATETIME_LONG);

	public static Integer parseFormat(String format) {
		if (format == null)
			return LocaleFormatter.DATETIME;
		return CH.getOrThrow(types, format, "date format");
	}

	private String formatString = DATETIME;
	private long value = types.get(formatString);

	public void setFormat(String format) {
	}

	public String getFormat() {
		return formatString;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	@Override
	protected void format(JspBuilderSession session, HttpTag tag, int i) {
		String format = IfTag.parseExpression(tag.getOptional("format", "0"));
		String value = IfTag.parseExpression(tag.getOptional("value", "0"));
		session.getBody().append("localFormatter.getDateFormatter(com.f1.suite.web.tags.TimeFormatterTag.parseFormat(").append(format);
		session.getBody().append(")).format(").append(value).append(", out);");
		session.getBody().append(SH.NEWLINE);
	}
}
