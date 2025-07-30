package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebContextMenuFactory;
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
import com.f1.vortexcommon.msg.eye.VortexEyeCloudInterface;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageCloudInterfaceRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeRequest;
import com.vortex.client.VortexClientCloudInterface;
import com.vortex.client.VortexClientEntity;
import com.vortex.client.VortexClientMachine;
import com.vortex.client.VortexClientMachineListener;
import com.vortex.web.messages.VortexInterPortletMessage;
import com.vortex.web.portlet.forms.VortexWebCloudInterfaceFormPortlet;
import com.vortex.web.portlet.forms.VortexWebCommentFormPortlet;

public class VortexWebCloudInterfaceTablePortlet extends VortexWebTablePortlet implements WebContextMenuFactory, VortexClientMachineListener {

	private LongKeyMap<Row> rowsById = new LongKeyMap<Row>();
	private BasicPortletSocket ciIdSocket;

	public VortexWebCloudInterfaceTablePortlet(PortletConfig config) {
		super(config, null);
		String[] ids = { "ciid", "desc", "vendor", "user", "data" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("CloudInterfaces");
		SmartTable st = new BasicSmartTable(inner);

		MapWebCellFormatter<Short> vendorFormatter = new MapWebCellFormatter<Short>(getManager().getTextFormatter());
		vendorFormatter.addEntry(VortexEyeCloudInterface.VENDOR_AMAZON_AWS, "Okay", "_cna=portlet_icon_amazon,style.color=blue", "&nbsp;&nbsp;&nbsp;&nbsp;Amazon (TM)");
		vendorFormatter.addEntry(VortexEyeCloudInterface.VENDOR_RACKSPACE, "Okay", "_cna=portlet_icon_rackspace,style.color=blue", "&nbsp;&nbsp;&nbsp;&nbsp;Rackspace (TM)");
		//vendorFormatter.addEntry(VortexEyeCloudInterface.VENDOR_GOOGLE, "Okay", "_cna=portlet_icon_google,style.color=blue", "&nbsp;&nbsp;&nbsp;&nbsp;Google (TM)");
		vendorFormatter.setDefaultWidth(140).lockFormatter();

		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());
		table.addColumn(false, "Id", "ciid", service.getIdFormatter("CI-"));
		table.addColumn(true, "Vendor", "vendor", vendorFormatter);
		table.addColumn(true, "Description", "desc", service.getBasicFormatter());
		table.addColumn(true, "User Name", "user", service.getBasicFormatter());
		table.setMenuFactory(this);
		agentManager.addMachineListener(this);
		setTable(table);
		//metadataColumnManager = new VortexWebMetadataColumnsManager(getTable(), VortexAgentEntity.TYPE_BACKUP, "mdata");

		this.ciIdSocket = addSocket(true, "cIID", "Send Clound Interface Id", true, CH.s(VortexInterPortletMessage.class), null);

		for (VortexClientCloudInterface bp : service.getAgentManager().getCloudInterfaces())
			onMachineEntityAdded(bp);
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);

		List<Row> srs = fastWebTable.getSelectedRows();
		Set<Long> selected = new LongSet();
		for (Row r : srs)
			selected.add((Long) r.get("ciid"));

		VortexInterPortletMessage m = new VortexInterPortletMessage("ciid", 0l, null, null, selected);
		ciIdSocket.sendMessage(m);
	}

	@Override
	public void onMachineAdded(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineUpdated(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineStale(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineRemoved(VortexClientMachine machine) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMachineEntityAdded(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_CLOUD_INTERFACE) {
			addVortexRow((VortexClientCloudInterface) node);
		}
	}

	private void addVortexRow(VortexClientCloudInterface node) {
		VortexEyeCloudInterface data = node.getData();
		rowsById.put(data.getId(), addRow(data.getId(), data.getDescription(), data.getCloudVendorType(), data.getUserName(), data));
	}
	private void removeVortexRow(VortexClientCloudInterface node) {
		VortexEyeCloudInterface data = node.getData();
		removeRow(rowsById.remove(data.getId()));
	}
	private void updateVortexRow(VortexClientCloudInterface node) {
		VortexEyeCloudInterface data = node.getData();
		Row row = rowsById.get(node.getId());
		row.put("desc", data.getDescription());
		row.put("vendor", data.getCloudVendorType());
		row.put("user", data.getUserName());
		row.put("data", data);
	}

	@Override
	public void onMachineEntityUpdated(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_CLOUD_INTERFACE) {
			updateVortexRow((VortexClientCloudInterface) node);
		}

	}

	@Override
	public void onMachineEntityRemoved(VortexClientEntity<?> node) {
		if (node.getType() == VortexAgentEntity.TYPE_CLOUD_INTERFACE) {
			removeVortexRow((VortexClientCloudInterface) node);
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		BasicWebMenu r = new BasicWebMenu();
		if (table.hasSelectedRows()) {
			r.addChild(new BasicWebMenuLink("Edit Cloud Interface", true, "edit"));
			r.addChild(new BasicWebMenuLink("Copy Cloud Interface", true, "copy"));
			r.addChild(new BasicWebMenuLink("Remove Cloud Interface", true, "remove"));
		}
		r.addChild(new BasicWebMenuLink("Add Cloud Interface", true, "add"));
		return r;
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
		if ("add".equals(action)) {
			getManager().showDialog("Add Cloud Interface", new VortexWebCloudInterfaceFormPortlet(generateConfig()), 850, 700);
		} else if ("copy".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long ciid = row.get("ciid", Long.class);
				VortexClientCloudInterface bp = service.getAgentManager().getCloudInterface(ciid);
				if (bp != null) {
					VortexWebCloudInterfaceFormPortlet p = new VortexWebCloudInterfaceFormPortlet(generateConfig());
					p.setCloudInterfaceToCopy(bp);
					getManager().showDialog("Copy Cloud Interface", p, 850, 700);
				}
			}
		} else if ("edit".equals(action)) {
			for (Row row : table.getSelectedRows()) {
				long ciid = row.get("ciid", Long.class);
				VortexClientCloudInterface bp = service.getAgentManager().getCloudInterface(ciid);
				if (bp != null) {
					VortexWebCloudInterfaceFormPortlet p = new VortexWebCloudInterfaceFormPortlet(generateConfig());
					p.setCloudInterfaceToEdit(bp);
					getManager().showDialog("Edit Cloud Interface", p, 850, 700);
				}
			}
		} else if ("remove".equals(action)) {
			List<VortexEyeRequest> eyeReqs = new ArrayList<VortexEyeRequest>();
			for (Row row : table.getSelectedRows()) {
				VortexEyeManageCloudInterfaceRequest request = nw(VortexEyeManageCloudInterfaceRequest.class);
				VortexEyeCloudInterface ci = nw(VortexEyeCloudInterface.class);
				ci.setId(row.get("ciid", Long.class));
				ci.setRevision(VortexAgentEntity.REVISION_DONE);
				request.setCloudInterface(ci);
				eyeReqs.add(request);
			}
			getManager().showDialog("Delete Cloud Iterface",
					new VortexWebCommentFormPortlet(generateConfig(), getPortletId(), eyeReqs, "Delete Could Interface", "cloud.jpg").setIconToDelete());
		}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebCloudInterfaceTablePortlet> {

		public static final String ID = "CloudInterfacesTablePortlet";

		public Builder() {
			super(VortexWebCloudInterfaceTablePortlet.class);
		}

		@Override
		public VortexWebCloudInterfaceTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebCloudInterfaceTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Cloud Interface Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onEyeDisconnected() {
		clearRows();
	}

}
