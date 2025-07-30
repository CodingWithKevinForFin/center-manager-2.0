package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_RadialGraph_Bar extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_RadialGraph> {
	//	protected final Set<String> fieldCheck = new HashSet<String>(
	//			Arrays.asList("xPos", "yPos", "descSz", "descColor", "desc", "descPos", "tooltip", "mShape", "mColor", "mWidth", "mHeight", "mBorderColor", "mBorderSize"));

	public AmiWebChartEditSeriesPortlet_RadialGraph_Bar(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_RadialGraph series = this.existing;
		addTitleField("Data Series");
		// outer radius
		addRequiredField(series.getTopField()).setTitle("Data:");
		addField(series.getDescriptionFormula()).setTitle("Data Label:");
		addField(series.getOrderByFormula());
		addField(series.getTooltipFormula());
		addTitleField("Color");
		addField(series.getmColorFormula()); // marker color
		addTitleField("Angle Stack");
		addField(series.getHorizontalStackMin()).setTitle("Maximum Angle:");
		addField(series.getHorizontalStackMax()).setTitle("Minimum Angle:");
		addPredefined(series.getmShapeFormula()); // marker shape
		addPredefined(series.getBottomField()); // inner radius
		addPredefined(series.getHorizontalStackOn()).setCopyFromFormula(series.getTopField()); // stack on
	}
	@Override
	public void updateFields() {
		super.updateFields();
	}
	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_RadialGraph> container) {
	}

	@Override
	public String getEditorLabel() {
		return "Radial Bar Chart";
	}
	@Override
	public String getEditorTypeId() {
		return TYPE_RADIAL_BAR;
	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_RadialGraph series = this.existing;
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"wedge\"");
		getEditor(series.getmColorFormula()).setSeriesIfNotPopulated("__row_num");
		getEditor(series.getBottomField()).setValueIfNotPopulated("0");
		getEditor(series.getHorizontalStackMin()).setValueIfNotPopulated("0");
		getEditor(series.getHorizontalStackMax()).setValueIfNotPopulated("360");
		String desc = getEditor(series.getDescriptionFormula()).getValue();
		String mTop = getEditor(series.getTopField()).getValue();
		String toolTipText = (desc == "" ? "" : desc + "+\"<BR>\"+") + (mTop == null ? mTop : "formatNumber(" + mTop + ", \"#.000\", \"\")");
		getEditor(series.getTooltipFormula()).setValueIfNotPopulated(toolTipText);
	}
}
