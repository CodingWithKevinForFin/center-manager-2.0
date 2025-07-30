package com.f1.qfix.msg;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEventListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgTopic;

public class FixMsgInputTopic extends AbstractMsgTopic implements MsgInputTopic {

	private List<MsgEventListener> listeners = new CopyOnWriteArrayList<MsgEventListener>();
	private AtomicLong msgCount = new AtomicLong();

	protected void fire(FixMsgEvent msgEvent) {
		for (MsgEventListener l : listeners)
			l.onEvent(msgEvent, this);
		super.fireIncoming(msgEvent);
	}

	public FixMsgInputTopic(MsgConnection connection, MsgTopicConfiguration configuration) {
		super(connection, configuration, null);
	}

	@Override
	public Collection<MsgExternalConnection> getExternalConnections() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public void subscribe(MsgEventListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void unsubscribe(MsgEventListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public Iterable<MsgEventListener> getListeners() {
		return listeners;
	}

	@Override
	public long getReceivedMessagesCount() {
		return msgCount.get();
	}

}
