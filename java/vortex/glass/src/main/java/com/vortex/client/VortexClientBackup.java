package com.vortex.client;

import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;

public class VortexClientBackup extends VortexClientEntity<VortexEyeBackup> {

	private VortexClientDeployment deployment;
	private VortexClientBackupDestination destination;
	public VortexClientBackup(VortexEyeBackup data) {
		super(VortexAgentEntity.TYPE_BACKUP, data);
		update(data);
	}
	public VortexClientDeployment getDeployment() {
		if (deployment != null)
			OH.assertEq(deployment.getId(), getData().getDeploymentId());
		return deployment;
	}
	public void setDeployment(VortexClientDeployment deployment) {
		if (deployment != null)
			OH.assertEq(deployment.getId(), getData().getDeploymentId());
		this.deployment = deployment;
	}

	public VortexClientBackupDestination getDestination() {
		if (destination != null)
			OH.assertEq(destination.getId(), getData().getBackupDestinationId());
		return destination;
	}
	public void setDestination(VortexClientBackupDestination destination) {
		if (destination != null)
			OH.assertEq(destination.getId(), getData().getBackupDestinationId());
		this.destination = destination;
	}

	public String getFullSourcePath() {
		if (deployment == null)
			return getData().getSourcePath();
		return deployment.getData().getTargetDirectory() + "/" + getData().getSourcePath();
	}
	public String getSourceMuid() {
		return deployment == null ? getData().getSourceMachineUid() : deployment.getMachine().getMachineUid();
	}
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("BU-").append(getId()).append(' ');
		if (getDeployment() != null)
			sb.append(getDeployment().getDescription());
		else
			sb.append(getHostName()).append(getData().getSourcePath());
		if (destination != null)
			sb.append(" --> ").append(destination.getDescription());
		return sb.toString();
	}
	public VortexClientMachine getSrcMachine() {
		if (getDeployment() != null)
			return getDeployment().getMachine();
		else
			return getMachine();
	}

	private String cachedIgnoreExpression = null;
	private TextMatcher cachedIgnoreExpressionMatcher = SH.m(null);

	public boolean shouldIgnore(String path) {
		String ignoreExpression = getData().getIgnoreExpression();
		if (cachedIgnoreExpression != ignoreExpression)
			this.cachedIgnoreExpressionMatcher = SH.m(this.cachedIgnoreExpression = ignoreExpression);
		path = SH.stripPrefix(path, getData().getSourcePath(), false);
		boolean r = cachedIgnoreExpressionMatcher.matches(path);
		return r;
	}
}
