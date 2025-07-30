package com.f1.ami.web.charts;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Row;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebChartSeries_RadialGraph extends AmiWebChartSeries {

	final private AmiWebChartFormula_Simple xPosFormula;
	final private AmiWebChartFormula_Simple yPosFormula;
	final private AmiWebChartFormula_Simple descriptionFormula;

	final private AmiWebChartFormula_Color lineColorFormula;
	final private AmiWebChartFormula_Color mBorderColorFormula;
	final private AmiWebChartFormula_Color mColorFormula;
	final private AmiWebChartFormula_Simple mShapeFormula;
	final private AmiWebChartFormula_Simple topFormula;
	final private AmiWebChartFormula_Simple bottomFormula;
	final private AmiWebChartFormula_Simple leftFormula;
	final private AmiWebChartFormula_Simple rightFormula;
	final private AmiWebChartFormula_Simple tooltipFormula;
	final private AmiWebChartFormula_Simple x2PosFormula;
	final private AmiWebChartFormula_Simple y2PosFormula;
	final private AmiWebChartFormula_Simple selectableFormula;
	final private AmiWebChartFormula_Simple mWidthFormula;
	final private AmiWebChartFormula_Simple mHeightFormula;
	final private AmiWebChartFormula_Simple lineSizeFormula;
	final private AmiWebChartFormula_Color line2ColorFormula;
	final private AmiWebChartFormula_Simple line2SizeFormula;
	final private AmiWebChartFormula_Simple fillBorderSizeFormula;
	final private AmiWebChartFormula_Color fillBorderColorFormula;
	final private AmiWebChartFormula_Simple mBorderSizeFormula;
	final private AmiWebChartFormula_Color fillColorFormula;
	final private AmiWebChartFormula_Color descriptionColorFormula;
	final private AmiWebChartFormula_Simple descriptionPositionFormula;
	final private AmiWebChartFormula_Simple descriptionSizeFormula;
	final private AmiWebChartFormula_Simple horizontalStackOn;
	final private AmiWebChartFormula_Simple horizonalStackMin;
	final private AmiWebChartFormula_Simple horizonalStackMax;
	final private AmiWebChartFormula_Simple verticalStackOn;
	final private AmiWebChartFormula_Simple verticaltackMin;
	final private AmiWebChartFormula_Simple verticaltackMax;
	final private AmiWebChartFormula_Simple angleFormula;
	final private AmiWebChartFormula_Simple radiusFormula;
	//	protected final Set<String> fieldCheck = new HashSet<String>(
	//			Arrays.asList("xPos", "yPos", "descSz", "descColor", "desc", "descPos", "tooltip", "mShape", "mColor", "mWidth", "mHeight", "mBorderColor", "mBorderSize"));

	public AmiWebChartSeries_RadialGraph(AmiWebService service, AmiWebPortlet thiz, AmiWebDmTableSchema model, AmiWebChartRenderingLayer<?> layer) {
		super(service, thiz, model, AmiWebChartSeries.SERIES_TYPE_RADIAL, layer);
		startGroup("Axis");
		this.xPosFormula = addFormula("xPos", "Angle:", TYPE_NUMBER).setXBound();
		this.yPosFormula = addFormula("yPos", "Radius:", TYPE_NUMBER).setYBound();

		startGroup("Axis Label");
		this.angleFormula = addFormula("angleLabel", "Angle Axis Label:", TYPE_AXIS);
		this.radiusFormula = addFormula("raidusLabel", "Radius Axis Label:", TYPE_AXIS);

		startGroup("Labels");
		this.selectableFormula = addFormula("sel", "User Selectable:", TYPE_BOOLEAN);
		this.descriptionFormula = addFormula("desc", "Label:", TYPE_STRING);
		this.descriptionColorFormula = addColorFormula("descColor", "Label Color:");
		this.descriptionSizeFormula = addFormula("descSz", "Label Size:", TYPE_NUMBER);
		this.descriptionPositionFormula = addFormula("descPos", "Label Position:", TYPE_POSITION);
		this.tooltipFormula = addFormula("tooltip", "Hover over:", TYPE_STRING).setHidden();

		startGroup("Marker");
		this.mShapeFormula = addFormula("mShape", "Shape:", TYPE_SHAPE);
		this.mColorFormula = addColorFormula("mColor", "Color:");
		this.mWidthFormula = addFormula("mWidth", "Width (px):", TYPE_NUMBER);
		this.mHeightFormula = addFormula("mHeight", "Height (px):", TYPE_NUMBER);
		this.mBorderColorFormula = addColorFormula("mBorderColor", "Border Color:");
		this.mBorderSizeFormula = addFormula("mBorderSize", "Border Size (px):", TYPE_NUMBER);

		startGroup("Marker Position Override");
		this.topFormula = addFormula("mTop", "Outer Radius:", TYPE_NUMBER).setYBound();
		this.bottomFormula = addFormula("mBottom", "Inner Radius:", TYPE_NUMBER).setYBound();
		this.leftFormula = addFormula("mLeft", "Left Angle:", TYPE_NUMBER).setXBound();
		this.rightFormula = addFormula("mRight", "Right Angle:", TYPE_NUMBER).setXBound();

		startGroup("Angle Stack");
		this.horizontalStackOn = addFormula("hStackOn", "Stack On:", TYPE_NUMBER);
		this.horizonalStackMin = addFormula("hStackMin", "Stack Min (const):", TYPE_CONST);
		this.horizonalStackMax = addFormula("hStackMax", "Stack Max (const):", TYPE_CONST);

		startGroup("Radius Stack");
		this.verticalStackOn = addFormula("vStackOn", "Stack On:", TYPE_NUMBER);
		this.verticaltackMin = addFormula("vStackMin", "Stack Min (const):", TYPE_CONST);
		this.verticaltackMax = addFormula("vStackMax", "Stack Max (const):", TYPE_CONST);

		startGroup("Lines");
		this.lineColorFormula = addColorFormula("lineColor", "Line Color:");
		this.lineSizeFormula = addFormula("lineSize", "Line Size (px):", TYPE_NUMBER);

		startGroup("Area");
		this.x2PosFormula = addFormula("x2Pos", "Angle2:", TYPE_NUMBER).setXBound();
		this.y2PosFormula = addFormula("y2Pos", "Radius2:", TYPE_NUMBER).setYBound();
		this.line2ColorFormula = addColorFormula("line2Color", "Line2 Color:");
		this.line2SizeFormula = addFormula("line2Size", "Line2 Size (px):", TYPE_NUMBER);
		this.fillBorderColorFormula = addColorFormula("fillBorderColor", "Border Color:");
		this.fillBorderSizeFormula = addFormula("fillBorderSize", "Border Size:", TYPE_NUMBER);
		this.fillColorFormula = addColorFormula("fillColor", "Fill Color:");
	}

	public AmiWebChartFormula_Simple getXField() {
		return this.xPosFormula;
	}
	public AmiWebChartFormula_Simple getYField() {
		return this.yPosFormula;
	}
	public AmiWebChartFormula_Simple getX2Field() {
		return this.x2PosFormula;
	}
	public AmiWebChartFormula_Simple getY2Field() {
		return this.y2PosFormula;
	}
	public AmiWebChartFormula_Simple getLeftField() {
		return this.leftFormula;
	}
	public AmiWebChartFormula_Simple getRightField() {
		return this.rightFormula;
	}
	public AmiWebChartFormula_Simple getTopField() {
		return this.topFormula;
	}
	public AmiWebChartFormula_Simple getBottomField() {
		return this.bottomFormula;
	}

	public AmiWebChartFormula_Simple getSelectableFormula() {
		return selectableFormula;
	}

	public AmiWebChartFormula_Simple getDescriptionFormula() {
		return descriptionFormula;
	}

	public AmiWebChartFormula_Simple getTooltipFormula() {
		return tooltipFormula;
	}
	public AmiWebChartFormula_Color getDescriptionColorFormula() {
		return descriptionColorFormula;
	}
	public AmiWebChartFormula_Simple getDescriptionSizeFormula() {
		return descriptionSizeFormula;
	}

	public AmiWebChartFormula_Simple getmShapeFormula() {
		return mShapeFormula;
	}

	public AmiWebChartFormula_Color getmColorFormula() {
		return mColorFormula;
	}

	public AmiWebChartFormula_Simple getmWidthFormula() {
		return mWidthFormula;
	}

	public AmiWebChartFormula_Simple getmHeightFormula() {
		return mHeightFormula;
	}

	public AmiWebChartFormula_Color getLineColorFormula() {
		return lineColorFormula;
	}

	public AmiWebChartFormula_Simple getLineSizeFormula() {
		return lineSizeFormula;
	}

	public AmiWebChartFormula_Color getLine2ColorFormula() {
		return line2ColorFormula;
	}

	public AmiWebChartFormula_Simple getLine2SizeFormula() {
		return line2SizeFormula;
	}

	public AmiWebChartFormula_Simple getFillBorderSizeFormula() {
		return fillBorderSizeFormula;
	}
	public AmiWebChartFormula_Color getFillBorderColorFormula() {
		return fillBorderColorFormula;
	}

	public AmiWebChartFormula_Color getmBorderColorFormula() {
		return mBorderColorFormula;
	}

	public AmiWebChartFormula_Simple getmBorderSizeFormula() {
		return mBorderSizeFormula;
	}

	public AmiWebChartFormula_Color getFillColorFormula() {
		return fillColorFormula;
	}

	@Override
	public String getLegendLineColor(Grouping group) {
		return Caster_String.INSTANCE.castNoThrow(getFirst(group, getLineColorFormula()));
	}

	@Override
	public String getLegendShapeColor(Grouping group) {
		return Caster_String.INSTANCE.castNoThrow(getFirst(group, getmColorFormula()));
	}

	@Override
	public String getLegendShapeBorderColor(Grouping group) {
		return Caster_String.INSTANCE.castNoThrow(getFirst(group, getmBorderColorFormula()));
	}

	@Override
	public String getLegendShape(Grouping group) {
		return Caster_String.INSTANCE.castNoThrow(getFirst(group, getmShapeFormula()));
	}
	@Override
	public Integer getLegendLineDash(Grouping group) {
		return null;
	}
	@Override
	public Integer getLegendLineSize(Grouping group) {
		return Caster_Integer.INSTANCE.cast(getFirst(group, getLineSizeFormula()));
	}
	@Override
	public String describe(Row row, CalcFrameStack sf) {
		tmpWrapper.reset(row, row.getLocation());
		return AmiUtils.snn(this.getDescriptionFormula().getData(new ReusableCalcFrameStack(sf, tmpWrapper)), "");
	}
	@Override
	public String getTooltip(Row row, CalcFrameStack sf) {
		tmpWrapper.reset(row, row.getLocation());
		return AmiUtils.snn(this.getTooltipFormula().getData(new ReusableCalcFrameStack(sf, tmpWrapper)), "");
	}

	public AmiWebChartFormula_Simple getDescriptionPositionFormula() {
		return descriptionPositionFormula;
	}

	public AmiWebChartFormula_Simple getHorizontalStackOn() {
		return this.horizontalStackOn;
	}

	public AmiWebChartFormula_Simple getHorizontalStackMin() {
		return this.horizonalStackMin;
	}

	public AmiWebChartFormula_Simple getHorizontalStackMax() {
		return this.horizonalStackMax;
	}
	public AmiWebChartFormula_Simple getVerticalStackOn() {
		return this.verticalStackOn;
	}

	public AmiWebChartFormula_Simple getVerticalStackMin() {
		return this.verticaltackMin;
	}

	public AmiWebChartFormula_Simple getVerticalStackMax() {
		return this.verticaltackMax;
	}

	@Override
	protected void postProcess(Grouping group) {
		stack(this.horizontalStackOn, this.horizonalStackMin, this.horizonalStackMax, this.leftFormula, this.rightFormula, group);
		stack(this.verticalStackOn, this.verticaltackMin, this.verticaltackMax, this.bottomFormula, this.topFormula, group);
		super.postProcess(group);
	}

	@Override
	public AmiWebChartRenderingLayer<?> getLayer() {
		return (AmiWebChartRenderingLayer<?>) super.getLayer();
	}

	@Override
	public void fireOnFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		super.fireOnFormulaChanged(formula, old, nuw);
		getLayer().getPlot().flagFormulaChanged();
	}

	public AmiWebFormula getAngleFormula() {
		return this.angleFormula;
	}
	public AmiWebFormula getRadiusFormula() {
		return this.radiusFormula;
	}
}
