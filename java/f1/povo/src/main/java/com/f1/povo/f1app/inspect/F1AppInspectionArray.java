package com.f1.povo.f1app.inspect;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.FA.IN.IA")
public interface F1AppInspectionArray extends F1AppInspectionEntity {

	@PID(11)
	public byte getArrayType();
	public void setArrayType(byte fieldTypes);

	//will be packed according to type, if type is TYPE_OBJECT then will be int
	@PID(12)
	public byte[] getValues();
	public void setValues(byte[] values);

	@PID(13)
	public int getLength();
	public void setLength(int length);

	@PID(14)
	public int getComponentType();
	public void setComponentType(int className);
}
