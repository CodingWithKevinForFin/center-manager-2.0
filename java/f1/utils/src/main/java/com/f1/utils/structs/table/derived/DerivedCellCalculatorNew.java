package com.f1.utils.structs.table.derived;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorNew implements DerivedCellCalculator {

	private int position;
	private Class<?> type;
	private DerivedCellCalculator[] params;
	private DerivedCellCalculator[] dimensions;
	private Class<? extends Object> rtype;
	private Constructor<?> constructor;
	private DerivedCellMemberMethod<Object> constructorFactory;

	public DerivedCellCalculatorNew(int position, Class<?> type, DerivedCellCalculator[] params, DerivedCellCalculator[] dimensions, MethodFactoryManager factory) {
		this.position = position;
		this.type = type;
		this.params = params;
		this.dimensions = dimensions;
		if (this.dimensions != null) {
			for (DerivedCellCalculator i : this.dimensions)
				if (!Number.class.isAssignableFrom(i.getReturnType()))
					throw new ExpressionParserException(i.getPosition(), "dimension not a number: " + i);
			this.rtype = Array.newInstance(type, new int[this.dimensions.length]).getClass();
		} else {
			Class[] ptypes;
			if (this.params != null) {
				ptypes = new Class[this.params.length];
				for (int i = 0; i < this.params.length; i++)
					ptypes[i] = this.params[i].getReturnType();
			} else
				ptypes = OH.EMPTY_CLASS_ARRAY;
			this.rtype = type;
			this.constructorFactory = factory.findMemberMethod(type, null, ptypes);
			if (this.constructorFactory == null) {
				try {
					this.constructor = RH.findConstructor(type, ptypes);
				} catch (Exception e) {
					StringBuilder sb = new StringBuilder("Constructor not found: ");
					sb.append(factory.forType(type));
					sb.append("(");
					for (int i = 0; i < ptypes.length; i++) {
						if (i > 0)
							sb.append(',');
						String t = factory.forType(ptypes[i]);
						sb.append(t);
					}
					sb.append(")");
					throw new ExpressionParserException(position, sb.toString(), e);
				}
			} else
				this.constructor = null;
		}
	}
	public DerivedCellCalculatorNew(int position, Class<?> type, DerivedCellCalculator[] params, DerivedCellCalculator[] dimensions, Class<? extends Object> rtype,
			Constructor<?> constructor) {
		this.position = position;
		this.type = type;
		this.rtype = rtype;
		this.params = params;
		this.dimensions = dimensions;
		this.constructor = constructor;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		if (this.dimensions != null) {
			int sizes[] = new int[this.dimensions.length];
			return evalDimensions(lcvs, 0, sizes);
		} else if (this.constructorFactory == null && AH.isEmpty(this.params)) {
			try {
				return constructor.newInstance(OH.EMPTY_OBJECT_ARRAY);
			} catch (Exception e) {
				throw new ExpressionParserException(position, "Runtime Exception in constructor ==> " + e.getMessage(), e);
			}
		} else {
			Object[] objects = new Object[this.params.length];
			return evalParams(lcvs, 0, objects);
		}
	}

	private Object evalDimensions(CalcFrameStack lcvs, int i, int sizes[]) {
		try {
			for (; i < this.dimensions.length; i++) {
				Object object = this.dimensions[i].get(lcvs);
				if (object instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, i, sizes);
				sizes[i] = ((Number) object).intValue();
			}
			return Array.newInstance(type, sizes);
		} catch (Exception e) {
			throw new ExpressionParserException(position, "Runtime Exception in constructor ==> " + e.getMessage(), e);
		}

	}
	private Object evalParams(CalcFrameStack lcvs, int i, Object[] objects) {
		try {
			for (; i < this.params.length; i++) {
				Object object = params[i].get(lcvs);
				if (object instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) object, this, lcvs, i, objects);
				objects[i] = object;
			}
		} catch (Throwable e) {
			throw DerivedHelper.onThrowable(this, e);
		}
		if (this.constructorFactory != null) {
			try {
				Object r = this.constructorFactory.invokeMethod(lcvs, null, objects, this);
				if (r instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, this.params.length, objects);
				return r;
			} catch (Throwable e) {
				throw DerivedHelper.onThrowable(this, e);
			}
		} else {
			try {
				return constructor.newInstance(objects);
			} catch (Throwable e) {
				throw DerivedHelper.onThrowable(this, e);
			}
		}

	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("new ");
		sink.append(type.getName());
		if (dimensions != null) {
			for (DerivedCellCalculator i : dimensions) {
				sink.append('[');
				i.toString(sink);
				sink.append(']');
			}
		} else if (params != null) {
			sink.append('(');
			boolean first = true;
			for (DerivedCellCalculator i : params) {
				if (first)
					first = false;
				else
					sink.append(',');
				i.toString(sink);
			}
			sink.append(')');
		} else {
			sink.append("()");
		}
		return sink;
	}
	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return rtype;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorNew(position, type, DerivedHelper.copy(params), DerivedHelper.copy(dimensions), rtype, constructor);
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorNew.class)
			return false;
		DerivedCellCalculatorNew o = (DerivedCellCalculatorNew) other;
		return OH.eq(type, o.type) && Arrays.equals(params, o.params) && Arrays.equals(dimensions, o.dimensions);
	}
	@Override
	public int hashCode() {
		return OH.hashCode(position, this.type);
	}
	@Override
	public Object resume(PauseStack paused) {
		Object object = paused.getNext().resume();
		int state = paused.getState();
		if (object instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) object, this, paused.getLcvs(), state, paused.getAttachment());
		if (this.dimensions != null) {
			int sizes[] = (int[]) paused.getAttachment();
			sizes[state] = ((Number) object).intValue();
			return evalDimensions(paused.getLcvs(), state + 1, sizes);
		} else {
			Object[] objects = (Object[]) paused.getAttachment();
			objects[state] = object;
			return evalParams(paused.getLcvs(), state + 1, objects);
		}
	}
	@Override
	public int getInnerCalcsCount() {
		return (this.params == null ? 0 : this.params.length) + (this.dimensions == null ? 0 : this.dimensions.length);
	}
	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		if (params == null)
			return this.dimensions[n];
		if (n < this.params.length)
			return this.params[n];
		return this.dimensions[n - this.params.length];
	}
	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorNew o = (DerivedCellCalculatorNew) other;
		return constructor == o.constructor && DerivedHelper.areSame(params, o.params) && DerivedHelper.areSame(dimensions, o.dimensions);
	}

}
