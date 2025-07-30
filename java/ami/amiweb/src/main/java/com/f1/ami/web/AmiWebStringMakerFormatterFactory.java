package com.f1.ami.web;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.stringmaker.StringMakerFormatter;
import com.f1.stringmaker.StringMakerFormatterFactory;
import com.f1.stringmaker.StringMakerSession;
import com.f1.stringmaker.impl.BasicStringMakerFormatter;
import com.f1.suite.web.util.WebHelper;

public class AmiWebStringMakerFormatterFactory implements StringMakerFormatterFactory {

	public static final StringMakerFormatter HTML_FORMATTER = new HtmlFormatter();

	public static class HtmlFormatter implements StringMakerFormatter {
		@Override
		public void append(Object value, String format, String formatter, StringMakerSession session) {
			if (value == null)
				return;
			if (format == null)
				WebHelper.escapeHtml(AmiUtils.snn(value, "null"), session.getSink());
		}
	}

	@Override
	public StringMakerFormatter getFormatter(String type, String args) {
		if (type == null)
			return BasicStringMakerFormatter.INSTANCE;
		else if ("html".equals(type))
			return HTML_FORMATTER;
		else
			throw new RuntimeException("Unknown type: " + type);
	}

}
