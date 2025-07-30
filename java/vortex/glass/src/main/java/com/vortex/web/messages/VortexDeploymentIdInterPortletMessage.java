package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexDeploymentIdInterPortletMessage implements InterPortletMessage {

	private LongSet deploymentIds;

	public VortexDeploymentIdInterPortletMessage(LongSet deploymentIds) {
		this.deploymentIds = deploymentIds;
	}

	public LongSet getDeploymentIds() {
		return deploymentIds;
	}

}
