package com.f1.utils.sql.aggs;

import com.f1.utils.structs.table.stack.CalcFrameStack;

public interface AggDeltaCalculator extends AggCalculator {

	public static final Object NOT_AGGEGATED = new Object() {
		@Override
		public String toString() {
			return "NOT_AGGREGATED";
		}
	};

	public abstract Object applyDelta(Object aggValue, Object oldValue, Object newValue);
	public abstract Object getUnderlying(CalcFrameStack sf);
}
