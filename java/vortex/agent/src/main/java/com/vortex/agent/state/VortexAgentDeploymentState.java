package com.vortex.agent.state;

import java.util.HashMap;
import java.util.Map;

import com.f1.container.impl.BasicState;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.vortex.agent.messages.VortexAgentDeploymentUpdateMessage;

public class VortexAgentDeploymentState extends BasicState {

	//private static final long MAX_COMMAND_WAIT_TIME_MS = 5000;
	private VortexAgentDeploymentWrapper deployment;
	//private long lastCommandTimeMs;
	//private long statusDuringLastCommand;
	private Map<String, String> puid2diid = new HashMap<String, String>();
	private VortexAgentDeploymentUpdateMessage pollingAction;

	public VortexAgentDeploymentState() {

	}
	public VortexDeployment getDeployment() {
		return deployment.getDeployment();
	}
	public void init(VortexAgentDeploymentWrapper deployment) {
		this.deployment = deployment;
		if (this.deployment != null)//this is an update
			needsFullResend = true;
	}
	//public boolean justRunCommand(long now, byte currentStatus) {
	//if (now - lastCommandTimeMs > MAX_COMMAND_WAIT_TIME_MS)
	//return false;
	//return currentStatus == statusDuringLastCommand;
	//}
	//public void onRunningCommand(long now) {
	//this.lastCommandTimeMs = now;
	//this.statusDuringLastCommand = getDeployment().getStatus();
	//}

	public String getF1AppDiidByPuid(String puid) {
		return puid2diid.get(puid);
	}

	public void putF1AppPuidToDiid(String puid, String diid) {
		puid2diid.put(puid, diid);
	}
	public String removeF1AppPuid(String puid) {
		return puid2diid.remove(puid);
	}

	public void clear() {
		this.deployment = null;
	}

	public void setPolling(VortexAgentDeploymentUpdateMessage action) {
		this.pollingAction = action;
	}

	public VortexAgentDeploymentUpdateMessage getPolling() {
		return this.pollingAction;
	}
	public VortexAgentDeploymentWrapper getDeploymentWrapper() {
		return deployment;
	}

	private int status;
	private String message;
	private String runningPuid;
	private Integer runningPid;
	private String scriptsFound;
	private boolean needsFullResend = false;

	public void setStatus(int status) {
		this.status = status;
	}
	public void setRunningPid(Integer runningPid) {
		this.runningPid = runningPid;
	}
	public void setRunningProcessUid(String runningPuid) {
		this.runningPuid = runningPuid;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public void setScriptsFound(String scriptsText) {
		this.scriptsFound = scriptsText;
	}
	public Map<String, String> getPuid2diid() {
		return puid2diid;
	}
	public VortexAgentDeploymentUpdateMessage getPollingAction() {
		return pollingAction;
	}
	public int getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	public String getRunningPuid() {
		return runningPuid;
	}
	public Integer getRunningPid() {
		return runningPid;
	}
	public String getScriptsFound() {
		return scriptsFound;
	}

	//private Long buildResultId;
	//public Long getCurrentBuildResultId() {
	//return buildResultId;
	//}
	//public void setCurrentBuildResultId(Long buildResultId) {
	//this.buildResultId = buildResultId;
	//}
	//
	//private String buildInvokedBy;
	//public String getCurrentBuildInvokedBy() {
	//return buildInvokedBy;
	//}
	//public void setCurrentBuildInvokedBy(String buildInvokedBy) {
	//this.buildInvokedBy = buildInvokedBy;
	//}

	public boolean needsFullsend() {
		return needsFullResend;
	}

	public void resetNeedsFullsend() {
		this.needsFullResend = false;
	}
}
