package com.f1.ami.webbalancer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import com.f1.ami.amicommon.AmiProcessStatsLogger;
import com.f1.ami.webbalancer.serverselector.AmiWebBalancerServerSelectorPlugin;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.IOH.TrustAllManager;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SafeFile;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Long;

public class AmiWebBalancerServer implements Runnable {

	private static final String ON_FAILURE_CONTINUE = "CONTINUE";
	private static final String ON_FAILURE_BREAK = "BREAK";

	@Override
	public void run() {
		try {
			for (;;) {
				purgeSessions();
				OH.sleep(this.checkFilesFrequency);
			}
		} catch (Throwable t) {
			LH.warning(log, "Unknown error: ", t);
		}
	}

	private class AmiLogger implements Runnable {

		@Override
		public void run() {
			logAmi();
		}

	}

	private static final Logger log = LH.get();
	private final HashMap<String, AmiWebBalancerClientSession> clients = new HashMap<String, AmiWebBalancerClientSession>();
	//	private final Map<String, AmiWebBalancerServerInstance> sticky = new LinkedHashMap<String, AmiWebBalancerServerInstance>();
	private final List<AmiWebBalancerServerInstance> availableServers = new ArrayList<AmiWebBalancerServerInstance>();
	private final HashMap<String, AmiWebBalancerServerInstance> availableServersByNames = new HashMap<String, AmiWebBalancerServerInstance>();

	private final AtomicInteger nextConnectionId = new AtomicInteger(0);
	private final AtomicInteger nextSessionId = new AtomicInteger(0);
	private final Executor threadPoolExecutor;
	private final long serverAliveCheckMillis;
	private final long sessionTimeoutMs;

	private SafeFile stickyFile;

	private CachedFile serversFile;
	private String serversFileText;

	private long testHttpPeriod;
	private Long closedClientsConnectionsCount = new Mutable.Long(0);
	private Long closedClientsBytesToClient = new Mutable.Long(0);
	private Long closedClientsBytesToServer = new Mutable.Long(0);
	private final List<AmiWebBalancerRouteRule> rules = new ArrayList<AmiWebBalancerRouteRule>();
	private long checkFilesFrequency;
	final private SSLSocketFactory sslSocketFactory;
	final private String testUrl;
	final private Long requestCount = new Mutable.Long(0);
	final private Long requestNanos = new Mutable.Long(0);
	final private int testPort;
	final private boolean testUrlIsSecure;
	final private AmiWebBalancerServerSelectorPlugin serverSelector;
	private static final String TLS = System.getProperty("f1.sslcontext.default", "TLSv1.2");

	public AmiWebBalancerServer(Executor threadPoolExecutor, CachedFile serversFileCache, File sessionsFileCache, long checkFilesFrequency, long keepAliveCheckMillis,
			long sessionTimeoutMs, String testUrl, long testPeriod, int testPort, boolean testUrlIsSecure, AmiWebBalancerServerSelectorPlugin serverSelector) throws Exception {
		this.testUrl = testUrl;
		this.testHttpPeriod = testPeriod;
		this.testPort = testPort;
		this.testUrlIsSecure = testUrlIsSecure;
		this.serverSelector = serverSelector;
		this.threadPoolExecutor = threadPoolExecutor;
		this.checkFilesFrequency = checkFilesFrequency;
		this.stickyFile = new SafeFile(sessionsFileCache);
		this.serversFile = serversFileCache;
		this.serverAliveCheckMillis = keepAliveCheckMillis;
		this.sessionTimeoutMs = sessionTimeoutMs;
		final SSLContext sslContext = SSLContext.getInstance(TLS);
		sslContext.init(null, TrustAllManager.getArrayInstance(), new java.security.SecureRandom());
		this.sslSocketFactory = sslContext.getSocketFactory();
		parseServerFile(10000);
		parseStickyFile();
		AmiProcessStatsLogger.INSTANCE.addLogger(new AmiLogger());
		Thread t = new Thread(this, "WBPURGE");
		t.setDaemon(true);
		t.start();
	}
	private void parseStickyFile() throws IOException {
		String text = this.stickyFile.getText();
		LH.info(log, "Processing session file ", IOH.getFullPath(this.stickyFile.getFile()));
		//		this.sticky.clear();
		if (SH.is(text)) {
			for (String line : SH.splitLines(text)) {
				String client = SH.trim(SH.beforeFirst(line, '=', null));
				String server = SH.trim(SH.afterFirst(line, '=', null));
				if (SH.is(client) && SH.is(server)) {
					AmiWebBalancerRouteRule rule = findRule(client);
					if (rule != null && rule.getTargets().contains(server)) {
						AmiWebBalancerServerInstance si = this.availableServersByNames.get(server);
						if (si != null) {
							AmiWebBalancerClientSession cs = new AmiWebBalancerClientSession(this, nextSessionId.incrementAndGet(), client);
							cs.recovery(si);
							this.clients.put(client, cs);
							LH.info(log, "Recovered session information: ", client, " -> ", server);
						} else {
							LH.info(log, "Ignoring entry in ", IOH.getFullPath(this.stickyFile.getFile()), " for client/server with down server: ", line);
						}
					} else {
						LH.info(log, "Ignoring entry in ", IOH.getFullPath(this.stickyFile.getFile()), " for client/server that doesn't match rules: ", line);
					}
				}
			}
		}
	}
	protected void accept(Socket socket, int serverPort) throws IOException {
		InetSocketAddress socketAddress = (InetSocketAddress) socket.getRemoteSocketAddress();
		String address = socketAddress.getAddress().getHostAddress();
		if ("0:0:0:0:0:0:0:1".equals(address))
			address = "127.0.0.1";//hack to keep ip v4... This might change at some point
		AmiWebBalancerClientSession session = clients.get(address);
		boolean isNew = false;
		if (session == null) {
			synchronized (clients) {
				session = clients.get(address);
				if (session == null) {
					session = new AmiWebBalancerClientSession(this, nextSessionId.incrementAndGet(), address);
					isNew = true;
					clients.put(address, session);
				}
			}
		}
		if (isNew)
			LH.info(log, "Received First connection from ", session.getClientAddress(), " on port ", serverPort);
		else
			LH.info(log, "Received Additional connection from ", session.getClientAddress(), " on port ", serverPort);
		session.accept(socket, nextConnectionId.incrementAndGet());
	}

	public Executor getExecutor() {
		return this.threadPoolExecutor;
	}

	public AmiWebBalancerServerInstance resolveServerHostPort(AmiWebBalancerClientSession session) {
		LH.info(log, "resolving session-", session.getSessionId(), " for ", session.getClientAddress());
		synchronized (this) {
			AmiWebBalancerServerInstance t = session.getCurrentServerHostPort();
			parseServerFile(100);
			String clientAddress = session.getClientAddress();
			AmiWebBalancerRouteRule rule = findRule(clientAddress);
			if (rule == null) {
				LH.info(log, "Could not resolve server for " + clientAddress, " because no rules match");
				return null;
			}
			AmiWebBalancerServerInstance r = rule.getBestTarget();
			if (r == null) {
				LH.info(log, "Could not resolve server for " + clientAddress, " because no servers are UP for rule ", rule.getRule());
				return null;
			}
			LH.info(log, "Replaced entry from " + IOH.getFullPath(this.stickyFile.getFile()), ": ", clientAddress, "=", r.getHostPort());
			return r;
		}

	}
	synchronized void writeStickyFile() {
		try {
			StringBuilder sb = new StringBuilder();
			for (Entry<String, AmiWebBalancerClientSession> i : this.clients.entrySet()) {
				AmiWebBalancerServerInstance t = i.getValue().getCurrentServerHostPort();
				if (t != null)
					sb.append(i.getKey()).append('=').append(t.getHostPort()).append('\n');
			}
			String stickyFileText = sb.toString();
			this.stickyFile.setText(stickyFileText);
			//			IOH.writeText(this.stickyFile, stickyFileText);
			LH.info(log, "Wrote " + IOH.getFullPath(this.stickyFile.getFile()));
		} catch (IOException e) {
			LH.warning(log, "Critical error writing to ", IOH.getFullPath(this.stickyFile.getFile()), e);
		}
	}

	private void parseServerFile(long pauseTime) {
		String text = this.serversFile.getData().getText();
		if (OH.ne(this.serversFileText, text)) {
			LH.info(log, "Processing servers file ", IOH.getFullPath(this.serversFile.getFile()));
			if (SH.is(text)) {
				Set<String> newNames = new HashSet<String>();
				this.rules.clear();
				int linenum = 0;
				for (String line : SH.splitLines(text)) {
					linenum++;
					line = SH.beforeFirst(line, '#');
					if (SH.isnt(line))
						continue;
					String[] parts = SH.split(';', line);
					if (parts.length >= 3) {
						String ruleStr = SH.trim(parts[0]);
						String targetsStr = SH.trim(parts[1]);
						String followupAction = parts.length >= 2 ? SH.trim(parts[2]) : null;
						if (SH.is(ruleStr) && SH.is(targetsStr)) {
							if (SH.isnt(followupAction) || ON_FAILURE_CONTINUE.equalsIgnoreCase(followupAction) || ON_FAILURE_BREAK.equalsIgnoreCase(followupAction)) {
								AmiWebBalancerRouteRule rule = new AmiWebBalancerRouteRule(this, ruleStr, targetsStr, ON_FAILURE_CONTINUE.equalsIgnoreCase(followupAction));
								rules.add(rule);
								for (String s : rule.getTargets())
									newNames.add(s);
								continue;
							}
						}
					}
					LH.warning(log, "Skipping invalid entry in ", IOH.getFullPath(this.serversFile.getFile()), " at line ", linenum, ": ", line);
				}
				Set<String> added = CH.comm(this.availableServersByNames.keySet(), newNames, false, true, false);
				Set<String> removed = CH.comm(this.availableServersByNames.keySet(), newNames, true, false, false);
				if (removed.size() > 0) {
					LH.info(log, "Servers Removed from list: ", removed);
					for (String s : removed) {
						AmiWebBalancerServerInstance rem = this.availableServersByNames.remove(s);
						this.availableServers.remove(rem);
					}
				}
				if (added.size() > 0) {
					List<AmiWebBalancerServerInstance> addedServers = new ArrayList<AmiWebBalancerServerInstance>(added.size());
					for (String s : added) {
						AmiWebBalancerServerInstance server = new AmiWebBalancerServerInstance(this, s);
						this.availableServersByNames.put(s, server);
						this.availableServers.add(server);
						addedServers.add(server);
					}
					LH.info(log, "Waiting for connection status on new servers: ", addedServers, "");
					for (AmiWebBalancerServerInstance n : addedServers)
						n.pauseTillActiveKnown(pauseTime);
					LH.info(log, "Done Waiting for connection status");
				}
				for (AmiWebBalancerRouteRule i : this.rules)
					i.bindTargets(this.availableServersByNames);
				List<AmiWebBalancerClientSession> toRemove = new ArrayList<AmiWebBalancerClientSession>();
				for (AmiWebBalancerClientSession i : this.clients.values()) {
					AmiWebBalancerRouteRule rule = findRule(i.getClientAddress());
					AmiWebBalancerServerInstance shp = i.getCurrentServerHostPort();
					if (rule == null || shp == null || !rule.getTargets().contains(shp.getHostPort()))
						toRemove.add(i);
				}
				if (!toRemove.isEmpty()) {
					for (AmiWebBalancerClientSession i : toRemove) {
						LH.warning(log, "Rule changes so removing binding: ", i.getClientAddress(), " ==> ", i.getCurrentServerHostPort());
						i.resetSession();
					}
					writeStickyFile();
				}
			}
		}
		this.serversFileText = text;
	}
	private AmiWebBalancerRouteRule findRule(String clientAddress) {
		for (int i = 0; i < this.rules.size(); i++) {
			AmiWebBalancerRouteRule rule = this.rules.get(i);
			if (rule.matches(clientAddress)) {
				if (!rule.hasTargetThatsAlive() && rule.shouldContinue())
					continue;
				return rule;
			}
		}
		return null;

	}
	public long getServerAliveCheckMillis() {
		return this.serverAliveCheckMillis;
	}
	private void purgeSessions() {
		synchronized (this) {
			parseServerFile(100);
			//			parseStickyFile();
			long now = EH.currentTimeMillis();
			List<String> toRemove = null;
			for (Entry<String, AmiWebBalancerClientSession> i : this.clients.entrySet()) {
				long it = i.getValue().getIdleTime();
				if (it == -1)
					continue;
				if (now - it > sessionTimeoutMs) {
					if (toRemove == null)
						toRemove = new ArrayList<String>();
					toRemove.add(i.getKey());
				}
			}
			if (toRemove != null) {
				for (String i : toRemove) {
					LH.info(log, "Removing stale IDLE session: ", i);
					AmiWebBalancerClientSession client = this.clients.remove(i);
					client.getConnectionStats(this.closedClientsConnectionsCount, this.closedClientsBytesToClient, this.closedClientsBytesToServer, this.requestCount,
							this.requestNanos);
				}
				writeStickyFile();
			}
		}
	}

	private static final Logger amilog = Logger.getLogger("AMI_STATS.CENTER");

	public void getConnections(List<AmiWebBalancerConnection> sink) {
		synchronized (this) {
			for (AmiWebBalancerClientSession i : this.clients.values())
				i.getConnections(sink);
		}
	}
	public void logAmi() {

		int clientsCountActive = 0, clientsCountIdle = 0;
		int serverCountUp = 0, serverCountDown = 0;
		long activeConnectionsCount = 0;
		long bytesToServer = this.closedClientsBytesToServer.value;
		long bytesToClient = this.closedClientsBytesToClient.value;
		int stickyCount = 0;
		Mutable.Long ac = new Mutable.Long();
		Mutable.Long btc = new Mutable.Long();
		Mutable.Long bts = new Mutable.Long();
		Mutable.Long cnt = new Mutable.Long();
		Mutable.Long nan = new Mutable.Long();
		synchronized (this) {
			for (AmiWebBalancerClientSession i : this.clients.values()) {
				if (i.getIdleTime() == -1) {
					clientsCountIdle++;
				} else {
					clientsCountActive++;
				}
				ac.value = btc.value = bts.value = cnt.value = nan.value = 0L;
				i.getConnectionStats(ac, btc, bts, cnt, nan);
				activeConnectionsCount += ac.value;
				bytesToClient += btc.value;
				bytesToServer += bts.value;
				long it = i.getIdleTime();

				AmiProcessStatsLogger.log(amilog, "AmiWebBalancerClient", //
						"address", i.getClientAddress(), //
						"startTime", i.getStartTime(), //
						"IdleSince", it == -1L ? null : it, //
						"target", i.getCurrentServerHostPort(), //
						"connections", ac.value, //
						"bytesToServer", bts.value, //
						"bytesToClient", btc.value//
				);

			}

			//			stickyCount = this.sticky.size();
			for (AmiWebBalancerServerInstance i : this.availableServers) {
				if (i.isAlive())
					serverCountUp++;
				else
					serverCountDown++;
				ac.value = btc.value = bts.value = cnt.value = nan.value = 0L;
				i.getConnectionStats(ac, btc, bts, cnt, nan);
				AmiProcessStatsLogger.log(amilog, "AmiWebBalancerServer", //
						"address", i.getHostPort(), //
						"activeSessions", i.getActiveSessionsCount(), //
						"status", i.isAlive() ? "UP" : "DOWN", //
						"bytesToServer", bts, //
						"avgResponseMillis", (cnt.value == 0 ? Double.NaN : nan.value / cnt.value / 1000000d) //
				);
			}
		}
		AmiProcessStatsLogger.log(amilog, "AmiWebBalancerStats", //
				"clientsActive", clientsCountActive, //
				"clientsIdle", clientsCountIdle, //
				"serversUp", serverCountUp, //
				"serversDown", serverCountDown, //
				"activeConnections", activeConnectionsCount, //
				"totBytesToServers", bytesToServer, //
				"totBytesToClients", bytesToClient, //
				"stickyCount", stickyCount);
	}

	public String getTestUrl() {
		return this.testUrl;
	}

	public long getTestHttpMethodPeriod() {
		return this.testHttpPeriod;
	}
	public Socket newSocket(Boolean isSecure, String host, int port) throws UnknownHostException, IOException {
		return isSecure ? this.sslSocketFactory.createSocket(host, port) : new Socket(host, port);
	}

	public ArrayList<AmiWebBalancerServerInstance> getAvailableServers() {
		synchronized (this) {
			return new ArrayList<AmiWebBalancerServerInstance>(this.availableServers);
		}
	}
	public int getTestPort() {
		return this.testPort;
	}
	public boolean getTestUrlIsSecure() {
		return this.testUrlIsSecure;
	}
	public AmiWebBalancerServerSelectorPlugin getServerSelector() {
		return serverSelector;
	}
}
