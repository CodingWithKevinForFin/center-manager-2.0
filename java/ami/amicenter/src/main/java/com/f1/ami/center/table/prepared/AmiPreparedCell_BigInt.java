package com.f1.ami.center.table.prepared;

import java.math.BigInteger;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_BigInteger;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_BigInt extends AmiPreparedCell {
	private BigInteger value;

	public AmiPreparedCell_BigInt(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = BigInteger.valueOf(value);
	}
	@Override
	public void setDouble(double value) {
		this.value = BigInteger.valueOf((long) value);
	}
	@Override
	public void setString(String value) {
		this.value = new BigInteger(value);
	}
	@Override
	public void setComparable(Comparable value) {
		this.value = Caster_BigInteger.INSTANCE.cast(value);
	}
	@Override
	public void setNull() {
		this.value = null;
	}
	@Override
	public long getLong() {
		return value.longValue();
	}
	@Override
	public double getDouble() {
		return value.doubleValue();
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
		return AmiTable.TYPE_BIGINT;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack session) {
		return this.column.setComparable(target, value, session);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		return OH.eq(this.column.getComparable(sink), value);
	}
}
