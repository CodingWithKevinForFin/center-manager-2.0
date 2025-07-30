package com.f1.ami.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import com.f1.base.Password;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;

/**
 * 
 * Used to programmatically connect to the Ami Relay's realtime streaming protocol.
 * <P>
 * <B>IMPORANT NOTE ON Threading: </B>Only a single thread should be interacting with this client at a time. If auto process incoming is enabled (default), then a second thread is
 * spun up internally used for processing call backs.:
 * <P>
 * after constructing, add yourself as a listener using {@link #addListener(AmiClientListener)} then use the {@link #start(String, int, String, int)} command to startup
 * <P>
 * Following that you should listen for the {@link AmiClientListener#onLoggedIn(RawAmiClient)} and then begin sending messages:
 * <P>
 * Messages are sent one at a time, by starting a message, adding params, and then sending:<BR>
 * 
 * 1) {@link #startObjectMessage(String, CharSequence, long)} --or-- {@link #startDeleteMessage(String, String)} --or-- {@link #startResponseMessage(String)} --or--
 * {@link #startStatusMessage()} --or-- {@link #startCommandDefinition(String)} <BR>
 * 2) Any combination of addMessageParam* calls to add attributes <BR>
 * 3) {@link #sendMessage()} or {@link #sendMessageAndFlush()}
 * 
 * 
 * 
 * 
 */
public class AmiClient implements Closeable {

	/**
	 * Same as setting O="QUIET", which tells ami relay to not send message ack back to this client
	 */
	public static final int ENABLE_QUIET = 4;

	/**
	 * By default, this client will automatically read inbound messages and process them in a seperate thread, if disabled you must manually call {@link #pumpIncomingEvent()}
	 */
	public static final int ENABLE_AUTO_PROCESS_INCOMING = 2;

	/**
	 * By default, this client will keep trying to reconnect to the ami server, see {@link #setAutoReconnectFrequencyMs(long)}
	 */
	public static final int DISABLE_AUTO_RECONNECT = 8;

	/**
	 * Should this client send timestamps, useful for enabling delayed message detection from ami client
	 */
	public static final int ENABLE_SEND_TIMESTAMPS = 32;

	/**
	 * Should this client send sequence numbers, useful for linking a particular client message to the message in ami server
	 */
	public static final int ENABLE_SEND_SEQNUM = 64;

	/**
	 * Should this client log errors each time a connection retry fails, if not set then just on the first connection failure
	 */
	public static final int LOG_CONNECTION_RETRY_ERRORS = 128;

	/**
	 * If set, all messages will be logged using standard java.util.logging.Logger framework
	 */
	public static final int LOG_MESSAGES = 256;

	/**
	 * If set, a seperate thread is started which will automatically flush messages as they are written.
	 */
	public static final int ENABLE_AUTO_FLUSH_OUTGOING = 512;

	public static final byte RESPONSE_STATUS_DONT_CLOSE_DIALOG = 2;
	public static final byte RESPONSE_STATUS_UPDATE_RECORD = 3;
	public static final byte RESPONSE_STATUS_OKAY = 0;

	private static final Logger log = LH.get();
	static final public String DEFAULT_HOST = "localhost";
	static final public int DEFAULT_PORT = 3289;
	private String host;
	private int port;

	final private RawAmiClient client = new RawAmiClient();

	private String loginId;
	private int options;
	boolean autoProcessIncoming;
	private boolean quietMode;
	private boolean autoReconnect;
	private boolean includeSeqNum;
	private boolean includeNow;
	private boolean autoflush;
	private File keystoreFile;
	private Password keystorePassword;
	final private Runner runner;
	volatile private boolean running = false;
	private long autoReconnectFrequencyMs = 1000;

	private boolean logConnectionRetryErrors;

	private List<AmiClientListener> clientListeners = new ArrayList<AmiClientListener>();

	private Thread thread;

	public AmiClient() {
		this.runner = new Runner();
		this.client.addListener(this.runner);
	}

	public void start(Socket socket, String loginId, int options) throws IOException {
		if (this.running)
			throw new IllegalStateException("Already init");
		this.running = true;
		this.host = null;
		this.port = -1;
		this.loginId = loginId;
		setOptions(options | DISABLE_AUTO_RECONNECT);
		this.client.connect(socket);
		sendLogin();
		if (autoProcessIncoming) {
			startPumping();
		}
	}

	public void start(String host, int port, String loginId, int options) {
		this.start(host, port, loginId, options, null, (Password) null);
	}
	public void start(String host, int port, String loginId, int options, File keystoreFile, String keystorePassword) {
		this.start(host, port, loginId, options, keystoreFile, Password.valueOf(keystorePassword));
	}
	public void start(String host, int port, String loginId, int options, File keystoreFile, Password keystorePassword) {
		if (this.running)
			throw new IllegalStateException("Already init");
		this.keystoreFile = keystoreFile;
		this.keystorePassword = keystorePassword;
		this.running = true;
		this.host = host;
		this.port = port;
		this.loginId = loginId;
		setOptions(options);
		if (this.autoReconnect || autoProcessIncoming) {
			if (client.connect(AmiClient.this.host, AmiClient.this.port, this.logConnectionRetryErrors, this.keystoreFile, this.keystorePassword, this.autoflush))
				sendLogin();
			startPumping();
		}
	}

	private void setOptions(int options) {
		this.options = options;
		this.autoProcessIncoming = MH.anyBits(options, ENABLE_AUTO_PROCESS_INCOMING);
		this.quietMode = MH.anyBits(options, ENABLE_QUIET);
		this.autoReconnect = !MH.anyBits(options, DISABLE_AUTO_RECONNECT);
		this.includeSeqNum = MH.anyBits(options, ENABLE_SEND_SEQNUM);
		this.includeNow = MH.anyBits(options, ENABLE_SEND_TIMESTAMPS);
		this.logConnectionRetryErrors = MH.anyBits(options, LOG_CONNECTION_RETRY_ERRORS);
		this.autoflush = MH.anyBits(options, ENABLE_AUTO_FLUSH_OUTGOING);
		this.setDebugMessages(MH.anyBits(options, LOG_MESSAGES));
	}

	/**
	 * @return options passed into {@link #start(String, int, String, int)}
	 */
	public int getOptions() {
		return options;
	}

	private void startPumping() {
		this.thread = new Thread(this.runner, "AmiClient-" + this.host + ":" + this.port);
		this.thread.setDaemon(true);
		this.thread.start();
	}

	private class Runner implements RawAmiClientListener, Runnable {
		private volatile boolean needsNotify = false;

		@Override
		public void run() {
			if (!AmiClient.this.autoReconnect && !AmiClient.this.autoProcessIncoming)
				return;
			try {
				while (running) {
					try {
						if (!isConnected()) {
							if (AmiClient.this.autoReconnect) {
								while (running && !isConnected())
									if (!client.connect(AmiClient.this.host, AmiClient.this.port, AmiClient.this.logConnectionRetryErrors, AmiClient.this.keystoreFile,
											AmiClient.this.keystorePassword, autoflush))
										OH.sleep(AmiClient.this.getAutoReconnectFrequencyMs());
									else {
										sendLogin();
									}
							} else {
								synchronized (this) {
									if (!isConnected())
										waitForNotify();
								}
							}
						} else {
							if (AmiClient.this.autoProcessIncoming) {
								while (running)
									if (!client.pumpIncomingEvent()) {
										client.disconnect();
										break;
									}
							} else {
								synchronized (this) {
									if (isConnected())
										waitForNotify();
								}
							}
						}
					} catch (InterruptedException t) {
					} catch (Throwable t) {
						LH.warning(log, "Error in ami client thread for " + AmiClient.this.host + ":" + AmiClient.this.port, t);
					}
				}
			} finally {
				AmiClient.this.client.removeListener(Runner.this);
			}
		}
		private void waitForNotify() throws InterruptedException {
			if (!running)
				return;
			try {
				this.needsNotify = true;
				wait(3600000);
			} finally {
				this.needsNotify = false;
			}

		}

		@Override
		public void onConnect(RawAmiClient source) {
			synchronized (this) {
				if (needsNotify)
					notifyAll();
			}
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onConnect(AmiClient.this);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}

		@Override
		public void onDisconnect(RawAmiClient source) {
			synchronized (this) {
				if (needsNotify)
					notifyAll();
			}
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onDisconnect(AmiClient.this);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}
		@Override
		public void onMessageReceived(RawAmiClient source, long now, long seqnum, int status, CharSequence message) {
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onMessageReceived(AmiClient.this, now, seqnum, status, message);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}
		@Override
		public void onMessageSent(RawAmiClient source, CharSequence message) {
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onMessageSent(AmiClient.this, message);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}

		@Override
		public void onLoggedIn(RawAmiClient rawAmiClient) {
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onLoggedIn(AmiClient.this);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}

		@Override
		public void onCommand(RawAmiClient source, String requestId, String cmd, String userName, String type, String id, Map<String, Object> params) {
			for (int i = 0; i < AmiClient.this.clientListeners.size(); i++)
				try {
					AmiClient.this.clientListeners.get(i).onCommand(AmiClient.this, requestId, cmd, userName, type, id, params);
				} catch (Exception e) {
					LH.warning(log, "Listener threw exception: ", e);
				}
		}
	}

	protected void sendLogin() {
		client.startMessage('L', includeSeqNum, includeNow);
		client.addMessageParamString("I", loginId);
		if (quietMode)
			client.addMessageParamString("O", "QUIET");
		sendMessageAndFlush();
		client.fireOnLogin();
	}

	/**
	 * send the pending message to AMI and block until the message is fully read by AMI
	 * 
	 * @return false if there was an io error
	 */
	public boolean sendMessageAndFlush() {
		return client.sendMessageAndFlush();
	}
	public void sendMessageAndFlush(CharSequence cs) {
		client.sendMessageAndFlush(cs);
	}

	/**
	 * Close the connection and stop trying to reconnect
	 */
	public void close() {
		if (this.running == false)
			throw new IllegalStateException("not running");
		if (this.isConnected()) {
			sendLogout();
		}
		this.running = false;
		if (this.thread != null)
			this.thread.interrupt();
		this.client.disconnect();

	}

	/**
	 * send pending message buffer to AMI, can be called at anytime
	 */
	public void flush() {
		this.client.flush();
	}

	/**
	 * Try and reconnect, will also sned login (L) instructions. Note, this can only be called if {@link #DISABLE_AUTO_RECONNECT} is set in {@link #start(String, int, String, int)}
	 * options
	 * 
	 * @return true if connected
	 */
	public boolean connect() {
		if (this.autoReconnect)
			throw new IllegalStateException("Can not manually connect when autoReconnect is enabled");
		boolean r = client.connect(this.host, this.port, this.logConnectionRetryErrors, this.keystoreFile, this.keystorePassword, autoflush);
		if (r)
			sendLogin();
		return r;
	}

	/**
	 * check for an incoming message and fire appropriate call back if found. Note, this can only be called if {@link #DISABLE_AUTO_RECONNECT} is set in
	 * {@link #start(String, int, String, int)}
	 * 
	 * @return true if a message was processed, false if there were no queued messages for processing.
	 */
	public boolean pumpIncomingEvent() {
		if (this.autoProcessIncoming)
			throw new IllegalStateException("Can not manually process incoming messages when auto-process-incoming is enabled");
		return client.pumpIncomingEvent();
	}

	/**
	 * @return true if there is a live connection to AMI
	 */
	public boolean isConnected() {
		return this.client.getIsConnected();
	}

	/**
	 * start at status (S) message
	 * 
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startStatusMessage() {
		client.startMessage('S', includeSeqNum, includeNow);
		return this;
	}

	/**
	 * start an object (O) message
	 * 
	 * @param type
	 *            - (T=) param
	 * @param id
	 *            - (I=) param, optional
	 * @param expiresOn
	 *            (E) param, 0 for no expiry,positive number for epoc aboslute time. negative for offset into future
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startObjectMessage(String type, CharSequence id, long expiresOn) {
		client.startMessage('O', includeSeqNum, includeNow);
		client.addMessageParamString("T", type);
		if (id != null)
			client.addMessageParamString("I", id);
		if (expiresOn != 0)
			client.addMessageParamLong("E", expiresOn);
		return this;
	}

	/**
	 * start an object (O) message
	 * 
	 * @param type
	 *            - (T) param
	 * @param id
	 *            - (I=) param, optional
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startObjectMessage(String type, CharSequence id) {
		client.startMessage('O', includeSeqNum, includeNow);
		client.addMessageParamString("T", type);
		if (id != null)
			client.addMessageParamString("I", id);
		return this;
	}

	/**
	 * start a response (R) message
	 * 
	 * @param origRequestId
	 *            - (I=) field
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */

	public AmiClient startResponseMessage(String origRequestId) {
		client.startMessage('R', includeSeqNum, includeNow);
		client.addMessageParamString("I", origRequestId);
		return this;
	}
	/**
	 * start a response (R) message
	 * 
	 * @param origRequestId
	 *            (I=) field
	 * @param status
	 *            (S=) param
	 * @param message
	 *            (M=) param
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startResponseMessage(String origRequestId, int status, String message) {
		client.startMessage('R', includeSeqNum, includeNow);
		client.addMessageParamString("I", origRequestId);
		client.addMessageParamInt("S", status);
		client.addMessageParamString("M", message);
		return this;
	}

	/**
	 * start a command definition (C) message
	 * 
	 * @param id
	 *            (I=) field
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startCommandDefinition(String id) {
		client.startMessage('C', includeSeqNum, includeNow);
		client.addMessageParamString("I", id);
		return this;
	}

	/**
	 * start a delete (D) message
	 * 
	 * @param type
	 *            - (T=) param
	 * @param id
	 *            - (I=) param, optional
	 * @return this instance
	 * @throws ConcurrentModificationException
	 *             if already in a send for a messages
	 */
	public AmiClient startDeleteMessage(String type, String id) {
		client.startMessage('D', includeSeqNum, includeNow);
		client.addMessageParamString("T", type);
		client.addMessageParamString("I", id);
		return this;
	}
	protected void sendLogout() {
		client.startMessage('X', includeSeqNum, includeNow);
		client.sendMessage();
	}

	/**
	 * send a pause (P message
	 * 
	 * @param delayMs
	 *            number of milliseconds to pause by, (D=) param
	 */
	public void sendPause(int delayMs) {
		client.startMessage('P', includeSeqNum, includeNow);
		client.addMessageParamInt("D", delayMs);
	}

	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with null
	 * @return this instance
	 */
	public AmiClient addMessageParamNull(CharSequence key) {
		client.addMessageParamNull(key);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamString(CharSequence key, CharSequence value) {
		client.addMessageParamString(key, value);
		return this;
	}
	public AmiClient addMessageParamString(CharSequence key, char value) {
		client.addMessageParamString(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamString(CharSequence key, CharSequence value, int start, int end) {
		client.addMessageParamString(key, value, start, end);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param start
	 *            first char to send from value, inclusive
	 * @param end
	 *            last char to send from value, exclusive
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamEnum(CharSequence key, CharSequence value, int start, int end) {
		client.addMessageParamEnum(key, value, start, end);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamJson(CharSequence key, Object value) {
		client.addMessageParamJson(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamEnum(CharSequence key, CharSequence value) {
		client.addMessageParamEnum(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamBinary(CharSequence key, byte[] value) {
		client.addMessageParamBinary(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param start
	 *            first char to send from value, inclusive
	 * @param end
	 *            last char to send from value, exclusive
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamBinary(CharSequence key, byte[] value, int start, int end) {
		client.addMessageParamBinary(key, value, start, end);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamLong(CharSequence key, long value) {
		client.addMessageParamLong(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamInt(CharSequence key, int value) {
		client.addMessageParamInt(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamDouble(CharSequence key, double value) {
		client.addMessageParamDouble(key, value);
		return this;
	}
	public AmiClient addMessageParamDoubleEncoded(CharSequence key, double value) {
		client.addMessageParamDoubleEncoded(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamFloat(CharSequence key, float value) {
		client.addMessageParamFloat(key, value);
		return this;
	}
	public AmiClient addMessageParamFloatEncoded(CharSequence key, float value) {
		client.addMessageParamFloatEncoded(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamBoolean(CharSequence key, boolean value) {
		client.addMessageParamBoolean(key, value);
		return this;
	}
	/**
	 * bypass this api and just send the raw chars on the string, you're responsible for properly escaping, quoting, etc
	 * 
	 * @param text
	 *            raw chars to send
	 * @param start
	 *            first char to send, inclusive
	 * @param end
	 *            last char to seend, exclusive
	 * @return this instance
	 */

	public AmiClient addRawText(CharSequence text, int start, int end) {
		client.addRawText(text, start, end);
		return this;
	}

	/**
	 * finalize and send the currently being built message return false if there was an io issue
	 */
	public boolean sendMessage() {
		return client.sendMessage();
	}

	/**
	 * reset the pending message, following this you need to re-start the message
	 * 
	 */
	public void resetMessage() {
		client.resetMessage();
	}

	/**
	 * Convenience message for quickly sending all the params from the map where key is the param name and object is the value
	 * 
	 * @param params
	 *            values to add to the message. The values' types are considered, for example "123" is sent as a string, 123d is sent as a double and 123 is sent as an int, etc
	 * @return this instance
	 */
	public AmiClient addMessageParams(Map<String, Object> params) {
		client.addMessageParams(params);
		return this;
	}
	/**
	 * Convenience message for sending a boxed value
	 * 
	 * @param key
	 *            key name to associate with value
	 * 
	 * @param value
	 *            values to add to the message. The values' types are considered, for example "123" is sent as a string, 123d is sent as a double and 123 is sent as an int, etc
	 * @return this instance
	 */
	public void addMessageParamObject(String key, Object value) {
		client.addMessageParamObject(key, value);
	}

	/**
	 * @param listener
	 *            add a listener for receiving callbacks on important events about this connection
	 */
	public void addListener(AmiClientListener listener) {
		CH.addIdentityOrThrow(this.clientListeners, listener);
	}
	/**
	 * @param listener
	 *            remove an exisisting listener (added via {@link #addListener(AmiClientListener)}) for receiving callbacks on important events about this connection
	 */
	public boolean removeListener(AmiClientListener listener) {
		return this.clientListeners.remove(listener);
	}

	/**
	 * flush existing messages and wait for a response.
	 * 
	 * @param timeoutMs
	 *            amount of time in milliseconds to wait before throwing an error
	 * @throws TimeoutException
	 */
	public void flushAndWaitForReplys(int timeoutMs) throws TimeoutException {
		this.flush();
		long endTime = this.client.getNow() + timeoutMs;
		if (MH.anyBits(options, ENABLE_QUIET))
			return;
		while (client.getClientMessageSendCount() > client.getClientMessageStatusCount()) {
			long now = System.currentTimeMillis();
			if (now > endTime)
				throw new TimeoutException("still didn't recieve final response");
			OH.sleep(100);
		}

	}

	/**
	 * @return milliseconds to wait before trying to reconnect, when in a disconnected state
	 */
	public long getAutoReconnectFrequencyMs() {
		return autoReconnectFrequencyMs;
	}

	/**
	 * @param autoReconnectFrequencyMs
	 *            milliseconds to wait before trying to reconnect, when in a disconnected state
	 */
	public void setAutoReconnectFrequencyMs(long autoReconnectFrequencyMs) {
		this.autoReconnectFrequencyMs = autoReconnectFrequencyMs;
	}

	/**
	 * send a command (C) declaration
	 * 
	 * @param def
	 *            the command to declare in AMI
	 */
	public void sendCommandDefinition(AmiClientCommandDef def) {
		startCommandDefinition(def.getCommandId());
		addMessageParamStringIfNotNull("N", def.getName());
		addMessageParamStringIfNotNull("A", def.getArgumentsJson());
		addMessageParamStringIfNotNull("W", def.getWhereClause());
		addMessageParamStringIfNotNull("H", def.getHelp());
		addMessageParamStringIfNotNull("E", def.getEnabledExpression());
		addMessageParamStringIfNotNull("F", def.getFields());
		addMessageParamStringIfNotNull("T", def.getFilterClause());
		addMessageParamStringIfNotNull("M", def.getSelectMode());
		addMessageParamStringIfNotNull("S", def.getStyle());
		addMessageParamStringIfNotNull("C", def.getConditions());
		addMessageParamIntegerIfNotNull("L", def.getLevel());
		addMessageParamIntegerIfNotNull("P", def.getPriority());
		sendMessageAndFlush();
	}

	private void addMessageParamStringIfNotNull(String key, String value) {
		if (value != null)
			client.addMessageParamString(key, value);
	}
	private void addMessageParamIntegerIfNotNull(String key, Integer value) {
		if (value != null)
			client.addMessageParamInt(key, value.intValue());
	}
	public boolean getDebugMessages() {
		return this.client.isDebug();
	}

	public void setDebugMessages(boolean debug) {
		this.client.setDebug(debug);
	}

	public boolean waitForLogin(long i) {
		return this.client.waitForLogin(i);
	}

	public CharSequence getOutputBuffer() {
		return this.client.getOutputBuffer();
	}

	public AmiClient startMessage(char type) {
		this.client.startMessage(type, includeSeqNum, includeNow);
		return this;
	}

	/**
	 * add a param to the current message being built, if vlaue is null, skip field
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamLong(CharSequence key, Long value) {
		if (value != null)
			client.addMessageParamLong(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built, if vlaue is null, skip field
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamInt(CharSequence key, Integer value) {
		if (value != null)
			client.addMessageParamInt(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built, if vlaue is null, skip field
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamDouble(CharSequence key, Double value) {
		if (value != null)
			client.addMessageParamDouble(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built, if vlaue is null, skip field
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamFloat(CharSequence key, Float value) {
		if (value != null)
			client.addMessageParamFloat(key, value);
		return this;
	}
	/**
	 * add a param to the current message being built, if vlaue is null, skip field
	 * 
	 * @param key
	 *            key name to associate with value
	 * @return this instance
	 */
	public AmiClient addMessageParamBoolean(CharSequence key, Boolean value) {
		if (value != null)
			client.addMessageParamBoolean(key, value);
		return this;
	}

	/**
	 * 
	 * @param seqnum
	 *            the sequence number to include on the next startMessage(...) with the includeSeqNum set to true.
	 * @return the old seqnum.
	 */
	public long resetSeqNum(long seqnum) {
		return this.client.resetSeqNum(seqnum);
	}
	/**
	 * @return When message are bursting in, Delay in milliseconds between autoflushes. Default is 2 millis. Note a higher number increases throughput at the cost of latency
	 */
	public long getAutoFlushBufferMillis() {
		return this.client.getAutoFlushBufferMillis();
	}

	/**
	 * 
	 * @param autoFlushBufferMillis
	 *            When message are bursting in, Delay in milliseconds between autoflushes. Default is 2 millis. Note a higher number increases throughput at the cost of latency
	 */
	public void setAutoFlushBufferMillis(long autoFlushBufferMillis) {
		this.client.setAutoFlushBufferMillis(autoFlushBufferMillis);
	}

	/**
	 * 
	 * @return Buffer Size in Bytes for outbound buffer (will force flush when buffer is filled). Default is 8192
	 */
	public int getBufferSizeOut() {
		return client.getBufferSizeOut();
	}

	/**
	 * 
	 * @param bufferSizeOut
	 *            Size in Bytes for outbound buffer (will force flush when buffer is filled). Default is 8192
	 */
	public void setBufferSizeOut(int bufferSizeOut) {
		if (this.running)
			throw new IllegalStateException("Already init");
		this.client.setBufferSizeOut(bufferSizeOut);
	}

	/**
	 * 
	 * @return bufferSizeOut Size in Bytes for inbound buffer (number of bytes attempted to be read off the socket in a single call). Default is 8192
	 */
	public int getBufferSizeIn() {
		return client.getBufferSizeIn();
	}

	/**
	 * 
	 * @param bufferSizeIn
	 *            Size in Bytes for inbound buffer (number of bytes attempted to be read off the socket in a single call). Default is 8192
	 */
	public void setBufferSizeIn(int bufferSizeIn) {
		if (this.running)
			throw new IllegalStateException("Already init");
		this.client.setBufferSizeIn(bufferSizeIn);
	}
}
