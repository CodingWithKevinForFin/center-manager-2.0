package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.ACT.MSG")
public interface ActionMessage extends PartialMessage, Lockable {

	byte PID_ACTION_MESSAGE_TYPE = 11;
	byte PID_UNDERLYING_SYMBOL = 12;
	byte PID_TRADE_DATE = 13;

	byte TYPE_CALC_RETURNS = 21;

	@PID(PID_ACTION_MESSAGE_TYPE)
	public byte getActionMessageType();
	public void setActionMessageType(byte type);

	@PID(PID_UNDERLYING_SYMBOL)
	public String getUnderlyingSymbol();
	public void setUnderlyingSymbol(String symbol);

	@PID(PID_TRADE_DATE)
	public String getTradeDate();
	public void setTradeDate(String td);

}