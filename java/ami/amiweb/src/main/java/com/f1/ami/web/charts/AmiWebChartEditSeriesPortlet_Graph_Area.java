package com.f1.ami.web.charts;

import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.portal.PortletConfig;

public class AmiWebChartEditSeriesPortlet_Graph_Area extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_Graph> {

	public AmiWebChartEditSeriesPortlet_Graph_Area(PortletConfig config) {
		super(config);
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_Graph series = this.existing;
		addTitleField("Axis");
		addRequiredField(series.getXField());
		addRequiredField(series.getYField()).setTitle("Top:");
		addRequiredField(series.getY2Field()).setTitle("Bottom:");
		//		addTitleField("Options");
		// group by
		//		addField(series.getNameFormula());
		//		addField(series.getOrderByFormula());
		addTitleField("Line");
		addField(series.getLineSizeFormula());
		addField(series.getLineColorFormula());
		addPredefined(series.getLine2ColorFormula()).setCopyFromFormula(series.getLineColorFormula());
		addPredefined(series.getFillColorFormula()).setCopyFromFormula(series.getLineColorFormula());//, "brighten(this.getStyle(\"seriesCls\", __series_num), 0.5d)");
		addPredefined(series.getTooltipFormula()).setCopyFromFormula(series.getNameFormula());
		addPredefined(series.getX2Field()).setCopyFromFormula(series.getXField());
		addPredefined(series.getLine2SizeFormula()).setCopyFromFormula(series.getLineSizeFormula());
	}
	@Override
	public String getEditorLabel() {
		return "Area Chart";
	}
	@Override
	public String getEditorTypeId() {
		return TYPE_2D_AREA;
	}

	@Override
	public void updateFields() {
		super.updateFields();
	}

	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_Graph> container) {
		AmiWebChartRenderingLayer_Graph layer = (AmiWebChartRenderingLayer_Graph) container;
		AmiWebChartAxisPortlet xAxis = layer.getXAxis();
		AmiWebChartAxisPortlet yAxis = layer.getYAxis();
		xAxis.setTitle(AmiWebUtils.toPrettyName(this.existing.getXField().getValue()));
		yAxis.setTitle(AmiWebUtils.toPrettyName(this.existing.getYField().getValue()));

	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_Graph series = this.existing;
		getEditor(series.getLineSizeFormula()).setValueIfNotPopulated("1");
		getEditor(series.getLineColorFormula()).setValueIfNotPopulated();
	};
}
