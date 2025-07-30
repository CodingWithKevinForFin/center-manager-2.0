package com.f1.utils.sql.aggs;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class VarAggCalculatorSample extends VarAggCalculatorAbstract {
	public static final String METHOD_NAME = "varS";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value");
		paramsDefinition.addDesc("Sample Variance of all non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new VarAggCalculatorSample(position, calcs[0]);
		}
	};

	public VarAggCalculatorSample(int position, DerivedCellCalculator inner) {
		super(position, inner, false);
	}
	@Override
	public DerivedCellCalculator copy() {
		return new VarAggCalculatorSample(getPosition(), inner.copy());
	}
	@Override
	public String getMethodName() {
		return "varS";
	}
}
