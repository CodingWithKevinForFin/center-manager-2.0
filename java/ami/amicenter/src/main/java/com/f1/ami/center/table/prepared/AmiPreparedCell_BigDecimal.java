package com.f1.ami.center.table.prepared;

import java.math.BigDecimal;

import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTable;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_BigDecimal;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiPreparedCell_BigDecimal extends AmiPreparedCell {
	private BigDecimal value;

	public AmiPreparedCell_BigDecimal(AmiColumn column, boolean includeReserved) {
		super(column, includeReserved);
	}
	@Override
	public void setLong(long value) {
		this.value = BigDecimal.valueOf(value);
	}
	@Override
	public void setDouble(double value) {
		this.value = BigDecimal.valueOf(value);
	}
	@Override
	public void setString(String value) {
		this.value = new BigDecimal(value);
	}
	@Override
	public void setComparable(Comparable value) {
		this.value = Caster_BigDecimal.INSTANCE.cast(value);
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
		return AmiTable.TYPE_BIGDEC;
	}
	@Override
	public boolean setOn(AmiRowImpl target, CalcFrameStack fs) {
		return this.column.setComparable(target, value, fs);
	}
	@Override
	public boolean isEqual(AmiRowImpl sink) {
		return OH.eq(this.column.getComparable(sink), value);
	}

}
