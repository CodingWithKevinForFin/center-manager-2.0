package com.f1.utils.structs.table.derived;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class DerivedCellCalculatorIfElse implements DerivedCellCalculatorFlowControl {

	final private DerivedCellCalculator condition;
	final private int position;
	final private DerivedCellCalculator ifBlock;
	final private DerivedCellCalculator elBlock;
	final private boolean flowControlAlwaysReturns;
	final private Class<?> flowControlReturnType;
	final private boolean hasReturn;

	public DerivedCellCalculatorIfElse(int position, DerivedCellCalculator condition, DerivedCellCalculator ifBlock, DerivedCellCalculator elseBlock) {
		this.position = position;
		this.condition = condition;
		this.ifBlock = ifBlock;
		if (this.condition.getReturnType() != Boolean.class && this.condition.getReturnType() != Object.class)
			throw new ExpressionParserException(this.condition.getPosition(), "Expecting Boolean expression");
		this.elBlock = elseBlock;
		DerivedCellCalculatorFlowControl b1 = ifBlock instanceof DerivedCellCalculatorFlowControl ? (DerivedCellCalculatorFlowControl) ifBlock : null;
		DerivedCellCalculatorFlowControl b2 = elBlock instanceof DerivedCellCalculatorFlowControl ? (DerivedCellCalculatorFlowControl) elBlock : null;
		if (b1 != null && b2 != null) {
			this.flowControlAlwaysReturns = b1.getFlowControlAlwaysCompletes() && b2.getFlowControlAlwaysCompletes();
			this.flowControlReturnType = DerivedCellCalculatorBlock.getWidestIgnoreNull(b1.getReturnType(), b2.getReturnType());
			this.hasReturn = b1.hasReturn() || b2.hasReturn();
		} else if (b1 != null) {
			this.flowControlAlwaysReturns = false;
			this.flowControlReturnType = b1.getReturnType();
			this.hasReturn = b1.hasReturn();
		} else if (b2 != null) {
			this.flowControlAlwaysReturns = false;
			this.flowControlReturnType = b2.getReturnType();
			this.hasReturn = b2.hasReturn();
		} else {
			this.flowControlAlwaysReturns = false;
			this.flowControlReturnType = null;
			this.hasReturn = false;
		}
	}

	private static final int STATE_CONDITION = 0;
	private static final int STATE_RUN_IF_BLOCK = 1;
	private static final int STATE_RUN_EL_BLOCK = 2;

	@Override
	public Object getFlowControl(CalcFrameStack lcvs) {
		return run(0, lcvs, null);
	}

	private Object run(int state, CalcFrameStack lcvs, PauseStack paused) {
		for (;;) {
			switch (state) {
				case STATE_CONDITION: {
					Object r;
					try {
						r = paused == null ? condition.get(lcvs) : paused.resume();
					} catch (Throwable e) {
						throw DerivedHelper.onThrowable(condition, e);
					}
					paused = null;
					if (r instanceof FlowControlPause)
						return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, state, null);
					if (Boolean.TRUE.equals(r))
						state = STATE_RUN_IF_BLOCK;
					else {
						if (elBlock == null)
							return null;
						state = STATE_RUN_EL_BLOCK;
					}
					break;
				}
				case STATE_RUN_IF_BLOCK: {
					Object r = DerivedHelper.getFlowControl(ifBlock, lcvs, paused);
					paused = null;
					if (r instanceof FlowControlPause)
						return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, state, null);
					return r;
				}
				case STATE_RUN_EL_BLOCK: {
					Object r = DerivedHelper.getFlowControl(elBlock, lcvs, paused);
					paused = null;
					if (r instanceof FlowControlPause)
						return DerivedHelper.onFlowControl((FlowControlPause) r, this, lcvs, state, null);
					return r;
				}
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
		sink.append("if(");
		condition.toString(sink);
		sink.append(")");
		ifBlock.toString(sink);
		if (elBlock != null) {
			sink.append(" else ");
			elBlock.toString(sink);
		}
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public Class<?> getReturnType() {
		return flowControlReturnType;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorIfElse(position, condition.copy(), ifBlock.copy(), elBlock.copy());
	}

	@Override
	public boolean isConst() {
		if (!condition.isConst())
			return false;
		if (Boolean.TRUE.equals(condition.get(null))) {
			return ifBlock == null || ifBlock.isConst();
		} else
			return elBlock == null || elBlock.isConst();
	}

	@Override
	public boolean isReadOnly() {
		return condition.isReadOnly() && ifBlock.isReadOnly() && (elBlock == null || elBlock.isReadOnly());
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || other.getClass() != DerivedCellCalculatorIfElse.class)
			return false;
		DerivedCellCalculatorIfElse o = (DerivedCellCalculatorIfElse) other;
		return OH.eq(condition, o.condition) && OH.eq(ifBlock, o.ifBlock) && OH.eq(elBlock, o.elBlock);
	}

	@Override
	public int hashCode() {
		return OH.hashCode(position, condition, ifBlock, elBlock);
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return flowControlAlwaysReturns;
	}

	@Override
	public int getInnerCalcsCount() {
		return elBlock != null ? 3 : 2;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		switch (n) {
			case 0:
				return condition;
			case 1:
				return ifBlock;
			case 2:
				if (elBlock != null)
					return elBlock;
			default:
				throw new IndexOutOfBoundsException("" + n);
		}
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
