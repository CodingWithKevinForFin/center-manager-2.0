/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgEventListener;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.utils.LH;

public abstract class AbstractMsgInputTopic extends AbstractMsgTopic implements MsgInputTopic {
	private final Logger log;

	public AbstractMsgInputTopic(MsgConnection connection, MsgTopicConfiguration configuration, String topicSuffix) {
		super(connection, configuration, topicSuffix);
		log = Logger.getLogger(connection.getLogName(AbstractMsgInputTopic.class.getName()));
	}

	final private List<MsgEventListener> listeners = new CopyOnWriteArrayList<MsgEventListener>();

	@Override
	public void subscribe(MsgEventListener listener) {
		listeners.add(listener);
	}

	@Override
	public void unsubscribe(MsgEventListener listener) {
		listeners.remove(listener);
	}

	public synchronized void broadcastMsgEvent(MsgEvent msg) {
		for (MsgEventListener l : listeners)
			try {
				l.onEvent(msg, this);
			} catch (Throwable e) {
				LH.severe(log, "Error onMessage:", e);
			}

	}

	@Override
	public Iterable<MsgEventListener> getListeners() {
		return listeners;
	}

}
