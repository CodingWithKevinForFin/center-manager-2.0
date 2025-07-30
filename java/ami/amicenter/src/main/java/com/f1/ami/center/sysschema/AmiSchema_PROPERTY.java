package com.f1.ami.center.sysschema;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.CH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_PROPERTY {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper propertyName;
	final public AmiColumnWrapper propertyValue;

	public AmiSchema_PROPERTY(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_PROPERTY);

		this.propertyName = def.addColumn("PropertyName", AmiTable.TYPE_STRING);
		this.propertyValue = def.addColumn("PropertyValue", AmiTable.TYPE_STRING);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(propertyName.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
	}
	public void addRow(String propertyName, String propertyValue, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.propertyName, propertyName);
		this.preparedRow.setString(this.propertyValue, propertyValue);
		this.table.insertAmiRow(this.preparedRow, sf);
	}
}
