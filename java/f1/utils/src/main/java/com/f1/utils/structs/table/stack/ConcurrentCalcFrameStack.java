package com.f1.utils.structs.table.stack;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;

public class ConcurrentCalcFrameStack extends ChildCalcFrameStack {

	private List<FlowControlPause> pauses = new ArrayList<FlowControlPause>();

	public ConcurrentCalcFrameStack(DerivedCellCalculator calc, CalcFrameStack parent) {
		super(calc, true, parent, EmptyCalcFrame.INSTANCE);
	}

	public void addPause(FlowControlPause pause) {
		this.pauses.add(pause);
	}

	public List<FlowControlPause> getPauses() {
		return this.pauses;
	}

}
