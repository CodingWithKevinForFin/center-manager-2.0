package com.f1.vortexcommon.msg.eye;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VO.MDF")
public interface VortexEyeMetadataField extends VortexEyeEntity {

	byte VALUE_TYPE_STRING = 1;
	byte VALUE_TYPE_DOUBLE = 2;
	byte VALUE_TYPE_INT = 3;
	byte VALUE_TYPE_BOOLEAN = 4;
	byte VALUE_TYPE_ENUM = 5;
	String BOOLEAN_TRUE = "1";
	String BOOLEAN_FALSE = "0";

	@PID(10)
	public long getTargetTypes();
	public void setTargetTypes(long targetType);

	@PID(11)
	public byte getValueType();
	public void setValueType(byte targetType);

	@PID(12)
	public boolean getRequired();
	public void setRequired(boolean required);

	@PID(13)
	public String getKeyCode();
	public void setKeyCode(String key);

	@PID(14)
	public String getTitle();
	public void setTitle(String title);

	//only for string types
	@PID(15)
	public Byte getMaxLength();
	public void setMaxLength(Byte maxLength);

	//only for Enum types
	@PID(16)
	public Map<String, String> getEnums();
	public void setEnums(Map<String, String> enums);

	@PID(17)
	public String getDescription();
	public void setDescription(String description);

	//only for numbers (int, double)
	@PID(18)
	public Double getMaxValue();
	public void setMaxValue(Double maxValue);

	//only for numbers (int, double)
	@PID(19)
	public Double getMinValue();
	public void setMinValue(Double maxValue);

	@Override
	public VortexEyeMetadataField clone();
}
