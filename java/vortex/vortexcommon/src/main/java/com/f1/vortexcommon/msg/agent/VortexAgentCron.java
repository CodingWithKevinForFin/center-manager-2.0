package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.C")
public interface VortexAgentCron extends PartialMessage, VortexAgentEntity {

	byte PID_SECOND = 1;
	byte PID_MINUTE = 2;
	byte PID_HOUR = 3;
	byte PID_DAY_OF_MONTH = 4;
	byte PID_MONTH = 5;
	byte PID_DAY_OF_WEEK = 6;
	byte PID_COMMAND = 7;
	byte PID_TIME_ZONE = 8;
	byte PID_USER = 9;

	@PID(PID_SECOND)
	public String getSecond();
	public void setSecond(String second);

	@PID(PID_MINUTE)
	public String getMinute();
	public void setMinute(String minute);

	@PID(PID_HOUR)
	public String getHour();
	public void setHour(String hour);

	@PID(PID_DAY_OF_MONTH)
	public String getDayOfMonth();
	public void setDayOfMonth(String dayOfMonth);

	@PID(PID_MONTH)
	public String getMonth();
	public void setMonth(String month);

	@PID(PID_DAY_OF_WEEK)
	public String getDayOfWeek();
	public void setDayOfWeek(String dayOfWeek);

	@PID(PID_COMMAND)
	public String getCommand();
	public void setCommand(String command);

	@PID(PID_TIME_ZONE)
	public String getTimeZone();
	public void setTimeZone(String timeZone);

	@PID(PID_USER)
	public String getUser();
	public void setUser(String user);

}
