package com.f1.ami.web;

public interface AmiWebCenterListener {

	//	void onCenterDisconnected(AmiWebManager manager, IterableAndSize<AmiWebObject> removed);
	//	public void onCenterSnapshotProcessed(AmiWebManager manager);
	void onCenterConnectionStateChanged(AmiWebManager managers, byte state);

}
