package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface DerivedCellCalculatorFlowControl extends DerivedCellCalculator {
	public static final Object VOID = new Object();
	public static final Object NULL = new Object();

	//true if always returns or always throws
	public boolean getFlowControlAlwaysCompletes();
	public boolean hasReturn();

	//return null if never returns, returns Void.class for empty return, otherwise widest values of all returns
	//	public Class<?> getFlowControlReturnType();

	//Should only be called by other flow controls. Returns:
	//  VOID on return;
	//  NULL on return null;
	//  Value on return value;
	// Otherwise, null
	public Object getFlowControl(CalcFrameStack sf);
	public Object resumeFlowControl(PauseStack paused);

	//Returns:
	// null on return;
	// null on return null;
	// value on return value;
	// Otherwise, last executed statement (null if no statements in block)
	@Override
	Object get(CalcFrameStack sf);
}
