package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;

public class EmptyCalcFrameStack implements CalcFrameStack {

	public static final CalcFrameStack INSTANCE = new EmptyCalcFrameStack();

	private EmptyCalcFrameStack() {
	}

	@Override
	public DerivedCellTimeoutController getTimeoutController() {
		return null;
	}

	@Override
	public int getLimit() {
		return SqlProcessor.NO_LIMIT;
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}

	@Override
	public CalcFrameStack getTop() {
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
	public SqlPlanListener getSqlPlanListener() {
		return null;
	}

	@Override
	public BreakpointManager getBreakPointManager() {
		return null;
	}

	@Override
	public Tableset getTableset() {
		return null;
	}

	@Override
	public CalcFrame getGlobal() {
		return EmptyCalcFrame.INSTANCE;
	}

	@Override
	public MethodFactoryManager getFactory() {
		return null;
	}

	@Override
	public CalcFrame getGlobalConsts() {
		return EmptyCalcFrame.INSTANCE;
	}

	@Override
	public CalcFrame getFrame() {
		return EmptyCalcFrame.INSTANCE;
	}

	@Override
	public CalcFrame getFrameConsts() {
		return EmptyCalcFrame.INSTANCE;
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
		return CalcFrameStack.DEFAULT_STACK_LIMIT;
	}

	@Override
	public SqlResultset getSqlResultset() {
		return null;
	}
}
