package com.f1.speedlogger;

public interface SpeedLoggerManagerListener {

	public void onNewLogger(SpeedLogger logger);
	public void onNewSink(SpeedLoggerSink sink);
}
