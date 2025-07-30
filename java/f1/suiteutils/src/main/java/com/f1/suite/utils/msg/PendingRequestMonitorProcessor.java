package com.f1.suite.utils.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import com.f1.base.Action;
import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.suite.utils.msg.ConverterState.MessageWrapper;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class PendingRequestMonitorProcessor extends BasicProcessor<Action, ConverterState> implements MsgConnectionListener {

	private MsgStatusProcessor msgStatusProcessor;

	public final OutputPort<Action> loopback = newOutputPort(Action.class);
	final private String topicDescription;

	private long delay = 5000;
	private Action loopbackAction;
	private long timeout;
	private boolean resetTimeoutOnReconnect;
	private PendingRequestHandler pendingRequestHandler;
	private boolean supportCircRefs = false;

	public PendingRequestMonitorProcessor(MsgStatusProcessor msgStatusProcessor, boolean resetTimeoutOnReconnect, long timeout, long checkFrequency, String topicDescription) {
		super(Action.class, ConverterState.class);
		this.msgStatusProcessor = msgStatusProcessor;
		this.msgStatusProcessor.addListener(this);
		this.timeout = timeout;
		this.delay = checkFrequency;
		this.topicDescription = topicDescription;
		this.resetTimeoutOnReconnect = resetTimeoutOnReconnect;
	}

	@Override
	public void init() {
		super.init();
		this.loopbackAction = nw(Action.class);
	}

	@Override
	public void start() {
		loopback.send(this.loopbackAction, null);
		super.start();
	}

	@Override
	public void processAction(Action action, ConverterState state, ThreadScope threadScope) throws Exception {
		try {
			Map<Long, MessageWrapper> pending = state.getPendingRequests();
			boolean isConnected = msgStatusProcessor.getIsAllConnected();
			boolean wasConnected = state.getIsAllConnected();
			if (isConnected != wasConnected) {
				LH.info(log, "Connection status has changed for ", state.getPartition().getPartitionId(), " to ", (isConnected ? "CONNECTED" : "DISCONNECTED"), ", There are ",
						pending.size(), " pending requests");
				if (this.pendingRequestHandler != null)
					this.pendingRequestHandler.onConnectionStatusChanged(isConnected, state);
			}
			state.setIsAllConnected(isConnected);
			if (pending.isEmpty())
				return;
			long now = getTools().getNow();
			long cutoff = now - timeout;
			if (isConnected && !wasConnected) {
				for (Entry<Long, MessageWrapper> i : pending.entrySet()) {
					MessageWrapper v = i.getValue();
					if (resetTimeoutOnReconnect)
						v.now = now;
					if (v.now >= cutoff) {
						RequestMessage req = state.getRequest(i.getKey(), false);
						if (!req.getPosDup()) {
							req.setPosDup(true);
							final byte[] data = state.write(v.message, supportCircRefs);
							v.e.setBytes(data, null);
						}
						if (pendingRequestHandler != null) {
							pendingRequestHandler.onResend(v.channel, v.e, req, v.now, now, this);
						} else {
							v.channel.send(v.e);
							LH.info(log, "Resent request due to reconnect:  ", v.channel.getFullTopicName());
						}
					}
				}
			}
			List<Long> expired = new ArrayList<Long>();
			for (Entry<Long, MessageWrapper> i : pending.entrySet()) {
				if (i.getValue().now < cutoff)
					expired.add(i.getKey());
			}

			for (int i = 0; i < expired.size(); i++) {
				Long guid = expired.get(i);
				MessageWrapper v = pending.get(guid);
				RequestMessage<?> req = state.getRequest(guid, true);
				String errMsg = "sent + timeout < now: " + v.now + " + " + timeout + " < " + now;
				if (log.isLoggable(Level.INFO))
					LH.info(log, this.topicDescription, " Removing correlationId: ", guid, " ==> ", errMsg, " for ", OH.getClassName(req.getAction()));
				if (pendingRequestHandler != null)
					pendingRequestHandler.onExpired(v.channel, v.e, req, v.now, now, this);
				else {
					ResultMessage<?> result = nw(ResultMessage.class);
					result.setError(new TimeoutException(errMsg));
					LH.info(log, "Timeout for request on topic:  ", v.channel.getFullTopicName());
					reply(req, result, threadScope);
				}
			}

		} finally {
			if (action == this.loopbackAction)
				loopback.sendDelayed(action, threadScope, delay, TimeUnit.MILLISECONDS);
		}
	}
	@Override
	public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
		loopback.send(nw(Action.class), null);//nudge on status change
	}

	@Override
	public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection externalConnection) {
		loopback.send(nw(Action.class), null);//nudge on status change
	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {
	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {
	}

	public PendingRequestHandler getPendingRequestHandler() {
		return pendingRequestHandler;
	}

	public void setPendingRequestHandler(PendingRequestHandler pendingRequestHandler) {
		assertNotStarted();
		this.pendingRequestHandler = pendingRequestHandler;
	}

	public boolean isSupportCircRefs() {
		return supportCircRefs;
	}

	public void setSupportCircRefs(boolean supportCircRefs) {
		this.supportCircRefs = supportCircRefs;
	}
}
