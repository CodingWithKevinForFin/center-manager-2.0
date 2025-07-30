package com.f1.ami.center;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.ds.AmiDatasourceAdapterManager;
import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiCenterChangesMessage;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.ami.amicommon.msg.AmiRelayMachine;
import com.f1.ami.center.hdb.AmiHdb;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiImdbSessionManagerService;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.base.CalcFrame;
import com.f1.base.IterableAndSize;
import com.f1.base.Message;
import com.f1.container.ContainerTools;
import com.f1.container.OutputPort;
import com.f1.container.Partition;
import com.f1.container.impl.BasicState;
import com.f1.utils.ByteArray;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.ids.LongSequenceIdGenerator;
import com.f1.utils.structs.CompactLongKeyMap;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarColumnEnumMapper;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;

public class AmiCenterState extends BasicState {
	public static final String FOUNTAIN_ID = "CENTER";
	static final long DEFAULT_TABLE_REFRESH_PERIOD = 100;

	private static final Logger log = Logger.getLogger(AmiCenterState.class.getName());

	public static final int MAX_CLIENT_EVENTS = 1000;

	public static final int STATUS_TYPES_COUNT = 13;

	public static final byte STATUS_TYPE_LOGIN = 6;
	public static final byte STATUS_TYPE_LOGOUT = 7;
	public static final byte STATUS_TYPE_PROCESS_EVENT = 8;
	public static final byte STATUS_TYPE_PROCESS_RELAY_EVENT = 9;
	public static final byte STATUS_TYPE_OBJECT_EXPIRED = 10;
	public static final byte STATUS_TYPE_GET_SNAPSHOT = 11;
	public static final byte STATUS_TYPE_QUERY_DATASOURCE = 12;

	private AmiCenterChangesMessageBuilder changesMsgBuilder;
	private AmiDatasourceAdapterManager dsManager;

	private long defaultTableRefreshPeriodMs;

	final private ContainerTools tools;
	final private AmiCenterItineraryProcessor itineraryProcessor;
	final private boolean anonymousDatasourcesPermitted;
	final private CalcFrame globalVars;
	final private boolean onStartupOnDiskDefrag;
	final private Map<String, AmiScriptClassPluginWrapper> customClassPlugins;
	private AmiHdb hdb;
	private AmiImdbScriptManager scriptManager;
	private AmiImdbSessionManagerService sessionManager;
	private AmiCenterGlobalProcess globalProcess;
	private AmiImdbSession globalSession;
	private AmiImdbSession rtFeedSession;

	public AmiCenterState(AmiCenterResourcesManager resourceManager, AmiImdbSessionManagerService sessionManager, ContainerTools tools, OutputPort<Message> timerPort,
			AmiCenterItineraryProcessor itineraryProcessor, CalcFrame globalVars, Map<String, AmiScriptClassPluginWrapper> customClassPlugins) {
		this.timerPort = timerPort;
		this.stackFramePool = new ReusableStackFramePool();
		this.itineraryProcessor = itineraryProcessor;
		this.tools = tools;
		this.defaultTableRefreshPeriodMs = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_TABLES_DEFAULT_REFRESH_PERIOD_MS, DEFAULT_TABLE_REFRESH_PERIOD);
		HashMap<String, Byte> behaviours = CH.m(new HashMap<String, Byte>(), "IGNORE", UNKNOWN_TYPE_BEHAVIOUR_IGNORE, "LOG_ERROR", UNKNOWN_TYPE_BEHAVIOUR_LOG_ERROR, "CREATE_TABLE",
				UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE);
		this.unknownTypeBehaviour = tools.getOptionalEnum(AmiCenterProperties.PROPERTY_AMI_UNKNOWN_REALTIME_TABLE_BEHAVIOR, behaviours, UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE);
		this.enumMapper = new AmiCenterEnumMapper(this);
		this.resourceManager = resourceManager;
		this.anonymousDatasourcesPermitted = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_ANONYMOUS_DATASOURCES_ENABLED, Boolean.TRUE);
		this.maxLogQueryMaxChars = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_LOG_QUERY_MAX_CHARS, 2000);
		this.onStartupOnDiskDefrag = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_ONSTARTUP_ONDISK_DEFRAG, Boolean.TRUE);
		this.globalVars = globalVars;
		this.customClassPlugins = customClassPlugins;
		this.scriptManager = new AmiImdbScriptManager(this, getTools());
		this.sessionManager = sessionManager;
		this.sessionManager.init(this);
		this.changesMsgBuilder = new AmiCenterChangesMessageBuilder(this);
		this.dsManager = new AmiDatasourceAdapterManager(this.getTools(), this.namingServiceResolver);
	}
	//	private byte parseUnknownRealtimeTableBehaviour(String val) {
	//		if (val == null)
	//			return UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE;
	//		if ("IGNORE".equals(val))
	//			return UNKNOWN_TYPE_BEHAVIOUR_IGNORE;
	//		if ("LOG_ERROR".equals(val))
	//			return UNKNOWN_TYPE_BEHAVIOUR_LOG_ERROR;
	//		if ("LEGACY".equals(val))
	//			throw new RuntimeException("no longer suppored: LEGACY");
	//		//					return UNKNOWN_TYPE_BEHAVIOUR_LEGACY;
	//		if ("CREATE_TABLE".equals(val))
	//			return UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE;
	//		throw new RuntimeException("Unknown type: " + val);
	//	}
	@Override
	public void setPartition(Partition partition) {
		super.setPartition(partition);
		this.amiImdb = new AmiImdbImpl(this, this.globalVars);
		this.hdb = new AmiHdb(this);
		this.globalSession = this.sessionManager.newSession(AmiTableUtils.DEFTYPE_SYSTEM, AmiCenterQueryDsRequest.ORIGIN_SYSTEM, "__SYSTEM", "SYSTEM SESSION",
				AmiImdbSession.PERMISSIONS_FULL, this.amiImdb.getDefaultQueryTimeoutMs(), this.amiImdb.getDefaultQueryLimit(), globalVars);
		this.globalProcess = new AmiCenterGlobalProcess(createNextProcessId(), this.globalSession.getSessionId());
		this.addProcess(this.globalProcess);
		this.rtFeedSession = this.sessionManager.newSession(AmiTableUtils.DEFTYPE_USER, AmiCenterQueryDsRequest.ORIGIN_RTFEED, "__RTFEED", "REALTIME FEED",
				AmiImdbSession.PERMISSIONS_FULL, this.amiImdb.getDefaultQueryTimeoutMs(), this.amiImdb.getDefaultQueryLimit(), globalVars);
		//		for (MappingEntry<String, Class<?>> i : this.globalVarTypes.entries()) {
		//			this.amiImdb.getGlobalSession().putVar(i.getKey(), i.getValue(), this.globalVars.get(i.getKey()));
		//		}
	}

	public AmiDatasourceAdapterManager getDatasourceManager() {
		return this.dsManager;
	}

	private Map<String, AmiCenterRelayState> agentsByPuid = new HashMap<String, AmiCenterRelayState>();
	private LongKeyMap<AmiCenterRelayState> agentsByMiid = new LongKeyMap<AmiCenterRelayState>();
	private AmiCenterResourcesManager resourceManager;

	public AmiCenterRelayState getAgentByPuid(String puid) {
		return CH.getOrThrow(agentsByPuid, puid);
	}
	public AmiCenterRelayState getAgentByPuidNoThrow(String puid) {
		return agentsByPuid.get(puid);
	}

	public AmiCenterRelayState createAgentState(String processUid, String relayId, String hostname, int remotePort, long connectedTime, AmiRelayMachine machine, AmiRow relayRow,
			byte centerId, boolean guaranteedMessaging) {
		if (SH.isnt(processUid))
			throw new RuntimeException("invalid processUid: " + processUid);
		if (SH.isnt(hostname))
			throw new RuntimeException("invalid hostname: " + hostname);
		AmiCenterRelayState r = new AmiCenterRelayState(this, processUid, relayId, hostname, remotePort, connectedTime, machine, relayRow, centerId, guaranteedMessaging);
		CH.putOrThrow(agentsByPuid, processUid, r);
		agentsByMiid.putOrThrow(machine.getId(), r);
		return r;
	}

	public AmiCenterRelayState removeAgent(String processUid) {
		AmiCenterRelayState r = CH.removeOrThrow(agentsByPuid, processUid);
		agentsByMiid.removeOrThrow(r.getMachineState().getId());
		return r;
	}

	public Set<String> getAgentPuids() {
		return agentsByPuid.keySet();
	}
	public Collection<AmiCenterRelayState> getAgents() {
		return agentsByPuid.values();
	}

	private long sequenceNumber;

	public long nextSequenceNumber() {
		return ++sequenceNumber;
	}

	public long currentSequenceNumber() {
		return sequenceNumber;
	}

	private LongSequenceIdGenerator uidGenerator;
	private AtomicLong processId = new AtomicLong(1);

	public long createNextProcessId() {
		return this.processId.getAndIncrement();
	}
	public long createNextId() {
		if (uidGenerator == null)
			uidGenerator = (LongSequenceIdGenerator) getPartition().getContainer().getServices().getUidGenerator(FOUNTAIN_ID);
		return uidGenerator.createNextLongId();
	}
	public long createNextIds(int count) {
		if (uidGenerator == null)
			uidGenerator = (LongSequenceIdGenerator) getPartition().getContainer().getServices().getUidGenerator(FOUNTAIN_ID);
		return uidGenerator.createNextLongId();
	}

	public AmiCenterRelayState getMachineByMiidNoThrow(long miid) {
		return agentsByMiid.get(miid);
	}

	public AmiCenterChangesMessageBuilder getChangesMessageBuilder() {
		return this.changesMsgBuilder;
	}
	public AmiCenterChangesMessageBuilder getChangesMessageBuilderNoReset() {
		return this.changesMsgBuilder;
	}

	long minExpiringObjectTime = Long.MAX_VALUE;//-1 indicates it needs to be calculated

	final IntKeyMap<AmiCenterApplication> amiApplicationsByAppId = new IntKeyMap<AmiCenterApplication>();
	final LongKeyMap<AmiCenterConnection> amiConnections = new LongKeyMap<AmiCenterConnection>();

	public AmiCenterApplication getAmiAppByAppId(short appId) {
		return amiApplicationsByAppId.get(appId);
	}

	public AmiCenterApplication putAmiApplication(short appId, String appName) {
		AmiCenterApplication eapp = new AmiCenterApplication(this, appId, appName);
		amiApplicationsByAppId.put(appId, eapp);
		return eapp;
	}

	public AmiCenterConnection getAmiConnection(long id) {
		return amiConnections.get(id);
	}
	public AmiCenterConnection putAmiConnection(AmiRow connection, long amiRelayId, int relaysConnectionid) {
		AmiCenterConnection r = new AmiCenterConnection(this, connection, amiRelayId, relaysConnectionid);
		amiConnections.put(connection.getAmiId(), r);
		return r;
	}
	public AmiCenterConnection removeAmiConnection(long id) {
		return amiConnections.remove(id);
	}
	public int getAmiConnectionsCount() {
		return amiConnections.size();
	}
	public Iterable<AmiCenterConnection> getAmiConnections() {
		return amiConnections.values();
	}

	public IterableAndSize<AmiCenterApplication> getAmiApplications() {
		return this.amiApplicationsByAppId.values();
	}

	private OneToOne<String, Short> amiKeyIdLookup = new OneToOne<String, Short>();

	public short getAmiKeyId(String text) {
		Short r = amiKeyIdLookup.getValue(text);
		if (r == null) {
			amiKeyIdLookup.put(text, r = (short) (amiKeyIdLookup.size() + 1));//TODO: check for overflow
			this.getChangesMessageBuilder().onStringPoolKeyEntry(r, text);
		}
		return r;
	}
	public short[] getAmiKeyId(String[] text) {
		short[] r = new short[text.length];
		for (int i = 0; i < text.length; i++)
			r[i] = getAmiKeyId(text[i]);

		return r;
	}
	public short getAmiKeyIdNoCreate(String text) {
		Short r = amiKeyIdLookup.getValue(text);
		if (r == null)
			return 0;
		return r;
	}

	public int getAmiKeyIdStringPoolMapSize() {
		return amiKeyIdLookup.size();
	}
	public int getAmiValueStringPoolMapSize() {
		return amiValuePool.size();
	}

	final private Map<ByteArray, Integer> amiValuePool = new HashMap<ByteArray, Integer>();
	final private IntKeyMap<String> amiValueToStringPool = new IntKeyMap<String>();
	private ByteArray tmpKey = new ByteArray();

	public int getAmiStringPool(String key) {
		tmpKey.resetNoCheck(key.getBytes(), 0, key.length());
		return getAmiStringPool(tmpKey);
	}
	public int getAmiStringPool(byte[] data, int start, int len) {
		tmpKey.resetNoCheck(data, start, start + len);
		return getAmiStringPool(tmpKey);
	}
	public int getAmiStringPool(ByteArray tmpKey) {
		Integer value = amiValuePool.get(tmpKey);
		if (value != null)
			return value.intValue();

		int r = amiValuePool.size() + 1;
		if (r >= 0xffffff)
			throw new RuntimeException("Exceeded max Enum count: " + 0xffffff);
		ByteArray nw = tmpKey.cloneToClipped();
		amiValuePool.put(nw, r);
		String s = new String(nw.getData(), SH.CHARSET_UTF);
		amiValueToStringPool.put(r, s);
		this.getChangesMessageBuilder().onStringPoolValueEntry(r, s);
		return r;
	}
	public Set<Entry<ByteArray, Integer>> getAmiStringPool() {
		return amiValuePool.entrySet();
	}
	public byte[] getAmiValuesStringPoolAsBytes() {
		FastByteArrayDataOutputStream out = getTmpBuf();
		for (Node<String> e : amiValueToStringPool)
			AmiCenterChangesMessageBuilder.writeStringPoolValueEntry(e.getIntKey(), e.getValue(), out);
		for (Entry<Short, String> e : amiKeyIdLookup.getInnerValueKeyMap().entrySet())
			AmiCenterChangesMessageBuilder.writeStringPoolKeyEntry(e.getKey(), e.getValue(), out);
		return out.toByteArray();
	}

	public String getAmiKeyString(short id) {
		String r = this.amiKeyIdLookup.getKey(id);
		if (r != null)
			return r;
		return id == 0 ? null : ("<KEY " + id + ">");
	}
	public String getAmiValueString(int id) {
		return this.amiValueToStringPool.get(id);
	}

	public IterableAndSize<AmiCenterRelayState> getRelays() {
		return this.agentsByMiid.values();
	}

	private AmiCenterApplication amiSystemApplication;

	public void setAmiSystemApplication(AmiCenterApplication amiSystemApplication) {
		this.amiSystemApplication = amiSystemApplication;
	}
	public AmiCenterApplication getAmiSystemApplication() {
		return this.amiSystemApplication;
	}

	private FastByteArrayDataOutputStream tmpBuf = new FastByteArrayDataOutputStream();

	public FastByteArrayDataOutputStream getTmpBuf() {
		this.tmpBuf.reset(8192);
		return this.tmpBuf;
	}

	private File persistDirectory;

	public File getPersistDirectory() {
		if (persistDirectory == null) {
			try {
				final File amiPersistDir = tools.getOptional(AmiCenterProperties.PROPERTY_AMI_DB_PERSIST_DIR, new File("persist"));
				this.persistDirectory = amiPersistDir;
			} catch (Exception e) {
				throw new RuntimeException("Error processing Persistance directory property (" + AmiCenterProperties.PROPERTY_AMI_DB_PERSIST_DIR + ")", e);
			}
		}
		return persistDirectory;
	}

	private long amiMessageCount = 0;

	public long incrementAmiMessageCount(int cnt) {
		return this.amiMessageCount += cnt;
	}

	private long amiMessageStats[] = new long[STATUS_TYPES_COUNT];

	public void incrementAmiMessageStats(byte type) {
		amiMessageStats[type]++;
	}
	public void incrementAmiMessageStats(byte type, long count) {
		amiMessageStats[type] += count;
	}
	public long getAmiMessageStat(byte type) {
		return this.amiMessageStats[type];
	}

	private void addTrigger(String type, AmiTriggerBindingImpl trigger, IntKeyMap<List<AmiTriggerBindingImpl>> sink) {
		int typeId = getAmiKeyId(type);
		List<AmiTriggerBindingImpl> list = sink.get(typeId);
		if (list == null)
			sink.put(typeId, list = new ArrayList<AmiTriggerBindingImpl>(1));
		list.add(trigger);
	}

	public AmiImdbImpl getAmiImdb() {
		return this.amiImdb;
	}

	private AmiImdbImpl amiImdb;

	private OutputPort<AmiCenterChangesMessage> toClientsPort;

	private AmiChainedNamingServiceResolver namingServiceResolver;

	private ColumnarColumnEnumMapper enumMapper;
	private AmiEncrypter encrypter;

	public void onProcessedEventsComplete() {
		this.amiImdb.onProcessedEventsComplete();
		//		this.hdb.flushPersisted();
		if (this.changesMsgBuilder.hasChanges()) {
			AmiCenterChanges toClient = this.changesMsgBuilder.popToChangesMsg(nextSequenceNumber());
			this.toClientsPort.send(toClient, null);
		}
	}

	public void setToClientsPort(OutputPort<AmiCenterChangesMessage> toClientsPort) {
		this.toClientsPort = toClientsPort;
	}

	public void setNamingServiceResolver(AmiChainedNamingServiceResolver namingServiceResolver) {
		this.namingServiceResolver = namingServiceResolver;
		this.dsManager.setNamingServiceResolver(this.namingServiceResolver);
	}

	public AmiChainedNamingServiceResolver getNamingServiceResolver() {
		return this.namingServiceResolver;
	}

	public ColumnarColumnEnumMapper getEnumMapper() {
		return this.enumMapper;
	}

	public void setEncrypter(AmiEncrypter encrypter) {
		this.encrypter = encrypter;
	}

	public String encrypt(String unencrypted) {
		return this.encrypter.encrypt(unencrypted);
	}

	public String decrypt(String encrypted) {
		return this.encrypter.decrypt(encrypted);
	}

	private HasherMap<CharSequence, AmiCenterResource> resources = new HasherMap<CharSequence, AmiCenterResource>();
	private OutputPort<Message> timerPort;
	private AmiAuthenticatorPlugin authenticator;

	public void syncResourcesToTable(AmiImdbSession session) {
		HasherMap<CharSequence, AmiCenterResource> resources = this.resourceManager.getResources();
		if (this.resources == resources)
			return;
		AmiSchema sys = this.amiImdb.getSystemSchema();
		CalcFrameStack sf = this.amiImdb.getGlobalSession().getReusableTopStackFrame();
		for (Entry<CharSequence, Tuple2<AmiCenterResource, AmiCenterResource>> e2 : CH.join(this.resources, resources).entrySet()) {
			Tuple2<AmiCenterResource, AmiCenterResource> v = e2.getValue();
			if (v.getA() == null)
				sys.__RESOURCE.addResource(this, v.getB(), sf);
			else if (v.getB() == null)
				sys.__RESOURCE.removeResource(this, v.getA(), sf);
			else if (v.getA() != v.getB())
				sys.__RESOURCE.addResource(this, v.getB(), sf);
		}
		this.resources = resources;

	}

	public AmiCenterResourcesManager getResourcesManager() {
		return this.resourceManager;
	}

	public long getDefaultTableRefreshPeriodMs() {
		return this.defaultTableRefreshPeriodMs;
	}

	public ContainerTools getTools() {
		return this.tools;
	}

	public OutputPort<Message> getTimerPort() {
		return this.timerPort;
	}

	public void setAuthenticator(AmiAuthenticatorPlugin authenticator) {
		this.authenticator = authenticator;
	}
	public AmiAuthenticatorPlugin getAuthenticator() {
		return this.authenticator;
	}

	public AmiCenterItineraryProcessor getItineraryProcessor() {
		return itineraryProcessor;
	}

	public static final byte UNKNOWN_TYPE_BEHAVIOUR_IGNORE = 1;
	//	public static final byte UNKNOWN_TYPE_BEHAVIOUR_LEGACY = 2;
	public static final byte UNKNOWN_TYPE_BEHAVIOUR_CREATE_TABLE = 3;
	public static final byte UNKNOWN_TYPE_BEHAVIOUR_LOG_ERROR = 4;
	private byte unknownTypeBehaviour;

	public byte getUnknownTypeBehaviour() {
		return unknownTypeBehaviour;
	}

	private int defaultDatasourceTimeout = -1;

	public int getDefaultDatasourceTimeout() {

		if (this.defaultDatasourceTimeout == -1)
			this.defaultDatasourceTimeout = AmiUtils.getDefaultTimeout(tools);
		return this.defaultDatasourceTimeout;
	}

	private Map<String, AmiCenterRelayConnectionStatuses> relayConnectionStatuses = new HashMap<String, AmiCenterRelayConnectionStatuses>();
	private int maxLogQueryMaxChars;

	public Map<String, AmiCenterRelayConnectionStatuses> getRelayConnectionStatuses() {
		return this.relayConnectionStatuses;
	}
	public boolean isAnonymousDatasourcesPermitted() {
		return this.anonymousDatasourcesPermitted;
	}
	public int getLogQueryMaxChars() {
		return this.maxLogQueryMaxChars;
	}

	private CompactLongKeyMap<AmiCenterProcess> runningProcesses = new CompactLongKeyMap<AmiCenterProcess>("Processes", new CompactLongKeyMap.KeyGetter<AmiCenterProcess>() {

		@Override
		public long getKey(AmiCenterProcess o) {
			return o.getProcessId();
		}
	}, 16);
	private ReusableStackFramePool stackFramePool;

	public void addProcess(AmiCenterProcess proc) {
		this.runningProcesses.put(proc);
	}
	public void removeProcess(AmiCenterProcess proc) {
		this.runningProcesses.remove(proc.getProcessId());
	}
	public Iterable<AmiCenterProcess> getProcesses() {
		synchronized (this.runningProcesses) {
			return CH.l(this.runningProcesses);
		}
	}
	public boolean onStartupOnDiskDefrag() {
		return this.onStartupOnDiskDefrag;
	}
	public void addProcessThreadSafe(AmiCenterProcess proc) {
		synchronized (this.runningProcesses) {
			this.runningProcesses.put(proc);
		}
	}
	public void removeProcessThreadSafe(AmiCenterProcess proc) {
		synchronized (this.runningProcesses) {
			this.runningProcesses.remove(proc.getProcessId());
		}
	}
	public Map<String, AmiScriptClassPluginWrapper> getCustomClassPlugins() {
		return customClassPlugins;
	}
	public AmiEncrypter getEncrypter(String encrypter) {
		OH.assertNotNull(this.encrypter);
		if ("default".equals(encrypter))
			return this.encrypter;
		else
			throw new RuntimeException("Encrypter not defined: " + encrypter + " (valid encrypters are: default)");
	}
	public AmiHdb getHdb() {
		return this.hdb;
	}
	//	public StackFrame createStackFrame() {
	//		return this.globalSession.createStackFrame();
	//		//		return new TopStackFrame(this.amiImdb.getGlobalSession(), SqlProcessor.NO_LIMIT, null, this.amiImdb.getAmiScriptMethodFactory(), new BasicFrame(new BasicTypes()));
	//	}
	public AmiImdbSessionManagerService getSessionManager() {
		return this.sessionManager;
	}
	public AmiImdbScriptManager getScriptManager() {
		return this.scriptManager;
	}
	public AmiImdbSession getGlobalSession() {
		return this.globalSession;
	}
	public AmiCenterGlobalProcess getGlobalProcess() {
		return this.globalProcess;
	}
	public AmiImdbSession getRtFeedSession() {
		return this.rtFeedSession;
	}
	public ReusableStackFramePool getStackFramePool() {
		return this.stackFramePool;
	}
	public CalcFrameStack getReusableTopStackFrame() {
		return this.globalSession.getReusableTopStackFrame();
	}
	public int getStackLimit() {
		return CalcFrameStack.DEFAULT_STACK_LIMIT;
	}

}
