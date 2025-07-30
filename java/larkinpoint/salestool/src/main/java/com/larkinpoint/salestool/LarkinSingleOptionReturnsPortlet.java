package com.larkinpoint.salestool;

import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.larkinpoint.messages.GetOptionDataByOptionIDRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;

public class LarkinSingleOptionReturnsPortlet extends FastTablePortlet implements WebContextMenuFactory, WebContextMenuListener, FormPortletListener {
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;

	List<SpreadMessage> optionList;

	public LarkinSingleOptionReturnsPortlet(PortletConfig config) {
		super(config, null);
		// TODO Auto-generated constructor stub
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();

		Object[] columns = new Object[] { "symbol", "under.close", "leg1.last", "leg1.bid", "leg1.ask", "leg1.strike_price", "leg1.expiry", "leg1.open_interest",
				"leg1.quote_date", "leg1.volume", "days.to.expiry", "call.to.put", "leg1.option_id", "sprd.sod.value", "sprd.eod.value", "sprd.cashflow", "sprd.pl", "leg1.iv",
				"leg1.delta", "daily.return" };
		FastWebTable table = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());
		setTable(table);
		BasicTable inner = new BasicTable(columns);
		inner.setTitle("Option Returns");
		SmartTable st = new BasicSmartTable(inner);

		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));

		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		table.getTable().setTitle("Underlying data");
		final int precision = 6;
		table.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		table.addColumn(true, "Trade Date", "leg1.quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY))).addCssClass("bold");
		table.addColumn(true, "SOD Val", "sprd.sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "EOD Val", "sprd.eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Cash Flow", "sprd.cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "P&L", "sprd.pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(true, "U Close", "under.close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Days Left", "days.to.expiry", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle)
				.addCssClass("bold");
		table.addColumn(true, "CP Ratio", "call.to.put", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(60).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(false, "Last", "leg1.last", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Bid", "leg1.bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Ask", "leg1.ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Strike", "leg1.strike_price", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle)
				.addCssClass("bold");
		table.addColumn(true, "Expiry", "leg1.expiry", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY))).addCssClass("bold");
		table.addColumn(true, "O.I.", "leg1.open_interest", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Volume", "leg1.volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "IV", "leg1.iv", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Delta", "leg1.delta", new NumberWebCellFormatter(localformatter.getNumberFormatter(3))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Daily Return", "daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(false, "OptID", "leg1.option_id", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle);

		setTitle("Option Return Data");
		MapWebCellFormatter stateFormatter = new MapWebCellFormatter(getManager().getTextFormatter());

		table.setMenuFactory(this);
		//table.addMenuListener(this);

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		// TODO Auto-generated method stub
		if (result.getAction() instanceof GetOptionDataResponse) {

			GetOptionDataResponse response = (GetOptionDataResponse) result.getAction();
			GetOptionDataByOptionIDRequest request = (GetOptionDataByOptionIDRequest) result.getRequestMessage().getAction();
			optionList = response.getOptionData();

			resetTableData(optionList);

		}

	}
	public void resetTableData(List<SpreadMessage> options) {
		// TODO Auto-generated method stub

		for (SpreadMessage e : options) {

			OptionMessage option = e.getLeg1();

			addRow(option.getUnderlying(), option.getUnderlyingClose(), option.getLast(), option.getBid(), option.getAsk(), option.getStrike(),
					option.getExpiry().getStartMillis(), option.getOpenInterest(), option.getTradeDate().getStartMillis(), option.getVolume(), option.getDaysToExpiry(),
					option.getPairedRatio(), option.getOptionId(), e.getStartingValue(), e.getEndingValue(), e.getCashFlow(), e.getDailyPAndL(), option.getImpliedVol(),
					option.getDelta(), e.getDailyReturn());

		}

	}

	public static class Builder extends AbstractPortletBuilder<LarkinSingleOptionReturnsPortlet> {

		private static final String ID = "SingleOptionReturn";

		public Builder() {
			super(LarkinSingleOptionReturnsPortlet.class);
		}

		@Override
		public LarkinSingleOptionReturnsPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinSingleOptionReturnsPortlet portlet = new LarkinSingleOptionReturnsPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Single Option Returns";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}

}
