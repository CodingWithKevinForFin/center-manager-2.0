/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.container.impl.dispatching;

import com.f1.utils.OH;

public class RootPartitionStats {
	private long statActionsProcessed;
	private long statTimeSpentMs;
	private long statQueueRuns;
	private boolean inThreadPool;
	private long actionsAdded;

	public RootPartitionStats() {
	}

	public RootPartitionStats(RootPartitionStats src) {
		this.copyStats(src);
	}
	public void copyStats(RootPartitionStats src) {
		this.actionsAdded = src.getActionsAdded();
		this.statActionsProcessed = src.getActionsProcessed();
		this.inThreadPool = src.getInThreadPool();
		this.statQueueRuns = src.getQueueRuns();
		this.statTimeSpentMs = src.getTimeSpentMs();
	}

	public boolean hasChanged(RootPartitionStats comp) {
		return OH.ne(this.statTimeSpentMs, comp.getTimeSpentMs()) //
				|| OH.ne(this.actionsAdded, comp.getActionsAdded()) //
				|| OH.ne(this.statQueueRuns, comp.getQueueRuns()) //
				|| OH.ne(this.statActionsProcessed, comp.getActionsProcessed()) //
				|| OH.ne(this.inThreadPool, comp.getInThreadPool());
	}

	public long getActionsProcessed() {
		return statActionsProcessed;
	}

	public void setActionsProcessed(long statActionsProcessed) {
		this.statActionsProcessed = statActionsProcessed;
	}

	public long getTimeSpentMs() {
		return statTimeSpentMs;
	}

	public void setTimeSpentMs(long statTimeSpentMs) {
		this.statTimeSpentMs = statTimeSpentMs;
	}

	public long getQueueRuns() {
		return statQueueRuns;
	}

	public void setQueueRuns(long statQueueRuns) {
		this.statQueueRuns = statQueueRuns;
	}

	public void setInThreadPool(boolean b) {
		this.inThreadPool = b;
	}

	public boolean getInThreadPool() {
		return this.inThreadPool;
	}

	public void setActionsAdded(long l) {
		this.actionsAdded = l;
	}

	public long getActionsAdded() {
		return this.actionsAdded;
	}

}
