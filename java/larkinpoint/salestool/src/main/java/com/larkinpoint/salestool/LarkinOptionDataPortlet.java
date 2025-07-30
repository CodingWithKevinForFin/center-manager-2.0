package com.larkinpoint.salestool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Day;
import com.f1.base.Message;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.chart.SeriesChartPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
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
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.BasicDay;
import com.f1.utils.CH;
import com.f1.utils.Duration;
import com.f1.utils.EH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongSet;
import com.larkinpoint.messages.GetLarkinBackTestingDataRequest;
import com.larkinpoint.messages.GetLarkinPutsRequest;
import com.larkinpoint.messages.GetOptionDataResponse;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateRequest;
import com.larkinpoint.messages.GetUnderlyingDataBySymbolDateResponse;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;
import com.larkinpoint.salestool.messages.LarkinOptionInterportletMessage;
import com.larkinpoint.salestool.portlets.SymbolChooserPortlet;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoUser;
import com.sso.messages.UpdateSsoGroupRequest;
import com.vortex.ssoweb.SsoService;
import com.vortex.ssoweb.SsoWebGroup;

public class LarkinOptionDataPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory {

	LocaleFormatter localformatter;
	GetLarkinBackTestingDataRequest straddleMsg;
	GetLarkinPutsRequest puts1Msg;
	GetLarkinPutsRequest puts2Msg;
	LarkinParameters Params;
	
	private SsoWebGroup group;
	private SsoUser user;
	private SsoService ssoservice;

	//	private FormPortletSelectField<String> SymbolNameField;
	private SymbolChooserPortlet SymbolNameField;
	private FormPortletDayChooserField QuoteDateNameField1;

	private FormPortletSelectField<Integer> StraddleChooser;
	private FormPortletTextField UnderlyingCount;
	private FormPortletTextField UnderlyingValue;
	private FormPortletTextField InvestmentValue;
	private FormPortletNumericRangeField CashPercentage;
	private FormPortletTextField InvestedAmount;
	private FormPortletTextField ManagementFee;
	//	private FormPortletTextField UnderlyingCount;

	private FormPortletNumericRangeField straddleDaysUB;
	private FormPortletNumericRangeField straddleDaysLB;
	private FormPortletNumericRangeField straddleRatioUB;
	private FormPortletNumericRangeField straddleRatioLB;
	private FormPortletNumericRangeField straddleRatio;
	private FormPortletNumericRangeField straddleCount;

	private FormPortletTextField putBucket1DaysUB;
	private FormPortletTextField putBucket2DaysUB;
	private FormPortletTextField putBucket3DaysUB;
	private FormPortletTextField putBucket1DaysLB;
	private FormPortletTextField putBucket2DaysLB;
	private FormPortletTextField putBucket3DaysLB;

	private FormPortletNumericRangeField putBucket1cpRatio;
	private FormPortletNumericRangeField putBucket1cpRatioUB;
	private FormPortletNumericRangeField putBucket1cpRatioLB;
	private FormPortletNumericRangeField putBucket1Count;

	private FormPortletNumericRangeField putBucket2cpRatio;
	private FormPortletNumericRangeField putBucket2cpRatioUB;
	private FormPortletNumericRangeField putBucket2cpRatioLB;
	private FormPortletNumericRangeField putBucket2Count;

	private FormPortletNumericRangeField putBucket3cpRatio;
	private FormPortletNumericRangeField putBucket3cpRatioUB;
	private FormPortletNumericRangeField putBucket3cpRatioLB;
	private FormPortletNumericRangeField putBucket3Count;

	private FormPortletButton runScenarioButton;
	private FormPortletButton saveScenariosButton;
	private FormPortletButton loadScenariosButton;

	private FormPortlet symbolFormPortlet;
	private FormPortlet investmentDataFormPortlet;
	private FormPortlet straddleFormPortlet;
	private FormPortlet putBucket1Portlet;
	private FormPortlet putBucket2Portlet;
	private LarkinLogoPortlet logo;

	public HtmlPortlet Data;
	public HtmlPortlet Data1;
	public HtmlPortlet DataCol1;
	public HtmlPortlet DataCol2;
	public HtmlPortlet DataCol3;
	public HtmlPortlet DataCol4;
	public HtmlPortlet DataCol5;
	private String larkinUrl = "<a href=\"http://www.w3schools.com/\">Visit W3Schools</a>";

	private FastTablePortlet tablePortlet;
	private FastTablePortlet summaryTablePortlet;
	private LarkinOptionTimeSeriesPortlet tsPortlet;
	private DividerPortlet div;
	private TabPortlet tabPortlet;
	private LarkinSingleOptionReturnsPortlet puts1Portlet;
	private LarkinSingleOptionReturnsPortlet puts2Portlet;
	private LarkinUnderlyingReturnsPortlet underReturnsPortlet;
	private SeriesChartPortlet plChart;
	private SeriesChartPortlet returnsChart;

	private List<SpreadMessage> nearMonthStraddlesList = new ArrayList<SpreadMessage>();
	private List<UnderlyingMessage> underlyingsList = new ArrayList<UnderlyingMessage>();
	private List<SpreadMessage> backMonthPutsList1 = new ArrayList<SpreadMessage>();
	private List<SpreadMessage> backMonthPutsList2 = new ArrayList<SpreadMessage>();

	private String WorkingSymbol;
	private long requestTime;
	public BasicPortletSocket setMachineSocket;
	public BasicPortletSocket recvMachineSocket;
	BasicMultiMap.List<Day, Message> tradeDateMasher;
	BasicMultiMap.List<Day, UnderlyingMessage> underlyingMasher;
	BasicMultiMap.List<Day, SpreadMessage> straddleMasher;
	BasicMultiMap.List<Day, SpreadMessage> putsMasher;
	private TabPortlet reportTabs;

	public String getWorkingSymbol() {
		return WorkingSymbol;
	}

	public void setWorkingSymbol(String workingSymbol) {
		WorkingSymbol = workingSymbol;
	}

	public LarkinOptionDataPortlet(PortletConfig config) {
		super(config);
		this.ssoservice = (SsoService) getManager().getService(SsoService.ID);
		
		localformatter = getManager().getState().getWebState().getFormatter();

		this.setMachineSocket = addSocket(true, "Option Send Selection", "Set selection", true, CH.s(LarkinOptionInterportletMessage.class), null);
		this.recvMachineSocket = addSocket(false, "Option Receive Selection", "Receive selection", true, null, CH.s(LarkinOptionInterportletMessage.class));
		//Create the Portlets
		{
			symbolFormPortlet = new FormPortlet(generateConfig());
			investmentDataFormPortlet = new FormPortlet(generateConfig());
			straddleFormPortlet = new FormPortlet(generateConfig());
			putBucket1Portlet = new FormPortlet(generateConfig());
			putBucket2Portlet = new FormPortlet(generateConfig());
			div = new DividerPortlet(generateConfig(), true);
			reportTabs = new TabPortlet(generateConfig());

			logo = new LarkinLogoPortlet(generateConfig());
			Data = new HtmlPortlet(generateConfig(), "", "larkin_form");
			Data1 = new HtmlPortlet(generateConfig(), "", "larkin_form");
			DataCol1 = new HtmlPortlet(generateConfig(), "", "larkin_form");
			DataCol2 = new HtmlPortlet(generateConfig(), "", "larkin_form");
			DataCol3 = new HtmlPortlet(generateConfig(), "", "larkin_form");
			DataCol4 = new HtmlPortlet(generateConfig(), "", "larkin_form");
			DataCol5 = new HtmlPortlet(generateConfig(), "", "larkin_form");
		}

		//Add Listeners to Portlets for callbacks
		{
			symbolFormPortlet.addFormPortletListener(this);
			investmentDataFormPortlet.addFormPortletListener(this);
		}
		//Add Fields to Portlets and set values and defaults
		{

			symbolFormPortlet.addField(this.SymbolNameField = new SymbolChooserPortlet("Choose Symbol:"));
			symbolFormPortlet.addField(this.StraddleChooser = new FormPortletSelectField<Integer>(Integer.class, "Choose Pair:"));
			symbolFormPortlet.addField(this.QuoteDateNameField1 = new FormPortletDayChooserField("Entry / Exit Date:", getManager().getLocaleFormatter().getTimeZone(), true));
			//symbolFormPortlet.addField(new FormPortletButtonField("add dates").setValue("..."));

			symbolFormPortlet.addButton(runScenarioButton = new FormPortletButton("Run Scenario"));
			symbolFormPortlet.addButton(saveScenariosButton = new FormPortletButton("Save Scenarios"));
			symbolFormPortlet.addButton(loadScenariosButton = new FormPortletButton("Load Scenarios"));

			putBucket1Portlet.addField(this.putBucket1cpRatio = new FormPortletNumericRangeField("Put #1 Ratio:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(.8D);
			putBucket1Portlet.addField(this.putBucket1cpRatioUB = new FormPortletNumericRangeField("Put #1 Ratio UB:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(
					.95D);
			putBucket1Portlet.addField(this.putBucket1cpRatioLB = new FormPortletNumericRangeField("Put #1 Ratio LB:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(
					.55D);
			putBucket1Portlet.addField(this.putBucket1DaysUB = new FormPortletTextField("Put #1 Days UB:").setWidth(75)).setValue("370");
			putBucket1Portlet.addField(this.putBucket1DaysLB = new FormPortletTextField("Put #1 Days LB:").setWidth(75)).setValue("270");
			putBucket1Portlet.addField(this.putBucket1Count = new FormPortletNumericRangeField("Put #1  Count:").setDecimals(3).setWidth(75).setRange(-2, 2)).setValue(1D);

			putBucket2Portlet.addField(this.putBucket2cpRatio = new FormPortletNumericRangeField("Put #2 Ratio:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(.55D);
			putBucket2Portlet.addField(this.putBucket2cpRatioUB = new FormPortletNumericRangeField("Put #2 Ratio UB:").setDecimals(3).setWidth(75).setRange(.01, .99))
					.setValue(.9D);
			putBucket2Portlet.addField(this.putBucket2cpRatioLB = new FormPortletNumericRangeField("Put #2 Ratio LB:").setDecimals(3).setWidth(75).setRange(.01, .99))
					.setValue(.3D);
			putBucket2Portlet.addField(this.putBucket2DaysUB = new FormPortletTextField("Put #2 Days UB:").setWidth(75)).setValue("730");
			putBucket2Portlet.addField(this.putBucket2DaysLB = new FormPortletTextField("Put #2 Days LB:").setWidth(75)).setValue("370");
			putBucket2Portlet.addField(this.putBucket2Count = new FormPortletNumericRangeField("Put #2  Count:").setDecimals(3).setWidth(75).setRange(-2, 2)).setValue(1D);

			investmentDataFormPortlet.addField(this.InvestmentValue = new FormPortletTextField("Investment Value:").setWidth(85)).setValue("1000000");
			investmentDataFormPortlet.addField(this.UnderlyingValue = new FormPortletTextField("Underlying Value:").setWidth(85)).setValue("0");
			investmentDataFormPortlet.addField(this.UnderlyingCount = new FormPortletTextField("Underlying Count:").setWidth(85)).setValue("1");
			investmentDataFormPortlet.addField(this.InvestedAmount = new FormPortletTextField("Cash Invested:").setWidth(85)).setValue("0");
			investmentDataFormPortlet.addField(this.CashPercentage = new FormPortletNumericRangeField("Min Cash %:").setWidth(85).setRange(1, 100)).setValue(50d);
			investmentDataFormPortlet.addField(this.ManagementFee = new FormPortletTextField("Management fee:").setWidth(85)).setValue("1.95");

			straddleFormPortlet.addField(this.straddleRatio = new FormPortletNumericRangeField("Strd Ratio:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(.45D);
			straddleFormPortlet.addField(this.straddleDaysUB = new FormPortletNumericRangeField("Strd Days UB:").setWidth(75).setRange(0, 1000)).setValue(58D);
			straddleFormPortlet.addField(this.straddleDaysLB = new FormPortletNumericRangeField("Strd Days LB:").setWidth(75).setRange(0, 1000)).setValue(19D);
			straddleFormPortlet.addField(this.straddleRatioUB = new FormPortletNumericRangeField("Strd Ratio UB:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(.9D);
			straddleFormPortlet.addField(this.straddleRatioLB = new FormPortletNumericRangeField("Strd Ratio LB:").setDecimals(3).setWidth(75).setRange(.01, .99)).setValue(.2D);
			straddleFormPortlet.addField(this.straddleCount = new FormPortletNumericRangeField("Strd Count:").setDecimals(3).setWidth(75).setRange(-2, 2)).setValue(1D);

			reportTabs.setIsCustomizable(true);
		}
		//Add values to our drop downs
		{
			StraddleChooser.addOption(0, "ATM Straddle");
			StraddleChooser.addOption(1, "1st Strangle");
			StraddleChooser.addOption(2, "2nd Strangle");

			//	tabPortlet.setIsCustomizable(false);

		}
		MapWebCellFormatter<Object> callPutFormatter = new MapWebCellFormatter<Object>(getManager().getTextFormatter());
		callPutFormatter.addEntry(0, "Call", "style.color=blue");
		callPutFormatter.addEntry(1, "Put", "style.color=green");

		///mainTableSetup();
		///summaryTableSetup();

		//Layout the grid
		int totalCols = 7;
		int totalRows = 4;
		int colCount = 0;
		int rowCount = 1;
		{

			{
				addChild(symbolFormPortlet, colCount, rowCount, 1, 2);
				setColSize(colCount++, 310);
			}
			{
				addChild(investmentDataFormPortlet, colCount, rowCount, 1, 2);
				setColSize(colCount++, 200);
			}
			{
				addChild(putBucket2Portlet, colCount, rowCount, 1, 2);
				setColSize(colCount++, 200);
			}
			{
				addChild(straddleFormPortlet, colCount, rowCount, 1, 2);
				setColSize(colCount++, 200);
			}
			{
				addChild(putBucket1Portlet, colCount, rowCount, 1, 2);
				setColSize(colCount++, 180);
			}
			
			addChild(DataCol1, 0, 0, 1, 1);
			addChild(DataCol2, 1, 0, 2, 1);
			//addChild(DataCol3, 2, 0, 1, 1);
			addChild(DataCol4, 3, 0, 2, 1);
			//addChild(DataCol5, 4, 0, 1, 1);
		//	addChild(Data1, 5, 0, totalCols - colCount, 1);
			//addChild(Data, colCount, rowCount+1, totalCols - ++colCount, 1);
			// addChild(Data, colCount, rowCount+1, totalCols - ++colCount, 1);

			{
				//addChild(logo, totalCols - 1, 0, 1, 1);
				addChild(logo, totalCols-2 , 0, 1,3);
				setColSize(totalCols - 2, 390);
			//	setColSize(totalCols -1, 300);
			}

			rowCount += 2;

			//		addChild(div, 0, rowCount, totalCols, 1);
			setRowSize(0, 40);
			setRowSize(1, 70);
			setRowSize(2, 70);
		}
		addChild(reportTabs, 0, rowCount, totalCols, 1);
		String columnHeader1 = "<div class='LarkinTitle'> Index Selection:</div>";
		DataCol1.setHtml(columnHeader1);
		String columnHeader2 = "<div class='LarkinTitle' > Hedged Equity Allocation:</div>";
		DataCol2.setHtml(columnHeader2);
	//	String columnHeader3 = "<div class='LarkinTitle' > Income Bracket:</div>";
	//	DataCol3.setHtml(columnHeader3);
		String columnHeader4 = "<div class='LarkinTitle' > Hedged Income Strategy:</div>";
		DataCol4.setHtml(columnHeader4);
	//	String columnHeader5 = "<div class='LarkinTitle' > 2nd Risk Bracket:</div>";
	//	DataCol5.setHtml(columnHeader5);

	}
	public void setParams() {
		Params = new LarkinParameters();
		Params.setSymbolName(SymbolNameField.getValue());
		Params.setStartingQuoteDate((Day) QuoteDateNameField1.getValue().getA());
		Params.setEndingQuoteDate((Day) QuoteDateNameField1.getValue().getB());
		Params.setStraddleChoice(StraddleChooser.getValue());
		Params.setUnderlyingValue(SH.parseFloat(UnderlyingValue.getValue()));
		Params.setInvestmentValue(SH.parseDouble(InvestmentValue.getValue()));
		Params.setInvestmentPercentage(CashPercentage.getValue().floatValue());
		Params.setInvestedAmount(SH.parseDouble(InvestedAmount.getValue()));
		Params.setManagementFee(SH.parseFloat(ManagementFee.getValue()));

		Params.setStraddleDaysUB(straddleDaysUB.getIntValue());
		Params.setStraddleDaysLB(straddleDaysLB.getIntValue());
		Params.setStraddleRatioUB(straddleRatioUB.getValue().floatValue());
		Params.setStraddleRatioLB(straddleRatioLB.getValue().floatValue());
		Params.setStraddleRatio(straddleRatio.getValue().floatValue());
		Params.setStraddleCount(straddleCount.getValue().floatValue());

		Params.setPutBucket1DaysUB(SH.parseInt(putBucket1DaysUB.getValue()));
		Params.setPutBucket1DaysLB(SH.parseInt(putBucket1DaysLB.getValue()));
		Params.setPutBucket1cpRatio(putBucket1cpRatio.getValue().floatValue());
		Params.setPutBucket1cpRatioUB(putBucket1cpRatioUB.getValue().floatValue());
		Params.setPutBucket1cpRatioLB(putBucket1cpRatioLB.getValue().floatValue());
		Params.setPutBucket1Count(putBucket1Count.getValue().floatValue());

		Params.setPutBucket2DaysLB(SH.parseInt(putBucket2DaysLB.getValue()));
		Params.setPutBucket2DaysUB(SH.parseInt(putBucket2DaysUB.getValue()));
		Params.setPutBucket2cpRatio(putBucket2cpRatio.getValue().floatValue());
		Params.setPutBucket2cpRatioUB(putBucket2cpRatioUB.getValue().floatValue());
		Params.setPutBucket2cpRatioLB(putBucket2cpRatioLB.getValue().floatValue());
		Params.setPutBucket2Count(putBucket2Count.getValue().floatValue());

		//	msg1.setMaxDaysToExpiry(SH.parseInt(putBucket1DaysUB.getValue()));
		//	msg1.setMinDaysToExpiry(SH.parseInt(putBucket1DaysLB.getValue()));

	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (remainingRequest != 0) {
			getManager().showAlert("Still processing prior request");
			return;
		}
		setParams();

		if (button == this.runScenarioButton) {
			//Format and send the message for the front month straddles...the day may come to do all this in one fell swoop...but for now...
			{
				if( checkDates(Params.getStartingQuoteDate(),Params.getEndingQuoteDate() )== false){
					getManager().showAlert("Bad Date Range...please correct and try again ");
					return;
					
				}
				GetLarkinBackTestingDataRequest msg = nw(GetLarkinBackTestingDataRequest.class);

				msg.setUnderlyingSymbol(Params.getSymbolName());
				setWorkingSymbol(Params.getSymbolName());
				msg.setQuoteDate1((BasicDay) Params.getStartingQuoteDate());
				msg.setQuoteDate2((BasicDay) Params.getEndingQuoteDate());
				msg.setQueryDatabase(false);
				msg.setMaxDaysToExpiry(Params.getStraddleDaysUB());
				msg.setMinDaysToExpiry(Params.getStraddleDaysLB());
				msg.setStrikeStep(Params.getStraddleChoice());
				msg.setRatio(Params.getStraddleRatio());
				msg.setRatioLowerBound(Params.getStraddleRatioLB());
				msg.setRatioUpperBound(Params.getStraddleRatioUB());
				msg.setTradeAmount(Params.getStraddleCount());
				getManager().sendRequestToBackend("LARKIN", getPortletId(), msg);
				remainingRequest++;
				nearMonthStraddlesList.clear();
				requestTime = EH.currentTimeMillis();
			}
			//now do the puts
			{
				GetLarkinPutsRequest msg1 = nw(GetLarkinPutsRequest.class);
				msg1.setUnderlyingSymbol(Params.getSymbolName());

				msg1.setQuoteDate1((BasicDay) Params.getStartingQuoteDate());
				msg1.setQuoteDate2((BasicDay) Params.getEndingQuoteDate());
				msg1.setQueryDatabase(false);
				msg1.setMaxDaysToExpiry(Params.getPutBucket1DaysUB());
				msg1.setMinDaysToExpiry(Params.getPutBucket1DaysLB());
				msg1.setStrikeStep(0);
				msg1.setPutRatio1(Params.getPutBucket1cpRatio());
				msg1.setPutRatio1LowerBound(Params.getPutBucket1cpRatioLB());
				msg1.setPutRatio1UpperBound(Params.getPutBucket1cpRatioUB());
				msg1.setTradeAmount(Params.getPutBucket1Count());
				puts1Msg = msg1;
				getManager().sendRequestToBackend("LARKIN", getPortletId(), msg1);
				remainingRequest++;
				backMonthPutsList1.clear();

			}
			{
				GetLarkinPutsRequest msg1 = nw(GetLarkinPutsRequest.class);
				msg1.setUnderlyingSymbol(Params.getSymbolName());

				msg1.setQuoteDate1((BasicDay) Params.getStartingQuoteDate());
				msg1.setQuoteDate2((BasicDay) Params.getEndingQuoteDate());
				msg1.setQueryDatabase(false);
				msg1.setMaxDaysToExpiry(Params.getPutBucket2DaysUB());
				msg1.setMinDaysToExpiry(Params.getPutBucket2DaysLB());
				msg1.setStrikeStep(0);
				msg1.setTradeAmount(Params.getPutBucket2Count());
				msg1.setPutRatio1(Params.getPutBucket2cpRatio());
				msg1.setPutRatio1LowerBound(Params.getPutBucket2cpRatioLB());
				msg1.setPutRatio1UpperBound(Params.getPutBucket2cpRatioUB());
				puts2Msg = msg1;
				getManager().sendRequestToBackend("LARKIN", getPortletId(), msg1);
				remainingRequest++;
				backMonthPutsList2.clear();

			}
			//now do the underlyings
			{
				GetUnderlyingDataBySymbolDateRequest msg1 = nw(GetUnderlyingDataBySymbolDateRequest.class);
				msg1.setUnderlyingSymbol(Params.getSymbolName());
				msg1.setQuoteDate1((BasicDay) Params.getStartingQuoteDate());
				msg1.setQuoteDate2((BasicDay) Params.getEndingQuoteDate());
				;

				getManager().sendRequestToBackend("LARKIN", getPortletId(), msg1);
				remainingRequest++;
				underlyingsList.clear();

			}

		} else if (button== this.saveScenariosButton){
			//saveParams();
			
		} else if (button== this.loadScenariosButton){
			
		}
	}

	private boolean checkDates(Day startingQuoteDate, Day endingQuoteDate) {
		// TODO Auto-generated method stub
		boolean r = false;
		if( endingQuoteDate != null  && startingQuoteDate != null){
			if ( startingQuoteDate.getDurationInDaysTo(endingQuoteDate) >= 1 )
				r = true;
		}
		return r;
	}

	public static class Builder extends AbstractPortletBuilder<LarkinOptionDataPortlet> {

		private static final String ID = "Larkin Point Returns";

		public Builder() {
			super(LarkinOptionDataPortlet.class);
		}

		@Override
		public LarkinOptionDataPortlet buildPortlet(PortletConfig portletConfig) {
			LarkinOptionDataPortlet portlet = new LarkinOptionDataPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Larkin Point Returns Grid";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	private int remainingRequest = 0;

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		boolean hasRemainingRequest = remainingRequest > 0;
		// TODO Auto-generated method stub
		if (result.getAction() instanceof GetOptionDataResponse) {
			if (result.getRequestMessage().getAction() instanceof GetLarkinPutsRequest) {
				Duration d = new Duration("option processing ");
				GetOptionDataResponse response = (GetOptionDataResponse) result.getAction();
				if (result.getRequestMessage().getAction() == puts1Msg) {
					backMonthPutsList1.addAll(response.getOptionData());

				} else if (result.getRequestMessage().getAction() == puts2Msg) {
					backMonthPutsList2.addAll(response.getOptionData());

				}
				long responseTime = EH.currentTimeMillis();

				if (!result.getIsIntermediateResult())
					remainingRequest--;

			} else if (result.getRequestMessage().getAction() instanceof GetLarkinBackTestingDataRequest) {

				Duration d = new Duration("option processing ");
				GetOptionDataResponse response = (GetOptionDataResponse) result.getAction();
				GetLarkinBackTestingDataRequest request = (GetLarkinBackTestingDataRequest) result.getRequestMessage().getAction();
				nearMonthStraddlesList.addAll(response.getOptionData());

				d.stampStdout();
				long responseTime = EH.currentTimeMillis();

				if (!result.getIsIntermediateResult())
					remainingRequest--;
			}

		}

		else if (result.getAction() instanceof GetUnderlyingDataBySymbolDateResponse) {
			GetUnderlyingDataBySymbolDateResponse response = (GetUnderlyingDataBySymbolDateResponse) result.getAction();
			this.underlyingsList.addAll(response.getUnderlyingData());
			//	this.nearMonthStraddlesList = null;
			if (QuoteDateNameField1.getValue().getA() == null)
				return;

			float value = getUnderlyingValueAtTradeDate((BasicDay) Params.getStartingQuoteDate());
			LocaleFormatter localformatter = getManager().getState().getWebState().getFormatter();
			NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));
			UnderlyingValue.setValue(SH.toString(value));
			Params.setUnderlyingValue(value);
			investmentDataFormPortlet.onFieldValueChanged(UnderlyingValue);
			onFieldValueChanged(investmentDataFormPortlet, UnderlyingValue, null);

			//report.setTitle(this.WorkingSymbol + " " + QuoteDateNameField1.toString());
			if (!result.getIsIntermediateResult())
				remainingRequest--;
		}
		if (remainingRequest < 0) {
			int t = remainingRequest;
			remainingRequest = 0;
			throw new IllegalStateException("bad remainingRequests: " + t);
		}
		if (hasRemainingRequest && remainingRequest == 0) {
			BasicDay now = new BasicDay(getManager().getLocaleFormatter().getTimeZone(),getManager().getNow());
			LarkinScenarioContext LSC= new LarkinScenarioContext(nearMonthStraddlesList, underlyingsList, backMonthPutsList1, backMonthPutsList2, WorkingSymbol,now, Params);
			
			LarkinOptionReportPortlet report = new LarkinOptionReportPortlet(generateConfig(), LSC,this);
			this.reportTabs.addChild(this.WorkingSymbol + " " + QuoteDateNameField1.getJsValue(), report);
		
			getManager().onPortletAdded(report);
			puts1Msg = null;
			puts2Msg = null;
		}

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		// TODO Auto-generated method stub
		super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		// TODO Auto-generated method stub
		List<Row> selected = table.getSelectedRows();
		int leg1 = 0;
		int leg2 = 0;
		if ("straddle".equals(action)) {
			if (selected.size() == 1) {

				for (Row addRow : selected) {
					leg1 = (Integer) addRow.get("call.option_id");
					leg2 = (Integer) addRow.get("put.option_id");

				}
				tsPortlet.setOptionIDS(SymbolNameField.getValue(), leg1, leg2, QuoteDateNameField1.getValue().getA(), QuoteDateNameField1.getValue().getB());
				return;
			}

		} else if ("strangle".equals(action)) {
			if (selected.size() == 2) {
				Row row1 = selected.get(0);
				Row row2 = selected.get(1);
				float under = (Float) row1.get("under.close");
				float strike1 = (Float) row1.get("call.strike_price");
				float strike2 = (Float) row2.get("put.strike_price");
				if ((strike1 < under && strike2 < under) || (strike1 > under && strike2 > under))
					return;
				if (strike1 < strike2) {
					leg1 = (Integer) row2.get("call.option_id");
					leg2 = (Integer) row1.get("put.option_id");
				} else {
					leg1 = (Integer) row1.get("call.option_id");
					leg2 = (Integer) row2.get("put.option_id");
				}
				tsPortlet.setOptionIDS(SymbolNameField.getValue(), leg1, leg2, QuoteDateNameField1.getValue().getA(), QuoteDateNameField1.getValue().getB());
				return;
			}

		}
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
		if (this.underlyingsList.isEmpty() == false)
			temp = (float) this.underlyingsList.get(0).getClose();
		for (UnderlyingMessage message : this.underlyingsList)
			if (message.getQuoteDate().isOn(bd.getStartNanoDate()))
				temp = (float) message.getClose();
		return temp;

	}
	public float getStartingUnderlyingValue() {

		return getUnderlyingValueAtTradeDate((BasicDay) QuoteDateNameField1.getValue().getA());

	}
	public float getEndingUnderlyingValue() {

		return getUnderlyingValueAtTradeDate((BasicDay) QuoteDateNameField1.getValue().getB());

	}
	public int getNumberOfTradingDatesBetween(BasicDay start, BasicDay end) {
		int count = 0;
		for (UnderlyingMessage message : this.underlyingsList)
			if (message.getQuoteDate().isOnOrAfter(start.getStartNanoDate()) && message.getQuoteDate().isOnOrBefore(start.getStartNanoDate()))
				count++;
		return count;

	}
	public List<UnderlyingMessage> getUnderlyingsBetweenTradingDates(BasicDay start, BasicDay end) {
		List<UnderlyingMessage> list = new ArrayList<UnderlyingMessage>();
		for (UnderlyingMessage message : this.underlyingsList)
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

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub

	}
	public void setReportTabTitle( String title, LarkinOptionReportPortlet child) {
		Tab t = reportTabs.findTab(child);
		t.setTitle(title);
		
	}
	public void saveParams()	{
	
		ObjectToJsonConverter converter = new ObjectToJsonConverter();
	//	SsoUser user = getManager().getState().getWebState().getUser();
		
		Map<String, Portlet> children = (HashMap<String, Portlet>) this.reportTabs.getChildren();
		UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
		updateRequest.setGroupId(user.getGroupId());
		SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
		attr.setGroupId(updateRequest.getGroupId());
		SsoWebGroup group = ssoservice.getSsoTree().getGroup(updateRequest.getGroupId());
		if (group == null) {
			getManager().showAlert("Group not found: " + updateRequest.getGroupId());
			return;
		}
		
		LarkinScenarioContext scenario= null;
		Map<String, Object> m = new HashMap<String, Object>();
		for( Portlet child :  children.values()){
			scenario = ((LarkinOptionReportPortlet) child).getLSC();
			
		
		//	Map<String, Object> m = new HashMap<String, Object>();
			m.put("Title", scenario.getParams().getTitle());
			m.put("SymbolName", scenario.getParams().getSymbolName());
			m.put("StartingQuoteDate", scenario.getParams().getStartingQuoteDate().toStringNoTimeZone());
			m.put("EndingQuoteDate", scenario.getParams().getEndingQuoteDate().toStringNoTimeZone());
			m.put("StraddleChoice", scenario.getParams().getStraddleChoice());
			
			m.put("UnderlyingValue", scenario.getParams().getUnderlyingValue());
			m.put("InvestmentValue", scenario.getParams().getInvestmentValue());
			m.put("InvestmentPercentage", scenario.getParams().getInvestmentPercentage());
			m.put("InvestedAmount", scenario.getParams().getInvestedAmount());
			m.put("ManagementFee", scenario.getParams().getManagementFee());
		
			m.put("straddleDaysUB", scenario.getParams().getStraddleDaysUB());
			m.put("straddleDaysLB", scenario.getParams().getStraddleDaysLB());
			m.put("straddleRatioUB", scenario.getParams().getStraddleRatioUB());
			m.put("straddleRatioLB", scenario.getParams().getStraddleRatioLB());
			m.put("straddleRatio", scenario.getParams().getStraddleRatio());
			m.put("straddleCount", scenario.getParams().getStraddleCount());
	
			m.put("putBucket1DaysUB", scenario.getParams().getPutBucket1DaysUB());
			m.put("putBucket1DaysLB", scenario.getParams().getPutBucket1DaysLB());
			m.put("putBucket1cpRatio", scenario.getParams().getPutBucket1cpRatio());
			m.put("putBucket1cpRatioUB", scenario.getParams().getPutBucket1cpRatioUB());
			m.put("putBucket1cpRatioLB", scenario.getParams().getPutBucket1cpRatioLB());
			m.put("putBucket1Count", scenario.getParams().getPutBucket1Count());
		
			m.put("putBucket2DaysUB", scenario.getParams().getPutBucket2DaysUB());
			m.put("putBucket2DaysLB", scenario.getParams().getPutBucket2DaysLB());
			m.put("putBucket2cpRatio", scenario.getParams().getPutBucket2cpRatio());
			m.put("putBucket2cpRatioUB", scenario.getParams().getPutBucket2cpRatioUB());
			m.put("putBucket2cpRatioLB", scenario.getParams().getPutBucket2cpRatioLB());
			m.put("putBucket2Count", scenario.getParams().getPutBucket2Count());
			m.put("user", user.getId());
			m.put("Display", true);
			m.put("Importance", 1);
			m.put("Version", 1.1);
			m.put("CreateTime", scenario.getCreationTime());
			
			
			
			attr.setKey("larkin_scenarios");
			attr.setType(SsoGroupAttribute.TYPE_JSON);
			attr.setValue(converter.objectToString(m));
			updateRequest.setGroupAttributes(CH.l(attr));
			ssoservice.sendRequestToBackend(getPortletId(), updateRequest);
			
		}
		attr.setKey("larkin_scenarios");
		attr.setType(SsoGroupAttribute.TYPE_JSON);
		attr.setValue(converter.objectToString(m));
		updateRequest.setGroupAttributes(CH.l(attr));
		ssoservice.sendRequestToBackend(getPortletId(), updateRequest);
	}
}
