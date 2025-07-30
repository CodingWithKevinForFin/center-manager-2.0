/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import java.util.HashMap;

import com.f1.http.HttpUtils;
import com.f1.http.impl.IncludeHttpRequestResponse;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.FastPrintStream;

public class JspPortlet extends HtmlPortlet {

	public static final PortletSchema<JspPortlet> SCHEMA = new BasicPortletSchema<JspPortlet>("Jsp", "JspPortlet", JspPortlet.class, true, true);
	private boolean needsInit;
	private String jsp;

	public JspPortlet(PortletConfig manager, String jsp) {
		super(manager, null);
		this.jsp = jsp;
	}

	@Override
	public String getHtml() {
		try {
			FastByteArrayOutputStream buf = new FastByteArrayOutputStream();
			FastPrintStream out = new FastPrintStream(buf);
			IncludeHttpRequestResponse dummy = new IncludeHttpRequestResponse(getManager().getCurrentRequestAction());
			dummy.setDummyRequestUri(HttpUtils.getCanonical(getManager().getCurrentRequestAction().getRequestUri(), jsp));
			dummy.setDummyOutputStream(out);
			dummy.setDummyAttributes(new HashMap<String, Object>(dummy.getAttributes()));
			prepareAction(dummy);
			getManager().getCurrentRequestAction().getHttpServer().service(dummy);
			dummy.getOutputStream().flush();
			return buf.toString();
		} catch (Exception e) {
			throw new RuntimeException("Error for jsp portlet: " + this.jsp, e);
		}
	}

	protected void prepareAction(IncludeHttpRequestResponse dummy) {

	}

	@Override
	public PortletSchema<? extends JspPortlet> getPortletSchema() {
		return SCHEMA;
	}
}
