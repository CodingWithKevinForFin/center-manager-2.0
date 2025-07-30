package com.f1.ami.center.timers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.AmiCenterGlobalProcess;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.AmiCenterAutoExecuteItinerary;
import com.f1.ami.center.table.AmiCenterAutoExecuteItineraryListener;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTimer_AmiScript extends AmiAbstractTimer implements AmiCenterAutoExecuteItineraryListener {

	private static final Logger log = LH.get();
	private String script;
	final private BasicCalcTypes valueTypes = new BasicCalcTypes();
	private CalcFrame values;
	private DerivedCellCalculatorExpression scriptCalc;
	private String onStartupScript;
	private AmiImdbImpl db;
	private SqlPlanListener planListener;
	private boolean loggingInfoEnabled = false;

	@Override
	public boolean onTimer(long scheduledTime, AmiCenterProcess process, CalcFrameStack sf) {
		if (this.scriptCalc == null)
			return true;
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		session.setIgnoreReturnTables(true);
		if (loggingInfoEnabled)
			LH.info(log, "Starting timer: ", getBinding().getTimerName());

		Object o;
		session.lock(process, this.planListener);
		try {
			o = db.getScriptManager().executeSql(this.scriptCalc, values, AmiImdbScriptManager.ON_EXECUTE_AUTO_HANDLE, session.createTimeoutController(), AmiConsts.DEFAULT, null,
					sf);
		} finally {
			session.unlock();
		}
		if (o instanceof AmiCenterAutoExecuteItinerary) {
			((AmiCenterAutoExecuteItinerary) o).setListener(this);
			return false;
		} else {
			if (loggingInfoEnabled)
				LH.info(log, "Completed timer: ", getBinding().getTimerName());
			return true;
		}
	}

	@Override
	public void onSchemaChanged(AmiImdb imdb, CalcFrameStack cfs) {
		try {
			this.scriptCalc = this.script == null ? null : this.getImdb().getScriptManager().prepareSql(this.script, this.valueTypes, false, true, cfs);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(script, e.getPosition(), "Error with script: " + e.getMessage(), e);
		}
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.db = (AmiImdbImpl) getImdb();
		this.script = this.getBinding().getOption(Caster_String.INSTANCE, "script", null);
		this.onStartupScript = this.getBinding().getOption(Caster_String.INSTANCE, "onStartupScript", null);
		this.valueTypes.putAll(parseVarTypes((AmiImdbImpl) getImdb(), this.getBinding().getOption(Caster_String.INSTANCE, "vars", "")));
		String option = getBinding().getOption(String.class, "logging", null);
		//		if ("quiet".equalsIgnoreCase(option)) {
		//			this.loggingInfoEnabled = false;
		//			this.planListener = null;
		//		} else if ("info".equalsIgnoreCase(option) || SH.isnt(option)) {
		//			this.loggingInfoEnabled = log.isLoggable(Level.INFO) && this.db.getTimerLoggingOptionEnabled();
		this.planListener = AmiCenterUtils.parseLoggingLevelOption(getBinding().getTimerName(), option);
		this.loggingInfoEnabled = log.isLoggable(Level.INFO) && this.planListener != null && this.db.getTimerLoggingOptionEnabled();
		//		} else
		//			throw new ExpressionParserException("logging option invalid: ", 0, " (valid options: QUIET,INFO)");
		this.values = new BasicCalcFrame(valueTypes);
		for (String e : this.valueTypes.getVarKeys())
			this.values.putValue(e, null);

		if (this.onStartupScript != null && this.getBinding().getIsEnabled()) {
			AmiImdbSession session = AmiCenterUtils.getSession(sf);
			AmiCenterGlobalProcess globalProcess = db.getGlobalProcess();
			session.lock(globalProcess, this.planListener);
			globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_RUN);
			try {
				this.getImdb().getScriptManager().executeSql(onStartupScript, values, session.createTimeoutController(), getBinding().getLimit(), this.planListener, sf);
			} catch (ExpressionParserException e) {
				throw new ExpressionParserException(onStartupScript, e.getPosition(), "Error with onStartupScript: " + e.getMessage(), e);
			} finally {
				globalProcess.setProcessStatus(AmiCenterProcess.PROCESS_IDLE);
				session.unlock();
			}
		}
		try {
			this.scriptCalc = this.script == null ? null : this.getImdb().getScriptManager().prepareSql(this.script, this.valueTypes, false, true, sf);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(script, e.getPosition(), "Error with script: " + e.getMessage(), e);
		}
	}
	public static CalcTypes parseVarTypes(AmiImdbImpl db, String varTypes) {
		BasicCalcTypes r = new BasicCalcTypes();
		if (SH.isnt(varTypes))
			return r;
		String[] args = SH.trimStrings(SH.split(',', varTypes));
		for (int i = 0; i < args.length; i++) {
			final String s = args[i];
			final String typeStr = SH.trim(SH.beforeFirst(s, ' ', null));
			final String nameStr = SH.trim(SH.afterFirst(s, ' ', null));
			final Class<?> type;
			if (!AmiUtils.isValidVariableName(nameStr, false, false))
				throw new RuntimeException("Illegal var name: " + nameStr);
			type = db.getScriptManager().getMethodFactory().forNameNoThrow(typeStr);
			if (type == null)
				throw new RuntimeException("Unknown type in var list: " + typeStr);
			if (r.getType(nameStr) != null)
				throw new RuntimeException("Duplicate var name: " + nameStr);
			r.putType(nameStr, type);
		}
		return r;
	}

	@Override
	public void onAutoExecuteError(AmiCenterAutoExecuteItinerary amiCenterAutoExecuteItinerary, String message, Exception exception) {
		if (loggingInfoEnabled)
			LH.info(log, "Completed timer async with error: ", getBinding().getTimerName());
		getBinding().onTimerCompletedWithError(exception);
	}

	@Override
	public void onAutoExecuteComplete(AmiCenterAutoExecuteItinerary amiCenterAutoExecuteItinerary) {
		if (loggingInfoEnabled)
			LH.info(log, "Completed timer async: ", getBinding().getTimerName());
		getBinding().onTimerCompleted();
	}
}
