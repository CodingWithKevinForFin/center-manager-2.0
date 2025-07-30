package com.f1.ami.center.hdb;

import java.util.Iterator;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.utils.CasterManager;

public class AmiHdbColumn implements Column {

	final private AmiHdbTable table;
	private long totalSize;
	private Caster<? extends Comparable> caster;
	private int location;
	private byte amiType;
	private short nameAmiKey;
	private String name;
	private byte mode;

	public AmiHdbColumn(AmiHdbTable table, int position, AmiHdbColumnDef def) {
		this.table = table;
		this.name = def.getName();
		this.location = position;
		this.caster = def.getCaster();
		this.amiType = def.getAmiType();
		this.mode = def.getMode();
		this.nameAmiKey = def.getAmiKey();
	}

	public String getName() {
		return name;
	}

	public byte getMode() {
		return mode;
	}

	public byte getAmiType() {
		return amiType;
	}

	public Class getType() {
		return this.caster.getCastToClass();
	}

	public Caster<? extends Comparable> getTypeCaster() {
		return caster;
	}

	public AmiHdbTable getHistoryTable() {
		return this.table;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public void incTotalSize(long sizeOnDisk) {
		this.totalSize += sizeOnDisk;
	}
	public Comparable<?> cast(Comparable<?> t) {
		return caster.cast(t);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int size() {
		return totalSize > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalSize;
	}

	@Override
	public Iterator iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getLocation() {
		return this.location;
	}

	@Override
	public String getId() {
		return name;
	}

	@Override
	public Object getValue(int location) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setValue(int location, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Table getTable() {
		return this.table.getTable();
	}

	public void onRowsCleared() {
		this.totalSize = 0;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public void setAmiType(byte atype) {
		this.amiType = atype;
		this.caster = (Caster<? extends Comparable>) CasterManager.getCaster(AmiUtils.getClassForValueType(atype));
	}

	public void setName(short nameAmiKey, String name) {
		this.nameAmiKey = nameAmiKey;
		this.name = name;
	}

	public void setMode(byte mode) {
		this.mode = mode;
	}

	public short getNameAmiKey() {
		return this.nameAmiKey;
	}

}
