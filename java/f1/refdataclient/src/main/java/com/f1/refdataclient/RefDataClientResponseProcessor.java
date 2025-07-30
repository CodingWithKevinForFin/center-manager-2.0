package com.f1.refdataclient;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.refdata.RefDataInfoMessage;

public class RefDataClientResponseProcessor extends BasicProcessor<ResultMessage<RefDataInfoMessage>, RefDataClientState> {

	public OutputPort<RefDataInfoMessage> refdataOutput = newOutputPort(RefDataInfoMessage.class);
	public RefDataClientResponseProcessor() {
		super((Class) ResultMessage.class, RefDataClientState.class);
	}

	@Override
	public void processAction(ResultMessage<RefDataInfoMessage> action, RefDataClientState state, ThreadScope threadScope) throws Exception {
		RequestMessage origReq = (RequestMessage) action.getRequestMessage().getCorrelationId();
		refdataOutput.send(action.getAction(), threadScope);
		ResultMessage<RefDataInfoMessage> nwResult = nw(ResultMessage.class);
		nwResult.setAction(action.getAction());
		reply(origReq, nwResult, threadScope);
	}

}
