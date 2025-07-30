package com.f1.persist.sinks;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import com.f1.persist.PersistSink;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public class PersistServerSocket implements Runnable {
	private static final Logger log = Logger.getLogger(PersistServerSocket.class.getName());

	private TransactionalPersistWriterFactory factory;
	private ServerSocket serverSocket;
	private boolean started;

	private boolean asyncConnections;

	public PersistServerSocket(int socket, boolean asyncConnections, TransactionalPersistWriterFactory factory) throws IOException {
		this.factory = factory;
		this.serverSocket = IOH.openServerSocketWithReason(socket, "Persistent Mirrors");
		this.asyncConnections = asyncConnections;
	}

	public void start() {
		if (started)
			throw new RuntimeException("already started");
		started = true;
		new Thread(this, "Persistent Server Socket").start();
	}

	@Override
	public void run() {
		while (started) {
			try {
				Socket socket = serverSocket.accept();
				LH.info(log, "Adding sink and sending snapshot to ", socket);
				PersistSink sink = new ServerSocketPersistSink(socket.getOutputStream(), socket.getInputStream());
				factory.addSink(sink, asyncConnections, true);
				LH.info(log, "Completed snapshot to ", socket);
			} catch (IOException e) {
				LH.warning(log, "Error accepting connection", e);
			}
		}
	}

}
