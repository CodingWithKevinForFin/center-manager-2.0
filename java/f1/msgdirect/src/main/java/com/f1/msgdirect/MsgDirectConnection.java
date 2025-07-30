/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.base.Factory;
import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgConnectionConfiguration;
import com.f1.msg.MsgConnectionExternalInterfaces;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgInputTopic;
import com.f1.msg.MsgOutputTopic;
import com.f1.msg.MsgTopic;
import com.f1.msg.MsgTopicConfiguration;
import com.f1.msg.impl.AbstractMsgConnection;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.concurrent.FastThreadPool;
import com.f1.utils.impl.PassThroughFactory;

public class MsgDirectConnection extends AbstractMsgConnection implements Runnable, Closeable {

	public static final int BUFFER_SIZE = 2048;
	public static final String DURABLE_PREFIX = "durable.";

	public final Logger log;

	private static final long RECONNECT_PERIOD_MS = 2000;
	public static final int ACK_PERSISTER_SIZE = 1024 * 1024 * 10;
	public static final int ACKER_SIZE = 1024 * 1024 * 10;
	public static final MsgBytesEvent KEEP_ALIVE = new MsgBytesEvent(OH.EMPTY_BYTE_ARRAY);
	private static final int MAX_THREAD_POOL = 128;
	private Thread reconnectThread;

	private Map<Integer, MsgDirectServerSocket> serverSockets = new CopyOnWriteHashMap<Integer, MsgDirectServerSocket>();
	private List<MsgDirectClientSocket> clientSockets = new CopyOnWriteArrayList<MsgDirectClientSocket>();
	private Factory<String, String> logNamer;

	private final FastThreadPool threadPool = new FastThreadPool(MAX_THREAD_POOL, "MsgDirectConnection");

	public MsgDirectConnection(MsgDirectConnectionConfiguration config) {
		super(config);
		log = Logger.getLogger(getLogName(MsgDirectConnection.class.getName()));
	}

	@Deprecated
	public MsgDirectConnection(MsgConnectionConfiguration config) {
		this(new MsgDirectConnectionConfiguration(config.getName(), config.getLogNamer()));
	}
	public Factory<String, String> getLogNamer() {
		return logNamer;
	}

	public void setLogNamer(Factory<String, String> logFactory) {
		this.logNamer = new PassThroughFactory<String>();
	}

	public FastThreadPool getThreadPool() {
		return threadPool;
	}

	@Override
	public void init() {
		super.init();
		for (String topicName : getTopicNames()) {
			MsgDirectTopicConfiguration config = (MsgDirectTopicConfiguration) getConfiguration(topicName);
			if (config.isServer()) {
				getServerSocket(config);
			}
		}
		for (MsgDirectServerSocket s : serverSockets.values())
			s.start();
		reconnectThread = MsgDirectHelper.newThread(this, MsgDirectHelper.THREAD_RECONNECT, this.toString(), true);
		threadPool.start();
	}

	private MsgDirectServerSocket getServerSocket(MsgDirectTopicConfiguration conf) {
		//expect only one since it is a server socket
		if (conf.getPorts().length > 1)
			throw new IllegalArgumentException("must have at most one port for server connection");
		if (conf.getSslPorts().length > 1)
			throw new IllegalArgumentException("must have at most one ssl port for server connenction");
		int port = conf.getPorts()[0];
		int sslPort = conf.getSslPorts()[0];
		MsgDirectServerSocket r = serverSockets.get(port);
		if (r != null)
			return r;
		synchronized (this) {
			if ((r = serverSockets.get(port)) == null)
				serverSockets.put(port, r = new MsgDirectServerSocket(this, conf.getServerBindAddress(), conf.getServerSocketEntitlements(), port, sslPort));
			return r;

		}
	}

	@Override
	protected MsgInputTopic newInputTopic(MsgTopicConfiguration config, String topicSuffix) {
		MsgDirectTopicConfiguration directConfig = (MsgDirectTopicConfiguration) config;
		if (!directConfig.isServer())
			return new MsgDirectInputClientTopic(this, directConfig, topicSuffix);
		else
			return new MsgDirectInputServerTopic(getServerSocket(directConfig), this, directConfig, topicSuffix);
	}

	@Override
	protected MsgOutputTopic newOutputTopic(MsgTopicConfiguration config, String topicSuffix) {
		MsgDirectTopicConfiguration directConfig = (MsgDirectTopicConfiguration) config;
		if (!directConfig.isServer())
			return new MsgDirectOutputClientTopic(this, directConfig, topicSuffix);
		else
			return new MsgDirectOutputServerTopic(getServerSocket(directConfig), this, directConfig, topicSuffix);

	}

	public void addClientSocket(MsgDirectClientSocket clientSocket) {
		clientSockets.add(clientSocket);

	}

	private boolean firstConnectAttempt = true;

	protected void reconnectClients() {
		List<MsgDirectClientSocket> closedClientSockets = null;
		for (MsgDirectClientSocket clientSocket : clientSockets) {
			if (clientSocket.isClosed()) {
				if (closedClientSockets == null)
					closedClientSockets = new ArrayList<MsgDirectClientSocket>();
				closedClientSockets.add(clientSocket);
			} else if (!clientSocket.isAlive()) {
				try {
					clientSocket.connectSocket();
					LH.info(log, MsgDirectHelper.CONNECTED, ": ", clientSocket);
				} catch (Exception e) {
					if (firstConnectAttempt) {
						if (log.isLoggable(Level.FINER))
							LH.finer(log, MsgDirectHelper.CONNECT_FAILED, ": ", clientSocket, e);
						else
							LH.warning(log, MsgDirectHelper.CONNECT_FAILED, ": ", clientSocket);
					}
					continue;
				}
				try {
					clientSocket.connectHandshake();
				} catch (Exception e) {
					LH.info(log, MsgDirectHelper.HANDSHAKE_ERROR, ": ", clientSocket);
					LH.warning(log, "Error reconnecting", e);
				}
			}
		}
		firstConnectAttempt = false;
		if (closedClientSockets != null)
			clientSockets.removeAll(closedClientSockets);
	}

	@Override
	public void run() {
		try {
			boolean first = true;
			while (isRunning()) {
				reconnectClients();
				sendKeepAlives();
				synchronized (this) {
					try {
						wait(RECONNECT_PERIOD_MS);
					} catch (InterruptedException e) {
					}
				}
				first = false;
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "error on housekeeping thread.", e);
		}
	}

	private void sendKeepAlives() {
		for (MsgDirectClientSocket clientSocket : clientSockets) {
			clientSocket.sendKeepAlive();
		}
		for (MsgDirectServerSocket serverSocket : serverSockets.values()) {
			serverSocket.sendKeepAlive();
		}
	}

	@Override
	public void shutdown() {
		synchronized (this) {
			super.shutdown();
			threadPool.stop();
			notify();
		}
	}

	@Override
	public void fireOnConnection(MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection msgExternalConnection) {
		super.fireOnConnection(msgTopic, topic, suffix, remoteHost, isWrite, msgExternalConnection);
	}

	@Override
	public void fireOnDisconnect(MsgTopic msgTopic, String topic, String suffix, String remoteHost, boolean isWrite, MsgExternalConnection msgExternalConnection) {
		super.fireOnDisconnect(msgTopic, topic, suffix, remoteHost, isWrite, msgExternalConnection);
	}

	@Override
	public Iterable<MsgConnectionExternalInterfaces> getExternalInterfaces() {
		return (Collection) serverSockets.values();
	}

	@Override
	public MsgDirectConnectionConfiguration getConfiguration() {
		return (MsgDirectConnectionConfiguration) super.getConfiguration();
	}

	@Override
	public void close() {
		for (MsgDirectClientSocket i : this.clientSockets)
			IOH.close(i);
		for (MsgDirectServerSocket i : this.serverSockets.values())
			IOH.close(i);
		shutdown();
	}

}
