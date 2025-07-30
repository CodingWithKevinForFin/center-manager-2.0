package com.f1.vortexcommon.msg.agent;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.AF")
public interface VortexAgentFile extends PartialMessage, VortexAgentEntity {

	short FILE = 1;
	short DIRECTORY = 2;
	short READABLE = 4;
	short WRITEABLE = 8;
	short EXECUTABLE = 16;
	short HIDDEN = 32;
	short ASCII = 64;
	short DELETED = 128;
	short DATA_DEFPLATED = 256;

	byte PID_DATA = 1;
	byte PID_MODIFIED_TIME = 2;
	byte PID_SIZE = 3;
	byte PID_PATH = 4;
	byte PID_CHECKSUM = 5;
	byte PID_MASK = 6;
	byte PID_DATA_OFFSET = 13;
	byte PID_SEARCH_OFFSETS = 14;

	@PID(PID_DATA)
	public byte[] getData();
	public void setData(byte data[]);

	public boolean askExistsData();

	@PID(PID_MODIFIED_TIME)
	public long getModifiedTime();
	public void setModifiedTime(long modifiedTime);

	@PID(PID_SIZE)
	public long getSize();
	public void setSize(long size);

	@PID(PID_PATH)
	public String getPath();
	public void setPath(String path);

	@PID(PID_CHECKSUM)
	public long getChecksum();
	public void setChecksum(long checksum);
	public boolean askExistsChecksum();

	@PID(PID_MASK)
	public short getMask();
	public void setMask(short type);

	@PID(PID_DATA_OFFSET)
	public long getDataOffset();
	public void setDataOffset(long dataOffset);

	@PID(PID_SEARCH_OFFSETS)
	public Map<String, long[]> getSearchOffsets();
	public void setSearchOffsets(Map<String, long[]> searchOffsets);

}
