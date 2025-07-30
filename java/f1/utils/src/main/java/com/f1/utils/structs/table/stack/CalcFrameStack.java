package com.f1.utils.structs.table.stack;

import com.f1.base.CalcFrame;
import com.f1.base.ToStringable;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.TimeoutController;

public interface CalcFrameStack extends CalcTypesStack, ToStringable {

	int DEFAULT_STACK_LIMIT = 64;

	@Override
	public CalcFrameStack getTop();
	@Override
	public CalcFrameStack getParent();

	//Top returns 1
	public int getStackSize();

	//These may all return null.. Never override (should go to top)
	public BreakpointManager getBreakPointManager();
	public int getStackLimit();

	//These are virtual (Need to walk)
	public SqlPlanListener getSqlPlanListener();
	public Tableset getTableset();
	public SqlResultset getSqlResultset();
	public TimeoutController getTimeoutController();
	public int getLimit();
	@Override
	public CalcFrame getGlobal();
	@Override
	public CalcFrame getFrame();

	//For debugger Purposes
	public DerivedCellCalculator getCalc();

}
