package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class OffsetAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "offset";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Object.class, "Object value,Integer offset");
		paramsDefinition.addDesc(
				"Will grab the value offset from the current row. If offset==0 use current row, negative number is nth prior row, positive number is nth future row. Evaluates to null if offset is out of bounds for current table. Ex: offset(price,-1) for first row returns null, for second row returns first Row's price and for nth row returns (n-1)ths row's price");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addParamDesc(1, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,offset(price,-1) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new OffsetAggCalculator(position, calcs);
		}
	};
	private boolean isNull;
	final private ArrayList<Object> values = new ArrayList<Object>();
	final private DerivedCellCalculator offsetCalc;
	final private Integer offset;

	public OffsetAggCalculator(int position, DerivedCellCalculator[] calcs) {
		super(position, calcs);
		if (this.inners.length != 2)
			throw new ExpressionParserException(position, getMethodName() + "(...) takes two parameters: (object value,int rowOffset)");
		offsetCalc = calcs[1];
		if (offsetCalc.getReturnType() != Integer.class)
			throw new ExpressionParserException(position, getMethodName() + "(...) 2nd param must be int: (object value,int rowOffset)");
		if (offsetCalc.isConst()) {
			this.offset = (Integer) offsetCalc.get(null);
			isNull = offset == null;
		} else {
			offset = null;
			isNull = false;
		}

	}
	@Override
	public Object get(CalcFrameStack lcvs, int position) {
		if (isNull)
			return null;
		int offset = position;
		if (this.offset != null)
			offset += this.offset.intValue();
		else {
			Integer t = (Integer) offsetCalc.get(lcvs);
			if (t == null)
				return null;
			offset += t;
		}
		return offset < 0 || offset >= values.size() ? null : values.get(offset);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty())
			isNull = true;
		else {
			isNull = false;
			for (int j = 0, n = values.size(); j < n; j++) {
				this.values.add(inner.get(sf.reset(values.get(j))));
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new OffsetAggCalculator(getPosition(), copyInners());
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public Class<?> getReturnType() {
		return inner.getReturnType();
	}

}