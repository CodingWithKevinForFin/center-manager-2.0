package com.f1.pofo.oms;

import com.f1.base.PID;
import com.f1.base.VID;

/**
 * represents a new order request from a client
 * 
 * @author rcooke
 * 
 */
public interface ChildNewOrderRequest extends ChildOrderRequest {

	/**
	 * @return the name of the session
	 */
        @PID(80)
	public String getSessionName();

	public void setSessionName(String session);
}
