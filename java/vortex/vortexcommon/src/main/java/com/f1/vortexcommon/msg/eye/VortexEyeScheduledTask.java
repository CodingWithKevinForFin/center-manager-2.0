package com.f1.vortexcommon.msg.eye;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.vortexcommon.msg.agent.VortexMetadatable;

@VID("F1.VO.SCHTSK")
public interface VortexEyeScheduledTask extends VortexEyeEntity, VortexMetadatable {

	long NO_DEPLOYMENT = -1;

	byte STATUS_NEVER_RUN = 0;
	byte STATUS_MODIFIED = 1;
	byte STATUS_RUNNING = 2;
	byte STATUS_OKAY = 3;
	byte STATUS_DISABLED = 4;
	byte STATUS_FAILURE = 5;
	byte STATUS_INVALID = 6;
	byte STATUS_QUEUED_TO_RUN = 7;

	byte STATE_ACTIVE = 0;
	byte STATE_PAUSED = 1;

	byte TYPE_SCRIPT = 1;
	byte TYPE_BACKUP = 2;
	byte TYPE_DATABASE_INSPECT = 3;
	byte TYPE_START = 4;
	byte TYPE_STOP = 5;

	byte OPTION_DISABLED = 1;

	@PID(24)
	public int getOptions();
	public void setOptions(int options);

	@PID(25)
	public String getDescription();
	public void setDescription(String description);

	@PID(26)
	public byte getType();
	public void setType(byte type);

	//NO_DEPLOYEMNT(-1) --> not associated with deployment, only machine
	@PID(27)
	public long getDeploymentId();
	public void setDeploymentId(long deploymentId);

	@PID(28)
	public byte getStatus();
	public void setStatus(byte status);

	@PID(29)
	public String getMessage();
	public void setMessage(String message);

	@PID(30)
	public long getTargetId();
	public void setTargetId(long id);

	@PID(31)
	public String getCommand();
	public void setCommand(String command);

	@PID(32)
	public long getNextRuntime();
	public void setNextRuntime(long nextRuntime);

	@PID(13)
	public long getLastRuntime();
	public void setLastRuntime(long lastRuntime);

	@PID(33)
	public String getInvokedBy();
	public void setInvokedBy(String invokedBy);

	//combination of bits 0 - 23, 0x0 = all
	@PID(34)
	public int getHours();
	public void setHours(int hours);

	//combination of bits 0-59, 0x0 = all
	@PID(35)
	public long getMinutes();
	public void setMinutes(long minutes);

	//combination of bits 0-59,0x0 = all
	@PID(36)
	public long getSeconds();
	public void setSeconds(long seconds);

	//combination of bits 0-6 (0=sun, 1=mon, ... , 6=sat) ,0x0 = all
	@PID(37)
	public byte getWeekdays();
	public void setWeekdays(byte weekdays);

	//combination of bits 0-11 (0=jan, 1=feb, ... , 11=dec) ,0x0 = all
	@PID(38)
	public short getMonthInYears();
	public void setMonthInYears(short monthInYear);

	//combination of bits 0-5 (0=first week, 1=2nd week, ... ,5=6th week) ,0x0 = all
	@PID(39)
	public byte getWeekInMonths();
	public void setWeekInMonths(byte weekInMonth);

	//combination of bits 0-52 (0=first week, 1=2nd week, ... ,52=53rd week) ,0x0 = all
	@PID(40)
	public long getWeekInYears();
	public void setWeekInYears(long weekInYear);

	//combination of bits 0-30 (0=1st, ... ,30=31st day) ,0x0 = all
	@PID(41)
	public int getDayInMonths();
	public void setDayInMonths(int weekInYear);

	//combination of bits 0-5 (0=1st, ... ,5=4th occurance of day) ,0x0 = all
	@PID(42)
	public byte getDayOfWeekInMonths();
	public void setDayOfWeekInMonths(byte dayOfWeekInMonth);

	//1=1st day,366=366th day,0=no days. Up to 4 days can be supplied using The  bits 0-15, 16-31, 32-47, 48-63 to store the 1st, 2nd and 3rd & 4th entries respectively
	@PID(43)
	public long getDayOfYears();
	public void setDayOfYears(long dayOfYear);

	@PID(44)
	public long getRunCount();
	public void setRunCount(long runCount);

	@PID(45)
	public String getTimezone();
	public void setTimezone(String timezone);

	@PID(46)
	public String getComments();
	public void setComments(String comments);

	@PID(47)
	public byte getState();
	public void setState(byte status);

	public VortexEyeScheduledTask clone();

}
