package com.f1.utils.structs.table.derived;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ChildCalcFrameStack;
import com.f1.utils.structs.table.stack.ConcurrentCalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;
import com.f1.utils.structs.table.stack.SingletonCalcTypes;
import com.f1.utils.structs.table.stack.SqlResultset;

public class DerivedCellCalculatorBlock implements DerivedCellCalculatorFlowControl {

	public static class CatchBlock {
		final DerivedCellCalculatorBlock block;
		final SingletonCalcTypes var;

		public CatchBlock(String varname, Class vartype, DerivedCellCalculatorBlock block) {
			this.var = new SingletonCalcTypes(varname, vartype);
			this.block = block;
		}
		public CatchBlock(SingletonCalcTypes var, DerivedCellCalculatorBlock block) {
			this.var = var;
			this.block = block;
		}
		public CatchBlock copy() {
			return new CatchBlock(var, block.copy());
		}
		public boolean isSame(CatchBlock other) {
			return OH.eq(var, other.var) && block.isSame(other.block);
		}
		public Class<?> getVarType() {
			return var.getValue();
		}
		public String getVarName() {
			return var.getKey();
		}
	}

	private BasicCalcTypes declaredVars = null;
	final private MethodFactoryManager dfactory;
	final private Class<?> retType;
	final private boolean flowControlAlwaysReturns;
	final private com.f1.base.CalcTypes variableTypes;
	final private CatchBlock[] catchBlocks;

	public MethodFactoryManager getMethodFactory() {
		return this.dfactory;
	}

	final private DerivedCellCalculator[] params;
	final private int position;
	final private boolean hasReturn;
	final private boolean returnLast;
	final private boolean isImplicit;
	final private boolean isConcurrent;

	public DerivedCellCalculatorBlock(int position, DerivedCellCalculator[] params, MethodFactoryManager dfactory, com.f1.base.CalcTypes variables, CatchBlock[] catchBlocks,
			boolean isImplicit, boolean isConcurrent) {
		this.isConcurrent = isConcurrent;
		this.isImplicit = isImplicit;
		this.params = params;
		this.position = position;
		this.catchBlocks = catchBlocks;
		this.dfactory = dfactory;
		Class retType = null;
		this.variableTypes = variables;
		boolean flowControlAlwaysReturns = false;
		boolean hasReturn = false;

		for (DerivedCellCalculator i : params) {
			if (flowControlAlwaysReturns)
				throw new ExpressionParserException(i.getPosition(), "Unreachable Code");
			if (i instanceof DerivedCellCalculatorAssignment && ((DerivedCellCalculatorAssignment) i).getIsDeclaration()) {
				DerivedCellCalculatorAssignment assignment = (DerivedCellCalculatorAssignment) i;
				if (declaredVars == null)
					declaredVars = new BasicCalcTypes();
				declaredVars.putType(assignment.getVariableName(), assignment.getReturnType());
			} else if (i == null)
				throw new NullPointerException();
			if (i instanceof DerivedCellCalculatorFlowControl) {
				DerivedCellCalculatorFlowControl fc = (DerivedCellCalculatorFlowControl) i;
				flowControlAlwaysReturns = fc.getFlowControlAlwaysCompletes();
				if (fc.hasReturn()) {
					if (isConcurrent)
						throw new ExpressionParserException(fc.getPosition(), "'concurrent' block can not contain 'return' clause");
					hasReturn = true;
					retType = getWidestIgnoreNull(retType, fc.getReturnType());
				}
			}

		}
		this.hasReturn = hasReturn;
		if (!hasReturn && params.length > 0) {
			DerivedCellCalculator last = AH.last(params);
			if (!(last instanceof DerivedCellCalculatorFlowControl)) {
				this.returnLast = true;
				retType = last.getReturnType();
			} else
				this.returnLast = false;
		} else
			this.returnLast = false;
		this.flowControlAlwaysReturns = flowControlAlwaysReturns;
		this.retType = retType == null ? Object.class : retType;

	}

	public static Class<?> getWidestIgnoreNull(Class<?> a, Class<?> b) {
		if (a == Void.class)
			return b == null ? Void.class : b;
		if (b == Void.class)
			return a == null ? Void.class : a;
		return OH.getWidestIgnoreNull(a, b);

	}

	@Override
	public boolean isConst() {
		for (DerivedCellCalculator param : params)
			if (!param.isConst())
				return false;
		if (this.dfactory != null)
			return false;
		if (AH.isntEmpty(this.catchBlocks))
			for (CatchBlock i : this.catchBlocks)
				if (!i.block.isConst())
					return false;
		return true;
	}
	@Override
	public Object getFlowControl(CalcFrameStack sf) {
		if (hasDeclaredVariables()) {
			sf = new ChildCalcFrameStack(this, true, sf, new BasicCalcFrame(declaredVars));
		}
		if (isConcurrent)
			sf = new ConcurrentCalcFrameStack(this, sf);

		return run(0, sf, null, false, false);
	}

	private Object run(int state, CalcFrameStack key, PauseStack paused, boolean skipFirstBreakpoint, boolean returnLast) {
		final DerivedCellCalculator[] p = this.params;
		TimeoutController timeoutController = key.getTimeoutController();
		Object r = null;
		BreakpointManager bpm = key.getBreakPointManager();
		for (;;) {
			if (state == p.length) {
				if (isConcurrent) {
					ConcurrentCalcFrameStack ccfs = (ConcurrentCalcFrameStack) key;
					return ccfs.getPauses().isEmpty() ? null : new FlowControlConcurrentPause(this, ccfs.getPauses()).push(this, key, state + 1);
				} else
					return r;
			} else if (isConcurrent && state == p.length + 1) {
				return null;
			}
			DerivedCellCalculator param = p[state];
			if (bpm != null) {
				if (skipFirstBreakpoint)
					skipFirstBreakpoint = false;
				else if (bpm.isBreakpoint(key, param))
					return new DebugPause(param).push(this, key, state);
			}
			try {
				if (returnLast && state + 1 == p.length) {
					if (paused != null)
						r = paused.resume();
					else
						r = param.get(key);
					if (r instanceof TableReturn) {
						SqlResultset rs = key.getSqlResultset();
						if (rs != null)
							rs.appendTable((TableReturn) r);
						return null;
					}
					if (r instanceof FlowControlPause) {
						r = DerivedHelper.onFlowControl((FlowControlPause) r, this, key, state, null);
						if (r != null)
							return r;
					} else
						return r;
				} else
					r = DerivedHelper.getFlowControl(param, key, paused);
			} catch (Throwable e) {
				if (e instanceof FlowControlThrow)
					return handleException(key, (FlowControlThrow) e, -1, null);
				throw DerivedHelper.onThrowable(param, e);
			}

			paused = null;
			if (r != null) {
				if (r instanceof FlowControlPause) {
					r = DerivedHelper.onFlowControl((FlowControlPause) r, this, key, state, null);
					if (r != null)
						return r;
				} else
					return r;
			}
			state = state + 1;
			if (timeoutController != null) {
				timeoutController.toDerivedThrowIfTimedout(param);
			}
		}
	}
	private Object handleException(CalcFrameStack sf, FlowControlThrow fc, int state, PauseStack paused) throws RuntimeException {
		if (this.catchBlocks != null) {
			Object origVal = fc.getThrownValue();
			outer: for (;;) {
				int pos = -1 - state;
				if (pos == catchBlocks.length)
					break;
				CatchBlock catchBlock = catchBlocks[pos];
				if (catchBlock.var.getValue().isInstance(origVal)) {
					Object val = OH.castNoThrow(origVal, catchBlock.getVarType());
					sf = new ChildCalcFrameStack(this, true, sf, new SingletonCalcFrame(catchBlock.var, val));
					Object r = DerivedHelper.getFlowControl(catchBlock.block, sf, paused);
					paused = null;
					if (r instanceof FlowControlPause)
						return DerivedHelper.onFlowControl((FlowControlPause) r, this, sf, state, fc);
					return r;
				} else if (FlowControlThrow.class.isAssignableFrom(catchBlock.getVarType())) {
					Object val = OH.castNoThrow(fc, catchBlock.getVarType());
					SingletonCalcFrame k = new SingletonCalcFrame(catchBlock.var, val);
					sf = new ChildCalcFrameStack(this, true, sf, new SingletonCalcFrame(catchBlock.var, val));
					Object r = DerivedHelper.getFlowControl(catchBlock.block, sf, paused);
					paused = null;
					if (r instanceof FlowControlPause)
						return DerivedHelper.onFlowControl((FlowControlPause) r, this, sf, state, fc);

					return r;
				}
				state--;
			}
		}
		throw fc;

	}

	@Override
	public Object resumeFlowControl(PauseStack paused) {
		if (paused.getState() < 0) {
			Object r = handleException(paused.getLcvs(), (FlowControlThrow) paused.getAttachment(), paused.getState(), paused.getNext());
			if (r == paused.getAttachment())
				return null;
			else
				return r;
		} else {
			return run(paused.getState(), paused.getLcvs(), paused.getNext(), true, false);
		}
	}

	@Override
	public Class<?> getReturnType() {
		return retType;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('{');
		for (int i = 0; i < params.length; i++) {
			params[i].toString(sink);
			sink.append(';');
		}
		sink.append('}');
		if (AH.isntEmpty(this.catchBlocks)) {
			for (CatchBlock i : this.catchBlocks) {
				sink.append("catch(");
				sink.append(i.getVarType()).append(' ').append(i.getVarName());
				sink.append(")");
				i.block.toString(sink);
			}

		}
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return flowControlAlwaysReturns;
	}

	public com.f1.base.CalcTypes getVariableTypes() {
		return variableTypes;
	}

	@Override
	public int getPosition() {
		return this.position;
	}

	@Override
	public DerivedCellCalculatorBlock copy() {
		CatchBlock[] cb = null;
		if (catchBlocks != null) {
			cb = catchBlocks.clone();
			for (int i = 0; i < cb.length; i++)
				cb[i] = cb[i].copy();
		}
		return new DerivedCellCalculatorBlock(getPosition(), DerivedHelper.copy(params), dfactory, variableTypes, cb, isImplicit, isConcurrent);
	}

	@Override
	public boolean isReadOnly() {
		for (DerivedCellCalculator param : params)
			if (!param.isReadOnly())
				return false;
		return true;
	}

	public int getBlockParamsCount() {
		if (this.catchBlocks == null)
			return this.params.length;
		else
			return this.params.length + this.catchBlocks.length;
	}
	public DerivedCellCalculator getBlockParam(int i) {
		if (i < this.params.length)
			return this.params[i];
		return this.catchBlocks[i - this.params.length].block;
	}

	@Override
	public int getInnerCalcsCount() {
		return this.params.length;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.params[n];
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	@Override
	public Object get(CalcFrameStack key) {
		if (hasDeclaredVariables()) {
			key = new ChildCalcFrameStack(this, true, key, new BasicCalcFrame(this.declaredVars), this.dfactory == null ? key.getFactory() : this.dfactory);
		} else if (this.dfactory != null) {
			key = new ChildCalcFrameStack(this, true, key, EmptyCalcFrame.INSTANCE, this.dfactory);
		}
		if (isConcurrent)
			key = new ConcurrentCalcFrameStack(this, key);
		Object r = run(0, key, null, false, this.returnLast);
		return DerivedHelper.getForFlowControl(r);
	}
	@Override
	public Object resume(PauseStack paused) {
		if (isConcurrent)
			return null;
		if (paused.getState() < 0) {
			Object r = handleException(paused.getLcvs(), (FlowControlThrow) paused.getAttachment(), paused.getState(), paused.getNext());
			if (r == paused.getAttachment())
				return null;
			else
				return r;
		} else {
			Object r = run(paused.getState(), paused.getLcvs(), paused.getNext(), true, this.returnLast);
			return DerivedHelper.getForFlowControl(r);
		}
	}

	@Override
	public boolean hasReturn() {
		return this.hasReturn;
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorBlock o = (DerivedCellCalculatorBlock) other;
		if (!DerivedHelper.areSame(this.params, o.params))
			return false;
		if (catchBlocks != o.catchBlocks) {
			if (catchBlocks == null || o.catchBlocks == null)
				return false;
			if (catchBlocks.length != o.catchBlocks.length)
				return false;
			for (int i = 0; i < catchBlocks.length; i++)
				if (!catchBlocks[i].isSame(o.catchBlocks[i]))
					return false;
		}
		return true;
	}

	public boolean isImplicitBlock() {
		return this.isImplicit;
	}

	public boolean hasDeclaredVariables() {
		return this.declaredVars != null;
	}

	public void clearDeclaredVariables() {
		this.declaredVars = null;
	}
	public BasicCalcTypes getDeclaredVariables() {
		return this.declaredVars;
	}
}
