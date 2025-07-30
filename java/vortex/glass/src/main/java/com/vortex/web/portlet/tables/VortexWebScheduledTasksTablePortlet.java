package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeScheduledTask;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageScheduledTaskRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunScheduledTaskRequest;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.client.VortexClientScheduledTask;
import com.vortex.web.messages.VortexDeploymentIdInterPortletMessage;
import com.vortex.web.messages.VortexDeploymentSetIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebScheduledTaskFormPortlet;

public class VortexWebScheduledTasksTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private BasicPortletSocket sendDeploymentIdSocket;
	private VortexWebMetadataColumnsManager metadataColumnManager;

	public VortexWebScheduledTasksTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { STID, "desc", NOW, "dpid", "cmd", "ib", "lrun", "nrun", "opt", "status", "state", "tgtid", "type", "msg", "rcount", "comments", "mdata" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Scheduled Tasks");
		SmartTable st = new BasicSmartTable(inner);

		MapWebCellFormatter<Byte> typeFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		typeFormatter.addEntry(VortexEyeScheduledTask.TYPE_BACKUP, "Backup", "_cna=portlet_icon_backup", "&nbsp;&nbsp;&nbsp;&nbsp;Run Backup");
		typeFormatter.addEntry(VortexEyeScheduledTask.TYPE_SCRIPT, "Scripts", "_cna=portlet_icon_file", "&nbsp;&nbsp;&nbsp;&nbsp;Run Script");
		typeFormatter.addEntry(VortexEyeScheduledTask.TYPE_START, "Start", "_cna=portlet_icon_process", "&nbsp;&nbsp;&nbsp;&nbsp;Start App");
		typeFormatter.addEntry(VortexEyeScheduledTask.TYPE_STOP, "Stop", "_cna=portlet_icon_process", "&nbsp;&nbsp;&nbsp;&nbsp;Stop App");
		typeFormatter.addEntry(VortexEyeScheduledTask.TYPE_DATABASE_INSPECT, "Inspect Db", "_cna=portlet_icon_db_database", "&nbsp;&nbsp;&nbsp;&nbsp;Inspect Db");
		typeFormatter.setDefaultWidth(90).lockFormatter();

		MapWebCellFormatter<Byte> statusFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_DISABLED, "Disabled", "_cna=portlet_icon_warning", "&nbsp;&nbsp;&nbsp;&nbsp;Disabled");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_FAILURE, "Failed", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Failed");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_INVALID, "Invalid", "_cna=portlet_icon_error", "&nbsp;&nbsp;&nbsp;&nbsp;Invalid");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_MODIFIED, "Modified", "_cna=", "&nbsp;&nbsp;&nbsp;&nbsp;Modified");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_NEVER_RUN, "Never Run", "_cna=", "&nbsp;&nbsp;&nbsp;&nbsp;Never Run");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_OKAY, "Okay", "_cna=portlet_icon_okay", "&nbsp;&nbsp;&nbsp;&nbsp;Okay");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_RUNNING, "Running", "_cna=portlet_icon_connection", "&nbsp;&nbsp;&nbsp;&nbsp;Running");
		statusFormatter.addEntry(VortexEyeScheduledTask.STATUS_QUEUED_TO_RUN, "Queued To Run", "_cna=portlet_icon_connection", "&nbsp;&nbsp;&nbsp;&nbsp;Queued To Run");
		statusFormatter.setDefaultWidth(90).lockFormatter();

		MapWebCellFormatter<Byte> stateFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		stateFormatter.addEntry(VortexEyeScheduledTask.STATE_ACTIVE, "Active", "_cna=portlet_icon_play", "&nbsp;&nbsp;&nbsp;&nbsp;Active");
		stateFormatter.addEntry(VortexEyeScheduledTask.STATE_PAUSED, "Paused", "_cna=portlet_icon_pause", "&nbsp;&nbsp;&nbsp;&nbsp;Paused");
		stateFormatter.setDefaultWidth(90).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", STID, service.getIdFormatter("ST-"));
		table.addColumn(true, "State", "state", stateFormatter);
		table.addColumn(true, "Description", "desc", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Next Run", "nrun", service.getDateTimeWebCellFormatter()).setCssColumn("bold");
		table.addColumn(false, "Updated", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Deployment Id", "dpid", service.getIdFormatter("DP-"));
		table.addColumn(true, "Command", "cmd", service.getBasicFormatter());
		table.addColumn(true, "Invoked By", "ib", service.getBasicFormatter());
		table.addColumn(true, "Last Run", "lrun", service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Options", "opt", service.getNumberFormatter());
		table.addColumn(true, "Status", "status", statusFormatter);
		table.addColumn(true, "Type", "type", typeFormatter);
		table.addColumn(true, "Message", "msg", service.getBasicFormatter());
		table.addColumn(true, "Runs", "rcount", service.getNumberFormatter());
		table.addColumn(false, "Target Id", "tgtid", service.getBasicFormatter());
		table.addColumn(false, "Comments", "comments", service.getBasicFormatter());
		table.setMenuFactory(this);
		this.sendDeploymentIdSocket = addSocket(true, "sendDeploymentId", "Send Deployment Id", true, CH.s(VortexDeploymentIdInterPortletMessage.class), null);
		agentManager.addMachineListener(this);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_SCHEDULED_TASK, "mdata");
		for (VortexClientScheduledTask bp : service.getAgentManager().getScheduledTasks())
			onMachineEntityAdded(bp);
	}

	@Override
	public void close() {
		agentManager.removeMachineListener(this);
		super.close();
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_SCHEDULED_TASK) {
			VortexClientScheduledTask t = (VortexClientScheduledTask) node;
			addScheduledTaskRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addScheduledTaskRow(VortexClientScheduledTask node) {
		VortexEyeScheduledTask st = node.getData();
		Row existing = rows.get(node.getId());
		String desc = st.getDescription();
		long now = st.getNow();
		String cmd = st.getCommand();
		long dpid = st.getDeploymentId();
		String msg = st.getMessage();
		String ib = st.getInvokedBy();
		long lrun = st.getLastRuntime();
		long nrun = st.getNextRuntime();
		long rcount = st.getRunCount();
		if (nrun < 0)
			nrun = 0;
		int opt = st.getOptions();
		byte status = st.getStatus();
		byte state = st.getState();
		String comments = st.getComments();
		long tgtid = st.getTargetId();
		byte type = st.getType();

		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), desc, now, dpid, cmd, ib, lrun, nrun, opt, status, state, tgtid, type, msg, rcount, comments, st.getMetadata()));
		} else {
			existing.put("desc", desc);
			existing.put("now", now);
			existing.put("dpid", dpid);
			existing.put("cmd", cmd);
			existing.put("ib", ib);
			existing.put("lrun", lrun);
			existing.put("nrun", nrun);
			existing.put("opt", opt);
			existing.put("status", status);
			existing.put("state", state);
			existing.put("tgtid", tgtid);
			existing.put("type", type);
			existing.put("msg", msg);
			existing.put("rcount", rcount);
			existing.put("comments", comments);
			existing.put("mdata", st.getMetadata());
		}
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();

	}
	private void removeScheduledTaskRow(VortexClientScheduledTask node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_SCHEDULED_TASK) {
			addScheduledTaskRow((VortexClientScheduledTask) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_SCHEDULED_TASK) {
			VortexClientScheduledTask rule = (VortexClientScheduledTask) node;
			removeScheduledTaskRow(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebScheduledTasksTablePortlet> {

		public static final String ID = "ScheduledTasksTablePortlet";

		public Builder() {
			super(VortexWebScheduledTasksTablePortlet.class);
		}

		@Override
		public VortexWebScheduledTasksTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebScheduledTasksTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Scheduled Tasks Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action)) {
			getManager().showDialog("Add Scheduled Task", new VortexWebScheduledTaskFormPortlet(generateConfig()));
		} else if ("activate".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null && bp.getData().getState() != VortexEyeScheduledTask.STATE_ACTIVE) {
					VortexEyeManageScheduledTaskRequest request = nw(VortexEyeManageScheduledTaskRequest.class);
					VortexEyeScheduledTask st = bp.getData().clone();
					st.setState(VortexEyeScheduledTask.STATE_ACTIVE);
					request.setScheduledTask(st);
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
		} else if ("pause".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null && bp.getData().getState() != VortexEyeScheduledTask.STATE_PAUSED) {
					VortexEyeManageScheduledTaskRequest request = nw(VortexEyeManageScheduledTaskRequest.class);
					VortexEyeScheduledTask st = bp.getData().clone();
					st.setState(VortexEyeScheduledTask.STATE_PAUSED);
					request.setScheduledTask(st);
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
		} else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null) {
					VortexWebScheduledTaskFormPortlet form = new VortexWebScheduledTaskFormPortlet(generateConfig());
					form.setScheduledTaskToCopy(bp);
					getManager().showDialog("Copy Scheduled Task", form);
				}
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null) {
					VortexWebScheduledTaskFormPortlet form = new VortexWebScheduledTaskFormPortlet(generateConfig());
					form.setScheduledTaskToEdit(bp);
					getManager().showDialog("Edit Scheduled Task", form);
				}
			}
		} else if ("delete".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null) {
					VortexEyeManageScheduledTaskRequest request = nw(VortexEyeManageScheduledTaskRequest.class);
					VortexEyeScheduledTask st = nw(VortexEyeScheduledTask.class);
					st.setId(bp.getId());
					st.setRevision(VortexAgentEntity.REVISION_DONE);
					request.setScheduledTask(st);
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
		} else if ("run".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long stid = row.get(STID, Long.class);
				VortexClientScheduledTask bp = service.getAgentManager().getScheduledTask(stid);
				if (bp != null) {
					VortexEyeRunScheduledTaskRequest request = nw(VortexEyeRunScheduledTaskRequest.class);
					VortexEyeScheduledTask st = nw(VortexEyeScheduledTask.class);
					st.setId(bp.getId());
					request.setScheduledTaskId(bp.getId());
					service.sendRequestToBackend(getPortletId(), request);
				}
			}
		}

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		onVortexRowsChanged();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		boolean hasActive = false;
		boolean hasPaused = false;
		for (Row row : table.getSelectedRows()) {
			Byte state = row.get("state", Byte.class);
			if (state == null)
				continue;
			switch (state) {
				case VortexEyeScheduledTask.STATE_ACTIVE:
					hasActive = true;
					break;
				case VortexEyeScheduledTask.STATE_PAUSED:
					hasPaused = true;
					break;
			}
		}
		if (hasActive)
			items.add(new BasicWebMenuLink("Pause Scheduled Task", true, "pause"));
		if (hasPaused)
			items.add(new BasicWebMenuLink("Activate Scheduled Task", true, "activate"));
		items.add(new BasicWebMenuLink("Create Scheduled Task", true, "create"));
		items.add(new BasicWebMenuLink("Copy Scheduled Task", true, "copy"));
		items.add(new BasicWebMenuLink("Edit Scheduled Task", true, "edit"));
		items.add(new BasicWebMenuLink("Delete Scheduled Task", true, "delete"));
		items.add(new BasicWebMenuLink("Run Scheduled Task", true, "run"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {

	}
	protected void onVortexRowsChanged() {
		if (!sendDeploymentIdSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendDeploymentIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get("dpid"));
			sendDeploymentIdSocket.sendMessage(new VortexDeploymentSetIdInterPortletMessage(selections));
		}
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
	}

}
