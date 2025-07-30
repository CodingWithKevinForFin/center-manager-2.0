package com.vortex.web.portlet.tables;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.base.Row;
import com.f1.bootstrap.F1Constants;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppLogger;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.suite.web.table.impl.WebTableFilteredSetTuple2Filter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebAppInstance;
import com.vortex.client.VortexClientF1AppState.AgentWebDatabase;
import com.vortex.client.VortexClientF1AppState.AgentWebDispatcher;
import com.vortex.client.VortexClientF1AppState.AgentWebLogger;
import com.vortex.client.VortexClientF1AppState.AgentWebMsgTopic;
import com.vortex.web.diff.DiffableApplication;
import com.vortex.web.diff.DiffableNode;
import com.vortex.web.messages.VortexF1AppIdInterPortletMessage;
import com.vortex.web.messages.VortexMachineIdInterPortletMessage;
import com.vortex.web.messages.VortexPidInterPortletMessage;
import com.vortex.web.messages.VortexProcedureIdInterPortletMessage;
import com.vortex.web.portlet.trees.VortexWebDiffTreePortlet;

public class VortexWebF1AppInstanceTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebAppInstance> implements WebContextMenuFactory {

	final private BasicPortletSocket sendF1appSocket;
	final private BasicPortletSocket sendPidSocket;
	private BasicPortletSocket pidSocket;
	private BasicPortletSocket sendMiidSocket;
	private BasicPortletSocket miidSocket;
	private Object procedureIdSocket;
	public VortexWebF1AppInstanceTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebAppInstance.class);
		String[] ids = { HOST, "javaHome", "javaVendor", "javaVersion", "mainClass", PID, PUID, "pwd", "startTimeMs", USER, "freeMemory", "clockNowMs", "nowMs", "timeSpentMs",
				"totalJavaThreadCount", "runningJavaThreadCount", "eventsDispatched", "eventsProcessed", "partitionsCount", "loggedEvents", "loggedBytes", "loggedWarnings",
				"loggedErrors", "loggedIgnored", "loggedExceptions", "mainFullClass", "sqlCalls", "databases", "totalMem", "maxMem", "usedMem", "debug", "connectionsCount",
				"messagesOut", "messagesIn", "bytesOut", "bytesIn", APPNAME, "apid", MUID, MIID };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Applications");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host Name", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Pid", PID, service.getBasicFormatter()).setWidth(50);
		table.addColumn(true, "Owner", USER, service.getBasicFormatter()).setWidth(90);
		table.addColumn(true, "Debug", "debug", service.getBasicFormatter()).setWidth(40);
		table.addColumn(true, "Main Classname", "mainClass", service.getBasicFormatter()).setWidth(150).addCssClass("bold");

		table.addColumn(true, "Started", "startTimeMs", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Updated", "nowMs", service.getTimeWebCellFormatter());

		table.addColumn(true, "Max Memory", "maxMem", service.getMemoryFormatter());
		table.addColumn(true, "Used Memory", "usedMem", service.getMemoryFormatter());

		table.addColumn(true, "Threads", "totalJavaThreadCount", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Active", "runningJavaThreadCount", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "F1 Partitions", "partitionsCount", service.getNumberFormatter()).setWidth(80);
		table.addColumn(true, "F1 Events", "eventsProcessed", service.getNumberFormatter()).setWidth(90).addCssClass("blue");
		table.addColumn(true, "Logs", "loggedEvents", service.getNumberFormatter()).setWidth(80).addCssClass("blue");
		table.addColumn(true, "Log Size", "loggedBytes", service.getMemoryFormatter());
		table.addColumn(true, "Warnings Logged", "loggedWarnings", service.getWarningNumberFormatter()).setWidth(70).addCssClass("red");
		table.addColumn(true, "Errors Logged", "loggedErrors", service.getWarningNumberFormatter()).setWidth(70).addCssClass("red");
		table.addColumn(true, "Exceptions Logged", "loggedExceptions", service.getWarningNumberFormatter()).setWidth(70).addCssClass("red");
		table.addColumn(true, "Sql Calls", "sqlCalls", service.getNumberFormatter()).setWidth(50);
		table.addColumn(true, "Out Msgs", "messagesOut", service.getNumberFormatter()).setWidth(50);
		table.addColumn(true, "In Msgs", "messagesIn", service.getNumberFormatter()).setWidth(50);
		table.addColumn(true, "Msg Connections", "connectionsCount", service.getNumberFormatter()).setWidth(40);
		table.addColumn(true, "App Name", APPNAME, service.getBasicFormatter()).addCssClass("bold");

		table.addColumn(false, "F1 Dispatched", "eventsDispatched", service.getNumberFormatter()).setWidth(90).addCssClass("blue");
		table.addColumn(false, "Java Home", "javaHome", service.getBasicFormatter()).setWidth(50);
		table.addColumn(false, "Java Vendor", "javaVendor", service.getBasicFormatter()).setWidth(50);
		table.addColumn(false, "Java Version", "javaVersion", service.getBasicFormatter());
		table.addColumn(false, "Process Uid", PUID, service.getBasicFormatter());
		table.addColumn(false, "Working Dir", "pwd", service.getBasicFormatter());
		table.addColumn(false, "Clock Time", "clockNowMs", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Monitor Cost", "timeSpentMs", service.getNumberFormatter());
		table.addColumn(false, "Log Events Dropped", "loggedIgnored", service.getNumberFormatter()).setWidth(100);
		table.addColumn(false, "Main Full Classname", "mainFullClass", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Free Memory", "freeMemory", service.getMemoryFormatter());
		table.addColumn(false, "Alloc. Memory", "totalMem", service.getMemoryFormatter());
		table.addColumn(false, "Out Msg bytes", "bytesOut", service.getMemoryFormatter());
		table.addColumn(false, "Machine UID", MUID, service.getBasicFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getBasicFormatter());
		table.addColumn(false, "In Msg bytes", "bytesIn", service.getMemoryFormatter());
		table.addColumn(false, "Id", "apid", service.getIdFormatter("AP-"));
		this.sendMiidSocket = addSocket(true, "sendMiid", "Send Machine ID", true, CH.s(VortexMachineIdInterPortletMessage.class), null);
		this.sendF1appSocket = addSocket(true, "sendf1appid", "F1 Application", true, CH.s(VortexF1AppIdInterPortletMessage.class), null);
		this.sendPidSocket = addSocket(true, "sendPid", "Pid", true, CH.s(VortexPidInterPortletMessage.class), null);
		this.pidSocket = addSocket(false, "pid", "Pid", false, null, CH.s(VortexPidInterPortletMessage.class));
		this.miidSocket = addSocket(false, "miid", "Machine ID", true, null, CH.s(VortexMachineIdInterPortletMessage.class));
		this.procedureIdSocket = addSocket(false, "procedureId", "build Procedure ID", true, null, CH.s(VortexProcedureIdInterPortletMessage.class));
		setTable(table);
		table.setMenuFactory(this);

	}

	@Override
	public void onF1AppAdded(VortexClientF1AppState appState) {
		onF1AppEntityAdded(appState.getSnapshotState());
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppInstanceTablePortlet> {
		public static final String ID = "AgentF1AppTablePortlet";

		public Builder() {
			super(VortexWebF1AppInstanceTablePortlet.class);
		}

		@Override
		public VortexWebF1AppInstanceTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppInstanceTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Applications";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}
	}

	@Override
	public void onF1AppRemoved(VortexClientF1AppState existing) {
		onF1AppEntityRemoved(existing.getSnapshotState());
	}

	@Override
	protected Row createAndAddRow(AgentWebAppInstance node) {
		VortexClientF1AppState appState = node.getAppState();
		F1AppInstance ss = appState.getSnapshot();
		final long totalMem = ss.getTotalMemory();
		final long maxMem = ss.getMaxMemory();
		final long freeMem = ss.getFreeMemory();
		final long usedMem = totalMem - freeMem;
		final String host = ss.getHostName();
		final String javaHome = ss.getJavaHome();
		final String javaVendor = ss.getJavaVendor();
		final String javaVersion = ss.getJavaVersion();
		final String mainFullClass = ss.getMainClassName();
		final String mainClass = SH.afterLast(mainFullClass, ".");
		final String pid = ss.getPid();
		final String processUid = ss.getProcessUid();
		final String pwd = ss.getPwd();
		final long startTimeMs = ss.getStartTimeMs();
		final String user = ss.getUserName();
		final long clockNowMs = ss.getClockNowMs();
		final long nowMs = ss.getNowMs();
		final long timeSpentMs = ss.getMonitorTimeSpentMs();
		long totalJavaThreadCount = ss.getThreadsBlockedCount() + ss.getThreadsNewCount() + ss.getThreadsRunnableCount() + ss.getThreadsTerminatedCount()
				+ ss.getThreadsTimedWaitingCount() + ss.getThreadsWaitingCount();
		long runningJavaThreadCount = ss.getThreadsRunnableCount();
		long loggedExceptions = 0;
		long loggedBytes = 0;
		long loggedEvents = 0;
		long loggedWarnings = 0;
		long loggedErrors = 0;
		long loggedIgnored = 0;
		for (AgentWebLogger sink : appState.getLoggersById().values()) {
			F1AppLogger as = sink.getObject();
			loggedExceptions += as.getExceptionsCount();
			loggedWarnings += as.getWarningOrHigherCount();
			loggedErrors += as.getErrorOrHigherCount();
			loggedEvents += as.getTotalEventsCount();
			loggedBytes += as.getBytesLoggedCount();
			loggedIgnored += as.getDroppedCount();
		}
		long eventsDispatched = 0;
		long eventsProcessed = 0;
		for (AgentWebDispatcher i : appState.getDispatchersByContainerScopeId().values()) {
			eventsProcessed += i.getObject().getProcessStats();
		}
		long databases = appState.getDatabases().size();
		long sqlCalls = 0;
		for (AgentWebDatabase i : appState.getDatabases().values()) {
			sqlCalls += i.getObject().getSqlSentCount();
		}
		long partitionsCount = appState.getPartitionsByContainerScopeId().size();
		boolean debug = ss.getIsDebug();
		long connectionsCount = 0;
		long messagesOut = 0;
		long messagesIn = 0;
		long bytesOut = 0;
		long bytesIn = 0;
		for (AgentWebMsgTopic i : appState.getTopics().values()) {
			connectionsCount += i.getObject().getConnectionsCount();
			messagesOut += i.getObject().getMessagesSentCount();
			messagesIn += i.getObject().getMessagesReceivedCount();
			bytesOut += i.getObject().getBytesSent();
			bytesIn += i.getObject().getBytesReceived();
		}
		String appName = ss.getAppName();
		return addRow(host, javaHome, javaVendor, javaVersion, mainClass, pid, processUid, pwd, startTimeMs, user, freeMem, clockNowMs, nowMs, timeSpentMs, totalJavaThreadCount,
				runningJavaThreadCount, eventsDispatched, eventsProcessed, partitionsCount, loggedEvents, loggedBytes, loggedWarnings, loggedErrors, loggedIgnored,
				loggedExceptions, mainFullClass, sqlCalls, databases, totalMem, maxMem, usedMem, debug, connectionsCount, messagesOut, messagesIn, bytesOut, bytesIn, appName,
				ss.getId(), ss.getAgentMachineUid(), appState.getMachineInstanceId());
	}

	@Override
	protected void updateRow(Row row, AgentWebAppInstance node) {
		VortexClientF1AppState appState = node.getAppState();
		F1AppInstance ss = appState.getSnapshot();
		final long totalMem = ss.getTotalMemory();
		final long maxMem = ss.getMaxMemory();
		final long freeMem = ss.getFreeMemory();
		final long usedMem = totalMem - freeMem;
		final String mainFullClass = ss.getMainClassName();
		final long clockNowMs = ss.getClockNowMs();
		final long nowMs = ss.getNowMs();
		final long timeSpentMs = ss.getMonitorTimeSpentMs();
		long totalJavaThreadCount = ss.getThreadsBlockedCount() + ss.getThreadsNewCount() + ss.getThreadsRunnableCount() + ss.getThreadsTerminatedCount()
				+ ss.getThreadsTimedWaitingCount() + ss.getThreadsWaitingCount();
		long runningJavaThreadCount = ss.getThreadsRunnableCount();
		long loggedExceptions = 0;
		long loggedBytes = 0;
		long loggedEvents = 0;
		long loggedWarnings = 0;
		long loggedErrors = 0;
		long loggedIgnored = 0;
		for (AgentWebLogger sink : appState.getLoggersById().values()) {
			F1AppLogger as = sink.getObject();
			loggedExceptions += as.getExceptionsCount();
			loggedWarnings += as.getWarningOrHigherCount();
			loggedErrors += as.getErrorOrHigherCount();
			loggedEvents += as.getTotalEventsCount();
			loggedBytes += as.getBytesLoggedCount();
			loggedIgnored += as.getDroppedCount();
		}
		long eventsDispatched = 0;
		long eventsProcessed = 0;
		for (AgentWebDispatcher i : appState.getDispatchersByContainerScopeId().values()) {
			eventsProcessed += i.getObject().getProcessStats();
		}
		long databases = appState.getDatabases().size();
		long sqlCalls = 0;
		for (AgentWebDatabase i : appState.getDatabases().values()) {
			sqlCalls += i.getObject().getSqlSentCount();
		}
		long partitionsCount = appState.getPartitionsByContainerScopeId().size();
		long connectionsCount = 0;
		long messagesOut = 0;
		long messagesIn = 0;
		long bytesOut = 0;
		long bytesIn = 0;
		for (AgentWebMsgTopic i : appState.getTopics().values()) {
			connectionsCount += i.getObject().getConnectionsCount();
			messagesOut += i.getObject().getMessagesSentCount();
			messagesIn += i.getObject().getMessagesReceivedCount();
			bytesOut += i.getObject().getBytesSent();
			bytesIn += i.getObject().getBytesReceived();
		}
		row.put("freeMemory", freeMem);
		row.put("clockNowMs", clockNowMs);
		row.put("nowMs", nowMs);
		row.put("timeSpentMs", timeSpentMs);
		row.put("totalJavaThreadCount", totalJavaThreadCount);
		row.put("runningJavaThreadCount", runningJavaThreadCount);
		row.put("eventsDispatched", eventsDispatched);
		row.put("eventsProcessed", eventsProcessed);
		row.put("partitionsCount", partitionsCount);
		row.put("loggedEvents", loggedEvents);
		row.put("loggedBytes", loggedBytes);
		row.put("loggedWarnings", loggedWarnings);
		row.put("loggedErrors", loggedErrors);
		row.put("loggedIgnored", loggedIgnored);
		row.put("loggedExceptions", loggedExceptions);
		row.put("mainFullClass", mainFullClass);
		row.put("sqlCalls", sqlCalls);
		row.put("databases", databases);
		row.put("totalMem", totalMem);
		row.put("maxMem", maxMem);
		row.put("usedMem", usedMem);
		row.put("connectionsCount", connectionsCount);
		row.put("messagesOut", messagesOut);
		row.put("messagesIn", messagesIn);
		row.put("bytesOut", bytesOut);
		row.put("bytesIn", bytesIn);
	}

	@Override
	protected Iterable<AgentWebAppInstance> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return CH.l(f1AppState.getSnapshotState());
	}

	@Override
	protected void onVortexRowsChanged() {
		if (!getIsEyeConnected())
			return;
		if (!sendF1appSocket.hasConnections() && !sendPidSocket.hasConnections() && !sendMiidSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		if (sendMiidSocket.hasConnections()) {
			List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(MIID));
			sendMiidSocket.sendMessage(new VortexMachineIdInterPortletMessage(selections));
		}
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendF1appSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get("apid"));
			sendF1appSocket.sendMessage(new VortexF1AppIdInterPortletMessage(selections));
		}
		if (sendPidSocket.hasConnections()) {
			Set<Tuple2<String, String>> selections = new HashSet<Tuple2<String, String>>();
			for (Row addRow : sel)
				selections.add(new Tuple2<String, String>((String) addRow.get(HOST), (String) addRow.get(PID)));
			sendPidSocket.sendMessage(new VortexPidInterPortletMessage(selections));
		}
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == pidSocket) {
			VortexPidInterPortletMessage msg = (VortexPidInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetTuple2Filter(getTable().getColumn(HOST), getTable().getColumn(PID), (Set) msg.getHostAndPids()));
			onVortexRowsChanged();
		} else if (localSocket == miidSocket) {
			VortexMachineIdInterPortletMessage msg = (VortexMachineIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(MIID), msg.getMiids()));
			onVortexRowsChanged();
		} else if (localSocket == procedureIdSocket) {
			VortexProcedureIdInterPortletMessage msg = (VortexProcedureIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(MIID), msg.getProcedureIds()));
			onVortexRowsChanged();
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	@Override
	public WebMenu createMenu(WebTable table) {
		if (table.getSelectedRows().size() == 2)
			return new BasicWebMenu(new BasicWebMenuLink("Compare", true, "diff"));
		return null;
	}
	private static final Set<String> DIFF_IGNORE_LIST = CH.s(F1Constants.PROPERTY_DEPLOYMENT_DESCRIPTION, F1Constants.PROPERTY_DEPLOYMENT_ID,
			F1Constants.PROPERTY_DEPLOYMENT_INSTANCE_ID, F1Constants.PROPERTY_DEPLOYMENT_INVOKED_BY, F1Constants.PROPERTY_DEPLOYMENT_SETID,
			F1Constants.PROPERTY_DEPLOYMENT_TIMESTAMP, F1Constants.PROPERTY_USERHOME, F1Constants.PROPERTY_USERNAME, F1Constants.PROPERTY_LOCALHOST);
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("diff".equals(action)) {
			List<Row> rows = table.getSelectedRows();
			if (rows.size() != 2) {
				getManager().showAlert("Select two applications for comparison");
				return;
			}
			VortexClientF1AppState app1 = service.getAgentManager().getJavaAppState(rows.get(0).get("apid", Long.class));
			VortexClientF1AppState app2 = service.getAgentManager().getJavaAppState(rows.get(1).get("apid", Long.class));
			DiffableNode d1 = new DiffableApplication(app1, DIFF_IGNORE_LIST);
			DiffableNode d2 = new DiffableApplication(app2, DIFF_IGNORE_LIST);
			VortexWebDiffTreePortlet diffTree = new VortexWebDiffTreePortlet(generateConfig(), describe(app1), d1, describe(app2), d2);
			getManager().showDialog("Diff", diffTree);
		} else
			super.onContextMenu(table, action);
	}

	private String describe(VortexClientF1AppState app) {
		return app.getSnapshot().getHostName() + " - " + app.getSnapshot().getMainClassName();
	}

}
