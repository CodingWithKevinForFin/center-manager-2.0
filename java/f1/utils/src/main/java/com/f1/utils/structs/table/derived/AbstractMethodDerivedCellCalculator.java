
package com.f1.utils.structs.table.derived;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractMethodDerivedCellCalculator implements MethodDerivedCellCalculator, DerivedCellCalculator {

	final private DerivedCellCalculator[] params;
	final private int position;

	public AbstractMethodDerivedCellCalculator(int position, DerivedCellCalculator params[]) {
		this.params = params;
		this.position = position;
	}

	@Override
	abstract public Object get(CalcFrameStack sf);

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName());
		if (params.length == 0)
			return sink.append("()");
		for (int i = 0; i < params.length; i++) {
			sink.append(i == 0 ? '(' : ',');
			this.params[i].toString(sink);
		}
		return sink.append(')');
	}

	@Override
	abstract public Class<?> getReturnType();

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		DerivedCellCalculator[] params2 = new DerivedCellCalculator[params.length];
		for (int i = 0; i < this.params.length; i++)
			params2[i] = this.params[i].copy();
		return copy(params2);
	}

	@Override
	public boolean isConst() {
		for (DerivedCellCalculator param : params)
			if (!param.isConst())
				return false;
		return true;
	}

	public String toString() {
		return this.toString(new StringBuilder()).toString();
	}

	@Override
	public int getInnerCalcsCount() {
		return this.params.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.params[n];
	}

	@Override
	public int getParamsCount() {
		return this.params.length;
	}

	@Override
	public DerivedCellCalculator getParamAt(int n) {
		return this.params[n];
	}

	public DerivedCellCalculator[] getParams() {
		return this.params;
	}

	@Override
	public boolean isReadOnly() {
		for (DerivedCellCalculator param : params)
			if (!param.isReadOnly())
				return false;
		return true;
	}

	abstract public DerivedCellCalculator copy(DerivedCellCalculator[] params2);

	@Override
	public String getMethodName() {
		return getDefinition().getMethodName();
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}
}
