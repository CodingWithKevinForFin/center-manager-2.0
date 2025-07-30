package com.f1.ami.web.charts;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDateTimeFormPortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Long;

public class AmiWebChartEditAxisPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private static final int GRID_HEIGHT = 700;
	final private FormPortlet form1;
	final private AmiWebDateTimeFormPortlet minValueFormDateTime;
	final private AmiWebDateTimeFormPortlet maxValueFormDateTime;
	final private FormPortlet minValueFormNumeric;
	final private FormPortlet maxValueFormNumeric;
	final private FormPortlet form2;
	final private FormPortlet form3;
	final private InnerPortlet autoMinInner;
	final private InnerPortlet autoMaxInner;
	final private FormPortletTextField minValueField;
	final private FormPortletTextField maxValueField;
	final private FormPortletToggleButtonsField<Boolean> reverseToggle;
	final private FormPortletTextField majorUnitField;
	final private FormPortletTextField minorUnitField;
	final private FormPortletTextField numberFormulaField;
	final private FormPortletTextField titleField;
	final private FormPortletToggleButtonsField<Boolean> sortGroupTitles;
	final private FormPortletButton closeButton;
	final private AmiWebChartAxisPortlet axis;
	final private FormPortletToggleButtonsField<Boolean> minValueAutoToggle;
	final private FormPortletToggleButtonsField<Boolean> maxValueAutoToggle;
	final private FormPortletToggleButtonsField<Boolean> majorUnitAutoToggle;
	final private FormPortletToggleButtonsField<Boolean> minorUnitAutoToggle;
	final private FormPortletSelectField<Byte> formatTypeField = new FormPortletSelectField<Byte>(Byte.class, "Axis Format Type:");
	final private static byte GRID_Y_POS_FORM1 = 0;
	final private static byte GRID_Y_POS_AUTO_MIN_FORM = 1;
	final private static byte GRID_Y_POS_FORM2 = 2;
	final private static byte GRID_Y_POS_AUTO_MAX_FORM = 3;
	final private static byte GRID_Y_POS_FORM3 = 4;

	public AmiWebChartEditAxisPortlet(PortletConfig config, AmiWebChartAxisPortlet axis) {
		super(config);
		this.axis = axis;
		this.form1 = new FormPortlet(generateConfig());
		this.form2 = new FormPortlet(generateConfig());
		this.form3 = new FormPortlet(generateConfig());
		this.minValueFormDateTime = new AmiWebDateTimeFormPortlet(generateConfig(), (long) this.axis.getMinValue());
		this.maxValueFormDateTime = new AmiWebDateTimeFormPortlet(generateConfig(), (long) this.axis.getMaxValue());
		this.minValueFormDateTime.setTitle("Min Value:");
		this.maxValueFormDateTime.setTitle("Max Value:");
		this.minValueFormNumeric = new FormPortlet(generateConfig());
		this.maxValueFormNumeric = new FormPortlet(generateConfig());

		// form1
		this.form1.addField(new FormPortletTitleField("Title").setHeight(12));
		this.form1.addField(new FormPortletTitleField("").setHeight(5));
		this.titleField = form1.addField(new FormPortletTextField("Title:"));
		this.sortGroupTitles = form1.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Sub-Title Ordering:"));
		this.sortGroupTitles.addOption(true, "Sorted");
		this.sortGroupTitles.addOption(false, "Keep original Ordering");
		this.form1.addField(new FormPortletTitleField("Numbers").setHeight(20));
		this.form1.addField(this.formatTypeField);
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_AUTO, "Auto");
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_NUMERIC, "Numeric");
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_DATE, "Date");
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_TIME, "Time");
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_DATETIME, "Date-Time");
		this.formatTypeField.addOption(AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM, "Custom");
		byte formatType = this.axis.getFormatType();
		if (formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_AUTO) {
			if (this.axis.getAutoMinValue() == false || this.axis.getAutoMaxValue() == false) {
				formatType = AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM;
			}
		}
		this.formatTypeField.setValue(formatType);
		this.numberFormulaField = form1.addField(new FormPortletTextField("Format Formula:")).setHasButton(true).setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.reverseToggle = form1.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Ordering:"));
		this.reverseToggle.addOption(true, "Descending");
		this.reverseToggle.addOption(false, "Ascending");
		this.reverseToggle.setMode('T');
		this.form1.addField(new FormPortletTitleField("").setHeight(5));
		this.minValueAutoToggle = this.form1.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Min Value:"));
		this.minValueAutoToggle.addOption(true, "Auto");
		this.minValueAutoToggle.addOption(false, "User select");

		// autoMinFormNumeric
		this.minValueField = this.minValueFormNumeric.addField(new FormPortletTextField("Min Value:"));

		// form2
		this.form2.addField(new FormPortletTitleField("").setHeight(5));
		this.maxValueAutoToggle = this.form2.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Max Value:"));
		this.maxValueAutoToggle.addOption(true, "Auto");
		this.maxValueAutoToggle.addOption(false, "User select");

		// autoMaxFormNumeric
		this.maxValueField = maxValueFormNumeric.addField(new FormPortletTextField("Max Value:"));

		// form3
		this.form3.addField(new FormPortletTitleField("").setHeight(5));
		this.majorUnitAutoToggle = form3.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Major-Ticks Unit:"));
		this.majorUnitAutoToggle.addOption(true, "Auto");
		this.majorUnitAutoToggle.addOption(false, "User select");
		this.majorUnitField = form3.addField(new FormPortletTextField("Major-Ticks Unit:"));
		this.form3.addField(new FormPortletTitleField("").setHeight(5));
		this.minorUnitAutoToggle = form3.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Minor-Ticks Unit:"));
		this.minorUnitAutoToggle.addOption(true, "Auto");
		this.minorUnitAutoToggle.addOption(false, "User select");
		this.minorUnitField = form3.addField(new FormPortletTextField("Minor-Ticks Unit:"));

		this.closeButton = this.form3.addButton(new FormPortletButton("Close"));

		this.titleField.setValue(this.axis.getTitle());
		this.sortGroupTitles.setValue(this.axis.getGroupOrdering());

		this.numberFormulaField.setValue(this.axis.getNumberFormula());
		this.reverseToggle.setValue(this.axis.isReverse());

		this.minValueAutoToggle.setValue(this.axis.getAutoMinValue());
		this.minValueField.setValue(SH.toString(this.axis.getMinValue()));

		this.maxValueAutoToggle.setValue(this.axis.getAutoMaxValue());
		this.maxValueField.setValue(SH.toString(this.axis.getMaxValue()));

		this.majorUnitAutoToggle.setValue(this.axis.getAutoMajorValue());
		this.majorUnitField.setValue(SH.toString(this.axis.getMajorUnit()));

		this.minorUnitAutoToggle.setValue(this.axis.getAutoMinorValue());
		this.minorUnitField.setValue(SH.toString(this.axis.getMinorUnit()));

		addChild(this.form1, 0, GRID_Y_POS_FORM1);
		boolean isDateTime = formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_DATETIME;
		this.autoMinInner = addChild(isDateTime ? this.minValueFormDateTime : this.minValueFormNumeric, 0, GRID_Y_POS_AUTO_MIN_FORM, 1, 1);
		addChild(this.form2, 0, GRID_Y_POS_FORM2);
		this.autoMaxInner = addChild(isDateTime ? this.maxValueFormDateTime : this.maxValueFormNumeric, 0, GRID_Y_POS_AUTO_MAX_FORM, 1, 1);
		addChild(this.form3, 0, GRID_Y_POS_FORM3);
		addHiddenForms(formatType);
		setRowSize(GRID_Y_POS_FORM1, (int) (0.40 * GRID_HEIGHT));
		setRowSize(GRID_Y_POS_AUTO_MIN_FORM, (int) (0.11 * GRID_HEIGHT));
		setRowSize(GRID_Y_POS_FORM2, (int) (0.08 * GRID_HEIGHT));
		setRowSize(GRID_Y_POS_AUTO_MAX_FORM, (int) (0.11 * GRID_HEIGHT));
		setSuggestedSize(450, GRID_HEIGHT);
		Set<FormPortlet> forms = CH.s(this.form1, this.maxValueFormDateTime, this.maxValueFormNumeric, this.minValueFormDateTime, this.minValueFormNumeric, this.form2, this.form3);
		for (FormPortlet fp : forms) {
			fp.addFormPortletListener(this);
			fp.addMenuListener(this);
			fp.setMenuFactory(this);
		}
		setFormatType(formatType);
		updateDisabledFields();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		this.close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.formatTypeField) {
			byte formatType = this.formatTypeField.getValue().byteValue();
			this.axis.setFormatType(formatType);
			setFormatType(formatType);
			if (formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_AUTO) {
				this.minValueAutoToggle.setValue(true);
				this.maxValueAutoToggle.setValue(true);
				this.majorUnitAutoToggle.setValue(true);
				this.minorUnitAutoToggle.setValue(true);
				this.autoMinInner.setPortlet(this.minValueFormNumeric);
				this.autoMaxInner.setPortlet(this.maxValueFormNumeric);
			}
		} else if (!this.minValueAutoToggle.getValue() && this.minValueFormDateTime.hasField(field)) {
			long value = this.minValueFormDateTime.getValue();
			this.axis.setMinValue(value);
			this.minValueField.setValue(SH.toString(value));
		} else if (!this.maxValueAutoToggle.getValue() && this.maxValueFormDateTime.hasField(field)) {
			long value = this.maxValueFormDateTime.getValue();
			this.axis.setMaxValue(value);
			this.maxValueField.setValue(SH.toString(value));
		} else if (field == this.minValueField) {
			copyDoubleToDateTimeField(this.minValueField, this.minValueFormDateTime);
		} else if (field == this.maxValueField) {
			copyDoubleToDateTimeField(this.maxValueField, this.maxValueFormDateTime);
		}
		updateDisabledFields();

		updateValues(new StringBuilder(), field);

	}
	private static void copyDoubleToDateTimeField(FormPortletTextField field, AmiWebDateTimeFormPortlet dateTimeForm) {
		long dateVal;
		try {
			dateVal = Caster_Long.INSTANCE.cast(Caster_Double.INSTANCE.castOr(field.getValue(), 0d));
			dateTimeForm.setValue(dateVal);
		} catch (NumberFormatException nfe) {
		}
	}
	private void addHiddenForms(byte formatType) {
		switch (formatType) {
			case AmiWebChartAxisPortlet.FORMAT_TYPE_NUMERIC:
				getManager().onPortletAdded(this.minValueFormDateTime);
				getManager().onPortletAdded(this.maxValueFormDateTime);
				break;
			case AmiWebChartAxisPortlet.FORMAT_TYPE_DATETIME:
				getManager().onPortletAdded(this.minValueFormNumeric);
				getManager().onPortletAdded(this.maxValueFormNumeric);
				break;
		}

	}
	private void setFormatType(byte formatType) {
		switch (formatType) {
			case AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM:
				if (SH.is(this.axis.getNumberFormula())) {
					this.numberFormulaField.setValue(this.axis.getNumberFormula());
				} else if (SH.is(this.numberFormulaField.getValue())) {
					this.axis.setNumberFormula(this.numberFormulaField.getValue());
				} else {
					String numberFormula = "formatNumber(n,\"#,###\",\"\")";
					this.axis.setNumberFormula(numberFormula);
					this.numberFormulaField.setValue(numberFormula);
				}
				this.autoMinInner.setPortlet(this.minValueFormNumeric);
				this.autoMaxInner.setPortlet(this.maxValueFormNumeric);
				getManager().onPortletAdded(this.minValueFormNumeric);
				getManager().onPortletAdded(this.maxValueFormNumeric);
				break;
			case AmiWebChartAxisPortlet.FORMAT_TYPE_NUMERIC:
				this.autoMinInner.setPortlet(this.minValueFormNumeric);
				this.autoMaxInner.setPortlet(this.maxValueFormNumeric);
				getManager().onPortletAdded(this.minValueFormNumeric);
				getManager().onPortletAdded(this.maxValueFormNumeric);
				this.axis.setNumberFormula(null);
				break;
			case AmiWebChartAxisPortlet.FORMAT_TYPE_DATE:
			case AmiWebChartAxisPortlet.FORMAT_TYPE_TIME:
			case AmiWebChartAxisPortlet.FORMAT_TYPE_DATETIME:
				this.autoMinInner.setPortlet(this.minValueFormDateTime);
				this.autoMaxInner.setPortlet(this.maxValueFormDateTime);
				this.axis.setNumberFormula(null);
				getManager().onPortletAdded(this.minValueFormDateTime);
				getManager().onPortletAdded(this.maxValueFormDateTime);
				break;
		}
	}
	private void updateValues(StringBuilder sink, FormPortletField<?> field) {
		this.axis.setTitle(this.titleField.getValue());
		this.axis.setGroupOrdering(this.sortGroupTitles.getValue());

		if (this.formatTypeField.getValue().byteValue() == AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM) {
			try {
				this.axis.setNumberFormula(this.numberFormulaField.getValue());
			} catch (Exception e) {
				sink.append("Error with number format: " + e.getMessage());
				return;
			}
		}
		this.axis.setReverse(this.reverseToggle.getValue());

		this.axis.setAutoMinValue(this.minValueAutoToggle.getValue());
		try {
			if (!this.minValueAutoToggle.getValue() && this.minValueFormNumeric.hasField(this.minValueField))
				this.axis.setMinValue(SH.parseDouble(this.minValueField.getValue()));
		} catch (Exception e) {
		}

		this.axis.setAutoMaxValue(this.maxValueAutoToggle.getValue());
		try {
			if (!this.maxValueAutoToggle.getValue() && this.maxValueFormNumeric.hasField(this.maxValueField))
				this.axis.setMaxValue(SH.parseDouble(this.maxValueField.getValue()));
		} catch (Exception e) {
		}

		this.axis.setAutoMajorValue(this.majorUnitAutoToggle.getValue());
		this.axis.setMajorUnit(toDouble(this.majorUnitField.getValue(), this.axis.getMajorUnit()));

		this.axis.setAutoMinorValue(this.minorUnitAutoToggle.getValue());
		this.axis.setMinorUnit(toDouble(this.minorUnitField.getValue(), this.axis.getMinorUnit()));

	}

	private void updateDisabledFields() {
		byte formatType = this.formatTypeField.getValue().byteValue();
		if (formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_AUTO) {
			this.numberFormulaField.setDisabled(true);
			this.minValueAutoToggle.setDisabled(true);
			this.maxValueAutoToggle.setDisabled(true);
			this.minValueField.setDisabled(true);
			this.maxValueField.setDisabled(true);
			this.minValueFormDateTime.setDisabled(true);
			this.maxValueFormDateTime.setDisabled(true);
			this.majorUnitField.setDisabled(true);
			this.minorUnitField.setDisabled(true);
			this.majorUnitAutoToggle.setDisabled(true);
			this.minorUnitAutoToggle.setDisabled(true);
		} else {
			boolean isCustom = formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM;
			boolean isNumericOrCustom = formatType == AmiWebChartAxisPortlet.FORMAT_TYPE_NUMERIC || isCustom;
			this.minValueAutoToggle.setDisabled(false);
			this.maxValueAutoToggle.setDisabled(false);
			this.numberFormulaField.setDisabled(!isCustom);
			this.minValueField.setDisabled(this.minValueAutoToggle.getValue());
			this.maxValueField.setDisabled(this.maxValueAutoToggle.getValue());
			this.minValueFormDateTime.setDisabled(this.minValueAutoToggle.getValue());
			this.maxValueFormDateTime.setDisabled(this.maxValueAutoToggle.getValue());
			this.majorUnitField.setDisabled(!isNumericOrCustom || this.majorUnitAutoToggle.getValue());
			this.minorUnitField.setDisabled(!isNumericOrCustom || this.minorUnitAutoToggle.getValue());
			this.majorUnitAutoToggle.setDisabled(!isNumericOrCustom);
			this.minorUnitAutoToggle.setDisabled(!isNumericOrCustom);
		}
	}

	private double toDouble(String value, double dflt) {
		try {
			return SH.parseDouble(value);
		} catch (Exception e) {
			return dflt;
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (field == this.numberFormulaField) {
			BasicWebMenu r = new BasicWebMenu();
			r.add(new BasicWebMenuLink("n", true, "var_n"));
			r.add(new BasicWebMenuDivider());
			AmiWebMenuUtils.createOperatorsMenu(r, this.axis.getChart().getService(), this.axis.getChart().getAmiLayoutFullAlias());
			return r;
		} else
			return null;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(this.axis.getChart().getService(), action, node);
	}
}
