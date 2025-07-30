package com.f1.bootstrap.appmonitor;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgEvent;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopic;
import com.f1.msg.MsgTopicListener;
import com.f1.povo.f1app.F1AppMsgTopic;
import com.f1.povo.f1app.audit.F1AppAuditTrailEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailMsgEvent;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.EH;

public class AppMonitorMsgTopicListener extends AbstractAppMonitorObjectListener<F1AppMsgTopic, MsgTopic> implements MsgTopicListener {

	private AtomicLong bytesReceived = new AtomicLong();
	private AtomicLong bytesSent = new AtomicLong();
	private AtomicLong messagesReceived = new AtomicLong();
	private AtomicLong messagesSent = new AtomicLong();
	private AtomicInteger connectionsCount = new AtomicInteger();
	private MsgConnection connection;
	private boolean isOutbound;
	private ObjectGeneratorForClass<F1AppAuditTrailMsgEvent> generator;

	public MsgConnection getConnection() {
		return connection;
	}
	public AppMonitorMsgTopicListener(AppMonitorState state, MsgTopic object, MsgConnection connection, boolean isOutbound) {
		super(state, object);
		this.connection = connection;
		this.isOutbound = isOutbound;
		Collection<MsgExternalConnection> connections = object.getExternalConnections();
		connectionsCount.addAndGet(connections.size());
		this.generator = state.getPartition().getContainer().getGenerator(F1AppAuditTrailMsgEvent.class);
	}
	@Override
	public Class<F1AppMsgTopic> getAgentType() {
		return F1AppMsgTopic.class;
	}

	@Override
	public void onIncomingMessage(MsgTopic topic, MsgEvent data) {
		messagesReceived.incrementAndGet();
		bytesReceived.addAndGet(data.getSize());
		handleAudit(topic, data, true);
		flagChanged();
	}

	@Override
	public void onOutgoingMessage(MsgTopic topic, MsgEvent data, int connectionsCount) {
		handleAudit(topic, data, false);
		messagesSent.incrementAndGet();
		bytesSent.addAndGet(data.getSize());
		flagChanged();
	}

	private void handleAudit(MsgTopic topic, MsgEvent data, boolean b) {
		AppMonitorAuditRule[] rules = getAuditRuleIdsOrNull();
		if (rules != null) {
			int matchCount = 0;
			boolean found[] = null;
			for (int i = 0; i < rules.length; i++) {
				AppMonitorMsgRule sqlRule = (AppMonitorMsgRule) rules[i];
				if (found == null)
					found = new boolean[rules.length];
				found[i] = true;
				matchCount++;
			}

			if (found != null) {
				F1AppAuditTrailMsgEvent event = generator.nw();
				long[] ruleIds = new long[matchCount];
				for (int i = 0, j = 0; i < rules.length; i++) {
					if (found[i])
						ruleIds[j++] = rules[i].getId();
				}
				event.setType(F1AppAuditTrailRule.EVENT_TYPE_MSG);
				event.setAgentRuleIds(ruleIds);
				event.setTimeMs(EH.currentTimeMillis());
				event.setAuditSequenceNumber(getState().nextAuditSequenceNumber());
				Object body = data.getBodyForAudit();
				event.setMsgType(data.getType());
				event.setTopic(topic.getFullTopicName());
				event.setIsIncoming(b);
				switch (data.getType()) {
					case MsgEvent.TYPE_F1_BINARY: {
						event.setPayloadAsBytes((byte[]) body);
						event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_BYTES_F1);
						break;
					}
					case MsgEvent.TYPE_FIX: {
						if (body instanceof byte[]) {
							event.setPayloadAsBytes((byte[]) body);
							event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_BYTES_FIX);
						} else if (body instanceof String) {
							event.setPayloadAsString((String) body);
							event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_STRING_FIX);
						}
						break;
					}
					case MsgEvent.TYPE_JSON: {
						event.setPayloadAsString((String) body);
						event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_STRING_JSON);
						break;
					}
					default: {
						event.setPayloadAsString("Unknown MsgType: " + data.getType());
						event.setPayloadFormat(F1AppAuditTrailEvent.FORMAT_STRING_TEXT);
					}

				}

				event.setAgentF1ObjectId(getAgentObject().getId());
				onAuditEvent(event);
			}
		}
	}
	@Override
	public void onConnect(MsgTopic topic, MsgExternalConnection externalInterface) {
		connectionsCount.incrementAndGet();
	}

	@Override
	public void onDisconnect(MsgTopic topic, MsgExternalConnection externalInterface) {
		connectionsCount.decrementAndGet();
	}

	@Override
	protected void populate(MsgTopic source, F1AppMsgTopic sink) {
		sink.setBytesReceived(bytesReceived.get());
		sink.setBytesSent(bytesSent.get());
		sink.setMessagesReceivedCount(messagesReceived.get());
		sink.setMessagesSentCount(messagesSent.get());
		sink.setTopicName(source.getFullTopicName());
		sink.setIsOutbound(isOutbound);
		sink.setConnectionsCount(connectionsCount.shortValue());
	}

	@Override
	public byte getListenerType() {
		return TYPE_MSGTOPIC;
	}
}
