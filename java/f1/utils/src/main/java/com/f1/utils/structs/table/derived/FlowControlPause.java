package com.f1.utils.structs.table.derived;

import com.f1.base.ToStringable;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class FlowControlPause implements FlowControl, ToStringable {

	private PauseStack head;
	private DerivedCellCalculator position;

	public FlowControlPause(DerivedCellCalculator position) {
		this.position = position;
	}

	public Object resume() {
		return head.resume();
	}

	public FlowControlPause push(DerivedCellCalculator dcc, CalcFrameStack sf, int state) {
		head = new PauseStack(this, dcc, sf, head, state, null);
		return this;
	}
	public FlowControlPause push(DerivedCellCalculator dcc, CalcFrameStack sf, int state, Object attachment) {
		head = new PauseStack(this, dcc, sf, head, state, attachment);
		return this;
	}

	public PauseStack pop() {
		PauseStack r = head;
		head = head.next;
		return r;
	}

	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("FlowControlPause: ").append(SH.NEWLINE);
		for (PauseStack i = head; i != null; i = i.next) {
			sink.append("  ==> ");
			i.toString(sink).append(SH.NEWLINE);
		}
		return sink;
	}

	public PauseStack getStack() {
		return this.head;
	}

	@Override
	public byte getType() {
		return STATEMENT_PAUSE;
	}

	@Override
	public DerivedCellCalculator getPosition() {
		return position;
	}

}
