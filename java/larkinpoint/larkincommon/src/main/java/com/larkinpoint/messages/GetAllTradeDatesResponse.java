package com.larkinpoint.messages;

import java.util.List;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.utils.BasicDay;

@VID("LP.TD.RES")
public interface GetAllTradeDatesResponse extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 42;
	byte PID_DATES = 43;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_DATES)
	public List<BasicDay> getDates();
	public void setDates(List<BasicDay> symbol);

}
