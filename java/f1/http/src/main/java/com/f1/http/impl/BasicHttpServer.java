package com.f1.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.http.HttpHandler;
import com.f1.http.HttpRequestResponse;
import com.f1.http.HttpServer;
import com.f1.http.HttpSessionManager;
import com.f1.http.HttpUtils;
import com.f1.http.handler.ErrorHttpHandler;
import com.f1.utils.BasicLocaleFormatter;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.impl.BasicClock;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.Tuple2;

public class BasicHttpServer implements HttpServer {

	public static final String DEFAULT_CACHE_CONTROL = "private,must-revalidate";

	private static final Logger log = Logger.getLogger(BasicHttpServer.class.getName());

	public static final int DEFAULT_CONNECTION_TIMEOUT = 300 * 1000;

	private boolean running;
	private Executor threadPool;
	private List<Tuple2<HttpRequestMatcher, HttpHandler>> handlers = new CopyOnWriteArrayList<Tuple2<HttpRequestMatcher, HttpHandler>>();
	private ConcurrentMap<String, Object> attributes = new CopyOnWriteHashMap<String, Object>();
	private ConcurrentMap<String, String> defaultResponseHeaders = new CopyOnWriteHashMap<String, String>();
	private Map<String, String> globalResponseHeaders = new CopyOnWriteHashMap<String, String>();
	private HttpSessionManager sessionManager;
	private PropertyController properties;
	private boolean debugging = false;
	private List<HttpServerSocket> serverSockets = new CopyOnWriteArrayList<HttpServerSocket>();
	private HttpServerSecurityPolicy securityPolicy;

	public BasicHttpServer(Executor threadPool, PropertyController properties, LocaleFormatter defaultLocaleFormatter) {
		this.properties = properties;
		this.threadPool = threadPool;
		this.sessionManager = new BasicHttpSessionManager(new BasicClock(), "F1SESSION", 30, TimeUnit.MINUTES, defaultLocaleFormatter);
	}

	public BasicHttpServer() {
		this(null, null, new BasicLocaleFormatter(Locale.getDefault(), TimeZone.getDefault(), true, OH.EMPTY_FILE_ARRAY, Collections.EMPTY_MAP));
	}

	private HttpHandler errorHandler = new ErrorHttpHandler();
	private HttpHandler resourceNotFoundHandler = new ResourceNotFoundHandler();

	private int unsecurePort = -1;

	private int securePort = -1;

	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

	private AtomicLong serviceStats = new AtomicLong();
	private AtomicLong includeStats = new AtomicLong();
	private AtomicLong connectionsOpenedStats = new AtomicLong();
	private AtomicLong connectionsClosedStats = new AtomicLong();

	private int maxUrlLengthLogging = 10000;

	private byte[] globalResponseHeadersBytes;

	private int maxDebugLength;

	private String defaultCacheControl = DEFAULT_CACHE_CONTROL;

	@Override
	public void start() throws IOException {
		if (running)
			throw new IllegalStateException("already running");
		this.running = true;
		if (threadPool == null)
			throw new IllegalStateException("must assign threadpool");
		for (HttpServerSocket socket : serverSockets)
			socket.start();
		this.sessionManager.start();
	}

	@Override
	public void stop() {
		if (!running)
			throw new IllegalStateException("not running");
		running = false;
		this.sessionManager.stop();
	}
	@Override
	public boolean isRunning() {
		return this.running;
	}

	@Override
	public void addHttpHandler(String uriPattern, HttpHandler httpRequestHandler, boolean highPriority) {
		addHttpHandler(new UriRequestMatcher(SH.m(uriPattern)), httpRequestHandler, highPriority);
	}
	@Override
	public void addHttpHandlerStrict(String uriPattern, HttpHandler httpRequestHandler, boolean highPriority) {
		uriPattern = SH.replaceAll(uriPattern, '/', "\\/");
		addHttpHandler(new UriRequestMatcher(TextMatcherFactory.FULL_CASE_SENSETIVE.toMatcher(uriPattern)), httpRequestHandler, highPriority);
	}

	@Override
	public void addHttpHandler(HttpRequestMatcher matcher, HttpHandler httpRequestHandler, boolean highPriority) {
		if (highPriority)
			handlers.add(0, new Tuple2<HttpRequestMatcher, HttpHandler>(matcher, httpRequestHandler));
		else
			handlers.add(new Tuple2<HttpRequestMatcher, HttpHandler>(matcher, httpRequestHandler));
	}

	@Override
	public void include(HttpRequestResponse request, String url) throws IOException {
		this.includeStats.incrementAndGet();
		String pwd = request.getRequestUri();
		String uri = HttpUtils.getCanonical(pwd, url);
		IncludeHttpRequestResponse t = new IncludeHttpRequestResponse(request);
		t.setDummyRequestUri(uri);
		service(t);

	}

	@Override
	public void service(HttpRequestResponse request) throws IOException {
		this.serviceStats.incrementAndGet();
		if (this.securityPolicy != null) {
			int code = this.securityPolicy.checkRequest(this, request);
			if (code != HttpRequestResponse.HTTP_200_OK) {
				request.setResponseType(code);
				return;
			}
		}
		for (Tuple2<HttpRequestMatcher, HttpHandler> handler : handlers)
			if (handler.getA().canHandle(request)) {
				if (log.isLoggable(Level.FINE)) {
					LH.fine(log, "Request from ", request.getRemoteHost(), ":", request.getRemotePort(), " Url: '",
							SH.suppress(request.getRequestUrl(), this.getMaxUrlLengthLogging()), "'. Handled by: ", OH.getSimpleClassName(handler.getB()));
				}
				handler.getB().handle(request);
				return;
			}
		if (log.isLoggable(Level.INFO))
			LH.info(log, "Request '", request.getRequestUrl(), "' is not found (404)");
		request.setResponseType(HttpRequestResponse.HTTP_404_NOT_FOUND);
	}

	@Override
	public HttpSessionManager getHttpSessionManager() {
		return sessionManager;
	}

	public void setHttpSessionManager(HttpSessionManager httpSessionManager) {
		this.sessionManager = httpSessionManager;
	}

	@Override
	public ConcurrentMap<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public PropertyController getProperties() {
		return properties;
	}

	public boolean isDebugging() {
		return debugging;
	}
	public int getMaxDebugLength() {
		return this.maxDebugLength;
	}

	public void setDebugging(boolean debugging, int maxDebugLength) {
		this.debugging = debugging;
		this.maxDebugLength = maxDebugLength;
	}

	@Override
	public void onError(HttpRequestResponse socketHttpRequestResponse) throws IOException {
		try {
			this.errorHandler.handle(socketHttpRequestResponse);
		} catch (IOException e) {
			LH.warning(log, "Error Handler ", this.errorHandler, " result in error", e);
		}
	}

	public HttpHandler getErrorHandler() {
		return errorHandler;
	}

	public void setErrorHandler(HttpHandler errorHandler) {
		OH.assertNotNull(errorHandler);
		this.errorHandler = errorHandler;
	}

	@Override
	public void onResourceNotFound(HttpRequestResponse request) {
		try {
			this.resourceNotFoundHandler.handle(request);
		} catch (IOException e) {
			LH.warning(log, "Resource Not Found (404) Handler ", this.resourceNotFoundHandler, " result in error", e);
		}
	}

	public HttpHandler getResourceNotFoundHandler() {
		return resourceNotFoundHandler;
	}

	public void setResourceNotFoundHandler(HttpHandler resourceNotFoundHandler) {
		OH.assertNotNull(resourceNotFoundHandler);
		this.resourceNotFoundHandler = resourceNotFoundHandler;
	}

	@Override
	public void onConnection(Socket skt, String description, HttpServerSocket socket, String remoteHost, int remotePort) throws IOException {
		InputStream in = skt.getInputStream();
		OutputStream out = skt.getOutputStream();
		skt.setSoTimeout(connectionTimeout);
		BasicHttpRequestResponse connection = new BasicHttpRequestResponse();
		this.connectionsOpenedStats.incrementAndGet();
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Received new http connection, sending to thread pool: ", description, " ==> ", connection);

		connection.setDebugging(isDebugging(), getMaxDebugLength());
		connection.setHttpResponseWarnMs(this.getHttpResponseWarnMs(), this.getHttpResponseWarnLogRequestSize(), this.getHttpResponseWarnLogSuppressor());
		connection.reset(in, out, description, this, socket.getIsSecure(), socket.getPort(), remoteHost, remotePort);
		threadPool.execute(connection);
	}

	@Override
	public Iterable<HttpServerSocket> getServerSockets() {
		return serverSockets;
	}

	@Override
	public void addServerSocket(HttpServerSocket serverSocket) throws IOException {
		if (serverSocket.getIsRunning())
			throw new IllegalStateException("socket already running");
		serverSocket.setServer(this);
		if (serverSocket.getIsSecure() && securePort == -1)
			securePort = serverSocket.getPort();
		else if (!serverSocket.getIsSecure() && unsecurePort == -1)
			unsecurePort = serverSocket.getPort();
		if (running)
			serverSocket.start();
		serverSockets.add(serverSocket);
	}

	@Override
	public int getSecurePort() {
		return securePort;
	}

	@Override
	public int getUnsecurePort() {
		return unsecurePort;

	}

	public void setProperties(PropertyController properties) {
		this.properties = properties;
	}

	@Override
	public Executor getThreadPool() {
		return threadPool;
	}

	@Override
	public void setThreadPool(Executor threadPool) {
		this.threadPool = threadPool;
	}

	public void setDefaultFormatter(LocaleFormatter localeFormatter) {
		this.sessionManager.setDefaultFormatter(localeFormatter);
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public long getStatsServices() {
		return this.serviceStats.get();
	}
	public long getStatsIncludes() {
		return this.includeStats.get();
	}
	public long getStatsConnectionsOpened() {
		return this.connectionsOpenedStats.get();
	}
	public long getStatsConnectionsClosed() {
		return this.connectionsClosedStats.get();
	}

	@Override
	public void onClosed(HttpRequestResponse basicHttpRequestResponse) {
		connectionsClosedStats.incrementAndGet();
	}

	public int getMaxUrlLengthLogging() {
		return maxUrlLengthLogging;
	}

	public void setMaxUrlLengthLogging(int maxUrlLengthLogging) {
		this.maxUrlLengthLogging = maxUrlLengthLogging;
	}

	public ConcurrentMap<String, String> getDefaultResponseHeaders() {
		return this.defaultResponseHeaders;
	}
	public void putDefaultResponseHeader(String key, String value) {
		this.defaultResponseHeaders.put(key, value);
	}
	protected byte[] getGlobalHeadersBytes() {
		byte[] t = globalResponseHeadersBytes;
		if (t == null) {
			synchronized (this.globalResponseHeaders) {
				StringBuilder sb = new StringBuilder();
				for (Entry<String, String> e : this.globalResponseHeaders.entrySet())
					sb.append(e.getKey()).append(": ").append(e.getValue()).append("\r\n");
				t = this.globalResponseHeadersBytes = sb.toString().getBytes();
			}
		}
		return t;
	}
	@Override
	public void putGlobalResponseHeader(String key, String value) {
		synchronized (this.globalResponseHeaders) {
			this.globalResponseHeadersBytes = null;
			this.globalResponseHeaders.put(key, value);
		}
	}

	@Override
	public Set<String> getGlobalResponseHeaders() {
		return this.globalResponseHeaders.keySet();
	}

	@Override
	public String getGlobalResponseHeader(String key) {
		return this.globalResponseHeaders.get(key);
	}

	public String getDefaultCacheControl() {
		return this.defaultCacheControl;
	}

	public void setDefaultCacheControl(String cc) {
		this.defaultCacheControl = cc;
	}

	private int httpResponseWarnMs = 5000;

	public int getHttpResponseWarnMs() {
		return this.httpResponseWarnMs;
	}
	public void setHttpResponseWarnMs(int warnMs) {
		this.httpResponseWarnMs = warnMs;
	}

	private int httpResponseWarnLogRequestSize = 0;

	private TextMatcher httpResponseWarnLogSuppressor;

	public int getHttpResponseWarnLogRequestSize() {
		return this.httpResponseWarnLogRequestSize;
	}
	public void setHttpResponseWarnLogRequestSize(int n, String suppressor) {
		this.httpResponseWarnLogRequestSize = n;
		this.httpResponseWarnLogSuppressor = suppressor == null || suppressor.length() == 0 ? null : TextMatcherFactory.PARTIAL_CASE_INSENSETIVE.toMatcher(suppressor);
	}

	private TextMatcher getHttpResponseWarnLogSuppressor() {
		return this.httpResponseWarnLogSuppressor;
	}

	@Override
	public HttpServerSecurityPolicy getSecurityPolicy() {
		return securityPolicy;
	}

	@Override
	public void setSecurityPolicy(HttpServerSecurityPolicy securityPolicy) {
		this.securityPolicy = securityPolicy;
	}

	public void debug(HttpRequestResponse httpReqRes, String description, String reqText, String resText) {
		LH.info(log, "For ", description, ": ", SH.NEWLINE, ">>>> Request: ", SH.NEWLINE, reqText, SH.NEWLINE, ">>>> Response: ", SH.NEWLINE, resText);
	}
}
