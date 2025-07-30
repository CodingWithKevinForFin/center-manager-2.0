package com.larkinpoint.messages;

import com.f1.base.Day;
import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.UN.MSG")
public interface UnderlyingMessage extends PartialMessage, Lockable {

	byte PID_SYMBOL = 10;
	byte PID_QUOTE_DATE = 11;
	byte PID_CURRENCY = 12;
	byte PID_CLOSE = 13;
	byte PID_ASK = 14;
	byte PID_BID = 15;
	byte PID_CUM_TOTAL_RETURN = 16;
	byte PID_OPEN = 17;
	byte PID_SECURITY_ID = 18;
	byte PID_TOTAL_RETURN = 19;
	byte PID_VOLUME = 20;

	byte PID_DAILY_P_AND_L = 36;
	byte PID_CASH_FLOW = 37;
	byte PID_STARTING_VALUE = 38;
	byte PID_ENDING_VALUE = 39;
	byte PID_DAILY_RETURN = 40;

	@PID(PID_SYMBOL)
	public String getSymbol();
	public void setSymbol(String symbol);

	@PID(PID_QUOTE_DATE)
	public Day getQuoteDate();
	public void setQuoteDate(Day date);

	@PID(PID_CURRENCY)
	public int getCurrency();
	public void setCurrency(int currency);

	@PID(PID_CLOSE)
	public double getClose();
	public void setClose(double value);
	@PID(PID_ASK)
	public double getAsk();
	public void setAsk(double value);
	@PID(PID_BID)
	public double getBid();
	public void setBid(double value);
	@PID(PID_CUM_TOTAL_RETURN)
	public double getCumTotalReturn();
	public void setCumTotalReturn(double value);
	@PID(PID_SECURITY_ID)
	public long getSecurityId();
	public void setSecurityId(long id);
	@PID(PID_TOTAL_RETURN)
	public double getTotalReturn();
	public void setTotalReturn(double value);
	@PID(PID_VOLUME)
	public long getVolume();
	public void setVolume(long value);
	@PID(PID_OPEN)
	public double getOpen();
	public void setOpen(double value);
	@PID(PID_DAILY_P_AND_L)
	public double getDailyPAndL();
	public void setDailyPAndL(double value);

	@PID(PID_CASH_FLOW)
	public double getCashFlow();
	public void setCashFlow(double value);

	@PID(PID_STARTING_VALUE)
	public double getStartingValue();
	public void setStartingValue(double value);

	@PID(PID_ENDING_VALUE)
	public double getEndingValue();
	public void setEndingValue(double value);
	@PID(PID_DAILY_RETURN)
	public double getDailyReturn();
	public void setDailyReturn(double value);

}