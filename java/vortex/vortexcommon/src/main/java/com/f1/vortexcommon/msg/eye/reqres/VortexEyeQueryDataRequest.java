package com.f1.vortexcommon.msg.eye.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.QDQ")
public interface VortexEyeQueryDataRequest extends VortexEyeRequest {

	//should be an agent revision
	@PID(1)
	public byte getType();
	public void setType(byte type);

	@PID(2)
	public long[] getIds();
	public void setIds(long[] id);

	@PID(3)
	public void setSearchDeleted(boolean b);
	public boolean getSearchDeleted();

	@PID(4)
	public String getSearchExpression();
	public void setSearchExpression(String searchExpression);

	////if supplied (not zero), target a particular revision
	//@PID(2)
	//public int getRevision();
	//public void setRevision(int revision);

	//any pids supplied should be included in the response
	//@PID(3)
	//public Set<Byte> getRequiredPids();
	//public void setRequiredPids(Set<Byte> fields);

	//if supplied (not zero) then this indicates a history start time
	//@PID(4)
	//public long getNowStartTime();
	//public void setNowStartTime(long startTime);

	//if supplied (not zero) then this indicates a history end time
	//@PID(5)
	//public long getNowEndTime();
	//public void setNowEndTime(long startTime);

	//if supplied (not zero) then this indicates a history search, and max number
	//@PID(6)
	//public long getHistoryLimit();
	//public void setHistoryLimit(long startTime);
}
