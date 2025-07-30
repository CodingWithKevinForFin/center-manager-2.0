package com.vortex.client;

import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;

public class VortexClientDeploymentSet extends VortexClientEntity<VortexDeploymentSet> {

	public LongKeyMap<VortexClientDeployment> deployments = new LongKeyMap<VortexClientDeployment>();
	public VortexClientDeploymentSet(VortexDeploymentSet data) {
		super(VortexAgentEntity.TYPE_DEPLOYMENT_SET, data);
		update(data);
	}

	public void addDeployment(VortexClientDeployment deployment) {
		deployments.put(deployment.getId(), deployment);
	}
	public VortexClientDeployment removeDeployment(VortexClientDeployment deployment) {
		return deployments.remove(deployment.getId());
	}

	public Iterable<VortexClientDeployment> getDeployments() {
		return deployments.values();
	}

}
