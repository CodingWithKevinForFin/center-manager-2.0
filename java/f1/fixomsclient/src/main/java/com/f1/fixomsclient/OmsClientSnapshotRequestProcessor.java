package com.f1.fixomsclient;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.OmsSnapshotRequest;
import com.f1.utils.LH;

/**
 * receives a snopshot request from the client and forwards the request to the oms
 */
public class OmsClientSnapshotRequestProcessor extends BasicProcessor<Message, OmsClientState> {

	public RequestOutputPort<OmsSnapshotRequest, OmsNotification> outputPort = newRequestOutputPort(OmsSnapshotRequest.class, OmsNotification.class);
	public OutputPort<ResultMessage<OmsNotification>> responsePort = outputPort.getResponsePort();

	public OmsClientSnapshotRequestProcessor() {
		super(Message.class, OmsClientState.class);
	}

	@Override
	public void processAction(Message m, OmsClientState state, ThreadScope threadScope) throws Exception {
		LH.info(log, "Sending snapshot request");
		OmsSnapshotRequest snRequest = nw(OmsSnapshotRequest.class);
		outputPort.requestWithFuture(snRequest, threadScope);
	}

}
