package com.f1.utils.flogger.impl;

import com.f1.utils.SH;
import com.f1.utils.flogger.Flogger;

public class FloggerUtils {

	public static String getLevelAsString(int level) {
		switch (level) {
			case Flogger.ALL :
				return "ALL";
			case Flogger.TRACE :
				return "TRC";
			case Flogger.FINEST :
				return "FNS";
			case Flogger.FINER :
				return "FNR";
			case Flogger.FINE :
				return "FNE";
			case Flogger.DEBUG :
				return "DBG";
			case Flogger.CONFIG :
				return "CFG";
			case Flogger.INFO :
				return "INF";
			case Flogger.WARNING :
				return "WRN";
			case Flogger.ERROR :
				return "ERR";
			case Flogger.SEVERE :
				return "SVR";
			case Flogger.FATAL :
				return "FTL";
			case Flogger.OFF :
				return "OFF";
			default :
				return "Level-" + SH.toString(level);
		}
	}

	public static String getLevelAsText(int level) {
		switch (level) {
			case Flogger.ALL :
				return "ALL";
			case Flogger.TRACE :
				return "TRACE";
			case Flogger.FINEST :
				return "FINEST";
			case Flogger.FINER :
				return "FINER";
			case Flogger.FINE :
				return "FINE";
			case Flogger.DEBUG :
				return "DEBUG";
			case Flogger.CONFIG :
				return "CONFIG";
			case Flogger.INFO :
				return "INFO";
			case Flogger.WARNING :
				return "WARNING";
			case Flogger.ERROR :
				return "ERROR";
			case Flogger.SEVERE :
				return "SEVERE";
			case Flogger.FATAL :
				return "FATAL";
			case Flogger.OFF :
				return "OFF";
			default :
				return "Level-" + SH.toString(level);
		}
	}

	static public StackTraceElement getStackTraceElement() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (int i = 2; i < stackTrace.length; i++)
			if (!stackTrace[i].getClassName().startsWith(Flogger.class.getPackage().getName()))
				return stackTrace[i];
		return stackTrace[2];
	}
}
