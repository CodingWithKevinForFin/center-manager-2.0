package com.f1.ami.web;

import java.util.Calendar;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebDateTimeFormPortlet extends FormPortlet implements FormPortletListener {

	private final AmiWebVarsManager varsManager = AmiWebUtils.getService(getManager()).getVarsManager();
	private final boolean useMiltaryTime = !this.varsManager.getTimeFormat().contains("a");
	private static final int FIELDS_LEFTMOST_POS_PX = 164;
	private static final int FIELDS_TOP_POS_PX = 9;
	private static final int FIELDS_TOP_POS_PX_ROW2 = FIELDS_TOP_POS_PX + FormPortletField.DEFAULT_HEIGHT + 25;
	private final FormPortletSelectField<Integer> monthField = new FormPortletSelectField<Integer>(Integer.class, "");
	private final FormPortletSelectField<Integer> dayField = new FormPortletSelectField<Integer>(Integer.class, "");
	private final FormPortletNumericRangeField yearField = new FormPortletNumericRangeField("", 0, 10000, 0);
	private final FormPortletNumericRangeField hourField = new FormPortletNumericRangeField("Hour", this.useMiltaryTime ? 0 : 1, this.useMiltaryTime ? 23 : 12, 0);
	private final FormPortletNumericRangeField minuteField = new FormPortletNumericRangeField("Min", 0, 59, 0);
	private final FormPortletNumericRangeField secondField = new FormPortletNumericRangeField("Sec", 0, 59, 0);
	private final FormPortletNumericRangeField millisecField = new FormPortletNumericRangeField("Millisec", 0, 999, 0);
	private final FormPortletSelectField<Boolean> amPmField = new FormPortletSelectField<Boolean>(Boolean.class, "");
	private final static String[] MONTH_NAMES = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	private final static int[] MONTH_IDS = { Calendar.JANUARY, Calendar.FEBRUARY, Calendar.MARCH, Calendar.APRIL, Calendar.MAY, Calendar.JUNE, Calendar.JULY, Calendar.AUGUST,
			Calendar.SEPTEMBER, Calendar.OCTOBER, Calendar.NOVEMBER, Calendar.DECEMBER };
	private final Calendar calendar = Calendar.getInstance();
	private boolean isDisabled = false;
	private final static int[] MILITARY_2_CIVILIAN = { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

	public AmiWebDateTimeFormPortlet(PortletConfig config, long timeMillis) {
		super(config);
		this.calendar.setTimeZone(this.varsManager.getTimeZone());
		this.calendar.setTimeInMillis(timeMillis);
		layoutFields();

		// Add months
		for (int i = 0; i < MONTH_IDS.length; i++) {
			this.monthField.addOptionNoFire(MONTH_IDS[i], MONTH_NAMES[i]);
		}
		// Add days
		for (int i = 1; i <= this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
			this.dayField.addOptionNoFire(i, SH.toString(i));
		}
		setFieldValues();
		addFormPortletListener(this);
	}
	private void layoutFields() {
		addField(this.yearField);
		addField(this.dayField);
		addField(this.monthField);
		addField(this.hourField);
		addField(this.minuteField);
		addField(this.secondField);
		addField(this.millisecField);
		if (!this.useMiltaryTime) {
			addField(this.amPmField);
			this.amPmField.setLabelHidden(true);
			this.amPmField.addOptionNoFire(true, "AM");
			this.amPmField.addOptionNoFire(false, "PM");
		}
		this.yearField.setSliderHidden(true);
		this.yearField.setTextHidden(false);
		this.hourField.setSliderHidden(true);
		this.hourField.setTextHidden(false);
		this.minuteField.setSliderHidden(true);
		this.minuteField.setTextHidden(false);
		this.secondField.setSliderHidden(true);
		this.secondField.setTextHidden(false);
		this.millisecField.setSliderHidden(true);
		this.millisecField.setTextHidden(false);

		this.monthField.setLeftPosPx(FIELDS_LEFTMOST_POS_PX);
		this.monthField.setWidthPx(50);
		this.monthField.setTopPosPx(FIELDS_TOP_POS_PX);
		this.monthField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.dayField.setLeftPosPx(this.monthField.getLeftPosPx() + this.monthField.getWidthPx() + 5);
		this.dayField.setWidthPx(40);
		this.dayField.setTopPosPx(FIELDS_TOP_POS_PX);
		this.dayField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.yearField.setLeftPosPx(this.dayField.getLeftPosPx() + this.dayField.getWidthPx() + 5);
		this.yearField.setWidthPx(70);
		this.yearField.setTopPosPx(FIELDS_TOP_POS_PX);
		this.yearField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);

		this.hourField.setLeftPosPx(FIELDS_LEFTMOST_POS_PX);
		this.hourField.setWidthPx(50);
		this.hourField.setTopPosPx(FIELDS_TOP_POS_PX_ROW2);
		this.hourField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.minuteField.setLeftPosPx(this.hourField.getLeftPosPx() + this.hourField.getWidthPx());
		this.minuteField.setWidthPx(50);
		this.minuteField.setTopPosPx(FIELDS_TOP_POS_PX_ROW2);
		this.minuteField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.secondField.setLeftPosPx(this.minuteField.getLeftPosPx() + this.minuteField.getWidthPx());
		this.secondField.setWidthPx(50);
		this.secondField.setTopPosPx(FIELDS_TOP_POS_PX_ROW2);
		this.secondField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.millisecField.setLeftPosPx(this.secondField.getLeftPosPx() + this.secondField.getWidthPx());
		this.millisecField.setWidthPx(60);
		this.millisecField.setTopPosPx(FIELDS_TOP_POS_PX_ROW2);
		this.millisecField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		if (!this.useMiltaryTime) {
			this.amPmField.setLeftPosPx(this.millisecField.getLeftPosPx() + this.millisecField.getWidthPx());
			this.amPmField.setWidthPx(40);
			this.amPmField.setTopPosPx(FIELDS_TOP_POS_PX_ROW2);
			this.amPmField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		}

		this.hourField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.hourField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
		this.minuteField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.minuteField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
		this.secondField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.secondField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
		this.millisecField.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
		this.millisecField.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
	}
	public void setValue(long timeMillis) {
		this.calendar.setTimeInMillis(timeMillis);
		setFieldValues();
	}
	private void setFieldValues() {
		this.monthField.setValueNoFire(this.calendar.get(Calendar.MONTH));
		this.dayField.setValueNoFire(this.calendar.get(Calendar.DAY_OF_MONTH));
		this.yearField.setValue(this.calendar.get(Calendar.YEAR));
		setHourField(this.calendar.get(Calendar.HOUR_OF_DAY));
		this.minuteField.setValue(this.calendar.get(Calendar.MINUTE));
		this.secondField.setValue(this.calendar.get(Calendar.SECOND));
		this.millisecField.setValue(this.calendar.get(Calendar.MILLISECOND));
	}
	private void setDaysFieldRange(int max) {
		if (max == this.dayField.getOptionsCount()) {
			return;
		}
		this.dayField.clearOptions();
		for (int i = 1; i <= max; i++) {
			this.dayField.addOptionNoFire(i, SH.toString(i));
		}
	}
	public long getTimeMillis() {
		return this.calendar.getTimeInMillis();
	}
	public void setTimeMillis(long timeMillis) {
		this.calendar.setTimeInMillis(timeMillis);
	}
	public void setDisabled(boolean disabled) {
		for (int i = 0; i < getFieldsCount(); i++) {
			getFieldAt(i).setDisabled(disabled);
		}
		this.isDisabled = disabled;
	}
	public boolean isDisabled() {
		return this.isDisabled;
	}
	public void setTitle(String title) {
		this.monthField.setTitle(title);
	}
	public String getTitle() {
		return this.monthField.getTitle();
	}
	private static int getDaysInMonth(int month, int year) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	public long getValue() {
		return this.calendar.getTimeInMillis();
	}
	public String getValueFormatted() {
		return AmiUtils.s(this.calendar.getTime());
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.monthField) {
			Integer prevDayVal = this.dayField.getValue();
			int prevDay = prevDayVal == null ? 1 : prevDayVal.intValue();

			// Dial back day to fit within new month
			int newMonth = this.monthField.getValue().intValue();
			int newMaxDays = getDaysInMonth(newMonth, this.calendar.get(Calendar.YEAR));
			if (prevDay > newMaxDays) {
				this.calendar.set(Calendar.DAY_OF_MONTH, newMaxDays);
			}
			this.calendar.set(Calendar.MONTH, newMonth);
			setDaysFieldRange(newMaxDays);
			this.dayField.setValueNoFire(OH.min(newMaxDays, prevDay));
		} else if (field == this.dayField) {
			this.calendar.set(Calendar.DAY_OF_MONTH, this.dayField.getValue().intValue());
		} else if (field == this.yearField) {
			this.calendar.set(Calendar.YEAR, this.yearField.getValue().intValue());
		} else if (field == this.hourField) {
			setCalendarHour(this.hourField.getValue().intValue());
		} else if (field == this.minuteField) {
			this.calendar.set(Calendar.MINUTE, this.minuteField.getValue().intValue());
		} else if (field == this.secondField) {
			this.calendar.set(Calendar.SECOND, this.secondField.getValue().intValue());
		} else if (field == this.millisecField) {
			this.calendar.set(Calendar.MILLISECOND, this.millisecField.getValue().intValue());
		} else if (field == this.amPmField) {
			this.calendar.set(Calendar.HOUR_OF_DAY, getMilitaryHour(this.hourField.getValue().intValue(), this.amPmField.getValue().booleanValue()));
		}
	}
	private void setHourField(int hour) {
		if (this.useMiltaryTime) {
			this.hourField.setValue(this.calendar.get(Calendar.HOUR_OF_DAY));
		} else {
			int calHrMil = this.calendar.get(Calendar.HOUR_OF_DAY);
			this.hourField.setValue(MILITARY_2_CIVILIAN[calHrMil]);
			this.amPmField.setValue(calHrMil < 12);
		}
	}
	private void setCalendarHour(int hour) {
		if (this.useMiltaryTime) {
			this.calendar.set(Calendar.HOUR_OF_DAY, this.hourField.getValue().intValue());
		} else {
			this.calendar.set(Calendar.HOUR_OF_DAY, getMilitaryHour(this.hourField.getValue().intValue(), this.amPmField.getValue().booleanValue()));
		}
	}
	private static int getMilitaryHour(int civHour, boolean am) {
		int milHour = 0;
		for (int i = 0; i < 12; i++) {
			if (MILITARY_2_CIVILIAN[i] == civHour) {
				milHour = i;
			}
		}
		if (!am) {
			milHour += 12;
		}
		return milHour;
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
}
