package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractMethodDerivedCellCalculator3 implements MethodDerivedCellCalculator {

	private static final byte MODE_PREINIT = -2;
	private static final byte MODE_ISNULL = -1;
	private static final byte MODE_0CONST = 1;
	private static final byte MODE_1CONST = 2;
	private static final byte MODE_2CONST = 4;
	private static final byte MODE_SHORTCIRCUIT = 64;
	final protected DerivedCellCalculator param0;
	final protected DerivedCellCalculator param1;
	final protected DerivedCellCalculator param2;
	private Object const0;
	private Object const1;
	private Object const2;
	private byte mode = MODE_PREINIT;
	final private int position;

	public AbstractMethodDerivedCellCalculator3(int position, DerivedCellCalculator param0, DerivedCellCalculator param1, DerivedCellCalculator param2) {
		this.param0 = DerivedHelper.reduceConst(param0);
		this.param1 = DerivedHelper.reduceConst(param1);
		this.param2 = DerivedHelper.reduceConst(param2);
		this.position = position;
		getDefinition().verify(this, false);
	}

	protected void evalConsts() {
		OH.assertEq(this.mode, MODE_PREINIT);
		mode = 0;
		if (shortCircuitNull())
			mode |= MODE_SHORTCIRCUIT;
		if (this.param0.isConst()) {
			Object o0 = param0.get(null);
			if (o0 == null && shortCircuitNull()) {
				this.mode = MODE_ISNULL;
				return;
			}
			this.const0 = get0(o0);
			mode |= MODE_0CONST;
		}
		if (this.param1.isConst()) {
			Object o1 = param1.get(null);
			if (o1 == null && shortCircuitNull()) {
				this.mode = MODE_ISNULL;
				return;
			}
			this.const1 = get1(o1);
			mode |= MODE_1CONST;
		}
		if (this.param2.isConst()) {
			Object o2 = param2.get(null);
			if (o2 == null && shortCircuitNull()) {
				this.mode = MODE_ISNULL;
				return;
			}
			this.const2 = get2(o2);
			mode |= MODE_2CONST;
		}
	}

	@Override
	final public Object get(CalcFrameStack key) {
		switch (mode) {
			case MODE_PREINIT:
				throw new IllegalStateException("Forgot to call evalConsts()");
			case MODE_ISNULL:
				return null;
			default: {
				Object p0 = param0.get(key);
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p1 = param1.get(key);
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, p0);
				p1 = get1(p1);

				Object p2 = param2.get(key);
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(p0, p1));
				p2 = get2(p2);

				return eval(p0, p1, p2);
			}
			case MODE_SHORTCIRCUIT: {
				Object p0 = param0.get(key);
				if (p0 == null)
					return null;
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p1 = param1.get(key);
				if (p1 == null)
					return null;
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, p0);
				p1 = get1(p1);

				Object p2 = param2.get(key);
				if (p2 == null)
					return null;
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(p0, p1));
				p2 = get2(p2);

				return eval(p0, p1, p2);
			}
			case MODE_0CONST: {
				Object p1 = param1.get(key);
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, const0);
				p1 = get1(p1);

				Object p2 = param2.get(key);
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(const0, p1));
				p2 = get2(p2);

				return eval(const0, p1, p2);
			}
			case MODE_1CONST: {
				Object p0 = param0.get(key);
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p2 = param2.get(key);
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(p0, const1));
				p2 = get2(p2);

				return eval(p0, const1, p2);
			}
			case MODE_2CONST: {
				Object p0 = param0.get(key);
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p1 = param1.get(key);
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, p0);
				p1 = get1(p1);
				return eval(p0, p1, const2);
			}
			case MODE_0CONST | MODE_1CONST: {
				Object p2 = param2.get(key);
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(const0, const1));
				p2 = get2(p2);
				return eval(const0, const1, p2);
			}
			case MODE_0CONST | MODE_2CONST: {
				Object p1 = param1.get(key);
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, const0);
				p1 = get1(p1);
				return eval(const0, p1, const2);
			}
			case MODE_1CONST | MODE_2CONST: {
				Object p0 = param0.get(key);
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				return eval(p0, const1, const2);
			}
			case MODE_0CONST | MODE_1CONST | MODE_2CONST: {
				return eval(const0, const1, const2);
			}
			case MODE_SHORTCIRCUIT | MODE_0CONST: {
				Object p1 = param1.get(key);
				if (p1 == null)
					return null;
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, const0);
				p1 = get1(p1);

				Object p2 = param2.get(key);
				if (p2 == null)
					return null;
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(const0, p1));
				p2 = get2(p2);

				return eval(const0, p1, p2);
			}
			case MODE_SHORTCIRCUIT | MODE_1CONST: {
				Object p0 = param0.get(key);
				if (p0 == null)
					return null;
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p2 = param2.get(key);
				if (p2 == null)
					return null;
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(p0, const1));
				p2 = get2(p2);

				return eval(p0, const1, p2);
			}
			case MODE_SHORTCIRCUIT | MODE_2CONST: {
				Object p0 = param0.get(key);
				if (p0 == null)
					return null;
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				Object p1 = param1.get(key);
				if (p1 == null)
					return null;
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, p0);
				p1 = get1(p1);
				return eval(p0, p1, const2);
			}
			case MODE_SHORTCIRCUIT | MODE_0CONST | MODE_1CONST: {
				Object p2 = param2.get(key);
				if (p2 == null)
					return null;
				if (p2 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p2, this, key, 2, new Tuple2<Object, Object>(const0, const1));
				p2 = get2(p2);
				return eval(const0, const1, p2);
			}
			case MODE_SHORTCIRCUIT | MODE_0CONST | MODE_2CONST: {
				Object p1 = param1.get(key);
				if (p1 == null)
					return null;
				if (p1 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p1, this, key, 1, const0);
				p1 = get1(p1);
				return eval(const0, p1, const2);
			}
			case MODE_SHORTCIRCUIT | MODE_1CONST | MODE_2CONST: {
				Object p0 = param0.get(key);
				if (p0 == null)
					return null;
				if (p0 instanceof FlowControlPause)
					return DerivedHelper.onFlowControl((FlowControlPause) p0, this, key, 0, null);
				p0 = get0(p0);

				return eval(p0, const1, const2);
			}
			case MODE_SHORTCIRCUIT | MODE_0CONST | MODE_1CONST | MODE_2CONST: {
				return eval(const0, const1, const2);
			}
		}
	}

	protected Object get0(Object o0) {
		return o0;
	}
	protected Object get1(Object o1) {
		return o1;
	}
	protected Object get2(Object o2) {
		return o2;
	}

	protected boolean shortCircuitNull() {
		return true;
	}

	@Override
	public Class<?> getReturnType() {
		return this.getDefinition().getReturnType();
	}
	abstract public Object eval(Object o0, Object o1, Object o2);
	abstract public DerivedCellCalculator copy(DerivedCellCalculator param0, DerivedCellCalculator param1, DerivedCellCalculator param2);

	@Override
	final public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName());
		sink.append('(');
		this.param0.toString(sink);
		sink.append(',');
		this.param1.toString(sink);
		sink.append(',');
		this.param2.toString(sink);
		return sink.append(')');
	}

	@Override
	final public int getPosition() {
		return this.position;
	}

	@Override
	final public DerivedCellCalculator copy() {
		return copy(param0.copy(), param1.copy(), param2.copy());
	}

	@Override
	public boolean isConst() {
		return mode == (MODE_0CONST | MODE_1CONST | MODE_2CONST);
	}

	final public String toString() {
		return this.toString(new StringBuilder()).toString();
	}

	abstract public ParamsDefinition getDefinition();

	@Override
	public String getMethodName() {
		return this.getDefinition().getMethodName();
	}

	@Override
	public boolean isReadOnly() {
		return param0.isReadOnly() && param1.isReadOnly() && param2.isReadOnly();
	}

	@Override
	final public int getInnerCalcsCount() {
		return 3;
	}

	@Override
	final public DerivedCellCalculator getInnerCalcAt(int n) {
		return n == 0 ? this.param0 : (n == 1 ? this.param1 : this.param2);
	}

	@Override
	final public int getParamsCount() {
		return 3;
	}

	@Override
	final public DerivedCellCalculator getParamAt(int n) {
		return n == 0 ? this.param0 : (n == 1 ? this.param1 : this.param2);
	}

	@Override
	final public Object resume(PauseStack paused) {
		Object o = paused.getNext().resume();
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), paused.getState(), paused.getAttachment());
		Object p0, p1, p2;
		if (paused.getState() == 0) {
			p0 = get0(o);
			p1 = param1.get(paused.getLcvs());
			if (p1 instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) p1, this, paused.getLcvs(), 1, p0);
			p1 = get1(p1);
			p2 = param2.get(paused.getLcvs());
			if (p2 instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) p2, this, paused.getLcvs(), 2, new Tuple2<Object, Object>(p0, p1));
			p2 = get2(p2);
		} else if (paused.getState() == 1) {
			p0 = paused.getAttachment();
			p1 = get1(o);
			p2 = param2.get(paused.getLcvs());
			if (p2 instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) p2, this, paused.getLcvs(), 2, new Tuple2<Object, Object>(p0, p1));
			p2 = get2(p2);
		} else {//state==2
			Tuple2<Object, Object> tuple = (Tuple2<Object, Object>) paused.getAttachment();
			p0 = tuple.getA();
			p1 = tuple.getB();
			p2 = get2(o);
		}
		if (shortCircuitNull() && (p0 == null | p1 == null || p2 == null))
			return null;
		return eval(p0, p1, p2);
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other == null || other.getClass() != getClass())
			return false;
		AbstractMethodDerivedCellCalculator3 o = (AbstractMethodDerivedCellCalculator3) other;
		return mode == o.mode && OH.eq(const0, o.const0) && OH.eq(const1, o.const1) && OH.eq(const2, o.const2) && DerivedHelper.childrenAreSame(this, o);
	}
}
