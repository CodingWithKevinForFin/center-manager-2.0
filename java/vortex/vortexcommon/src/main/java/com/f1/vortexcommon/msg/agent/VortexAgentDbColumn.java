package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.DC")
public interface VortexAgentDbColumn extends VortexAgentDbEntity {

	byte TYPE_BOOLEAN = 1;
	byte TYPE_INT = 2;
	byte TYPE_CHAR = 3;
	byte TYPE_VARCHAR = 4;
	byte TYPE_FLOAT = 5;
	byte TYPE_FIXEDPOINT = 6;

	byte TYPE_BLOB = 20;

	byte TYPE_ENUM = 40;
	byte TYPE_SET = 41;

	byte TYPE_DATE = 60;
	byte TYPE_DATETIME = 61;
	byte TYPE_TIMESTAMP = 62;
	byte TYPE_TIME = 63;
	byte TYPE_YEAR = 64;

	byte TYPE_OTHER = 70;

	byte MASK_NULLABLE = 1;
	byte MASK_UNSIGNED = 2;

	byte PID_NAME = 3;
	byte PID_TYPE = 4;
	byte PID_DESCRIPTION = 5;
	byte PID_MASK = 6;
	byte PID_SIZE = 7;
	byte PID_PRECISION = 8;
	byte PID_SCALE = 9;
	byte PID_PERMISSIBLE_VALUES = 10;
	byte PID_POSITION = 11;
	byte PID_COMMENTS = 12;
	byte PID_TABLE_ID = 14;

	@PID(PID_NAME)
	public String getName();
	public void setName(String time);

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte processUid);

	@PID(PID_DESCRIPTION)
	public String getDescription();
	public void setDescription(String description);

	@PID(PID_MASK)
	public byte getMask();
	public void setMask(byte mask);

	@PID(PID_SIZE)
	public long getSize();
	public void setSize(long maxBytes);

	@PID(PID_PRECISION)
	public short getPrecision();
	public void setPrecision(short maxBytes);

	@PID(PID_SCALE)
	public short getScale();
	public void setScale(short maxBytes);

	@PID(PID_PERMISSIBLE_VALUES)
	public String getPermissibleValues();
	public void setPermissibleValues(String permissibleValues);

	@PID(PID_POSITION)
	public int getPosition();
	public void setPosition(int position);

	@PID(PID_COMMENTS)
	public String getComments();
	public void setComments(String comments);

	@PID(PID_TABLE_ID)
	public long getTableId();
	public void setTableId(long tableId);

	//@PID(15)
	//public String getTableName();
	//public void setTableName(String tableName);
}
