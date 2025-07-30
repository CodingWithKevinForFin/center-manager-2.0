package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
import com.f1.povo.f1app.F1AppInstance;
import com.f1.povo.f1app.F1AppPartition;
import com.f1.povo.f1app.inspect.F1AppInspectionEntity;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionRequest;
import com.f1.povo.f1app.reqres.F1AppInspectPartitionResponse;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyePassToF1AppResponse;
import com.vortex.client.VortexClientF1AppState;
import com.vortex.client.VortexClientF1AppState.AgentWebPartition;
import com.vortex.web.portlet.trees.VortexWebInspectionTreePortlet;

public class VortexWebF1AppPartitionsTablePortlet extends VortexWebF1AppAbstractTablePortlet<VortexClientF1AppState.AgentWebPartition> implements WebContextMenuFactory {

	public VortexWebF1AppPartitionsTablePortlet(PortletConfig config) {
		super(config, null, VortexClientF1AppState.AgentWebPartition.class);
		String[] ids = { HOST, USER, MAIN, "name", "tpk", "class", "proc", "exceptions", "pop", "push", "queue", "started", "id", APPNAME, "data", "apid" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("F1 Partitions");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Host", HOST, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "User", USER, service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "Main", MAIN, service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "Partition Id", "name", service.getBasicFormatter()).setWidth(200).setWidth(100).setCssColumn("bold");
		table.addColumn(true, "Queued", "queue", service.getNumberFormatter()).setWidth(70).addCssClass("blue");
		table.addColumn(true, "Processed", "proc", service.getNumberFormatter()).setWidth(70);
		table.addColumn(true, "Exceptions", "exceptions", service.getWarningNumberFormatter()).setWidth(70);
		table.addColumn(true, "App Name", APPNAME, service.getBasicFormatter()).addCssClass("bold");
		table.addColumn(false, "ThreadPool Key", "tpk", service.getBasicFormatter());
		table.addColumn(false, "Class", "class", service.getBasicFormatter()).setWidth(250);
		table.addColumn(false, "Pop", "pop", service.getNumberFormatter()).setWidth(70);
		table.addColumn(false, "Push", "push", service.getNumberFormatter()).setWidth(70);
		table.addColumn(false, "Created", "started", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Id", "id", service.getIdFormatter("APPA-")).setWidth(70);
		table.addColumn(false, "AppId", "apid", service.getIdFormatter("AP-"));
		table.setMenuFactory(this);
		setTable(table);
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebF1AppPartitionsTablePortlet> {

		public static final String ID = "AgentF1PartitionsTablePortlet";

		public Builder() {
			super(VortexWebF1AppPartitionsTablePortlet.class);
		}

		@Override
		public VortexWebF1AppPartitionsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebF1AppPartitionsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "F1 Partitions Table";
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
			children.add(new BasicWebMenuLink("Analyze Partition", true, "analyze"));
			BasicWebMenu r = new BasicWebMenu("", true, children);
			return r;
		}
		return null;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = ((VortexEyePassToF1AppResponse) result.getAction()).getF1AppResponse();

		if (action instanceof F1AppInspectPartitionResponse) {
			F1AppInspectPartitionResponse response = (F1AppInspectPartitionResponse) action;
			SmartTable st = new BasicSmartTable(new BasicTable(new Object[] { "class", "count" }));
			FastWebTable classesTable = new FastWebTable(st, getManager().getTextFormatter());
			classesTable.addColumn(true, "Class", "class", service.getClassNameFormatter()).setWidth(300);
			classesTable.addColumn(true, "Count", "count", service.getNumberFormatter()).setWidth(100);
			FastTablePortlet ftp = new FastTablePortlet(generateConfig(), classesTable);
			classesTable.sortRows("count", false, false, false);
			ftp.setSize(400, 400);
			for (Entry<String, Long> e : response.getInstances().entrySet()) {
				ftp.addRow(e.getKey(), e.getValue());
			}
			IntKeyMap<F1AppInspectionEntity> inspectionEntities = new IntKeyMap<F1AppInspectionEntity>();
			for (F1AppInspectionEntity i : response.getInspectionEntities())
				inspectionEntities.put(i.getId(), i);
			Portlet overView = new HtmlPortlet(generateConfig(), "<PRE>Total Size: " + SH.formatMemory(response.getPartitionSize()) + "</PRE>");
			TabPortlet tabs = new TabPortlet(generateConfig());
			tabs.addChild("Classes", ftp);
			tabs.addChild("Overview", overView);
			tabs.addChild("Overview", new VortexWebInspectionTreePortlet(generateConfig(), inspectionEntities));
			getManager().showDialog("Partition Classes count", tabs);
		}
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("analyze".equals(action)) {
			List<Row> sel = table.getSelectedRows();
			if (sel.size() == 1) {
				AgentWebPartition ts = sel.get(0).get("data", AgentWebPartition.class);
				F1AppInspectPartitionRequest req = nw(F1AppInspectPartitionRequest.class);
				req.setPartitionId(ts.getId());
				req.setTargetF1AppProcessUid(ts.getSnapshot().getProcessUid());
				req.setTimeoutMs(5000);
				//req.setF1AppInstanceId(ts.getAppState().getSnapshot().getF1AppInstanceId());
				req.setInvokedBy(getManager().getState().getWebState().getUser().getUserName());
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
	protected Row createAndAddRow(AgentWebPartition node) {
		F1AppPartition o = node.getObject();
		long processStats = o.getProcessStats();
		long thrownStats = o.getThrownStats();
		long queuePopStats = o.getQueuePopStats();
		long queuePushStats = o.getQueuePushStats();
		long queueSize = queuePushStats - queuePopStats;
		long startedMs = o.getStartedMs();
		F1AppInstance snapshot = node.getSnapshot();
		String host = snapshot.getHostName();
		String user = snapshot.getUserName();
		String appName = snapshot.getAppName();
		String mainClassName = SH.afterLast(snapshot.getMainClassName(), '.');
		String name = OH.toString(o.getPartitionId());
		String threadPoolKey = SH.toString(o.getThreadPoolKey());
		String className = node.getClassName();
		Long id = node.getId();
		return addRow(host, user, mainClassName, name, threadPoolKey, className, processStats, thrownStats, queuePopStats, queuePushStats, queueSize, startedMs, id, appName, node,
				o.getF1AppInstanceId());
	}

	@Override
	protected void updateRow(Row row, AgentWebPartition node) {
		F1AppPartition o = node.getObject();
		long processStats = o.getProcessStats();
		long thrownStats = o.getThrownStats();
		long queuePopStats = o.getQueuePopStats();
		long queuePushStats = o.getQueuePushStats();
		long queueSize = queuePushStats - queuePopStats;
		row.put("proc", processStats);
		row.put("exceptions", thrownStats);
		row.put("pop", queuePopStats);
		row.put("push", queuePushStats);
		row.put("queue", queueSize);

	}

	@Override
	protected Iterable<AgentWebPartition> getEntitiesForSnapshot(VortexClientF1AppState f1AppState) {
		return f1AppState.getPartitionsByContainerScopeId().values();
	}
}
