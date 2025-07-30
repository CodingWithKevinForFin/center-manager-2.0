package com.f1.ami.center.table;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiScriptDerivedCellParser;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.functions.AmiWebFunctionEval;
import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.amicommon.functions.AmiWebFunctionIsInstanceOf;
import com.f1.ami.amicommon.functions.AmiWebFunctionStrClassName;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiScriptMemberMethods;
import com.f1.ami.center.AmiCenterItineraryProcessor;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.extern.PythonExtern;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.string.ExpressionParser;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.structs.table.derived.BasicExternFactoryManager;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorAssignment;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorBlock;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ThreadSafeMethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiImdbScriptManager {

	private static final Logger log = LH.get();

	final private SqlProcessor processor;
	final private AmiCenterSqlProcessorMutator mutator;
	final private AmiCenterState state;
	final private AmiCenterItineraryProcessor itineraryProcessor;
	final private AmiDebugManager debugManager = new AmiCenterDebugManager();
	final private ThreadSafeMethodFactoryManager methodFactory;
	final private BasicMethodFactory predefinedMethodsFactory;//SYSTEM
	final private BasicMethodFactory declaredMethodsFactory;//CONFIG (from others.amisql)
	final private BasicMethodFactory managedMethodsFactory;//USER (from managed_schema.amksql)
	private TimeZone timezone;

	public AmiImdbScriptManager(AmiCenterState state, ContainerTools tools) {
		this.state = state;
		this.itineraryProcessor = this.state.getItineraryProcessor();
		this.methodFactory = new ThreadSafeMethodFactoryManager();
		this.declaredMethodsFactory = new BasicMethodFactory();
		this.managedMethodsFactory = new BasicMethodFactory();
		this.predefinedMethodsFactory = new BasicMethodFactory();
		AmiUtils.addTypes(predefinedMethodsFactory);

		String disabled = this.state.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_DB_DISABLE_FUNCTIONS);
		String timezoneId = this.state.getTools().getOptional(AmiCommonProperties.PROPERTY_AMI_DEFAULT_USER_TIMEZONE, AmiCommonProperties.DEFAULT_USER_TIMEZONE);
		this.timezone = EH.getTimeZone(timezoneId);
		Set<String> disabledFuncs = SH.is(disabled) ? SH.splitToSet(",", disabled) : Collections.EMPTY_SET;

		SqlProcessor sp = new SqlProcessor();
		sp.setSelectProcessor(new AmiCenterSqlProcessor_Select(sp));
		sp.setDeleteProcessor(new AmiCenterSqlProcessor_Delete(sp));
		sp.setUpdateProcessor(new AmiCenterSqlProcessor_Update(sp));
		sp.setInsertProcessor(new AmiCenterSqlProcessor_Insert(sp));
		//		SqlProcessor sp = new AmiCenterSqlProcessor(db.getState(), methodFactory);
		List<AmiWebFunctionFactory> funcs = CH.l(AmiUtils.getFunctions());
		funcs.add(new AmiCenterFunctionStrEncrypt.Factory(this.state));
		funcs.add(new AmiCenterFunctionStrDecrypt.Factory(this.state));
		funcs.add(new AmiWebFunctionIsInstanceOf.Factory(predefinedMethodsFactory));
		funcs.add(new AmiWebFunctionStrClassName.Factory(predefinedMethodsFactory));
		funcs.add(new AmiWebFunctionEval.Factory(sp.getParser(), predefinedMethodsFactory));
		for (AmiWebFunctionFactory f : funcs)
			if (disabledFuncs.contains(f.getDefinition().getMethodName()))
				LH.info(log, "Disabling function: " + disabled);
			else
				predefinedMethodsFactory.addFactory(f);

		//		this.defaultTimeout = db.getDefaultQueryTimeoutMs();
		AmiScriptMemberMethods.registerMethods(this.getDebugManager(), predefinedMethodsFactory);

		ExpressionParser ep = sp.getExpressionParser();
		BasicExternFactoryManager efm = new BasicExternFactoryManager();
		efm.addLanguage("python", new PythonExtern());
		sp.setParser(new AmiScriptDerivedCellParser(ep, sp, state.getTools(), efm, true));
		this.mutator = new AmiCenterSqlProcessorMutator(sp);
		sp.setMutator(mutator);
		sp.getExpressionParser().setAllowSqlInjection(true);
		sp.lock();
		this.methodFactory.clearFactoryManagers();
		this.methodFactory.addFactoryManager(predefinedMethodsFactory);
		this.methodFactory.addFactoryManager(managedMethodsFactory);
		this.methodFactory.addFactoryManager(declaredMethodsFactory);
		this.processor = sp;
		for (AmiScriptClassPluginWrapper i : this.state.getCustomClassPlugins().values()) {
			predefinedMethodsFactory.addVarType(i.getName(), i.getClazz());
			for (AmiAbstractMemberMethod method : i.getMethods())
				predefinedMethodsFactory.addMemberMethod(method);
			for (AmiAbstractMemberMethod method : i.getConstructors())
				predefinedMethodsFactory.addMemberMethod(method);
		}
	}
	public void executedSqlFile(File file, byte definedBy, String username, SqlPlanListener planListener, DerivedCellTimeoutController tc, int limit, AmiCenterProcess process,
			CalcFrameStack sf) {
		AmiImdbSession origSession = AmiCenterUtils.getSession(sf);
		String fullPath = IOH.getFullPath(file);
		AmiImdbSession session = origSession.getImdb().getSessionManager().newTempSession(definedBy, AmiCenterQueryDsRequest.ORIGIN_SYSTEM, username,
				"STARTUP FILE " + IOH.getFullPath(file), AmiImdbSession.PERMISSIONS_FULL, tc.getTimeoutMillisRemaining(), limit, EmptyCalcFrame.INSTANCE);
		session.lock(process, planListener);
		try {
			LH.info(log, "Processing Schema script: ", IOH.getFullPathAndCheckSum(file));
			String data = IOH.readText(file);
			if (SH.is(data)) {
				DerivedCellCalculatorExpression calc = prepareSql(data, EmptyCalcTypes.INSTANCE, true, false, session.getReusableTopStackFrame());
				executeSql(calc, EmptyCalcFrame.INSTANCE, ON_EXECUTE_THROW, tc, limit, planListener, session.getReusableTopStackFrame());
			}
		} catch (ExpressionParserException e) {
			int ind = SH.indexOfIgnoreCase(e.getMessage(), "unknown variable", 0);
			if (ind != -1) {
				String varname = SH.afterFirst(e.getMessage(), ':');
				throw new ExpressionParserException(e.getExpression(), e.getPosition(), "The variable [" + varname + "] could not be resolved. If this is a datasource name in use ds syntax, please run [tools.sh --migrate filename...] with your amisql files to automatically add double quotes to variables where applicable.") ;
			} else {
				throw e;
			}
		} catch (Exception e) {
			throw new RuntimeException("Error processing ami managed schema file: " + fullPath, e);
		} finally {
			session.unlock();
			session.close();
		}
	}

	public static final byte ON_EXECUTE_THROW = 1;
	public static final byte ON_EXECUTE_RETURN_PAUSE = 2;
	public static final byte ON_EXECUTE_AUTO_HANDLE = 3;

	//	public Object executeSql(DerivedCellCalculatorExpression sql, Map<String, Object> objects, AmiImdbSession session, byte onExecute, TimeoutController timeout, int limit) {
	//		return executeSql(sql, objects, session, onExecute, timeout, limit, null);
	//	}
	public Object executeSql(String sql, CalcFrame variables, TimeoutController tc, int limit, SqlPlanListener sqlPlanListener, CalcFrameStack sf) {
		DerivedCellCalculatorExpression calc = prepareSql(sql, variables, true, true, sf);
		return executeSql(calc, variables, ON_EXECUTE_AUTO_HANDLE, tc, limit, sqlPlanListener, sf);
	}
	public Object executeSql(DerivedCellCalculatorExpression sql, CalcFrame objects, byte onExecute, TimeoutController tc, int limit, SqlPlanListener planListener,
			CalcFrameStack sf) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		OH.assertNotNull(session.getProcess());
		if (sql == null)
			return null;
		if (limit == AmiConsts.DEFAULT)
			limit = sf.getLimit();
		if (tc == null)
			tc = sf.getTimeoutController();
		if (planListener == null)
			planListener = sf.getSqlPlanListener();
		CalcFrameStack sf2 = new ChildCalcFrameStack(sql, true, sf, objects, EmptyCalcFrame.INSTANCE, sf.getTableset(), sf.getFactory(), limit, tc, planListener);
		Object t;
		//		try {
		DerivedCellCalculator inner = sql.getInnerCalcAt(0);
		if (inner instanceof DerivedCellCalculatorBlock) {
			DerivedCellCalculatorBlock block = (DerivedCellCalculatorBlock) inner;
			if (block.isImplicitBlock()) {
				if (block.hasDeclaredVariables()) {
					MutableCalcFrame types2 = (MutableCalcFrame) sf2.getTop().getGlobal();
					types2.putAllTypes(block.getDeclaredVariables());
					block.clearDeclaredVariables();
				}
				Collection<MethodFactory> sink = new ArrayList<MethodFactory>();
				block.getMethodFactory().getMethodFactories(sink);
				if (!sink.isEmpty()) {
					BasicMethodFactory mf = (BasicMethodFactory) sf2.getTop().getFactory();
					for (MethodFactory s : sink) {
						MethodFactory existing = mf.findFactory(s.getDefinition());
						if (existing != null)
							mf.removeFactory(existing);
						mf.addFactory(s);
					}
				}
			}
		} else if (inner instanceof DerivedCellCalculatorAssignment) {
			MutableCalcFrame types2 = (MutableCalcFrame) sf2.getTop().getGlobal();
			DerivedCellCalculatorAssignment assignment = (DerivedCellCalculatorAssignment) inner;
			types2.putType(assignment.getVariableName(), assignment.getReturnType());
		}
		t = sql.get(sf2);
		//		} catch (FlowControlThrow ftc) {
		//			Throwable thrown = ftc.getCause();
		//			if (thrown instanceof ExpressionParserException) {
		//				ExpressionParserException epe = (ExpressionParserException) thrown;
		//				if (epe.getExpression() == null)
		//					epe.setExpression(sql.getExpression());
		//
		//			}
		//			throw ftc;
		//		}
		if (t instanceof FlowControl) {
			if (t instanceof FlowControlPause) {
				FlowControlPause pause = (FlowControlPause) t;
				switch (onExecute) {
					case ON_EXECUTE_THROW:
						throw new ExpressionParserException(sql.getExpression(), pause.getPosition().getPosition(), "Dispatching EXECUTE not supported");
					case ON_EXECUTE_RETURN_PAUSE:
						return t;
					case ON_EXECUTE_AUTO_HANDLE: {
						AmiCenterAutoExecuteItinerary source = new AmiCenterAutoExecuteItinerary(this.state, this.itineraryProcessor, session.getProcess(), sql.getExpression(),
								pause, session, sf2);
						return source;
					}
				}
			} else {
				switch (((FlowControl) t).getType()) {
					case FlowControlPause.STATEMENT_SQL:
						return t;
					case FlowControlPause.STATEMENT_BREAK:
					case FlowControlPause.STATEMENT_CONTINUE:
						return null;
					default:
						LH.warning(log, "Should not have gotten here: " + t);
				}
			}
		}
		return t;
	}
	public DerivedCellCalculatorExpression prepareSql(String sql, CalcTypes types, boolean allowImplicitBlock, boolean allowSqlInjection, CalcTypesStack stack) {
		try {
			if (!allowImplicitBlock)
				this.processor.getExpressionParser().setAllowImplicitBlock(false);
			if (!allowSqlInjection)
				this.processor.getExpressionParser().setAllowSqlInjection(false);
			return this.processor.toCalc(sql, new ChildCalcTypesStack(stack, false, types));
		} finally {
			if (!allowImplicitBlock)
				this.processor.getExpressionParser().setAllowImplicitBlock(true);
			if (!allowSqlInjection)
				this.processor.getExpressionParser().setAllowSqlInjection(true);
		}
	}
	public DerivedCellCalculatorExpression prepareSql(String sql, Node node, CalcTypes types, boolean allowImplicitBlock, boolean allowSqlInjection, CalcTypesStack stack) {
		try {
			if (!allowImplicitBlock)
				this.processor.getExpressionParser().setAllowImplicitBlock(false);
			if (!allowSqlInjection)
				this.processor.getExpressionParser().setAllowSqlInjection(false);
			return this.processor.toCalc(sql, node, new ChildCalcTypesStack(stack, false, types));
		} finally {
			if (!allowImplicitBlock)
				this.processor.getExpressionParser().setAllowImplicitBlock(true);
			if (!allowSqlInjection)
				this.processor.getExpressionParser().setAllowSqlInjection(true);
		}
	}

	public SqlProcessor getSqlProcessor() {
		return this.processor;
	}
	public MethodFactoryManager getMethodFactory() {
		return this.methodFactory;
	}
	public BasicMethodFactory getPredefinedMethodFactory() {
		return this.predefinedMethodsFactory;
	}
	public BasicMethodFactory getDeclaredMethodFactory() {
		return this.declaredMethodsFactory;
	}
	public BasicMethodFactory getManagedMethodFactory() {
		return this.managedMethodsFactory;
	}

	public AmiDebugManager getDebugManager() {
		return this.debugManager;
	}

	public TimeZone getTimezone() {
		return this.timezone;
	}
	public String getTimezoneId() {
		return this.timezone.getID();
	}

}
