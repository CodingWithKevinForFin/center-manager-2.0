package com.f1.ami.web.dm.portlets.vizwiz;

import java.util.Set;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartEditLayerPortlet;
import com.f1.ami.web.charts.AmiWebChartEditLayerPortlet_Graph;
import com.f1.ami.web.charts.AmiWebChartEditLayerPortlet_RadialGraph;
import com.f1.ami.web.charts.AmiWebChartEditSeriesPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Graph;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_RadialGraph;
import com.f1.ami.web.charts.AmiWebChartSeries_Graph;
import com.f1.ami.web.charts.AmiWebChartSeries_RadialGraph;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.Row;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.CH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebVizwiz_Chart extends AmiWebVizwiz<AmiWebChartGridPortlet> implements TileFormatter {

	private TabPortlet creatorTabsPortlet;
	private TilesPortlet chartTiles;
	private final Set<String> RADIALS = CH.s(AmiWebChartEditSeriesPortlet.TYPE_RADIAL_ADVANCED, AmiWebChartEditSeriesPortlet.TYPE_RADIAL_PIE,
			AmiWebChartEditSeriesPortlet.TYPE_RADIAL_BAR, AmiWebChartEditSeriesPortlet.TYPE_RADIAL_SPEEDOMETER);
	private AmiWebChartEditLayerPortlet<?> layerEditor;
	public int creatorPortletWidth;

	public AmiWebVizwiz_Chart(AmiWebService service, String layoutAlias) {
		super(service, "Chart");
		AmiWebChartGridPortlet tm = (AmiWebChartGridPortlet) service.getDesktop().newPortlet(AmiWebChartGridPortlet.Builder.ID, layoutAlias);
		setPreviewPortlet(tm);
		//		AmiCenterGetAmiSchemaResponse response = getService().nw(AmiCenterGetAmiSchemaResponse.class);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		RootPortlet root = (RootPortlet) service.getPortletManager().getRoot();
		// roughly 50% of the editor (NOT screen) size
		int width = (int) (root.getWidth() * 0.39);
		setCreatorPortletWidth(width);
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(tm.getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addRefreshButton();
	}
	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	public boolean preview() {
		AmiWebChartEditSeriesPortlet<?> activeEditor = layerEditor.getActiveEditor();
		activeEditor.fillDefaultFields();
		if (this.layerEditor == null)
			return true;
		return this.layerEditor.preview();
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmTableName) {
		AmiWebChartGridPortlet previewPortlet = getPreviewPortlet();
		AmiWebChartGridPortlet tm = getPreviewPortlet();
		previewPortlet.addUsedDm(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		previewPortlet.setAmiTitle(dmTableName, false);
		AmiWebDmTableSchema dmTable = dm.getResponseOutSchema().getTable(dmTableName);
		AmiWebChartPlotPortlet plot = tm.addPlot(0, 0);
		Row tile = this.chartTiles.getActiveTile();
		String id;
		if (tile != null)
			id = tile.get("Id", Caster_String.INSTANCE);
		else
			id = AmiWebChartEditSeriesPortlet.TYPE_2D_ADVANCED;
		boolean isRadial = RADIALS.contains(id);
		AmiWebChartEditLayerPortlet<?> layerEditor;
		if (isRadial) {
			AmiWebChartRenderingLayer_RadialGraph layer = new AmiWebChartRenderingLayer_RadialGraph(plot);
			layer.setCenterXPos(.5);
			layer.setCenterYPos(.5);
			layer.setName("Layer");
			plot.addRenderylingLayer(layer, 0);
			layer.setDm(dm.getAmiLayoutFullAliasDotId(), dmTable.getName());
			AmiWebChartSeries_RadialGraph series = new AmiWebChartSeries_RadialGraph(this.getService(), this.getPreviewPortlet(), dmTable, layer);
			series.setName("Series");
			layer.setSeries(series, null);
			layerEditor = new AmiWebChartEditLayerPortlet_RadialGraph(generateConfig(), layer, series);
		} else {
			AmiWebChartRenderingLayer_Graph layer = new AmiWebChartRenderingLayer_Graph(plot);
			layer.setName("Layer");
			AmiWebChartAxisPortlet yAxis = tm.addAxis(AmiWebChartGridPortlet.POS_L, 0, 0);
			AmiWebChartAxisPortlet xAxis = tm.addAxis(AmiWebChartGridPortlet.POS_B, 0, 0);
			plot.addRenderylingLayer(layer, 0);
			layer.setDm(dm.getAmiLayoutFullAliasDotId(), dmTable.getName());
			AmiWebChartSeries_Graph series = new AmiWebChartSeries_Graph(this.getService(), this.getPreviewPortlet(), dmTable, layer);
			series.setName("Series");
			layer.setXAxis(xAxis.getAxisId());
			layer.setYAxis(yAxis.getAxisId());
			layer.setSeries(series, null);
			layerEditor = new AmiWebChartEditLayerPortlet_Graph(generateConfig(), layer, series);
		}
		layerEditor.setActiveEditor(id);
		this.layerEditor = layerEditor;
		this.creatorTabsPortlet.addChild(0, "Chart", layerEditor);
		this.creatorTabsPortlet.setActiveTab(layerEditor);

		previewPortlet.onDmDataChanged(dm);
		return true;
	}
	@Override
	public Portlet getInitForm(AmiWebDm dm, String tableName) {

		GridPortlet grid = new GridPortlet(generateConfig());
		TilesPortlet chartTiles = new TilesPortlet(generateConfig());
		chartTiles.setTable(new BasicSmartTable(new BasicTable(new String[] { "name", "img", "Id" })));
		chartTiles.setTileFormatter(this);
		chartTiles.addRow("Advanced Chart", "advanced_chart.png", AmiWebChartEditSeriesPortlet.TYPE_2D_ADVANCED);
		chartTiles.addRow("Advanced Radial", "advanced_radial_chart.png", AmiWebChartEditSeriesPortlet.TYPE_RADIAL_ADVANCED);
		chartTiles.addRow("Area Chart", "area_chart.png", AmiWebChartEditSeriesPortlet.TYPE_2D_AREA);
		chartTiles.addRow("V Bar Chart", "bar_chart_v.png", AmiWebChartEditSeriesPortlet.TYPE_2D_BAR_V);
		chartTiles.addRow("H Bar Chart", "bar_chart_h.png", AmiWebChartEditSeriesPortlet.TYPE_2D_BAR_H);
		chartTiles.addRow("Line Chart", "line_chart.png", AmiWebChartEditSeriesPortlet.TYPE_2D_LINE);
		chartTiles.addRow("Pie Chart", "pie_chart.png", AmiWebChartEditSeriesPortlet.TYPE_RADIAL_PIE);
		chartTiles.addRow("Radial Bar Chart", "radial_bar_chart.png", AmiWebChartEditSeriesPortlet.TYPE_RADIAL_BAR);
		chartTiles.addRow("Scatter Plot", "scatter_plot.png", AmiWebChartEditSeriesPortlet.TYPE_2D_SCATTER);
		chartTiles.addRow("Speedometer", "speedometer.png", AmiWebChartEditSeriesPortlet.TYPE_RADIAL_SPEEDOMETER);

		chartTiles.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#4c4c4c");
		chartTiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, 115);
		chartTiles.addOption(TilesPortlet.OPTION_TILE_HEIGHT, 100);
		chartTiles.addOption(TilesPortlet.OPTION_TILE_PADDING, 2);
		chartTiles.addOption(TilesPortlet.OPTION_ALIGN, TilesPortlet.VALUE_ALIGN_JUSTIFY);
		chartTiles.setMultiselectEnabled(false);

		grid.setSuggestedSize(1185, 45);
		grid.addChild(chartTiles, 0, 0);
		this.chartTiles = chartTiles;
		return grid;
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		String img = tile.get("img", Caster_String.INSTANCE);
		styleSink.append(
				"_fs=12|_fm=bold|_bg=white|_fg=#004400|style.backgroundRepeat=no-repeat|style.justifyContent=center|style.backgroundPosition=center|style.display=flex|style.alignItems=flex-end|style.backgroundImage=url('rsc/ami/chart_style_icons/"
						+ img + "')");
		sink.append(tile.get("name", Caster_String.INSTANCE));
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {

	}
	@Override
	public int getCreatorPortletWidth() {
		return creatorPortletWidth;
	}

	public void setCreatorPortletWidth(int creatorPortletWidth) {
		this.creatorPortletWidth = creatorPortletWidth;
	}

	public TilesPortlet getChartTiles() {
		return chartTiles;
	}
	public AmiWebChartEditLayerPortlet<?> getLayerEditor() {
		return layerEditor;
	}

}
