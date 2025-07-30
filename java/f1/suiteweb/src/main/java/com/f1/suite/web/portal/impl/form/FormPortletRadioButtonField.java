package com.f1.suite.web.portal.impl.form;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.suite.web.JsFunction;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;

public class FormPortletRadioButtonField extends FormPortletField<Boolean> {
	private static final Logger log = LH.get();

	public static final int DEFAULT_DIM = 19;
	public static final String JSNAME = "RadioButtonField";
	public static final String DEFAULT_GROUP_NAME = "group1";
	private String groupName;

	private String fullName;

	public FormPortletRadioButtonField(String title) {
		super(Boolean.class, title);
		setGroupName(DEFAULT_GROUP_NAME);
		setDefaultValue(false);
		setWidthPx(DEFAULT_DIM);
		setHeightPx(DEFAULT_DIM);
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final Boolean value = CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "value", null); // NOTE: value always yields true
		setValueNoFire(value);
		FormPortletRadioButtonField lastChecked = (FormPortletRadioButtonField) getForm().putLastCheckedForGroup(this.fullName, this);
		if (lastChecked != null && lastChecked != this)
			lastChecked.setValue(false);
		return true;
	}

	@Override
	public FormPortletRadioButtonField setValue(Boolean value) {
		if (value) {
			FormPortletRadioButtonField lastChecked = (FormPortletRadioButtonField) getForm().putLastCheckedForGroup(this.fullName, this);
			if (lastChecked != null && lastChecked != this)
				lastChecked.setValueNoFire(false);
		} else {
			FormPortletRadioButtonField lastChecked = (FormPortletRadioButtonField) getForm().getLastCheckedForGroup(this.fullName);
			if (lastChecked == this)
				getForm().removeLastCheckedForGroup(this.fullName);
		}
		super.setValue(value);
		return this;
	}
	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG))
			new JsFunction(pendingJs, jsObjectName, "init").addParamQuoted(this.fullName).end();
		if (hasChanged(MASK_STYLE))
			new JsFunction(pendingJs, jsObjectName, "setRadioStyle").addParamQuoted(getBgColor()).addParamQuoted(getFontColor()).addParamQuoted(getBorderColorMaterialized())
					.addParam(getBorderWidthMaterialized()).end();
		super.updateJs(pendingJs);
	}
	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		if (OH.ne(getGroupName(), groupName)) {
			this.groupName = groupName;
			this.flagConfigChanged();
			buildName();
		}
	}

	private void buildName() {
		FormPortlet form = getForm();
		this.fullName = form == null ? null : form.getPortletId() + getGroupName();
	}

	public String getGroupNameWithFormPortletId() {
		return this.fullName;
	}

	@Override
	public void setForm(FormPortlet form) {
		if (super.getForm() == form)
			return;
		super.setForm(form);
		buildName();
	}

}
