package com.f1.utils.structs.table.derived;

import com.f1.base.CalcTypes;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcTypes;

public class DerivedCellCalculatorFor implements DerivedCellCalculatorFlowControl {

	final private DerivedCellCalculator condition;
	final private DerivedCellCalculator precondition;
	final private DerivedCellCalculator postcondition;
	final private int position;
	final private DerivedCellCalculator block;
	final private Class<?> flowControlReturnType;
	final private CalcTypes variableTypes;
	final private boolean hasReturn;

	public DerivedCellCalculatorFor(int position, DerivedCellCalculator precondition, DerivedCellCalculator condition, DerivedCellCalculator postcondition,
			DerivedCellCalculator block) {
		this.position = position;
		this.precondition = precondition;
		if (this.precondition != null && !(this.precondition instanceof DerivedCellCalculatorAssignment))
			throw new ExpressionParserException(precondition.getPosition(), "Expecting Assignment");
		this.postcondition = postcondition;
		this.condition = condition;
		if (this.condition != null && this.condition.getReturnType() != Boolean.class)
			throw new ExpressionParserException(this.condition.getPosition(), "Expecting Boolean expression");
		this.block = block;
		if (this.block instanceof DerivedCellCalculatorFlowControl) {
			DerivedCellCalculatorFlowControl fc = (DerivedCellCalculatorFlowControl) this.block;
			this.flowControlReturnType = fc.getReturnType();
			hasReturn = fc.hasReturn();
		} else {
			this.flowControlReturnType = null;
			hasReturn = false;
		}
		if (precondition instanceof DerivedCellCalculatorAssignment && ((DerivedCellCalculatorAssignment) precondition).getIsDeclaration()) {
			String variableName = ((DerivedCellCalculatorAssignment) precondition).getVariableName();
			this.variableTypes = new SingletonCalcTypes(variableName, precondition.getReturnType());
		} else
			this.variableTypes = null;

	}

	private static final int STATE_INITIALIZATION = 0;
	private static final int STATE_TERMINATION_CHECK = 1;
	private static final int STATE_RUN_STATEMENT = 2;
	private static final int STATE_INCREMENT = 4;

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		if (variableTypes != null) {
			lcvs = new ChildCalcFrameStack(this, true, lcvs, new BasicCalcFrame(this.variableTypes));
		}
		return run(0, lcvs, null);
	}

	private Object run(int state, CalcFrameStack lcvs, PauseStack paused) {
		BreakpointManager bpm = lcvs.getBreakPointManager();
		TimeoutController timeoutController = lcvs.getTimeoutController();
		for (;;) {
			switch (state) {
				case STATE_INITIALIZATION:
					if (precondition != null)
						try {
							precondition.get(lcvs);
						} catch (Throwable e) {
							throw DerivedHelper.onThrowable(precondition, e);
						}
					state = STATE_TERMINATION_CHECK;
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(this);
					break;
				case STATE_TERMINATION_CHECK:
					try {
						if (condition != null && !Boolean.TRUE.equals(condition.get(lcvs)))
							return null;
					} catch (Throwable e) {
						throw DerivedHelper.onThrowable(condition, e);
					}
					state = STATE_RUN_STATEMENT;
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
									r = DerivedHelper.onFlowControl((FlowControlPause) fc, this, lcvs, state, null);
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
					state = STATE_INCREMENT;
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(this.block);
					break;
				case STATE_INCREMENT:
					try {
						if (postcondition != null)
							postcondition.get(lcvs);
					} catch (Throwable e) {
						throw DerivedHelper.onThrowable(postcondition, e);
					}
					state = STATE_TERMINATION_CHECK;
					if (this.condition != null && bpm != null && bpm.isBreakpoint(lcvs, this.condition))
						return new DebugPause(this.condition).push(this, lcvs, state);
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(this);
					break;
				default:
					throw new IllegalStateException(SH.toString(state));
			}
		}
	}
	@Override
	public Object resumeFlowControl(PauseStack paused) {
		return run(paused.getState(), paused.getLcvs(), paused.getNext());
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("for(");
		if (precondition != null)
			precondition.toString(sink);
		sink.append(';');
		if (condition != null)
			condition.toString(sink);
		sink.append(';');
		if (postcondition != null)
			postcondition.toString(sink);
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
		return new DerivedCellCalculatorFor(position, cp(precondition), cp(condition), cp(postcondition), block.copy());
	}

	private DerivedCellCalculator cp(DerivedCellCalculator t) {
		return t == null ? null : t.copy();
	}

	@Override
	public boolean isConst() {
		if (condition != null && condition.isConst() && Boolean.FALSE.equals(condition.get(null)))
			return true;
		return false;
	}

	@Override
	public boolean isReadOnly() {
		return condition.isReadOnly() && block.isReadOnly();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorFor.class)
			return false;
		DerivedCellCalculatorFor o = (DerivedCellCalculatorFor) other;
		return OH.eq(condition, o.condition) && OH.eq(precondition, o.precondition) && OH.eq(postcondition, o.postcondition) && OH.eq(block, o.block);
	}
	@Override
	public int hashCode() {
		return OH.hashCode(position, condition, block, precondition, postcondition);
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return false;
	}

	@Override
	public int getInnerCalcsCount() {
		int n = 1;
		if (precondition != null)
			n++;
		if (condition != null)
			n++;
		if (postcondition != null)
			n++;
		return n;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		if (precondition != null && n-- == 0)
			return precondition;
		if (condition != null && n-- == 0)
			return condition;
		if (postcondition != null && n-- == 0)
			return postcondition;
		return block;
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
