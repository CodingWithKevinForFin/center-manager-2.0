package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorWhile implements DerivedCellCalculatorFlowControl {

	private DerivedCellCalculator condition;
	private int position;
	private DerivedCellCalculator block;
	private boolean conditionAfterBlock;
	private Class<?> flowControlReturnType;
	private boolean hasReturn;

	public DerivedCellCalculatorWhile(int position, DerivedCellCalculator condition, DerivedCellCalculator block, boolean conditionAfterBlock) {
		this.position = position;
		this.condition = condition;
		this.block = block;
		this.conditionAfterBlock = conditionAfterBlock;
		if (block instanceof DerivedCellCalculatorFlowControl) {
			DerivedCellCalculatorFlowControl fc = (DerivedCellCalculatorFlowControl) block;
			this.flowControlReturnType = fc.getReturnType();
			hasReturn = fc.hasReturn();
		} else
			hasReturn = false;
	}

	private static final int STATE_TERMINATION_CHECK = 1;
	private static final int STATE_RUN_STATEMENT = 2;

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		int state = this.conditionAfterBlock ? STATE_RUN_STATEMENT : STATE_TERMINATION_CHECK;
		return run(state, lcvs, null);
	}

	private Object run(int state, CalcFrameStack lcvs, PauseStack paused) {
		BreakpointManager bpm = lcvs.getBreakPointManager();
		TimeoutController timeoutController = lcvs.getTimeoutController();
		for (;;) {
			switch (state) {
				case STATE_TERMINATION_CHECK:
					try {
						if (!Boolean.TRUE.equals(condition.get(lcvs)))
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
								case FlowControl.STATEMENT_CONTINUE:
									break;
								case FlowControl.STATEMENT_BREAK:
									return null;
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
					state = STATE_TERMINATION_CHECK;
					if (bpm != null && bpm.isBreakpoint(lcvs, this.condition))
						return new DebugPause(this.condition).push(this, lcvs, state);
					if (timeoutController != null)
						timeoutController.toDerivedThrowIfTimedout(block);
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
		if (conditionAfterBlock) {
			sink.append("do");
			block.toString(sink);
			sink.append("while(");
			condition.toString(sink);
			sink.append(");");
		} else {
			sink.append("while(");
			condition.toString(sink);
			sink.append(")");
			block.toString(sink);
		}
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
		return new DerivedCellCalculatorWhile(position, condition.copy(), block.copy(), this.conditionAfterBlock);
	}

	@Override
	public boolean isConst() {
		return condition.isConst() && (Boolean.FALSE.equals(condition.get(null)) || block.isConst());
	}

	@Override
	public boolean isReadOnly() {
		return condition.isReadOnly() && block.isReadOnly();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorWhile.class)
			return false;
		DerivedCellCalculatorWhile o = (DerivedCellCalculatorWhile) other;
		return OH.eq(condition, o.condition) && OH.eq(block, o.block) && this.conditionAfterBlock == o.conditionAfterBlock;
	}
	@Override
	public int hashCode() {
		return OH.hashCode(position, condition, block, conditionAfterBlock);
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return false;
	}

	@Override
	public int getInnerCalcsCount() {
		return 2;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int m) {
		return m == 0 ? condition : block;
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
		return hasReturn;
	}
	@Override
	public boolean isSame(DerivedCellCalculator other) {
		return other.getClass() == this.getClass() && DerivedHelper.childrenAreSame(this, other);
	}
}
