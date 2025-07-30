package com.f1.console.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;

import com.f1.console.ConsoleManager;
import com.f1.console.ConsoleServer;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class ConsoleServerImpl implements ConsoleServer, Runnable {
	private static java.util.logging.Logger log = java.util.logging.Logger.getLogger(ConsoleServerImpl.class.getName());
	public static final int DEFAULT_PORT = 3333;

	protected final Executor executor;
	protected final ServerSocket serverSocket;
	protected boolean isRunning;
	protected final ConsoleManager manager;
	private final BasicServicePackage basicServicePackage;
	private final InvokersConsoleServicePackage invokerServicePackage;

	public ConsoleServerImpl(Executor executor, int port, File globalHistory, ConsoleAuthenticator authenticator) throws IOException {
		this(executor, IOH.openServerSocketWithReason(port, "F1 Console (f1.console.port) "), globalHistory, authenticator);
		EH.toStdout("To access this admin console via command line: telnet " + EH.getLocalHost() + " " + serverSocket.getLocalPort(), true);
	}

	public ConsoleServerImpl() {
		this(null, null, null, null);
	}

	public ConsoleServerImpl(Executor executor, ServerSocket serverSocket, File globalHistory, ConsoleAuthenticator authenticator) {
		this.manager = new ConsoleManagerImpl(this, globalHistory, authenticator);
		this.manager.addServicePackage(this.basicServicePackage = new BasicServicePackage());
		this.manager.addServicePackage(this.invokerServicePackage = new InvokersConsoleServicePackage());
		this.executor = executor;
		this.serverSocket = serverSocket;
		isRunning = true;
		if (serverSocket != null) {
			executor.execute(this);
			LH.info(log, "Console Server listening on: ", serverSocket.getLocalSocketAddress(), ":", serverSocket.getLocalPort());
		} else {
			LH.info(log, "Console Server not listening,local loopback only");
		}
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				Socket s = serverSocket.accept();
				LH.info(log, "Received a console connection from ", s.getRemoteSocketAddress());
				createConnection(s.getInputStream(), s.getOutputStream(), "remote address: " + s.getRemoteSocketAddress().toString());
			} catch (SocketException e2) {
			} catch (Exception e2) {
				LH.severe(log, "error with run()", e2);
				OH.sleep(1000);
			}
		}
	}

	@Override
	public ConsoleConnectionImpl createConnection(InputStream i, OutputStream o) {
		return createConnection(i, o, "local");
	}

	public ConsoleConnectionImpl createConnection(InputStream i, OutputStream o, String connectionDetails) {
		ConsoleConnectionImpl msc = new ConsoleConnectionImpl(this, i, o, connectionDetails);
		if (i != null)
			executor.execute(msc);
		return msc;

	}

	@Override
	public boolean shutdown() {
		isRunning = false;
		IOH.close(serverSocket);
		return serverSocket.isClosed();
	}

	@Override
	public ConsoleManager getManager() {
		return manager;
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	public BasicServicePackage getBasicServicePackage() {
		return basicServicePackage;
	}

	public InvokersConsoleServicePackage getInvokerServicePackage() {
		return invokerServicePackage;
	}
}
