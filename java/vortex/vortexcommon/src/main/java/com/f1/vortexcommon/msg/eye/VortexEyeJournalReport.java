package com.f1.vortexcommon.msg.eye;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VO.VEJR")
public interface VortexEyeJournalReport extends VortexEyeEntity, Lockable {

	@PID(1)
	public int getYear();
	public void setYear(int year);

	@PID(2)
	public int getMonth();
	public void setMonth(int month);

	@PID(3)
	public int getMinutesUp();
	public void setMinutesUp(int minutesUp);

	@PID(4)
	public int getMaxAgents();
	public void setMaxAgents(int maxAgents);

	@PID(5)
	public int getAvgAgents();
	public void setAvgAgents(int avgAgents);

	@PID(6)
	public int getMaxAccounts();
	public void setMaxAccounts(int maxAccounts);

	@PID(7)
	public String getText();
	public void setText(String maxAccounts);

	@PID(8)
	public String getClient();
	public void setClient(String maxAccounts);

	@PID(9)
	public String getHostname();
	public void setHostname(String hostname);
}