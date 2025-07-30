package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public class IncludeTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		SH.repeat(' ', indent, session.getBody());
		session.getBody().append("request.getHttpServer().include(request, ").append(IfTag.parseExpression(tag.getRequired("page"))).append(");").append(SH.NEWLINE);
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
	}

}
