package com.vortex.eye.processors;

import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.LH;
import com.vortex.eye.messages.VortexVaultResponse;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeVaultResponseProcessor extends BasicProcessor<ResultMessage<VortexVaultResponse>, VortexEyeState> {

	public VortexEyeVaultResponseProcessor() {
		super((Class) ResultMessage.class, VortexEyeState.class);
	}

	@Override
	public void processAction(ResultMessage<VortexVaultResponse> action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		if (!action.getAction().getOk()) {
			LH.warning(log, "Vault returned an error: ", action.getAction());
		}

	}

}
