package com.f1.utils.sql.aggs;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.stack.CalcTypesStack;

public interface AggMethodFactory extends MethodFactory {

	@Override
	public AggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, CalcTypesStack variables);
}
