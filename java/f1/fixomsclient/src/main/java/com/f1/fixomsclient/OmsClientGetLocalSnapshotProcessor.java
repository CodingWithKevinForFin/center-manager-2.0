package com.f1.fixomsclient;

import com.f1.base.Message;
import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.pofo.oms.OmsNotification;
import com.f1.utils.CH;

/**
 * Can produce and return a single snapshot ( {@link OmsNotification} with {OmsAction#SHAPSHOT}) from the client SOW (without reaching out to the main OMS)
 */
public class OmsClientGetLocalSnapshotProcessor extends BasicRequestProcessor<Message, OmsClientState, OmsClientNotification> {

	public OmsClientGetLocalSnapshotProcessor() {
		super(Message.class, OmsClientState.class, OmsClientNotification.class);
	}

	@Override
	protected OmsClientNotification processRequest(RequestMessage<Message> action, OmsClientState state, ThreadScope threadScope) throws Exception {
		OmsClientNotification r = nw(OmsClientNotification.class);
		r.setAddedExecutions(CH.l(state.getExecutions()));
		r.setAddedOrders(CH.l(state.getOrders()));
		return r;
	}

}
