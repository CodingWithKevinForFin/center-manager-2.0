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
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;

public class LarkinUnderlyingReturnsPortlet extends FastTablePortlet implements WebContextMenuFactory, WebContextMenuListener, FormPortletListener {
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;

	List<SpreadMessage> underlyingsList;

	public LarkinUnderlyingReturnsPortlet(PortletConfig config) {
		super(config, null);
		// TODO Auto-generated constructor stub
		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();

		Object[] columns = new Object[] { "symbol", "close", "bid", "ask", "quote_date", "open", "total_return", "volume", "security_id", "sod.value", "eod.value", "cashflow",
				"pl", "daily.return" };
		FastWebTable table = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());
		setTable(table);
		BasicTable inner = new BasicTable(columns);
		inner.setTitle("Underlying Returns");
		SmartTable st = new BasicSmartTable(inner);

		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";
		final int precision = 6;
		table.getTable().setTitle("Underlying data");
		table.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		table.addColumn(true, "U Close", "close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "SOD Val", "sod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "EOD Val", "eod.value", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Cash Flow", "cashflow", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "P&L", "pl", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle).addCssClass("bold");
		table.addColumn(true, "Open", "open", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Bid", "bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Ask", "ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Volume", "volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(false, "Total Return", "total_return", new NumberWebCellFormatter(localformatter.getPercentFormatter(precision)));
		table.addColumn(true, "Trade Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		table.addColumn(true, "Daily Return", "daily.return", new NumberWebCellFormatter(localformatter.getNumberFormatter(precision))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(false, "Sec ID", "security_id", new NumberWebCellFormatter(localformatter.getNumberFormatter(0)));

		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));

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

	}
	public void resetTableData(List<UnderlyingMessage> unders) {
		// TODO Auto-generated method stub

		for (UnderlyingMessage e : unders) {
			addRow(e.getSymbol(), e.getClose(), e.getBid(), e.getAsk(), e.getQuoteDate().getStartMillis(), e.getOpen(), e.getTotalReturn(), e.getVolume(), e.getSecurityId(),
					e.getStartingValue(), e.getEndingValue(), e.getCashFlow(), e.getDailyPAndL(), e.getDailyReturn());

		}

	}

	public static class Builder extends AbstractPortletBuilder<LarkinUnderlyingReturnsPortlet> {

		private static final String ID = "UnderlyingReturns";

		public Builder() {
			super(LarkinUnderlyingReturnsPortlet.class);
		}

		@Override
		public LarkinUnderlyingReturnsPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinUnderlyingReturnsPortlet portlet = new LarkinUnderlyingReturnsPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Underlying Returns";
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
