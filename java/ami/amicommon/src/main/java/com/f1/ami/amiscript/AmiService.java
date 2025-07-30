package com.f1.ami.amiscript;

import java.util.TimeZone;

import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AmiService {

	MethodFactoryManager getMethodFactory();
	SqlProcessor getSqlProcessor();
	//	com.f1.base.Types getGlobalVarTypes();
	//	Map<String, Object> getGlobalVars();
	//	DerivedCellTimeoutController createTimeoutController(int timeoutMs);
	AmiDebugManager getDebugManager();
	//	int getDefaultTimeoutMs();
	TimeZone getTimezone();
	String getTimezoneId();

	String getformatDate(String format, long time, CalcFrameStack sf);
	String getformatDate(String format, DateMillis time, CalcFrameStack sf);
	String getformatDate(String format, DateNanos time, CalcFrameStack sf);

	String getformatDate(String format, long time, String timezone);
	String getformatDate(String format, DateMillis time, String timezone);
	String getformatDate(String format, DateNanos time, String timezone);
}
