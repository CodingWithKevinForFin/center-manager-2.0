package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.centerclient.AmiCenterClientStats;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.EH;
import com.f1.utils.GcMemoryMonitor;
import com.f1.utils.OH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_Long;

public class AmiWebStatsManager {

	public class Stats implements Comparable<Stats> {
		private int users;
		private int maxUsers;
		private long memory;
		private long maxMemory;
		private boolean available;
		private final String description;
		private byte centerId;//-1==web

		public Stats(byte centerId, String description) {
			this.centerId = centerId;
			this.description = description;
		}

		public boolean setAvailable(boolean available) {
			if (this.available == available)
				return false;
			this.available = available;
			return true;

		}

		public boolean update(int users, int maxUsers, long memory, long maxMemory) {
			if (this.users == users && this.maxUsers == maxUsers && this.memory == memory && this.maxMemory == maxMemory)
				return false;
			this.users = users;
			this.maxUsers = maxUsers;
			this.memory = memory;
			this.maxMemory = maxMemory;
			return true;
		}

		public double getUserPct() {
			return (double) this.users / this.maxUsers;
		}

		public double getMemoryPct() {
			return (double) this.memory / this.maxMemory;
		}

		public int getUsers() {
			return users;
		}

		public int getMaxUsers() {
			return maxUsers;
		}

		public long getMemory() {
			return memory;
		}

		public long getMaxMemory() {
			return maxMemory;
		}

		public boolean isAvailable() {
			if (centerId != -1 && OH.eq(EH.getProcessUid(), service.getWebManagers().getWebManager(centerId).getProcessUid()))
				return false;
			return available;
		}

		@Override
		public int compareTo(Stats o) {
			return OH.compare(Math.max(getMemoryPct(), getUserPct()), Math.max(o.getMemoryPct(), o.getUserPct()));
		}

		public String getDescription() {
			return this.description;
		}

		public Table buildHistory() {
			Table r;
			if (this.centerId == -1) {
				r = globalStats.getStats();
			} else {
				r = AmiWebUtils.toTable(service.getWebManagers(), AmiWebManagers.FEED + AmiConsts.TYPE_STATS);
				TableHelper.sort(r, AmiConsts.PARAM_STATS_TIME);
			}
			int c = r.getColumn(AmiConsts.PARAM_STATS_POST_GC_USED_MEMORY).getLocation();
			for (Row row : r.getRows())
				row.putAt(c, toPaddedMemory(row.getAt(c, Caster_Long.INSTANCE)));
			return r;
		}

	}

	private Stats webStats = new Stats((byte) -1, "web");
	private Stats centerStats[];

	final private AmiWebService service;
	final private AmiCenterClientStats globalStats;
	final private double memoryMultiplier;

	public AmiWebStatsManager(AmiWebService service, AmiCenterClientStats webstats) {
		this.service = service;
		this.globalStats = webstats;
		this.webStats.setAvailable(true);
		this.memoryMultiplier = service.getPortletManager().getTools().getOptional(AmiCommonProperties.PROPERTY_AMI_WARNING_MEMORY_MULTIPLIER,
				AmiCommonProperties.DEFAULT_AMI_WARNING_MEMORY_MULTIPLIER);
		AmiWebManager[] centerManagers = this.service.getWebManagers().getManagers();
		this.centerStats = new Stats[centerManagers.length];
		for (int i = 0; i < this.centerStats.length; i++)
			this.centerStats[i] = new Stats(centerManagers[i].getCenterId(), centerManagers[i].getCenterName());
		evaluate();
	}

	private long nextCheck = 0;
	private double worstUserStats;
	private double worstMemoryStats;

	public boolean hasChanged() {
		return false;
	}

	public boolean evaluate() {
		double wus = 0;
		double wms = 0;
		boolean changed = false;
		for (int i = 0; i < centerStats.length; i++) {
			AmiWebManager cm = this.service.getWebManagers().getWebManager((byte) i);
			Stats cs = centerStats[i];
			AmiWebObject stats = cm.getIsEyeConnected() ? this.service.getWebManagers().getSystemObjectsManager((byte) i).getLatestStatsEvent() : null;
			if (cs.setAvailable(stats != null))
				changed = true;
			if (!cs.isAvailable())
				continue;
			if (stats == null)
				continue;
			AmiWebSystemObjectsManager om = this.service.getWebManagers().getSystemObjectsManager((byte) i);
			int users = (Integer) stats.get(AmiConsts.PARAM_STATS_UNIQUE_USERS);
			int maxUsers = (Integer) stats.get(AmiConsts.PARAM_STATS_MAX_USERS);
			long memory = toPaddedMemory((Long) stats.get(AmiConsts.PARAM_STATS_POST_GC_USED_MEMORY));
			long maxMemory = (Long) stats.get(AmiConsts.PARAM_STATS_MAX_MEMORY);
			if (cs.update(users, maxUsers, memory, maxMemory))
				changed = true;
			wus = Math.max(wus, cs.getUserPct());
			wms = Math.max(wms, cs.getMemoryPct());
		}
		short users = AmiAuthManager.INSTANCE.getUsersCount();
		short maxUsers = AmiAuthManager.INSTANCE.getMaxUsers();
		long memory = toPaddedMemory(GcMemoryMonitor.getLastUsedMemory());
		long maxMemory = GcMemoryMonitor.getLastMaxMemory();
		if (webStats.update(users, maxUsers, memory, maxMemory))
			changed = true;
		wus = Math.max(wus, webStats.getUserPct());
		wms = Math.max(wms, webStats.getMemoryPct());
		this.worstUserStats = wus;
		this.worstMemoryStats = wms;
		return changed;
	}

	private long toPaddedMemory(long memory) {
		return (long) (this.memoryMultiplier * memory);
	}

	public void onFrentendCalled() {
		long now = EH.currentTimeMillis();
		if (now < nextCheck)
			return;
		nextCheck = now + 10000;
		if (evaluate())
			service.getDesktop().flagStatusPanelNeedsUpdate();

	}

	public double getWorstUserStats() {
		return worstUserStats;

	}
	public double getWorstMemoryStats() {
		return worstMemoryStats;
	}

	public List<Stats> getStats() {
		List<Stats> r = new ArrayList<Stats>(this.centerStats.length + 1);
		if (this.webStats.isAvailable())
			r.add(this.webStats);
		for (Stats i : this.centerStats)
			if (i.isAvailable())
				r.add(i);
		Collections.sort(r);
		return r;
	}

	public String getWorstUserIconHTML() {
		double stat = worstUserStats * 100d;
		String iconClass = "user-stat-icon stat-icon";
		String imageUrl = "rsc/";
		if (stat <= 20)
			imageUrl += "user-ex-healthy.svg";
		else if (stat <= 40)
			imageUrl += "user-healthy.svg";
		else if (stat <= 60)
			imageUrl += "user-warning.svg";
		else if (stat <= 80) {
			imageUrl += "user-severe.svg";
			iconClass += " blink-severe";
		} else {
			imageUrl += "user-critical.svg";
			iconClass += " blink-critical";
		}
		return "<img class='" + iconClass + "' src='" + imageUrl + "'>";
	}

	public String getWorstMemoryIconHTML() {
		double stat = worstMemoryStats * 100d;
		String iconClass = "mem-stat-icon stat-icon";
		String imageUrl = "rsc/";
		if (stat <= 20)
			imageUrl += "mem-ex-healthy.svg";
		else if (stat <= 40)
			imageUrl += "mem-healthy.svg";
		else if (stat <= 60)
			imageUrl += "mem-warning.svg";
		else if (stat <= 80) {
			imageUrl += "mem-severe.svg";
			iconClass += " blink-severe";
		} else {
			imageUrl += "mem-critical.svg";
			iconClass += " blink-critical";
		}
		return "<img class='" + iconClass + "' src='" + imageUrl + "'>";
	}

}
