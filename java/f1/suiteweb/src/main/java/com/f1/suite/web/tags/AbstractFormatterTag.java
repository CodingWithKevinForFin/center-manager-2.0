/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.tags;

import com.f1.http.HttpTag;
import com.f1.http.handler.JspBuilderSession;
import com.f1.http.handler.JspTagBuilder;
import com.f1.utils.SH;

public abstract class AbstractFormatterTag implements JspTagBuilder {

	@Override
	public void doStart(JspBuilderSession session, HttpTag tag, int indent) {
		StringBuilder sb = session.getBody();
		sb.append("{").append(SH.NEWLINE);
		sb.append("  com.f1.suite.web.WebState webstate=(WebState) request.getAttribute(com.f1.suite.web.JettyJspProcessor.REQUEST_ATTRIBUTE_STATE)");
		sb.append(SH.NEWLINE);
		sb.append("  com.f1.utils.LocaleFormatter localFormatter = state.getFormatter();");
		sb.append(SH.NEWLINE);
		format(session, tag, indent + 2);
		sb.append("}").append(SH.NEWLINE);
	}

	protected abstract void format(JspBuilderSession session, HttpTag tag, int i);

	@Override
	public void doSimple(JspBuilderSession session, HttpTag tag, int indent) {
		doStart(session, tag, indent);
	}

	@Override
	public void doEnd(JspBuilderSession session, HttpTag tag, int indent) {
	}

}
