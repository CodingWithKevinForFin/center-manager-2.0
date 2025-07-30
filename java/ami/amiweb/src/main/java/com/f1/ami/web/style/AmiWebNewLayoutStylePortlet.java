package com.f1.ami.web.style;

import java.util.Comparator;
import java.util.Map;
import java.util.Random;

import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebAbstractTablePortlet;
import com.f1.ami.web.AmiWebDatasourceTablePortlet;
import com.f1.ami.web.AmiWebDividerPortlet;
import com.f1.ami.web.AmiWebFormatterManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Graph;
import com.f1.ami.web.charts.AmiWebChartSeries_Graph;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class AmiWebNewLayoutStylePortlet extends GridPortlet implements TilesListener, TileFormatter, FormPortletListener {

	private static final String STYLE_ID = "style_id";
	private static final String STYLE_LABEL = "style_label";
	private final TilesPortlet tiles;
	private final AmiWebDividerPortlet divider1;
	private final AmiWebDividerPortlet divider2;
	private final AmiWebAbstractTablePortlet table;
	private final AmiWebChartGridPortlet chart;
	private final AmiWebQueryFormPortlet queryForm;
	private final AmiWebService service;
	private final FormPortlet buttonsForm;
	private final FormPortletButton submitButton = new FormPortletButton("OK");
	private final AmiWebStyleManager styleManager;
	private String currentStyleId;
	private final HtmlPortlet blankPreview;

	private final static Comparator<Row> TILE_SORTER = new Comparator<Row>() {
		@Override
		public int compare(Row r1, Row r2) {
			String r1Label = (String) r1.get(STYLE_LABEL);
			String r2Label = (String) r2.get(STYLE_LABEL);
			if (r1Label.contains("Classic") && r2Label.contains("Classic"))
				return SH.COMPARATOR_CASEINSENSITIVE.compare(r1Label, r2Label);
			else if (r1Label.contains("Classic") && !r2Label.contains("Classic"))
				return 1;
			else if (!r1Label.contains("Classic") && r2Label.contains("Classic"))
				return -1;
			else
				return SH.COMPARATOR_CASEINSENSITIVE.compare(r1Label, r2Label);
		};
	};

	public AmiWebNewLayoutStylePortlet(PortletConfig config) {
		super(config);
		AmiWebHeaderPortlet header = new AmiWebHeaderPortlet(generateConfig());
		addChild(header, 0, 0, 2, 1);
		header.updateBlurbPortletLayout("Layout Style Chooser", "Choose a style for your new layout. <BR>Hint: You can adjust your style later using Dashboard > Style Manager. ");
		header.setShowSearch(false);
		header.setShowLegend(false);
		this.service = AmiWebUtils.getService(getManager());
		this.tiles = new TilesPortlet(generateConfig());
		addChild(this.tiles, 0, 1);
		BasicSmartTable tilesTable = new BasicSmartTable(new BasicTable(new String[] { STYLE_LABEL, STYLE_ID }));
		this.tiles.setTable(tilesTable);
		this.tiles.setMultiselectEnabled(false);
		this.tiles.addTilesListener(this);
		this.tiles.setTileFormatter(this);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_HEIGHT, 50);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, 180);
		this.tiles.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#AAAAAA");
		tilesTable.sortRows(TILE_SORTER, true);
		header.setShowBar(false);
		String id;
		this.styleManager = this.service.getStyleManager();
		for (AmiWebStyle s : this.styleManager.getAllStyles()) {
			if (s.getReadOnly()) {
				id = s.getId();
				this.tiles.addRow(s.getLabel(), id);
			}
		}
		this.currentStyleId = AmiWebStyleManager.FACTORY_DEFAULT_ID;

		this.blankPreview = new HtmlPortlet(generateConfig());
		this.blankPreview.setHtml("<BR><BR><BR><BR><BR>Please select a layout style from the left panel");
		this.blankPreview.setCssStyle("_bg=#4c4c4c|_fm=center|_fs=18|_fg=#ffffff");
		Row row;
		int N = 15;
		Random rng = new Random(123);

		// Set up table
		AmiWebFormatterManager fm = this.service.getFormatterManager();
		this.table = new AmiWebDatasourceTablePortlet(generateConfig());
		this.table.setPanelTitle("Data");
		FastTablePortlet ftp = this.table.getTablePortlet();
		FastWebTable fwt = ftp.getTable();
		SmartTable smart = fwt.getTable();
		BasicWebCellFormatter formatter = fm.getDecimalWebCellFormatter();
		smart.addColumn(Integer.class, "n_col");
		fwt.addColumn(true, "n", "n_col", fm.getIntegerWebCellFormatter());
		smart.addColumn(Double.class, "x_col");
		fwt.addColumn(true, "x", "x_col", formatter);
		smart.addColumn(Double.class, "y_col");
		fwt.addColumn(true, "y", "y_col", formatter);
		smart.addColumn(Double.class, "z_col");
		fwt.addColumn(true, "z", "z_col", formatter);
		for (int i = 0; i < N; i++) {
			row = smart.newEmptyRow();
			for (int j = 0; j < smart.getColumnsCount() - 2; j++) {
				if (smart.getColumnAt(j).getType() == Long.class)
					row.putAt(j + 2, rng.nextInt(10));
				else
					row.putAt(j + 2, rng.nextDouble());
			}
			smart.getRows().add(row);
		}

		// Set up chart
		this.chart = new AmiWebChartGridPortlet(generateConfig());
		//		this.chart.setAdn("CHART_DEMO");
		this.chart.setPanelTitle("X vs Y");
		AmiWebChartPlotPortlet plot = this.chart.addPlot(0, 0);
		AmiWebChartRenderingLayer_Graph layer = new AmiWebChartRenderingLayer_Graph(plot);
		layer.setName("Layer");
		AmiWebChartAxisPortlet xAxis = this.chart.addAxis(AmiWebChartGridPortlet.POS_B, 0, 0);
		AmiWebChartAxisPortlet yAxis = this.chart.addAxis(AmiWebChartGridPortlet.POS_L, 0, 0);
		plot.addRenderylingLayer(layer, 0);
		AmiWebChartSeries_Graph series = new AmiWebChartSeries_Graph(this.service, this.chart, null, layer);
		layer.setCustomFormulaTypes(smart.getColumnTypesMapping());
		series.setName("Series");
		layer.setXAxis(xAxis.getAxisId());
		layer.setYAxis(yAxis.getAxisId());
		series.getXField().setValue("x_col", smart);
		series.getYField().setValue("y_col", smart);
		series.getmShapeFormula().setValue("\"circle\"");
		series.getmColorFormula().setValue("this.getStyle(\"seriesCls\", n_col)", smart);
		series.getmBorderColorFormula().setValue("brighten(this.getStyle(\"seriesCls\", n_col), -0.5D)", smart);
		series.getmBorderSizeFormula().setValue("2");
		series.getmWidthFormula().setValue("30 * z_col", smart);
		series.getmHeightFormula().setValue("30 * z_col", smart);
		layer.setSeries(series, smart);
		xAxis.setAutoMajorValue(true);
		xAxis.setAutoMinorValue(true);
		xAxis.setAutoMaxValue(true);
		xAxis.setAutoMinValue(true);
		xAxis.setMaxValue(1);
		xAxis.setMinValue(0);
		yAxis.setMaxValue(1);
		yAxis.setMinValue(0);
		xAxis.setNumberFormula("formatNumber(n,\"#,###.###\",\"\")");
		yAxis.setNumberFormula("formatNumber(n,\"#,###.###\",\"\")");
		xAxis.setTitle("x");
		yAxis.setTitle("y");

		this.queryForm = new AmiWebQueryFormPortlet(generateConfig());
		this.queryForm.setHtmlTemplate("&nbsp Datamodel Inputs", false);
		this.queryForm.setPanelTitle("Dashboard Controls");
		QueryField<?> slider1 = createQueryField(service.getFormFieldFactory(QueryField.TYPE_ID_RANGE), this.queryForm, "x", "X");
		slider1.ensureHorizontalPosDefined();
		int topFieldPx = 30;
		int fieldDeltaPx = 40;
		slider1.ensureVerticalPosDefined(topFieldPx);
		slider1.setValue(null, 40);
		QueryField<?> slider2 = createQueryField(service.getFormFieldFactory(QueryField.TYPE_ID_RANGE), this.queryForm, "y", "Y");
		slider2.ensureHorizontalPosDefined();
		slider2.ensureVerticalPosDefined(slider1.getTopPosPx() + fieldDeltaPx);
		slider2.setValue(null, 10);
		QueryField<?> slider3 = createQueryField(service.getFormFieldFactory(QueryField.TYPE_ID_RANGE), this.queryForm, "z", "Z");
		slider3.ensureHorizontalPosDefined();
		slider3.ensureVerticalPosDefined(slider2.getTopPosPx() + fieldDeltaPx);
		slider3.setValue(null, 88);

		this.divider2 = new AmiWebDividerPortlet(generateConfig(), true);
		DividerPortlet divInner2 = this.divider2.getInnerContainer();
		divInner2.setFirst(this.table);
		divInner2.setSecond(this.queryForm);

		this.divider1 = new AmiWebDividerPortlet(generateConfig(), false);
		DividerPortlet divInner1 = this.divider1.getInnerContainer();
		divInner1.setFirst(this.divider2);
		divInner1.setSecond(this.chart);
		addChild(this.blankPreview, 1, 1);
		InnerPortlet panel = this.getPanelAt(1, 1);
		panel.setPadding(15, 15, 15, 15);
		this.setCssStyle("_bg=#AAAAAA");
		panel.setCssStyle("style.boxShadow=0px 0px 8px 2px #000000");
		this.buttonsForm = new FormPortlet(generateConfig());
		addChild(this.buttonsForm, 0, 2, 2, 1);
		this.buttonsForm.addButton(this.submitButton);
		setRowSize(2, 40);

		setColSize(0, 220);

		this.buttonsForm.addFormPortletListener(this);
	}
	private static final QueryField createQueryField(AmiWebFormFieldFactory factory, AmiWebQueryFormPortlet form, String name, String label) {
		QueryField queryField = factory.createQueryField(form);
		queryField.setVarName(name);
		FormPortletField field = queryField.getField();
		field.setTitle(label);
		form.addQueryField(queryField, true);
		return queryField;
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		sink.append(tile.get(STYLE_LABEL));
		if (!selected)
			styleSink.append("_fm=bold,left|style.border=1px solid #ffffff|_bg=#ffffff|style.padding=15px 10px");
		else
			styleSink.append("_fm=bold,left|style.border=2px solid black|_bg=#ffffff|style.padding=15px 10px");
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {

	}

	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}

	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		if (this.blankPreview.equals(getChildAt(1, 1))) {
			replaceChild(this.blankPreview.getPortletId(), this.divider1);
			getManager().onPortletAdded(this.divider1);
		}
		this.currentStyleId = (String) row.get(STYLE_ID);
		this.chart.getStylePeer().setParentStyle(this.currentStyleId);
		this.table.getStylePeer().setParentStyle(this.currentStyleId);
		this.queryForm.getStylePeer().setParentStyle(this.currentStyleId);
		this.divider1.getStylePeer().setParentStyle(this.currentStyleId);
		this.divider2.getStylePeer().setParentStyle(this.currentStyleId);
	}

	@Override
	public void onSelectedChanged(TilesPortlet tiles) {

	}

	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {

	}

	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
		submit();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			submit();
		}
	}
	private void submit() {
		this.styleManager.getStyleById(AmiWebStyleManager.LAYOUT_DEFAULT_ID).setParentStyle(this.currentStyleId);
		close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 800;
	}
	public int getSuggestedHeight(PortletMetrics pm) {
		return 800;
	}
	@Override
	public void onClosed() {
		super.onClosed();
		this.chart.close();
		this.table.close();
		this.queryForm.close();
		this.divider1.close();
		this.divider2.close();
	}
}
