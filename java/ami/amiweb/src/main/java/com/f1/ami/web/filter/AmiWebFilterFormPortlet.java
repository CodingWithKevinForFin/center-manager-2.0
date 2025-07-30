package com.f1.ami.web.filter;

import java.util.List;

import com.f1.ami.web.filter.AmiWebFilterPortlet.Option;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.concurrent.HasherSet;

public abstract class AmiWebFilterFormPortlet extends FormPortlet {

	private boolean filterOutNulls;
	private String backgroundColor;
	private String fontColor;
	private Integer fontSize;
	private String fontFamily;
	private String titleAlignment;
	private String titleFontColor;
	private Integer titleFontSize;
	private String titleFontFamily;
	private Boolean titleBold;
	private Boolean titleItalic;
	private Boolean titleUnderline;
	private String fieldBorderColor;
	private String fieldsBackgroundColor;
	private String fieldsFontColor;
	private final FormPortletDivField titleField;
	private final PortletStyleManager_Form styleManager = new PortletStyleManager_Form();

	public AmiWebFilterFormPortlet(PortletConfig config) {
		super(config);
		this.titleField = addField(new FormPortletDivField(""));
		this.filterOutNulls = false;
		initStyleManager();
	}
	private void initStyleManager() {
		this.styleManager.setDefaultFormButtonsHeight(28);
		this.styleManager.setDefaultFormButtonsPaddingTop(10);
		this.styleManager.setDefaultFormButtonsPaddingBottom(4);
		this.styleManager.setDefaultFormButtonsSpacing(8);
		this.styleManager.setFormButtonsStyle("_bg=#e2e2e2|style.border=1px solid #aaaaaa|style.minWidth=95px|_fn=arial|_fs=17");
		this.styleManager.putDefaultFormFieldStyle(FormPortletTitleField.JSNAME,
				"_fs=11px|style.textTransform=upperCase|_fg=#000000|style.padding=4px 0px 0px 0px|style.display=block");
		this.styleManager.putDefaultFormFieldWidth(FormPortletTextField.JSNAME, 200);
		this.styleManager.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_GRIP_COLOR, "#007608");
		this.styleManager.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_TRACK_LEFT_COLOR, "#007608");
		this.styleManager.putDefaultFormFieldHeight(FormPortletTitleField.JSNAME, 27);
		this.styleManager.setDefaultFormWidthStretchPadding(6);
		this.styleManager.setDefaultFormFieldHeight(25);
		this.styleManager.setDefaultFormFieldSpacing(6);

		setStyle(this.styleManager);
	}
	public boolean isValidDataType(Class<?> type) {
		return true;
	}

	protected void setFilterOutNulls(boolean filterOutNulls) {
		this.filterOutNulls = filterOutNulls;
	}

	protected boolean getFilerOutNulls() {
		return this.filterOutNulls;
	}

	protected void setOptions(String title, List<Option> options) {
		this.titleField.setValue(title);
	}
	protected void setFilterTitle(String title) {
		this.titleField.setValue(WebHelper.escapeHtml(title));
	}

	public void setFieldsBackgroundColor(String bgColor) {
		if (this.fieldsBackgroundColor == bgColor)
			return;
		for (FormPortletField<?> f : getFormFields()) {
			f.setBgColor(bgColor);
		}
		this.fieldsBackgroundColor = bgColor;
	}
	public String getFieldsBackgroundColor() {
		return this.fieldsBackgroundColor;
	}
	public void setFieldsFontColor(String fontColor) {
		if (this.fieldsFontColor == fontColor)
			return;
		for (FormPortletField<?> f : getFormFields()) {
			f.setFontColor(fontColor);
		}
		this.fieldsFontColor = fontColor;
	}
	public String getFieldsFontColor() {
		return this.fieldsFontColor;
	}

	//returning null means not filter
	abstract HasherSet<Row> getSelectedRows();
	abstract void clearSelectedRows();

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		if (this.backgroundColor == backgroundColor)
			return;
		this.backgroundColor = backgroundColor;
		this.titleField.setBgColor(backgroundColor);
		updateBodyCssStyle();
		updateTitleCssStyle();
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		if (this.fontColor == fontColor)
			return;
		this.fontColor = fontColor;
		updateBodyCssStyle();
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontSize(Integer fontSize) {
		if (this.fontSize == fontSize)
			return;
		this.fontSize = fontSize;
		updateBodyCssStyle();
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		if (this.fontFamily == fontFamily)
			return;
		this.fontFamily = fontFamily;
		updateBodyCssStyle();
	}

	public String getTitleAlignment() {
		return titleAlignment;
	}

	public void setTitleAlignment(String titleAlignment) {
		if (this.titleAlignment == titleAlignment)
			return;
		this.titleAlignment = titleAlignment;
		updateTitleCssStyle();
	}

	public FormPortletDivField getTitleField() {
		return titleField;
	}

	public String getTitleFontColor() {
		return titleFontColor;
	}

	public void setTitleFontColor(String titleFontColor) {
		if (this.titleFontColor == titleFontColor)
			return;
		this.titleFontColor = titleFontColor;
		updateTitleCssStyle();
	}

	public Integer getTitleFontSize() {
		return titleFontSize;
	}

	public void setTitleFontSize(Integer titleFontSize) {
		if (this.titleFontSize == titleFontSize)
			return;
		this.titleFontSize = titleFontSize;
		updateTitleCssStyle();
		this.titleField.setHeightPx(this.titleFontSize);
		setFieldAbsPositioning();
	}

	public String getTitleFontFamily() {
		return titleFontFamily;
	}

	public void setTitleFontFamily(String titleFontFamily) {
		if (this.titleFontFamily == titleFontFamily)
			return;
		this.titleFontFamily = titleFontFamily;
		updateTitleCssStyle();
	}

	public Boolean getTitleBold() {
		return titleBold;
	}

	public void setTitleBold(Boolean titleBold) {
		if (this.titleBold == titleBold)
			return;
		this.titleBold = titleBold;
		updateTitleCssStyle();
	}

	public Boolean getTitleItalic() {
		return titleItalic;
	}

	public void setTitleItalic(Boolean titleItalic) {
		if (this.titleItalic == titleItalic)
			return;
		this.titleItalic = titleItalic;
		updateTitleCssStyle();
	}

	public Boolean getTitleUnderline() {
		return titleUnderline;
	}

	public void setTitleUnderline(Boolean titleUnderline) {
		if (this.titleUnderline == titleUnderline)
			return;
		this.titleUnderline = titleUnderline;
		updateTitleCssStyle();
	}

	public void setFieldBorderColor(String fieldBorderColor) {
		if (this.fieldBorderColor == fieldBorderColor)
			return;
		this.fieldBorderColor = fieldBorderColor;
		this.styleManager.putDefaultFormFieldStyle(FormPortletTextField.JSNAME, "style.border=1px solid " + this.fieldBorderColor + "|_fs=11px");
		this.styleManager.putDefaultFormFieldStyle(FormPortletSelectField.JSNAME, "style.border=1px solid " + this.fieldBorderColor + "|_fs=11px");
		this.styleManager.putDefaultFormFieldStyle(FormPortletTextAreaField.JSNAME, "style.border=1px solid " + this.fieldBorderColor + "|_fs=11px|style.resize=none");
		this.styleManager.putDefaultFormFieldStyle(FormPortletColorField.JSNAME, "style.border=1px solid " + this.fieldBorderColor + "|_fs=11px");
		flagLayoutChanged();
		flagChange(MASK_POSITIONS);

	}

	public String getFieldBorderColor() {
		return this.fieldBorderColor;
	}

	private void updateBodyCssStyle() {
		this.getFormPortletStyle()
				.setCssStyle("style.background=" + this.backgroundColor + "|style.color=" + this.fontColor + "|style.fontSize=" + this.fontSize + "px|_fm=" + this.fontFamily);
		flagStyleChanged();
	}

	private void updateTitleCssStyle() {
		String biuString = (this.titleBold == null || !this.titleBold ? "" : ",bold") + (this.titleItalic == null || !this.titleItalic ? "" : ",italic")
				+ (this.titleUnderline == null || !this.titleUnderline ? "" : ",underline");
		this.titleField.setCssStyle("_fm=" + this.titleFontFamily + biuString + "," + this.titleAlignment + "|style.color=" + this.titleFontColor + "|style.fontSize="
				+ this.titleFontSize + "px" + "|_bg=" + this.backgroundColor);
		flagLayoutChanged();
	}
	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.setFieldAbsPositioning();
	}
	protected void setFieldAbsPositioning() {
		this.titleField.setTopPosPx(10);
		if (!WebAbsoluteLocation.is(this.titleField.getHeightPx())) {
			this.titleField.setHeightPx(this.titleField.getDefaultHeight());
		}
		FormPortletField<?> prevField = this.titleField;
		int leftPosPx = this instanceof AmiWebFilterFormPortlet_Range ? 50 : 10;
		for (FormPortletField<?> f : getFormFields()) {
			if (f != this.titleField) {
				f.setHeightPx(f.getDefaultHeight());
				f.setTopPosPx(prevField.getTopPosPx() + prevField.getHeightPx() + (prevField == this.titleField ? 15 : 5));
			}
			if (!this.updateFieldAbsPositioning(f)) {
				f.setWidthPx(f.getDefaultWidth());
				f.setLeftPosPx(leftPosPx);
				if (f instanceof FormPortletCheckboxField) {
					f.setLabelWidthPx(this.getWidth() - 40);
				}
			}
			prevField = f;
		}
	}
	protected boolean updateFieldAbsPositioning(FormPortletField f) {
		//If the field positioning is manually overridden return true, otherwise false;
		return false;
	}
}
