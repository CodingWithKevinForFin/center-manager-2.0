package com.f1.qfix.msg;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.msg.MsgEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgTopic;

import quickfix.Session;
import quickfix.SessionID;

public class FixMsgOutputTopic extends AbstractMsgTopic implements MsgOutputTopic {

	private SessionID sessionId;
	final private AtomicLong count = new AtomicLong();
	private Map<String, SessionID> sessions;
	private Session session;

	public FixMsgOutputTopic(FixMsgConnection connection, MsgTopicConfiguration configuration) {
		super(connection, configuration, null);
		this.sessions = connection.getSessions();
	}
	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void send(MsgEvent event) {
		FixMsgEvent fixmsg = (FixMsgEvent) event;
		if (sessionId == null)
			sessionId = sessions.get(getName());
		if (session == null)
			session = Session.lookupSession(sessionId);
		try {
			session.send(fixmsg.getMessage());
			super.fireOutgoing(event, 1);
		} catch (RuntimeException e) {
			sessionId = null;
			session = null;
			throw e;
		}
		count.incrementAndGet();
	}

	@Override
	public FixMsgEvent createMessage() {
		return new FixMsgEvent();
	}

	@Override
	public long getSentMessagesCount() {
		return count.get();
	}
	public SessionID getSessionId() {
		return sessionId;
	}
	@Override
	public long getSendQueueSize() {
		return 0;
	}

}
