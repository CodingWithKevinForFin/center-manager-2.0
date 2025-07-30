package com.f1.ami.center.hdb;

public class AmiHdbSchema_Column {

	final private byte amiType;
	final private String name;
	final private int location;
	final private Class type;

	public AmiHdbSchema_Column(AmiHdbColumn column) {
		this.amiType = column.getAmiType();
		this.type = column.getType();
		this.name = column.getName();
		this.location = column.getLocation();
	}

	public String getName() {
		return name;
	}
	public Class getType() {
		return this.type;
	}

	public byte getAmiType() {
		return this.amiType;
	}

}
