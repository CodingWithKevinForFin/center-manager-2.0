package com.vortex.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.LongIterable;
import com.f1.povo.f1app.F1AppEntity;
import com.f1.povo.f1app.F1AppEvent;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongKeyMap.Node;
import com.f1.utils.structs.LongKeyMapSource;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.MapInMapInMap;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentCron;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentEvent;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
import com.f1.vortexcommon.msg.agent.VortexAgentMachineEventStats;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.f1.vortexcommon.msg.agent.VortexAgentNetConnection;
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.f1.vortexcommon.msg.agent.VortexAgentProcess;
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
import com.f1.vortexcommon.msg.eye.VortexEyeEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeJournalReport;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.VortextEyeCloudMachineInfo;
import com.vortex.client.VortexClientF1AppState.AgentWebObject;
import com.vortex.web.VortexWebEyeService;

public class VortexClientManager {

	final private static Logger log = LH.get(VortexClientManager.class);

	private static final int MAX_CLIENT_EVENTS = 1000;

	final private Map<String, VortexClientNetAddress> addressesByIp = new HashMap<String, VortexClientNetAddress>();
	final private LongKeyMap<VortexClientMachine> agentMachines = new LongKeyMap<VortexClientMachine>();
	final private Map<String, VortexClientMachine> agentMachinesByMachineUid = new HashMap<String, VortexClientMachine>();
	final private LongKeyMap<VortexClientAuditTrailRule> auditTrailRules = new LongKeyMap<VortexClientAuditTrailRule>();

	final private MapInMapInMap<String, Byte, Long, VortexClientExpectation> matchedExpectationsByMachineUidAndTargetTypeAndId = new MapInMapInMap<String, Byte, Long, VortexClientExpectation>();
	final private MapInMapInMap<String, Byte, Long, VortexClientExpectation> unmatchedExpectationsByMachineUidAndTargetTypeAndId = new MapInMapInMap<String, Byte, Long, VortexClientExpectation>();
	final private LongKeyMap<VortexClientExpectation> expectations = new LongKeyMap<VortexClientExpectation>();
	final private LongKeyMap<VortexClientBuildProcedure> buildProcedures = new LongKeyMap<VortexClientBuildProcedure>();
	final private LongKeyMap<VortexClientBuildResult> buildResults = new LongKeyMap<VortexClientBuildResult>();
	final private LongKeyMap<VortexClientMetadataField> metadataFields = new LongKeyMap<VortexClientMetadataField>();
	final private HashMap<String, VortexClientMetadataField> metadataFieldsByKeyCode = new HashMap<String, VortexClientMetadataField>();
	final private LongKeyMap<VortexClientDeploymentSet> deploymentSets = new LongKeyMap<VortexClientDeploymentSet>();
	final private LongKeyMap<VortexClientScheduledTask> scheduledTasks = new LongKeyMap<VortexClientScheduledTask>();
	final private LongKeyMap<VortexClientBackupDestination> backupDestinations = new LongKeyMap<VortexClientBackupDestination>();
	final private LongKeyMap<VortexClientBackup> backups = new LongKeyMap<VortexClientBackup>();
	final private LongKeyMap<VortexClientCloudInterface> cloudInterfaces = new LongKeyMap<VortexClientCloudInterface>();
	final private LongKeyMap<VortexClientCloudMachineInfo> cloudMachinesInfo = new LongKeyMap<VortexClientCloudMachineInfo>();
	final private MapInMap<Long, Long, VortexClientBackup> backupsByDestIdAndId = new MapInMap<Long, Long, VortexClientBackup>();
	final private MapInMap<Long, Long, VortexClientBackup> backupsByDeploymentIdAndId = new MapInMap<Long, Long, VortexClientBackup>();
	final private MapInMap<String, String, VortexClientBackup> backupsByMachineUidAndPath = new MapInMap<String, String, VortexClientBackup>();
	final private MapInMap<String, String, VortexClientBackupFile> backupFilesByMachineUidAndPath = new MapInMap<String, String, VortexClientBackupFile>();
	final private LongKeyMap<VortexClientDeployment> deployments = new LongKeyMap<VortexClientDeployment>();
	final private MapInMap<Long, Long, VortexClientDeployment> deploymentsByBuildResultIdAndId = new MapInMap<Long, Long, VortexClientDeployment>();
	//final private LongKeyMap<VortexClientAmiApplication> amiApplications = new LongKeyMap<VortexClientAmiApplication>();
	//final private IntKeyMap<VortexClientAmiApplication> amiApplicationsByName = new IntKeyMap<VortexClientAmiApplication>();
	//final private LongKeyMap<VortexClientAmiAlert> amiAlerts = new LongKeyMap<VortexClientAmiAlert>();
	//final private LongKeyMap<VortexClientAmiCommandDef> amiCommandDefs = new LongKeyMap<VortexClientAmiCommandDef>();
	//final private LongKeyMap<VortexClientAmiObject> amiObjects = new LongKeyMap<VortexClientAmiObject>();
	//final private Map<String, LongKeyMap<VortexClientAmiObject>> amiObjectsByType = new HashMap<String, LongKeyMap<VortexClientAmiObject>>();
	private IntKeyMap<VortexEyeJournalReport> eyeJournalReports = new IntKeyMap<VortexEyeJournalReport>();
	private LongKeyMap<VortexClientBackupFile> backupFiles = new LongKeyMap<VortexClientBackupFile>();

	final private List<VortexClientMachineListener> machineListeners = new ArrayList<VortexClientMachineListener>();
	final private List<VortexClientManagerListener> connectListeners = new ArrayList<VortexClientManagerListener>();
	final private List<VortexClientEventListener> clientEventListeners = new ArrayList<VortexClientEventListener>();
	final private List<VortexClientJournalReportListener> reportListeners = new ArrayList<VortexClientJournalReportListener>();
	final private List<VortexClientF1AppStateListener> f1AppListeners = new ArrayList<VortexClientF1AppStateListener>();
	final private List<VortexClientAuditEventsListener> auditEventsListeners = new ArrayList<VortexClientAuditEventsListener>();
	//final private List<VortexClientAmiListener> amiListeners = new ArrayList<VortexClientAmiListener>();

	private LongKeyMap<VortexEntity> entities = new LongKeyMap<VortexEntity>();

	//private LongKeyMap<VortexClientAmiEntity<?>> amiEntities = new LongKeyMap<VortexClientAmiEntity<?>>();

	public void onAgentSnapshotRemoved(long id) {
		VortexClientMachine r = agentMachines.remove(id);
		if (r != null) {
			agentMachinesByMachineUid.get(r.getData().getMachineUid());
			fireRemove(r);
		} else
			LH.warning(log, "remove for unknown vortex agent machine:", id);
	}
	public void onAgentSnapshot(VortexAgentMachine machine) {
		VortexClientMachine existing = agentMachines.get(machine.getId());
		if (machine.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existing == null) {
				LH.warning(log, "remove for unknown vortex agent machine:", machine);
			} else {
				removeMachine(existing.getId());
			}
		} else if (existing == null) {
			existing = new VortexClientMachine(this, machine);
			entities.put(machine.getId(), machine);
			addMachine(existing);
		} else {
			existing.update(machine);
			fireUpdate(existing);
		}

	}

	private void removeMachine(long id) {
		VortexClientMachine r = agentMachines.remove(id);
		if (r != null) {
			agentMachinesByMachineUid.remove(r.getData().getMachineUid());
			fireRemove(r);
		} else
			LH.warning(log, "remove for unknown vortex agent machine:", id);
	}
	private void addMachine(VortexClientMachine existing) {
		agentMachines.put(existing.getId(), existing);
		agentMachinesByMachineUid.put(existing.getData().getMachineUid(), existing);
		fireNew(existing);
	}
	public void onChanges(Iterable<VortexEntity> revisions) {
		for (VortexEntity revision : revisions) {
			onChange(revision);
		}
	}
	//	public void onAmiRemoved(long id) {
	//		VortexClientAmiEntity<?> entity = amiEntities.remove(id);
	//		if (entity == null) {
	//		}
	//		if (entity instanceof VortexClientAmiApplication) {
	//			removeAmiApplication(id);
	//		} else if (entity instanceof VortexClientAmiAlert) {
	//			removeAmiAlert(id);
	//		} else if (entity instanceof VortexClientAmiObject) {
	//			removeAmiObject(id);
	//		} else if (entity instanceof VortexClientAmiCommandDef) {
	//			removeAmiCommandDef(id);
	//		} else {
	//			//LH.warning(log, "Unknown ami remove: ", entity);
	//			return;
	//		}
	//		fireRemoveAmi(entity);
	//	}

	//	public void onAmiAdd(VortexAmiEntity entity) {
	//		if (entity.getRevision() == VortexAgentEntity.REVISION_DONE)
	//			throw new RuntimeException("bad revision: " + entity);
	//		//if (amiEntities.containsKey(entity.getId()))
	//		//throw new RuntimeException("Duplicate ami entity: " + entity);
	//		VortexClientAmiEntity<?> clientEntity = null;
	//		//Node<VortexClientAmiEntity<?>> node = amiEntities.getNodeOrCreate(entity.getId());
	//		//if (node.getValue() != null)
	//		//node.setValue(entity);
	//		if (entity instanceof VortexAmiConnection) {
	//			clientEntity = addAmiApplication((VortexAmiConnection) entity);
	//		} else if (entity instanceof VortexAmiAlert) {
	//			clientEntity = addAmiAlert((VortexAmiAlert) entity);
	//		} else if (entity instanceof VortexAmiObject) {
	//			clientEntity = addAmiObject((VortexAmiObject) entity);
	//		} else if (entity instanceof VortexAmiCommandDefinition) {
	//			clientEntity = addAmiCommandDef((VortexAmiCommandDefinition) entity);
	//		} else
	//			LH.warning(log, "Unknown ami add: ", entity);
	//		//		if (clientEntity != null) {
	//		//			this.amiEntities.put(entity.getId(), clientEntity);
	//		//			fireNewAmi(clientEntity);
	//		//		}
	//	}
	//
	//	public void onAmiChange(VortexClientAmiEntity<?> target, byte pid, Object value, boolean moreChangesComing) {
	//		target.update(pid, value);
	//		if (!moreChangesComing)
	//			fireUpdateAmi(target);
	//	}
	public void onChange(VortexEntity entity) {
		if (entity.getRevision() != VortexAgentEntity.REVISION_DONE) {
			Node<VortexEntity> node = entities.getNodeOrCreate(entity.getId());
			if (node.getValue() == null)
				node.setValue(entity);
		} else
			entities.remove(entity.getId());
		if (entity instanceof VortexAgentEntity) {
			VortexAgentEntity vae = (VortexAgentEntity) entity;
			VortexClientMachine existing = getAgentMachine(vae.getMachineInstanceId());
			if (existing == null) {
				LH.fine(log, "Machine not found for machine entity: ", vae);
			} else if (entity instanceof VortexAgentProcess) {
				onProcessNode(existing, (VortexAgentProcess) entity);
			} else if (entity instanceof VortexAgentNetConnection) {
				onNetConnectionNode(existing, (VortexAgentNetConnection) entity);
			} else if (entity instanceof VortexAgentNetLink) {
				onNetConnectionLink(existing, (VortexAgentNetLink) entity);
			} else if (entity instanceof VortexAgentNetAddress) {
				onNetAddressNode(existing, (VortexAgentNetAddress) entity);
			} else if (entity instanceof VortexAgentMachine) {
				onMachineNode(existing, (VortexAgentMachine) entity);
			} else if (entity instanceof VortexAgentFileSystem) {
				onFileSystem(existing, (VortexAgentFileSystem) entity);
			} else if (entity instanceof VortexAgentCron) {
				onCron(existing, (VortexAgentCron) entity);
			} else if (entity instanceof VortexAgentMachineEventStats) {
				//TODO:handle event stats
			} else if (entity instanceof VortexAgentEvent) {
				//TODO:handle event stats
			} else if (entity instanceof VortexAgentBackupFile) {
				onBackupFile(existing, (VortexAgentBackupFile) entity);
			} else {
				LH.warning(log, "Unknown change: ", entity);
			}
		} else if (entity instanceof VortexEyeEntity) {
			if (entity instanceof VortexEyeClientEvent) {
				onClientEvent((VortexEyeClientEvent) entity);
			} else if (entity instanceof VortexEyeAuditTrailRule) {
				onAuditTrailNode((VortexEyeAuditTrailRule) entity);
			} else if (entity instanceof VortexEyeScheduledTask) {
				onScheduledTask((VortexEyeScheduledTask) entity);
			} else if (entity instanceof VortexExpectation) {
				onExpectation((VortexExpectation) entity);
			} else if (entity instanceof VortexBuildProcedure) {
				onBuildProcedure((VortexBuildProcedure) entity);
			} else if (entity instanceof VortexBuildResult) {
				onBuildResult((VortexBuildResult) entity);
			} else if (entity instanceof VortexDeploymentSet) {
				onDeploymentSet((VortexDeploymentSet) entity);
			} else if (entity instanceof VortexDeployment) {
				onDeployment((VortexDeployment) entity);
			} else if (entity instanceof VortexEyeCloudInterface) {
				onCloudInterface((VortexEyeCloudInterface) entity);
			} else if (entity instanceof VortextEyeCloudMachineInfo) {
				onCloudMachineInfo((VortextEyeCloudMachineInfo) entity);
			} else if (entity instanceof VortexAgentDbServer) {
				onDbServerNode((VortexAgentDbServer) entity);
			} else if (entity instanceof VortexEyeJournalReport) {
				onJournalReport((VortexEyeJournalReport) entity);
			} else if (entity instanceof VortexEyeMetadataField) {
				onMetadataField((VortexEyeMetadataField) entity);
			} else if (entity instanceof VortexAgentDbEntity) {
				VortexAgentDbEntity dbrevision = (VortexAgentDbEntity) entity;
				if (dbrevision.getDbServerId() == 0) {//hack to get the dbserver id
					dbrevision.setDbServerId(((VortexAgentDbEntity) entities.get(entity.getId())).getDbServerId());
				}
				VortexClientDbServer dbServer = getDbServer(dbrevision.getDbServerId());
				if (dbServer == null) {
					LH.warning(log, "update for unknown db server: ", dbrevision);
				} else if (dbrevision instanceof VortexAgentDbDatabase) {
					onDbDatabaseNode(dbServer, (VortexAgentDbDatabase) dbrevision);
				} else if (dbrevision instanceof VortexAgentDbTable) {
					onDbTableNode(dbServer, (VortexAgentDbTable) dbrevision);
				} else if (dbrevision instanceof VortexAgentDbColumn) {
					onDbColumnNode(dbServer, (VortexAgentDbColumn) dbrevision);
				} else if (dbrevision instanceof VortexAgentDbPrivilege) {
					onDbPrivilegeNode(dbServer, (VortexAgentDbPrivilege) dbrevision);
				} else if (dbrevision instanceof VortexAgentDbObject) {
					onDbObjectNode(dbServer, (VortexAgentDbObject) dbrevision);
				}
			} else if (entity instanceof VortexEyeBackupDestination) {
				onBackupDestination((VortexEyeBackupDestination) entity);
			} else if (entity instanceof VortexEyeBackup) {
				onBackup((VortexEyeBackup) entity);
			} else {
				LH.warning(log, "update for unknown machine: ", entity);
			}
		}
	}

	private int clientEventsCount = 0;

	private void onClientEvent(VortexEyeClientEvent entity) {
		clientEvents.add((clientEventsCount++) % MAX_CLIENT_EVENTS, entity);
		if (!clientEventListeners.isEmpty())
			for (VortexClientEventListener i : clientEventListeners)
				i.onClientEvent(entity);
	}

	private boolean isEyeConnected;

	private void onJournalReport(VortexEyeJournalReport entity) {
		this.eyeJournalReports.put(entity.getYear() * 100 + entity.getMonth(), entity);
		for (VortexClientJournalReportListener i : reportListeners)
			i.onJournalReport(entity);
	}

	public void onAuditTrailNode(VortexEyeAuditTrailRule agentNode) {
		VortexClientAuditTrailRule existingNode = getAuditTrailRule(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeAuditTrailRule(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addAuditTrailRule(existingNode = new VortexClientAuditTrailRule(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onExpectation(VortexExpectation agentNode) {
		VortexClientExpectation existingNode = getExpectation(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeExpectation(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addExpectation(existingNode = new VortexClientExpectation(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onBuildProcedure(VortexBuildProcedure agentNode) {
		VortexClientBuildProcedure existingNode = getBuildProcedure(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeBuildProcedure(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addBuildProcedure(existingNode = new VortexClientBuildProcedure(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onBuildResult(VortexBuildResult agentNode) {
		VortexClientBuildResult existingNode = getBuildResult(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeBuildResult(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addBuildResult(existingNode = new VortexClientBuildResult(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onMetadataField(VortexEyeMetadataField agentNode) {
		VortexClientMetadataField existingNode = getMetadataField(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeMetadataField(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addMetadataField(existingNode = new VortexClientMetadataField(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onScheduledTask(VortexEyeScheduledTask agentNode) {
		VortexClientScheduledTask existingNode = getScheduledTask(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeScheduledTask(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addScheduledTask(existingNode = new VortexClientScheduledTask(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onDeploymentSet(VortexDeploymentSet agentNode) {
		VortexClientDeploymentSet existingNode = getDeploymentSet(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeDeploymentSet(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addDeploymentSet(existingNode = new VortexClientDeploymentSet(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onDeployment(VortexDeployment agentNode) {
		VortexClientDeployment existingNode = getDeployment(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeDeployment(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addDeployment(existingNode = new VortexClientDeployment(agentNode));
			fireNew(existingNode);
		} else {
			updateDeployment(existingNode, agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onCloudInterface(VortexEyeCloudInterface agentNode) {
		VortexClientCloudInterface existingNode = getCloudInterface(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeCloudInterface(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addCloudInterface(existingNode = new VortexClientCloudInterface(agentNode));
			fireNew(existingNode);
		} else {
			updateCloudInterface(existingNode, agentNode);
			fireUpdate(existingNode);
		}
	}

	private void onCloudMachineInfo(VortextEyeCloudMachineInfo agentNode) {
		VortexClientCloudMachineInfo existingNode = getCloudMachineInfo(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeCloudMachineInfo(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addCloudMachineInfo(existingNode = new VortexClientCloudMachineInfo(agentNode));
			fireNew(existingNode);
		} else {
			updateCloudMachineInfo(existingNode, agentNode);
			fireUpdate(existingNode);
		}

	}

	public void onBackup(VortexEyeBackup agentNode) {
		VortexClientBackup existingNode = getBackup(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeBackup(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addBackup(existingNode = new VortexClientBackup(agentNode));
			fireNew(existingNode);
		} else {
			updateBackup(existingNode, agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onBackupDestination(VortexEyeBackupDestination agentNode) {
		VortexClientBackupDestination existingNode = getBackupDestination(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeBackupDestination(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addBackupDestination(existingNode = new VortexClientBackupDestination(agentNode));
			fireNew(existingNode);
		} else {
			updateBackupDestination(existingNode, agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onDbDatabaseNode(VortexClientDbServer machine, VortexAgentDbDatabase agentNode) {
		VortexClientDbDatabase existingNode = machine.getDatabase(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeDatabase(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addDbDatabase(existingNode = new VortexClientDbDatabase(agentNode));
			fireNew(existingNode);
			entities.put(agentNode.getId(), agentNode);
			for (VortexAgentDbTable table : CH.values(agentNode.getTables()))
				onDbTableNode(machine, table);
			for (VortexAgentDbPrivilege priviledge : CH.i(agentNode.getPrivileges()))
				onDbPrivilegeNode(machine, priviledge);
			for (VortexAgentDbObject object : CH.i(agentNode.getObjects()))
				onDbObjectNode(machine, object);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	public void onDbServerNode(VortexAgentDbServer agentNode) {
		VortexClientDbServer existingNode = getDbServer(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeDbServer(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			addDbServer(existingNode = new VortexClientDbServer(agentNode));
			fireNew(existingNode);
			entities.put(agentNode.getId(), agentNode);
			for (VortexAgentDbDatabase database : CH.values(agentNode.getDatabases()))
				onDbDatabaseNode(existingNode, database);

		} else {
			existingNode.update(agentNode);
			//if (agentNode.askExists(VortexAgentDbServer.PID_DATABASES)) {
			//for (VortexAgentDbDatabase db : CH.values(agentNode.getDatabases())) {
			//onDbDatabaseNode(existingNode, db);
			//}
			//}
			fireUpdate(existingNode);
		}
	}

	private void onDbTableNode(VortexClientDbServer machine, VortexAgentDbTable agentNode) {
		VortexClientDbTable existingNode = machine.getDbTable(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeDbTable(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addDbTable(existingNode = new VortexClientDbTable(agentNode));
			entities.put(agentNode.getId(), agentNode);
			fireNew(existingNode);
			for (VortexAgentDbColumn column : CH.values(agentNode.getColumns())) {
				onDbColumnNode(machine, column);
			}

		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onDbPrivilegeNode(VortexClientDbServer machine, VortexAgentDbPrivilege agentNode) {
		VortexClientDbPrivilege existingNode = machine.getDbPrivilege(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeDbPrivilege(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addDbPrivilege(existingNode = new VortexClientDbPrivilege(agentNode));
			entities.put(agentNode.getId(), agentNode);
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onDbObjectNode(VortexClientDbServer machine, VortexAgentDbObject agentNode) {
		VortexClientDbObject existingNode = machine.getDbObject(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeDbObject(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addDbObject(existingNode = new VortexClientDbObject(agentNode));
			entities.put(agentNode.getId(), agentNode);
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onDbColumnNode(VortexClientDbServer machine, VortexAgentDbColumn agentNode) {
		VortexClientDbColumn existingNode = machine.getDbColumn(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeDbColumn(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addDbColumn(existingNode = new VortexClientDbColumn(agentNode));
			entities.put(agentNode.getId(), agentNode);
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onBackupFile(VortexClientMachine machine, VortexAgentBackupFile agentNode) {
		if (agentNode.getData() != null)
			throw new IllegalArgumentException("backup file should not have data");
		VortexClientBackupFile existingNode = backupFiles.get(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				removeBackupFile(agentNode.getId());
			}
		} else if (existingNode == null) {
			backupFiles.put(agentNode.getId(), existingNode = new VortexClientBackupFile(agentNode));
			existingNode.setMachine(machine);
			backupFilesByMachineUidAndPath.putMulti(machine.getMachineUid(), existingNode.getData().getPath(), existingNode);
			fireNew(existingNode);
		} else {
			if (OH.ne(agentNode.getPath(), existingNode.getData().getPath()) && agentNode.getPath() != null) {
				backupFilesByMachineUidAndPath.removeMulti(machine.getMachineUid(), existingNode.getData().getPath());
				backupFilesByMachineUidAndPath.putMulti(machine.getMachineUid(), agentNode.getPath(), existingNode);
			}
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}

	private void onCron(VortexClientMachine machine, VortexAgentCron agentNode) {
		VortexClientCron existingNode = machine.getCron(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeCron(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addCron(existingNode = new VortexClientCron(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onFileSystem(VortexClientMachine machine, VortexAgentFileSystem agentNode) {
		VortexClientFileSystem existingNode = machine.getFileSystem(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeFileSystem(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addFileSystem(existingNode = new VortexClientFileSystem(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onNetConnectionLink(VortexClientMachine machine, VortexAgentNetLink agentNode) {
		VortexClientNetLink existingNode = machine.getNetLink(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeNetLink(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addNetLink(existingNode = new VortexClientNetLink(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onMachineNode(VortexClientMachine machine, VortexAgentMachine agentNode) {
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			this.onAgentSnapshotRemoved(agentNode.getId());
		} else if (agentNode.askExists(VortexAgentMachine.PID_AGENT_PROCESS_UID)) {
			if (OH.ne(agentNode.getAgentProcessUid(), machine.getData().getAgentProcessUid())) {
				if (agentNode.getAgentProcessUid() == null) {
					fireStale(machine);
					for (VortexClientMachineEntity<?> address : machine.getNodes(VortexAgentEntity.TYPE_NET_ADDRESS))
						addressesByIp.remove(address.getId());//TODO: update links??
					machine.update(agentNode);
					machine.onStale();
					handleExpectationsForRemovedMachine(machine);
				} else if (machine.getData().getAgentProcessUid() == null) {
					fireActive(machine);
					machine.update(agentNode);
					handleExpectationsForNewMachine(machine);
				}
			}
		} else
			machine.update(agentNode);
		fireUpdate(machine);
	}
	private void onProcessNode(VortexClientMachine machine, VortexAgentProcess agentNode) {
		VortexClientProcess existingNode = machine.getProcess(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeProcess(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addProcess(existingNode = new VortexClientProcess(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}
	private void onNetConnectionNode(VortexClientMachine machine, VortexAgentNetConnection agentNode) {
		VortexClientNetConnection existingNode = machine.getNetConnection(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeNetConnection(existingNode.getId());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addNetConnection(existingNode = new VortexClientNetConnection(agentNode));
			fireNew(existingNode);
		} else {
			existingNode.update(agentNode);
			fireUpdate(existingNode);
		}
	}

	private void onNetAddressNode(VortexClientMachine machine, VortexAgentNetAddress agentNode) {
		VortexClientNetAddress existingNode = machine.getNetAddress(agentNode.getId());
		if (agentNode.getRevision() == VortexAgentEntity.REVISION_DONE) {
			if (existingNode == null) {
				LH.warning(log, "remove for unknown vortex node:", agentNode);
			} else {
				machine.removeNetAddress(existingNode.getId());
				addressesByIp.remove(existingNode.getData().getAddress());
				fireRemove(existingNode);
			}
		} else if (existingNode == null) {
			machine.addNetAddress(existingNode = new VortexClientNetAddress(agentNode));
			String addr = existingNode.getData().getAddress();
			if (!isLoopback(addr))
				addressesByIp.put(addr, existingNode);
			fireNew(existingNode);
		} else {
			LH.warning(log, "TODO: need to handle changing ip addresses, this needs to be applied to connections!: " + agentNode);
			addressesByIp.remove(existingNode.getData().getAddress());
			existingNode.update(agentNode);
			addressesByIp.put(existingNode.getData().getAddress(), existingNode);
			fireUpdate(existingNode);
		}
	}

	public VortexClientMachine getAgentMachine(long machineInstanceId) {
		return this.agentMachines.get(machineInstanceId);
	}
	private void fireUpdate(VortexClientEntity<?> existingNode) {
		//TODO: did the change cause expectations to change?
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineEntityUpdated(existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error updating node: ", existingNode, " on portlet: ", p, e);
			}
	}
	private void fireNew(VortexClientEntity<?> existingNode) {
		//		handleExpectationsForNewNode(existingNode);
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineEntityAdded(existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error adding node: ", existingNode, " on portlet: ", p, e);
			}
	}
	private void fireRemove(VortexClientEntity<?> existingNode) {
		//		handleExpectationsForRemovedNode(existingNode);
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineEntityRemoved(existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error removing node: ", existingNode, " on portlet: ", p, e);
			}
	}
	//	private void fireUpdateAmi(VortexClientAmiEntity existing) {
	//		for (VortexClientAmiListener p : amiListeners)
	//			try {
	//				if (p.isRealtime())
	//					p.onAmiEntityUpdated(existing);
	//			} catch (Exception e) {
	//				LH.warning(log, "Error updating ami entity: ", existing, " on portlet: ", p, e);
	//			}
	//	}
	//
	//	private void fireNewAmi(VortexClientAmiEntity existingNode) {
	//		for (VortexClientAmiListener p : amiListeners)
	//			try {
	//				if (p.isRealtime())
	//					p.onAmiEntityAdded(existingNode);
	//			} catch (Exception e) {
	//				LH.warning(log, "Error adding ami entity: ", existingNode, " on portlet: ", p, e);
	//			}
	//	}
	//	private void fireRemoveAmi(VortexClientAmiEntity existingNode) {
	//		for (VortexClientAmiListener p : amiListeners)
	//			try {
	//				if (p.isRealtime())
	//					p.onAmiEntityRemoved(existingNode);
	//			} catch (Exception e) {
	//				LH.warning(log, "Error removing ami entity: ", existingNode, " on portlet: ", p, e);
	//			}
	//	}

	private void fireUpdate(VortexClientMachine existing) {
		//TODO: did the change cause expectations to change?
		for (VortexClientMachineListener p : machineListeners)
			p.onMachineUpdated(existing);
	}

	private void fireNew(VortexClientMachine existingNode) {
		handleExpectationsForNewMachine(existingNode);
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineAdded(existingNode);
			} catch (Exception e) {
				LH.warning(log, "Error adding machine: ", existingNode, " on portlet: ", p, e);
			}
	}
	private void fireRemove(VortexClientMachine existing) {
		handleExpectationsForRemovedMachine(existing);
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineRemoved(existing);
			} catch (Exception e) {
				LH.warning(log, "Error removing machine: ", existing, " on portlet: ", p, e);
			}
	}
	private void fireStale(VortexClientMachine existing) {
		handleExpectationsForRemovedMachine(existing);
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineStale(existing);
			} catch (Exception e) {
				LH.warning(log, "Error publishing machine stale: ", existing, " on portlet: ", p, e);
			}
	}
	private void fireActive(VortexClientMachine machine) {
		for (VortexClientMachineListener p : machineListeners)
			try {
				p.onMachineActive(machine);
			} catch (Exception e) {
				LH.warning(log, "Error publishing machine active: ", machine, " on portlet: ", p, e);
			}
	}

	public void fireAgentAuditEventsList(List<F1AppEvent> list) {
		for (VortexClientAuditEventsListener p : auditEventsListeners)
			p.onAgentAuditEvents(list);
	}

	public Iterable<VortexClientMachine> getAgentMachinesSortedByHostname() {
		List<VortexClientMachine> r = CH.l(this.agentMachines.values());
		Collections.sort(r, HostNameComparator.INSTANCE);
		return r;
	}
	public Iterable<VortexClientMachine> getAgentMachines() {
		return this.agentMachines.values();
	}
	public void onDisconnect(boolean fireToListeners) {
		this.isEyeConnected = false;
		this.agentMachines.clear();
		this.metadataFields.clear();
		this.metadataFieldsByKeyCode.clear();
		this.agentMachinesByMachineUid.clear();
		this.addressesByIp.clear();
		this.auditTrailRules.clear();
		this.expectations.clear();
		this.matchedExpectationsByMachineUidAndTargetTypeAndId.clear();
		this.unmatchedExpectationsByMachineUidAndTargetTypeAndId.clear();
		this.deployments.clear();
		//		this.amiAlerts.clear();
		//		this.amiCommandDefs.clear();
		//		this.amiApplications.clear();
		//		this.amiApplicationsByName.clear();
		//		this.amiObjects.clear();
		//		for (LongKeyMap<VortexClientAmiObject> value : this.amiObjectsByType.values())
		//			value.clear();
		//		this.amiEntities.clear();
		//		this.amiStringValuePoolMappings.clear();
		//		this.amiStringValuePoolMappings2.clear();
		//		this.amiStringKeyPool = new String[Short.MAX_VALUE];
		//		this.amiStringKeyPoolMap.clear();
		this.deploymentsByBuildResultIdAndId.clear();
		this.deploymentSets.clear();
		this.machineDbServers.clear();
		this.backupDestinations.clear();
		this.backups.clear();
		this.backupsByDeploymentIdAndId.clear();
		this.backupsByDestIdAndId.clear();
		this.backupsByMachineUidAndPath.clear();
		this.backupFiles.clear();
		this.backupFilesByMachineUidAndPath.clear();
		this.buildProcedures.clear();
		this.buildResults.clear();
		this.entities.clear();
		this.javaAppsByProcessUid.clear();
		this.f1Apps.clear();
		if (fireToListeners) {
			for (VortexClientManagerListener p : connectListeners) {
				try {
					p.onVortexEyeDisconnected();
				} catch (Exception e) {
					LH.warning(log, "Error processing disconnect for portlet: ", p, e);
				}
			}
		}
	}
	public void fireConnectionStateChanged(VortexWebEyeService vortexWebEyeService) {
		for (VortexClientManagerListener p : connectListeners) {
			try {
				p.onVortexConnectionStateChanged(this, vortexWebEyeService);
			} catch (Exception e) {
				LH.warning(log, "Error processing disconnect for portlet: ", p, e);
			}
		}
	}

	public VortexClientNetAddress getNetAddressByIp(String foreignHost, VortexClientMachine machine) {
		if (isLoopback(foreignHost)) {
			return machine.getLoopbackAddress();
		}
		return this.addressesByIp.get(foreignHost);
	}
	public static boolean isLoopback(String addr) {
		return addr == null || addr.equals("*") || addr.equals("127.0.0.1");
	}
	public Iterable<VortexClientAuditTrailRule> getAuditTrailRules() {
		return auditTrailRules.values();
	}
	public VortexClientAuditTrailRule getAuditTrailRule(long id) {
		return auditTrailRules.get(id);
	}
	private VortexClientAuditTrailRule removeAuditTrailRule(long id) {
		return auditTrailRules.remove(id);
	}
	private void addAuditTrailRule(VortexClientAuditTrailRule node) {
		auditTrailRules.put(node.getId(), node);
	}

	//backup destinations
	public VortexClientBackupDestination getBackupDestination(long id) {
		return backupDestinations.get(id);
	}
	private VortexClientBackupDestination removeBackupDestination(long id) {
		VortexClientBackupDestination r = backupDestinations.remove(id);
		if (r != null) {
			for (VortexClientBackup backup : r.getBackups().values()) {
				backup.setDestination(null);
				fireUpdate(backup);
			}
			r.removeBackups();
		}
		return r;
	}
	private void addBackupDestination(VortexClientBackupDestination node) {
		backupDestinations.put(node.getId(), node);
		node.setMachine(getAgentMachineByUid(node.getData().getDestinationMachineUid()));
		Map<Long, VortexClientBackup> backs = backupsByDestIdAndId.get(node.getId());
		if (backs != null) {
			for (VortexClientBackup bu : backs.values()) {
				bu.setDestination(node);
				fireUpdate(bu);
				node.addBackup(bu);
			}
		}
		backups.get(node.getId());
	}
	private void updateBackupDestination(VortexClientBackupDestination existingNode, VortexEyeBackupDestination agentNode) {
		existingNode.update(agentNode);
	}

	public Iterable<VortexClientBackupDestination> getBackupDestinations() {
		return backupDestinations.values();
	}

	//backups
	public VortexClientBackup getBackup(long id) {
		return backups.get(id);
	}
	private VortexClientBackup removeBackup(long id) {
		final VortexClientBackup r = backups.remove(id);
		if (r != null) {
			VortexEyeBackup data = r.getData();
			backupsByDeploymentIdAndId.removeMulti(data.getDeploymentId(), r.getId());
			backupsByDestIdAndId.removeMulti(data.getBackupDestinationId(), r.getId());
			backupsByMachineUidAndPath.removeMulti(r.getSourceMuid(), r.getFullSourcePath());
			final VortexClientDeployment deployment = r.getDeployment();
			if (deployment != null) {
				deployment.removeBackup(id);
				fireUpdate(deployment);
			}
			VortexClientBackupDestination dest = r.getDestination();
			if (dest != null) {
				dest.removeBackup(id);
				fireUpdate(dest);
			}
		}
		return r;
	}
	private void addBackup(VortexClientBackup node) {
		VortexEyeBackup data = node.getData();
		node.setMachine(getAgentMachineByUid(data.getSourceMachineUid()));
		backups.put(node.getId(), node);
		final long id = node.getId();
		final long depId = data.getDeploymentId();
		final long destId = data.getBackupDestinationId();
		backupsByDeploymentIdAndId.putMulti(depId, id, node);
		backupsByDestIdAndId.putMulti(destId, id, node);
		backupsByMachineUidAndPath.putMulti(node.getSourceMuid(), node.getFullSourcePath(), node);
		VortexClientDeployment dep = deployments.get(depId);
		if (dep != null) {
			dep.addBackup(node);
			node.setDeployment(dep);
			fireUpdate(dep);
		}
		VortexClientBackupDestination dest = backupDestinations.get(destId);
		if (dest != null) {
			dest.addBackup(node);
			node.setDestination(dest);
			fireUpdate(dest);
		}
	}
	private void updateBackup(VortexClientBackup existing, VortexEyeBackup agentNode) {
		VortexEyeBackup data = existing.getData();
		final boolean refsChanged = data.getDeploymentId() != agentNode.getDeploymentId() || data.getBackupDestinationId() != agentNode.getBackupDestinationId()
				|| OH.ne(data.getSourceMachineUid(), agentNode.getSourceMachineUid()) || OH.ne(data.getSourcePath(), agentNode.getSourcePath());
		if (refsChanged) {
			removeBackup(existing.getId());
			existing.update(agentNode);
			addBackup(existing);
		} else
			existing.update(agentNode);
	}

	public Map<String, VortexClientBackup> getBackupsByPathForMachine(String muid) {
		return CH.noNull(this.backupsByMachineUidAndPath.get(muid));
	}
	public Map<String, VortexClientBackupFile> getBackupFilesByPathForMachine(String muid) {
		return CH.noNull(this.backupFilesByMachineUidAndPath.get(muid));
	}

	public Iterable<VortexClientBackup> getBackups() {
		return backups.values();
	}
	public LongIterable getBackupIds() {
		return backups.keys();
	}

	public VortexClientMachine getAgentMachineByUid(String muid) {
		return agentMachinesByMachineUid.get(muid);
	}
	public Iterable<VortexClientExpectation> getExpectations() {
		return expectations.values();
	}

	public VortexClientExpectation getExpectation(long id) {
		return expectations.get(id);
	}

	private VortexClientExpectation removeExpectation(long id) {
		final VortexClientExpectation r = expectations.remove(id);
		if (r != null) {
			final String muid = r.getMachineUid();
			final byte ttype = r.getTargetType();
			VortexClientEntity<?> match = r.getMatch();
			if (match != null) {
				matchedExpectationsByMachineUidAndTargetTypeAndId.removeMulti(muid, ttype, r.getId());
				r.bind(null);
				findExpectationForNode(match);
				fireUpdate(match);
			} else {
				unmatchedExpectationsByMachineUidAndTargetTypeAndId.removeMulti(muid, ttype, r.getId());
			}
		}

		return r;
	}
	private void addExpectation(VortexClientExpectation exp) {
		expectations.put(exp.getId(), exp);
		final String muid = exp.getMachineUid();
		final byte ttype = exp.getTargetType();
		if (findNodeForExpectation(exp)) {
			matchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(muid, ttype, exp.getId(), exp);
		} else {
			unmatchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(muid, ttype, exp.getId(), exp);
		}
	}
	private boolean findNodeForExpectation(VortexClientExpectation exp) {
		final String muid = exp.getMachineUid();
		final byte ttype = exp.getTargetType();
		VortexClientMachine machine = getAgentMachineByUid(exp.getMachineUid());
		if (machine != null) {
			for (VortexClientEntity<?> node : machine.getNodes(exp.getTargetType())) {
				if (node.getMatchingExpectation() == null && exp.matches(node)) {
					exp.bind(node);
					fireUpdate(node);
					return true;
				}
			}
		}
		return false;
	}
	private void findExpectationForNode(VortexClientEntity<?> existingNode) {
		final String muid = existingNode.getMachine().getMachineUid();
		final byte ttype = existingNode.getType();
		for (VortexClientExpectation exp : unmatchedExpectationsByMachineUidAndTargetTypeAndId.getMulti(muid, ttype).values()) {
			if (exp.matches(existingNode)) {
				exp.bind(existingNode);
				unmatchedExpectationsByMachineUidAndTargetTypeAndId.removeMulti(muid, ttype, exp.getId());
				matchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(muid, ttype, exp.getId(), exp);
				fireUpdate(exp);
				break;
			}
		}

	}

	private void handleExpectationsForNewMachine(VortexClientMachine machine) {
		MapInMap<Byte, Long, VortexClientExpectation> exps = unmatchedExpectationsByMachineUidAndTargetTypeAndId.get(machine.getMachineUid());
		if (exps != null) {
			for (Byte targetType : AH.toArrayByte(exps.keySet())) {// each type of expectation for the given machine
				Iterable<VortexClientMachineEntity<?>> candidates = machine.getNodes(targetType);
				for (VortexClientEntity<?> candidate : candidates) {//each candidate for given type
					inner: for (VortexClientExpectation exp2 : exps.get(targetType).values()) {
						if (candidate.getMatchingExpectation() == null && exp2.matches(candidate)) {
							exp2.bind(candidate);
							unmatchedExpectationsByMachineUidAndTargetTypeAndId.removeMulti(exp2.getMachineUid(), exp2.getTargetType(), exp2.getId());
							matchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(exp2.getMachineUid(), exp2.getTargetType(), exp2.getId(), exp2);
							fireUpdate(exp2);
							break inner;
						}
					}
				}
			}
		}
	}
	private void handleExpectationsForNewNode(VortexClientEntity<?> existingNode) {
		if (existingNode.getType() == VortexAgentEntity.TYPE_EXPECTATION)
			return;
		if (existingNode.getMachine() == null)
			return;
		if (existingNode.getMatchingExpectation() != null)
			return;
		findExpectationForNode(existingNode);
	}
	private void handleExpectationsForRemovedMachine(VortexClientMachine machine) {
		final MapInMap<Byte, Long, VortexClientExpectation> exps = matchedExpectationsByMachineUidAndTargetTypeAndId.remove(machine.getMachineUid());
		if (exps == null)
			return;
		for (VortexClientExpectation exp2 : exps.valuesMulti()) {
			exp2.bind(null);
			unmatchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(exp2.getMachineUid(), exp2.getTargetType(), exp2.getId(), exp2);
			fireUpdate(exp2);
		}
	}
	private void handleExpectationsForRemovedNode(VortexClientEntity<?> existingNode) {
		if (existingNode.getType() == VortexAgentEntity.TYPE_EXPECTATION)
			return;
		final VortexClientExpectation exp = existingNode.getMatchingExpectation();
		if (exp == null)
			return;
		exp.bind(null);
		if (!findNodeForExpectation(exp)) {
			String muid = exp.getMachineUid();
			byte ttype = exp.getTargetType();
			matchedExpectationsByMachineUidAndTargetTypeAndId.removeMulti(muid, ttype, exp.getId());
			unmatchedExpectationsByMachineUidAndTargetTypeAndId.putMulti(muid, ttype, exp.getId(), exp);
		}
		fireUpdate(exp);
	}

	//F1 APPLICATION SECTION

	final private LongKeyMap<VortexClientF1AppState> f1Apps = new LongKeyMap<VortexClientF1AppState>();
	final private Map<String, VortexClientF1AppState> javaAppsByProcessUid = new HashMap<String, VortexClientF1AppState>();

	public Iterable<VortexClientF1AppState> getJavaAppStates() {
		return f1Apps.values();
	}
	public VortexClientF1AppState getJavaAppState(long key) {
		return f1Apps.getOrThrow(key);
	}
	public VortexClientF1AppState getJavaAppStateByProcessUid(String key) {
		return javaAppsByProcessUid.get(key);
	}

	private VortexClientF1AppState addF1App(F1AppInstance javaApp) {
		VortexClientMachine machine;
		machine = this.getAgentMachineByUid(javaApp.getAgentMachineUid());
		long miid = machine == null ? -1 : machine.getMachineId();
		VortexClientF1AppState r = new VortexClientF1AppState(javaApp, miid);
		f1Apps.putOrThrow(javaApp.getId(), r);
		CH.putOrThrow(javaAppsByProcessUid, javaApp.getProcessUid(), r);
		//for (VortexClientF1AppStateListener p : f1AppListeners)
		//p.onF1AppAdded(r);
		return r;
	}
	public VortexClientF1AppState removeF1App(long aiid) {
		VortexClientF1AppState r = f1Apps.removeOrThrow(aiid);
		CH.removeOrThrow(javaAppsByProcessUid, r.getSnapshot().getProcessUid());
		for (VortexClientF1AppStateListener p : f1AppListeners)
			p.onF1AppRemoved(r);
		return r;
	}

	public void onF1AppEntityUpdate(AgentWebObject update) {
		update.bind();
		for (VortexClientF1AppStateListener p : f1AppListeners)
			p.onF1AppEntityUpdated(update);
	}

	//build procedures
	public VortexClientBuildProcedure getBuildProcedure(long id) {
		return buildProcedures.get(id);
	}

	private VortexClientBuildProcedure removeBuildProcedure(long id) {
		final VortexClientBuildProcedure r = buildProcedures.remove(id);
		return r;
	}

	public Iterable<VortexClientBuildProcedure> getBuildProcedures() {
		return buildProcedures.values();
	}
	private void addBuildProcedure(VortexClientBuildProcedure exp) {
		buildProcedures.put(exp.getId(), exp);
	}

	//build results
	public VortexClientBuildResult getBuildResult(long id) {
		return buildResults.get(id);
	}

	private VortexClientBuildResult removeBuildResult(long id) {
		final VortexClientBuildResult r = buildResults.remove(id);
		if (r != null) {
			final VortexClientBuildProcedure bp = r.getBuildProcedure();
			if (bp != null)
				bp.removeBuildResult(r);
			final LongKeyMapSource<VortexClientDeployment> deps = r.getDeployments();
			for (VortexClientDeployment dep : deps.values()) {
				dep.setBuildResult(null);
				fireUpdate(dep);
			}
		}
		return r;
	}

	public Iterable<VortexClientBuildResult> getBuildResults() {
		return buildResults.values();
	}

	private void addBuildResult(VortexClientBuildResult exp) {
		VortexClientBuildProcedure bp = getBuildProcedure(exp.getData().getProcedureId());
		if (bp != null) {
			exp.setBuildProcedure(bp);
			bp.addBuildResult(exp);
		}
		buildResults.put(exp.getId(), exp);
		Map<Long, VortexClientDeployment> deps = deploymentsByBuildResultIdAndId.get(exp.getId());
		if (CH.isntEmpty(deps)) {
			for (VortexClientDeployment dep : deps.values()) {
				dep.setBuildResult(exp);
				fireUpdate(dep);
			}
		}
	}

	//metadata fields
	public VortexClientMetadataField getMetadataField(long id) {
		return metadataFields.get(id);
	}
	//metadata fields
	public VortexClientMetadataField getMetadataFieldByKeyCode(String id) {
		return metadataFieldsByKeyCode.get(id);
	}

	private VortexClientMetadataField removeMetadataField(long id) {
		final VortexClientMetadataField r = metadataFields.remove(id);
		if (r != null)
			metadataFieldsByKeyCode.remove(r.getData().getKeyCode());
		return r;
	}

	public Iterable<VortexClientMetadataField> getMetadataFields() {
		return metadataFields.values();
	}
	private void addMetadataField(VortexClientMetadataField vortexClientMetadataField) {
		metadataFields.put(vortexClientMetadataField.getId(), vortexClientMetadataField);
		metadataFieldsByKeyCode.put(vortexClientMetadataField.getData().getKeyCode(), vortexClientMetadataField);
	}

	//scheduled tasks
	public VortexClientScheduledTask getScheduledTask(long id) {
		return scheduledTasks.get(id);
	}

	private VortexClientScheduledTask removeScheduledTask(long id) {
		final VortexClientScheduledTask r = scheduledTasks.remove(id);
		return r;
	}

	public Iterable<VortexClientScheduledTask> getScheduledTasks() {
		return scheduledTasks.values();
	}
	private void addScheduledTask(VortexClientScheduledTask exp) {
		scheduledTasks.put(exp.getId(), exp);
	}

	//Cloud Interfaces
	public VortexClientCloudInterface getCloudInterface(long id) {
		return cloudInterfaces.get(id);
	}

	private VortexClientCloudInterface removeCloudInterface(long id) {
		final VortexClientCloudInterface r = cloudInterfaces.remove(id);
		return r;
	}

	public Iterable<VortexClientCloudInterface> getCloudInterfaces() {
		return cloudInterfaces.values();
	}
	private void addCloudInterface(VortexClientCloudInterface exp) {
		cloudInterfaces.put(exp.getId(), exp);
	}

	private void updateCloudInterface(VortexClientCloudInterface existing, VortexEyeCloudInterface agentNode) {
		existing.update(agentNode);
	}

	//CloudMachineInfo
	public VortexClientCloudMachineInfo getCloudMachineInfo(long id) {
		return cloudMachinesInfo.get(id);
	}

	private VortexClientCloudMachineInfo removeCloudMachineInfo(long id) {
		final VortexClientCloudMachineInfo r = cloudMachinesInfo.remove(id);
		return r;
	}

	public Iterable<VortexClientCloudMachineInfo> getCloudMachinesInfo() {
		return cloudMachinesInfo.values();
	}
	private void addCloudMachineInfo(VortexClientCloudMachineInfo exp) {
		cloudMachinesInfo.put(exp.getId(), exp);
	}

	private void updateCloudMachineInfo(VortexClientCloudMachineInfo existing, VortextEyeCloudMachineInfo agentNode) {
		existing.update(agentNode);
	}

	//	private VortexClientAmiApplication removeAmiApplication(long id) {
	//		VortexClientAmiApplication r = amiApplications.remove(id);
	//		if (r != null)
	//			amiApplicationsByName.remove(r.getData().getAppId());
	//		return r;
	//	}
	//	public VortexClientAmiApplication getAmiApplication(long id) {
	//		return amiApplications.get(id);
	//	}
	//	public VortexClientAmiApplication getAmiApplicationByName(short name) {
	//		return amiApplicationsByName.get(name);
	//	}
	//	private VortexClientAmiApplication addAmiApplication(VortexAmiConnection a) {
	//		VortexClientAmiApplication app = new VortexClientAmiApplication(a, this);
	//		amiApplications.put(app.getId(), app);
	//		amiApplicationsByName.put(a.getAppId(), app);
	//		return app;
	//	}
	//	public Iterable<VortexClientAmiApplication> getAmiApplications() {
	//		return this.amiApplications.values();
	//	}

	//	//Ami Alerts
	//	private void removeAmiAlert(long id) {
	//		amiAlerts.remove(id);
	//	}
	//	public VortexClientAmiAlert getAmiAlert(long id) {
	//		return amiAlerts.get(id);
	//	}
	//	private VortexClientAmiAlert addAmiAlert(VortexAmiAlert a) {
	//		VortexClientAmiAlert alert = new VortexClientAmiAlert(a, this);
	//		amiAlerts.put(a.getId(), alert);
	//		return alert;
	//	}
	//	public Iterable<VortexClientAmiAlert> getAmiAlerts() {
	//		return this.amiAlerts.values();
	//	}
	//
	//	//Ami Command Defs
	//	private void removeAmiCommandDef(long id) {
	//		amiCommandDefs.remove(id);
	//	}
	//	public VortexClientAmiCommandDef getAmiCommandDef(long id) {
	//		return amiCommandDefs.get(id);
	//	}
	//	private VortexClientAmiCommandDef addAmiCommandDef(VortexAmiCommandDefinition a) {
	//		VortexClientAmiCommandDef def = new VortexClientAmiCommandDef(a, this);
	//		amiCommandDefs.put(a.getId(), def);
	//		return def;
	//	}
	//	public Iterable<VortexClientAmiCommandDef> getAmiCommandDefs() {
	//		return this.amiCommandDefs.values();
	//	}

	//	//Ami Objects
	//	private void removeAmiObject(long id) {
	//		VortexClientAmiObject obj = amiObjects.remove(id);
	//		if (obj != null) {
	//			String type = getAmiKeyStringFromPool(obj.getData().getType());
	//			LongKeyMap<VortexClientAmiObject> objbyType = amiObjectsByType.get(type);
	//			if (objbyType != null)
	//				objbyType.remove(id);
	//		}
	//	}
	//	private VortexClientAmiObject getAmiObject(long id) {
	//		return amiObjects.get(id);
	//	}
	//	public LongKeyMap<VortexClientAmiObject> getAmiObjectsByType(String type) {
	//		return amiObjectsByType.get(type);
	//	}
	//long count = 0;
	//long latency = 0;
	//long first;
	//Long exectime = (Long) object.getParams().get("exectime");
	//if (exectime != null) {
	//latency += EH.currentTimeMillis() - exectime;
	//
	//if (++count % 100000 == 0) {
	//System.out.println(new Date() + "|" + (System.currentTimeMillis() - first) + "|" + count + "|" + latency);
	//} else if (count == 1)
	//first = System.currentTimeMillis();
	//}
	//	private VortexClientAmiObject addAmiObject(VortexAmiObject o) {
	//		String type = getAmiKeyStringFromPool(o.getType());
	//		LongKeyMap<VortexClientAmiObject> byType = amiObjectsByType.get(type);
	//		if (byType == null)
	//			return null;
	//
	//		VortexClientAmiObject object = new VortexClientAmiObject(o, this);
	//
	//		amiObjects.put(o.getId(), object);
	//		byType.put(o.getId(), object);
	//		return object;
	//	}
	//	public Iterable<VortexClientAmiObject> getAmiObjects() {
	//		return this.amiObjects.values();
	//	}

	//deployment sets
	public VortexClientDeploymentSet getDeploymentSet(long id) {
		return deploymentSets.get(id);
	}

	private VortexClientDeploymentSet removeDeploymentSet(long id) {
		final VortexClientDeploymentSet r = deploymentSets.remove(id);
		if (r != null) {
			for (VortexClientDeployment dep : r.getDeployments()) {
				dep.setDeploymentSet(null);
				fireUpdate(dep);
			}
		}
		return r;
	}

	public Iterable<VortexClientDeploymentSet> getDeploymentSets() {
		return deploymentSets.values();
	}
	private void addDeploymentSet(VortexClientDeploymentSet exp) {
		deploymentSets.put(exp.getId(), exp);
	}

	public Iterable<VortexClientBackupFile> getBackupFiles() {
		return this.backupFiles.values();
	}

	public VortexClientBackupFile getBackupFile(long id) {
		VortexClientBackupFile r = this.backupFiles.get(id);
		return r;
	}
	public VortexClientBackupFile removeBackupFile(long id) {
		VortexClientBackupFile r = this.backupFiles.remove(id);
		if (r != null) {
			this.backupFilesByMachineUidAndPath.removeMulti(r.getMachine().getMachineUid(), r.getData().getPath());
			fireRemove(r);
		}
		return r;
	}

	//deployment
	public VortexClientDeployment getDeployment(long id) {
		return deployments.get(id);
	}

	private VortexClientDeployment removeDeployment(long id) {
		final VortexClientDeployment r = deployments.remove(id);
		if (r != null) {
			deploymentsByBuildResultIdAndId.removeMulti(r.getData().getCurrentBuildResultId(), r.getId());
			VortexClientDeploymentSet ds = r.getDeploymentSet();
			if (ds != null)
				ds.removeDeployment(r);
			r.setDeploymentSet(null);
			for (VortexClientBackup backup : r.getBackups().values()) {
				backup.setDeployment(null);
				fireUpdate(backup);
			}
			r.removeBackups();
			r.setBuildProcedure(null);
			r.setBuildResult(null);
		}
		return r;
	}

	public Iterable<VortexClientDeployment> getDeployments() {
		return deployments.values();
	}
	private void addDeployment(VortexClientDeployment dep) {
		dep.setMachine(getAgentMachineByUid(dep.getData().getTargetMachineUid()));
		deployments.put(dep.getId(), dep);
		deploymentsByBuildResultIdAndId.putMulti(dep.getData().getCurrentBuildResultId(), dep.getId(), dep);
		final VortexClientBuildProcedure bp = buildProcedures.get(dep.getData().getProcedureId());
		final VortexClientBuildResult br = dep.getData().getCurrentBuildResultId() == null ? null : buildResults.get(dep.getData().getCurrentBuildResultId());
		final VortexClientDeploymentSet ds = deploymentSets.get(dep.getData().getDeploymentSetId());
		dep.setBuildProcedure(bp);
		if (br != null) {
			dep.setBuildResult(br);
			br.addDeployment(dep);
			fireUpdate(br);
		}
		dep.setDeploymentSet(ds);
		if (ds != null)
			ds.addDeployment(dep);
		Map<Long, VortexClientBackup> backups = backupsByDeploymentIdAndId.get(dep.getId());
		if (backups != null) {
			for (VortexClientBackup value : backups.values()) {
				value.setDeployment(dep);
				dep.addBackup(value);
				fireUpdate(value);
			}
		}
	}
	private void updateDeployment(VortexClientDeployment dep, VortexDeployment agentNode) {
		VortexDeployment existing = dep.getData();
		if (agentNode.askExists(VortexDeployment.PID_CURRENT_BUILD_RESULT_ID) && OH.ne(existing.getCurrentBuildResultId(), agentNode.getCurrentBuildResultId())) {
			VortexClientBuildResult br = dep.getBuildResult();
			if (br != null) {
				br.removeDeployment(dep);
				fireUpdate(br);
			}
			VortexClientBuildResult br2 = agentNode.getCurrentBuildResultId() == null ? null : getBuildResult(agentNode.getCurrentBuildResultId());
			dep.setBuildResult(br2);
			deploymentsByBuildResultIdAndId.removeMulti(existing.getCurrentBuildResultId(), existing.getId());
			deploymentsByBuildResultIdAndId.putMulti(agentNode.getCurrentBuildResultId(), existing.getId(), dep);
			br2.addDeployment(dep);
			fireUpdate(br2);

		}
		if (agentNode.askExists(VortexDeployment.PID_PROCEDURE_ID) && existing.getProcedureId() != agentNode.getProcedureId()) {
			dep.setBuildProcedure(getBuildProcedure(agentNode.getProcedureId()));
		}
		if (agentNode.askExists(VortexDeployment.PID_DEPLOYMENT_SET_ID) && existing.getDeploymentSetId() != agentNode.getDeploymentSetId()) {
			VortexClientDeploymentSet ds = dep.getDeploymentSet();
			if (ds != null)
				ds.removeDeployment(dep);
			VortexClientDeploymentSet ds2 = getDeploymentSet(agentNode.getDeploymentSetId());
			dep.setDeploymentSet(ds2);
			if (ds2 != null)
				ds2.addDeployment(dep);
		}

		dep.update(agentNode);
	}

	//DBSERVERS
	final private LongKeyMap<VortexClientDbServer> machineDbServers = new LongKeyMap<VortexClientDbServer>();

	public void addDbServer(VortexClientDbServer node) {
		node.setMachine(getAgentMachineByUid(node.getData().getMachineUid()));
		machineDbServers.put(node.getData().getId(), node);
	}
	public VortexClientDbServer removeDbServer(long id) {
		VortexClientDbServer r = machineDbServers.remove(id);
		if (r != null) {
			for (VortexClientDbDatabase database : r.getDatabases()) {
				for (VortexClientDbTable table : database.getTables()) {
					for (VortexClientDbColumn column : table.getColumns())
						fireRemove(column);
					fireRemove(table);
				}
				for (VortexClientDbObject object : database.getObjects())
					fireRemove(object);
				for (VortexClientDbPrivilege object : database.getPrivileges())
					fireRemove(object);
				fireRemove(database);
				//removeDbDatabase(database.getId());
				//TODO: notify
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientDbServer getDbServer(long id) {
		return machineDbServers.get(id);
	}
	public Iterable<VortexClientDbServer> getDbServers() {
		return machineDbServers.values();
	}
	public VortexEntity getEntityById(long id) {
		return entities.get(id);
	}
	//	public VortexClientAmiEntity<?> getAmiEntityById(long id) {
	//		return amiEntities.get(id);
	//	}

	public Iterable<VortexEyeJournalReport> getJournalReports() {
		return this.eyeJournalReports.values();
	}
	public void addF1AppEntities(List<F1AppEntity> toAdd) {
		LongSet f1AppsJustAdded = null;
		ArrayList<AgentWebObject<?>> toBind = new ArrayList<AgentWebObject<?>>(toAdd.size());
		for (F1AppEntity f1entity : toAdd) {
			if (f1entity instanceof F1AppInstance) {
				addF1App((F1AppInstance) f1entity);
				if (f1AppsJustAdded == null)
					f1AppsJustAdded = new LongSet();
				f1AppsJustAdded.add(f1entity.getId());
			} else {
				toBind.add(getJavaAppState(f1entity.getF1AppInstanceId()).addObject(f1entity));
			}
		}
		for (AgentWebObject<?> entity : toBind)
			entity.bind();
		if (f1AppsJustAdded == null) {
			for (VortexClientF1AppStateListener p : f1AppListeners)
				for (AgentWebObject<?> entity : toBind)
					p.onF1AppEntityAdded(entity);
		} else {
			for (VortexClientF1AppStateListener p : f1AppListeners) {
				for (long l : f1AppsJustAdded)
					p.onF1AppAdded(getJavaAppState(l));
			}
			for (AgentWebObject<?> entity : toBind)
				if (!f1AppsJustAdded.contains(entity.getObject().getF1AppInstanceId())) {
					for (VortexClientF1AppStateListener p : f1AppListeners)
						p.onF1AppEntityAdded(entity);
				}
		}
	}

	private long currentSeqNum;

	private List<VortexEyeClientEvent> clientEvents = new ArrayList<VortexEyeClientEvent>();

	public long getCurrentSeqNum() {
		return currentSeqNum;
	}
	public void setCurrentSeqNum(long currentSeqNum) {
		this.currentSeqNum = currentSeqNum;
	}

	static public class HostNameComparator implements Comparator<VortexClientMachineEntity<?>> {

		public static final HostNameComparator INSTANCE = new HostNameComparator();

		@Override
		public int compare(VortexClientMachineEntity<?> o1, VortexClientMachineEntity<?> o2) {
			return SH.COMPARATOR_CASEINSENSITIVE.compare(o1.getHostName(), o2.getHostName());
		}
	}

	public void fireSnapshotProcessed() {
		this.isEyeConnected = true;
		for (VortexClientManagerListener p : connectListeners) {
			try {
				p.onVortexEyeSnapshotProcessed();
			} catch (Exception e) {
				LH.warning(log, "Error processing connect for portlet: ", p, e);
			}
		}
	}
	public Iterable<VortexEyeClientEvent> getClientEvents() {
		return clientEvents;
	}
	public Map<String, VortexClientMetadataField> getMetadataFieldsForEntityType(byte entityType) {
		Map<String, VortexClientMetadataField> r = new HashMap<String, VortexClientMetadataField>();
		for (VortexClientMetadataField mf : getMetadataFields()) {
			if (mf.appliesTo(entityType)) {
				r.put(mf.getData().getKeyCode(), mf);
			}
		}
		return r;
	}

	public void addMachineListener(VortexClientMachineListener listener) {
		CH.addIdentityOrThrow(machineListeners, listener);
		fireClientListenerAdded(listener);
	}

	public void removeMachineListener(VortexClientMachineListener listener) {
		CH.removeOrThrow(machineListeners, listener);
	}

	public void addF1AppListener(VortexClientF1AppStateListener listener) {
		CH.addIdentityOrThrow(f1AppListeners, listener);
		fireClientListenerAdded(listener);
		//for (VortexClientF1AppState app : this.f1Apps.values())
		//listener.onF1AppAdded(app);
	}
	public void removeF1AppListener(VortexClientF1AppStateListener listener) {
		CH.removeOrThrow(f1AppListeners, listener);
	}

	public void addClientConnectedListener(VortexClientManagerListener listener) {
		CH.addIdentityOrThrow(this.connectListeners, listener);
		fireClientListenerAdded(listener);
	}
	public void removeClientConnectedListener(VortexClientManagerListener listener) {
		CH.removeOrThrow(this.connectListeners, listener);
	}
	public void addClientEventlistener(VortexClientEventListener listener) {
		CH.addIdentityOrThrow(clientEventListeners, listener);
		fireClientListenerAdded(listener);
	}
	public void removeClientEventListener(VortexClientEventListener listener) {
		CH.removeOrThrow(clientEventListeners, listener);
	}
	public void addJournalReportListener(VortexClientJournalReportListener listener) {
		CH.addIdentityOrThrow(reportListeners, listener);
		fireClientListenerAdded(listener);
	}
	public void removeJournalReportListener(VortexClientJournalReportListener listener) {
		CH.removeOrThrow(reportListeners, listener);
	}

	public void addAuditEventsListener(VortexClientAuditEventsListener listener) {
		CH.addIdentityOrThrow(this.auditEventsListeners, listener);
		fireClientListenerAdded(listener);
	}

	public void removeAuditEventsListener(VortexClientAuditEventsListener listener) {
		CH.removeOrThrow(this.auditEventsListeners, listener);
	}

	private void fireClientListenerAdded(Object o) {
		for (VortexClientManagerListener i : this.connectListeners)
			i.onVortexClientListenerAdded(o);
	}
	public boolean getIsEyeConnected() {
		return isEyeConnected;
	}
	//	public void addAmiListener(VortexClientAmiListener listener) {
	//		CH.addIdentityOrThrow(this.amiListeners, listener);
	//		//rebuildAmiToObjects();
	//	}
	//	public void rebuildAmiToObjects() {
	//		Set<String> nuw = new HashSet<String>();
	//		for (VortexClientAmiListener i : this.amiListeners)
	//			nuw.addAll(i.getInterestedObjectTypes());
	//		Set<String> current = this.amiObjectsByType.keySet();
	//		Set<String> added = CH.comm(nuw, current, true, false, false);
	//		Set<String> removed = CH.comm(nuw, current, false, true, false);
	//		for (String remove : removed) {
	//			LongIterator ids = this.amiObjectsByType.remove(remove).keyIterator();
	//			while (ids.hasNext())
	//				amiObjects.remove(ids.nextLong());
	//		}
	//		if (added != null) {
	//			for (String add : added) {
	//				this.amiObjectsByType.put(add, new LongKeyMap<VortexClientAmiObject>());
	//				//TODO:send request to backend
	//			}
	//		}
	//	}

	//	public void addAmiObjectTypeBeingViewed(String type) {
	//		CH.putOrThrow(this.amiObjectsByType, type, new LongKeyMap<VortexClientAmiObject>());
	//	}
	//	public void removeAmiObjectTypeBeingViewed(String type) {
	//		LongIterator ids = CH.removeOrThrow(this.amiObjectsByType, type).keyIterator();
	//		while (ids.hasNext()) {
	//			long id = ids.nextLong();
	//			amiObjects.remove(id);
	//			amiEntities.remove(id);
	//		}
	//	}
	//	public Set<String> getAmiObjectTypesBeingViewed() {
	//		return this.amiObjectsByType.keySet();
	//		//Set<String> current = new HashSet<String>(this.amiObjectsByType.size());
	//		//for (int i : this.amiObjectsByType.keys())
	//		//current.add(getAmiKeyStringFromPool((short) i));
	//		//return current;
	//	}
	//	public int getAmiObjectTypesBeingViewedByCount(String type) {
	//		int cnt = 0;
	//		for (VortexClientAmiListener i : this.amiListeners)
	//			if (i.isRealtime() && i.getInterestedObjectTypes().contains(type))
	//				cnt++;
	//		return cnt;
	//	}
	//
	//	public void removeAmiListener(VortexClientAmiListener listener) {
	//		CH.removeOrThrow(this.amiListeners, listener);
	//		if (listener.isRealtime())
	//			for (String remove : listener.getInterestedObjectTypes()) {
	//				if (getAmiObjectTypesBeingViewedByCount(remove) == 0)// none left.. bye bye
	//					removeAmiObjectTypeBeingViewed(remove);
	//			}
	//		//rebuildAmiToObjects();
	//	}

	//	public String getAmiKeyStringFromPool(short type) {
	//		String r = amiStringKeyPool[type];
	//		if (r != null)
	//			return r;
	//		if (type == 0)
	//			return null;
	//		return "<key " + SH.toString(type) + ">";
	//	}
	//	private String[] amiStringKeyPool = new String[Short.MAX_VALUE];
	//
	//	private Map<String, Integer> amiStringKeyPoolMap = new HashMap<String, Integer>();
	//	public void addAmiKeyStringPoolMappings(Map<Short, String> amiStringPoolMap) {
	//		for (Entry<Short, String> e : amiStringPoolMap.entrySet()) {
	//			this.amiStringKeyPool[e.getKey()] = e.getValue();
	//			this.amiStringKeyPoolMap.put(e.getValue(), e.getKey().intValue());
	//		}
	//	}
	//	public Map<String, Integer> getAmiKeyStringPool() {
	//		return amiStringKeyPoolMap;
	//	}
	//
	//	private IntKeyMap<String> amiStringValuePoolMappings = new IntKeyMap<String>();
	//	private Map<String, Integer> amiStringValuePoolMappings2 = new HashMap<String, Integer>();
	//
	//	public void addAmiValuesStringPoolMappings(int key, String value) {
	//		amiStringValuePoolMappings.put(key, value);
	//		amiStringValuePoolMappings2.put(value, key);
	//	}
	//	public String getAmiValuesStringPoolMapping(int key) {
	//		if (key == 0)
	//			return null;
	//		String r = amiStringValuePoolMappings.get(key);
	//		if (r != null)
	//			return r;
	//		return "<val " + SH.toString(key) + ">";
	//	}
	//	public Map<String, Integer> getAmiValueStringPool() {
	//		return amiStringValuePoolMappings2;
	//	}
}
