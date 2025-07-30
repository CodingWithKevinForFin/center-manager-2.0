package com.f1.pofo.mktdata;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;

/**
 * unsubscribe to a level one market data for a particular security
 */
@VID("F1.MK.1U")
public interface LevelOneUnsubscribeRequest extends Message {

	@PID(1)
	public int getSecurityRefId();

	public void setSecurityRefId(int securityRefId);

	@PID(2)
	public int getSubscriberId();

	public void setSubscriberId(int subscriberId);
}
