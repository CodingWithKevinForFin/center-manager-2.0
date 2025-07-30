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
import com.f1.vortexcommon.msg.agent.VortexAgentNetLink;
import com.vortex.client.VortexClientNetLink;

public class VortexWebMachineNetLinksTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentNetLink, VortexClientNetLink> {

	public VortexWebMachineNetLinksTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_NET_LINK);
		// TODO Auto-generated constructor stub
		Class[] clazz = { String.class, Long.class, Integer.class, String.class, String.class, String.class, Long.class, String.class, Short.class, Long.class, Long.class,
				Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class, Long.class };
		String[] ids = { HOST, NLID, REV, "broad", "mac", "trans", "mtu", "name", "state", "now", "rxp", "rxe", "rxd", "rxo", "rxm", "txp", "txe", "txd", "txc", "txcln", MIID,
				"packets", "errors", "dropped" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Net Links");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Machine ID", MIID, service.getIdFormatter("MA-"));
		table.addColumn(false, "Net Link Id", NLID, service.getIdFormatter("NL-"));
		table.addColumn(true, "MAC Address", "mac", service.getBasicFormatter()).setWidth(120);
		table.addColumn(true, "Broadcast", "broad", service.getBasicFormatter()).setWidth(120);
		table.addColumn(true, "MTU", "mtu", service.getNumberFormatter()).setWidth(45);
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(40);
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());

		table.addColumn(true, "Status", "state", service.getNetLinkStateFormatter());
		table.addColumn(true, "Total Packets", "packets", service.getNumberFormatter());
		table.addColumn(true, "Errors", "errors", service.getWarningNumberFormatter());
		table.addColumn(true, "Dropped", "dropped", service.getWarningNumberFormatter());
		table.addColumn(true, "Rx Overruns", "rxo", service.getWarningNumberFormatter());
		table.addColumn(true, "Multicast Received ", "rxm", service.getNumberFormatter());
		table.addColumn(true, "Trans Carrier", "txc", service.getNumberFormatter());
		table.addColumn(true, "Collisions", "txcln", service.getWarningNumberFormatter());
		table.addColumn(false, "Received Pkts", "rxp", service.getNumberFormatter());
		table.addColumn(false, "Rx errors", "rxe", service.getWarningNumberFormatter());
		table.addColumn(false, "Rx dropped", "rxd", service.getWarningNumberFormatter());
		table.addColumn(false, "Transmitted Pkts", "txp", service.getNumberFormatter());
		table.addColumn(false, "Tx errors", "txe", service.getWarningNumberFormatter());
		table.addColumn(false, "Tx dropped", "txd", service.getWarningNumberFormatter());
		table.addColumn(false, "Transmission Details", "trans", service.getBasicFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
	}

	@Override
	public void onClosed() {
		super.onClosed();
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

	}

	public static class Builder extends AbstractPortletBuilder<VortexWebMachineNetLinksTablePortlet> {

		public static final String ID = "netLinksTablePortlet";

		public Builder() {
			super(VortexWebMachineNetLinksTablePortlet.class);
		}

		@Override
		public VortexWebMachineNetLinksTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebMachineNetLinksTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Net Links";
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
	protected Row createAndAddRow(VortexClientNetLink node) {
		VortexAgentNetLink a = (VortexAgentNetLink) node.getData();
		return addRow(node.getHostName(), a.getId(), a.getRevision(), a.getBroadcast(), a.getMac(), a.getTransmissionDetails(), a.getMtu(), a.getName(), a.getState(), a.getNow(),
				a.getRxPackets(), a.getRxErrors(), a.getRxDropped(), a.getRxOverrun(), a.getRxMulticast(), a.getTxPackets(), a.getTxErrors(), a.getTxDropped(), a.getTxCarrier(),
				a.getTxCollsns(), a.getMachineInstanceId(), calcPackets(a), calcErrors(a), calcDropped(a));
	}

	@Override
	protected void updateRow(Row row, VortexClientNetLink node) {
		VortexAgentNetLink a = (VortexAgentNetLink) node.getData();
		row.put("host", node.getHostName());
		row.put("nlid", a.getId());
		row.put("rev", a.getRevision());
		row.put("broad", a.getBroadcast());
		row.put("mac", a.getMac());
		row.put("trans", a.getTransmissionDetails());
		row.put("mtu", a.getMtu());
		row.put("name", a.getName());
		row.put("state", a.getState());
		row.put("now", a.getNow());
		row.put("rxp", a.getRxPackets());
		row.put("rxe", a.getRxErrors());
		row.put("rxd", a.getRxDropped());
		row.put("rxo", a.getRxOverrun());
		row.put("rxm", a.getRxMulticast());
		row.put("txp", a.getTxPackets());
		row.put("txe", a.getTxErrors());
		row.put("txd", a.getTxDropped());
		row.put("txc", a.getTxCarrier());
		row.put("txcln", a.getTxCollsns());
		row.put("miid", a.getMachineInstanceId());
		row.put("packets", calcPackets(a));
		row.put("errors", calcErrors(a));
		row.put("dropped", calcDropped(a));
	}

	private long calcDropped(VortexAgentNetLink a) {
		return a.getRxDropped() + a.getTxDropped();
	}

	private long calcErrors(VortexAgentNetLink a) {
		return a.getRxErrors() + a.getTxErrors();
	}

	private long calcPackets(VortexAgentNetLink a) {
		return a.getRxPackets() + a.getTxPackets();
	}

}
