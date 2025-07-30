package com.f1.ami.web.charts;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.utils.OH;

public class AmiWebChartEditSeriesPortlet_Graph_Bar extends AmiWebChartEditSeriesPortlet<AmiWebChartSeries_Graph> {

	private final boolean verticalBars;

	public AmiWebChartEditSeriesPortlet_Graph_Bar(PortletConfig config, boolean verticalBars) {
		super(config);
		this.verticalBars = verticalBars;
	}

	@Override
	protected void initForm() {
		AmiWebChartSeries_Graph series = this.existing;

		addTitleField("Axis");
		AmiWebChartFormula_Simple categoryLabelField;
		AmiWebChartFormula_Simple valStackOn;
		if (this.verticalBars) {
			categoryLabelField = series.getXLabelField();
			valStackOn = series.getVerticalStackOn();
			addRequiredField(categoryLabelField).setTitle("X:");
			addRequiredField(series.getVerticalStackOn()).setTitle("Y:");
		} else {
			categoryLabelField = series.getYLabelField();
			valStackOn = series.getHorizontalStackOn();
			addRequiredField(categoryLabelField).setTitle("Y:");
			addRequiredField(valStackOn).setTitle("X:");
		}
		addTitleField("Markers");
		addField(series.getmShapeFormula());
		addField(series.getmColorFormula());
		addPredefined(series.getmBorderColorFormula());
		addPredefined(series.getmBorderSizeFormula());

		if (this.verticalBars) {
			addPredefined(series.getLeftField());
			addPredefined(series.getRightField());
		} else {
			addPredefined(series.getTopField());
			addPredefined(series.getBottomField());
		}
		addPredefined(series.getNameFormula()).setCopyFromFormula(categoryLabelField);
		addPredefined(series.getOrderByFormula()).setCopyFromFormula(valStackOn);
	}

	@Override
	public void updateFields() {
		super.updateFields();
	}

	@Override
	public String getEditorLabel() {
		return "Bar Chart";
	}
	@Override
	public String getEditorTypeId() {
		return this.verticalBars ? TYPE_2D_BAR_V : TYPE_2D_BAR_H;
	}

	@Override
	protected void prepareContainer(AmiWebChartSeriesContainer<AmiWebChartSeries_Graph> container) {
		AmiWebChartFormula categoryFormula = this.verticalBars ? this.existing.getXLabelField() : this.existing.getYLabelField();
		String categoryLbl = categoryFormula.getValue();
		Class<?> categoryLblType = categoryFormula.getReturnType();
		AmiWebChartFormula valFormula = this.verticalBars ? this.existing.getVerticalStackOn() : this.existing.getHorizontalStackOn();
		String valLbl = valFormula.getValue();
		this.existing.getFormula("tooltip").setValue("\"" + (this.verticalBars ? "X" : "Y") + ": \"+"
				+ ((categoryLbl == null || !(OH.isAssignableFrom(Number.class, categoryLblType))) ? categoryLbl : getDefaultNumberFormatFormula(categoryLbl, categoryFormula))
				+ "+\"<BR>" + (this.verticalBars ? "Y" : "X") + ": \"+" + (valLbl == null ? valLbl : getDefaultNumberFormatFormula(valLbl, valFormula)));
	}

	@Override
	public void fillDefaultFields() {
		AmiWebChartSeries_Graph series = this.existing;
		getEditor(series.getmShapeFormula()).setValueIfNotPopulated("\"square\"");
		//		getEditor(series.getmBorderSizeFormula()).setValueIfNotPopulated("1");

		if (this.verticalBars) {
			getEditor(series.getLeftField()).setValueIfNotPopulated(".1");
			getEditor(series.getRightField()).setValueIfNotPopulated(".9");
		} else {
			getEditor(series.getTopField()).setValueIfNotPopulated(".9");
			getEditor(series.getBottomField()).setValueIfNotPopulated(".1");
		}
		getEditor(series.getmColorFormula()).setValueIfNotPopulated();
	};
}
