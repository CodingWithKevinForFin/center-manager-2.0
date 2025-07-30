package com.f1.utils.structs.table.derived;

import com.f1.base.CalcFrame;
import com.f1.base.ToStringable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.SqlResultset;

public class PauseStack implements ToStringable {
	final public PauseStack next;
	final private DerivedCellCalculator dcc;
	final private int state;
	private boolean resumed;
	private Object attachment;
	private FlowControlPause fc;
	final private CalcFrameStack lcvs;

	public PauseStack(FlowControlPause fc, DerivedCellCalculator dcc, CalcFrameStack lcvs, PauseStack next, int state, Object attachment) {
		this(fc, dcc, lcvs, next, state);
		this.attachment = attachment;
	}
	public PauseStack(FlowControlPause fc, DerivedCellCalculator dcc, CalcFrameStack lcvs, PauseStack next, int state) {
		this.fc = fc;
		this.dcc = dcc;
		this.lcvs = lcvs;
		this.next = next;
		this.state = state;
	}
	public CalcFrameStack getLcvs() {
		return this.lcvs;
	}
	public DerivedCellCalculator getDcc() {
		return dcc;
	}
	public int getState() {
		return state;
	}
	@Override
	public StringBuilder toString(StringBuilder sink) {
		SH.getSimpleName(dcc.getClass(), sink).append(": ");
		dcc.toString(sink);
		sink.append(" [").append(state).append("]");
		if (lcvs != null) {
			sink.append(" {");
			CalcFrame frame = lcvs.getFrame();
			for (String s : frame.getVarKeys())
				sink.append(lcvs.getFactory().forType(frame.getType(s))).append(" ").append(s).append(" = ").append(frame.getValue(s));
			sink.append("}");
		}
		return sink;

	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}
	public Object resume() {
		OH.assertFalse(resumed);
		resumed = true;
		return dcc.resume(this);
	}
	public Object resumeFlowControl() {
		OH.assertFalse(resumed);
		resumed = true;
		if (dcc instanceof DerivedCellCalculatorFlowControl) {
			return ((DerivedCellCalculatorFlowControl) dcc).resumeFlowControl(this);
		} else {
			Object r = dcc.resume(this);
			if (r instanceof FlowControlPause)
				return r;
			else if (r instanceof TableReturn) {
				SqlResultset rs = this.lcvs.getSqlResultset();
				if (rs != null)
					rs.appendTable((TableReturn) r);
			}
			return null;
		}
	}
	public PauseStack getNext() {
		return next;
	}
	public Object getAttachment() {
		return attachment;
	}
	public DerivedCellCalculator getTail() {
		for (PauseStack i = this;; i = i.next)
			if (i.next == null)
				return i.dcc;
	}
	public FlowControlPause getFlowControlPause() {
		return this.fc;
	}
}
