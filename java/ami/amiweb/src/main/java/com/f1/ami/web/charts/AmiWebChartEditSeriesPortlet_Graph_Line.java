package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_Graph_Line extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_Graph> {

	public AmiWebChartEditSeriesPortlet_Graph_Line(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_Graph series = this.existing;
		addTitleField("Axis");
		addRequiredField(series.getXField());
		addRequiredField(series.getYField());
		addTitleField("Markers");
		addField(series.getmHeightFormula()).setTitle("Marker Size(px):");
		addField(series.getmColorFormula());
		addField(series.getmShapeFormula());
		addField(series.getmBorderColorFormula());
		addField(series.getmBorderSizeFormula());
		addTitleField("Lines");
		addField(series.getLineSizeFormula()).setTitle("Line Size(px):");
		addField(series.getLineColorFormula());
		addField(series.getLineDashFormula()); // not necessary (nc) 
		addField(series.getLineTypeFormula()); // already has a default
		addTitleField("Options");
		addField(series.getNameFormula()); // nc
		addField(series.getOrderByFormula()); // nc

		addPredefined(series.getmWidthFormula()).setCopyFromFormula(series.getmHeightFormula());
		addPredefined(series.getTooltipFormula()).setCopyFromFormula(series.getNameFormula());
	}
	@Override
	public void updateFields() {
		super.updateFields();
	}
	@Override
	public String getEditorLabel() {
		return "Line Chart";
	}
	@Override
	public String getEditorTypeId() {
		return TYPE_2D_LINE;
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
	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_Graph series = this.existing;
		getEditor(series.getmHeightFormula()).setValueIfNotPopulated("3");
		getEditor(series.getLineSizeFormula()).setValueIfNotPopulated("1");
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"circle\"");
		getEditor(series.getmColorFormula()).setValueIfNotPopulated();
		getEditor(series.getLineColorFormula()).setValueIfNotPopulated();
		getEditor(series.getmBorderColorFormula()).setValueIfNotPopulated();
		getEditor(series.getmBorderSizeFormula()).setValueIfNotPopulated("1");

	};
}
