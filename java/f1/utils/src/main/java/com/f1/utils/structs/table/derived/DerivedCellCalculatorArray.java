package com.f1.utils.structs.table.derived;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorArray implements DerivedCellCalculator {

	private final DerivedCellCalculator params[];
	private final int position;

	public DerivedCellCalculatorArray(int position, DerivedCellCalculator[] params) {
		this.position = position;
		this.params = params;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('[');
		for (int i = 0; i < params.length; i++) {
			if (i > 0)
				sink.append(',');
			params[i].toString(sink);
		}
		return sink.append(']');
	}

	@Override
	public Object get(CalcFrameStack sf) {
		List r = new ArrayList(this.params.length);
		for (int i = 0; i < params.length; i++) {
			r.add(params[i].get(sf));
		}
		return r;
	}

	@Override
	public Class<?> getReturnType() {
		return List.class;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorArray(this.position, DerivedHelper.copy(this.params));
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
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
	public Object resume(PauseStack paused) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return DerivedHelper.areSame(this.params, ((DerivedCellCalculatorArray) other).params);
	}

}
