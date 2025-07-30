package com.f1.http.impl;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import com.f1.http.HttpServer;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.ServerSocketAcceptor;
import com.f1.utils.ServerSocketEntitlements;

public class HttpServerSocket extends ServerSocketAcceptor {
	private static final Logger log = Logger.getLogger(HttpServerSocket.class.getName());

	private static final int DEFAULT_HTTP_PORT = 80;
	private static final int DEFAULT_HTTPS_PORT = 443;

	private HttpServer server;

	private int port;

	// unsecure
	public HttpServerSocket(int port) {
		this(null, null, port);
	}
	public HttpServerSocket(String bindAddr, ServerSocketEntitlements entitlements, int port) {
		super(bindAddr, entitlements, port);
		this.port = port;
	}

	public HttpServerSocket() {
		this(null, null, DEFAULT_HTTP_PORT);
	}

	// secure
	public HttpServerSocket(int port, File keystore, String keystorePassword) {
		this(null, null, port, keystore, keystorePassword);
	}
	public HttpServerSocket(String bindAddr, ServerSocketEntitlements entitlements, int port, File keystore, String keystorePassword) {
		super(bindAddr, entitlements, port, keystore, keystorePassword);
		this.port = port;
	}
	public HttpServerSocket(String bindAddr, ServerSocketEntitlements entitlements, int port, byte[] keystoreContents, String keystorePassword) {
		super(bindAddr, entitlements, port, keystoreContents, keystorePassword);
		this.port = port;
	}

	public HttpServerSocket(String bindAddr, ServerSocketEntitlements entitlements, File keystore, String keystorePassword) {
		this(bindAddr, entitlements, DEFAULT_HTTPS_PORT, keystore, keystorePassword);
	}

	public void start() throws IOException {
		super.start();
		if (getIsSecure())
			System.out.println("To access this secure web server browse to: https://" + EH.getLocalHost() + ":" + getServerLocalPort());
		else
			System.out.println("To access this web server browse to: http://" + EH.getLocalHost() + ":" + getServerLocalPort());
	}

	@Override
	public String getDescription() {
		return getIsSecure() ? "Https Server" : "Http Server";
	}

	@Override
	protected void accept(Socket socket) throws IOException {
		server.onConnection(socket, socket.toString(), this, IOH.getRemoteHostName(socket), socket.getPort());
	}

	public void setServer(BasicHttpServer server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}

}
