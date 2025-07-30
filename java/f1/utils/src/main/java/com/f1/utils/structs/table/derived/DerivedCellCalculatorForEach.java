package com.f1.utils.structs.table.derived;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.EmptyIterator;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;

public class DerivedCellCalculatorForEach implements DerivedCellCalculatorFlowControl {

	final private DerivedCellCalculatorAssignment init;
	final private Caster<?> caster;
	final private int position;
	final private DerivedCellCalculator block;
	final private Set<String> declaredVars = new HashSet<String>();
	final private Class<?> flowControlReturnType;
	final private DerivedCellCalculator array;
	final private com.f1.utils.structs.table.stack.BasicCalcTypes variableTypes;
	final private DerivedCellMemberMethod<Object> getIterator;
	final private boolean hasReturn;

	public DerivedCellCalculatorForEach(int position, DerivedCellCalculatorAssignment init, DerivedCellCalculator array, DerivedCellCalculator block,
			DerivedCellMemberMethod<Object> getIterator) {
		this.position = position;
		this.init = init;
		this.caster = OH.getCaster(init.getReturnType());
		this.array = array;
		this.getIterator = getIterator;
		if (getIterator == null) {
			if (!Iterable.class.isAssignableFrom(this.array.getReturnType()) && this.array.getReturnType() != Object.class) {
				throw new ExpressionParserException(position, "foreach(...) must be used with iterables");
			}
		} else if (getIterator.getReturnType() != Iterator.class)
			throw new ExpressionParserException(array.getPosition(), "iterator() must return Iterator");

		this.block = block;
		if (this.block instanceof DerivedCellCalculatorFlowControl) {
			DerivedCellCalculatorFlowControl fc = (DerivedCellCalculatorFlowControl) this.block;
			this.flowControlReturnType = fc.getReturnType();
			this.hasReturn = fc.hasReturn();
		} else {
			this.flowControlReturnType = null;
			this.hasReturn = false;
		}

		this.variableTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		if (init != null && ((DerivedCellCalculatorAssignment) init).getIsDeclaration()) {
			String variableName = ((DerivedCellCalculatorAssignment) init).getVariableName();
			declaredVars.add(variableName);
			this.variableTypes.putType(variableName, init.getReturnType());
		}
	}

	private static final int STATE_INITIALIZATION = 0;
	private static final int STATE_TERMINATION_CHECK = 1;
	private static final int STATE_RUN_STATEMENT = 2;
	private static final int STATE_INCREMENT = 4;

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		if (!this.declaredVars.isEmpty()) {
			lcvs = new ChildCalcFrameStack(this, true, lcvs, new BasicCalcFrame(this.variableTypes));
		}
		return run(0, lcvs, null, null);
	}

	private Object run(int state, CalcFrameStack lcvs, PauseStack paused, Iterator it) {
		TimeoutController timeoutController = lcvs.getTimeoutController();
		for (;;) {
			switch (state) {
				case STATE_INITIALIZATION:
					if (init != null) {
						try {
							init.get(lcvs);
						} catch (Throwable e) {
							throw DerivedHelper.onThrowable(init, e);
						}
					}
					final Object targetObject;
					try {
						targetObject = paused == null ? array.get(lcvs) : paused.resume();
					} catch (FlowControlThrow e) {
						throw DerivedHelper.onThrowable(this.array, e);
					}
					paused = null;
					if (targetObject instanceof FlowControlPause) {
						FlowControlPause r = DerivedHelper.onFlowControl((FlowControlPause) targetObject, this, lcvs, state, null);
						if (r != null)
							return r;
					}
					if (this.getIterator != null)
						try {
							Iterator it2 = (Iterator) this.getIterator.invokeMethod(lcvs, targetObject, OH.EMPTY_OBJECT_ARRAY, this);

							ArrayList t = new ArrayList();
							if (it2 != null)
								while (it2.hasNext())
									t.add(it2.next());
							it = t.iterator();
						} catch (Throwable e) {
							throw DerivedHelper.onThrowable(this.array, e);
						}
					else
						it = CH.l((Iterable) targetObject).iterator();
					if (it == null)
						it = EmptyIterator.INSTANCE;
					state = STATE_TERMINATION_CHECK;
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(this);
					break;
				case STATE_TERMINATION_CHECK:
					if (!it.hasNext())
						return null;
					state = STATE_INCREMENT;
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(this);
					break;
				case STATE_RUN_STATEMENT:
					Object r = DerivedHelper.getFlowControl(block, lcvs, paused);
					paused = null;
					if (r != null) {
						if (r instanceof FlowControl) {
							FlowControl fc = (FlowControl) r;
							switch (fc.getType()) {
								case FlowControl.STATEMENT_BREAK:
									return null;
								case FlowControl.STATEMENT_CONTINUE:
									break;
								//								case FlowControl.STATEMENT_RETURN:
								//									return fc;
								case FlowControl.STATEMENT_PAUSE: {
									r = DerivedHelper.onFlowControl((FlowControlPause) fc, this, lcvs, state, it);
									if (r != null)
										return r;
									break;
								}
								default:
									return r;
							}
						} else
							return r;
					}
					state = STATE_TERMINATION_CHECK;
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(block);
					break;
				case STATE_INCREMENT:
					Object o = it.next();
					DerivedHelper.putValue(lcvs, this.init.getVariableName(), this.caster.cast(o, false, false));
					state = STATE_RUN_STATEMENT;
					break;
				default:
					throw new IllegalStateException(SH.toString(state));
			}
		}
	}
	@Override
	public Object resumeFlowControl(PauseStack paused) {
		return run(paused.getState(), paused.getLcvs(), paused.getNext(), (Iterator) paused.getAttachment());
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("for(");
		if (init != null)
			init.toString(sink);
		sink.append(':');
		if (array != null)
			array.toString(sink);
		sink.append(")");
		block.toString(sink);
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return this.flowControlReturnType;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorForEach(position, cp(init), cp(array), block.copy(), this.getIterator);
	}

	private <T extends DerivedCellCalculator> T cp(T t) {
		return t == null ? null : (T) t.copy();
	}

	@Override
	public boolean isConst() {
		if ((init == null || init.isConst()) && array.isConst() && block.isConst())
			return true;
		return false;
	}
	@Override
	public boolean isReadOnly() {
		return (init == null || init.isReadOnly()) && array.isReadOnly() && block.isReadOnly();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorForEach.class)
			return false;
		DerivedCellCalculatorForEach o = (DerivedCellCalculatorForEach) other;
		return OH.eq(array, o.array) && OH.eq(init, o.init) && OH.eq(block, o.block);
	}
	@Override
	public int hashCode() {
		return OH.hashCode(position, array, block, init);
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return false;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.init == null ? 2 : 3;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		if (this.init == null)
			return n == 0 ? this.array : this.block;
		else
			return n == 0 ? this.init : n == 1 ? this.array : this.block;
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public Object get(CalcFrameStack lcvs) {
		return DerivedHelper.getForFlowControl(this.getFlowControl(lcvs));
	}
	@Override
	public Object resume(PauseStack lcvs) {
		return DerivedHelper.getForFlowControl(this.resumeFlowControl(lcvs));
	}

	@Override
	public boolean hasReturn() {
		return this.hasReturn;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}
}
