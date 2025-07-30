package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.OH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTriggerAgg_Max extends AmiTriggerAgg {

	public static class AggregateHelper {

		private Object currentMaxValue = null;
		private int count = 0;
	}

	final private ReusableStackFramePool pool;

	public AmiTriggerAgg_Max(int position, DerivedCellCalculator inner, ReusableStackFramePool pool) {
		super(position, inner);
		this.pool = pool;
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public String getMethodName() {
		return "max";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Max(getPosition(), getInner().copy(), this.pool);
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (ah.count == 0) {
			ah.currentMaxValue = nuw;
			ah.count = 1;
		} else {
			int n = compare(ah.currentMaxValue, nuw);
			if (n == 0)
				ah.count++;
			else if (n < 0) {
				ah.currentMaxValue = nuw;
				ah.count = 1;
			}
		}
		return ah.currentMaxValue;
	}
	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (compare(old, ah.currentMaxValue) == 0) {//we are update from the current max
			if (compare(old, nuw) > 0) {//the value is going down 
				if (--ah.count == 0) {//the max has gone down 
					getMax(sourceRows, causingSourceRow, ah, sf);
					int n = compare(ah.currentMaxValue, nuw);//check to see if the updating-to value is greater than or equal to the new max
					if (n == 0)
						ah.count++;
					else if (n < 0) {
						ah.currentMaxValue = nuw;
						ah.count = 1;
					}
				}
			} else {//the value is going up
				ah.currentMaxValue = nuw;
				ah.count = 1;
			}
		} else {
			int n = compare(ah.currentMaxValue, nuw);
			if (n == 0)
				ah.count++;
			else if (n < 0) {
				ah.currentMaxValue = nuw;
				ah.count = 1;
			}
		}
		return ah.currentMaxValue;
	}
	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (compare(old, ah.currentMaxValue) == 0) {
			if (--ah.count == 0)
				getMax(sourceRows, causingSourceRow, ah, sf);
		}
		return ah.currentMaxValue;
	}

	private void getMax(LinkedHasherSet<AmiRowImpl> sourceRows, AmiRowImpl skip, AggregateHelper ah, CalcFrameStack sf) {
		Object max = null;
		int cnt = 0;
		if (!sourceRows.isEmpty()) {
			boolean first = true;
			ReusableCalcFrameStack rsf = this.pool.borrow(sf, EmptyCalcFrame.INSTANCE);
			for (AmiRowImpl i : sourceRows) {
				if (i == skip)
					continue;
				Object v = getValue(rsf.reset(i));
				if (first) {
					max = v;
					cnt = 1;
					first = false;
				} else {
					int n = compare(max, v);
					if (n == 0)
						cnt++;
					else if (n < 0) {
						max = v;
						cnt = 1;
					}
				}
			}
			pool.release(rsf);
		}
		ah.currentMaxValue = max;
		ah.count = cnt;
	}

	private int compare(Object old, Object nuw) {
		if (old == null)
			return -1;
		if (nuw == null)
			return 1;
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
