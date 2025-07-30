package com.f1.ami.web.charts;

import java.util.Map;

import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class AmiWebChartSettingsPortlet extends AmiWebPanelSettingsPortlet {

	//	private FormPortletNumericRangeField maxPointsVisibleLimit;
	private final FormPortletToggleButtonsField<Boolean> clearOnDataStale;
	private final AmiWebChartGridPortlet chart;

	public AmiWebChartSettingsPortlet(PortletConfig config, AmiWebChartGridPortlet chart) {
		super(config, chart);
		this.chart = chart;
		FormPortlet form = getSettingsForm();
		//		maxPointsVisibleLimit = new FormPortletNumericRangeField("Max points visible:", 1, 10000000, 0);
		//		maxPointsVisibleLimit.setSliderHidden(true);
		//		maxPointsVisibleLimit.setValue(chart.getMaxPointsVisibleLimit());
		//		maxPointsVisibleLimit.setWidthPx(60);
		//		maxPointsVisibleLimit.setHeightPx(18);
		//		form.addField(maxPointsVisibleLimit);

		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.addOption(true, "On");
		this.clearOnDataStale.addOption(false, "Off");
		this.clearOnDataStale.setValue(chart.isClearOnDataStale());
		form.addField(this.clearOnDataStale);
	}

	@Override
	protected void submitChanges() {
		this.chart.setClearOnDataStale(this.clearOnDataStale.getValue());
		super.submitChanges();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		AmiWebChartGridPortlet chart = (AmiWebChartGridPortlet) this.getPortlet();
		//		if (field == maxPointsVisibleLimit) {
		//			double v = maxPointsVisibleLimit.getValue();
		//			long x = (long) (v);
		//			chart.setMaxPointsVisibleLimit(x);
		//		} else
		super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 400;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 250;
	}

}
