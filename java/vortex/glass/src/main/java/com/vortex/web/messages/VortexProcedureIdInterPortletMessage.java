package com.vortex.web.messages;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.utils.structs.LongSet;

public class VortexProcedureIdInterPortletMessage implements InterPortletMessage {

	private LongSet procedureIds;

	public VortexProcedureIdInterPortletMessage(LongSet procedureIds) {
		this.procedureIds = procedureIds;
	}

	public LongSet getProcedureIds() {
		return procedureIds;
	}

}
