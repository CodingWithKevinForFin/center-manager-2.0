package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

abstract public class VarAggCalculatorAbstract extends AbstractAggCalculator {

	private Class returnType;
	private boolean isNull;
	private double doubleValue;
	private final boolean population;

	public VarAggCalculatorAbstract(int position, DerivedCellCalculator inner, boolean population) {
		super(position, inner);
		PrimitiveMath manager = getPrimitiveMathManager(inner);
		this.returnType = manager.getReturnType();
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
			long cnt = 0;
			Object objX;
			double x;
			double M = 0;
			double S = 0;
			double prevM;
			// Welford's Algorithm for computing variance
			for (int i = 0; i < values.size(); i++) {
				sf.reset(values.get(i));
				objX = this.inner.get(sf);
				if (objX instanceof Number) {
					cnt++;
					x = ((Number) objX).doubleValue();
					prevM = M;
					M += (x - M) / cnt;
					S += (x - M) * (x - prevM);
				}
			}
			if (cnt == 0)
				isNull = true;
			else
				this.doubleValue = S / (this.population ? cnt : Math.max(cnt - 1, 0));
		}
	}
	@Override
	public Class<?> getReturnType() {
		return Double.class;
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
	public boolean isSame(DerivedCellCalculator other) {
		return super.isSame(other) && ((VarAggCalculatorAbstract) other).population == this.population;
	}
	@Override
	public void visitRows(CalcFrameStack values, long count) {
		setValue(0D);
	}

}
