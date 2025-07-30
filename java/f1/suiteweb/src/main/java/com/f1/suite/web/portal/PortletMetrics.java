package com.f1.suite.web.portal;

public interface PortletMetrics {

	public int getWidth(CharSequence text, CharSequence style, int fontSize);
	public int getHtmlWidth(CharSequence text, CharSequence style, int fontSize);
	public int getWidth(CharSequence text);
}
