package com.larkinpoint.messages;

import com.f1.base.Day;
import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.QD.UND")
public interface GetUnderlyingDataBySymbolDateRequest extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 32;
	byte PID_QUOTE_DATE1 = 33;
	byte PID_QUOTE_DATE2 = 34;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_QUOTE_DATE1)
	public Day getQuoteDate1();
	public void setQuoteDate1(Day symbol);
	@PID(PID_QUOTE_DATE2)
	public Day getQuoteDate2();
	public void setQuoteDate2(Day symbol);

}
