package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.MH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiTriggerAgg_Beta extends AmiTriggerAgg2 {

	private PrimitiveMath math1;
	private PrimitiveMath math2;
	private Class<?> returnType1;
	private Class<?> returnType2;
	private boolean isFloatX;
	private boolean isFloatY;

	public static class AggregateHelper {
		private double meanx = 0;
		private double meany = 0;
		private double M2 = 0; // Variance numerator
		private double C = 0; // Covariance numerator
		private int count = 0;
	}

	public AmiTriggerAgg_Beta(int position, DerivedCellCalculator inner, DerivedCellCalculator inner2) {
		super(position, inner, inner2);
		this.math1 = getPrimitiveMathManager(inner);
		this.math2 = getPrimitiveMathManager(inner2);
		this.returnType1 = math1.getReturnType();
		this.returnType2 = math2.getReturnType();
		this.isFloatX = returnType1 == Float.class || returnType1 == Double.class || returnType1 == float.class || returnType1 == double.class;
		this.isFloatY = returnType2 == Float.class || returnType2 == Double.class || returnType2 == float.class || returnType2 == double.class;
	}

	@Override
	public Class<?> getReturnType() {
		return double.class;
	}
	@Override
	public String getMethodName() {
		return "beta";
	}
	@Override
	Object calculateInsert(Object nuw1, Object nuw2, Object current, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		addElement(nuw1, nuw2, ah);
		return calculateResult(ah);
	}

	@Override
	Object calculateUpdate(Object old1, Object old2, Object nuw1, Object nuw2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows,
			Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		removeElement(old1, old2, ah);
		addElement(nuw1, nuw2, ah);
		return calculateResult(ah);
	}
	@Override
	Object calculateDelete(Object old1, Object old2, Object current, AmiRowImpl causingSourceRow, LinkedHasherSet<AmiRowImpl> sourceRows, Object aggregateHelper) {
		AggregateHelper ah = (AggregateHelper) aggregateHelper;
		removeElement(old1, old2, ah);
		return calculateResult(ah);
	}

	private double calculateResult(AggregateHelper ah) {
		return ah.count > 1 ? (ah.C - ah.count * ah.meanx * ah.meany) / ah.M2 : Double.NaN;
	}

	private void addElement(Object objX, Object objY, AggregateHelper ah) {
		if (objX instanceof Number && objY instanceof Number) {
			ah.count++;
			double x = toDouble(objX, isFloatX);
			double y = toDouble(objY, isFloatY);

			double dx = x - ah.meanx;
			double dy = y - ah.meany;
			ah.meanx += dx / ah.count;
			ah.meany += dy / ah.count;
			double dy2 = y - ah.meany;
			ah.M2 += dy * dy2;
			if (ah.M2 < 0) {
				ah.M2 = 0;
			}
			if (MH.isClose(ah.M2, 0))
				ah.M2 = 0;
			ah.C += x * y;
		}
	}
	private void removeElement(Object objX, Object objY, AggregateHelper ah) {
		if (objX instanceof Number && objY instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double x = toDouble(objX, isFloatX);
				double y = toDouble(objY, isFloatY);

				double dx = x - ah.meanx;
				double dy = y - ah.meany;
				ah.meanx -= dx / ah.count;
				ah.meany -= dy / ah.count;
				double dy2 = y - ah.meany;
				ah.M2 -= dy * dy2;
				if (ah.M2 < 0) {
					ah.M2 = 0;
				}
				if (MH.isClose(ah.M2, 0))
					ah.M2 = 0;
				ah.C -= x * y;
			} else {
				ah.meanx = 0;
				ah.meany = 0;
				ah.M2 = 0;
				ah.C = 0;
			}
		}
	}
	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Beta(getPosition(), getInner1().copy(), getInner2().copy());
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
