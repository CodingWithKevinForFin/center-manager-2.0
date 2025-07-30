package com.f1.speedlogger;

public interface SpeedLoggerEventListener {
	public void onlogEvent(SpeedLoggerSink sink, SpeedLogger logger, char[] data, int dataStart, int dataLength, int level, Object msg, long timeMs, StackTraceElement ste);
	public void onLoggerMinLevelChanged(SpeedLogger logger, int oldLevel, int newLevel);
}
