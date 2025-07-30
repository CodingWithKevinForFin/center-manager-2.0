package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public class SetTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		String scope = tag.getRequired("scope");
		String var = tag.getRequired("var");
		String value = tag.getOptional("value", null);
		if (value != null) {
			String expression = IfTag.parseExpression(value);
			if ("request".equals(scope))
				session.getBody().append("request.getAttributes().");
			else if ("session".equals(scope))
				session.getBody().append("request.getSession(true).getAttributes().");
			else if ("server".equals(scope))
				session.getBody().append("request.getHttpServer().getAttributes().");
			else if ("lcv".equals(scope)) {
				String type = tag.getRequired("type");
				session.getBody().append(type).append(" ").append(var).append(" = (").append(type).append(") ").append(expression).append(";").append(SH.NEWLINE);
				return;
			} else
				throw new IllegalArgumentException("invalid scope(must be request, session, server or lcv): " + scope);
			session.getBody().append("put(\"").append(var).append("\",").append(expression).append(");").append(SH.NEWLINE);
		} else {
			if ("request".equals(scope))
				session.getBody().append("request.getAttributes().");
			else if ("session".equals(scope))
				session.getBody().append("request.getSession(true).getAttributes().");
			else if ("server".equals(scope))
				session.getBody().append("request.getHttpServer().getAttributes().");
			else if ("lcv".equals(scope)) {
				return;
			} else
				throw new IllegalArgumentException("invalid scope(must be request, session, server or lcv): " + scope);
			session.getBody().append("remove(\"").append(var).append("\");").append(SH.NEWLINE);
		}
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {

	}

}
