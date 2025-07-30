package com.f1.utils.flogger.impl;

import java.util.concurrent.ConcurrentMap;
import com.f1.utils.CH;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.flogger.Flogger;
import com.f1.utils.flogger.FloggerAdapter;
import com.f1.utils.flogger.FloggerFormatter;
import com.f1.utils.flogger.FloggerManager;

public class BasicFloggerManager implements FloggerManager {

	private ConcurrentMap<Class, FloggerAdapter> adapters = new CopyOnWriteHashMap<Class, FloggerAdapter>();
	private FloggerFormatter formatter;

	public BasicFloggerManager() {
		registerFloggerAdapter(new SunFloggerAdapter());
		setDefaultFloggerFormatter(new BasicFloggerFormatter());
	}

	@Override
	public void registerFloggerAdapter(FloggerAdapter adapter) {
		CH.putOrThrow(adapters, adapter.getLoggerType(), adapter);
	}

	@Override
	public <T> FloggerAdapter<T> getAdapter(T logger) {
		FloggerAdapter r = adapters.get(logger.getClass());
		if (r != null)
			return r;
		for (FloggerAdapter adapter : adapters.values()) {
			if (adapter.canAdapt(logger)) {
				adapters.put(logger.getClass(), adapter);
				return adapter;
			}
		}
		throw new RuntimeException("not adapter registered for logger type: " + logger.getClass());
	}

	@Override
	public void setDefaultFloggerFormatter(FloggerFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public FloggerFormatter getDefaultFloggerFormatter() {
		return formatter;
	}

	@Override
	public Flogger createFlogger(String txnId) {
		return new BasicFlogger(txnId, this);
	}

}
