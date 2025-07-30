package com.f1.ami.center.table.prepared;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Float;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_Float extends AmiPreparedCell {
	private double value;
	boolean isNull = false;

	public AmiPreparedCell_Float(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = value;
		this.isNull = false;
	}
	@Override
	public void setDouble(double value) {
		this.value = value;
		this.isNull = false;
	}
	@Override
	public void setString(String value) {
		if (value == null)
			this.isNull = true;
		else {
			this.value = SH.parseFloat(value);
			this.isNull = false;
		}
	}
	@Override
	public void setComparable(Comparable value) {
		if (value == null)
			this.isNull = true;
		else {
			this.value = Caster_Float.INSTANCE.cast(value);
			this.isNull = false;
		}
	}
	@Override
	public void setNull() {
		this.isNull = true;
	}
	@Override
	public long getLong() {
		if (isNull)
			throw new NullPointerException();
		return (long) value;
	}
	@Override
	public double getDouble() {
		if (isNull)
			throw new NullPointerException();
		return value;
	}
	@Override
	public String getString() {
		return isNull ? null : SH.toString((float) value);
	}
	@Override
	public Comparable getComparable() {
		return isNull ? null : value;
	}
	@Override
	public boolean getIsNull() {
		return isNull;
	}
	@Override
	public byte getType() {
		return AmiTable.TYPE_FLOAT;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack session) {
		if (isNull)
			return this.column.setNull(target, session);
		else
			return this.column.setDouble(target, value, session);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		boolean n2 = this.column.getIsNull(sink);
		if (isNull || n2)
			return isNull == n2;
		return this.column.getDouble(sink) == this.value;
	}

}
