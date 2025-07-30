/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;

import com.f1.msg.MsgBytesEvent;
import com.f1.msg.MsgException;
import com.f1.msg.MsgExternalConnection;
import com.f1.msg.MsgTopic;
import com.f1.utils.AH;
import com.f1.utils.ByteHelper;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.ack.AckPersister;
import com.f1.utils.ack.FileAcker;
import com.f1.utils.concurrent.FastQueue;
import com.f1.utils.concurrent.FastSemaphore;
import com.f1.utils.converter.bytes.StringToByteArrayConverter;

public class MsgDirectClientSocket implements Closeable, Runnable, MsgExternalConnection {

	public static final String HEADER_V1 = "3FDM|";

	private static final byte[] KEEP_ALIVE_BYTES = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };//8 bytes represent both ackid,length (both zero)

	static final int FAIR_EVENT_BATCH_SIZE = 1000;

	private final Logger log;

	private String host;
	private int port;
	final private String fullTopicName;
	final private boolean writeMode;// this is the mode of the REMOTE side (so it's backwards)
	final private MsgDirectInputClientTopic inputTopic;
	final private MsgDirectOutputClientTopic outputTopic;
	private boolean isClosed;
	private Socket socket;
	private FastBufferedOutputStream outputStream;
	private DataInputStream inputStream;
	private boolean isAlive;
	private boolean isDurable;

	final private AckPersister outgoingPersister;
	final private FileAcker incomingAcker;

	final private MsgDirectConnection connection;

	private MsgTopic topic;

	private String remoteHost;

	private boolean ssl;

	private MsgDirectConnectionConfiguration config;

	private OutputStream rawOutput;

	final private String[] hosts;

	final private int[] ports;

	private boolean logFiner;

	private boolean logInfo;

	public MsgDirectClientSocket(MsgDirectConnection connection, String[] hosts, int[] ports, int[] sslPorts, MsgTopic topic, MsgDirectInputClientTopic inputTopic) {
		log = Logger.getLogger(connection.getLogName(getClass().getName()));
		logFiner = log.isLoggable(Level.FINER);
		logInfo = log.isLoggable(Level.INFO);

		this.connection = connection;
		config = connection.getConfiguration();
		this.hosts = hosts;
		if (AH.isntEmpty(sslPorts) && sslPorts[0] != MsgDirectTopicConfiguration.NO_PORT) {
			this.ssl = true;
			this.ports = sslPorts;
		} else {
			this.ssl = config.getForceSsl();
			this.ports = ports;
		}

		if (AH.isEmpty(hosts))
			throw new IllegalStateException("need at least one host");

		if (hosts.length != ports.length)
			throw new IllegalStateException("# of hosts doesn't equal # of ports");

		setCurrentHostPort(hosts[0], ports[0]);

		this.fullTopicName = topic.getFullTopicName();
		this.topic = topic;
		this.inputTopic = inputTopic;
		this.isClosed = false;
		this.writeMode = inputTopic == null;
		this.outputTopic = (MsgDirectOutputClientTopic) (writeMode ? topic : null);
		isDurable = fullTopicName.startsWith(MsgDirectConnection.DURABLE_PREFIX);
		if (isDurable) {
			if (!writeMode) {
				File ackFile = new File("ack/client/ackid/" + fullTopicName);
				try {
					this.incomingAcker = new FileAcker(ackFile, MsgDirectConnection.ACKER_SIZE);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				this.outgoingPersister = null;
			} else {
				try {
					this.outgoingPersister = new AckPersister(new File("ack/client/store/" + fullTopicName), MsgDirectConnection.ACK_PERSISTER_SIZE, false);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				this.incomingAcker = null;
			}
		} else {
			outgoingPersister = null;
			incomingAcker = null;
		}
	}

	private void setCurrentHostPort(String host, int port) {
		this.host = host;
		this.port = port;
		this.remoteHost = host + ":" + port;
	}

	public void connectSocket() throws UnknownHostException, IOException {
		assertNotClosed();
		if (isAlive)
			return;
		innerClose();

		//iterate over hosts/ports combos till first success
		for (int i = 0; i < hosts.length; i++) {
			setCurrentHostPort(hosts[i], ports[i]);

			try {
				if (ssl) {
					if (SH.isnt(config.getKeystorePassword())) {
						LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL missing password");
						throw new RuntimeException("Bad configuration:Missing keystore password");
					}
					if (SH.is(config.getKeystoreFile() != null))
						socket = IOH.openSSLClientSocketWithReason(host, port, config.getKeystoreFile(), config.getKeystorePassword(), null);
					else if (SH.is(config.getKeystoreContents())) {
						socket = IOH.openSSLClientSocketWithReason(host, port, config.getKeystoreContents(), config.getKeystorePassword(), null);
					} else {
						LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL. Must supply either keystore file or keystore contents. Both missing");
						throw new RuntimeException("Bad configuration:Missing keystore files");
					}
				} else {
					socket = IOH.openClientSocketWithReason(host, port, null);
				}
				break;
			} catch (Exception e) {
				if (OH.findInnerException(e, GeneralSecurityException.class) != null)
					LH.warning(log, MsgDirectHelper.CONFIG_ERROR, ": ", this, " => SSL. Key Error ", e);
				if (i == hosts.length - 1)
					throw new IOException(e);
			}
		}

		socket.setKeepAlive(true);
		inputStream = new DataInputStream(new FastBufferedInputStream(socket.getInputStream(), MsgDirectConnection.BUFFER_SIZE));
		outputStream = new FastBufferedOutputStream(rawOutput = socket.getOutputStream(), MsgDirectConnection.BUFFER_SIZE);
	}

	public void connectHandshake() throws IOException {
		StringToByteArrayConverter.writeString(HEADER_V1 + "dTP", outputStream);
		outputStream.writeChar(writeMode ? 'R' : 'W');
		StringToByteArrayConverter.writeString(fullTopicName, outputStream);
		StringToByteArrayConverter.writeString(EH.getProcessUid(), outputStream);
		outputStream.flush();
		String header = StringToByteArrayConverter.readString(inputStream);
		if (!header.startsWith(HEADER_V1))
			throw new RuntimeException("Invalid header");
		char parts[] = header.substring(HEADER_V1.length()).toCharArray();
		for (char c : parts) {
			if (c == 'P')
				this.remoteProcessUid = StringToByteArrayConverter.readString(inputStream);
			else if (OH.isBetween(c, 'a', 'z'))
				LH.warning(log, "Unkown option ", c, "=", inputStream.readChar());
			else if (OH.isBetween(c, 'A', 'Z'))
				LH.warning(log, "Unkown option ", c, "=", StringToByteArrayConverter.readString(inputStream));
			else
				throw new RuntimeException("bad header option: " + c);
		}
		if (isDurable) {
			if (!writeMode) {
				isAlive = true;
				List<Integer> missing = incomingAcker.getMissing();
				outputStream.writeInt(missing.size());
				for (Integer i : missing)
					outputStream.writeInt(i);
				outputStream.flush();
			} else {
				int size = inputStream.readInt();
				int lastAckId = 0;
				for (int i = 0; i < size; i++) {
					lastAckId = inputStream.readInt();
					byte[] data = outgoingPersister.readMessage(lastAckId);
					if (data != null) {
						if (logInfo)
							LH.info(log, "Resending ", lastAckId, " for ", fullTopicName, " byte[", data.length, "]");
						ByteHelper.writeInt(-lastAckId, data, 0);
						outputStream.write(data);
					}
				}
				for (;;) {
					byte[] data = outgoingPersister.readMessage(++lastAckId);
					if (data != null) {
						if (logInfo)
							LH.info(log, "Resending ", lastAckId, " for ", fullTopicName, " byte[", data.length, "]");
						ByteHelper.writeInt(-lastAckId, data, 0);
						outputStream.write(data);
					} else
						break;
				}
				try {
					sendSemaphore.aquire();
					for (;;) {
						byte[] data = outgoingPersister.readMessage(++lastAckId);
						if (data != null) {
							if (logInfo)
								LH.info(log, "Resending ", lastAckId, " for ", fullTopicName, " byte[", data.length, "]");
							ByteHelper.writeInt(-lastAckId, data, 0);
							outputStream.write(data);
						} else
							break;
					}
				} finally {
					sendSemaphore.release();
				}
				outputStream.flush();
				isAlive = true;
				// TODO:add messages inflight during this time
			}
		} else {
			isAlive = true;
		}
		Thread t = MsgDirectHelper.newThread(this, MsgDirectHelper.THREAD_PROCESS_MESSAGES, this.toString(), true);
		if (inputTopic != null)
			inputTopic.fireConnected(this);
		else
			outputTopic.fireConnected(this);
	}

	private void innerClose() {
		isAlive = false;
		IOH.close(inputStream);
		IOH.close(outputStream);
		IOH.close(socket);
	}

	private final FastSemaphore sendSemaphore = new FastSemaphore();

	private AtomicLong messagesCount = new AtomicLong(0);

	private boolean hasReceivedKeepAlive;

	private String remoteProcessUid;

	private void sendMessageEvent2(MsgBytesEvent event) {

		if (logFiner)
			LH.finer(log, this, ": Sending Message (", System.identityHashCode(event), "): ", event.getSize());
		if (event.getBytes().length > 0)//skip HEARTBEAT
			messagesCount.incrementAndGet();
		try {
			try {
				sendSemaphore.aquire();
				if (outgoingPersister != null)
					outgoingPersister.writePreparedMessageAndStoreAckId(event.getBytes());
				assertNotClosed();
				if (!isAlive)
					return;
				byte[] data = event.getBytes();
				outputStream.writeInt(event.askAckId());
				outputStream.writeInt(data.length);
				outputStream.write(data);
				if (outputTopic != null)
					outputTopic.fireOutgoing(event, 1);
			} finally {
				sendSemaphore.release();
			}
			event.ack(null);
			if (logFiner)
				LH.finer(log, this, ": Sent Message (", System.identityHashCode(event), "): ", event.getSize());
			return;
		} catch (SocketException e) {
			if (logFiner)
				LH.finer(log, MsgDirectHelper.CLOSED_SEND_MESSAGE_ERROR, ": ", this, e);
			else if (logInfo)
				LH.info(log, MsgDirectHelper.CLOSED_SEND_MESSAGE_ERROR, ": ", this);
			LH.warning(log, "Socket disconnect: ", e.getMessage());
			innerClose();
		} catch (Exception e) {
			if (logFiner)
				LH.finer(log, MsgDirectHelper.CLOSED_SEND_MESSAGE_ERROR, ": ", this, e);
			else if (logInfo)
				LH.info(log, MsgDirectHelper.CLOSED_SEND_MESSAGE_ERROR, ": ", this);
			LH.warning(log, "Bad Socket disconnected", e);
			innerClose();
		}
	}
	private void flush() {
		try {
			try {
				sendSemaphore.aquire();
				if (isAlive)
					outputStream.flush();
			} finally {
				sendSemaphore.release();
			}
			return;
		} catch (SocketException e) {
			LH.warning(log, "Socket disconnect: ", e.getMessage());
			innerClose();
		} catch (Exception e) {
			LH.warning(log, "Bad Socket disconnected", e);
			innerClose();
		}
	}

	@Override
	public String toString() {
		if (ssl) {
			if (writeMode)
				return "[" + host + ":" + port + "--" + fullTopicName + "--client,secure,write]";
			else
				return "[" + host + ":" + port + "--" + fullTopicName + "--client,secure,read]";
		} else {
			if (writeMode)
				return "[" + host + ":" + port + "--" + fullTopicName + "--client,plain,write]";
			else
				return "[" + host + ":" + port + "--" + fullTopicName + "--client,plain,read]";
		}
	}
	@Override
	public void close() throws IOException {
		isClosed = true;
		innerClose();
	}

	@Override
	public void run() {
		assertNotClosed();
		if (!isAlive)
			return;
		connection.fireOnConnection(topic, topic.getConfiguration().getTopicName(), topic.getTopicSuffix(), remoteHost, !writeMode, this);
		while (!isClosed)
			try {
				this.missedKeepAlives = 0;
				if (logFiner)
					LH.finer(log, this, ": Waiting for Message...");
				int ackId = inputStream.readInt();
				int len = inputStream.readInt();
				if (len < 0)
					throw new EOFException("readInt() returned len: " + len);
				this.missedKeepAlives = 0;
				if (len == 0) {
					this.hasReceivedKeepAlive = true;
					if (logFiner)
						LH.finer(log, this, ": Got Keep alive ");
					continue;
				}
				if (logFiner)
					LH.finer(log, this, ": Got Length ", len);
				byte[] data = new byte[len];
				IOH.readData(inputStream, data, 0, len);
				MsgBytesEvent m = new MsgBytesEvent(data);
				if (logFiner)
					LH.finer(log, this, ": Firing Message");
				m.setSource(this.remoteHost);
				m.putAckId(ackId, false);
				if (isDurable)
					m.registerAcker(incomingAcker);
				inputTopic.broadcastMsgEvent(m);
				inputTopic.fireIncoming(m);
			} catch (SSLException e) {
				if (logFiner)
					LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY_SSL, ": ", this, e);
				else if (logInfo)
					LH.info(log, MsgDirectHelper.CLOSED_REMOTELY_SSL, ": ", this);
				innerClose();
				break;
			} catch (SocketException e) {
				if (logFiner)
					LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY, ": ", this, e);
				else if (logInfo)
					LH.info(log, MsgDirectHelper.CLOSED_REMOTELY, ": ", this);
				innerClose();
				break;
			} catch (EOFException e) {
				if (logFiner)
					LH.finer(log, MsgDirectHelper.CLOSED_REMOTELY_EOF, ": ", this, e);
				else if (logInfo)
					LH.info(log, MsgDirectHelper.CLOSED_REMOTELY_EOF, ": ", this);
				innerClose();
				break;

			} catch (Exception e) {
				LH.warning(log, MsgDirectHelper.CLOSED_ERROR_READING, ": ", this, e);
				innerClose();
				break;
			}
		connection.fireOnDisconnect(topic, topic.getConfiguration().getTopicName(), topic.getTopicSuffix(), remoteHost, !writeMode, this);
		if (inputTopic != null)
			inputTopic.fireDisconnected(this);
		else
			outputTopic.fireDisconnected(this);
	}
	protected void assertNotClosed() {
		if (isClosed)
			throw new MsgException("closed");
	}

	public boolean isAlive() {
		return isAlive;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public long getMessagesCount() {
		return messagesCount.get();
	}

	public void sendMessageEvent(MsgBytesEvent msg) {
		if (logFiner)
			LH.finer(log, this, ": Queing Message for send (", System.identityHashCode(msg), "): ", msg.getSize());
		if (!isClosed) {
			sendQueueSize.incrementAndGet();
			sendQueue.put(msg);
			sendToThreadPoolIfNeeded();
		}

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
		int cnt = 0;
		for (; cnt < MsgDirectClientSocket.FAIR_EVENT_BATCH_SIZE; cnt++) {
			if (isClosed) {
				drainQueue();
				break;
			}
			MsgBytesEvent msg = sendQueue.get();
			if (msg == null)
				break;
			sendQueueSize.decrementAndGet();
			sendMessageEvent2(msg);
		}
		if (cnt > 0 && !isClosed)
			flush();
		inThreadPool.set(false);
		sendToThreadPoolIfNeeded();
	}

	private void drainQueue() {
		int cnt = 0;
		while (sendQueue.get() != null)
			cnt++;
		sendQueueSize.addAndGet(-cnt);
		LH.info(log, this, ":Closed during write, Drained ", cnt, " pending message(s) from queue");
	}

	final public FastQueue<MsgBytesEvent> sendQueue = new FastQueue<MsgBytesEvent>();
	final public AtomicLong sendQueueSize = new AtomicLong();
	final private AtomicBoolean inThreadPool = new AtomicBoolean();

	volatile private int missedKeepAlives;

	public void sendKeepAlive() {
		if (isAlive() && !isClosed()) {
			try {
				sendMessageEvent(MsgDirectConnection.KEEP_ALIVE);
				if (this.hasReceivedKeepAlive && !isClosed && missedKeepAlives++ > MsgDirectHelper.MAX_MISSED_KEEP_ALIVES) {
					if (logInfo)
						LH.info(log, MsgDirectHelper.CLOSED_KEEP_ALIVES_MISSED, ": ", connection);
					innerClose();
					connection.fireOnDisconnect(topic, topic.getConfiguration().getTopicName(), topic.getTopicSuffix(), remoteHost, !writeMode, this);
					if (inputTopic != null)
						inputTopic.fireDisconnected(this);
					else
						outputTopic.fireDisconnected(this);
				}
			} catch (Exception e) {
				if (logFiner)
					LH.finer(log, MsgDirectHelper.CLOSED_KEEP_ALIVE_FAILED, ": ", this, e);
				else if (logInfo)
					LH.info(log, MsgDirectHelper.CLOSED_KEEP_ALIVE_FAILED, ": ", this);
				innerClose();
			}

		}

	}

	public Socket getSocket() {
		return this.socket;
	}

	@Override
	public String getRemoteProcessUid() {
		return this.remoteProcessUid;
	}

	public long getSendQueueSize() {
		return this.sendQueueSize.get();
	}
}
