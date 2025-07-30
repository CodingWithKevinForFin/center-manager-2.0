package com.f1.ami.center.procs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.timers.AmiTimer_AmiScript;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProjector;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiStoredProc_AmiScript extends AmiAbstractStoredProc {

	private static final Logger log = LH.get();
	private String script;
	private String argumentsString;
	final private List<AmiFactoryOption> argumentsList = new ArrayList<AmiFactoryOption>();
	final private Map<String, AmiFactoryOption> argumentsByName = new HashMap<String, AmiFactoryOption>();
	final private com.f1.utils.structs.table.stack.BasicCalcTypes argumentTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
	private String argumentEntries[];

	private DerivedCellCalculatorExpression scriptCalc;
	private String onStartupScript;
	private SqlPlanListener planListener;
	private AmiImdbImpl db;

	@Override
	public FlowControl execute(AmiStoredProcRequest request, CalcFrameStack sf) throws Exception {
		List<Object> arguments = request.getArguments();
		CalcFrame t = new BasicCalcFrame(this.argumentTypes);
		for (int i = 0; i < argumentEntries.length; i++)
			t.putValue(argumentEntries[i], arguments.get(i));
		return executeSqlAndReturn(scriptCalc, t, request.getLimitOffset(), request.getLimit(), request.getInvokedBy(), planListener, sf);//TODO: planListener should be sessions
	}
	private FlowControl executeSqlAndReturn(DerivedCellCalculatorExpression sql, CalcFrame t, int limitOffset, int limit, String username, SqlPlanListener planListener,
			CalcFrameStack sf) {
		AmiImdbScriptManager scriptManager = ((AmiImdbImpl) this.getImdb()).getScriptManager();
		Object r;
		try {
			r = scriptManager.executeSql(sql, t, AmiImdbScriptManager.ON_EXECUTE_RETURN_PAUSE, sf.getTimeoutController(), limit + limitOffset, planListener, sf);
		} catch (RuntimeException e) {
			throw e;
		}
		if (r == null)
			return null;
		if (r instanceof FlowControl)
			return (FlowControl) r;//SHOULDN'T WE PUSH SOMETHING ON A PAUSE?
		if (r instanceof Table)
			return new TableReturn(trim((Table) r, limitOffset, limit));
		if (r instanceof List) {
			List l = (List) r;
			if (l.size() == 0)
				return new TableReturn(toSingletonTable("result", "result", (Class) getImdb().getAmiScriptMethodFactory().getDefaultImplementation(r.getClass()), r));
			ArrayList<Table> r2 = new ArrayList<Table>(l.size());
			for (int i = 0; i < l.size(); i++) {
				Object v = l.get(i);
				if (v instanceof Table) {
					r2.add(trim((Table) v, limitOffset, limit));
				} else
					return new TableReturn(toSingletonTable("result", "result", (Class) getImdb().getAmiScriptMethodFactory().getDefaultImplementation(r.getClass()), r));
			}
			return new TableReturn(r2);
		}
		return new TableReturn(toSingletonTable("result", "result", (Class) getImdb().getAmiScriptMethodFactory().getDefaultImplementation(r.getClass()), r));
	}
	private Table trim(Table table, int limitOffset, int limit) {
		SqlProjector.trimTable(table, limitOffset, limit);
		return table;
	}
	@Override
	public List<AmiFactoryOption> getArguments() {
		return argumentsList;
	}

	@Override
	public Class getReturnType() {
		return String.class;
	}

	@Override
	protected void onStartup(CalcFrameStack sf) {
		this.script = getBinding().getOption(String.class, "script");
		this.argumentsString = getBinding().getOption(String.class, "arguments");
		this.planListener = AmiCenterUtils.parseLoggingLevelOption("PROCEDURE " + getBinding().getStoredProcName(), getBinding().getOption(String.class, "logging", null));
		this.db = (AmiImdbImpl) getImdb();
		this.argumentsList.clear();
		this.argumentsByName.clear();
		this.argumentTypes.clear();
		AmiImdbImpl db = (AmiImdbImpl) this.getImdb();
		String[] args = SH.trimStrings(SH.split(',', this.argumentsString));
		this.argumentEntries = new String[args.length];
		for (int i = 0; i < args.length; i++) {
			final String s = args[i];
			final String typeStr = SH.trim(SH.beforeFirst(s, ' ', null));
			if (typeStr == null)
				throw new RuntimeException("Illegal syntax for arguments, expecting type value,...: " + s);
			String nameStr = SH.trim(SH.afterFirst(s, ' ', null));
			boolean noNull;
			if (SH.endsWithIgnoreCase(nameStr, " NONULL")) {
				noNull = true;
				nameStr = SH.trim(SH.beforeLast(nameStr, ' '));
			} else
				noNull = false;
			final Class<?> type;
			if (!AmiUtils.isValidVariableName(nameStr, false, false))
				throw new RuntimeException("Illegal argument name: " + nameStr);
			type = db.getScriptManager().getMethodFactory().forNameNoThrow(typeStr);
			if (type == null)
				throw new RuntimeException("Unknown type in arguments list: " + typeStr);
			AmiFactoryOption afo = new AmiFactoryOption(nameStr, type, noNull);
			CH.putOrThrow(this.argumentsByName, nameStr, afo, "Duplicate argument name: ");
			this.argumentsList.add(afo);
			argumentEntries[i] = nameStr;
			this.argumentTypes.putType(nameStr, type);

		}
		this.onStartupScript = this.getBinding().getOption(Caster_String.INSTANCE, "onStartupScript", null);
		CalcTypes parseVarTypes = AmiTimer_AmiScript.parseVarTypes((AmiImdbImpl) getImdb(), this.getBinding().getOption(Caster_String.INSTANCE, "vars", ""));
		for (String e : parseVarTypes.getVarKeys()) {
			if (this.argumentTypes.getType(e) != null)
				throw new RuntimeException("argument / var name clash: " + e);
		}
		this.argumentTypes.putAll(parseVarTypes);

		try {
			this.scriptCalc = this.getImdb().getScriptManager().prepareSql(this.script, argumentTypes, false, true, sf);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(script, e.getPosition(), "Error with script: " + e.getMessage(), e);
		}
		if (this.onStartupScript != null) {
			try {
				CalcFrame arguments = new BasicCalcFrame(this.argumentTypes);
				this.getImdb().getScriptManager().executeSql(onStartupScript, arguments, null, sf.getLimit(), sf.getSqlPlanListener(), sf);
			} catch (ExpressionParserException e) {
				throw new ExpressionParserException(onStartupScript, e.getPosition(), "Error with onStartupScript: " + e.getMessage(), e);
			}
		}
	};

	@Override
	public void onSchemaChanged(AmiImdbImpl db, CalcFrameStack sf) {
		try {
			this.scriptCalc = this.getImdb().getScriptManager().prepareSql(this.script, argumentTypes, false, true, sf);
		} catch (ExpressionParserException e) {
			throw new ExpressionParserException(script, e.getPosition(), "Error with script: " + e.getMessage(), e);
		}
		super.onSchemaChanged(db, sf);
	}

}
