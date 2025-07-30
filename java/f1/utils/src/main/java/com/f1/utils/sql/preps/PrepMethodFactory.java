package com.f1.utils.sql.preps;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.MethodFactory;

public interface PrepMethodFactory extends MethodFactory {

	@Override
	public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context);
}
