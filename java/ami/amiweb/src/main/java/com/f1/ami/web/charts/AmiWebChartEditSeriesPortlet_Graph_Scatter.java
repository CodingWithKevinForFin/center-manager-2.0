package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_Graph_Scatter extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_Graph> {

	public AmiWebChartEditSeriesPortlet_Graph_Scatter(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_Graph series = this.existing;

		addTitleField("Axis");
		addRequiredField(series.getXField());
		addRequiredField(series.getYField());
		addTitleField("Options");
		addField(series.getNameFormula());
		addField(series.getOrderByFormula());

		addTitleField("Markers");
		addField(series.getmShapeFormula());
		addField(series.getmColorFormula());
		addRequiredField(series.getmHeightFormula()).setTitle("Marker Size(px):");
		addPredefined(series.getmWidthFormula()).setCopyFromFormula(series.getmHeightFormula());

	}
	@Override
	public String getEditorLabel() {
		return "Scatter Plot";
	}
	@Override
	public String getEditorTypeId() {
		return TYPE_2D_SCATTER;
	}

	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_Graph> container) {
		AmiWebChartRenderingLayer_Graph layer = (AmiWebChartRenderingLayer_Graph) container;
		AmiWebChartAxisPortlet xAxis = layer.getXAxis();
		AmiWebChartAxisPortlet yAxis = layer.getYAxis();
		xAxis.setTitle(AmiWebUtils.toPrettyName(this.existing.getXField().getValue()));
		yAxis.setTitle(AmiWebUtils.toPrettyName(this.existing.getYField().getValue()));

		String name = this.existing.getFormula("name").getValue();
		AmiWebChartFormula xPosFormula = this.existing.getFormula("xPos");
		AmiWebChartFormula yPosFormula = this.existing.getFormula("yPos");
		String xPos = xPosFormula.getValue();
		String yPos = yPosFormula.getValue();
		this.existing.getFormula("tooltip")
				.setValue((name == null ? "" : name + "+\"<BR>\"+") + "\"X: \"+" + (xPos == null ? xPos : getDefaultNumberFormatFormula(xPos, xPosFormula)) + "+\"<BR>Y: \"+"
						+ (yPos == null ? yPos : getDefaultNumberFormatFormula(yPos, yPosFormula)));
	};

	@Override
	public void updateFields() {
		super.updateFields();
	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_Graph series = this.existing;
		getEditor(series.getmHeightFormula()).setValueIfNotPopulated("3");
		getEditor(series.getmWidthFormula()).setValueIfNotPopulated("3");
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"square\"");
		getEditor(series.getmColorFormula()).setValueIfNotPopulated();

	}
}
