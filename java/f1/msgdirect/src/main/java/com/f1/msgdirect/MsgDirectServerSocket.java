/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgConnectionExternalInterfaces;
import com.f1.msg.MsgException;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopic;
import com.f1.msg.impl.AbstractMsgTopic;
import com.f1.utils.ByteHelper;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.ack.AckPersister;
import com.f1.utils.ack.FileAcker;
import com.f1.utils.concurrent.FastQueue;
import com.f1.utils.concurrent.FastSemaphore;
import com.f1.utils.converter.bytes.StringToByteArrayConverter;

public class MsgDirectServerSocket implements Closeable, MsgConnectionExternalInterfaces {

	public final Logger log;

	private ConcurrentMap<String, ServerReadConnection> topic2ReadConnections = new ConcurrentHashMap<String, ServerReadConnection>();
	private ConcurrentMap<String, ServerWriteConnection> topic2WriteConnections = new ConcurrentHashMap<String, ServerWriteConnection>();

	final private MsgDirectConnection connection;
	final private int port;
	final private ServerSocket serverSocket;
	final private ServerSocket sslServerSocket;
	final private String bindAddr;

	private boolean isClosed = false;

	private int sslPort;

	private MsgDirectConnectionConfiguration config;

	private boolean logFiner;

	private boolean logInfo;

	private ServerSocketEntitlements entitlements;

	public MsgDirectServerSocket(MsgDirectConnection connection, String bindAddr, ServerSocketEntitlements entitlements, int port, int sslPort) {
		this.bindAddr = bindAddr;
		this.entitlements = entitlements;
		config = connection.getConfiguration();
		if (config.getForceSsl()) {
			if (sslPort == MsgDirectTopicConfiguration.NO_PORT)
				this.sslPort = port;
			else if (sslPort == port || port == MsgDirectTopicConfiguration.NO_PORT)
				this.sslPort = sslPort;
			else
				throw new IllegalArgumentException("when forceSslPort is true can't supply both ports and ssl ports");
			this.port = MsgDirectTopicConfiguration.NO_PORT;
		} else {
			this.sslPort = sslPort;
			this.port = port;
		}
		log = Logger.getLogger(connection.getLogName(MsgDirectServerSocket.class.getName()));
		logFiner = log.isLoggable(Level.FINER);
		logInfo = log.isLoggable(Level.INFO);
		this.connection = connection;

		if (this.sslPort == MsgDirectTopicConfiguration.NO_PORT && port == MsgDirectTopicConfiguration.NO_PORT)
			throw new IllegalArgumentException("must have at least a plain port or ssl port ");

		try {
			if (this.sslPort != MsgDirectTopicConfiguration.NO_PORT) {
				if (SH.isnt(config.getKeystorePassword())) {
					LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL missing password");
					throw new RuntimeException("Bad configuration:Missing keystore password");
				}
				if (SH.is(config.getKeystoreFile()))
					this.sslServerSocket = IOH.openSSLServerSocketWithReason(this.bindAddr, this.sslPort, config.getKeystoreFile(), config.getKeystorePassword(), null);
				else if (SH.is(config.getKeystoreContents())) {
					this.sslServerSocket = IOH.openSSLServerSocketWithReason(this.bindAddr, this.sslPort, config.getKeystoreContents(), config.getKeystorePassword(), null);
				} else {
					LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL. Must supply either keystore file or keystore contents. Both missing");
					throw new RuntimeException("Bad configuration:Missing keystore files");
				}
				IOH.optimize(sslServerSocket);
				if (logInfo)

					LH.info(log, MsgDirectHelper.SERVER_LISTENING_LISTEN, ": ", this.sslServerSocket.getLocalPort(), " (ssl)");
			} else
				this.sslServerSocket = null;
		} catch (Exception e) {
			if (OH.findInnerException(e, GeneralSecurityException.class) != null)
				LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL. Key Error ", e);
			else if (logFiner)
				LH.info(log, MsgDirectHelper.SERVER_LISTENING_FAILED, ": ", this.sslPort, " (ssl)", e);
			else if (logInfo)
				LH.info(log, MsgDirectHelper.SERVER_LISTENING_FAILED, ": ", this.sslPort, " (ssl)");
			throw new MsgException("could not listen on ssl port " + this.sslPort, e);
		}

		try {
			if (this.port != MsgDirectTopicConfiguration.NO_PORT) {
				this.serverSocket = IOH.openServerSocketWithReason(this.bindAddr, this.port, null);
				IOH.optimize(serverSocket);
				if (logInfo)
					LH.info(log, MsgDirectHelper.SERVER_LISTENING_LISTEN, ": ", this.serverSocket.getLocalPort());
			} else
				this.serverSocket = null;
		} catch (Exception e) {
			if (logFiner)
				LH.finer(log, MsgDirectHelper.SERVER_LISTENING_FAILED, ": ", this.port, e);
			else if (logInfo)
				LH.info(log, MsgDirectHelper.SERVER_LISTENING_FAILED, ": ", this.port);
			throw new MsgException("could not listen on port " + this.port, e);
		}

	}
	public void start() {
		if (serverSocket != null)
			MsgDirectHelper.newThread(new ServerSocketAcceptor(serverSocket, "ServerSocketReader:" + port), MsgDirectHelper.THREAD_LISTENING, toString(), false);
		if (sslServerSocket != null)
			MsgDirectHelper.newThread(new ServerSocketAcceptor(sslServerSocket, "SslServerSocketReader:" + sslPort), MsgDirectHelper.THREAD_LISTENING, toString(), false);
	}

	@Override
	public String toString() {
		return "[" + port + "/" + sslPort + "]";
	}

	public ServerReadConnection subscribe(String topicName, MsgDirectInputServerTopic topic) {
		ServerReadConnection r = getServerReadConnection(topicName);
		r.addTopic(topic);
		return r;
	}

	@Override
	public void close() throws IOException {
		isClosed = true;
		IOH.close(serverSocket);
		for (ServerReadConnection i : topic2ReadConnections.values())
			IOH.close(i);
		for (ServerWriteConnection i : topic2WriteConnections.values())
			IOH.close(i);
	}

	private class ServerSocketAcceptor implements Runnable {

		private final ServerSocket ssocket;
		private final String name;

		public ServerSocketAcceptor(ServerSocket ssocket, String name) {
			this.ssocket = ssocket;
			this.name = name;
		}
		@Override
		public void run() {
			while (!isClosed) {
				ServerConnection connection = null;
				try {
					final Socket socket = ssocket.accept();
					if (!com.f1.utils.ServerSocketAcceptor.handleEntitlements(entitlements, ssocket, socket))
						continue;
					connection = new ServerConnection(socket, ssocket instanceof SSLServerSocket);
					MsgDirectHelper.newThread(connection, MsgDirectHelper.THREAD_PROCESS_MESSAGES, name, false);
				} catch (Throwable e) {
					LH.severe(log, "Error accepting for ", this, e);
					if (connection != null)
						connection.close();
				}
			}

		}

	}

	public class ServerConnection implements Runnable, Closeable, MsgExternalConnection {

		final private Socket socket;
		final private boolean secure;
		final private String remoteHost;
		final private DataInputStream inputStream;
		final private FastBufferedOutputStream outputStream;

		private boolean readMode;
		private String topicName;
		private String remoteProcessUid;
		private ServerReadConnection readConnection;
		private ServerWriteConnection writeConnection;
		private boolean isDurable;
		volatile boolean isClosed;
		volatile private int missedKeepAlives = 0;
		private boolean hasReceivedKeepAlive = false;

		public ServerConnection(Socket socket, boolean secure) throws IOException {
			IOH.optimize(socket);
			this.secure = secure;
			this.socket = socket;
			socket.setKeepAlive(true);
			String remoteHost = socket.getRemoteSocketAddress().toString();
			if (remoteHost.startsWith("/"))
				this.remoteHost = remoteHost.substring(1);
			else
				this.remoteHost = remoteHost;
			outputStream = new FastBufferedOutputStream(socket.getOutputStream(), MsgDirectConnection.BUFFER_SIZE);
			inputStream = new DataInputStream(new FastBufferedInputStream(socket.getInputStream(), MsgDirectConnection.BUFFER_SIZE));
		}

		public Socket getSocket() {
			return this.socket;
		}

		private boolean handshake() throws IOException {
			StringToByteArrayConverter.writeString(MsgDirectClientSocket.HEADER_V1 + "P", outputStream);
			StringToByteArrayConverter.writeString(EH.getProcessUid(), outputStream);
			outputStream.flush();
			String header = StringToByteArrayConverter.readString(inputStream);
			if (!header.startsWith(MsgDirectClientSocket.HEADER_V1))
				throw new RuntimeException("Invalid header");
			char parts[] = header.substring(5).toCharArray();

			for (char c : parts) {
				if (c == 'd') {
					char mode = inputStream.readChar();
					if (mode == 'R')
						readMode = true;
					else if (mode == 'W')
						readMode = false;
					else
						throw new MsgException(this + ":unknown mode: " + mode);
				} else if (c == 'T')
					topicName = StringToByteArrayConverter.readString(inputStream);
				else if (c == 'P')
					remoteProcessUid = StringToByteArrayConverter.readString(inputStream);
				else if (OH.isBetween(c, 'a', 'z'))
					LH.warning(log, "Unkown option ", c, "=", inputStream.readChar());
				else if (OH.isBetween(c, 'A', 'Z'))
					LH.warning(log, "Unkown option ", c, "=", StringToByteArrayConverter.readString(inputStream));
				else
					throw new RuntimeException("bad header option: " + c);
			}
			isDurable = topicName.startsWith(MsgDirectConnection.DURABLE_PREFIX);
			if (readMode) {
				readConnection = getServerReadConnection(topicName);
				writeConnection = null;
				if (isDurable) {
					List<Integer> missing = readConnection.incomingAcker.getMissing();
					outputStream.writeInt(missing.size());
					for (Integer i : missing)
						outputStream.writeInt(i);
				}
				outputStream.flush();
				readConnection.addConnection(this);

			} else {
				final String topic = SH.beforeFirst(topicName, "$", topicName);
				final String suffix = SH.afterFirst(topicName, "$", null);
				final MsgDirectOutputServerTopic msgTopic;
				try {
					msgTopic = (MsgDirectOutputServerTopic) connection.getOutputTopic(topic, suffix);
				} catch (Exception e) {
					if (logFiner)
						LH.finer(log, MsgDirectHelper.HANDSHAKE_ERROR, ": ", this, "  ==> Topic not found: ", topicName, e);
					else
						LH.warning(log, MsgDirectHelper.HANDSHAKE_ERROR, ": ", this, "  ==> Topic not found: ", topicName);
					return false;
				}
				writeConnection = msgTopic.getServerWriteConnection();

				readConnection = null;
				if (isDurable) {
					int size = inputStream.readInt();
					int lastAckId = 0;
					for (int i = 0; i < size; i++) {
						lastAckId = inputStream.readInt();
						byte[] data = writeConnection.outgoingPersister.readMessage(lastAckId);
						if (data != null) {
							if (logInfo)
								LH.info(log, "Resending ", lastAckId, " for ", topicName, " byte[", data.length, "]");
							ByteHelper.writeInt(-lastAckId, data, 0);
							outputStream.write(data);
						}
					}
					for (;;) {
						byte[] data = writeConnection.outgoingPersister.readMessage(++lastAckId);
						if (data != null) {
							if (logInfo)
								LH.info(log, "Resending ", lastAckId, " for ", topicName, " byte[", data.length, "]");
							ByteHelper.writeInt(-lastAckId, data, 0);
							outputStream.write(data);
						} else
							break;
					}
					try {
						writeConnection.sendSemaphore.aquire();
						for (;;) {
							byte[] data = writeConnection.outgoingPersister.readMessage(++lastAckId);
							if (data != null) {
								if (logInfo)
									LH.info(log, "Resending ", lastAckId, " for ", topicName, " byte[", data.length, "]");
								ByteHelper.writeInt(-lastAckId, data, 0);
								outputStream.write(data);
							} else
								break;
						}
					} finally {
						writeConnection.sendSemaphore.release();
					}
				}
				outputStream.flush();
				writeConnection.addConnection(this);
			}
			return true;
		}
		public boolean isRead() {
			return readMode;
		}

		@Override
		public void run() {
			boolean ok = false;
			try {
				if (logInfo)
					LH.info(log, MsgDirectHelper.SERVER_ACCEPTED_CONNECTION, ": ", this);
				ok = handshake();
				if (logInfo)
					LH.info(log, MsgDirectHelper.CONNECTED, ": ", this);
			} catch (Exception e1) {
				ok = false;
				if (logInfo)
					LH.warning(log, MsgDirectHelper.HANDSHAKE_ERROR, ": ", this, e1);
			}
			if (!ok) {
				try {
					outputStream.flush();
				} catch (Exception e) {
				}
				close();
				return;
			}
			if (readConnection != null)
				readConnection.fireOnConnection(remoteHost, this);
			else
				writeConnection.fireOnConnection(remoteHost, this);
			for (;;) {
				try {
					this.missedKeepAlives = 0;
					if (logFiner)
						LH.finer(log, this, ": Waiting for Message...");
					int ackId = inputStream.readInt();
					int len = inputStream.readInt();
					this.missedKeepAlives = 0;
					if (len == 0) {
						if (logFiner)
							LH.finer(log, this, ": Got Keep alive ");
						this.hasReceivedKeepAlive = true;
						continue;
					}
					byte[] data = new byte[len];
					IOH.readData(inputStream, data, 0, len);
					MsgBytesEvent m = new MsgBytesEvent(data);
					m.setSource(this.remoteHost);
					m.putAckId(ackId, false);
					readConnection.onMessage(m);
					if (logFiner)
						LH.finer(log, this, ": Got Length ", len);
				} catch (SSLException e) {
					if (logFiner)
						LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY_SSL, ": ", this, e);
					else if (logInfo)
						LH.info(log, MsgDirectHelper.CLOSED_REMOTELY_SSL, ": ", this);
					break;
				} catch (SocketException e) {
					if ("Connection reset".equals(e.getMessage()) || "Socket closed".equals(e.getMessage())) {
						if (logFiner)
							LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY, ": ", this, e);
						else if (logInfo)
							LH.info(log, MsgDirectHelper.CLOSED_REMOTELY, ": ", this, ": ", e.getMessage());
					} else
						LH.warning(log, MsgDirectHelper.CLOSED_ERROR_READING, ": ", this, e);
					break;
				} catch (EOFException e) {
					if (logFiner)
						LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY_EOF, ": ", this, e);
					else if (logInfo)
						LH.info(log, MsgDirectHelper.CLOSED_REMOTELY_EOF, ": ", this);
					break;
				} catch (Exception e) {
					LH.warning(log, MsgDirectHelper.CLOSED_ERROR_READING, ": ", this, e);
					break;
				}
			}
			if (readConnection != null)
				readConnection.fireOnDisconnect(remoteHost, this);
			else
				writeConnection.fireOnDisconnect(remoteHost, this);
			close();
		}

		@Override
		public String toString() {
			String topicName = this.topicName == null ? "<NO_TOPIC>" : this.topicName;
			if (secure) {
				if (isRead())
					return "[" + remoteHost + "--" + sslPort + "--" + topicName + "--server,secure,read]";
				else
					return "[" + remoteHost + "--" + sslPort + "--" + topicName + "--server,secure,write]";
			} else {
				if (isRead())
					return "[" + remoteHost + "--" + sslPort + "--" + topicName + "--server,plain,read]";
				else
					return "[" + remoteHost + "--" + sslPort + "--" + topicName + "--server,plain,write]";
			}

		}

		@Override
		public void close() {
			isClosed = true;
			IOH.close(inputStream);
			IOH.close(outputStream);
			IOH.close(socket);
			if (readConnection != null)
				readConnection.removeConnection(this);
			if (writeConnection != null)
				writeConnection.removeConnection(this);

		}

		final public FastQueue<MsgBytesEvent> sendQueue = new FastQueue<MsgBytesEvent>();
		final public AtomicLong sendQueueSize = new AtomicLong();
		final private AtomicBoolean inThreadPool = new AtomicBoolean();

		public void sendMsgEvent(MsgBytesEvent msg) {
			if (logFiner)
				LH.finer(log, this, ": Queing Message for send (", System.identityHashCode(msg), "): ", msg.getSize());
			sendQueueSize.incrementAndGet();
			sendQueue.put(msg);
			sendToThreadPoolIfNeeded();

		}

		private class WriteRunner implements Runnable {

			@Override
			public void run() {
				runWrite();
			}

		}

		WriteRunner writeRunner = new WriteRunner();

		public boolean sendToThreadPool() {
			if (inThreadPool.compareAndSet(false, true)) {
				connection.getThreadPool().execute(writeRunner);
				return true;
			} else
				return false;
		}

		protected void sendToThreadPoolIfNeeded() {
			if (!sendQueue.isEmpty())
				sendToThreadPool();
		}

		public void runWrite() {
			for (int cnt = 0; cnt < MsgDirectClientSocket.FAIR_EVENT_BATCH_SIZE; cnt++) {
				MsgBytesEvent msg = sendQueue.get();
				if (msg == null)
					break;
				sendQueueSize.decrementAndGet();
				if (!isClosed)
					sendMsgEvent2(msg);
			}
			inThreadPool.set(false);
			sendToThreadPoolIfNeeded();
		}

		public void sendMsgEvent2(MsgBytesEvent msg) {
			try {
				if (logFiner)
					LH.finer(log, this, ": Sending Message (", System.identityHashCode(msg), "): ", msg.getSize());
				byte[] data = msg.getBytes();
				outputStream.writeInt(msg.askAckId());
				outputStream.writeInt(data.length);
				outputStream.write(data);
				outputStream.flush();
				if (logFiner)
					LH.finer(log, this, ": Sent Message (", System.identityHashCode(msg), "): ", msg.getSize());
			} catch (Exception e) {
				if (e instanceof SocketException) {
					if (logFiner)
						LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY, this, ":", e);
					else if (logInfo)
						LH.info(log, MsgDirectHelper.CLOSED_REMOTELY, this, ":", e.getMessage());
				} else
					LH.warning(log, "Error writing, closing", e);
				close();
			}
		}

		public boolean isClosed() {
			return isClosed;
		}

		public long getMessagesCount() {
			return readConnection != null ? readConnection.getMessagesCount() : writeConnection.getMessagesCount();
		}

		@Override
		public String getRemoteProcessUid() {
			return this.remoteProcessUid;
		}

	}

	public ServerReadConnection getServerReadConnection(String topicName) {
		ServerReadConnection r = topic2ReadConnections.get(topicName);
		if (r == null) {
			topic2ReadConnections.putIfAbsent(topicName, new ServerReadConnection(topicName, topicName.startsWith(MsgDirectConnection.DURABLE_PREFIX)));
			r = topic2ReadConnections.get(topicName);
		}
		return r;
	}

	public ServerWriteConnection getServerWriteConnection(String topicName) {
		ServerWriteConnection r = topic2WriteConnections.get(topicName);
		if (r == null) {
			topic2WriteConnections.putIfAbsent(topicName, new ServerWriteConnection(topicName, topicName.startsWith(MsgDirectConnection.DURABLE_PREFIX)));
			r = topic2WriteConnections.get(topicName);
		}
		return r;
	}

	public class ServerReadConnection implements Closeable {
		final private List<ServerConnection> connections = new CopyOnWriteArrayList<ServerConnection>();
		final private List<MsgDirectInputServerTopic> topics = new CopyOnWriteArrayList<MsgDirectInputServerTopic>();
		final private String topic;
		final private FileAcker incomingAcker;
		final private boolean isDurable;
		private long msgCount = 0;

		public ServerReadConnection(String topic, boolean isDurable) {
			this.isDurable = isDurable;
			this.topic = topic;
			if (isDurable)
				try {
					File ackFile = new File("ack/server/ackid/" + topic);
					this.incomingAcker = new FileAcker(ackFile, MsgDirectConnection.ACKER_SIZE);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			else
				this.incomingAcker = null;
		}

		public void fireOnConnection(String remoteHost, ServerConnection serverConnection) {
			for (MsgDirectInputServerTopic topic : topics) {
				connection.fireOnConnection(topic, topic.getConfiguration().getTopicName(), topic.getTopicSuffix(), remoteHost, false, serverConnection);
				topic.fireConnected(serverConnection);
			}
		}
		public void fireOnDisconnect(String remoteHost, ServerConnection serverConnection) {
			for (MsgDirectInputServerTopic topic : topics) {
				connection.fireOnDisconnect(topic, topic.getConfiguration().getTopicName(), topic.getTopicSuffix(), remoteHost, false, serverConnection);
				topic.fireDisconnected(serverConnection);
			}
		}

		public void onMessage(MsgBytesEvent msgBytesEvent) {
			if (msgBytesEvent.getBytes().length > 0)//skip HEARTBEAT
				msgCount++;
			if (isDurable)
				msgBytesEvent.registerAcker(incomingAcker);
			for (MsgDirectInputServerTopic topic : topics) {
				topic.broadcastMsgEvent(msgBytesEvent);
				topic.fireIncoming(msgBytesEvent);
			}
			ServerWriteConnection loopBack = topic2WriteConnections.get(topic);
			if (loopBack != null)
				loopBack.sendMsgEvent(msgBytesEvent);
		}

		public void addTopic(MsgDirectInputServerTopic topic) {
			topics.add(topic);
		}

		public void removeConnection(ServerConnection connection) {
			connections.remove(connection);
		}

		public void addConnection(ServerConnection connection) {
			connections.add(connection);
		}

		@Override
		public void close() throws IOException {
			for (ServerConnection c : connections)
				IOH.close(c);
		}

		public List<MsgExternalConnection> getExternalConnections() {
			return (List) connections;
		}

		public long getMessagesCount() {
			return msgCount;
		}

	}

	public class ServerWriteConnection implements Closeable {
		final public FastSemaphore sendSemaphore = new FastSemaphore();
		final private AckPersister outgoingPersister;
		final private List<ServerConnection> connections = new CopyOnWriteArrayList<ServerConnection>();
		final private String fulltopic;
		final private boolean isDurable;
		final private String topic;
		final private String suffix;
		private long msgCount;
		private AbstractMsgTopic msgTopic;

		protected void setTopic(MsgTopic msgTopic) {
			this.msgTopic = (AbstractMsgTopic) msgTopic;
		}
		public ServerWriteConnection(String fulltopic, boolean isDurable) {
			this.isDurable = isDurable;
			this.fulltopic = fulltopic;
			this.topic = SH.beforeFirst(fulltopic, "$", fulltopic);
			this.suffix = SH.afterFirst(fulltopic, "$", null);
			if (isDurable)
				try {
					this.outgoingPersister = new AckPersister(new File("ack/server/store/" + fulltopic), MsgDirectConnection.ACK_PERSISTER_SIZE, false);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			else
				this.outgoingPersister = null;
		}

		public void fireOnConnection(String remoteHost, ServerConnection serverConnection) {
			connection.fireOnConnection(msgTopic, topic, suffix, remoteHost, true, serverConnection);
			msgTopic.fireConnected(serverConnection);
		}

		public void fireOnDisconnect(String remoteHost, ServerConnection serverConnection) {
			connection.fireOnDisconnect(msgTopic, topic, suffix, remoteHost, true, serverConnection);
			msgTopic.fireDisconnected(serverConnection);
		}

		public void removeConnection(ServerConnection connection) {
			connections.remove(connection);
		}

		public void addConnection(ServerConnection connection) {
			connections.add(connection);
		}

		public void sendMsgEvent(MsgBytesEvent message) {

			if (logFiner)
				LH.finer(log, this, ": Queing Message for send (", System.identityHashCode(message), ") to ", connections.size(), " connections: ", message.getSize());
			try {
				sendSemaphore.aquire();
				if (message.getBytes().length > 0)//skip HEARTBEAT
					msgCount++;
				if (isDurable)
					outgoingPersister.writePreparedMessageAndStoreAckId(message.getBytes());
				this.msgTopic.fireOutgoing(message, connections.size());
				for (ServerConnection connection : connections)
					if (!connection.isClosed())
						connection.sendMsgEvent(message);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				sendSemaphore.release();
			}
			message.ack(null);
		}
		@Override
		public void close() throws IOException {
			for (ServerConnection c : connections)
				IOH.close(c);
		}

		public int getConnectionsCount() {
			return connections.size();
		}

		public Collection<MsgExternalConnection> getExternalConnections() {
			return (List) connections;
		}

		public long getMessagesCount() {
			return msgCount;
		}
		public long getSendQueueSize() {
			final List<ServerConnection> t = this.connections;
			long r = 0;
			for (int i = 0; i < t.size(); i++)
				r += t.get(i).sendQueueSize.get();
			return r;
		}

	}

	public void sendKeepAlive() {
		for (ServerWriteConnection i : this.topic2WriteConnections.values()) {
			i.sendMsgEvent(MsgDirectConnection.KEEP_ALIVE);
			for (ServerConnection connection : i.connections)
				if (connection.hasReceivedKeepAlive && !connection.isClosed && connection.missedKeepAlives++ > MsgDirectHelper.MAX_MISSED_KEEP_ALIVES) {
					if (logInfo)
						LH.info(log, "Closing write socket due to missed keep alives: ", connection.missedKeepAlives, "connection:", connection);
					if (connection.readConnection != null)
						connection.readConnection.fireOnDisconnect(connection.remoteHost, connection);
					else
						connection.writeConnection.fireOnDisconnect(connection.remoteHost, connection);
					connection.close();
				}
		}
		for (ServerReadConnection i : this.topic2ReadConnections.values()) {
			for (ServerConnection connection : i.connections) {
				connection.sendMsgEvent(MsgDirectConnection.KEEP_ALIVE);
				if (connection.hasReceivedKeepAlive && !connection.isClosed && connection.missedKeepAlives++ > MsgDirectHelper.MAX_MISSED_KEEP_ALIVES) {
					if (logInfo)
						LH.info(log, "Closing read socket due to missed keep alives: ", connection.missedKeepAlives, "connection:", connection);
					if (connection.readConnection != null)
						connection.readConnection.fireOnDisconnect(connection.remoteHost, connection);
					else
						connection.writeConnection.fireOnDisconnect(connection.remoteHost, connection);
					connection.close();
				}
			}
		}
	}
}
