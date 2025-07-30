package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_RadialGraph_Speedometer extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_RadialGraph> {

	public AmiWebChartEditSeriesPortlet_RadialGraph_Speedometer(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_RadialGraph series = this.existing;

		addTitleField("Data Series");
		addRequiredField(series.getXField()).setTitle("Value:");
		addField(series.getDescriptionFormula()).setTitle("Name:");
		addField(series.getmColorFormula()).setTitle("Color:");

		addPredefined(series.getmShapeFormula());

		addPredefined(series.getmWidthFormula());
		addPredefined(series.getmBorderColorFormula()).setCopyFromFormula(series.getmColorFormula());
		addPredefined(series.getmBorderSizeFormula());

		addPredefined(series.getTopField());
		addPredefined(series.getBottomField());
	}

	@Override
	public void updateFields() {
		super.updateFields();
		AmiWebChartSeries_RadialGraph series = this.existing;
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"wedge\"");

		getEditor(series.getmWidthFormula()).setValueIfNotPopulated("1");
		getEditor(series.getmBorderColorFormula());
		getEditor(series.getmBorderSizeFormula()).setValueIfNotPopulated("1");

		getEditor(series.getTopField()).setValueIfNotPopulated("1");
		getEditor(series.getBottomField()).setValueIfNotPopulated("0");
	}
	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_RadialGraph> container) {
		String desc = this.existing.getFormula("desc").getValue();
		String xPos = this.existing.getFormula("xPos").getValue();
		this.existing.getFormula("tooltip").setValue((desc == null ? "" : desc + "+\"<BR>\"+") + xPos);
	}

	@Override
	public String getEditorLabel() {
		return "Radial Speedometer";
	}

	@Override
	public String getEditorTypeId() {
		return TYPE_RADIAL_SPEEDOMETER;
	}

	@Override
	public void fillDefaultFields() {
		// TODO Auto-generated method stub

	}
}
