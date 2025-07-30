package com.f1.ami.relay.fh.iress;

import java.util.logging.Logger;

import com.f1.utils.LH;
import com.feedos.api.core.Session;
import com.feedos.api.core.SessionObserver;

public class IressSessionObserver implements SessionObserver {

	private boolean logHeartbeats = false;;

	public IressSessionObserver(boolean logHeartbeats) {
		this.logHeartbeats = logHeartbeats;
	}

	private static final Logger Log = LH.get();

	@Override
	public void adminMessage(Session arg0, boolean arg1, String arg2, String arg3, String arg4) {
		LH.info(Log, "Admin Message: " + arg2);

	}

	@Override
	public void closeComplete(Session arg0) {
		LH.info(Log, "Session closed");

	}

	@Override
	public void closeInProgress(Session arg0) {
		LH.info(Log, "Session closing");

	}

	@Override
	public void heartbeat(Session arg0, long arg1) {
		if (logHeartbeats)
			LH.info(Log, "heartbeat: " + arg1);

	}

	@Override
	public void sessionOpened(Session arg0, int arg1, int arg2, int arg3) {
		LH.info(Log, "Session opened");

	}

}
