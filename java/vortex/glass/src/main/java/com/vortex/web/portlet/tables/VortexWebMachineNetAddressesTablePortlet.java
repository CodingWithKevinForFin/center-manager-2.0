package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.agent.VortexAgentNetAddress;
import com.vortex.client.VortexClientNetAddress;

public class VortexWebMachineNetAddressesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentNetAddress, VortexClientNetAddress> {

	public VortexWebMachineNetAddressesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_NET_ADDRESS);
		// TODO Auto-generated constructor stub
		Class[] clazz = { String.class, Long.class, Integer.class, String.class, String.class, String.class, Byte.class, Byte.class, Long.class, Long.class };
		String[] ids = { HOST, ADID, REV, "broad", "link", "addr", "type", "scope", NOW, MIID };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Net Addresses");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());

		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(false, "Addr Id", ADID, service.getIdFormatter("AD-"));
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Broadcast", "broad", service.getBasicFormatter());
		table.addColumn(true, "Name", "link", service.getBasicFormatter());
		table.addColumn(true, "IP Address", "addr", service.getBasicFormatter());
		table.addColumn(true, "Type", "type", service.getNetAddressTypeFormatter());
		table.addColumn(true, "Scope", "scope", service.getNetAddressScopeFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		children.add(new BasicWebMenuLink("Show History", true, "history"));
		WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		/*
		action = parseContext(action);
		List<Row> selectedRows = table.getSelectedRows();
		if ("history".equals(action)) {
			List<Long> ids = new ArrayList<Long>();
			AgentNetAddress a = null;
			for (Row row : selectedRows) {
				a = (AgentNetAddress) getIdToRevision().get(row.get("id"));
				ids.add(a.getId());
			}
			if (null != a)
				service.getHistory(AgentHistoryRequest.TYPE_PROCESS, ids, getPortletId(), false, false);
		}
		*/

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineNetAddressesTablePortlet> {

		public static final String ID = "netAddressesTablePortlet";

		public Builder() {
			super(VortexWebMachineNetAddressesTablePortlet.class);
		}

		@Override
		public VortexWebMachineNetAddressesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineNetAddressesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Net Addresses";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
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
	protected Row createAndAddRow(VortexClientNetAddress node) {
		VortexClientNetAddress address = (VortexClientNetAddress) node;
		VortexAgentNetAddress a = address.getData();
		return addRow(node.getHostName(), a.getId(), a.getRevision(), a.getBroadcast(), a.getLinkName(), a.getAddress(), a.getType(), a.getScope(), a.getNow(),
				a.getMachineInstanceId());
	}

	@Override
	protected void updateRow(Row row, VortexClientNetAddress node) {
		VortexClientNetAddress address = (VortexClientNetAddress) node;
		VortexAgentNetAddress a = address.getData();
		row.put(HOST, node.getHostName());
		row.put(ADID, a.getId());
		row.put(REV, a.getRevision());
		row.put("broad", a.getBroadcast());
		row.put("link", a.getLinkName());
		row.put("addr", a.getAddress());
		row.put("type", a.getType());
		row.put("scope", a.getScope());
		row.put(NOW, a.getNow());
		row.put(MIID, a.getMachineInstanceId());
	}

}
