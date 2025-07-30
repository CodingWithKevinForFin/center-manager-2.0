package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiTriggerAgg_First extends AmiTriggerAgg {

	private static final Object UNDEF = new Object();

	public static class AggregateHelper {
		private Object currentFirst = UNDEF;
	}

	private ReusableStackFramePool pool;

	public AmiTriggerAgg_First(int position, DerivedCellCalculator inner, ReusableStackFramePool pool) {
		super(position, inner);
		this.pool = pool;
	}

	@Override
	public Class<?> getReturnType() {
		return Integer.class;
	}

	@Override
	public String getMethodName() {
		return "first";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_First(getPosition(), getInner().copy(), pool);
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (ah.currentFirst == UNDEF)
			ah.currentFirst = nuw;
		return ah.currentFirst;
	}

	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (causingSourceRow == sourceRows.getHead())
			ah.currentFirst = nuw;
		return ah.currentFirst;
	}

	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (causingSourceRow == sourceRows.getHead())
			ah.currentFirst = sourceRows.size() == 1 ? UNDEF : getValue(sourceRows.getHeadNode().getNextNode().getValue(), sf);
		return ah.currentFirst == UNDEF ? null : ah.currentFirst;
	}

	private Object getValue(AmiRowImpl causingSourceRow, CalcFrameStack sf) {
		ReusableCalcFrameStack rsf = pool.borrow(sf, causingSourceRow);
		Object r = this.inner.get(rsf);
		pool.release(rsf);
		return r;
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
