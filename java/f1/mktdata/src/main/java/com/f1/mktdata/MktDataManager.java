package com.f1.mktdata;

import java.util.Set;

public interface MktDataManager {

	public boolean isConnected();
	public void unsubscribe(int itemName);
	public Set<Integer> getSubscribedNames();
	public void subscribe(int itemName);
	public void addListener(MktDataListener listener);
	public void removeListener(MktDataListener listener);
}