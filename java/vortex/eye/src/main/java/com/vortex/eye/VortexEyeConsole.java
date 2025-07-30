package com.vortex.eye;

import com.f1.base.Table;
import com.f1.container.Container;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class VortexEyeConsole {

	private Container container;

	public VortexEyeConsole(Container container) {
		this.container = container;
	}

	public Table query(String sqlExpression) {
		SqlProcessor sp = new SqlProcessor();
		Tableset bqf = new TablesetImpl();
		//TODO: populate tableset

		//		state.getPartition().lockForRead(1, TimeUnit.SECONDS);
		//		final BasicQueryFacade bqf = new BasicQueryFacade(container.getServices().getGenerator());
		//		VortexEyeState state = container.getPartitionController().getState(VortexEyeSuite.PARTITIONID_VORTEX_EYE, VortexEyeState.class);
		//		state.getPartition().lockForRead(1, TimeUnit.SECONDS);
		//
		//		try {
		//			addRows(bqf, "deployments", VortexDeployment.class, state.getDeployments(), sqlExpression, false);
		//			addRows(bqf, "auditTrailRules", VortexEyeAuditTrailRule.class, state.getAuditTrailRules(), sqlExpression, false);
		//			addRows(bqf, "buildProcedures", VortexBuildProcedure.class, state.getBuildProcedures(), sqlExpression, false);
		//			addRows(bqf, "dbServers", VortexAgentDbServer.class, state.getDbServers(), sqlExpression, false);
		//			addRows(bqf, "buildResults", VortexBuildResult.class, state.getBuildResults(), sqlExpression, false);
		//			addRows(bqf, "expectations", VortexExpectation.class, state.getExpectations(), sqlExpression, false);
		//			addRows(bqf, "backupFiles", VortexAgentBackupFile.class, state.getBackupFiles(), sqlExpression, false);
		//			boolean forceFileSystems = false, forceProcesses = false, forceMachines = false, forceNetLinks = false, forceNetAddresses = false, forceConnections = false;
		//			BasicTable f1AppsTable = new BasicTable(new Object[] { "processUid", "appName", "mainClassName", "agentConnectTime", "agentDisconnectTime", "freeMemory", "maxMemory",
		//					"nowMs", "hostName", "javaHome", "javaVendor", "javaVersion" });
		//			f1AppsTable.setTitle("f1Apps");
		//
		//			BasicTable agentsTable = new BasicTable(new Object[] { "remoteHost", "remotePort", "processUid", "connectedTime", "seqNum" });
		//			agentsTable.setTitle("agents");
		//
		//			BasicTable agentEntitiesTable = new BasicTable(new Object[] { "id", "miid", "now", "type", "agentProcessUid", "agentRemoteHost" });
		//			agentEntitiesTable.setTitle("agentEntities");
		//			bqf.addTable(agentEntitiesTable);
		//			BasicTable f1AppEntitiesTable = new BasicTable(long.class, "origId", long.class, "id", long.class, "mid", String.class, "type", String.class, "f1appProcessUid",
		//					String.class, "f1appName", String.class, "f1appHost");
		//			f1AppEntitiesTable.setTitle("f1AppEntities");
		//			bqf.addTable(f1AppEntitiesTable);
		//
		//			for (VortexEyeMachineState machine : state.getAllMachines()) {
		//				VortexEyeAgentState agent = machine.getAgentState();
		//				for (VortexAgentEntity e : machine.getEntities()) {
		//					agentEntitiesTable.getRows().addRow(e.getId(), e.getMachineInstanceId(), e.getNow(), e.askSchema().askOriginalType().getSimpleName(),
		//							agent == null ? "<not active>" : agent.getProcessUid(), agent == null ? "<not active>" : agent.getRemoteHost());
		//				}
		//				//VortexAgentSnapshot ss = machine.getSnapshot();
		//				VortexEntityMap ebt = machine.getEntitiesByType();
		//				forceMachines = addRows(bqf, "machines", VortexAgentMachine.class, CH.l(machine.getMachine()), sqlExpression, forceMachines);
		//				forceFileSystems = addRows(bqf, "fileSystems", VortexAgentFileSystem.class, ebt.getEntities(VortexAgentFileSystem.class).values(), sqlExpression, forceFileSystems);
		//				forceProcesses = addRows(bqf, "processes", VortexAgentProcess.class, ebt.getEntities(VortexAgentProcess.class).values(), sqlExpression, forceProcesses);
		//				forceNetLinks = addRows(bqf, "netLinks", VortexAgentNetLink.class, ebt.getEntities(VortexAgentNetLink.class).values(), sqlExpression, forceNetLinks);
		//				forceNetAddresses = addRows(bqf, "netAddresses", VortexAgentNetAddress.class, ebt.getEntities(VortexAgentNetAddress.class).values(), sqlExpression,
		//						forceNetAddresses);
		//				forceConnections = addRows(bqf, "connections", VortexAgentNetConnection.class, ebt.getEntities(VortexAgentNetConnection.class).values(), sqlExpression,
		//						forceConnections);
		//			}
		//			QueryTable<Valued> f1loggersTable = bqf.addTable("f1Loggers", container.nw(F1AppLogger.class).askSchema());
		//			QueryTable<Valued> f1threadsTable = bqf.addTable("f1Threads", container.nw(F1AppThreadScope.class).askSchema());
		//			QueryTable<Valued> f1PropertiesTable = bqf.addTable("f1Properties", container.nw(F1AppProperty.class).askSchema());
		//			QueryTable<Valued> f1PartitionTable = bqf.addTable("f1Partitions", container.nw(F1AppPartition.class).askSchema());
		//			for (VortexEyeAgentState agent : state.getAgents()) {
		//				agentsTable.getRows().addRow(agent.getRemoteHost(), agent.getRemotePort(), agent.getProcessUid(), agent.getConnectedTime(), agent.getCurrentSeqNum());
		//				for (VortexEyeF1AppState f1app : agent.getF1Apps()) {
		//					F1AppInstance f1ss = f1app.getF1AppInstance();
		//					f1AppsTable.getRows().addRow(f1ss.getProcessUid(), f1ss.getAppName(), f1ss.getMainClassName(), f1ss.getAgentConnectTime(), f1ss.getAgentDisconnectTime(),
		//							f1ss.getFreeMemory(), f1ss.getMaxMemory(), f1ss.getNowMs(), f1ss.getHostName(), f1ss.getJavaHome(), f1ss.getJavaVendor(), f1ss.getJavaVersion());
		//					f1AppEntitiesTable.getRows().addRow(f1app.getOrigId(), f1ss.getId(), f1ss.getF1AppInstanceId(), f1ss.askSchema().askOriginalType().getSimpleName(),
		//							f1app.getPuid(), f1app.getF1AppInstance().getAppName(), f1app.getF1AppInstance().getHostName());
		//					for (Node<F1AppEntity> entry : f1app.getEntities()) {
		//						F1AppEntity e = entry.getValue();
		//						f1AppEntitiesTable.getRows().addRow(entry.getKey(), e.getId(), e.getF1AppInstanceId(), e.askSchema().askOriginalType().getSimpleName(), f1app.getPuid(),
		//								f1app.getF1AppInstance().getAppName(), f1app.getF1AppInstance().getHostName());
		//						if (e instanceof F1AppLogger)
		//							f1loggersTable.addRow(e);
		//						else if (e instanceof F1AppThreadScope)
		//							f1threadsTable.addRow(e);
		//						else if (e instanceof F1AppProperty)
		//							f1PropertiesTable.addRow(e);
		//						else if (e instanceof F1AppPartition)
		//							f1PartitionTable.addRow(e);
		//					}
		//				}
		//			}
		//
		//			BasicTable itinTable = new BasicTable(new Object[] { "itineraryId", "type", "pendingRequestType" });
		//			for (VortexEyeItinerary<?> it : state.getActiveItineraries()) {
		//				if (it.getPendingRequests().size() == 0)
		//					itinTable.getRows().addRow(it.getItineraryId(), it.getClass().getSimpleName(), "<empty>");
		//				else
		//					for (RequestMessage<?> req : it.getPendingRequests())
		//						itinTable.getRows().addRow(it.getItineraryId(), it.getClass().getSimpleName(), req.getClass().getSimpleName());
		//			}
		//			itinTable.setTitle("itineraryRequest");
		//			bqf.addTable(itinTable);
		//			bqf.addTable(f1AppsTable);
		//			bqf.addTable(agentsTable);
		//		} finally {
		//			state.getPartition().unlockForRead();
		//		}
		//		return bqf.processQuery(sqlExpression, false);
		return sp.process(sqlExpression, new TopCalcFrameStack(bqf, EmptyCalcFrame.INSTANCE));
	}
	//	private <T extends Message> boolean addRows(BasicQueryFacade bqf, String tableName, Class<T> type, Iterable<T> rows, String sqlExpression, boolean force) {
	//		if (!bqf.getTableNames().contains(tableName))
	//			bqf.addTable(tableName, container.nw(type).askSchema());
	//		if (force || SH.startsWithIgnoreCase(sqlExpression, "show ", 0) || sqlExpression.indexOf(tableName) != -1) {
	//			bqf.addRows(tableName, rows);
	//			return true;
	//		}
	//		return false;
	//	}
}
