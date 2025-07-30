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
import com.f1.utils.ColorHelper;
import com.f1.utils.OH;
import com.f1.utils.json.JsonBuilder;

public class FormPortletColorField extends FormPortletField<String> {

	public static final String JSNAME = "ColorField";
	private boolean allowNull = true;
	private boolean needsColors;
	private boolean alphaEnabled;
	private String noColorText = "No color";
	private boolean hasButton = false;
	private String displayText;
	private String origDisplayText;

	public FormPortletColorField(String title) {
		super(String.class, title);
		setDefaultValue("");
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public FormPortletColorField setValue(String value) {
		this.displayText = null;
		this.origDisplayText = null;
		super.setValue(value);
		return this;
	}
	@Override
	public void setBgColor(String bgColor) {
	}

	@Override
	public void setFontColor(String fontColor) {
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		final String value = CH.getOr(attributes, "value", null);
		final String status = CH.getOr(attributes, "status", null);
		if ("ok".equals(status)) {
			setDisplayText(null);
			this.origDisplayText = null;
		} else if ("cancel".equals(status)) {
			setDisplayText(origDisplayText);
		} else if ("momentary".equals(status)) {
			setDisplayText(null);
		} else if ("nocolor".equals(status)) {
			setDisplayText(null);
		}
		if (OH.eq(getValue(), value))
			return false;
		setValueNoFire(value);
		return true;
	}

	public void setDisplayText(String displayText) {
		if (OH.ne(this.displayText, displayText)) {
			this.displayText = displayText;
			flagConfigChanged();
		}

	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_CONFIG)) {
			JsFunction js = new JsFunction(pendingJs, jsObjectName, "init").addParam(allowNull).addParam(this.alphaEnabled).addParam(this.hasButton)
					.addParamQuoted(this.displayText).end();
			js.reset(pendingJs, jsObjectName, "setNoColorText").addParamQuoted(noColorText).end();
		}

		if (hasChanged(MASK_CUSTOM)) {
			if (this.needsColors) {
				JsFunction js = new JsFunction(pendingJs);
				Collection<ColorUsingPortlet> found = PortletHelper.findPortletsByType(getForm().getManager().getRoot(), ColorUsingPortlet.class);
				Set<String> sink = new HashSet<String>();
				for (ColorUsingPortlet i : found)
					i.getUsedColors(sink);
				List<String> colors = CH.sort(sink);
				if (!colors.isEmpty()) {
					js.reset(pendingJs, jsObjectName, "addCustomColor");
					JsonBuilder json = js.startJson();
					json.addQuoted(colors);
					js.end();
				}
				this.needsColors = false;
			}
		}
		super.updateJs(pendingJs);
	}

	public boolean getAllowNull() {
		return this.allowNull;
	}
	public FormPortletColorField setAllowNull(boolean allowNull) {
		if (this.allowNull == allowNull)
			return this;
		this.allowNull = allowNull;
		flagConfigChanged();
		return this;
	}

	public void onClicked(StringBuilder stringBuilder, String string) {
		flagChange(MASK_CUSTOM);
		this.needsColors = true;
	}

	public FormPortletColorField setAlphaEnabled(boolean b) {
		if (this.alphaEnabled == b)
			return this;
		this.alphaEnabled = b;
		if (!this.alphaEnabled) {
			String v = this.getValue();
			if (v != null) {
				long color = ColorHelper.parseRgbNoThrow(v);
				if (color != ColorHelper.NO_COLOR) {
					v = ColorHelper.toRgbString(ColorHelper.toRgb(ColorHelper.getR((int) color), ColorHelper.getB((int) color), ColorHelper.getG((int) color)));
				} else
					v = null;

				setValue(v, this.displayText);
			}
		}
		flagConfigChanged();
		return this;
	}
	public boolean getAlphaEnabled() {
		return this.alphaEnabled;
	}

	@Override
	public FormPortletColorField setName(String name) {
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
	public FormPortletColorField setWidth(int width) {
		super.setWidth(width);
		return this;
	}

	public FormPortletColorField setHasButton(boolean hasButton) {
		if (hasButton == this.hasButton)
			return this;
		this.hasButton = hasButton;
		this.flagConfigChanged();
		return this;
	}

	public void setValue(String color, String textToShow) {
		super.setValue(color);
		setDisplayText(textToShow);
		this.origDisplayText = textToShow;
	}

	public String getDisplayText() {
		return this.displayText;
	}
}
