package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.TD.OPT")
public interface GetAllTradeDatesRequest extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 42;

	//byte PID_QUOTE_DATE1 = 43;
	//byte PID_QUOTE_DATE2 = 44;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	//	@PID(PID_QUOTE_DATE1)
	//	public String getQuoteDate1();
	//	public void setQuoteDate1(String symbol);

	//	@PID(PID_QUOTE_DATE2)
	//	public String getQuoteDate2();
	// void setQuoteDate2(String symbol);

}
