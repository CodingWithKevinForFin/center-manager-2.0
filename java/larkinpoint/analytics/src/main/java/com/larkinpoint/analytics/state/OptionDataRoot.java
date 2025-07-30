package com.larkinpoint.analytics.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Day;
import com.f1.base.IdeableGenerator;
import com.f1.utils.BasicDay;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;

public class OptionDataRoot {

	public Map<String, OptionUnderlying> symbol2Underlyings = new HashMap<String, OptionUnderlying>();
	public Map<String, SecurityInfo> securityMap = new HashMap<String, SecurityInfo>();
	private boolean securityMapLoaded;
	
	public void refreshAll(){
		for (OptionUnderlying under : getAllUnderlyings()){
			under.setNeedsRefresh(true);
		}
	}

	public OptionDataRoot() {
		super();
		setSecurityMapLoaded(false);
	}

	final private Map<Day, Day> dayPool = new HashMap<Day, Day>();

	public boolean isSecurityMapLoaded() {
		return securityMapLoaded;
	}

	public void setSecurityMapLoaded(boolean securityMapLoaded) {
		this.securityMapLoaded = securityMapLoaded;
	}

	public void addRecord(OptionMessage ov) {
		OptionUnderlying existing = symbol2Underlyings.get(ov.getUnderlying());
		if (existing == null)
			symbol2Underlyings.put(ov.getUnderlying(), existing = new OptionUnderlying(ov.getUnderlying()));
		existing.addRecord(ov);
	}

	public void addTradeDates(String name, OptionUnderlying tradeDates) {
		this.symbol2Underlyings.put(name, tradeDates);
	}

	public OptionUnderlying getUnderlying(String name) {
		OptionUnderlying existing = symbol2Underlyings.get(name);
		if (existing == null)
			symbol2Underlyings.put(name, existing = new OptionUnderlying(name));

		return symbol2Underlyings.get(name);
	}

	public Collection<OptionUnderlying> getAllUnderlyings() {
		return symbol2Underlyings.values();
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {
		for (OptionUnderlying under : getAllUnderlyings())
			under.buildOptionList(ideableGenerator, sink);
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, String symbol, BasicDay qdate1, BasicDay qdate2, int top) {
		for (OptionUnderlying under : getAllUnderlyings())
			under.buildOptionList(ideableGenerator, sink, symbol, qdate1, qdate2, top);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, String symbol, long leg1, long leg2, BasicDay day1, BasicDay day2) {
		for (OptionUnderlying under : getAllUnderlyings())
			if (under.getSymbol().equals(symbol))
				under.buildOptionList(ideableGenerator, sink, symbol, leg1, leg2, day1, day2);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> list, String symbol, BasicDay qdate1, BasicDay qdate2, int top, int daysMax, int daysMin) {
		for (OptionUnderlying under : getAllUnderlyings())
			under.buildOptionList(ideableGenerator, list, symbol, qdate1, qdate2, top, daysMax, daysMin);
	}
	public void buildUnderlyingList(IdeableGenerator ideableGenerator, List<UnderlyingMessage> list, String symbol, BasicDay qdate1, BasicDay qdate2) {
		OptionUnderlying existing = symbol2Underlyings.get(symbol);
		if( existing == null ){
			System.out.println( "Symbol does not exist: " + symbol);	
		}
		else {
			existing.buildUnderlyingList(ideableGenerator, list, symbol, qdate1, qdate2);
		}
	}
	public boolean underlyingExist(String symbol) {
		OptionUnderlying existing = symbol2Underlyings.get(symbol);
		if (existing == null || existing.haveUnderRecords() == false)
			return false;

		return true;
	}

	public Day pool(Day day) {
		Day existing = dayPool.get(day);
		if (existing != null)
			return existing;
		dayPool.put(day, day);
		return day;
	}

	public class SecurityInfo {
		private int expiry_dayofweek;
		private int expiry_weekofmonth;

		public SecurityInfo(long securityID, String ticker, int valor, String issuer, String isin, String cusip, int dayofweek, int weekofmonth) {
			super();
			this.securityID = securityID;

			this.ticker = ticker;
			this.valor = valor;
			this.issuer = issuer;
			this.isin = isin;
			this.cusip = cusip;
			this.expiry_dayofweek = dayofweek;
			this.expiry_weekofmonth = weekofmonth;
		}

		public int getExpiry_dayofweek() {
			return expiry_dayofweek;
		}
		public void setExpiry_dayofweek(int expiry_dayofweek) {
			this.expiry_dayofweek = expiry_dayofweek;
		}
		public int getExpiry_weekofmonth() {
			return expiry_weekofmonth;
		}
		public void setExpiry_weekofmonth(int expiry_weekofmonth) {
			this.expiry_weekofmonth = expiry_weekofmonth;
		}

		private long securityID;

		public long getSecurityID() {
			return securityID;
		}
		public void setSecurityID(long securityID) {
			this.securityID = securityID;
		}

		public String getTicker() {
			return ticker;
		}
		public void setTicker(String ticker) {
			this.ticker = ticker;
		}
		public int getValor() {
			return valor;
		}
		public void setValor(int valor) {
			this.valor = valor;
		}
		public String getIssuer() {
			return issuer;
		}
		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}
		public String getIsin() {
			return isin;
		}
		public void setIsin(String isin) {
			this.isin = isin;
		}
		public String getCusip() {
			return cusip;
		}
		public void setCusip(String cusip) {
			this.cusip = cusip;
		}

		String ticker;
		int valor;
		String issuer;
		String isin;
		String cusip;

	}

	public void addSecurity(long id, String ticker, int valor, String issuer, String isin, String cusip, int dayofweek, int weekofmonth) {
		if (securityMap.containsKey(ticker))
			return;
		SecurityInfo sec = new SecurityInfo(id, ticker, valor, issuer, isin, cusip, dayofweek, weekofmonth);
		securityMap.put(ticker, sec);
	}
	public SecurityInfo getSecurityInfo(String symbol) {

		return securityMap.get(symbol);
	}
	public SecurityInfo getSecurityInfo(long id) {

		for (SecurityInfo sec : securityMap.values()) {
			if (sec.getSecurityID() == id)
				return sec;

		}
		return null;
	}

}
