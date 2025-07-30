package com.larkinpoint.salestool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletDayChooserField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
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
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.larkinpoint.messages.GetOptionDataBySymbolDateRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;
import com.larkinpoint.salestool.portlets.SymbolChooserPortlet;

public class LarkinOptionQueryToolPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory {

	private SymbolChooserPortlet SymbolNameField;
	private FormPortletDayChooserField QuoteDateNameField1;
	private FormPortletTextField QuoteDateNameField2;
	private FormPortletNumericRangeField MaxDaystoExpiry;
	private FormPortletNumericRangeField MinDaystoExpiry;
	private FormPortletNumericRangeField NumberOfStrikes;

	private FormPortletSelectField<Boolean> QueryDatabase;

	private FormPortletButton runButton;
	private FormPortlet symbolFormPortlet;

	private FormPortlet datesFormPortlet;
	private FormPortlet expiryFormPortlet;

	private FormPortlet col4Portlet;
	private FormPortlet btnFormPortlet;
	private FastTablePortlet tablePortlet;

	//private Map<Tuple4<String, Day, Day, Double>, Tuple2<OptionMessage, OptionMessage>> current;
	private List<SpreadMessage> current = new ArrayList<SpreadMessage>();
	private String WorkingSymbol;
	private long requestTime;
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;
	public HtmlPortlet Data;
	private Larkin3dPortlet chartPortlet;

	private Set<Row> currentSelections = new IdentityHashSet<Row>();

	public String getWorkingSymbol() {
		return WorkingSymbol;
	}

	public void setWorkingSymbol(String workingSymbol) {
		WorkingSymbol = workingSymbol;
	}

	public LarkinOptionQueryToolPortlet(PortletConfig config) {
		super(config);
		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));

		symbolFormPortlet = new FormPortlet(generateConfig());
		datesFormPortlet = new FormPortlet(generateConfig());

		expiryFormPortlet = new FormPortlet(generateConfig());
		col4Portlet = new FormPortlet(generateConfig());
		btnFormPortlet = new FormPortlet(generateConfig());
		btnFormPortlet.addFormPortletListener(this);

		symbolFormPortlet.addField(this.SymbolNameField = new SymbolChooserPortlet("Choose Symbol:"));
		symbolFormPortlet.addField(this.NumberOfStrikes = new FormPortletNumericRangeField("Strike Count:").setRange(1, 500).setWidth(150)).setValue(1d);

		datesFormPortlet.addField(this.QuoteDateNameField1 = new FormPortletDayChooserField("Quote Date:", getManager().getLocaleFormatter().getTimeZone(), true));
		//datesFormPortlet.addField(this.QuoteDateNameField2 = new FormPortletTextField("Exit Date:").setWidth(75)).setValue("12/31/2012");
		expiryFormPortlet.addField(this.MaxDaystoExpiry = new FormPortletNumericRangeField("Max Days Left:").setRange(10, 2000).setWidth(100)).setValue(56d);
		expiryFormPortlet.addField(this.MinDaystoExpiry = new FormPortletNumericRangeField("Min Days Left:").setRange(1, 20).setWidth(100)).setValue(15d);

		col4Portlet.addField(this.QueryDatabase = new FormPortletSelectField<Boolean>(Boolean.class, "Query"));
		btnFormPortlet.addButton(runButton = new FormPortletButton("Fetch Data"));

		QueryDatabase.addOption(false, "Cache Only");
		QueryDatabase.addOption(true, "Query Database");

		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		Object[] columns = new Object[] { "symbol", "under.close", "call.last", "call.bid", "call.ask", "strike_price", "expiry", "call.open_interest", "quote_date", "call.delta",
				"call.gamma", "call.vega", "call.theta", "call.implied_vol", "call.volume", "put.last", "put.bid", "put.ask", "put.open_interest", "put.delta", "put.gamma",
				"put.vega", "put.theta", "put.implied_vol", "put.volume", "straddle.last", "straddle.bid", "straddle.ask", "straddle.intrinsic", "days.to.expiry", "call.to.put",
				"call.option_id", "put.option_id", "norm.strike" };
		MapWebCellFormatter<Object> callPutFormatter = new MapWebCellFormatter<Object>(getManager().getTextFormatter());
		callPutFormatter.addEntry(0, "Call", "style.color=blue");
		callPutFormatter.addEntry(1, "Put", "style.color=green");
		FastWebTable table = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());
		table.getTable().setTitle("Option data");
		table.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		table.addColumn(true, "Quote Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));

		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		table.addColumn(true, "Strike", "strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(true, "Expiry", "expiry", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		table.addColumn(true, "U Close", "under.close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(80).addCssClass(calcStyle);
		table.addColumn(true, "Call Last", "call.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call Bid", "call.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call Ask", "call.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(callStyle);
		table.addColumn(true, "Call O.I.", "call.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);
		table.addColumn(true, "Call Delta", "call.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call Gamma", "call.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call Vega", "call.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call Theta", "call.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call IV", "call.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(callStyle);
		table.addColumn(true, "Call Volume", "call.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(callStyle);

		table.addColumn(true, "Put Last", "put.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put Bid", "put.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put Ask", "put.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(80).addCssClass(putStyle);
		table.addColumn(true, "Put O.I.", "put.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);
		table.addColumn(true, "Put Delta", "put.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put Gamma", "put.gamma", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put Vega", "put.vega", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put Theta", "put.theta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put IV", "put.implied_vol", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(putStyle);
		table.addColumn(true, "Put Volume", "put.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(putStyle);

		table.addColumn(true, "Str Last", "straddle.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Bid", "straddle.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Ask", "straddle.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Str Prem", "straddle.intrinsic", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Norm Strike", "norm.strike", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle);
		table.addColumn(true, "Days Left", "days.to.expiry", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(true, "CP ratio", "call.to.put", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(false, "Call OptID", "call.option_id", new BasicWebCellFormatter());
		table.addColumn(false, "Put OptID", "put.option_id", new BasicWebCellFormatter());

		this.tablePortlet = new FastTablePortlet(generateConfig(), table);
		tablePortlet.setTitle("Underlying data");
		tablePortlet.getTable().addMenuListener(this);
		table.setMenuFactory(this);

		Data = new HtmlPortlet(generateConfig(), "", "portal_form");

		addChild(symbolFormPortlet, 0, 0);
		addChild(datesFormPortlet, 1, 0);
		addChild(expiryFormPortlet, 2, 0);
		addChild(col4Portlet, 3, 0);
		addChild(btnFormPortlet, 4, 0);

		this.chartPortlet = new Larkin3dPortlet(generateConfig());
		DividerPortlet div = new DividerPortlet(generateConfig(), false);
		div.addChild(tablePortlet);
		div.addChild(chartPortlet);
		addChild(div, 0, 1, 5, 10);
		this.chartPortlet.setCenterY(20);
		this.chartPortlet.setCenterX(-180);
		//addOption("rotY", "180");
		//addOption("rotX", "180");
		//addOption("rotZ", "180");
		this.chartPortlet.setRotY(-22);
		this.chartPortlet.setRotX(-26);
		this.chartPortlet.setZoom(.7);

		setRowSize(0, 45);
		registerChildPortletToBeSaved("OPGR", tablePortlet);

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.runButton) {
			GetOptionDataBySymbolDateRequest msg = nw(GetOptionDataBySymbolDateRequest.class);
			msg.setUnderlyingSymbol(SymbolNameField.getValue());
			setWorkingSymbol(SymbolNameField.getValue());
			msg.setQuoteDate1((BasicDay) QuoteDateNameField1.getValue().getA());
			msg.setQuoteDate2((BasicDay) QuoteDateNameField1.getValue().getB());
			msg.setQueryDatabase(QueryDatabase.getValue());
			msg.setMaxDaysToExpiry((MaxDaystoExpiry.getIntValue()));
			msg.setMinDaysToExpiry((MinDaystoExpiry.getIntValue()));
			msg.setStrikeCount(NumberOfStrikes.getIntValue());
			current.clear();
			getManager().sendRequestToBackend("LARKIN", getPortletId(), msg);
			tablePortlet.clearRows();
			requestTime = EH.currentTimeMillis();

		}
	}

	public static class Builder extends AbstractPortletBuilder<LarkinOptionQueryToolPortlet> {

		private static final String ID = "Query Tool";

		public Builder() {
			super(LarkinOptionQueryToolPortlet.class);
		}

		@Override
		public LarkinOptionQueryToolPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinOptionQueryToolPortlet portlet = new LarkinOptionQueryToolPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Query Tool";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		// TODO Auto-generated method stub

		Duration d = new Duration("option processing ");
		GetOptionDataResponse response = (GetOptionDataResponse) result.getAction();
		GetOptionDataBySymbolDateRequest request = (GetOptionDataBySymbolDateRequest) result.getRequestMessage().getAction();
		current.addAll(response.getOptionData());

		if (result.getIsIntermediateResult() == false) {

			resetTableData();
			d.stampStdout();
			long responseTime = EH.currentTimeMillis();
			this.tablePortlet.setTableTitle("Options For " + getWorkingSymbol() + " loaded in " + SH.formatDuration(responseTime - requestTime));
		}
	}
	private void resetTableData() {
		// TODO Auto-generated method stub
		int call_error_count = 0;
		int put_error_count = 0;
		double underlying = 0.0;
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));
		NumberWebCellFormatter perfmt = new NumberWebCellFormatter(localformatter.getPercentFormatter(2));

		this.chartPortlet.clearPoints();
		int cnt = 0;
		for (SpreadMessage e : current) {
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

			double normStrike = Math.log(call.getStrike() / call.getUnderlyingClose());
			this.tablePortlet.addRow(call.getUnderlying(), call.getUnderlyingClose(), call.getLast(), call.getBid(), call.getAsk(), call.getStrike(), call.getExpiry()
					.getStartMillis(), call.getOpenInterest(), call.getTradeDate().getStartMillis(), call.getDelta(), call.getGamma(), call.getVega(), call.getTheta(), call
					.getImpliedVol(), call.getVolume(), put.getLast(), put.getBid(), put.getAsk(), put.getOpenInterest(), put.getDelta(), put.getGamma(), put.getVega(), put
					.getTheta(), put.getImpliedVol(), put.getVolume(), put.getLast() + call.getLast(), put.getBid() + call.getBid(), put.getAsk() + call.getAsk(),
					call.getPairedValue() - call.getIntrinsicValue(), call.getDaysToExpiry(), call.getPairedRatio(), call.getOptionId(), put.getOptionId(), normStrike);

			if (cnt == 2000)
				continue;
			double exp = (put.getExpiry().getStartMillis() - put.getTradeDate().getStartMillis()) / TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

			if (normStrike <= 0.0 && put.getImpliedVol() >= 0) {
				this.chartPortlet.addSurfaceValue(normStrike, exp, put.getImpliedVol());
				cnt++;
			} else if (normStrike > 0.0 && call.getImpliedVol() >= 0) {
				this.chartPortlet.addSurfaceValue(normStrike, exp, call.getImpliedVol());
				cnt++;
			}
			//put.getStrike();
			//put.getImpliedVol();
		}

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
		FastWebTable t = this.tablePortlet.getTable();

		if (setMachineSocket.hasConnections()) {
			List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
			LongSet selections = new LongSet();
			for (Row addRow : sel) {
				selections.add((Long) addRow.get("call.option_id"));
				selections.add((Long) addRow.get("put.option_id"));
			}
			setMachineSocket.sendMessage(new LarkinOptionInterportletMessage(selections, SymbolNameField.getValue()));
		}

	}

	@Override
	public void onVisibleRowsChanged(FastWebTable fastWebTable) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

}
