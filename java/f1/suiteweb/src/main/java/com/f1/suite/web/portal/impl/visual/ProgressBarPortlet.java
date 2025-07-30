package com.f1.suite.web.portal.impl.visual;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class ProgressBarPortlet extends AbstractPortlet {

	private double progress;
	private String message = "";
	private String color;
	private int paddingT = 10;
	private int paddingR = 10;
	private int paddingB = 10;
	private int paddingL = 10;
	private boolean styleChanged = true;
	private String cssStyle;

	public ProgressBarPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}

	public void setProgress(double progress) {
		progress = MH.clip(progress, 0, 1);
		if (this.progress == progress)
			return;
		this.progress = progress;
		flagPendingAjax();
	}

	public void setMessage(String message) {
		message = SH.noNull(message);
		if (OH.eq(this.message, message))
			return;
		this.message = message;
		flagPendingAjax();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			callJsFunction("setProgress").addParam(this.progress).addParamQuoted(this.message).addParamQuoted(color).end();
			if (styleChanged) {
				callJsFunction("setStyle").addParam(this.paddingT).addParam(this.paddingR).addParam(this.paddingB).addParam(this.paddingL).addParamQuoted(this.cssStyle).end();
				this.styleChanged = false;
			}
		}
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		if (isVisible) {
			styleChanged = true;
			flagPendingAjax();
		}
	}

	public String getMessage() {
		return message;
	}

	public double getProgress() {
		return this.progress;
	}

	public static final PortletSchema<ProgressBarPortlet> SCHEMA = new BasicPortletSchema<ProgressBarPortlet>("ProgressBar", "ProgressBarPortlet", ProgressBarPortlet.class, true,
			true);

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}
	public void setColor(String color) {
		this.color = color;
		flagPendingAjax();
	}

	public int getPaddingT() {
		return paddingT;
	}

	public int getPaddingR() {
		return paddingR;
	}

	public int getPaddingB() {
		return paddingB;
	}

	public ProgressBarPortlet setPadding(int top, int right, int bottom, int left) {
		OH.assertGe(top, 0);
		OH.assertGe(bottom, 0);
		OH.assertGe(left, 0);
		OH.assertGe(right, 0);
		this.paddingT = top;
		this.paddingR = right;
		this.paddingB = bottom;
		this.paddingL = left;
		this.styleChanged = true;
		flagPendingAjax();
		return this;
	}
	public int getPaddingL() {
		return paddingL;
	}

	public void setCssStyle(String cssStyle) {
		if (OH.eq(cssStyle, this.cssStyle))
			return;
		this.styleChanged = true;
		flagPendingAjax();
		this.cssStyle = cssStyle;
	}

	public String getCssStyle() {
		return this.cssStyle;
	}

}
