package com.larkinpoint.messages;

import com.f1.base.Day;
import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.QD.OID")
public interface GetOptionDataByOptionIDRequest extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 42;
	byte PID_LEG1_OPTION_ID = 43;
	byte PID_LEG2_OPTION_ID = 44;
	byte PID_QUOTE_DATE1 = 45;
	byte PID_QUOTE_DATE2 = 46;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_LEG1_OPTION_ID)
	public long getLeg1OptionId();
	public void setLeg1OptionId(long id);

	@PID(PID_LEG2_OPTION_ID)
	public long getLeg2OptionId();
	public void setLeg2OptionId(long id);

	@PID(PID_QUOTE_DATE1)
	public Day getQuoteDate1();
	public void setQuoteDate1(Day day);

	@PID(PID_QUOTE_DATE2)
	public Day getQuoteDate2();
	public void setQuoteDate2(Day day);

}