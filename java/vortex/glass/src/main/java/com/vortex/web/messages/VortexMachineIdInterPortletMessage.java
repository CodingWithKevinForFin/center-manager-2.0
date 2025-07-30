package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexMachineIdInterPortletMessage implements InterPortletMessage {

	private LongSet miids;

	public VortexMachineIdInterPortletMessage(LongSet miids) {
		this.miids = miids;
	}

	public LongSet getMiids() {
		return miids;
	}

}
