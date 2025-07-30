package com.vortex.agent;

import java.util.concurrent.TimeUnit;

import com.f1.base.Message;
import com.f1.base.Table;
import com.f1.container.Container;
import com.f1.utils.TableHelper;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;
import com.vortex.agent.state.VortexAgentState;

public class VortexAgentConsole {

	private Container container;

	public VortexAgentConsole(Container container) {
		this.container = container;
	}

	public Table query(String sqlExpression) {
		SqlProcessor sp = new SqlProcessor();
		Tableset bqf = new TablesetImpl();
		VortexAgentState state = container.getPartitionController().getState(VortexAgentSuite.PARTITIONID_VORTEX_AGENT, VortexAgentState.class);
		state.getPartition().lockForRead(1, TimeUnit.SECONDS);
		//TODO: populate tableset

		//		try {
		//			addRows(bqf, "deployments", VortexDeployment.class, state.getVortexDeployments(), sqlExpression, false);
		//			addRows(bqf, "auditTrailRules", VortexEyeAuditTrailRule.class, state.getAuditTrailRules(), sqlExpression, false);
		//			boolean forceFileSystems = false, forceProcesses = false, forceMachines = false, forceNetLinks = false, forceNetAddresses = false, forceConnections = false;
		//
		//			BasicTable ruleToApp = new BasicTable(new Object[] { "auditRuleId", "auditRuleName", "processUid", "appName" });
		//			ruleToApp.setTitle("AuditTrailRulesToApps");
		//			bqf.putTable(ruleToApp);
		//			for (VortexEyeAuditTrailRule rule : state.getAuditTrailRules()) {
		//				for (String processUid : CH.i(state.getAgentsForRule(rule.getId()))) {
		//					ruleToApp.getRows().addRow(rule.getId(), rule.getName(), processUid, state.getF1AppByProcessUidNoThrow(processUid).getF1AppInstance().getAppName());
		//				}
		//			}
		//
		//			BasicTable status = new BasicTable(new Object[] { "isConnectedToEye", "isSnapshotSentToEye", "currentSeqNum" });
		//			status.setTitle("status");
		//			status.getRows().addRow(state.getIsEyeConnected(), state.getIsSnapshotSentToEye(), state.currentSequenceNumber());
		//			bqf.putTable(status);
		//
		//			if (state.getMachine() != null)
		//				forceMachines = addRows(bqf, "machines", VortexAgentMachine.class, CH.l(state.getMachine()), sqlExpression, forceMachines);
		//			BasicTable f1AppsTable = new BasicTable(new Object[] { "processUid", "id", "seqNum", "appName", "mainClassName", "agentConnectTime", "agentDisconnectTime",
		//					"freeMemory", "maxMemory", "nowMs", "hostName", "javaHome", "javaVendor", "javaVersion" });
		//			f1AppsTable.setTitle("f1Apps");
		//
		//			bqf.putTable(f1AppsTable);
		//			QueryTable<Valued> filesystemsTable = bqf.putTable("fileSystems", container.nw(VortexAgentFileSystem.class).askSchema());
		//			QueryTable<Valued> processTable = bqf.putTable("processes", container.nw(VortexAgentProcess.class).askSchema());
		//
		//			BasicTable agentEntitiesTable = new BasicTable(new Object[] { "id", "mid", "now", "type" });
		//			agentEntitiesTable.setTitle("agentEntities");
		//			for (VortexAgentEntity e : state.getEntities()) {
		//				agentEntitiesTable.getRows().addRow(e.getId(), e.getMachineInstanceId(), e.getNow(), e.askSchema().askOriginalType().getSimpleName());
		//				if (e instanceof VortexAgentFileSystem)
		//					bqf.addRows("fileSystems", e);
		//				else if (e instanceof VortexAgentProcess)
		//					bqf.addRows("processes", e);
		//			}
		//			bqf.putTable(agentEntitiesTable);
		//
		//			//			QueryTable<Valued> f1loggersTable = bqf.putTable("f1Loggers", container.nw(F1AppLogger.class).askSchema());
		//			//			QueryTable<Valued> f1threadsTable = bqf.putTable("f1Threads", container.nw(F1AppThreadScope.class).askSchema());
		//			//			QueryTable<Valued> f1partitionsTable = bqf.putTable("f1Partitions", container.nw(F1AppPartition.class).askSchema());
		//			for (VortexAgentF1AppState f1app : state.getApps()) {
		//				F1AppInstance f1ss = f1app.getF1AppInstance();
		//				f1AppsTable.getRows().addRow(f1ss.getProcessUid(), f1ss.getId(), f1app.getCurrentSeqNum(), f1ss.getAppName(), f1ss.getMainClassName(), f1ss.getAgentConnectTime(),
		//						f1ss.getAgentDisconnectTime(), f1ss.getFreeMemory(), f1ss.getMaxMemory(), f1ss.getNowMs(), f1ss.getHostName(), f1ss.getJavaHome(), f1ss.getJavaVendor(),
		//						f1ss.getJavaVersion());
		//
		//				for (F1AppEntity e : f1app.getEntities().values()) {
		//					if (e instanceof F1AppLogger)
		//						f1loggersTable.addRow(e);
		//					else if (e instanceof F1AppThreadScope)
		//						f1threadsTable.addRow(e);
		//					else if (e instanceof F1AppPartition)
		//						f1partitionsTable.addRow(e);
		//				}
		//			}
		//		} finally {
		//			state.getPartition().unlockForRead();
		//		}
		return sp.process(sqlExpression, new TopCalcFrameStack(bqf, EmptyCalcFrame.INSTANCE));
	}
	private <T extends Message> boolean addRows(Tableset bqf, String tableName, Class<T> type, Iterable<T> rows, String sqlExpression, boolean force) {
		Table table = TableHelper.toTable(rows);
		table.setTitle(tableName);
		bqf.putTable(table);
		return false;
	}
}
