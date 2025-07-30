package com.f1.ami.web.form.field;

import java.util.Map;

import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDateRangeFieldFactory;
import com.f1.ami.web.form.queryfield.DateRangeQueryField;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class DateRangeFieldEditFormPortlet extends BaseEditFieldPortlet<DateRangeQueryField> implements FormPortletListener {

	//	private FormPortletColorField headerColorField;
	private FormPortletTextField enableLastNDaysFieldStart;
	private FormPortletCheckboxField disableFutureDatesFieldStart;
	private FormPortletTextField enableLastNDaysFieldEnd;
	private FormPortletCheckboxField disableFutureDatesFieldEnd;

	public DateRangeFieldEditFormPortlet(AmiWebFormDateRangeFieldFactory factory, AmiWebQueryFormPortlet queryFormPortlet, int fieldX, int fieldY) {
		super(factory, queryFormPortlet, fieldX, fieldY);
		FormPortlet settingsForm = getSettingsForm();
		this.enableLastNDaysFieldStart = settingsForm.addField(new FormPortletTextField("Enable Last N Days for start: "));
		this.enableLastNDaysFieldStart.setHelp(
				"If set to X (non-negative integer), then allow selection only within the past X days from today in the <b>left</b> calendar. Leave blank to allow selection on all past dates.");
		this.enableLastNDaysFieldEnd = settingsForm.addField(new FormPortletTextField("Enable Last N Days for end: "));
		this.enableLastNDaysFieldEnd.setHelp(
				"If set to X (non-negative integer), then allow selection only within the past X days from today in the <b>right</b> calendar. Leave blank to allow selection on all past dates.");
		this.disableFutureDatesFieldStart = settingsForm.addField(new FormPortletCheckboxField("Disable Future Days for start: ", false));
		this.disableFutureDatesFieldStart
				.setHelp("If toggled, disallow selecting future days from <b>today </b>in the <b>left</b> calendar. Leave blank to allow selection of future dates.");
		this.disableFutureDatesFieldEnd = settingsForm.addField(new FormPortletCheckboxField("Disable Future Days for end: ", false));
		this.disableFutureDatesFieldEnd
				.setHelp("If toggled, disallow selecting future days from <b>today </b>in the left calendar in the <b>right</b>. Leave blank to allow selection of future dates.");
		updateSettingsPositions();
	}

	@Override
	public void readFromField(DateRangeQueryField field) {
		//		this.headerColorField.setValue(field.getPrimaryColor());
		Tuple2<?, ?> enableLastNDays = field.getField().getEnableLastNDays(); // throws NPE if specify type
		Tuple2<Boolean, Boolean> disableFutureDates = field.getField().getDisableFutureDates();
		if (enableLastNDays.getA() != null)
			this.enableLastNDaysFieldStart.setValue(enableLastNDays.getA().toString());
		if (enableLastNDays.getB() != null)
			this.enableLastNDaysFieldEnd.setValue(field.getField().getEnableLastNDays().getB().toString());
		if (disableFutureDates.getA() != null)
			this.disableFutureDatesFieldStart.setValue((Boolean) disableFutureDates.getA());
		if (disableFutureDates.getB() != null)
			this.disableFutureDatesFieldEnd.setValue((Boolean) disableFutureDates.getB());
		updateSettingsPositions();
	}

	@Override
	public void writeToField(DateRangeQueryField field) {
		//		field.setPrimaryColor(this.headerColorField.getValue());
		String value = this.enableLastNDaysFieldStart.getValue();
		Tuple2<String, String> t1 = new Tuple2<String, String>(SH.is(value) ? value : null, this.enableLastNDaysFieldEnd.getValue());
		Tuple2<Boolean, Boolean> t2 = new Tuple2<Boolean, Boolean>(this.disableFutureDatesFieldStart.getBooleanValue(), this.disableFutureDatesFieldEnd.getBooleanValue());
		field.getField().setEnableLastNDays(t1);
		field.getField().setDisableFutureDates(t2);
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (getHorizSettingsToggle() == field) {
			updateSettingsPositions();
		}
		super.onFieldValueChanged(portlet, field, attributes);
	}

	private void updateSettingsPositions() {
		byte align = getHorizontalSettingsToggle();
		int top;
		if (align == BaseEditFieldPortlet.FIELD_ALIGN_ADVANCED) {
			top = 400;
		} else {
			top = 300;
		}
		this.enableLastNDaysFieldStart.setLeftTopWidthHeightPx(200, top, 80, 25);//don't use layout manager autoSizing
		this.enableLastNDaysFieldEnd.setLeftTopWidthHeightPx(200, 30 + top, 80, 25);//don't use layout manager autoSizing
		this.disableFutureDatesFieldStart.setLeftPosPx(500);
		this.disableFutureDatesFieldStart.setTopPosPx(5 + top);//100 is the vertical distance added
		this.disableFutureDatesFieldEnd.setLeftPosPx(500);
		this.disableFutureDatesFieldEnd.setTopPosPx(30 + 5 + top);
	}

	@Override
	public boolean submit() {
		String enableLastNDaysStart = this.enableLastNDaysFieldStart.getValue();
		String enableLastNDaysEnd = this.enableLastNDaysFieldEnd.getValue();
		// first check if it is an empty str, then check if it is not an int or if it casts to a negative number
		if (SH.is(enableLastNDaysStart)) {
			try {
				if (SH.parseInt(enableLastNDaysStart) < 0) {
					getManager().showAlert("<b>Enable Last N Days for start</b> must be a positive number");
					return false;
				}
			} catch (NumberFormatException e) {
				getManager().showAlert("<b>Enable Last N Days for start</b> must be a positive number");
				return false;
			}
		}
		if (SH.is(enableLastNDaysEnd)) {
			try {
				if (SH.parseInt(enableLastNDaysEnd) < 0) {
					getManager().showAlert("<b>Enable Last N Days for end</b> must be a positive number");
					return false;
				}
			} catch (NumberFormatException e) {
				getManager().showAlert("<b>Enable Last N Days for end</b> must be a positive number");
				return false;
			}
		}

		Tuple2<String, String> t = new Tuple2<String, String>(enableLastNDaysStart, enableLastNDaysEnd);
		this.queryField.getField().setEnableLastNDays(t);
		Tuple2<Boolean, Boolean> t1 = new Tuple2<Boolean, Boolean>(this.disableFutureDatesFieldStart.getBooleanValue(), this.disableFutureDatesFieldEnd.getBooleanValue());
		this.queryField.getField().setDisableFutureDates(t1);

		return super.submit();
	}

}
