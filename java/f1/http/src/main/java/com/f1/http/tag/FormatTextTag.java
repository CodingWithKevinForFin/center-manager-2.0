package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.FastPrintStream;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;

public class FormatTextTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		final String key = tag.getRequired("key");
		session.appendIndent(indent);
		session.getBody().append(getClass().getName()).append(".append(out,formatter,\"").append(IfTag.parseExpression(key)).append("\");").append(SH.NEWLINE);
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
	}

	public static void append(FastPrintStream out, LocaleFormatter formatter, String key) {
		String text = formatter.getBundledTextFormatter().format(key);
		for (char c : text.toCharArray())
			if (SH.isAscii(c))
				out.append(c);
			else
				out.append("&#").append(SH.toString((int) c)).append(';');
	}

}
