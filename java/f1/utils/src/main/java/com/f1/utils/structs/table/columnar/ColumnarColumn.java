package com.f1.utils.structs.table.columnar;

import java.util.Iterator;

import com.f1.base.Caster;
import com.f1.base.Column;
import com.f1.utils.OH;

public abstract class ColumnarColumn<T> implements Column {

	protected final ColumnsTable table;
	private int location;
	final private Class<T> type;
	final private Caster<T> caster;
	private String id;
	final protected boolean allowNull;
	final protected boolean noNull;
	final private byte basicType;

	public ColumnarColumn(ColumnsTable table2, int location, Caster<T> type, String id, boolean allowNull) {
		this.allowNull = allowNull;
		this.noNull = !allowNull;
		this.table = table2;
		this.location = location;
		this.type = type.getCastToClass();
		this.caster = type;
		this.basicType = OH.getBasicType(this.type);
		if (id == null)
			throw new NullPointerException("id");
		this.id = id;
	}
	public ColumnarColumn(ColumnsTable table2, int location, Class<T> type, String id, boolean allowNull) {
		this.allowNull = allowNull;
		this.noNull = !allowNull;
		this.table = table2;
		this.location = location;
		this.type = type;
		this.caster = OH.getCaster(type);
		this.basicType = OH.getBasicType(type);
		if (id == null)
			throw new NullPointerException("id");
		this.id = id;
	}
	@Override
	public int size() {
		return table.getSize();
	}

	@Override
	public Iterator iterator() {
		return null;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public Caster<T> getTypeCaster() {
		return this.caster;
	}

	@Override
	public ColumnsTable getTable() {
		return table;
	}

	@Override
	public int getLocation() {
		return location;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public T getValue(int location) {
		return getValueAtArrayIndex(table.mapRowNumToIndex(location));
	}

	public T getValue(ColumnarRow location) {
		return getValueAtArrayIndex(location.getArrayIndex());
	}
	public boolean setValue(ColumnarRow location, T value) {
		return OH.ne(setValueAtArrayIndex(location.getArrayIndex(), value), value);
	}

	public void setValue(int location, Object value) {
		setValueAtArrayIndex(table.mapRowNumToIndex(location), (T) value);
	}
	protected void setLocation(int i) {
		this.location = i;
	}

	abstract protected void clearData();

	final public boolean isNull(int rowNumber) {
		if (noNull)
			return false;
		return isNullAtArrayIndex(table.mapRowNumToIndex(rowNumber));
	}
	final public boolean isNull(ColumnarRow rowNumber) {
		if (noNull)
			return false;
		return isNullAtArrayIndex(rowNumber.getArrayIndex());
	}
	final public boolean setNull(int rowNumber) {
		if (noNull)
			throw new NullPointerException();
		return setNullAtArrayIndex(table.mapRowNumToIndex(rowNumber));
	}
	final public boolean setNull(ColumnarRow row) {
		if (noNull)
			throw new NullPointerException();
		return setNullAtArrayIndex(row.getArrayIndex());
	}

	abstract protected boolean setNullAtArrayIndex(int index);
	abstract protected boolean isNullAtArrayIndex(int index);
	abstract protected T getValueAtArrayIndex(int index);
	abstract protected T setValueAtArrayIndex(int index, T value);
	abstract protected void ensureCapacity(int size);
	abstract protected void setValues(Object valuesArray, long[] nullsMasks);
	abstract public Object getValues();
	abstract public Object getValuesCloned();
	abstract public long[] getValueNullsMasks();
	public byte getBasicType() {
		return this.basicType;
	}
	abstract public StringBuilder toString(ColumnarRow row, StringBuilder sink);

	public void setValuesAndNulls(Object valuesArray, long[] nullsMasks) {
		this.setValues(valuesArray, nullsMasks);
	}
	@Override
	public String toString() {
		return "[id=" + id + ", location=" + location + ", type=" + type + "]";
	}
	public boolean getAllowNull() {
		return allowNull;
	}
	public void setId(String Id) {
		this.id = Id;
	}
	public void copyValue(ColumnarRow source, ColumnarRow target) {
		setValue(target, getValue(source));
	}

	public void onRemoved() {
	}
	abstract public long getMemorySize();
}
