package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.json.ShowJsonInterPortletMessage;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.LargeStringWebCellFormatter;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.LongKeyMap;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.UpdateSsoGroupRequest;

public class SsoAttributesTablePortlet extends FastTablePortlet implements WebContextMenuListener, WebContextMenuFactory, SsoPortlet {

	private SsoService service;
	private PortletManager manager;
	private LongKeyMap<Row> attToRow;
	protected Logger log;
	private BasicPortletSocket maskSocket;
	private BasicPortletSocket showJsonSocket;

	public SsoAttributesTablePortlet(PortletConfig config) {
		super(config, null);
		String className = SH.afterFirst("" + this.getClass(), " ");
		log = Logger.getLogger(className);
		manager = getManager();
		service = (SsoService) manager.getService(SsoService.ID);
		LH.info(log, "SsoAttributesTablePortlet created");
		//Class[] clazz = { Long.class, String.class, String.class, Long.class, Integer.class, String.class, Long.class };
		String[] ids = { "id", "key", "value", "update", "rev", "userid", "groupid", "type" };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Attributes");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, manager.getTextFormatter());

		table.addColumn(true, "ssoa.userid", "userid", service.getBasicFormatter()).setWidth(125);
		table.addColumn(true, "ssoa.key", "key", service.getBasicFormatter()).setWidth(125).setCssColumn("blue");
		table.addColumn(true, "ssoa.value", "value", new LargeStringWebCellFormatter().setMaxLength(100));
		table.addColumn(false, "ssoa.update", "update", service.getTimeWebCellFormatter());
		table.addColumn(false, "ssoa.rev", "rev", service.getQuantityFormatter());
		table.addColumn(false, "ssoa.id", "id", service.getIdFormatter("GR-"));
		table.addColumn(false, "ssoa.groupid", "groupid", service.getIdFormatter("GR-"));
		table.addColumn(false, "ssoa.type", "type", service.getBasicFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);

		attToRow = new LongKeyMap<Row>();
		service.addSsoPortlet(this);
		for (SsoWebGroup group : service.getSsoTree().getGroups()) {
			for (SsoGroupAttribute attribute : group.getGroupAttributes().values())
				add(group, attribute);
		}
		this.maskSocket = addSocket(false, "selection", "Node Selection", false, null, CH.s(NodeSelectionInterPortletMessage.class));
		this.showJsonSocket = addSocket(true, "showEventsTree", "Show selected events in Tree", false, CH.s(ShowJsonInterPortletMessage.class), null);
	}
	public void onClosed() {
		super.onClosed();
	}

	public void add(SsoWebGroup group, SsoGroupAttribute attribute) {
		attToRow.put(attribute.getId(), addRow(attribute.getId(), attribute.getKey(), attribute.getValue(), attribute.getNow(), attribute.getRevision(), group.getName(),
				group.getGroupId(), attribute.getType()));
	}

	public void update(SsoGroupAttribute attribute) {
		Row row = attToRow.get(attribute.getId());
		if (null == row)
			LH.warning(log, getClass(), " trying to update non-existent attribute ", attribute.toString());
		else {
			row.put("id", attribute.getId());
			row.put("key", attribute.getKey());
			row.put("value", attribute.getValue());
			row.put("update", attribute.getNow());
			row.put("rev", attribute.getRevision());
			row.put("type", attribute.getType());
		}
	}

	public void remove(SsoGroupAttribute attribute) {
		Row row = attToRow.remove(attribute.getId());
		if (null != row) {
			removeRow(row);
			LH.warning(log, row, " not present in table");
		} else
			LH.warning(log, "Tried to delete non-existent ", attribute);
	}

	public void clear() {
		rows.clear();
	}

	public static class Builder extends AbstractPortletBuilder<SsoAttributesTablePortlet> {

		public static final String ID = "ssoAttributesTablePortlet";

		public Builder() {
			super(SsoAttributesTablePortlet.class);
		}

		@Override
		public SsoAttributesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoAttributesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Member Attributes Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		if (table.getSelectedRows().size() == 1) {
			children.add(new BasicWebMenuLink("Modify Attribute", true, "modify"));
		}
		children.add(new BasicWebMenuLink("Delete Attribute", true, "delete"));
		children.add(new BasicWebMenuLink("Add Attribute", true, "add"));
		//children.add(new BasicWebMenuLink("Revert", true, "revert"));
		//children.add(new BasicWebMenuLink("Show History", true, "history"));
		WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		if ("modify".equals(action) || "add".equals(action)) {
			SsoAttributeFormPortlet portlet = new SsoAttributeFormPortlet(generateConfig());
			if (selectedRows.size() == 1) {
				Row row = selectedRows.get(0);
				portlet.init(row.get("groupid", Long.class), row.get("key", String.class), row.get("value", String.class));
				if ("modify".equals(action))
					portlet.setModifyAttributeId(row.get("id", Long.class));
			}
			getManager().showDialog("Add Attribute", portlet);
		} else if ("delete".equals(action)) {
			for (Row row : table.getSelectedRows()) {//TODO: only one request necessary per user, not per attribute
				UpdateSsoGroupRequest updateRequest = getManager().getGenerator().nw(UpdateSsoGroupRequest.class);
				updateRequest.setGroupId(row.get("groupid", Long.class));
				SsoGroupAttribute attr = getManager().getGenerator().nw(SsoGroupAttribute.class);
				attr.setRevision(65535);
				attr.setGroupId(updateRequest.getGroupId());
				attr.setId(row.get("id", Long.class));
				attr.setKey(row.get("key", String.class));
				updateRequest.setGroupAttributes(CH.l(attr));
				service.sendRequestToBackend(getPortletId(), updateRequest);
			}
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		if (showJsonSocket.hasConnections()) {
			StringBuilder js = new StringBuilder();
			JsonBuilder jsb = new JsonBuilder(js);
			jsb.startList();
			for (Row row : fastWebTable.getSelectedRows()) {
				String value = (String) row.get("value");
				byte type = (Byte) row.get("type");
				switch (type) {
					case SsoGroupAttribute.TYPE_BINARY:
						jsb.addEntryQuoted("bytes[]");
						break;
					case SsoGroupAttribute.TYPE_JSON:
						jsb.addEntryJson(value);
						break;
					case SsoGroupAttribute.TYPE_TEXT:
						jsb.addEntryQuoted(value);
						break;
					default:
						jsb.addEntryQuoted("Unknown sso attribute type, cannot display: " + SH.toString(type));
						break;

				}
			}
			jsb.endList();
			String json = js.toString();
			ShowJsonInterPortletMessage message = new ShowJsonInterPortletMessage(json);
			showJsonSocket.sendMessage(message);
		}
	}

	@Override
	public void onNewGroup(SsoWebGroup group) {
		for (SsoGroupAttribute att : group.getGroupAttributes().values())
			add(group, att);

	}

	@Override
	public void onEvent(SsoUpdateEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGroup(SsoWebGroup group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		Map<String, String> map = CH.m("user", "userid");//TODO:make static
		if (localSocket == maskSocket) {
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;
			Set<Long> groups = new HashSet<Long>(msg.getSelectedGroupIds());
			if (groups.size() == 0)
				getTable().setExternalFilter(null);
			else
				getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("groupid"), groups));
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	@Override
	public void onNewGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		Row row = attToRow.get(attribute.getId());
		if (row != null) {
			update(attribute);
		} else
			add(group, attribute);

	}

	@Override
	public void onRemoveGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
		Row row = attToRow.remove(attribute.getId());
		if (row != null)
			removeRow(row);
	}

	@Override
	public void onNewGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}

}
