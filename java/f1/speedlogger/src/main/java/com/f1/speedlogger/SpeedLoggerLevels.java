/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.speedlogger;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SpeedLoggerLevels {
	public static final int ALL = 0;
	public static final int TRACE = 10;
	public static final int FINEST = 20;
	public static final int FINER = 30;
	public static final int FINE = 40;
	public static final int DEBUG = 50;
	public static final int CONFIG = 60;
	public static final int INFO = 70;
	public static final int WARNING = 80;
	public static final int ERROR = 90;
	public static final int SEVERE = 100;
	public static final int FATAL = 110;
	public static final int OFF = 120;

	public static final String LABEL_ALL = "ALL";
	public static final String LABEL_TRACE = "TRC";
	public static final String LABEL_FINEST = "FNS";
	public static final String LABEL_FINER = "FNR";
	public static final String LABEL_FINE = "FNE";
	public static final String LABEL_DEBUG = "DBG";
	public static final String LABEL_CONFIG = "CFG";
	public static final String LABEL_INFO = "INF";
	public static final String LABEL_WARNING = "WRN";
	public static final String LABEL_ERROR = "ERR";
	public static final String LABEL_SEVERE = "SVR";
	public static final String LABEL_FATAL = "FTL";
	public static final String LABEL_OFF = "OFF";
	public static final String FULL_LABEL_ALL = "ALL";
	public static final String FULL_LABEL_TRACE = "TRACE";
	public static final String FULL_LABEL_FINEST = "FINEST";
	public static final String FULL_LABEL_FINER = "FINER";
	public static final String FULL_LABEL_FINE = "FINE";
	public static final String FULL_LABEL_DEBUG = "DEBUG";
	public static final String FULL_LABEL_CONFIG = "CONFIG";
	public static final String FULL_LABEL_INFO = "INFO";
	public static final String FULL_LABEL_WARNING = "WARNING";
	public static final String FULL_LABEL_ERROR = "ERROR";
	public static final String FULL_LABEL_SEVERE = "SEVERE";
	public static final String FULL_LABEL_FATAL = "FATAL";
	public static final String FULL_LABEL_OFF = "OFF";

	public static final Map<String, Integer> LABEL_2_LEVELS = new HashMap<String, Integer>();
	public static final Map<Integer, String> LEVELS_2_LABEL = new HashMap<Integer, String>();
	public static final Map<Integer, String> LEVELS_2_LABEL_SORTED = new TreeMap<Integer, String>();

	static {
		LABEL_2_LEVELS.put(LABEL_ALL, ALL);
		LABEL_2_LEVELS.put(LABEL_TRACE, TRACE);
		LABEL_2_LEVELS.put(LABEL_FINEST, FINEST);
		LABEL_2_LEVELS.put(LABEL_FINER, FINER);
		LABEL_2_LEVELS.put(LABEL_FINE, FINE);
		LABEL_2_LEVELS.put(LABEL_DEBUG, DEBUG);
		LABEL_2_LEVELS.put(LABEL_CONFIG, CONFIG);
		LABEL_2_LEVELS.put(LABEL_INFO, INFO);
		LABEL_2_LEVELS.put(LABEL_WARNING, WARNING);
		LABEL_2_LEVELS.put(LABEL_ERROR, ERROR);
		LABEL_2_LEVELS.put(LABEL_SEVERE, SEVERE);
		LABEL_2_LEVELS.put(LABEL_FATAL, FATAL);
		LABEL_2_LEVELS.put(LABEL_OFF, OFF);
		for (Map.Entry<String, Integer> e : LABEL_2_LEVELS.entrySet())
			LEVELS_2_LABEL.put(e.getValue(), e.getKey());
		LABEL_2_LEVELS.put(FULL_LABEL_ALL, ALL);
		LABEL_2_LEVELS.put(FULL_LABEL_TRACE, TRACE);
		LABEL_2_LEVELS.put(FULL_LABEL_FINEST, FINEST);
		LABEL_2_LEVELS.put(FULL_LABEL_FINER, FINER);
		LABEL_2_LEVELS.put(FULL_LABEL_FINE, FINE);
		LABEL_2_LEVELS.put(FULL_LABEL_DEBUG, DEBUG);
		LABEL_2_LEVELS.put(FULL_LABEL_CONFIG, CONFIG);
		LABEL_2_LEVELS.put(FULL_LABEL_INFO, INFO);
		LABEL_2_LEVELS.put(FULL_LABEL_WARNING, WARNING);
		LABEL_2_LEVELS.put(FULL_LABEL_ERROR, ERROR);
		LABEL_2_LEVELS.put(FULL_LABEL_SEVERE, SEVERE);
		LABEL_2_LEVELS.put(FULL_LABEL_FATAL, FATAL);
		LABEL_2_LEVELS.put(FULL_LABEL_OFF, OFF);
		LEVELS_2_LABEL_SORTED.putAll(LEVELS_2_LABEL);
	}

}
