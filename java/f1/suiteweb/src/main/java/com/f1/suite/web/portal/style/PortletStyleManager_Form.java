package com.f1.suite.web.portal.style;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.OH;

public class PortletStyleManager_Form implements Cloneable {
	private String defaultFormButtonPanelStyle = "_bg=#007608"; // green background
	private String formButtonPanelStyle = defaultFormButtonPanelStyle;
	private String defaultFormButtonsStyle = "_bg=#e2e2e2|style.border=1px|style.minWidth=95px|_fn=arial|_fs=17"; // grey button
	private String formButtonsStyle = defaultFormButtonsStyle;
	private String formButtonsFontFam = "arial";
	private int defaultFormButtonsSpacing = 8;
	private int defaultFormButtonsPaddingBottom = 4;
	private int defaultFormButtonsPaddingTop = 10;
	private int defaultFormButtonsHeight = 28;
	//	private int defaultFormButtonsHeight = 21;
	private String defaultFormStyle = "_bg=#e2e2e2";
	private String formStyle = defaultFormStyle;
	private int defaultFormLabelsWidth = 164;
	private String defaultFormLabelsStyle = "_fs=13|_fm=bold,arial|style.padding=0px 6px 0px 0px";
	private Map<String, Field> defaultFormFieldStyles = new HashMap<String, Field>();
	private int defaultFormFieldHeight = 25;
	private int defaultFormFieldWidth = 220;
	private int defaultFormFieldSpacing = 6;
	private int defaultFormWidthStretchPadding = 6;
	private String defaultFormScrollGripColor = "#fafafa";
	private String defaultFormScrollTrackColor = "#d6d4d2";
	private String defaultFormScrollButtonColor = "#fafafa";
	private String defaultFormScrollIconsColor = "#000000";
	private int defaultFormScrollBarWidth = 15;
	private String defaultFormTitleColor = "#000000";
	private String defaultFormFontColor = "#000000";
	private String defaultFormBgColor = "#ffffff";
	private String defaultFormButtonBackgroundColor = "#e2e2e2";
	private String defaultFormIconColor = "#657080";
	private String defaultFormButtonFontColor = "#000000";
	private String defaultFormButtonTopColor = "#aaaaaa";
	private String defaultFormButtonBottomColor = "#aaaaaa";
	private String formBorderColor = "#aaaaaa";
	private String defaultFormFontFam = "arial";
	private String defaultAlertFormButtonPanelStyle = "_bg=#007608";
	private boolean useDefaultStyling = false;
	private String formButtonFontColor = defaultFormButtonFontColor;
	private String formButtonBackgroundColor = defaultFormButtonBackgroundColor;
	private String timeDisplayFormat;
	private String dateDisplayFormat;

	private static final class Field implements Cloneable {
		private String style;
		private Integer width;
		private Integer height;
		public Map<String, String> options = new HashMap<String, String>();;

		@Override
		public Field clone() {
			try {
				Field r = (Field) super.clone();
				r.options.putAll(options);
				return r;
			} catch (CloneNotSupportedException e) {
				throw OH.toRuntime(e);
			}
		}
	}

	public PortletStyleManager_Form() {
		putDefaultFormFieldHeight(FormPortletTitleField.JSNAME, 27);
		putDefaultFormFieldWidth(FormPortletTitleField.JSNAME, FormPortletField.WIDTH_STRETCH);
		putDefaultFormFieldWidth(FormPortletTextAreaField.JSNAME, FormPortletField.WIDTH_STRETCH);
		putDefaultFormFieldWidth(FormPortletToggleButtonsField.JSNAME, FormPortletField.WIDTH_STRETCH);
		putDefaultFormFieldWidth(FormPortletTextField.JSNAME, 200);

	}

	public PortletStyleManager_Form clone() {
		try {
			PortletStyleManager_Form r = (PortletStyleManager_Form) super.clone();
			r.defaultFormFieldStyles = new HashMap<String, Field>(r.defaultFormFieldStyles);
			for (Entry<String, Field> e : r.defaultFormFieldStyles.entrySet())
				e.setValue(e.getValue().clone());
			return r;
		} catch (CloneNotSupportedException e) {
			throw OH.toRuntime(e);
		}
	}

	public String getDefaultFormButtonPanelStyle() {
		return this.defaultFormButtonPanelStyle;
	}

	public String getDefaultFormButtonsStyle() {
		return this.defaultFormButtonsStyle;
	}

	public void setDefaultFormButtonPanelStyle(String defaultFormButtonPanelStyle) {
		this.defaultFormButtonPanelStyle = defaultFormButtonPanelStyle;
	}

	public void setDefaultFormButtonsStyle(String defaultFormButtonsStyle) {
		this.defaultFormButtonsStyle = defaultFormButtonsStyle;
	}

	public int getDefaultFormButtonsHeight() {
		return this.defaultFormButtonsHeight;
	}

	public int getDefaultFormButtonsPaddingTop() {
		return this.defaultFormButtonsPaddingTop;
	}

	public int getDefaultFormButtonsPaddingBottom() {
		return this.defaultFormButtonsPaddingBottom;
	}

	public int getDefaultFormButtonsSpacing() {
		return this.defaultFormButtonsSpacing;
	}

	public void setDefaultFormButtonsSpacing(int defaultFormButtonsSpacing) {
		this.defaultFormButtonsSpacing = defaultFormButtonsSpacing;
	}

	public void setDefaultFormButtonsPaddingBottom(int defaultFormButtonsPaddingBottom) {
		this.defaultFormButtonsPaddingBottom = defaultFormButtonsPaddingBottom;
	}

	public void setDefaultFormButtonsPaddingTop(int defaultFormButtonsPaddingTop) {
		this.defaultFormButtonsPaddingTop = defaultFormButtonsPaddingTop;
	}

	public void setDefaultFormButtonsHeight(int defaultFormButtonsHeight) {
		this.defaultFormButtonsHeight = defaultFormButtonsHeight;
	}

	public void setDefaultFormStyle(String defaultFormStyle) {
		this.defaultFormStyle = defaultFormStyle;
	}
	public String getDefaultFormStyle() {
		return this.defaultFormStyle;
	}

	public int getDefaultFormLabelsWidth() {
		return defaultFormLabelsWidth;
	}
	public String getDefaultFormLabelsStyle() {
		return defaultFormLabelsStyle;
	}

	public void setDefaultFormLabelsWidth(int defaultFormLabelsWidth) {
		this.defaultFormLabelsWidth = defaultFormLabelsWidth;
	}

	public void setDefaultFormLabelsStyle(String defaultFormLabelsStyle) {
		this.defaultFormLabelsStyle = defaultFormLabelsStyle;
	}

	public String getDefaultFormFieldStyle(String jsName) {
		return getOrCreateFormField(jsName).style;
	}
	public void putDefaultFormFieldStyle(String jsName, String style) {
		getOrCreateFormField(jsName).style = style;
	}
	public void removeDefaultFormFieldStyle(String jsName) {
		getOrCreateFormField(jsName).style = null;
	}

	public int getDefaultFormFieldWidth(String jsName) {
		Integer r = getOrCreateFormField(jsName).width;
		return r == null ? defaultFormFieldWidth : r.intValue();
	}
	public void putDefaultFormFieldWidth(String jsName, int width) {
		getOrCreateFormField(jsName).width = width;
	}
	public void removeDefaultFormFieldWidth(String jsName) {
		getOrCreateFormField(jsName).width = null;
	}

	public int getDefaultFormFieldHeight(String jsName) {
		Integer r = getOrCreateFormField(jsName).height;
		return r == null ? defaultFormFieldHeight : r.intValue();
	}
	public void putDefaultFormFieldHeight(String jsName, int height) {
		getOrCreateFormField(jsName).height = height;
	}
	public void removeDefaultFormFieldHeight(String jsName) {
		getOrCreateFormField(jsName).height = null;
	}
	public Map<String, String> getDefaultFormFieldOptions(String jsName) {
		return getOrCreateFormField(jsName).options;
	}
	public void putDefaultFormFieldOption(String jsName, String option, String value) {
		getOrCreateFormField(jsName).options.put(option, value);
	}
	public String getDefaultFormFieldOption(String jsName, String option) {
		return getOrCreateFormField(jsName).options.get(option);
	}
	public void removeDefaultFormFieldOption(String jsName, String option) {
		getOrCreateFormField(jsName).options.remove(option);
	}

	public int getDefaultFormFieldHeight() {
		return defaultFormFieldHeight;
	}

	public void setDefaultFormFieldHeight(int defaultFormFieldHeight) {
		this.defaultFormFieldHeight = defaultFormFieldHeight;
	}

	public int getDefaultFormFieldWidth() {
		return defaultFormFieldWidth;
	}

	public void setDefaultFormFieldWidth(int defaultFormFieldWidth) {
		this.defaultFormFieldWidth = defaultFormFieldWidth;
	}

	public int getDefaultFormFieldSpacing() {
		return this.defaultFormFieldSpacing;
	}

	public void setDefaultFormFieldSpacing(int defaultFormFieldSpacing) {
		this.defaultFormFieldSpacing = defaultFormFieldSpacing;
	}

	public int getDefaultFormWidthStretchPadding() {
		return defaultFormWidthStretchPadding;
	}

	public void setDefaultFormWidthStretchPadding(int defaultFormWidthStretchPadding) {
		this.defaultFormWidthStretchPadding = defaultFormWidthStretchPadding;
	}

	private Field getOrCreateFormField(String jsName) {
		Field r = this.defaultFormFieldStyles.get(jsName);
		if (r == null)
			this.defaultFormFieldStyles.put(jsName, r = new Field());
		return r;
	}

	public String getDefaultFormScrollGripColor() {
		return defaultFormScrollGripColor;
	}

	public void setDefaultFormScrollGripColor(String defaultFormScrollGripColor) {
		this.defaultFormScrollGripColor = defaultFormScrollGripColor;
	}

	public String getDefaultFormScrollTrackColor() {
		return defaultFormScrollTrackColor;
	}

	public void setDefaultFormScrollTrackColor(String defaultFormScrollTrackColor) {
		this.defaultFormScrollTrackColor = defaultFormScrollTrackColor;
	}

	public String getDefaultFormScrollButtonColor() {
		return defaultFormScrollButtonColor;
	}

	public void setDefaultFormScrollButtonColor(String defaultFormScrollButtonColor) {
		this.defaultFormScrollButtonColor = defaultFormScrollButtonColor;
	}

	public String getDefaultFormScrollIconsColor() {
		return defaultFormScrollIconsColor;
	}

	public void setDefaultFormScrollIconsColor(String defaultFormScrollIconsColor) {
		this.defaultFormScrollIconsColor = defaultFormScrollIconsColor;
	}

	public int getDefaultFormScrollBarWidth() {
		return defaultFormScrollBarWidth;
	}

	public void setDefaultFormScrollBarWidth(int defaultFormScrollBarWidth) {
		this.defaultFormScrollBarWidth = defaultFormScrollBarWidth;
	}

	public String getDefaultFormTitleColor() {
		return defaultFormTitleColor;
	}

	public void setDefaultFormTitleColor(String defaultFormTitleColor) {
		this.defaultFormTitleColor = defaultFormTitleColor;
	}

	public String getDefaultFormFontColor() {
		return defaultFormFontColor;
	}

	public void setDefaultFormFontColor(String defaultFormFontColor) {
		this.defaultFormFontColor = defaultFormFontColor;
	}

	public String getDefaultFormBgColor() {
		return defaultFormBgColor;
	}

	public void setDefaultFormBgColor(String defaultFormBgColor) {
		this.defaultFormBgColor = defaultFormBgColor;
	}

	public String getDefaultFormButtonBackgroundColor() {
		return defaultFormButtonBackgroundColor;
	}

	public void setDefaultFormButtonBackgroundColor(String defaultFormButtonBackgroundColor) {
		this.defaultFormButtonBackgroundColor = defaultFormButtonBackgroundColor;
		updateButtonCss();
	}

	public String getDefaultFormButtonFontColor() {
		return defaultFormButtonFontColor;
	}

	public void setDefaultFormButtonFontColor(String defaultFormButtonFontColor) {
		this.defaultFormButtonFontColor = defaultFormButtonFontColor;
	}

	public String getDefaultFormButtonTopColor() {
		return defaultFormButtonTopColor;
	}

	public void setDefaultFormButtonTopColor(String defaultFormButtonTopColor) {
		this.defaultFormButtonTopColor = defaultFormButtonTopColor;
	}

	public String getDefaultFormButtonBottomColor() {
		return defaultFormButtonBottomColor;
	}

	public void setDefaultFormButtonBottomColor(String defaultFormButtonBottomColor) {
		this.defaultFormButtonBottomColor = defaultFormButtonBottomColor;
	}

	public String getDefaultFormFontFam() {
		return defaultFormFontFam;
	}

	public void setDefaultFormFontFam(String defaultFormFontFam) {
		this.defaultFormFontFam = defaultFormFontFam;
	}

	public String getDefaultFormIconColor() {
		return defaultFormIconColor;
	}

	public void setDefaultFormIconColor(String defaultFormIconColor) {
		this.defaultFormIconColor = defaultFormIconColor;
	}

	public String getFormBorderColor() {
		return formBorderColor;
	}

	public void setFormBorderColor(String formBorderColor) {
		this.formBorderColor = formBorderColor;
	}

	public boolean isUseDefaultStyling() {
		return useDefaultStyling;
	}

	public void setUseDefaultStyling(boolean useDefaultStyling) {
		this.useDefaultStyling = useDefaultStyling;
	}

	public String getFormButtonPanelStyle() {
		return formButtonPanelStyle;
	}

	public void setFormButtonPanelStyle(String formButtonPanelStyle) {
		this.formButtonPanelStyle = formButtonPanelStyle;
	}

	public String getFormButtonsStyle() {
		return formButtonsStyle;
	}

	public void setFormButtonsStyle(String formButtonsStyle) {
		this.formButtonsStyle = formButtonsStyle;
	}

	public String getFormStyle() {
		return formStyle;
	}

	public void setFormStyle(String formStyle) {
		this.formStyle = formStyle;
	}

	public String getFormButtonsFontFam() {
		return formButtonsFontFam;
	}

	public void setFormButtonsFontFam(String formButtonsFontFam) {
		this.formButtonsFontFam = formButtonsFontFam;
		updateButtonCss();
	}

	public String buildButtonCss() {
		final String defaultStyle = "style.border=1px|style.minWidth=95px|_fs=17";
		String fontColorStyle = "|_fg=" + getFormButtonFontColor();
		String bgColorStyle = "|_bg=" + getFormButtonBackgroundColor();
		String additionalStyles = "|_fm=" + getFormButtonsFontFam();
		String res = defaultStyle + fontColorStyle + bgColorStyle + additionalStyles;
		return res;
	}

	public void updateButtonCss() {
		setFormButtonsStyle(buildButtonCss());
	}

	public void setFormButtonFontColor(String nuw) {
		// TODO Auto-generated method stub
		this.formButtonFontColor = nuw;
		updateButtonCss();
	}

	public String getFormButtonFontColor() {
		return this.formButtonFontColor;
	}

	public String getFormButtonBackgroundColor() {
		return formButtonBackgroundColor;
	}

	public void setFormButtonBackgroundColor(String formButtonBackgroundColor) {
		this.formButtonBackgroundColor = formButtonBackgroundColor;
		updateButtonCss();
	}

	public String getDefaultAlertFormButtonPanelBackgroundColor() {
		return defaultAlertFormButtonPanelStyle;
	}

	public String getTimeDisplayFormat() {
		return timeDisplayFormat;
	}

	public void setTimeDisplayFormat(String timeDisplayFormat) {
		this.timeDisplayFormat = timeDisplayFormat;
	}

	public String getDateDisplayFormat() {
		return dateDisplayFormat;
	}

	public void setDateDisplayFormat(String dateDisplayFormat) {
		this.dateDisplayFormat = dateDisplayFormat;
	}

}
