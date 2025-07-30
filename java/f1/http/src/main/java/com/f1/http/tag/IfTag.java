package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class IfTag implements JspTagBuilder {

	private boolean testCondition;

	public IfTag(boolean testCondition) {
		this.testCondition = testCondition;
	}

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		throw new RuntimeException("if tag must have a body");
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		String test = tag.getRequired("test");
		session.appendIndent(indent);
		session.getBody().append("if(").append(testCondition ? "" : "!").append("com.f1.http.HttpUtils.toBoolean(").append(parseExpression(test)).append(")){").append(SH.NEWLINE);

	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
		session.appendIndent(indent);
		session.getBody().append("}").append(SH.NEWLINE);
	}

	private static final int[] TOKENS = new int[] { '\\', '$', '\'', CharReader.EOF };

	static public String parseExpression(String text) {
		StringCharReader reader = new StringCharReader(text);
		StringBuilder sb = new StringBuilder();
		StringBuilder r = new StringBuilder();
		for (;;) {
			int token = reader.readUntilAny(TOKENS, SH.clear(sb));
			switch (token) {
				case '\\':
					reader.expect('\\');
					r.append(sb);
					r.append(reader.readChar());
					break;
				case CharReader.EOF:
					r.append(sb);
					return r.toString();
				case '\'':
					reader.expect('\'');
					r.append(sb);
					r.append('"');
					break;
				case '$':
					r.append(sb);
					String method;
					if (reader.peakSequence("$$")) {
						reader.expect('$');
						method = "getParams().get";
					} else
						method = "findAttribute";

					reader.expectSequence("${");
					if (CharReader.EOF == reader.readUntil('}', SH.clear(sb)))
						throw new RuntimeException("missing } in: " + text);
					reader.expect('}');
					String param = sb.toString();
					int i = param.indexOf('.');
					if (i == -1)
						r.append("request.").append(method).append("(\"").append(param).append("\")");
					else {
						if (i + 1 < param.length() && param.charAt(i + 1) == '.')
							r.append("VH.getNestedValue(").append(param.substring(0, i)).append(",\"" + param.substring(i + 2) + "\",false)");
						else
							r.append("VH.getNestedValue(request.").append(method).append("(\"").append(param.substring(0, i))
									.append("\"),\"" + param.substring(i + 1) + "\",false)");
					}
					break;
			}
		}
	}
}
