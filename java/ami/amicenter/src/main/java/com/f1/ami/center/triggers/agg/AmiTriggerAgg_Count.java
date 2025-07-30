package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_Count extends AmiTriggerAgg {

	public AmiTriggerAgg_Count(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public String getMethodName() {
		return "count";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Count(getPosition(), getInner().copy());
	}

	@Override
	public Object calculateInsert(Object nuw, Object cur, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		int curN = cur == null ? 0 : ((Integer) cur).intValue();
		int nuwN = nuw == null ? 0 : 1;
		return curN + nuwN;
	}

	public Object calculateUpdate(Object old, Object nuw, Object cur, AmiRowImpl causingSourceRow, com.f1.utils.concurrent.LinkedHasherSet<AmiRowImpl> sourceRows,
			Object aggregateHelper, CalcFrameStack sf) {
		int curN = cur == null ? 0 : ((Integer) cur).intValue();
		int oldN = old == null ? 0 : 1;
		int nuwN = nuw == null ? 0 : 1;
		return (nuwN - oldN) + curN;
	};
	@Override
	public Object calculateDelete(Object old, Object cur, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		int curN = cur == null ? 0 : ((Integer) cur).intValue();
		int oldN = old == null ? 0 : 1;
		return curN - oldN;
	}
}
