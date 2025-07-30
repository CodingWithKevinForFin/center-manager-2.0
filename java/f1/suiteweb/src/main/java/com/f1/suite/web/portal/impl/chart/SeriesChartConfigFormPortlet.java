package com.f1.suite.web.portal.impl.chart;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.OH;

public class SeriesChartConfigFormPortlet extends FormPortlet {

	private SeriesChartPortlet chart;
	private FormPortletSelectField<Byte> typeSelectField;
	private Map<String, FormPortletColorField> colorFields = new HashMap<String, FormPortletColorField>();
	private Map<String, FormPortletTextField> labelFields = new HashMap<String, FormPortletTextField>();
	private Map<String, FormPortletCheckboxField> isVisibleFields = new HashMap<String, FormPortletCheckboxField>();
	private Map<String, FormPortletField<String>> optionFields = new LinkedHashMap<String, FormPortletField<String>>();
	private FormPortletButton cancelButton;
	private FormPortletButton applyButton;

	public SeriesChartConfigFormPortlet(PortletConfig config, SeriesChartPortlet portlet) {
		super(config);
		this.chart = portlet;
		typeSelectField = new FormPortletSelectField<Byte>(Byte.class, "Series Chart Type");
		typeSelectField.addOption(SeriesChartPortlet.STYLE_AREA, "Area Chart");
		typeSelectField.addOption(SeriesChartPortlet.STYLE_BAR, "Bar Chart");
		typeSelectField.addOption(SeriesChartPortlet.STYLE_LINE, "Line Chart");
		typeSelectField.addOption(SeriesChartPortlet.STYLE_BAR_STACKED, "Stacked Bar Chart");
		typeSelectField.addOption(SeriesChartPortlet.STYLE_AREA_STACKED, "Stacked Area Chart");
		typeSelectField.setValue(this.chart.getStyle());
		addField(typeSelectField);

		String[] options = new String[] { SeriesChartPortlet.OPTION_RANGE_LABEL_SUFFIX, SeriesChartPortlet.OPTION_Y_MAX, SeriesChartPortlet.OPTION_Y_MIN,
				SeriesChartPortlet.OPTION_X_MAX, SeriesChartPortlet.OPTION_X_MIN, SeriesChartPortlet.OPTION_H_GRID_HIDE, SeriesChartPortlet.OPTION_V_GRID_HIDE,
				SeriesChartPortlet.OPTION_BORDER_HIDE, SeriesChartPortlet.OPTION_RANGE_LABEL_HIDE, SeriesChartPortlet.OPTION_DOMAIN_LABEL_HIDE,
				SeriesChartPortlet.OPTION_KEY_POSITION, SeriesChartPortlet.OPTION_TITLE, SeriesChartPortlet.OPTION_CHART_TEXT };

		for (String option : options)
			optionFields.put(option, addField(new FormPortletTextField("Option - " + option).setWidth(300).setValue(OH.toString(this.chart.getOption(option)))));
		for (String id : this.chart.getSeries()) {
			FormPortletTextField fieldLabel = new FormPortletTextField(id + " label").setValue(this.chart.getSeriesLabel(id)).setWidth(300);
			FormPortletColorField fieldColor = new FormPortletColorField(id + " color").setValue(this.chart.getSeriesColor(id));
			FormPortletCheckboxField fieldVisible = new FormPortletCheckboxField(id, this.chart.getSeriesVisible(id));
			colorFields.put(id, fieldColor);
			labelFields.put(id, fieldLabel);
			isVisibleFields.put(id, fieldVisible);
			super.addField(fieldLabel);
			super.addField(fieldColor);
			super.addField(fieldVisible);
		}

		addButton(this.cancelButton = new FormPortletButton("Close"));

	}

	@Override
	protected boolean onUserChangedValue(com.f1.suite.web.portal.impl.form.FormPortletField<?> field, java.util.Map<String, String> attributes) {
		boolean r = super.onUserChangedValue(field, attributes);
		for (Entry<String, FormPortletColorField> e : colorFields.entrySet())
			this.chart.setSeriesColor(e.getKey(), e.getValue().getValue());
		for (Entry<String, FormPortletTextField> e : labelFields.entrySet())
			this.chart.setSeriesLabel(e.getKey(), e.getValue().getValue());
		for (Entry<String, FormPortletCheckboxField> e : isVisibleFields.entrySet())
			this.chart.setSeriesVisible(e.getKey(), e.getValue().getValue());

		this.chart.setStyle(this.typeSelectField.getValue());
		this.chart.clearOptions();
		for (Entry<String, FormPortletField<String>> e : optionFields.entrySet()) {
			if (e.getValue().getValue() != null)
				this.chart.addOption(e.getKey(), e.getValue().getValue());
		}
		return r;
	};

	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		if (button == this.applyButton) {
		}
		close();
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 500;
	}
}
