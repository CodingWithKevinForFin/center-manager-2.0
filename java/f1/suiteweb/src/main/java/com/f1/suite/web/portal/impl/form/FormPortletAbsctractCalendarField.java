package com.f1.suite.web.portal.impl.form;

import com.f1.suite.web.JsFunction;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public abstract class FormPortletAbsctractCalendarField<TYPE> extends FormPortletField<TYPE> {
	public static final String DISABLE_FUTURE_DATES_L = "disableFutureDatesL";
	public static final String DISABLE_FUTURE_DATES_R = "disableFutureDatesR";
	public static final String ENABLE_LAST_N_DAYS_L = "enableLastNDaysL";
	public static final String ENABLE_LAST_N_DAYS_R = "enableLastNDaysR";
	private String calBgColor;
	private String calBtnBgColor;
	private String calYrFgColor;
	private String calSelYrFgColor;
	private String calMtnFgColor;
	private String calSelMtnFgColor;
	private String calSelMtnBgColor;
	private String calWkFgColor;
	private String calWkBgColor;
	private String calDayFgColor;
	private String calBtnFgColor;
	private String calXDayFgColor;
	private String calHoverBgColor;
	private String headerColor;
	// AmiWebVarsManager handles changes to the user setting as well as initializing default user setting values, both of which will be saved in user form style manager (AmiWeb)
	// AmiWebQueryFormPortlet will fetch the formats from user form style manager where it will eventually propagate to here.
	private String dateDisplayFormat;
	private String timeDisplayFormat;
	// flag calendar style updates
	//	private boolean needsUpdate = false;
	private boolean isRange;
	private Tuple2<Boolean, Boolean> disableFutureDates = new Tuple2<Boolean, Boolean>(false, false);
	// setValue for textField requires a string
	private Tuple2<Integer, Integer> enableLastNDays = new Tuple2<Integer, Integer>(null, null);

	public FormPortletAbsctractCalendarField(Class<TYPE> type, String title, boolean isRange) {
		super(type, title);
		this.setRange(isRange);
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		JsFunction js = new JsFunction();
		if (hasChanged(FormPortletField.MASK_REBUILD))
			js.reset(pendingJs, jsObjectName, "initCalendar").addParam(isRange()).end();
		if (hasChanged(FormPortletField.MASK_STYLE))
			updateCalStyle(pendingJs, jsObjectName);
		if (hasChanged(FormPortletField.MASK_CONFIG)) {
			js.reset(pendingJs, jsObjectName, "setDateDisplayFormat").addParamQuoted(this.dateDisplayFormat).end();
			if (this instanceof FormPortletDateTimeChooserField)
				js.reset(pendingJs, jsObjectName, "setTimeDisplayFormat").addParamQuoted(this.getTimeDisplayFormat()).end();
			js.reset(pendingJs, jsObjectName, "setEnableLastNDays").addParam(this.getEnableLastNDays()).end();
			js.reset(pendingJs, jsObjectName, "setDisableFutureDays").addParam(this.getDisableFutureDates()).end();
		}
		super.updateJs(pendingJs);
	}

	private void updateCalStyle(StringBuilder pendingJs, String objectName) {
		JsFunction js = new JsFunction();
		js.reset(pendingJs, objectName, "setColors").addParamQuoted(this.headerColor).end();
		js.reset(pendingJs, objectName, "setCalendarBgColor").addParamQuoted(getCalBgColor()).end();
		js.reset(pendingJs, objectName, "setBtnBgColor").addParamQuoted(getCalBtnBgColor()).end();
		js.reset(pendingJs, objectName, "setBtnFgColor").addParamQuoted(getCalBtnFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarYearFgColor").addParamQuoted(getCalYrFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarSelYearFgColor").addParamQuoted(getCalSelYrFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarMonthFgColor").addParamQuoted(getCalMtnFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarSelMonthFgColor").addParamQuoted(getCalSelMtnFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarSelMonthBgColor").addParamQuoted(getCalSelMtnBgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarWeekFgColor").addParamQuoted(getCalWkFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarWeekBgColor").addParamQuoted(getCalWkBgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarDayFgColor").addParamQuoted(getCalDayFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarXDayFgColor").addParamQuoted(getCalXDayFgColor()).end();
		js.reset(pendingJs, objectName, "setCalendarHoverBgColor").addParamQuoted(getCalHoverBgColor()).end();
	}

	final public String getHeaderColor() {
		return headerColor;
	}
	final public FormPortletAbsctractCalendarField<TYPE> setHeaderColor(String headerColor) {
		if (OH.eq(this.headerColor, headerColor))
			return this;
		// week fade color
		this.headerColor = headerColor;
		flagStyleChanged();
		return this;
	}

	public void setCalBgColor(String col) {
		if (OH.eq(this.calBgColor, col))
			return;
		this.calBgColor = col;
		flagStyleChanged();
	}
	public String getCalBgColor() {
		return this.calBgColor;
	}
	public String getCalBtnBgColor() {
		return calBtnBgColor;
	}
	public void setCalBtnBgColor(String calBtnBgColor) {
		if (OH.eq(this.calBtnBgColor, calBtnBgColor))
			return;
		this.calBtnBgColor = calBtnBgColor;
		flagStyleChanged();
	}
	public String getCalYrFgColor() {
		return calYrFgColor;
	}
	public void setCalYrFgColor(String calYrFgColor) {
		if (OH.eq(this.calYrFgColor, calYrFgColor))
			return;
		this.calYrFgColor = calYrFgColor;
		flagStyleChanged();
	}
	public String getCalSelYrFgColor() {
		return calSelYrFgColor;
	}
	public void setCalSelYrFgColor(String calSelYrFgColor) {
		if (OH.eq(this.calSelYrFgColor, calSelYrFgColor))
			return;
		this.calSelYrFgColor = calSelYrFgColor;
		flagStyleChanged();
	}
	public String getCalMtnFgColor() {
		return calMtnFgColor;
	}
	public void setCalMtnFgColor(String calMtnFgColor) {
		if (OH.eq(this.calMtnFgColor, calMtnFgColor))
			return;
		this.calMtnFgColor = calMtnFgColor;
		flagStyleChanged();
	}
	public String getCalSelMtnFgColor() {
		return calSelMtnFgColor;
	}
	public void setCalSelMtnFgColor(String calSelMtnFgColor) {
		if (OH.eq(this.calSelMtnFgColor, calSelMtnFgColor))
			return;
		this.calSelMtnFgColor = calSelMtnFgColor;
		flagStyleChanged();
	}
	public String getCalSelMtnBgColor() {
		return calSelMtnBgColor;
	}
	public void setCalSelMtnBgColor(String calSelMtnBgColor) {
		if (OH.eq(this.calSelMtnBgColor, calSelMtnBgColor))
			return;
		this.calSelMtnBgColor = calSelMtnBgColor;
		flagStyleChanged();
	}
	public String getCalWkFgColor() {
		return calWkFgColor;
	}
	public void setCalWkFgColor(String calWkFgColor) {
		if (OH.eq(this.calWkFgColor, calWkFgColor))
			return;
		this.calWkFgColor = calWkFgColor;
		flagStyleChanged();
	}
	public String getCalWkBgColor() {
		return calWkBgColor;
	}
	public void setCalWkBgColor(String calWkBgColor) {
		if (OH.eq(this.calWkBgColor, calWkBgColor))
			return;
		this.calWkBgColor = calWkBgColor;
		flagStyleChanged();
	}
	public String getCalDayFgColor() {
		return calDayFgColor;
	}
	public void setCalDayFgColor(String calDayFgColor) {
		if (OH.eq(this.calDayFgColor, calDayFgColor))
			return;
		this.calDayFgColor = calDayFgColor;
		flagStyleChanged();
	}
	public String getCalBtnFgColor() {
		return calBtnFgColor;
	}
	public void setCalBtnFgColor(String calBtnFgColor) {
		if (OH.eq(this.calBtnFgColor, calBtnFgColor))
			return;
		this.calBtnFgColor = calBtnFgColor;
		flagStyleChanged();
	}
	public String getCalXDayFgColor() {
		return calXDayFgColor;
	}
	public void setCalXDayFgColor(String calXDayFgColor) {
		if (OH.eq(this.calXDayFgColor, calXDayFgColor))
			return;
		this.calXDayFgColor = calXDayFgColor;
		flagStyleChanged();
	}

	public String getCalHoverBgColor() {
		return calHoverBgColor;
	}
	public void setCalHoverBgColor(String calHoverBgColor) {
		if (OH.eq(this.calHoverBgColor, calHoverBgColor))
			return;
		this.calHoverBgColor = calHoverBgColor;
		flagStyleChanged();
	}
	public void setEnableLastNDays(Object o) {
		Tuple2<String, String> tup = (Tuple2<String, String>) o;
		if (OH.eq(enableLastNDays.getA(), tup.getA()) && OH.eq(enableLastNDays.getB(), tup.getB()))
			return;
		if (isRange)
			this.enableLastNDays.setAB(SH.isnt(tup.getA()) ? null : SH.parseInt(tup.getA()), SH.isnt(tup.getB()) ? null : SH.parseInt(tup.getB()));
		else
			this.enableLastNDays.setA(SH.isnt(tup.getA()) ? null : SH.parseInt(tup.getA()));
		flagConfigChanged();
	}
	public void setDisableFutureDates(Object o) {
		Tuple2<Boolean, Boolean> tup = (Tuple2<Boolean, Boolean>) o;
		if (OH.eq(disableFutureDates.getA(), tup.getA()) && OH.eq(disableFutureDates.getB(), tup.getB()))
			return;
		if (isRange)
			this.disableFutureDates.setAB(tup.getA(), tup.getB());
		else
			this.disableFutureDates.setA(tup.getA());
		flagConfigChanged();
	}
	public Tuple2<Integer, Integer> getEnableLastNDays() {
		return enableLastNDays;
	}
	public Tuple2<Boolean, Boolean> getDisableFutureDates() {
		return disableFutureDates;
	}

	public boolean isRange() {
		return isRange;
	}

	public void setRange(boolean isRange) {
		this.isRange = isRange;
	}

	public void setDateDisplayFormat(String dateDisplayFormat) {
		if (OH.eq(this.dateDisplayFormat, dateDisplayFormat))
			return;
		this.dateDisplayFormat = dateDisplayFormat;
		flagConfigChanged();
	}

	public String getTimeDisplayFormat() {
		return timeDisplayFormat;
	}

	public void setTimeDisplayFormat(String timeDisplayFormat) {
		this.timeDisplayFormat = timeDisplayFormat;
		flagConfigChanged();
	}

	public void setValueDisplayFormat(String dateFormat, String timeFormat) {
		setDateDisplayFormat(dateFormat);
		setTimeDisplayFormat(timeFormat);
	}
}
