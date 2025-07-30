package com.f1.utils.structs.table.derived;

import com.f1.utils.AH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AbstractMethodDerivedCellCalculatorN implements MethodDerivedCellCalculator {

	final protected DerivedCellCalculator params[];
	//	private Object consts[];
	final private int position;
	private Object[] buf;
	protected int notConsts[];//positions of all non-consts
	public static final Object KEEP_GOING = new Object();
	protected Object shortCircuitConstant = KEEP_GOING;

	public AbstractMethodDerivedCellCalculatorN(int position, DerivedCellCalculator[] params) {
		this.position = position;
		this.params = params.clone();
		for (int i = 0; i < this.params.length; i++)
			params[i] = DerivedHelper.reduceConst(params[i]);
		this.buf = new Object[this.params.length];
		getDefinition().verify(this, false);
	}

	//returns position of consts
	protected int[] evalConsts() {
		int cnt = 0;
		for (DerivedCellCalculator p : this.params)
			if (!p.isConst())
				cnt++;
		notConsts = AH.newInts(cnt);
		int[] consts = AH.newInts(this.params.length - cnt);
		cnt = 0;
		int cnt2 = 0;
		for (int i = 0; i < params.length; i++) {
			if (params[i].isConst()) {
				Object object = params[i].get(null);
				Object ss = shortCircuit(i, object);
				if (ss != KEEP_GOING && this.shortCircuitConstant == KEEP_GOING)
					this.shortCircuitConstant = ss;
				this.buf[i] = object;
				consts[cnt2++] = i;
			} else
				notConsts[cnt++] = i;
		}
		return consts;
	}
	protected Object[] getBuf() {
		return this.buf;
	}

	@Override
	final public Object get(CalcFrameStack key) {
		if (shortCircuitConstant != KEEP_GOING)
			return shortCircuitConstant;
		for (int i : notConsts) {
			Object o = params[i].get(key);
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, key, i, null);
			Object ss = shortCircuit(i, o);
			if (ss != KEEP_GOING)
				return ss;
			buf[i] = o;
		}
		return eval(buf);

	}

	protected Object shortCircuit(int i, Object val) {
		return KEEP_GOING;
	}

	abstract public Object eval(Object[] o0);

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(getMethodName());
		if (params.length == 0)
			return sink.append("()");
		for (int i = 0; i < params.length; i++) {
			sink.append(i == 0 ? '(' : ',');
			this.params[i].toString(sink);
		}
		return sink.append(')');
	}

	@Override
	public Class<?> getReturnType() {
		return this.getDefinition().getReturnType();
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculator copy() {
		DerivedCellCalculator[] params2 = new DerivedCellCalculator[params.length];
		for (int i = 0; i < this.params.length; i++)
			params2[i] = this.params[i].copy();
		return copy(params2);
	}

	@Override
	public boolean isConst() {
		return this.shortCircuitConstant != KEEP_GOING || this.notConsts.length == 0;
	}

	public String toString() {
		return this.toString(new StringBuilder()).toString();
	}
	abstract public ParamsDefinition getDefinition();

	@Override
	public int getInnerCalcsCount() {
		return this.params.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.params[n];
	}

	@Override
	public int getParamsCount() {
		return this.params.length;
	}

	@Override
	public DerivedCellCalculator getParamAt(int n) {
		return this.params[n];
	}

	public String getMethodName() {
		return this.getDefinition().getMethodName();
	}

	public DerivedCellCalculator[] getParams() {
		return this.params;
	}

	@Override
	public boolean isReadOnly() {
		for (DerivedCellCalculator param : params)
			if (!param.isReadOnly())
				return false;
		return true;
	}

	abstract public DerivedCellCalculator copy(DerivedCellCalculator[] params2);

	@Override
	final public Object resume(PauseStack paused) {
		Object o = paused.getNext().resume();
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), paused.getState(), paused.getAttachment());
		Object ss = shortCircuit(paused.getState(), o);
		if (ss != KEEP_GOING)
			return ss;
		buf[paused.getState()] = o;
		for (int i : notConsts) {
			if (i <= paused.getState())
				continue;
			o = params[i].get(paused.getLcvs());
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), i, null);
			ss = shortCircuit(i, o);
			if (ss != KEEP_GOING)
				return ss;
			buf[i] = o;
		}
		return eval(buf);
	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other == null || other.getClass() != getClass())
			return false;
		AbstractMethodDerivedCellCalculatorN o = (AbstractMethodDerivedCellCalculatorN) other;
		return AH.eq(this.notConsts, o.notConsts) && DerivedHelper.childrenAreSame(this, o);
	}
}
