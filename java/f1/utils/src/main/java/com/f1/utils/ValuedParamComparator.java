package com.f1.utils;

import java.util.Comparator;

import com.f1.base.Valued;
import com.f1.base.ValuedParam;

public class ValuedParamComparator implements Comparator<Valued> {

	private ValuedParam<Valued> vp;
	private char code;

	public ValuedParamComparator(ValuedParam<Valued> vp) {
		if (vp.isPrimitive())
			code = OH.getClassJvmCode(vp.getReturnType());
		else
			code = 'L';
		this.vp = vp;
	}

	@Override
	public int compare(Valued o1, Valued o2) {
		switch (code) {
			case RH.JAVA_SIGNATURE_BOOLEAN:
				return OH.compare(vp.getBoolean(o1), vp.getBoolean(o2));
			case RH.JAVA_SIGNATURE_BYTE:
				return OH.compare(vp.getByte(o1), vp.getByte(o2));
			case RH.JAVA_SIGNATURE_CHAR:
				return OH.compare(vp.getChar(o1), vp.getChar(o2));
			case RH.JAVA_SIGNATURE_DOUBLE:
				return OH.compare(vp.getDouble(o1), vp.getDouble(o2));
			case RH.JAVA_SIGNATURE_FLOAT:
				return OH.compare(vp.getFloat(o1), vp.getFloat(o2));
			case RH.JAVA_SIGNATURE_INT:
				return OH.compare(vp.getInt(o1), vp.getInt(o2));
			case RH.JAVA_SIGNATURE_LONG:
				return OH.compare(vp.getLong(o1), vp.getLong(o2));
			case RH.JAVA_SIGNATURE_SHORT:
				return OH.compare(vp.getShort(o1), vp.getShort(o2));
			default:
				return OH.compare((Comparable) vp.getValue(o1), (Comparable) vp.getValue(o2));
		}
	}

}
