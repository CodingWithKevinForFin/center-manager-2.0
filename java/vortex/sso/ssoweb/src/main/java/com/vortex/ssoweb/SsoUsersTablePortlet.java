package com.vortex.ssoweb;

import java.util.ArrayList;
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
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuFactory;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.sso.messages.QuerySsoHistoryRequest;
import com.sso.messages.SsoUser;

public class SsoUsersTablePortlet extends FastTablePortlet implements WebContextMenuListener, WebContextMenuFactory {

	private SsoService service;
	private PortletManager manager;
	private Map<SsoUser, Row> userToRow;
	protected Logger log;
	private boolean isHistory = false;
	private BasicPortletSocket maskSocket;

	public SsoUsersTablePortlet(PortletConfig config) {
		super(config, null);
		String className = SH.afterFirst("" + this.getClass(), " ");
		log = Logger.getLogger(className);
		manager = getManager();
		service = (SsoService) manager.getService(SsoService.ID);
		LH.info(log, "SsoUsersTablePortlet created");
		Class[] clazz = { Long.class, Long.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class,
				String.class, String.class, Byte.class, Byte.class, Integer.class, Integer.class };
		String[] ids = { "id", "gid", "now", "expire", "user", "first", "last", "phone", "psw", "email", "comp", "resetq", "reseta", "status", "encode", "rev", "attempts" };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Users");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, manager.getTextFormatter());

		table.addColumn(true, "sso.user", "user", service.getBasicFormatter()).setWidth(100).setCssColumn("bold");
		table.addColumn(true, "sso.first", "first", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "sso.last", "last", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "sso.exp", "expire", service.getTimeWebCellFormatter());
		table.addColumn(true, "sso.phone", "phone", service.getBasicFormatter()).setWidth(100);
		table.addColumn(true, "sso.email", "email", service.getBasicFormatter()).setWidth(150);
		table.addColumn(true, "sso.comp", "comp", service.getBasicFormatter()).setWidth(100);

		table.addColumn(false, "sso.id", "id", service.getIdFormatter("USR-"));
		table.addColumn(false, "sso.id", "gid", service.getIdFormatter("GR-"));
		table.addColumn(false, "sso.status", "status", service.getUserStatusFormatter());
		table.addColumn(false, "sso.now", "now", service.getTimeWebCellFormatter());
		table.addColumn(false, "sso.resetq", "resetq", service.getBasicFormatter());
		table.addColumn(false, "sso.reseta", "reseta", service.getBasicFormatter());
		table.addColumn(false, "sso.encode", "encode", service.getUserEncodingFormatter());
		table.addColumn(false, "sso.rev", "rev", service.getQuantityFormatter());
		table.addColumn(false, "sso.psw", "psw", service.getBasicFormatter());
		table.addColumn(false, "sso.attempts", "attempts", service.getQuantityFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);

		this.maskSocket = addSocket(true, "selection", "Node Selection", false, CH.s(NodeSelectionInterPortletMessage.class), null);
		userToRow = new HashMap<SsoUser, Row>();
		service.addPortlet(this);
	}
	public void onClosed() {
		super.onClosed();
	}

	public void add(SsoWebGroup user2) {
		SsoUser user = (SsoUser) user2.getPeer();
		try {
			userToRow.put(user,
					addRow(user.getId(), user.getGroupId(), user.getNow(), user.getExpires(), user.getUserName(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(),
							user.getPassword(), user.getEmail(), user.getCompany(), user.getResetQuestion(), user.getResetAnswer(), user.getStatus(), user.getEncodingAlgorithm(),
							user.getRevision(), user.getMaxBadAttempts()));
		} catch (Exception e) {
			throw new RuntimeException("invalid group: " + user2.getGroupId(), e);
		}
	}

	public void update(SsoWebGroup webuser) {
		if (isHistory) {
			add(webuser);
		} else {
			SsoUser user = (SsoUser) webuser.getPeer();
			Row row = userToRow.get(user);
			if (null == row)
				LH.warning(log, getClass(), " trying to update non-existent user ", user.toString());
			else {
				row.put("id", user.getId());
				row.put("gid", user.getGroupId());
				row.put("now", user.getNow());
				row.put("expire", user.getExpires());
				row.put("user", user.getUserName());
				row.put("first", user.getFirstName());
				row.put("last", user.getLastName());
				row.put("phone", user.getPhoneNumber());
				row.put("psw", user.getPassword());
				row.put("email", user.getEmail());
				row.put("comp", user.getCompany());
				row.put("resetq", user.getResetQuestion());
				row.put("reseta", user.getResetAnswer());
				row.put("status", user.getStatus());
				row.put("encode", user.getEncodingAlgorithm());
				row.put("rev", user.getRevision());
				row.put("attempts", user.getMaxBadAttempts());
			}
		}
	}

	public void remove(SsoUser user) {
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

	public static class Builder extends AbstractPortletBuilder<SsoUsersTablePortlet> {

		public static final String ID = "ssoUsersTablePortlet";

		public Builder() {
			super(SsoUsersTablePortlet.class);
		}

		@Override
		public SsoUsersTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new SsoUsersTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Users Table";
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
		children.add(new BasicWebMenuLink("Copy User(s)", true, "copy"));
		children.add(new BasicWebMenuLink("Add User(s)", true, "adduser"));
		//children.add(new BasicWebMenuLink("Get User(s) History", true, "history"));
		children.add(new BasicWebMenuLink("Show User Attributes", true, "attributes"));
		children.add(new BasicWebMenuLink("Show User Groups", true, "groups"));
		children.add(new BasicWebMenuLink("Show User Groups History", true, "groupshistory"));
		children.add(new BasicWebMenuLink("Edit Portlet Entitlements", true, "entitlements"));
		for (PortletBuilder<? extends SsoUserDialog> i : service.getSsoUserDialogs().values())
			children.add(new BasicWebMenuLink(i.getPortletBuilderName(), true, "custom-" + i.getPortletBuilderId()));
		WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		List<Row> selectedRows = table.getSelectedRows();
		Set<Long> ids = new HashSet<Long>();
		for (Row row : selectedRows) {
			ids.add((Long) row.get("gid"));
		}
		SsoWebTreeManager ssoTree = service.getSsoTree();
		if (action.startsWith("custom-")) {
			PortletBuilder<? extends SsoUserDialog> builder = CH.getOrThrow(service.getSsoUserDialogs(), SH.stripPrefix(action, "custom-", false));
			for (long id : ids) {
				SsoUserDialog portlet = builder.buildPortlet(generateConfig());
				SsoWebGroup group = ssoTree.getGroup(id);
				portlet.setUser(group, (SsoUser) group.getPeer());
				getManager().showDialog(portlet.getTitle(), portlet);
			}
		}
		if ("adduser".equals(action)) {
			NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
			getManager().showDialog("Add User", p);
		} else if ("update".equals(action)) {
			for (long id : ids) {
				NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
				SsoWebGroup group = ssoTree.getGroup(id);
				p.setUserToEdit((SsoUser) group.getPeer());
				getManager().showDialog("Edit User", p);
			}
		} else if ("copy".equals(action)) {
			for (long id : ids) {
				NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
				SsoWebGroup group = ssoTree.getGroup(id);
				p.setUserToCopy((SsoUser) group.getPeer());
				getManager().showDialog("Edit User", p);
			}
		} else if ("entitlements".equals(action)) {
			for (long id : ids) {
				NewSsoUserFormPortlet p = new NewSsoUserFormPortlet(generateConfig());
				SsoWebGroup group = ssoTree.getGroup(id);
				getManager().showDialog("Entitlements", new SsoEditEntitlementsDialogPortlet(generateConfig(), group));
			}

		} else if ("history".equals(action)) {
			//TODO:service.getHistory(getPortletId(), ids);
		} else if ("attributes".equals(action)) {
			//TODO:service.showAttributes(getPortletId(), ids);
		} else if ("groups".equals(action)) {
			//TODO:service.showGroups(getPortletId(), ids, true);
		} else if ("groupshistory".equals(action)) {
			service.sendGroupHistoryRequest(getPortletId(), ids, QuerySsoHistoryRequest.TYPE_USER_PARENT_GROUPS);
		}
	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		// TODO Auto-generated method stub

	}

	public Map<SsoUser, Row> getUserToRow() {
		// TODO Auto-generated method stub
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

		if (maskSocket.hasConnections()) {
			NodeSelectionInterPortletMessage msg = new NodeSelectionInterPortletMessage();
			Set<Long> selectedGroupIds = new HashSet<Long>();
			for (Row row : getTable().getSelectedRows()) {
				selectedGroupIds.add(row.get("gid", Long.class));
			}
			msg.setSelectedGroupIds(selectedGroupIds);
			maskSocket.sendMessage(msg);
		}

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
