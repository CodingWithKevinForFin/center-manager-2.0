package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.MES")
public interface VortexAgentMachineEventStats extends PartialMessage, VortexAgentEntity {

	byte LEVEL_ALL = 0;
	byte LEVEL_ERROR = 1;
	byte LEVEL_WARNING = 2;
	byte LEVEL_INFORMATION = 3;
	byte LEVEL_SUCCESS_AUDIT = 4;
	byte LEVEL_FAILURE_AUDIT = 5;
	byte LEVEL_UNKNOWN = 6;

	byte PID_LEVEL = 1;
	byte PID_HOST = 2;
	byte PID_NAME = 3;
	byte PID_SOURCE = 4;
	byte PID_USER_NAME = 5;
	byte PID_MESSAGE = 6;
	byte PID_TIME_GENERATED = 8;
	byte PID_END_TIME = 9;
	byte PID_NOT_ENDED = 10;
	byte PID_DURATION = 11;

	@PID(PID_LEVEL)
	public byte getLevel();
	public void setLevel(byte level);

	@PID(PID_HOST)
	public String getHost();
	public void setHost(String host);

	@PID(PID_NAME)
	public String getName();
	public void setName(String name);

	@PID(PID_SOURCE)
	public String getSource();
	public void setSource(String source);

	@PID(PID_USER_NAME)
	public String getUserName();
	public void setUserName(String userName);

	@PID(PID_MESSAGE)
	public String getMessage();
	public void setMessage(String message);

	@PID(PID_TIME_GENERATED)
	public long getTimeGenerated();
	public void setTimeGenerated(long timeGenerated);

	@PID(PID_END_TIME)
	public long getEndTime();
	public void setEndTime(long endTime);

	@PID(PID_NOT_ENDED)
	public String getNotEnded();
	public void setNotEnded(String notEnded);

	@PID(PID_DURATION)
	public String getDuration();
	public void setDuration(String duration);

}
