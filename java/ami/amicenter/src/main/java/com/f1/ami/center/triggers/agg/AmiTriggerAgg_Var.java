package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiTriggerAgg_Var extends AmiTriggerAgg {

	private Class<?> returnType;
	private boolean isFloat;
	private PrimitiveMath math;
	private boolean isSample = false;

	public static class AggregateHelper {
		private double num = 0;
		private double meanValue = 0;
		private int count = 0;
	}

	public AmiTriggerAgg_Var(int position, DerivedCellCalculator inner) {
		super(position, inner);
		this.math = getPrimitiveMathManager(inner);
		this.returnType = math.getReturnType();
		this.isFloat = returnType == Float.class || returnType == Double.class || returnType == float.class || returnType == double.class;
	}

	public AmiTriggerAgg_Var(int position, DerivedCellCalculator inner, boolean isSample) {
		this(position, inner);
		this.isSample = isSample;
	}

	@Override
	public Class<?> getReturnType() {
		return double.class;
	}
	@Override
	public String getMethodName() {
		return isSample ? "varS" : "var";
	}

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Var(getPosition(), getInner().copy(), isSample);
	}

	@Override
	protected Object calculateInsert(Object nuw, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (nuw instanceof Number) {
			ah.count++;
			double nuwN;
			if (isFloat) {
				nuwN = toDouble(nuw);
			} else {
				nuwN = toLong(nuw);
			}
			double deltaI = nuwN - ah.meanValue;
			ah.meanValue += deltaI / ah.count;
			double deltaI2 = nuwN - ah.meanValue;
			ah.num += deltaI * deltaI2;
		}
		if (isSample)
			return ah.count > 1 ? ah.num / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.num / ah.count : Double.NaN;
	}
	@Override
	protected Object calculateUpdate(Object old, Object nuw, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper,
			CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double oldU;
				if (isFloat) {
					oldU = toDouble(old);
				} else {
					oldU = toLong(old);
				}
				double deltaU = oldU - ah.meanValue;
				ah.meanValue -= deltaU / (ah.count);
				double deltaU2 = oldU - ah.meanValue;
				ah.num -= deltaU * deltaU2;
			} else {
				ah.meanValue = 0;
				ah.num = 0;
			}
		}
		if (nuw instanceof Number) {
			ah.count++;
			double nuwU;
			if (isFloat) {
				nuwU = toDouble(nuw);
			} else {
				nuwU = toLong(nuw);
			}
			double deltaU3 = nuwU - ah.meanValue;
			ah.meanValue += deltaU3 / ah.count;
			double deltaU4 = nuwU - ah.meanValue;
			ah.num += deltaU3 * deltaU4;
		}
		if (isSample)
			return ah.count > 1 ? ah.num / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.num / ah.count : Double.NaN;
	}
	@Override
	protected Object calculateDelete(Object old, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper, CalcFrameStack sf) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double oldN;
				if (isFloat) {
					oldN = toDouble(old);
				} else {
					oldN = toLong(old);
				}
				double deltaD = oldN - ah.meanValue;
				ah.meanValue -= deltaD / ah.count;
				double deltaD2 = oldN - ah.meanValue;
				ah.num -= deltaD * deltaD2;
			} else {
				ah.meanValue = 0;
				ah.num = 0;
			}
		}
		if (isSample)
			return ah.count > 1 ? ah.num / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.num / ah.count : Double.NaN;
	}
	static private double toDouble(Object old) {
		if (old instanceof Number)
			return ((Number) old).doubleValue();
		else
			return 0;
	}
	static private long toLong(Object old) {
		if (old instanceof Number)
			return ((Number) old).longValue();
		else
			return 0L;
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
}
