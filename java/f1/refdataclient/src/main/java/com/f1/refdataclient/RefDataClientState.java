package com.f1.refdataclient;

import com.f1.container.impl.BasicState;
import com.f1.refdata.impl.BasicRefDataManager;

public class RefDataClientState extends BasicState {

	public RefDataClientState() {
		manager = new BasicRefDataManager(null, null);
	}
	public RefDataClientState(BasicRefDataManager manager) {
		this.manager = manager;
	}

	final public BasicRefDataManager manager;

}
