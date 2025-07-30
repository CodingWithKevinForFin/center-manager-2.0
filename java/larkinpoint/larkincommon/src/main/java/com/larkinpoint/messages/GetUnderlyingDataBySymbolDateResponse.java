package com.larkinpoint.messages;

import java.util.List;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.RD.UND")
public interface GetUnderlyingDataBySymbolDateResponse extends PartialMessage, Lockable {

	byte PID_UNDERLYING_DATA = 31;

	@PID(PID_UNDERLYING_DATA)
	public List<UnderlyingMessage> getUnderlyingData();
	public void setUnderlyingData(List<UnderlyingMessage> data);

}