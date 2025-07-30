package com.f1.ami.web;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.utils.CH;
import com.f1.utils.sql.SqlPlanListener;

public class AmiWebPlanListener implements SqlPlanListener {

	private AmiDebugManager debugManager;
	private StringBuilder buf = new StringBuilder();

	public AmiWebPlanListener(AmiDebugManager debugManager) {
		this.debugManager = debugManager;
	}

	@Override
	public void onStart(String query) {
		this.buf.setLength(0);
	}

	@Override
	public void onStep(String step, String msg) {
		this.buf.append(step).append(": ").append(msg).append('\n');
	}

	@Override
	public void onEnd(Object result) {
		if (this.buf.length() > 0) {
			this.buf.append("QUERY_COMPLETED\n");
			if (this.debugManager.shouldDebug(AmiDebugMessage.SEVERITY_INFO))
				this.debugManager.addMessage(
						new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_QUERY_PLAN, null, null, "Query Plan", CH.m("plan", buf.toString()), null));
			this.buf.setLength(0);
		}
	}
	@Override
	public void onEndWithError(Exception e) {
		this.buf.append("QUERY_ERROR\n");
	}

}
