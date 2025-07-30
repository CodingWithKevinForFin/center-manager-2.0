package com.f1.ami.amicommon.functions;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellParser;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebFunctionEval extends AbstractMethodDerivedCellCalculator {
	private static final ParamsDefinition VERIFIER = new ParamsDefinition("eval", Object.class, "String expression,java.util.Map variables");
	static {
		VERIFIER.addDesc("Compiles and executes the expression, using the variables map. Returns the result of the script.");
		VERIFIER.addParamDesc(0, "valid amiscript");
		VERIFIER.addRetDesc("result of expression");
		VERIFIER.addExample("15*4", null);
		VERIFIER.addExample("a + b", CH.m("a", 5, "b", 10));
	}

	private DerivedCellParser parser;
	private MethodFactoryManager factory;
	private DerivedCellCalculator p2;
	private DerivedCellCalculator p1;

	public AmiWebFunctionEval(DerivedCellParser parser, int position, DerivedCellCalculator p1, DerivedCellCalculator p2, MethodFactoryManager factory) {
		super(position, new DerivedCellCalculator[] { p1, p2 });//make sure we don't evaluate on construction... this should never be a const
		this.p1 = p1;
		this.p2 = p2;
		this.parser = parser;
		this.factory = factory;
	}
	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}

	@Override
	public Object get(CalcFrameStack key) {
		String exp = (String) p1.get(key);
		if (exp == null)
			return null;
		Map<Object, Object> vals = (Map) p2.get(key);
		if (vals == null)
			vals = Collections.EMPTY_MAP;
		MutableCalcFrame frame = new MutableCalcFrame();
		key = new ChildCalcFrameStack(this, true, key, frame);
		for (Entry<Object, Object> e : vals.entrySet()) {
			if (e.getKey() instanceof CharSequence) {
				String name = e.getKey().toString();
				Object value = e.getValue();
				frame.putTypeValue(name, value == null ? Object.class : (Class) value.getClass(), value);
			}
		}
		DerivedCellCalculator t = parser.toCalc(exp, key);
		if (t == null)
			return null;
		try {
			Object r = t.get(key);
			if (r instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, key, 0, exp);
			return r;
		} catch (FlowControlThrow e) {
			e.getTailFrame().setOriginalSourceCode("ANONYMOUS", exp);
			e.addFrame(this);
			throw e;
		}
	}
	@Override
	public Object resume(PauseStack paused) {
		try {
			Object r = paused.getNext().resume();
			if (r instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, paused.getLcvs(), 0, paused.getAttachment());
			return r;
		} catch (FlowControlThrow e) {
			e.getTailFrame().setOriginalSourceCode("ANONYMOUS", (String) paused.getAttachment());
			e.addFrame(this);
			throw e;
		}
	}
	@Override
	public boolean isConst() {
		return false;
	}

	public static class Factory implements AmiWebFunctionFactory {

		private DerivedCellParser parser;
		private MethodFactoryManager methodFactory;

		public Factory(DerivedCellParser parser, MethodFactoryManager factory) {
			this.parser = parser;
			this.methodFactory = factory;
		}

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionEval(parser, position, calcs[0], calcs[1], this.methodFactory);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

		public void setParser(DerivedCellParser parser) {
			this.parser = parser;
		}

	}

	@Override
	public boolean isPausable() {
		return false;
	}
	@Override
	public Class<?> getReturnType() {
		return Object.class;
	}
	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new AmiWebFunctionEval(parser, getPosition(), params2[0], params2[1], factory);
	}

}