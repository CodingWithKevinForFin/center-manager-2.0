package com.larkinpoint.salestool;

import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlCustomPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.LocaleFormatter;

public class LarkinScenarioResultsPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener, WebContextMenuFactory {

	private LocaleFormatter localformatter;
	private HtmlPortlet Title;
	private HtmlPortlet startingDate;
	private HtmlPortlet endingDate;
	private HtmlPortlet noOfUnderlyingDays;
	private HtmlPortlet noOfStraddleDays;
	private HtmlPortlet noOfPuts1Days;
	private HtmlPortlet noOfPuts2Days;
	private HtmlPortlet indexPL;
	private HtmlPortlet totalPL;
	private HtmlPortlet indexReturn;
	private HtmlPortlet totalReturn;
	private HtmlPortlet Data1;
	private HtmlPortlet DataCol1;
	private HtmlPortlet DataCol2;
	private HtmlPortlet DataCol3;
	private HtmlPortlet DataCol4;
	private HtmlPortlet DataCol5;
	private LarkinScenarioContext LSC;
	private HtmlPortlet Data;
	
	public LarkinScenarioResultsPortlet(PortletConfig config, LarkinScenarioContext LSC) {
		super(config);
		this.LSC = LSC;
		localformatter = getManager().getState().getWebState().getFormatter();
		
		NumberWebCellFormatter datefmt = new NumberWebCellFormatter(localformatter.getDateFormatter(LocaleFormatter.MMDDYYYY));
		NumberWebCellFormatter numfmt = new NumberWebCellFormatter(localformatter.getNumberFormatter(3));
		NumberWebCellFormatter numfmt1 = new NumberWebCellFormatter(localformatter.getNumberFormatter(0));
		NumberWebCellFormatter perfmt1 = new NumberWebCellFormatter(localformatter.getPercentFormatter(3));
		
		{
			Title = new HtmlPortlet(generateConfig(), "Index: " + LSC.getSymbol(), "larkin_form");
			startingDate = new HtmlPortlet(generateConfig(), "From: " + datefmt.formatCellToText(LSC.getParams().getStartingQuoteDate()) +" to "+
					datefmt.formatCellToText(LSC.getParams().getEndingQuoteDate()), "larkin_scenario");
			//endingDate = new HtmlPortlet(generateConfig(),   "Ending Date: " + datefmt.formatCellToText(LSC.getParams().getEndingQuoteDate()), "larkin_form");
			
			noOfUnderlyingDays = new HtmlPortlet(generateConfig(), "Underlying records: "+ numfmt1.formatCellToText(LSC.getUnderlyingsList().size())+ " covering "+ LSC.getUnderlyingMasher().entrySet().size() + " days", "larkin_scenario");
			noOfStraddleDays = new HtmlPortlet(generateConfig(),   "Straddle   records: "+ numfmt1.formatCellToText(LSC.getNearMonthStraddlesList().size()) + " covering "+ LSC.getStraddleMasher().entrySet().size() + " days", "larkin_scenario");
			
			noOfPuts1Days = new HtmlPortlet(generateConfig(),    "First Put  records: "+ numfmt1.formatCellToText(LSC.getBackMonthPutsList1().size())+ " covering "+ LSC.getPuts1Masher().entrySet().size() + " days", "larkin_scenario");
			noOfPuts2Days = new HtmlPortlet(generateConfig(),   "Second Put records: "+ numfmt1.formatCellToText(LSC.getBackMonthPutsList2().size())+ " covering "+ LSC.getPuts2Masher().entrySet().size() + " days", "larkin_scenario");
			Data = new HtmlPortlet(generateConfig(), "", "larkin_form");
			indexPL= new HtmlPortlet(generateConfig()," Index :  PL is " + numfmt.formatCellToText(LSC.getIndexPL()) + " and Return is "+perfmt1.formatCellToText(LSC.getIndexReturn()),"larkin_scenario");
			totalPL= new HtmlPortlet(generateConfig()," Totals:  PL is " + numfmt.formatCellToText(LSC.getTotalPL()) + " and Return is "+perfmt1.formatCellToText(LSC.getTotalReturn()),"larkin_scenario");
		//	indexReturn= new HtmlPortlet(generateConfig()," Index Return is: " + perfmt1.formatCellToText(LSC.getIndexReturn()),"larkin_scenario");
			//totalReturn= new HtmlPortlet(generateConfig()," Total Return is: " + perfmt1.formatCellToText(LSC.getTotalReturn()),"larkin_scenario");
			indexReturn = new HtmlPortlet(generateConfig(),"\n <hr noshade size=7>","larkin_scenario");
		}
		
		
		int totalCols = 1;
		int totalRows = 35;
		int colCount = 0;
		int rowCount = 0;
		
		{

			{
				addChild(Title, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,26);
			//	setColSize(colCount++, 310);
			}
			{
				addChild(startingDate, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
				//setColSize(colCount++, 220);
			}
			{
				addChild(noOfUnderlyingDays, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
			{
				addChild(noOfStraddleDays, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
			{
				addChild(noOfPuts1Days, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
			{
				addChild(noOfPuts2Days, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
			{
				addChild(indexReturn, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
		
			{
				addChild(totalPL, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
		
			{
				addChild(indexPL, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
		/*
			{
				addChild(totalReturn, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
		
			{
				addChild(indexReturn, colCount, rowCount, 1, 1);
				setRowSize(rowCount++,24);
			//	setColSize(colCount++, 220);
			}
		*/
			addChild(Data, colCount, rowCount, totalCols - colCount,1);
			//addChild(DataCol1, 0, 0, 1, 1);
			//addChild(DataCol2, 1, 0, 1, 1);
			//addChild(DataCol3, 2, 0, 1, 1);
		}
		// TODO Auto-generated constructor stub
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		// TODO Auto-generated method stub
		
	}
	
	
	

}
