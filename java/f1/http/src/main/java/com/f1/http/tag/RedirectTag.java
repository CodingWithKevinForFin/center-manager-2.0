package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public class RedirectTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		SH.repeat(' ', indent, session.getBody());
		session.getBody().append("request.sendRedirect((String)").append(IfTag.parseExpression(tag.getRequired("page"))).append(");").append(SH.NEWLINE);
		session.getBody().append("if(true) return;").append(SH.NEWLINE);
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
	}

}
