package com.f1.omsweb;

import com.f1.pofo.oms.Execution;

public class WebOmsExecution {
	private Execution execution;

	private WebOmsOrder parent;

	public WebOmsExecution(Execution execution) {
		this.execution = execution;
	}

	public WebOmsOrder getParent() {
		return parent;
	}

	public void setParent(WebOmsOrder parent) {
		this.parent = parent;
	}

	public Execution getExecution() {
		return execution;
	}

	public void setExecution(Execution execution) {
		this.execution = execution;
	}

	public boolean getIsClientExecution() {
		return parent != null && parent.getIsClientOrder();
	}
	public boolean getIsSliceExecution() {
		return parent != null && parent.getIsSliceOrder();
	}

}
