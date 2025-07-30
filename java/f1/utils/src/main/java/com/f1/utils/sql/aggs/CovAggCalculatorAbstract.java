package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class CovAggCalculatorAbstract extends AbstractAggCalculator {

	private final DerivedCellCalculator argTwo;
	private boolean isNull;
	private double doubleValue;
	private final boolean population;

	public CovAggCalculatorAbstract(int position, DerivedCellCalculator argOne, DerivedCellCalculator argTwo, boolean population) {
		super(position, argOne, argTwo);
		getPrimitiveMathManager(argOne);
		getPrimitiveMathManager(argTwo);
		this.argTwo = argTwo;
		this.population = population;
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
			long cnt = 0;
			double meanx = 0;
			double meany = 0;
			double C = 0;
			double dx = 0;
			// Welford's algorithm
			for (int i = 0; i < values.size(); i++) {
				sf.reset(values.get(i));
				objX = this.inner.get(sf);
				objY = this.argTwo.get(sf);
				if (objX instanceof Number && objY instanceof Number) {
					cnt++;
					x = ((Number) objX).doubleValue();
					y = ((Number) objY).doubleValue();
					dx = x - meanx;
					meanx += dx / cnt;
					meany += (y - meany) / cnt;
					C += dx * (y - meany);
				}
			}
			if (cnt == 0)
				isNull = true;
			else
				this.doubleValue = C / (this.population ? cnt : Math.max(cnt - 1, 0));
		}
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
	}
	protected DerivedCellCalculator getArgTwo() {
		return this.argTwo;
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
}
