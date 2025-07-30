package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormButtonFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Boolean;

public class FormButtonQueryField extends QueryField<FormPortletButtonField> {

	private boolean shouldDisableAfterFirstClick = false;

	public FormButtonQueryField(AmiWebFormButtonFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletButtonField(""));
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		this.setDisableAfterFirstClick(CH.getOrNoThrow(Caster_Boolean.PRIMITIVE, initArgs, "dafc", false));
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		if (shouldDisableAfterFirstClick)
			sink.put("dafc", shouldDisableAfterFirstClick());
		return super.getJson(sink);
	}
	@Override
	public void setLabel(String value) {
		super.setLabel(value);
		this.getField().setValue(value);
	}
	@Override
	public Object getDefaultValue() {
		return this.getField().getTitle();
	}
	public boolean shouldDisableAfterFirstClick() {
		return shouldDisableAfterFirstClick;
	}
	public void setDisableAfterFirstClick(boolean disableAfterFirstClick) {
		this.shouldDisableAfterFirstClick = disableAfterFirstClick;
		this.getField().setDisableAfterFirstClick(disableAfterFirstClick);
	}
	@Override
	public void onFieldValueChanged(FormPortletField<?> field2, boolean fire) { // fires when button is clicked
	}
}