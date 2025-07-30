package com.vortex.sso;

import java.sql.Connection;

import com.f1.container.OutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.utils.IOH;
import com.sso.messages.SsoUpdateEvent;

public class InsertSsoEventProcessor extends BasicProcessor<SsoUpdateEvent, SsoState> {

	public final OutputPort<SsoUpdateEvent> broadcastPort = newOutputPort(SsoUpdateEvent.class);

	public InsertSsoEventProcessor() {
		super((Class) ResultMessage.class, SsoState.class);
	}

	public void init() {
		super.init();
	}

	@Override
	public void processAction(SsoUpdateEvent action, SsoState state, ThreadScope threadScope) throws Exception {
		final SsoDbService dbservice = (SsoDbService) getServices().getService("DB");
		Connection connection = dbservice.getConnection();
		try {
			dbservice.insertUpdateEvent(action, dbservice, connection);
		} finally {
			IOH.close(connection);
		}
		state.addEvent(action);
		broadcastPort.send(action, threadScope);
	}
}