package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormButtonFieldFactory;
import com.f1.ami.web.form.queryfield.FormButtonQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class FormButtonEditFormPortlet extends BaseEditFieldPortlet<FormButtonQueryField> {
	private FormPortletCheckboxField disableClickCheckboxField;
	private FormPortletTitleField buttonOptionsTitleField;

	public FormButtonEditFormPortlet(AmiWebFormButtonFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		buttonOptionsTitleField = settingsForm.addField(new FormPortletTitleField("Button Field Options"));
		buttonOptionsTitleField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 320, 220, 25);
		this.disableClickCheckboxField = settingsForm.addField(new FormPortletCheckboxField("Auto-Disable After First Click"));

		this.disableClickCheckboxField.setLeftTopWidthHeightPx(COL1_HORIZONTAL_POS_PX, 350, 16, 16);
		this.disableClickCheckboxField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
		this.disableClickCheckboxField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_END);
	}
	@Override
	public void writeToField(FormButtonQueryField queryField) {
		//		queryField.setStyle(AmiWebStyleConsts.CODE_FLD_LBL_STATUS, Boolean.FALSE);
		//		queryField.setLabelVisible(false);
		queryField.setDisableAfterFirstClick(disableClickCheckboxField.getBooleanValue());
		queryField.getField().resetDisabledDueToClick();
	}
	@Override
	public void readFromField(FormButtonQueryField field) {
		disableClickCheckboxField.setValue(queryField.shouldDisableAfterFirstClick());
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (getHorizSettingsToggle() == field) {
			FormPortletToggleButtonsField<Byte> tbf = (FormPortletToggleButtonsField<Byte>) field;
			if (tbf.getValue() == BaseEditFieldPortlet.FIELD_ALIGN_ADVANCED) {
				buttonOptionsTitleField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 420, 220, 25);
				disableClickCheckboxField.setLeftTopWidthHeightPx(COL1_HORIZONTAL_POS_PX, 450, 16, 16);
			} else {
				buttonOptionsTitleField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 320, 220, 25);
				this.disableClickCheckboxField.setLeftTopWidthHeightPx(COL1_HORIZONTAL_POS_PX, 350, 16, 16);
			}
		}
		super.onFieldValueChanged(portlet, field, attributes);
	}
}
