package com.f1.utils.structs.table.derived;

public class DebugPause extends FlowControlPause {

	private long pauseTime;

	public DebugPause(DerivedCellCalculator position) {
		super(position);
		this.pauseTime = System.currentTimeMillis();
	}

	@Override
	public Object resume() {
		TimeoutController timeoutController = this.getStack().getLcvs().getTimeoutController();
		if (timeoutController != null) {
			long addTime = System.currentTimeMillis() - this.pauseTime;
			timeoutController.addTime(addTime);
		}
		return super.resume();
	}
}
