package com.vortex.agent.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.container.RequestMessage;
import com.f1.container.impl.BasicState;
import com.f1.utils.ByteArray;
import com.f1.utils.CH;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LocalToolkit;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.BasicFromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.ids.BasicIdGenerator;
import com.f1.utils.ids.IdGenerator;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.vortex.agent.itinerary.VortexAgentItinerary;

public class VortexAgentState extends BasicState {

	/////////////////////
	//Eye Connectivity status
	/////////////////////
	private boolean isEyeConnected;
	public void setIsEyeConnected(boolean b) {
		this.isEyeConnected = b;
	}
	public boolean getIsEyeConnected() {
		return this.isEyeConnected;
	}

	private boolean isSnapshotSent;
	public void setIsSnapshotSentToEye(boolean b) {
		this.isSnapshotSent = b;
	}
	public boolean getIsSnapshotSentToEye() {
		return this.isSnapshotSent;
	}

	/////////////////////
	//Active Itineraries
	/////////////////////
	private int nextItineraryId = 1;;
	private Map<RequestMessage<?>, VortexAgentItinerary<?>> requestsToItineraries = new IdentityHashMap<RequestMessage<?>, VortexAgentItinerary<?>>();
	public LongKeyMap<VortexAgentItinerary<?>> activeItineraries = new LongKeyMap<VortexAgentItinerary<?>>();

	public long generateNextItineraryId() {
		return nextItineraryId++;
	}

	public VortexAgentItinerary<?> popItineraryForRequest(RequestMessage<?> requestMessage) {
		VortexAgentItinerary<?> r = requestsToItineraries.remove(requestMessage);
		if (r == null)
			throw new IllegalStateException("request not associated with an itinerary: " + requestMessage);
		return r;
	}

	public void pushRequestForItinerary(VortexAgentItinerary<?> itinerary, RequestMessage<?> requestMessage) {
		CH.putOrThrow(requestsToItineraries, requestMessage, itinerary);
	}

	public void addItinerary(VortexAgentItinerary<?> itinerary) {
		this.activeItineraries.put(itinerary.getItineraryId(), itinerary);
	}
	public void removeItinerary(VortexAgentItinerary<?> itinerary) {
		VortexAgentItinerary<?> r = this.activeItineraries.remove(itinerary.getItineraryId());
		if (r == null)
			throw new RuntimeException("itinerary not found: " + itinerary);
		for (RequestMessage<?> req : itinerary.getPendingRequests())
			popItineraryForRequest(req);
	}

	/////////////////////
	//Machine information
	/////////////////////
	private Map<String, VortexAgentEntity> entities = new HashMap<String, VortexAgentEntity>();
	private VortexAgentMachine machine;

	public Set<String> getEntityKeys() {
		return entities.keySet();
	}

	public VortexAgentEntity getEntityByKey(String key) {
		return entities.get(key);
	}
	public void addEntity(String key, VortexAgentEntity entity) {
		entities.put(key, entity);
	}
	public VortexAgentEntity removeEntity(String key) {
		return entities.remove(key);
	}
	public Collection<VortexAgentEntity> getEntities() {
		return entities.values();
	}
	public Map<String, VortexAgentEntity> getEntitiesMap() {
		return entities;
	}

	public void setMachine(VortexAgentMachine machine) {
		this.machine = machine;
	}
	public VortexAgentMachine getMachine() {
		return this.machine;
	}

	/////////////////////
	//F1 Applications
	/////////////////////
	private Map<String, VortexAgentF1AppState> f1Apps = new HashMap<String, VortexAgentF1AppState>();

	public Set<String> getJavaProcessUids() {
		return f1Apps.keySet();
	}

	public Iterable<VortexAgentF1AppState> getApps() {
		return f1Apps.values();
	}

	public void addF1App(VortexAgentF1AppState testTrackAgentSnapshot) {
		CH.putOrThrow(this.f1Apps, testTrackAgentSnapshot.getF1AppInstance().getProcessUid(), testTrackAgentSnapshot);
	}

	public VortexAgentF1AppState removeF1AppNoThrow(String processUid) {
		return f1Apps.remove(processUid);
	}

	public VortexAgentF1AppState getF1AppByProcessUidNoThrow(String processUid) {
		return f1Apps.get(processUid);
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
	private LongKeyMap<VortexEyeAuditTrailRule> auditTrailRules = new LongKeyMap<VortexEyeAuditTrailRule>();
	public void addAuditTrailRule(VortexEyeAuditTrailRule auditTrailRule) {
		auditTrailRule.lock();
		auditTrailRules.put(auditTrailRule.getId(), auditTrailRule);
	}

	public VortexEyeAuditTrailRule getAuditTrailRule(long serverId) {
		return auditTrailRules.get(serverId);
	}

	public Iterable<VortexEyeAuditTrailRule> getAuditTrailRules() {
		return auditTrailRules.values();
	}

	public VortexEyeAuditTrailRule removeAuditTrailRule(long id) {
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

	/////////////////////
	//Deployments
	/////////////////////
	private LongKeyMap<VortexAgentDeploymentWrapper> deployments = new LongKeyMap<VortexAgentDeploymentWrapper>();

	public void addDeployment(VortexAgentDeploymentWrapper deployment) {
		deployments.put(deployment.getId(), deployment);
	}
	public VortexAgentDeploymentWrapper getDeployment(long id) {
		return deployments.get(id);
	}
	public Iterable<VortexAgentDeploymentWrapper> getDeployments() {
		return deployments.values();
	}
	public VortexAgentDeploymentWrapper removeDeployment(long deploymentId) {
		return deployments.remove(deploymentId);
	}

	public void clearDeployments() {
		deployments.clear();
	}

	public int getDeploymentsCount() {
		return deployments.size();
	}

	/////////////////////
	//Backups
	/////////////////////
	private LongKeyMap<VortexEyeBackup> backups = new LongKeyMap<VortexEyeBackup>();
	public void addBackup(VortexEyeBackup backup) {
		backups.put(backup.getId(), backup);
	}
	public VortexEyeBackup getBackup(long id) {
		return backups.get(id);
	}
	public Iterable<VortexEyeBackup> getBackups() {
		return backups.values();
	}
	public VortexEyeBackup removeBackup(long deploymentId) {
		return backups.remove(deploymentId);
	}

	public void clearBackups() {
		backups.clear();
	}

	public int getBackupsCount() {
		return backups.size();
	}
	private IdGenerator<Long> generator = new BasicIdGenerator(-100000, -1);//start at negative 

	//public List<VortexDeployment> getDeploymentObjects() {
	//List<VortexDeployment> r = new ArrayList<VortexDeployment>();
	//for (VortexAgentDeploymentState dep : this.deployments.values())
	//r.add(dep.getDeployment());
	//return r;
	//}
	public IdGenerator<Long> getGenerator() {
		return generator;
	}
	public long createNextId() {
		return getGenerator().createNextId();
	}
	public String getMachineUid() {
		return machine == null ? null : machine.getMachineUid();
	}
	public Iterable<VortexDeployment> getVortexDeployments() {
		final List<VortexDeployment> r = new ArrayList<VortexDeployment>(this.deployments.size());
		for (VortexAgentDeploymentWrapper i : this.deployments.values())
			r.add(i.getDeployment());
		return r;
	}

	////////// AMI STUFF //////////////

	//	private Map<ByteArray, Short> amiKeyIdLookup = new HasherMap<ByteArray, Short>();
	//	private Map<Short, String> pendingNewKeysSink = new HashMap<Short, String>();

	final private ByteArray tmp = new ByteArray();

	//	public short getAmiKeyId(byte[] data, int start, int end) {
	//		tmp.reset(data, start, end);
	//		Short r = amiKeyIdLookup.get(tmp);
	//		if (r == null) {
	//			amiKeyIdLookup.put(tmp.cloneToClipped(), r = (short) (amiKeyIdLookup.size() + 1));//TODO: check for overflow
	//			pendingNewKeysSink.put(r, new String(data, start, end - start));
	//		}
	//		return r;
	//	}
	//	public Map<Short, String> getPendingNewKeysSink() {
	//		return pendingNewKeysSink;
	//	}
	//	public Map<Short, String> getAmiStringKeys() {
	//		Map<Short, String> r = new HashMap<Short, String>(amiKeyIdLookup.size());
	//		for (Entry<ByteArray, Short> e : amiKeyIdLookup.entrySet())
	//			r.put(e.getValue(), new String(e.getKey().getData()));
	//		return r;
	//	}
	private ObjectToByteArrayConverter converter = new ObjectToByteArrayConverter(true);
	private FastByteArrayDataInputStream converterIn = new FastByteArrayDataInputStream(OH.EMPTY_BYTE_ARRAY);
	private FastByteArrayDataOutputStream converterOut = new FastByteArrayDataOutputStream();
	private BasicFromByteArrayConverterSession converterSession = new BasicFromByteArrayConverterSession(converter, converterIn);

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

	//	public byte[] convertAmiParamsToIds(byte[] in) {
	//		try {
	//			if (in == null)
	//				return null;
	//			converterIn.reset(in);
	//			converterOut.reset();
	//			int size = converterIn.readShort();
	//			converterOut.writeShort(size);
	//			int pos = 2;//first 2 bytes are size
	//			for (int i = 0; i < size; i++) {
	//				int len = converterIn.readByte();
	//				pos++;//read length
	//				converterOut.writeShort(getAmiKeyId(in, pos, pos + len));
	//				pos += len;
	//				converterIn.skip(len);
	//			}
	//			converterOut.write(in, pos, in.length - pos);
	//			return converterOut.toByteArray();
	//		} catch (IOException e) {
	//			throw OH.toRuntime(e);
	//		}
	//	}

	public LocalToolkit getToolkit() {
		return toolkit;
	}

	final private LocalToolkit toolkit = new LocalToolkit();

	//	final private LongKeyMap<VortexAgentAmiConnectionState> amiConnections = new LongKeyMap<VortexAgentAmiConnectionState>();
	//
	//	public void addAmiConnection(AgentAmiConnectionMessage action) {
	//		amiConnections.putOrThrow(action.getConnectionId(), new VortexAgentAmiConnectionState(action));
	//	}
	//
	//	public VortexAgentAmiConnectionState getAmiConnection(int connectionId) {
	//		return amiConnections.get(connectionId);
	//	}
	//
	//	public VortexAgentAmiConnectionState removeAmiConnection(int connectionId) {
	//		return amiConnections.remove(connectionId);
	//	}
	//
	//	public Iterable<VortexAgentAmiConnectionState> getActiveAmiLogins() {
	//		return amiConnections.values();
	//	}

}
