package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.AMIAX")
public interface AmiRelayLogoutMessage extends AmiRelayMessage {

	@PID(9)
	boolean getCleanLogout();
	void setCleanLogout(boolean cleanLogout);

}
