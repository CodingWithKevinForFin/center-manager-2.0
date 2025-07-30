package com.f1.pofo.fix;

import com.f1.base.DateNanos;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * 
 * a fix status message.
 * 
 */
@VID("F1.FX.SR")
public interface FixStatusReport extends FixReport {

	/**
	 * @return order id (typically fix tag 37)
	 */
	@PID(40)
	public String getOrderId();
	public void setOrderId(String id);

	/**
	 * @return order id (typically fix tag 41)
	 */
	@PID(41)
	public String getOrigId();
	public void setOrigId(String id);

	/**
	 * @return the time of the transaction (fix tag 60)
	 */
	@PID(42)
	public DateNanos getTransactTime();
	public void setTransactTime(DateNanos transactTime);

	@PID(91)
	public int getExecTransType();
	public void setExecTransType(int type);
}
