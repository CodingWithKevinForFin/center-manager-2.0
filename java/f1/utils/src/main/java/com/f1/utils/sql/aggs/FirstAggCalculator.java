package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class FirstAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "first";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Object.class, "Object value");
		paramsDefinition.addDesc("First value, may be null if first value is null.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new FirstAggCalculator(position, calcs[0]);
		}
	};
	private Object value;

	public FirstAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return value;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		value = values.isEmpty() ? null : inner.get(sf.reset(values.get(0)));
	}
	@Override
	public DerivedCellCalculator copy() {
		return new FirstAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return inner.getReturnType();
	}
	@Override
	public String getMethodName() {
		return "first";
	}
	@Override
	public boolean getOrderingMatters() {
		return true;
	}
	@Override
	public void setValue(Object object) {
		this.value = object;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(getReturnType().cast(inner.get(values)));
	}
}
