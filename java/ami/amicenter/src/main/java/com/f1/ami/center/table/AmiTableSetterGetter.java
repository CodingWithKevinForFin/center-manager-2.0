package com.f1.ami.center.table;

import com.f1.utils.structs.table.columnar.ColumnarColumn;

public interface AmiTableSetterGetter<T extends ColumnarColumn<?>> {

	public String getString(T column, AmiRowImpl colpos);
	public long getLong(T column, AmiRowImpl row);
	public double getDouble(T column, AmiRowImpl row);
	public Comparable<?> getComparable(T column, AmiRowImpl row);

	public boolean setString(T column, AmiRowImpl row, String value);
	public boolean setLong(T column, AmiRowImpl row, long value);
	public boolean setDouble(T column, AmiRowImpl row, double value);
	public boolean setComparable(T column, AmiRowImpl row, Comparable value);
	public boolean isPrimitive();
	public Object getDefaultValue();

}
