package com.f1.ami.center;

import com.f1.ami.center.table.AmiCenterProcess;

public class AmiCenterGlobalProcess implements AmiCenterProcess {

	final private long processId;
	private String step;
	final private long startTime = System.currentTimeMillis();
	final private long sessionId;

	public AmiCenterGlobalProcess(long id, long sessionId) {
		this.processId = id;
		this.sessionId = sessionId;
	}
	@Override
	public long getSessionId() {
		return step == PROCESS_IDLE ? -1 : sessionId;
	}

	@Override
	public long getProcessId() {
		return processId;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}

	@Override
	public String getQuery() {
		return null;
	}

	@Override
	public String getDsName() {
		return "AMI";
	}

	@Override
	public String getDsRelayId() {
		return null;
	}

	@Override
	public String getProcessStatus() {
		return step;
	}

	@Override
	public long getParentProcessId() {
		return -1;
	}
	public void setProcessStatus(String step) {
		this.step = step;
	}

}
