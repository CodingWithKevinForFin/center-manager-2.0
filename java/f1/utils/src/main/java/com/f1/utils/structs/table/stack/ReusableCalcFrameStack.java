package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;

public class ReusableCalcFrameStack implements CalcFrameStack {

	private CalcFrameStack parent;
	private CalcFrame frame;
	private SqlResultset resultset;
	private int stackSize;

	public ReusableCalcFrameStack(CalcFrameStack inner) {
		this.parent = inner;
		this.stackSize = inner.getStackSize();
	}
	public ReusableCalcFrameStack(CalcFrameStack inner, CalcFrame frame) {
		this.parent = inner;
		this.stackSize = inner.getStackSize();
		reset(frame);
	}

	public ReusableCalcFrameStack reset(CalcFrame frame) {
		OH.assertNotNull(frame);
		this.frame = frame;
		return this;
	}
	public ReusableCalcFrameStack reset(CalcFrameStack sf, CalcFrame frame) {
		OH.assertNotNull(frame);
		this.frame = frame;
		this.parent = sf;
		this.frame = frame;
		this.stackSize = parent.getStackSize();
		return this;
	}

	@Override
	public boolean isParentVisible() {
		return true;
	}

	@Override
	public CalcFrameStack getTop() {
		return parent.getTop();
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
	public CalcFrame getFrameConsts() {
		return parent.getFrameConsts();
	}

	@Override
	public SqlPlanListener getSqlPlanListener() {
		return parent.getSqlPlanListener();
	}

	@Override
	public BreakpointManager getBreakPointManager() {
		return parent.getBreakPointManager();
	}

	@Override
	public CalcFrame getGlobal() {
		return parent.getGlobal();
	}

	@Override
	public Tableset getTableset() {
		return parent.getTableset();
	}

	@Override
	public TimeoutController getTimeoutController() {
		return parent.getTimeoutController();
	}

	@Override
	public int getLimit() {
		return parent.getLimit();
	}

	@Override
	public MethodFactoryManager getFactory() {
		return parent.getFactory();
	}

	@Override
	public CalcFrame getGlobalConsts() {
		return parent.getGlobalConsts();
	}

	@Override
	public CalcFrame getFrame() {
		return this.frame;
	}

	public void clear() {
		this.parent = null;
		this.frame = null;
		this.resultset = null;
		this.stackSize = -1;
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
		return this.parent.getStackLimit();
	}
	@Override
	public SqlResultset getSqlResultset() {
		return this.resultset == null ? parent.getSqlResultset() : resultset;
	}
	public void setSqlResultSet(SqlResultset resultset) {
		this.resultset = resultset;
	}
}
