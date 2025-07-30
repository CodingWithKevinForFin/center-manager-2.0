package com.f1.utils.sql.aggs;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class StdevAggCalculator extends StdevAggCalculatorAbstract {
	public static final String METHOD_NAME = "stdev";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value");
		paramsDefinition.addDesc("Population Standard Deviation of all non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new StdevAggCalculator(position, calcs[0]);
		}
	};

	public StdevAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner, true);
	}
	@Override
	public DerivedCellCalculator copy() {
		return new StdevAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public String getMethodName() {
		return "stdev";
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(0);
	}
}
