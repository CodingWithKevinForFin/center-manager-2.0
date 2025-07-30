package com.f1.pofo.fix;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * a chile request message ,such as an exection report,cancel, replace, etc.
 */
@VID("F1.FX.CM")
public interface ChildMessage extends Message {
	/**
	 * @return the child order id of this request
	 */
	@PID(100)
	public ChildOrderId getChildId();
	public void setChildId(ChildOrderId struct);
}
