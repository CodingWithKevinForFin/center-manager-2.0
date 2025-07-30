package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcTypesStack;

public interface MethodFactory {

	public ParamsDefinition getDefinition();
	public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack variables);
}
