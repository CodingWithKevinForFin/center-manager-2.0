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
import com.f1.suite.web.table.impl.MapEntryWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexExpectation;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageExpectationRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientExpectation;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage;
import com.vortex.ssoweb.SsoService;
import com.vortex.web.VortexWebEyeService;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;
import com.vortex.web.portlet.forms.VortexWebExpectationFormPortlet;

public class VortexWebExpectationsTablePortlet extends VortexWebTablePortlet implements VortexClientMachineListener, WebContextMenuFactory, WebContextMenuListener {

	private VortexWebEyeService service;
	private LongKeyMap<Row> rows = new LongKeyMap<Row>();
	private BasicPortletSocket maskSocket;

	public VortexWebExpectationsTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "id", "name", "now", "type", "masks", "tolerances", "metadata", "state", "muid", "hostname", "matchid", "mdata" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Expectations");
		SmartTable st = new BasicSmartTable(inner);
		this.service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);

		MapWebCellFormatter stateFormatter = new MapWebCellFormatter(getManager().getTextFormatter());
		stateFormatter.addEntry(VortexClientExpectation.STATE_MATCHED, "Okay", "_cna=portlet_icon_okay", "");
		stateFormatter.addEntry(VortexClientExpectation.STATE_NO_MATCH, "Error", "_cna=portlet_icon_error", "");
		stateFormatter.setDefaultWidth(20).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(true, "Id", "id", service.getIdFormatter("XP-"));
		table.addColumn(true, "Expectation Name", "name", service.getBasicFormatter()).setWidth(150).addCssClass("bold");
		table.addColumn(true, "Type", "type", service.getAgentTypeFormatter());
		table.addColumn(true, "Tolerances", "tolerances", service.getBasicFormatter());
		table.addColumn(true, "Metadata", "metadata", service.getBasicFormatter());
		table.addColumn(true, "State", "state", stateFormatter);
		table.addColumn(true, "Type Mask", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_TYPE)).setCssColumn("italic");
		table.addColumn(true, "Name Mask", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_NAME)).setCssColumn("italic");
		table.addColumn(true, "Type Mask", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_USER)).setCssColumn("italic");
		table.addColumn(true, "Name Mask", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_COMMAND)).setCssColumn("italic");
		table.addColumn(true, "State", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_STATE)).setCssColumn("italic");
		table.addColumn(true, "Foreign Host", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_FOREIGN_HOST)).setCssColumn("italic");
		table.addColumn(true, "Foreign Port", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_FOREIGN_PORT)).setCssColumn("italic");
		table.addColumn(true, "Local Host", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_LOCAL_HOST)).setCssColumn("italic");
		table.addColumn(true, "Local Port", "masks", new MapEntryWebCellFormatter(VortexExpectation.MASK_TYPE_LOCAL_PORT)).setCssColumn("italic");
		table.addColumn(true, "Mask", "masks", service.getBasicFormatter());
		table.addColumn(false, "Machine ID", "muid", service.getBasicFormatter()).setWidth(150);
		table.addColumn(false, "Host Name", "hostname", service.getBasicFormatter()).setWidth(150);
		table.addColumn(false, "Matcher ID", "matchid", service.getBasicFormatter()).setWidth(150);
		table.setMenuFactory(this);
		//table.addMenuListener(this);
		this.agentManager.addMachineListener(this);
		setTable(table);
		metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_EXPECTATION, "mdata");

		for (VortexClientExpectation expectation : service.getAgentManager().getExpectations())
			onMachineEntityAdded(expectation);
		this.maskSocket = addSocket(false, "selection", "Node Selection", true, null, CH.s(NodeSelectionInterPortletMessage.class));
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
		if (node.getType() == VortexAgentEntity.TYPE_EXPECTATION) {
			VortexClientExpectation t = (VortexClientExpectation) node;
			addExecptationRow(t);
		}
		metadataColumnManager.onMachineEntityAdded(node);
	}

	private void addExecptationRow(VortexClientExpectation node) {
		VortexExpectation exp = node.getData();
		Row existing = rows.get(node.getId());
		String name = exp.getName();
		long now = exp.getNow();
		byte type = exp.getTargetType();
		//String rules = node.getRules();
		//Map<Short, String> rulesMap = node.getRulesMap();
		Map<Byte, String> masks = exp.getFieldMasks();
		String hostname = null;
		String matchid = null;
		VortexClientEntity<?> match = node.getMatch();
		if (match != null) {
			hostname = match.getHostName();
			matchid = SH.toString(match.getId());
		}

		String tolerances = null;
		String metadata = null;
		if (existing == null) {
			rows.put(node.getId(), addRow(node.getId(), name, now, type, masks, tolerances, metadata, node.getState(), node.getMachineUid(), hostname, matchid, exp.getMetadata()));
		} else {
			existing.put("name", name);
			existing.put("now", now);
			existing.put("type", type);
			existing.put("masks", masks);
			existing.put("tolerances", tolerances);
			existing.put("metadata", metadata);
			existing.put("state", node.getState());
			existing.put("hostname", hostname);
			existing.put("matchid", matchid);
			existing.put("mdata", exp.getMetadata());
		}
	}
	private void removeExpectation(VortexClientExpectation node) {
		Row existing = rows.remove(node.getId());
		if (existing != null)
			removeRow(existing);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_EXPECTATION) {
			addExecptationRow((VortexClientExpectation) node);
		}
		metadataColumnManager.onMachineEntityUpdated(node);
	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_EXPECTATION) {
			VortexClientExpectation rule = (VortexClientExpectation) node;
			removeExpectation(rule);
		}
	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebExpectationsTablePortlet> {

		public static final String ID = "ExpectationsTablePortlet";

		public Builder() {
			super(VortexWebExpectationsTablePortlet.class);
		}

		@Override
		public VortexWebExpectationsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebExpectationsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Expectations Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("create".equals(action))
			getManager().showDialog("Add Expectation", new VortexWebExpectationFormPortlet(generateConfig()));
		else if ("delete".equals(action)) {
			ArrayList<VortexEyeRequest> reqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageExpectationRequest request = nw(VortexEyeManageExpectationRequest.class);
				VortexExpectation exp = nw(VortexExpectation.class);
				exp.setId(row.get("id", Long.class));
				exp.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setExpectation(exp);
				reqs.add(request);
			}
			getManager().showDialog("Delete Expectation",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), reqs, "Delete Expectation", "expectation.jpg").setIconToDelete());
		} else if ("addSso".equals(action)) {

			SsoService ssoService = (SsoService) getManager().getService(SsoService.ID);
			for (Row row : table.getSelectedRows()) {
				long id = row.get("id", Long.class);
				String name = row.get("name", String.class);
				SsoGroup group = nw(SsoGroup.class);
				group.setType(SsoGroup.GROUP_TYPE_EXPECTATION);
				group.setName(name);
				SsoGroupAttribute att = nw(SsoGroupAttribute.class);
				att.setKey("id");
				att.setValue(SH.toString(id));
				att.setType(SsoGroupAttribute.TYPE_TEXT);
				ssoService.addToMemberTree(group, CH.l(att));
			}
		}
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		// TODO Auto-generated method stub

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> items = new ArrayList<WebMenuItem>();
		items.add(new BasicWebMenuLink("Create Expectation", true, "create"));
		items.add(new BasicWebMenuLink("Delete Expectation", true, "delete"));
		items.add(new BasicWebMenuLink("Add To Tree", true, "addSso"));
		//items.add(new BasicWebMenuLink("Update Rule", true, "update"));
		return new BasicWebMenu("", true, items);
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == maskSocket) {
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("id"), msg.getExpectationIds()));
		} else {
			super.onMessage(localSocket, remoteSocket, message);
		}
	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onEyeSnapshotProcessed() {
		metadataColumnManager.onEyeSnapshotProcessed(service.getAgentManager());

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

}
