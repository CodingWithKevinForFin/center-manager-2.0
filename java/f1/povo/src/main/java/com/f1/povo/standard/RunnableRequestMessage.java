package com.f1.povo.standard;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.Prioritized;
import com.f1.base.VID;

@VID("F1.ST.RQ")
public interface RunnableRequestMessage extends Message, Prioritized {

	@PID(1)
	public Object getPartitionId();
	public void setPartitionId(Object partitionId);

	@PID(2)
	public Runnable getRunnable();
	public void setRunnable(Runnable runnable);

	@PID(3)
	public int getTimeoutMs();
	public void setTimeoutMs(int timeoutMs);

	@PID(4)
	public int getPriority();
	public void setPriority(int priority);
}
