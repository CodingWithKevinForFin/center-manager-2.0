package com.f1.ami.center.sysschema;

import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.CH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_TIMER {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper timerName;
	final public AmiColumnWrapper timerType;
	final public AmiColumnWrapper schedule;
	final public AmiColumnWrapper priority;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper definedBy;
	final public AmiColumnWrapper lastRunTime;
	final public AmiColumnWrapper nextRunTime;
	final public AmiColumnWrapper enabled;

	public AmiSchema_TIMER(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_TIMER);

		this.timerName = def.addColumn("TimerName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.timerType = def.addColumn("TimerType", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.priority = def.addColumn("Priority", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.schedule = def.addColumn("Schedule", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.options = def.addColumn("Options", AmiTable.TYPE_STRING);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.lastRunTime = def.addColumn("LastRunTime", AmiTable.TYPE_UTC);
		this.nextRunTime = def.addColumn("NextRunTime", AmiTable.TYPE_UTC);
		this.enabled = def.addColumn("Enabled", AmiTable.TYPE_BOOLEAN, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(timerName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String timerName, String timerType, int priority, String schedule, String options, long lastRunTime, long nextRunTime, byte defType,
			boolean enabled, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.timerName, timerName);
		this.preparedRow.setString(this.timerType, timerType);
		this.preparedRow.setLong(this.priority, priority);
		this.preparedRow.setString(this.schedule, schedule);
		this.preparedRow.setString(this.options, options);
		this.preparedRow.setLong(this.enabled, enabled ? 1 : 0);
		if (lastRunTime != -1L)
			this.preparedRow.setLong(this.lastRunTime, lastRunTime);
		else
			this.preparedRow.setNull(this.lastRunTime);
		if (nextRunTime != -1L)
			this.preparedRow.setLong(this.nextRunTime, nextRunTime);
		else
			this.preparedRow.setNull(this.nextRunTime);
		this.preparedRow.setString(this.definedBy, AmiTableUtils.toStringForDefType(defType));
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public Map<String, AmiRow> getRowsByName() {
		Map<String, AmiRow> r = new HashMap<String, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(row.getString(this.timerName), row);
		}
		return r;
	}
	public void updateRowTimes(String timerName, long lastRunTime, long nextRunTime, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.timerName, timerName);

		if (lastRunTime != -1L)
			this.preparedRow.setLong(this.lastRunTime, lastRunTime);
		else
			this.preparedRow.setNull(this.lastRunTime);

		if (nextRunTime != -1L)
			this.preparedRow.setLong(this.nextRunTime, nextRunTime);
		else
			this.preparedRow.setNull(this.nextRunTime);
		this.table.updateAmiRow(this.preparedRow, sf);
	}
}
