package com.f1.utils.sql.preps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.HasherMap.Entry;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class RankAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "rank";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Integer.class, "Comparable value");
		paramsDefinition.addDesc(
				"will evaluate value for all rows and the lowest value will get 1, 2nd will get 2, etc. If multiple rows value evaluate to the same value, they will get the same ranking.");
		paramsDefinition.addParamDesc(0, "");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,rank(price) AS newCol FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new RankAggCalculator(position, calcs[0]);
		}
	};
	private boolean isNull;
	private ArrayList<Object> values = new ArrayList<Object>();

	public RankAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Object get(CalcFrameStack lcvs, int position) {
		return isNull ? null : values.get(position);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty())
			isNull = true;
		else {
			HasherMap<Comparable, Integer> valueToRank = new HasherMap<Comparable, Integer>();
			isNull = false;
			int len = values.size();
			for (int j = 0, n = values.size(); j < n; j++) {
				Object value = inner.get(sf.reset(values.get(j)));
				if (value instanceof Comparable)
					this.values.add(valueToRank.getOrCreateEntry((Comparable) value));
				else
					this.values.add(null);
			}
			List<java.util.Map.Entry<Comparable, Integer>> a = CH.l(valueToRank.entrySet());
			Collections.sort(a, SORTER);
			for (int i = 0, l = a.size(); i < l; i++)
				a.get(i).setValue(i + 1);
			for (int i = 0; i < len; i++) {
				Entry<Comparable, Integer> node = (Entry<Comparable, Integer>) this.values.get(i);
				if (node != null)
					this.values.set(i, node.getValue());
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new RankAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	static Comparator<Map.Entry<Comparable, Integer>> SORTER = new Comparator<Map.Entry<Comparable, Integer>>() {

		@Override
		public int compare(java.util.Map.Entry<Comparable, Integer> o1, java.util.Map.Entry<Comparable, Integer> o2) {
			return OH.compare(o1.getKey(), o2.getKey());
		}

	};
}
