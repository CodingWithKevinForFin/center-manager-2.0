package com.vortex.ssoweb;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.vortex.ssoweb.NodeSelectionInterPortletMessage.Mask;

public class SsoMaskingTablePortlet extends FastTablePortlet {

	private SsoService service;
	private BasicPortletSocket socket;

	public SsoMaskingTablePortlet(PortletConfig config) {
		super(config, null);
		Class[] clazz = { String.class, String.class, String.class, String.class };
		String[] ids = { "type", "name", "key", "mask" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Group Masking Inheritance");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		service = (SsoService) getManager().getService(SsoService.ID);
		table.addColumn(true, "ssog.type", "type", service.getBasicFormatter());
		table.addColumn(true, "ssog.name", "name", service.getBasicFormatter());
		table.addColumn(true, "ssog.key", "key", service.getBasicFormatter());
		table.addColumn(true, "ssog.mask", "mask", service.getBasicFormatter());
		this.socket = addSocket(false, "selection", "Node Selection", false, null, CH.s(NodeSelectionInterPortletMessage.class));
		super.setTable(table);
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == socket) {
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			getTable().clear();
			for (Mask i : msg.getMasks()) {
				while (i != null) {
					addRow(i.type, i.name, i.key, i.mask);
					i = i.next;
				}
			}
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	public static class Builder extends AbstractPortletBuilder<SsoMaskingTablePortlet> {

		public Builder() {
			super(SsoMaskingTablePortlet.class);
		}

		public static final String ID = "ssoMaskingTablePortlet";

		@Override
		public SsoMaskingTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoMaskingTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Masks table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

}
