package com.f1.http;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;

import com.f1.http.impl.HttpRequestMatcher;
import com.f1.http.impl.HttpServerSecurityPolicy;
import com.f1.http.impl.HttpServerSocket;
import com.f1.utils.PropertyController;

public interface HttpServer {

	public void service(HttpRequestResponse request) throws IOException;

	void include(HttpRequestResponse request, String url) throws IOException;

	public HttpSessionManager getHttpSessionManager();

	public ConcurrentMap<String, Object> getAttributes();

	public PropertyController getProperties();

	void onError(HttpRequestResponse request) throws IOException;
	void onResourceNotFound(HttpRequestResponse request);

	public Iterable<HttpServerSocket> getServerSockets();

	public void addServerSocket(HttpServerSocket serverSocket) throws IOException;

	public void addHttpHandler(HttpRequestMatcher matcher, HttpHandler httpRequestHandler, boolean highPriority);

	public void addHttpHandler(String uriPattern, HttpHandler httpRequestHandler, boolean highPriority);//case insensitive, partial match
	public void addHttpHandlerStrict(String uriPattern, HttpHandler httpRequestHandler, boolean highPriority);//case sensitive, full expression

	void onConnection(Socket socket, String description, HttpServerSocket serverSocket, String remoteHost, int remotePort) throws IOException;

	int getSecurePort();

	int getUnsecurePort();

	Executor getThreadPool();

	void setThreadPool(Executor threadPool);

	void start() throws IOException;

	void stop();

	boolean isRunning();

	public void onClosed(HttpRequestResponse basicHttpRequestResponse);

	/*
	 * Add to global response headers, they cant be modified or removed
	 */
	public void putGlobalResponseHeader(String key, String value);
	/*
	 * These are global response headers, they cannot be modified or removed
	 */
	public Set<String> getGlobalResponseHeaders();
	public String getGlobalResponseHeader(String key);

	/*
	 * Map of default response headers which can be overridden by HttpHandlers
	 */
	public ConcurrentMap<String, String> getDefaultResponseHeaders();
	/*
	 * Puts default response headers, can be overridden by HttpHandlers
	 */
	public void putDefaultResponseHeader(String key, String value);

	HttpServerSecurityPolicy getSecurityPolicy();
	void setSecurityPolicy(HttpServerSecurityPolicy securityPolicy);

}
