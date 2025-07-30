package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.RadioButtonQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletRadioButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class RadioButtonFieldEditFormPortlet extends BaseEditFieldPortlet<RadioButtonQueryField> {
	public static final String DEFAULT_GROUP_NAME = "group1";
	private FormPortletTextField groupNameField;
	private FormPortletTitleField radioButtonOptionsTitleField;

	public RadioButtonFieldEditFormPortlet(AmiWebFormFieldFactory<RadioButtonQueryField> factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		radioButtonOptionsTitleField = settingsForm.addField(new FormPortletTitleField("Radio Button Options"));
		this.groupNameField = settingsForm.addField(new FormPortletTextField("Group Name:").setValue(DEFAULT_GROUP_NAME));
		getRequiredFields().add(this.groupNameField);
	}

	@Override
	public void readFromField(RadioButtonQueryField queryField) {
		String gn = queryField.getField().getGroupName();
		groupNameField.setValue(gn);
	}

	@Override
	public void writeToField(RadioButtonQueryField queryField) {
		String userFieldValue = this.groupNameField.getValue();
		queryField.setGroupName(userFieldValue);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (getHorizSettingsToggle() == field) {
			FormPortletToggleButtonsField<Byte> tbf = (FormPortletToggleButtonsField<Byte>) field;
			if (tbf.getValue() == BaseEditFieldPortlet.FIELD_ALIGN_ADVANCED) {
				this.radioButtonOptionsTitleField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 390, 220, 25);
				this.groupNameField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 420, 220, FormPortletField.DEFAULT_HEIGHT);
			} else {
				this.radioButtonOptionsTitleField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 290, 220, 25);
				this.groupNameField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 320, 220, FormPortletField.DEFAULT_HEIGHT);
			}
		}
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public boolean verifyForSubmit() {
		String userFieldValue = SH.trim(this.groupNameField.getValue());
		String queryFieldValue = queryField.getGroupName();
		if (OH.ne(userFieldValue, queryFieldValue) && queryField.getField().getValue() != null) {
			boolean isCurFieldChecked = queryField.getField().getValue();
			if (isCurFieldChecked) {
				String mapKey = queryField.getField().getForm().getPortletId() + "_" + userFieldValue;
				queryField.getField().getForm().putLastCheckedForGroup(queryField.getField().getGroupNameWithFormPortletId(), null);
				// check if the new groupname already exists
				FormPortletRadioButtonField newGroupCheckedField = queryField.getField().getForm().getLastCheckedForGroup(mapKey);
				if (newGroupCheckedField == null) {
					queryField.getField().getForm().putLastCheckedForGroup(mapKey, queryField.getField());
				} else
					queryField.getField().setValueNoFire(false);
			}
		}
		return super.verifyForSubmit();
	}
}
