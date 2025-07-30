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

public class AmiSchema_TRIGGER {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper triggerName;
	final public AmiColumnWrapper tableName;
	final public AmiColumnWrapper triggerType;
	final public AmiColumnWrapper priority;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper definedBy;
	final public AmiColumnWrapper enabled;

	public AmiSchema_TRIGGER(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_TRIGGER);

		this.triggerName = def.addColumn("TriggerName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.tableName = def.addColumn("TableName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.triggerType = def.addColumn("TriggerType", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.priority = def.addColumn("Priority", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.options = def.addColumn("Options", AmiTable.TYPE_STRING);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.enabled = def.addColumn("Enabled", AmiTable.TYPE_BOOLEAN, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(triggerName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String tableName, String triggerName, String triggerType, int priority, String options, byte defType, boolean enabled, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.tableName, tableName);
		this.preparedRow.setString(this.triggerName, triggerName);
		this.preparedRow.setString(this.triggerType, triggerType);
		this.preparedRow.setLong(this.priority, priority);
		this.preparedRow.setString(this.options, options);
		this.preparedRow.setString(this.definedBy, AmiTableUtils.toStringForDefType(defType));
		this.preparedRow.setLong(this.enabled, enabled ? 1 : 0);
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public Map<String, AmiRow> getRowsByTriggerName() {
		Map<String, AmiRow> r = new HashMap<String, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(row.getString(triggerName), row);
		}
		return r;
	}
}
