package com.f1.persist.sinks;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import com.f1.persist.reader.TransactionalPersistReader;
import com.f1.utils.IOH;
import com.f1.utils.LH;

public class PersistClientSocket implements Runnable {
	private static final Logger log = Logger.getLogger(PersistClientSocket.class.getName());
	final private Socket socket;
	final private TransactionalPersistReader persist;
	final private String hostName;
	final private int port;
	private boolean started = false;
	private int eventsCount;

	public PersistClientSocket(String hostName, int port, TransactionalPersistReader persist, boolean autoStart) throws IOException {
		this.socket = IOH.openClientSocketWithReason(hostName, port, "Persistence");
		this.persist = persist;
		this.hostName = hostName;
		this.port = port;
		if (autoStart)
			start();
	}

	public void start() {
		if (started)
			throw new RuntimeException("already started");
		started = true;
		new Thread(this, this.toString()).start();

	}

	@Override
	public void run() {
		int j = 1;
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			OutputStream out = socket.getOutputStream();
			while (started) {
				if (!persist.consumeTransaction(in))
					break;
				out.write((byte) (j % 100));
				j++;
				int count = persist.pumpTransaction();
				if (count == 0) {
				LH.info(log,"closing socket " , this);
					break;
				}

			}
		} catch (Exception e) {
			LH.warning(log, "error reading ", this, ". Read ", eventsCount, " event(s)", e);
		}
		IOH.close(socket);
	}

	public String toString() {
		return getClass().getSimpleName() + " " + hostName + ":" + port;
	}
}
