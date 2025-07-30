package com.f1.ami.center.table.prepared;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_String extends AmiPreparedCell {
	private String value;

	public AmiPreparedCell_String(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = SH.toString(value);
	}
	@Override
	public void setDouble(double value) {
		this.value = SH.toString(value);
	}
	@Override
	public void setString(String value) {
		this.value = value;
	}
	@Override
	public void setComparable(Comparable value) {
		this.value = Caster_String.INSTANCE.cast(value);
	}
	@Override
	public void setNull() {
		this.value = null;
	}
	@Override
	public long getLong() {
		return SH.parseLong(value);
	}
	@Override
	public double getDouble() {
		return SH.parseDouble(value);
	}
	@Override
	public String getString() {
		return value;
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
		return AmiTable.TYPE_STRING;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack session) {
		return this.column.setString(target, value, session);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		return OH.eq(this.column.getString(sink), value);
	}

}
