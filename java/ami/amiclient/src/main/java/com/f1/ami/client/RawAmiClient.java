package com.f1.ami.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.f1.base.Bytes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Password;
import com.f1.base.UUID;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastOutputStreamWriter;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.impl.StringCharReader;

public class RawAmiClient {

	private static final Logger log = LH.get();
	private volatile long autoFlushBufferMillis = 2;
	private final List<RawAmiClientListener> clientListeners = new CopyOnWriteArrayList<RawAmiClientListener>();
	static final public String DEFAULT_HOST = "localhost";
	static final public int DEFAULT_PORT = 3289;
	private Socket socket;
	private AtomicBoolean connected = new AtomicBoolean();
	private long seqnum;
	private FastOutputStreamWriter outStream;
	private InputStream inStream;
	final private StringBuilder inBuffer = new StringBuilder();
	final private AtomicBoolean isInReceive = new AtomicBoolean();
	final private StringBuilder outBuffer = new StringBuilder();
	final private AtomicBoolean isInSend = new AtomicBoolean();
	private volatile int clientMessageStatusCount;
	private volatile int clientMessageSendCount;
	private boolean hasLoggedCouldntConnect = false;
	final private ObjectToJsonConverter jsonParser;
	private final Object autoflushSemephore = new Object();
	private AutoFlusher autoflushThread;
	private int bufferSizeOut = 8192;
	private int bufferSizeIn = 8192;

	public RawAmiClient() {
		this.jsonParser = new ObjectToJsonConverter();
		this.jsonParser.setCompactMode(true);
	}

	public boolean connect(String host, int port, boolean logErrorOnRetries, boolean autoflush) {
		return this.connect(host, port, logErrorOnRetries, null, null, autoflush);
	}
	public boolean connect(String host, int port, boolean logErrorOnRetries, File keystore, Password keystorePassword, boolean autoflush) {
		synchronized (this) {
			if (connected.get())
				throw new IllegalStateException("Already connected");
			try {
				Socket socket;
				if (keystore != null)
					socket = IOH.openSSLClientSocketWithReason(host, port, keystore, Password.valueFrom(keystorePassword), "SSL AMI CLIENT");
				else
					socket = IOH.openClientSocketWithReason(host, port, "AMI CLIENT");
				connect(socket);
				if (autoflush) {
					this.autoflushThread = new AutoFlusher("AmiClientFlusher-" + host + ":" + port);
					this.autoflushThread.start();
				}
				return true;
			} catch (IOException e) {
				disconnect();
				if (!hasLoggedCouldntConnect || logErrorOnRetries) {
					if (e.getCause() != null && SH.is(e.getCause().getMessage()))
						LH.warning(log, e.getMessage(), " ==> ", e.getCause().getMessage());
					else
						LH.warning(log, e.getMessage());
					hasLoggedCouldntConnect = true;
				}

				return false;
			}
		}
	}

	public void connect(Socket socket) throws IOException {
		synchronized (this) {
			IOH.optimize(socket);
			socket.setKeepAlive(true);
			this.socket = socket;
			synchronized (this.autoflushSemephore) {
				if (bufferSizeOut > 0)
					this.outStream = new FastOutputStreamWriter(new FastBufferedOutputStream(socket.getOutputStream(), bufferSizeOut));
				else
					this.outStream = new FastOutputStreamWriter(socket.getOutputStream());
			}
			if (bufferSizeIn > 0)
				this.inStream = new FastBufferedInputStream(socket.getInputStream(), bufferSizeIn);
			else
				this.inStream = socket.getInputStream();
			connected.set(true);
			this.seqnum = 0;
			hasLoggedCouldntConnect = false;
			fireConnect();
		}
	}

	public boolean pumpIncomingEvent() {
		if (!isInReceive.compareAndSet(false, true))
			throw new ConcurrentModificationException("Already in pump for receive");
		SH.clear(inBuffer);
		try {
			while (this.connected.get()) {
				int c = inStream.read();
				switch (c) {
					case -1:
						if (inBuffer.length() != 0)
							LH.warning(log, "Trailing text: ", inBuffer);
						return false;
					case '\n':
						String err = processIncoming(inBuffer);
						if (err != null)
							LH.warning(log, "General error: ", err, " for string '", inBuffer + "'");
						return true;
					case '\r':
						continue;
					default:
						inBuffer.append((char) c);

				}
			}
			if (inBuffer.length() != 0)
				LH.warning(log, "Trailing text: ", inBuffer);
			return false;
		} catch (Exception e) {
			LH.warning(log, e);
			disconnect();
			return false;
		} finally {
			isInReceive.set(false);
		}
	}

	StringBuilder inBuffer2 = new StringBuilder();

	private Map<String, Object> tmpMap = new HashMap<String, Object>();
	private boolean debug;
	private volatile boolean loggedIn;
	private volatile boolean needsFlush;

	private String processIncoming(CharSequence sb) {
		if (debug)
			LH.info(log, "<< ", sb);
		if (sb.length() < 2)
			return "Too short";
		if (sb.charAt(1) != '@')
			return "missing @";
		int pos = SH.indexOf(sb, '|', 2);
		long ts = SH.parseLong(sb, 2, pos, 10);
		pos++;//skip |
		SH.clear(inBuffer2);
		switch (sb.charAt(0)) {
			case 'M': {
				if (sb.charAt(pos++) != 'Q')
					return "Expecting Q";
				if (sb.charAt(pos++) != '=')
					return "Expecting =";
				int pos2 = SH.indexOf(sb, '|', pos);
				long seqNum = SH.parseLong(sb, pos, pos2, 10);
				pos = pos2 + 1;
				if (sb.charAt(pos++) != 'S')
					return "Expecting S";
				if (sb.charAt(pos++) != '=')
					return "Expecting =";
				pos2 = SH.indexOf(sb, '|', pos);
				int status = SH.parseInt(sb, pos, pos2, 10);
				pos = pos2 + 1;
				if (sb.charAt(pos++) != 'M')
					return "Expecting M";
				if (sb.charAt(pos++) != '=')
					return "Expecting =";
				if (sb.charAt(pos++) != '"')
					return "Expecting =";
				pos = SH.unescapeUntil(sb, pos, '\\', '"', inBuffer2);
				if (sb.charAt(pos++) != '"')
					return "Expecting =";
				if (pos != sb.length())
					return "trailing text after message";
				fireStatus(ts, seqNum, status, inBuffer2);
				break;
			}
			case 'E': {
				tmpMap.clear();
				parseIncomingParams(sb, pos, tmpMap);
				String requestId = (String) tmpMap.remove("I");
				String userName = (String) tmpMap.remove("U");
				String cmd = (String) tmpMap.remove("C");
				String type = (String) tmpMap.remove("T");
				String objectId = (String) tmpMap.remove("O");
				fireCommand(requestId, cmd, userName, type, objectId, (Map) tmpMap);
				tmpMap.clear();
			}
		}
		return null;
	}

	private void parseIncomingParams(CharSequence sb, int pos, Map<String, Object> tmpMap) {
		StringBuilder tmp = new StringBuilder();
		StringCharReader scr = new StringCharReader(sb, pos, sb.length() - pos);
		if (!scr.isEof()) {
			for (;;) {
				scr.readUntil('=', tmp);
				scr.expect('=');
				String key = SH.toStringAndClear(tmp);
				Object val;
				switch (scr.peak()) {
					case 't':
						scr.expectSequence("true");
						val = Boolean.TRUE;
						break;
					case 'f':
						scr.expectSequence("false");
						val = Boolean.FALSE;
						break;
					case 'n':
						scr.expectSequence("null");
						val = null;
						break;
					case '"':
						scr.expect('"');
						readUntilSkipEscaped(scr, '"', tmp);
						switch (scr.peakOrEof()) {
							case 'J': {
								scr.expect('J');
								val = jsonParser.stringToObject(tmp);
								break;
							}
							case 'U': {
								scr.expect('U');
								val = EncoderUtils.decode64(tmp);
								break;
							}
							default: {
								val = SH.toStringAndClear(tmp);
							}

						}

						break;
					case '\'':
						scr.expect('\'');
						readUntilSkipEscaped(scr, '\'', tmp);
						val = SH.toStringAndClear(tmp);
						break;
					default:
						scr.readUntil('|', tmp);
						if (SH.indexOf(tmp, '.', 0) != -1) {
							if (SH.endsWith(tmp, 'D'))
								val = (SH.parseDouble(tmp, 0, tmp.length() - 1));
							else
								val = (SH.parseFloat(tmp));
						} else if (SH.endsWith(tmp, 'L'))
							val = (SH.parseLong(tmp, 0, tmp.length() - 1, 10));
						else
							val = (SH.parseInt(tmp, 10));
						SH.clear(tmp);
						break;
				}
				if (val != null)
					tmpMap.put(key, val);
				if (scr.isEof())
					break;
				scr.expect('|');
			}
		}
	}
	private void readUntilSkipEscaped(StringCharReader scr, char end, StringBuilder tmp) {
		for (;;) {
			if (scr.isEof())
				throw scr.newExpressionParserException("Missing closing " + end);
			char c = scr.readChar();
			if (c == end) {
				return;
			} else if (c == '\\') {
				if (scr.isEof())
					scr.newExpressionParserException("dangling escape (\\) ");
				c = scr.readChar();
				switch (c) {
					case 'r':
						tmp.append('\r');
						continue;
					case 'n':
						tmp.append('\n');
						continue;
					case 't':
						tmp.append('\t');
						continue;
					case 'f':
						tmp.append('\f');
						continue;
					case 'b':
						tmp.append('\b');
						continue;
					case '\\':
						tmp.append('\\');
						continue;
					case '\'':
						tmp.append('\'');
						continue;
					case '\"':
						tmp.append('\"');
						continue;
					case 'u':
						if (scr.getAvailable() < 4)
							throw scr.newExpressionParserException("invalid unicode format, expecting \\u####) ");
						final int n = (toHex((byte) scr.readChar()) << 12) + (toHex((byte) scr.readChar()) << 8) + (toHex((byte) scr.readChar()) << 4) + ((byte) scr.readChar());
						if (n >= 65536)
							throw scr.newExpressionParserException("invalid unicode format, expecting \\u####) ");
						tmp.append((char) n);
						break;
				}
			} else
				tmp.append(c);
		}
	}
	private static int toHex(byte b) {
		if (OH.isBetween(b, '0', '9'))
			return b - '0';
		else if (OH.isBetween(b, 'A', 'F'))
			return b - 'A' + 10;
		else if (OH.isBetween(b, 'a', 'f'))
			return b - 'a' + 10;
		return 65536;
	}

	private void fireStatus(long ts, long seqNum, int status, CharSequence text) {
		clientMessageStatusCount++;
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				this.clientListeners.get(i).onMessageReceived(this, ts, seqNum, status, text);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
	}
	private void fireCommand(String requestId, String cmd, String userName, String type, String objectId, Map<String, Object> params) {
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				clientListeners.get(i).onCommand(this, requestId, cmd, userName, type, objectId, params);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
	}
	private void fireMessageSent(CharSequence msg) {
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				clientListeners.get(i).onMessageSent(this, msg);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
	}
	private void fireDisconnect() {
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				clientListeners.get(i).onDisconnect(this);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
	}
	private void fireConnect() {
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				clientListeners.get(i).onConnect(this);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
	}
	public void fireOnLogin() {
		for (int i = 0; i < this.clientListeners.size(); i++)
			try {
				clientListeners.get(i).onLoggedIn(this);
			} catch (Throwable e) {
				LH.warning(log, "Listener threw exception: ", e);
			}
		synchronized (this) {
			this.loggedIn = true;
			this.notifyAll();
		}
	}
	public long resetSeqNum(long seqnum) {
		long r = this.seqnum;
		this.seqnum = seqnum;
		return r;
	}
	public RawAmiClient startMessage(char type, boolean includeSeqNum, boolean includeNow) {
		assertConnected();
		if (!isInSend.compareAndSet(false, true))
			throw new ConcurrentModificationException("Already in object send");
		SH.clear(outBuffer);
		outBuffer.append(type);
		if (includeSeqNum)
			outBuffer.append('#').append(seqnum++);
		if (includeNow)
			outBuffer.append('@').append(getNow());
		return this;
	}

	public RawAmiClient addMessageParamNull(CharSequence key) {
		assertInMessage();
		outBuffer.append('|').append(key).append("=null");
		return this;
	}

	public RawAmiClient addMessageParamString(CharSequence key, char value) {
		assertInMessage();
		outBuffer.append('|').append(key);
		if (value == '"')
			outBuffer.append("=\"\\\"\"");
		else
			outBuffer.append("=\"").append(value).append('\"');
		return this;
	}
	public RawAmiClient addMessageParamString(CharSequence key, CharSequence value) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("=\"");
		escape(value, 0, value.length(), '"', outBuffer).append('\"');
		return this;
	}

	public RawAmiClient addMessageParamString(CharSequence key, CharSequence value, int start, int end) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("=\"");
		escape(value, start, end, '"', outBuffer).append('\"');
		return this;
	}
	public RawAmiClient addMessageParamEnum(CharSequence key, CharSequence value, int start, int end) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("=\'");
		escape(value, start, end, '\'', outBuffer).append('\'');
		return this;
	}
	public RawAmiClient addMessageParamJson(CharSequence key, Object value) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("=\"");
		String strValue = jsonParser.objectToString(value);
		escape(strValue, 0, strValue.length(), '"', outBuffer).append("\"J");
		return this;
	}
	public RawAmiClient addMessageParamBinary(CharSequence key, byte[] value) {
		if (value == null)
			return addMessageParamNull(key);
		else
			return addMessageParamBinary(key, value, 0, value.length);
	}
	public RawAmiClient addMessageParamBinary(CharSequence key, byte[] value, int start, int end) {
		assertInMessage();
		outBuffer.append('|').append(key).append("=\"");
		EncoderUtils.encode64(value, start, end, true, outBuffer);
		outBuffer.append("\"U");
		return this;
	}

	public RawAmiClient addMessageParamEnum(CharSequence key, CharSequence value) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("='");
		escape(value, 0, value.length(), '\'', outBuffer).append('\'');
		return this;
	}
	public RawAmiClient addMessageParamEnum(CharSequence key, char[] value) {
		if (value == null)
			return addMessageParamNull(key);
		assertInMessage();
		outBuffer.append('|').append(key).append("='");
		SH.escape(value, '\'', '\\', outBuffer).append('\'');
		return this;
	}

	public RawAmiClient addMessageParamLong(CharSequence key, long value) {
		assertInMessage();
		outBuffer.append('|').append(key).append('=').append(value).append('L');
		return this;
	}

	public RawAmiClient addMessageParamInt(CharSequence key, int value) {
		assertInMessage();
		outBuffer.append('|').append(key).append('=').append(value);
		return this;
	}

	public RawAmiClient addMessageParamDouble(CharSequence key, double value) {
		assertInMessage();
		if (MH.isNumber(value))
			outBuffer.append('|').append(key).append('=').append(value).append('D');
		return this;
	}

	public RawAmiClient addMessageParamFloat(CharSequence key, float value) {
		assertInMessage();
		if (MH.isNumber(value))
			outBuffer.append('|').append(key).append('=').append(value);
		return this;
	}

	public RawAmiClient addMessageParamDoubleEncoded(CharSequence key, double value) {
		assertInMessage();
		if (MH.isNumber(value)) {
			outBuffer.append('|').append(key).append("=D");
			EncoderUtils.encodeLong64(Double.doubleToRawLongBits(value), outBuffer);
		}

		return this;
	}

	public RawAmiClient addMessageParamFloatEncoded(CharSequence key, float value) {
		assertInMessage();
		if (MH.isNumber(value)) {
			outBuffer.append('|').append(key).append("=F");
			EncoderUtils.encodeInt64(Float.floatToRawIntBits(value), outBuffer);
		}
		return this;
	}

	public RawAmiClient addMessageParamBoolean(CharSequence key, boolean value) {
		assertInMessage();
		outBuffer.append('|').append(key).append('=').append(value);
		return this;
	}

	public RawAmiClient addRawText(CharSequence data, int start, int end) {
		assertInMessage();
		if (end == start)
			return this;
		outBuffer.append(data, start, end);
		return this;
	}

	public void sendMessage(CharSequence rawMessage) {
		sendMessage(rawMessage, false);
	}
	public void sendMessageAndFlush(CharSequence rawMessage) {
		sendMessage(rawMessage, true);
	}
	public void sendMessage(CharSequence rawMessage, boolean andFlush) {
		assertConnected();
		if (!isInSend.compareAndSet(false, true))
			throw new ConcurrentModificationException("Already in object send");
		SH.clear(outBuffer);
		outBuffer.append(rawMessage);
		if (andFlush)
			sendMessageAndFlush();
		else
			sendMessage();
	}
	public void resetMessage() {
		assertConnected();
		isInSend.set(false);
		SH.clear(outBuffer);
	}
	public boolean sendMessage() {
		assertConnected();
		if (!isInSend.compareAndSet(true, false))
			throw new ConcurrentModificationException("not in object send");
		outBuffer.append(SH.CHAR_NEWLINE);
		try {
			if (this.autoflushThread == null) {
				outStream.write(outBuffer, 0, outBuffer.length());
				this.needsFlush = true;
			} else {
				synchronized (this.autoflushSemephore) {
					outStream.write(outBuffer, 0, outBuffer.length());
					this.needsFlush = true;
					this.autoflushSemephore.notify();
				}
			}

			if (debug)
				LH.info(log, ">> ", outBuffer.toString());
			fireMessageSent(outBuffer);
			clientMessageSendCount++;
			return true;
		} catch (IOException e) {
			LH.info(log, e);
			disconnect();
			return false;
		} finally {
			isInSend.set(false);
		}
	}
	private void assertConnected() {
		if (!connected.get())
			throw new IllegalStateException("not connected");
	}
	private void assertInMessage() {
		if (!isInSend.get())
			throw new IllegalStateException("not in object send, call startMessage(...) first");
	}

	public void disconnect() {
		synchronized (this) {
			this.loggedIn = false;
			if (connected.compareAndSet(true, false)) {
				synchronized (this.autoflushSemephore) {
					if (this.outStream != null)
						try {
							this.outStream.flush();
						} catch (Exception e) {
							LH.fine(log, e);
						}
					needsFlush = false;
					this.outStream = null;
					if (this.autoflushThread != null) {
						this.autoflushThread.close();
						this.autoflushThread = null;
					}
				}
				IOH.close(this.socket);
				this.inStream = null;
				this.seqnum = 0;
				this.isInReceive.set(false);
				this.isInSend.set(false);
				this.clientMessageSendCount = 0;
				this.clientMessageStatusCount = 0;
				this.inBuffer.setLength(0);
				this.outBuffer.setLength(0);
				this.hasLoggedCouldntConnect = false;
				fireDisconnect();
			}
		}
	}

	public boolean flush() {
		if (!needsFlush)
			return true;
		assertConnected();
		try {
			if (this.autoflushThread == null) {
				this.outStream.flush();
				needsFlush = false;
			} else {
				synchronized (this.autoflushSemephore) {
					this.outStream.flush();
					needsFlush = false;
				}
			}

			return true;
		} catch (Exception e) {
			LH.info(log, "Error on flush, disconnecting amiclient. ", e);
			disconnect();
			return false;
		}
	}

	public void addListener(RawAmiClientListener listener) {
		CH.addIdentityOrThrow(this.clientListeners, listener);
	}
	public boolean removeListener(RawAmiClientListener listener) {
		return this.clientListeners.remove(listener);
	}

	public int getClientMessageStatusCount() {
		return clientMessageStatusCount;
	}
	public int getClientMessageSendCount() {
		return clientMessageSendCount;
	}

	public long getNow() {
		return EH.currentTimeMillis();
	}

	public void addMessageParams(Map<String, Object> params) {
		for (Map.Entry<String, Object> e : params.entrySet()) {
			String key = e.getKey();
			Object value = e.getValue();
			addMessageParamObject(key, value);
		}
	}

	public void addMessageParamObject(String key, Object value) {
		if (value == null)
			addMessageParamNull(key);
		else if (value instanceof CharSequence)
			addMessageParamString(key, (CharSequence) value);
		else if (value instanceof Integer)
			addMessageParamInt(key, ((Integer) value).intValue());
		else if (value instanceof Long)
			addMessageParamLong(key, ((Long) value).longValue());
		else if (value instanceof Float)
			addMessageParamFloat(key, ((Float) value).floatValue());
		else if (value instanceof Double)
			addMessageParamDouble(key, ((Double) value).doubleValue());
		else if (value instanceof Byte)
			addMessageParamInt(key, ((Byte) value).intValue());
		else if (value instanceof Short)
			addMessageParamInt(key, ((Short) value).intValue());
		else if (value instanceof Boolean)
			addMessageParamBoolean(key, ((Boolean) value));
		else if (value instanceof Character)
			addMessageParamEnum(key, SH.toString((Character) value));
		else if (value instanceof char[])
			addMessageParamEnum(key, (char[]) value);
		else if (value instanceof BigDecimal)
			addMessageParamDouble(key, ((BigDecimal) value).doubleValue());
		else if (value instanceof BigInteger)
			addMessageParamDouble(key, ((BigInteger) value).longValue());
		else if (value instanceof Date)
			addMessageParamLong(key, ((Date) value).getTime());
		else if (value instanceof DateMillis)
			addMessageParamLong(key, ((DateMillis) value).getDate());
		else if (value instanceof DateNanos)
			addMessageParamLong(key, ((DateNanos) value).getNanos());
		else if (value instanceof Complex)
			addMessageParamString(key, ((Complex) value).toString());
		else if (value instanceof Bytes)
			addMessageParamBinary(key, ((Bytes) value).getBytes());
		else if (value instanceof byte[])
			addMessageParamBinary(key, (byte[]) value);
		else if (value instanceof UUID)
			addMessageParamString(key, ((UUID) value).toString());
		else if (value instanceof DateMillis)
			addMessageParamLong(key, ((DateMillis) value).getDate());
		else if (value instanceof DateNanos)
			addMessageParamLong(key, ((DateNanos) value).getNanos());
		else if (value instanceof Complex)
			addMessageParamString(key, ((Complex) value).toString());
		else
			throw new IllegalArgumentException("Unknown type " + value.getClass().getName() + " for key value pair: " + key + "==>" + value);
	}
	public boolean getIsConnected() {
		return this.connected.get();
	}

	public boolean sendMessageAndFlush() {
		return sendMessage() && flush();
	}

	public ObjectToJsonConverter getJsonConverter() {
		return this.jsonParser;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean waitForLogin(long i) {
		synchronized (this) {
			if (this.loggedIn)
				return true;
			try {
				this.wait(i);
			} catch (InterruptedException e) {
				return false;
			}
		}
		return true;
	}
	private StringBuilder escape(CharSequence value, int start, int end, char quote, StringBuilder buf) {
		while (start < end)
			escapeSpecial(quote, buf, value.charAt(start++));
		return buf;
	}

	private StringBuilder escape(char value[], int start, int end, char quote, StringBuilder buf) {
		while (start < end)
			escapeSpecial(quote, buf, value[start++]);
		return buf;
	}
	private void escapeSpecial(char quote, StringBuilder buf, char c) {
		switch (c) {
			case '\\':
				buf.append("\\\\");
				break;
			case '\'':
			case '\"':
				if (c == quote)
					buf.append('\\');
				buf.append(c);
				break;
			case '\n':
				buf.append("\\n");
				break;
			default:
				buf.append(c);
				break;
		}
	}

	public StringBuilder getOutputBuffer() {
		return this.outBuffer;
	}

	public long getAutoFlushBufferMillis() {
		return autoFlushBufferMillis;
	}

	public void setAutoFlushBufferMillis(long autoflushBufferMillis) {
		this.autoFlushBufferMillis = autoflushBufferMillis;
	}

	public int getBufferSizeOut() {
		return bufferSizeOut;
	}

	public void setBufferSizeOut(int bufferSizeOut) {
		this.bufferSizeOut = bufferSizeOut;
	}

	public int getBufferSizeIn() {
		return bufferSizeIn;
	}

	public void setBufferSizeIn(int bufferSizeIn) {
		this.bufferSizeIn = bufferSizeIn;
	}

	private class AutoFlusher extends Thread {

		private boolean isClosed = false;

		public AutoFlusher(String string) {
			super(string);
		}

		@Override
		public void run() {
			while (!isClosed) {
				synchronized (autoflushSemephore) {
					try {
						if (!needsFlush)
							RawAmiClient.this.autoflushSemephore.wait();
						if (RawAmiClient.this.outStream != null)
							flush();
					} catch (Exception e) {
						LH.fine(log, e);
					}
				}
				OH.sleep(RawAmiClient.this.autoFlushBufferMillis);
			}

		}

		public void close() {
			this.interrupt();
			synchronized (autoflushSemephore) {
				this.isClosed = true;
				RawAmiClient.this.autoflushSemephore.notify();
			}
		}

	}

}
