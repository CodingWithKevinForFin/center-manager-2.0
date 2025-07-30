package com.f1.ami.web;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;

public class AmiWebBasicWebCellFormatter extends BasicWebCellFormatter {

	@Override
	protected StringBuilder s(Object o, StringBuilder sb) {
		return AmiUtils.s(o, sb);
	}

	@Override
	protected String s(Object o) {
		return AmiUtils.s(o);
	}
}
