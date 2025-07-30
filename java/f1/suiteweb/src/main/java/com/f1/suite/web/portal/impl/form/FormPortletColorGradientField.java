package com.f1.suite.web.portal.impl.form;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.ColorUsingPortlet;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.OH;
import com.f1.utils.json.JsonBuilder;

public class FormPortletColorGradientField extends FormPortletField<ColorGradient> {

	public static final String JSNAME = "ColorGradientField";
	private boolean allowNull;
	private boolean needsColors;
	private boolean alphaEnabled;
	private String noColorText = "No color";

	public FormPortletColorGradientField(String title) {
		super(ColorGradient.class, title);
		setDefaultValue(null);
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public FormPortletColorGradientField setValue(ColorGradient value) {
		super.setValue(value);
		return this;
	}
	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final String value = CH.getOr(attributes, "value", null);
		ColorGradient gradient = "".equals(value) ? null : new ColorGradient(0, 1, value);
		if (OH.eq(getValue(), gradient))
			return false;
		setValueNoFire(gradient);
		return true;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG)) {
			new JsFunction(pendingJs, jsObjectName, "init").addParam(allowNull).addParam(this.alphaEnabled).addParamQuoted(this.getBorderColorMaterialized())
					.addParam(this.getBorderRadius()).addParam(this.getBorderWidthMaterialized()).end();
			new JsFunction(pendingJs, jsObjectName, "setNoColorText").addParamQuoted(noColorText).end();
		}
		if (hasChanged(MASK_CUSTOM)) {
			if (this.needsColors) {
				Collection<ColorUsingPortlet> found = PortletHelper.findPortletsByType(getForm().getManager().getRoot(), ColorUsingPortlet.class);
				Set<String> sink = new HashSet<String>();
				for (ColorUsingPortlet i : found)
					i.getUsedColors(sink);
				List<String> colors = CH.sort(sink);
				if (!colors.isEmpty()) {
					JsFunction jsFunction = new JsFunction(pendingJs, jsObjectName, "addCustomColor");
					JsonBuilder json = jsFunction.startJson();
					json.addQuoted(colors);
					jsFunction.end();
				}
				this.needsColors = false;
			}
		}
		super.updateJs(pendingJs);
	}

	public boolean getAllowNull() {
		return this.allowNull;
	}
	public FormPortletColorGradientField setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
		return this;
	}

	public void onClicked(StringBuilder stringBuilder, String string) {
		flagChange(MASK_CUSTOM);
		this.needsColors = true;
	}

	public FormPortletColorGradientField setAlphaEnabled(boolean b) {
		if (this.alphaEnabled == b)
			return this;
		this.alphaEnabled = b;
		if (!this.alphaEnabled) {
			ColorGradient v = this.getValue();
			if (v != null && v.hasAlpha()) {
				v = v.clearAlpha();
				setValue(v);
			}
		}
		flagConfigChanged();
		return this;
	}
	public boolean getAlphaEnabled() {
		return this.alphaEnabled;
	}

	@Override
	public FormPortletColorGradientField setName(String name) {
		super.setName(name);
		return this;
	}

	public String getNoColorText() {
		return noColorText;
	}
	public void setNoColorText(String noColorText) {
		if (OH.eq(this.noColorText, noColorText))
			return;
		this.noColorText = noColorText;
		flagChange(MASK_CONFIG);
	}

	@Override
	public FormPortletColorGradientField setWidth(int width) {
		super.setWidth(width);
		return this;
	}
	public String getJsValue() {
		ColorGradient t = getValue();
		return t == null ? null : t.toString();
	}

}
