package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterStatusRequest;
import com.f1.ami.amicommon.msg.AmiCenterStatusResponse;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;

public class AmiCenterClientStatusRequestProcessor extends AmiCenterRequestProcessor<AmiCenterStatusRequest, AmiCenterState, AmiCenterStatusResponse> {

	public AmiCenterClientStatusRequestProcessor() {
		super(AmiCenterStatusRequest.class, AmiCenterState.class, AmiCenterStatusResponse.class);
	}

	@Override
	protected AmiCenterStatusResponse processRequest(RequestMessage<AmiCenterStatusRequest> action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		return nw(AmiCenterStatusResponse.class);
	}

}
