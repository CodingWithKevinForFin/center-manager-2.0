package com.f1.ami.webbalancer;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.f1.utils.ServerSocketAcceptor;
import com.f1.utils.ServerSocketEntitlements;

public class AmiWebBalancerClientAcceptor extends ServerSocketAcceptor {

	private AmiWebBalancerServer server;

	public AmiWebBalancerClientAcceptor(AmiWebBalancerServer server, String bindAddr, ServerSocketEntitlements entitlments, int port) {
		super(bindAddr, entitlments, port);
		this.server = server;
	}

	public AmiWebBalancerClientAcceptor(AmiWebBalancerServer server, String bindAddr, ServerSocketEntitlements entitlments, int port, File keystore, String keystorePassword) {
		super(bindAddr, entitlments, port, keystore, keystorePassword);
		this.server = server;
	}

	@Override
	protected void accept(Socket socket) throws IOException {
		server.accept(socket, super.getServerLocalPort());
	}

}
