package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppThreadScope;
import com.f1.povo.f1app.reqres.F1AppInterruptThreadRequest;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebPartition;
import com.vortex.client.VortexClientF1AppState.AgentWebProcessor;
import com.vortex.client.VortexClientF1AppState.AgentWebThreadScope;

public class VortexWebF1AppThreadsTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebThreadScope> implements WebContextMenuFactory {

	public VortexWebF1AppThreadsTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebThreadScope.class);
		String[] ids = { HOST, USER, MAIN, "name", "tpk", "class", "proc", "exceptions", START, "partition", "processor", "active", APPNAME, "data", "id", "apid" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Threads");
		SmartTable st = new BasicSmartTable(inner);

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host", HOST, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "User", USER, service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Main", MAIN, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "ThreadScope Id", "name", service.getBasicFormatter()).setWidth(200).setWidth(100).setCssColumn("bold");
		table.addColumn(true, "Status", "active", service.getBasicFormatter()).setWidth(60).setCssColumn("green");
		table.addColumn(true, "Processed", "proc", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Exceptions", "exceptions", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(true, "Partition", "partition", service.getBasicFormatter()).setWidth(150).addCssClass("blue");
		table.addColumn(true, "Processor", "processor", service.getBasicFormatter()).setWidth(150).addCssClass("blue");
		table.addColumn(false, "ThreadPool Key", "tpk", service.getBasicFormatter());
		table.addColumn(false, "Class", "class", service.getBasicFormatter()).setWidth(250);
		table.addColumn(true, "App Name", APPNAME, service.getBasicFormatter()).addCssClass("bold");
		table.addColumn(false, "Id", "id", service.getIdFormatter("APTH-"));
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));
		setTable(table);
		table.setMenuFactory(this);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppThreadsTablePortlet> {

		public static final String ID = "AgentF1ThreadScopesTablePortlet";

		public Builder() {
			super(VortexWebF1AppThreadsTablePortlet.class);
		}

		@Override
		public VortexWebF1AppThreadsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppThreadsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 ThreadScopes Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		if (table.getSelectedRows().size() == 1) {
			List<WebMenuItem> children = new ArrayList<WebMenuItem>();
			children.add(new BasicWebMenuLink("Interrupt Thread", true, "interrupt"));
			BasicWebMenu r = new BasicWebMenu("", true, children);
			return r;
		}
		return null;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("interrupt".equals(action)) {
			List<Row> sel = table.getSelectedRows();
			if (sel.size() == 1) {
				AgentWebThreadScope ts = sel.get(0).get("data", AgentWebThreadScope.class);
				ts.getId();
				F1AppInterruptThreadRequest req = nw(F1AppInterruptThreadRequest.class);
				req.setThreadMonitorId(ts.getId());
				req.setTargetF1AppProcessUid(ts.getAppState().getSnapshot().getProcessUid());
				req.setProcessedEventsCount(ts.getObject().getProcessStats());
				req.setProcessorMonitorId(ts.getObject().getCurrentProcessorId());
				VortexEyePassToF1AppRequest wrapper = nw(VortexEyePassToF1AppRequest.class);
				wrapper.setF1AppId(ts.getAppState().getSnapshot().getId());
				wrapper.setF1AppRequest(req);
				wrapper.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
				service.sendRequestToBackend(getPortletId(), wrapper);
			}
		} else
			super.onContextMenu(table, action);
	}
	@Override
	protected Row createAndAddRow(AgentWebThreadScope node) {
		F1AppThreadScope o = node.getObject();
		F1AppInstance snapshot = node.getSnapshot();
		long processStats = o.getProcessStats();
		long thrownStats = o.getThrownStats();
		long startedMs = o.getStartedMs();
		AgentWebProcessor processor = node.getCurrentProcessor();
		AgentWebPartition partition = node.getCurrentPartition();
		String processorName = "";
		String partitionName = "";
		String activeText = "";
		boolean active = o.getCurrentProcessorId() != -1 || o.getCurrentPartitionId() != -1;
		if (active) {
			processorName = processor == null ? SH.toString(o.getCurrentProcessorId()) : processor.getObject().getName();
			partitionName = partition == null ? SH.toString(o.getCurrentPartitionId()) : partition.getPartitionId();
			activeText = "ACTIVE";
		}
		String host = snapshot.getHostName();
		String user = snapshot.getUserName();
		String appName = snapshot.getAppName();
		String mainClassName = SH.afterLast(snapshot.getMainClassName(), '.');
		String name = o.getThreadName();
		String threadPoolKey = SH.toString(o.getThreadPoolKey());
		String className = node.getClassName();
		return addRow(host, user, mainClassName, name, threadPoolKey, className, processStats, thrownStats, startedMs, partitionName, processorName, activeText, appName, node,
				o.getId(), o.getF1AppInstanceId());
	}
	@Override
	protected void updateRow(Row row, AgentWebThreadScope node) {
		F1AppThreadScope o = node.getObject();
		long processStats = o.getProcessStats();
		long thrownStats = o.getThrownStats();
		AgentWebProcessor processor = node.getCurrentProcessor();
		AgentWebPartition partition = node.getCurrentPartition();
		String processorName = "";
		String partitionName = "";
		String activeText = "";
		boolean active = o.getCurrentProcessorId() != -1 || o.getCurrentPartitionId() != -1;
		if (active) {
			processorName = processor == null ? SH.toString(o.getCurrentProcessorId()) : processor.getObject().getName();
			partitionName = partition == null ? SH.toString(o.getCurrentPartitionId()) : partition.getPartitionId();
			activeText = "ACTIVE";
		}
		row.put("proc", processStats);
		row.put("exceptions", thrownStats);
		row.put("partition", partitionName);
		row.put("active", activeText);
		row.put("processor", processorName);
	}
	@Override
	protected Iterable<AgentWebThreadScope> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getThreadScopesByContainerScopeId().values();
	}
}
