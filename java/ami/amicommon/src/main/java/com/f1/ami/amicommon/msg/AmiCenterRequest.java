package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.Prioritized;
import com.f1.base.VID;

@VID("F1.VE.Q")
public interface AmiCenterRequest extends PartialMessage, Prioritized {

	@PID(51)
	public String getInvokedBy();
	public void setInvokedBy(String invokedBy);

	@PID(52)
	public String getComment();
	public void setComment(String comment);

	@PID(53)
	public int getPriority();
	public void setPriority(int priority);

	@PID(54)
	public long getRequestTime();
	public void setRequestTime(long timeMillis);
}
