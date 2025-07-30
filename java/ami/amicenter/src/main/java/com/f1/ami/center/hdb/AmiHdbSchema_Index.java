package com.f1.ami.center.hdb;

public class AmiHdbSchema_Index {

	final private String columnName;
	final private String name;
	final private byte defType;

	public AmiHdbSchema_Index(AmiHdbIndex at) {
		this.columnName = at.getColumn().getName();
		this.name = at.getName();
		this.defType = at.getDefType();
	}

	public String getColumnName() {
		return columnName;
	}

	public String getName() {
		return this.name;
	}

	public byte getDefType() {
		return defType;
	}

}
