package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;

public class ChildCalcFrameStack implements CalcFrameStack {

	final private CalcFrameStack parent;
	final private CalcFrame frame;
	final private CalcFrame frameConsts;
	final private boolean isOuterVarsVisible;
	final private CalcFrameStack origValues;
	final private int stackSize;
	final private MethodFactoryManager factory;
	final private TimeoutController timeoutController;
	final private Tableset tableset;
	final private SqlPlanListener sqlPlanListener;
	final private int limit;
	final private DerivedCellCalculator calc;

	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame) {
		this(calc, isOuterVarsVisible, parent, frame, EmptyCalcFrame.INSTANCE, parent.getTableset(), parent.getFactory(), parent.getLimit(), parent.getTimeoutController(),
				parent.getSqlPlanListener());
	}
	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame, CalcFrame frameConsts, Tableset ts,
			MethodFactoryManager factory, int limit, TimeoutController tc, SqlPlanListener sqlPlanListener) {
		this.calc = calc;
		this.isOuterVarsVisible = isOuterVarsVisible;
		this.stackSize = parent.getStackSize() + 1;
		this.parent = parent;
		this.origValues = this.parent.getTop();
		this.frame = frame;
		this.frameConsts = frameConsts;
		this.tableset = ts;
		this.factory = factory;
		this.timeoutController = tc;
		this.limit = limit;
		this.sqlPlanListener = sqlPlanListener;
		if (this.stackSize > parent.getStackLimit()) {
			System.err.println(this);
			throw new FlowControlThrow("STACK_OVERFLOW(" + this.stackSize + " frames)");
		}
	}
	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame, Tableset ts) {
		this(calc, isOuterVarsVisible, parent, frame, EmptyCalcFrame.INSTANCE, ts, parent.getFactory(), parent.getLimit(), parent.getTimeoutController(),
				parent.getSqlPlanListener());
	}
	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame, MethodFactoryManager factory) {
		this(calc, isOuterVarsVisible, parent, frame, EmptyCalcFrame.INSTANCE, parent.getTableset(), factory, parent.getLimit(), parent.getTimeoutController(),
				parent.getSqlPlanListener());
	}
	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame, CalcFrame frameConsts, TimeoutController tc,
			SqlPlanListener spl) {
		this(calc, isOuterVarsVisible, parent, frame, frameConsts, parent.getTableset(), parent.getFactory(), parent.getLimit(), tc, spl);
	}

	public ChildCalcFrameStack(DerivedCellCalculator calc, boolean isOuterVarsVisible, CalcFrameStack parent, CalcFrame frame, CalcFrame frameConsts, Tableset tableset, int limit,
			TimeoutController tc) {
		this(calc, isOuterVarsVisible, parent, frame, frameConsts, tableset, parent.getFactory(), limit, tc, parent.getSqlPlanListener());
	}
	@Override
	public boolean isParentVisible() {
		return this.isOuterVarsVisible;
	}

	@Override
	public CalcFrameStack getTop() {
		return this.origValues;
	}

	@Override
	public int getStackSize() {
		return this.stackSize;
	}

	@Override
	public CalcFrameStack getParent() {
		return this.parent;
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
		return this.origValues.getBreakPointManager();
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
		return this.origValues.getGlobal();
	}

	@Override
	public CalcFrame getFrame() {
		return this.frame;
	}

	@Override
	public MethodFactoryManager getFactory() {
		return this.factory;
	}

	@Override
	public CalcFrame getGlobalConsts() {
		return this.origValues.getGlobalConsts();
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
		return this.calc;
	}
	@Override
	public int getStackLimit() {
		return this.origValues.getStackLimit();
	}
	@Override
	public SqlResultset getSqlResultset() {
		return parent.getSqlResultset();
	}

}
