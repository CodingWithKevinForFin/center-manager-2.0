package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.UD.REQ")
public interface GetUnderlyingSymbolsRequest extends PartialMessage, Lockable {

	byte PID_UNDERLYING_SYMBOL = 42;
	byte PID_OPTION_IDS = 43;

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_OPTION_IDS)
	public long[] getOptionIds();
	public void setOptionIds(long[] ids);

}