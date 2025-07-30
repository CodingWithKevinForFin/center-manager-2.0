package com.f1.ami.center.table;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiColumnWrapper implements AmiColumn {

	private AmiColumn inner;

	public AmiColumn getInner() {
		return inner;
	}

	public void setInner(AmiColumn inner) {
		if (this.inner != null)
			throw new IllegalStateException();
		this.inner = inner;
	}

	public AmiTable getAmiTable() {
		return inner.getAmiTable();
	}

	public String getName() {
		return inner.getName();
	}

	public byte getAmiType() {
		return inner.getAmiType();
	}

	public int getLocation() {
		return inner.getLocation();
	}

	public String getString(AmiRow colpos) {
		return inner.getString(colpos);
	}

	public long getLong(AmiRow row) {
		return inner.getLong(row);
	}

	public double getDouble(AmiRow row) {
		return inner.getDouble(row);
	}

	public boolean getIsNull(AmiRow row) {
		return inner.getIsNull(row);
	}

	public boolean setString(AmiRow row, String value, CalcFrameStack session) {
		return inner.setString(row, value, session);
	}

	public boolean setLong(AmiRow row, long value, CalcFrameStack session) {
		return inner.setLong(row, value, session);
	}

	public boolean setDouble(AmiRow row, double value, CalcFrameStack session) {
		return inner.setDouble(row, value, session);
	}

	public boolean setNull(AmiRow row, CalcFrameStack session) {
		return inner.setNull(row, session);
	}

	public boolean copyToFrom(AmiRow toRow, AmiColumn fromCol, AmiRow fromRow, CalcFrameStack session) {
		return inner.copyToFrom(toRow, fromCol, fromRow, session);
	}

	public Comparable getComparable(AmiRowImpl row) {
		return inner.getComparable(row);
	}

	@Override
	public boolean areEqual(AmiRow row, AmiColumn col2, AmiRow row2) {
		return inner.areEqual(row, col2, row2);
	}

	@Override
	public boolean setComparable(AmiRow amiRowImpl, Comparable value, CalcFrameStack session) {
		return inner.setComparable(amiRowImpl, value, session);
	}

	@Override
	public Comparable getComparable(AmiRow row) {
		return inner.getComparable(row);
	}

	@Override
	public boolean getIsOnDisk() {
		return this.inner.getIsOnDisk();
	}

	@Override
	public boolean getAllowNull() {
		return inner.getAllowNull();
	}

	@Override
	public long getColumnPositionMask0() {
		return inner.getColumnPositionMask0();
	}

	@Override
	public long getColumnPositionMask64() {
		return inner.getColumnPositionMask64();
	}

	@Override
	public byte getReservedType() {
		return this.inner.getReservedType();
	}

}
