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

public class ExchangePublishHandler extends BasicProcessor<OrderEvent, OrderCache> {
	private static Logger log = Logger.getLogger(ExchangePublishHandler.class.getName());

	private static volatile SessionManager sessionManager = null;
	private static volatile Session senderSession;
	private static volatile String senderSessionName;

	public static void setSessionManager(final SessionManager sessionManager) {
		ExchangePublishHandler.sessionManager = sessionManager;
		senderSession = sessionManager.getFixSessionContext(senderSessionName).getSenderFixSession().getSenderSession();
	}

	public ExchangePublishHandler(final PropertyController props) throws ConfigError {
		super(OrderEvent.class, OrderCache.class);
		senderSessionName = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_SENDER_SESSSION_NAME, AbstractHandler.DEFAULT_EXCHANGE_SENDER_SESSION_NAME);
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
