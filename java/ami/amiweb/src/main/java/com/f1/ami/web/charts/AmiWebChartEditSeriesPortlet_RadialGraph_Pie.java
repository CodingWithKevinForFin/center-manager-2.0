package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_RadialGraph_Pie extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_RadialGraph> {

	public AmiWebChartEditSeriesPortlet_RadialGraph_Pie(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_RadialGraph series = this.existing;
		addTitleField("Data Series");
		addRequiredField(series.getHorizontalStackOn()).setTitle("Data:");
		addField(series.getDescriptionFormula()).setTitle("Data Label:");
		addField(series.getOrderByFormula());
		addField(series.getTooltipFormula());
		addTitleField("Color");
		addField(series.getmColorFormula());
		addTitleField("Angle Stack");
		addField(series.getHorizontalStackMin()).setTitle("Maximum Angle:");
		addField(series.getHorizontalStackMax()).setTitle("Minimum Angle:");
		addTitleField("Radius (optional)");
		// outer radius
		addField(series.getTopField());
		// inner radius
		addField(series.getBottomField());
		// marker shape must be "wedge"
		addPredefined(series.getmShapeFormula());
	}
	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_RadialGraph> container) {
		this.existing.getFormula("mTop").setValue("1");
	}

	@Override
	public void updateFields() {
		super.updateFields();
	}

	@Override
	public String getEditorLabel() {
		return "Radial Pie Chart";
	}

	@Override
	public String getEditorTypeId() {
		return TYPE_RADIAL_PIE;
	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_RadialGraph series = this.existing;
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"wedge\"");
		getEditor(series.getTopField()).overrideValue("1");
		getEditor(series.getmColorFormula()).setSeriesIfNotPopulated("__row_num");
		getEditor(series.getBottomField()).setValueIfNotPopulated("0");
		getEditor(series.getHorizontalStackMin()).setValueIfNotPopulated("0");
		getEditor(series.getHorizontalStackMax()).setValueIfNotPopulated("360");
		//		getEditor(series.getTopField()).getFormula().setValue("1");
		String desc = getEditor(series.getDescriptionFormula()).getValue();
		String hStackOn = getEditor(series.getHorizontalStackOn()).getValue();
		String toolTipText = (desc == "" || "\"\"".equals(desc) ? "" : desc + "+\"<BR>\"+") + (hStackOn == "" ? hStackOn
				: "formatNumber(" + hStackOn + ", \"#.000\", \"\")+\"<BR>(\"+formatNumber((" + hStackOn + "*100d)/sum(" + hStackOn + "), \"#.000\", \"\")+\" %)\"");
		getEditor(series.getTooltipFormula()).setValueIfNotPopulated(toolTipText);

	}
}
