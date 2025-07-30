package com.f1.suite.web.portal.impl.form;

import java.util.Map;

import com.f1.base.ToStringable;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;

public class FormPortletToggleButtonsField<T> extends FormPortletField<T> {
	private IndexedList<T, Option<T>> options = new BasicIndexedList<T, Option<T>>();

	public static final String OPTION_TOGGLE_BUTTON_FIELD_STYLE = "TOGGLE_BUTTON_FIELD_STYLE";
	public static final String OPTION_TOGGLE_BUTTON_STYLE = "TOGGLE_BUTTON_STYLE";
	public static final String OPTION_TOGGLE_BUTTON_ON_STYLE = "TOGGLE_BUTTON_ON_STYLE";
	public static final String OPTION_TOGGLE_BUTTON_OFF_STYLE = "TOGGLE_BUTTON_OFF_STYLE";
	public static final char TOGGLE_MODE_SELECT = 'S';
	public static final char TOGGLE_MODE_TOGGLE = 'T';

	public static final String JSNAME = "ToggleButtonsField";
	private int spacing = -1;
	private int minButtonWidth = -1;
	private String buttonStyle;
	private String buttonOnStyle;
	private String buttonOffStyle;
	private String style;
	private char mode = 'S';

	public FormPortletToggleButtonsField(Class<T> type, String title) {
		super(type, title);
	}

	@Override
	public String getjsClassName() {
		return JSNAME;
	}

	@Override
	public boolean onUserValueChanged(Map<String, String> attributes) {
		if (!isDisabled()) {
			final Integer value = CH.getOrThrow(Caster_Integer.INSTANCE, attributes, "value");
			T val = this.options.getKeyAt(value);
			setValueNoFire(val);
			return true;
		} else
			return false;
	}
	public FormPortletToggleButtonsField<T> addOption(T key, String value) {
		return this.addOption(key, value, null);
	}

	public FormPortletToggleButtonsField<T> addOption(T key, String value, String cssStyle) {
		options.add(key, new Option<T>(key, value, cssStyle));
		if (options.getSize() == 1) {
			setValue(key);
			setDefaultValue(key);
		}
		flagChange(MASK_OPTIONS);
		return this;
	}

	public int getSpacing() {
		return this.spacing;
	}

	public int getMinButtonWidth() {
		return this.minButtonWidth;
	}

	public FormPortletToggleButtonsField<T> setSpacing(int spacing) {
		if (this.spacing == spacing)
			return this;
		this.spacing = spacing;
		flagConfigChanged();
		return this;
	}

	public FormPortletToggleButtonsField<T> setMinButtonWidth(int minButtonWidth) {
		if (this.minButtonWidth == minButtonWidth)
			return this;
		this.minButtonWidth = minButtonWidth;
		flagConfigChanged();
		return this;
	}

	public char getMode() {
		return mode;
	}

	public FormPortletToggleButtonsField<T> setMode(char mode) {
		if (this.mode == mode)
			return this;
		this.mode = mode;
		flagConfigChanged();
		return this;
	}

	public int getSuggestedSpacing(PortletMetrics pm) {
		return this.spacing == -1 ? 8 : spacing;
	}

	public int getSuggestedMinButtonWidth(PortletMetrics pm) {
		return this.minButtonWidth == -1 ? this.getSuggestedWidth() / this.options.getSize() : minButtonWidth;
	}

	@Override
	public void updateJs(StringBuilder pendingJs) {
		if (hasChanged(MASK_OPTIONS)) {
			new JsFunction(pendingJs, jsObjectName, "clear").end();
			for (int i = 0, l = this.options.getSize(); i < l; i++) {
				final Option<T> option = this.options.getAt(i);
				new JsFunction(pendingJs, jsObjectName, "addOption").addParamQuoted(i).addParamQuoted(option.getValue()).addParamQuoted(option.getCssStyle()).end();
			}
		}
		if (hasChanged(MASK_CONFIG)) {
			if (spacing != -1)
				new JsFunction(pendingJs, jsObjectName, "setSpacing").addParam(getSpacing()).end();
			if (minButtonWidth != -1)
				new JsFunction(pendingJs, jsObjectName, "setMinButtonWidth").addParam(getMinButtonWidth()).end();
			new JsFunction(pendingJs, jsObjectName, "setMode").addParamQuoted(mode).end();
			new JsFunction(pendingJs, jsObjectName, "init").end();
		}
		if (hasChanged(MASK_STYLE | MASK_CONFIG)) {
			Map<String, String> styleOptions = super.getForm().getStyleManager().getDefaultFormFieldOptions(getjsClassName());
			String css = this.style != null ? this.style : CH.getOr(Caster_String.INSTANCE, styleOptions, OPTION_TOGGLE_BUTTON_FIELD_STYLE, null);
			String btnCss = this.buttonStyle != null ? this.buttonStyle : CH.getOr(Caster_String.INSTANCE, styleOptions, OPTION_TOGGLE_BUTTON_STYLE, null);
			String btnOnCss = this.buttonOnStyle != null ? this.buttonOnStyle : CH.getOr(Caster_String.INSTANCE, styleOptions, OPTION_TOGGLE_BUTTON_ON_STYLE, null);
			String btnOffCss = this.buttonOffStyle != null ? this.buttonOffStyle : CH.getOr(Caster_String.INSTANCE, styleOptions, OPTION_TOGGLE_BUTTON_OFF_STYLE, null);
			new JsFunction(pendingJs, jsObjectName, "setOnCssStyle").addParamQuoted(btnOnCss).end();
			new JsFunction(pendingJs, jsObjectName, "setOffCssStyle").addParamQuoted(btnOffCss).end();
			new JsFunction(pendingJs, jsObjectName, "applyStyle").addParamQuoted(css).addParamQuoted(btnCss).end();
		}
		super.updateJs(pendingJs);
	}

	@Override
	public void setBgColor(String bgColor) {
	}

	private static class Option<T> implements ToStringable {
		private T key;
		private String value;
		private String cssStyle;

		public Option(T key, String value, String cssStyle) {
			this.value = value;
			this.key = key;
			this.cssStyle = cssStyle;
		}
		public String getValue() {
			return value;
		}
		public T getKey() {
			return key;
		}
		public String getCssStyle() {
			return cssStyle;
		}

		public boolean setCssStyle(String cssStyle) {
			if (OH.eq(this.cssStyle, cssStyle))
				return false;
			this.cssStyle = cssStyle;
			return true;
		}

		@Override
		public String toString() {
			return toString(new StringBuilder()).toString();
		}

		@Override
		public StringBuilder toString(StringBuilder sink) {
			sink.append("key:");
			sink.append(key);
			sink.append("; value:");
			sink.append(value);
			sink.append("; cssStyle:");
			sink.append(cssStyle);
			return sink;
		}
	}

	public FormPortletToggleButtonsField<T> setCssStyle(String style) {
		this.style = style + "|_fg=" + this.getFontColor() + "|_bg=" + this.getBgColor();
		flagStyleChanged();
		return this;
	}

	public FormPortletToggleButtonsField<T> setButtonStyle(String style) {
		this.buttonStyle = style;
		flagStyleChanged();
		return this;
	}

	public void setButtonStyleAtIndex(String style, int index) {
		if (index < options.getSize()) {
			if (options.getAt(index).setCssStyle(style))
				flagChange(MASK_OPTIONS);
		}
	}

	public void setButtonStyleAtIndexNoFire(String style, int index) {
		if (index < options.getSize()) {
			options.getAt(index).setCssStyle(style);
		}
	}
	public void setButtonOnStyle(String style) {
		this.buttonOnStyle = style;
		flagStyleChanged();
	}

	public void setButtonOffStyle(String style) {
		this.buttonOffStyle = style;
		flagStyleChanged();
	}

	@Override
	public String getJsValue() {
		return SH.toString(options.getPosition(super.getValue()));
	}

	@Override
	public FormPortletToggleButtonsField<T> setName(String name) {
		super.setName(name);
		return this;
	}

	public FormPortletToggleButtonsField<T> addDefaultOption() {
		addOption(null, "Inherited", "style.background=#aaaaaa");
		return this;
	}

	@Override
	public FormPortletToggleButtonsField<T> setCorrelationData(Object correlationData) {
		super.setCorrelationData(correlationData);
		return this;
	}

	public FormPortletToggleButtonsField<T> setValue(T value) {
		if (!this.options.containsKey(value))
			throw new RuntimeException("unknown key: " + value);
		super.setValue(value);
		return this;
	};

	@Override
	public boolean canFocus() {
		return false;
	}

}
