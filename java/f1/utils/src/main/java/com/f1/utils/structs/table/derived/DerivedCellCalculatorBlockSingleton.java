package com.f1.utils.structs.table.derived;

import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.SqlResultset;

public class DerivedCellCalculatorBlockSingleton implements DerivedCellCalculatorFlowControl {

	final private DerivedCellCalculator inner;

	public DerivedCellCalculatorBlockSingleton(DerivedCellCalculator params) {
		this.inner = params;
	}

	@Override
	public boolean isConst() {
		return inner.isConst();
	}
	@Override
	public Object getFlowControl(CalcFrameStack key) {
		return run(key, null, false, false);
	}

	private Object run(CalcFrameStack key, PauseStack paused, boolean skipFirstBreakpoint, boolean returnLast) {
		BreakpointManager bpm = key.getBreakPointManager();
		DerivedCellCalculator param = inner;
		if (bpm != null) {
			if (skipFirstBreakpoint)
				skipFirstBreakpoint = false;
			else if (bpm.isBreakpoint(key, param))
				return new DebugPause(param).push(this, key, 0);
		}
		Object r;
		if (returnLast) {
			if (paused != null)
				r = paused.resume();
			else
				r = param.get(key);
			if (r instanceof FlowControlPause)
				return DerivedHelper.onFlowControl((FlowControlPause) r, this, key, 0, null);
			else if (r instanceof TableReturn) {
				SqlResultset rs = key.getSqlResultset();
				if (rs != null)
					rs.appendTable((TableReturn) r);
				return null;
			}
			return r;
		} else
			r = DerivedHelper.getFlowControl(param, key, paused);
		return r;
	}

	@Override
	public Object resumeFlowControl(PauseStack paused) {
		return run(paused.getLcvs(), paused.getNext(), true, false);
	}

	@Override
	public Class<?> getReturnType() {
		return inner.getReturnType();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('{');
		inner.toString(sink);
		sink.append(';');
		sink.append('}');
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public boolean getFlowControlAlwaysCompletes() {
		return this.inner instanceof DerivedCellCalculatorFlowControl && ((DerivedCellCalculatorFlowControl) this.inner).getFlowControlAlwaysCompletes();
	}

	@Override
	public int getPosition() {
		return this.inner.getPosition();
	}

	@Override
	public DerivedCellCalculator copy() {
		return new DerivedCellCalculatorBlockSingleton(DerivedHelper.copy(inner));
	}

	@Override
	public boolean isReadOnly() {
		return inner.isReadOnly();
	}

	@Override
	public int getInnerCalcsCount() {
		return 1;
	}

	@Override
	public DerivedCellCalculator getInnerCalcAt(int n) {
		return this.inner;
	}

	@Override
	public boolean isPausable() {
		return this.inner.isPausable();
	}

	@Override
	public Object get(CalcFrameStack key) {
		Object r = run(key, null, false, true);
		return DerivedHelper.getForFlowControl(r);
	}
	@Override
	public Object resume(PauseStack paused) {
		return run(paused.getLcvs(), paused.getNext(), true, true);
	}

	@Override
	public boolean hasReturn() {
		return this.inner instanceof DerivedCellCalculatorFlowControl && ((DerivedCellCalculatorFlowControl) this.inner).hasReturn();
	}

	@Override
	public boolean isSame(DerivedCellCalculator other) {
		if (other.getClass() != this.getClass())
			return false;
		DerivedCellCalculatorBlockSingleton o = (DerivedCellCalculatorBlockSingleton) other;
		if (!DerivedHelper.areSame(this.inner, o.inner))
			return false;
		return true;
	}

}
