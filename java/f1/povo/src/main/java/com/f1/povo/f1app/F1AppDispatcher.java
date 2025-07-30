package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.D")
public interface F1AppDispatcher extends F1AppContainerScope {

	byte PID_PROCESS_STATS = 12;
	byte PID_DISPATCH_STATS = 13;
	byte PID_THROWN_STATS = 14;
	byte PID_FORWARD_STATS = 15;

	@PID(PID_PROCESS_STATS)
	public long getProcessStats();
	public void setProcessStats(long processStats);

	@PID(PID_DISPATCH_STATS)
	public long getDispatchStats();
	public void setDispatchStats(long dispatchStats);

	@PID(PID_THROWN_STATS)
	public long getThrownStats();
	public void setThrownStats(long thrownStats);

	@PID(PID_FORWARD_STATS)
	public long getForwardStats();
	public void setForwardStats(long processStats);
}
