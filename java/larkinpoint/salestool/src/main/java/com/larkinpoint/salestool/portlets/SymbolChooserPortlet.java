package com.larkinpoint.salestool.portlets;

import com.f1.suite.web.portal.impl.form.FormPortletSelectField;

public class SymbolChooserPortlet extends FormPortletSelectField<String> {

	public SymbolChooserPortlet(String title) {
		super(String.class, title);
		// TODO Auto-generated constructor stub
		this.addOption("SPX", "SPX");
		this.addOption("NDX", "NDX");
		this.addOption("DJX", "DJX");
		this.addOption("RUT", "Russell 2000");
		this.addOption("EEM", "IShares TR");
		this.addOption("OMXH25", "OMXH Helsinki 25");
		this.addOption("SX5P", "DJ STOXX 50");
		this.addOption("SXXP", "DJ STOXX 600");
		this.addOption("SXXE", "SJ Euro STOXX50");
		this.addOption("DAX", "DAX Ind");
		this.addOption("CAC", "CAC 40");
		this.addOption("SSMI", "SMI");
		this.addOption("UKX", "FTSE 100");
		this.addOption("AEX", "AEX Index");
		this.addOption("BFX", "EuroNext BEL-20");
		this.addOption("DJGTE", "DJ FL TIT 50");
		this.addOption("IBEX", "IBEX-35");
		this.addOption("FIB", "MIB Index");
		this.addOption("N225", "Nikkei 225");
		this.addOption("HSI", "Hang Seng");
		this.addOption("TPX", "Topix SEC 1");
		this.addOption("KS11", "Kospi 200");
		this.addOption("XJO", "ASX 200");
		this.addOption("HSCEI", "China Ent Indx");
		this.addOption("TXO", "TAIEX");
		this.addOption("TX60", "TSX 60 Indx");
	}

}
