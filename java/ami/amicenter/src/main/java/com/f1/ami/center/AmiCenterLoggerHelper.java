package com.f1.ami.center;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.center.procs.AmiStoredProcBindingImpl;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.utils.CH;
import com.f1.utils.mutable.Mutable;

public class AmiCenterLoggerHelper {
	private static final Logger amilog = Logger.getLogger("AMI_STATS.CENTER");

	private Map<String, Mutable.Long> timerStats = new HashMap<String, Mutable.Long>();
	private Map<String, Mutable.Long> tableSizes = new HashMap<String, Mutable.Long>();
	private Map<String, Mutable.Long> triggerStats = new HashMap<String, Mutable.Long>();
	private Map<String, Mutable.Long> procStats = new HashMap<String, Mutable.Long>();

	public void log(AmiCenterState state) {

		long rowCount = logTables(state);
		logTimers(state);
		logTriggers(state);
		logProcedures(state);

		int stringPooolSize = state.getAmiKeyIdStringPoolMapSize();
		int valuePooolSize = state.getAmiValueStringPoolMapSize();
		AmiProcessStatsLogger.log(amilog, "AmiCenterEvents", //
				"rowsCount", rowCount, //
				"strPoolSize", stringPooolSize, //
				"valPoolSize", valuePooolSize, //
				"logins", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_LOGIN), //
				"logouts", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_LOGOUT), //
				"events", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_PROCESS_EVENT), //
				"relayEvents", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_PROCESS_RELAY_EVENT), //
				"dsQueries", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_QUERY_DATASOURCE), //
				"snapshots", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_GET_SNAPSHOT), //
				"objExpires", state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_OBJECT_EXPIRED)//
		);
	}

	private void logTimers(AmiCenterState state) {
		Collection<AmiTimerBindingImpl> amiTimers = state.getAmiImdb().getObjectsManager().getAmiTimerBindings();
		for (AmiTimerBindingImpl timer : amiTimers) {
			long count = timer.getStatsCount();
			if (put(this.timerStats, timer.getTimerName(), count)) {
				AmiProcessStatsLogger.log(amilog, "AmiCenterTimers", "name", timer.getTimerName(), "count", count, "millis", timer.getStatsNanos() / 1000000, "errors",
						timer.getStatsErrors());
			}
		}
		if (timerStats.size() > amiTimers.size()) {//timer got removed
			for (String name : CH.l(timerStats.keySet())) {
				if (state.getAmiImdb().getAmiTimer(name) == null) {
					timerStats.remove(name);
					AmiProcessStatsLogger.log(amilog, "AmiCenterTimers", "name", name, "count", -1);
				}
			}
		}
	}
	private void logTriggers(AmiCenterState state) {
		Collection<AmiTriggerBindingImpl> amiTriggers = state.getAmiImdb().getObjectsManager().getAmiTriggerBindings();
		for (AmiTriggerBindingImpl trigger : amiTriggers) {
			long count = trigger.getStatsCountTotal();
			if (put(this.triggerStats, trigger.getTriggerName(), count)) {
				AmiProcessStatsLogger.log(amilog, "AmiCenterTriggers", "name", trigger.getTriggerName(), "count", count, "millis", trigger.getStatsNanosTotal() / 1000000, "errors",
						trigger.getStatsErrorsTotal());
			}
		}
		if (triggerStats.size() > amiTriggers.size()) {//trigger got removed
			for (String name : CH.l(triggerStats.keySet())) {
				if (state.getAmiImdb().getAmiTrigger(name) == null) {
					triggerStats.remove(name);
					AmiProcessStatsLogger.log(amilog, "AmiCenterTriggers", "name", name, "count", -1);
				}
			}
		}
	}
	private void logProcedures(AmiCenterState state) {
		Collection<AmiStoredProcBindingImpl> amiProcs = state.getAmiImdb().getObjectsManager().getAmiStoredProcBindings();
		for (AmiStoredProcBindingImpl proc : amiProcs) {
			long count = proc.getStatsCount();
			if (put(this.procStats, proc.getStoredProcName(), count)) {
				AmiProcessStatsLogger.log(amilog, "AmiCenterStoredProcs", "name", proc.getStoredProcName(), "count", count, "millis", proc.getStatsNanos() / 1000000, "errors",
						proc.getStatsErrors());
			}
		}
		if (procStats.size() > amiProcs.size()) {//proc got removed
			for (String name : CH.l(procStats.keySet())) {
				if (state.getAmiImdb().getAmiStoredProc(name) == null) {
					procStats.remove(name);
					AmiProcessStatsLogger.log(amilog, "AmiCenterStoredProcs", "name", name, "count", -1);
				}
			}
		}
	}

	private long logTables(AmiCenterState state) {
		long rowCount = 0;
		Collection<AmiTableImpl> amiTables = state.getAmiImdb().getObjectsManager().getAmiTables();
		for (AmiTableImpl table : amiTables) {
			int count = table.getRowsCount();
			if (put(tableSizes, table.getName(), count))
				AmiProcessStatsLogger.log(amilog, "AmiCenterTables", "type", table.getName(), "count", count);
		}
		if (tableSizes.size() > amiTables.size()) {//table got removed
			for (String name : CH.l(tableSizes.keySet())) {
				if (state.getAmiImdb().getTableNoThrow(name) == null) {
					tableSizes.remove(name);
					AmiProcessStatsLogger.log(amilog, "AmiCenterTables", "type", name, "count", -1);
					if (amiTables.size() == tableSizes.size())
						break;
				}
			}
		}
		return rowCount;
	}

	static private boolean put(Map<String, Mutable.Long> stats, String name, long num) {
		Mutable.Long existing = stats.get(name);
		if (existing == null) {
			stats.put(name, new Mutable.Long(num));
			return true;
		} else if (existing.value == num)
			return false;
		else {
			existing.value = num;
			return true;
		}
	}

}
