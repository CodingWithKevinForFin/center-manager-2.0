package com.f1.ami.web;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField.Option;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTimeZoneField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;

public class AmiWebUserSettingsPortlet extends FormPortlet implements FormPortletListener, ConfirmDialogListener, AmiWebLockedPermissiblePortlet {

	private static final String OPTION_LABEL_DEFAULT = "<Default>";

	private final FormPortletTimeZoneField timeZoneField;
	private final FormPortletSelectField<String> languageField;
	private final FormPortletSelectField<String> dateFormatField;
	private final FormPortletSelectField<AmiWebTimeFormats> timeFormatField;
	private final FormPortletSelectField<String> numberSeparatorField;
	private final FormPortletNumericRangeField numberDecimalPrecisionField;
	private final FormPortletSelectField<String> numberNegativeFormatField;
	private final FormPortletNumericRangeField sciNotLeftField;
	private final FormPortletNumericRangeField sciNotRightField;
	private final FormPortletToggleButtonsField<String> autoApplyUserPrefs;
	private final FormPortletToggleButtonsField<String> spreadSheetFormattingField;
	private final FormPortletButton okButton = new FormPortletButton("OK");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");
	private final AmiWebService service;
	private final AmiWebVarsManager varsManager;
	private final PortletManager manager;
	private static final Comparator<Option<String>> NUMBER_FORMAT_SORTER = new Comparator<Option<String>>() {

		@Override
		public int compare(Option<String> o1, Option<String> o2) {
			String s1 = o1.getKey();
			String s2 = o2.getKey();
			boolean default1 = OPTION_LABEL_DEFAULT.equals(s1);
			boolean default2 = OPTION_LABEL_DEFAULT.equals(s2);
			if (default1 && default2) {
				return 0;
			} else if (default1) {
				return 1;
			} else if (default2) {
				return -1;
			} else {
				return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(s1, s2);
			}
		}

	};
	private boolean valuesChanged = false;

	private FormPortletTextField userNameField;

	private PortletStyleManager_Form formStyle;

	private FormPortletTitleField numberFormatTitleField;

	private FormPortletTitleField dateTimeTitleField;

	private FormPortletTitleField sciNotTitleField;

	private FormPortletTitleField loginTitleField;

	public AmiWebUserSettingsPortlet(PortletConfig config) {
		super(config);
		this.manager = getManager();
		this.service = AmiWebUtils.getService(this.manager);
		this.formStyle = getStyleManager();
		this.varsManager = this.service.getVarsManager();
		this.getFormPortletStyle().setCssStyle("_bg=#e2e2e2");
		userNameField = addField(new FormPortletTextField("Username: ").setValue(this.varsManager.getUsername()).setDisabled(true));
		userNameField.setBorderWidth(0);
		this.timeZoneField = addField(new FormPortletTimeZoneField());
		this.languageField = addField(new FormPortletSelectField<String>(String.class, "Language: "));
		this.languageField.addOption(null, OPTION_LABEL_DEFAULT);
		for (String l : EH.getLocaleLanguagesDisplay()) {
			this.languageField.addOption(l.toUpperCase(), l);
		}
		this.languageField.sortOptionsByName();
		this.languageField.setVisible(false); // TODO: Make visible once language setting functionality is finished.

		dateTimeTitleField = addField(new FormPortletTitleField("Date/Time Formatting"));

		this.dateFormatField = addField(new FormPortletSelectField<String>(String.class, "Date: "));
		this.dateFormatField.addOption(null, OPTION_LABEL_DEFAULT);
		List<String> dateOptions = CH.l(AmiUtils.dateOptionToFormatMap.keySet());
		Collections.sort(dateOptions);
		for (int i = 0; i < dateOptions.size(); i++) {
			String name = dateOptions.get(i);
			this.dateFormatField.addOption(AmiUtils.dateOptionToFormatMap.get(name), name);
		}

		this.timeFormatField = addField(new FormPortletSelectField<AmiWebTimeFormats>(AmiWebTimeFormats.class, "Time: "));
		this.timeFormatField.addOption(null, OPTION_LABEL_DEFAULT);
		for (AmiWebTimeFormats i : AmiWebTimeFormats.options())
			this.timeFormatField.addOption(i, i.example);

		numberFormatTitleField = addField(new FormPortletTitleField("Number Formatting"));
		this.numberSeparatorField = addField(new FormPortletSelectField<String>(String.class, "Numeric Separators: "));
		this.numberSeparatorField.addOption(null, OPTION_LABEL_DEFAULT);
		for (String k : AmiWebFormatterManager.NUMBER_FORMATS_2_LOCALES.keySet()) {
			this.numberSeparatorField.addOption(k, k);
		}
		this.numberSeparatorField.sortOptions(NUMBER_FORMAT_SORTER);

		this.numberDecimalPrecisionField = addField(new FormPortletNumericRangeField("Decimal Precision: ", 0, 10, 0)).setNullable(true);

		this.numberNegativeFormatField = addField(new FormPortletSelectField<String>(String.class, "Negative Number: "));
		this.numberNegativeFormatField.addOption(null, OPTION_LABEL_DEFAULT);
		this.numberNegativeFormatField.addOption("sign", "-237");
		this.numberNegativeFormatField.addOption("parentheses", "(237)");

		sciNotTitleField = addField(new FormPortletTitleField("Scientific Notation"));
		this.sciNotLeftField = addField(new FormPortletNumericRangeField("Digits left of decimal:", 1, 20, 0));
		this.sciNotRightField = addField(new FormPortletNumericRangeField("Digits Right of decimal:", 1, 20, 0));
		AmiWebTimeFormats timeFormat = this.varsManager.getTimeFormats();
		this.timeZoneField.setValueNoThrow(this.varsManager.getTimeZoneId());
		this.languageField.setValueNoThrow(this.varsManager.getLanguage());
		this.dateFormatField.setValueNoThrow(this.varsManager.getDateFormat());
		this.timeFormatField.setValueNoThrow(timeFormat);
		this.numberSeparatorField.setValueNoThrow(this.varsManager.getNumberSeparator());
		this.numberDecimalPrecisionField.setValue(this.varsManager.getNumberDecimalPrecision());
		this.numberNegativeFormatField.setValueNoThrow(this.varsManager.getNumberNegativeFormat());
		this.sciNotLeftField.setValue(this.varsManager.getSciNotNumDigitsLeft());
		this.sciNotRightField.setValue(this.varsManager.getSciNotNumDigitsRight());

		//Excel formatting settings
		this.spreadSheetFormattingField = addField(new FormPortletToggleButtonsField<String>(String.class, "Spread Sheet Formatting: "));
		this.spreadSheetFormattingField.addOption(AmiWebConsts.ALWAYS, "Apply");
		this.spreadSheetFormattingField.addOption(AmiWebConsts.NEVER, "Do Not Apply");
		this.spreadSheetFormattingField.setValue(this.varsManager.getSpreadSheetFormatOption());

		loginTitleField = addField(new FormPortletTitleField("Login Settings"));
		this.autoApplyUserPrefs = addField(new FormPortletToggleButtonsField<String>(String.class, "Apply User Preferences: "));
		this.autoApplyUserPrefs.addOption(AmiWebConsts.ALWAYS, "Always");
		this.autoApplyUserPrefs.addOption(AmiWebConsts.NEVER, "Never");
		this.autoApplyUserPrefs.addOption(AmiWebConsts.ASK, "Ask");
		this.autoApplyUserPrefs.setValue(this.varsManager.getAutoApplyUserPrefs());
		this.timeZoneField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_TIME_ZONE));
		this.languageField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_LANGUAGE));
		this.dateFormatField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_DATE_FORMAT));
		this.timeFormatField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_TIME_FORMAT));
		this.numberDecimalPrecisionField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_NUMBER_DECIMAL_PRECISION));
		this.numberSeparatorField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_NUMBER_SEPARATOR));
		this.numberNegativeFormatField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_NUMBER_NEGATIVE_FORMAT));
		this.sciNotLeftField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_LEFT));
		this.sciNotRightField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_RIGHT));
		this.autoApplyUserPrefs.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_AUTOAPPLY_USERPREFS));
		this.spreadSheetFormattingField.setDisabled(this.varsManager.isReadonlySetting(AmiWebConsts.USER_SETTING_SPREAD_SHEET_FORMAT_OPTION));
		addButton(this.okButton);
		addButton(this.cancelButton);
		addFormPortletListener(this);
		prepareStyle();
	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 400;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 510;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.okButton && this.valuesChanged) {
			String language = this.languageField.getValue();
			String timeZone = this.timeZoneField.getValue();
			String dateFormat = this.dateFormatField.getValue();
			AmiWebTimeFormats timeFormat = this.timeFormatField.getValue();
			String numberSeparator = this.numberSeparatorField.getValue();
			Integer numberDecimalPrecision = Caster_Integer.INSTANCE.cast(this.numberDecimalPrecisionField.getValue());
			String numberNegativeFormat = this.numberNegativeFormatField.getValue();
			language = language == null ? AmiWebVarsManager.DEFAULT_LANGUAGE : language;
			timeZone = timeZone == null ? AmiWebVarsManager.DEFAULT_TIME_ZONE : timeZone;
			dateFormat = dateFormat == null ? AmiWebVarsManager.DEFAULT_DATE_FORMAT : dateFormat;
			if (timeFormat == null)
				timeFormat = AmiWebTimeFormats.DEFAULT;
			Integer sciNotLeft = Caster_Integer.INSTANCE.cast(this.sciNotLeftField.getValue());
			Integer sciNotRight = Caster_Integer.INSTANCE.cast(this.sciNotRightField.getValue());
			numberSeparator = numberSeparator == null ? AmiWebVarsManager.DEFAULT_NUMBER_SEPARATOR : numberSeparator;
			numberDecimalPrecision = numberDecimalPrecision == null ? AmiWebVarsManager.DEFAULT_NUMBER_DECIMAL_PRECISION : numberDecimalPrecision;
			numberNegativeFormat = numberNegativeFormat == null ? AmiWebVarsManager.DEFAULT_NUMBER_NEGATIVE_FORMAT : numberNegativeFormat;
			sciNotLeft = sciNotLeft == null ? AmiWebVarsManager.DEFAULT_SCI_NOT_NUM_DIGITS_LEFT : sciNotLeft;
			sciNotRight = sciNotRight == null ? AmiWebVarsManager.DEFAULT_SCI_NOT_NUM_DIGITS_RIGHT : sciNotRight;
			String ssFormatOption = this.spreadSheetFormattingField.getValue();
			String autoapplyUserPrefs = this.autoApplyUserPrefs.getValue();
			//			this.varsManager.setLanguage(language);
			//			this.varsManager.setTimeZoneId(timeZone);
			//			this.varsManager.setDateFormat(dateFormat);
			//			this.varsManager.setTimeFormats(timeFormat);
			//			this.varsManager.setNumberSeparator(numberSeparator);
			//			this.varsManager.setNumberDecimalPrecision(numberDecimalPrecision);
			//			this.varsManager.setNumberNegativeFormat(numberNegativeFormat);
			//			this.varsManager.setSciNotNumDigitsLeft(sciNotLeft);
			//			this.varsManager.setSciNotNumDigitsRight(sciNotRight);
			//			this.varsManager.setSpreadSheetFormatOption(ssFormatOption);
			//			this.varsManager.setAutoApplyuserPrefs(autoapplyUserPrefs);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_LANGUAGE, language);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_TIME_ZONE, timeZone);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_DATE_FORMAT, dateFormat);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_TIME_FORMAT, timeFormat.timeFormat);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_NUMBER_SEPARATOR, numberSeparator);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_NUMBER_DECIMAL_PRECISION, OH.toString(numberDecimalPrecision));
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_NUMBER_NEGATIVE_FORMAT, numberNegativeFormat);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_LEFT, OH.toString(sciNotLeft));
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_RIGHT, OH.toString(sciNotRight));
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_SPREAD_SHEET_FORMAT_OPTION, ssFormatOption);
			this.varsManager.putSetting(AmiWebConsts.USER_SETTING_AUTOAPPLY_USERPREFS, OH.toString(autoapplyUserPrefs));
			//			this.store.saveSettings();

			// Let user know they need to reset
			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
					"In order for changes to user settings to take effect, the current layout must be reset. Would you like to reset now, or manually log out and log in later?",
					ConfirmDialog.TYPE_YES_NO).setCallback("reset_message");
			dialog.updateButton(ConfirmDialog.ID_YES, "Reset Now");
			dialog.updateButton(ConfirmDialog.ID_NO, "Reset Later");
			dialog.addDialogListener(this);
			this.manager.showDialog("Reset Required", dialog);
		}
		close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		this.valuesChanged = true;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("reset_message".equals(source.getCallback()))
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				service.processUserSettings();
				service.getLayoutFilesManager().rebuildLayout();
			}
		return true;
	}

	private void prepareStyle() {
		if (formStyle.isUseDefaultStyling())
			return;
		this.getFormPortletStyle().setCssStyle(service.getUserFormStyleManager().getFormStyle());
		this.getFormPortletStyle().setButtonPanelStyle(service.getUserFormStyleManager().getFormButtonPanelStyle());

		this.userNameField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.userNameField.setBgColor(formStyle.getDefaultFormBgColor());
		this.userNameField.setBorderColor(formStyle.getFormBorderColor());
		this.userNameField.setFontColor(formStyle.getDefaultFormFontColor());
		this.userNameField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		dateTimeTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());

		this.numberFormatTitleField.setCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		sciNotTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		loginTitleField.setStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.timeZoneField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.timeZoneField.setBgColor(formStyle.getDefaultFormBgColor());
		this.timeZoneField.setBorderColor(formStyle.getFormBorderColor());
		this.timeZoneField.setFontColor(formStyle.getDefaultFormFontColor());
		this.timeZoneField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.dateFormatField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.dateFormatField.setBgColor(formStyle.getDefaultFormBgColor());
		this.dateFormatField.setBorderColor(formStyle.getFormBorderColor());
		this.dateFormatField.setFontColor(formStyle.getDefaultFormFontColor());
		this.dateFormatField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.timeFormatField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.timeFormatField.setBgColor(formStyle.getDefaultFormBgColor());
		this.timeFormatField.setBorderColor(formStyle.getFormBorderColor());
		this.timeFormatField.setFontColor(formStyle.getDefaultFormFontColor());
		this.timeFormatField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.numberSeparatorField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.numberSeparatorField.setBgColor(formStyle.getDefaultFormBgColor());
		this.numberSeparatorField.setBorderColor(formStyle.getFormBorderColor());
		this.numberSeparatorField.setFontColor(formStyle.getDefaultFormFontColor());
		this.numberSeparatorField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.numberNegativeFormatField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.numberNegativeFormatField.setBgColor(formStyle.getDefaultFormBgColor());
		this.numberNegativeFormatField.setBorderColor(formStyle.getFormBorderColor());
		this.numberNegativeFormatField.setFontColor(formStyle.getDefaultFormFontColor());
		this.numberNegativeFormatField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.numberDecimalPrecisionField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		//		this.numberDecimalPrecisionField.setPrimaryColor(formStyle.getDefaultFormFontColor());
		//		this.numberDecimalPrecisionField.setSecondaryColor(formStyle.getDefaultFormBgColor());
		this.numberDecimalPrecisionField.setBgColor(formStyle.getDefaultFormBgColor());
		this.numberDecimalPrecisionField.setBorderColor(formStyle.getFormBorderColor());
		this.numberDecimalPrecisionField.setFontColor(formStyle.getDefaultFormFontColor());
		this.numberDecimalPrecisionField.setFieldFontFamily(formStyle.getDefaultFormFontFam());

		this.sciNotTitleField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.sciNotTitleField.setBgColor(formStyle.getDefaultFormBgColor());
		this.sciNotTitleField.setBorderColor(formStyle.getFormBorderColor());
		this.sciNotTitleField.setFontColor(formStyle.getDefaultFormFontColor());
		this.sciNotTitleField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.sciNotLeftField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		//		this.sciNotLeftField.setPrimaryColor(formStyle.getDefaultFormFontColor());
		//		this.sciNotLeftField.setSecondaryColor(formStyle.getDefaultFormBgColor());
		this.sciNotLeftField.setBgColor(formStyle.getDefaultFormBgColor());
		this.sciNotLeftField.setBorderColor(formStyle.getFormBorderColor());
		this.sciNotLeftField.setFontColor(formStyle.getDefaultFormFontColor());
		this.sciNotLeftField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.sciNotRightField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		//		this.sciNotRightField.setPrimaryColor(formStyle.getDefaultFormFontColor());
		//		this.sciNotRightField.setSecondaryColor(formStyle.getDefaultFormBgColor());
		this.sciNotRightField.setBgColor(formStyle.getDefaultFormBgColor());
		this.sciNotRightField.setBorderColor(formStyle.getFormBorderColor());
		this.sciNotRightField.setFontColor(formStyle.getDefaultFormFontColor());
		this.sciNotRightField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.spreadSheetFormattingField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.loginTitleField.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
		this.loginTitleField.setBgColor(formStyle.getDefaultFormBgColor());
		this.loginTitleField.setBorderColor(formStyle.getFormBorderColor());
		this.loginTitleField.setFontColor(formStyle.getDefaultFormFontColor());
		this.loginTitleField.setFieldFontFamily(formStyle.getDefaultFormFontFam());
		this.autoApplyUserPrefs.setLabelCssStyle("_fg=" + formStyle.getDefaultFormTitleColor() + "|_fm=bold," + formStyle.getDefaultFormFontFam());
	}
}
