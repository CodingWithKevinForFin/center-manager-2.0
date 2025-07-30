package com.f1.ami.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientListener;
import com.f1.ami.amicommon.msg.AmiRelayOnConnectRequest;
import com.f1.base.ObjectGenerator;
import com.f1.base.Password;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.codegen.impl.DummyCodeCompiler;
import com.f1.container.Container;
import com.f1.container.impl.BasicContainer;
import com.f1.utils.BasicIdeableGenerator;
import com.f1.utils.SH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.structs.IntKeyMap;

public class AmiCenterClient {
	final private Container bc;
	final private IntKeyMap<AmiCenterClientConnection> centersById = new IntKeyMap<AmiCenterClientConnection>();
	final private Map<String, AmiCenterClientConnection> centersByName = new HashMap<String, AmiCenterClientConnection>();
	final private String username;

	public AmiCenterClient(String username, Container container) {
		this.username = username;
		this.bc = container;
		ContainerBootstrap.registerMessagesInPackages(bc.getServices().getGenerator(), AmiRelayOnConnectRequest.class.getPackage());
	}

	/**
	 * 
	 * @param username
	 *            the name passed to the center in the "invokedBy" property, which is logged in the target center
	 * @param listener
	 *            receives callbacks
	 * @throws IOException
	 */
	public AmiCenterClient(String username) throws IOException {
		this.username = username;
		this.bc = new BasicContainer();
		CodeCompiler compiler = new DummyCodeCompiler(".");
		ObjectGenerator inner = new BasicCodeGenerator(compiler, true);
		this.bc.getServices().setGenerator(new BasicIdeableGenerator(inner));
		this.bc.getServices().setConverter(new ObjectToByteArrayConverter());
		bc.init();
		bc.start();
		ContainerBootstrap.registerMessagesInPackages(bc.getServices().getGenerator(), AmiRelayOnConnectRequest.class.getPackage());
	}

	public AmiCenterClientConnection connect(String name, String host, int port, byte[] secureToken, Password password, AmiCenterClientListener listener) throws IOException {
		AmiCenterDefinition r = new AmiCenterDefinition(nextCenterId(), name, host, port, null, secureToken, password);
		return connect(r, listener);
	}

	public AmiCenterClientConnection connect(String name, String host, int port, AmiCenterClientListener listener) throws IOException {
		AmiCenterDefinition r = new AmiCenterDefinition(nextCenterId(), name, host, port, null, null, null);
		return connect(r, listener);
	}

	synchronized public AmiCenterClientConnection connect(AmiCenterDefinition center, AmiCenterClientListener listener) {
		if (centersById.containsKey(center.getId()))
			throw new RuntimeException("Center ID already exists: " + center.getId());
		if (centersByName.containsKey(center.getName()))
			throw new RuntimeException("Center Name already exists: " + center.getName());

		AmiCenterClientConnection c = new AmiCenterClientConnection(this, center, listener);
		this.centersById.put(center.getId(), c);
		this.centersByName.put(center.getName(), c);
		return c;
	}

	public AmiCenterClientConnection getClientConnection(String name) {
		return this.centersByName.get(name);
	}
	public AmiCenterClientConnection getClientConnection(byte centerId) {
		return this.centersById.get(centerId);
	}

	public String getUsername() {
		return this.username;
	}

	private byte nextCenterId() {
		return (byte) this.centersById.size();
	}

	public void subscribe(String name, Set<String> subscribe) {
		AmiCenterClientConnection t = this.getClientConnection(name);
		if (t == null)
			throw new RuntimeException("Connection not found: " + name + " (options are: [" + SH.join(',', this.centersByName.keySet()) + "])");
		t.subscribe(subscribe);
	}

	public Container getContainer() {
		return this.bc;
	}

	public void closeClientConnection(AmiCenterClientConnection connection) {
		this.centersById.removeOrThrow(connection.getCenterDef().getId());
		this.centersByName.remove(connection.getCenterDef().getName());
		connection.close();
	}

}
