package com.f1.ami.client;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.f1.base.Password;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiClientAsServer implements Closeable {
	private static final java.util.logging.Logger log = LH.get();

	private class Runner implements Runnable {

		@Override
		public void run() {
			while (running) {
				Socket socket;
				try {
					socket = serverSocket.accept();
					LH.info(log, "Socket received from: " + socket);
					AmiClient client = new AmiClient();
					factory.onClient(socket, client);
					if (!client.isConnected())
						LH.warning(log, "Client not connected, did your factory forget to call AmiClient::start(socket,login,options)");
				} catch (IOException e) {
					if (running)
						e.printStackTrace();
				}
			}

		}
	}

	private ServerSocket serverSocket;
	private Runner runner;
	private boolean running;
	private AmiClientAsServerFactory factory;
	private Thread thread;

	public AmiClientAsServer(int serverPort, File keystoreFile, Password keystorePassword, AmiClientAsServerFactory factory) throws IOException {
		OH.assertNotNull(factory, "factory");
		if (keystorePassword == null && keystoreFile == null)
			this.serverSocket = IOH.openServerSocketWithReason(serverPort, "AmiClient");
		else
			this.serverSocket = IOH.openSSLServerSocketWithReason(serverPort, keystoreFile, keystorePassword.getPasswordString(), "AmiClient");
		this.runner = new Runner();
		this.running = true;
		this.factory = factory;
		this.thread = new Thread(runner, "AmiClientServerSocket");
		this.thread.start();
	}

	@Override
	public void close() throws IOException {
		if (this.running) {
			this.running = false;
			IOH.close(this.serverSocket);
			this.thread.interrupt();
		}
	}
}
