package com.f1.fixomsclient;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.container.impl.BasicState;
import com.f1.povo.standard.MapMessage;

/**
 * Transforms generic {@link MapMessage} messages and wraps then in {@link OmsClientNotification} for consumption by {@link OmsClientOrdersExecutionsListener}s
 * 
 */

public class OmsClientBroadcastTransformProcessor extends BasicProcessor<MapMessage, BasicState> {

	private ObjectGeneratorForClass<OmsClientNotification> notifgen;
	final OutputPort<OmsClientNotification> toClient = newOutputPort(OmsClientNotification.class);
	public OmsClientBroadcastTransformProcessor() {
		super(MapMessage.class, BasicState.class);
		toClient.setConnectionOptional(true);
	}

	public void start() {
		super.start();
		notifgen = getGenerator(OmsClientNotification.class);
	}
	@Override
	public void processAction(MapMessage action, BasicState state, ThreadScope threadScope) throws Exception {
		OmsClientNotification notif = notifgen.nw();
		notif.setClientBroadcast(action);
		toClient.send(notif, threadScope);
	}

}
