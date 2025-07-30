package com.f1.mktdataclient;

import com.f1.pofo.mktdata.LevelOneData;

public interface MktDataClientListener {

	public int getClientListenerId();

	public void onLevelOneUpdate(MktDataClientManager manager, LevelOneData deltas, LevelOneData current);

	public void onLevelOneStale(MktDataClientManager manager, LevelOneData deltas);

	public void onLevelOneSubscribed(MktDataClientManager manager, boolean isStale, LevelOneData snapshot);

	public void onLevelOneUnsubscribed(MktDataClientManager manager, int referenceData);
}
