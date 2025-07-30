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
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_COLUMN {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper tableName;
	final public AmiColumnWrapper columnName;
	final public AmiColumnWrapper dataType;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper noNull;
	final public AmiColumnWrapper position;
	final public AmiColumnWrapper definedBy;

	public AmiSchema_COLUMN(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_COLUMN);

		this.tableName = def.addColumn("TableName", AmiTable.TYPE_STRING);
		this.columnName = def.addColumn("ColumnName", AmiTable.TYPE_STRING);
		this.dataType = def.addColumn("DataType", AmiTable.TYPE_ENUM);
		this.options = def.addColumn("Options", AmiTable.TYPE_ENUM);
		this.noNull = def.addColumn("NoNull", AmiTable.TYPE_BOOLEAN);
		this.position = def.addColumn("Position", AmiTable.TYPE_INT);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(tableName.getName(), columnName.getName()), CH.l(AmiIndexImpl.TYPE_SORT, AmiIndexImpl.TYPE_SORT),
				AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String tableName, String columnName, String dataType, String options, boolean noNull, int position, byte defType, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.tableName, tableName);
		this.preparedRow.setString(this.columnName, columnName);
		this.preparedRow.setString(this.dataType, dataType);
		this.preparedRow.setString(this.options, options);
		this.preparedRow.setLong(this.noNull, noNull ? 1 : 0);
		this.preparedRow.setLong(this.position, position);
		this.preparedRow.setString(this.definedBy, AmiTableUtils.toStringForDefType(defType));
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public Map<Tuple2<String, String>, AmiRow> getRowsByTableNameColumnName() {
		Map<Tuple2<String, String>, AmiRow> r = new HashMap<Tuple2<String, String>, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(new Tuple2<String, String>(row.getString(tableName), row.getString(columnName)), row);
		}
		return r;
	}
}
