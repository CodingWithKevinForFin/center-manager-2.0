package com.f1.utils.structs.table.derived;

public interface MethodDerivedCellCalculator extends DerivedCellCalculator {

	public int getParamsCount();
	public DerivedCellCalculator getParamAt(int n);
	public String getMethodName();
	public ParamsDefinition getDefinition();

}
