package com.larkinpoint.salestool;

import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.BasicDay;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateRequest;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateResponse;
import com.larkinpoint.messages.UnderlyingMessage;
import com.larkinpoint.salestool.portlets.SymbolChooserPortlet;

public class LarkinUnderlyingDataPortlet extends GridPortlet implements FormPortletListener {

	private SymbolChooserPortlet SymbolNameField;
	//	private FormPortletDayChooserField QuoteDateNameField;

	private FormPortletButton runButton;
	private FormPortletButton rtnButton;
	private FormPortlet symFormPortlet;
	private FormPortlet btnFormPortlet;
	private FastTablePortlet tablePortlet;

	public LarkinUnderlyingDataPortlet(PortletConfig config) {
		super(config);
		this.symFormPortlet = new FormPortlet(generateConfig());
		this.btnFormPortlet = new FormPortlet(generateConfig());
		this.symFormPortlet.addFormPortletListener(this);
		this.btnFormPortlet.addFormPortletListener(this);

		symFormPortlet.addField(this.SymbolNameField = new SymbolChooserPortlet("Choose Symbol:"));
		btnFormPortlet.addButton(runButton = new FormPortletButton("Show Closing Prints"));

		String calcStyle = "red";
		String callStyle = "green";
		String putStyle = "blue";

		LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
		Object[] columns = new Object[] { "symbol", "close", "bid", "ask", "quote_date", "open", "total_return", "volume", "security_id" };
		FastWebTable table = new FastWebTable(new BasicSmartTable(new BasicTable(columns)), getManager().getTextFormatter());
		table.getTable().setTitle("Underlying data");
		table.addColumn(true, "Symbol", "symbol", new BasicWebCellFormatter()).addCssClass("bold");
		table.addColumn(true, "Close", "close", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Open", "open", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Bid", "bid", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Ask", "ask", new NumberWebCellFormatter(localformatter.getNumberFormatter(2))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Volume", "volume", new NumberWebCellFormatter(localformatter.getNumberFormatter(0))).setWidth(60).addCssClass(calcStyle);
		table.addColumn(true, "Total Return", "total_return", new NumberWebCellFormatter(localformatter.getPercentFormatter(3)));
		table.addColumn(true, "Quote Date", "quote_date", new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY)));
		table.addColumn(false, "Sec ID", "security_id", new NumberWebCellFormatter(localformatter.getNumberFormatter(0)));
		this.tablePortlet = new FastTablePortlet(generateConfig(), table);
		tablePortlet.setTitle("Underlying data");

		addChild(symFormPortlet, 0, 0);

		addChild(btnFormPortlet, 1, 0);
		//	addChild(rbtnFormPortlet, 2, 1, 1, 1);
		addChild(tablePortlet, 0, 1, 2, 10);
		setRowSize(0, 45);
		setSuggestedSize(580, 700);
		registerChildPortletToBeSaved("UNTB", tablePortlet);

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.runButton) {
			GetUnderlyingDataBySymbolDateRequest msg = nw(GetUnderlyingDataBySymbolDateRequest.class);
			msg.setUnderlyingSymbol(SymbolNameField.getValue());

			TimeZone tz = getManager().getLocaleFormatter().getTimeZone();
			BasicDay bd = new BasicDay(tz, getManager().getState().getWebState().getPartition().getContainer().getTools().getNow());
			//	msg.setQuoteDate1(bd);
			//	msg.setQuoteDate2(bd);

			getManager().sendRequestToBackend("LARKIN", getPortletId(), msg);
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub

	}

	public static class Builder extends AbstractPortletBuilder<LarkinUnderlyingDataPortlet> {

		private static final String ID = "getunderdata";

		public Builder() {
			super(LarkinUnderlyingDataPortlet.class);
		}

		@Override
		public LarkinUnderlyingDataPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinUnderlyingDataPortlet portlet = new LarkinUnderlyingDataPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Show Underlyings";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		this.tablePortlet.clearRows();
		GetUnderlyingDataBySymbolDateResponse response = (GetUnderlyingDataBySymbolDateResponse) result.getAction();
		GetUnderlyingDataBySymbolDateRequest request = (GetUnderlyingDataBySymbolDateRequest) result.getRequestMessage().getAction();
		List<UnderlyingMessage> message = response.getUnderlyingData();

		for (UnderlyingMessage under : message)
			this.tablePortlet.addRow(under.getSymbol(), under.getClose(), under.getBid(), under.getAsk(), under.getQuoteDate(), under.getOpen(), under.getTotalReturn(),
					under.getVolume(), under.getSecurityId());

		this.tablePortlet.setTableTitle("Underlying records for " + request.getUnderlyingSymbol());

		// TODO Auto-generated method stub

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}
}
