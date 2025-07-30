package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTriggerAgg_Min extends AmiTriggerAgg {

	public static class AggregateHelper {

		private Object currentMinValue = null;
		private int count = 0;
	}

	final private ReusableStackFramePool pool;

	public AmiTriggerAgg_Min(int position, DerivedCellCalculator inner, ReusableStackFramePool pool) {
		super(position, inner);
		this.pool = pool;
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public String getMethodName() {
		return "min";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Min(getPosition(), getInner().copy(), this.pool);
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (ah.count == 0) {
			ah.currentMinValue = nuw;
			ah.count = 1;
		} else {
			int n = compare(ah.currentMinValue, nuw);
			if (n == 0)
				ah.count++;
			else if (n > 0) {
				ah.currentMinValue = nuw;
				ah.count = 1;
			}
		}
		return ah.currentMinValue;
	}
	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (compare(old, ah.currentMinValue) == 0) {//we are update from the current min
			if (compare(old, nuw) < 0) {//the value is going up
				if (--ah.count == 0) {//the min has gone up
					getMin(sourceRows, causingSourceRow, ah, sf);
					int n = compare(ah.currentMinValue, nuw);//check to see if the updating-to value is less than or equal to the new min
					if (n == 0)
						ah.count++;
					else if (n > 0) {
						ah.currentMinValue = nuw;
						ah.count = 1;
					}
				}
			} else {//the value is going down
				ah.currentMinValue = nuw;
				ah.count = 1;
			}
		} else {
			int n = compare(ah.currentMinValue, nuw);
			if (n == 0)
				ah.count++;
			else if (n > 0) {
				ah.currentMinValue = nuw;
				ah.count = 1;
			}
		}
		return ah.currentMinValue;
	}
	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (compare(old, ah.currentMinValue) == 0) {
			if (--ah.count == 0)
				getMin(sourceRows, causingSourceRow, ah, sf);
		}
		return ah.currentMinValue;
	}

	private void getMin(LinkedHasherSet<AmiRowImpl> sourceRows, AmiRowImpl skip, AggregateHelper ah, CalcFrameStack sf) {
		Object min = null;
		int cnt = 0;
		if (!sourceRows.isEmpty()) {
			boolean first = true;
			ReusableCalcFrameStack rsf = this.pool.borrow(sf, EmptyCalcFrame.INSTANCE);
			for (AmiRowImpl i : sourceRows) {
				if (i == skip)
					continue;
				Object v = getValue(rsf.reset(i));
				if (first) {
					min = v;
					cnt = 1;
					first = false;
				} else {
					int n = compare(min, v);
					if (n == 0)
						cnt++;
					else if (n > 0) {
						min = v;
						cnt = 1;
					}
				}
			}
		}
		ah.currentMinValue = min;
		ah.count = cnt;
	}

	private int compare(Object old, Object nuw) {
		if (old == null)
			return 1;
		if (nuw == null)
			return -1;
		return OH.compare((Comparable) old, (Comparable) nuw);
	}

	private Object getValue(ReusableCalcFrameStack sf) {
		return this.inner.get(sf);
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
