package com.f1.utils.sql.aggs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CatUniqueAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "catUnique";
	public static final String METHOD_NAME_ASC = "catUniqueAsc";
	public static final String METHOD_NAME_DES = "catUniqueDes";
	public static final ParamsDefinition paramsDefinition;
	public static final ParamsDefinition paramsDefinitionAsc;
	public static final ParamsDefinition paramsDefinitionDes;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, String.class, "Object value,String delim");
		paramsDefinition.addDesc("Concatenates non-null values to a single string using delim. Will skip dulicate values. Values are naturally orders");
		paramsDefinition.addParamDesc(0, "column to concatenate");
		paramsDefinition.addParamDesc(1, "delimiter");

		paramsDefinitionAsc = new ParamsDefinition(METHOD_NAME_ASC, String.class, "Object value,String delim");
		paramsDefinitionAsc.addDesc("Concatenates non-null values to a single string using delim. Will skip dulicate values. Values are sorted in ascending order");
		paramsDefinitionAsc.addParamDesc(0, "column to concatenate");
		paramsDefinitionAsc.addParamDesc(1, "delimiter");

		paramsDefinitionDes = new ParamsDefinition(METHOD_NAME_DES, String.class, "Object value,String delim");
		paramsDefinitionDes.addDesc("Concatenates non-null values to a single string using delim. Will skip dulicate values. Values are sorted in descending order");
		paramsDefinitionDes.addParamDesc(0, "column to concatenate");
		paramsDefinitionDes.addParamDesc(1, "delimiter");
	}
	public static final AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueAggCalculator(position, calcs[0], calcs[1], SORT_OFF);
		}
	};
	public static final AggMethodFactory FACTORY_ASC = new AggMethodFactory() {
		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinitionAsc;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueAggCalculator(position, calcs[0], calcs[1], SORT_ASC);
		}
	};
	public static final AggMethodFactory FACTORY_DES = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinitionDes;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CatUniqueAggCalculator(position, calcs[0], calcs[1], SORT_DES);
		}
	};
	private String delimiter;
	private String joinedVal;
	private DerivedCellCalculator argTwo;
	private static final byte SORT_ASC = 1;
	private static final byte SORT_DES = -1;
	private static final byte SORT_OFF = 0;
	private byte sort = 0;

	public CatUniqueAggCalculator(int position, DerivedCellCalculator inner, DerivedCellCalculator argTwo, byte sort) {
		super(position, inner);
		this.sort = sort;
		this.argTwo = argTwo;
		if (argTwo.getReturnType() != String.class || !argTwo.isConst() || argTwo.get(null) == null)
			throw new ExpressionParserException(argTwo.getPosition(), "2nd argument must be constant string");
		this.delimiter = (String) argTwo.get(null);
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return joinedVal;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		boolean oneVal = true;
		boolean firstRow = true;
		Object firstArg = null;
		LinkedHashSet<Object> usedValues = null;
		for (CalcFrame row : values) {
			Object value = inner.get(sf.reset(row));
			if (value == null)
				continue;
			if (firstRow) {
				firstArg = value;
				firstRow = false;
			} else if (!oneVal || OH.ne(value, firstArg)) {
				if (oneVal) {
					usedValues = new LinkedHashSet<Object>();
					usedValues.add(firstArg);
					oneVal = false;
				}
				usedValues.add(value);
			}
		}
		if (firstRow) {
			joinedVal = "";
		} else if (oneVal) {
			joinedVal = OH.toString(firstArg);
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
			joinedVal = SH.join(delimiter, l);
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CatUniqueAggCalculator(getPosition(), inner.copy(), argTwo.copy(), sort);
	}
	@Override
	public Class<?> getReturnType() {
		return String.class;
	}
	@Override
	public String getMethodName() {
		switch (sort) {
			case SORT_ASC:
				return METHOD_NAME_ASC;
			case SORT_DES:
				return METHOD_NAME_DES;
			default:
				return METHOD_NAME;
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
		CatUniqueAggCalculator other2 = (CatUniqueAggCalculator) other;
		return other2.sort == sort && DerivedHelper.areSame(other2.argTwo, argTwo);
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(inner.get(values));
	}

}
