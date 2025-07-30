package com.vortex.ssoweb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.SsoGroup;
import com.sso.messages.SsoGroupAttribute;
import com.sso.messages.SsoGroupMember;
import com.sso.messages.SsoUpdateEvent;
import com.sso.messages.SsoUser;

public class SsoGroupsTablePortlet extends FastTablePortlet implements SsoPortlet, WebContextMenuListener, WebContextMenuFactory {

	private SsoService service;
	private PortletManager manager;
	private Map<SsoGroup, Row> userToRow;
	protected Logger log;
	private boolean isHistory = false;
	private Object maskSocket;

	public SsoGroupsTablePortlet(PortletConfig config) {
		super(config, null);
		String className = SH.afterFirst("" + this.getClass(), " ");
		log = Logger.getLogger(className);
		manager = getManager();
		service = (SsoService) manager.getService(SsoService.ID);
		LH.info(log, "SsoGroupsTablePortlet created");
		Class[] clazz = { Long.class, Long.class, Integer.class, String.class, String.class };
		String[] ids = { "id", "now", "rev", "name", "type" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Groups");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, manager.getTextFormatter());

		table.addColumn(true, "ssog.name", "name", service.getBasicFormatter()).setWidth(200).setCssColumn("bold");
		table.addColumn(true, "ssog.type", "type", service.getBasicFormatter()).setWidth(120);
		table.addColumn(true, "ssog.now", "now", service.getTimeWebCellFormatter());
		table.addColumn(false, "ssog.id", "id", service.getIdFormatter("GR-"));
		table.addColumn(false, "ssog.rev", "rev", service.getGroupRevisionFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);

		userToRow = new HashMap<SsoGroup, Row>();
		this.maskSocket = addSocket(false, "selection", "Node Selection", true, null, CH.s(NodeSelectionInterPortletMessage.class));
		service.addSsoPortlet(this);
		for (SsoWebGroup group : service.getSsoTree().getGroups())
			add(group);
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		Map<String, String> map = Collections.EMPTY_MAP;//CH.m("process", "mainClass", "user", "userName");//TODO:make static
		if (localSocket == maskSocket) {
			NodeSelectionInterPortletMessage msg = (NodeSelectionInterPortletMessage) message;

			Set<Long> ids = new HashSet<Long>(msg.getSelectedGroupIds());
			if (ids.size() == 0)
				getTable().setExternalFilter(null);
			else
				getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn("id"), ids));
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}

	public void onClosed() {
		service.removeSsoPortlet(this);
		super.onClosed();
	}

	public void add(SsoWebGroup group) {
		userToRow.put(group.getGroup(), addRow(group.getGroupId(), group.getGroup().getNow(), group.getGroup().getRevision(), group.getName(), group.getTypeName()));
	}
	public void update(SsoWebGroup group) {
		if (isHistory) {
			add(group);
		} else {
			Row row = userToRow.get(group);
			if (null == row)
				LH.warning(log, getClass(), " trying to update non-existent user ", group.toString());
			else {
				row.put("id", group.getGroup().getId());
				row.put("now", group.getGroup().getNow());
				row.put("rev", group.getGroup().getRevision());
				row.put("name", group.getGroup().getName());
				row.put("type", group.getTypeName());
			}
		}
	}

	public void remove(SsoGroup user) {
		if (!isHistory) {
			Row row = userToRow.get(user);
			if (null != row) {
				int loc = row.getLocation();
				if (loc > -1) {
					super.removeRow(row);
				} else {
					LH.warning(log, row, " not present in table");
				}
				userToRow.remove(user);
			} else
				LH.warning(log, "Tried to delete non-existent ", user.getClass());
		}
	}

	public static class Builder extends AbstractPortletBuilder<SsoGroupsTablePortlet> {

		public static final String ID = "ssoGroupsTablePortlet";

		public Builder() {
			super(SsoGroupsTablePortlet.class);
		}

		@Override
		public SsoGroupsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoGroupsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Members Table";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		children.add(new BasicWebMenuLink("Edit User(s)", true, "update"));
		children.add(new BasicWebMenuLink("Add User", true, "adduser"));
		children.add(new BasicWebMenuLink("Get User(s) History", true, "history"));
		children.add(new BasicWebMenuLink("Show User Attributes", true, "attributes"));
		children.add(new BasicWebMenuLink("Show Parent Groups", true, "groups"));
		children.add(new BasicWebMenuLink("Show Parent Groups History", true, "groupshistory"));
		WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		Set<Long> ids = new HashSet<Long>();
		for (Row row : selectedRows) {
			ids.add((Long) row.get("id"));
		}
		if ("adduser".equals(action)) {
			NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
			getManager().showDialog("Add User", p);
		} else if ("update".equals(action)) {
			for (Row row : selectedRows) {
				Long id = row.get("id", Long.class);
				NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
				p.setUserToEdit((SsoUser) service.getSsoTree().getGroup(id).getPeer());
				getManager().showDialog("Edit User", p);
			}
			//service.updateGroups(getPortletId(), ids);
		} else if ("history".equals(action)) {
			service.getHistory(getPortletId(), ids);
		} else if ("attributes".equals(action)) {
			//TODO:service.showAttributes(getPortletId(), ids);
		} else if ("groups".equals(action)) {
			//TODO:service.showGroups(getPortletId(), ids, false);
		} else if ("groupshistory".equals(action)) {
			service.sendGroupHistoryRequest(getPortletId(), ids, QuerySsoHistoryRequest.TYPE_GROUP_PARENT_GROUPS);
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {

	}

	public Map<SsoGroup, Row> getUserToRow() {
		return userToRow;
	}

	public void clear() {
		rows.clear();

	}

	public void setIsHistory() {
		isHistory = true;
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {

	}

	@Override
	public void onNewGroup(SsoWebGroup group) {
		add(group);
	}

	@Override
	public void onRemoveGroup(SsoWebGroup group) {
		Row row = userToRow.remove(group);
		if (row != null)
			removeRow(row);

	}

	@Override
	public void onEvent(SsoUpdateEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNewGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
	}

	@Override
	public void onRemoveGroupAttribute(SsoWebGroup group, SsoGroupAttribute attribute) {
	}
	@Override
	public void onNewGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node) {

	}
	@Override
	public void onRemoveGroupMember(SsoGroupMember gm, SsoWebGroup group, SsoWebGroup node) {

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
