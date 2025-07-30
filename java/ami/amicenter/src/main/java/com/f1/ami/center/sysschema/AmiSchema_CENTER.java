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

public class AmiSchema_CENTER {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper name;
	final public AmiColumnWrapper url;
	final public AmiColumnWrapper certFile;
	final public AmiColumnWrapper password;
	final public AmiIndexImpl index;
	private AmiImdbImpl imdb;

	public AmiSchema_CENTER(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.imdb = imdb;
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_CENTER);
		this.name = def.addColumn("CenterName", AmiTable.TYPE_STRING);
		this.url = def.addColumn("Url", AmiTable.TYPE_STRING);
		this.certFile = def.addColumn("CertFile", AmiTable.TYPE_STRING);
		this.password = def.addColumn("Password", AmiTable.TYPE_STRING);
		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(name.getName()), CH.l(AmiIndexImpl.TYPE_SORT), AmiIndex.CONSTRAINT_TYPE_UNIQUE, null, sf);
		this.index = this.table.getIndex("pk");
		this.preparedRow = this.table.createAmiPreparedRow();
		AmiTableUtils.setSystemPersister(imdb, table);
	}
	public void addRow(AmiRow existing, byte centerId, String name, String url, String certFile, String password, CalcFrameStack sf) {
		String encryptedPassword = password == null ? null : imdb.getState().encrypt(password);
		this.preparedRow.reset();
		this.preparedRow.setString(this.name, name);
		this.preparedRow.setString(this.url, url);
		this.preparedRow.setString(this.certFile, certFile);
		this.preparedRow.setString(this.password, encryptedPassword);
		if (existing != null)
			this.table.updateAmiRow(existing.getAmiId(), this.preparedRow, sf);
		else
			this.table.insertAmiRow(this.preparedRow, sf);
	}
	public void removeRow(String name, CalcFrameStack sf) {
		AmiIndexMap_Rows t = (AmiIndexMap_Rows) index.getRootMap().getIndex(name);
		if (t == null)
			throw new RuntimeException("REPLICATION_SOURCE not found: '" + name + "'");
		table.removeAmiRow(t.getSingleValue(), sf);
	}
}
