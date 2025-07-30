package com.f1.ami.web;

public interface AmiWebManagersListener {

	public void onProcesserAdded(AmiWebRealtimeProcessor processor);
	public void onProcesserRenamed(AmiWebRealtimeProcessor processor, String oldAri, String newAri);
	public void onProcesserRemoved(AmiWebRealtimeProcessor processor);
	public void onRealtimeListenerAdded(AmiWebRealtimeObjectManager objects, AmiWebRealtimeObjectListener listener);
	public void onRealtimeListenerRemoved(AmiWebRealtimeObjectManager objects, AmiWebRealtimeObjectListener listener);

}
