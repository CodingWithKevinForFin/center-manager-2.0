package com.f1.povo.f1app.inspect;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.IN.IO")
public interface F1AppInspectionObject extends F1AppInspectionEntity {

	@PID(2)
	public int getClassName();
	public void setClassName(int className);

	@PID(3)
	public int[] getFieldNames();
	public void setFieldNames(int fieldNames[]);

	@PID(6)
	public byte[] getFieldTypes();
	public void setFieldTypes(byte fieldTypes[]);

	@PID(4)
	public long[] getFieldValues();
	public void setFieldValues(long fieldValues[]);

}
