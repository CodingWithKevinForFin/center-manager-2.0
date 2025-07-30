package com.f1.utils.sql.preps;

import java.util.Arrays;
import java.util.Map;

import com.f1.utils.impl.FastArrayList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class UniqueRankAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "urank";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Comparable value");
		paramsDefinition.addDesc(
				"will evaluate value for all rows and the lowest value will get 1, 2nd will get 2, etc. If multiple rows value evaluate to the same value, the first occurrence gets the lower value.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,urank(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new UniqueRankAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	private FastArrayList<Object> values = new FastArrayList<Object>(10);

	public UniqueRankAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Object get(CalcFrameStack lcvs, int position) {

		return isNull ? null : values.get(position);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.setSize(values.size());
		if (values.isEmpty())
			isNull = true;
		else {
			isNull = false;
			java.util.Map.Entry<Comparable, Integer>[] a = new Map.Entry[values.size()];
			int len = values.size();
			int j = 0;
			int nonNullCount = 0;
			for (; j < len; j++) {
				Comparable value = (Comparable) inner.get(sf.reset(values.get(j)));
				if (value == null)
					this.values.setNoCheck(j, null);
				else
					a[nonNullCount++] = new Tuple2<Comparable, Integer>(value, j);
			}
			Arrays.sort(a, 0, nonNullCount, RankAggCalculator.SORTER);
			for (int i = 0, n = 1; i < nonNullCount; i++)
				this.values.set(a[i].getValue(), n++);
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new UniqueRankAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}
