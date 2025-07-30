package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CatAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "cat";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, String.class, "Object value,String delim,Integer limit");
		paramsDefinition.addDesc("concatenates non-null values to a single string using delim, will stop concatinating after limit is reached.");
		paramsDefinition.addParamDesc(0, "column to concatenate");
		paramsDefinition.addParamDesc(1, "delimiter");
		paramsDefinition.addParamDesc(2, "limit, negative number means no limit");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatAggCalculator(position, calcs[0], calcs[1], calcs[2]);
		}
	};
	private DerivedCellCalculator argThree;
	private DerivedCellCalculator argTwo;
	private String delimiter;
	private Integer limit;

	private String catValue;

	public CatAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator argTwo, DerivedCellCalculator argThree) {
		super(position, inner);
		this.argTwo = argTwo;
		this.argThree = argThree;

		if (argTwo.getReturnType() != String.class || !argTwo.isConst() || argTwo.get(null) == null)
			throw new ExpressionParserException(argTwo.getPosition(), "2nd argument must be a constant string");
		if (argThree.getReturnType() != Integer.class || !argThree.isConst() || argThree.get(null) == null)
			throw new ExpressionParserException(argThree.getPosition(), "3rd argument must be a constant int");
		this.delimiter = (String) argTwo.get(null);
		this.limit = (Integer) argThree.get(null);
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return catValue;
	}

	private StringBuilder sb = new StringBuilder();

	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		int limit = this.limit;

		SH.clear(sb);
		int pos = 0;
		for (int i = 0, l = values.size(); i < l; i++) {
			if (pos == limit)
				break;
			Object value = inner.get(sf.reset(values.get(i)));
			if (value == null)
				continue;
			if (pos != 0)
				sb.append(delimiter);
			SH.s(value, sb);
			pos++;
		}

		catValue = sb.toString();
	}

	@Override
	public String getMethodName() {
		return "cat";
	}

	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new CatAggCalculator(getPosition(), inner.copy(), argTwo.copy(), argThree.copy());
	}

	@Override
	public boolean getOrderingMatters() {
		return true;
	}
	@Override
	public void setValue(Object value) {
		this.catValue = (String) value;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		SH.clear(sb);
		if (count > limit)
			count = limit;
		if (count == 0)
			setValue("");
		else {
			SH.clear(sb);
			Object value = inner.get(values);
			if (value == null)
				setValue("");
			else {
				sb.append(value);
				for (int i = 1; i < count; i++)
					sb.append(delimiter).append(value);
			}
			setValue(sb.toString());
			sb.setLength(0);
		}
	}
}
