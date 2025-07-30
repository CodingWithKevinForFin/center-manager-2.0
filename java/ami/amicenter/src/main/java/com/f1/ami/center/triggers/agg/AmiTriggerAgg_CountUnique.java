package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.IntValueMap;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_CountUnique extends AmiTriggerAgg {

	public static class AggregateHelper {
		private IntValueMap<Object> counts = new IntValueMap<Object>();
		private int count = 0;
	}

	public AmiTriggerAgg_CountUnique(int position, DerivedCellCalculator inner) {
		super(position, inner);
	}

	@Override
	public Class<?> getReturnType() {
		return long.class;
	}
	@Override
	public String getMethodName() {
		return "countUnique";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_CountUnique(getPosition(), getInner().copy());
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (nuw != null) {
			int cntIns = ah.counts.add(nuw, 1);
			if (cntIns == 1) {
				ah.count++;
			}
		}
		return ah.count;
	}
	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old != null) {
			int cntUpdRem = ah.counts.add(old, -1);
			if (cntUpdRem == 0)
				ah.count--;
		}
		if (nuw != null) {
			int cntUpdIns = ah.counts.add(nuw, 1);
			if (cntUpdIns == 1)
				ah.count++;
		}
		return ah.count;
	}
	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old != null) {
			int cntRem = ah.counts.add(old, -1);
			if (cntRem == 0)
				ah.count--;
		}
		return ah.count;
	}
	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
