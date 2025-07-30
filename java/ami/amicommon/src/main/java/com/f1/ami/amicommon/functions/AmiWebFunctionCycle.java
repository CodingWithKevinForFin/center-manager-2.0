package com.f1.ami.amicommon.functions;

import com.f1.utils.AH;
import com.f1.utils.MH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebFunctionCycle extends AbstractMethodDerivedCellCalculator {

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("cycle", Object.class, "Number offset,Object ... choices");
	static {
		VERIFIER.addDesc(
				"Returns the nth object from choices where n is the offset + 1. If the offset is greater than or equal to the number of choices, n is calculated as (offset % number of choices) + 1.");
		VERIFIER.addParamDesc(0, "index into choices");
		VERIFIER.addExample(0, "zero", "one", "two", "three");
		VERIFIER.addExample(5, "zero", "one", "two", "three");
		VERIFIER.addExample(-1, "zero", "one", "two", "three");
		VERIFIER.addExample(17, "even", "odd");
	}

	private static final Object NOT_CONST = new Object();
	private final Object choices[];
	private final Object indexConst;
	private final Object valueConst;
	private final DerivedCellCalculator valueParam;
	private final DerivedCellCalculator[] choicesParam;
	private final Class<?> returnType;

	public AmiWebFunctionCycle(int position, DerivedCellCalculator[] params) {
		super(position, params);
		VERIFIER.verify(this, false);
		valueParam = params[0];
		this.choicesParam = AH.remove(params, 0);
		if (this.choicesParam.length < 2)
			throw new ExpressionParserException(position, "There must be at least two choices.");
		choices = new Object[this.choicesParam.length];
		for (int i = 0; i < this.choicesParam.length; i++) {
			DerivedCellCalculator t = this.choicesParam[i];
			choices[i] = t.isConst() ? t.get(null) : NOT_CONST;
		}
		if (this.valueParam.isConst()) {
			Number n = (Number) valueParam.get(null);
			if (n == null) {
				indexConst = null;
				valueConst = null;
			} else {
				final int t = n.intValue();
				indexConst = t;
				valueConst = choices[MH.mod(t, choices.length)];
			}
		} else {
			indexConst = NOT_CONST;
			valueConst = NOT_CONST;
		}
		Class<?> t = null;
		this.returnType = DerivedHelper.getReturnType(this.choicesParam, Object.class).getCastToClass();
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	@Override
	public boolean isConst() {
		return valueConst != NOT_CONST;
	}

	@Override
	public Class<?> getReturnType() {
		return returnType;
	}

	@Override
	public Object get(CalcFrameStack key) {
		if (valueConst != NOT_CONST)
			return valueConst;

		int idx;
		if (indexConst != NOT_CONST)
			idx = ((Integer) indexConst);
		else {
			Object o = valueParam.get(key);
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 0, null);
			final Number n = (Number) o;
			if (n == null)
				return null;
			idx = MH.mod(n.intValue(), choices.length);
		}
		final Object r = choices[idx];
		if (r != NOT_CONST)
			return r;
		Object o = choicesParam[idx].get(key);
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, key, 1, null);
		return o;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionCycle(getPosition(), params2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionCycle(position, calcs);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	@Override
	final public Object resume(PauseStack paused) {
		Object o = paused.getNext().resume();
		if (o instanceof FlowControlPause)
			return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), paused.getState(), paused.getAttachment());
		if (paused.getState() == 0) {
			final Number n = (Number) o;
			if (n == null)
				return null;
			int idx = MH.mod(n.intValue(), choices.length);
			final Object r = choices[idx];
			if (r != NOT_CONST)
				return r;
			o = choicesParam[idx].get(paused.getLcvs());
			if (o instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) o, this, paused.getLcvs(), 1, null);
			return o;
		} else {//state==1
			return o;
		}
	}

	@Override
	public boolean isPausable() {
		return false;
	}

}
