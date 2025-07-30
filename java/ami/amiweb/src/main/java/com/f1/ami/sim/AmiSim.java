package com.f1.ami.sim;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientListener;
import com.f1.ami.sim.AmiSimField.IdSim;
import com.f1.ami.sim.AmiSimField.NumberSim;
import com.f1.ami.sim.AmiSimField.PatternSim;
import com.f1.ami.sim.AmiSimField.RefSim;
import com.f1.ami.sim.AmiSimField.TimeSim;
import com.f1.ami.sim.plugins.AmiSimPluginChildQty;
import com.f1.base.Clock;
import com.f1.utils.CH;
import com.f1.utils.CircularList;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.impl.BasicClock;
import com.f1.utils.structs.Tuple2;

public class AmiSim implements AmiClientListener {

	private static final Logger log = LH.get();
	public static final byte STATUS_NOT_CONNECTED = 0;
	public static final byte STATUS_CONNECTED = 1;
	public static final byte STATUS_RUNNING = 2;

	private static final int DEFAULT_PORT = AmiClient.DEFAULT_PORT;
	private static final String DEFAULT_HOST = "localhost";
	private static final String DEFAULT_APP_NAME = "AmiSimulator";
	private static final int MAX_RECENT_MESSAGE_BUFFER = 500000;

	private int remotePort = DEFAULT_PORT;
	private String remoteHost = DEFAULT_HOST;
	private String appName = DEFAULT_APP_NAME;
	private byte status = STATUS_NOT_CONNECTED;

	private AmiClient client;
	private Random random = new Random();
	private Thread sendThread;
	private volatile boolean isRunning = false;
	private Clock clock = new BasicClock();

	private Map<String, AmiSimType> types = new LinkedHashMap<String, AmiSimType>();
	private long periodMs = 1000;
	private Map<String, AmiSimPlugin> plugins = new LinkedHashMap<String, AmiSimPlugin>();
	volatile private long messagesCount;
	volatile private long objectsCount;
	private CircularList<String> recentMessages = new CircularList<String>(MAX_RECENT_MESSAGE_BUFFER);
	private Long seed;
	private long iterations = -1;
	private int clientStatusCount;

	public void addAmiSimType(AmiSimType type) {
		assertNotRunning();
		this.types.put(type.getId(), type);
	}

	public AmiSim() {
		this.client = new AmiClient();
		client.addListener(this);
	}

	public AmiSim setRemotePort(int port) {
		if (port == this.remotePort)
			return this;
		assertNotConnected();
		this.remotePort = port;
		return this;
	}
	public AmiSim setRemoteHost(String host) {
		if (OH.eq(host, this.remoteHost))
			return this;
		assertNotConnected();
		this.remoteHost = host;
		return this;
	}
	public AmiSim setAppName(String appName) {
		if (OH.eq(appName, this.appName))
			return this;
		assertNotConnected();
		this.appName = appName;
		return this;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public byte getStatus() {
		return status;
	}

	private void assertNotRunning() {
		if (status == STATUS_RUNNING)
			throw new IllegalStateException("running");
	}
	private void assertNotConnected() {
		if (status != STATUS_NOT_CONNECTED)
			throw new IllegalStateException("connected");
	}
	private void assertConnected() {
		if (status == STATUS_NOT_CONNECTED)
			throw new IllegalStateException("not connected");
	}

	private void connect() {
		assertNotConnected();
		client.start(remoteHost, remotePort, this.appName, AmiClient.ENABLE_QUIET);
		this.messagesCount++;
		status = STATUS_CONNECTED;
	}
	public void start() {
		if (status == STATUS_NOT_CONNECTED)
			connect();
		else if (status == STATUS_RUNNING)
			throw new IllegalStateException("Already running");
		else if (isRunning)
			throw new IllegalStateException("old run has not completed yet");

		status = STATUS_RUNNING;
		this.sendThread = new Thread(new Runner(), "Ami Simulator");
		this.sendThread.start();
	}

	public void stop() {
		if (status != STATUS_RUNNING)
			throw new IllegalStateException("Not running");
		status = STATUS_CONNECTED;
		isRunning = false;
		this.sendThread.interrupt();
	}

	private void pumpSimulator() {
		if (isRunning == true)
			throw new IllegalStateException("isRunning");
		isRunning = true;
		int iterations = 0;
		try {
			long now = clock.getNow();
			final AmiSimSession session = new AmiSimSession(client, random, now, this);
			outer: while (status == STATUS_RUNNING && !Thread.interrupted()) {
				session.resetChanges();
				long next = Long.MAX_VALUE;
				OH.sleep(periodMs);
				session.setNow(now);
				for (AmiSimType type : this.types.values())
					type.visit(client, session, periodMs);
				for (AmiSimPlugin plugin : this.plugins.values())
					plugin.visit(client, session, periodMs);
				objectsCount += session.getAdded().size() - session.getRemoved().size();
				iterations++;
				this.client.flush();
				if (this.iterations > 0 && iterations >= this.iterations) {
					try {
						this.client.flushAndWaitForReplys(10000);
					} catch (Exception e2) {
					}
					try {
						disconnect();
					} catch (Exception e2) {
					}
				}
			}
		} catch (Exception e) {
			LH.warning(log, "disconnecting due to error ", e);
			try {
				disconnect();
			} catch (Exception e2) {

			}
		} finally {
			isRunning = false;
		}
	}

	private class Runner implements Runnable {

		@Override
		public void run() {
			try {
				pumpSimulator();
			} catch (Exception e) {
				LH.warning(log, e);
				AmiSim.this.isRunning = false;
				if (AmiSim.this.status == STATUS_RUNNING)
					AmiSim.this.status = STATUS_CONNECTED;
			}
		}
	}

	public static void main(String a[]) {
		AmiSim sim = new AmiSim().setPeriodMs(50);
		sim.start();
	}

	public Clock getClock() {
		return clock;
	}

	public void setClock(Clock clock) {
		this.clock = clock;
	}

	public long getPeriodMs() {
		return periodMs;
	}

	public AmiSim setPeriodMs(long periodMs) {
		OH.assertGt(periodMs, 0);
		this.periodMs = periodMs;
		return this;
	}

	public long getIterations() {
		return iterations;
	}

	public AmiSim setIterations(long iterations) {
		this.iterations = iterations;
		return this;
	}

	public void disconnect() {
		if (status == STATUS_RUNNING)
			stop();
		assertConnected();
		this.client.close();
		this.status = STATUS_NOT_CONNECTED;
	}

	public void setConfig(Map<String, Object> cfg) {
		setPeriodMs(CH.getOr(Caster_Long.INSTANCE, cfg, "period", getPeriodMs()));
		setIterations(CH.getOr(Caster_Long.INSTANCE, cfg, "iterations", getIterations()));
		setAppName(CH.getOr(Caster_String.INSTANCE, cfg, "I", getAppName()));
		List<Map<String, Object>> types = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, cfg, "objects", null);
		Set<String> typeIds = new HashSet<String>();
		for (Map<String, Object> i : types) {
			String typeName = CH.getOrThrow(Caster_String.INSTANCE, i, "type");
			String id = CH.getOr(Caster_String.INSTANCE, i, "type", typeName);
			String idPrefix = CH.getOr(Caster_String.INSTANCE, i, "idPrefix", null);
			String expires = CH.getOrThrow(Caster_String.INSTANCE, i, "expires");
			boolean store = CH.getOr(Caster_Boolean.INSTANCE, i, "store", true);
			boolean isAlert = CH.getOr(Caster_Boolean.INSTANCE, i, "alert", false);
			Tuple2<Long, Long> entries = toRange(i, "entriesCount");
			double aps = CH.getOrThrow(Caster_Double.INSTANCE, i, "addPerSecond");
			double ups = CH.getOrThrow(Caster_Double.INSTANCE, i, "updatePerSecond");
			double dps = CH.getOrThrow(Caster_Double.INSTANCE, i, "deletePerSecond");
			AmiSimType type = getType(id);
			if (type == null) {
				type = new AmiSimType(id, idPrefix == null ? null : new AmiSimField.IdSim(idPrefix), new AmiSimField.NumberSim().setStartValue(expires).setType("L"), isAlert);
			} else {
				AmiSimField.IdSim idgen = (IdSim) type.getIdGenerator();
				idgen.setPrefix(idPrefix);
				AmiSimField.NumberSim expgen = (NumberSim) type.getExpiresGenerator();
				expgen.setStartValue(expires);
			}
			type.setType(typeName);
			type.setAddPerSecond(aps);
			type.setUpdPerSecond(ups);
			type.setDelPerSecond(dps);
			type.setStoring(store);

			type.setCapacityRange(entries.getA().intValue(), entries.getB().intValue());
			List<Map<String, Object>> params = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, i, "params", null);
			if (params != null) {
				for (Map<String, Object> p : params) {
					String clazz = CH.getOrThrow(Caster_String.INSTANCE, p, "class");
					String name = CH.getOrThrow(Caster_String.INSTANCE, p, "name");
					AmiSimField<?> prm = type.getFields().get(name);
					if ("number".equals(clazz)) {
						NumberSim param = prm instanceof NumberSim ? (NumberSim) prm : new AmiSimField.NumberSim();
						double canUpdate = canUpdate(p);
						param.setCanUpdate(canUpdate);
						param.setStartValue(CH.getOrThrow(Caster_String.INSTANCE, p, "start"));
						param.setMinValue(CH.getOr(Caster_String.INSTANCE, p, "min", null));
						param.setMaxValue(CH.getOr(Caster_String.INSTANCE, p, "max", null));
						param.setDeltaValue(CH.getOr(Caster_String.INSTANCE, p, "delta", null));
						param.setType(CH.getOrThrow(Caster_String.INSTANCE, p, "type"));
						param.setParamName(name);
						prm = param;
					} else if ("id".equals(clazz)) {
						IdSim param = prm instanceof IdSim ? (IdSim) prm : new AmiSimField.IdSim();
						String prefix = CH.getOrThrow(Caster_String.INSTANCE, p, "prefix");
						double canUpdate = canUpdate(p);
						int digits = CH.getOrThrow(Caster_Integer.INSTANCE, p, "digits");
						param.setPrefix(prefix);
						param.setParamName(name);
						param.setDigits(digits);
						param.setCanUpdate(canUpdate);
						prm = param;
					} else if ("pattern".equals(clazz)) {
						PatternSim param = prm instanceof PatternSim ? (PatternSim) prm : new AmiSimField.PatternSim();
						param.setParamName(name);
						double canUpdate = canUpdate(p);
						param.setCanUpdate(canUpdate);
						param.setType(CH.getOrThrow(Caster_String.INSTANCE, p, "type"));
						param.setPattern(CH.getOrThrow(Caster_String.INSTANCE, p, "text"));
						param.setMaxUniqueValues(CH.getOr(Caster_Integer.INSTANCE, p, "uniqueCount", -1));
						prm = param;
					} else if ("time".equals(clazz)) {
						TimeSim param = prm instanceof TimeSim ? (TimeSim) prm : new AmiSimField.TimeSim();
						param.setParamName(name);
						param.setOffsetRange(CH.getOr(Caster_String.INSTANCE, p, "offset", null));
						prm = param;
					} else if ("ref".equals(clazz)) {
						RefSim param = prm instanceof RefSim ? (RefSim) prm : new AmiSimField.RefSim();
						String refType = CH.getOrThrow(Caster_String.INSTANCE, p, "type");
						String refParam = CH.getOrThrow(Caster_String.INSTANCE, p, "param");
						double canUpdate = canUpdate(p);
						param.setOffsetRange(CH.getOr(Caster_String.INSTANCE, p, "offset", null));
						param.setParamName(name);
						param.setCanUpdate(canUpdate);
						param.setRef(refType, refParam);
						prm = param;
					} else
						throw new AmiSimException("Unknown param type: " + clazz);
					type.addField(prm);
				}
			}
			addAmiSimType(type);
			typeIds.add(id);
		}
		List<Map<String, Object>> plugins = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, cfg, "plugins", null);
		Set<String> pluginIds = new HashSet<String>();
		if (plugins != null) {
			for (Map<String, Object> p : plugins) {
				String id = CH.getOrThrow(Caster_String.INSTANCE, p, "id");
				String clazz = CH.getOrThrow(Caster_String.INSTANCE, p, "class");
				AmiSimPlugin plg = getPlugin(id);
				if ("child_qty".equals(clazz)) {
					AmiSimPluginChildQty plugin = plg instanceof AmiSimPluginChildQty ? (AmiSimPluginChildQty) plg : new AmiSimPluginChildQty();
					double addPerSecond = CH.getOrThrow(Caster_Double.INSTANCE, p, "addPerSecond");
					plugin.setAddPerSecond(addPerSecond);
					plugin.setChildIdParam(CH.getOrThrow(Caster_String.INSTANCE, p, "childIdParam"));
					plugin.setChildQuantityParam(CH.getOrThrow(Caster_String.INSTANCE, p, "childQuantityParam"));
					plugin.setChildType(CH.getOrThrow(Caster_String.INSTANCE, p, "childType"));
					plugin.setDelta(CH.getOrThrow(Caster_String.INSTANCE, p, "delta"));
					plugin.setParentIdParam(CH.getOrThrow(Caster_String.INSTANCE, p, "parentIdParam"));
					plugin.setParentQuantityParam(CH.getOrThrow(Caster_String.INSTANCE, p, "parentQuantityParam"));
					plugin.setParentType(CH.getOrThrow(Caster_String.INSTANCE, p, "parentType"));
					plg = plugin;
				} else
					throw new AmiSimException("Unknown plugin type: " + clazz);
				addPlugin(id, plg);
				pluginIds.add(id);
			}
		}
		if (plugins == null)
			this.plugins.clear();
		else
			for (String i : CH.comm(this.plugins.keySet(), pluginIds, true, false, false))
				this.plugins.remove(i);
		if (types == null)
			this.types.clear();
		else
			for (String i : CH.comm(this.types.keySet(), typeIds, true, false, false))
				this.types.remove(i);

	}
	private double canUpdate(Map<String, Object> p) {
		String changes = CH.getOr(Caster_String.INSTANCE, p, "changes", "false");
		if (changes == null || "false".equals(changes))
			return 0;
		else if ("true".equals(changes))
			return 1;
		else
			return Double.parseDouble(changes);
	}

	private AmiSimPlugin getPlugin(String id) {
		return plugins.get(id);
	}

	private void addPlugin(String id, AmiSimPlugin plugin) {
		plugins.put(id, plugin);
	}

	private String getAppName() {
		return appName;
	}

	private static Tuple2<Long, Long> toRange(Map<String, Object> m, String key) {
		Object o = CH.getOrThrow(m, key);
		if (o instanceof Number)
			return new Tuple2<Long, Long>(((Number) o).longValue(), ((Number) o).longValue());
		else if (o instanceof List) {
			return new Tuple2<Long, Long>(Caster_Long.INSTANCE.cast(((List) o).get(0)), Caster_Long.INSTANCE.cast(((List) o).get(1)));
		} else
			throw new RuntimeException("bad " + key + ": " + o);
	}
	private static Tuple2<Double, Double> toRangeDouble(Map<String, Object> m, String key) {
		Object o = CH.getOrThrow(m, key);
		if (o instanceof Number)
			return new Tuple2<Double, Double>(((Number) o).doubleValue(), ((Number) o).doubleValue());
		else if (o instanceof List) {
			return new Tuple2<Double, Double>(Caster_Double.INSTANCE.cast(((List) o).get(0)), Caster_Double.INSTANCE.cast(((List) o).get(1)));
		} else
			throw new RuntimeException("bad " + key + ": " + o);
	}

	public AmiSimType getType(String refType) {
		return this.types.get(refType);
	}

	public long getMessagesCount() {
		return messagesCount;
	}

	public long getObjectsCount() {
		return objectsCount;
	}

	public void clear() {
		this.types.clear();
		this.plugins.clear();
		this.messagesCount = 0;
		this.objectsCount = 0;
		this.recentMessages.clear();
	}

	public Set<String> getTypes() {
		return this.types.keySet();
	}

	public List<String> getRecentMessages() {
		return recentMessages;
	}

	public void setRandomSeed(Long value) {
		assertNotRunning();
		this.seed = value;
		if (seed == null)
			this.random = new Random();
		else
			this.random = new Random(seed);
	}

	@Override
	public void onConnect(AmiClient source) {
	}

	@Override
	public void onDisconnect(AmiClient source) {
	}

	@Override
	public void onMessageReceived(AmiClient source, long now, long seqnum, int status, CharSequence message) {

	}

	@Override
	public void onMessageSent(AmiClient source, CharSequence message) {
		while (this.recentMessages.size() >= MAX_RECENT_MESSAGE_BUFFER)
			this.recentMessages.remove(0);
		this.recentMessages.add(message.toString());
		this.messagesCount++;
	}

	@Override
	public void onLoggedIn(AmiClient rawAmiClient) {

	}

	@Override
	public void onCommand(AmiClient source, String requestId, String cmd, String userName, String type, String id, Map<String, Object> params) {

	}

}
