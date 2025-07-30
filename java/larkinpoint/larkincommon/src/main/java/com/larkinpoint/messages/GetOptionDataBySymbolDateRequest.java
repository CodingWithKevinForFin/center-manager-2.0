package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.utils.BasicDay;

@VID("LP.QD.OPT")
public interface GetOptionDataBySymbolDateRequest extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 42;
	byte PID_QUOTE_DATE1 = 43;
	byte PID_QUOTE_DATE2 = 44;
	byte PID_QUERY_DATABASE = 45;
	byte PID_MAX_DAYS_TO_EXPIRY = 46;
	byte PID_MIN_DAYS_TO_EXPIRY = 47;
	byte PID_STRIKE_COUNT = 48;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_QUOTE_DATE1)
	public BasicDay getQuoteDate1();
	public void setQuoteDate1(BasicDay symbol);

	@PID(PID_QUOTE_DATE2)
	public BasicDay getQuoteDate2();
	public void setQuoteDate2(BasicDay symbol);

	@PID(PID_QUERY_DATABASE)
	public Boolean getQueryDatabase();
	public void setQueryDatabase(Boolean queryflag);

	@PID(PID_MAX_DAYS_TO_EXPIRY)
	public int getMaxDaysToExpiry();
	public void setMaxDaysToExpiry(int days);

	@PID(PID_MIN_DAYS_TO_EXPIRY)
	public int getMinDaysToExpiry();
	public void setMinDaysToExpiry(int days);

	@PID(PID_STRIKE_COUNT)
	public int getStrikeCount();
	public void setStrikeCount(int count);

}
