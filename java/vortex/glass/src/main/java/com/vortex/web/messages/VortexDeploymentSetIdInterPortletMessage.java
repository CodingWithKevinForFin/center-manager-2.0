package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexDeploymentSetIdInterPortletMessage implements InterPortletMessage {

	private LongSet deploymentSetIds;

	public VortexDeploymentSetIdInterPortletMessage(LongSet deploymentSetIds) {
		this.deploymentSetIds = deploymentSetIds;
	}

	public LongSet getDeploymentSetIds() {
		return deploymentSetIds;
	}

}
