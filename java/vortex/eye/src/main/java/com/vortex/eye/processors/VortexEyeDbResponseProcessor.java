package com.vortex.eye.processors;

import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.povo.db.DbResultMessage;
import com.f1.utils.LH;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeDbResponseProcessor extends VortexEyeResultProcessor<DbResultMessage> {

	public VortexEyeDbResponseProcessor() {
		super(DbResultMessage.class);
	}

	@Override
	public void processAction(ResultMessage<DbResultMessage> action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		if (!action.getAction().getOk()) {
			LH.severe(log, "DB insert failed... TODO: something needs to happen here");
		}
	}

}
