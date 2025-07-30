package com.f1.ami.center.hdb;

public class AmiHdbIndex {

	final private String name;
	final private AmiHdbColumn column;
	private long totalDiskSize;
	private byte definedBy;

	public AmiHdbIndex(String name, byte definedBy, AmiHdbColumn cols) {
		super();
		this.name = name;
		this.column = cols;
		this.definedBy = definedBy;
	}

	public long getTotalDiskSize() {
		return totalDiskSize;
	}

	public void incTotalDiskSize(long totalDiskSize) {
		this.totalDiskSize += totalDiskSize;
	}

	public String getName() {
		return name;
	}

	public AmiHdbColumn getColumn() {
		return column;
	}

	public void onRowsCleared() {
		this.totalDiskSize = 0;
	}

	public AmiHdbTable getTable() {
		return column.getHistoryTable();
	}

	public byte getDefType() {
		return this.definedBy;
	}
}
