package com.f1.ami.center.sysschema;

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
import com.f1.ami.center.table.index.AmiIndexMap_Rows;
import com.f1.utils.CH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_REPLICATION {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper replicationName;
	final public AmiColumnWrapper targetTable;
	final public AmiColumnWrapper sourceCenter;
	final public AmiColumnWrapper sourceTable;
	final public AmiColumnWrapper mapping;
	final public AmiColumnWrapper options;
	final public AmiIndexImpl index;

	public AmiSchema_REPLICATION(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_REPLICATION);
		this.replicationName = def.addColumn("ReplicationName", AmiTable.TYPE_STRING);
		this.targetTable = def.addColumn("TargetTable", AmiTable.TYPE_STRING);
		this.sourceCenter = def.addColumn("SourceCenter", AmiTable.TYPE_STRING);
		this.sourceTable = def.addColumn("SourceTable", AmiTable.TYPE_STRING);
		this.mapping = def.addColumn("Mapping", AmiTable.TYPE_STRING);
		this.options = def.addColumn("Options", AmiTable.TYPE_STRING);
		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(replicationName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
		this.index = this.table.getIndex("pk");
		this.preparedRow = this.table.createAmiPreparedRow();
		AmiTableUtils.setSystemPersister(imdb, table);
	}
	public void addRow(AmiRow existing, String replicationName, String targetTable, String sourceCenter, String sourceTable, String mapping, String options, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.replicationName, replicationName);
		this.preparedRow.setString(this.targetTable, targetTable);
		this.preparedRow.setString(this.sourceCenter, sourceCenter);
		this.preparedRow.setString(this.sourceTable, sourceTable);
		this.preparedRow.setString(this.mapping, mapping);
		this.preparedRow.setString(this.options, options);
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public void removeRow(String name, CalcFrameStack sf) {
		AmiIndexMap_Rows t = (AmiIndexMap_Rows) index.getRootMap().getIndex(name);
		if (t == null)
			throw new RuntimeException("REPLICATION not found: '" + name + "'");
		table.removeAmiRow(t.getSingleValue(), sf);
	}
}
