package com.f1.pofo.mktdata;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

/**
 * status of a market data subscription
 */
@VID("F1.MK.RS")
public interface MktDataStatus extends PartialMessage {

	byte STATUS_SUBSCRIBED = 1;
	byte STATUS_UNSUBSCRIBED = 2;
	byte STATUS_LEVELONE_SUBSCRIBED = 3;
	byte STATUS_LEVELONE_UNSUBSCRIBED = 4;
	byte STATUS_LEVELONE_UPDATE = 5;
	byte STATUS_LEVELONE_DISCONNECTED = 6;

	@PID(1)
	public byte getStatusType();

	public void setStatusType(byte statusType);

	@PID(2)
	public int getSubscriberId();

	public void setSubscriberId(int subscriberId);

	@PID(3)
	public LevelOneData getLevelOneQuote();

	public void setLevelOneQuote(LevelOneData levelOneQuote);
}
