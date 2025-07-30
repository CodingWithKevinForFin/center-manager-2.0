package com.f1.http.tag;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public class UnsecureTag implements JspTagBuilder {

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		StringBuilder out = session.getBody();
		SH.repeat(' ', indent, out).append("if (request.getIsSecure()) {").append(SH.NEWLINE);

		SH.repeat(' ', indent + 2, out)
				.append("request.sendRedirect(HttpUtils.buildUrl(false, request.getHost(), request.getHttpServer().getUnsecurePort(), request.getRequestUri(), request.getQueryString()));")
				.append(SH.NEWLINE);

		SH.repeat(' ', indent + 2, out).append("return;").append(SH.NEWLINE);

		SH.repeat(' ', indent, out).append("}").append(SH.NEWLINE);
	}

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		doSimple(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
	}

}
