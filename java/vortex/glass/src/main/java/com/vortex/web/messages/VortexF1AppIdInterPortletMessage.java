package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexF1AppIdInterPortletMessage implements InterPortletMessage {

	private LongSet appIds;

	public VortexF1AppIdInterPortletMessage(LongSet appIds) {
		this.appIds = appIds;
	}

	public LongSet getAppIds() {
		return appIds;
	}

}
