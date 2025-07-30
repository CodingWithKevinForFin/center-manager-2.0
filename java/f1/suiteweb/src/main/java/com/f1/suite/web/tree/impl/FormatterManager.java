package com.f1.suite.web.tree.impl;

import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;

public interface FormatterManager {

	public BasicWebCellFormatter getBasicFormatter();
	public BasicWebCellFormatter getImageWebCellFormatter();
	public WebCellFormatter getHtmlWebCellFormatter();
}
