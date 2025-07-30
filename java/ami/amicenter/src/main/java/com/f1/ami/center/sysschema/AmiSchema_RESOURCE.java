package com.f1.ami.center.sysschema;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_RESOURCE {

	final public AmiTableImpl table;
	final public AmiColumnWrapper i;
	final public AmiColumnWrapper modifiedOn;
	final public AmiColumnWrapper checksum;
	final public AmiColumnWrapper fileSize;
	final public AmiColumnWrapper imageWidth;
	final public AmiColumnWrapper imageHeight;
	final public AmiPreparedRow preparedRow;

	public AmiSchema_RESOURCE(AmiImdbImpl imdb, CalcFrameStack sf) {
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_AMI, AmiConsts.TYPE_RESOURCE);

		this.i = def.addColumn(AmiConsts.TABLE_PARAM_I, AmiTable.TYPE_STRING);
		this.modifiedOn = def.addColumn("ModifiedOn", AmiTable.TYPE_LONG);
		this.checksum = def.addColumn("Checksum", AmiTable.TYPE_LONG);
		this.fileSize = def.addColumn("FileSize", AmiTable.TYPE_LONG);
		this.imageWidth = def.addColumn("ImageWidth", AmiTable.TYPE_LONG);
		this.imageHeight = def.addColumn("ImageHeight", AmiTable.TYPE_LONG);
		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
	}
	public void addResource(AmiCenterState state, AmiCenterResource res, CalcFrameStack sf) {

		preparedRow.reset();
		preparedRow.setString(this.i, res.getPath());
		preparedRow.setLong(this.modifiedOn, res.getModifiedOn());
		preparedRow.setLong(this.checksum, res.getChecksum());
		preparedRow.setLong(this.fileSize, res.getSize());
		preparedRow.setLong(this.imageWidth, res.getImageWidth());
		preparedRow.setLong(this.imageHeight, res.getImageHeight());
		this.table.insertAmiRow(preparedRow, sf);

	}
	public void removeResource(AmiCenterState amiCenterState, AmiCenterResource a, CalcFrameStack sf) {
		preparedRow.reset();
		preparedRow.setString(i, a.getPath());
		AmiRow row = this.table.getAmiRow(null, a.getPath(), preparedRow);
		if (row != null)
			this.table.removeAmiRow(row, sf);
	}
}
