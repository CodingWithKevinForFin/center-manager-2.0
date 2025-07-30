package com.f1.suite.web.portal.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.suite.web.portal.PortletMetrics;
import com.f1.utils.CharSubSequence;
import com.f1.utils.SH;

public class BasicPortletMetrics implements PortletMetrics {

	private CharSubSequence tmp = new CharSubSequence();
	@Override
	public int getWidth(CharSequence text, CharSequence style, int fontSize) {
		if (text == null || text.length() == 0)
			return 0;
		FontMetrics fm = getFontMetrics(style);
		int r = fm.toPx(fontSize, fm.getWidth(text), style != null && SH.indexOf(style, "bold", 0) != -1);
		return r;
	}
	private FontMetrics getFontMetrics(CharSequence style) {
		FontMetrics fm = null;
		if (style != null) {
			int start = SH.indexOf(style, "_fm=", 0);
			if (start != -1) {
				start += 4;
				int end = SH.indexOf(style, ',', start);
				if (end == -1) {
					end = SH.indexOf(style, '|', start);
					if (end == -1)
						end = style.length();
				}
				tmp.reset(style, start, end);
				fm = FontMetricsManager.INSTANCE.getFont(tmp);
			}
		}
		if (fm == null)
			fm = FontMetricsConsts.ARIAL;
		return fm;
	}
	@Override
	public int getWidth(CharSequence text) {
		return text.length() * 7;
	}

	public int getMaxWidth(int chars) {
		return chars * 10;
	}
	public int getAvgWidth(int chars) {
		return chars * 8;
	}
	private static final Pattern PATTERN_AMP = Pattern.compile("&[0-9A-Z]+;", Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_BRACKET = Pattern.compile("<[A-Z]+>", Pattern.CASE_INSENSITIVE);
	private static final Pattern PATTERN_BR = Pattern.compile("<(?:P|BR)>", Pattern.CASE_INSENSITIVE);
	private Matcher MATCHER_AMP = PATTERN_AMP.matcher("");
	private Matcher MATCHER_LT = PATTERN_BRACKET.matcher("");
	@Override
	public int getHtmlWidth(CharSequence text, CharSequence style, int fontSize) {
		if (text == null || text.length() == 0)
			return 0;

		FontMetrics fm = getFontMetrics(style);
		boolean isBold = style != null && SH.indexOf(style, "bold", 0) != -1;

		int i = SH.indexOf(text, '&', 0);
		boolean hasAmp = i != -1 && SH.indexOf(text, ';', i + 1) != -1;
		i = SH.indexOf(text, '<', 0);
		boolean hasLt = i != -1 && SH.indexOf(text, '>', i + 1) != -1;
		if (hasLt) {
			String[] parts = PATTERN_BR.split(text);
			int r = 0;
			for (String part : parts) {
				if (hasAmp && part.indexOf('&') != -1)
					part = MATCHER_AMP.reset(part).replaceAll(" ");
				if (part.indexOf('<') != -1)
					part = MATCHER_LT.reset(part).replaceAll("");
				int n = fm.toPx(fontSize, fm.getWidth(part), isBold);
				r = Math.max(r, n);
			}
			return r;
		}
		if (hasAmp)
			text = MATCHER_AMP.reset(text).replaceAll(" ");
		return fm.toPx(fontSize, fm.getWidth(text), isBold);
	}
}
