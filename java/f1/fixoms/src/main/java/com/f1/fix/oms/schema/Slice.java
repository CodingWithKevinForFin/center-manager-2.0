package com.f1.fix.oms.schema;

import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.oms.ChildOrderRequest;

@VID("F1.OM.SL")
public interface Slice extends OmsOrder {

	/**
	 * @return the revision id of the pending request
	 */
	@PID(20)
	int getFixReqRevisionID();

	void setFixReqRevisionID(int id);

	/**
	 * @return the current revision id
	 */
	@PID(21)
	int getFixRevisionID();

	void setFixRevisionID(int id);

	/**
	 * @param childRequest
	 *            the state of the pending request.
	 */
	@PID(22)
	void setPendingChildReplace(ChildOrderRequest childRequest);

	ChildOrderRequest getPendingChildReplace();
}
