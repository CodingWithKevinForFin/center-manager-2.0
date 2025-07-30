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

public class AmiSchema_PROCEDURE {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper procedureName;
	final public AmiColumnWrapper procedureType;
	final public AmiColumnWrapper returnType;
	final public AmiColumnWrapper arguments;
	final public AmiColumnWrapper options;
	final public AmiColumnWrapper definedBy;

	public AmiSchema_PROCEDURE(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_PROCEDURE);

		this.procedureName = def.addColumn("ProcedureName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.procedureType = def.addColumn("ProcedureType", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.returnType = def.addColumn("ReturnType", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.arguments = def.addColumn("Arguments", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.options = def.addColumn("Options", AmiTable.TYPE_STRING);
		this.definedBy = def.addColumn("DefinedBy", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(procedureName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String procedureName, String procedureType, String returnType, String arguments, String options, byte defType, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.reset();
		this.preparedRow.setString(this.procedureName, procedureName);
		this.preparedRow.setString(this.procedureType, procedureType);
		this.preparedRow.setString(this.options, options);
		this.preparedRow.setString(this.returnType, returnType);
		this.preparedRow.setString(this.arguments, arguments);
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
			r.put(row.getString(this.procedureName), row);
		}
		return r;
	}
}
