package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.Prioritized;
import com.f1.base.VID;

@VID("F1.VE.R")
public interface AmiCenterResponse extends AmiResponse, Prioritized {

	@PID(55)
	public String getTicket();
	public void setTicket(String message);

	@PID(53)
	public double getProgress();
	public void setProgress(double progress);

	@PID(57)
	public int getPriority();
	public void setPriority(int priority);

	@PID(58)
	public List<AmiCenterQueryDsTrackerEvent> getTrackedEvents();
	public void setTrackedEvents(List<AmiCenterQueryDsTrackerEvent> trackedEvents);
}
