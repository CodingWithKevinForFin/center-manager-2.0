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
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeBackupDestination;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBackupDestinationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientBackupDestination;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebBackupDestinationFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunBackupFormPortlet;

public class VortexWebBackupDestinationTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private VortexWebEyeService service;
	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private VortexWebMetadataColumnsManager metadataColumnManager;

	public VortexWebBackupDestinationTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "bdid", "host", "name", "path", "now", "bmuid", "data", "bcnt", "mdata" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Backup Destinations");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", "bdid", service.getIdFormatter("BD-"));
		table.addColumn(true, "Backup Destination Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Host", "host", service.getHostnameFormatter());
		table.addColumn(true, "path", "path", service.getBasicFormatter()).setWidth(600);
		table.addColumn(true, "Backups", "bcnt", service.getNumberFormatter());
		table.addColumn(false, "Updated", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Machine Uid", "bmuid", service.getBasicFormatter());
		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_BACKUP_DESTINATION, "mdata");

		for (VortexClientBackupDestination bp : service.getAgentManager().getBackupDestinations())
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
		if (node.getType() == VortexAgentEntity.TYPE_BACKUP_DESTINATION) {
			VortexClientBackupDestination t = (VortexClientBackupDestination) node;
			adBackupDestinationRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void adBackupDestinationRow(VortexClientBackupDestination node) {
		VortexEyeBackupDestination bd = node.getData();
		Row existing = rows.get(node.getId());
		String path = bd.getDestinationPath();
		long now = bd.getNow();
		String bmuid = bd.getDestinationMachineUid();
		//String dp = bd.getDestinationPath();
		String name = bd.getName();
		VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(bmuid);
		String host = null;
		int bcnt = node.getBackups().size();
		if (machine != null)
			host = machine.getHostName();
		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), host, name, path, now, bmuid, node, bcnt, bd.getMetadata()));
		} else {
			existing.put("host", host);
			existing.put("name", name);
			existing.put("path", path);
			existing.put("now", now);
			existing.put("bmuid", bmuid);
			existing.put("bcnt", bcnt);
			existing.put("mdata", bd.getMetadata());
		}
	}
	private void removeBuildProcedure(VortexClientBackupDestination node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BACKUP_DESTINATION) {
			adBackupDestinationRow((VortexClientBackupDestination) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BACKUP_DESTINATION) {
			VortexClientBackupDestination rule = (VortexClientBackupDestination) node;
			removeBuildProcedure(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebBackupDestinationTablePortlet> {

		public static final String ID = "BackupDestnationTablePortlet";

		public Builder() {
			super(VortexWebBackupDestinationTablePortlet.class);
		}

		@Override
		public VortexWebBackupDestinationTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebBackupDestinationTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Backup Destinations Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action))
			getManager().showDialog("Add Backup Destination", new VortexWebBackupDestinationFormPortlet(generateConfig()));
		else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long bdid = row.get("bdid", Long.class);
				VortexClientBackupDestination bp = service.getAgentManager().getBackupDestination(bdid);
				if (bp != null) {
					VortexWebBackupDestinationFormPortlet p = new VortexWebBackupDestinationFormPortlet(generateConfig());
					p.setBackupDestinationToCopy(bp);
					getManager().showDialog("Copy Backup Destination", p);
				}
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long bdid = row.get("bdid", Long.class);
				VortexClientBackupDestination bp = service.getAgentManager().getBackupDestination(bdid);
				if (bp != null) {
					VortexWebBackupDestinationFormPortlet p = new VortexWebBackupDestinationFormPortlet(generateConfig());
					p.setBackupDestinationToEdit(bp);
					getManager().showDialog("Edit Backup Destination", p);
				}
			}
		} else if ("delete".equals(action)) {
			List<VortexEyeRequest> req = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageBackupDestinationRequest request = nw(VortexEyeManageBackupDestinationRequest.class);
				VortexEyeBackupDestination exp = nw(VortexEyeBackupDestination.class);
				exp.setId(row.get("bdid", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setBackupDestination(exp);
				req.add(request);
			}
			getManager().showDialog("delete Backup Destination",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), req, "Delete Backup Destination", "backupdest.jpg").setIconToDelete());
		} else if ("backup".equals(action)) {
			List<VortexClientBackupDestination> destinations = new ArrayList<VortexClientBackupDestination>();
			for (Row row : table.getSelectedRows()) {
				VortexClientBackupDestination bp = row.get("data", VortexClientBackupDestination.class);
				if (bp.getBackups().size() > 0)
					destinations.add(bp);
			}
			if (destinations.isEmpty()) {
				getManager().showAlert("Selected destinations do not have backups nor archives configured");
			} else {
				final VortexWebRunBackupFormPortlet p = new VortexWebRunBackupFormPortlet(generateConfig());
				p.setDestinations(destinations);
				getManager().showDialog("Run Backup", p);
			}
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		items.add(new BasicWebMenuLink("Create Backup Destination", true, "create"));
		items.add(new BasicWebMenuLink("Copy Backup Destination", true, "copy"));
		items.add(new BasicWebMenuLink("Edit Backup Destination", true, "edit"));
		items.add(new BasicWebMenuLink("Delete Backup Destination", true, "delete"));
		boolean hasBackups = false;
		for (Row row : table.getSelectedRows()) {
			int cnt = row.get("bcnt", int.class);
			if (cnt > 0) {
				hasBackups = true;
				break;
			}
		}
		if (hasBackups)
			items.add(new BasicWebMenuLink("Run backup(s)", true, "backup"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		//if (localSocket == recvMachineSocket) {
		//VortexUtils.applyIDFilter((VortexInterPortletMessage) message, getTable());
		//onSelectedChanged(getTable());
		//} else {
		super.onMessage(localSocket, remoteSocket, message);
		//}
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
}
