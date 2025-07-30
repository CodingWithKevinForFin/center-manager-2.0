package com.f1.povo.f1app;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.FA.FAE")
public interface F1AppEvent extends PartialMessage {

	@PID(31)
	public long getF1AppInstanceId();
	public void setF1AppInstanceId(long f1AppInstanceId);

	@PID(32)
	public long getTimeMs();
	public void setTimeMs(long ms);

}
