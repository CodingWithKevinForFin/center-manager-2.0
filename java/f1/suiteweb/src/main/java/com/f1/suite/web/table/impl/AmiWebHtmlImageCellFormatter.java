package com.f1.suite.web.table.impl;

import com.f1.base.Bytes;
import com.f1.utils.encrypt.EncoderUtils;

public class AmiWebHtmlImageCellFormatter extends BasicWebCellFormatter {

	@Override
	public void formatCellToHtml(Object value, StringBuilder sb, StringBuilder cellStyle) {
		if (value instanceof Bytes) {
			sb.append("<img width=100% src='data:image;base64,");
			EncoderUtils.encode64(((Bytes) value).getBytes(), sb);
			sb.append("'/>");
		} else
			super.formatCellToHtml(value, sb, cellStyle);
	}
	@Override
	public String formatCellToHtml(Object value) {
		if (value instanceof byte[])
			return "";
		else
			return super.formatCellToHtml(value);
	}
}
