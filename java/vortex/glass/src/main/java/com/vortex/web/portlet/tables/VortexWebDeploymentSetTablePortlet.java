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
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexDeploymentSet;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageDeploymentSetRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientDeploymentSet;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.messages.VortexDeploymentSetIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCopyDeploymentsFormPortlet;
import com.vortex.web.portlet.forms.VortexWebDeploymentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebDeploymentSetFormPortlet;

public class VortexWebDeploymentSetTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private VortexWebEyeService service;
	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private BasicPortletSocket sendDeploymentSetIdSocket;
	private VortexWebMetadataColumnsManager metadataColumnManager;

	public VortexWebDeploymentSetTablePortlet(PortletConfig config) {
		super(config, null);
		addOption(OPTION_ROW_HEIGHT, 30);
		addOption(OPTION_USE_GREY_BARS, false);
		addOption(OPTION_SELECTED_CSS_CLASS, "");
		addOption(OPTION_ACTIVE_CSS_CLASS, "");
		addOption(OPTION_BACKGROUND_STYLE, "style.background=#666666");
		String[] ids = { DSID, "name", NOW, "props", "mdata" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Deployment Sets");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", DSID, service.getIdFormatter("DS-"));
		table.addColumn(true, "Deployment Set Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("table_icon");
		table.addColumn(true, "Properties", "props", service.getBasicFormatter());
		table.addColumn(false, "Updated", NOW, service.getDateTimeWebCellFormatter());
		table.setMenuFactory(this);
		this.sendDeploymentSetIdSocket = addSocket(true, "sendDeploymentSetId", "Send Deployment Set Id", true, CH.s(VortexDeploymentSetIdInterPortletMessage.class), null);
		agentManager.addMachineListener(this);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_DEPLOYMENT_SET, "mdata");
		for (VortexClientDeploymentSet bp : service.getAgentManager().getDeploymentSets())
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
		if (node.getType() == VortexAgentEntity.TYPE_DEPLOYMENT_SET) {
			VortexClientDeploymentSet t = (VortexClientDeploymentSet) node;
			addDeploymentSetRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addDeploymentSetRow(VortexClientDeploymentSet node) {
		VortexDeploymentSet bp = node.getData();
		Row existing = rows.get(node.getId());
		String name = bp.getName();
		long now = bp.getNow();
		String bmuid = bp.getName();
		String props = bp.getProperties();
		VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(bmuid);
		String host = null;
		if (machine != null)
			host = machine.getHostName();
		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), name, now, props, bp.getMetadata()));
		} else {
			existing.put("name", name);
			existing.put("now", now);
			existing.put("props", props);
			existing.put("mdata", bp.getMetadata());
		}
		if (!getTable().hasSelectedRows())
			onVortexRowsChanged();

	}
	private void removeDeploymentSet(VortexClientDeploymentSet node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_DEPLOYMENT_SET) {
			addDeploymentSetRow((VortexClientDeploymentSet) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_DEPLOYMENT_SET) {
			VortexClientDeploymentSet rule = (VortexClientDeploymentSet) node;
			removeDeploymentSet(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDeploymentSetTablePortlet> {

		public static final String ID = "DeploymentSetsTablePortlet";

		public Builder() {
			super(VortexWebDeploymentSetTablePortlet.class);
		}

		@Override
		public VortexWebDeploymentSetTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDeploymentSetTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Deployment-Sets Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action)) {
			getManager().showDialog("Add Deployment Set", new VortexWebDeploymentSetFormPortlet(generateConfig()));
		} else if ("createdeployment".equals(action)) {
			if (table.getSelectedRows().size() == 1) {
				long dsid = table.getSelectedRows().get(0).get("dsid", Long.class);
				VortexWebDeploymentFormPortlet p = new VortexWebDeploymentFormPortlet(generateConfig());
				p.setDeploymentSetId(dsid);
				getManager().showDialog("Add Deployment", p);
			}

		} else if ("copy".equals(action)) {
			VortexWebCopyDeploymentsFormPortlet form = new VortexWebCopyDeploymentsFormPortlet(generateConfig());
			int cnt = 0;
			for (Row row : table.getSelectedRows()) {
				long dsid = row.get("dsid", Long.class);
				VortexClientDeploymentSet bp = service.getAgentManager().getDeploymentSet(dsid);
				if (bp != null) {
					cnt++;
					form.addDeploymentSetToCopy(null, bp);
				}
			}
			if (cnt > 0)
				getManager().showDialog("Copy Deployment Set", form);//new VortexWebCopyDeploymentSetFormPortlet(generateConfig(), bp));
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long dsid = row.get("dsid", Long.class);
				VortexClientDeploymentSet bp = service.getAgentManager().getDeploymentSet(dsid);
				if (bp != null) {
					VortexWebDeploymentSetFormPortlet p = new VortexWebDeploymentSetFormPortlet(generateConfig());
					p.setDeploymentSetToEdit(bp);
					getManager().showDialog("Edit Deployment Set", p);
				}
			}
		} else if ("delete".equals(action)) {
			List<VortexEyeRequest> requests = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageDeploymentSetRequest request = nw(VortexEyeManageDeploymentSetRequest.class);
				VortexDeploymentSet exp = nw(VortexDeploymentSet.class);
				exp.setId(row.get("dsid", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setDeploymentSet((exp));
				requests.add(request);
			}
			getManager().showDialog("Delete Deployment Set",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), requests, "Delete Deployment Set", "deployment.jpg").setIconToDelete());
			//} else if ("run".equals(action)) {
			//List<WebAgentDeploymentSet> bps = new ArrayList<WebAgentDeploymentSet>();
			//for (Row row : table.getSelectedRows()) {
			//long dsid = row.get("dsid", Long.class);
			//WebAgentDeploymentSet bp = service.getAgentManager().getDeploymentSet(dsid);
			//if (bp != null) {
			//bps.add(bp);
			//}
			//}
			//if (bps.size() > 0) {
			//VortexRunProcedureFormPortlet p = new VortexRunProcedureFormPortlet(generateConfig());
			//p.setDeploymentSets(bps);
			//getManager().showDialog("Run Command - " + p.describe(), p);
			//}
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
		if (table.getSelectedRows().size() == 1) {
			items.add(new BasicWebMenuLink("Add Deployment to selected set", true, "createdeployment"));
		}
		items.add(new BasicWebMenuLink("Create Deployment Set", true, "create"));
		items.add(new BasicWebMenuLink("Copy Deployment Set", true, "copy"));
		items.add(new BasicWebMenuLink("Edit Deployment Set", true, "edit"));
		items.add(new BasicWebMenuLink("Delete Deployment Set", true, "delete"));
		//items.add(new BasicWebMenuLink("Run Deployment Set", true, "run"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	protected void onVortexRowsChanged() {
		if (!sendDeploymentSetIdSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendDeploymentSetIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(DSID));
			sendDeploymentSetIdSocket.sendMessage(new VortexDeploymentSetIdInterPortletMessage(selections));
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
