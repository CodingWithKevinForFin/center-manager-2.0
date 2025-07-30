package com.f1.ami.web;

import java.util.logging.Logger;

public class AmiWebActivityLogger {
	private static final Logger statsLogger = Logger.getLogger("AMI_ACTIVITY_TRACKER");
	//	private static final Logger statsLogger = LH.get();

	public static void fillBasicInfo(StringBuilder sink, String username, String sessionID) {
		sink.append(username).append("|").append(sessionID).append("|");
	}

	public static void logPanel(String username, String sessionID, boolean isVisible, String ari) {
		StringBuilder sb = new StringBuilder();
		fillBasicInfo(sb, username, sessionID);
		String viz = isVisible ? "PANEL_VISIBLE" : "PANEL_HIDDEN";
		sb.append(viz).append("|").append(ari);
		statsLogger.info(sb.toString());
	}
	public static void logScript(boolean verbose, String username, String sessionID, String cbName, String ari, String params) {
		StringBuilder sb = new StringBuilder();
		fillBasicInfo(sb, username, sessionID);
		sb.append("CALLBACK|").append(ari).append("|").append(cbName);
		if (verbose) {
			sb.append("|").append(params);
		}
		statsLogger.info(sb.toString());
	}
}
