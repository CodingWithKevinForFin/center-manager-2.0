package com.f1.ami.center.sysschema;

import java.lang.Thread.State;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.web.auth.AmiAuthManager;
import com.f1.utils.EH;
import com.f1.utils.GcMemoryMonitor;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_STATS {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final private AmiColumnWrapper time;
	final private AmiColumnWrapper usedMemory;
	final private AmiColumnWrapper maxMemory;
	final private AmiColumnWrapper postGcUsedMemory;
	final private AmiColumnWrapper runningThreads;
	final private AmiColumnWrapper rows;
	final private AmiColumnWrapper events;
	final private AmiColumnWrapper queries;
	final private AmiColumnWrapper expires;
	final private AmiColumnWrapper uniqueUsers;
	final private AmiColumnWrapper maxUsers;
	final private AmiImdbImpl imdb;

	public AmiSchema_STATS(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_STATS);

		this.time = def.addColumn(AmiConsts.PARAM_STATS_TIME, AmiTable.TYPE_UTC);
		this.expires = def.addColumn("E", AmiTable.TYPE_LONG);
		this.usedMemory = def.addColumn(AmiConsts.PARAM_STATS_USED_MEMORY, AmiTable.TYPE_LONG);
		this.maxMemory = def.addColumn(AmiConsts.PARAM_STATS_MAX_MEMORY, AmiTable.TYPE_LONG);
		this.postGcUsedMemory = def.addColumn(AmiConsts.PARAM_STATS_POST_GC_USED_MEMORY, AmiTable.TYPE_LONG);
		this.runningThreads = def.addColumn(AmiConsts.PARAM_STATS_RUNNING_THREADS, AmiTable.TYPE_SHORT);
		this.rows = def.addColumn(AmiConsts.PARAM_STATS_ROWS, AmiTable.TYPE_LONG);
		this.events = def.addColumn(AmiConsts.PARAM_STATS_EVENTS, AmiTable.TYPE_LONG);
		this.queries = def.addColumn(AmiConsts.PARAM_STATS_QUERIES, AmiTable.TYPE_LONG);
		this.uniqueUsers = def.addColumn(AmiConsts.PARAM_STATS_UNIQUE_USERS, AmiTable.TYPE_SHORT);
		this.maxUsers = def.addColumn(AmiConsts.PARAM_STATS_MAX_USERS, AmiTable.TYPE_SHORT);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
	}
	public void addRow(long expires, CalcFrameStack sf) {
		AmiCenterState state = imdb.getState();
		this.preparedRow.reset();
		long time = EH.currentTimeMillis();
		long usedMemory = EH.getTotalMemory() - EH.getFreeMemory();
		long maxMemory = EH.getMaxMemory();
		long postGcUsedMemory = GcMemoryMonitor.getLastUsedMemory();
		long runningThreads = 0;
		long queries = state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_PROCESS_RELAY_EVENT);
		long events = state.getAmiMessageStat(AmiCenterState.STATUS_TYPE_PROCESS_RELAY_EVENT);
		long rows = 0;
		for (AmiTableImpl table : state.getAmiImdb().getObjectsManager().getAmiTables())
			rows += table.getRowsCount();
		for (Thread thread : EH.getAllThreads())
			if (thread.getState() == State.RUNNABLE)
				runningThreads++;
		short usersCount = AmiAuthManager.INSTANCE.getUsersCount();
		short maxUsers = AmiAuthManager.INSTANCE.getMaxUsers();
		this.preparedRow.setLong(this.time, time);
		this.preparedRow.setLong(this.expires, time + expires);
		this.preparedRow.setLong(this.usedMemory, usedMemory);
		this.preparedRow.setLong(this.maxMemory, maxMemory);
		this.preparedRow.setLong(this.postGcUsedMemory, postGcUsedMemory);
		this.preparedRow.setLong(this.runningThreads, runningThreads);
		this.preparedRow.setLong(this.rows, rows);
		this.preparedRow.setLong(this.events, events);
		this.preparedRow.setLong(this.queries, queries);
		this.preparedRow.setLong(this.uniqueUsers, usersCount);
		this.preparedRow.setLong(this.maxUsers, maxUsers);
		this.table.insertAmiRow(this.preparedRow, sf);
	}
}
