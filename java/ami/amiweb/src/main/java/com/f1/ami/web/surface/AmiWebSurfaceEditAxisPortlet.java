package com.f1.ami.web.surface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.SH;

public class AmiWebSurfaceEditAxisPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

	final private FormPortlet form;
	final private FormPortletSelectField<Boolean> typeField;
	final private FormPortletTextField minValueField;
	final private FormPortletTextField maxValueField;
	final private FormPortletToggleButtonsField<Boolean> reverseToggle;
	final private FormPortletTextField majorUnitField;
	final private FormPortletTextField minorUnitField;
	final private FormPortletTextField formatField;
	final private FormPortletNumericRangeField startPaddingField;
	final private FormPortletNumericRangeField endPaddingField;
	final private FormPortletNumericRangeField textPaddingField;
	final private FormPortletNumericRangeField minorSizeField;
	final private FormPortletNumericRangeField majorSizeField;
	final private FormPortletTextField titleField;
	final private FormPortletColorField titleColorField;
	final private FormPortletSelectField<String> titleFontField;
	final private FormPortletNumericRangeField titlePaddingField;
	final private FormPortletNumericRangeField titleSizeField;
	final private FormPortletButton closeButton;
	final private AmiWebSurfaceAxisPortlet axis;
	private FormPortletToggleButtonsField<Boolean> minValueAutoToggle;
	private FormPortletToggleButtonsField<Boolean> maxValueAutoToggle;
	private FormPortletToggleButtonsField<Boolean> majorUnitAutoToggle;
	private FormPortletToggleButtonsField<Boolean> minorUnitAutoToggle;
	private FormPortletColorField lineColorField;
	private FormPortletColorField otherLineColorField;
	private List<FormPortletField> numericOnlyFields = new ArrayList<FormPortletField>();

	public AmiWebSurfaceEditAxisPortlet(PortletConfig config, AmiWebSurfaceAxisPortlet axis) {
		super(config);
		this.axis = axis;
		this.form = new FormPortlet(generateConfig());
		this.form.addField(new FormPortletTitleField("Title"));
		this.titleField = form.addField(new FormPortletTextField("Title:"));
		this.titleSizeField = form.addField(new FormPortletNumericRangeField("Title Size:"));
		this.titleFontField = form.addField(new FormPortletSelectField<String>(String.class, "Title Font:"));
		this.titlePaddingField = form.addField(new FormPortletNumericRangeField("Title Padding:"));
		this.titleColorField = form.addField(new FormPortletColorField("Title Color:"));

		this.form.addField(new FormPortletTitleField("Values"));
		this.formatField = form.addField(new FormPortletTextField("Formating:")).setHasButton(true).setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.typeField = form.addField(new FormPortletSelectField<Boolean>(Boolean.class, "Type:").addOption(true, "Numerical").addOption(false, "Series").setDisabled(true));

		this.reverseToggle = form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Ordering:"));
		this.reverseToggle.addOption(true, "Descending");
		this.reverseToggle.addOption(false, "Ascending");
		this.reverseToggle.setMode('T');

		numericOnlyFields.add(this.form.addField(new FormPortletTitleField("").setHeight(5)));
		this.minValueAutoToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Min Value Auto:"));
		this.minValueAutoToggle.addOption(true, "Auto");
		this.minValueAutoToggle.addOption(false, "User select");
		numericOnlyFields.add(this.minValueAutoToggle);
		numericOnlyFields.add(this.minValueField = this.form.addField(new FormPortletTextField("Min Value:")));
		numericOnlyFields.add(this.form.addField(new FormPortletTitleField("").setHeight(5)));
		this.maxValueAutoToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Max Value Auto:"));
		this.maxValueAutoToggle.addOption(true, "Auto");
		this.maxValueAutoToggle.addOption(false, "User select");
		numericOnlyFields.add(this.maxValueAutoToggle);
		numericOnlyFields.add(this.maxValueField = form.addField(new FormPortletTextField("Max Value:")));
		numericOnlyFields.add(this.form.addField(new FormPortletTitleField("Ticks")));
		this.majorUnitAutoToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Major-Ticks Unit:"));
		this.majorUnitAutoToggle.addOption(true, "Auto");
		this.majorUnitAutoToggle.addOption(false, "User select");
		numericOnlyFields.add(this.majorUnitAutoToggle);
		numericOnlyFields.add(this.majorUnitField = form.addField(new FormPortletTextField("Major-Ticks Unit:")));
		numericOnlyFields.add(this.form.addField(new FormPortletTitleField("").setHeight(5)));
		this.minorUnitAutoToggle = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Auto Minor-Ticks Unit:"));
		this.minorUnitAutoToggle.addOption(true, "Auto");
		this.minorUnitAutoToggle.addOption(false, "User select");
		numericOnlyFields.add(this.minorUnitAutoToggle);
		numericOnlyFields.add(this.minorUnitField = form.addField(new FormPortletTextField("Minor-Ticks Unit:")));
		numericOnlyFields.add(this.form.addField(new FormPortletTitleField("").setHeight(5)));
		this.majorSizeField = form.addField(new FormPortletNumericRangeField("Major Tick Size (px):", 0, 20, 0));
		this.minorSizeField = form.addField(new FormPortletNumericRangeField("Minor Tick Size (px):", 0, 20, 0));
		this.lineColorField = form.addField(new FormPortletColorField("Primary Line Color:"));
		this.otherLineColorField = form.addField(new FormPortletColorField("Other Line Color:"));

		this.form.addField(new FormPortletTitleField("Padding"));
		this.startPaddingField = form.addField(new FormPortletNumericRangeField("Start Padding (px):", 0, 200, 0).setWidth(200));
		this.endPaddingField = form.addField(new FormPortletNumericRangeField("End Padding (px):", 0, 200, 0).setWidth(200));
		this.textPaddingField = form.addField(new FormPortletNumericRangeField("Text Padding (px):", 0, 100, 0));
		this.closeButton = this.form.addButton(new FormPortletButton("Close"));

		for (String s : AmiWebUtils.getFonts(this)) {
			this.titleFontField.addOption(s, s);
		}
		if (!this.titleFontField.containsOption(axis.getTitleFont()))
			this.titleFontField.addOption(axis.getTitleFont(), axis.getTitleFont());

		this.typeField.setValue(!axis.getIsSeries());
		this.minValueField.setValue(SH.toString(this.axis.getMinValue()));
		this.maxValueField.setValue(SH.toString(this.axis.getMaxValue()));
		this.reverseToggle.setValue(this.axis.isReverse());
		this.formatField.setValue(this.axis.getNumberFormula(false));
		this.majorUnitField.setValue(SH.toString(this.axis.getMajorUnit()));
		this.minorUnitField.setValue(SH.toString(this.axis.getMinorUnit()));
		this.majorSizeField.setValue(this.axis.getMajorUnitSize());
		this.minorSizeField.setValue(this.axis.getMinorUnitSize());
		this.startPaddingField.setValue(this.axis.getStartPadding());
		this.endPaddingField.setValue(this.axis.getEndPadding());
		this.textPaddingField.setValue(this.axis.getTextPadding());
		this.lineColorField.setValue(this.axis.getLineColor());
		this.otherLineColorField.setValue(this.axis.getOtherLineColor());
		this.titleField.setValue(this.axis.getTitle());
		this.titleColorField.setValue(this.axis.getTitleColor());
		this.titleFontField.setValueNoThrow(axis.getTitleFont());
		this.titlePaddingField.setValue(this.axis.getTitlePadding());
		this.titleSizeField.setValue(this.axis.getTitleSize());

		this.minValueAutoToggle.setValue(this.axis.getAutoMinValue());
		this.maxValueAutoToggle.setValue(this.axis.getAutoMaxValue());
		this.majorUnitAutoToggle.setValue(this.axis.getAutoMajorValue());
		this.minorUnitAutoToggle.setValue(this.axis.getAutoMinorValue());

		this.addChild(this.form, 0, 0);
		this.setSuggestedSize(400, 650);
		this.form.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
		updateDisabledFields();

	}

	public AmiWebSurfaceEditAxisPortlet hideCloseButtons(boolean hide) {
		this.form.clearButtons();
		if (!hide)
			this.form.addButton(this.closeButton);
		return this;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		StringBuilder sb = new StringBuilder();
		updateValues(sb);
		this.close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

		updateDisabledFields();

		updateValues(new StringBuilder());

	}
	private void updateValues(StringBuilder sink) {
		boolean isNumeric = this.typeField.getValue().booleanValue();
		if (isNumeric) {
			this.axis.setAutoMinValue(this.minValueAutoToggle.getValue());
			this.axis.setAutoMaxValue(this.maxValueAutoToggle.getValue());
			this.axis.setAutoMajorValue(this.majorUnitAutoToggle.getValue());
			this.axis.setAutoMinorValue(this.minorUnitAutoToggle.getValue());
			this.axis.setMajorUnit(getSafeMajorUnit());
			this.axis.setMinorUnit(toDouble(this.minorUnitField.getValue(), this.axis.getMinorUnit()));
			this.axis.setMajorUnitSize(this.majorSizeField.getIntValue());
			this.axis.setMinorUnitSize(this.minorSizeField.getIntValue());
		}
		this.axis.setIsSeries(!isNumeric);
		this.axis.setMajorUnitSize(this.majorSizeField.getIntValue());
		this.axis.setTextPadding(this.textPaddingField.getIntValue());
		this.axis.setStartPadding(this.startPaddingField.getIntValue());
		this.axis.setEndPadding(this.endPaddingField.getIntValue());
		this.axis.setReverse(this.reverseToggle.getValue());
		this.axis.setTitle(this.titleField.getValue());
		this.axis.setTitleColor(this.titleColorField.getValue());
		this.axis.setTitleFont(this.titleFontField.getValue());
		this.axis.setTitlePadding(this.titlePaddingField.getIntValue());
		Integer intValue = this.titleSizeField.getIntValue();
		this.axis.setTitleSize(intValue);
		try {
			this.axis.setNumberFormula(this.formatField.getValue());
		} catch (Exception e) {
			sink.append("Error with number format: " + e.getMessage());
			return;
		}
		this.axis.setLineColor(this.lineColorField.getValue());
		this.axis.setOtherLineColor(this.otherLineColorField.getValue());
		if (isNumeric) {
			try {
				if (!this.minValueAutoToggle.getValue())
					this.axis.setMinValue(SH.parseDouble(this.minValueField.getValue()));
			} catch (Exception e) {
			}
			try {
				if (!this.maxValueAutoToggle.getValue())
					this.axis.setMaxValue(SH.parseDouble(this.maxValueField.getValue()));
			} catch (Exception e) {
			}
			try {
				if (!this.majorUnitAutoToggle.getValue())
					this.axis.setMajorUnit(getSafeMajorUnit());
			} catch (Exception e) {
			}
			try {
				if (!this.minorUnitAutoToggle.getValue())
					this.axis.setMinorUnit(toDouble(this.minorUnitField.getValue(), this.axis.getMinorUnit()));
			} catch (Exception e) {
			}
		}

	}

	private double getSafeMajorUnit() {
		// Prevent major unit from being <= 0. This condition causes a Java heap memory error
		double majorUnit = this.axis.getMajorUnit();
		double majUnitFieldVal = toDouble(this.majorUnitField.getValue(), majorUnit);
		if (majUnitFieldVal > 0) {
			return majUnitFieldVal;
		} else {
			this.majorUnitField.setValue(SH.toString(majorUnit));
			getManager().showAlert("Major-Ticks Unit must be greater than or equal to zero. Resetting Major-Ticks Unit to previous value.");
			return majorUnit;
		}
	}
	private void updateDisabledFields() {
		this.minValueField.setDisabled(this.minValueAutoToggle.getValue());
		this.maxValueField.setDisabled(this.maxValueAutoToggle.getValue());
		this.majorUnitField.setDisabled(this.majorUnitAutoToggle.getValue());
		this.minorUnitField.setDisabled(this.minorUnitAutoToggle.getValue());
		boolean isNumeric = this.typeField.getValue().booleanValue();
		if (isNumeric) {
			if (!this.form.getFields().contains(minValueAutoToggle.getId())) {
				for (FormPortletField f : this.numericOnlyFields) {
					this.form.addFieldBefore(this.majorSizeField, f);
				}
				this.majorSizeField.setTitle("Major Tick Size(px)");
				this.form.addFieldAfter(this.majorSizeField, this.minorSizeField);
			}
		} else {
			if (this.form.getFields().contains(minValueAutoToggle.getId())) {
				for (FormPortletField f : this.numericOnlyFields) {
					this.form.removeField(f);
				}
				this.majorSizeField.setTitle("Tick Size(px)");
				this.form.removeField(this.minorSizeField);
			}
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
		if (field == this.formatField) {
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

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
			//			this.onButtonPressed(this.form, this.closeButton);
			return true;
		} else
			return super.onUserKeyEvent(keyEvent);
	}

}
