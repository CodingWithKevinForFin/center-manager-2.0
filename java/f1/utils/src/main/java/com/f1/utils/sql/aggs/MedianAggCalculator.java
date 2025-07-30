package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class MedianAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "median";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Comparable.class, "Comparable value");
		paramsDefinition.addDesc("The median of all non-null values.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new MedianAggCalculator(position, calcs[0]);
		}
	};

	private double doubleValue;
	private boolean isNull;

	public MedianAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return isNull ? null : doubleValue;
	}

	@Override
	protected void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty())
			// Case no values;
			isNull = true;
		else {
			DoubleArrayList numbers = AggHelper.getDoubles(sf, values, inner);
			if (numbers == null) {
				isNull = true;
				return;
			} else {
				doubleValue = MH.median(numbers);
			}
		}
	}

	@Override
	public String getMethodName() {
		return "median";
	}

	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new MedianAggCalculator(getPosition(), inner.copy());
	}

	@Override
	public boolean getOrderingMatters() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		if (value == null)
			isNull = true;
		else {
			isNull = false;
			this.doubleValue = (Double) value;
		}
	}

	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(getReturnType().cast(inner.get(values)));
	}
}
