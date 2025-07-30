package com.vortex.eye.state;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.container.Partition;
import com.f1.container.RequestMessage;
import com.f1.container.impl.BasicState;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CachedFile;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.ReverseComparator;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.VortexBuildResult;
import com.f1.vortexcommon.msg.eye.VortexDeployment;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.VortexEyeAuditTrailRule;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.VortexEyeClientEvent;
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.vortex.eye.VortexEyeChangesMessageBuilder;
import com.vortex.eye.VortexEyeDbService;
import com.vortex.eye.VortexEyeJournal;
import com.vortex.eye.VortexEyeUtils;
import com.vortex.eye.evaluators.VortexAbstractRevisionEvaluator;
import com.vortex.eye.evaluators.VortexCrontabEvaluator;
import com.vortex.eye.evaluators.VortexFileSystemEvaluator;
import com.vortex.eye.evaluators.VortexMachineEvaluator;
import com.vortex.eye.evaluators.VortexNetAddressEvaluator;
import com.vortex.eye.evaluators.VortexNetConnectionEvaluator;
import com.vortex.eye.evaluators.VortexNetLinkEvaluator;
import com.vortex.eye.evaluators.VortexProcessEvaluator;
import com.vortex.eye.itinerary.VortexEyeItinerary;

public class VortexEyeState extends BasicState {

	private static final Logger log = Logger.getLogger(VortexEyeState.class.getName());

	public static final int MAX_CLIENT_EVENTS = 1000;

	final private Map<Class<?>, VortexAbstractRevisionEvaluator<?>> evaluators = new LinkedHashMap<Class<?>, VortexAbstractRevisionEvaluator<?>>();

	private VortexEyeChangesMessageBuilder changesMsgBuilder;

	public VortexEyeState() {
		addEvaluator(new VortexMachineEvaluator());
		addEvaluator(new VortexFileSystemEvaluator());
		addEvaluator(new VortexProcessEvaluator());
		addEvaluator(new VortexNetAddressEvaluator());
		addEvaluator(new VortexNetLinkEvaluator());
		addEvaluator(new VortexNetConnectionEvaluator());
		addEvaluator(new VortexCrontabEvaluator());
	}
	private void addEvaluator(VortexAbstractRevisionEvaluator<?> eval) {
		CH.putOrThrow(evaluators, eval.getAgentType(), eval);
	}
	public <T extends VortexAgentEntity> VortexAbstractRevisionEvaluator<T> getEvaluator(T entity) {
		return (VortexAbstractRevisionEvaluator<T>) evaluators.get(entity.askSchema().askOriginalType());
	}

	@Override
	public void setPartition(Partition partition) {
		super.setPartition(partition);
		for (VortexAbstractRevisionEvaluator<?> evaluator : evaluators.values())
			evaluator.setTools(partition.getContainer().getTools());
		this.changesMsgBuilder = new VortexEyeChangesMessageBuilder(this);
	}

	//Agent Events
	//private List<VortexAgentEvent> agentEvents = new ArrayList<VortexAgentEvent>();
	//public List<VortexAgentEvent> getAgentEvents() {
	//return agentEvents;
	//}
	//public void setAgentEvents(List<VortexAgentEvent> agentEvents) {
	//this.agentEvents = agentEvents;
	//}
	//public void registerEvent(VortexAgentEvent event) {
	//agentEvents.add(event);
	//}

	//db servers
	private Map<Long, VortexAgentDbServer> dbServers = new HashMap<Long, VortexAgentDbServer>();

	public Iterable<VortexAgentDbServer> getDbServers() {
		return dbServers.values();
	}

	public void addDbServer(VortexAgentDbServer server) {
		server.lock();
		dbServers.put(server.getId(), server);
	}
	public VortexAgentDbServer getDbServer(long serverId) {
		return dbServers.get(serverId);
	}

	public int getDbServersCount() {
		return dbServers.size();
	}
	public void removeDbServer(long id) {
		CH.removeOrThrow(dbServers, id);
	}

	//metadata rules
	private LongKeyMap<VortexEyeMetadataField> metadataFields = new LongKeyMap<VortexEyeMetadataField>();
	private Map<String, VortexEyeMetadataField> metadataFieldsByKeyCode = new HashMap<String, VortexEyeMetadataField>();

	public void addMetadataField(VortexEyeMetadataField metadataField) {
		metadataField.lock();
		metadataFields.put(metadataField.getId(), metadataField);
		metadataFieldsByKeyCode.put(metadataField.getKeyCode(), metadataField);
	}
	public VortexEyeMetadataField getMetadataField(long id) {
		return metadataFields.get(id);
	}
	public VortexEyeMetadataField getMetadataFieldByKeyCode(String keyCode) {
		return metadataFieldsByKeyCode.get(keyCode);
	}
	public Iterable<VortexEyeMetadataField> getMetadataFields() {
		return metadataFields.values();
	}
	public VortexEyeMetadataField removeMetadataField(long id) {
		VortexEyeMetadataField r = metadataFields.remove(id);
		if (r != null)
			metadataFieldsByKeyCode.remove(r.getKeyCode());
		return r;
	}

	public int getMetadataFieldCounts() {
		return metadataFields.size();
	}

	//audit trail rules
	private LongKeyMap<VortexEyeAuditTrailRule> auditTrailRules = new LongKeyMap<VortexEyeAuditTrailRule>();

	public void addAuditTrailRule(VortexEyeAuditTrailRule auditTrailRule) {
		auditTrailRule.lock();
		auditTrailRules.put(auditTrailRule.getId(), auditTrailRule);
	}
	public VortexEyeAuditTrailRule getAuditTrailRule(long id) {
		return auditTrailRules.get(id);
	}
	public Iterable<VortexEyeAuditTrailRule> getAuditTrailRules() {
		return auditTrailRules.values();
	}
	public VortexEyeAuditTrailRule removeAuditTrailRule(long id) {
		return auditTrailRules.remove(id);
	}

	public int getAuditTrailRulesCount() {
		return metadataFields.size();
	}

	//expectations
	private LongKeyMap<VortexExpectation> expectations = new LongKeyMap<VortexExpectation>();

	public void addExpectation(VortexExpectation expectation) {
		expectation.lock();
		expectations.put(expectation.getId(), expectation);
	}
	public VortexExpectation getExpectation(long id) {
		return expectations.get(id);
	}
	public Iterable<VortexExpectation> getExpectations() {
		return expectations.values();
	}
	public VortexExpectation removeExpectation(long id) {
		return expectations.remove(id);
	}
	public int getExpectationsCount() {
		return expectations.size();
	}

	//backup destinations
	private LongKeyMap<VortexEyeBackupDestination> backupDestinations = new LongKeyMap<VortexEyeBackupDestination>();

	public void addBackupDestination(VortexEyeBackupDestination dest) {
		dest.lock();
		backupDestinations.put(dest.getId(), dest);
	}
	public VortexEyeBackupDestination getBackupDestination(long id) {
		return backupDestinations.get(id);
	}
	public Iterable<VortexEyeBackupDestination> getBackupDestinations() {
		return backupDestinations.values();
	}
	public VortexEyeBackupDestination removeBackupDestination(long id) {
		return backupDestinations.remove(id);
	}
	public int getBackupDestinationsCount() {
		return backupDestinations.size();
	}

	//backups
	private LongKeyMap<VortexEyeBackup> backups = new LongKeyMap<VortexEyeBackup>();

	public void addBackup(VortexEyeBackup dest) {
		dest.lock();
		backups.put(dest.getId(), dest);
	}
	public VortexEyeBackup getBackup(long id) {
		return backups.get(id);
	}
	public Iterable<VortexEyeBackup> getBackups() {
		return backups.values();
	}
	public VortexEyeBackup removeBackup(long id) {
		return backups.remove(id);
	}
	public int getBackupsCount() {
		return backupDestinations.size();
	}

	//build procedures
	private LongKeyMap<VortexBuildProcedure> buildProcedures = new LongKeyMap<VortexBuildProcedure>();

	public void addBuildProcedure(VortexBuildProcedure buildProcedure) {
		buildProcedure.lock();
		buildProcedures.put(buildProcedure.getId(), buildProcedure);
	}
	public VortexBuildProcedure getBuildProcedure(long id) {
		return buildProcedures.get(id);
	}
	public Iterable<VortexBuildProcedure> getBuildProcedures() {
		return buildProcedures.values();
	}
	public VortexBuildProcedure removeBuildProcedure(long id) {
		return buildProcedures.remove(id);
	}
	public int getBuildProceduresCount() {
		return buildProcedures.size();
	}

	//deployments
	private LongKeyMap<VortexDeployment> deployments = new LongKeyMap<VortexDeployment>();

	public void addDeployment(VortexDeployment deployment) {
		deployment.lock();
		deployments.put(deployment.getId(), deployment);
	}
	public VortexDeployment getDeployment(long id) {
		return deployments.get(id);
	}
	public Iterable<VortexDeployment> getDeployments() {
		return deployments.values();
	}
	public VortexDeployment removeDeployment(long id) {
		return deployments.remove(id);
	}
	public int getDeploymentsCount() {
		return deployments.size();
	}

	//deployment sets
	private LongKeyMap<VortexDeploymentSet> deploymentSets = new LongKeyMap<VortexDeploymentSet>();

	public void addDeploymentSet(VortexDeploymentSet deploymentSet) {
		deploymentSet.lock();
		deploymentSets.put(deploymentSet.getId(), deploymentSet);
	}
	public VortexDeploymentSet getDeploymentSet(long id) {
		return deploymentSets.get(id);
	}
	public Iterable<VortexDeploymentSet> getDeploymentSets() {
		return deploymentSets.values();
	}
	public VortexDeploymentSet removeDeploymentSet(long id) {
		return deploymentSets.remove(id);
	}
	public int getDeploymentSetsCount() {
		return deploymentSets.size();
	}

	//build results
	private LongKeyMap<VortexBuildResult> buildResults = new LongKeyMap<VortexBuildResult>();

	public void addBuildResult(VortexBuildResult buildResult) {
		if (AH.length(buildResult.getData()) > VortexEyeDbService.MAX_QUERY_BLOB_RESULT_LENGTH)
			throw new IllegalArgumentException("buildResult should not have data: " + buildResult.getId());
		buildResult.lock();
		buildResults.put(buildResult.getId(), buildResult);
	}
	public VortexBuildResult getBuildResult(long id) {
		return buildResults.get(id);
	}
	public Iterable<VortexBuildResult> getBuildResults() {
		return buildResults.values();
	}
	public VortexBuildResult removeBuildResult(long id) {
		return buildResults.remove(id);
	}
	public int getBuildResultsCount() {
		return buildResults.size();
	}

	//scheduledTasks
	private LongKeyMap<VortexEyeScheduledTask> scheduledTasks = new LongKeyMap<VortexEyeScheduledTask>();

	public void addScheduledTask(VortexEyeScheduledTask scheduledTask) {
		scheduledTask.lock();
		scheduledTasks.put(scheduledTask.getId(), scheduledTask);
	}
	public VortexEyeScheduledTask getScheduledTask(long id) {
		return scheduledTasks.get(id);
	}
	public Iterable<VortexEyeScheduledTask> getScheduledTasks() {
		return scheduledTasks.values();
	}
	public VortexEyeScheduledTask removeScheduledTask(long id) {
		return scheduledTasks.remove(id);
	}
	public int getScheduledTasksCount() {
		return scheduledTasks.size();
	}

	private Map<String, VortexEyeAgentState> agentsByPuid = new HashMap<String, VortexEyeAgentState>();
	private LongKeyMap<VortexEyeF1AppState> f1AppsById = new LongKeyMap<VortexEyeF1AppState>();
	private Map<String, VortexEyeAgentState> agentsByMachineMuid = new HashMap<String, VortexEyeAgentState>();
	private Map<String, VortexEyeMachineState> machinesByMuid = new HashMap<String, VortexEyeMachineState>();
	private LongKeyMap<VortexEyeMachineState> machinesByMiid = new LongKeyMap<VortexEyeMachineState>();

	public VortexEyeAgentState getAgentByPuid(String puid) {
		return CH.getOrThrow(agentsByPuid, puid);
	}
	public VortexEyeAgentState getAgentByPuidNoThrow(String puid) {
		return agentsByPuid.get(puid);
	}

	public VortexEyeAgentState createAgentState(String processUid, String hostname, int remotePort, long connectedTime) {
		if (SH.isnt(processUid))
			throw new RuntimeException("invalid processUid: " + processUid);
		if (SH.isnt(hostname))
			throw new RuntimeException("invalid hostname: " + hostname);
		VortexEyeAgentState r = new VortexEyeAgentState(this, processUid, hostname, remotePort, connectedTime);
		CH.putOrThrow(agentsByPuid, processUid, r);
		return r;
	}

	//includes those not connected 
	public Collection<VortexEyeMachineState> getAllMachines() {
		return machinesByMuid.values();
	}

	public VortexEyeAgentState removeAgent(String processUid) {
		VortexEyeAgentState r = CH.removeOrThrow(agentsByPuid, processUid);
		for (VortexEyeMachineState machine : r.getMachines())
			machine.setAgentState(null);
		for (VortexEyeF1AppState f1App : r.getF1Apps())
			f1App.setAgentState(null);
		return r;
	}

	public Set<String> getAgentPuids() {
		return agentsByPuid.keySet();
	}
	//public Set<String> getMachineUids() {
	//return agentsByMachineMuid.keySet();
	//}
	public Collection<VortexAbstractRevisionEvaluator<?>> getEvaluators() {
		return evaluators.values();
	}
	public Collection<VortexEyeAgentState> getAgents() {
		return agentsByPuid.values();
	}

	public VortexEyeMachineState createMachineState(VortexAgentMachine machine) {
		final VortexEyeMachineState r = new VortexEyeMachineState(this, machine);
		CH.putOrThrow(machinesByMuid, r.getMuid(), r);
		machinesByMiid.putOrThrow(r.getMiid(), r);
		return r;
	}
	public VortexEyeF1AppState createF1AppState(F1AppInstance f1App) {
		final VortexEyeF1AppState r = new VortexEyeF1AppState(this, f1App);
		return r;
	}

	//called only by the app state itself
	protected void onF1AppStateAgentChanged(VortexEyeF1AppState f1AppState) {
		if (f1AppState.getAgentState() == null)
			f1AppsById.removeOrThrow(f1AppState.getId());
		else
			f1AppsById.put(f1AppState.getId(), f1AppState);
	}

	//called only by the machine state itself
	protected void onMachineStateAgentChanged(VortexEyeMachineState machineState) {
		if (machineState.getAgentState() == null) {
			CH.removeOrThrow(agentsByMachineMuid, machineState.getMuid());
		} else
			agentsByMachineMuid.put(machineState.getMuid(), machineState.getAgentState());
	}

	private long sequenceNumber;

	public long nextSequenceNumber() {
		return ++sequenceNumber;
	}

	public long currentSequenceNumber() {
		return sequenceNumber;
	}

	public VortexEyeF1AppState getF1SnapshotByProcessId(long id) {
		return f1AppsById.getOrThrow(id);
	}

	//cloud Interfaces
	private LongKeyMap<VortexEyeCloudInterface> cloudInterfaces = new LongKeyMap<VortexEyeCloudInterface>();

	public void addCloudInterface(VortexEyeCloudInterface ci) {
		ci.lock();
		cloudInterfaces.put(ci.getId(), ci);
	}
	public VortexEyeCloudInterface getCloudInterface(long id) {
		return cloudInterfaces.get(id);
	}
	public Iterable<VortexEyeCloudInterface> getCloudInterfaces() {
		return cloudInterfaces.values();
	}
	public VortexEyeCloudInterface removeCloudInterface(long id) {
		return cloudInterfaces.remove(id);
	}
	public int getCloudInterfacesCount() {
		return cloudInterfaces.size();
	}

	private LongKeyMap<VortextEyeCloudMachineInfo> cloudMachinesInfo = new LongKeyMap<VortextEyeCloudMachineInfo>();

	public void addCloudMachineInfo(VortextEyeCloudMachineInfo mi) {
		cloudMachinesInfo.put(mi.getId(), mi);
	}
	public VortextEyeCloudMachineInfo getCloudMachineInfo(long id) {
		return this.cloudMachinesInfo.get(id);
	}
	public VortextEyeCloudMachineInfo removeCloudMachineInfo(long id) {
		return cloudMachinesInfo.remove(id);
	}
	public Iterable<VortextEyeCloudMachineInfo> getCloudMachineInfos() {
		return cloudMachinesInfo.values();
	}

	//backup files
	private LongKeyMap<VortexAgentBackupFile> backupFiles = new LongKeyMap<VortexAgentBackupFile>();
	private LongKeyMap<Map<String, VortexAgentBackupFile>> miidToPathToBackup = new LongKeyMap<Map<String, VortexAgentBackupFile>>();

	public void addBackupFile(VortexAgentBackupFile ci) {
		if (ci.getData() != null)
			throw new IllegalStateException("file has data: " + ci);
		ci.lock();
		backupFiles.put(ci.getId(), ci);
		Node<Map<String, VortexAgentBackupFile>> e = miidToPathToBackup.getNodeOrCreate(ci.getMachineInstanceId());
		Map<String, VortexAgentBackupFile> map = e.getValue();
		if (map == null)
			e.setValue(map = new HashMap<String, VortexAgentBackupFile>());
		map.put(ci.getPath(), ci);
	}
	public VortexAgentBackupFile getBackupFile(long id) {
		return backupFiles.get(id);
	}
	public Iterable<VortexAgentBackupFile> getBackupFiles() {
		return backupFiles.values();
	}

	public VortexAgentBackupFile getBackupFileByMiidAndPath(long miid, String path) {
		Map<String, VortexAgentBackupFile> map = miidToPathToBackup.get(miid);
		return map == null ? null : map.get(path);
	}
	public Collection<VortexAgentBackupFile> getBackupFileByMiid(long miid) {
		Map<String, VortexAgentBackupFile> map = miidToPathToBackup.get(miid);
		return CH.noNull(map).values();
	}
	public VortexAgentBackupFile removeBackupFile(long id) {
		VortexAgentBackupFile r = backupFiles.remove(id);
		if (r != null) {
			Map<String, VortexAgentBackupFile> map = miidToPathToBackup.get(r.getMachineInstanceId());
			if (map != null)
				map.remove(r.getPath());
		}
		return r;
	}
	public int getBackupFilesCount() {
		return backupFiles.size();
	}

	/////////////////////
	//Active Itineraries
	/////////////////////
	private int nextItineraryId = 1;;
	private Map<RequestMessage<?>, VortexEyeItinerary<?>> requestsToItineraries = new IdentityHashMap<RequestMessage<?>, VortexEyeItinerary<?>>();
	public LongKeyMap<VortexEyeItinerary<?>> activeItineraries = new LongKeyMap<VortexEyeItinerary<?>>();

	public long generateNextItineraryId() {
		return nextItineraryId++;
	}

	public Iterable<VortexEyeItinerary<?>> getActiveItineraries() {
		return activeItineraries.values();
	}

	public VortexEyeItinerary<?> popItineraryForRequest(RequestMessage<?> requestMessage) {
		VortexEyeItinerary<?> r = requestsToItineraries.remove(requestMessage);
		if (r == null)
			throw new IllegalStateException("request not associated with an itinerary: " + requestMessage);
		return r;
	}

	public void pushRequestForItinerary(VortexEyeItinerary<?> itinerary, RequestMessage<?> requestMessage) {
		CH.putOrThrow(requestsToItineraries, requestMessage, itinerary);
	}

	public void addItinerary(VortexEyeItinerary<?> itinerary) {
		this.activeItineraries.put(itinerary.getItineraryId(), itinerary);
	}
	public void removeItinerary(VortexEyeItinerary<?> itinerary) {
		VortexEyeItinerary<?> r = this.activeItineraries.remove(itinerary.getItineraryId());
		if (r == null)
			throw new RuntimeException("itinerary not found: " + itinerary);
		for (RequestMessage<?> req : itinerary.getPendingRequests())
			popItineraryForRequest(req);
	}
	public long createNextId() {
		return getPartition().getContainer().getServices().getUidGenerator(VortexEyeUtils.FOUNTAIN_ID).createNextId();
	}
	public VortexEyeMachineState getMachineByMuidNoThrow(String machineUid) {
		return machinesByMuid.get(machineUid);
	}
	public VortexEyeMachineState getMachineByMiidNoThrow(long miid) {
		return machinesByMiid.get(miid);
	}

	public VortexEyeChangesMessageBuilder getChangesMessageBuilder() {
		this.changesMsgBuilder.reset();
		return this.changesMsgBuilder;
	}
	public List<VortexDeployment> getDeploymentsByMachineUid(String machineUid) {
		List<VortexDeployment> r = new ArrayList<VortexDeployment>();
		for (VortexDeployment dep : this.deployments.values()) {
			if (OH.eq(machineUid, dep.getTargetMachineUid())) {
				r.add(dep);
			}
		}
		return r;
	}
	public VortexEyeMachineState findMachineByHostName(String host) {
		for (VortexEyeMachineState m : getAllMachines()) {
			if (m.getMachine().getHostName().equals(host))
				return m;
		}
		return null;
	}
	public void removeMachine(VortexEyeMachineState mstate) {
		CH.removeOrThrow(machinesByMuid, mstate.getMuid());
		machinesByMiid.remove(mstate.getMiid());
		agentsByMachineMuid.remove(mstate.getMuid());
		if (mstate.getPuid() != null)
			CH.removeOrThrow(agentsByPuid, mstate.getPuid());
	}

	public VortexEyeJournal getJournal() {
		return journal;
	}
	public void setJournal(VortexEyeJournal journal) {
		this.journal = journal;
	}

	private VortexEyeJournal journal;

	private final List<VortexEyeClientEvent> clientEvents = new ArrayList<VortexEyeClientEvent>();
	private int clientEventsCount = 0;

	public void addClientEvent(VortexEyeClientEvent event) {
		clientEvents.add((clientEventsCount++) % MAX_CLIENT_EVENTS, event);
	}
	public Iterable<VortexEyeClientEvent> getClientEvents() {
		return clientEvents;
	}

	private File agentPackageDirectory;

	private Map<String, AgentInterface> agentInterfaces;

	public File getAgentPackageDirectory() {
		return agentPackageDirectory;
	}

	public Map<String, AgentInterface> getAgentInterfaces() {
		return agentInterfaces;
	}

	public void initAgentConfig(File agentPackageDirectory, Map<String, AgentInterface> agentInterfaces, String defaultTargetDirectory, Properties agentProperties) {
		this.agentPackageDirectory = agentPackageDirectory;
		this.agentInterfaces = agentInterfaces;
		this.defaultTargetDirectory = defaultTargetDirectory;
		this.agentProperties = agentProperties;
		LH.info(log, "Agent package versions found under ", IOH.getFullPath(agentPackageDirectory), ": ", getAgentVersions());
	}

	private String defaultTargetDirectory;

	private Map<String, CachedFile> agentPackagesCache = new HashMap<String, CachedFile>();

	private Properties agentProperties;

	public byte[] getAgentPackageData(String version) {
		CachedFile r = agentPackagesCache.get(version);
		if (r == null) {
			r = new CachedFile(new File(getAgentPackageDirectory(), "vortexagent." + version + ".tar.gz"), 1000);
			if (!r.getFile().isFile())
				return null;
			agentPackagesCache.put(version, r);
		}
		return r.getData().getBytes();
	}
	public String getDefaultAgentTargetDirectory() {
		return defaultTargetDirectory;
	}

	public static class AgentInterface {
		final public String hostname;
		final public int port;
		final public boolean isSecure;
		final public String keyFile;
		final public String description;
		final public String keyPassword;

		public AgentInterface(String hostname, int port, boolean isSecure, String keyFile, String keyPassword, String description) {
			this.hostname = hostname;
			this.port = port;
			this.isSecure = isSecure;
			this.keyFile = keyFile;
			this.keyPassword = keyPassword;
			this.description = description;
		}

	}

	public Properties getAgentProperties() {
		return agentProperties;
	}
	public List<String> getAgentVersions() {
		if (this.agentPackageDirectory == null)
			return null;
		List<String> versions = new ArrayList<String>();
		for (String name : this.agentPackageDirectory.list())
			if (name.startsWith("vortexagent.") && name.endsWith(".tar.gz"))
				versions.add(SH.strip(name, "vortexagent.", ".tar.gz", true));
		Collections.sort(versions, new ReverseComparator(SH.COMPARATOR_CASEINSENSITIVE));
		return versions;
	}
	private long toMs(int expiresInSeconds) {
		return MH.toUnsignedInt(expiresInSeconds) * 1000L;
	}

	//	LongKeyMap<VortexAmiAlert> expiringAlerts = new LongKeyMap<VortexAmiAlert>();
	//	long minExpiringAlertTime = Long.MAX_VALUE;//-1 indicates it needs to be calculated
	//	public void addExpiringAmiAlert(VortexAmiAlert alert) {
	//		long expires = toMs(alert.getExpiresInSeconds());
	//		if (expires == 0)
	//			throw new IllegalArgumentException("alert does not expire: " + alert);
	//		expiringAlerts.put(alert.getId(), alert);
	//		if (minExpiringAlertTime != -1L && expires < minExpiringAlertTime)
	//			minExpiringAlertTime = expires;
	//	}
	//
	//	public void removeExpiringAmiAlert(long alertId) {
	//		VortexAmiAlert remove = expiringAlerts.remove(alertId);
	//		if (remove != null && toMs(remove.getExpiresInSeconds()) == minExpiringAlertTime)
	//			minExpiringAlertTime = -1L;
	//	}
	//
	//	public long getMinExpiringAmiAlertTime() {
	//		if (minExpiringAlertTime == -1L) {
	//			minExpiringAlertTime = Long.MAX_VALUE;
	//			if (!expiringAlerts.isEmpty())
	//				for (VortexAmiAlert alert : expiringAlerts.values())
	//					minExpiringAlertTime = Math.min(toMs(alert.getExpiresInSeconds()), minExpiringAlertTime);
	//		}
	//		return minExpiringAlertTime;
	//	}
	//	public Iterable<VortexAmiAlert> getExpiringAmiAlerts() {
	//		return expiringAlerts.values();
	//	}

	//	LongKeyMap<VortexAmiObject> expiringObjects = new LongKeyMap<VortexAmiObject>();
	//	long minExpiringObjectTime = Long.MAX_VALUE;//-1 indicates it needs to be calculated
	//	public void addExpiringAmiObject(VortexAmiObject alert) {
	//		long expires = toMs(alert.getExpiresInSeconds());
	//		if (expires == 0L)
	//			throw new IllegalArgumentException("object does not expire: " + alert);
	//		expiringObjects.put(alert.getId(), alert);
	//		if (minExpiringObjectTime != -1L && expires < minExpiringObjectTime)
	//			minExpiringObjectTime = expires;
	//	}
	//
	//	public void removeExpiringAmiObject(long alertId) {
	//		VortexAmiObject remove = expiringObjects.remove(alertId);
	//		if (remove != null && toMs(remove.getExpiresInSeconds()) == minExpiringObjectTime)
	//			minExpiringObjectTime = -1L;
	//	}
	//
	//	public long getMinExpiringAmiObjectTime() {
	//		if (minExpiringObjectTime == -1L) {
	//			minExpiringObjectTime = Long.MAX_VALUE;
	//			if (!expiringObjects.isEmpty())
	//				for (VortexAmiObject alert : expiringObjects.values())
	//					minExpiringObjectTime = Math.min(toMs(alert.getExpiresInSeconds()), minExpiringObjectTime);
	//		}
	//		return minExpiringObjectTime;
	//	}
	//	public Iterable<VortexAmiObject> getExpiringAmiObjects() {
	//		return expiringObjects.values();
	//	}

	//	final IntKeyMap<VortexEyeAmiApplication> amiApplicationsByAppId = new IntKeyMap<VortexEyeAmiApplication>();
	//	//final LongKeyMap<VortexEyeAmiApplication> amiApplications = new LongKeyMap<VortexEyeAmiApplication>();
	//	final LongKeyMap<VortexEyeAmiConnection> amiConnections = new LongKeyMap<VortexEyeAmiConnection>();
	//
	//	public VortexEyeAmiApplication getAmiAppByAppId(short appId) {
	//		return amiApplicationsByAppId.get(appId);
	//	}
	//public VortexEyeAmiApplication getAmiAppById(long id) {
	//return amiApplications.get(id);
	//}

	//	public VortexEyeAmiApplication putAmiApplication(short appId) {
	//		VortexEyeAmiApplication eapp = new VortexEyeAmiApplication(this, appId);
	//		amiApplicationsByAppId.put(appId, eapp);
	//		return eapp;
	//		//amiApplications.putOrThrow(getId(), eapp);
	//	}

	//	public VortexEyeAmiConnection getAmiConnection(long id) {
	//		return amiConnections.get(id);
	//	}
	//	public void putAmiConnection(VortexAmiConnection connection) {
	//		connection.lock();
	//		amiConnections.put(connection.getId(), new VortexEyeAmiConnection(connection));
	//	}
	//	public VortexEyeAmiConnection removeAmiConnection(long id) {
	//		return amiConnections.remove(id);
	//	}
	//	public int getAmiConnectionsCount() {
	//		return amiConnections.size();
	//	}
	//	public Iterable<VortexEyeAmiConnection> getAmiConnections() {
	//		return amiConnections.values();
	//	}

	//	public Iterable<VortexEyeAmiApplication> getAmiApplications() {
	//		return this.amiApplicationsByAppId.values();
	//	}

	//	private OneToOne<String, Short> amiKeyIdLookup = new OneToOne<String, Short>();
	//	private Map<Short, String> pendingNewKeysSink = new HashMap<Short, String>();
	//
	//	public short getAmiKeyId(String text) {
	//		Short r = amiKeyIdLookup.getValue(text);
	//		if (r == null) {
	//			amiKeyIdLookup.put(text, r = (short) (amiKeyIdLookup.size() + 1));//TODO: check for overflow
	//			pendingNewKeysSink.put(r, text);
	//		}
	//		return r;
	//	}
	//	public short getAmiKeyIdNoCreate(String text) {
	//		Short r = amiKeyIdLookup.getValue(text);
	//		if (r == null)
	//			return 0;
	//		return r;
	//	}

	//	public Map<Short, String> popPendingNewAmiKeysSink() {
	//		if (pendingNewKeysSink.size() == 0)
	//			return null;
	//		Map<Short, String> r = new HashMap<Short, String>(pendingNewKeysSink);
	//		pendingNewKeysSink.clear();
	//		return r;
	//	}
	//public Map<Short, String> getAmiStringPoolMap() {
	//return amiKeyIdLookup.toValueKeyMap();
	//}

	//	final private Map<ByteArray, Integer> amiKeyPool = new HashMap<ByteArray, Integer>();
	//	final private IntKeyMap<String> amiKeyToPool = new IntKeyMap<String>();
	//	private ByteArray tmpKey = new ByteArray();

	//final private Map<ByteArray, Integer> pending = new HashMap<ByteArray, Integer>();

	//	public int getAmiStringPool(byte[] data, int start, int len) {
	//		tmpKey.resetNoCheck(data, start, start + len);
	//		Integer value = amiKeyPool.get(tmpKey);
	//		if (value != null)
	//			return value.intValue();
	//
	//		int r = amiKeyPool.size() + 1;
	//		if (MH.getByteDepth(r) > len)
	//			return 0;
	//		ByteArray nw = tmpKey.cloneToClipped();
	//		amiKeyPool.put(nw, r);
	//		amiKeyToPool.put(r, new String(nw.getData(), SH.CHARSET_UTF));
	//		pending.put(nw, r);
	//		return r;
	//	}
	//
	//	public Set<Entry<ByteArray, Integer>> getAmiStringPool() {
	//		return amiKeyPool.entrySet();
	//	}
	//	public byte[] getAmiValuesStringPoolAsBytes() {
	//
	//		return toBytes(amiKeyPool);
	//	}
	//
	//	public byte[] popPendingAmiValuesStringPool() {
	//		byte[] r = toBytes(pending);
	//		if (r.length > 0)
	//			pending.clear();
	//		return r;
	//	}
	//	private byte[] toBytes(Map<ByteArray, Integer> m) {
	//		if (m.size() == 0)
	//			return OH.EMPTY_BYTE_ARRAY;
	//		int len = m.size() * 5;//5=key+string length
	//		for (ByteArray e : m.keySet())
	//			len += e.length();
	//		byte[] r = new byte[len];
	//		int pos = 0;
	//		for (Entry<ByteArray, Integer> e : m.entrySet()) {
	//			ByteHelper.writeInt(e.getValue(), r, pos);
	//			pos += 4;
	//			ByteHelper.writeByte((byte) e.getKey().length(), r, pos);
	//			pos++;
	//			pos = ByteHelper.writeBytes(e.getKey().getData(), r, pos);
	//		}
	//		return r;
	//	}
	//	public String getAmiKeyString(short id) {
	//		String r = this.amiKeyIdLookup.getKey(id);
	//		if (r != null)
	//			return r;
	//		return id == 0 ? null : ("<KEY " + id + ">");
	//	}
	//	public String getAmiValueString(int id) {
	//		return this.amiKeyToPool.get(id);
	//	}

	//	private LongKeyMap<Mutable.Long> category_appId_ObjType_ParamKey_ParamValue_To_Count = new LongKeyMap<Mutable.Long>();
	//	public void incrementAmiSchemaCount(byte category, short appId, short objType, short paramKey, byte type, int count) {
	//		if (type == 1 || count == 0)
	//			return;
	//		long key = ((long) category << 56) | ((long) appId << 40) | ((long) objType << 24) | ((long) paramKey << 8) | type;
	//		Mutable.Long val = category_appId_ObjType_ParamKey_ParamValue_To_Count.get(key);
	//		if (val == null) {
	//			category_appId_ObjType_ParamKey_ParamValue_To_Count.put(key, val = new Mutable.Long(count));
	//			//debugAmiSchemasCount();
	//		} else {
	//			if ((val.value += count) == 0)
	//				category_appId_ObjType_ParamKey_ParamValue_To_Count.remove(key);
	//			//debugAmiSchemasCount();
	//		}
	//	}
	//	public void debugAmiSchemasCount() {
	//		BasicTable t = new BasicTable(new Object[] { "cat", "app", "obj", "param", "type", "count" });
	//		for (Node<com.f1.utils.mutable.Mutable.Long> node : category_appId_ObjType_ParamKey_ParamValue_To_Count) {
	//			long key = node.getLongKey();
	//			t.getRows().addRow(//
	//					(byte) ((key >> 56) & 0xff), //
	//					getAmiKeyString((short) ((key >> 40) & 0xffff)), //
	//					getAmiKeyString((short) ((key >> 24) & 0xffff)), //
	//					getAmiKeyString((short) ((key >> 8) & 0xffff)), //
	//					((byte) (key >> 0)), //
	//					node.getValue());
	//		}
	//		System.out.println(t);
	//	}
	//	public List<VortexAmiSchemaRecord> getAmiSchema() {
	//		List<VortexAmiSchemaRecord> r = new ArrayList<VortexAmiSchemaRecord>(category_appId_ObjType_ParamKey_ParamValue_To_Count.size());
	//		BasicTable t = new BasicTable(new Object[] { "cat", "app", "obj", "param", "type", "count" });
	//		for (Node<com.f1.utils.mutable.Mutable.Long> node : category_appId_ObjType_ParamKey_ParamValue_To_Count) {
	//			final long key = node.getLongKey();
	//			final byte cat = (byte) ((key >> 56) & 0xff);
	//			final short app = ((short) ((key >> 40) & 0xffff));
	//			final short obj = ((short) ((key >> 24) & 0xffff));
	//			final short param = ((short) ((key >> 8) & 0xffff));
	//			final byte type = ((byte) (key >> 0));
	//			final long count = node.getValue().value;
	//			final VortexAmiSchemaRecord record = nw(VortexAmiSchemaRecord.class);
	//			record.setCategory(cat);
	//			record.setAppId(app);
	//			record.setParam(param);
	//			record.setValueType(type);
	//			record.setObjectType(obj);
	//			record.setCount(count);
	//			r.add(record);
	//		}
	//		return r;
	//	}

	//	private IntKeyMap<IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>>> amiIndexes = new IntKeyMap<IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>>>();
	//	public void addAmiIndex(String objectType, String param) {
	//		short typeKid = getAmiKeyId(objectType);
	//		short paramKid = getAmiKeyId(param);
	//		IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>> indexForType = amiIndexes.get(typeKid);
	//		if (indexForType == null)
	//			amiIndexes.put(typeKid, indexForType = new IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>>());
	//		Map<Object, CompactLongKeyMap<VortexAmiObject>> indexForParam = indexForType.get(paramKid);
	//		if (indexForParam != null)
	//			throw new RuntimeException("Duplicate ami Object index: " + objectType + "." + param);
	//		indexForType.put(paramKid, new HashMap<Object, CompactLongKeyMap<VortexAmiObject>>());
	//	}
	//
	//	public Map<Object, CompactLongKeyMap<VortexAmiObject>> getIndexesByType(short typeKid, short paramKid) {
	//		IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>> r = amiIndexes.get(typeKid);
	//		if (r == null)
	//			return null;
	//		Map<Object, CompactLongKeyMap<VortexAmiObject>> r2 = r.get(paramKid);
	//		return r2;
	//	}
	//	public void addAmiObjectToIndexes(VortexAmiObject object) {
	//		IntKeyMap<Map<Object, CompactLongKeyMap<VortexAmiObject>>> idx = amiIndexes.get(object.getType());
	//		if (idx == null)
	//			return;
	//		for (IntKeyMap.Node<Map<Object, CompactLongKeyMap<VortexAmiObject>>> i : idx) {
	//			Object value = VortexEyeAmiUtils.getValue(object.getParams(), (short) i.getIntKey(), this);
	//			if (value != null) {
	//				CompactLongKeyMap<VortexAmiObject> entries = i.getValue().get(value);
	//				if (entries == null)
	//					i.getValue().put(value, entries = new CompactLongKeyMap<VortexAmiObject>(VortexEyeAmiUtils.KEYGETTER, 16));
	//				entries.put(object);
	//			}
	//		}
	//	}
	//	public void clearAmiData() {
	//		for (VortexEyeAmiApplication i : getAmiApplications()) {
	//			i.clearData();
	//		}
	//		LongArrayList toRemove = new LongArrayList();
	//		for (Node<com.f1.utils.mutable.Mutable.Long> node : category_appId_ObjType_ParamKey_ParamValue_To_Count) {
	//			long key = node.getLongKey();
	//			byte cat = (byte) ((key >> 56) & 0xff);
	//			switch (cat) {
	//				case VortexAmiEntity.CATEGORY_ALERT:
	//				case VortexAmiEntity.CATEGORY_OBJECT:
	//					toRemove.add(key);
	//					break;
	//			}
	//		}
	//		for (long l : toRemove)
	//			category_appId_ObjType_ParamKey_ParamValue_To_Count.remove(l);
	//	}
}
