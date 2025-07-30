package com.f1.suite.web.portal.impl.text;

import com.f1.suite.web.menu.WebMenu;

public interface TextModel {

	public int getNumberOfLines(FastTextPortlet portlet);
	public void prepareLines(FastTextPortlet portlet, int start, int count);
	public int getLabelWidth(FastTextPortlet portlet);
	public void formatHtml(FastTextPortlet portlet, int lineNumber, StringBuilder sink);
	public void formatText(FastTextPortlet portlet, int lineNumber, StringBuilder sink);
	public void formatLabel(FastTextPortlet portlet, int lineNumber, StringBuilder sink);
	public void formatStyle(FastTextPortlet portlet, int lineNumber, StringBuilder sink);
	public WebMenu createMenu(FastTextPortlet fastTextPortlet);

	public void setColumnsVisible(int columns);
}
