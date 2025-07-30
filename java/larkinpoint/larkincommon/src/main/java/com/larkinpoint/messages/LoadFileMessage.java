package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.FL.MSG")
public interface LoadFileMessage extends PartialMessage, Lockable {

	byte PID_LOAD_FILENAME = 21;
	byte PID_LOAD_FILETYPE = 22;
	//	byte PID_LOAD_SYMBOL = 23;

	byte TYPE_CURRENCY = 60;
	byte TYPE_SYMBOL_NAMES = 61;
	byte TYPE_DAILY_OPTION = 62;
	byte TYPE_EXCHANGE_DATA = 63;
	byte TYPE_HIST_VOL = 64;
	byte TYPE_INDEX_DIV = 65;
	byte TYPE_SECURITY_PRICES = 66;
	byte TYPE_STD_OPTIONS = 67;
	byte TYPE_VOL_SURFACE = 68;
	byte TYPE_ZERO_CURVES = 69;

	@PID(PID_LOAD_FILENAME)
	public String getLoadFilename();
	public void setLoadFilename(String Filename);

	@PID(PID_LOAD_FILETYPE)
	public byte getLoadFiletype();
	public void setLoadFiletype(byte Filetype);

}