package com.f1.ami.center.timers;

import java.util.Map;

import com.f1.base.Caster;

public interface AmiTimerBinding {

	public AmiTimer getTimer();
	public int getPriority();
	public String getSchedule();
	public String getTimerName();
	public String getTimerType();
	public byte getDefType();
	public long getNextRunTime();
	public long getLastRunTime();
	public Map<String, Object> getOptions();
	public <T> T getOption(Class<T> castType, String string);
	public <T> T getOption(Class<T> castType, String string, T defaultValue);
	public <T> T getOption(Caster<T> caster, String string, T defaultValue);
	public void onTimerCompleted();
	public boolean getIsRunning();
	public void onTimerCompletedWithError(Exception exception);
	boolean getIsEnabled();
	int getTimeoutMillis();
	int getLimit();
}
