package com.f1.utils.structs.table.derived;

import java.util.List;

public class FlowControlConcurrentPause extends FlowControlPause {

	private List<FlowControlPause> pauses;

	public FlowControlConcurrentPause(DerivedCellCalculator position, List<FlowControlPause> pauses) {
		super(position);
		this.pauses = pauses;
	}

	public List<FlowControlPause> getPauses() {
		return this.pauses;
	}

}
