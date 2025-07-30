/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.SH;

public class InputPortlet extends AbstractPortlet {
	private boolean isPassword;

	public InputPortlet(PortletConfig manager) {
		super(manager);
	}

	public PortletSchema<InputPortlet> SCHEMA = new BasicPortletSchema<InputPortlet>("Input", "InputPortlet", InputPortlet.class, false, false);
	private String value;
	private String type;
	private String name;

	@Override
	public PortletSchema<InputPortlet> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void initJs() {
		super.initJs();
		if (SH.is(value))
			flagPendingAjax();
	}

	@Override
	public void drainJavascript() {
		callJsFunction("init").addParamQuoted(name).addParamQuoted(type).end();
		callJsFunction("setValue").addParamQuoted(value).end();
	}

	public void setValue(String value) {
		this.value = value;
		flagPendingAjax();
	}

	public void setIsPassword(boolean isPassword) {
		this.isPassword = isPassword;
		flagPendingAjax();
	}

	public boolean getIsPassword() {
		return isPassword;
	}

	public InputPortlet setType(String type) {
		this.type = type;
		flagPendingAjax();
		return this;
	}

	public String getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public InputPortlet setName(String name) {
		this.name = name;
		flagPendingAjax();
		return this;
	}
}
