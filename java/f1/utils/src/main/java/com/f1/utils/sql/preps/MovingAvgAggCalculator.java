package com.f1.utils.sql.preps;

import java.util.ArrayList;

import com.f1.base.CalcFrame;
import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class MovingAvgAggCalculator extends AbstractPrepCalculator {
	public static final String METHOD_NAME = "movingAvg";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Number.class, "Number value,Integer sampleSize,String options");
		paramsDefinition.addDesc("moving average of nearest sampleSize records");
		paramsDefinition.addParamDesc(0, "data to calculate avg from");
		paramsDefinition.addParamDesc(1, "number of records to consider");
		paramsDefinition.addParamDesc(2, "Either MID,BEFORE,AFTER (null defaults to MID)");
		paramsDefinition.addAdvancedExample(
				"CREATE TABLE input(symbol String, id int, price double);\nINSERT INTO input VALUES (\"CAT\", 1, 50),(\"CAT\", 2, 54),(\"CAT\", 3, 52),(\"CAT\", 4, 52),(\"CAT\", 5, null),(\"CAT\", 6, 57);\nINSERT INTO input VALUES (\"IBM\", 1, 20),(\"IBM\", 2, 24),(\"IBM\", 3, 22),(\"IBM\", 4, 27),(\"IBM\", 5, null);\nCREATE TABLE result AS PREPARE *,movingAvg(price,3,\"MID\") AS movAvg3Mid, movingAvg(price,2,\"BEFORE\") AS movAvg2Before FROM input PARTITION BY symbol;\nTable input = SELECT * FROM input;\nTABLE result = SELECT * FROM result;",
				new String[] { "input", "result" });
	}
	public final static PrepMethodFactory FACTORY = new PrepMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractPrepCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new MovingAvgAggCalculator(position, calcs[0], calcs[1], calcs[2]);
		}
	};
	private boolean isNull;
	private ArrayList<Number> values = new ArrayList<Number>();
	private DerivedCellCalculator sizeCalc;
	private DerivedCellCalculator optionsCalc;

	public MovingAvgAggCalculator(int position, DerivedCellCalculator val, DerivedCellCalculator sizeCalc, DerivedCellCalculator optionsCalc) {
		super(position, val, sizeCalc, optionsCalc);
		this.sizeCalc = sizeCalc;
		this.optionsCalc = optionsCalc;
	}
	@Override
	public Object get(CalcFrameStack lcvs, int position) {
		return isNull ? null : values.get(position);
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, PrepRows values) {
		this.values.clear();
		this.values.ensureCapacity(values.size());
		if (values.isEmpty()) {
			isNull = true;
			return;
		}
		sf.reset(values.get(0));
		String options = (String) optionsCalc.get(sf);
		Integer sampleSize = (Integer) sizeCalc.get(sf);
		if (sampleSize == null || sampleSize.intValue() < 1) {
			isNull = true;
			return;
		}
		int skip;
		if (SH.isnt(options) || "MID".equalsIgnoreCase(options)) {
			skip = sampleSize / 2;
		} else if ("BEFORE".equalsIgnoreCase(options))
			skip = 0;
		else if ("AFTER".equalsIgnoreCase(options)) {
			skip = sampleSize - 1;
		} else {
			isNull = true;
			return;
		}

		isNull = false;
		Window w = new Window(sampleSize.intValue());
		for (int i = 0; i < values.size(); i++) {
			w.push(getDoubleValue(sf, values, i));
			if (skip > 0)
				skip--;
			else
				this.values.add(w.getAvg());
			if (w.isFull())
				w.pop();
		}
		int skipAfter = sampleSize.intValue() - w.size() - 1;
		while (this.values.size() < values.size()) {
			this.values.add(w.getAvg());
			if (skipAfter > 0)
				skipAfter--;
			else
				w.pop();
		}

	}

	private double getDoubleValue(ReusableCalcFrameStack sf, PrepRows values, int i) {
		CalcFrame row = values.get(i);
		Object value = inner.get(sf.reset(row));
		if (value instanceof Number) {
			double doubleValue = ((Number) value).doubleValue();
			if (MH.isNumber(doubleValue))
				return doubleValue;
		}
		return Double.NaN;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new MovingAvgAggCalculator(getPosition(), inners[0].copy(), inners[1].copy(), inners[2].copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	private static class Window {
		private double[] values;
		private double numerator;
		private int denominator;
		private int head;
		private int tail;
		private int size;

		public Window(int size) {
			this.values = new double[size];
		}

		public boolean isFull() {
			return this.values.length == size;
		}

		public void push(double d) {
			size++;
			if (size > values.length)
				throw new IllegalStateException();
			values[head] = d;
			head++;
			head %= values.length;
			if (MH.isNumber(d)) {
				numerator += d;
				denominator++;
			}
		}
		public double pop() {
			size--;
			if (size < 0)
				throw new IllegalStateException();
			double r = values[tail];
			tail++;
			tail %= values.length;
			if (MH.isNumber(r)) {
				numerator -= r;
				denominator--;
			}
			return r;
		}

		public int size() {
			return size;
		}
		public double getAvg() {
			return denominator == 0 ? Double.NaN : numerator / denominator;
		}

	}

}