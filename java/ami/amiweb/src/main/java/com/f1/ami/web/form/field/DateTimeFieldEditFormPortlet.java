package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDateTimeFieldFactory;
import com.f1.ami.web.form.queryfield.DateTimeQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class DateTimeFieldEditFormPortlet extends BaseEditFieldPortlet<DateTimeQueryField> implements FormPortletListener {

	private FormPortletColorField headerColorField;
	private FormPortletTextField enableLastNDaysField;
	private FormPortletCheckboxField disableFutureDatesField;

	public DateTimeFieldEditFormPortlet(AmiWebFormDateTimeFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		this.enableLastNDaysField = settingsForm.addField(new FormPortletTextField("Enable Last N Days: "));
		this.enableLastNDaysField
				.setHelp("If set to X (non-negative integer), then allow selection only within the past X days from today. Leave blank to allow selection on all past dates.");
		this.disableFutureDatesField = settingsForm.addField(new FormPortletCheckboxField("Disable Future Dates: ", false));
		this.disableFutureDatesField.setHelp("If toggled, disallow selecting future days from <b>today</b>. Leave blank to allow selection of future dates.");
	}

	@Override
	public void readFromField(DateTimeQueryField field) {
		//		this.headerColorField.setValue(field.getPrimaryColor());
		Tuple2<Integer, Integer> enableLastNDays = field.getField().getEnableLastNDays();
		Tuple2<Boolean, Boolean> disableFutureDates = field.getField().getDisableFutureDates();
		if (enableLastNDays.getA() != null)
			this.enableLastNDaysField.setValue(enableLastNDays.getA().toString());
		if (disableFutureDates.getA() != null)
			this.disableFutureDatesField.setValue((Boolean) disableFutureDates.getA());
	}

	@Override
	public void writeToField(DateTimeQueryField field) {
		//		field.setPrimaryColor(this.headerColorField.getValue());
		Tuple2<String, String> t1 = new Tuple2<String, String>(this.enableLastNDaysField.getValue(), null);
		Tuple2<Boolean, Boolean> t2 = new Tuple2<Boolean, Boolean>(this.disableFutureDatesField.getValue(), false);
		field.getField().setEnableLastNDays(t1);
		field.getField().setDisableFutureDates(t2);
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (getHorizSettingsToggle() == field) {
			FormPortletToggleButtonsField<Byte> tbf = (FormPortletToggleButtonsField<Byte>) field;
			if (tbf.getValue() == BaseEditFieldPortlet.FIELD_ALIGN_ADVANCED) {
				this.enableLastNDaysField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 400, 220, 16);
				this.disableFutureDatesField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 430, FormPortletCheckboxField.DEFAULT_DIM,
						FormPortletCheckboxField.DEFAULT_DIM);
			} else {
				this.enableLastNDaysField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 300, 220, 16);
				this.disableFutureDatesField.setLeftTopWidthHeightPx(BaseEditFieldPortlet.COL1_HORIZONTAL_POS_PX, 330, FormPortletCheckboxField.DEFAULT_DIM,
						FormPortletCheckboxField.DEFAULT_DIM);
			}
		}
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public boolean submit() {
		String enableLastNDays = this.enableLastNDaysField.getValue();
		// first check if it is an empty str, then check if it is not an int or if it casts to a negative number
		if (SH.is(enableLastNDays)) {
			try {
				if (SH.parseInt(enableLastNDays) < 0) {
					getManager().showAlert("<b>Enable Last N Days </b> must be a positive integer");
					return false;
				}
			} catch (NumberFormatException e) {
				getManager().showAlert("<b>Enable Last N Days </b> must be a positive integer");
				return false;
			}
		}
		Tuple2<String, String> t = new Tuple2<String, String>(this.enableLastNDaysField.getValue(), null);
		this.queryField.getField().setEnableLastNDays(t);
		Tuple2<Boolean, Boolean> t1 = new Tuple2<Boolean, Boolean>(this.disableFutureDatesField.getValue(), null);
		this.queryField.getField().setDisableFutureDates(t1);
		return super.submit();
	}

}
