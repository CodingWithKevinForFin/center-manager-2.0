package com.f1.utils.structs.table.derived;

import java.util.HashSet;

import com.f1.base.Caster;
import com.f1.utils.OH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;

public class DerivedCellCalculatorMethod extends AbstractMethodDerivedCellCalculator implements DerivedCellCalculator {

	private String[] argumentNames;
	private final Class<?>[] argumentTypes;
	private final Caster<?>[] argumentCasters;
	private Caster<?> returnType;
	private HashSet<String> names;
	private com.f1.utils.structs.table.stack.BasicCalcTypes types;
	private DeclaredMethodFactory declaredMethodFactory;
	private final ParamsDefinition definition;

	public DerivedCellCalculatorMethod(int position, Class<?> returnType, String methodName, String[] argumentNames, Class[] argumentTypes,
			DeclaredMethodFactory declaredMethodFactory, DerivedCellCalculator[] calcs) {
		super(position, calcs);
		this.definition = new ParamsDefinition(methodName, returnType, argumentNames, argumentTypes, false, (byte) 0);
		OH.assertNotNull(declaredMethodFactory);
		if (calcs.length != argumentNames.length)
			throw new ExpressionParserException(position, "Arugment count mismatch: " + calcs.length + " != " + argumentNames.length);
		this.returnType = OH.getCaster(returnType);
		this.argumentNames = argumentNames;
		this.argumentTypes = argumentTypes;
		this.argumentCasters = OH.getAllCasters(argumentTypes);
		this.declaredMethodFactory = declaredMethodFactory;
		this.names = new HashSet<String>();
		this.types = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (int i = 0; i < this.argumentTypes.length; i++) {
			String name = this.argumentNames[i];
			names.add(name);
			types.putType(name, this.argumentTypes[i]);
		}
	}

	@Override
	public Object get(CalcFrameStack key) {
		return run(0, key, null, new ChildCalcFrameStack(this, false, key, new BasicCalcFrame(types)));
	}
	private Object run(int step, CalcFrameStack key, PauseStack pauseStack, CalcFrameStack m) {
		TimeoutController tc = key.getTimeoutController();
		if (tc != null)
			tc.throwIfTimedout();
		Object r;
		DerivedCellCalculator[] params = getParams();
		for (;;) {
			if (step < params.length) {
				Object o;
				if (pauseStack == null) {
					o = params[step].get(key);
					if (o instanceof FlowControlPause)
						return ((FlowControlPause) o).push(this, key, step, m);
				} else {
					o = pauseStack.resume();
					pauseStack = null;
				}
				DerivedHelper.putValue(m, argumentNames[step], this.argumentCasters[step].cast(o));
			} else if (step == params.length) {
				try {
					if (pauseStack == null) {
						r = declaredMethodFactory.inner.get(m);
					} else {
						r = pauseStack.resume();
						pauseStack = null;
					}
				} catch (Throwable e) {
					final FlowControlThrow th = DerivedHelper.onThrowable(declaredMethodFactory.inner, e);
					th.getTailFrame().setOriginalSourceCode(declaredMethodFactory.getBodyTextLabel(), declaredMethodFactory.getBodyText());
					th.addFrame(this);
					throw th;
				}
				if (r instanceof FlowControl) {
					FlowControl fc = (FlowControl) r;
					switch (fc.getType()) {
						case FlowControl.STATEMENT_PAUSE:
							return ((FlowControlPause) fc).push(this, key, step, m);
						case FlowControl.STATEMENT_BREAK:
						case FlowControl.STATEMENT_CONTINUE:
							return fc;
						default:
							throw new RuntimeException("Unknown type: " + fc);
					}
				} else
					return this.returnType.cast(r, false, false);
			} else
				return null;
			step++;
		}
	}
	@Override
	public boolean isConst() {
		return this.declaredMethodFactory.inner != null && this.declaredMethodFactory.inner.isConst() && super.isConst();
	}

	@Override
	public Class<?> getReturnType() {
		return returnType.getCastToClass();
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator[] params2) {
		return new DerivedCellCalculatorMethod(getPosition(), returnType.getCastToClass(), getMethodName(), argumentNames, argumentTypes, this.declaredMethodFactory, params2);
	}

	@Override
	public Object resume(PauseStack paused) {
		return run(paused.getState(), paused.getLcvs(), paused.getNext(), (CalcFrameStack) paused.getAttachment());
	}

	public DeclaredMethodFactory getMethodFactory() {
		return this.declaredMethodFactory;
	}

	@Override
	public String getMethodName() {
		return getDefinition().getMethodName();
	}

	@Override
	public ParamsDefinition getDefinition() {
		return this.definition;
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (!super.isSame(other))
			return false;
		DerivedCellCalculatorMethod o = (DerivedCellCalculatorMethod) other;
		return DerivedHelper.areSame(this.declaredMethodFactory.inner, o.declaredMethodFactory.inner) && o.definition.equals(definition);
	}

	public DerivedCellCalculator getInnerBlock() {
		return this.declaredMethodFactory.inner;
	}
}
