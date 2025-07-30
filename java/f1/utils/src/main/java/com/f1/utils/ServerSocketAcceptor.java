package com.f1.utils;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public abstract class ServerSocketAcceptor implements Runnable {
	private static final Logger log = Logger.getLogger(ServerSocketAcceptor.class.getName());

	private ServerSocket serverSocket;
	private boolean running;
	private boolean isSecure = false;
	private File keystore;
	private String keystorePassword;

	private int listenPort;

	private byte[] keystoreContents;

	private String listenPortBindAddr;

	private ServerSocketEntitlements entitlements;

	// unsecure
	public ServerSocketAcceptor(String bindAddr, ServerSocketEntitlements entitlments, int port) {
		this.listenPort = port;
		this.listenPortBindAddr = bindAddr;
		this.entitlements = entitlments;
	}

	// secure
	public ServerSocketAcceptor(String bindAddr, ServerSocketEntitlements entitlments, int port, File keystore, String keystorePassword) {
		this.listenPort = port;
		this.listenPortBindAddr = bindAddr;
		this.entitlements = entitlments;
		setSecure(keystore, keystorePassword);
	}
	public ServerSocketAcceptor(String bindAddr, ServerSocketEntitlements entitlments, int port, byte[] keystore, String keystorePassword) {
		this.listenPort = port;
		this.listenPortBindAddr = bindAddr;
		this.entitlements = entitlments;
		setSecure(keystore, keystorePassword);
	}

	public void start() throws IOException {
		if (running)
			throw new IllegalStateException("already running");
		this.running = true;
		if (isSecure) {
			if (keystore != null)
				this.serverSocket = IOH.openSSLServerSocketWithReason(listenPortBindAddr, listenPort, keystore, keystorePassword, getDescription());
			else
				this.serverSocket = IOH.openSSLServerSocketWithReason(listenPortBindAddr, listenPort, keystoreContents, keystorePassword, getDescription());
		} else
			this.serverSocket = IOH.openServerSocketWithReason(listenPortBindAddr, listenPort, getDescription());
		new Thread(this, SH.replaceAll(getDescription(), ' ', "") + "Socket-" + listenPort).start();
	}

	public String getDescription() {
		return getIsSecure() ? "Secure Server" : "Server";
	}

	@Override
	public void run() {
		int errorCount = 0;
		while (running) {
			Socket socket;
			try {
				socket = this.serverSocket.accept();
				IOH.optimize(socket);
				socket.setKeepAlive(true);
				errorCount = 0;
			} catch (Exception e) {
				if (running) {
					if (++errorCount >= 10) {
						LH.warning(log, "Error accepting socket on port ", listenPort, ", will try again in ", errorCount, " second(s)...", e);
						OH.sleep(errorCount * 1000);
						if (errorCount > 30)
							errorCount = 30;
					} else {
						LH.warning(log, "Error accepting socket on port ", listenPort, e);
					}
				}
				continue;
			}
			if (!handleEntitlements(this.entitlements, this.serverSocket, socket))
				continue;
			try {
				accept(socket);
			} catch (Exception e) {
				if (running)
					LH.warning(log, "Error processing newly accepted socket on port ", listenPort, e);
			}
		}
		IOH.close(this.serverSocket);
	}
	static public boolean handleEntitlements(ServerSocketEntitlements entitlements, ServerSocket serverSocket, Socket socket) {
		if (entitlements != null)
			try {
				String error = entitlements.getNotEntitledMessage(serverSocket, socket);
				if (error != null) {
					LH.info(log, "closing un-entitled socket on port ", serverSocket.getLocalPort(), ": ", error);
					IOH.close(socket);
					return false;
				}
			} catch (Exception e) {
				LH.warning(log, "Error processing entitlements for newly accepted socket on port ", serverSocket.getLocalPort(), ": ", socket.getRemoteSocketAddress(), e);
				IOH.close(socket);
				return false;
			}
		return true;
	}

	protected abstract void accept(Socket socket) throws IOException;

	public void stop() {
		if (!running)
			throw new IllegalStateException("not running");
		running = false;
	}

	public boolean getIsSecure() {
		return isSecure;
	}

	public void setUnsecure() {
		if (running)
			throw new IllegalStateException("http server already running");
		this.isSecure = false;
		this.keystore = null;
		this.keystoreContents = null;
		this.keystorePassword = null;
	}

	public void setSecure(File keystore, String keystorePassword) {
		assertNotRunning();
		this.isSecure = keystore != null || keystorePassword != null;
		this.keystore = keystore;
		this.keystoreContents = null;
		this.keystorePassword = keystorePassword;
	}
	public void setSecure(byte[] keystoreContents, String keystorePassword) {
		assertNotRunning();
		this.isSecure = keystoreContents != null || keystorePassword != null;
		this.keystoreContents = keystoreContents;
		this.keystore = null;
		this.keystorePassword = keystorePassword;
	}

	private void assertNotRunning() {
		if (running)
			throw new IllegalStateException("Already running");
	}

	public boolean getIsRunning() {
		return running;
	}

	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		assertNotRunning();
		this.listenPort = listenPort;
	}

	/**
	 * @return may be different than configured port, specifically if configured port is 0 (dynamic port)
	 */
	public int getServerLocalPort() {
		return this.serverSocket == null ? -1 : this.serverSocket.getLocalPort();
	}

}
