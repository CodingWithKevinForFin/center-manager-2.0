package com.larkinpoint.salestool;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Day;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.larkinpoint.messages.GetOptionDataByOptionIDRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;

public class LarkinOptionTimeSeriesPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener {

	private HtmlPortlet TitleArea;
	private FormPortletTextField QuoteDateNameField1;
	private FormPortletTextField QuoteDateNameField2;
	private FormPortletTextField MaxDaystoExpiry;
	private FormPortletTextField MinDaystoExpiry;

	private FormPortlet datesFormPortlet;
	private FormPortlet expiryFormPortlet;
	private FormPortlet col4Portlet;
	private FormPortlet btnFormPortlet;
	private FastTablePortlet tablePortlet;
	//	private Map<Tuple4<String, Day, Day, Double>, Tuple2<OptionMessage, OptionMessage>> current;
	private List<SpreadMessage> current;
	private String WorkingSymbol;
	private long requestTime;
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;
	private SeriesChartPortlet chartCPPortlet;
	private SeriesChartPortlet chartVolPortlet;
	private SeriesChartPortlet chartDeltaPortlet;

	private Set<Row> currentSelections = new IdentityHashSet<Row>();

	public String getWorkingSymbol() {
		return WorkingSymbol;
	}

	public void setWorkingSymbol(String workingSymbol) {
		WorkingSymbol = workingSymbol;
	}

	public LarkinOptionTimeSeriesPortlet(PortletConfig config) {
		super(config);
		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));

		datesFormPortlet = new FormPortlet(generateConfig());
		expiryFormPortlet = new FormPortlet(generateConfig());
		col4Portlet = new FormPortlet(generateConfig());
		btnFormPortlet = new FormPortlet(generateConfig());
		btnFormPortlet.addFormPortletListener(this);

		//datesFormPortlet.addField(this.QuoteDateNameField1 = new FormPortletTextField("Start Date:").setWidth(75)).setValue("12/01/2012");
		//datesFormPortlet.addField(this.QuoteDateNameField2 = new FormPortletTextField("End Date:").setWidth(75)).setValue("12/31/2012");
		//expiryFormPortlet.addField(this.MaxDaystoExpiry = new FormPortletTextField("Max Days Left:").setWidth(30)).setValue("56");
		//expiryFormPortlet.addField(this.MinDaystoExpiry = new FormPortletTextField("Min Days Left:").setWidth(30)).setValue("15");

		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		Object[] columns = new Object[] { "symbol", "under.close", "call.last", "call.bid", "call.ask", "call.strike_price", "expiry", "call.open_interest", "quote_date",
				"call.delta", "call.gamma", "call.vega", "call.theta", "call.implied_vol", "call.volume", "put.last", "put.bid", "put.ask", "put.open_interest", "put.delta",
				"put.gamma", "put.vega", "put.theta", "put.implied_vol", "put.volume", "straddle.last", "straddle.bid", "straddle.ask", "straddle.intrinsic", "days.to.expiry",
				"call.to.put", "call.option_id", "put.option_id", "put.strike_price" };
		MapWebCellFormatter<Object> callPutFormatter = new MapWebCellFormatter<Object>(getManager().getTextFormatter());
		callPutFormatter.addEntry(0, "Call", "style.color=blue");
		callPutFormatter.addEntry(1, "Put", "style.color=green");
		final int precision = 6;

		FastWebTable table = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());
		table.getTable().setTitle("Option TimeSeries");
		table.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		table.addColumn(true, "Quote Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		table.addColumn(true, "Call-K", "call.strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle)
				.addCssClass("bold");
		table.addColumn(true, "Put-K", "put.strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle)
				.addCssClass("bold");
		table.addColumn(true, "Expiry", "expiry", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		table.addColumn(true, "U Close", "under.close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle);
		table.addColumn(false, "Call Last", "call.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call Bid", "call.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call Ask", "call.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call O.I.", "call.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);
		table.addColumn(false, "Call Delta", "call.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(false, "Call Gamma", "call.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(false, "Call Vega", "call.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(false, "Call Theta", "call.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(false, "Call IV", "call.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call Volume", "call.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);

		table.addColumn(false, "Put Last", "put.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put Bid", "put.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put Ask", "put.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put O.I.", "put.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);
		table.addColumn(false, "Put Delta", "put.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(false, "Put Gamma", "put.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(false, "Put Vega", "put.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(false, "Put Theta", "put.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(false, "Put IV", "put.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put Volume", "put.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);

		table.addColumn(false, "Str Last", "straddle.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Bid", "straddle.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Ask", "straddle.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Prem", "straddle.intrinsic", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Days Left", "days.to.expiry", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(true, "CP ratio", "call.to.put", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(false, "Call OptID", "call.option_id", new BasicWebCellFormatter());
		table.addColumn(false, "Put OptID", "put.option_id", new BasicWebCellFormatter());

		this.setTablePortlet(new FastTablePortlet(generateConfig(), table));
		getTablePortlet().setTitle("Options TimeSeries");
		getTablePortlet().getTable().addMenuListener(this);

		//addChild(datesFormPortlet, 0, 0);
		//addChild(expiryFormPortlet, 1, 0);

		chartCPPortlet = new SeriesChartPortlet(generateConfig());

		chartCPPortlet.addOption(SeriesChartPortlet.OPTION_TITLE, "CP Ratio");

		chartDeltaPortlet = new SeriesChartPortlet(generateConfig());
		chartDeltaPortlet.addOption(SeriesChartPortlet.OPTION_KEY_POSITION, "below");

		chartVolPortlet = new SeriesChartPortlet(generateConfig());

		chartCPPortlet.setStyle(SeriesChartPortlet.STYLE_AREA);
		DividerPortlet div = new DividerPortlet(generateConfig(), false);
		div.setOffset(.5);
		div.addChild(getTablePortlet());
		DividerPortlet chartdiv = new DividerPortlet(generateConfig(), false);
		DividerPortlet chartdiv2 = new DividerPortlet(generateConfig(), false);
		chartdiv.addChild(chartdiv2);
		chartdiv.setOffset(.66);
		chartdiv2.addChild(chartCPPortlet);
		chartdiv2.addChild(chartDeltaPortlet);
		chartdiv.addChild(chartVolPortlet);
		div.addChild(chartdiv);
		addChild(div, 0, 0, 1, 1);
		//setRowSize(0, 45);
		setSuggestedSize(800, 500);
		registerChildPortletToBeSaved("OPTS", getTablePortlet());

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}

	public static class Builder extends AbstractPortletBuilder<LarkinOptionTimeSeriesPortlet> {

		private static final String ID = "Options TimeSeries";

		public Builder() {
			super(LarkinOptionTimeSeriesPortlet.class);
		}

		@Override
		public LarkinOptionTimeSeriesPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinOptionTimeSeriesPortlet portlet = new LarkinOptionTimeSeriesPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Options TimeSeries";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		// TODO Auto-generated method stub

		if (result.getAction() instanceof GetOptionDataResponse) {
			Duration d = new Duration("option processing ");
			GetOptionDataResponse response = (GetOptionDataResponse) result.getAction();
			GetOptionDataByOptionIDRequest request = (GetOptionDataByOptionIDRequest) result.getRequestMessage().getAction();
			current = response.getOptionData();

			resetTableData();
			d.stampStdout();
			long responseTime = EH.currentTimeMillis();
			this.getTablePortlet().setTableTitle("Records For " + getWorkingSymbol() + " loaded in " + SH.formatDuration(responseTime - requestTime));
		}

	}
	private void resetTableData() {
		// TODO Auto-generated method stub
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));

		int call_error_count = 0;
		int put_error_count = 0;
		chartCPPortlet.clear();
		chartVolPortlet.clear();
		for (SpreadMessage e : current) {
			OptionMessage put = e.getLeg2();
			OptionMessage call = e.getLeg1();

			if (call == null) {
				call_error_count++;
				continue;

			}
			if (put == null) {
				put_error_count++;
				continue;

			}
			double straddle = (put.getBid() + call.getBid() + call.getAsk() + put.getAsk());
			double intrinsic = Math.abs(call.getStrike() - call.getUnderlyingClose());
			double cpratio = (call.getBid() + call.getAsk()) / straddle;
			straddle *= .5;

			this.getTablePortlet().addRow(call.getUnderlying(), call.getUnderlyingClose(), call.getLast(), call.getBid(), call.getAsk(), call.getStrike(),
					call.getExpiry().getStartMillis(), call.getOpenInterest(), call.getTradeDate().getStartMillis(), call.getDelta(), call.getGamma(), call.getVega(),
					call.getTheta(), call.getImpliedVol(), call.getVolume(), put.getLast(), put.getBid(), put.getAsk(), put.getOpenInterest(), put.getDelta(), put.getGamma(),
					put.getVega(), put.getTheta(), put.getImpliedVol(), put.getVolume(), put.getLast() + call.getLast(), put.getBid() + call.getBid(),
					put.getAsk() + call.getAsk(), straddle - intrinsic, call.getDaysToExpiry(), cpratio, call.getOptionId(), put.getOptionId(), put.getStrike());

			String domain = call.getTradeDate().toStringNoTimeZone();

			if (call.getDelta() > -99)
				chartDeltaPortlet.addPoint("Delta", domain, call.getDelta());
			if (call.getGamma() > -99)
				chartDeltaPortlet.addPoint("Gamma", domain, call.getGamma() * 100);

			chartCPPortlet.addPoint("CP", domain, cpratio);

			chartVolPortlet.addPoint("Vol", domain, call.getVolume());
			chartVolPortlet.addOption(SeriesChartPortlet.OPTION_Y_MIN, 0);

			setWorkingSymbol(call.getUnderlying() + " " + datefmt.formatCellToText(call.getExpiry()) + " " + call.getStrike() + "-" + put.getStrike()
					+ (put.getStrike() != call.getStrike() ? " Strangle" : " Straddle"));
		}
		chartCPPortlet.setSeriesColor("CP", "#ff5a00");
		chartDeltaPortlet.setSeriesColor("Delta", "#b22cff");
		chartDeltaPortlet.setSeriesColor("Gamma", "green");
		chartDeltaPortlet.setSeriesLabel("Delta", "Delta");
		chartDeltaPortlet.setSeriesLabel("Gamma", "Gamma");
		chartDeltaPortlet.setStyle(SeriesChartPortlet.STYLE_LINE);
		chartVolPortlet.setStyle(SeriesChartPortlet.STYLE_BAR);
		chartVolPortlet.addOption(SeriesChartPortlet.OPTION_TITLE, "Volume");
		chartVolPortlet.setSeriesColor("Vol", "blue");

		if ((call_error_count + put_error_count) != 0)
			System.out.println("Errors in option mapping :" + call_error_count + " " + put_error_count);

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		// TODO Auto-generated method stub
		if (localSocket == recvMachineSocket) {
			//	LarkinOptionInterportletMessage ids = (LarkinOptionInterportletMessage) message;
			//	GetOptionDataByOptionIDRequest msg = nw(GetOptionDataByOptionIDRequest.class);
			//		msg.setUnderlyingSymbol(ids.getSymbol());
			//	setWorkingSymbol(ids.getSymbol());
			//msg.setLeg1(ids.getOptionIds().toLongArray());

			//	getManager().sendRequestToBackend("LARKIN", getPortletId(), msg);
			//	tablePortlet.clearRows();
			//	requestTime = EH.currentTimeMillis();

		} else
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

	}

	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}
	public void setOptionIDS(String symbol, int leg1, int leg2, Day day1, Day day2) {
		GetOptionDataByOptionIDRequest msg = nw(GetOptionDataByOptionIDRequest.class);
		msg.setUnderlyingSymbol(symbol);
		setWorkingSymbol(symbol);
		msg.setLeg1OptionId(leg1);
		msg.setLeg2OptionId(leg2);
		msg.setQuoteDate1(day1);
		msg.setQuoteDate2(day2);

		getManager().sendRequestToBackend("LARKIN", getPortletId(), msg);
		this.getTablePortlet().clearRows();
		requestTime = EH.currentTimeMillis();
	}
	public void clearData() {
		this.getTablePortlet().clearRows();
		chartCPPortlet.clear();
		chartDeltaPortlet.clear();
		chartVolPortlet.clear();
	}

	public FastTablePortlet getTablePortlet() {
		return tablePortlet;
	}

	public void setTablePortlet(FastTablePortlet tablePortlet) {
		this.tablePortlet = tablePortlet;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}
}
