package com.vortex.eye.processors;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.povo.standard.CountMessage;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.VH;
import com.f1.utils.casters.Caster_File;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentFileSystem;
import com.f1.vortexcommon.msg.agent.VortexAgentMachine;
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
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.vortex.agent.VortexAgentUtils;
import com.vortex.eye.VortexEyeDbService;
import com.vortex.eye.VortexEyeJournal;
import com.vortex.eye.itinerary.VortexEyeInspectDbSchemaItinerary;
import com.vortex.eye.processors.agent.VortexEyeAgentDeploymentChangesProcessor;
import com.vortex.eye.state.VortexEyeMachineState;
import com.vortex.eye.state.VortexEyeState;
import com.vortex.eye.state.VortexEyeState.AgentInterface;

public class VortexEyeStartupProcessor extends VortexEyeBasicProcessor<Message> {

	public OutputPort<CountMessage> onStartedPort = newOutputPort(CountMessage.class);

	public VortexEyeStartupProcessor() {
		super(Message.class);
	}

	@Override
	public void processAction(Message action, VortexEyeState state, ThreadScope threadScope) throws Exception {

		//handle journal
		{
			File journalFile = getTools().getRequired("journal.file", File.class);
			File journalDir = journalFile.getParentFile();
			IOH.ensureDir(journalDir);
			long freeSpace = journalDir.getFreeSpace();
			if (freeSpace < 1024 * 1024 * 100) {
				String msg = ("EXITING DUE TO LIMITED DISK SPACE, MUST BE ATLEAST 100 MB FREE: " + SH.formatMemory(journalDir.getFreeSpace()));
				System.out.println(msg);
				System.err.println(msg);
				log.severe(msg);
				EH.systemExit(17);
				System.exit(17);
			} else {
				try {
					VortexEyeJournal journal = new VortexEyeJournal(journalFile, getServices().getGenerator());
					journal.logStartup();
					state.setJournal(journal);
				} catch (Exception e) {
					e.printStackTrace(System.err);
					System.err.println("journal is inconsistent with license file, exiting with error code 17: " + IOH.getFullPath(journalFile));
					EH.systemExit(17);
					System.exit(17);
				}
			}
		}

		//		String amiIndexes = getTools().getOptional("ami.object.indexes");
		//		if (SH.is(amiIndexes)) {
		//			for (String index : SH.split(',', amiIndexes)) {
		//				if (index.indexOf('.') == -1)
		//					throw new RuntimeException("Malformatted ami index (should be 'type.param' ): " + index);
		//				String objectType = SH.beforeFirst(index, '.').trim();
		//				String param = SH.afterFirst(index, '.').trim();
		//				state.addAmiIndex(objectType, param);
		//				log.info("Created Ami object Index for: " + index);
		//			}
		//		}

		//handle agent packaging
		try {
			File packageDirectory = getTools().getOptional("vortex.eye.agent.package.dir", Caster_File.INSTANCE);
			if (packageDirectory != null) {
				String defaultTargetDirectory = getTools().getRequired("vortex.eye.agent.default.target.dir");
				String interfaces[] = SH.trimStrings(SH.split(',', getTools().getRequired("vortex.eye.agent.interfaces")));
				Map<String, AgentInterface> agentInterfaces = new HashMap<String, VortexEyeState.AgentInterface>();
				for (String agentInterface : interfaces) {
					PropertyController props = getTools().getSubPropertyController("agent.interface." + agentInterface + ".");
					String hostName = props.getRequired("hostname");
					String description = props.getRequired("description");
					Integer port = props.getRequired("port", Integer.class);
					String keyFile = props.getOptional("keyfile");
					String keyPassword = props.getOptional("keypassword");
					agentInterfaces.put(agentInterface, new AgentInterface(hostName, port, keyFile != null, keyFile, keyPassword, description));
				}
				state.initAgentConfig(packageDirectory, agentInterfaces, defaultTargetDirectory, getTools().getSubPropertyController("agent.properties.").getProperties());
			}
		} catch (Exception e) {
			LH.warning(log, "will not be able to deploy agents", e);
		}

		final VortexEyeDbService dbservice = getDbService();
		if (dbservice == null) {
			LH.info(log, "Skipping query from database.");
		} else {
			final Connection conn = dbservice.getConnection();
			LH.info(log, "Starting query from database.");
			final long now = getTools().getNow();
			try {

				//load active agent snapshots
				loadAgentMachines(conn, state);

				//load active data servers
				LongKeyMap<VortexAgentDbServer> dbServers = loadDbServers(conn);
				for (VortexAgentDbServer server : dbServers.values()) {
					applyDbServerId(server);
					state.addDbServer(server);
					VortexEyeInspectDbSchemaItinerary.assertIds(server);
				}

				//load metadata fields
				for (VortexEyeMetadataField dest : dbservice.queryMetadataFields(conn))
					state.addMetadataField(dest);
				LH.info(log, "Queried metadata fields database: ", state.getBackupDestinationsCount());

				//load backup destinations
				for (VortexEyeBackupDestination dest : dbservice.queryBackupDestinations(conn))
					state.addBackupDestination(dest);
				LH.info(log, "Queried backup destinations from database: ", state.getBackupDestinationsCount());

				//load active audit rules
				for (VortexEyeAuditTrailRule auditTrailRule : dbservice.queryAuditTrailRules(conn).values())
					state.addAuditTrailRule(auditTrailRule);
				LH.info(log, "Queried active audit Rules from database: ", state.getAuditTrailRulesCount());

				//load active expectations
				for (VortexExpectation expectation : dbservice.queryExpectations(conn).values())
					state.addExpectation(expectation);
				LH.info(log, "Queried active expectations from database: ", state.getExpectationsCount());

				//load build servers
				for (VortexBuildProcedure bp : dbservice.queryBuildProcedures(conn).values())
					state.addBuildProcedure(bp);
				LH.info(log, "Queried active build procedures from database: ", state.getBuildProceduresCount());

				//load active deployments
				for (VortexDeployment dep : dbservice.queryDeployments(conn).values())
					state.addDeployment(dep);
				LH.info(log, "Queried active deployments from database: ", state.getDeploymentsCount());

				//load active deployment sets
				for (VortexDeploymentSet ds : dbservice.queryDeploymentSets(conn).values())
					state.addDeploymentSet(ds);
				LH.info(log, "Queried active deploymentSets from database: ", state.getDeploymentSetsCount());

				//load active scheduled tasks
				for (VortexEyeScheduledTask dep : dbservice.queryScheduledTasks(conn).values()) {
					//TODO: determine next runtime, should it be kicked off now?
					state.addScheduledTask(dep);
				}
				LH.info(log, "Queried active scheduled tasks from database: ", state.getScheduledTasksCount());

				//load active scheduled tasks
				List<VortexEyeClientEvent> events = dbservice.queryClientEvents(conn);
				VH.sort(events, VortexEyeClientEvent.PID_NOW);
				for (VortexEyeClientEvent event : events)
					state.addClientEvent(event);
				LH.info(log, "Queried active scheduled tasks from database: ", state.getScheduledTasksCount());

				//load active build results
				for (VortexBuildResult bp : dbservice.queryBuildResults(conn).values())
					state.addBuildResult(bp);
				LH.info(log, "Queried active buildResults from database: ", state.getBuildResultsCount());

				//load active backups
				for (VortexEyeBackup bp : dbservice.queryBackups(conn))
					state.addBackup(bp);
				LH.info(log, "Queried active backups from database: ", state.getBackupsCount());

				//load cloud interfaces
				for (VortexEyeCloudInterface ci : dbservice.queryCloudInterfaces(conn))
					state.addCloudInterface(ci);
				LH.info(log, "Queried cloud interfaces from database: ", state.getCloudInterfacesCount());

				//load backup files
				long last = -1;
				for (VortexAgentBackupFile bf : dbservice.queryBackupFiles(conn)) {
					if (bf.getBackupId() != last) {
						if (state.getBackup(bf.getBackupId()) == null) {
							LH.warning(log, "backup file missing backup: ", bf);
							continue;
						} else
							last = bf.getBackupId();//cache to avoid map lookup each time
					}
					bf.setStatus(VortexAgentBackupFile.STATUS_OFFLINE);
					state.addBackupFile(bf);
				}
				LH.info(log, "Queried backup files from database: ", state.getBackupFilesCount());

				int cnt = 0;
				for (VortexDeployment dep : state.getDeployments()) {
					if (dep.getStatus() != VortexDeployment.STATUS_PROCESS_AGENT_DOWN___) {
						dep = dep.clone();
						dep.setNow(now);
						dep.setStatus(VortexDeployment.STATUS_PROCESS_AGENT_DOWN___);
						state.addDeployment(dep);
						VortexEyeAgentDeploymentChangesProcessor.insertDeploymentStatus(dep, this.getTools());
						cnt++;
					}
				}
				//LH.info(log, "Updated deployments to agent-not-running status: ", cnt);

				//load ami applications
				//cnt = 0;
				//				for (VortexAmiApplication aa : dbservice.queryAmiApplications(conn)) {
				//					//aa.setConnectionsCount(0);
				//					//state.putAmiApplication(aa);
				//					cnt++;
				//				}
				//LH.info(log, "Queried ami applications database: ", cnt);

				//load ami alerts
				//cnt = 0;
				//for (VortexAmiAlert alert : dbservice.queryAmiAlerts(conn, now)) {
				//VortexEyeAmiApplication app = state.getAmiAppById(alert.getAmiApplicationId());
				//if (app == null)
				//LH.warning(log, "app not found for ami alert: ", alert.getAlertId(), ", appId: ", alert.getAmiApplicationId());
				//else {
				//app.putAmiAlert(alert);
				//if (alert.getExpiresInSeconds() != 0)
				//state.addExpiringAmiAlert(alert);
				//cnt++;
				//}
				//}
				//LH.info(log, "Queried ami alerts database: ", cnt);

				//load ami objects
				//				cnt = 0;
				//				for (VortexAmiObject object : dbservice.queryAmiObjects(conn, now)) {
				//					VortexEyeAmiApplication app = state.getAmiAppById(object.getAmiApplicationId());
				//					if (app == null)
				//						LH.warning(log, "app not found for ami object: ", object.getId(), ", appId: ", object.getAmiApplicationId());
				//					else {
				//						app.putAmiObject(object);
				//						if (object.getExpiresInSeconds() != 0)
				//							state.addExpiringAmiObject(object);
				//						cnt++;
				//					}
				//				}
				//				LH.info(log, "Queried ami objects database: ", cnt);

				LH.info(log, "Finished query from database");

			} finally {
				IOH.close(conn);
			}
		}
		onStartedPort.send(nw(CountMessage.class), threadScope);
	}
	private LongKeyMap<VortexAgentDbServer> loadDbServers(Connection conn) throws Exception {
		VortexEyeDbService dbservice = getDbService();
		LongKeyMap<VortexAgentDbServer> dbServers = dbservice.queryDbServers(conn);
		LH.info(log, "Queried active dbServers from database: " + dbServers.size());

		LongKeyMap<VortexAgentDbDatabase> databases = dbservice.queryDatabases(conn);
		LH.info(log, "Queried active databases from database: " + databases.size());

		LongKeyMap<VortexAgentDbTable> tables = dbservice.queryTables(conn);
		LH.info(log, "Queried active tables from database: " + tables.size());

		LongKeyMap<VortexAgentDbColumn> columns = dbservice.queryColumns(conn);
		LH.info(log, "Queried active columns from database: " + columns.size());

		LongKeyMap<VortexAgentDbObject> objects = dbservice.queryDbObjects(conn);
		LH.info(log, "Queried active objects from database: " + objects.size());

		LongKeyMap<VortexAgentDbPrivilege> privs = dbservice.queryPrivileges(conn);
		LH.info(log, "Queried active privs from database: " + privs.size());

		//handle servers
		for (VortexAgentDbServer o : dbServers.values())
			o.setDatabases(new HashMap<String, VortexAgentDbDatabase>());

		//handle databases
		for (VortexAgentDbDatabase o : databases.values()) {
			o.setObjects(new ArrayList<VortexAgentDbObject>());
			o.setPrivileges(new ArrayList<VortexAgentDbPrivilege>());
			o.setTables(new HashMap<String, VortexAgentDbTable>());
			VortexAgentDbServer dbs = dbServers.get(o.getDbServerId());
			if (dbs == null)
				LH.warning(log, "Skipping database for unknown server: ", o);
			else
				dbs.getDatabases().put(o.getName(), o);
		}

		//handle tables
		for (VortexAgentDbTable o : tables.values()) {
			o.setColumns(new HashMap<String, VortexAgentDbColumn>());
			VortexAgentDbDatabase db = databases.get(o.getDatabaseId());
			if (db == null)
				LH.warning(log, "Skipping table for unknown database: ", o);
			else
				db.getTables().put(o.getName(), o);
		}

		//handle columns
		for (VortexAgentDbColumn o : columns.values()) {
			VortexAgentDbTable tb = tables.get(o.getTableId());
			if (tb == null)
				LH.warning(log, "Skipping column for unknown table: ", o);
			else
				tb.getColumns().put(o.getName(), o);
		}

		//handle objects
		for (VortexAgentDbObject o : objects.values()) {
			final VortexAgentDbDatabase db = databases.get(o.getDatabaseId());
			if (db == null)
				LH.warning(log, "Skipping object for unknown database: ", o);
			else
				db.getObjects().add(o);
		}

		//handle privileges
		for (VortexAgentDbPrivilege o : privs.values()) {
			final VortexAgentDbDatabase db = databases.get(o.getDatabaseId());
			if (db == null)
				log.warning("Skipping privilege for unknown database: " + o);
			else
				db.getPrivileges().add(o);
		}
		return dbServers;
	}

	private LongKeyMap<VortexEyeMachineState> loadAgentMachines(Connection connection, VortexEyeState state) throws Exception {
		VortexEyeDbService dbservice = getDbService();
		LongKeyMap<VortexAgentMachine> agentMachines = dbservice.queryMachineInstance(connection);
		LH.info(log, "Queried agent Machines: " + agentMachines.size());

		LongKeyMap<VortexEyeMachineState> r = new LongKeyMap<VortexEyeMachineState>();
		for (VortexAgentMachine m : agentMachines.values()) {
			VortexEyeMachineState ss = state.createMachineState(m);
			//ss.addEntity(VortexAgentUtils.getKey(m), null, m);
			r.put(m.getId(), ss);

		}

		//processes
		List<VortexAgentProcess> processes = dbservice.queryProcesses(connection);
		LH.info(log, "Queried agent processes: " + processes.size());
		for (VortexAgentProcess value : processes) {
			VortexEyeMachineState ss = r.get(value.getMachineInstanceId());
			if (ss == null)
				LH.warning(log, "Skipping process for unknown machine: ", value);
			else
				ss.addEntityNoThrow(VortexAgentUtils.getKey(value), null, value);
		}

		//network connections
		List<VortexAgentNetConnection> connections = dbservice.queryNetConnections(connection);
		LH.info(log, "Queried agent connections: " + connections.size());
		for (VortexAgentNetConnection value : connections) {
			VortexEyeMachineState ss = r.get(value.getMachineInstanceId());
			if (ss == null)
				LH.warning(log, "Skipping network connection for unknown machine: ", value);
			else
				ss.addEntityNoThrow(VortexAgentUtils.getKey(value), null, value);
		}

		//network links
		List<VortexAgentNetLink> links = dbservice.queryNetLinks(connection);
		LH.info(log, "Queried agent links: " + links.size());
		for (VortexAgentNetLink value : links) {
			VortexEyeMachineState ss = r.get(value.getMachineInstanceId());
			if (ss == null)
				LH.warning(log, "Skipping network link for unknown machine: ", value);
			else
				ss.addEntityNoThrow(VortexAgentUtils.getKey(value), null, value);
		}

		//network addresses
		List<VortexAgentNetAddress> addresses = dbservice.queryNetAddresses(connection);
		LH.info(log, "Queried addresses: " + addresses.size());
		for (VortexAgentNetAddress value : addresses) {
			VortexEyeMachineState ss = r.get(value.getMachineInstanceId());
			if (ss == null)
				LH.warning(log, "Skipping network address for unknown machine: ", value);
			else
				ss.addEntityNoThrow(VortexAgentUtils.getKey(value), null, value);
		}

		//file systems
		List<VortexAgentFileSystem> fileSystems = dbservice.queryFileSystems(connection);
		LH.info(log, "Queried file systems: " + fileSystems.size());
		for (VortexAgentFileSystem value : fileSystems) {
			VortexEyeMachineState ss = r.get(value.getMachineInstanceId());
			if (ss == null)
				LH.warning(log, "Skipping file system for unknown machine: ", value);
			else
				ss.addEntityNoThrow(VortexAgentUtils.getKey(value), null, value);
		}

		//crons
		//List<VortexAgentCron> crontabs = dbservice.queryCrontab(connection);
		//LH.info(log, "Queried crontabs: " + crontabs.size());
		//for (VortexAgentCron value : crontabs) {
		//VortexEyeAgentState ss = r.get(value.getMachineInstanceId());
		//if (ss == null)
		//LH.warning(log, "Skipping crontab for unknown machine: ", value);
		//else {
		//String key = VortexAgentUtils.getKey(value);
		//List<VortexAgentCron> list = ss.getCron().get(key);
		//if (list == null)
		//ss.getCron().put(key, list = new ArrayList<VortexAgentCron>(1));
		//list.add(value);
		//}
		//}
		return r;

	}
	public static void applyDbServerId(VortexAgentDbServer existingDbServer) {
		final long dbServerId = existingDbServer.getId();
		for (VortexAgentDbDatabase db : CH.values(existingDbServer.getDatabases())) {
			db.setDbServerId(dbServerId);
			for (VortexAgentDbTable table : CH.values(db.getTables())) {
				table.setDbServerId(dbServerId);
				for (VortexAgentDbColumn column : CH.values(table.getColumns())) {
					column.setDbServerId(dbServerId);
				}
			}
			for (VortexAgentDbObject object : db.getObjects())
				object.setDbServerId(dbServerId);
			for (VortexAgentDbPrivilege privilege : db.getPrivileges())
				privilege.setDbServerId(dbServerId);
		}

	}

}
