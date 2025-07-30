package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetTuple2Filter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDbServerRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunDbInspectionRequest;
import com.vortex.client.VortexClientDbServer;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientNetConnection;
import com.vortex.web.messages.VortexDbInterPortletMessage;
import com.vortex.web.messages.VortexPidInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebAddDbServerFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;

public class VortexWebDatabaseServersTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbServer, VortexClientDbServer> {

	final private BasicPortletSocket sendDbIdSocket;
	private BasicPortletSocket pidSocket;
	public VortexWebDatabaseServersTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_SERVER);
		BasicTable inner = new BasicTable(new Object[] { DSID, "url", "desc", "type", MUID, "stat", "msg", "invokedBy", "itime", HOST, "sport", "pid", "mdata" });
		inner.setTitle("Database Servers");
		SmartTable st = new BasicSmartTable(inner);
		MapWebCellFormatter<Byte> statusFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_CREATED, "Never Inspected", "_cna=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Never Inspected");
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_OKAY, "Okay", "_cna=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp;Okay");
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_INSPECTING, "Inspecting", "_cna=portlet_icon_process", "&nbsp;&nbsp;&nbsp;&nbsp;Inspecting");
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_MODIFIED, "Modified", "_cna=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Modified");
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_GENERAL_ERROR, "General Error", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;General Error");
		statusFormatter.addEntry(VortexAgentDbServer.STATUS_CONNECTION_ERROR, "Could not connect", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Could not connect");
		statusFormatter.setDefaultWidth(100).lockFormatter();

		MapWebCellFormatter<Byte> typeFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		typeFormatter.addEntry(VortexAgentDbServer.TYPE_MYSQL, "Mysql");
		typeFormatter.addEntry(VortexAgentDbServer.TYPE_ORACLE, "Okay");
		typeFormatter.addEntry(VortexAgentDbServer.TYPE_SQLSERVER, "Sql Server");
		typeFormatter.addEntry(VortexAgentDbServer.TYPE_SYBASE, "Sybase");
		typeFormatter.setDefaultWidth(50).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(true, "Server Port", "sport", service.getBasicFormatter()).setWidth(80);
		table.addColumn(true, "url", "url", service.getBasicFormatter());
		table.addColumn(true, "description", "desc", service.getBasicFormatter());
		table.addColumn(true, "type", "type", typeFormatter);
		table.addColumn(true, "Machine Id", MUID, service.getBasicFormatter());
		table.addColumn(true, "Status", "stat", statusFormatter);
		table.addColumn(true, "Message", "msg", service.getBasicFormatter());
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Pid", "pid", service.getBasicFormatter()).setWidth(40);
		table.addColumn(true, "Inspected on", "itime", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Invoked By", "invokedBy", service.getBasicFormatter());

		this.pidSocket = addSocket(false, "pid", "Pid", false, null, CH.s(VortexPidInterPortletMessage.class));
		this.sendDbIdSocket = addSocket(true, "sendDbId", "Send Dataserver ID", true, CH.s(VortexDbInterPortletMessage.class), null);
		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_DB_SERVER, "mdata");
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			onMachineEntityAdded(dbserver);
	}
	@Override
	public BasicWebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		List<Row> selected = table.getSelectedRows();
		if (selected.size() > 0) {
			children.add(new BasicWebMenuLink("Inspect Database Server for changes", true, "inspect"));
			children.add(new BasicWebMenuLink("Edit Database Server", true, "edit"));
			children.add(new BasicWebMenuLink("copy Database Server", true, "copy"));
			children.add(new BasicWebMenuLink("Delete Database Server", true, "delete"));
		}
		children.add(new BasicWebMenuLink("Add new Data Server", true, "add"));
		BasicWebMenu r = new BasicWebMenu("", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selected = table.getSelectedRows();
		if ("edit".equals(action)) {
			if (selected.size() == 1) {
				VortexClientDbServer dbserver = service.getAgentManager().getDbServer(selected.get(0).get(DSID, Long.class));
				VortexWebAddDbServerFormPortlet form = new VortexWebAddDbServerFormPortlet(generateConfig());
				form.setDbServerToEdit(dbserver);
				getManager().showDialog("Edit db server", form);
				return;
			}
			getManager().showAlert("Must select 1 data server");
		} else if ("inspect".equals(action)) {
			for (Row select : selected) {
				VortexClientDbServer dbserver = service.getAgentManager().getDbServer(select.get(DSID, Long.class));
				VortexEyeRunDbInspectionRequest req = nw(VortexEyeRunDbInspectionRequest.class);
				req.setDbServerId(dbserver.getId());
				service.sendRequestToBackend(getPortletId(), req);
			}
		} else if ("copy".equals(action)) {
			if (selected.size() == 1) {
				VortexClientDbServer dbserver = service.getAgentManager().getDbServer(selected.get(0).get(DSID, Long.class));
				VortexWebAddDbServerFormPortlet form = new VortexWebAddDbServerFormPortlet(generateConfig());
				form.setDbServerToCopy(dbserver);
				getManager().showDialog("Add db server", form);
				return;
			}
			getManager().showAlert("Must select 1 data server");
		} else if ("add".equals(action)) {
			VortexWebAddDbServerFormPortlet form = new VortexWebAddDbServerFormPortlet(generateConfig());
			getManager().showDialog("Add db server", form);
			return;
		} else if ("delete".equals(action)) {
			for (Row select : selected) {
				VortexClientDbServer dbserver = service.getAgentManager().getDbServer(select.get(DSID, Long.class));
				VortexEyeManageDbServerRequest request = nw(VortexEyeManageDbServerRequest.class);
				VortexAgentDbServer dbserver2 = nw(VortexAgentDbServer.class);
				dbserver2.setId(dbserver.getId());
				dbserver2.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setDbServer(dbserver2);
				getManager().showDialog("Delete dataserver",
						new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), request, "Delete Dataserver", "db.jpg").setIconToDelete());
			}
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabaseServersTablePortlet> {
		public static final String ID = "databaseServersTablePortlet";

		public Builder() {
			super(VortexWebDatabaseServersTablePortlet.class);
		}

		@Override
		public VortexWebDatabaseServersTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabaseServersTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Database Servers";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
	}

	@Override
	protected Row createAndAddRow(VortexClientDbServer node) {
		VortexAgentDbServer a = node.getData();
		VortexClientNetConnection connection = node.getMachine() == null ? null : node.getMachine().getNetConnectionByServerPort(a.getServerPort());
		String pid = connection == null ? null : SH.toString(connection.getLocalPid());
		return addRow(a.getId(), a.getUrl(), a.getDescription(), a.getDbType(), a.getMachineUid(), a.getStatus(), a.getMessage(), a.getInvokedBy(), a.getInspectedTime(),
				node.getHostName(), a.getServerPort(), pid, a.getMetadata());
	}
	@Override
	protected void updateRow(Row row, VortexClientDbServer node) {
		VortexAgentDbServer a = node.getData();
		VortexClientNetConnection connection = node.getMachine().getNetConnectionByServerPort(a.getServerPort());
		String pid = connection == null ? null : SH.toString(connection.getLocalPid());
		row.put("msg", a.getMessage());
		row.put("invokedBy", a.getInvokedBy());
		row.put("url", a.getUrl());
		row.put("desc", a.getDescription());
		row.put("type", a.getDbType());
		row.put("muid", a.getMachineUid());
		row.put("stat", a.getStatus());
		row.put("itime", a.getInspectedTime());
		row.put("sport", a.getServerPort());
		row.put("mdata", a.getMetadata());
		row.put("pid", pid);
	}

	@Override
	protected void onVortexRowsChanged() {
		if (!sendDbIdSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		if (sendDbIdSocket.hasConnections()) {
			List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(DSID));
			sendDbIdSocket.sendMessage(new VortexDbInterPortletMessage(VortexDbInterPortletMessage.TYPE_DBSERVERID, selections));
		}
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == pidSocket) {
			VortexPidInterPortletMessage msg = (VortexPidInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetTuple2Filter(getTable().getColumn(HOST), getTable().getColumn("pid"), (Set) msg.getHostAndPids()));
			onVortexRowsChanged();
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	final private VortexWebMetadataColumnsManager metadataColumnManager;
	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityAdded(node);
		super.onMachineEntityAdded(node);
	}
	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityUpdated(node);
		super.onMachineEntityUpdated(node);
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		metadataColumnManager.onMachineEntityRemoved(node);
		super.onMachineEntityRemoved(node);
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		metadataColumnManager.init(configuration, origToNewIdMapping, sb);
		super.init(configuration, origToNewIdMapping, sb);
	}
	@Override
	public Map<String, Object> getConfiguration() {
		return metadataColumnManager.getConfiguration(super.getConfiguration());
	}
	@Override
	public void onEyeSnapshotProcessed() {
		metadataColumnManager.onEyeSnapshotProcessed(service.getAgentManager());
		super.onEyeSnapshotProcessed();
	}
}
