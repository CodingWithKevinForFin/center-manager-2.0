package com.f1.mktdata;

import com.f1.pofo.mktdata.LevelOneData;

public interface MktDataListener {

	public void onLogin(MktDataManager manager);

	public void onLoginFailure(MktDataManager manager);

	public void onDisconnected(MktDataManager manager);

	public void onLevelOneData(MktDataManager manager, LevelOneData data);
}
