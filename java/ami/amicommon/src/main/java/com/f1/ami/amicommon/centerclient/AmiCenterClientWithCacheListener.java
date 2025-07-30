package com.f1.ami.amicommon.centerclient;

import java.util.List;

import com.f1.ami.amicommon.AmiCenterDefinition;

public interface AmiCenterClientWithCacheListener extends AmiCenterClientListener {
	int getObjectsCount();
	void toWebCached(String type, List<Object> sink);
	void init(AmiCenterClientState amiWebGlobalCacheState);
	void onSubscribe(AmiCenterDefinition def, String s);
	void onUnsubscribe(AmiCenterDefinition def, String s);

}
