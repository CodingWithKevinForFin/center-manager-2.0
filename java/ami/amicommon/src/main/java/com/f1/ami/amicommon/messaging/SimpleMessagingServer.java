package com.f1.ami.amicommon.messaging;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executor;

import com.f1.utils.OfflineConverter;
import com.f1.utils.ServerSocketAcceptor;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;

public class SimpleMessagingServer extends ServerSocketAcceptor {

	final private SimpleMessagingServerConnectionHandlerFactory factory;
	final private OfflineConverter converter;
	final private String description;
	final private Executor executor;
	final private int version;

	public SimpleMessagingServer(String description, String portBindAddr, ServerSocketEntitlements ssEntitlements, int port, SimpleMessagingServerConnectionHandlerFactory factory,
			OfflineConverter offlineConverter, Executor executor, int version) {
		super(portBindAddr, ssEntitlements, port);
		this.version = version;
		this.description = description;
		this.converter = offlineConverter;
		this.factory = factory;
		this.executor = executor;
	}
	public SimpleMessagingServer(String description, String portBindAddr, ServerSocketEntitlements ssEntitlements, int sslPort, SimpleMessagingServerConnectionHandlerFactory factory,
			OfflineConverter offlineConverter, Executor executor, int version, File keystore, String keystorePass) {
		super(portBindAddr, ssEntitlements, sslPort, keystore, keystorePass);
		this.version = version;
		this.description = description;
		this.converter = offlineConverter;
		this.factory = factory;
		this.executor = executor;
	}
	public String getDescription() {
		return getIsSecure() ? ("Secure " + description) : description;
	}

	@Override
	protected void accept(Socket socket) throws IOException {
		executor.execute(new SimpleMessagingServerConnection((ObjectToByteArrayConverter) this.converter, socket,
				this.factory.newHandler(socket.getRemoteSocketAddress().toString()), this.description, this.version));
	}

}
