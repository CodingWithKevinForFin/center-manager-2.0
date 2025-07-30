package com.larkinpoint.salestool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.base.Action;
import com.f1.base.Clock;
import com.f1.base.Day;
import com.f1.base.Message;
import com.f1.base.Row;
import com.f1.container.ContainerServices;
import com.f1.container.ResultMessage;
import com.f1.container.impl.BasicContainerServices;
import com.f1.suite.web.WebWindowState;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.PropertiesBuilder;
import com.f1.utils.PropertyController;
import com.f1.utils.agg.DoubleAggregator;
import com.f1.utils.agg.DoubleStatistics;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;

public class LarkinOptionReportPortlet extends GridPortlet implements WebContextMenuListener, WebContextMenuFactory {

	private FastWebTable summaryTable;
	private FastWebTable straddlesTable;
//	private LarkinParameters params;
	LocaleFormatter localformatter;
	String chartText = "Because the performance obtained through hypothetical or back-tested strategies does not result from actual trading, there is no market risk involved. Because these \'results\' are hypothetical and often created with the benefit of hindsight, it may be difficult, if not impossible, to account for all of the factors that might have affected a manager\'s decision making process. Hypothetical or back-tested performance often involves certain material assumptions in applying investment decisions that might have been made, based on the investment theory espoused, during the relevant historical period and the data set chosen may not be indicative of present or future market conditions.  There are often sharp differences between hypothetical performance results and actual returns subsequently achieved. Due to the benefit of hindsight, hypothetical (and, particularly, back-tested) performance almost invariably will show attractive returns, while actual results going forward may not be as attractive.";
			  

	private FastTablePortlet straddlesPortlet;
	private FastTablePortlet summaryTablePortlet;
	private LarkinOptionTimeSeriesPortlet tsPortlet;
	private DividerPortlet div;
	private TabPortlet tabPortlet;
	private LarkinSingleOptionReturnsPortlet puts1Portlet;
	private LarkinSingleOptionReturnsPortlet puts2Portlet;
	private LarkinUnderlyingReturnsPortlet underReturnsPortlet;
	private SeriesChartPortlet plChart;
	private SeriesChartPortlet returnsChart;
	
	private LarkinScenarioSummaryPortlet summaryPortlet;
	

	
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;
	
	private LarkinOptionDataPortlet parent;
	
	
	private LarkinScenarioContext LSC;

	public LarkinScenarioContext getLSC() {
		return LSC;
	}

	public void setLSC(LarkinScenarioContext lSC) {
		LSC = lSC;
	}
	
	String[] blank;

	public String getWorkingSymbol() {
		return LSC.getSymbol();
	}

	public LarkinOptionReportPortlet(PortletConfig config, LarkinScenarioContext LSC, LarkinOptionDataPortlet parent) {
		// TODO Auto-generated constructor stub
	
		super(config);
		this.LSC=LSC;
		rebuildMultiMaps();
		this.LSC=LSC;
		this.parent = parent;
	
		this.blank = new String[8];
		blank[0] = "blank0";
		blank[1] = "blank1";
		blank[2] = "blank2";
		blank[3] = "blank3";
		blank[4] = "blank4";
		blank[5] = "blank5";
		blank[6] = "blank6";
		blank[7] = "blank7";

		localformatter = getManager().getState().getWebState().getFormatter();
		Object[] columns = new Object[] { "symbol", "under.close", "call.last", "call.bid", "call.ask", "call.strike_price", "expiry", "call.open_interest", "quote_date",
				"call.delta", "call.gamma", "call.vega", "call.theta", "call.implied_vol", "call.volume", "put.last", "put.bid", "put.ask", "put.open_interest", "put.delta",
				"put.gamma", "put.vega", "put.theta", "put.implied_vol", "put.volume", "straddle.last", "straddle.bid", "straddle.ask", "straddle.intrinsic", "days.to.expiry",
				"call.to.put", "call.option_id", "put.option_id", "put.strike_price", "sprd.sod.value", "sprd.eod.value", "sprd.cashflow", "sprd.pl", "daily.return", "isSummary" };
		straddlesTable = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());

		Object[] summaryColumns = new Object[] { "symbol", "under.close", "quote_date", "strd.sod.value", "strd.eod.value", "strd.cashflow", "strd.pl", "strd.daily.return",
				"under.sod.value", "under.eod.value", "under.cashflow", "under.pl", "under.daily.return", "put.sod.value", "put.eod.value", "put.cashflow", "put.pl",
				"put.daily.return", "total.sod.value", "total.eod.value", "total.cashflow", "total.pl", "total.daily.return", "isSummary", "fee.pl", "total.delta", "strd.delta",
				"put.delta", "under.delta", "strd.gamma", "put.gamma", "strd.theta", "put.theta", blank[0], blank[1], blank[2], blank[3], blank[4], blank[5], blank[6], blank[7] };

		summaryTable = new FastWebTable(new BasicSmartTable(new BasicTable(summaryColumns)), getManager().getTextFormatter());

		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));
		//Create the Portlets
		{
		
			tsPortlet = new LarkinOptionTimeSeriesPortlet(generateConfig());
			this.straddlesPortlet = new FastTablePortlet(generateConfig(), straddlesTable);
			this.summaryTablePortlet = new FastTablePortlet(generateConfig(), summaryTable);
			tabPortlet = new TabPortlet(generateConfig());
			puts1Portlet = new LarkinSingleOptionReturnsPortlet(generateConfig());
			puts2Portlet = new LarkinSingleOptionReturnsPortlet(generateConfig());
			underReturnsPortlet = new LarkinUnderlyingReturnsPortlet(generateConfig());
			plChart = new SeriesChartPortlet(generateConfig());
			returnsChart = new SeriesChartPortlet(generateConfig());
			
		}

		//Add Fields to Portlets and set values and defaults
		{
			
			//PropertyController props = getManager().getService(BasicContainerServices.ID);
			chartText = getManager().getState().getWebState().getPartition().getContainer().getTools().getOptional("chart.disclosure.text");
			
			plChart.setStyle(SeriesChartPortlet.STYLE_LINE);
			plChart.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, "below");
			returnsChart.setStyle(SeriesChartPortlet.STYLE_BAR);
			returnsChart.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, "below");
			
			if( chartText != null ) {
				
				plChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT,chartText);
				returnsChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT,chartText);
				
				String style = getManager().getState().getWebState().getPartition().getContainer().getTools().getOptional("chart.disclosure.style");
				if( style != null ){
		
				plChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT_STYLE,style);
				returnsChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT_STYLE,style);
				}
				String font = getManager().getState().getWebState().getPartition().getContainer().getTools().getOptional("chart.disclosure.font");
				if( font !=  null){
					
					plChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT_FONT,font);
					returnsChart.addOption(SeriesChartPortlet.OPTION_CHART_TEXT_FONT,font);
					
				}
		
			}
		}
		{
	//		resetReturnsChart();
			resetStraddlesTable();
			
		}
		
		MapWebCellFormatter<Object> callPutFormatter = new MapWebCellFormatter<Object>(getManager().getTextFormatter());
		callPutFormatter.addEntry(0, "Call", "style.color=blue");
		callPutFormatter.addEntry(1, "Put", "style.color=green");

		mainTableSetup();
		summaryTableSetup();
		resetPLChart();
		resetReturnsChart();
		summaryPortlet = new LarkinScenarioSummaryPortlet(generateConfig(),LSC,this);
		{
			//	div.setOffset(.3);
			//	div.addChild(tablePortlet);
			tabPortlet.addChild("Param Data", summaryPortlet);
			tabPortlet.addChild("Straddles", straddlesPortlet);
			tabPortlet.addChild("1st Risk Puts", puts1Portlet);
			tabPortlet.addChild("2nd Risk Puts", puts2Portlet);
			tabPortlet.addChild("Underlying Returns", underReturnsPortlet);
			tabPortlet.addChild("Larkin Point Strategy Back-Test (with Fees Deducted)", plChart);
			tabPortlet.addChild("Returns Chart", returnsChart);
			tabPortlet.addChild("Summary Data", summaryTablePortlet);
			tabPortlet.setActiveTab(plChart);
			tabPortlet.setTitle("Returns Tables");
			//tabPortlet.addChild(tsPortlet);
			//	div.addChild(tabPortlet);

			//	rowCount += 2;

			addChild(tabPortlet, 0, 0, 1, 1);
			//setRowSize(0, 40);
			//setRowSize(1, 70);
		}

		registerChildPortletToBeSaved("OPGR", straddlesPortlet);
		registerChildPortletToBeSaved("OPTS", tsPortlet);
		registerChildPortletToBeSaved("PUTS", puts1Portlet);
		registerChildPortletToBeSaved("UNDS", underReturnsPortlet);
		registerChildPortletToBeSaved("SUMM", summaryTablePortlet);
	
		
		puts1Portlet.resetTableData(LSC.getBackMonthPutsList1());
		puts2Portlet.resetTableData(LSC.getBackMonthPutsList2());
		underReturnsPortlet.resetTableData(LSC.getUnderlyingsList());
	}
	

	public void summaryTableSetup()
	//Setup the summary table
	{
		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		final int precision = 6;
		summaryTable.getTable().setTitle("Summary Data");
		summaryTable.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		summaryTable.addColumn(true, "Quote Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		summaryTable.addColumn(true, "U Close", "under.close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle);
		summaryTable.addColumn(true, "  ", blank[0], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Total P&L", "total.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");

		summaryTable.addColumn(true, "Under P&L", "under.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		summaryTable.addColumn(true, "Strd P&L", "strd.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		summaryTable.addColumn(true, "Puts P&L", "put.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		summaryTable.addColumn(true, "Fee P&L", "fee.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		summaryTable.addColumn(true, "  ", blank[1], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Total RTN", "total.daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60)
				.addCssClass(calcStyle);
		summaryTable.addColumn(true, "Under RTN", "under.daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60)
				.addCssClass(calcStyle);
		summaryTable.addColumn(true, "Strd RTN", "strd.daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Puts RTN", "put.daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);

		summaryTable.addColumn(true, "  ", blank[2], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);

		summaryTable.addColumn(true, "Total Delta", "total.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Under Delta", "under.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Strd Delta", "strd.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Puts Delta", "put.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "  ", blank[3], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Strd Gamma", "strd.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Puts Gamma", "put.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "  ", blank[4], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Strd Theta", "strd.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Puts Theta", "put.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "  ", blank[5], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Total SOD", "total.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Under SOD", "under.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Strd SOD", "strd.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");

		summaryTable.addColumn(true, "Puts SOD", "put.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "  ", blank[6], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Total EOD", "total.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Under EOD", "under.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Strd EOD", "strd.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Puts EOD", "put.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "  ", blank[7], new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		summaryTable.addColumn(true, "Total Cash", "total.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Under Cash", "under.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Strd Cash", "strd.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");
		summaryTable.addColumn(true, "Puts Cash", "put.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle)
				.addCssClass("bold");

		summaryTable.addColumn(false, "summary", "isSummary", new BasicWebCellFormatter());

	}
	public void mainTableSetup()
	//Setup the main table
	{
		final int precision = 6;

		straddlesTable.getTable().setTitle("Straddle");
		straddlesTable.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		straddlesTable.addColumn(true, "Quote Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		straddlesTable.addColumn(true, "Call K", "call.strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle)
				.addCssClass("bold");
		straddlesTable.addColumn(true, "Put K", "put.strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle)
				.addCssClass("bold");
		straddlesTable.addColumn(true, "Expiry", "expiry", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		straddlesTable.addColumn(true, "U Close", "under.close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle);
		straddlesTable.addColumn(true, "Call Last", "call.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(80).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Bid", "call.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(80).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Ask", "call.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(80).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call O.I.", "call.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Delta", "call.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Gamma", "call.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Vega", "call.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Theta", "call.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call IV", "call.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		straddlesTable.addColumn(true, "Call Volume", "call.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);

		straddlesTable.addColumn(true, "Put Last", "put.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Bid", "put.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Ask", "put.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put O.I.", "put.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Delta", "put.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Gamma", "put.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Vega", "put.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Theta", "put.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put IV", "put.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		straddlesTable.addColumn(true, "Put Volume", "put.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);

		straddlesTable.addColumn(true, "Str Last", "straddle.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		straddlesTable.addColumn(true, "Str Bid", "straddle.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		straddlesTable.addColumn(true, "Str Ask", "straddle.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		straddlesTable.addColumn(true, "Str Prem", "straddle.intrinsic", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		straddlesTable.addColumn(true, "Days Left", "days.to.expiry", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(true, "CP ratio", "call.to.put", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(true, "SOD Value", "sprd.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(true, "EOD Value", "sprd.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(true, "Cash Flow", "sprd.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(true, "P&L", "sprd.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).addCssClass(calcStyle).addCssClass("bold");
		straddlesTable.addColumn(false, "Call OptID", "call.option_id", new BasicWebCellFormatter());
		straddlesTable.addColumn(false, "Put OptID", "put.option_id", new BasicWebCellFormatter());
		straddlesTable.addColumn(true, "Daily Return", "daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		straddlesTable.addColumn(false, "summary", "isSummary", new BasicWebCellFormatter());

		//straddlesPortlet.setTitle("Underlying data");
		straddlesPortlet.getTable().addMenuListener(this);
		straddlesTable.setMenuFactory(this);
	}
	public void setParameterTree() {
		//parametersPortlet.createNode(name, parent, expanded)
		//	WebTreeNode r = super.getTree().getTreeManager().createNode(group.getName(), root, false).setData(group).setCssClass("clickable");

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		// TODO Auto-generated method stub

	}
	private void resetStraddlesTable() {
		// TODO Auto-generated method stub
		int call_error_count = 0;
		int put_error_count = 0;
		double underlying = 0.0;
		this.straddlesPortlet.clearRows();

		for (SpreadMessage e : LSC.getNearMonthStraddlesList()) {
			OptionMessage call = e.getLeg1();
			OptionMessage put = e.getLeg2();

			if (call == null) {
				call_error_count++;
				continue;

			}
			if (put == null) {
				put_error_count++;
				continue;

			}

			this.straddlesPortlet.addRow(call.getUnderlying(), call.getUnderlyingClose(), call.getLast(), call.getBid(), call.getAsk(), call.getStrike(), call.getExpiry()
					.getStartMillis(), call.getOpenInterest(), call.getTradeDate().getStartMillis(), call.getDelta(), call.getGamma(), call.getVega(), call.getTheta(), call
					.getImpliedVol(), call.getVolume(), put.getLast(), put.getBid(), put.getAsk(), put.getOpenInterest(), put.getDelta(), put.getGamma(), put.getVega(), put
					.getTheta(), put.getImpliedVol(), put.getVolume(), put.getLast() + call.getLast(), put.getBid() + call.getBid(), put.getAsk() + call.getAsk(),
					call.getPairedValue() - call.getIntrinsicValue(), call.getDaysToExpiry(), call.getPairedRatio(), call.getOptionId(), put.getOptionId(), put.getStrike(), e
							.getStartingValue(), e.getEndingValue(), e.getCashFlow(), e.getDailyPAndL(), e.getDailyReturn(), false);

		}

		if ((call_error_count + put_error_count) != 0)
			System.out.println("Errors in option mapping :" + call_error_count + " " + put_error_count);

	}

	public void clearData() {
		straddlesPortlet.clearRows();
		tsPortlet.getTablePortlet().clearRows();
		puts1Portlet.clearRows();
		puts2Portlet.clearRows();
		summaryTablePortlet.clearRows();
		underReturnsPortlet.clearRows();
		plChart.clear();
		returnsChart.clear();
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		// TODO Auto-generated method stub
		super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub
		if (!setMachineSocket.hasConnections())
			return;
		FastWebTable t = this.straddlesPortlet.getTable();

	}
	@Override
	public WebMenu createMenu(WebTable table) {
		// TODO Auto-generated method stub
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		List<Row> selected = table.getSelectedRows();
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));
		if (selected.size() == 1) {
			Row row1 = selected.get(0);
			float price = (Float) row1.get("call.bid") + (Float) row1.get("put.bid");
			String data = new String(row1.get("symbol") + " " + datefmt.formatCellToText(row1.get("expiry")) + " " + row1.get("call.strike_price") + " straddle @"
					+ numfmt.formatCellToText(price));
			children.add(new BasicWebMenuLink("Sell " + data, true, "straddle"));

		}
		if (selected.size() == 2) {
			Row row1 = selected.get(0);
			Row row2 = selected.get(1);

			float under = (Float) row1.get("under.close");
			float strike1 = (Float) row1.get("call.strike_price");
			float strike2 = (Float) row2.get("put.strike_price");

			if (!((strike1 < under && strike2 < under) || (strike1 > under && strike2 > under))) {
				float price = (float) 0.0;
				if (strike1 < strike2) {
					price = (Float) row2.get("call.bid") + (Float) row1.get("put.bid");

				} else {
					price = (Float) row1.get("put.bid") + (Float) row2.get("call.bid");
				}

				String data = new String(row1.get("symbol") + " " + datefmt.formatCellToText(row1.get("expiry")) + " " + row1.get("call.strike_price") + "-"
						+ row2.get("put.strike_price") + " strangle @" + numfmt.formatCellToText(price));
				children.add(new BasicWebMenuLink("Sell " + data, true, "strangle"));

				//	children.add(new BasicWebMenuLink("Select Strangle", true, "strangle"));
			}
		}

		BasicWebMenu r = new BasicWebMenu("", true, children);
		return r;

	}
	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}
	public float getUnderlyingValueAtTradeDate(BasicDay bd) {
		float temp = (float) 0.0;
		if (LSC.getUnderlyingsList().isEmpty() == false)
			temp = (float) LSC.getUnderlyingsList().get(0).getClose();
		for (UnderlyingMessage message : LSC.getUnderlyingsList())
			if (message.getQuoteDate().isOn(bd.getStartNanoDate()))
				temp = (float) message.getClose();
		return temp;

	}

	

	//public float getStartingUnderlyingValue() {

	//return getUnderlyingValueAtTradeDate((BasicDay) QuoteDateNameField1.getValue().getA());

	//}
	//public float getEndingUnderlyingValue() {

	//return getUnderlyingValueAtTradeDate((BasicDay) QuoteDateNameField1.getValue().getB());

	///}
	public int getNumberOfTradingDatesBetween(BasicDay start, BasicDay end) {
		int count = 0;
		for (UnderlyingMessage message : LSC.getUnderlyingsList())
			if (message.getQuoteDate().isOnOrAfter(start.getStartNanoDate()) && message.getQuoteDate().isOnOrBefore(start.getStartNanoDate()))
				count++;
		return count;

	}
	public List<UnderlyingMessage> getUnderlyingsBetweenTradingDates(BasicDay start, BasicDay end) {
		List<UnderlyingMessage> list = new ArrayList<UnderlyingMessage>();
		for (UnderlyingMessage message : this.LSC.getUnderlyingsList())
			if (message.getQuoteDate().isOnOrAfter(start.getStartNanoDate()) && message.getQuoteDate().isOnOrBefore(start.getStartNanoDate()))
				list.add(message);
		return list;

	}
	@Override
	public Map<String, Object> getConfiguration() {
		// TODO Auto-generated method stub
		Map<String, Object> local = super.getConfiguration();
		local.put("div.offset", div.getOffset());
		return local;
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		// TODO Auto-generated method stub
		super.init(configuration, origToNewIdMapping, sb);
		Double lcd = CH.getOr(Double.class, configuration, "div.offset", .6);
		div.setOffset(lcd);
	}
	public void rebuildMultiMaps() {
		this.LSC.setTradeDateMasher (new BasicMultiMap.List<Day, Message>());
		this.LSC.getTradeDateMasher().setInnerMap(new TreeMap<Day, List<Message>>());
		this.LSC.setUnderlyingMasher(new BasicMultiMap.List<Day, UnderlyingMessage>());
		this.LSC.getUnderlyingMasher().setInnerMap(new TreeMap<Day, List<UnderlyingMessage>>());
		this.LSC.setStraddleMasher( new BasicMultiMap.List<Day, SpreadMessage>());
		this.LSC.getStraddleMasher().setInnerMap(new TreeMap<Day, List<SpreadMessage>>());
		this.LSC.setPuts1Masher(new BasicMultiMap.List<Day, SpreadMessage>());
		this.LSC.getPuts1Masher().setInnerMap(new TreeMap<Day, List<SpreadMessage>>());
		this.LSC.setPuts2Masher(new BasicMultiMap.List<Day, SpreadMessage>());
		this.LSC.getPuts2Masher().setInnerMap(new TreeMap<Day, List<SpreadMessage>>());

		for (UnderlyingMessage message : LSC.getUnderlyingsList()) {
			this.LSC.getTradeDateMasher().putMulti(message.getQuoteDate(), message);
			this.LSC.getUnderlyingMasher().putMulti(message.getQuoteDate(), message);
		}
		for (SpreadMessage message : LSC.getNearMonthStraddlesList()) {
			this.LSC.getTradeDateMasher().putMulti(message.getLeg1().getTradeDate(), message);
			this.LSC.getStraddleMasher().putMulti(message.getLeg1().getTradeDate(), message);
		}
		for (SpreadMessage message : LSC.getBackMonthPutsList1()) {
			this.LSC.getTradeDateMasher().putMulti(message.getLeg1().getTradeDate(), message);
			this.LSC.getPuts1Masher().putMulti(message.getLeg1().getTradeDate(), message);
		}
		for (SpreadMessage message :LSC.getBackMonthPutsList2()) {
			this.LSC.getTradeDateMasher().putMulti(message.getLeg1().getTradeDate(), message);
			this.LSC.getPuts2Masher().putMulti(message.getLeg1().getTradeDate(), message);
		}
	}

	public void resetPLChart() {
		plChart.setAllowConfig(true);
		plChart.clear();
		plChart.setSeriesColor("Unders", "#ff0000");

		plChart.setSeriesColor("Options", "#9f9f9f");
		plChart.setSeriesColor("Total", "#007fff");
		plChart.setSeriesLabel("Options", "Larkin Point Combined Options Components");
		plChart.setSeriesLabel("Total", "Larkin Point Strategy Back-Test (with Fees Deducted)");
		plChart.setSeriesLabel("Unders", "Underlying Index P/L, Long-Only");

		DoubleAggregator daUnders = new DoubleAggregator();
		DoubleAggregator daOptions = new DoubleAggregator();
		DoubleAggregator daTotal = new DoubleAggregator();

	}
	public void resetReturnsChart() {
		returnsChart.setAllowConfig(true);
		returnsChart.clear();
		resetPLChart();
		returnsChart.setSeriesColor("Unders","#ff0000");// "cornflowerblue");
		returnsChart.setSeriesColor("TotalReturn","#007fff"); // " #0e5234");

		returnsChart.setSeriesLabel("TotalReturn", "Larkin Point Options Strategy Return");
		returnsChart.setSeriesLabel("Unders", "Long-only Index Return");
		summaryTablePortlet.clearRows();

		DoubleAggregator daUnders = new DoubleAggregator();
		DoubleAggregator daOptions = new DoubleAggregator();
		DoubleAggregator daTotal = new DoubleAggregator();

		DoubleStatistics larkinDa = new DoubleStatistics();
		DoubleStatistics underDa = new DoubleStatistics();
		int[] totalReturns = new int[23];
		int[] underReturns = new int[23];
		int[] putReturns = new int[23];
		int[] strdReturns = new int[23];
		double totalPL = 0.0;
		double indexPL = 0.0;
		double fee = LSC.getParams().getManagementFee();
		double totalFee=0.0d;

		boolean firstDay = true;
		double tReturn = 0d, tSOD = 0d, tEOD = 0d, tCashFlow = 0d;
		double uReturn = 0d, uSOD = 0d, uEOD = 0d, uCashFlow = 0d;
		Day lastday = null;
		
		//For every trading day
		for (Entry<Day, List<Message>> entry : LSC.getTradeDateMasher().entrySet()) {
			Day day = entry.getKey();
			String domain = day.toStringNoTimeZone();
			int datecount = 1;
			if (lastday == null) {
				datecount = 1;
			} else {
				//get datecount from lastday and day then set lastday = day
				datecount = lastday.getDurationInDaysTo(day);
			}
			lastday = day;
			double value = 0.0;
		//	double sod = 0d, eod = 0d, cashflow = 0d, pl = 0d;
			double undsod = 0d, undeod = 0d, undcashflow = 0d, undpl = 0d, undreturn = 0d, unddelta = 1d;
			double putsod = 0d, puteod = 0d, putcashflow = 0d, putpl = 0d, putreturn = 0d, putdelta = 0d, putgamma = 0d, puttheta = 0d;
			double strdsod = 0d, strdeod = 0d, strdcashflow = 0d, strdpl = 0d, strdreturn = 0d, strddelta = 0d, strdgamma = 0d, strdtheta = 0d;
			String symbol = null;
			double close = 0.0;
			
			
			//Accumulate all the transactions for that day and gather all data
			List<Message> messages = entry.getValue();
			for (Message mess : messages) {
				if (mess instanceof UnderlyingMessage) {
					symbol = ((UnderlyingMessage) mess).getSymbol();
					close = ((UnderlyingMessage) mess).getClose();
					//sod += ((UnderlyingMessage) mess).getStartingValue();
					//eod += ((UnderlyingMessage) mess).getEndingValue();
					//cashflow += ((UnderlyingMessage) mess).getCashFlow();
					totalPL += ((UnderlyingMessage) mess).getDailyPAndL();
					indexPL += ((UnderlyingMessage) mess).getDailyPAndL();

					undsod += ((UnderlyingMessage) mess).getStartingValue();
					undeod += ((UnderlyingMessage) mess).getEndingValue();
					undcashflow += ((UnderlyingMessage) mess).getCashFlow();
					undpl += ((UnderlyingMessage) mess).getDailyPAndL();

				} else { //Process Straddles
					if (((SpreadMessage) mess).getLegCounts() == 2) {
						symbol = ((SpreadMessage) mess).getLeg1().getUnderlying();
						close = ((SpreadMessage) mess).getLeg1().getUnderlyingClose();
						double strdCount = ((SpreadMessage) mess).getNumberOfSpreads();
						if(((SpreadMessage) mess).getEndingValue() != 0d ){
							strddelta -= strdCount * ((SpreadMessage) mess).getLeg1().getDelta();
							strdgamma -= strdCount * ((SpreadMessage) mess).getLeg1().getGamma();
							strdtheta -= strdCount * ((SpreadMessage) mess).getLeg1().getTheta();
	
							strddelta -= strdCount * ((SpreadMessage) mess).getLeg2().getDelta();
							strdgamma -= strdCount * ((SpreadMessage) mess).getLeg2().getGamma();
							strdtheta -= strdCount * ((SpreadMessage) mess).getLeg2().getTheta();
						}

					//	sod += ((SpreadMessage) mess).getStartingValue();
						//eod += ((SpreadMessage) mess).getEndingValue();
						//cashflow += ((SpreadMessage) mess).getCashFlow();
						totalPL += ((SpreadMessage) mess).getDailyPAndL();
						strdsod += ((SpreadMessage) mess).getStartingValue();
						strdeod += ((SpreadMessage) mess).getEndingValue();
						strdcashflow += ((SpreadMessage) mess).getCashFlow();
						strdpl += ((SpreadMessage) mess).getDailyPAndL();

					} else { //Process Puts
						symbol = ((SpreadMessage) mess).getLeg1().getUnderlying();
						close = ((SpreadMessage) mess).getLeg1().getUnderlyingClose();
						double putCount = ((SpreadMessage) mess).getNumberOfSpreads();
						if(((SpreadMessage) mess).getEndingValue() != 0d ){
							putdelta += putCount * ((SpreadMessage) mess).getLeg1().getDelta();
							putgamma += putCount * ((SpreadMessage) mess).getLeg1().getGamma();
							puttheta += putCount * ((SpreadMessage) mess).getLeg1().getTheta();
						}

						//sod += ((SpreadMessage) mess).getStartingValue();
						//eod += ((SpreadMessage) mess).getEndingValue();
						//cashflow += ((SpreadMessage) mess).getCashFlow();
						totalPL += ((SpreadMessage) mess).getDailyPAndL();
						putsod += ((SpreadMessage) mess).getStartingValue();
						puteod += ((SpreadMessage) mess).getEndingValue();
						putcashflow += ((SpreadMessage) mess).getCashFlow();
						putpl += ((SpreadMessage) mess).getDailyPAndL();

					}
				}
			}

			double tdelta = unddelta + putdelta + strddelta;
			double tsod = putsod + strdsod + undsod;
			double feepl = tsod * (datecount * (fee / 100)) / 365;
			double teod = puteod + strdeod + undeod;
			double tcf = putcashflow + strdcashflow + undcashflow;
			double tpl = putpl + strdpl + undpl - feepl;
			double tret = ((tsod+tcf) == 0.0 ? 0.0 : (teod - tsod + tcf - feepl) / (tsod + tcf));
			double uret = ((undsod+undcashflow) == 0.0 ? 0.0 : (undeod - undsod + undcashflow) / (undsod+undcashflow));
			totalPL -= feepl;
			daUnders.add(undpl);
			daTotal.add(tpl);
			daOptions.add(putpl + strdpl);
			
			larkinDa.add(tret*100d);
			underDa.add(uret*100d);
			String empty = " ";
			plChart.addPoint("Unders", domain, daUnders.getTotal());
			plChart.addPoint("Options", domain, daOptions.getTotal());
			plChart.addPoint("Total", domain, daTotal.getTotal());
			
			
			this.summaryTablePortlet.addRow(symbol, close, day, strdsod, strdeod, strdcashflow, strdpl,
					(strdsod == 0.0 ? 0.0 : -1d * (strdeod - strdsod + strdcashflow) / strdsod), undsod, undeod, undcashflow, undpl, uret, putsod, puteod, putcashflow, putpl,
					(putsod == 0.0 ? 0.0 : (puteod - putsod + putcashflow) / putsod), tsod, teod, tcf, tpl, tret, false, feepl, tdelta, strddelta, putdelta, unddelta,
					strdgamma * 100, putgamma * 100, strdtheta, puttheta, empty, empty, empty, empty, empty, empty, empty, empty);

			if (tsod != 0d) {
				value = tret;
			}
			int bucket = (int) ((value * 200d) + .5);
			if (bucket < -10)
				bucket = 0;
			else if (bucket > 10)
				bucket = 22;
			else
				bucket += 11;
			totalReturns[bucket] += 1;
			//Collect Total Return data
			//if (firstDay && tsod != 0.0) {
			if (firstDay ) {
				uSOD = undsod-undcashflow;
				tSOD = tsod - tcf;
				firstDay = false;
			}
		//	if (undeod != 0.0) {
			{
				uEOD = undeod;
				tEOD = teod;
			}

			//tCashFlow += cashflow - feepl;
			tCashFlow += tcf - feepl;
			uCashFlow += undcashflow;

		}

		for (Entry<Day, List<UnderlyingMessage>> entry : LSC.getUnderlyingMasher().entrySet()) {
			Day day = entry.getKey();
			double value = 0.0;
			double lsod = 0d, leod = 0d, lcashflow = 0d;
			List<UnderlyingMessage> messages = entry.getValue();
			for (UnderlyingMessage mess : messages) {
				lsod += mess.getStartingValue();
				leod += mess.getEndingValue();
				lcashflow += mess.getCashFlow();
			}
			if (lsod != 0d)
				value = (leod - lsod + lcashflow) / (lsod + lcashflow);
			int bucket = (int) ((value * 200d) + .5);
			if (bucket < -10)
				bucket = 0;
			else if (bucket > 10)
				bucket = 22;
			else
				bucket += 11;
			underReturns[bucket] += 1;

		}

		for (Integer i = 0; i < 23; i++) {
			Float k = (float) ((i - 11) / 2d);
			String bucketLabel = k.toString() + "%";
			returnsChart.addPoint("TotalReturn", bucketLabel, totalReturns[i]);
			returnsChart.addPoint("Unders", bucketLabel, underReturns[i]);

		}
		//tReturn = ((tSOD+tCashFlow) == 0.0 ? 0.0 : (tEOD - (tSOD +tCashFlow) ) / (tSOD +tCashFlow));
		//uReturn = ((uSOD+uCashFlow) == 0.0 ? 0.0 : (uEOD - (uSOD  + uCashFlow) ) / (uSOD + uCashFlow));
		tReturn = totalPL/tSOD;
		uReturn = indexPL/uSOD;
		
		LSC.setTotalPL(totalPL);
		LSC.setIndexPL(indexPL);
		LSC.setTotalReturn(tReturn);
		LSC.setIndexReturn(uReturn);
	
		//		tReturn = tCashFlow;
		//		uReturn = uCashFlow;
	//	String larkinLabel = "Larkin Period Return " + localformatter.getPercentFormatter(2).format(tReturn) + " Variance "
	//			+ localformatter.getNumberFormatter(2).format(larkinDa.getVariance()) + "  Stddev " + localformatter.getNumberFormatter(2).format( larkinDa.getStdev());
	//	String undersLabel = "Index  Period Return  " + localformatter.getPercentFormatter(2).format(uReturn) + " Variance "
	//			+ localformatter.getNumberFormatter(2).format(underDa.getVariance()) + "  Stddev " + localformatter.getNumberFormatter(2).format( underDa.getStdev());
		
		String larkinLabel = "Larkin Period Return " + localformatter.getPercentFormatter(2).format(tReturn) + " P&L "
				+ localformatter.getNumberFormatter(2).format(totalPL) ;//+ "  Stddev " + localformatter.getNumberFormatter(2).format( larkinDa.getStdev());
		
		String undersLabel = "Index  Period Return  " + localformatter.getPercentFormatter(2).format(uReturn) + " P&L "
				+ localformatter.getNumberFormatter(2).format(indexPL) ;

		returnsChart.setSeriesLabel("TotalReturn", larkinLabel);
		returnsChart.setSeriesLabel("Unders", undersLabel);
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		plChart.addOption(SeriesChartPortlet.OPTION_TITLE,"Larkin Point Strategy Back-Test for " + LSC.getSymbol() + " between " +datefmt.formatCellToText(LSC.getParams().getStartingQuoteDate()) + " to " +datefmt.formatCellToText(LSC.getParams().getEndingQuoteDate()));
		returnsChart.addOption(SeriesChartPortlet.OPTION_TITLE, LSC.getSymbol() + " Returns Distribution for " + datefmt.formatCellToText(LSC.getParams().getStartingQuoteDate()) + " to " +datefmt.formatCellToText(LSC.getParams().getEndingQuoteDate()));
		//returnsChart.addOption(SeriesChartPortlet.OPTION_TITLE, "Total P&L: " + localformatter.getNumberFormatter(2).format(totalPL) + " while Index PL is :"
		//		+ localformatter.getNumberFormatter(2).format(indexPL));

	}

	public void setTabTitle( String title){
		if( title != null){
			parent.setReportTabTitle(title,this);
		//	this.callJsFunction(functionName);
			
		}
			
	}
	

}
