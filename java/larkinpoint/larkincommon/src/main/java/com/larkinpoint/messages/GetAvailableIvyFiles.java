package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;




@VID("LP.IV.FTP")
public interface  GetAvailableIvyFiles extends PartialMessage, Lockable {
	

		byte PID_GET_FILES = 21;
		

		@PID(PID_GET_FILES)
		public byte getGetFiles();
		public void setGetFiles(byte Trigger);

}
