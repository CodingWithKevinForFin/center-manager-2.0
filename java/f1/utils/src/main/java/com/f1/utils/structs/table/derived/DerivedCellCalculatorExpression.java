package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorExpression implements DerivedCellCalculator {

	private DerivedCellCalculator inner;
	private String expression;

	public DerivedCellCalculatorExpression(String orig, DerivedCellCalculator inner) {
		OH.assertNotNull(inner);
		OH.assertNotNull(orig);
		this.inner = inner;
		this.expression = orig;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		try {
			return inner.get(lcvs);
		} catch (ExpressionParserException e) {
			SqlPlanListener planListener = lcvs.getSqlPlanListener();
			if (planListener != null)
				planListener.onEndWithError(e);
			if (e.getExpression() == null)
				e.setExpression(getExpression());
			throw e;
		}
	}

	@Override
	public Class<?> getReturnType() {
		return inner.getReturnType();
	}

	@Override
	public int getPosition() {
		return inner.getPosition();
	}

	@Override
	public DerivedCellCalculator copy() {
		return inner.copy();
	}

	@Override
	public boolean isConst() {
		return inner.isConst();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(expression);
	}

	@Override
	public String toString() {
		return this.expression;
	}

	@Override
	public boolean isReadOnly() {
		return inner.isReadOnly();
	}

	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inner;
	}

	public String getExpression() {
		return this.expression;
	}

	@Override
	public Object resume(PauseStack paused) {
		return inner.resume(paused);
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}
}
