package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public class ForEachTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		throw new RuntimeException("forEach tag must have a body");
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		String items = tag.getRequired("items");
		String var = tag.getRequired("var");
		session.appendIndent(indent);
		session.getBody().append("for(Object ").append(var).append(": com.f1.http.HttpUtils.toIterable(").append(IfTag.parseExpression(items)).append(")){").append(SH.NEWLINE);

	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
		session.appendIndent(indent);
		session.getBody().append("}").append(SH.NEWLINE);
	}

}
