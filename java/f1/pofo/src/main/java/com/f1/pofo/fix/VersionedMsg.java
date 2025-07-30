package com.f1.pofo.fix;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FX.VM")
public interface VersionedMsg extends Message {
	/**
	 * @return cl orig order id (typically fix tag 41)
	 */
	@PID(110)
	public String getRefId();

	public void setRefId(String id);
}
