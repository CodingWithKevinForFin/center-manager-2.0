package com.f1.http.impl;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.SecureRandom;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLException;

import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.HttpSession;
import com.f1.http.HttpSessionManager;
import com.f1.http.HttpUtils;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.FastBufferedOutputStream;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.FastPrintStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.NullOutputStream;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TeeOutputStream;
import com.f1.utils.TextMatcher;
import com.f1.utils.concurrent.CountingInputStream;
import com.f1.utils.concurrent.CountingOutputStream;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.TeeInputStream;
import com.f1.utils.impl.CaseInsensitiveHasher;

public class BasicHttpRequestResponse implements Runnable, HttpRequestResponse {

	private static final AtomicLong statsConnectionCounts = new AtomicLong(0);
	private final SimpleDateFormat dateFormatter;
	private final SimpleDateFormat dateFormatter2;
	private final SimpleDateFormat dateFormatter3;
	private static final Logger log = Logger.getLogger(BasicHttpRequestResponse.class.getName());

	//DON'T MODIFY!
	public static final byte[] BYTES_HTTP_403_FORBIDDEN = "HTTP/1.1 403 Forbidden\r\n".getBytes();
	public static final byte[] BYTES_HTTP_404_NOT_FOUND = "HTTP/1.1 404 Not Found\r\n".getBytes();
	public static final byte[] BYTES_HTTP_401_UNAUTHORIZED = "HTTP/1.1 401 Unauthorized\r\n".getBytes();
	public static final byte[] BYTES_HTTP_400_BAD_REQUEST = "HTTP/1.1 400 Bad Request\r\n".getBytes();
	public static final byte[] BYTES_HTTP_200_OK = "HTTP/1.1 200 OK\r\n".getBytes();
	public static final byte[] BYTES_HTTP_204_NO_CONTENT = "HTTP/1.1 204 No Content\r\n".getBytes();
	public static final byte[] BYTES_HTTP_500_SERVICE_ERROR = "HTTP/1.1 500 Service Error\r\n".getBytes();
	public static final byte[] BYTES_HTTP_304_NOT_MODIFIED = "HTTP/1.1 304 Not Modified\r\n".getBytes();
	public static final byte[] BYTES_HTTP_302_FOUND = "HTTP/1.1 302 Found\r\n".getBytes();
	public static final byte[] BYTES_HTTP_503_SERVICE_UNAVAILABLE = "HTTP/1.1 503 Service Unavailable\r\n".getBytes();
	public static final byte[] BYTES_HTTP_206_PARTIAL_CONTENT = "HTTP/1.1 206 Partial Content\r\n".getBytes();

	private static final byte[] CONNECTION_KEEP_ALIVE = "Connection: Keep-Alive\r\n".getBytes();
	private static final byte[] SET_COOKIE = "Set-Cookie: ".getBytes();
	private static final byte[] NEW_LINE = "\r\n".getBytes();
	private static final byte[] LAST_MODIFIED = "Last-Modified: ".getBytes();
	private static final byte[] CACHE_CONTROL = "Cache-Control: ".getBytes();
	private static final byte[] LOCATION = "Location: ".getBytes();
	private static final byte[] COLLON = ": ".getBytes();
	private static final byte[] CONTENT_LENGTH = "Content-Length: ".getBytes();
	private static final byte[] DATE = "Date: ".getBytes();
	private static final byte[] SERVER = "Server: 3forge Ultrafast HTTP Server\r\n".getBytes();
	private static final byte[] CONTENT_TYPE = "Content-Type: ".getBytes();
	private static final byte[] TEXT_HTML_UTF8 = "text/html;charset=utf-8".getBytes();
	private static final byte[] TEXT_HTML_ISO_8859_1 = "text/html;charset=ISO-8859-1".getBytes();
	private final FastByteArrayOutputStream byteOutputStream = new FastByteArrayOutputStream();
	private final FastPrintStream printOutputStream;
	private InputStream in;
	private OutputStream out;
	private FastBufferedInputStream bufIn = new FastBufferedInputStream(null);
	private FastBufferedOutputStream bufOut = new FastBufferedOutputStream(null);
	private boolean closed = false;
	private StringBuilder sb = new StringBuilder();
	private StringBuilder tmpBuf = new StringBuilder();
	private Map<String, String> header = new HasherMap<String, String>(CaseInsensitiveHasher.INSTANCE);
	private Map<String, String> responseHeader = new HashMap<String, String>();
	private Map<String, String> params = new LinkedHashMap<String, String>();
	private Map<String, String> uparams = Collections.unmodifiableMap(params);
	private Map<String, List<Object>> extendedParams = new HashMap<String, List<Object>>();
	private Map<String, String> uheader = Collections.unmodifiableMap(header);
	private Map<String, String> cookies = new HashMap<String, String>();
	private Map<String, String> ucookies = Collections.unmodifiableMap(cookies);
	private BasicHttpServer server;
	private boolean paramsParsed;
	private String method;
	private String path;
	private Date now = new Date(0);
	private byte[] postContentData = new byte[1024];
	private byte[] contentType;
	private int qmark;
	private String query;
	private String uri;
	private ParsePosition parsePosition = new ParsePosition(0);
	private boolean shouldClose;
	private Date modifiedSince;
	private byte[] responseType;
	private String redirect;
	private long lastModified;
	private boolean debugging;

	private TeeOutputStream debugOut;
	private FastByteArrayOutputStream debugOutBuffer;
	private TeeInputStream debugIn;
	private FastByteArrayOutputStream debugInBuffer;
	private String description;
	private Map<String, String> responseCookies = new HashMap<String, String>();
	private Map<String, Object> attributes = new HashMap<String, Object>();
	private HttpSession session;
	private HttpSessionManager sessionManager;
	private boolean isSecure;
	private int port;
	private String host;
	private String requestUrl;
	private int remotePort = -1;
	private String remoteHost;
	volatile private boolean asyncMode = false;
	volatile private boolean needsToRespond;
	private boolean option_ReturnErrorCodeInBody = true;
	private int postContentLength;
	private boolean captureStats = false;
	private long statsConnectionCount;
	private int statsRequestCount = 0;
	private boolean isMultiPart = false;
	private int maxDebugLength;
	private String cacheControl;
	private long requestTime;
	private int httpResponseWarnMs = 5000;

	public BasicHttpRequestResponse() {
		try {
			printOutputStream = new FastPrintStream(byteOutputStream, false, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw OH.toRuntime(e);
		}
		dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy  HH:mm:ss zzz");
		dateFormatter2 = new SimpleDateFormat("EEEE, dd-MMM-yy  HH:mm:ss zzz");
		dateFormatter3 = new SimpleDateFormat("EEE dd MMM yyyy  HH:mm:ss");
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormatter2.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateFormatter3.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private Date parseDate(String s) {
		if (s == null)
			return null;
		parsePosition.setIndex(0);
		parsePosition.setErrorIndex(0);
		Date r;
		if (s.indexOf(',') == -1)
			r = dateFormatter3.parse(s, parsePosition);
		else if (s.indexOf('-') == -1)
			r = dateFormatter.parse(s, parsePosition);
		else
			r = dateFormatter2.parse(s, parsePosition);
		return r;
	}

	public void reset(InputStream in, OutputStream out, String description, BasicHttpServer server, boolean isSecure, int port, String remoteHost, int remotePort)
			throws IOException {
		if (closed)
			throw new IllegalStateException();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.in = in;
		this.out = out;
		this.description = description;
		this.isSecure = isSecure;
		this.server = server;
		this.port = port;
		if (captureStats) {
			this.in = in = new CountingInputStream(this.in);
			this.out = out = new CountingOutputStream(this.out);
			this.statsConnectionCount = statsConnectionCounts.incrementAndGet();
			logStats("Opened");
		}
		if (debugging) {
			LH.info(log, "Resetting http connection, in debug mode: ", description);
			this.debugInBuffer = new FastByteArrayOutputStream();
			this.debugOutBuffer = new FastByteArrayOutputStream();
			this.debugIn = new TeeInputStream(in, debugInBuffer);
			this.debugOut = new TeeOutputStream(out, debugOutBuffer);
			bufIn.reset(debugIn);
			bufOut.reset(debugOut);
		} else {
			this.debugIn = null;
			this.debugOut = null;
			this.debugInBuffer = null;
			this.debugOutBuffer = null;
			bufIn.reset(in);
			bufOut.reset(out);
		}
		closed = false;
		session = null;
		if (server != null)
			this.sessionManager = server.getHttpSessionManager();
		reset();
	}

	private void logStats(String string) {
		LH.info(log, "Http Connection #", this.statsConnectionCount, " ", string, " REQ/I/O: ", this.statsRequestCount, "/", ((CountingInputStream) in).getCount(), "/",
				((CountingOutputStream) out).getCount());

	}

	public void reset() {
		if (captureStats)
			logStats("Request");
		if (debugging) {

			if (debugOutBuffer.size() == 0) {
				LH.info(log, "Quick Reset: ", description);
			} else {
				String reqText = debugUpto(debugInBuffer.getBuffer(), debugInBuffer.size(), this.maxDebugLength);
				String resText = debugOutBuffer.toString();
				this.server.debug(this, description, reqText, resText);
			}
			debugInBuffer.reset();
			debugOutBuffer.reset();
		}
		isMultiPart = false;
		postContentLength = 0;
		byteOutputStream.reset();
		requestUrl = null;
		paramsParsed = false;
		if (!cookies.isEmpty())
			cookies.clear();
		if (!params.isEmpty())
			params.clear();
		if (!responseCookies.isEmpty())
			responseCookies.clear();
		if (!header.isEmpty())
			header.clear();
		if (!responseHeader.isEmpty())
			responseHeader.clear();
		if (!attributes.isEmpty())
			attributes.clear();
		if (!extendedParams.isEmpty())
			extendedParams.clear();
		contentType = TEXT_HTML_UTF8;
		this.cacheControl = this.server.getDefaultCacheControl();
		modifiedSince = null;
		redirect = null;
		asyncMode = false;
		needsToRespond = false;
		lastModified = -1;
	}

	static private String debugUpto(byte[] bytes, int bytesLength, int maxDebugLength) {
		int binaryCnt = 0;
		int l = Math.min(maxDebugLength, bytesLength);
		for (int i = 0; i < l; i++) {
			if (!SH.isAscii(bytes[i]))
				binaryCnt++;
		}
		if (bytesLength == 0 || (double) binaryCnt / l < .1) {
			if (bytesLength > maxDebugLength)
				return "ASCII:" + SH.NEWLINE + new String(bytes, 0, maxDebugLength) + SH.NEWLINE + "<suppressing remaining " + (bytesLength - maxDebugLength) + " bytes of data>";
			else
				return "ASCII:" + SH.NEWLINE + new String(bytes, 0, bytesLength);
		}
		StringBuilder sb = new StringBuilder("BINARY:").append(SH.NEWLINE);
		for (int j = 0; j < bytesLength; j++) {
			if (sb.length() > bytesLength)
				return sb.append(" <suppressing remaining ").append(bytesLength - j).append(" bytes of data>").toString();
			if (j > 0) {
				if (j % 128 == 0)
					sb.append(SH.NEWLINE);
				else
					sb.append(' ');
			}
			byte b = bytes[j];
			if (b >= 0x20 && b < 0x7f)
				sb.append((char) b).append(" ");
			else {
				switch (b) {
					case '\n':
						sb.append("\\n");
						break;
					case '\r':
						sb.append("\\r");
						break;
					case '\t':
						sb.append("\\t");
						break;
					default:
						if ((b & 0xff) < 16)
							sb.append('0');
						SH.toString(b & 0xff, 16, sb);
				}
			}
		}
		return sb.toString();
	}
	@Override
	public void run() {
		if (needsToRespond) {//need to handle an async response first
			if (log.isLoggable(Level.FINE))
				LH.fine(log, logMe(), " Executing async response: ", description);
			try {
				sendResponse();
			} catch (SocketException e) {
				LH.info(log, logMe(), " Socket disconnected from '", getRemoteHost(), "': ", logException(e));
			} catch (Exception e) {
				LH.info(log, logMe(), " For ", description, ": ", ">>>> async response failed: ", e);
				close();
				reset();
				return;
			}
		} else
			shouldClose = false;
		if (closed) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, logMe(), " Connection already closed: ", description);
			return;
		}
		while (!shouldClose) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, logMe(), " Entering process loop for connection: ", description);
			try {
				reset();
				if (!readUntil((byte) ' ', SH.clear(sb)))
					break;
				this.requestTime = System.currentTimeMillis();
				if (SH.equals(GET, sb))
					method = GET;
				else if (SH.equals(POST, sb))
					method = POST;
				else if (SH.equals(HEAD, sb))
					method = HEAD;
				else if (SH.equals(OPTIONS, sb))
					method = OPTIONS;
				else {
					method = null;
					throw new RuntimeException("unknown method: " + sb);
				}
				readUntil((byte) ' ', SH.clear(sb));
				if (path == null || !SH.equals(path, sb))
					parseUrl(sb.toString());
				readUntil((byte) '\n', SH.clear(sb));// ignore protocol for now
				while (readUntil((byte) ':', SH.clear(sb))) {
					String key = pooled(sb);
					readUntil((byte) '\n', SH.clear(sb));
					String value = pooled(sb);
					header.put(key, value);
				}
				processHeader();
				// TODO: handle session timeout scenario
				if (session != null && OH.ne(session.getSessionId(), sessionManager.getSessionId(this)))
					session = null;
				responseType = BYTES_HTTP_200_OK;
				server.service(this);
				if (getResponseAsyncMode()) {
					if (log.isLoggable(Level.FINE))
						LH.fine(log, logMe(), " Leaving process loop because response will be asynch: ", description);
					return;
				} else {
					sendResponse();
				}
			} catch (SSLException e) {
				LH.info(log, logMe(), " SSL exception from '", getRemoteHost(), "': ", logException(e));
				break;
			} catch (SocketTimeoutException e) {
				LH.fine(log, logMe(), " Socket timed out from '", getRemoteHost(), "': ", logException(e));
				break;
			} catch (SocketException e) {
				LH.info(log, logMe(), " Socket disconnected from '", getRemoteHost(), "': ", logException(e));
				break;
			} catch (FileNotFoundException e) {
				try {
					LH.info(log, "file not found(404) for url ", path, e);
					responseType = BYTES_HTTP_404_NOT_FOUND;
					onError(e);
					sendResponse();
				} catch (Exception e2) {
					break;
				}
			} catch (Exception e) {
				if (e instanceof EOFException) {
					LH.info(log, logMe(), " EOF from '", getRemoteHost(), "': ", logException(e));
					break;
				}
				Throwable cause = e.getCause();
				if (cause instanceof EOFException) {
					LH.info(log, logMe(), " EOF from '", getRemoteHost(), "': ", logException(e), " caused-by ", cause.getMessage());
					break;
				}
				LH.warning(log, "error on connection", e);
				try {
					responseType = BYTES_HTTP_500_SERVICE_ERROR;
					onError(e);
					sendResponse();
				} catch (Exception e2) {
					LH.warning(log, logMe(), " error handling error", e2);
					LH.warning(log, logMe(), " causing error", e);
					break;
				}
			}
		}
		close();
		reset();
	}
	private Object logException(Exception e) {
		return e == null ? null : (isDebugging() ? e : e.getMessage());
	}

	@Override
	public void setKeepAlive(boolean keepAlive) {
		this.shouldClose = keepAlive;
	}

	private void onError(Exception e) {
		try {
			if (e != null)
				getAttributes().put("exception", e);
			server.onError(this);
		} catch (Exception e2) {
			if (e != null) {
				LH.warning(log, logMe(), " Error processing original error(see original exception below) ", e2);
				LH.warning(log, logMe(), " Original exception: ", e);
			} else
				LH.warning(log, logMe(), " Error processing original error", e2);
		}

	}

	private void sendResponse() throws IOException {
		if (captureStats)
			this.statsRequestCount++;
		if (redirect != null) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, logMe(), " Redirecting to: ", redirect, " for: ", description);
			responseType = BYTES_HTTP_302_FOUND;
			writeHeader(0);

			bufOut.write(LOCATION);
			write(redirect);
			bufOut.write(NEW_LINE);

			bufOut.write(NEW_LINE);
			bufOut.flush();
		} else {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, logMe(), " Responding for ", description);

			if (option_ReturnErrorCodeInBody && byteOutputStream.size() == 0 && this.responseType != BYTES_HTTP_200_OK && !Arrays.equals(this.responseType, BYTES_HTTP_200_OK))
				byteOutputStream.write(responseType);

			writeHeader(byteOutputStream.size());
			bufOut.write(NEW_LINE);
			if (debugging && byteOutputStream.size() > 0) {
				bufOut.flush();
				OutputStream tmp = debugOut.setTee(NullOutputStream.INSTANCE);
				try {
					bufOut.write(byteOutputStream.getBuffer(), 0, byteOutputStream.size());
					bufOut.flush();
				} finally {
					int len = byteOutputStream.size();
					byte[] buf = byteOutputStream.getBuffer();
					String t = debugUpto(buf, len, maxDebugLength);
					tmp.write(t.getBytes());
					debugOut.setTee(tmp);
				}
			} else {
				bufOut.write(byteOutputStream.getBuffer(), 0, byteOutputStream.size());
			}
			bufOut.flush();
		}
		long responseTime = System.currentTimeMillis() - this.requestTime;
		if (responseTime >= httpResponseWarnMs) {
			if (this.httpResponseWarnLogRequestSize > 0) {
				String ddd = SH.ddd(getRequestUrl(), this.httpResponseWarnLogRequestSize);
				if (this.httpResponseWarnLogRequestSuppress != null && this.httpResponseWarnLogRequestSuppress.matches(ddd))
					ddd = "<redacted>";
				LH.warning(log, logMe(), " Slow Response at ", path, ": ", responseTime, " ms. Original Request: ", ddd);
			} else {
				LH.warning(log, logMe(), " Slow Response at ", path, ": ", responseTime, " ms");
			}
		}
	}
	private String logMe() {
		if (session == null)
			return "<no-session>";
		Object id = session.getSessionId();
		if (id == null)
			return "<no-sessionid>";
		String description = session.getDescription();
		return description == null ? id.toString() : id + "(" + description + ")";
	}

	private void processHeader() throws IOException {
		if (method == POST) {
			String contentLength = header.get("Content-Length");
			try {
				postContentLength = Integer.parseInt(contentLength);
			} catch (Exception e) {
				throw new RuntimeException("invalid content-Length for POST: " + contentLength, e);
			}
			if (postContentData.length < postContentLength)
				postContentData = new byte[postContentLength];
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Receiving ", contentLength, " byte(s) for post from ", description);
			IOH.readData(bufIn, postContentData, 0, postContentLength);
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Received ", contentLength, " byte(s) for  post from ", description);
			if (!processMultiPart(postContentData, 0, postContentLength)) {
				this.paramsParsed = false;
			} else
				this.isMultiPart = true;
		} else
			postContentLength = 0;
		host = header.get("Host");
		if (host != null)
			host = SH.beforeLast(host, ':');
		if ("close".equals(header.get("Connection")))
			shouldClose = true;
		final String cookieString = header.get("Cookie");
		if (cookieString != null) {
			HttpUtils.parseCookie(cookieString, cookies);
		}

	}

	@Override
	public boolean getIsMultipart() {
		return this.isMultiPart;
	}

	private boolean processMultiPart(byte[] data, int start, int end) {
		FastByteArrayInputStream fin = new FastByteArrayInputStream(data, start, end);
		String contentType = header.get("Content-Type");

		if (contentType == null)
			return false;
		Map<String, String> sink = new HashMap<String, String>();
		contentType = HttpUtils.parseContent(contentType, sink);
		if (!"multipart/form-data".equals(contentType))
			return false;
		if (debugging)
			LH.info(log, "Receiviig multipart for  post from ", description);

		String boundary = sink.get("boundary");
		sink.clear();
		if (boundary == null)
			throw new RuntimeException("'boundary' required for 'multipart/form-data': " + contentType);
		byte[] boundaryBytes = ("--" + boundary).getBytes();
		byte[] data2 = fin.readUntil(boundaryBytes);
		fin.skip(boundaryBytes.length);
		boundaryBytes = ("\r\n--" + boundary).getBytes();
		for (;;) {
			Map<String, String> contentDisposition = new HashMap<String, String>();
			byte[] data3 = fin.readUntil(NEW_LINE);
			if (data3 == null || (data3.length >= 2 && data3[0] == '-' && data3[1] == '-'))
				break;
			fin.skip(NEW_LINE.length);
			String partName = null, partContentType = null;
			for (;;) {
				data3 = fin.readUntil(NEW_LINE);
				if (data3.length == 0) {
					fin.skip(NEW_LINE.length);
					break;
				}
				String header = new String(data3);
				String key = SH.beforeFirst(header, ':');
				sink.clear();
				String value = SH.trim(HttpUtils.parseContent(SH.afterFirst(header, ':'), sink));
				if ("Content-Disposition".equals(key)) {
					for (Map.Entry<String, String> e : sink.entrySet())
						contentDisposition.put(e.getKey(), SH.trim('"', e.getValue()));
					partName = contentDisposition.get("name");
				} else if ("Content-Type".equals(key))
					partContentType = value;
				fin.skip(NEW_LINE.length);
			}
			if (partName == null)
				throw new RuntimeException("multipart missing name");
			data2 = fin.readUntil(boundaryBytes);
			boolean isAscii = SH.isAscii(data2);

			if (isAscii) {
				String s = new String(data2);
				params.put(partName, s);
			}
			if (!isAscii || partContentType != null) {
				List<Object> l = extendedParams.get(partName);
				if (l == null)
					extendedParams.put(partName, l = new ArrayList<Object>(1));
				l.add(new HttpMultiPart(data2, partContentType, contentDisposition));
			}
			fin.skip(boundaryBytes.length);
		}
		paramsParsed = true;
		return true;
	}

	private void parseUrl(String text) {
		int qmark = text.indexOf('?');
		this.path = text;

		if (qmark == -1) {
			this.query = null;
			this.uri = IOH.getCanonical(text);
			extendedParams.clear();
			paramsParsed = true;
		} else {
			paramsParsed = false;
			this.uri = text.substring(0, qmark);
			this.query = IOH.getCanonical(text.substring(qmark + 1));
		}
	}

	private void writeHeader(int contentLength) throws IOException {
		bufOut.write(responseType);

		bufOut.write(SERVER);

		bufOut.write(DATE);
		String nowTime = dateFormatter.format(now());

		write(nowTime);
		bufOut.write(NEW_LINE);

		if (SH.is(this.cacheControl)) {
			bufOut.write(CACHE_CONTROL);
			write(this.cacheControl);
			bufOut.write(NEW_LINE);
		}

		bufOut.write(LAST_MODIFIED);
		if (lastModified == -1) {
			write(nowTime);
			bufOut.write(NEW_LINE);
		} else {
			write(dateFormatter.format(lastModified));
			bufOut.write(NEW_LINE);
		}

		bufOut.write(CONTENT_LENGTH);
		SH.toString(contentLength, 10, SH.clear(this.tmpBuf));
		SH.writeUTF(this.tmpBuf, bufOut, this.tmpData);
		bufOut.write(NEW_LINE);

		bufOut.write(CONTENT_TYPE);
		bufOut.write(contentType);
		bufOut.write(NEW_LINE);
		// Add missing default response headers to response headers
		if (!this.server.getDefaultResponseHeaders().isEmpty()) {
			for (Entry<String, String> e : this.server.getDefaultResponseHeaders().entrySet()) {
				String key = e.getKey();
				if (!responseHeader.containsKey(key))
					responseHeader.put(key, e.getValue());
			}
		}
		// Add CSP Nonces
		if (!this.cspNonce.isEmpty()) {
			if (this.responseHeader.containsKey(HttpUtils.CONTENT_SECURITY_POLICY)) {
				String csp = this.responseHeader.get(HttpUtils.CONTENT_SECURITY_POLICY);

				int idx = SH.indexOfFirst(csp, 0, "script-src 'self'");
				String newCsp = csp;
				if (idx == -1) {
					LH.warning(log, "CSP policy doesnt have script-src tag: , defaulting to script-src 'self'", csp);
					newCsp = SH.splice(newCsp, 0, 0, "script-src 'self'; ");
					idx = 0;
				}
				idx += 10;
				StringBuilder sb = new StringBuilder();
				for (String nonce : this.cspNonce) {
					sb.append(' ');
					sb.append(SH.quote("nonce-" + nonce));
				}
				String nonces = SH.toStringAndClear(sb);
				newCsp = SH.splice(newCsp, idx, 0, nonces);
				this.responseHeader.put(HttpUtils.CONTENT_SECURITY_POLICY, newCsp);

			} else
				LH.warning(log, "No CSP Policy was set but nonces are being used, please set a csp policy");
		}
		if (!responseHeader.isEmpty()) {
			for (Map.Entry<String, String> e : responseHeader.entrySet()) {
				bufOut.write(e.getKey().getBytes());
				bufOut.write(COLLON);
				bufOut.write(e.getValue().getBytes());
				bufOut.write(NEW_LINE);
			}
		}
		bufOut.write(this.server.getGlobalHeadersBytes());
		for (String cookie : responseCookies.values()) {
			bufOut.write(SET_COOKIE);
			write(cookie);
			bufOut.write(NEW_LINE);
		}
		if (!shouldClose)
			bufOut.write(CONNECTION_KEEP_ALIVE);
	}

	private byte[] tmpData = new byte[32];
	private int httpResponseWarnLogRequestSize = -1;
	private TextMatcher httpResponseWarnLogRequestSuppress = null;

	private void write(CharSequence format) throws IOException {
		int len = format.length();
		if (tmpData.length < len)
			tmpData = new byte[len];
		SH.writeUTF(format, bufOut, tmpData);
	}

	private Date now() {
		now.setTime(System.currentTimeMillis());
		return now;
	}

	private String pooled(StringBuilder sb) {
		return SH.trim(sb);
	}

	private void close() {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Closing connection: " + this);
		IOH.close(bufIn);
		IOH.close(bufOut);
		this.server.onClosed(this);
		closed = true;
		if (captureStats)
			logStats("Closed");
	}

	private boolean readUntil(byte c, StringBuilder sb) throws IOException {
		while (true) {
			int c2 = bufIn.read();
			if (c2 == -1)
				return false;
			if (c2 == c)
				return true;
			else if (c2 == SH.BYTE_CR)
				continue;
			else if (c2 == SH.BYTE_NEWLINE)
				return false;
			sb.append((char) c2);
		}
	}

	@Override
	public FastPrintStream getOutputStream() {
		return printOutputStream;
	}

	@Override
	public Map<String, String> getHeader() {
		return uheader;
	}

	@Override
	public Map<String, String> getParams() {
		parseParams();
		return uparams;
	}

	public static final void parseParams(String query, int start, int end, Map<String, String> paramsSink, Map<String, List<Object>> multiParamsSink, StringBuilder buf) {
		buf.setLength(0);
		while (start < end) {
			String key, value;
			int n = query.indexOf('=', start);
			if (n == -1)
				break;
			key = decodeUrl(query, start, n, buf);
			start = n + 1;
			n = query.indexOf('&', start);
			if (n != -1) {
				value = decodeUrl(query, start, n, buf);
				start = n + 1;
			} else {
				value = decodeUrl(query, start, query.length(), buf);
				start = end;
			}
			String oldValue = paramsSink.put(key, value);
			if (oldValue != null && multiParamsSink != null) {
				List<Object> o = multiParamsSink.get(key);
				if (o == null) {
					multiParamsSink.put(key, o = new ArrayList<Object>(2));
					o.add(oldValue);
				}
				o.add(value);
			}
		}
	}

	private static String decodeUrl(String query, int start, int n, StringBuilder buf) {
		return SH.needsUrlDecoding(query, start, n) ? SH.toStringAndClear(SH.decodeUrl(query, start, n, buf)) : query.substring(start, n);
	}

	private void parseParams() {
		if (!paramsParsed) {
			if (query != null) {
				parseParams(query, 0, SH.length(query), params, extendedParams, this.tmpBuf);
			} else if (this.postContentLength > 0) {
				final int len = this.postContentLength;
				int i = 0;
				while (i < len) {
					String key, value;
					int n = AH.indexOf((byte) '=', this.postContentData, i, len);
					if (n != -1) {
						key = new String(this.postContentData, i, n - i);
						i = n + 1;
						n = AH.indexOf((byte) '&', this.postContentData, i, len);
						if (n != -1) {
							value = new String(this.postContentData, i, n - i);
							i = n + 1;
						} else {
							value = new String(this.postContentData, i, len - i);
							i = len;
						}
						String key2 = SH.decodeUrl(key);
						String value2 = SH.decodeUrl(value);
						String oldValue = params.put(key2, value2);
						if (oldValue != null) {
							List<Object> o = extendedParams.get(key2);
							if (o == null) {
								extendedParams.put(key2, o = new ArrayList<Object>(2));
								o.add(oldValue);
							}
							o.add(value2);
						}
					} else {
						i = len;
					}
				}
			}
			paramsParsed = true;
		}

	}

	@Override
	public String getContextPath() {
		return path;
	}

	@Override
	public Map<String, String> getCookies() {
		return ucookies;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public void setContentTypeAsBytes(byte[] contentType) {
		this.contentType = contentType;
	}

	@Override
	public String getQueryString() {
		return query;
	}

	@Override
	public String getRequestUri() {
		return uri;
	}

	@Override
	public byte[] getRequestContentBuffer() {
		return postContentData;
	}
	@Override
	public int getRequestContentLength() {
		return postContentLength;
	}

	@Override
	public long getIfModifiedSince() {
		if (modifiedSince == null) {
			modifiedSince = parseDate(header.get("If-Modified-Since"));
		}
		return modifiedSince == null ? -1 : modifiedSince.getTime();

	}

	@Override
	public void setResponseType(byte[] type) {
		this.responseType = type;
	}

	@Override
	public void setResponseType(int type) {
		switch (type) {
			case HttpRequestResponse.HTTP_200_OK:
				responseType = BYTES_HTTP_200_OK;
				break;
			case HttpRequestResponse.HTTP_204_NO_CONTENT:
				responseType = BYTES_HTTP_204_NO_CONTENT;
				break;
			case HttpRequestResponse.HTTP_400_BAD_REQUEST:
				responseType = BYTES_HTTP_400_BAD_REQUEST;
				break;
			case HttpRequestResponse.HTTP_401_UNAUTHORIZED:
				responseType = BYTES_HTTP_401_UNAUTHORIZED;
				break;
			case HttpRequestResponse.HTTP_404_NOT_FOUND:
				responseType = BYTES_HTTP_404_NOT_FOUND;
				break;
			case HttpRequestResponse.HTTP_304_NOT_MODIFIED:
				responseType = BYTES_HTTP_304_NOT_MODIFIED;
				break;
			case HttpRequestResponse.HTTP_500_SERVICE_ERROR:
				responseType = BYTES_HTTP_500_SERVICE_ERROR;
				break;
			case HttpRequestResponse.HTTP_503_SERVICE_UNAVAILABLE:
				responseType = BYTES_HTTP_503_SERVICE_UNAVAILABLE;
				break;
			case HttpRequestResponse.HTTP_206_PARTAIL_CONTENT:
				responseType = BYTES_HTTP_206_PARTIAL_CONTENT;
				break;
			case HttpRequestResponse.HTTP_403_FORBIDDEN:
				responseType = BYTES_HTTP_403_FORBIDDEN;
				break;
			default:
				throw new NoSuchElementException("unknown http response code: " + type);
		}
	}
	@Override
	public void setContentType(String contentType) {
		setContentTypeAsBytes(contentType.getBytes());
	}

	private static final Set<String> reserved = CH.s("Content-Length", "Content-Type", "Date", "Last-Modified", "Location", "Server", "Last-Modified");
	private static final byte RESPONSE_STATE_SYNC = 0;

	@Override
	public void putResponseHeader(String key, String value) {
		if (reserved.contains(key))
			throw new IllegalArgumentException("key is reserved and can not be set via putResponseHeader(...): " + key);
		responseHeader.put(key, value);
	}

	@Override
	public String getResponseHeader(String key) {
		return this.responseHeader.get(key);
	}

	@Override
	public void sendRedirect(String url) {
		this.redirect = url;
	}

	@Override
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isDebugging() {
		return debugging;
	}

	public void setDebugging(boolean debugging, int maxDebugLength) {
		this.debugging = debugging;
		this.maxDebugLength = maxDebugLength;
		try {
			if (in != null)
				reset(in, out, description, server, isSecure, port, remoteHost, remotePort);
		} catch (IOException e) {
			LH.severe(log, "", e);
		}
	}

	@Override
	public void putCookie(String key, String value, String optionalDomain, long optionalExpires, String additionalOptions) {
		SH.clear(sb);
		sb.append(key).append('=').append(value);
		if (optionalDomain != null)
			sb.append("; Domain=").append(optionalDomain);
		if (optionalExpires > 0) {
			now.setTime(System.currentTimeMillis());
			sb.append("; Expires=").append(dateFormatter.format(now));
		}
		if (isSecure)
			sb.append("; Secure");
		sb.append("; HttpOnly; Path=/");
		if (SH.is(additionalOptions))
			sb.append("; ").append(additionalOptions);
		responseCookies.put(key, sb.toString());
		SH.clear(sb);
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public HttpSession getSession(boolean createIfMissing) {
		if (session == null) {
			if (createIfMissing)
				session = sessionManager.getOrCreateHttpSession(this);
			else
				session = sessionManager.getHttpSession(this);
		} else if (!session.isAlive()) {
			session = null;
			if (createIfMissing)
				session = sessionManager.getOrCreateHttpSession(this);
			else
				session = sessionManager.getHttpSession(this);
		}
		return session;
	}

	@Override
	public HttpServer getHttpServer() {
		return server;
	}

	@Override
	public Object findAttribute(String key) {
		Object r = getAttributes().get(key);
		getSession(false);
		if (r == null && session != null) {
			r = session.getAttributes().get(key);
		}
		if (r == null)
			r = getHttpServer().getAttributes().get(key);
		return r;
	}

	@Override
	public List<Object> getParamAsList(String key) {
		parseParams();
		List<Object> r = extendedParams.get(key);
		if (r != null)
			return r;
		String o = params.get(key);
		if (o == null)
			return null;
		extendedParams.put(key, r = new ArrayList<Object>(1));
		r.add(o);
		return r;
	}

	@Override
	public String getResponseType() {
		return new String(responseType);
	}

	@Override
	public boolean getIsSecure() {
		return isSecure;
	}

	@Override
	public void forward(String url) throws IOException {
		server.include(this, url);
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getRequestUrl() {
		if (requestUrl == null) {
			String s = getQueryString();
			if (s == null && postContentLength > 0)
				s = new String(getRequestContentBuffer(), 0, getRequestContentLength());
			requestUrl = HttpUtils.buildUrl(getIsSecure(), getHost(), getPort(), getRequestUri(), s, SH.clear(sb)).toString();
			SH.clear(sb);
		}
		return requestUrl;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public String getRemoteHost() {
		return remoteHost;
	}

	@Override
	public int getRemotePort() {
		return remotePort;
	}

	@Override
	public void setResponseAsyncMode() {
		this.asyncMode = true;
	}

	@Override
	public boolean getResponseAsyncMode() {
		return asyncMode;
	}

	@Override
	public boolean respondNow(boolean sendInThisThread) {
		if (closed)
			throw new IllegalStateException("Connection already closed");
		if (!getResponseAsyncMode())
			throw new IllegalStateException("Only available in async mode. Call setResponseAsyncMode() first");
		try {
			if (sendInThisThread) {
				sendResponse();
				if (!shouldClose)
					this.server.getThreadPool().execute(this);
			} else {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "async responding sending into thread pool", description);
				this.needsToRespond = true;
				this.server.getThreadPool().execute(this);
			}
			return true;
		} catch (Exception e) {
			LH.info(log, "For ", description, ": ", ">>>> async response failed: ", e);
			close();
			return false;
		}
	}
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void getRequestContentBuffer(StringBuilder sink) {
		SH.appendBytes(getRequestContentBuffer(), 0, getRequestContentLength(), sink);
	}

	public void readData() {

	}

	public String getCacheControl() {
		return cacheControl;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public void setHttpResponseWarnMs(int httpResponseWarnMs, int logRequestSize, TextMatcher logSuppression) {
		this.httpResponseWarnMs = httpResponseWarnMs;
		this.httpResponseWarnLogRequestSize = logRequestSize;
		this.httpResponseWarnLogRequestSuppress = logSuppression;
	}

	private static final SecureRandom r = new SecureRandom();
	private Set<String> cspNonce = new HashSet<String>();

	public String genCspNonce() {
		String nonce = nonce(16);
		while (cspNonce.contains(nonce))
			nonce = nonce(16);
		cspNonce.add(nonce);

		return nonce;
	}
	private String nonce(int nbytes) {
		byte b[] = new byte[nbytes];
		r.nextBytes(b);
		byte[] enc = Base64.getEncoder().encode(b);
		String nonce = new String(enc);

		return nonce;

	}

}
