package com.vortex.testtrackagent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommandRunnerState {

	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
	private final SimpleDateFormat dateFormatter1 = new SimpleDateFormat("EEE MMM dd HH:mm");

	public Date parseSystemDate(String date) throws java.text.ParseException {
		return dateFormatter.parse(date);
	}

	public Date parseSystemDateBasicVersion(String date) throws java.text.ParseException {
		return dateFormatter1.parse(date);
	}

}
