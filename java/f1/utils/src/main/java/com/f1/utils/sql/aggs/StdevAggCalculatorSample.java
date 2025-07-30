package com.f1.utils.sql.aggs;

import com.f1.base.Mapping;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class StdevAggCalculatorSample extends StdevAggCalculatorAbstract {
	public static final String METHOD_NAME = "stdevS";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value");
		paramsDefinition.addDesc("Sample Standard Deviation of all non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new StdevAggCalculatorSample(position, calcs[0]);
		}
	};

	public StdevAggCalculatorSample(int position, DerivedCellCalculator inner) {
		super(position, inner, false);
	}
	@Override
	public DerivedCellCalculator copy() {
		return new StdevAggCalculatorSample(getPosition(), inner.copy());
	}
	@Override
	public String getMethodName() {
		return "stdevSample";
	}
}
