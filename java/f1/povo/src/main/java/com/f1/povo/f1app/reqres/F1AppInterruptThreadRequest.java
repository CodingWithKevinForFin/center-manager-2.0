package com.f1.povo.f1app.reqres;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.ITQ")
public interface F1AppInterruptThreadRequest extends F1AppRequest {

	byte PID_THREAD_MONITOR_ID = 10;
	@PID(PID_THREAD_MONITOR_ID)
	public long getThreadMonitorId();
	public void setThreadMonitorId(long id);

	byte PROCESSED_EVENTS_COUNT = 11;
	@PID(PROCESSED_EVENTS_COUNT)
	public long getProcessedEventsCount();
	public void setProcessedEventsCount(long id);

	byte PID_PROCESSOR_MONITOR_ID = 12;
	@PID(PID_PROCESSOR_MONITOR_ID)
	public long getProcessorMonitorId();
	public void setProcessorMonitorId(long id);

}
