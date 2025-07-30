package com.f1.ami.center.table.prepared;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Short;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_Short extends AmiPreparedCell {
	private short value;
	boolean isNull = false;

	public AmiPreparedCell_Short(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = (short) value;
		this.isNull = false;
	}
	@Override
	public void setDouble(double value) {
		this.value = (short) value;
		this.isNull = false;
	}
	@Override
	public void setString(String value) {
		if (value == null)
			this.isNull = true;
		else {
			this.value = SH.parseShort(value);
			this.isNull = false;
		}
	}
	@Override
	public void setComparable(Comparable value) {
		if (value == null)
			this.isNull = true;
		else {
			this.value = Caster_Short.INSTANCE.cast(value);
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
		return isNull ? null : SH.toString(value);
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
		return AmiTable.TYPE_SHORT;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack session) {
		if (isNull)
			return this.column.setNull(target, session);
		else
			return this.column.setLong(target, value, session);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		boolean n2 = this.column.getIsNull(sink);
		if (isNull || n2)
			return isNull == n2;
		return this.column.getLong(sink) == value;
	}

	private long revision;

}
