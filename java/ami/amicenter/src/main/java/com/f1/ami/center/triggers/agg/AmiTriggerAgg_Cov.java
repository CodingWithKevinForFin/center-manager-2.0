package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiTriggerAgg_Cov extends AmiTriggerAgg2 {

	private PrimitiveMath math1;
	private PrimitiveMath math2;
	private Class<?> returnType1;
	private Class<?> returnType2;
	private boolean isFloat1;
	private boolean isFloat2;
	private boolean isSample = false;

	public static class AggregateHelper {
		private double mean1 = 0;
		private double mean2 = 0;
		private double sum1m2 = 0;
		private int count = 0;
	}

	public AmiTriggerAgg_Cov(int position, DerivedCellCalculator inner, DerivedCellCalculator inner2) {
		super(position, inner, inner2);
		this.math1 = getPrimitiveMathManager(inner);
		this.math2 = getPrimitiveMathManager(inner2);
		this.returnType1 = math1.getReturnType();
		this.returnType2 = math2.getReturnType();
		this.isFloat1 = returnType1 == Float.class || returnType1 == Double.class || returnType1 == float.class || returnType1 == double.class;
		this.isFloat2 = returnType2 == Float.class || returnType2 == Double.class || returnType2 == float.class || returnType2 == double.class;
	}
	public AmiTriggerAgg_Cov(int position, DerivedCellCalculator inner, DerivedCellCalculator inner2, boolean isSample) {
		this(position, inner, inner2);
		this.isSample = isSample;
	}
	@Override
	public Class<?> getReturnType() {
		return double.class;
	}
	@Override
	public String getMethodName() {
		return isSample ? "covarS" : "covar";
	}
	@Override
	Object calculateInsert(Object nuw1, Object nuw2, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (nuw1 instanceof Number && nuw2 instanceof Number) {
			ah.count++;
			double nuwN1;
			double nuwN2;
			if (isFloat1) {
				nuwN1 = toDouble(nuw1);
			} else {
				nuwN1 = toLong(nuw1);
			}

			if (isFloat2) {
				nuwN2 = toDouble(nuw2);
			} else {
				nuwN2 = toLong(nuw2);
			}

			double delta1 = nuwN1 - ah.mean1;
			double delta2 = nuwN2 - ah.mean2;
			ah.mean1 += delta1 / ah.count;
			ah.mean2 += delta2 / ah.count;

			ah.sum1m2 += nuwN1 * nuwN2;
		}
		if (isSample)
			return ah.count > 1 ? ah.sum1m2 / (ah.count - 1) - ah.count * (ah.mean1 * ah.mean2) / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.sum1m2 / ah.count - (ah.mean1 * ah.mean2) : Double.NaN;
	}
	@Override
	Object calculateUpdate(Object old1, Object old2, Object nuw1, Object nuw2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows,
			Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old1 instanceof Number && old2 instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double oldN1;
				double oldN2;
				if (isFloat1) {
					oldN1 = toDouble(old1);
				} else {
					oldN1 = toLong(old1);
				}

				if (isFloat2) {
					oldN2 = toDouble(old2);
				} else {
					oldN2 = toLong(old2);
				}

				double delta1 = oldN1 - ah.mean1;
				double delta2 = oldN2 - ah.mean2;
				ah.mean1 -= delta1 / ah.count;
				ah.mean2 -= delta2 / ah.count;

				ah.sum1m2 -= oldN1 * oldN2;
			} else {
				ah.mean1 = 0;
				ah.mean2 = 0;
				ah.sum1m2 = 0;
			}
		}
		if (nuw1 instanceof Number && nuw2 instanceof Number) {
			ah.count++;
			double nuwN1;
			double nuwN2;
			if (isFloat1) {
				nuwN1 = toDouble(nuw1);
			} else {
				nuwN1 = toLong(nuw1);
			}

			if (isFloat2) {
				nuwN2 = toDouble(nuw2);
			} else {
				nuwN2 = toLong(nuw2);
			}

			double delta1 = nuwN1 - ah.mean1;
			double delta2 = nuwN2 - ah.mean2;
			ah.mean1 += delta1 / ah.count;
			ah.mean2 += delta2 / ah.count;

			ah.sum1m2 += nuwN1 * nuwN2;
		}
		if (isSample)
			return ah.count > 1 ? ah.sum1m2 / (ah.count - 1) - ah.count * (ah.mean1 * ah.mean2) / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.sum1m2 / ah.count - (ah.mean1 * ah.mean2) : Double.NaN;
	}
	@Override
	Object calculateDelete(Object old1, Object old2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		if (old1 instanceof Number && old2 instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double oldN1;
				double oldN2;
				if (isFloat1) {
					oldN1 = toDouble(old1);
				} else {
					oldN1 = toLong(old1);
				}

				if (isFloat2) {
					oldN2 = toDouble(old2);
				} else {
					oldN2 = toLong(old2);
				}

				double delta1 = oldN1 - ah.mean1;
				double delta2 = oldN2 - ah.mean2;
				ah.mean1 -= delta1 / ah.count;
				ah.mean2 -= delta2 / ah.count;

				ah.sum1m2 -= oldN1 * oldN2;
			} else {
				ah.mean1 = 0;
				ah.mean2 = 0;
				ah.sum1m2 = 0;
			}
		}
		if (isSample)
			return ah.count > 1 ? ah.sum1m2 / (ah.count - 1) - ah.count * (ah.mean1 * ah.mean2) / (ah.count - 1) : Double.NaN;
		else
			return ah.count > 0 ? ah.sum1m2 / ah.count - (ah.mean1 * ah.mean2) : Double.NaN;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Cov(getPosition(), getInner1().copy(), getInner2().copy());
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
