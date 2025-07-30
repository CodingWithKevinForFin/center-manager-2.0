package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.EmptyTableset;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;

public class TopCalcFrameStack implements CalcFrameStack {

	final private Tableset tableset;
	final private int limit;
	final private TimeoutController timeoutController;
	final private BreakpointManager breakpointManager;
	final private MethodFactoryManager methodFactory;
	final private CalcFrame globalVars;
	final private CalcFrame consts;
	final private SqlPlanListener sqlPlanListener;
	final private CalcFrame frameConsts;
	private int stackLimit;
	private SqlResultset sqlresultset;

	public TopCalcFrameStack(int stackLimit, Tableset tableset, int limit, TimeoutController timeoutController, SqlPlanListener sqlPlanListener,
			BreakpointManager breakpointManager, CalcFrame globalVars, CalcFrame frameConsts, MethodFactoryManager methodFactory, CalcFrame consts, SqlResultset resultset) {
		OH.assertGt(stackLimit, 0);
		OH.assertNotNull(methodFactory);
		this.tableset = tableset;
		this.limit = limit;
		this.timeoutController = timeoutController;
		this.sqlPlanListener = sqlPlanListener;
		this.breakpointManager = breakpointManager;
		this.methodFactory = methodFactory;
		this.globalVars = globalVars;
		this.consts = consts;
		this.frameConsts = frameConsts;
		this.stackLimit = stackLimit;
		this.sqlresultset = resultset;
	}
	public TopCalcFrameStack(CalcFrame frame) {
		this(DEFAULT_STACK_LIMIT, EmptyTableset.INSTANCE, SqlProcessor.NO_LIMIT, new DerivedCellTimeoutController(DerivedCellTimeoutController.DEFAULT_TIMEOUT), null, null, frame,
				EmptyCalcFrame.INSTANCE, new BasicMethodFactory(), EmptyCalcFrame.INSTANCE, null);
	}
	public TopCalcFrameStack(CalcFrame frame, SqlPlanListener listener) {
		this(DEFAULT_STACK_LIMIT, EmptyTableset.INSTANCE, SqlProcessor.NO_LIMIT, new DerivedCellTimeoutController(DerivedCellTimeoutController.DEFAULT_TIMEOUT), listener, null,
				frame, EmptyCalcFrame.INSTANCE, new BasicMethodFactory(), EmptyCalcFrame.INSTANCE, null);
	}
	public TopCalcFrameStack(Tableset tableset, CalcFrame frame) {
		this(DEFAULT_STACK_LIMIT, tableset, SqlProcessor.NO_LIMIT, new DerivedCellTimeoutController(DerivedCellTimeoutController.DEFAULT_TIMEOUT), null, null, frame,
				EmptyCalcFrame.INSTANCE, new BasicMethodFactory(), EmptyCalcFrame.INSTANCE, null);
	}

	public TopCalcFrameStack(Tableset tableset, int limit, TimeoutController tc, MethodFactoryManager methodFactory, CalcFrame glbalVars) {
		this(DEFAULT_STACK_LIMIT, tableset, limit, tc, null, null, glbalVars, EmptyCalcFrame.INSTANCE, methodFactory, EmptyCalcFrame.INSTANCE, null);
	}
	public TopCalcFrameStack(Tableset tableset, int limit, TimeoutController tc, SqlPlanListener sqlPlanListener, MethodFactoryManager methodFactory, CalcFrame globalVars) {
		this(DEFAULT_STACK_LIMIT, tableset, limit, tc, sqlPlanListener, null, globalVars, EmptyCalcFrame.INSTANCE, methodFactory, EmptyCalcFrame.INSTANCE, null);
	}
	public TopCalcFrameStack(Tableset tableset, MethodFactoryManager methodFactory, CalcFrame globalVars) {
		this(DEFAULT_STACK_LIMIT, tableset, SqlProcessor.NO_LIMIT, new DerivedCellTimeoutController(DerivedCellTimeoutController.DEFAULT_TIMEOUT), null, null, globalVars,
				EmptyCalcFrame.INSTANCE, methodFactory, EmptyCalcFrame.INSTANCE, null);
	}
	@Override
	public boolean isParentVisible() {
		return false;
	}

	@Override
	public TopCalcFrameStack getTop() {
		return this;
	}

	@Override
	public int getStackSize() {
		return 1;
	}

	@Override
	public CalcFrameStack getParent() {
		return null;
	}

	@Override
	public TimeoutController getTimeoutController() {
		return this.timeoutController;
	}

	@Override
	public SqlPlanListener getSqlPlanListener() {
		return this.sqlPlanListener;
	}

	@Override
	public BreakpointManager getBreakPointManager() {
		return this.breakpointManager;
	}

	@Override
	public int getLimit() {
		return this.limit;
	}

	@Override
	public Tableset getTableset() {
		return this.tableset;
	}

	@Override
	public CalcFrame getGlobal() {
		return globalVars;
	}

	@Override
	public MethodFactoryManager getFactory() {
		return methodFactory;
	}

	@Override
	public CalcFrame getGlobalConsts() {
		return this.consts;
	}

	@Override
	public CalcFrame getFrame() {
		return EmptyCalcFrame.INSTANCE;//this.frame;
	}
	@Override
	public CalcFrame getFrameConsts() {
		return this.frameConsts;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		DerivedHelper.toStringNoRecurse(this, sink);
		if (this.getParent() != null)
			this.getParent().toString(sink.append(SH.CHAR_NEWLINE));
		return sink;
	}
	@Override
	public DerivedCellCalculator getCalc() {
		return null;
	}
	@Override
	public int getStackLimit() {
		return this.stackLimit;
	}
	@Override
	public SqlResultset getSqlResultset() {
		return sqlresultset;
	}
}
