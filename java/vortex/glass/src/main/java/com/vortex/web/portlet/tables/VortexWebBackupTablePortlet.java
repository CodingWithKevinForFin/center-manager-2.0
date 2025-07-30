package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.container.ResultMessage;
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
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.VortexEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentBackupFile;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentFile;
import com.f1.vortexcommon.msg.eye.VortexEyeBackup;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeQueryDataResponse;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBackupRequest;
import com.vortex.client.VortexClientBackup;
import com.vortex.client.VortexClientBackupDestination;
import com.vortex.client.VortexClientBackupFile;
import com.vortex.client.VortexClientDeployment;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.messages.VortexBackupIdInterPortletMessage;
import com.vortex.web.messages.VortexDeploymentIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebBackupFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.grids.VortexWebMachineFilesDialog;
import com.vortex.web.portlet.visuals.VortexTerminalPortlet;

public class VortexWebBackupTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	final private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	final private BasicPortletSocket deploymentIdSocket;
	private VortexWebMetadataColumnsManager metadataColumnManager;
	private BasicPortletSocket sendBackupIdSocket;
	private VortexEyeQueryDataRequest currentRequest;
	private String currentAction;

	public VortexWebBackupTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "buid", "shost", "spath", "dname", "bdid", "dpid", "now", "smuid", "status", "message", "data", "mdata", "desc", "ignore", "bytes_count", "files_count",
				"ignore_count", "latest_mod_time", "manifest_time" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Managed Directorys");
		SmartTable st = new BasicSmartTable(inner);

		MapWebCellFormatter<Byte> backupStatusFormatter = new MapWebCellFormatter<Byte>(getManager().getTextFormatter());
		backupStatusFormatter.addEntry(VortexEyeBackup.STATUS_FAILURE, "Failure", "_cna=portlet_icon_error,style.color=", "&nbsp;&nbsp;&nbsp;&nbsp;Failure");
		backupStatusFormatter.addEntry(VortexEyeBackup.STATUS_OKAY, "Okay", "_cna=portlet_icon_backup,style.color=blue", "&nbsp;&nbsp;&nbsp;&nbsp;Okay");
		backupStatusFormatter.addEntry(VortexEyeBackup.STATUS_PARTIAL_OKAY, "Empty", "_cna=portlet_icon_warning,style.color=#4444dd", "&nbsp;&nbsp;&nbsp;&nbsp;Empty");
		backupStatusFormatter.addEntry(VortexEyeBackup.STATUS_RUNNING, "Running", "_cna=portlet_icon_connection,style.color=green", "&nbsp;&nbsp;&nbsp;&nbsp;Running");
		backupStatusFormatter.addEntry(VortexEyeBackup.STATUS_NEVER_RUN, "Never Run", "_cna=portlet_icon_info,style.color=black", "&nbsp;&nbsp;&nbsp;&nbsp;Never Run");
		backupStatusFormatter.setDefaultWidth(80).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", "buid", service.getIdFormatter("BU-"));
		table.addColumn(true, "Src. Host", "shost", service.getHostnameFormatter());
		table.addColumn(true, "Src. Path", "spath", service.getBasicFormatter()).setWidth(600);
		table.addColumn(true, "Destination", "dname", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Status", "status", backupStatusFormatter);
		table.addColumn(true, "Backup Message", "message", service.getBasicFormatter()).setWidth(400);
		table.addColumn(false, "Destination Id", "bdid", service.getIdFormatter("BD-"));
		table.addColumn(false, "Deployment Id", "dpid", service.getIdFormatter("DP-"));
		table.addColumn(false, "Updated", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Machine Uid", "smuid", service.getBasicFormatter());
		table.addColumn(false, "Description", "desc", service.getBasicFormatter());
		table.addColumn(false, "Size", "bytes_count", service.getMemoryFormatter());
		table.addColumn(false, "Files", "files_count", service.getNumberFormatter());
		table.addColumn(false, "Files Ignored", "ignore_count", service.getNumberFormatter());
		table.addColumn(false, "Latest File Modified Time", "latest_mod_time", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Capture Time", "manifest_time", service.getDateTimeWebCellFormatter());
		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		this.deploymentIdSocket = addSocket(false, "miid", "Machine ID", true, null, CH.s(VortexDeploymentIdInterPortletMessage.class));
		this.sendBackupIdSocket = addSocket(true, "sendBuid", "Send Backup ID", true, CH.s(VortexBackupIdInterPortletMessage.class), null);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_BACKUP, "mdata");

		for (VortexClientBackup bp : service.getAgentManager().getBackups())
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
		if (node.getType() == VortexAgentEntity.TYPE_BACKUP) {
			VortexClientBackup t = (VortexClientBackup) node;
			addBackupRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addBackupRow(VortexClientBackup node) {
		VortexEyeBackup bd = node.getData();
		Row existing = rows.get(node.getId());
		long now = bd.getNow();
		String smuid = node.getSourceMuid();
		String spath = node.getFullSourcePath();
		String message = bd.getMessage();
		byte status = bd.getStatus();
		long dpid = bd.getDeploymentId();
		VortexClientMachine machine = smuid == null ? null : service.getAgentManager().getAgentMachineByUid(smuid);
		String shost = null;
		String dname = null;
		long bdid = bd.getBackupDestinationId();
		VortexClientBackupDestination backupDest = node.getDestination();// service.getAgentManager().getBackupDestination(bdid);
		if (backupDest != null) {
			dname = backupDest.getData().getName();
		}
		String description = bd.getDescription();
		if (machine != null)
			shost = machine.getHostName();
		String[] ids = { "buid", "shost", "spath", "dname", "bdid", "dpid", "now", "smuid", "status", "message", "data", "mdata", "desc", "ignore", "bytes_count", "files_count",
				"ignore_count", "latest_mod_time", "manifest_time" };
		if (existing == null) {
			rows.put(
					node.getId(),
					addRow(node.getId(), shost, spath, dname, bdid, dpid, now, smuid, status, message, node, bd.getMetadata(), description, bd.getIgnoreExpression(),
							bd.getBytesCount(), bd.getFileCount(), bd.getIgnoredFileCount(), bd.getLatestModifiedTime(), bd.getManifestTime()));
			onVortexRowsChanged();
		} else {
			existing.put("shost", shost);
			existing.put("spath", spath);
			existing.put("dname", dname);
			existing.put("dpid", dpid);
			existing.put("now", now);
			existing.put("smuid", smuid);
			existing.put("desc", description);
			existing.put("status", status);
			existing.put("message", message);
			existing.put("mdata", bd.getMetadata());
			existing.put("ignore", bd.getIgnoreExpression());
			existing.put("bytes_count", bd.getBytesCount());
			existing.put("files_count", bd.getFileCount());
			existing.put("ignore_count", bd.getIgnoredFileCount());
			existing.put("latest_mod_time", bd.getLatestModifiedTime());
			existing.put("manifest_time", bd.getManifestTime());
		}
	}
	private void removeBuildProcedure(VortexClientBackup node) {
		Row existing = rows.remove(node.getId());
		if (existing != null) {
			removeRow(existing);
			onVortexRowsChanged();
		}
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node == null)
			return;
		switch (node.getType()) {
			case VortexAgentEntity.TYPE_DEPLOYMENT: {
				for (VortexClientBackup backup : ((VortexClientDeployment) node).getBackups().values()) {
					onMachineEntityUpdated(backup);

				}
				break;
			}
			case VortexAgentEntity.TYPE_BACKUP_DESTINATION: {
				for (VortexClientBackup backup : ((VortexClientBackupDestination) node).getBackups().values()) {
					onMachineEntityUpdated(backup);
				}
				break;
			}
			case VortexAgentEntity.TYPE_BACKUP: {
				addBackupRow((VortexClientBackup) node);
				break;
			}
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}
	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BACKUP) {
			VortexClientBackup rule = (VortexClientBackup) node;
			removeBuildProcedure(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebBackupTablePortlet> {

		public static final String ID = "BackupTablePortlet";

		public Builder() {
			super(VortexWebBackupTablePortlet.class);
		}

		@Override
		public VortexWebBackupTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebBackupTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Backup Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action))
			getManager().showDialog("Add Backup", new VortexWebBackupFormPortlet(generateConfig()));
		else if ("run".equals(action)) {
			List<Long> backups = new ArrayList<Long>();
			for (Row row : table.getSelectedRows()) {
				long buid = row.get("buid", Long.class);
				backups.add(buid);
			}
			VortexEyeRunBackupRequest req = nw(VortexEyeRunBackupRequest.class);
			req.setBackups(backups);
			getManager().showDialog("Run Backups", new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), req, "Run Backup", "backup.jpg"));
		} else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long buid = row.get("buid", Long.class);
				VortexClientBackup bp = service.getAgentManager().getBackup(buid);
				if (bp != null) {
					VortexWebBackupFormPortlet p = new VortexWebBackupFormPortlet(generateConfig());
					p.setBackupToCopy(bp);
					getManager().showDialog("Copy Backup", p);
				}
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long buid = row.get("buid", Long.class);
				VortexClientBackup bp = service.getAgentManager().getBackup(buid);
				if (bp != null) {
					VortexWebBackupFormPortlet p = new VortexWebBackupFormPortlet(generateConfig());
					p.setBackupToEdit(bp);
					getManager().showDialog("Edit Backup", p);
				}
			}
		} else if ("delete".equals(action)) {
			List<VortexEyeRequest> reqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageBackupRequest request = nw(VortexEyeManageBackupRequest.class);
				VortexEyeBackup exp = nw(VortexEyeBackup.class);
				exp.setId(row.get("buid", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setBackup(exp);
				reqs.add(request);
			}
			getManager().showDialog("Delete backup", new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), reqs, "Delete Backups", "backupdest.jpg").setIconToDelete());
		} else if ("showfiles".equals(action)) {
			List<Tuple3<String, VortexAgentFile, String>> files = new ArrayList<Tuple3<String, VortexAgentFile, String>>();
			for (Row row : table.getSelectedRows()) {
				VortexClientBackup backup = row.get("data", VortexClientBackup.class);
				VortexAgentFile srcFile = nw(VortexAgentFile.class);
				VortexAgentFile dstFile = nw(VortexAgentFile.class);
				srcFile.setPath(backup.getFullSourcePath());
				String destPath = backup.getDestination().getData().getDestinationPath() + "/vortexstore/" + backup.getSrcMachine().getMachineUid() + "/latest"
						+ backup.getFullSourcePath();
				dstFile.setPath(destPath);
				srcFile.setMask(VortexAgentFile.DIRECTORY);
				dstFile.setMask(VortexAgentFile.DIRECTORY);
				files.add(new Tuple3<String, VortexAgentFile, String>(backup.getSrcMachine().getMachineUid(), srcFile, "Source (" + backup.getSrcMachine().getHostName() + ":"
						+ backup.getFullSourcePath() + ")"));
				//files.add(new Tuple3<String, VortexAgentFile, String>(backup.getDestination().getMachine().getMachineUid(), dstFile, "Destination ("
				//+ backup.getDestination().getHostName() + ":" + destPath + ")"));
			}
			getManager().showDialog("Backup Files", new VortexWebMachineFilesDialog(generateConfig(), files));
		} else if (action.startsWith("terminal")) {
			for (Row row : getTable().getSelectedRows()) {
				VortexClientBackup data = row.get("data", VortexClientBackup.class);
				String muid = data.getMachine().getMachineUid();
				String pwd = data.getData().getSourcePath();
				String user = getManager().getState().getWebState().getUser().getUserName();
				VortexTerminalPortlet vtp = new VortexTerminalPortlet(generateConfig(), user, muid, pwd);
				vtp.setAllowExit(true);

				vtp.logLocation("Opened Terminal from Managed Directory - BU-" + data.getId() + " '" + data.getDescription() + "' at ");
				getManager().showDialog("Terminal", vtp);
			}
		} else if ("deleted".equals(action) || "deleted_filtered".equals(action)) {
			for (Row row : getTable().getSelectedRows()) {
				VortexClientBackup data = row.get("data", VortexClientBackup.class);
				VortexEyeQueryDataRequest eyeReq = nw(VortexEyeQueryDataRequest.class);
				eyeReq.setIds(new long[] { data.getId() });
				eyeReq.setType(VortexAgentEntity.TYPE_BACKUP_FILE);
				eyeReq.setSearchDeleted(true);
				eyeReq.setSearchExpression("%");
				service.sendRequestToBackend(getPortletId(), eyeReq);
				this.currentRequest = eyeReq;
				this.currentAction = action;
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
		int sel = table.getSelectedRows().size();
		if (sel > 0) {
			items.add(new BasicWebMenuLink("Backup Managed Directories", true, "run"));
			if (sel == 1) {
				items.add(new BasicWebMenuLink("Copy Managed Directory", true, "copy"));
				items.add(new BasicWebMenuLink("Edit Managed Directory", true, "edit"));
			}
			items.add(new BasicWebMenuLink("Delete Managed Directory", true, "delete"));
			items.add(new BasicWebMenuLink("Browse Files", true, "showfiles"));
			items.add(new BasicWebMenuLink("Show Deleted Files", true, "deleted"));
			items.add(new BasicWebMenuLink("Show Deleted Files (Including Filtered)", true, "deleted_filtered"));
			items.add(new BasicWebMenuLink("Open Terminal", true, "terminal"));
		}
		items.add(new BasicWebMenuLink("Create Managed Directory", true, "create"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		//if (localSocket == recvMachineSocket) {
		//VortexUtils.applyIDFilter((VortexInterPortletMessage) message, getTable());
		//onSelectedChanged(getTable());
		//} else 
		if (localSocket == deploymentIdSocket) {
			VortexDeploymentIdInterPortletMessage msg = (VortexDeploymentIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("dpid"), msg.getDeploymentIds()));
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
		onVortexRowsChanged();
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

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

	protected void onVortexRowsChanged() {
		if (!getIsEyeConnected())
			return;

		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendBackupIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add(addRow.get("buid", Long.class));
			sendBackupIdSocket.sendMessage(new VortexBackupIdInterPortletMessage(selections));
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		if (result.getRequestMessage().getAction() != this.currentRequest)
			return;
		super.onBackendResponse(result);
		VortexEyeQueryDataResponse qres = (VortexEyeQueryDataResponse) result.getAction();
		List<VortexEntity> rows = qres.getData();
		VortexWebBackupFilesTablePortlet portlet = new VortexWebBackupFilesTablePortlet(generateConfig(), true);
		boolean includeFiltered = currentAction.equals("deleted_filtered");
		for (VortexEntity row : rows) {
			VortexAgentBackupFile file = (VortexAgentBackupFile) row; //TODO: apply filter getManager().get file.getBackupId();
			if (!includeFiltered) {
				VortexClientBackup backup = agentManager.getBackup(file.getBackupId());
				if (backup != null && backup.shouldIgnore(file.getPath()))
					continue;
			}
			portlet.addBackupRow(new VortexClientBackupFile(file));
		}
		getManager().showDialog("Delete Files", portlet);
		this.currentAction = null;
		this.currentRequest = null;
	}

}
