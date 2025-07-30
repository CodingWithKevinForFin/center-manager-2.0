package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public interface AggCalculator extends DerivedCellCalculator {

	public void visitRows(ReusableCalcFrameStack sf, List<? extends CalcFrame> values);
	public void visitRows(CalcFrameStack sf, long count);//Its the same value many times
	public String getMethodName();
	public abstract void setValue(Object object);
	public boolean getOrderingMatters();
	public void reset();
}
