package com.vortex.client;

import java.util.HashSet;
import java.util.Set;

import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMapSource;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeployment;

public class VortexClientDeployment extends VortexClientEntity<VortexDeployment> {

	private VortexClientDeploymentSet deploymentSet;
	private VortexClientBuildProcedure buildProcedure;
	private VortexClientBuildResult buildResult;
	final private LongKeyMap<VortexClientBackup> backups = new LongKeyMap<VortexClientBackup>();
	public VortexClientDeployment(VortexDeployment data) {
		super(VortexAgentEntity.TYPE_DEPLOYMENT, data);
		update(data);
	}
	public VortexClientDeploymentSet getDeploymentSet() {
		return deploymentSet;
	}
	public void setDeploymentSet(VortexClientDeploymentSet deploymentSet) {
		this.deploymentSet = deploymentSet;
	}
	public VortexClientBuildProcedure getBuildProcedure() {
		return buildProcedure;
	}
	public void setBuildProcedure(VortexClientBuildProcedure buildProcedure) {
		this.buildProcedure = buildProcedure;
	}
	public VortexClientBuildResult getBuildResult() {
		return buildResult;
	}
	public void setBuildResult(VortexClientBuildResult buildResult) {
		this.buildResult = buildResult;
	}

	public Set<String> getVariables() {
		LocalToolkit tk = new LocalToolkit();
		VortexDeployment t = getData();
		Set<String> variableNames = new HashSet<String>();
		VortexClientBuildProcedure.findVariables(t.getTargetDirectory(), variableNames, tk);
		VortexClientBuildProcedure.findVariables(t.getTargetUser(), variableNames, tk);
		if (deploymentSet != null)
			VortexClientBuildProcedure.findVariables(deploymentSet.getData().getProperties(), variableNames, tk);
		return variableNames;
	}

	public void addBackup(VortexClientBackup backup) {
		OH.assertEq(backup.getData().getDeploymentId(), getId());
		backups.put(backup.getId(), backup);
	}
	public VortexClientBackup removeBackup(long backupId) {
		return backups.remove(backupId);
	}

	public LongKeyMapSource<VortexClientBackup> getBackups() {
		return backups;
	}
	public void removeBackups() {
		backups.clear();
	}
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		if (buildProcedure != null)
			sb.append(buildProcedure.getData().getName());
		if (getData().getDescription() != null)
			sb.append(' ').append(getData().getDescription());
		sb.append(" [");
		sb.append(getHostName()).append(":");
		sb.append(getData().getTargetDirectory());
		sb.append("]");
		return sb.toString();
	}
	public String getDescriptionWithoutTarget() {
		StringBuilder sb = new StringBuilder();
		if (buildProcedure != null)
			sb.append(buildProcedure.getData().getName());
		if (getData().getDescription() != null)
			sb.append(' ').append(getData().getDescription());
		return sb.toString();
	}

}
