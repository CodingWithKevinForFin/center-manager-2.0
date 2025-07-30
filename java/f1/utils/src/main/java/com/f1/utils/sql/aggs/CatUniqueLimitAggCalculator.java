package com.f1.utils.sql.aggs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CatUniqueLimitAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME_LIMIT = "catUniqueLimit";
	public static final String METHOD_NAME_LIMIT_ASC = "catUniqueLimitAsc";
	public static final String METHOD_NAME_LIMIT_DESC = "catUniqueLimitDes";

	public final static ParamsDefinition paramsDefinitionLim;
	public final static ParamsDefinition paramsDefinitionLimAsc;
	public final static ParamsDefinition paramsDefinitionLimDes;
	static {
		paramsDefinitionLim = new ParamsDefinition(METHOD_NAME_LIMIT, String.class, "Object value,String delim,Integer limit");
		paramsDefinitionLim.addDesc(
				"Concatenates non-null values to a single string using delim. Will skip dulicate values stop concatenating after limit is reached. Values are naturally ordered");
		paramsDefinitionLim.addParamDesc(0, "column to concatenate");
		paramsDefinitionLim.addParamDesc(1, "delimiter");
		paramsDefinitionLim.addParamDesc(2, "limit, negative number means no limit");

		paramsDefinitionLimAsc = new ParamsDefinition(METHOD_NAME_LIMIT_ASC, String.class, "Object value,String delim,Integer limit");
		paramsDefinitionLimAsc.addDesc(
				"Concatenates non-null values to a single string using delim. Will skip dulicate values stop concatenating after limit is reached. Values are stored in ascending order");
		paramsDefinitionLimAsc.addParamDesc(0, "column to concatenate");
		paramsDefinitionLimAsc.addParamDesc(1, "delimiter");
		paramsDefinitionLimAsc.addParamDesc(2, "limit, negative number means no limit");

		paramsDefinitionLimDes = new ParamsDefinition(METHOD_NAME_LIMIT_DESC, String.class, "Object value,String delim,Integer limit");
		paramsDefinitionLimDes.addDesc(
				"Concatenates non-null values to a single string using delim. Will skip dulicate values stop concatenating after limit is reached. Values are stored in descending order");
		paramsDefinitionLimDes.addParamDesc(0, "column to concatenate");
		paramsDefinitionLimDes.addParamDesc(1, "delimiter");
		paramsDefinitionLimDes.addParamDesc(2, "limit, negative number means no limit");

	}
	public final static AggMethodFactory FACTORY_LIM = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinitionLim;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueLimitAggCalculator(position, calcs[0], calcs[1], calcs[2], SORT_OFF);
		}
	};
	public final static AggMethodFactory FACTORY_LIM_ASC = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinitionLimAsc;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueLimitAggCalculator(position, calcs[0], calcs[1], calcs[2], SORT_ASC);
		}
	};
	public final static AggMethodFactory FACTORY_LIM_DES = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinitionLimDes;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueLimitAggCalculator(position, calcs[0], calcs[1], calcs[2], SORT_DES);
		}
	};
	private String delimiter;
	private String joinedVal;
	private Integer limit;
	private DerivedCellCalculator argTwo;
	private DerivedCellCalculator argThree;
	private static final byte SORT_ASC = 1;
	private static final byte SORT_DES = -1;
	private static final byte SORT_OFF = 0;
	private byte sort = 0;

	public CatUniqueLimitAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator argTwo, DerivedCellCalculator argThree, byte sort) {
		super(position, inner);
		this.sort = sort;
		this.argTwo = argTwo;
		this.argThree = argThree;
		if (argTwo.getReturnType() != String.class || !argTwo.isConst() || argTwo.get(null) == null)
			throw new ExpressionParserException(argTwo.getPosition(), "2nd argument must be constant string");
		if (argThree.getReturnType() != Integer.class || !argThree.isConst() || argThree.get(null) == null)
			throw new ExpressionParserException(argThree.getPosition(), "3rd argument must be a constant int");
		this.delimiter = (String) argTwo.get(null);
		this.limit = (Integer) argThree.get(null);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return joinedVal;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		int limit = this.limit;
		if (limit == 0 || values.size() == 0) {
			joinedVal = "";
			return;
		}
		LinkedHashSet<Object> usedValues = new LinkedHashSet<Object>();
		for (CalcFrame row : values) {
			Object value = inner.get(sf.reset(row));
			if (value == null)
				continue;
			if (usedValues.add(value))
				if (sort == SORT_OFF && limit > 0 && usedValues.size() == limit)
					break;
		}
		if (usedValues.size() == 1) {
			joinedVal = SH.join(delimiter, usedValues);
		} else if (sort == SORT_OFF) {
			joinedVal = SH.join(delimiter, usedValues);
		} else {
			List<String> l = new ArrayList<String>(usedValues.size());
			for (Object s : usedValues)
				l.add(SH.s(s));
			if (sort == SORT_ASC)
				Collections.sort(l, SH.COMPARATOR_CASEINSENSITIVE_STRING);
			else
				Collections.sort(l, SH.COMPARATOR_CASEINSENSITIVE_STRING_REVERSE);

			joinedVal = limit >= 0 && l.size() > limit ? SH.join(delimiter, l.subList(0, limit)) : SH.join(delimiter, l);
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CatUniqueLimitAggCalculator(getPosition(), inner.copy(), argTwo.copy(), argThree.copy(), sort);
	}
	@Override
	public Class<?> getReturnType() {
		return String.class;
	}
	@Override
	public String getMethodName() {
		switch (sort) {
			case SORT_ASC:
				return METHOD_NAME_LIMIT_ASC;
			case SORT_DES:
				return METHOD_NAME_LIMIT_DESC;
			default:
				return METHOD_NAME_LIMIT;
		}
	}
	@Override
	public boolean getOrderingMatters() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		this.joinedVal = (String) value;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (!super.isSame(other))
			return false;
		CatUniqueLimitAggCalculator other2 = (CatUniqueLimitAggCalculator) other;
		return other2.sort == sort && DerivedHelper.areSame(other2.argTwo, argTwo);
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(inner.get(values));
	}
}
