package com.f1.utils.structs.table.derived;

import com.f1.base.ToStringable;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface DerivedCellCalculator extends ToStringable {

	public Object get(CalcFrameStack sf);
	public Class<?> getReturnType();
	public int getPosition();
	public DerivedCellCalculator copy();
	public boolean isConst();//DOES recurse
	public boolean isReadOnly();//DOES recurse
	public int getInnerCalcsCount();
	public DerivedCellCalculator getInnerCalcAt(int n);

	public Object resume(PauseStack paused);
	public boolean isPausable();//DOES NOT recurse

	public boolean isSame(DerivedCellCalculator other);
}
