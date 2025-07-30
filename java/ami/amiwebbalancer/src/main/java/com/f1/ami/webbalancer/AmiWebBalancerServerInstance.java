package com.f1.ami.webbalancer;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.webbalancer.serverselector.AmiWebBalancerServerTestUrlResults;
import com.f1.http.HttpUtils;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.mutable.Mutable.Long;
import com.f1.utils.structs.Tuple3;

public class AmiWebBalancerServerInstance implements Runnable {

	private static final Logger log = LH.get();
	public static final long MIN_TEST_PERIOD = 1000;
	private final String host;
	private final int port;

	private final IdentityHashSet<AmiWebBalancerClientSession> activeSessions = new IdentityHashSet<AmiWebBalancerClientSession>();
	//	private final AtomicInteger activeSessionCount = new AtomicInteger(0);
	private final AmiWebBalancerServer server;
	private final String hostPort;
	private Boolean isAlive;
	private Boolean isSecure;
	final private String testUrl;
	private long lastActiveTime = -1;
	private int portForTest;
	private boolean isSecureForTest;
	private AmiWebBalancerServerTestUrlResults stats;

	public AmiWebBalancerServerInstance(AmiWebBalancerServer server, String hostport) {
		Tuple3<Boolean, String, Integer> hp = parseHostPort(hostport);
		if (hp == null)
			throw new RuntimeException("Bad url: " + hostport);
		this.server = server;
		this.isSecure = hp.getA();
		this.host = hp.getB();
		this.port = hp.getC();
		this.hostPort = hostport;
		if (server.getTestPort() != -1) {
			this.portForTest = server.getTestPort();
			this.isSecureForTest = server.getTestUrlIsSecure();
		} else {
			this.portForTest = this.port;
			this.isSecureForTest = this.isSecure;
		}
		this.isSecureForTest = this.isSecure;
		Thread thread = new Thread(this, "WEBBALMON-" + hostport);
		thread.setDaemon(true);
		thread.start();
		this.testUrl = HttpUtils.buildUrl(isSecureForTest, this.host, this.portForTest, server.getTestUrl(), null);
	}

	@Override
	public String toString() {
		return this.hostPort;
	}

	public String getHost() {
		return host;
	}
	public int getPort() {
		return port;
	}

	public boolean isAlive() {
		return Boolean.TRUE.equals(isAlive);
	}

	public int getActiveSessionsCount() {
		synchronized (activeSessions) {
			return this.activeSessions.size();
		}
	}

	public int incrementActiveSessionsCount(AmiWebBalancerClientSession i) {
		synchronized (activeSessions) {
			this.activeSessions.add(i);
			return this.activeSessions.size();
		}
	}
	public int decrementActiveSessionsCount(AmiWebBalancerClientSession i) {
		synchronized (activeSessions) {
			this.activeSessions.remove(i);
			return this.activeSessions.size();
		}
	}

	public String getHostPort() {
		return hostPort;
	}

	public static Tuple3<Boolean, String, Integer> parseHostPort(String url) {
		if (SH.isnt(url))
			return null;
		String protocol = SH.beforeFirst(url, "://", null);
		String s = SH.afterFirst(url, "://", url);
		boolean secure = "https".equalsIgnoreCase(protocol);
		int i = s.indexOf(':');
		if (i < 1 || i == s.length() - 1)
			return null;
		try {
			String host = s.substring(0, i);
			int port = SH.parseInt(s, i + 1, s.length(), 10);
			return new Tuple3<Boolean, String, Integer>(secure, host, port);
		} catch (Exception e) {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Parse error for ", url, e);
			return null;
		}
	}

	@Override
	public void run() {

		for (;;) {
			this.stats = testKeepAlive();
			if (stats != null && stats.isAlive()) {
				setIsAlive(true);
				for (;;) {
					OH.sleep(server.getServerAliveCheckMillis());
					this.stats = testKeepAlive();
					if (stats == null || !stats.isAlive())
						break;
				}
				setIsAlive(false);
			} else
				OH.sleep(server.getServerAliveCheckMillis());
		}
	}
	private AmiWebBalancerServerTestUrlResults testKeepAlive() {
		Socket socket = null;
		try {
			socket = this.newTestSocket();
			AmiWebBalancerFastHttpRequestResponse rr = new AmiWebBalancerFastHttpRequestResponse(socket.getOutputStream(), socket.getInputStream());
			rr.setRequestPath(this.server.getTestUrl());
			rr.setRequestMethod("GET");
			rr.writeRequest();
			rr.readResponse();
			try {
				return this.server.getServerSelector().processHealthStats(this, rr);
			} catch (Exception e) {
				LH.warning(log, e);
				return null;
			}
		} catch (Throwable e) {
			return null;
		} finally {
			IOH.close(socket);
		}

	}

	private void setIsAlive(boolean b) {
		if (b)
			this.lastActiveTime = System.currentTimeMillis();
		if (this.isAlive == null) {
			this.isAlive = b;
			synchronized (this) {
				this.notifyAll();
			}
			if (b)
				LH.warning(log, "Server Connection established, Marked as UP: ", this.hostPort);
			else
				LH.warning(log, "Server Connection failed, Marked as DOWN: ", this.hostPort);
			return;
		}
		if (this.isAlive == b)
			return;
		this.isAlive = b;
		if (b)
			LH.warning(log, "Server Connection reestablished, Marked as UP: ", this.hostPort);
		else
			LH.warning(log, "Server Connection lost, Marked as DOWN: ", this.hostPort);
	}

	public void pauseTillActiveKnown(long timeout) {
		if (isAlive == null)
			try {
				synchronized (this) {
					this.wait(timeout);
				}
			} catch (InterruptedException e) {
				if (log.isLoggable(Level.FINE))
					LH.fine(log, "Exception for ", this.hostPort, e);
			}

	}

	public AmiWebBalancerServer getServer() {
		return this.server;
	}

	public Socket newSocket() throws UnknownHostException, IOException {
		return server.newSocket(this.isSecure, this.host, this.port);
	}
	public Socket newTestSocket() throws UnknownHostException, IOException {
		return server.newSocket(this.isSecureForTest, this.host, this.portForTest);
	}

	public boolean isSecure() {
		return this.isSecure;
	}

	public long getLastActiveTime() {
		return this.lastActiveTime;
	}

	public void getConnectionStats(Long connectionsCount, Long bytesToClient, Long bytesToServer, Long requestsCount, Long RequestNanos) {
		synchronized (activeSessions) {
			for (AmiWebBalancerClientSession i : this.activeSessions) {
				i.getConnectionStats(connectionsCount, bytesToClient, bytesToServer, requestsCount, RequestNanos);
			}
		}
	}

	public void getSessions(List<AmiWebBalancerConnection> sink) {
		synchronized (activeSessions) {
			for (AmiWebBalancerClientSession i : this.activeSessions) {
				i.getConnections(sink);
			}
		}
	}

	public AmiWebBalancerServerTestUrlResults getTestUrlStats() {
		return this.stats;
	}

}
