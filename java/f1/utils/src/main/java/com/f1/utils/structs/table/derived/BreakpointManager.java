package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface BreakpointManager {

	public boolean hasBreakpoints();
	public boolean isBreakpoint(CalcFrameStack frame, DerivedCellCalculator statment);

}
