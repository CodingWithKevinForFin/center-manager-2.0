package com.f1.ami.amicommon.centerclient;

import com.f1.ami.amicommon.AmiCenterDefinition;

public interface AmiCenterClientListener {
	void onCenterMessage(AmiCenterDefinition center, AmiCenterClientObjectMessage m);
	void onCenterDisconnect(AmiCenterDefinition center);
	void onCenterConnect(AmiCenterDefinition center);
	void onCenterMessageBatchDone(AmiCenterDefinition center);
}
