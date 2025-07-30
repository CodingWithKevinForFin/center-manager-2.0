package com.f1.ami.webbalancer;

import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.mutable.Mutable.Long;

public class AmiWebBalancerClientSession {

	private static final Logger log = LH.get();
	final private AmiWebBalancerServer server;
	private AmiWebBalancerServerInstance serverHostPort;
	final private String clientAddress;
	final private int sessionId;
	final private LinkedHashSet<AmiWebBalancerConnection> connections = new LinkedHashSet<AmiWebBalancerConnection>();
	private volatile long idleTime = -1;
	private long closedConnectionsBytesToClient = 0;
	private long closedConnectionsBytesToServer = 0;
	private long startTime;

	public AmiWebBalancerClientSession(AmiWebBalancerServer server, int sessionId, String address) {
		this.server = server;
		this.sessionId = sessionId;
		this.clientAddress = address;
		this.startTime = EH.currentTimeMillis();
	}

	public void accept(Socket socket, int connectionId) {
		this.idleTime = -1;
		AmiWebBalancerConnection t = new AmiWebBalancerConnection(this, connectionId, socket);

		server.getExecutor().execute(t);
	}
	public AmiWebBalancerServerInstance getCurrentServerHostPort() {
		return this.serverHostPort;
	}

	public AmiWebBalancerServerInstance getOrResolveServerHostPort() {
		AmiWebBalancerServerInstance shp = this.serverHostPort;
		if (shp == null || !shp.isAlive()) {
			synchronized (this) {
				shp = this.serverHostPort;
				if (shp == null || !shp.isAlive()) {
					serverHostPort = shp = server.resolveServerHostPort(this);
					if (serverHostPort != null)
						server.writeStickyFile();
				}
			}
		}
		return shp;
	}

	public void onClosed(AmiWebBalancerConnection c) {
		synchronized (connections) {
			connections.remove(c);
			if (connections.size() == 0) {
				this.idleTime = EH.currentTimeMillis();
				LH.info(log, "All connections closed for client ", clientAddress, " marking session as IDLE");
				if (this.serverHostPort != null)
					this.serverHostPort.decrementActiveSessionsCount(this);
			} else
				LH.info(log, "Connections close for client ", clientAddress, ", other connections still active though");
			this.closedConnectionsBytesToClient += c.getBytesToClient();
			this.closedConnectionsBytesToServer += c.getBytesToServer();
		}
	}

	public void onOpened(AmiWebBalancerConnection c) {
		if (this.idleTime != -1)
			LH.info(log, "Connections active for client ", clientAddress, " marking session as ACTIVE");
		this.idleTime = -1;
		synchronized (connections) {
			connections.add(c);
			if (this.serverHostPort != null && connections.size() == 1)
				this.serverHostPort.incrementActiveSessionsCount(this);
		}
	}

	public String getClientAddress() {
		return this.clientAddress;
	}

	public int getSessionId() {
		return this.sessionId;
	}

	public long getIdleTime() {
		return this.idleTime;
	}

	public void resetSession() {
		synchronized (connections) {
			this.serverHostPort = null;
			for (AmiWebBalancerConnection i : CH.l(connections))
				i.close();
		}
	}

	public void getConnectionStats(Long connectionsCount, Long bytesToClient, Long bytesToServer, Long requests, Long requestNanos) {
		synchronized (this.connections) {
			bytesToClient.value += this.closedConnectionsBytesToClient;
			bytesToServer.value += this.closedConnectionsBytesToServer;
			connectionsCount.value += this.connections.size();
			for (AmiWebBalancerConnection i : this.connections) {
				bytesToClient.value += i.getBytesToClient();
				bytesToServer.value += i.getBytesToServer();
				requests.value += i.getRequestCounts();
				requestNanos.value += i.getRequestNanos();
			}
		}
	}

	public long getStartTime() {
		return this.startTime;
	}

	public void recovery(AmiWebBalancerServerInstance si) {
		this.serverHostPort = si;
		this.idleTime = System.currentTimeMillis();
	}

	public void getConnections(List<AmiWebBalancerConnection> sink) {
		synchronized (this.connections) {
			sink.addAll(connections);
		}
	}

}
