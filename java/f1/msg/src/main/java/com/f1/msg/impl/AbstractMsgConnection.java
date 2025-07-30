/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msg.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import com.f1.msg.MsgConnection;
import com.f1.msg.MsgConnectionConfiguration;
import com.f1.msg.MsgConnectionListener;
import com.f1.msg.MsgException;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.utils.CH;
import com.f1.utils.LH;

public abstract class AbstractMsgConnection implements MsgConnection {
	private static final Logger log = Logger.getLogger(AbstractMsgConnection.class.getName());
	private final List<MsgConnectionListener> listeners = new CopyOnWriteArrayList<MsgConnectionListener>();
	private final Map<String, MsgTopicConfiguration> configurations = new HashMap<String, MsgTopicConfiguration>();
	private final Map<String, MsgInputTopic> inputTopics = new ConcurrentHashMap<String, MsgInputTopic>();
	private final Map<String, MsgOutputTopic> outputTopics = new ConcurrentHashMap<String, MsgOutputTopic>();
	private final MsgConnectionConfiguration configuration;
	private boolean isRunning = false;

	public AbstractMsgConnection(MsgConnectionConfiguration config) {
		if (config == null)
			throw new NullPointerException("connection config required");
		this.configuration = config;
	}

	@Override
	public void addTopic(MsgTopicConfiguration config) {
		if (config.getName().indexOf("$") != -1)
			throw new MsgException("$ not allowed: " + config);
		CH.putOrThrow(configurations, config.getName(), config);
	}

	@Override
	public void addMsgConnectionListener(MsgConnectionListener listener) {
		if (listener == null)
			throw new NullPointerException("listener");
		listeners.add(listener);
	}

	@Override
	public Set<String> getInputTopicNames() {
		return inputTopics.keySet();
	}

	@Override
	public Set<String> getTopicNames() {
		return configurations.keySet();
	}

	@Override
	public Set<String> getOutputTopicNames() {
		return outputTopics.keySet();
	}

	@Override
	public MsgTopicConfiguration getConfiguration(String topicName) {
		return CH.getOrThrow(configurations, topicName);
	}

	@Override
	public MsgConnectionConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public MsgInputTopic getInputTopic(String topicName) {
		return getInputTopic(topicName, null);
	}

	@Override
	public MsgOutputTopic getOutputTopic(String topicName, String topicSuffix) {
		String fullName = topicSuffix == null ? topicName : (topicName + "$" + topicSuffix);
		try {
			MsgOutputTopic r = outputTopics.get(fullName);
			if (r == null)
				synchronized (this) {
					r = outputTopics.get(fullName);
					if (r == null) {
						MsgTopicConfiguration topicConfig = CH.getOrThrow(configurations, topicName);
						r = newOutputTopic(topicConfig, topicSuffix);
						if (isRunning)
							r.init();
						outputTopics.put(fullName, r);
						fireOnNewOutputTopic(r);
					}
				}
			return r;
		} catch (final Exception e) {
			throw new MsgException("error getting output topic for: " + fullName, e);
		}
	}

	@Override
	public MsgOutputTopic getOutputTopic(String topicName) {
		return getOutputTopic(topicName, null);
	}

	@Override
	public MsgInputTopic getInputTopic(String topicName, String topicSuffix) {
		String fullName = topicSuffix == null ? topicName : (topicName + "$" + topicSuffix);
		try {
			MsgInputTopic r = inputTopics.get(fullName);
			if (r == null)
				synchronized (this) {
					r = inputTopics.get(fullName);
					if (r == null) {
						MsgTopicConfiguration topicConfig = CH.getOrThrow(configurations, topicName);
						r = newInputTopic(topicConfig, topicSuffix);
						if (isRunning)
							r.init();
						inputTopics.put(fullName, r);
						fireOnNewInputTopic(r);
					}
				}
			return r;
		} catch (final Exception e) {
			throw new MsgException("error getting output topic for: " + fullName, e);
		}
	}

	@Override
	public void removeMsgConnectionListener(MsgConnectionListener listener) {
		listeners.remove(listener);
	}

	protected void fireOnNewInputTopic(MsgInputTopic r) {
		for (MsgConnectionListener listener : listeners) {
			try {
				listener.onNewInputTopic(this, r);
			} catch (Exception e) {
				LH.warning(log, "Error firing on new topic to listener: ", r, e);
			}
		}
	}
	protected void fireOnNewOutputTopic(MsgOutputTopic r) {
		for (MsgConnectionListener listener : listeners) {
			try {
				listener.onNewOutputTopic(this, r);
			} catch (Exception e) {
				LH.warning(log, "Error firing on new topic to listener: ", r, e);
			}
		}
	}

	protected void fireOnConnection(MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection msgExternalConnection) {
		for (MsgConnectionListener listener : listeners) {
			try {
				listener.onConnect(this, msgTopic, topic, suffix, remoteHost, isWrite, msgExternalConnection);
			} catch (Exception e) {
				LH.warning(log, "Error firing on connection to listener: ", topic, e);
			}
		}
	}

	protected void fireOnDisconnect(MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection msgExternalConnection) {
		for (MsgConnectionListener listener : listeners) {
			try {
				listener.onDisconnect(this, msgTopic, topic, suffix, remoteHost, isWrite, msgExternalConnection);
			} catch (Exception e) {
				LH.warning(log, "Error firing on connection to listener: ", topic, e);
			}
		}
	}

	@Override
	public void shutdown() {
		isRunning = false;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	abstract protected MsgInputTopic newInputTopic(MsgTopicConfiguration config, String topicSuffix);

	abstract protected MsgOutputTopic newOutputTopic(MsgTopicConfiguration config, String topicSuffix);

	@Override
	public void init() {
		assertNotRunning();
		isRunning = true;
		for (MsgTopic c : inputTopics.values())
			c.init();
		for (MsgTopic c : outputTopics.values())
			c.init();
	}

	public void assertNotRunning() {
		if (isRunning)
			throw new MsgException("already running");
	}

	public void assertRunning() {
		if (!isRunning)
			throw new MsgException("not running");
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + configuration;
	}

	@Override
	public String getLogName(String name) {
		return this.configuration.getLogNamer().get(name);
	}

	@Override
	public Collection<MsgConnectionListener> getListeners() {
		return this.listeners;
	}
	@Override
	public Collection<MsgInputTopic> getInputTopics() {
		return this.inputTopics.values();
	}

	@Override
	public Collection<MsgOutputTopic> getOutputTopics() {
		return this.outputTopics.values();
	}

}
