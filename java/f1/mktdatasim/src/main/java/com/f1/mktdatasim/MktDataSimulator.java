package com.f1.mktdatasim;

import java.util.Set;

import com.f1.mktdata.MktDataListener;
import com.f1.mktdata.MktDataManager;

public class MktDataSimulator implements MktDataManager {

	final private MktDataManager inner;

	public MktDataSimulator(MktDataManager inner) {
		this.inner = inner;
	}

	@Override
	public boolean isConnected() {
		return inner.isConnected();
	}

	@Override
	public void unsubscribe(int itemName) {
		inner.unsubscribe(itemName);
	}

	@Override
	public Set<Integer> getSubscribedNames() {
		return inner.getSubscribedNames();
	}

	@Override
	public void subscribe(int itemName) {
		inner.subscribe(itemName);
	}

	@Override
	public void addListener(MktDataListener listener) {
		inner.addListener(listener);
	}

	@Override
	public void removeListener(MktDataListener listener) {
		inner.removeListener(listener);
	}

}
