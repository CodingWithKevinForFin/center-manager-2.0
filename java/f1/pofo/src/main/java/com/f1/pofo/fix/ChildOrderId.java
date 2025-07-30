package com.f1.pofo.fix;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.oms.Order;

/**
 * in composite, uniquely identifies a request
 */
@VID("F1.GE.CI")
public interface ChildOrderId extends Message {

	/**
	 * @return the order id of the child. See {@link Order#getId()}
	 */
	@PID(1)
	public String getOrderId();

	public void setOrderId(String id);

	/**
	 * @return Current valid ClOrdId sent out
	 */
	@PID(2)
	public int getRequestId();

	public void setRequestId(int reqRev);

	/**
	 * @return the revision id of the order
	 */
	@PID(3)
	public int getOrderRevisionId();

	public void setOrderRevisionId(int id);
}
