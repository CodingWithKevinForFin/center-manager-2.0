package com.f1.suite.utils.msg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.container.OutputPort;
import com.f1.container.impl.AbstractConnectable;
import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.povo.msg.MsgStatusMessage;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class MsgStatusProcessor extends AbstractConnectable implements MsgConnectionListener {

	public final OutputPort<MsgStatusMessage> outputPort = newOutputPort(MsgStatusMessage.class);
	private List<MsgStatusMessage> pendingStart = new ArrayList<MsgStatusMessage>();
	private boolean hasPending = true;
	private MsgOutputTopic outboundTopic;
	private MsgInputTopic inboundTopic;
	private MsgInputTopic inboundUniqueTopic;
	private AtomicInteger outboundTopicConnectionsCount = new AtomicInteger(-1);
	private AtomicInteger inboundTopicConnectionsCount = new AtomicInteger(-1);
	private AtomicInteger inboundUniqueTopicConnectionsCount = new AtomicInteger(-1);
	private AtomicBoolean allConnected = new AtomicBoolean(true);
	private List<MsgConnectionListener> listeners = new CopyOnWriteArrayList<MsgConnectionListener>();

	public MsgStatusProcessor() {
		outputPort.setConnectionOptional(true);
	}

	@Override
	synchronized public void onDisconnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite,
			MsgExternalConnection externalConnection) {
		boolean fire = true;
		if (msgTopic == null)
			fire = false;
		else if (msgTopic == this.outboundTopic)
			add(this.outboundTopicConnectionsCount, -1, this.outboundTopic);
		else if (msgTopic == this.inboundTopic)
			add(this.inboundTopicConnectionsCount, -1, this.inboundTopic);
		else if (msgTopic == this.inboundUniqueTopic)
			add(this.inboundUniqueTopicConnectionsCount, -1, this.inboundUniqueTopic);
		else
			fire = false;
		MsgStatusMessage msg = nw(MsgStatusMessage.class);
		msg.setTopic(topic);
		msg.setSuffix(suffix);
		msg.setIsWrite(isWrite);
		msg.setRemoteProcessUid(externalConnection.getRemoteProcessUid());
		msg.setIsConnected(false);
		applyHost(remoteHost, msg);
		onEvent(msg);
		if (fire && !this.listeners.isEmpty())
			for (MsgConnectionListener i : this.listeners)
				i.onDisconnect(connection, msgTopic, topic, suffix, remoteHost, isWrite, externalConnection);
	}

	//Listeners add here are guaranteed to be fired AFTER this has done its status updates
	public void addListener(MsgConnectionListener listener) {
		this.listeners.add(listener);
	}
	public void removeListener(MsgConnectionListener listener) {
		this.listeners.remove(listener);
	}

	private void add(AtomicInteger sink, int delta, MsgTopic topic) {
		int n = sink.get();
		if (n == -1) {
			LH.warning(log, "Invalid condition, can not unregistered connection: " + topic);
			return;
		} else if (delta > 0)
			sink.addAndGet(delta);
		else if (delta < 0) {
			for (;;) {
				if (n == 0) {
					LH.severe(log, "IGNORING DECREMENT FOR CONNECTION, ALREADY AT ZERO. CONNECTION COUNT IN BAD STATE: " + topic);
					return;
				} else if (sink.compareAndSet(n, n + delta))
					break;
				else
					n = sink.get();
			}
		}

		boolean isAllConnected;
		for (;;) {
			final int i = this.inboundTopicConnectionsCount.get();
			final int o = this.outboundTopicConnectionsCount.get();
			final int u = this.inboundUniqueTopicConnectionsCount.get();
			isAllConnected = i != 0 && o != 0 && u != 0;
			if (i == this.inboundTopicConnectionsCount.get() && o == this.outboundTopicConnectionsCount.get() && u == this.inboundUniqueTopicConnectionsCount.get())
				break;
		}
		boolean wasAllConnected = this.allConnected.getAndSet(isAllConnected);
		if (wasAllConnected != isAllConnected)
			onConnectionStatusChanged();

	}

	private void onConnectionStatusChanged() {
	}

	static public void applyHost(String remoteHost, MsgStatusMessage msg) {
		if (SH.isnt(remoteHost))
			return;
		if (remoteHost.indexOf(':') != -1) {
			final String host = SH.trim('/', SH.beforeLast(remoteHost, ':'));
			final int port = Integer.parseInt(SH.afterLast(remoteHost, ':'));
			msg.setRemoteHost(host);
			msg.setRemotePort(port);
		} else
			msg.setRemoteHost(remoteHost);
	}

	@Override
	synchronized public void onConnect(MsgConnection connection, MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite,
			MsgExternalConnection externalConnection) {
		boolean fire = true;
		if (msgTopic == null)
			fire = false;
		else if (msgTopic == this.outboundTopic)
			add(this.outboundTopicConnectionsCount, 1, this.outboundTopic);
		else if (msgTopic == this.inboundTopic)
			add(this.inboundTopicConnectionsCount, 1, this.inboundTopic);
		else if (msgTopic == this.inboundUniqueTopic)
			add(this.inboundUniqueTopicConnectionsCount, 1, this.inboundUniqueTopic);
		else
			fire = false;
		MsgStatusMessage msg = nw(MsgStatusMessage.class);
		msg.setTopic(topic);
		msg.setSuffix(suffix);
		msg.setIsWrite(isWrite);
		msg.setIsConnected(true);
		msg.setSource(remoteHost);
		msg.setRemoteProcessUid(externalConnection.getRemoteProcessUid());
		applyHost(remoteHost, msg);
		onEvent(msg);
		if (fire && !this.listeners.isEmpty())
			for (MsgConnectionListener i : this.listeners)
				i.onConnect(connection, msgTopic, topic, suffix, remoteHost, isWrite, externalConnection);
	}

	public void onEvent(MsgStatusMessage event) {
		if (hasPending) {
			synchronized (pendingStart) {
				if (hasPending) {
					pendingStart.add(event);
					return;
				}
			}
		}
		sendEvent(event);
	}

	private void sendEvent(MsgStatusMessage event) {
		outputPort.send(event, null);
	}

	@Override
	public void start() {
		super.start();
		synchronized (pendingStart) {
			for (MsgStatusMessage e : pendingStart)
				sendEvent(e);
			pendingStart.clear();
			hasPending = false;
		}
	}

	@Override
	public void onNewInputTopic(MsgConnection connection, MsgInputTopic r) {
	}

	@Override
	public void onNewOutputTopic(MsgConnection connection, MsgOutputTopic r) {
	}

	public void setOutboundTopic(MsgOutputTopic topic) {
		if (topic != null && topic.getConnection().isRunning())
			throw new IllegalStateException("connection already running, too late to register");
		this.outboundTopic = topic;
		this.outboundTopicConnectionsCount.set(topic == null ? -1 : 0);
		this.allConnected.set(false);
	}

	public void setInboundTopic(MsgInputTopic topic) {
		if (topic != null && topic.getConnection().isRunning())
			throw new IllegalStateException("connection already running, too late to register");
		this.inboundTopic = topic;
		this.inboundTopicConnectionsCount.set(topic == null ? -1 : 0);
		this.allConnected.set(false);
	}

	public void setInboundUniqueTopic(MsgInputTopic topic) {
		if (topic != null && topic.getConnection().isRunning())
			throw new IllegalStateException("connection already running, too late to register");
		this.inboundUniqueTopic = topic;
		this.inboundUniqueTopicConnectionsCount.set(topic == null ? -1 : 0);
		this.allConnected.set(false);
	}

	public int getOutboundTopicConnectionsCount() {
		return this.outboundTopicConnectionsCount.get();
	}
	public int getInboundTopicConnectionsCount() {
		return this.inboundTopicConnectionsCount.get();
	}
	public int getInboundUniqueTopicConnectionsCount() {
		return this.inboundUniqueTopicConnectionsCount.get();
	}

	public boolean getIsAllConnected() {
		return this.allConnected.get();
	}
}
