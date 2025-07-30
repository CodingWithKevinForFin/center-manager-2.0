package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ScrollPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebScrollSettingsPortlet extends AmiWebPanelSettingsPortlet {
	private static final String INNER_MIN_WIDTH = "innerMinWidth";
	private static final String INNER_MAX_WIDTH = "innerMaxWidth";
	private static final String INNER_MIN_HEIGHT = "innerMinHeight";
	private static final String INNER_MAX_HEIGHT = "innerMaxHeight";
	private static final String VERTICAL_ALIGN = "verticalAlign";
	private static final String HORIZONTAL_ALIGN = "horizontalAlign";

	private final AmiWebScrollPortlet portlet;
	private final ScrollPortlet scrollPortlet;
	private Map<String, Object> settings;
	private Map<String, Object> originalSettings;
	private FormPortletNumericRangeField innerMinWidthField;
	private FormPortletNumericRangeField innerMaxWidthField;
	private FormPortletNumericRangeField innerMinHeightField;
	private FormPortletNumericRangeField innerMaxHeightField;
	private FormPortletToggleButtonsField<Byte> verticalAlignField;
	private FormPortletToggleButtonsField<Byte> horizontalAlignField;

	public AmiWebScrollSettingsPortlet(PortletConfig config, AmiWebScrollPortlet portlet) {
		super(config, portlet);
		this.portlet = portlet;
		this.scrollPortlet = (ScrollPortlet) this.portlet.getInnerContainer();
		this.settings = new HashMap<String, Object>();
		this.originalSettings = new HashMap<String, Object>();
		this.updateFieldValues();
	}

	private void updateFieldValues() {
		int innerMinWidth = this.scrollPortlet.getInnerMinWidth();
		int innerMaxWidth = this.scrollPortlet.getInnerMaxWidth();
		int innerMinHeight = this.scrollPortlet.getInnerMinHeight();
		int innerMaxHeight = this.scrollPortlet.getInnerMaxHeight();
		if (innerMinWidth != -1)
			this.innerMinWidthField.setValue(innerMinWidth);
		if (innerMaxWidth != -1)
			this.innerMaxWidthField.setValue(innerMaxWidth);
		if (innerMinHeight != -1)
			this.innerMinHeightField.setValue(innerMinHeight);
		if (innerMaxHeight != -1)
			this.innerMaxHeightField.setValue(innerMaxHeight);
		this.verticalAlignField.setValue(this.scrollPortlet.getVerticalAlign());
		this.horizontalAlignField.setValue(this.scrollPortlet.getHorizontalAlign());

		this.originalSettings.put(INNER_MIN_WIDTH, innerMinWidthField.getValue());
		this.originalSettings.put(INNER_MAX_WIDTH, innerMaxWidthField.getValue());
		this.originalSettings.put(INNER_MIN_HEIGHT, innerMinHeightField.getValue());
		this.originalSettings.put(INNER_MAX_HEIGHT, innerMaxHeightField.getValue());
		this.originalSettings.put(VERTICAL_ALIGN, verticalAlignField.getValue());
		this.originalSettings.put(HORIZONTAL_ALIGN, horizontalAlignField.getValue());
	}

	@Override
	protected void initForms() {
		super.initForms();
		FormPortlet settingsForm = getSettingsForm();
		innerMinWidthField = new FormPortletNumericRangeField("Inner Min Width: ", 0, Integer.MAX_VALUE, 0);
		innerMaxWidthField = new FormPortletNumericRangeField("Inner Max Width: ", 0, Integer.MAX_VALUE, 0);
		innerMinHeightField = new FormPortletNumericRangeField("Inner Min Height: ", 0, Integer.MAX_VALUE, 0);
		innerMaxHeightField = new FormPortletNumericRangeField("Inner Max Height: ", 0, Integer.MAX_VALUE, 0);
		verticalAlignField = new FormPortletToggleButtonsField<Byte>(Byte.class, "Vertical Align:");
		horizontalAlignField = new FormPortletToggleButtonsField<Byte>(Byte.class, "Horizontal Align:");

		innerMinWidthField.setBgColor("#ffffff");
		innerMaxWidthField.setBgColor("#ffffff");
		innerMinHeightField.setBgColor("#ffffff");
		innerMaxHeightField.setBgColor("#ffffff");
		innerMinWidthField.setSliderHidden(true);
		innerMaxWidthField.setSliderHidden(true);
		innerMinHeightField.setSliderHidden(true);
		innerMaxHeightField.setSliderHidden(true);
		innerMinWidthField.setNullable(true);
		innerMaxWidthField.setNullable(true);
		innerMinHeightField.setNullable(true);
		innerMaxHeightField.setNullable(true);

		verticalAlignField.addOption(ScrollPortlet.ALIGN_STRETCH_RELATIVE, "Stretch");
		verticalAlignField.addOption(ScrollPortlet.ALIGN_LEFT_TOP, "Top");
		verticalAlignField.addOption(ScrollPortlet.ALIGN_CENTER_MIDDLE, "Middle");
		verticalAlignField.addOption(ScrollPortlet.ALIGN_RIGHT_BOTTOM, "Bottom");

		horizontalAlignField.addOption(ScrollPortlet.ALIGN_STRETCH_RELATIVE, "Stretch");
		horizontalAlignField.addOption(ScrollPortlet.ALIGN_LEFT_TOP, "Left");
		horizontalAlignField.addOption(ScrollPortlet.ALIGN_CENTER_MIDDLE, "Center");
		horizontalAlignField.addOption(ScrollPortlet.ALIGN_RIGHT_BOTTOM, "Right");

		//Add fields and set ids
		settingsForm.addField(horizontalAlignField.setId(HORIZONTAL_ALIGN));
		settingsForm.addField(innerMinWidthField.setId(INNER_MIN_WIDTH));
		settingsForm.addField(innerMaxWidthField.setId(INNER_MAX_WIDTH));
		settingsForm.addField(verticalAlignField.setId(VERTICAL_ALIGN));
		settingsForm.addField(innerMinHeightField.setId(INNER_MIN_HEIGHT));
		settingsForm.addField(innerMaxHeightField.setId(INNER_MAX_HEIGHT));

		//verticalAlign
		//horizontalAlign

		//not needed
		//clipHorizontal
		//clipVertical
	}
	private boolean validateSetting(String id, Object value) {
		boolean valid = false;
		StringBuilder sink = new StringBuilder();
		if (INNER_MIN_WIDTH.equals(id)) {
			int setting = Caster_Integer.PRIMITIVE.castOr(value, -1);
			int comp = scrollPortlet.getInnerMaxWidth();
			valid = setting == -1 || comp == -1 ? true : setting <= comp;
			if (valid == false)
				sink.append("Invalid setting Inner Min Width, Min Width must be less than or equal to Max Width");
		} else if (INNER_MAX_WIDTH.contentEquals(id)) {
			int setting = Caster_Integer.PRIMITIVE.castOr(value, -1);
			int comp = scrollPortlet.getInnerMinWidth();
			valid = setting == -1 || comp == -1 ? true : setting >= comp;
			if (valid == false)
				sink.append("Invalid setting Inner Max Width, Max Width must be greater than or equal to Min Width");
		} else if (INNER_MIN_HEIGHT.contentEquals(id)) {
			int setting = Caster_Integer.PRIMITIVE.castOr(value, -1);
			int comp = scrollPortlet.getInnerMaxHeight();
			valid = setting == -1 || comp == -1 ? true : setting <= comp;
			if (valid == false)
				sink.append("Invalid setting Inner Min Height, Min Height must be less than or equal to Max Height");
		} else if (INNER_MAX_HEIGHT.contentEquals(id)) {
			int setting = Caster_Integer.PRIMITIVE.castOr(value, -1);
			int comp = scrollPortlet.getInnerMinHeight();
			valid = setting == -1 || comp == -1 ? true : setting >= comp;
			if (valid == false)
				sink.append("Invalid setting Inner Max Height, Max Height must be greater than or equal to Min Height");
		} else
			valid = true;

		if (valid == false)
			getManager().showAlert(sink.toString());

		return valid;
	}

	private void applySetting(String id, Object value) {
		if (INNER_MIN_WIDTH.equals(id)) {
			scrollPortlet.setInnerMinWidth(Caster_Integer.PRIMITIVE.castOr(value, -1));
		} else if (INNER_MAX_WIDTH.contentEquals(id)) {
			scrollPortlet.setInnerMaxWidth(Caster_Integer.PRIMITIVE.castOr(value, -1));
		} else if (INNER_MIN_HEIGHT.contentEquals(id)) {
			scrollPortlet.setInnerMinHeight(Caster_Integer.PRIMITIVE.castOr(value, -1));
		} else if (INNER_MAX_HEIGHT.contentEquals(id)) {
			scrollPortlet.setInnerMaxHeight(Caster_Integer.PRIMITIVE.castOr(value, -1));
		} else if (VERTICAL_ALIGN.contentEquals(id)) {
			scrollPortlet.setVerticalAlign(Caster_Byte.PRIMITIVE.cast(value));
		} else if (HORIZONTAL_ALIGN.contentEquals(id)) {
			scrollPortlet.setHorizontalAlign(Caster_Byte.PRIMITIVE.cast(value));
		}
	}

	@Override
	protected boolean verifyChanges() {
		for (String id : settings.keySet()) {
			if (validateSetting(id, settings.get(id)) == false)
				return false;
		}
		return super.verifyChanges();
	}
	@Override
	protected void submitChanges() {
		for (String id : settings.keySet()) {
			applySetting(id, settings.get(id));
		}
		super.submitChanges();
	}

	@Override
	protected void onCancelled() {
		for (String id : originalSettings.keySet()) {
			applySetting(id, originalSettings.get(id));
		}
		super.onCancelled();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		this.settings.put(field.getId(), field.getValue());
		this.applySetting(field.getId(), field.getValue());
		super.onFieldValueChanged(portlet, field, attributes);
	}
}
