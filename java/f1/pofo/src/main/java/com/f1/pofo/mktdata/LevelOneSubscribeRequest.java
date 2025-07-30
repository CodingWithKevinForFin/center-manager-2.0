package com.f1.pofo.mktdata;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.refdata.Security;

/**
 * subscribe to a level one market data for a particular security
 */
@VID("F1.MK.1S")
public interface LevelOneSubscribeRequest extends Message {

	/**
	 * @return the id of the security to subscribe to. see
	 *         {@link Security#getSecurityId()}
	 */
	@PID(1)
	public int getSecurityRefId();

	public void setSecurityRefId(int securityRefId);

	/**
	 * @return the id of the subscriber which will receive events
	 */
	@PID(2)
	public int getSubscriberId();

	public void setSubscriberId(int subscriberId);

}
