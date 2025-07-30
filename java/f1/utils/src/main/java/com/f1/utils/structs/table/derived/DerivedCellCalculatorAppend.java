package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.node.StringTemplateNode;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorAppend implements DerivedCellCalculator {

	final private int position;
	final private String value;

	public DerivedCellCalculatorAppend(int position, String text, char escape) {
		this.position = position;
		if (escape != StringTemplateNode.NO_ESCAPE) {
			StringBuilder sb = new StringBuilder();
			sb.append(escape);
			if (text != null)
				SH.toStringEncode(text, escape, sb);
			sb.append(escape);
			this.value = sb.toString();
		} else if (text != null)
			this.value = text;
		else
			this.value = null;
	}

	private DerivedCellCalculatorAppend(int position, String value) {
		this.position = position;
		this.value = value;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return value;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(value);
	}

	@Override
	public Class<?> getReturnType() {
		return String.class;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorAppend(position, value);
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
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public int getInnerCalcsCount() {
		return 0;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		throw new IndexOutOfBoundsException("" + n);
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
		DerivedCellCalculatorAppend o = (DerivedCellCalculatorAppend) other;
		return OH.eq(value, o.value);
	}

}
