package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_String;

public class AmiWebTitlePortlet extends HtmlPortlet {
	private AmiWebOverrideValue<String> title = new AmiWebOverrideValue<String>("");
	private Integer titleFontSize = 10;
	private String titleFontFam;
	private String paddingBorderColor;
	private String titleAlign;
	private String titleColor;
	private int titleWidthPx;

	public AmiWebTitlePortlet(PortletConfig manager) {
		super(manager);
	}

	private void update() {
		setCssStyle("_bg=" + this.paddingBorderColor + "|_fs=" + this.titleFontSize + "|_fm=" + this.titleFontFam + "," + this.titleAlign + "|_fg=" + this.titleColor
				+ "|style.userSelect=none");
		setHtml(this.title == null ? "" : "&nbsp;" + WebHelper.escapeHtml(this.title.getValue(true)));
	}

	public void setPaddingBorderColor(String paddingBorderColor) {
		if (paddingBorderColor != null && paddingBorderColor.equals(this.paddingBorderColor)) {
			return;
		}
		this.paddingBorderColor = paddingBorderColor;
		update();
	}
	public void setTitleFontSize(Integer titleFontSize) {
		if (titleFontSize != null && titleFontSize.equals(this.titleFontSize)) {
			return;
		}
		this.titleFontSize = titleFontSize;
		update();
	}
	public void setTitleFontFam(String fontFam) {
		if (fontFam != null && fontFam.equals(this.titleFontFam))
			return;
		this.titleFontFam = fontFam;
		update();

	}
	public String getTitleFontFam() {
		return this.titleFontFam;
	}
	public void setTitle(String title, boolean isOverride) {
		this.title.setValue(title, isOverride);
		this.titleWidthPx = title == null ? FormPortletField.DEFAULT_WIDTH : (getManager().getPortletMetrics().getWidth(title, getCssStyle(), this.titleFontSize));
		if (this.titleWidthPx < 5) { // Make sure user can find field if title is very small
			this.titleWidthPx = FormPortletField.DEFAULT_WIDTH;
		}
		update();
	}
	public String getTitle(boolean isOverride) {
		return this.title.getValue(isOverride);
	}
	public void setTitleAlign(String titleAlign) {
		if (titleAlign != null && titleAlign.equals(this.titleAlign))
			return;
		this.titleAlign = titleAlign;
		update();
	}
	public void setTitleColor(String titleColor) {
		if (titleColor != null && titleColor.equals(this.titleColor))
			return;
		this.titleColor = titleColor;
		update();
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		AmiWebUtils.putSkipEmpty(r, "title", this.title.getValue(false));
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToPortletIdMapping, StringBuilder sb) {
		super.init(configuration, origToPortletIdMapping, sb);
		setTitle(CH.getOr(Caster_String.INSTANCE, configuration, "title", ""), false);
	}

	@Override
	protected void onUserClick() {
		if (AmiWebUtils.getService(this.getManager()).getDesktop().getInEditMode())
			getManager().showDialog("Title Settings", new AmiWebTitleSettingsPortlet(generateConfig(), this));
	}
	@Override
	protected void onUserCallback(String id, int mouseX, int mouseY, HtmlPortlet.Callback attributes) {

	}

}
