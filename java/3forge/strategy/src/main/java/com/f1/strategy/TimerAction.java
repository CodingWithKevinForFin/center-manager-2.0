package com.f1.strategy;

import com.f1.base.Message;

public interface TimerAction extends Message {

	public long getScheduledTime();
	public void setScheduledTime(long scheduledTime);
}
