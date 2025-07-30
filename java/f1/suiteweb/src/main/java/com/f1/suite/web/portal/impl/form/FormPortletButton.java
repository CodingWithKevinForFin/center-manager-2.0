package com.f1.suite.web.portal.impl.form;

import java.util.HashSet;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.utils.OH;

public class FormPortletButton {

	private String name;
	private String help;
	private String id;
	private FormPortlet form;
	private boolean enabled = true;

	public FormPortletButton(String name) {
		this.name = name;
	}

	public void rebuildJs(String jsObjectName, StringBuilder pendingJs) {
		new JsFunction(jsObjectName).reset(pendingJs).addProperty("disabled").addPropertyValue(!enabled);
	}

	public String getName() {
		return name;
	}

	public String getHtmlLayoutSignature() {
		return "<span id='formbutton_" + this.form.getPortletId() + "_" + getId() + "'></span>";
	}

	public void setName(String name) {
		this.name = name;
		fireOnButtonChanged();
	}

	protected void fireOnButtonChanged() {
		if (this.getForm() != null)
			getForm().onButtonChanged(this);
	}

	public String getHelp() {
		return help;
	}

	public String getId() {
		return id;
	}
	public FormPortletButton setId(String id) {
		if (this.id != null && this.form != null)
			throw new IllegalStateException("id already assigned");
		this.id = id;
		return this;
	}

	public FormPortlet getForm() {
		return form;
	}

	public void setForm(FormPortlet form) {
		if (this.form != null && form != null)
			throw new IllegalStateException("already member of a form");
		this.form = form;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled)
			return;
		this.enabled = enabled;
		fireOnButtonChanged();
	}

	public Object getCorrelationData() {
		return correlationData;
	}

	public FormPortletButton setCorrelationData(Object correlationData) {
		this.correlationData = correlationData;
		return this;
	}

	private Object correlationData;
	private String cssStyle = "";
	private Set<String> hotkeys = new HashSet<String>();

	public String getCssStyle() {
		return this.cssStyle;
	}

	public FormPortletButton setCssStyle(String cssStyle) {
		if (OH.eq(this.cssStyle, cssStyle))
			return this;
		this.cssStyle = cssStyle;
		if (this.form != null)
			this.form.flagButtonsChanged();
		return this;
	}

	public void fireClicked() {
		this.form.fireButtonClicked(this);
	}

	public FormPortletButton addHotKey(String string) {
		hotkeys.add(string);
		return this;
	}
	public FormPortletButton removeHotKey(String string) {
		hotkeys.remove(string);
		return this;
	}
	public boolean hasHotKey(String string) {
		return hotkeys.contains(string);
	}

}
