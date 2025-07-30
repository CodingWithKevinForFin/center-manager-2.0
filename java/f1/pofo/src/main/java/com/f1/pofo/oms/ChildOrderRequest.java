package com.f1.pofo.oms;

import com.f1.pofo.fix.FixOrderInfo;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * 
 * represents a child order request from a client
 * 
 */
public interface ChildOrderRequest extends FixOrderInfo {
	/**
	 * the unique id for this request
	 */
        @PID(81)
	public String getRequestId();

	public void setRequestId(String id);

	/**
	 * @return the id of the original order
	 */
        @PID(82)
	public String getOrigRequestId();

	public void setOrigRequestId(String id);

	/**
	 * @return destination for this order
	 */
        @PID(83)
	public String getDestination();

	public void setDestination(String destination);
}
