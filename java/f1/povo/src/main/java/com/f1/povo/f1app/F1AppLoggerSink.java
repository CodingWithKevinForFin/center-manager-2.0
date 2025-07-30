package com.f1.povo.f1app;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.LS")
public interface F1AppLoggerSink extends F1AppEntity {

	byte PID_SINK_ID=11;
	byte PID_CONFIGURATION=12;
	byte PID_EXCEPTIONS_COUNT=13;
	byte PID_TOTAL_EVENTS_COUNT=14;
	byte PID_WARNING_OR_HIGHER_COUNT=15;
	byte PID_ERROR_OR_HIGHER_COUNT=16;
	byte PID_BYTES_LOGGED_COUNT=17;
	
	
	@PID(PID_SINK_ID)
	public String getSinkId();
	public void setSinkId(String sinkId);

	@PID(PID_CONFIGURATION)
	public Map<String, String> getConfiguration();
	public void setConfiguration(Map<String, String> configuration);

	@PID(PID_EXCEPTIONS_COUNT)
	public long getExceptionsCount();
	public void setExceptionsCount(long exceptionsCount);

	@PID(PID_TOTAL_EVENTS_COUNT)
	public long getTotalEventsCount();
	public void setTotalEventsCount(long count);

	@PID(PID_WARNING_OR_HIGHER_COUNT)
	public long getWarningOrHigherCount();
	public void setWarningOrHigherCount(long count);

	@PID(PID_ERROR_OR_HIGHER_COUNT)
	public long getErrorOrHigherCount();
	public void setErrorOrHigherCount(long count);

	@PID(PID_BYTES_LOGGED_COUNT)
	public long getBytesLoggedCount();
	public void setBytesLoggedCount(long bytesCount);

}
