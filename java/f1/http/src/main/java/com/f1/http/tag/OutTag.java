package com.f1.http.tag;

import java.util.Map;

import com.f1.http.HttpTag;
import com.f1.http.HttpUtils;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.CH;
import com.f1.utils.FastPrintStream;
import com.f1.utils.SH;

public class OutTag implements JspTagBuilder {

	private static final char[] NEEDS_ESCAPED = "\"\'".toCharArray();
	private static Map<String, String> ESCAPE_MODES = CH.m("NONE", "escapeHtml_None", "FULL", "escapeHtml_Full", "QUOTES", "escapeHtml_Quotes");

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		String param = tag.getRequired("value");
		String escapeFunction = CH.getOrThrow(ESCAPE_MODES, tag.getOptional("escape", "QUOTES"), "unknown value for escape param");

		boolean ignoreExceptions = "true".equals(tag.getOptional("ignoreExceptions", "false"));
		if (ignoreExceptions)
			session.getBody().append("try{");
		session.getBody().append("com.f1.http.tag.OutTag.").append(escapeFunction).append("(out,").append(IfTag.parseExpression(param)).append(");").append(SH.NEWLINE);
		if (ignoreExceptions)
			session.getBody().append("}catch(Exception e){}");
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {

	}
	public static final void escapeHtml_None(FastPrintStream out, Object o) {
		if (o == null)
			return;
		out.print(o.toString());
	}

	public static final void escapeHtml_Quotes(FastPrintStream out, Object o) {
		if (o == null)
			return;
		String s = o.toString();
		int i = SH.indexOfFirst(s, 0, NEEDS_ESCAPED);
		if (i == -1) {
			out.print(s);
			return;
		}
		out.append(s, 0, i);
		for (int l = s.length(); i < l; i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\'':
					out.append("&#39;");
					break;
				case '"':
					out.append("&#34;");
					break;
				default:
					out.print(c);
			}
		}
	}
	public static final void escapeHtml_Full(FastPrintStream out, Object o) {
		if (o == null)
			return;
		CharSequence s = SH.toCharSequence(o);
		HttpUtils.escapeHtml(s, 0, s.length(), true, "<BR>", out);
	}
}
