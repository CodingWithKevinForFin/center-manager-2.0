package com.f1.utils.sql.aggs;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class CovAggCalculator extends CovAggCalculatorAbstract {
	public static final String METHOD_NAME = "covar";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value1,Number value2");
		paramsDefinition.addDesc("Population  Covariance of 1st value and 2nd value where both aren't null.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CovAggCalculator(position, calcs[0], calcs[1]);
		}
	};

	public CovAggCalculator(int position, DerivedCellCalculator argOne, DerivedCellCalculator argTwo) {
		super(position, argOne, argTwo, true);
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CovAggCalculator(getPosition(), inner.copy(), getArgTwo().copy());
	}
	@Override
	public String getMethodName() {
		return "covar";
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(0);
	}
}
