package com.f1.ami.center.table.prepared;

import java.util.Map;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiColumnImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiPreparedCell implements Map.Entry<String, Object> {
	private long revision;
	final protected AmiColumn column;
	private boolean canSet = true;

	public AmiPreparedCell(AmiColumn column, boolean includeReserved) {
		this.column = column;
		this.canSet = includeReserved || column.getReservedType() != AmiColumnImpl.RESERVED;
	}
	public abstract void setLong(long value);
	public abstract void setDouble(double value);
	public abstract void setString(String value);
	public abstract void setComparable(Comparable value);
	public abstract void setNull();
	public abstract long getLong();
	public abstract double getDouble();
	public abstract String getString();
	public abstract Comparable getComparable();
	public abstract boolean getIsNull();
	public abstract byte getType();
	public abstract boolean setOn(AmiRowImpl target, CalcFrameStack fs);
	public abstract boolean isEqual(AmiRowImpl sink);

	final public AmiColumn getColumn() {
		return this.column;
	}
	final public long getRevision() {
		return revision;
	}
	final public boolean setRevision(long revision) {
		if (this.revision == revision)
			return false;
		this.revision = revision;
		return true;
	}

	public boolean canSet() {
		return canSet;
	}

	@Override
	public String getKey() {
		return this.getColumn().getName();
	}
	@Override
	public Object getValue() {
		return this.getComparable();
	}
	@Override
	public Object setValue(Object value) {
		//This doesn't support replacing the existing value
		throw new UnsupportedOperationException();
	}
}
