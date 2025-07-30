package com.larkinpoint.messages;

import java.util.List;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.RD.OPT")
public interface GetOptionDataResponse extends PartialMessage, Lockable {

	byte PID_OPTION_DATA = 40;

	@PID(PID_OPTION_DATA)
	public List<SpreadMessage> getOptionData();
	public void setOptionData(List<SpreadMessage> data);

}
