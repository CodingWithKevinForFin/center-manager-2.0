package com.f1.generator.handler;

import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.transportManagement.SessionManager;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;
import quickfix.Session;

public class ClientPublishHandler extends BasicProcessor<OrderEvent, OrderCache> {
	private static final Logger log = Logger.getLogger(ClientPublishHandler.class.getName());

	private static volatile SessionManager sessionManager = null;

	private static volatile String senderSessionName;
	private static volatile Session senderSession = null;

	public static void setSessionManager(final SessionManager sessionManager) {
		ClientPublishHandler.sessionManager = sessionManager;
		senderSession = sessionManager.getFixSessionContext(senderSessionName).getSenderFixSession().getSenderSession();
	}

	public ClientPublishHandler(final PropertyController props) throws ConfigError {
		super(OrderEvent.class, OrderCache.class);
		senderSessionName = props.getOptional(AbstractHandler.ATTR_GENERATOR_CLIENT_SENDER_SESSSION_NAME, AbstractHandler.DEFAULT_CLIENT_SENDER_SESSION_NAME);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		if (null == senderSession) {
			senderSession = sessionManager.getFixSessionContext(senderSessionName).getSenderFixSession().getSenderSession();
			if (null == senderSession) {
				LH.warning(log, "FIX sender session (", senderSessionName, ") is not up, dropping message ->  ", event.getFIXMessage());
				return;
			}
		}

		senderSession.send(event.getFIXMessage());
	}

}
