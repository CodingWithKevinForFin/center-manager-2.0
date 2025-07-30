package com.f1.pofo.mktdata;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.MK.UQ")
public interface MktDataUnSubscribeRequest extends Message {

	/**
	 * @return your unique subscription id.
	 */
	@PID(1)
	public int getSubscriberId();

	public void setSubscriberId(int subscriberId);
}
