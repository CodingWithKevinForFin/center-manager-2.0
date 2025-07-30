package com.f1.ami.web.form.queryfield;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormRadioButtonFieldFactory;
import com.f1.suite.web.portal.impl.form.FormPortletRadioButtonField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;

public class RadioButtonQueryField extends QueryField<FormPortletRadioButtonField> {
	private String groupName;

	public RadioButtonQueryField(AmiWebFormRadioButtonFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletRadioButtonField(""));
		getField().setDefaultValue(Boolean.FALSE);
		setGroupName(FormPortletRadioButtonField.DEFAULT_GROUP_NAME);
	}

	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		String gid = CH.getOr(Caster_String.INSTANCE, initArgs, "gn", FormPortletRadioButtonField.DEFAULT_GROUP_NAME);
		setGroupName(gid);
	}

	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		FormPortletRadioButtonField rbf = getField();
		CH.m(sink, "gn", rbf.getGroupName());
		return super.getJson(sink);
	}

	public String getGroupName() {
		return groupName;
	}

	public String getFormattedGroupName() {
		return getField().getGroupNameWithFormPortletId();
	}

	public void setGroupName(String groupName) {
		if (OH.ne(getGroupName(), groupName)) {
			this.groupName = groupName;
			getField().setGroupName(groupName);
		}
	}
}
