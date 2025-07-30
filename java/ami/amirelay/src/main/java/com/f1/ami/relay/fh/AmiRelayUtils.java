package com.f1.ami.relay.fh;

public class AmiRelayUtils {

	static public String toStatusString(int status) {
		switch (status) {

			case AmiFH.STATUS_STARTED:
				return "STARTED";
			case AmiFH.STATUS_STOPPED:
				return "STOPPED";
			case AmiFH.STATUS_FAILED:
				return "FAILED";
			case AmiFH.STATUS_STARTING:
				return "STARTING";
			case AmiFH.STATUS_STOPPING:
				return "STOPPING";
			case AmiFH.STATUS_START_FAILED:
				return "START_FAILED";
			case AmiFH.STATUS_STOP_FAILED:
				return "STOP_FAILED";
		}
		return "UNKNOWN_STATUS_" + status;
	}
}
