package com.f1.ami.web.charts;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.IterableAndSize;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabManager;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;

public class AmiWebChartEditLayersPortlet extends GridPortlet implements FormPortletListener, TabManager, ConfirmDialogListener {

	private TabPortlet tabsPortlet;
	private AmiWebHeaderPortlet header;
	private AmiWebService service;
	private IntKeyMap<AmiWebChartEditLayerPortlet<?>> seriesEditors = new IntKeyMap<AmiWebChartEditLayerPortlet<?>>();
	private FormPortlet buttonsPortlet;
	private FormPortletButton okButton;
	private FormPortletButton previewButton;
	final private FormPortletButton addRadialLayerButton;
	final private FormPortletButton add2dLayerButton;
	final private FormPortletButton addLegendLayerButton;
	final private AmiWebChartPlotPortlet plot;
	final private GridPortlet editGrid;
	private Map<Tab, AmiWebChartRenderingLayer<? extends AmiWebChartSeries>> tabs2Layers = new HashMap<Tab, AmiWebChartRenderingLayer<? extends AmiWebChartSeries>>();

	public AmiWebChartEditLayersPortlet(PortletConfig config, AmiWebChartPlotPortlet plot) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.plot = plot;
		this.editGrid = new GridPortlet(generateConfig());
		this.header = (AmiWebHeaderPortlet) addChild(new AmiWebHeaderPortlet(generateConfig()), 0, 0, 1, 1).getPortlet();
		this.header.setShowSearch(false);
		this.header.getBarFormPortlet().addFormPortletListener(this);
		addRadialLayerButton = new FormPortletButton("Add Radial").setCssStyle("_cn=ami_chart_addradial|_fg=#000000");
		add2dLayerButton = new FormPortletButton("Add 2D").setCssStyle("_cn=ami_chart_addchart|_fg=#000000");
		addLegendLayerButton = new FormPortletButton("Add Legend").setCssStyle("_cn=ami_chart_addlegend|_fg=#000000");
		this.header.getBarFormPortlet().addButton(add2dLayerButton);
		this.header.getBarFormPortlet().addButton(addRadialLayerButton);
		this.header.getBarFormPortlet().addButton(addLegendLayerButton);
		header.updateBarPortletLayout(add2dLayerButton.getHtmlLayoutSignature() + addRadialLayerButton.getHtmlLayoutSignature() + addLegendLayerButton.getHtmlLayoutSignature());
		tabsPortlet = new TabPortlet(generateConfig());
		this.tabsPortlet.setIsCustomizable(false);
		this.tabsPortlet.setTabManager(this);
		this.tabsPortlet.getTabPortletStyle().setHasMenuAlways(true);
		this.buttonsPortlet = new FormPortlet(generateConfig());
		this.addChild(this.tabsPortlet, 0, 1);
		this.addChild(this.buttonsPortlet, 0, 2);
		this.setRowSize(2, 40);
		this.okButton = this.buttonsPortlet.addButton(new FormPortletButton("OK"));
		this.previewButton = this.buttonsPortlet.addButton(new FormPortletButton("Apply"));
		this.buttonsPortlet.addFormPortletListener(this);
		for (AmiWebChartRenderingLayer<?> i : this.plot.getRenderyingLayers())
			addSeriesEditor(i);
		setSuggestedSize(990, 750);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.add2dLayerButton) {
			final AmiWebChartRenderingLayer_Graph layer = new AmiWebChartRenderingLayer_Graph(this.plot);
			AmiWebDmTableSchema dm = getLatestDatamodel();
			if (dm == null)
				;
			else
				layer.setDm(dm.getDm().getAmiLayoutFullAliasDotId(), dm.getName());
			AmiWebChartGridPortlet chart = this.plot.getChart();
			final AmiWebChartSeries_Graph series = new AmiWebChartSeries_Graph(this.service, chart, layer.getDataModelSchema(), layer);
			//			series.setName("Series");
			series.setId(plot.getChart().getNextId());
			layer.setId(plot.getChart().getNextId());
			AmiWebChartAxisPortlet xAxis;
			int chartCol = this.plot.getCol();
			if (chart.getBottomAxisCountAtCol(chartCol) > 0) { // Check if there is a bottom axis 
				xAxis = chart.getAxis(AmiWebChartGridPortlet.POS_B, chartCol, 0);
			} else if (chart.getTopAxisCountAtCol(chartCol) > 0) { // Check if there is a top axis 
				xAxis = chart.getAxis(AmiWebChartGridPortlet.POS_T, chartCol, 0);
			} else { // Otherwise, add new axis to bottom
				xAxis = chart.addAxis(AmiWebChartGridPortlet.POS_B, chartCol, 0);
			}
			AmiWebChartAxisPortlet yAxis;
			int chartRow = this.plot.getRow();
			if (chart.getLeftAxisCountAtRow(chartRow) > 0) { // Check if there is a left axis
				yAxis = chart.getAxis(AmiWebChartGridPortlet.POS_L, chartRow, 0);
			} else if (chart.getRightAxisCountAtRow(chartRow) > 0) { // Check if there is a right axis 
				yAxis = chart.getAxis(AmiWebChartGridPortlet.POS_R, chartRow, 0);
			} else { // Otherwise, add new axis to left
				yAxis = chart.addAxis(AmiWebChartGridPortlet.POS_L, chartRow, 0);
			}
			layer.setXAxis(xAxis.getAxisId());
			layer.setYAxis(yAxis.getAxisId());
			layer.setName(SH.getNextId("Layer", this.tabsPortlet.getTabTitles(), 2));
			this.plot.addRenderylingLayer(layer, this.plot.getRenderyingLayersCount());
			layer.setSeries(series);
			this.addSeriesEditor(layer);
		} else if (button == this.addRadialLayerButton) {
			AmiWebDmTableSchema dm = getLatestDatamodel();
			AmiWebChartRenderingLayer_RadialGraph layer = new AmiWebChartRenderingLayer_RadialGraph(plot);
			layer.setCenterXPos(.5);
			layer.setCenterYPos(.5);
			layer.setId(plot.getChart().getNextId());
			plot.addRenderylingLayer(layer, this.plot.getRenderyingLayersCount());
			layer.setDm(dm.getDm().getAmiLayoutFullAliasDotId(), dm.getName());
			layer.setName(SH.getNextId("Radial", this.tabsPortlet.getTabTitles(), 2));
			AmiWebChartSeries_RadialGraph series = new AmiWebChartSeries_RadialGraph(this.service, layer.getChart(), layer.getDataModelSchema(), layer);
			series.setId(plot.getChart().getNextId());
			//			series.setName("Series");
			layer.setSeries(series);
			this.addSeriesEditor(layer);
		} else if (button == this.addLegendLayerButton) {
			AmiWebChartRenderingLayer_Legend layer = new AmiWebChartRenderingLayer_Legend(this.plot);
			layer.setName(SH.getNextId("Legend", this.tabsPortlet.getTabTitles(), 2));
			layer.setId(plot.getChart().getNextId());
			plot.addRenderylingLayer(layer, this.plot.getRenderyingLayersCount());
			for (AmiWebChartRenderingLayer<?> l : this.plot.getRenderyingLayers()) {
				if (!(l instanceof AmiWebChartRenderingLayer_Legend))
					layer.addSeries(l.getSeries().getId());
			}
			this.addSeriesEditor(layer);
		} else if (button == this.previewButton || button == this.okButton) {
			for (AmiWebChartEditLayerPortlet<?> i : this.seriesEditors.values())
				if (i.preview() && button == this.okButton)
					close();
		}
	}

	private AmiWebDmTableSchema getLatestDatamodel() {
		String dmId = CH.first(this.plot.getChart().getUsedDmAliasDotNames());
		String tableName = CH.first(this.plot.getChart().getUsedDmTables(dmId));
		return this.plot.getService().getDmManager().getDmByAliasDotName(dmId).getResponseOutSchema().getTable(tableName);
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	private AmiWebChartEditLayerPortlet<?> addSeriesEditor(AmiWebChartRenderingLayer i) {
		Tab newTab = null;
		if (i.getSeries() != null && i.getSeries().clearOverrides()) {
			i.getSeries().buildData(i.getDataModelSchema(), i.getSeries().getPortlet().getStackFrame());
			i.flagDataStale();
		}
		if (i instanceof AmiWebChartRenderingLayer_Graph) {
			AmiWebChartRenderingLayer_Graph i2 = (AmiWebChartRenderingLayer_Graph) i;
			AmiWebChartEditLayerPortlet_Graph seriesEditor = new AmiWebChartEditLayerPortlet_Graph(generateConfig(), i2, i2.getSeries());
			this.getManager().onPortletAdded(seriesEditor);
			this.seriesEditors.put(i.getId(), seriesEditor);
			newTab = this.tabsPortlet.addChild(i.getName(), seriesEditor);
			this.tabsPortlet.setActiveTab(newTab.getPortlet());
			this.tabs2Layers.put(newTab, i2);
			i2.setName(newTab.getTitle());
			applyLayerNameToSeries(i2);
			return seriesEditor;
		} else if (i instanceof AmiWebChartRenderingLayer_RadialGraph) {
			AmiWebChartRenderingLayer_RadialGraph i2 = (AmiWebChartRenderingLayer_RadialGraph) i;
			AmiWebChartEditLayerPortlet_RadialGraph seriesEditor = new AmiWebChartEditLayerPortlet_RadialGraph(generateConfig(), i2, i2.getSeries());
			this.getManager().onPortletAdded(seriesEditor);
			this.seriesEditors.put(i.getId(), seriesEditor);
			newTab = this.tabsPortlet.addChild(i.getName(), seriesEditor);
			this.tabsPortlet.setActiveTab(newTab.getPortlet());
			this.tabs2Layers.put(newTab, i2);
			i2.setName(newTab.getTitle());
			applyLayerNameToSeries(i2);
			return seriesEditor;
		} else if (i instanceof AmiWebChartRenderingLayer_Legend) {
			AmiWebChartRenderingLayer_Legend i2 = (AmiWebChartRenderingLayer_Legend) i;
			AmiWebChartEditRenderingLayerPortlet_Legend editor = new AmiWebChartEditRenderingLayerPortlet_Legend(generateConfig(), this.plot, i2);
			AmiWebChartEditLayerPortlet<AmiWebChartSeries> seriesEditor = new AmiWebChartEditLayerPortlet<AmiWebChartSeries>(generateConfig(), i2, null, editor);
			this.getManager().onPortletAdded(seriesEditor);
			this.seriesEditors.put(i.getId(), seriesEditor);
			newTab = this.tabsPortlet.addChild(i.getName(), seriesEditor);
			this.tabsPortlet.setActiveTab(newTab.getPortlet());
			this.tabs2Layers.put(newTab, i2);
			return seriesEditor;
		}

		return null;
	}

	private void applyLayerNameToSeries(AmiWebChartRenderingLayer<?> layer) {
		if (layer == null)
			return;
		AmiWebChartSeries s = layer.getSeries();
		s.setName(layer.getName());
		for (AmiWebChartRenderingLayer<?> l : this.plot.getRenderyingLayers())
			if (l instanceof AmiWebChartRenderingLayer_Legend) {
				// Add layer/series to legend
				l.flagViewStale();
				AmiWebChartEditLayerPortlet<?> legendEditor = this.seriesEditors.get(l.getId());
				if (legendEditor != null)
					((AmiWebChartEditRenderingLayerPortlet_Legend) legendEditor.getEditRenderingLayerPortlet()).addSeriesField(layer);
			}
	}

	@Override
	public void onClosed() {
		super.onClosed();
	}

	@Override
	public void onUserMenu(TabPortlet tabPortlet, Tab tab, String menuId) {
		AmiWebChartRenderingLayer<? extends AmiWebChartSeries> layer = this.tabs2Layers.get(tab);
		int origLayerPos = this.plot.getLayerPosition(layer);
		IterableAndSize<AmiWebChartRenderingLayer> layers = this.plot.getRenderyingLayers();
		if ("rename".equals(menuId)) {
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "", ConfirmDialogPortlet.TYPE_OK_CANCEL, tabPortlet, new FormPortletTextField("Layer Id:"));
			String origTitle = tabPortlet.getSelectedTab().getTitle();
			cdp.setInputFieldValue(origTitle);
			cdp.setCallback("rename_layer_confirm");
			cdp.getInputField().focus();
			cdp.addDialogListener(this);
			((FormPortletTextField) cdp.getInputField()).setSelection(0, origTitle.length());
			getManager().showDialog("Rename Layer", cdp);
		} else if ("copy".equals(menuId)) {
			addCopyLayer(layer);
		} else if ("delete".equals(menuId)) {
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete this layer?", ConfirmDialogPortlet.TYPE_OK_CANCEL, tabPortlet);
			cdp.setCallback("delete_layer_confirm");
			cdp.addDialogListener(this);
			getManager().showDialog("Delete Layer", cdp);
		} else if ("back".equals(menuId)) {
			if (origLayerPos < layers.size() - 1) {
				this.plot.moveRenderingLayer(layer, layers.size() - 1);
				this.tabsPortlet.moveTabRightmost(tab);
			}
		} else if ("front".equals(menuId)) {
			if (origLayerPos > 0) {
				this.plot.moveRenderingLayer(layer, 0);
				this.tabsPortlet.moveTabLeftmost(tab);
			}
		} else if ("backward".equals(menuId)) {
			if (origLayerPos < layers.size() - 1) {
				this.plot.moveRenderingLayer(layer, origLayerPos + 1);
				this.tabsPortlet.moveTabRight(tab);
			}
		} else if ("forward".equals(menuId)) {
			if (origLayerPos > 0) {
				this.plot.moveRenderingLayer(layer, origLayerPos - 1);
				this.tabsPortlet.moveTabLeft(tab);
			}
			//		} else if ("export_import_style".equals(menuId)) {
			//			this.seriesEditors.get(layer.getId()).getEditRenderingLayerPortlet().getEditStylePortlet().showExportImport();
		} else if ("export_import_layer".equals(menuId)) {
			Portlet portlet = this.tabsPortlet.getSelectedTab().getPortlet();
			if (portlet instanceof AmiWebChartEditLayerPortlet) {
				getManager().showDialog("Export/Import Layer", new AmiWebChartLayerExportPortlet(generateConfig(), layer, (AmiWebChartEditLayerPortlet) portlet));
			}
		}

	}
	@Override
	public WebMenu createMenu(TabPortlet tabPortlet, Tab tab) {
		BasicWebMenu r = new BasicWebMenu();
		int cnt = tabPortlet.getChildrenCount();
		int pos = tab.getLocation();
		r.addChild(new BasicWebMenuLink("Rename Layer Id", true, "rename"));
		r.addChild(new BasicWebMenuLink("Copy Layer", true, "copy"));
		r.addChild(new BasicWebMenuLink("Delete Layer", cnt > 1, "delete"));
		r.addChild(new BasicWebMenuLink("Move Layer To Back", pos != cnt - 1, "back"));
		r.addChild(new BasicWebMenuLink("Move Layer To Front", pos != 0, "front"));
		r.addChild(new BasicWebMenuLink("Move Layer Backward", pos < cnt - 1, "backward"));
		r.addChild(new BasicWebMenuLink("Move Layer Forward", pos > 0, "forward"));
		//		r.addChild(new BasicWebMenuLink("Export/Import Style", true, "export_import_style"));
		r.addChild(new BasicWebMenuLink("Export/Import Layer", true, "export_import_layer"));
		return r;
	}

	@Override
	public void onUserAddTab(TabPortlet tabPortlet) {

	}

	@Override
	public void onUserRenamedTab(TabPortlet tabPortlet, Tab tab, String newName) {
		tab.setTitle(newName);
		AmiWebChartRenderingLayer<? extends AmiWebChartSeries> layer = this.tabs2Layers.get(tab);
		layer.setName(newName);
		AmiWebChartSeries s = layer.getSeries();
		s.setName(layer.getName());
		for (AmiWebChartRenderingLayer<?> l : this.plot.getRenderyingLayers())
			if (l instanceof AmiWebChartRenderingLayer_Legend) {
				l.flagViewStale();
				((AmiWebChartEditRenderingLayerPortlet_Legend) this.seriesEditors.get(l.getId()).getEditRenderingLayerPortlet()).renameSeriesField(layer);
			}
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (ConfirmDialog.ID_YES.equals(id)) {
			String callback = source.getCallback();
			Tab selectedTab = this.tabsPortlet.getSelectedTab();
			if ("rename_layer_confirm".equals(callback)) {
				String newName = (String) source.getInputFieldValue();
				if (SH.isnt(newName)) {
					getManager().showAlert("Layer name required");
					return false;
				}
				for (Entry<Tab, AmiWebChartRenderingLayer<? extends AmiWebChartSeries>> i : this.tabs2Layers.entrySet()) {
					if (OH.eq(i.getKey().getTitle(), newName) && i.getKey() != selectedTab) {
						getManager().showAlert("Layer name already exists");
						return false;
					}
				}
				selectedTab.setTitle(newName);
				this.tabs2Layers.get(selectedTab).setName(newName);
				return true;
			} else if ("delete_layer_confirm".equals(callback)) {
				AmiWebChartRenderingLayer<? extends AmiWebChartSeries> removedLayer = this.tabs2Layers.get(selectedTab);
				removedLayer.setDm(null, null);
				this.tabs2Layers.remove(selectedTab);
				this.plot.removeRenderyingLayer(removedLayer.getId());
				removedLayer.onClosed();
				this.tabsPortlet.removeTab(selectedTab);
				for (AmiWebChartRenderingLayer<?> l : this.plot.getRenderyingLayers())
					if (l instanceof AmiWebChartRenderingLayer_Legend)
						((AmiWebChartEditRenderingLayerPortlet_Legend) this.seriesEditors.get(l.getId()).getEditRenderingLayerPortlet()).removeSeriesField(removedLayer);
				this.seriesEditors.remove(removedLayer.getId());
			}
		}
		source.closeDialog();
		return false;
	}
	private <T extends AmiWebChartSeries> void addCopyLayer(AmiWebChartRenderingLayer<T> layer) {
		AmiWebChartRenderingLayer<T> copyLayer = layer.copy();
		copyLayer.setName(SH.getNextId(layer.getName(), this.tabsPortlet.getTabTitles(), 2));
		copyLayer.setDm(layer.getDmAliasDotName(), layer.getDmTableName());
		copyLayer.setId(this.plot.getChart().getNextId());
		//		int oldSeriesId = copyLayer.getSeries().getId();
		//		T copySeries = copyLayer.removeSeriesById(oldSeriesId);
		T copySeries = copyLayer.getSeries();
		copySeries.setId(this.plot.getChart().getNextId());
		//		copyLayer.addSeries(copySeries, 0);
		this.plot.addRenderylingLayer(copyLayer, this.plot.getRenderyingLayersCount());
		addSeriesEditor(copyLayer);
	}

	public Portlet setActiveLayer(String name) {
		for (Entry<Tab, AmiWebChartRenderingLayer<? extends AmiWebChartSeries>> i : this.tabs2Layers.entrySet()) {
			if (OH.eq(i.getValue().getName(), name)) {
				this.tabsPortlet.setActiveTab(i.getKey().getPortlet());
				return i.getKey().getPortlet();
			}
		}
		return null;
	}
}
