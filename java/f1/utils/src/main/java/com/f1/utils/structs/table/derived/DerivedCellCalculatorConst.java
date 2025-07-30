package com.f1.utils.structs.table.derived;

import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorConst implements DerivedCellCalculator {

	private final Object value;
	private final Class type;
	private int position;

	public DerivedCellCalculatorConst(int textPosition, Object value) {
		this(textPosition, value, value == null ? Object.class : value.getClass());
	}
	public DerivedCellCalculatorConst(int textPosition, Object value, Class<?> type) {
		this.position = textPosition;
		this.value = value;
		this.type = type;
	}

	@Override
	public Object get(CalcFrameStack key) {
		return value;
	}

	@Override
	public Class<?> getReturnType() {
		return type;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		return toString(value, sink);
	}
	public static StringBuilder toString(Object value, StringBuilder sink) {
		if (value == null)
			sink.append("null");
		else if (value instanceof String)
			SH.quoteToJavaConst('"', (String) value, sink);
		else if (value instanceof Number) {
			if (value instanceof Long)
				sink.append(value).append('L');
			else if (value instanceof Float)
				sink.append(value).append('F');
			else if (value instanceof Double)
				sink.append(value).append('D');
			else
				sink.append(value);
		} else if (value instanceof ToDerivedString)
			((ToDerivedString) value).toDerivedString(sink);
		else if (value instanceof ToStringable)
			((ToStringable) value).toString(sink);
		else
			sink.append(value);
		return sink;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorConst.class)
			return false;
		DerivedCellCalculatorConst o = (DerivedCellCalculatorConst) other;
		return OH.eq(value, o.value) && OH.eq(type, o.type);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(value);
	}
	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	@Override
	public DerivedCellCalculatorConst copy() {
		return new DerivedCellCalculatorConst(position, value, type);
	}
	@Override
	public boolean isConst() {
		return true;
	}
	@Override
	public boolean isReadOnly() {
		return true;
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}
	@Override
	public Object resume(PauseStack paused) {
		throw new IllegalStateException();
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorConst o = (DerivedCellCalculatorConst) other;
		return OH.eq(this.value, o.value) && OH.eq(this.type, o.type);
	}
}
