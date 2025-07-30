package com.f1.pofo.fix;

import com.f1.base.PID;
import com.f1.base.VID;

/**
 * indicates a replace request. see {@link MsgType#CANCEL_REJECT}
 */
@VID("F1.FX.RR")
public interface FixOrderReplaceReject extends FixRequestReject {

	/**
	 * @return the id of the order
	 */
	@PID(10)
	public String getOrderID();

	public void setOrderID(String orderID);

	/**
	 * @return client order id (typically tag 11)
	 */
	@PID(11)
	public String getRequestID();

	public void setRequestID(String requestID);

	/**
	 * @return the order status. See {@link OrdStatus}
	 */
	@PID(12)
	public int getOrderStatus();

	public void setOrderStatus(int ordStatus);

	/**
	 * @return the cancel reject response to (typically tag 434)
	 */
	@PID(13)
	public int getResponseTo();

	public void setResponseTo(int responseTo);

	/**
	 * @return text on the fix message(typically tag 58)
	 */
	@PID(14)
	public String getText();

	public void setText(String text);

	/**
	 * @return the cancel reject reason (typically tag 102)
	 */
	@PID(15)
	public int getReason();

	public void setReason(int i);

	/**
	 * @return original client order id (typically tag 41)
	 */
	@PID(16)
	public String getRefId();

	public void setRefId(String refId);
}
