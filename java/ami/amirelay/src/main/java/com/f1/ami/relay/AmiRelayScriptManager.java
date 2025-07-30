package com.f1.ami.relay;

import java.util.List;
import java.util.TimeZone;

import com.f1.ami.amicommon.AmiScriptDerivedCellParser;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.functions.AmiWebFunctionEval;
import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.amicommon.functions.AmiWebFunctionIsInstanceOf;
import com.f1.ami.amicommon.functions.AmiWebFunctionStrClassName;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiScriptMemberMethods;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.extern.PythonExtern;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;

public class AmiRelayScriptManager implements AmiService {

	final private AmiRelayState state;
	final private ContainerTools tools;
	final private SqlProcessor processor;
	final private AmiDebugManager debugManager = new AmiRelayDebugManager();
	private int defaultTimeout = (int) DerivedCellTimeoutController.DEFAULT_TIMEOUT;
	//	final private DerivedCellTimeoutController timeoutController = new DerivedCellTimeoutController(10000);
	private BasicMethodFactory methodFactory;

	public AmiRelayScriptManager(AmiRelayState state, ContainerTools tools) {
		this.state = state;
		this.tools = tools;
		this.defaultTimeout = AmiUtils.getDefaultTimeout(tools);
		BasicMethodFactory methodFactory = new BasicMethodFactory(null);
		AmiUtils.addTypes(methodFactory);
		methodFactory.addFactoryManager(AmiUtils.METHOD_FACTORY);

		SqlProcessor sp = new SqlProcessor();
		List<AmiWebFunctionFactory> funcs = CH.l(AmiUtils.getFunctions());
		funcs.add(new AmiWebFunctionIsInstanceOf.Factory(methodFactory));
		funcs.add(new AmiWebFunctionStrClassName.Factory(methodFactory));
		funcs.add(new AmiWebFunctionEval.Factory(sp.getParser(), methodFactory));
		for (AmiWebFunctionFactory f : funcs)
			methodFactory.addFactory(f);

		AmiScriptMemberMethods.registerMethods(debugManager, methodFactory);
		methodFactory.addVarType("UTC", DateMillis.class);
		methodFactory.addVarType("UTCN", DateNanos.class);
		this.methodFactory = methodFactory;

		ExpressionParser ep = sp.getExpressionParser();
		BasicExternFactoryManager efm = new BasicExternFactoryManager();
		efm.addLanguage("python", new PythonExtern());
		sp.setParser(new AmiScriptDerivedCellParser(ep, sp, tools, efm, true));
		sp.getExpressionParser().setAllowSqlInjection(true);
		this.processor = sp;
	}

	public DerivedCellCalculatorExpression prepareSql(String sql, com.f1.base.CalcTypes types) {
		return processor.toCalc(sql, new ChildCalcTypesStack(state.createStackFrame(), types));
	}

	@Override
	public MethodFactoryManager getMethodFactory() {
		return methodFactory;
	}

	@Override
	public SqlProcessor getSqlProcessor() {
		return processor;
	}

	@Override
	public AmiDebugManager getDebugManager() {
		return this.debugManager;
	}

	@Override
	public String getformatDate(String format, long time, String timezone) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getformatDate(String format, DateMillis time, String timezone) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getformatDate(String format, DateNanos time, String timezone) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TimeZone getTimezone() {
		throw new UnsupportedOperationException();
	}
	@Override
	public String getTimezoneId() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getformatDate(String format, long time, CalcFrameStack sf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getformatDate(String format, DateMillis time, CalcFrameStack sf) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getformatDate(String format, DateNanos time, CalcFrameStack sf) {
		throw new UnsupportedOperationException();
	}

}
