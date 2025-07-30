package com.f1.ami.relay;

import java.io.IOException;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiChainedNamingServiceResolver;
import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.ds.AmiDatasourceAdapterManager;
import com.f1.ami.amicommon.msg.AmiRelayConnectionMessage;
import com.f1.container.ContainerTools;
import com.f1.container.Partition;
import com.f1.container.RequestMessage;
import com.f1.container.impl.BasicState;
import com.f1.povo.f1app.audit.F1AppAuditTrailRule;
import com.f1.utils.ByteArray;
import com.f1.utils.ByteHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.ids.BasicIdGenerator;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiRelayState extends BasicState {
	private static final Logger log = LH.get();
	private final AmiRelayRoutes router;
	private final AmiRelayTransformManager transformManager;

	final private Map<String, AmiRelayCenterDefinition> centersByName = new HasherMap<String, AmiRelayCenterDefinition>();
	final private AmiRelayScriptManager scriptManager;
	final private int checkConfigFilesChangedPeriodMs;

	/////////////////////
	//Eye Connectivity status
	/////////////////////

	public AmiRelayState(ContainerTools tools, AmiRelayCenterDefinition[] centers) throws IOException {
		this.checkConfigFilesChangedPeriodMs = tools.getOptional(AmiRelayProperties.OPTION_AMI_RELAY_CHECK_CONFIG_FILES_CHANGE_PERIOD_MS, 2000);
		for (AmiRelayCenterDefinition i : centers)
			this.centersByName.put(i.getName(), i);
		this.scriptManager = new AmiRelayScriptManager(this, tools);
		this.router = new AmiRelayRoutes(tools, this, this.checkConfigFilesChangedPeriodMs);
		this.transformManager = new AmiRelayTransformManager(tools, this.scriptManager, this.checkConfigFilesChangedPeriodMs);
		this.journal = new AmiRelayJournal(tools, router);
		for (Entry<Short, String> i : this.journal.getRecovedKeys().entrySet()) {
			this.amiKeyIdLookup.put(new ByteArray(i.getValue()), i.getKey());
			this.pendingNewKeysSink.put(i.getKey(), i.getValue());
		}
		this.sequenceNumber = this.journal.getCurrentSeqnum();
	}

	private AmiDatasourceAdapterManager dsManager;

	final private AmiRelayJournal journal;

	private int maxLogQueryMaxChars;

	@Override
	public void setPartition(Partition partition) {
		super.setPartition(partition);
		this.dsManager = new AmiDatasourceAdapterManager(partition.getContainer().getTools(), this.namingServiceResolver);
		this.maxLogQueryMaxChars = partition.getContainer().getTools().getOptional(AmiCommonProperties.PROPERTY_AMI_LOG_QUERY_MAX_CHARS, 2000);
	}
	public AmiDatasourceAdapterManager getDatasourceManager() {
		return this.dsManager;
	}

	/////////////////////
	//Active Itineraries
	/////////////////////
	private int nextItineraryId = 1;;
	private Map<RequestMessage<?>, AmiRelayItinerary<?>> requestsToItineraries = new IdentityHashMap<RequestMessage<?>, AmiRelayItinerary<?>>();
	public LongKeyMap<AmiRelayItinerary<?>> activeItineraries = new LongKeyMap<AmiRelayItinerary<?>>();

	public long generateNextItineraryId() {
		return nextItineraryId++;
	}

	public AmiRelayItinerary<?> popItineraryForRequest(RequestMessage<?> requestMessage) {
		AmiRelayItinerary<?> r = requestsToItineraries.remove(requestMessage);
		if (r == null)
			throw new IllegalStateException("request not associated with an itinerary: " + requestMessage);
		return r;
	}

	public void pushRequestForItinerary(AmiRelayItinerary<?> itinerary, RequestMessage<?> requestMessage) {
		CH.putOrThrow(requestsToItineraries, requestMessage, itinerary);
	}

	public void addItinerary(AmiRelayItinerary<?> itinerary) {
		this.activeItineraries.put(itinerary.getItineraryId(), itinerary);
	}
	public void removeItinerary(AmiRelayItinerary<?> itinerary) {
		AmiRelayItinerary<?> r = this.activeItineraries.remove(itinerary.getItineraryId());
		if (r == null)
			throw new RuntimeException("itinerary not found: " + itinerary);
		for (RequestMessage<?> req : itinerary.getPendingRequests())
			popItineraryForRequest(req);
	}

	public long sequenceNumber;

	public long nextSequenceNumber() {
		return ++sequenceNumber;
	}
	public long currentSequenceNumber() {
		return sequenceNumber;
	}

	/////////////////////
	//F1 Audit Rules
	/////////////////////
	private LongKeyMap<F1AppAuditTrailRule> auditTrailRules = new LongKeyMap<F1AppAuditTrailRule>();

	public void addAuditTrailRule(F1AppAuditTrailRule auditTrailRule) {
		auditTrailRule.lock();
		auditTrailRules.put(auditTrailRule.getId(), auditTrailRule);
	}

	public F1AppAuditTrailRule getAuditTrailRule(long serverId) {
		return auditTrailRules.get(serverId);
	}

	public Iterable<F1AppAuditTrailRule> getAuditTrailRules() {
		return auditTrailRules.values();
	}

	public F1AppAuditTrailRule removeAuditTrailRule(long id) {
		return auditTrailRules.remove(id);
	}

	public int getAuditTrailRulesCount() {
		return auditTrailRules.size();
	}

	private BasicMultiMap.Set<Long, String> rulesToF1Agents = new BasicMultiMap.Set<Long, String>();

	public Set<String> getAgentsForRule(long ruleId) {
		return rulesToF1Agents.get(ruleId);
	}
	public Set<String> removeAgentsForRule(long ruleId) {
		return rulesToF1Agents.remove(ruleId);
	}
	public void addRuleToAgentMapping(long ruleId, String processUid) {
		rulesToF1Agents.putMulti(ruleId, processUid);
	}

	public boolean removeAgentRuleMapping(long id, String processUid) {
		return rulesToF1Agents.removeMulti(id, processUid);
	}

	private IdGenerator<Long> generator = new BasicIdGenerator(-100000, -1);//start at negative 
	private AmiRelayServer amiServer;

	public IdGenerator<Long> getGenerator() {
		return generator;
	}
	public long createNextId() {
		return getGenerator().createNextId();
	}
	public AmiRelayServer getAmiServer() {
		return this.amiServer;
	}
	public void setAmiServer(AmiRelayServer server) {
		this.amiServer = server;
	}

	////////// AMI STUFF //////////////

	private Map<ByteArray, Short> amiKeyIdLookup = new HasherMap<ByteArray, Short>();
	private Map<Short, String> pendingNewKeysSink = new HashMap<Short, String>();

	final private ByteArray tmp = new ByteArray();

	public short getAmiKeyId(String data) {
		tmp.reset(data.getBytes());
		Short r = amiKeyIdLookup.get(tmp);
		if (r == null) {
			amiKeyIdLookup.put(tmp.cloneToClipped(), r = (short) (amiKeyIdLookup.size() + 1));//TODO: check for overflow
			pendingNewKeysSink.put(r, data);
		}
		return r;
	}
	public short getAmiKeyId(byte[] data, int start, int end) {
		tmp.reset(data, start, end);
		Short r = amiKeyIdLookup.get(tmp);
		if (r == null) {
			amiKeyIdLookup.put(tmp.cloneToClipped(), r = (short) (amiKeyIdLookup.size() + 1));//TODO: check for overflow
			pendingNewKeysSink.put(r, new String(data, start, end - start));
		}
		return r;
	}
	public Map<Short, String> getPendingNewKeysSink() {
		return pendingNewKeysSink;
	}
	public Map<Short, String> getAmiStringKeys() {
		Map<Short, String> r = new HashMap<Short, String>(amiKeyIdLookup.size());
		for (Entry<ByteArray, Short> e : amiKeyIdLookup.entrySet())
			r.put(e.getValue(), new String(e.getKey().getData()));
		return r;
	}

	//	private ObjectToByteArrayConverter converter = new ObjectToByteArrayConverter(true);
	//	private FastByteArrayDataInputStream converterIn = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
	//	private FastByteArrayDataOutputStream converterOut = new FastByteArrayDataOutputStream();
	//	private BasicFromByteArrayConverterSession converterSession = new BasicFromByteArrayConverterSession(converter, converterIn);

	/*
	IN SIGNATURE:
	2 bytes: entry count(n)
	n times{
	   1 byte: char length(m)
	   m bytes:chars
	}
	n times{
	  byte basictype
	  byte[..] marshalled value
	 }
	
	OUT SIGNATURE:
	2 bytes: entry count(n)
	n times{
	   2 bytes:key
	}
	n times{
	  byte basictype
	  byte[..] marshalled value
	 }
	 */

	public byte[][] convertAmiParamsToIds(byte[][] in) {
		if (in == null)
			return null;

		byte[][] r = new byte[in.length][];
		for (int i = 0; i < in.length; i++)
			r[i] = convertAmiParamsToIds(in[i]);

		return r;
	}

	private short keysBuf[] = new short[128];

	public byte[] convertAmiParamsToIds(byte[] in) {
		if (in == null)
			return null;
		int size = ByteHelper.readShort(in, 0);
		if (keysBuf.length < size)
			keysBuf = new short[size];
		int pos = 2;//first 2 bytes are size
		for (int i = 0; i < size; i++) {
			int len = in[pos++];
			keysBuf[i] = getAmiKeyId(in, pos, pos + len);
			pos += len;
		}
		byte[] r = new byte[size * 2 + 2 + in.length - pos];
		ByteHelper.writeShort(size, r, 0);
		int pos2 = 2;
		for (int i = 0; i < size; i++) {
			ByteHelper.writeShort(keysBuf[i], r, pos2);
			pos2 += 2;
		}
		System.arraycopy(in, pos, r, pos2, in.length - pos);
		return r;
	}

	public LocalToolkit getToolkit() {
		return toolkit;
	}

	final private LocalToolkit toolkit = new LocalToolkit();

	final private LongKeyMap<AmiRelayConnectionState> amiConnections = new LongKeyMap<AmiRelayConnectionState>();

	public void addAmiConnection(AmiRelayConnectionMessage action) {
		amiConnections.putOrThrow(action.getConnectionId(), new AmiRelayConnectionState(this, action));
	}

	public AmiRelayConnectionState getAmiConnection(int connectionId) {
		return amiConnections.get(connectionId);
	}

	public AmiRelayConnectionState removeAmiConnection(int connectionId) {
		return amiConnections.remove(connectionId);
	}

	public Iterable<AmiRelayConnectionState> getActiveAmiLogins() {
		return amiConnections.values();
	}

	private int maxConcurrentDsQueriesPerUser = 16;
	private Map<String, Integer> nextDsNumForUser = new HashMap<String, Integer>();

	public String getDsRunnerPartitionId(long datasourceId, String invokedBy) {
		int num = OH.noNull(nextDsNumForUser.get(invokedBy), 0);
		nextDsNumForUser.put(invokedBy, (num + 1) % maxConcurrentDsQueriesPerUser);
		String r = "DS-" + datasourceId + "." + invokedBy + "." + num;
		return r;
	}
	public int getMaxConcurrentDsQueriesPerUser() {
		return maxConcurrentDsQueriesPerUser;
	}

	public void setMaxConcurrentDsQueriesPerUser(int maxConcurrentDsQueriesPerUser) {
		this.maxConcurrentDsQueriesPerUser = maxConcurrentDsQueriesPerUser;
	}

	public AmiServiceLocator resolve(AmiServiceLocator orig) {
		AmiServiceLocator r = namingServiceResolver.resolve(orig);
		return r == null ? orig : r;
	}

	private AmiChainedNamingServiceResolver namingServiceResolver;
	//	final private CalcFrameStack stackFrame;

	public void setNamingServiceResolver(AmiChainedNamingServiceResolver namingServiceResolver) {
		this.namingServiceResolver = namingServiceResolver;
	}

	public AmiChainedNamingServiceResolver getNamingServiceResolver() {
		return this.namingServiceResolver;
	}

	public AmiRelayJournal getJournal() {
		return this.journal;
	}

	public int getLogQueryMaxChars() {
		return this.maxLogQueryMaxChars;
	}
	public AmiRelayRoutes getRouter() {
		return this.router;
	}
	public AmiRelayScriptManager getScriptManager() {
		return this.scriptManager;
	}
	public Map<String, AmiRelayCenterDefinition> getCentersByName() {
		return this.centersByName;
	}
	//	public int getDefaultTimeoutMs() {
	//		return this.scriptManager.getDefaultTimeoutMs();
	//	}
	public CalcFrameStack createStackFrame() {
		return new TopCalcFrameStack(new TablesetImpl(), this.scriptManager.getMethodFactory(), EmptyCalcFrame.INSTANCE);
	}
	public AmiRelayTransformManager getTransformManager() {
		return this.transformManager;
	}
	public int getCheckConfigFilesChangedPeriodMs() {
		return checkConfigFilesChangedPeriodMs;
	}
}
