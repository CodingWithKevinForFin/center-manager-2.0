package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_Graph extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_Graph> {

	public AmiWebChartEditSeriesPortlet_Graph(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		super.initForm();
	}

	@Override
	public String getEditorLabel() {
		return "Advanced Chart";
	}

	@Override
	public String getEditorTypeId() {
		return TYPE_2D_ADVANCED;
	}

	// adv chart is all manual, so no auto fill
	@Override
	public void fillDefaultFields() {
		// TODO Auto-generated method stub

	}

}
