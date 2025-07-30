package com.f1.ami.web;

import java.util.List;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.web.AmiWebStatsManager.Stats;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Graph;
import com.f1.ami.web.charts.AmiWebChartRenderingLayer_Legend;
import com.f1.ami.web.charts.AmiWebChartSeries_Graph;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebMemoryUserUsagePortlet extends GridPortlet {

	private AmiWebService service;

	public AmiWebMemoryUserUsagePortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		List<Stats> stats = this.service.getWebStatsManager().getStats();
		AmiWebChartGridPortlet grid = new AmiWebChartGridPortlet(generateConfig());
		grid.setTransient(true);
		this.addChild(grid);
		AmiWebChartPlotPortlet plot = grid.addPlot(0, 0);
		AmiWebChartAxisPortlet yAxis = grid.addAxis(AmiWebChartGridPortlet.POS_L, 0, 0);
		AmiWebChartAxisPortlet xAxis = grid.addAxis(AmiWebChartGridPortlet.POS_B, 0, 0);
		grid.onAmiInitDone();
		yAxis.setMinValue(0);
		yAxis.setMaxValue(1);
		yAxis.setAutoMaxValue(false);
		yAxis.setAutoMinValue(false);
		yAxis.setFormatType(AmiWebChartAxisPortlet.FORMAT_TYPE_CUSTOM);
		yAxis.setNumberFormula("formatNumber(n,\"#%\",\"\")");
		yAxis.setLabeleRotate(90);
		yAxis.setGroupPadding(15);
		yAxis.setLabelPadding(35);
		yAxis.setLabelFontSize(20);
		yAxis.setLabelTickSize(0);
		xAxis.setFormatType(AmiWebChartAxisPortlet.FORMAT_TYPE_TIME);

		Table table = new BasicTable(String.class, "name", Long.class, "time", double.class, "memPercent", double.class, "userPercent");
		for (Stats i : stats) {
			Table t = i.buildHistory();
			for (Row row : t.getRows()) {
				long time = row.get(AmiConsts.PARAM_STATS_TIME, Caster_Long.INSTANCE);
				long users = row.get(AmiConsts.PARAM_STATS_UNIQUE_USERS, Caster_Long.INSTANCE);
				long memory = row.get(AmiConsts.PARAM_STATS_POST_GC_USED_MEMORY, Caster_Long.INSTANCE);
				long maxUsers = row.get(AmiConsts.PARAM_STATS_MAX_USERS, Caster_Long.INSTANCE);
				long maxMemory = row.get(AmiConsts.PARAM_STATS_MAX_MEMORY, Caster_Long.INSTANCE);
				table.getRows().addRow(i.getDescription(), time, (double) memory / maxMemory, (double) users / maxUsers);
			}
		}

		AmiWebChartRenderingLayer_Graph layer = new AmiWebChartRenderingLayer_Graph(plot);
		layer.setCustomFormulaTypes(table.getColumnTypesMapping());
		layer.setXAxis(xAxis.getAxisId());
		layer.setYAxis(yAxis.getAxisId());
		layer.setVMajorGridSize(1);
		layer.setVMajorGridColor("#eeeeee");
		layer.setVMidGridSize(2);
		layer.setVMidGridColor("#000000");
		plot.addRenderylingLayer(layer, 0);
		AmiWebChartSeries_Graph memorySeries = new AmiWebChartSeries_Graph(this.service, grid, null, layer);
		memorySeries.getNameFormula().setValue("name + \" memory\"", table);
		memorySeries.getXField().setValue("time", table);
		memorySeries.getYField().setValue("memPercent", table);
		memorySeries.getYLabelField().setValue("name", table);
		memorySeries.getmShapeFormula().setValue("\"circle\"");
		memorySeries.getmColorFormula().setValue("\"#008800\"");
		memorySeries.getmWidthFormula().setValue("2");
		memorySeries.getmHeightFormula().setValue("2");
		memorySeries.getLineColorFormula().setValue("\"#008800\"");
		memorySeries.getLineSizeFormula().setValue("2");
		layer.setSeries(memorySeries, table);

		AmiWebChartRenderingLayer_Graph layer2 = new AmiWebChartRenderingLayer_Graph(plot);
		layer2.setCustomFormulaTypes(table.getColumnTypesMapping());
		layer2.setXAxis(xAxis.getAxisId());
		layer2.setYAxis(yAxis.getAxisId());
		AmiWebChartSeries_Graph userSeries = new AmiWebChartSeries_Graph(this.service, grid, null, layer2);
		userSeries.getNameFormula().setValue("name + \" users\"", table);
		userSeries.getXField().setValue("time", table);
		userSeries.getYField().setValue("userPercent", table);
		userSeries.getYLabelField().setValue("name", table);
		userSeries.getmShapeFormula().setValue("\"circle\"");
		userSeries.getmColorFormula().setValue("\"#000088\"");
		userSeries.getmWidthFormula().setValue("2");
		userSeries.getmHeightFormula().setValue("2");
		userSeries.getLineColorFormula().setValue("\"#000088\"");
		userSeries.getLineSizeFormula().setValue("2");
		layer2.setSeries(userSeries, table);
		layer2.setId(1);
		plot.addRenderylingLayer(layer2, 1);
		AmiWebChartRenderingLayer_Legend legend = new AmiWebChartRenderingLayer_Legend(plot);
		legend.addSeries(memorySeries.getId());
		legend.addSeries(userSeries.getId());
		legend.setId(2);
		legend.setMaxWidth(200);
		plot.addRenderylingLayer(legend, 2);

	}

}
