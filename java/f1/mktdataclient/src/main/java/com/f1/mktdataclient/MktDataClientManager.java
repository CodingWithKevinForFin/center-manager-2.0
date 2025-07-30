package com.f1.mktdataclient;

import com.f1.base.Reference;
import com.f1.pofo.mktdata.LevelOneData;

public interface MktDataClientManager {

	// returns a new clientListenerId to be associated w/ lister for use on subsequent calls.
	public int subscribeClientListener();

	public void unsubscribeClientListener(MktDataClientListener listener);

	public void subscribeLevelOneData(int securityRefId, MktDataClientListener listener);
	public void unsubscribeLevelOneData(int securityRefId, MktDataClientListener listener);

	// will return null if subscription not complete. Reference will point to null if unsubscribed
	public Reference<LevelOneData> getLevelOneData(int securityRefId);
}
