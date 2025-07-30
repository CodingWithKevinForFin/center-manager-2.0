package com.f1.vortexcommon.msg.agent;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("F1.VA.TFE")
public interface VortexAgentTailFileEvent extends PartialMessage {
	byte TYPE_OPENED = 1;
	byte TYPE_RESET = 2;
	byte TYPE_REMOVED = 3;
	byte TYPE_DATA = 3;

	byte PID_TYPE = 1;
	byte PID_DATA = 2;
	byte PID_FILE_ID = 3;
	byte PID_FILE_POSITION = 4;
	byte PID_POS_DUP = 5;
	byte PID_FILE_NAME = 6;
	byte PID_FROM_FILE_NAME = 7;
	byte PID_PARTIAL = 8;

	@PID(PID_TYPE)
	public byte getType();
	public void setType(byte type);

	@PID(PID_DATA)
	public byte[] getData();
	public void setData(byte[] data);

	@PID(PID_FILE_ID)
	public long getFileId();
	public void setFileId(long Id);

	@PID(PID_FILE_POSITION)
	public long getFilePosition();
	public void setFilePosition(long fileOffset);

	@PID(PID_POS_DUP)
	public boolean getPosDup();
	public void setPosDup(boolean posDup);

	@PID(PID_FILE_NAME)
	public String getFileName();
	public void setFileName(String fileName);

	// supplied in the open action, if the agent notices this tracked file has the same checksum as a preexisting file
	@PID(PID_FROM_FILE_NAME)
	public String getFromFileName();
	public void setFromFileName(String fileName);

	@PID(PID_PARTIAL)
	public boolean getPartial();
	public void setPartial(boolean posDup);
}
