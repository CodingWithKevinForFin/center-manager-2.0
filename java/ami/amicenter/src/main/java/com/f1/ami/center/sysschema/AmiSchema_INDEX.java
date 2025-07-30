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
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_INDEX {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper tableName;
	final public AmiColumnWrapper indexName;
	final public AmiColumnWrapper columnName;
	final public AmiColumnWrapper indexType;
	final public AmiColumnWrapper indexPosition;
	final public AmiColumnWrapper constraint;
	final public AmiColumnWrapper autoGen;
	final public AmiColumnWrapper definedBy;

	public AmiSchema_INDEX(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_INDEX);

		this.indexName = def.addColumn("IndexName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.tableName = def.addColumn("TableName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.columnName = def.addColumn("ColumnName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.indexType = def.addColumn("IndexType", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.indexPosition = def.addColumn("IndexPosition", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.constraint = def.addColumn("Constraint", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.autoGen = def.addColumn("AutoGen", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(tableName.getName(), indexName.getName(), indexPosition.getName()),
				CH.l(AmiIndexImpl.TYPE_SORT, AmiIndexImpl.TYPE_SORT, AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String tableName, String columnName, String indexName, String indexType, int position, String constraint, byte defType, byte autogen,
			CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.tableName, tableName);
		this.preparedRow.setString(this.columnName, columnName);
		this.preparedRow.setString(this.indexName, indexName);
		this.preparedRow.setString(this.indexType, indexType);
		this.preparedRow.setString(this.constraint, constraint);
		this.preparedRow.setString(this.autoGen, AmiTableUtils.toStringForIndexAutoGenType(autogen));
		this.preparedRow.setLong(this.indexPosition, position);
		this.preparedRow.setString(this.definedBy, AmiTableUtils.toStringForDefType(defType));
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public Map<Tuple3<String, String, Integer>, AmiRow> getRowsByTableNameIndexNameIndexPosition() {
		Map<Tuple3<String, String, Integer>, AmiRow> r = new HashMap<Tuple3<String, String, Integer>, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(new Tuple3<String, String, Integer>(row.getString(tableName), row.getString(indexName), (int) row.getLong(indexPosition)), row);
		}
		return r;
	}
}
