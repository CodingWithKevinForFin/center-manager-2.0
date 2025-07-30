package com.f1.ami.center.table.prepared;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.base.Bytes;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Bytes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_Binary extends AmiPreparedCell {
	private Bytes value;

	public AmiPreparedCell_Binary(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = null;
	}
	@Override
	public void setDouble(double value) {
		this.value = null;
	}
	@Override
	public void setString(String value) {
		this.value = value == null ? null : new Bytes(value.getBytes());
	}
	@Override
	public void setComparable(Comparable value) {
		this.value = Caster_Bytes.INSTANCE.cast(value);
	}
	@Override
	public void setNull() {
		this.value = null;
	}
	@Override
	public long getLong() {
		return Long.MIN_VALUE;
	}
	@Override
	public double getDouble() {
		return Double.NaN;
	}
	@Override
	public String getString() {
		return OH.toString(value);
	}
	@Override
	public Comparable getComparable() {
		return value;
	}
	@Override
	public boolean getIsNull() {
		return value == null;
	}
	@Override
	public byte getType() {
		return AmiTable.TYPE_BINARY;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack session) {
		return this.column.setComparable(target, value, session);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		return OH.eq(this.column.getComparable(sink), value);
	}

	private long revision;

}
