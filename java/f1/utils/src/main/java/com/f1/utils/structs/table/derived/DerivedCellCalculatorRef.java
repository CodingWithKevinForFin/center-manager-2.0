package com.f1.utils.structs.table.derived;

import java.util.Set;

import com.f1.base.NameSpaceCalcFrame;
import com.f1.base.NameSpaceIdentifier;
import com.f1.utils.OH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorRef implements DerivedCellCalculatorWithDependencies {
	private static final int VAR = -1;
	private static final int CONST = -2;
	private static final int NAMESPACE = -3;

	private Class<?> type;
	private Object id;
	private int loc = VAR;
	private int position;
	private Object value;

	public DerivedCellCalculatorRef(int position, Class<?> type, Object id) {
		this.position = position;
		this.type = OH.getBoxed(type);
		setId(id);
	}

	public static String stripTicks(String s) {
		int len = s.length();
		if (len > 2 && s.charAt(0) == '`' && s.charAt(len - 1) == '`')
			return s.substring(1, len - 1);
		return s;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	@Override
	public Object get(CalcFrameStack key) {
		switch (loc) {
			case VAR:
				return DerivedHelper.getValue(key, (String) id);
			case CONST:
				return value;
			case NAMESPACE:
				final NameSpaceCalcFrame frame = DerivedHelper.getNameSpaceCalcFrame(key);
				int loc = frame.getPosition((NameSpaceIdentifier) id);
				return loc != -1 ? frame.getAt(loc) : frame.getValue(((NameSpaceIdentifier) id).getVarName());
			default:
				NameSpaceCalcFrame t = DerivedHelper.getNameSpaceCalcFrame(key);
				return t == null ? null : t.getAt(this.loc);
		}
	}
	public void setConst(Object value) {
		this.loc = CONST;
		this.value = value;
	}

	@Override
	public Class<?> getReturnType() {
		return this.type;
	}

	@Override
	public Set<Object> getDependencyIds(Set<Object> sink) {
		if (!isConst())
			sink.add(id);
		return sink;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		return sink.append(id);
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		OH.assertNull(this.id);
		if (id instanceof NameSpaceIdentifier)
			loc = NAMESPACE;
		this.id = id;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorRef.class)
			return false;
		DerivedCellCalculatorRef o = (DerivedCellCalculatorRef) other;
		return OH.eq(id, o.id) && OH.eq(type, o.type);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(id, type);
	}
	@Override
	public int getPosition() {
		return position;
	}
	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorRef(position, type, id);
	}
	@Override
	public boolean isConst() {
		return this.loc == CONST;
	}

	@Override
	public String toString() {
		return OH.toString(id);
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
		DerivedCellCalculatorRef o = (DerivedCellCalculatorRef) other;
		if (OH.ne(type, o.type))
			return false;
		if (OH.ne(id, o.id))
			return false;
		//		if (OH.ne(loc, o.loc))
		//			return false;
		//		if (OH.ne(origId, o.origId))
		//			return false;
		if (OH.ne(value, o.value))
			return false;
		return true;
	}

}
