package com.vortex.agent.state;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.f1.base.LockedException;
import com.f1.container.impl.BasicState;

public class VortexAgentOsAdapterState extends BasicState {

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
	private final SimpleDateFormat basicDateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm");
	private VortexAgentOsAdapterManager manager;

	public Date parseSystemDate(String date) throws java.text.ParseException {
		return dateFormatter.parse(date);
	}

	public Date parseSystemDateBasicVersion(String date) throws java.text.ParseException {
		return basicDateFormatter.parse(date);
	}

	public VortexAgentOsAdapterManager getManager() {
		return manager;
	}

	public void setManager(VortexAgentOsAdapterManager manager) {
		LockedException.assertLocked(manager);
		this.manager = manager;
	}
}
