package com.vortex.eye.processors.client;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeStatusRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeStatusResponse;
import com.vortex.eye.processors.VortexEyeRequestProcessor;
import com.vortex.eye.state.VortexEyeState;

public class VortexEyeClientStatusRequestProcessor extends VortexEyeRequestProcessor<VortexEyeStatusRequest, VortexEyeStatusResponse> {

	public VortexEyeClientStatusRequestProcessor() {
		super(VortexEyeStatusRequest.class, VortexEyeStatusResponse.class);
	}

	@Override
	protected VortexEyeStatusResponse processRequest(RequestMessage<VortexEyeStatusRequest> action, VortexEyeState state, ThreadScope threadScope) throws Exception {
		return nw(VortexEyeStatusResponse.class);
	}

}
