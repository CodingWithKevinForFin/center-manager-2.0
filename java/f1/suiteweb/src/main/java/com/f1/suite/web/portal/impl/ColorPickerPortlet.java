package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.ColorUsingPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;

public class ColorPickerPortlet extends AbstractPortlet {

	public static final PortletSchema<ColorPickerPortlet> SCHEMA = new BasicPortletSchema<ColorPickerPortlet>("ColorPickerPortlet", "ColorPickerPortlet", ColorPickerPortlet.class,
			true, true);
	private static final String DEFAULT_COLOR = "#FFFFFF";
	private String color;
	private Object correlationData;
	private String defaultColor;
	private List<ColorPickerListener> listeners = new ArrayList<ColorPickerListener>();
	private List<String> customColors;
	private boolean alphaEnabled = false;
	private boolean needsInit;

	public ColorPickerPortlet(PortletConfig config) {
		super(config);
		this.setColor(DEFAULT_COLOR);
	}

	public ColorPickerPortlet(PortletConfig config, String color) {
		super(config);
		this.defaultColor = this.color = color;
		setSize(460, 360);
		findUsedColors();
	}
	public ColorPickerPortlet(PortletConfig config, String color, ColorPickerListener listener) {
		super(config);
		this.defaultColor = this.color = color;
		this.listeners.add(listener);
		setSize(460, 360);
		findUsedColors();
	}
	private void findUsedColors() {
		Collection<ColorUsingPortlet> found = PortletHelper.findPortletsByType(getManager().getRoot(), ColorUsingPortlet.class);
		Set<String> sink = new HashSet<String>();
		for (ColorUsingPortlet i : found)
			i.getUsedColors(sink);
		List<String> colors = CH.sort(sink);
		setCustomColors(colors);
	}

	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 360;
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 460;
	}
	public ColorPickerPortlet(PortletConfig config, String color, ColorPickerListener listener, Object correlationData) {
		super(config);
		this.defaultColor = this.color = color;
		this.correlationData = correlationData;
		this.listeners.add(listener);
		setSize(460, 330);
	}

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		if (OH.eq(color, this.color))
			return;
		this.color = color;
	}

	@Override
	protected void initJs() {
		super.initJs();
		callJsFunction("init").addParamQuoted(defaultColor).addParamQuoted(color).addParam(this.alphaEnabled).end();
		if (CH.isntEmpty(customColors))
			for (String s : customColors)
				callJsFunction("addColorChoice").addParamQuoted(s).end();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (this.needsInit) {
			callJsFunction("init").addParamQuoted(defaultColor).addParamQuoted(color).addParam(this.alphaEnabled).end();
			this.needsInit = false;
		}
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("onOkay".equals(callback)) {
			fireOnOkay();
		} else if ("onCancel".equals(callback)) {
			fireOnCancel();
		} else if ("onNoColor".equals(callback)) {
			fireOnChanged(null);
			fireOnOkay();
		} else if ("onColorChanged".equals(callback)) {
			fireOnChanged(attributes.get("color"));
		} else
			super.handleCallback(callback, attributes);
	}

	private void fireOnCancel() {
		for (ColorPickerListener l : this.listeners)
			l.onCancelPressed(this);
	}

	private void fireOnChanged(String nuwColor) {
		if (OH.eq(this.color, nuwColor))
			return;
		String oldColor = this.color;
		this.color = nuwColor;
		for (ColorPickerListener l : this.listeners)
			l.onColorChanged(this, oldColor, color);
	}

	private void fireOnOkay() {
		for (ColorPickerListener l : this.listeners)
			l.onOkayPressed(this);
	}

	public void addListener(ColorPickerListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(ColorPickerListener listener) {
		this.listeners.remove(listener);
	}

	public Object getCorrelationData() {
		return correlationData;
	}

	public ColorPickerPortlet setCorrelationData(Object correlationData) {
		this.correlationData = correlationData;
		return this;
	}

	public String getDefaultColor() {
		return defaultColor;
	}
	public void setDefaultColor(String defaultColor) {
		this.defaultColor = defaultColor;
	}

	public void setCustomColors(List<String> colors) {
		this.customColors = colors;
	}
	public List<String> getCustomColors() {
		return this.customColors;
	}

	public ColorPickerPortlet setAlphaEnabled(boolean b) {
		if (this.alphaEnabled == b)
			return this;
		this.alphaEnabled = b;
		this.needsInit = true;
		return this;
	}
	public boolean getAlphaEnabled() {
		return this.alphaEnabled;
	}

}
