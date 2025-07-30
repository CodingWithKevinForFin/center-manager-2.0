package com.vortex.client;

import com.f1.utils.Formatter;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMapSource;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;

public class VortexClientBuildResult extends VortexClientEntity<VortexBuildResult> {

	private VortexClientBuildProcedure buildProcedure;
	final private LongKeyMap<VortexClientDeployment> deployments = new LongKeyMap<VortexClientDeployment>();
	public VortexClientBuildResult(VortexBuildResult data) {
		super(VortexAgentEntity.TYPE_BUILD_RESULT, data);
		update(data);
	}

	public VortexClientBuildProcedure getBuildProcedure() {
		return buildProcedure;
	}
	public void setBuildProcedure(VortexClientBuildProcedure bp) {
		this.buildProcedure = bp;
	}

	public String getDescription(Formatter timeFormatter) {
		VortexBuildResult data = getData();
		StringBuilder sb = new StringBuilder();
		sb.append("BR-").append(data.getId());
		if (SH.is(data.getName()) || SH.is(data.getRevision())) {
			sb.append(": ");
			if (SH.is(data.getName()))
				sb.append(data.getName()).append(' ');
			if (SH.is(data.getVersion()))
				sb.append(data.getVersion()).append(' ');
		} else
			sb.append(' ');
		sb.append("[");
		timeFormatter.format(data.getNow(), sb);
		sb.append("]");
		return sb.toString();
	}

	public void addDeployment(VortexClientDeployment deployment) {
		deployments.put(deployment.getId(), deployment);
	}
	public VortexClientDeployment removeDeployment(VortexClientDeployment deployment) {
		return deployments.remove(deployment.getId());
	}

	public LongKeyMapSource<VortexClientDeployment> getDeployments() {
		return deployments;
	}
}
