/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.msgdirect;

import com.f1.msg.impl.BasicMsgTopicConfiguration;
import com.f1.utils.AH;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;

public class MsgDirectTopicConfiguration extends BasicMsgTopicConfiguration {

	public static final int NO_PORT = -1;
	private int[] sslPorts = new int[] { NO_PORT };

	final private int[] ports;
	final private String[] hosts;
	private String serverBindAddress;
	private ServerSocketEntitlements serverSocketEntitlements;

	public MsgDirectTopicConfiguration(String name, String hosts[], int[] ports, String topic) {
		super(name, topic);

		if (AH.isEmpty(hosts) || AH.isEmpty(ports))
			throw new IllegalStateException("hosts or ports are empty");

		if (hosts.length != ports.length)
			throw new IllegalStateException("# of hosts != # of ports");

		this.ports = ports;
		this.hosts = hosts;
	}
	public MsgDirectTopicConfiguration(String name, String hosts[], int[] ports) {
		this(name, hosts, ports, name);
	}
	public MsgDirectTopicConfiguration(String name, String host, int port, String topic) {
		this(name, AH.a(host), new int[] { port }, topic);
	}
	public MsgDirectTopicConfiguration(String name, int port, String topic) {
		this(name, null, port, topic);
	}

	public MsgDirectTopicConfiguration(String name, String host, int port) {
		this(name, host, port, name);
	}

	public MsgDirectTopicConfiguration(String name, int port) {
		this(name, null, port, name);
	}

	public void setSslPort(int sslPort) {
		if (this.sslPorts == null)
			this.sslPorts = new int[1];

		this.sslPorts[0] = sslPort;
	}

	public void setSslPorts(int[] sslPorts) {
		if (AH.isntEmpty(sslPorts)) {
			if (sslPorts.length != hosts.length)
				throw new IllegalStateException("# of ssl ports != # of hosts");

			this.sslPorts = sslPorts;
		}
	}

	public int[] getSslPorts() {
		return sslPorts;
	}

	public int[] getPorts() {
		return ports;
	}

	public String[] getHosts() {
		return hosts;
	}

	public boolean isServer() {
		return AH.isEmpty(hosts) || SH.isnt(hosts[0]);
	}

	public boolean isSsl() {
		return AH.isntEmpty(sslPorts) && sslPorts[0] != NO_PORT;
	}
	public String getServerBindAddress() {
		return this.serverBindAddress;
	}
	public MsgDirectTopicConfiguration setServerBindAddress(String serverBindAddress) {
		this.serverBindAddress = serverBindAddress;
		return this;
	}
	public ServerSocketEntitlements getServerSocketEntitlements() {
		return this.serverSocketEntitlements;
	}
	public MsgDirectTopicConfiguration setServerSocketEntitlements(ServerSocketEntitlements serverSocketEntitlements) {
		this.serverSocketEntitlements = serverSocketEntitlements;
		return this;
	}
}
