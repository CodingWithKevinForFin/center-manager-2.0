package com.f1.ami.web.charts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.charts.AmiWebChartSeries.Grouping;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.base.IterableAndSize;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.impl.IdentityHasher;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntSet;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebChartPlotPortlet extends AbstractPortlet implements AmiWebManagedPortlet, ConfirmDialogListener, ColorPickerListener, WebMenuListener, AmiWebDomObject {

	private static final Logger log = LH.get();
	public static final PortletSchema<AmiWebChartPlotPortlet> SCHEMA = new BasicPortletSchema<AmiWebChartPlotPortlet>("AmiPlotPortlet");

	final private AmiWebChartGridPortlet chart;
	private int plotId;
	private int col;
	private int row;
	private byte status = 0;
	private static final byte STATUS_NO_CHANGE = 0;
	private static final byte STATUS_SELECT_CHANGED = 1;//select layer changed
	private static final byte STATUS_VIEW_CHANGED = 2;//zoom or window size changed
	private static final byte STATUS_CONFIG_CHANGED = 3;//the style changed, eg: background color
	private static final byte STATUS_FORMULA_CHANGED = 4;//the style changed, eg: background color
	private String bgColor = "#CCCCCC";
	private String selColor = "#FFFF3F";
	private String selBoxBorderColor = "#404040EF";
	private String selBoxFillColor = "#40404007";
	private AmiWebChartZoomMetrics zoomMetrics;
	final private Set<AmiWebChartShape> selectedShapes = new LinkedHasherSet<AmiWebChartShape>(AmiWebChartShape.HASHER);

	public int getPlotId() {
		return plotId;
	}

	public AmiWebChartPlotPortlet(AmiWebChartGridPortlet amiWebChartGridPortlet, int plotId, PortletConfig config, int col, int row) {
		super(config);
		this.plotId = plotId;
		this.chart = amiWebChartGridPortlet;
		this.col = col;
		this.row = row;
	}

	public int getCol() {
		return col;
	}
	public int getRow() {
		return row;
	}
	public void setCol(int col) {
		this.col = col;
	}
	public void setRow(int row) {
		this.row = row;
	}

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	public int getRenderyingLayersCount() {
		return this.renderingLayers.getSize();
	}
	public AmiWebChartRenderingLayer getRenderyingLayerAt(int position) {
		return this.renderingLayers.getAt(position);
	}
	public AmiWebChartRenderingLayer getRenderyingLayerById(int id) {
		return this.renderingLayers.get(id);
	}

	@Override
	public boolean onAmiContextMenu(String id) {
		if ("edit_layers".equals(id)) {
			getManager().showDialog("Edit Rendering Layer", new AmiWebChartEditLayersPortlet(generateConfig(), this)).setShadeOutside(false);
			return true;
		} else if (id.startsWith("layer_")) {
			final String part = SH.stripPrefix(id, "layer_", true);
			final int layerId = SH.parseInt(SH.beforeFirst(part, '_'));
			final String part2 = SH.afterFirst(part, '_');
			getRenderyingLayerById(layerId).onAmiContextMenu(part2);
			return true;
		} else if ("add_above".equals(id)) {
			this.getChart().addPlotsRow(this.row);
			return true;
		} else if ("add_below".equals(id)) {
			this.getChart().addPlotsRow(this.row + 1);
			return true;
		} else if ("add_left".equals(id)) {
			this.getChart().addPlotsCol(this.col);
			return true;
		} else if ("add_right".equals(id)) {
			this.getChart().addPlotsCol(this.col + 1);
			return true;
		} else if ("add_axis_left".equals(id)) {
			this.getChart().addAxis(AmiWebChartGridPortlet.POS_L, this.row, 0);
			return true;
		} else if ("add_axis_right".equals(id)) {
			this.getChart().addAxis(AmiWebChartGridPortlet.POS_R, this.row, 0);
			return true;
		} else if ("add_axis_above".equals(id)) {
			this.getChart().addAxis(AmiWebChartGridPortlet.POS_T, this.col, 0);
			return true;
		} else if ("add_axis_below".equals(id)) {
			this.getChart().addAxis(AmiWebChartGridPortlet.POS_B, this.col, 0);
			return true;
		} else if ("del_row".equals(id)) {
			getManager().showDialog("Delete plots",
					new ConfirmDialogPortlet(generateConfig(), "Delete <B>all " + getChart().getColsCount() + " plot(s)</B> from this row?", ConfirmDialogPortlet.TYPE_YES_NO, this)
							.setCallback("DELETE_ROW"));
			return true;
		} else if ("del_col".equals(id)) {
			getManager().showDialog("Delete plots",
					new ConfirmDialogPortlet(generateConfig(), "Delete <B>all " + getChart().getRowsCount() + " plot(s)</B> from this col?", ConfirmDialogPortlet.TYPE_YES_NO, this)
							.setCallback("DELETE_COL"));
			return true;
			//		} else if ("set_bgc".equals(id)) {
			//			getManager().showDialog("Plot Background color", new ColorPickerPortlet(generateConfig(), bgColor, this).setCorrelationData("BG"));
			//			return true;
			//		} else if ("set_selc".equals(id)) {
			//			getManager().showDialog("User Selection color", new ColorPickerPortlet(generateConfig(), selColor, this).setCorrelationData("SEL"));
			//			return true;
			//		} else if ("set_selbf".equals(id)) {
			//			getManager().showDialog("User Selection Box Fill color",
			//					new ColorPickerPortlet(generateConfig(), selBoxFillColor, this).setCorrelationData("SELBF").setAlphaEnabled(true));
			//			return true;
			//		} else if ("set_selbb".equals(id)) {
			//			getManager().showDialog("User Selection Box Border color",
			//					new ColorPickerPortlet(generateConfig(), selBoxBorderColor, this).setCorrelationData("SELBB").setAlphaEnabled(true));
			//			return true;
		} else if (id.startsWith("del_rend_")) {
			int rlId = SH.parseInt(SH.stripPrefix(id, "del_rend_", true));
			AmiWebChartRenderingLayer layer = this.renderingLayers.get(rlId);
			getManager().showDialog("Delete Renderying Layer",
					new ConfirmDialogPortlet(generateConfig(), "Delete <B> " + layer.getDescription() + "</B> from this plot?", ConfirmDialogPortlet.TYPE_YES_NO, this)
							.setCallback("DELETE_LAYER").setCorrelationData(rlId));
			return true;
		}
		return false;
	}
	@Override
	public void populateLowerConfigMenu(WebMenu headMenu) {
		if (!getChart().isReadonlyLayout()) {
			if (!isTransient()) {
				headMenu.add(new BasicWebMenuLink("Edit Plot", true, "edit_layers"));
				BasicWebMenu add = (BasicWebMenu) new BasicWebMenu("Add", true).setBackgroundImage(AmiWebConsts.ICON_ADD);
				if (getChart().getColsCount() > 1) {
					add.addChild(new BasicWebMenuLink("Plots Above", true, "add_above"));
					add.addChild(new BasicWebMenuLink("Plots Below", true, "add_below"));
				} else {
					add.addChild(new BasicWebMenuLink("Plot Above", true, "add_above"));
					add.addChild(new BasicWebMenuLink("Plot Below", true, "add_below"));
				}
				if (getChart().getRowsCount() > 1) {
					add.addChild(new BasicWebMenuLink("Plots to the Left", true, "add_left"));
					add.addChild(new BasicWebMenuLink("Plots to the Right", true, "add_right"));
				} else {
					add.addChild(new BasicWebMenuLink("Plot to the Left", true, "add_left"));
					add.addChild(new BasicWebMenuLink("Plot to the Right", true, "add_right"));
				}
				add.addChild(new BasicWebMenuLink("Axis To Left", true, "add_axis_left"));
				add.addChild(new BasicWebMenuLink("Axis To Right", true, "add_axis_right"));
				add.addChild(new BasicWebMenuLink("Axis Above", true, "add_axis_above"));
				add.addChild(new BasicWebMenuLink("Axis Below", true, "add_axis_below"));
				headMenu.add(add);
			}

			BasicWebMenu remove = (BasicWebMenu) new BasicWebMenu("Remove", true).setBackgroundImage(AmiWebConsts.ICON_DELETE);
			if (this.renderingLayers.getSize() > 0) {
				for (AmiWebChartRenderingLayer i : this.renderingLayers.values()) {
					remove.add(new BasicWebMenuLink(i.getDescription(), true, "del_rend_" + i.getId()));
				}
				remove.addChild(new BasicWebMenuDivider());
			}
			if (this.getChart().getColsCount() == 1 && this.getChart().getRowsCount() == 1)
				remove.addChild(new BasicWebMenuLink("This Plot", false, "del_row"));
			else if (this.getChart().getColsCount() == 1)
				remove.addChild(new BasicWebMenuLink("This Plot", true, "del_row"));
			else if (this.getChart().getRowsCount() == 1)
				remove.addChild(new BasicWebMenuLink("This Plot", true, "del_col"));
			else {
				if (this.getChart().getColsCount() > 1)
					remove.addChild(new BasicWebMenuLink("This row of Plots", getChart().getRowsCount() > 1, "del_row"));
				if (this.getChart().getRowsCount() > 1)
					remove.addChild(new BasicWebMenuLink("This column of Plots", getChart().getColsCount() > 1, "del_col"));
			}

			headMenu.add(remove);
		}

	}
	@Override
	public void populateConfigMenu(WebMenu headMenu) {
	}

	@Override
	public void drainJavascript() {
		switch (status) {
			case STATUS_FORMULA_CHANGED: {
				for (AmiWebChartRenderingLayer layer : this.renderingLayers.values()) {
					AmiWebChartRenderingLayer<?> n = layer;
					n.buildData(n.getDataModelSchema(), getChart().getStackFrame());
				}
				if (getVisible()) {
					callInitJs();
					callDrawImagesJs();
					callDrawSelectedJs();
				}
				break;
			}
			case STATUS_CONFIG_CHANGED: {
				if (getVisible()) {
					callInitJs();
					callDrawImagesJs();
					callDrawSelectedJs();
				}
				break;
			}
			case STATUS_SELECT_CHANGED: {
				if (getVisible()) {
					callDrawSelectedJs();
				}
				break;
			}
			case STATUS_VIEW_CHANGED: {
				if (getVisible()) {
					callDrawImagesJs();
					callDrawSelectedJs();
				}
				break;
			}
		}
		status = STATUS_NO_CHANGE;
		super.drainJavascript();
	}

	// responsible for showing tooltip?
	private void callDrawSelectedJs() {
		AmiWebChartZoomMetrics zm = this.getZoom();

		List<AmiWebChartShape> selectedShapes2 = new ArrayList<AmiWebChartShape>(this.selectedShapes.size());
		for (AmiWebChartShape i : this.selectedShapes) {
			AmiWebChartShape other = this.getShape(i.getLayerPos(), i.getGroupNum(), i.getRowNum());
			if (other != null)
				selectedShapes2.add(other);
		}
		final AmiWebImageGenerator ig = new AmiWebImageGenerator_Select(selectedShapes2, this.getSelectionColor());
		final String igid = this.selectedShapes.isEmpty() ? null
				: getService().getChartImagesManager().addImageGenerator(getPortletId(), -1, new AmiWebImageGenerator[] { ig }, zm);
		final JsFunction jsf = callJsFunction("setImage");
		jsf.addParam(-1);
		jsf.addParamQuoted(igid);
		jsf.addParam(zm.getWidth());
		jsf.addParam(zm.getHeight());
		jsf.addParam(zm.getPosOffsetX());
		jsf.addParam(zm.getPosOffsetY());
		jsf.addParam(zm.getPosZoomX());
		jsf.addParam(zm.getPosZoomY());
		jsf.end();
	}
	private AmiWebChartShape getShape(int layerPos, int groupNum, int rowNum) {
		AmiWebChartRenderingLayer layer = this.renderingLayers.getAt(layerPos);
		if (layer == null)
			return null;
		List<AmiWebChartShape> shapes = layer.getShapesAtGroup(groupNum);
		if (shapes == null)
			return null;
		return rowNum < shapes.size() ? shapes.get(rowNum) : null;
	}

	private void callDrawImagesJs() {
		try {
			AmiWebImageGenerator[] imageGenerators = new AmiWebImageGenerator[this.renderingLayers.getSize()];
			AmiWebChartZoomMetrics zm = getZoom();
			StringBuilder pjs = getManager().getPendingJs();
			// loop over layers (Edit Plot -> Edit Rendering Layer)
			for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
				AmiWebChartRenderingLayer<?> layer = this.renderingLayers.getAt(pos);
				AmiWebImageGenerator ig = layer.getCurrentImageGenerator();
				imageGenerators[this.renderingLayers.getSize() - 1 - pos] = ig;
				if (layer.getJsClassName() != null) {
					// draw legend (and maybe other stuff?)
					pjs.append("//LAYER " + layer.getId()).append("\n");
					pjs.append("{var t=");
					callJsFunction("getLayer").addParam(pos).end();
					layer.buildJs(pjs, "t");
					pjs.append("}");
				}
			}
			// set image for plot
			String id = getService().getChartImagesManager().addImageGenerator(this.getPortletId(), 0, imageGenerators, zm);
			JsFunction jsf = callJsFunction("setImage");
			jsf.addParam(0);
			jsf.addParamQuoted(id);
			jsf.addParam(zm.getWidth());
			jsf.addParam(zm.getHeight());
			jsf.addParam(zm.getPosOffsetX());
			jsf.addParam(zm.getPosOffsetY());
			jsf.addParam(zm.getPosZoomX());
			jsf.addParam(zm.getPosZoomY());
			jsf.end();
			flagConfigStale();
		} catch (Exception e) {
			LH.warning(log, "Error processing selection, ", e);
		}
	}
	@Override
	public String getConfigMenuTitle() {
		return "Chart Plot";

	}
	@Override
	public boolean getIsFreeFloatingPortlet() {
		return false;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE_COL".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				this.getChart().removePlotsColumn(this.col);
			}
			return true;
		} else if ("DELETE_ROW".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				this.getChart().removePlotsRow(this.row);
			}
			return true;
		} else if ("DELETE_LAYER".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				AmiWebChartRenderingLayer layer = this.removeRenderyingLayer((Integer) source.getCorrelationData());
				layer.onClosed();
			}

			return true;
		}
		return false;
	}

	public AmiWebChartRenderingLayer removeRenderyingLayer(int renderingLayerId) {
		AmiWebChartRenderingLayer r = this.renderingLayers.remove(renderingLayerId);
		updateRenderLayerPositions();
		this.chart.flagConfigStale();
		return r;
	}

	@Override
	public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {
		setColor(target.getCorrelationData(), target.getColor());
		flagConfigStale();
	}

	@Override
	public void onOkayPressed(ColorPickerPortlet target) {
		String color = target.getColor();
		setColor(target.getCorrelationData(), target.getColor());
		target.close();
	}

	@Override
	public void onCancelPressed(ColorPickerPortlet target) {
		setColor(target.getCorrelationData(), target.getDefaultColor());
		target.close();
	}

	private void setColor(Object id, String color) {
		if ("BG".equals(id))
			this.bgColor = color;
		else if ("SEL".equals(id))
			this.selColor = color;
		else if ("SELBF".equals(id))
			this.selBoxFillColor = color;
		else if ("SELBB".equals(id))
			this.selBoxBorderColor = color;
		flagConfigStale();
	}

	@Override
	protected void initJs() {
		super.initJs();
		flagConfigStale();
	}
	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
	}
	private void callInitJs() {
		JsFunction func = callJsFunction("init");
		JsonBuilder json = func.startJson();
		json.startMap();
		json.addKeyValueQuoted("bgColor", this.bgColor);
		json.addKeyValueQuoted("selColor", this.selColor);
		json.addKeyValueQuoted("selBoxBorderColor", this.selBoxBorderColor);
		json.addKeyValueQuoted("selBoxFillColor", this.selBoxFillColor);
		json.endMap();
		func.end();
		for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
			String jsClassName = this.renderingLayers.getAt(pos).getJsClassName();
			if (jsClassName != null) {
				func = callJsFunction("addLayer").addParam(pos).startParam();
				func.getStringBuilder().append("new ").append(jsClassName + "()");
				func.end();
			}
		}
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("bgColor", this.bgColor);
		r.put("selColor", this.selColor);
		r.put("selBoxBorderColor", this.selBoxBorderColor);
		r.put("selBoxFillColor", this.selBoxFillColor);
		r.put("plotId", this.plotId);
		List<Map<String, Object>> layers = new ArrayList<Map<String, Object>>();
		for (AmiWebChartRenderingLayer layer : this.renderingLayers.values()) {
			layers.add(layer.getConfiguration());
		}
		r.put("layers", layers);

		return r;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		this.bgColor = CH.getOrThrow(Caster_String.INSTANCE, configuration, "bgColor");
		this.selColor = CH.getOr(Caster_String.INSTANCE, configuration, "selColor", selColor);
		this.selBoxBorderColor = CH.getOr(Caster_String.INSTANCE, configuration, "selBoxBorderColor", selBoxBorderColor);
		this.selBoxFillColor = CH.getOr(Caster_String.INSTANCE, configuration, "selBoxFillColor", selBoxFillColor);
		this.plotId = CH.getOrThrow(Caster_Integer.INSTANCE, configuration, "plotId");
		this.chart.registerUsedId(plotId);
		List<Map<String, Object>> layers = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "layers");
		int n = 0;
		String amiLayoutFullAlias = getChart().getAmiLayoutFullAlias();
		for (Map<String, Object> layer : layers) {
			String type = CH.getOr(Caster_String.INSTANCE, layer, "type", null);
			//START-backwards compatibility
			List<Map<String, Object>> series = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, layer, "layers", null);
			if (series != null && series.size() > 1) {
				LH.info(log, "Backwards compatibility converting multiple series to multiple layers");
				for (Map<String, Object> s : series) {
					AmiWebChartRenderingLayer l;
					if ("Chart".equals(type))
						l = new AmiWebChartRenderingLayer_Graph(this);
					else if ("Radial".equals(type))
						l = new AmiWebChartRenderingLayer_RadialGraph(this);
					else if ("Legend".equals(type))
						l = new AmiWebChartRenderingLayer_Legend(this);
					else
						continue;
					HashMap<String, Object> layer2 = new HashMap<String, Object>(layer);
					s.put("id", 0);
					layer2.put("layers", CH.l(s));
					layer2.put("id", n++);
					if (layer2.get("name") == null)
						layer2.put("name", "Layer" + (this.renderingLayers.getSize() + 1));
					l.init(amiLayoutFullAlias, layer2);
					l.addToDomManager();
					this.renderingLayers.add(l.getId(), l);
				}
				//END-backwards compatibility
			} else {
				AmiWebChartRenderingLayer l;
				if ("Chart".equals(type))
					l = new AmiWebChartRenderingLayer_Graph(this);
				else if ("Radial".equals(type))
					l = new AmiWebChartRenderingLayer_RadialGraph(this);
				else if ("Legend".equals(type))
					l = new AmiWebChartRenderingLayer_Legend(this);
				else
					continue;
				l.init(amiLayoutFullAlias, layer);
				l.addToDomManager();
				this.renderingLayers.add(l.getId(), l);
			}
		}
		this.flagConfigStale();
		updateRenderLayerPositions();
	}
	@Override
	public AmiWebService getService() {
		return this.getChart().getService();
	}
	//	@Override
	//	public AmiWebManager getAgentManager() {
	//		return this.getChart().getAgentManager();
	//	}
	public String getBgColor() {
		return this.bgColor;
	}
	public String getSelectionColor() {
		return this.selColor;
	}

	public AmiWebChartGridPortlet getChart() {
		return chart;
	}

	private BasicIndexedList<Integer, AmiWebChartRenderingLayer> renderingLayers = new BasicIndexedList<Integer, AmiWebChartRenderingLayer>();
	private String ari;

	public void addRenderylingLayer(AmiWebChartRenderingLayer layer, int position) {
		int existing = renderingLayers.getPositionNoThrow(layer.getId());
		if (existing == -1)
			renderingLayers.add(layer.getId(), layer, position);
		else {
			renderingLayers.update(layer.getId(), layer);
			if (renderingLayers.getPosition(layer.getId()) != position) {
				renderingLayers.remove(layer.getId());
				renderingLayers.add(layer.getId(), layer, position);
			} else
				renderingLayers.update(layer.getId(), layer);
		}
		layer.addToDomManager();
		updateRenderLayerPositions();
		this.chart.flagConfigStale();
	}
	public void moveRenderingLayer(AmiWebChartRenderingLayer layer, int newPosition) {
		removeRenderyingLayer(layer.getId());
		addRenderylingLayer(layer, newPosition);
		flagConfigStale();
	}

	private void updateRenderLayerPositions() {
		for (int i = 0; i < this.renderingLayers.getSize(); i++)
			this.renderingLayers.getAt(i).setzPosition(i);
	}

	public IterableAndSize<AmiWebChartRenderingLayer> getRenderyingLayers() {
		return this.renderingLayers.values();
	}

	public int getLayerPosition(AmiWebChartRenderingLayer layer) {
		return this.renderingLayers.getPositionNoThrow(layer.getId());
	}

	public void processDataModel(AmiWebDm datamodel, CalcFrameStack sf) {
		clearSelected();
		for (AmiWebChartRenderingLayer layer : this.renderingLayers.values()) {
			layer.onDataModelChanged(datamodel);
			if (OH.eq(layer.getDmAliasDotName(), datamodel.getAmiLayoutFullAliasDotId())) {
				AmiWebDmTableSchema table = datamodel.getResponseOutSchema().getTable(layer.getDmTableName());
				if (table != null)
					layer.buildData(table, sf);
			}
		}
	}
	public void processDataModelRunningQuery(AmiWebDm datamodel, boolean isRequery) {
		if (isRequery)
			return;

		this.clearSelected();
		boolean hasClearedZoom = false;
		for (AmiWebChartRenderingLayer layer : this.renderingLayers.values()) {
			if (OH.eq(layer.getDmAliasDotName(), datamodel.getAmiLayoutFullAliasDotId())) {
				if (!hasClearedZoom) {
					clearZoom();
					hasClearedZoom = true;
				}
				layer.clearData();
				//TODO: clear out data
			}
		}
		if (getVisible())
			callJsFunction("clearPlot").end();
	}

	public void setBgColor(String backgroundColor) {
		if (OH.eq(this.bgColor, backgroundColor))
			return;
		this.bgColor = backgroundColor;
		flagConfigStale();
	}

	public void setSelectionColor(String selectionColor) {
		if (OH.eq(this.selColor, selectionColor))
			return;
		this.selColor = selectionColor;
		flagConfigStale();
	}

	public void needsRepaint() {
		flagViewStale();
	}
	public void flagViewStale() {
		setStatus(STATUS_VIEW_CHANGED);
	}
	public void flagConfigStale() {
		if (setStatus(STATUS_CONFIG_CHANGED))
			for (AmiWebChartRenderingLayer in : this.renderingLayers.values())
				in.flagDataStale();

	}
	public void flagFormulaChanged() {
		setStatus(STATUS_FORMULA_CHANGED);
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {

		BasicWebMenu m = new BasicWebMenu("", true);
		if (this.getService().getDesktop().getIsLocked())
			m.setStyle(this.getService().getDesktop().getMenuStyle());
		if ("onMouse".equals(callback)) {
			Integer x = CH.getOr(Caster_Integer.INSTANCE, attributes, "x", null);
			Integer y = CH.getOr(Caster_Integer.INSTANCE, attributes, "y", null);
			int button = CH.getOr(Caster_Integer.INSTANCE, attributes, "b", null);
			boolean ctrl = CH.getOr(Caster_Boolean.INSTANCE, attributes, "c", null);
			boolean shift = CH.getOr(Caster_Boolean.INSTANCE, attributes, "s", null);

			AmiWebChartZoomMetrics zm = getZoom();
			List<AmiWebChartShape> sink = new ArrayList<AmiWebChartShape>();
			for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
				AmiWebChartRenderingLayer<?> layer = this.renderingLayers.getAt(pos);
				for (AmiWebChartShape shape : layer.getCurrentShapes())
					if (shape.isValid() && shape.isSelectable() && shape.intersects(zm, x - 2, y - 2, 4, 4))
						sink.add(shape);
			}
			if (button == 1) {
				if (shift && this.selectedShapes != null) {
					sink.addAll(selectedShapes);
				}
				if (ctrl)
					this.reverseSelectedShapes(sink);
				else if (shift)
					this.addSelectedShapes(sink);
				else
					this.setSelectedShapes(sink);
			}
			if (button == 2) {
				if (shift && this.selectedShapes != null) {
					sink.addAll(selectedShapes);
				}
				if (ctrl)
					this.reverseSelectedShapes(sink);
				else if (shift)
					this.addSelectedShapes(sink);
				else {
					boolean isSelected = false;
					for (AmiWebChartShape i : sink) {
						if (isSelected(i)) {
							isSelected = true;
							break;
						}
					}
					if (!isSelected)
						this.setSelectedShapes(sink);
				}
				if (!this.selectedShapes.isEmpty()) {

					AmiWebDmManager dm = getService().getDmManager();
					IdentityHashSet<AmiWebDmTableSchema> selectedDataModels = new IdentityHashSet<AmiWebDmTableSchema>();
					IntSet layerPositions = new IntSet();
					for (AmiWebChartShape shape : this.selectedShapes)
						if (layerPositions.add(shape.getLayerPos()) && layerPositions.size() == this.renderingLayers.getSize())
							break;
					int[] layers = layerPositions.toIntArray();
					if (layers.length > 0) {
						for (int i : layers)
							selectedDataModels.add(renderingLayers.getAt(i).getSeries().getDataModelSchema());
						BasicMultiMap.Set<String, String> title2link = new BasicMultiMap.Set<String, String>();
						for (AmiWebDmLink link : this.chart.getDmLinksFromThisPortlet()) {
							if (selectedDataModels.contains(link.getSourceTable()))
								for (String title : SH.split('|', link.getTitle()))
									title2link.putMulti(title, link.getLinkUid());
						}

						for (String s : CH.sort(title2link.keySet())) {
							m.add(new BasicWebMenuLink(s, true, SH.join('_', title2link.get(s), new StringBuilder("query_")).toString()));
						}
					}
				}
				if (isZoomed()) {
					if (m.getChildrenCount() > 0)
						m.add(new BasicWebMenuDivider());
					m.add(new BasicWebMenuLink("Cancel Zoom", true, "cancel_zoom"));
				}
				if (!this.selectedShapes.isEmpty()) {
					if (m.getChildrenCount() > 0)
						m.add(new BasicWebMenuDivider());
					m.add(new BasicWebMenuLink("Clear Selection", true, "clear_selection"));
				}
				this.chart.addCustomMenuItems(m);
			}
			if (m.getChildren().size() > 0)
				getManager().showContextMenu(m, this);
			else if (getVisible())
				callJsFunction("clearSelectRegion").end();

		} else if ("select".equals(callback)) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			int w = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "w");
			int h = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "h");
			boolean ctrl = CH.getOr(Caster_Boolean.INSTANCE, attributes, "isCtrl", null);
			boolean shift = CH.getOr(Caster_Boolean.INSTANCE, attributes, "isShift", null);
			if (w < 0) {
				x += w;
				w = -w;
			}
			if (h < 0) {
				y += h;
				h = -h;
			}
			int foundCount = 0;
			AmiWebChartZoomMetrics zm = getZoom();
			for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
				AmiWebChartRenderingLayer<?> layer = this.renderingLayers.getAt(pos);
				for (AmiWebChartShape shape : layer.getCurrentShapes())
					if (shape.isValid() && shape.isSelectable() && shape.intersects(zm, x, y, w, h))
						foundCount++;
			}
			if (foundCount > 0) {
				m.add(new BasicWebMenuLink("Select these " + foundCount + " item(s)", true, "select_" + x + "_" + y + "_" + w + "_" + h + "_" + shift + "_" + ctrl));
			}
			if (Math.abs(w) > 5 && Math.abs(h) > 5)
				m.add(new BasicWebMenuLink("Zoom In", true, "zoom_" + x + "_" + y + "_" + w + "_" + h));
			if (isZoomed()) {
				if (m.getChildrenCount() > 0)
					m.add(new BasicWebMenuDivider());
				m.add(new BasicWebMenuLink("Cancel Zoom", true, "cancel_zoom"));
			}
			if (!this.selectedShapes.isEmpty()) {
				if (m.getChildrenCount() > 0)
					m.add(new BasicWebMenuDivider());
				m.add(new BasicWebMenuLink("Clear Selection", true, "clear_selection"));
			}
			getManager().showContextMenu(m, this);
		} else if ("hover".equals(callback)) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			try {
				produceHover(x, y);
			} catch (RuntimeException e) {
				LH.warning(log, "Error processing hover over at ", x, ",", y, ": ", e);
			}
		} else if ("zoomMove".equals(callback)) {
			int dx = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "dx");
			int dy = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "dy");
			this.chart.moveZoom(this.col, this.row, dx, dy);

			if (this.getVisible())
				callJsFunction("zoomMoveConsumed").end();
		} else if ("zoom".equals(callback)) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			int delta = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "delta");
			this.chart.zoomAtPoint(col, row, x, delta, y, delta);
			this.getChart().getAmiScriptCallbacks().execute(AmiWebChartGridPortlet.CALLBACK_NAME_ONZOOM, row, col);

		} else if ("onDragLegend".equals(callback)) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			int layerPos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "layerPos");
			AmiWebChartRenderingLayer_Legend legend = (AmiWebChartRenderingLayer_Legend) this.renderingLayers.getAt(layerPos);
			legend.getDragXPos().setOverride(x);
			legend.getDragYPos().setOverride(y);
		} else if ("onResizeLegend".equals(callback)) {
			int w = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "w");
			int h = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "h");
			int layerPos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "layerPos");
			AmiWebChartRenderingLayer_Legend legend = (AmiWebChartRenderingLayer_Legend) this.renderingLayers.getAt(layerPos);
			legend.setMaxHeight(h);
			legend.setMaxWidth(w);
		} else if ("onCheckboxLegend".equals(callback)) {
			int layerPos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "layerPos");
			boolean isChecked = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "checked");
			// grouping is used to identify which group by value was selected when user clicks on a checkbox. 
			//Ex: if in the plot editor I set the plot to group by region, then the legend will show all the regions (Caribbean, Europe...) of that layer. If Europe has the index of 1, then grouping would be 1. By default grouping is set to 0 (if no group by is set).
			int grouping = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "grouping", -1);
			int series = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "series", -1);
			AmiWebChartRenderingLayer_Legend layer = (AmiWebChartRenderingLayer_Legend) this.renderingLayers.getAt(layerPos);
			layer.onCheckbox(series, grouping, isChecked);
			return;
		} else
			super.handleCallback(callback, attributes);
	}
	private boolean isSelected(AmiWebChartShape i) {
		return this.selectedShapes.contains(i);
	}
	private AmiWebChartZoomMetrics getZoom() {
		if (zoomMetrics == null)
			this.zoomMetrics = this.chart.getZoom(col, row);
		return this.zoomMetrics;
	}

	private void produceHover(int x, int y) {
		AmiWebChartShape bestShape = null;
		double bestDistance = Double.NaN;
		AmiWebChartZoomMetrics zm = getZoom();
		for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
			AmiWebChartRenderingLayer<?> layer = this.renderingLayers.getAt(pos);
			for (AmiWebChartShape shape : layer.getCurrentShapes()) {
				if (shape.isValid() && shape.intersects(zm, x - 2, y - 2, 4, 4)) {
					double d = shape.getDistance(zm, x, y);
					if (MH.isNumber(d) && (bestShape == null || d <= bestDistance)) {
						bestDistance = d;
						bestShape = shape;
					}
				}
			}
		}
		if (bestShape == null) {
			callJsFunction("clearHover").end();
			return;
		}

		int layerPos = bestShape.getLayerPos();
		int groupid = bestShape.getGroupNum();
		int rowNum = bestShape.getRowNum();
		AmiWebChartSeries series = this.renderingLayers.getAt(layerPos).getSeries();
		Grouping group = series.getGroupById(groupid);
		String value = getService().cleanHtml(series.getTooltip(group.getOrigRows().get(rowNum), getChart().getStackFrame()));
		if (SH.is(value))
			callJsFunction("setHover").addParam(x).addParam(y).addParamQuoted(null).addParamQuoted(value).addParam(1).addParam(1).end();
	}
	@Override
	public void onMenuItem(String id) {
		if (getVisible())
			callJsFunction("clearSelectRegion").end();
		if (id.equals("cancel_zoom")) {
			clearZoom();
			this.getChart().getAmiScriptCallbacks().execute(AmiWebChartGridPortlet.CALLBACK_NAME_ONZOOM, row, col);
		} else if (id.startsWith("zoom_")) {
			String[] parts = SH.split("_", SH.stripPrefix(id, "zoom_", true));
			int x = SH.parseInt(parts[0]), y = SH.parseInt(parts[1]), w = SH.parseInt(parts[2]), h = SH.parseInt(parts[3]);
			zoom(x, y, w, h);
			//			Object[] args = { row, col };
			this.getChart().getAmiScriptCallbacks().execute(AmiWebChartGridPortlet.CALLBACK_NAME_ONZOOM, row, col);
		} else if (id.equals("clear_selection")) {
			setSelectedShapes(Collections.EMPTY_LIST);
		} else if (id.startsWith("select_")) {
			String[] parts = SH.split("_", SH.stripPrefix(id, "select_", true));
			int x = SH.parseInt(parts[0]), y = SH.parseInt(parts[1]), w = SH.parseInt(parts[2]), h = SH.parseInt(parts[3]);
			boolean shift = "true".equals(parts[4]);
			boolean ctrl = "true".equals(parts[5]);
			List<AmiWebChartShape> sink = new ArrayList<AmiWebChartShape>();
			AmiWebChartZoomMetrics zm = getZoom();
			for (int pos = 0; pos < this.renderingLayers.getSize(); pos++) {
				AmiWebChartRenderingLayer<?> layer = this.renderingLayers.getAt(pos);
				for (AmiWebChartShape shape : layer.getCurrentShapes())
					if (shape.isValid() && shape.isSelectable() && shape.intersects(zm, x, y, w, h))
						sink.add(shape);
			}
			if (ctrl)
				this.reverseSelectedShapes(sink);
			else if (shift)
				this.addSelectedShapes(sink);
			else
				this.setSelectedShapes(sink);
		} else if (id.startsWith("query_")) {
			String linkIds = SH.stripPrefix(id, "query_", true);
			for (String t : SH.split('_', linkIds)) {
				runAmiLink(t);
			}
		} else if (this.chart.isCustomContextMenuAction(id)) {
			this.chart.processCustomContextMenuAction(id);
		}
	}
	private void runAmiLink(String t) {
		AmiWebDmLink link = getService().getDmManager().getDmLink(t);
		AmiWebDmUtils.sendRequest(getService(), link);
	}

	private void clearZoom() {
		this.chart.setZoomAndOffset(col, row, 1d, 0d, 1d, 0d);
	}

	public void clearSelected() {
		if (this.selectedShapes.isEmpty())
			return;
		this.selectedShapes.clear();
		onSelectedChanged();
	}

	private void setSelectedShapes(List<AmiWebChartShape> ss) {
		if (this.selectedShapes.containsAll(ss) && ss.size() == this.selectedShapes.size())
			return;
		this.selectedShapes.clear();
		this.selectedShapes.addAll(ss);
		onSelectedChanged();
	}
	private void addSelectedShapes(List<AmiWebChartShape> ss) {
		if (ss.isEmpty())
			return;
		if (this.selectedShapes.addAll(ss))
			onSelectedChanged();
	}
	private void reverseSelectedShapes(List<AmiWebChartShape> ss) {
		if (ss.isEmpty())
			return;
		if (this.selectedShapes.isEmpty()) {
			this.selectedShapes.addAll(ss);
		} else {
			for (AmiWebChartShape i : ss) {
				if (!this.selectedShapes.remove(i))
					this.selectedShapes.add(i);
			}
		}
		onSelectedChanged();
	}

	private void onSelectedChanged() {
		this.chart.onSelectChanged(this);
		setStatus(STATUS_SELECT_CHANGED);
		this.getChart().getAmiScriptCallbacks().execute(AmiWebChartGridPortlet.CALLBACK_DEF_ONSELECTED.getMethodName());
		for (AmiWebDmLink link : chart.getDmLinksFromThisPortlet())
			if (link.isRunOnSelect())
				for (AmiWebChartRenderingLayer i : this.renderingLayers.values())
					if (OH.eq(link.getSourceDmAliasDotName(), i.getDmAliasDotName()))
						runAmiLink(link.getLinkUid());
	}

	private void zoom(int x, int y, int w, int h) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		chart.setZoomClip(col, row, x, x + w, y, y + h);
	}

	@Override
	public void onMenuDismissed() {
		if (getVisible())
			callJsFunction("clearSelectRegion").end();
	}

	//TODO: cache this!
	public AmiWebChartSeries getSeries(int i) {
		for (AmiWebChartRenderingLayer rl : this.renderingLayers.values()) {
			AmiWebChartSeries series = rl.getSeries();
			if (series != null && series.getId() == i)
				return series;
		}
		return null;
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		this.chart.onPlotSizeChanged(this.col, this.row, width, height);
		for (AmiWebChartRenderingLayer i : this.renderingLayers.values())
			i.onSizeChanged(width, height);
	}
	public boolean isZoomed() {
		AmiWebChartZoomMetrics zm = getZoom();
		return zm.getPosZoomX() > 1 || zm.getPosZoomY() > 1;
	}

	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.bgColor, sink);
		AmiWebUtils.getColors(this.selColor, sink);
		for (AmiWebChartRenderingLayer i : getRenderyingLayers())
			i.getUsedColors(sink);
	}

	public boolean getShowConfigButtons() {
		return getChart().getShowConfigButtons();
	}

	public void setShowConfigButtons(boolean showConfigButtons) {

	}

	public void onUsedZoomed(AmiWebChartZoom zoomX, AmiWebChartZoom zoomY) {
		if (zoomMetrics == null || zoomMetrics.getPosZoomX() != zoomX.getZoom() || zoomMetrics.getPosZoomY() != zoomY.getZoom())
			for (AmiWebChartRenderingLayer i : this.renderingLayers.values())
				i.onZoomChanged();
		this.zoomMetrics = null;
		flagViewStale();
	}
	private boolean setStatus(byte status) {
		if (status <= this.status)
			return false;
		this.status = status;
		flagPendingAjax();
		return true;
	}

	public boolean hasSelectedRows(AmiWebDmLink link) {
		if (this.selectedShapes.isEmpty())
			return false;
		AmiWebDmTableSchema sourceDm = link.getSourceTable();
		for (AmiWebChartShape i : this.selectedShapes)
			if (this.renderingLayers.getAt(i.getLayerPos()).getDataModelSchema() == sourceDm)
				return true;
		return false;
	}

	public void getSelectedRows(AmiWebDmLink link, byte type, Table r) {
		if (type == AmiWebPortlet.SELECTED) {
			AmiWebDmTableSchema sourceDm = link.getSourceTable();
			HasherSet<Row> allRows = new HasherSet<Row>(IdentityHasher.INSTANCE);
			for (AmiWebChartShape i : this.selectedShapes) {
				AmiWebChartRenderingLayer layer = this.renderingLayers.getAt(i.getLayerPos());
				if (layer.getDataModelSchema() == sourceDm) {
					Grouping group = layer.getSeries().getGroupById(i.getGroupNum());
					List<Row> rows = group.getOrigRows();
					Row row = rows.get(i.getRowNum());
					if (allRows.add(row))
						r.getRows().addRow(row.getValuesCloned());
				}
			}
		} else if (type == AmiWebPortlet.ALL) {
			AmiWebDmTableSchema sourceDm = link.getSourceTable();
			HasherSet<Row> allRows = new HasherSet<Row>(IdentityHasher.INSTANCE);
			for (AmiWebChartRenderingLayer layer : this.getRenderyingLayers()) {
				if (layer.getDataModelSchema() == sourceDm) {
					for (Grouping group : layer.getSeries().getUserSelectedGroupings().values()) {
						List<Row> rows = group.getOrigRows();
						for (Row row : rows)
							if (allRows.add(row))
								r.getRows().addRow(row.getValuesCloned());
					}
				}
			}
		}
	}
	public int getSelectedCount() {
		return this.selectedShapes.size();
	}
	public Set<AmiWebChartShape> getSelected() {
		return this.selectedShapes;
	}

	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		for (Entry<Integer, AmiWebChartRenderingLayer> i : this.renderingLayers)
			i.getValue().onDmNameChanged(oldAliasDotName, dm);
	}

	public void setSelectionBoxColor(String s) {
		if (OH.eq(this.selBoxFillColor, s))
			return;
		this.selBoxFillColor = s;
		flagConfigStale();
	}

	public void setSelectionBoxBorderColor(String s) {
		if (OH.eq(this.selBoxBorderColor, s))
			return;
		this.selBoxBorderColor = s;
		flagConfigStale();
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_CHART_PLOT;
	}
	@Override
	public String getAri() {
		return this.ari;
	}

	@Override
	public String getDomLabel() {
		return this.getXPos() + "," + getYPos();
	}

	private int getXPos() {
		return this.chart.getXPos(this);
	}
	private int getYPos() {
		return this.chart.getYPos(this);
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return (List) CH.l(this.getRenderyingLayers());
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getChart();
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebChartPlotPortlet.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		String domLabel = this.getDomLabel();
		this.amiLayoutFullAlias = this.chart.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.chart.getAmiLayoutFullAliasDotId();
		this.ari = AmiWebDomObject.ARI_TYPE_CHART_PLOT + ":" + amiLayoutFullAliasDotId + "?" + domLabel;
		chart.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		for (AmiWebChartRenderingLayer i : this.getRenderyingLayers())
			i.updateAri();
	}
	@Override
	public void onClosed() {
		this.removeFromDomManager();
		super.onClosed();
		for (AmiWebChartRenderingLayer i : this.getRenderyingLayers())
			i.onClosed();
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	@Override
	public boolean isTransient() {
		return this.getChart().isTransient();
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.chart.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	private boolean isInitDone = false;

	public void onAmiInitDone() {
		if (this.isInitDone)
			throw new IllegalStateException("already init done");
		this.isInitDone = true;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
