package com.f1.ami.web.dm.portlets;

import java.util.Map;

import com.f1.ami.web.dm.AmiWebDmLinkWhereClause;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;

public class AmiWebDmWhereFieldAdvanced extends FormPortlet implements FormPortletListener {
	FormPortletTextField prefix;
	FormPortletTextField join;
	FormPortletTextField suffix;
	FormPortletTextField trueOverrideField;
	FormPortletTextField falseOverrideField;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private AmiWebDmLinkWhereClause whereClauseObject;

	public AmiWebDmWhereFieldAdvanced(AmiWebDmLinkWhereClause whereClauseObject, PortletConfig config) {
		super(config);
		this.setLabelsWidth(200);
		this.prefix = new FormPortletTextField("Prefix:");
		this.join = new FormPortletTextField("Join:");
		this.suffix = new FormPortletTextField("Suffix:");
		this.trueOverrideField = new FormPortletTextField("Show Everything Expression:");
		this.falseOverrideField = new FormPortletTextField("Clear Expression:");

		this.whereClauseObject = whereClauseObject;
		this.prefix.setValue(whereClauseObject.getPrefix());
		this.join.setValue(whereClauseObject.getJoin());
		this.suffix.setValue(whereClauseObject.getSuffix());
		this.trueOverrideField.setValue(whereClauseObject.getTrueOverride());
		this.falseOverrideField.setValue(whereClauseObject.getFalseOverride());

		this.addField(prefix);
		this.addField(join);
		this.addField(suffix);
		this.addField(trueOverrideField);
		this.addField(falseOverrideField);
		submitButton = this.addButton(new FormPortletButton("Submit"));
		cancelButton = this.addButton(new FormPortletButton("Cancel"));
		this.addFormPortletListener(this);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.submitButton) {
			whereClauseObject.setPrefix(prefix.getValue());
			whereClauseObject.setJoin(join.getValue());
			whereClauseObject.setSuffix(suffix.getValue());
			whereClauseObject.setTrueOverride(trueOverrideField.getValue());
			whereClauseObject.setFalseOverride(falseOverrideField.getValue());
			close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

}
