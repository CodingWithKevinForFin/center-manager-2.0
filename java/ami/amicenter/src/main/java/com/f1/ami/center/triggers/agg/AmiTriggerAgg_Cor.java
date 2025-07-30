package com.f1.ami.center.triggers.agg;

import com.f1.ami.center.table.AmiRowImpl;
import com.f1.utils.MH;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.math.PrimitiveMath;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiTriggerAgg_Cor extends AmiTriggerAgg2 {

	private PrimitiveMath math1;
	private PrimitiveMath math2;
	private Class<?> returnType1;
	private Class<?> returnType2;
	private boolean isFloat1;
	private boolean isFloat2;

	public static class AggregateHelper {
		private double Mx = 0;
		private double My = 0;
		private double Sx = 0;
		private double Sy = 0;
		private double Cxy = 0;
		private int count = 0;
	}

	public AmiTriggerAgg_Cor(int position, DerivedCellCalculator inner, DerivedCellCalculator inner2) {
		super(position, inner, inner2);
		this.math1 = getPrimitiveMathManager(inner);
		this.math2 = getPrimitiveMathManager(inner2);
		this.returnType1 = math1.getReturnType();
		this.returnType2 = math2.getReturnType();
		this.isFloat1 = returnType1 == Float.class || returnType1 == Double.class || returnType1 == float.class || returnType1 == double.class;
		this.isFloat2 = returnType2 == Float.class || returnType2 == Double.class || returnType2 == float.class || returnType2 == double.class;
	}

	@Override
	public Class<?> getReturnType() {
		return double.class;
	}
	@Override
	public String getMethodName() {
		return "cor";
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

	@Override
	public DerivedCellCalculator copy() {
		return new AmiTriggerAgg_Cor(getPosition(), getInner1().copy(), getInner2().copy());
	}

	@Override
	public boolean needsHelper() {
		return true;
	}
	public Object initHelper() {
		return new AggregateHelper();
	};
	private Object calculateResult(AggregateHelper ah) {
		return ah.count > 1 ? ah.Cxy / Math.sqrt(ah.Sx * ah.Sy) : Double.NaN;
	}
	private void addElement(Object objX, Object objY, AggregateHelper ah) {
		if (objX instanceof Number && objY instanceof Number) {
			ah.count++;
			double x = toDouble(objX, isFloat1);
			double y = toDouble(objY, isFloat2);

			double dx = x - ah.Mx;
			double dy = y - ah.My;
			// cov(x, y)
			ah.Mx += dx / ah.count;
			ah.My += dy / ah.count;
			ah.Cxy += dx * (y - ah.My);

			// var(x)
			ah.Sx += dx * (x - ah.Mx);

			// var(y)
			ah.Sy += dy * (y - ah.My);

			assertLegalValues(ah);
		}
	}
	private void removeElement(Object objX, Object objY, AggregateHelper ah) {
		if (objX instanceof Number && objY instanceof Number) {
			ah.count--;
			if (ah.count != 0) {
				double x = toDouble(objX, isFloat1);
				double y = toDouble(objY, isFloat2);

				double dx = x - ah.Mx;
				double dy = y - ah.My;
				// cov(x, y)
				ah.Mx -= dx / ah.count;
				ah.My -= dy / ah.count;
				ah.Cxy -= dx * (y - ah.My);

				// var(x)
				ah.Sx -= dx * (x - ah.Mx);

				// var(y)
				ah.Sy -= dy * (y - ah.My);

				assertLegalValues(ah);
			} else {
				ah.Mx = 0;
				ah.My = 0;
				ah.Sx = 0;
				ah.Sy = 0;
				ah.Cxy = 0;
			}
		}
	}

	private static void assertLegalValues(AggregateHelper ah) {
		if (ah.Sx < 0 || MH.isClose(ah.Sx, 0))
			ah.Sx = 0;
		if (ah.Sy < 0 || MH.isClose(ah.Sy, 0))
			ah.Sy = 0;
	}

}
