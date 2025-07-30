package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.L")
public interface F1AppLogger extends F1AppEntity {

	byte PID_LOGGER_ID = 12;
	byte PID_EXCEPTIONS_COUNT = 13;
	byte PID_TOTAL_EVENTS_COUNT = 14;
	byte PID_WARNING_OR_HIGHER_COUNT = 15;
	byte PID_ERROR_OR_HIGHER_COUNT = 16;
	byte PID_BYTES_LOGGED_COUNT = 17;
	byte PID_DROPPED_COUNT = 18;
	byte PID_MIN_LOG_LEVEL = 19;

	@PID(PID_LOGGER_ID)
	public String getLoggerId();
	public void setLoggerId(String id);

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

	@PID(PID_DROPPED_COUNT)
	public long getDroppedCount();
	public void setDroppedCount(long droppedCount);

	@PID(PID_MIN_LOG_LEVEL)
	public int getMinLogLevel();
	public void setMinLogLevel(int minLogLevel);
}
