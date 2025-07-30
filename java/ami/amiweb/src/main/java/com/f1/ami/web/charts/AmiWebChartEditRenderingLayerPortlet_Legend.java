package com.f1.ami.web.charts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;

public class AmiWebChartEditRenderingLayerPortlet_Legend extends AmiWebChartEditRenderingLayerPortlet<AmiWebChartRenderingLayer_Legend> implements FormPortletListener {

	final private FormPortlet form;
	final private AmiWebChartRenderingLayer_Legend existing;
	final private AmiWebChartPlotPortlet plot;
	final private FormPortletTextField nameField;
	final private AmiWebChartGridPortlet chart;
	final private FormPortlet seriesSelectForm;
	final private List<FormPortletCheckboxField> seriesFields = new ArrayList<FormPortletCheckboxField>();
	private final AmiWebEditStylePortlet editStylePortlet;
	private final static int LABELS_WIDTH = 100;

	public AmiWebChartEditRenderingLayerPortlet_Legend(PortletConfig config, AmiWebChartPlotPortlet plot, AmiWebChartRenderingLayer_Legend layer) {
		super(config, layer);
		this.plot = plot;
		this.chart = plot.getChart();
		this.existing = layer;
		this.form = new FormPortlet(generateConfig());
		this.seriesSelectForm = new FormPortlet(generateConfig());
		this.form.addField(new FormPortletTitleField("").setHeight(0));
		this.form.addField(new FormPortletTitleField("").setHeight(0));
		this.form.addField(new FormPortletTitleField("Title"));
		this.nameField = this.form.addField(new FormPortletTextField("Legend Title:"));
		this.nameField.focus();
		this.form.addField(new FormPortletTitleField("").setHeight(0));
		this.form.getFormPortletStyle().setLabelsWidth(LABELS_WIDTH);

		// Add settings form
		this.addChild(form, 0, 0);
		this.setRowSize(0, 400);

		// Add series select form
		this.addChild(seriesSelectForm, 0, 1);
		this.setRowSize(1, 150);

		// Add style form
		FormPortlet styleFormHeader = new FormPortlet(generateConfig());
		styleFormHeader.addField(new FormPortletTitleField("Style Options"));
		styleFormHeader.getFormPortletStyle().setLabelsWidth(LABELS_WIDTH);
		this.addChild(styleFormHeader, 0, 2);
		this.editStylePortlet = new AmiWebEditStylePortlet(layer.getStylePeer(), generateConfig());
		this.addChild(this.editStylePortlet, 0, 3);
		this.editStylePortlet.hideButtonsForm(true);
		this.editStylePortlet.getStyleForm().getFormPortletStyle().setLabelsWidth(LABELS_WIDTH);
		this.setRowSize(0, 100);
		this.setRowSize(2, 35);

		if (existing != null) {
			this.nameField.setValue(existing.getName());
		}
		seriesSelectForm.getFormPortletStyle().setLabelsWidth(LABELS_WIDTH);
		seriesSelectForm.addField(new FormPortletTitleField("Available Series"));
		for (AmiWebChartRenderingLayer<?> l : plot.getRenderyingLayers()) {
			if (!(l instanceof AmiWebChartRenderingLayer_Legend)) {
				addSeriesField(l);
			}
		}

		this.form.addFormPortletListener(this);
		this.seriesSelectForm.addFormPortletListener(this);
		this.setSuggestedSize(700, 500);

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (existing != null) {
			if (portlet == this.form) {
				applySettings(existing);
			} else if (portlet == this.seriesSelectForm) {
				for (FormPortletCheckboxField s : this.seriesFields) {
					if (s.getBooleanValue()) {
						existing.addSeries(((AmiWebChartRenderingLayer<AmiWebChartSeries>) s.getCorrelationData()).getSeries().getId());
					} else {
						existing.removeSeries(((AmiWebChartRenderingLayer<AmiWebChartSeries>) s.getCorrelationData()).getSeries().getId());
					}
				}
			}
		}
	}
	private void applySettings(AmiWebChartRenderingLayer_Legend layer) {
		layer.setName(this.nameField.getValue());
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public AmiWebEditStylePortlet getEditStylePortlet() {
		return this.editStylePortlet;
	}

	public void addSeriesField(AmiWebChartRenderingLayer<?> layer) {
		if (layer == null)
			return;
		// Check to see if field already exists... 
		for (FormPortletField<?> f : this.seriesFields)
			if (f.getCorrelationData() != null && f.getCorrelationData().equals(layer))
				return;
		FormPortletCheckboxField field = this.seriesSelectForm
				.addField(new FormPortletCheckboxField(layer.getName() + ":").setCorrelationData(layer).setValue(this.existing != null));
		this.seriesFields.add(field);
		this.existing.addSeries(layer.getSeries().getId());
	}
	public void removeSeriesField(AmiWebChartRenderingLayer<?> layer) {
		if (layer == null)
			return;
		FormPortletField<?> removedField = null;
		for (FormPortletField<?> f : this.seriesSelectForm.getFormFields())
			if (f.getCorrelationData() != null && f.getCorrelationData().equals(layer)) {
				removedField = f;
				break;
			}
		if (removedField != null) {
			this.seriesFields.remove(removedField);
			this.seriesSelectForm.removeField(removedField);
		}
	}
	public void renameSeriesField(AmiWebChartRenderingLayer<?> layer) {
		if (layer == null)
			return;
		for (FormPortletField<?> f : this.seriesSelectForm.getFormFields())
			if (f.getCorrelationData() != null && f.getCorrelationData().equals(layer)) {
				f.setTitle(layer.getName() + ":");
				break;
			}
	}
}
