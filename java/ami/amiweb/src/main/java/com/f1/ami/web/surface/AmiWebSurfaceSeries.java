package com.f1.ami.web.surface;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebChartFormula;
import com.f1.ami.web.charts.AmiWebChartFormula_Color;
import com.f1.ami.web.charts.AmiWebChartSeries;
import com.f1.ami.web.charts.AmiWebSurfaceRenderingLayer;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebSurfaceSeries extends AmiWebChartSeries {

	public static final byte TYPE_XYZ = 1;
	public static final byte TYPE_SURFACE = 2;
	private byte type = TYPE_XYZ;
	final private AmiWebChartFormula xPosFormula;
	final private AmiWebChartFormula yPosFormula;
	final private AmiWebChartFormula descriptionFormula;

	final private AmiWebChartFormula_Color lineColorFormula;
	final private AmiWebChartFormula_Color mColorFormula;
	final private AmiWebChartFormula mShapeFormula;
	final private AmiWebChartFormula topFormula;
	final private AmiWebChartFormula bottomFormula;

	final private AmiWebChartFormula leftFormula;
	final private AmiWebChartFormula rightFormula;
	final private AmiWebChartFormula backFormula;
	final private AmiWebChartFormula frontFormula;

	final private AmiWebChartFormula tooltipFormula;
	final private AmiWebChartFormula zPosFormula;
	final private AmiWebChartFormula selectableFormula;

	final private AmiWebChartFormula mWidthFormula;
	final private AmiWebChartFormula mHeightFormula;
	final private AmiWebChartFormula mDepthFormula;

	final private AmiWebChartFormula lineSizeFormula;
	private AmiWebSurfacePortlet owner;

	public AmiWebSurfaceSeries(AmiWebService service, AmiWebSurfacePortlet owner, AmiWebDmTableSchema model, AmiWebSurfaceRenderingLayer value) {
		super(service, owner, model, SERIES_TYPE_SURFACE, value);
		this.owner = owner;
		startGroup("Axis");
		this.xPosFormula = addFormula("xPos", "X:", TYPE_NUMBER).setXBound();
		this.yPosFormula = addFormula("yPos", "Y:", TYPE_NUMBER).setYBound();
		this.zPosFormula = addFormula("zPos", "Z:", TYPE_NUMBER);

		startGroup("Labels");
		this.selectableFormula = (addFormula("sel", "User Selectable:", TYPE_BOOLEAN));
		this.descriptionFormula = addFormula("desc", "Description:", TYPE_STRING);
		this.tooltipFormula = addFormula("tooltip", "Hover Over:", TYPE_STRING).setHidden();

		startGroup("Markers");
		this.mShapeFormula = addFormula("mShape", "Shape:", TYPE_SHAPE);
		this.mColorFormula = addColorFormula("mColor", "Color:");
		this.mWidthFormula = addFormula("mWidth", "Width(px):", TYPE_NUMBER);
		this.mHeightFormula = addFormula("mHeight", "Height(px):", TYPE_NUMBER);
		this.mDepthFormula = addFormula("mDepth", "Depth(px):", AmiWebChartSeries.TYPE_NUMBER);

		startGroup("Marker Position Override");
		this.topFormula = addFormula("mTop", "Top:", TYPE_NUMBER).setYBound();
		this.bottomFormula = addFormula("mBottom", "Bottom:", TYPE_NUMBER).setYBound();
		this.leftFormula = addFormula("mLeft", "Left:", TYPE_NUMBER).setXBound();
		this.rightFormula = addFormula("mRight", "Right:", TYPE_NUMBER).setXBound();
		this.frontFormula = addFormula("mFront", "Front:", AmiWebChartSeries.TYPE_NUMBER);
		this.backFormula = addFormula("mBack", "Back:", AmiWebChartSeries.TYPE_NUMBER);

		startGroup("Lines");
		this.lineColorFormula = addColorFormula("lineColor", "Line Color:");
		this.lineSizeFormula = addFormula("lineSize", "Line Size(px):", TYPE_NUMBER);

	}
	@Override
	public void setDatamodel(String dmId, String dmTableName) {
		String oldDmId = super.getDmAliasDotName();
		String oldTableName = super.getDmTableName();
		super.setDatamodel(dmId, dmTableName);
		if (this.owner != null)
			this.owner.onUsedDmChanged(oldDmId, oldTableName, this);
	}
	public void setType(byte type) {
		this.type = type;
	}

	public byte getType() {
		return this.type;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("type", type);
		r.put("dmadn", this.getDmAliasDotName());
		r.put("tblid", this.getDmTableName());
		return r;
	}

	@Override
	public void init(Map<String, Object> values) {
		this.type = CH.getOr(Caster_Byte.PRIMITIVE, values, "type", AmiWebSurfaceSeries.TYPE_XYZ);
		super.init(values);
	}
	public AmiWebChartFormula getXField() {
		return this.xPosFormula;
	}
	public AmiWebChartFormula getYField() {
		return this.yPosFormula;
	}
	public AmiWebChartFormula getLeftField() {
		return this.leftFormula;
	}
	public AmiWebChartFormula getRightField() {
		return this.rightFormula;
	}
	public AmiWebChartFormula getTopField() {
		return this.topFormula;
	}
	public AmiWebChartFormula getBottomField() {
		return this.bottomFormula;
	}

	public AmiWebChartFormula getSelectableFormula() {
		return selectableFormula;
	}

	public AmiWebChartFormula getDescriptionFormula() {
		return descriptionFormula;
	}

	public AmiWebChartFormula getTooltipFormula() {
		return tooltipFormula;
	}

	public AmiWebChartFormula getmShapeFormula() {
		return mShapeFormula;
	}

	public AmiWebChartFormula_Color getmColorFormula() {
		return mColorFormula;
	}

	public AmiWebChartFormula getmWidthFormula() {
		return mWidthFormula;
	}

	public AmiWebChartFormula getmHeightFormula() {
		return mHeightFormula;
	}

	public AmiWebChartFormula_Color getLineColorFormula() {
		return lineColorFormula;
	}

	public AmiWebChartFormula getLineSizeFormula() {
		return lineSizeFormula;
	}

	public AmiWebChartFormula getmDepthField() {
		return mDepthFormula;
	}
	public AmiWebChartFormula getBackField() {
		return backFormula;
	}
	public AmiWebChartFormula getFrontField() {
		return frontFormula;
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
		return Caster_String.INSTANCE.castNoThrow(getFirst(group, getmColorFormula()));
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
		return Caster_Integer.INSTANCE.castNoThrow(getFirst(group, getLineSizeFormula()));
	}
	@Override
	public String describe(Row row, CalcFrameStack sf) {
		return AmiUtils.snn(this.getDescriptionFormula().getData(new ReusableCalcFrameStack(sf, row)), "");
	}
	@Override
	public String getTooltip(Row row, CalcFrameStack sf) {
		return AmiUtils.snn(this.getTooltipFormula().getData(new ReusableCalcFrameStack(sf, row)), "");
	}
	public AmiWebChartFormula getZField() {
		return this.zPosFormula;
	}

	@Override
	public AmiWebSurfaceRenderingLayer getLayer() {
		return (AmiWebSurfaceRenderingLayer) super.getLayer();
	}

	@Override
	public void fireOnFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		super.fireOnFormulaChanged(formula, old, nuw);
		getLayer().getChart().flagViewStale();
	}

}
