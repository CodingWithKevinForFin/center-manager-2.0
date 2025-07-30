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
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexBuildProcedure;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageBuildProcedureRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRunBuildProcedureResponse;
import com.vortex.client.VortexClientBuildProcedure;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.messages.VortexProcedureIdInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebBuildProcedureFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebRunProcedureFormPortlet;

public class VortexWebBuildProcedureTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	final private BasicPortletSocket sendProcedureIdSocket;
	private BasicPortletSocket procedureIdSocket;

	public VortexWebBuildProcedureTablePortlet(PortletConfig config) {
		super(config, null);
		addOption(OPTION_ROW_HEIGHT, 30);
		addOption(OPTION_USE_GREY_BARS, false);
		addOption(OPTION_SELECTED_CSS_CLASS, "");
		addOption(OPTION_ACTIVE_CSS_CLASS, "");
		addOption(OPTION_BACKGROUND_STYLE, "style.background=#aabbaa");

		String[] ids = { "bpid", "name", "now", "bmuid", "boutput", "buser", "bproc", "brn", "host", "bver", "stdin", "mdata", "vfn" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Build Procedures");
		SmartTable st = new BasicSmartTable(inner);

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", BPID, service.getIdFormatter("BP-"));
		table.addColumn(true, "Build Procedure Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("table_icon");
		table.addColumn(true, "Host", "host", service.getHostnameFormatter());
		table.addColumn(true, "User Template", "buser", service.getBasicFormatter());
		table.addColumn(true, "Command Template", "bproc", service.getBasicFormatter()).setWidth(400);
		table.addColumn(true, "Version Template", "bver", service.getBasicFormatter()).setWidth(400);
		table.addColumn(false, "Updated", "now", service.getDateTimeWebCellFormatter());
		table.addColumn(false, "Machine Uid", "bmuid", service.getBasicFormatter());
		table.addColumn(false, "Build Output File", "boutput", service.getBasicFormatter());
		table.addColumn(false, "Build Output Verify File", "vfn", service.getBasicFormatter());
		table.addColumn(false, "Build Result Name", "brn", service.getBasicFormatter());
		table.addColumn(false, "Stdin Template", "stdin", service.getBasicFormatter());
		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		setTable(table);
		this.sendProcedureIdSocket = addSocket(true, "sendProcedureId", "Send Build Procedure ID", true, CH.s(VortexProcedureIdInterPortletMessage.class), null);
		this.procedureIdSocket = addSocket(false, "procedureId", "build Procedure ID", true, null, CH.s(VortexProcedureIdInterPortletMessage.class));

		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_BUILD_PROCEDURE, "mdata");

		for (VortexClientBuildProcedure bp : service.getAgentManager().getBuildProcedures())
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
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_PROCEDURE) {
			VortexClientBuildProcedure t = (VortexClientBuildProcedure) node;
			addBuildProcedureRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addBuildProcedureRow(VortexClientBuildProcedure node) {
		VortexBuildProcedure bp = node.getData();
		Row existing = rows.get(node.getId());
		String name = bp.getName();
		long now = bp.getNow();
		String bmuid = bp.getBuildMachineUid();
		String bo = bp.getTemplateResultFile();
		String vfn = bp.getTemplateResultVerifyFile();
		String bu = bp.getTemplateUser();
		String bproc = bp.getTemplateCommand();
		String brn = bp.getTemplateResultName();
		String bver = bp.getTemplateResultVersion();
		String stdin = bp.getTemplateStdin();
		VortexClientMachine machine = service.getAgentManager().getAgentMachineByUid(bmuid);
		String host = null;
		if (machine != null)
			host = machine.getHostName();
		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), name, now, bmuid, bo, bu, bproc, brn, host, bver, stdin, bp.getMetadata(), vfn));
		} else {
			existing.put("name", name);
			existing.put("now", now);
			existing.put("bmuid", bmuid);
			existing.put("boutput", bo);
			existing.put("buser", bu);
			existing.put("bproc", bproc);
			existing.put("brn", brn);
			existing.put("host", host);
			existing.put("bver", bver);
			existing.put("stdin", stdin);
			existing.put("mdata", bp.getMetadata());
			existing.put("vfn", vfn);
		}
	}
	private void removeBuildProcedure(VortexClientBuildProcedure node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_PROCEDURE) {
			addBuildProcedureRow((VortexClientBuildProcedure) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_BUILD_PROCEDURE) {
			VortexClientBuildProcedure rule = (VortexClientBuildProcedure) node;
			removeBuildProcedure(rule);
		}
		metadataColumnManager.onMachineEntityRemoved(node);
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebBuildProcedureTablePortlet> {

		public static final String ID = "BuildProceduresTablePortlet";

		public Builder() {
			super(VortexWebBuildProcedureTablePortlet.class);
		}

		@Override
		public VortexWebBuildProcedureTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebBuildProcedureTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Build Procedures Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action))
			getManager().showDialog("Add Build Procedure", new VortexWebBuildProcedureFormPortlet(generateConfig()));
		else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long bpid = row.get("bpid", Long.class);
				VortexClientBuildProcedure bp = service.getAgentManager().getBuildProcedure(bpid);
				if (bp != null) {
					VortexWebBuildProcedureFormPortlet p = new VortexWebBuildProcedureFormPortlet(generateConfig());
					p.setBuildProcedureToCopy(bp);
					getManager().showDialog("Copy Build Procedure", p);
				}
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long bpid = row.get("bpid", Long.class);
				VortexClientBuildProcedure bp = service.getAgentManager().getBuildProcedure(bpid);
				if (bp != null) {
					VortexWebBuildProcedureFormPortlet p = new VortexWebBuildProcedureFormPortlet(generateConfig());
					p.setBuildProcedureToEdit(bp);
					getManager().showDialog("Edit Build Procedure", p);
				}
			}
		} else if ("delete".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageBuildProcedureRequest request = nw(VortexEyeManageBuildProcedureRequest.class);
				VortexBuildProcedure exp = nw(VortexBuildProcedure.class);
				exp.setId(row.get("bpid", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setBuildProcedure(exp);
				getManager().showDialog("delete Build Procedure",
						new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), request, "Delete Build Procedure", "build.jpg").setIconToDelete());
			}
		} else if ("run".equals(action)) {
			List<VortexClientBuildProcedure> bps = new ArrayList<VortexClientBuildProcedure>();
			for (Row row : table.getSelectedRows()) {
				long bpid = row.get("bpid", Long.class);
				VortexClientBuildProcedure bp = service.getAgentManager().getBuildProcedure(bpid);
				if (bp != null) {
					bps.add(bp);
				}
			}
			if (bps.size() > 0) {
				VortexWebRunProcedureFormPortlet p = new VortexWebRunProcedureFormPortlet(generateConfig(), getPortletId());
				p.setBuildProcedures(bps);
				getManager().showDialog("Run Command - " + p.describe(), p);
			}
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		//if (!getTable().hasSelectedRows())
		onVortexRowsChanged();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		items.add(new BasicWebMenuLink("Create Build Procedure", true, "create"));
		items.add(new BasicWebMenuLink("Copy Build Procedure", true, "copy"));
		items.add(new BasicWebMenuLink("Edit Build Procedure", true, "edit"));
		items.add(new BasicWebMenuLink("Delete Build Procedure", true, "delete"));
		items.add(new BasicWebMenuLink("Run Build Procedure", true, "run"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		//if (localSocket == recvMachineSocket) {
		//VortexUtils.applyIDFilter((VortexInterPortletMessage) message, getTable());
		//onSelectedChanged(getTable());
		//} else 
		if (localSocket == procedureIdSocket) {
			VortexProcedureIdInterPortletMessage msg = (VortexProcedureIdInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(BPID), msg.getProcedureIds()));
			onVortexRowsChanged();
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	protected void onVortexRowsChanged() {
		if (!sendProcedureIdSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
		if (sendProcedureIdSocket.hasConnections()) {
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(BPID));
			sendProcedureIdSocket.sendMessage(new VortexProcedureIdInterPortletMessage(selections));
		}
	}

	final private VortexWebMetadataColumnsManager metadataColumnManager;
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
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		if (action instanceof VortexEyeRunBuildProcedureResponse) {
			VortexEyeRunBuildProcedureResponse response = (VortexEyeRunBuildProcedureResponse) result.getAction();
			if (!response.getOk())
				getManager().showAlert(response.getMessage());
		} else
			super.onBackendResponse(result);

	}

}
