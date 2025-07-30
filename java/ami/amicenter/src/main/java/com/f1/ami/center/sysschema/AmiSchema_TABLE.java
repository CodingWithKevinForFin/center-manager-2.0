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

public class AmiSchema_TABLE {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper tableName;
	final public AmiColumnWrapper broadcast;
	final public AmiColumnWrapper refreshPeriodMs;
	final public AmiColumnWrapper persistEngine;
	final public AmiColumnWrapper onUndefColumn;
	final public AmiColumnWrapper definedBy;
	private AmiColumnWrapper initialCapacity;

	public AmiSchema_TABLE(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_TABLE);

		this.tableName = def.addColumn("TableName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.broadcast = def.addColumn("Broadcast", AmiTable.TYPE_BOOLEAN, AmiConsts.NONULL_OPTIONS);
		this.refreshPeriodMs = def.addColumn("RefreshPeriodMs", AmiTable.TYPE_LONG);
		this.persistEngine = def.addColumn("PersistEngine", AmiTable.TYPE_ENUM, null);
		this.onUndefColumn = def.addColumn("OnUndefColumn", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.initialCapacity = def.addColumn("InitialCapacity", AmiTable.TYPE_INT, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(tableName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}

	public void addRow(AmiRow existing, String tableName, boolean broadcast, long refreshPeriodMs, String persistEngine, byte onUndefColumnType, byte defType, int initialCapacity,
			CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.tableName, tableName);
		this.preparedRow.setLong(this.broadcast, broadcast ? 1 : 0);
		this.preparedRow.setLong(this.refreshPeriodMs, refreshPeriodMs);
		this.preparedRow.setString(this.persistEngine, persistEngine);
		this.preparedRow.setString(this.onUndefColumn, AmiTableUtils.toStringForOnUndefColType(onUndefColumnType));
		this.preparedRow.setString(this.definedBy, AmiTableUtils.toStringForDefType(defType));
		this.preparedRow.setLong(this.initialCapacity, initialCapacity);
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public Map<String, AmiRow> getRowsByName() {
		Map<String, AmiRow> r = new HashMap<String, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(row.getString(tableName), row);
		}
		return r;
	}
}
