package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;

public class AmiWebTitleSettingsPortlet extends GridPortlet implements FormPortletListener {

	private final FormPortletTextField titleField = new FormPortletTextField("Title:");
	private final AmiWebTitlePortlet titlePortlet;
	private final FormPortlet form;
	private final FormPortlet buttonsForm;
	private final FormPortletButton submitButton = new FormPortletButton("OK");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");

	public AmiWebTitleSettingsPortlet(PortletConfig config, AmiWebTitlePortlet titlePortlet) {
		super(config);
		this.titlePortlet = titlePortlet;
		this.form = new FormPortlet(generateConfig());
		this.buttonsForm = new FormPortlet(generateConfig());
		addChild(this.form, 0, 0);
		addChild(this.buttonsForm, 0, 1);
		this.titleField.setValue(titlePortlet.getTitle(false));
		this.form.addField(this.titleField);
		this.buttonsForm.addButton(this.submitButton);
		this.buttonsForm.addButton(this.cancelButton);
		this.form.addFormPortletListener(this);
		this.buttonsForm.addFormPortletListener(this);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 500;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 200;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.submitButton == button) {
			this.titlePortlet.setTitle(this.titleField.getValue(), false);
		}
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
}
