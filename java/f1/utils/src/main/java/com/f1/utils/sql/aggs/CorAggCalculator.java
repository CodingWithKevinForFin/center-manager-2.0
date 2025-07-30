package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CorAggCalculator extends AbstractAggCalculator {
	public static final String METHOD_NAME = "cor";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Double.class, "Number value1,Number value2");
		paramsDefinition.addDesc("Correlation of 1st value and 2nd value where both aren't null. Returns 0 - 1 or NaN.");
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
			return new CorAggCalculator(position, calcs[0], calcs[1]);
		}
	};
	private boolean isNull;
	private double doubleValue;
	private DerivedCellCalculator argTwo;

	public CorAggCalculator(int position, DerivedCellCalculator argOne, DerivedCellCalculator argTwo) {
		super(position, argOne, argTwo);
		this.argTwo = argTwo;
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return this.isNull ? null : this.doubleValue;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		if (values.isEmpty())
			isNull = true;
		else {
			isNull = false;
			doubleValue = 0;
			Object objX, objY;
			double x, y;
			double dx = 0;
			double dy = 0;
			double C = 0;
			double Mx = 0;
			double My = 0;
			double Sx = 0;
			double Sy = 0;
			long cnt = 0;
			for (int i = 0; i < values.size(); i++) {
				sf.reset(values.get(i));
				objX = this.inner.get(sf);
				objY = this.argTwo.get(sf);
				if (objX instanceof Number && objY instanceof Number) {
					cnt++;
					x = ((Number) objX).doubleValue();
					y = ((Number) objY).doubleValue();

					// cov(x, y)
					dx = x - Mx;
					dy = y - My;
					Mx += dx / cnt;
					My += dy / cnt;
					C += dx * (y - My);

					// var(x)
					Sx += (x - Mx) * dx;

					// var(y)
					Sy += (y - My) * dy;

				}
			}
			if (cnt == 0)
				isNull = true;
			else
				this.doubleValue = C / (Math.sqrt(Sx) * Math.sqrt(Sy));
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CorAggCalculator(getPosition(), inner.copy(), argTwo.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	@Override
	public String getMethodName() {
		return "cor";
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
		setValue(Double.NaN);
	}
}
