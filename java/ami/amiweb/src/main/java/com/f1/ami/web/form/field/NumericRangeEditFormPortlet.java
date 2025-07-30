package com.f1.ami.web.form.field;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormRangeFieldFactory;
import com.f1.ami.web.form.queryfield.RangeQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.SH;

public class NumericRangeEditFormPortlet extends BaseEditFieldPortlet<RangeQueryField> {

	private FormPortletTextField minValueField;
	private FormPortletTextField maxValueField;
	private FormPortletTextField stepField;
	//	private FormPortletColorField primaryColorField;
	//	private FormPortletColorField secondaryColorField;
	final private FormPortletToggleButtonsField<Boolean> showSliderField;

	public NumericRangeEditFormPortlet(AmiWebFormRangeFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		FormPortletTitleField titleField = settingsForm.addField(new FormPortletTitleField("Numeric Slider Field Options"));
		this.minValueField = settingsForm.addField(new FormPortletTextField("Minimum Value:").setValue("0"));
		this.maxValueField = settingsForm.addField(new FormPortletTextField("Maximum Value:").setValue("10"));
		this.stepField = settingsForm.addField(new FormPortletTextField("Step:").setValue("1"));
		//		this.primaryColorField = addStyleField(new FormPortletColorField("Primary Color:"), AmiWebStyleConsts.CODE_FLD_PRIM_CL);
		//		this.secondaryColorField = addStyleField(new FormPortletColorField("Secondary Color:"), AmiWebStyleConsts.CODE_FLD_SEC_CL);
		this.showSliderField = settingsForm.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Slider:"));
		this.showSliderField.addOption(true, "Show");
		this.showSliderField.addOption(false, "Hide");
		titleField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		titleField.setTopPosPx(420).setHeightPx(25);
		this.minValueField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.minValueField.setTopPosPx(455).setHeightPx(25);
		this.maxValueField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.maxValueField.setTopPosPx(487).setHeightPx(25);
		this.stepField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.stepField.setTopPosPx(519).setHeightPx(25);
		this.showSliderField.setLeftPosPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX).setWidthPx(220);
		this.showSliderField.setTopPosPx(551).setHeightPx(25);

		// Add required fields
		getRequiredFields().add(this.minValueField);
		getRequiredFields().add(this.maxValueField);
		getRequiredFields().add(this.stepField);
	}

	@Override
	public void readFromField(RangeQueryField field) {
		FormPortletNumericRangeField rangeField = (FormPortletNumericRangeField) field.getField();
		minValueField.setValue(SH.toString(field.getMin()));
		maxValueField.setValue(SH.toString(field.getMax()));
		stepField.setValue(SH.toString(rangeField.getStep()));
		//		primaryColorField.setValue(field.getPrimaryColor());
		//		secondaryColorField.setValue(field.getSecondaryColor());
		this.showSliderField.setValue(!rangeField.isSliderHidden());
		if (minValueField.getValue() == null)
			this.minValueField.setValue("0");
		if (maxValueField.getValue() == null)
			this.maxValueField.setValue("10");
		if (stepField.getValue() == null)
			this.stepField.setValue("1");
	}

	@Override
	public boolean verifyForSubmit() {
		double min, max, step;
		try {
			min = SH.parseDouble(SH.trim(this.minValueField.getValue()));
			max = SH.parseDouble(SH.trim(this.maxValueField.getValue()));
			step = SH.parseDouble(SH.trim(this.stepField.getValue()));
		} catch (Exception e) {
			getManager().showAlert("Mininum Value, Maximum Value and Step must be valid numbers");
			return false;
		}

		if (min >= max) {
			getManager().showAlert("Maximum value must be larger than Minimum");
			return false;
		}
		if (step <= 0) {
			getManager().showAlert("Step value must be a positive number");
			return false;
		}
		return super.verifyForSubmit();
	}

	@Override
	public void writeToField(RangeQueryField queryField) {
		double min = SH.parseDouble(SH.trim(this.minValueField.getValue()));
		double max = SH.parseDouble(SH.trim(this.maxValueField.getValue()));
		double step = SH.parseDouble(SH.trim(this.stepField.getValue()));
		//		queryField.setPrimaryColor(this.primaryColorField.getValue());
		//		queryField.setSecondaryColor(this.secondaryColorField.getValue());
		FormPortletNumericRangeField field = queryField.getField();
		queryField.setRange(min, max);
		queryField.setStep(step);
		field.setValue(min);
		field.setDefaultValue(min);
		field.setSliderHidden(!this.showSliderField.getValue());
		field.setTextHidden(false);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.labelField.getValue() == null || this.nameField.getValue() == null) {
			getManager().showAlert("Field Must have NAME and LABEL");
			return;
		}

		if (this.minValueField.getValue() == null || this.maxValueField.getValue() == null || this.stepField.getValue() == null) {
			getManager().showAlert("Field must have a MINIMUM, MAXIMUM, and STEP value");
			return;
		}
		super.onButtonPressed(portlet, button);
	}

	//	@Override
	//	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	//		super.onFieldValueChanged(portlet, field, attributes);
	//		if (this.primaryColorField == field) {
	//			this.queryField.setPrimaryColor(primaryColorField.getValue());
	//		} else if (this.secondaryColorField == field) {
	//			this.queryField.setSecondaryColor(secondaryColorField.getValue());
	//		}
	//	}

}
