package com.f1.ami.web.surface;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebAbstractDmPortlet;
import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDmPortletBuilder;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebStyledScrollbarPortlet;
import com.f1.ami.web.charts.AmiWebChartEditSeriesPortlet;
import com.f1.ami.web.charts.AmiWebChartFormula_Color;
import com.f1.ami.web.charts.AmiWebChartSeries;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.charts.AmiWebSurfaceRenderingLayer;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_3dChart;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.chart.Basic3dPortlet;
import com.f1.suite.web.portal.impl.chart.Basic3dPortlet.Line;
import com.f1.suite.web.portal.impl.chart.Basic3dPortlet.Text;
import com.f1.suite.web.portal.impl.chart.Basic3dPortlet.Triangle;
import com.f1.suite.web.portal.impl.chart.Basic3dPortletListener;
import com.f1.suite.web.portal.impl.chart.TdHelper;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.ColorHelper;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.math.tridt.DtPoint;
import com.f1.utils.math.tridt.DtTriangle;
import com.f1.utils.math.tridt.DtTriangulation;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebSurfacePortlet extends AmiWebAbstractDmPortlet implements FormPortletListener, Basic3dPortletListener, ConfirmDialogListener, ColorPickerListener,
		WebMenuListener, FormPortletContextMenuListener, AmiWebStyledScrollbarPortlet {

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebSurfacePortlet> implements AmiWebDmPortletBuilder<AmiWebSurfacePortlet> {

		public static final String ID = "amisurface";

		public Builder() {
			super(AmiWebSurfacePortlet.class);
		}

		@Override
		public AmiWebSurfacePortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebSurfacePortlet r = new AmiWebSurfacePortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "3D Surface";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			List<Map<String, Object>> series = (List<Map<String, Object>>) portletConfig.get("series");
			List<String> r = new ArrayList<String>(series.size());
			for (int i = 0; i < series.size(); i++)
				r.add((String) series.get(i).get("dmadn"));
			return r;
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			List<Map<String, Object>> series = (List<Map<String, Object>>) portletConfig.get("series");
			series.get(position).put("dmadn", name);
		}

	}

	private static final int MAX_QUICK_VIEW = 5;

	public static final byte AXIS_LOCK_NONE = 0;
	public static final byte AXIS_LOCK_XYZ = 1;
	public static final byte AXIS_LOCK_XY = 2;
	public static final byte AXIS_LOCK_XZ = 3;
	public static final byte AXIS_LOCK_YZ = 4;
	private static final int CONTROL_BAR_HEIGHT = 50;
	private static final int CONTROL_SLIDER_WIDTH = 85;

	private static final int DEFAULT_COLOR = 0;//black

	private Basic3dPortlet threeDeePortlet;
	private FormPortletNumericRangeField xRotField;
	private FormPortletNumericRangeField yRotField;
	private FormPortletNumericRangeField zRotField;
	private FormPortletNumericRangeField zoomField;
	private FormPortletNumericRangeField fovField;
	private FormPortletNumericRangeField xPosField;
	private FormPortletNumericRangeField yPosField;
	private FormPortletButtonField fButton;
	private FormPortletButtonField bButton;
	private FormPortletButtonField lButton;
	private FormPortletButtonField rButton;
	private FormPortletButtonField tButton;
	private FormPortletButtonField uButton;
	private FormPortlet controlPanelForm;
	private List<String> colorSeries;
	private List<Color> colorSeriesColors;
	private ColorGradient colorGradient;

	final private AmiWebSurfaceAxisPortlet axisX;
	final private AmiWebSurfaceAxisPortlet axisY;
	final private AmiWebSurfaceAxisPortlet axisZ;

	private AmiWebSurfaceAxisPortlet[] axes;

	private boolean isViewStale;

	private String bgColor;
	private String selColor;
	private String labelColor;
	private String controlButtonsColor = "#00ffff";

	private byte axisLock;

	private boolean controlsVisible = true;
	final private PortletStyleManager_Form buttonFormStyle = getService().getUserFormStyleManager().clone();

	public AmiWebSurfacePortlet(PortletConfig config) {
		super(config);
		setChild(this.threeDeePortlet = new Basic3dPortlet(generateConfig()));
		this.threeDeePortlet.addListener(this);
		prepareForms();

		this.axisX = new AmiWebSurfaceAxisPortlet(this, AmiWebSurfaceAxisPortlet.DIMENSION_X);
		this.axisY = new AmiWebSurfaceAxisPortlet(this, AmiWebSurfaceAxisPortlet.DIMENSION_Y);
		this.axisZ = new AmiWebSurfaceAxisPortlet(this, AmiWebSurfaceAxisPortlet.DIMENSION_Z);
		this.axes = new AmiWebSurfaceAxisPortlet[] { this.axisX, this.axisY, this.axisZ };

		// not sure what these two lines are for as they aren't doing anything. Should have used getGridPortlet() first		
		//		setRowSize(1, this.isControlsVisible() ? CONTROL_BAR_HEIGHT : 0);
		//		setRowSize(2, this.isControlsVisible() ? CONTROL_BAR_HEIGHT : 0);
		getStylePeer().initStyle();
		this.axisX.addToDomManager();
		this.axisY.addToDomManager();
		this.axisZ.addToDomManager();
	}
	private void prepareForms() {

		this.fButton = new FormPortletButtonField("").setValue("F");
		this.bButton = new FormPortletButtonField("").setValue("B");
		this.lButton = new FormPortletButtonField("").setValue("L");
		this.rButton = new FormPortletButtonField("").setValue("R");
		this.tButton = new FormPortletButtonField("").setValue("T");
		this.uButton = new FormPortletButtonField("").setValue("U");
		xRotField = new FormPortletNumericRangeField("X:", -180, 180, 0).setValue(threeDeePortlet.getRotX()).setWidth(CONTROL_SLIDER_WIDTH);
		yRotField = new FormPortletNumericRangeField("Y:", -180, 180, 0).setValue(threeDeePortlet.getRotY()).setWidth(CONTROL_SLIDER_WIDTH);
		zRotField = new FormPortletNumericRangeField("Z:", -180, 180, 0).setValue(threeDeePortlet.getRotZ()).setWidth(CONTROL_SLIDER_WIDTH);
		zoomField = new FormPortletNumericRangeField("Zoom:", 100, 4000, 0).setValue(threeDeePortlet.getZoom()).setWidth(CONTROL_SLIDER_WIDTH);
		fovField = new FormPortletNumericRangeField("FOV:", 50, 179, 0).setValue(threeDeePortlet.getFov()).setWidth(CONTROL_SLIDER_WIDTH);
		xPosField = new FormPortletNumericRangeField("X pos:", -180, 180, 0).setValue(threeDeePortlet.getCenterX()).setWidth(CONTROL_SLIDER_WIDTH);
		yPosField = new FormPortletNumericRangeField("Y pos:", -180, 180, 0).setValue(threeDeePortlet.getCenterY()).setWidth(CONTROL_SLIDER_WIDTH);

		this.controlPanelForm = new FormPortlet(generateConfig());
		this.controlPanelForm.addMenuListener(this);
		this.controlPanelForm.addFormPortletListener(this);
		// TODO: Add ability to disable field title. Then wouldn't have to be so careful about the ordering of the fields... 
		this.controlPanelForm.addField(this.yPosField);
		this.controlPanelForm.addField(this.xPosField);
		this.controlPanelForm.addField(this.fovField);
		this.controlPanelForm.addField(this.zoomField);
		this.controlPanelForm.addField(this.zRotField);
		this.controlPanelForm.addField(this.yRotField);
		this.controlPanelForm.addField(this.xRotField);
		this.controlPanelForm.addField(this.uButton);
		this.controlPanelForm.addField(this.tButton);
		this.controlPanelForm.addField(this.rButton);
		this.controlPanelForm.addField(this.lButton);
		this.controlPanelForm.addField(this.bButton);
		this.controlPanelForm.addField(this.fButton);

		int buttonDimPx = 25;
		int buttonPaddingPx = 5;
		int sliderWidthPx = 100;
		this.fButton.setLeftPosPx(buttonPaddingPx);
		this.fButton.setTopPosPx(buttonPaddingPx);
		this.fButton.setWidthPx(buttonDimPx);
		this.fButton.setHeightPx(buttonDimPx);

		this.bButton.setLeftPosPx(buttonPaddingPx + this.fButton.getLeftPosPx() + this.fButton.getWidthPx());
		this.bButton.setTopPosPx(buttonPaddingPx);
		this.bButton.setWidthPx(buttonDimPx);
		this.bButton.setHeightPx(buttonDimPx);

		this.lButton.setLeftPosPx(buttonPaddingPx + this.bButton.getLeftPosPx() + this.bButton.getWidthPx());
		this.lButton.setTopPosPx(buttonPaddingPx);
		this.lButton.setWidthPx(buttonDimPx);
		this.lButton.setHeightPx(buttonDimPx);

		this.rButton.setLeftPosPx(buttonPaddingPx + this.lButton.getLeftPosPx() + this.lButton.getWidthPx());
		this.rButton.setTopPosPx(buttonPaddingPx);
		this.rButton.setWidthPx(buttonDimPx);
		this.rButton.setHeightPx(buttonDimPx);

		this.tButton.setLeftPosPx(buttonPaddingPx + this.rButton.getLeftPosPx() + this.rButton.getWidthPx());
		this.tButton.setTopPosPx(buttonPaddingPx);
		this.tButton.setWidthPx(buttonDimPx);
		this.tButton.setHeightPx(buttonDimPx);

		this.uButton.setLeftPosPx(buttonPaddingPx + this.tButton.getLeftPosPx() + this.tButton.getWidthPx());
		this.uButton.setTopPosPx(buttonPaddingPx);
		this.uButton.setWidthPx(buttonDimPx);
		this.uButton.setHeightPx(buttonDimPx);

		this.xRotField.setLeftPosPx(buttonPaddingPx + this.uButton.getLeftPosPx() + this.uButton.getWidthPx() + 25);
		this.xRotField.setTopPosPx(buttonPaddingPx);
		this.xRotField.setWidthPx(sliderWidthPx);
		this.xRotField.setHeightPx(buttonDimPx);

		this.yRotField.setLeftPosPx(buttonPaddingPx + this.xRotField.getLeftPosPx() + this.xRotField.getWidthPx() + 15);
		this.yRotField.setTopPosPx(buttonPaddingPx);
		this.yRotField.setWidthPx(sliderWidthPx);
		this.yRotField.setHeightPx(buttonDimPx);

		this.zRotField.setLeftPosPx(buttonPaddingPx + this.yRotField.getLeftPosPx() + this.yRotField.getWidthPx() + 15);
		this.zRotField.setTopPosPx(buttonPaddingPx);
		this.zRotField.setWidthPx(sliderWidthPx);
		this.zRotField.setHeightPx(buttonDimPx);

		this.zoomField.setLeftPosPx(buttonPaddingPx + this.zRotField.getLeftPosPx() + this.zRotField.getWidthPx() + 45);
		this.zoomField.setTopPosPx(buttonPaddingPx);
		this.zoomField.setWidthPx(sliderWidthPx);
		this.zoomField.setHeightPx(buttonDimPx);

		this.fovField.setLeftPosPx(buttonPaddingPx + this.zoomField.getLeftPosPx() + this.zoomField.getWidthPx() + 35);
		this.fovField.setTopPosPx(buttonPaddingPx);
		this.fovField.setWidthPx(sliderWidthPx);
		this.fovField.setHeightPx(buttonDimPx);

		this.xPosField.setLeftPosPx(buttonPaddingPx + this.fovField.getLeftPosPx() + this.fovField.getWidthPx() + 45);
		this.xPosField.setTopPosPx(buttonPaddingPx);
		this.xPosField.setWidthPx(sliderWidthPx);
		this.xPosField.setHeightPx(buttonDimPx);

		this.yPosField.setLeftPosPx(buttonPaddingPx + this.xPosField.getLeftPosPx() + this.xPosField.getWidthPx() + 45);
		this.yPosField.setTopPosPx(buttonPaddingPx);
		this.yPosField.setWidthPx(sliderWidthPx);
		this.yPosField.setHeightPx(buttonDimPx);
		/*
		 * rows:
		 * 0. base grid
		 * 1. chart
		 * 2. control panel form
		 */
		setChild(this.controlPanelForm, 0, 1);
		getGridPortlet().setRowSize(2, 50); // control panel
		this.controlPanelForm.setStyle(this.buttonFormStyle);

	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		headMenu.add(new BasicWebMenuLink("Add Series", true, "ser_add").setBackgroundImage(AmiWebConsts.ICON_ADD));
		BasicWebMenu sEdtMenu = new BasicWebMenu("Edit Series", this.getSeriesIds().size() > 0);
		BasicWebMenu sDelMenu = new BasicWebMenu("Delete Series", this.getSeriesIds().size() > 0);
		for (AmiWebSurfaceRenderingLayer s : this.series.values()) {
			AmiWebSurfaceSeries ser = s.getSeries();
			sDelMenu.add(new BasicWebMenuLink("Series " + ser.getId(), true, "del_ser_" + ser.getId()));
			sEdtMenu.add(new BasicWebMenuLink("Series " + ser.getId(), true, "upd_ser_" + ser.getId()));
		}
		sDelMenu.sort();
		sEdtMenu.sort();
		headMenu.add(sDelMenu);
		headMenu.add(sEdtMenu);
		headMenu.add(new BasicWebMenuDivider());
		BasicWebMenu lockMenu = (BasicWebMenu) new BasicWebMenu("Lock Axis Aspect Ratios", true).setBackgroundImage(AmiWebConsts.ICON_LOCK);
		lockMenu.add(new BasicWebMenuLink("Don't Lock Axis", true, "lock_none").setCssStyle(getAxisLock() == AXIS_LOCK_NONE ? "className=ami_menu_checked" : null));
		lockMenu.add(new BasicWebMenuLink("Lock X,Y,Z Axis", true, "lock_xyz").setCssStyle(getAxisLock() == AXIS_LOCK_XYZ ? "className=ami_menu_checked" : null));
		lockMenu.add(new BasicWebMenuLink("Lock X,Y Axis", true, "lock_xy").setCssStyle(getAxisLock() == AXIS_LOCK_XY ? "className=ami_menu_checked" : null));
		lockMenu.add(new BasicWebMenuLink("Lock X,Z Axis", true, "lock_xz").setCssStyle(getAxisLock() == AXIS_LOCK_XZ ? "className=ami_menu_checked" : null));
		lockMenu.add(new BasicWebMenuLink("Lock Y,Z Axis", true, "lock_yz").setCssStyle(getAxisLock() == AXIS_LOCK_YZ ? "className=ami_menu_checked" : null));
		headMenu.add(new BasicWebMenuLink("Edit X Axis...", true, "edit_axis_x"));
		headMenu.add(new BasicWebMenuLink("Edit Y Axis...", true, "edit_axis_y"));
		headMenu.add(new BasicWebMenuLink("Edit Z Axis...", true, "edit_axis_z"));
		headMenu.add(lockMenu);
		headMenu.add(new BasicWebMenuDivider());
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("bgColor", this.bgColor);
		r.put("selColor", this.selColor);
		r.put("labelColor", this.labelColor);
		r.put("controls", controlsVisible);
		r.put("fov", this.threeDeePortlet.getFov());
		r.put("rx", this.threeDeePortlet.getRotX());
		r.put("ry", this.threeDeePortlet.getRotY());
		r.put("rz", this.threeDeePortlet.getRotZ());
		r.put("zm", this.threeDeePortlet.getZoom());
		r.put("axx", this.axisX.getConfiguration());
		r.put("axy", this.axisY.getConfiguration());
		r.put("axz", this.axisZ.getConfiguration());
		r.put("axisLock", this.getAxisLock());

		List<Map<String, Object>> series = new ArrayList<Map<String, Object>>();
		for (AmiWebSurfaceRenderingLayer layer : this.series.values())
			series.add(layer.getSeries().getConfiguration());
		r.put("series", series);
		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);

		String firstDmId = CH.first(getUsedDmAliasDotNames());
		String firstTableId = CH.first(getUsedDmTables(firstDmId));
		List<Map<String, Object>> series = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "series");

		for (int i = 0; i < series.size(); i++) {
			Map<String, Object> s = series.get(i);
			// Backwards compatibility
			String dmId = CH.getOr(Caster_String.INSTANCE, s, "dmid", firstDmId);
			String tableId = CH.getOr(Caster_String.INSTANCE, s, "tblid", firstTableId);

			AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(dmId);
			AmiWebDmTableSchema dmTable = dm.getResponseOutSchema().getTable(tableId);

			AmiWebSurfaceRenderingLayer value = new AmiWebSurfaceRenderingLayer(this);
			AmiWebSurfaceSeries l = new AmiWebSurfaceSeries(getService(), this, dmTable, value);
			value.setSeries(l);
			l.init(s);
			this.series.add(l.getId(), value);
			value.updateAri();
			value.addToDomManager();
		}

		this.axisX.init((Map<String, Object>) configuration.get("axx"));
		this.axisY.init((Map<String, Object>) configuration.get("axy"));
		this.axisZ.init((Map<String, Object>) configuration.get("axz"));
		this.threeDeePortlet.setRotX(CH.getOrThrow(Caster_Double.PRIMITIVE, configuration, "rx"));
		this.threeDeePortlet.setRotY(CH.getOrThrow(Caster_Double.PRIMITIVE, configuration, "ry"));
		this.threeDeePortlet.setRotZ(CH.getOrThrow(Caster_Double.PRIMITIVE, configuration, "rz"));
		this.threeDeePortlet.setZoom(CH.getOrThrow(Caster_Double.PRIMITIVE, configuration, "zm"));
		this.threeDeePortlet.setFov(CH.getOrThrow(Caster_Double.PRIMITIVE, configuration, "fov"));
		this.setAxisLock(CH.getOr(Caster_Byte.INSTANCE, configuration, "axisLock", (byte) 0));
	}
	@Override
	public void clearAmiData() {
		this.threeDeePortlet.clear();

	}

	@Override
	public String getConfigMenuTitle() {
		return "3D Chart";
	}

	@Override
	public boolean isRealtime() {
		return false;
	}

	public void flagViewStale() {
		flagPendingAjax();
		this.isViewStale = true;
	}
	private void drawAxis(Basic3dPortlet threeDeePortlet2, AmiWebSurfaceAxisPortlet[] xyz, double[] scales, int xyzIdx) {
		double xScale, yScale, zScale;
		AmiWebSurfaceAxisPortlet axis1, axis2, axis3;
		switch (xyzIdx) {
			case 0://x
				axis1 = xyz[0];
				axis2 = xyz[1];
				axis3 = xyz[2];
				xScale = scales[0];
				yScale = scales[1];
				zScale = scales[2];
				break;
			case 1://y
				axis1 = xyz[1];
				axis2 = xyz[0];
				axis3 = xyz[2];
				xScale = scales[1];
				yScale = scales[0];
				zScale = scales[2];
				break;
			default://z
				axis1 = xyz[2];
				axis2 = xyz[1];
				axis3 = xyz[0];
				xScale = scales[2];
				yScale = scales[1];
				zScale = scales[0];
				break;
		}
		int color = parseColorToInt(axis1.getLineColor());
		// calculation for axis length
		double x1 = axis1.getMinValue() - axis1.getStartPadding() / xScale;
		double x2 = axis1.getMaxValue() + axis1.getEndPadding() / xScale;
		double y1 = axis2.getMinValue() - axis2.getStartPadding() / yScale;
		double y2 = axis2.getMaxValue() + axis2.getEndPadding() / yScale;
		double z1 = axis3.getMinValue() - axis3.getStartPadding() / zScale;
		double z2 = axis3.getMaxValue() + axis3.getEndPadding() / zScale;
		line(this.threeDeePortlet, xyzIdx, x1, y1, z1, x2, y1, z1, color);
		if (SH.is(axis1.getOtherLineColor())) {
			int otherColor = parseColorToInt(axis1.getOtherLineColor());
			line(this.threeDeePortlet, xyzIdx, x1, y2, z1, x2, y2, z1, otherColor);
			line(this.threeDeePortlet, xyzIdx, x1, y1, z2, x2, y1, z2, otherColor);
			line(this.threeDeePortlet, xyzIdx, x1, y2, z2, x2, y2, z2, otherColor);
		}
		int unitSize = axis1.getMajorUnitSize();
		int padding = axis1.getTextPadding();
		DoubleArrayList ticks = axis1.getMajorTicks();
		// drawing lines for ticks
		ReusableCalcFrameStack sf = getStackFrame();
		for (int i = 0; i < ticks.size(); i++) {
			double tick = ticks.get(i);
			line(this.threeDeePortlet, xyzIdx, tick, y1, z1, tick, y1 - unitSize / yScale, z1 - unitSize / zScale, color);
			String txt = axis1.format(tick, sf);
			if (xyzIdx == 1) {
				int ypad = padding + 3;
				text(this.threeDeePortlet, xyzIdx, tick, y1 - (unitSize + ypad) / yScale, z1 - (unitSize + padding) / zScale, color, txt, Text.ALIGNED_CENTER);
			} else
				text(this.threeDeePortlet, xyzIdx, tick, y1 - (unitSize + padding) / yScale, z1 - (unitSize + padding) / zScale, color, txt, Text.ALIGNED_CENTER);
		}
		text(this.threeDeePortlet, xyzIdx, x2 + axis1.getTitlePadding() / xScale, y1, z1, parseColorToInt(axis1.getTitleColor()), axis1.getTitle(), Text.ALIGNED_CENTER);
		int minorUnitSize = axis1.getMinorUnitSize();
		double minorUnit = axis1.getMinorUnit();
		if (minorUnitSize > 0 && minorUnit > 0d) {
			for (double i = axis1.getMinValue(), l = axis1.getMaxValue(); i < l; i += axis1.getMinorUnit()) {
				line(this.threeDeePortlet, xyzIdx, i, y1, z1, i, y1 - minorUnitSize / yScale, z1 - minorUnitSize / zScale, color);
			}
		}

	}

	static void text(Basic3dPortlet surface, int xyzIdx, double x1, double y1, double z1, int color, String txt, byte alignment) {
		// x->0 y->1 z->2
		switch (xyzIdx) {
			case 0:
				surface.addText(new Text(x1, y1, z1, color, txt, alignment));
				break;
			case 1:
				surface.addText(new Text(y1, x1, z1, color, txt, alignment));
				break;
			case 2:
				surface.addText(new Text(z1, y1, x1, color, txt, alignment));
				break;
		}
	}
	static void line(Basic3dPortlet surface, int xyzIdx, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
		switch (xyzIdx) {
			case 0:
				surface.addLine(new Line(x1, y1, z1, x2, y2, z2, color, 1));
				break;
			case 1:
				surface.addLine(new Line(y1, x1, z1, y2, x2, z2, color, 1));
				break;
			case 2:
				surface.addLine(new Line(z1, y1, x1, z2, y2, x2, color, 1));
				break;
		}
	}

	private <T> T get(List<T> l, int i) {
		if (l.isEmpty())
			return null;
		return l.size() == 1 ? l.get(0) : l.get(i);
	}
	private <T> int get(int[] l, int i) {
		if (l == null || l.length == 0)
			return 0;
		return l.length == 1 ? l[0] : l[i];
	}
	private double getDouble(List<?> l, int i) {
		Object r = l.size() == 1 ? l.get(0) : l.get(i);
		if (r instanceof Number)
			return ((Number) r).doubleValue();
		return Double.NaN;
	}
	private int parseColorToInt(Object mc) {
		if (mc == null)
			return 0;
		else if (mc instanceof Number)
			return ((Number) mc).intValue();
		String str = AmiUtils.s(mc);
		if (SH.isnt(str))
			return 0;
		else if (SH.areBetween(str, '0', '9'))
			return SH.parseInt(str);
		else if (SH.startsWith(str, '#')) {
			return SH.parseInt(str, 1, str.length(), 16);
		}
		return 0;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		threeDeePortlet.setRotX(this.xRotField.getIntValue());
		threeDeePortlet.setRotY(this.yRotField.getIntValue());
		threeDeePortlet.setRotZ(this.zRotField.getIntValue());
		threeDeePortlet.setZoom(this.zoomField.getIntValue() / 100d);
		threeDeePortlet.setFov(this.fovField.getIntValue());
		threeDeePortlet.setCenterX(this.xPosField.getIntValue());
		threeDeePortlet.setCenterY(this.yPosField.getIntValue());
		threeDeePortlet.setFocalX(-this.xPosField.getIntValue());
		threeDeePortlet.setFocalY(-this.yPosField.getIntValue());
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onPerspective(Basic3dPortlet portlet) {
		xRotField.setValue(threeDeePortlet.getRotX());
		yRotField.setValue(threeDeePortlet.getRotY());
		zRotField.setValue(threeDeePortlet.getRotZ());
		zoomField.setValue(threeDeePortlet.getZoom() * 100d);
		fovField.setValue(threeDeePortlet.getFov());
		xPosField.setValue(threeDeePortlet.getCenterX());
		yPosField.setValue(threeDeePortlet.getCenterY());
		this.needsRot = false;
	}
	@Override
	public boolean onAmiContextMenu(String id) {
		if (id.startsWith("ser_add")) {
			AmiWebDm dm = getService().getDmManager().getDmByAliasDotName(CH.first(getUsedDmAliasDotNames()));
			AmiWebDmTableSchema dmTable = dm.getResponseOutSchema().getTable(CH.first(getUsedDmTables(dm.getAmiLayoutFullAliasDotId())));
			AmiWebSurfaceRenderingLayer layer = new AmiWebSurfaceRenderingLayer(this);
			AmiWebSurfaceSeries se = new AmiWebSurfaceSeries(getService(), this, dmTable, layer);
			layer.setSeries(se);
			AmiWebChartEditSeriesPortlet<AmiWebSurfaceSeries> dialog = new AmiWebSurfaceEditSeriesPortlet(generateConfig()).setContainer(layer, se);
			getManager().showDialog("Add 3D Series", dialog);
			return true;
		} else if (id.startsWith("lock_")) {
			String type = SH.stripPrefix(id, "lock_", true);
			if ("none".equals(type))
				this.setAxisLock(AXIS_LOCK_NONE);
			else if ("xy".equals(type))
				this.setAxisLock(AXIS_LOCK_XY);
			else if ("xz".equals(type))
				this.setAxisLock(AXIS_LOCK_XZ);
			else if ("yz".equals(type))
				this.setAxisLock(AXIS_LOCK_YZ);
			else if ("xyz".equals(type))
				this.setAxisLock(AXIS_LOCK_XYZ);
			flagViewStale();
			return true;
		} else if (id.startsWith("edit_axis_x")) {
			final AmiWebSurfaceEditAxisPortlet dialog = new AmiWebSurfaceEditAxisPortlet(generateConfig(), axisX);
			getManager().showDialog("Edit X Axis", dialog);
			return true;
		} else if (id.startsWith("edit_axis_y")) {
			final AmiWebSurfaceEditAxisPortlet dialog = new AmiWebSurfaceEditAxisPortlet(generateConfig(), axisY);
			getManager().showDialog("Edit Y Axis", dialog);
			return true;
		} else if (id.startsWith("edit_axis_z")) {
			final AmiWebSurfaceEditAxisPortlet dialog = new AmiWebSurfaceEditAxisPortlet(generateConfig(), axisZ);
			getManager().showDialog("Edit Z Axis", dialog);
			return true;
		} else if (id.startsWith("upd_ser_")) {
			int seriesId = Integer.parseInt(SH.stripPrefix(id, "upd_ser_", true));
			AmiWebSurfaceRenderingLayer ser = series.get(seriesId);
			AmiWebChartEditSeriesPortlet<AmiWebSurfaceSeries> dialog = new AmiWebSurfaceEditSeriesPortlet(generateConfig()).setContainer(ser, ser.getSeries());
			getManager().showDialog("Edit 3d Series", dialog);
			return true;
		} else if (id.startsWith("del_ser_")) {
			int sid = SH.parseInt(SH.stripPrefix(id, "del_ser_", true));
			getManager().showDialog("Delete Renderying Layer",
					new ConfirmDialogPortlet(generateConfig(), "Delete <B> Series" + sid + "</B>?", ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("DELETE_SERIES")
							.setCorrelationData(sid));
			return true;
		} else if ("set_bgc".equals(id)) {
			getManager().showDialog("Plot Background color", new ColorPickerPortlet(generateConfig(), this.bgColor, this).setCorrelationData("BG"));
			return true;
		} else if ("set_selc".equals(id)) {
			getManager().showDialog("User Selection color", new ColorPickerPortlet(generateConfig(), this.selColor, this).setCorrelationData("SEL"));
			return true;
		} else if ("set_label_color".equals(id)) {
			getManager().showDialog("User Selection color", new ColorPickerPortlet(generateConfig(), this.labelColor, this).setCorrelationData("LAB"));
			return true;
		} else
			return super.onAmiContextMenu(id);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE_SERIES".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				Integer sid = (Integer) source.getCorrelationData();
				removeSeriesById(sid);
				redraw();
			}
			return true;
		} else
			return super.onButton(source, id);
	}

	private IndexedList<Integer, AmiWebSurfaceRenderingLayer> series = new BasicIndexedList<Integer, AmiWebSurfaceRenderingLayer>();

	private double rotTargetX;
	private double rotTargetY;
	private double rotTargetZ;
	private boolean needsRot = false;

	private int length;

	public AmiWebSurfaceRenderingLayer getSeriesById(int id) {
		return this.series.get(id);
	}
	public AmiWebSurfaceRenderingLayer getSeriesAt(int pos) {
		return this.series.getAt(pos);
	}

	public void addSeries(AmiWebSurfaceRenderingLayer series, int position) {
		if (series.getId() < 1) {
			int id = 1;
			while (this.series.containsKey(id))
				id++;
			series.setId(id);

		}
		if (this.series.containsKey(series.getId()))
			this.series.remove(series.getId());
		this.series.add(series.getId(), (AmiWebSurfaceRenderingLayer) series, position);
		series.updateAri();
		series.addToDomManager();
		flagViewStale();
	}

	public Set<Integer> getSeriesIds() {
		return this.series.keySet();
	}

	public AmiWebSurfaceRenderingLayer removeSeriesById(int id) {
		AmiWebSurfaceRenderingLayer r = this.series.remove(id);
		if (r != null)
			r.removeFromDomManager();
		return r;
	}
	public int getSeriesCount() {
		return this.series.getSize();
	}
	private void spin(int x, int y, int z) {
		this.rotTargetX = x;
		this.rotTargetY = y;
		this.rotTargetZ = z;
		this.needsRot = true;
		flagPendingAjax();
		this.onFieldValueChanged(null, null, null);
	}

	@Override
	public void drainJavascript() {
		if (isViewStale) {
			this.isViewStale = false;
			redraw();
		}
		super.drainJavascript();
		if (this.needsRot) {
			double x = animateToward(this.rotTargetX, this.threeDeePortlet.getRotX());
			double y = animateToward(this.rotTargetY, this.threeDeePortlet.getRotY());
			double z = animateToward(this.rotTargetZ, this.threeDeePortlet.getRotZ());
			this.xRotField.setValue(x);
			this.yRotField.setValue(y);
			this.zRotField.setValue(z);
			this.threeDeePortlet.setRotX(x);
			this.threeDeePortlet.setRotY(y);
			this.threeDeePortlet.setRotZ(z);
			this.threeDeePortlet.drainJavascript();
			this.needsRot = (x != this.rotTargetX || y != this.rotTargetY || z != this.rotTargetZ);
			if (this.needsRot)
				flagPendingAjax();
		}
	}

	private ArrayList<Tuple2<Grouping, Integer>> idToRow = new ArrayList<Tuple2<Grouping, Integer>>();

	private void redraw() {
		CalcFrameStack sf = getStackFrame();
		for (AmiWebSurfaceRenderingLayer layer : this.series.values()) {
			AmiWebSurfaceSeries s = layer.getSeries();
			s.buildData(s.getDataModelSchema(), sf);
		}

		// obtain scale size for three axis
		double xScale = toScale(getMinX(), getMaxX()) / 2;
		double yScale = toScale(getMinY(), getMaxY()) / 2;
		double zScale = toScale(getMinZ(), getMaxZ()) / 2;

		switch (getAxisLock()) {
			case AXIS_LOCK_XYZ:
				xScale = yScale = zScale = Math.min(Math.min(xScale, yScale), zScale);
				break;
			case AXIS_LOCK_XY:
				xScale = yScale = Math.min(xScale, yScale);
				break;
			case AXIS_LOCK_XZ:
				xScale = zScale = Math.min(xScale, zScale);
				break;
			case AXIS_LOCK_YZ:
				yScale = zScale = Math.min(yScale, zScale);
				break;
			case AXIS_LOCK_NONE:
				break;
		}

		axisX.updateData();
		axisY.updateData();
		axisZ.updateData();
		threeDeePortlet.clearTriangles();
		threeDeePortlet.clearLines();
		threeDeePortlet.clearTexts();
		this.idToRow.clear();
		List<Triangle> sink = new ArrayList<Basic3dPortlet.Triangle>();
		for (AmiWebSurfaceRenderingLayer layer : this.series.values()) {
			AmiWebSurfaceSeries series = layer.getSeries();
			if (series.getType() == AmiWebSurfaceSeries.TYPE_XYZ) {
				for (Grouping group : series.getUserSelectedGroupings().values()) {
					List<Number> xs = (List) group.getValuesForFormula("xPos");
					List<Number> ys = (List) group.getValuesForFormula("yPos");
					List<Number> zs = (List) group.getValuesForFormula("zPos");
					if (xs.isEmpty() || ys.isEmpty() || zs.isEmpty())
						continue;
					List<Number> ws = (List) group.getValuesForFormula("mWidth");
					List<Number> lef = (List) group.getValuesForFormula("mLeft");
					List<Number> rig = (List) group.getValuesForFormula("mRight");

					List<Number> hs = (List) group.getValuesForFormula("mHeight");
					List<Number> top = (List) group.getValuesForFormula("mTop");
					List<Number> bot = (List) group.getValuesForFormula("mBottom");

					List<Number> ds = (List) group.getValuesForFormula("mDepth");
					List<Number> fro = (List) group.getValuesForFormula("mFront");
					List<Number> bac = (List) group.getValuesForFormula("mBack");

					List<Object> ms = (List) group.getValuesForFormula("mShape");
					int[] mc = getColors(this.colorGradient, this.colorSeriesColors, series.getmColorFormula(), (List) group.getValuesForFormula("mColor"));
					List<Number> ls = (List) group.getValuesForFormula("lineSize");
					List<Object> lc = (List) group.getValuesForFormula("lineColor");
					List<Object> desc = (List) group.getValuesForFormula("desc");
					int lineSize = ls.size() == 0 ? 0 : ls.get(0).intValue();
					int lineColor = lc.size() == 0 ? -1 : parseColorToInt(lc.get(0));

					boolean needsW = lef.isEmpty() || rig.isEmpty();
					boolean needsH = top.isEmpty() || bot.isEmpty();
					boolean needsD = fro.isEmpty() || bac.isEmpty();
					boolean hasMarker;
					if (AH.isEmpty(mc))
						hasMarker = false;
					else if (ws.isEmpty() && needsW)
						hasMarker = false;
					else if (hs.isEmpty() && needsH)
						hasMarker = false;
					else if (ds.isEmpty() && needsD)
						hasMarker = false;
					else
						hasMarker = true;
					int length = xs.size();
					boolean hasLine = lineSize > 0 && lineColor >= 0 && length > 1;
					double ox = 0, oy = 0, oz = 0;
					// looping over x/y/z values
					for (int i = 0; i < length; i++) {
						double x = getDouble(xs, i);
						double y = getDouble(ys, i);
						double z = getDouble(zs, i);
						if (hasMarker) {
							double x1, x2, y1, y2, z1, z2;
							// width/height/depth of marker
							double w = needsW ? (getDouble(ws, i) / xScale) / 3 : Double.NaN;
							double h = needsH ? (getDouble(hs, i) / yScale) / 3 : Double.NaN;
							double d = needsD ? (getDouble(ds, i) / zScale) / 3 : Double.NaN;
							x1 = lef.isEmpty() ? x - w : getDouble(lef, i);
							x2 = rig.isEmpty() ? x + w : getDouble(rig, i);
							y1 = top.isEmpty() ? y - h : getDouble(top, i);
							y2 = bot.isEmpty() ? y + h : getDouble(bot, i);
							z1 = fro.isEmpty() ? z - d : getDouble(fro, i);
							z2 = bac.isEmpty() ? z + d : getDouble(bac, i);

							// drawing the shapes of each coord
							if (!(Double.isNaN(x1) || Double.isNaN(y1) || Double.isNaN(z1) || Double.isNaN(x2) || Double.isNaN(y2) || Double.isNaN(z2))) {
								int color = parseColorToInt(get(mc, i));
								this.idToRow.add(new Tuple2<AmiWebChartSeries.Grouping, Integer>(group, i));
								Object shape = get(ms, i);
								if ("square".equals(shape))
									TdHelper.newCube(x1, y1, z1, x2, y2, z2, color, this.idToRow.size(), sink);
								else if ("triangle".equals(shape))
									TdHelper.newTriangle(x1, y1, z1, x2, y2, z2, color, this.idToRow.size(), sink);
								else if ("diamond".equals(shape))
									TdHelper.newDiamond(x1, y1, z1, x2, y2, z2, color, this.idToRow.size(), sink);
								else if ("pyramid".equals(shape))
									TdHelper.newPyramid(x1, y1, z1, x2, y2, z2, color, this.idToRow.size(), sink);

								if (CH.isntEmpty(desc)) {
									Object t = get(desc, i);
									if (SH.is(t))
										this.threeDeePortlet.addText(new Basic3dPortlet.Text(x2, (y1 + y2) / 2, (z1 + z2) / 2, color, "  " + t, Text.ALIGNED_LEFT));
								}
							}
						}
						// if line options were filled
						if (hasLine) {
							if (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))) {
								if (i > 0)
									threeDeePortlet.addLine(new Line(ox, oy, oz, x, y, z, lineColor, lineSize));
								ox = x;
								oy = y;
								oz = z;
							}
						}
					}
				}
			} else {
				for (Grouping group : series.getUserSelectedGroupings().values()) {
					DtTriangulation dtt = new DtTriangulation();
					List<Number> xs = (List) group.getValuesForFormula("xPos");
					List<Number> ys = (List) group.getValuesForFormula("yPos");
					List<Number> zs = (List) group.getValuesForFormula("zPos");
					int[] mc = getColors(this.colorGradient, this.colorSeriesColors, series.getmColorFormula(), (List) group.getValuesForFormula("mColor"));
					int length = xs.size();
					int[] colors = new int[length];
					for (int i = 0; i < length; i++) {
						double x = getDouble(xs, i);
						double y = getDouble(ys, i);

						double z = getDouble(zs, i);
						if (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))) {
							int color = get(mc, i);
							colors[i] = color;
							DtPoint p = new DtPoint(x, y, z, color);
							try {
								dtt.addPoint(p);

							} catch (RuntimeException e) {
								continue;
							}
						}
					}
					if (!dtt.allCollinear()) {
						for (DtTriangle t : dtt.getTriangles()) {
							if (!t.isHalfplane()) {
								DtPoint p1 = t.getPoint1();
								DtPoint p2 = t.getPoint2();
								DtPoint p3 = t.getPoint3();
								sink.add(new Triangle(p1.getX(), p1.getY(), p1.getZ(), p1.getColor(), p2.getX(), p2.getY(), p2.getZ(), p2.getColor(), p3.getX(), p3.getY(),
										p3.getZ(), p3.getColor()));
							}
						}
					} else { // Draw as line instead
						double ox = 0, oy = 0, oz = 0;
						double x, y, z;
						for (int i = 0; i < length; i++) {
							x = getDouble(xs, i);
							y = getDouble(ys, i);
							z = getDouble(zs, i);
							if (!(Double.isNaN(x) || Double.isNaN(y) || Double.isNaN(z))) {
								this.idToRow.add(new Tuple2<AmiWebChartSeries.Grouping, Integer>(group, i));
								TdHelper.newCube(x, y, z, x, y, z, 0, this.idToRow.size(), sink);
								if (i > 0)
									threeDeePortlet.addLine(new Line(ox, oy, oz, x, y, z, colors[i], 1));
								ox = x;
								oy = y;
								oz = z;
							}
						}
					}
				}
			}
		}

		threeDeePortlet.setScaleX(axisX.isReverse() ? -xScale : xScale);
		threeDeePortlet.setScaleY(axisY.isReverse() ? yScale : -yScale);
		threeDeePortlet.setScaleZ(axisZ.isReverse() ? -zScale : zScale);
		threeDeePortlet.setShiftX((axisX.getMaxValue() + axisX.getMinValue()) / -2);
		threeDeePortlet.setShiftY((axisY.getMaxValue() + axisY.getMinValue()) / -2);
		threeDeePortlet.setShiftZ((axisZ.getMaxValue() + axisZ.getMinValue()) / -2);
		threeDeePortlet.setBackgroundColor(bgColor);
		double[] scales = new double[] { xScale, yScale, zScale };
		drawAxis(this.threeDeePortlet, axes, scales, 0);
		drawAxis(this.threeDeePortlet, axes, scales, 1);
		drawAxis(this.threeDeePortlet, axes, scales, 2);
		for (Triangle i : sink)
			threeDeePortlet.addTriangle(i);
	}

	// controls the size of tick lines, text and shape. Lower the numerator, bigger the size.
	private double toScale(double min, double max) {
		double diff = MH.diff(min, max);

		if (diff == 0) {
			diff = max * .5;
			if (diff == 0)
				diff = 1;
		}
		return 30 / diff;

	}
	private double animateToward(double target, double current) {
		if (current < target)
			current = Math.min(target, current + 15);
		else if (current > target)
			current = Math.max(target, current - 15);
		return current;
	}
	private Set<Object> getUnique(String fieldName) {
		Set<Object> sink = new LinkedHashSet<Object>();
		for (AmiWebSurfaceRenderingLayer i : this.series.values())
			sink.addAll(i.getSeries().getFormula(fieldName).getUniqueValues());
		return sink;
	}
	private double getMin(String fieldName) {
		double r = Double.NaN;
		for (AmiWebSurfaceRenderingLayer i : this.series.values()) {
			Number val = i.getSeries().getFormula(fieldName).getMin();
			if (val != null)
				r = MH.minAvoidNan(r, val.doubleValue());
		}
		return r;
	}
	private double getMax(String fieldName) {
		double r = Double.NaN;
		for (AmiWebSurfaceRenderingLayer i : this.series.values()) {
			Number val = i.getSeries().getFormula(fieldName).getMax();
			if (val != null)
				r = MH.maxAvoidNan(r, val.doubleValue());
		}
		return r;
	}

	public Set<Object> getUniqueX() {
		return getUnique("xPos");
	}
	public double getMaxX() {
		return getMax("xPos");
	}
	public double getMinX() {
		return getMin("xPos");
	}

	public Set<Object> getUniqueY() {
		return getUnique("xPos");
	}
	public double getMaxY() {
		return getMax("yPos");
	}
	public double getMinY() {
		return getMin("yPos");
	}

	public Set<Object> getUniqueZ() {
		return getUnique("zPos");
	}
	public double getMaxZ() {
		return getMax("zPos");
	}
	public double getMinZ() {
		return getMin("zPos");
	}

	@Override
	public void setSize(int width, int height) {
		this.length = (int) Math.sqrt(width * width + height * height);
		this.axisX.setLength(length / 5);
		this.axisY.setLength(length / 5);
		this.axisZ.setLength(length / 5);
		super.setSize(width, height);
	}
	public void setBgColor(String backgroundColor) {
		if (OH.eq(this.bgColor, backgroundColor))
			return;
		this.bgColor = backgroundColor;
		updateOptionsBarStyle();
		flagViewStale();
	}
	public void setLabelColor(String labelColor) {
		if (OH.eq(this.labelColor, labelColor))
			return;
		this.labelColor = labelColor;
		updateOptionsBarStyle();
	}
	private void updateOptionsBarStyle() {
		// control panel at row 2
		getGridPortlet().setRowSize(2, controlsVisible ? CONTROL_BAR_HEIGHT : 0);
		if (controlsVisible) {
			this.buttonFormStyle.setFormStyle("_bg=" + getBgColor() + "|style.color=" + getLabelColor());
			this.controlPanelForm.getFormPortletStyle().setCssStyle(this.buttonFormStyle.getFormStyle());
		}
	}
	public void updateControlButtonsColor() {
		int ctrlBtnsClRgb = ColorHelper.parseRgb(getControlButtonsColor());
		String buttonCssStyle = "style.background=linear-gradient(" + ColorHelper.generateGradientLimitPairRgb(ctrlBtnsClRgb, 0.4, true) + ")|_fg="
				+ ColorHelper.toRgbString(ColorHelper.colorDodgeRgb(ctrlBtnsClRgb)) + "|style.border=0px|_cn=none|style.borderRadius=8px|_bg=" + getBgColor();
		this.buttonFormStyle.putDefaultFormFieldStyle(FormPortletButtonField.JSNAME, buttonCssStyle);
	}
	public void setSelectionColor(String selectionColor) {
		if (selectionColor == null || OH.eq(this.selColor, selectionColor))
			return;
		this.selColor = selectionColor;
		this.threeDeePortlet.setSelectedColor(SH.parseInt(this.selColor, 1, 7, 16));
		flagViewStale();
	}
	public String getBgColor() {
		return this.bgColor;
	}
	public String getSelectionColor() {
		return this.selColor;
	}
	@Override
	public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {
		if ("BG".equals(target.getCorrelationData()))
			setBgColor(target.getColor());
		else if ("LAB".equals(target.getCorrelationData())) {
			setLabelColor(target.getColor());
		} else
			setSelectionColor(target.getColor());
		flagViewStale();
	}

	@Override
	public void onOkayPressed(ColorPickerPortlet target) {
		if ("BG".equals(target.getCorrelationData()))
			this.bgColor = target.getColor();
		else if ("LAB".equals(target.getCorrelationData())) {
			setLabelColor(target.getColor());
		} else
			this.selColor = target.getColor();
		target.close();
		flagViewStale();
	}
	@Override
	public void onCancelPressed(ColorPickerPortlet target) {
		if ("BG".equals(target.getCorrelationData()))
			this.bgColor = target.getDefaultColor();
		else if ("LAB".equals(target.getCorrelationData())) {
			setLabelColor(target.getDefaultColor());
		} else
			this.selColor = target.getDefaultColor();
		flagViewStale();
		target.close();
	}
	@Override
	public void onContextMenu(Basic3dPortlet basic3dPortlet) {
		int displayed = 0;
		int cnt = this.threeDeePortlet.getSelected().size();
		WebMenu m = new BasicWebMenu();
		addCustomMenuItems(m);
		BasicWebMenu perspective = new BasicWebMenu("Perspective...", true);
		perspective.add(new BasicWebMenuLink("Front", true, "_p_front"));
		perspective.add(new BasicWebMenuLink("Back", true, "_p_back"));
		perspective.add(new BasicWebMenuLink("Left", true, "_p_left"));
		perspective.add(new BasicWebMenuLink("Right", true, "_p_right"));
		perspective.add(new BasicWebMenuLink("Top", true, "_p_top"));
		perspective.add(new BasicWebMenuLink("Bottom", true, "_p_bottom"));
		m.add(perspective);
		IdentityHashSet<AmiWebChartSeries> selectedSeries = new IdentityHashSet<AmiWebChartSeries>();
		for (int i : this.threeDeePortlet.getSelected()) {
			Tuple2<Grouping, Integer> row = this.idToRow.get(i - 1);
			Grouping group = row.getA();
			selectedSeries.add(group.getSeries());
		}
		BasicMultiMap.List<String, String> title2portletId = new BasicMultiMap.List<String, String>();
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			for (String s : SH.split('|', link.getTitle())) {
				title2portletId.putMulti(SH.trimWhitespace(s), link.getLinkUid());
			}
		}

		for (String s : CH.sort(title2portletId.keySet())) {
			m.add(new BasicWebMenuLink(s, true, SH.join('_', title2portletId.get(s), new StringBuilder("query_")).toString()));
		}
		CalcFrameStack sf = getStackFrame();
		for (int i : this.threeDeePortlet.getSelected()) {
			Tuple2<Grouping, Integer> row = this.idToRow.get(i - 1);
			Grouping group = row.getA();
			String description = group.getSeries().describe(group.getOrigRows().get(row.getB()), sf);
			if (SH.isnt(description))
				description = "Selection #" + SH.toString(displayed + 1);
			m.add(new BasicWebMenuLink(description, false, "").setCssStyle("_bg=#ffffaf|_fg=#000000"));
			if (displayed++ >= MAX_QUICK_VIEW)
				break;
		}

		if (cnt > MAX_QUICK_VIEW)
			m.add(new BasicWebMenuLink("(supressing remaining " + (cnt - MAX_QUICK_VIEW) + " records)...", false, "").setCssStyle("_bg=#ffffaf|_fg=#000000"));
		getManager().showContextMenu(m, this);
	}
	@Override
	public void onSelectionChanged(Basic3dPortlet basic3dPortlet) {
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			AmiWebDmUtils.sendRequest(getService(), link);
		}
	}
	@Override
	public void onMenuItem(String id) {
		if (id.startsWith("query_")) {
			String linkIds = SH.stripPrefix(id, "query_", true);
			for (String t : SH.split('_', linkIds)) {
				AmiWebDmLink link = getService().getDmManager().getDmLink(t);
				AmiWebDmUtils.sendRequest(getService(), link);
			}

		} else if (SH.startsWith(id, "_p_")) {
			String cid = SH.stripPrefix(id, "_p_", true);
			if ("front".equals(cid)) {
				spin(0, 0, 0);
			} else if ("back".equals(cid)) {
				spin(0, 180, 0);
			} else if ("left".equals(cid)) {
				spin(0, -90, 0);
			} else if ("right".equals(cid)) {
				spin(0, 90, 0);
			} else if ("top".equals(cid)) {
				spin(-90, 0, 0);
			} else if ("bottom".equals(cid)) {
				spin(90, 0, 0);
			}
		} else if (isCustomContextMenuAction(id)) {
			processCustomContextMenuAction(id);
		}
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		for (int i : this.threeDeePortlet.getSelected()) {
			Tuple2<Grouping, Integer> row = this.idToRow.get(i - 1);
			Grouping group = row.getA();
			AmiWebDmTableSchema dm = group.getSeries().getDataModelSchema();
			if (OH.eq(link.getSourceDmAliasDotName(), dm.getDm().getAmiLayoutFullAliasDotId()) && link.getSourceDmTableName().equals(dm.getName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		Table table = link.getSourceTableData();
		Table values = new BasicTable(table.getColumns());
		if (type != NONE)
			for (int i : this.threeDeePortlet.getSelected()) {
				Tuple2<Grouping, Integer> row = this.idToRow.get(i - 1);
				Grouping group = row.getA();
				AmiWebDmTableSchema dm = group.getSeries().getDataModelSchema();
				if (type == ALL || OH.eq(link.getSourceDmAliasDotName(), dm.getDm().getAmiLayoutFullAliasDotId()) && link.getSourceDmTableName().equals(dm.getName())) {
					Row row2 = group.getOrigRows().get(row.getB());
					values.getRows().addRow(row2.getValuesCloned());
				}
			}
		return values;
	}
	@Override
	public void onMenuDismissed() {
	}

	@Override
	public String getPanelType() {
		return "chart_3d";
	}
	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		super.onDmDataChanged(datamodel);
		flagViewStale();
	}
	@Override
	public void clearUserSelection() {
		this.threeDeePortlet.clearSelected();
	}
	public IterableAndSize<AmiWebSurfaceRenderingLayer> getSeries() {
		return this.series.values();
	}
	public String getLabelColor() {
		return labelColor;
	}
	public boolean isControlsVisible() {
		return controlsVisible;
	}
	public void setControlsVisible(Boolean isVisible) {
		if (isVisible == null || isVisible.equals(controlsVisible))
			return;
		this.controlsVisible = isVisible;
		updateOptionsBarStyle();
	}
	public byte getAxisLock() {
		return axisLock;
	}
	public void setAxisLock(byte axisLock) {
		this.axisLock = axisLock;
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		for (AmiWebSurfaceRenderingLayer i : this.getSeries()) {
			i.getSeries().getUsedColors(sink);
		}

	}
	public AmiWebSurfaceAxisPortlet getAxisX() {
		return axisX;
	}
	public AmiWebSurfaceAxisPortlet getAxisY() {
		return axisY;
	}
	public AmiWebSurfaceAxisPortlet getAxisZ() {
		return axisZ;
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_3dChart.TYPE_3DCHART;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);

		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				setBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_CL:
				setSelectionColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				setLabelColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_HIDE_CTRLS:
				setControlsVisible((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_X_CL:
				this.xRotField.setLeftScrollTrackColor((String) nuw);
				this.xRotField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_Y_CL:
				this.yRotField.setLeftScrollTrackColor((String) nuw);
				this.yRotField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_Z_CL:
				this.zRotField.setLeftScrollTrackColor((String) nuw);
				this.zRotField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_ZOOM_CL:
				this.zoomField.setLeftScrollTrackColor((String) nuw);
				this.zoomField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_FOV_CL:
				this.fovField.setLeftScrollTrackColor((String) nuw);
				this.fovField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_X_POS_CL:
				this.xPosField.setLeftScrollTrackColor((String) nuw);
				this.xPosField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SCR_Y_POS_CL:
				this.yPosField.setLeftScrollTrackColor((String) nuw);
				this.yPosField.setScrollGripColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_CTRL_BTNS_CL:
				setControlButtonsColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_GRADIENT:
				this.colorGradient = (ColorGradient) nuw;
				break;
			case AmiWebStyleConsts.CODE_SERIES_CLS:
				this.colorSeries = (List<String>) nuw;
				this.colorSeriesColors = ColorHelper.parseColorsNoThrow(this.colorSeries);
				break;
		}
	}
	public void onUsedDmChanged(String oldDmAliasDotName, String oldDmTableName, AmiWebSurfaceSeries layer) {
		if (OH.eq(layer.getDmAliasDotName(), oldDmAliasDotName) && OH.eq(layer.getDmTableName(), oldDmTableName))
			return;
		boolean remove = oldDmAliasDotName != null;
		boolean add = true;
		for (AmiWebSurfaceRenderingLayer k : series.values()) {
			AmiWebSurfaceSeries j = k.getSeries();
			if (OH.eq(oldDmAliasDotName, j.getDmAliasDotName()) && OH.eq(oldDmTableName, j.getDmTableName()))
				remove = false;
			if (j != layer && OH.eq(layer.getDmAliasDotName(), j.getDmAliasDotName()) && OH.eq(layer.getDmTableName(), j.getDmTableName()))
				add = false;
		}
		if (add)
			addUsedDm(layer.getDmAliasDotName(), layer.getDmTableName());
		if (remove)
			removeUsedDm(oldDmAliasDotName, oldDmTableName);
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (this.fButton == node)
			spin(0, 0, 0);
		else if (this.bButton == node)
			spin(0, 180, 0);
		else if (this.lButton == node)
			spin(0, -90, 0);
		else if (this.rButton == node)
			spin(0, 90, 0);
		else if (this.tButton == node)
			spin(-90, 0, 0);
		else if (this.uButton == node)
			spin(90, 0, 0);
	}

	public String getControlButtonsColor() {
		return controlButtonsColor;
	}
	public void setControlButtonsColor(String controlButtonsColor) {
		if (OH.eq(this.controlButtonsColor, controlButtonsColor) || controlButtonsColor == null)
			return;
		this.controlButtonsColor = controlButtonsColor;
		updateControlButtonsColor();
	}
	@Override
	public void onHover(Basic3dPortlet basic3dPortlet, int x, int y, int selectId, Triangle triangle) {
		if (triangle == null)
			return;
		if (triangle.getId() == 0)
			return;
		Tuple2<Grouping, Integer> row = this.idToRow.get(triangle.getId() - 1);
		Object o = row.getA().getSeries().getFormula("tooltip").getData(new ReusableCalcFrameStack(getStackFrame(), row.getA().getOrigRows().get(row.getB())));
		this.threeDeePortlet.setHoverOver(x, y, selectId, getService().cleanHtml(AmiUtils.s(o)));
	}

	public void flagNeedsRepaint() {
		flagViewStale();
		this.flagPendingAjax();
	}
	@Override
	public String getScrollGripColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollGripColor();
	}
	@Override
	public void setScrollGripColor(String scrollGripColor) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollGripColor(scrollGripColor);
	}
	@Override
	public String getScrollTrackColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollTrackColor();
	}
	@Override
	public void setScrollTrackColor(String scrollTrackColor) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollTrackColor(scrollTrackColor);
	}
	@Override
	public String getScrollButtonColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollButtonColor();
	}
	@Override
	public void setScrollButtonColor(String scrollButtonColor) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollButtonColor(scrollButtonColor);
	}
	@Override
	public String getScrollIconsColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollIconsColor();
	}
	@Override
	public void setScrollIconsColor(String scrollIconsColor) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollIconsColor(scrollIconsColor);
	}
	@Override
	public String getScrollBorderColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollBorderColor();
	}
	@Override
	public void setScrollBorderColor(String color) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollBorderColor(color);
	}
	@Override
	public Integer getScrollBarWidth() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollBarWidth();
	}
	@Override
	public void setScrollBarWidth(Integer scrollBarWidth) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollBarWidth(scrollBarWidth);
	}
	@Override
	public Set<String> getUsedDmVariables(String dmAliasDotName, String dmTable, Set<String> r) {
		for (AmiWebSurfaceRenderingLayer t : this.series.values()) {
			AmiWebSurfaceSeries s = t.getSeries();
			if (OH.eq(dmAliasDotName, s.getDmAliasDotName()) && OH.eq(dmTable, s.getDmTableName())) {
				for (int i = 0, l = s.getFormulasCount(); i < l; i++) {
					s.getFormulaAt(i).getDependencies((Set) r);
				}
			}
		}
		return r;
	}
	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		super.onDmNameChanged(oldAliasDotName, dm);
		for (AmiWebSurfaceRenderingLayer t : this.series.values()) {
			AmiWebSurfaceSeries ser = t.getSeries();
			ser.onDmNameChanged(oldAliasDotName, dm);
		}
	}
	//	public void setSeriesAt(int position, AmiWebSurfaceSeries series) {
	//		addSeries(series, position);
	//	}
	public ColorGradient getStyleColorGradient() {
		return this.colorGradient;
	}
	public List<String> getStyleColorSeries() {
		return this.colorSeries;
	}
	private int[] getColors(ColorGradient dfltGradient, List<Color> dfltSeries, AmiWebChartFormula_Color cf, List values) {
		int[] list;
		switch (cf.getColorType()) {
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_GRADIENT: {
				ColorGradient gradient = cf.getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_DFLT_GRADIENT ? dfltGradient : cf.getGradient();
				if (gradient == null || cf.getMin() == null || cf.getMax() == null) {
					list = null;
					break;
				}
				double min = cf.getMin().doubleValue();
				double max = cf.getMax().doubleValue();
				double diff = max - min;
				if (diff == 0 || gradient.getStopsCount() == 1)
					list = new int[] { gradient.toColor(.5) };
				else {
					list = new int[values.size()];
					for (int n = 0; n < values.size(); n++) {
						Number val = (Number) values.get(n);
						list[n] = val == null ? DEFAULT_COLOR : gradient.toColor((val.doubleValue() - min) / (diff));
					}
				}
				break;
			}
			case AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM_SERIES: {
				List<Color> seriesColors = cf.getColorType() == AmiWebChartFormula_Color.TYPE_COLOR_DFLT_SERIES ? dfltSeries : cf.getSeriesColors();
				if (CH.isEmpty(seriesColors)) {
					list = null;
					break;
				}
				if (seriesColors.size() == 1) {
					Color sc = seriesColors.get(0);
					list = new int[] { sc == null ? 0 : sc.getRGB() };
				} else {
					list = new int[values.size()];
					for (int n = 0; n < values.size(); n++) {
						Number val = (Number) values.get(n);
						Color t = val == null ? null : CH.getAtMod(seriesColors, val.intValue());
						list[n] = t == null ? DEFAULT_COLOR : t.getRGB();
					}
				}
				break;
			}
			default:
			case AmiWebChartFormula_Color.TYPE_COLOR_CONST:
			case AmiWebChartFormula_Color.TYPE_COLOR_CUSTOM:
			case AmiWebChartFormula_Color.TYPE_COLOR_NONE: {
				list = new int[values.size()];
				for (int n = 0; n < values.size(); n++)
					list[n] = parseColorToInt(values.get(n));
				break;
			}
		}
		return list;
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebSurfaceSettingsPortlet(generateConfig(), this);
	}
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		CH.addAll(r, this.series.values());
		r.add(this.axisX);
		r.add(this.axisY);
		r.add(this.axisZ);
		return r;
	}

	@Override
	public void updateAri() {
		super.updateAri();
		for (AmiWebSurfaceRenderingLayer i : this.series.values())
			i.updateAri();
		for (AmiWebSurfaceAxisPortlet i : this.axes)
			i.updateAri();
	}
	public ColorGradient getColorGradient() {
		// TODO Auto-generated method stub
		return null;
	}
	public List<Color> getColorSeriesColors() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getScrollBarRadius() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollBarRadius();

	}
	@Override
	public void setScrollBarRadius(Integer borderRadius) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollBarRadius(borderRadius);
	}

	@Override
	public void setScrollBarHideArrows(Boolean hide) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollBarHideArrows(hide);
	}
	@Override
	public Boolean getScrollBarHideArrows() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollBarHideArrows();
	}
	@Override
	public void setScrollBarCornerColor(String color) {
		if (this.controlPanelForm == null)
			return;
		this.controlPanelForm.getFormPortletStyle().setScrollBarCornerColor(color);
	}
	@Override
	public String getScrollBarCornerColor() {
		if (this.controlPanelForm == null)
			return null;
		return this.controlPanelForm.getFormPortletStyle().getScrollBarCornerColor();
	}

}
