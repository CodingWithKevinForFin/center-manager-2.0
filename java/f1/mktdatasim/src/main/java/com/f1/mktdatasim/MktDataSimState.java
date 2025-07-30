package com.f1.mktdatasim;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.base.ObjectGenerator;
import com.f1.container.impl.BasicState;
import com.f1.refdata.RefDataManager;
import com.f1.refdata.impl.BasicRefDataManager;
import com.f1.utils.CachedFile;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.PropertyController;

public class MktDataSimState extends BasicState {
	private static final Logger log = Logger.getLogger(MktDataSimState.class.getName());

	public Cache data;
	public CachedFile file;
	private RefDataManager refDataManager;

	private boolean simulateRefData;

	public MktDataSimState(CachedFile file, ObjectGenerator generator, PropertyController properties, TimeZone timeZone, RefDataManager refDataManager) {
		this.file = file;
		this.timeZone = timeZone;
		this.generator = generator;
		if (refDataManager == null) {
			simulateRefData = true;
			this.refDataManager = new BasicRefDataManager(null, null);
		} else {
			simulateRefData = false;
			this.refDataManager = refDataManager;
		}
	}

	Map<Integer, MktDataSimNameSettings> subscribed = new HashMap<Integer, MktDataSimNameSettings>();
	Map<Integer, MktDataSimNameSettings> configuration = new HashMap<Integer, MktDataSimNameSettings>();
	Map<Integer, Double> openPrices=new HashMap<Integer,Double>();
	public Map<Integer, Double> getOpenPrices() {
		return openPrices;
	}

	private long count = 0;

	private long startTime;
	private TimeZone timeZone;

	private ObjectGenerator generator;

	private int nextSecurityId;

	public Map<Integer, MktDataSimNameSettings> getSubscribed() {
		return subscribed;
	}
	public Map<Integer, MktDataSimNameSettings> getConfiguration() {
		return configuration;
	}
	public long incCount() {
		return ++count;
	}
	public long getStartTime() {
		return startTime;
	}

	public void start(long now) {
		this.startTime = now;
		this.count = 0;
	}
	public void stop() {
		this.startTime = 0;
		this.count = 0;
	}
	public boolean isRunning() {
		return startTime != 0;
	}
	public long getCount() {
		return count;
	}
}
