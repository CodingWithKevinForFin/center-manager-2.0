package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

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
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.BitMaskDescription;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbPrivilege;
import com.vortex.client.VortexClientDbServer;
import com.vortex.web.messages.VortexDbInterPortletMessage;

public class VortexWebDatabasePrivilegesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbPrivilege, VortexClientDbPrivilege> {

	BitMaskDescription bitMask = new BitMaskDescription("");
	final private BasicPortletSocket dbIDSocket;

	public VortexWebDatabasePrivilegesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_PRIVILEDGE);
		// TODO Auto-generated constructor stub
		//Class[] clazz = { Long.class, Integer.class, String.class, Long.class, String.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
		//Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class, Boolean.class,
		//Boolean.class, Boolean.class, Boolean.class, Boolean.class, Long.class, String.class, Long.class, String.class, Long.class };
		String[] ids = { "pvid", REV, USER, "pmtype", NOW, "SELECT", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "REFERENCES", "INDEX", "ALTER", "CREATE_TEMP_TABLES",
				"CREATE_VIEW", "SHOW_VIEW", "CREATE_ROUTINE", "EVENT", "TRIGGER", "LOCK_TABLES", "EXECUTE", "ALTER_ROUTINE", "OTHER", DBID, "dbname", HOST, DSID };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Database Privileges");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		bitMask.define(VortexAgentDbPrivilege.SELECT, "SELECT");
		bitMask.define(VortexAgentDbPrivilege.INSERT, "INSERT");
		bitMask.define(VortexAgentDbPrivilege.UPDATE, "UPDATE");
		bitMask.define(VortexAgentDbPrivilege.DELETE, "DELETE");
		bitMask.define(VortexAgentDbPrivilege.CREATE, "CREATE");
		bitMask.define(VortexAgentDbPrivilege.DROP, "DROP");
		bitMask.define(VortexAgentDbPrivilege.REFERENCES, "REFERENCES");
		bitMask.define(VortexAgentDbPrivilege.INDEX, "INDEX");
		bitMask.define(VortexAgentDbPrivilege.ALTER, "ALTER");
		bitMask.define(VortexAgentDbPrivilege.CREATE_TEMP_TABLES, "CREATE_TEMP_TABLES");
		bitMask.define(VortexAgentDbPrivilege.CREATE_VIEW, "CREATE_VIEW");
		bitMask.define(VortexAgentDbPrivilege.SHOW_VIEW, "SHOW_VIEW");
		bitMask.define(VortexAgentDbPrivilege.CREATE_ROUTINE, "CREATE_ROUTINE");
		bitMask.define(VortexAgentDbPrivilege.EVENT, "EVENT");
		bitMask.define(VortexAgentDbPrivilege.TRIGGER, "TRIGGER");
		bitMask.define(VortexAgentDbPrivilege.LOCK_TABLES, "LOCK_TABLES");
		bitMask.define(VortexAgentDbPrivilege.EXECUTE, "EXECUTE");
		bitMask.define(VortexAgentDbPrivilege.ALTER_ROUTINE, "ALTER_ROUTINE");
		bitMask.define(VortexAgentDbPrivilege.OTHER, "OTHER");

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "User", USER, service.getUserFormatter()).setWidth(120).setCssColumn("bold");
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());

		table.addColumn(false, "Database Id", DBID, service.getIdFormatter("DBDB-"));
		table.addColumn(false, "DB Server Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(true, "Database Name", "dbname", service.getBasicFormatter());
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Permissions", "pmtype", service.getBasicFormatter()).setWidth(300);

		table.addColumn(false, "SELECT", "SELECT", service.getBasicFormatter());
		table.addColumn(false, "INSERT", "INSERT", service.getBasicFormatter());
		table.addColumn(false, "UPDATE", "UPDATE", service.getBasicFormatter());
		table.addColumn(false, "DELETE", "DELETE", service.getBasicFormatter());
		table.addColumn(false, "CREATE", "CREATE", service.getBasicFormatter());
		table.addColumn(false, "DROP", "DROP", service.getBasicFormatter());
		table.addColumn(false, "REFERENCES", "REFERENCES", service.getBasicFormatter());
		table.addColumn(false, "INDEX", "INDEX", service.getBasicFormatter());
		table.addColumn(false, "ALTER", "ALTER", service.getBasicFormatter());
		table.addColumn(false, "CREATE_TEMP_TABLES", "CREATE_TEMP_TABLES", service.getBasicFormatter());
		table.addColumn(false, "CREATE_VIEW", "CREATE_VIEW", service.getBasicFormatter());
		table.addColumn(false, "SHOW_VIEW", "SHOW_VIEW", service.getBasicFormatter());
		table.addColumn(false, "CREATE_ROUTINE", "CREATE_ROUTINE", service.getBasicFormatter());
		table.addColumn(false, "EVENT", "EVENT", service.getBasicFormatter());
		table.addColumn(false, "TRIGGER", "TRIGGER", service.getBasicFormatter());
		table.addColumn(false, "LOCK_TABLES", "LOCK_TABLES", service.getBasicFormatter());
		table.addColumn(false, "EXECUTE", "EXECUTE", service.getBasicFormatter());
		table.addColumn(false, "ALTER_ROUTINE", "ALTER_ROUTINE", service.getBasicFormatter());
		table.addColumn(false, "OTHER", "OTHER", service.getBasicFormatter());
		table.addColumn(false, "Id", "pvid", service.getIdFormatter("DBPV-"));
		super.setTable(table);
		this.dbIDSocket = addSocket(false, "sendDbId", "Send Dataserver ID", false, null, CH.s(VortexDbInterPortletMessage.class));
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			for (VortexClientDbPrivilege priv : dbserver.getDbPrivileges())
				onMachineEntityAdded(priv);
	}
	@Override
	public void onClosed() {
		super.onClosed();
	}

	private boolean mask(int mask, int type) {
		return (mask & type) != 0;
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		children.add(new BasicWebMenuLink("Show Database", true, "database"));
		children.add(new BasicWebMenuLink("Show Database Tables", true, "table"));
		children.add(new BasicWebMenuLink("Show Database Objects", true, "objects"));
		children.add(new BasicWebMenuLink("Show Database Columns", true, "cols"));
		WebMenu r = new BasicWebMenu("test", true, children);
		return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		/*
		action = parseContext(action);
		List<Row> selectedRows = table.getSelectedRows();
		if ("update".equals(action)) {
			Set<Long> ids = new HashSet<Long>();
			Map<String, String> dbInfo = new HashMap<String, String>();
			Map<String, String> machine = new HashMap<String, String>();
			for (Row row : selectedRows) {
				ids.add((Long) row.get("id"));
				dbInfo.put((String) row.get("url"), (String) row.get("psw"));
				machine.put((String) row.get("url"), (String) row.get("muid"));
			}
			service.updateDatabases(dbInfo, machine, getPortletId());
		} else {
			Set<Long> dbids = new HashSet<Long>();
			AgentDbDatabase a = null;
			List<Byte> builders = new ArrayList<Byte>();
			for (Row row : selectedRows) {
				dbids.add((Long) row.get("dbid"));
			}
			if ("table".equals(action)) {
				builders.add((byte) 1);
			} else if ("objects".equals(action)) {
				builders.add((byte) 2);
			} else if ("database".equals(action)) {
				builders.add((byte) 0);
			} else if ("cols".equals(action)) {
				builders.add((byte) 4);
			} else if ("showall".equals(action)) {
				builders.add((byte) 1);
				builders.add((byte) 2);
				builders.add((byte) 0);
				builders.add((byte) 4);

			}

			service.displayDbs(dbids, builders, this);
		}
		*/
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		if ("update".equals(col.getColumnId())) {
			boolean show = !row.get("update", Boolean.class);
			row.put("update", show);
			List<Long> ids = new ArrayList<Long>();
			ids.add((Long) row.get("id"));
			//			service.updateDatabases(ids);
		}
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabasePrivilegesTablePortlet> {

		public static final String ID = "databasePrivilegesTablePortlet";

		public Builder() {
			super(VortexWebDatabasePrivilegesTablePortlet.class);
		}

		@Override
		public VortexWebDatabasePrivilegesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabasePrivilegesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Database Privileges";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		super.onSelectedChanged(fastWebTable);
		// TODO Auto-generated method stub

	}
	@Override
	protected Row createAndAddRow(VortexClientDbPrivilege node) {
		VortexAgentDbPrivilege a = node.getData();
		int t = a.getType();
		return addRow(a.getId(), a.getRevision(), a.getUser(), bitMask.toDetailedString(t), a.getNow(), mask(VortexAgentDbPrivilege.SELECT, t),
				mask(VortexAgentDbPrivilege.INSERT, t), mask(VortexAgentDbPrivilege.UPDATE, t), mask(VortexAgentDbPrivilege.DELETE, t), mask(VortexAgentDbPrivilege.CREATE, t),
				mask(VortexAgentDbPrivilege.DROP, t), mask(VortexAgentDbPrivilege.REFERENCES, t), mask(VortexAgentDbPrivilege.INDEX, t), mask(VortexAgentDbPrivilege.ALTER, t),
				mask(VortexAgentDbPrivilege.CREATE_TEMP_TABLES, t), mask(VortexAgentDbPrivilege.CREATE_VIEW, t), mask(VortexAgentDbPrivilege.SHOW_VIEW, t),
				mask(VortexAgentDbPrivilege.CREATE_ROUTINE, t), mask(VortexAgentDbPrivilege.EVENT, t), mask(VortexAgentDbPrivilege.TRIGGER, t),
				mask(VortexAgentDbPrivilege.LOCK_TABLES, t), mask(VortexAgentDbPrivilege.EXECUTE, t), mask(VortexAgentDbPrivilege.ALTER_ROUTINE, t),
				mask(VortexAgentDbPrivilege.OTHER, t), a.getDatabaseId(), node.getDbName(), node.getHostName(), node.getDatabase().getDbServer().getId());
	}
	@Override
	protected void updateRow(Row row, VortexClientDbPrivilege node) {
		VortexClientDbPrivilege node2 = (VortexClientDbPrivilege) node;
		VortexAgentDbPrivilege a = node.getData();
		int t = a.getType();
		row.put("pvid", a.getId());
		row.put("rev", a.getRevision());
		row.put("user", a.getUser());
		row.put("pmtype", bitMask.toDetailedString(t));
		row.put("now", a.getNow());
		row.put("SELECT", mask(VortexAgentDbPrivilege.SELECT, t));
		row.put("INSERT", mask(VortexAgentDbPrivilege.INSERT, t));
		row.put("UPDATE", mask(VortexAgentDbPrivilege.UPDATE, t));
		row.put("DELETE", mask(VortexAgentDbPrivilege.DELETE, t));
		row.put("CREATE", mask(VortexAgentDbPrivilege.CREATE, t));
		row.put("DROP", mask(VortexAgentDbPrivilege.DROP, t));
		row.put("REFERENCES", mask(VortexAgentDbPrivilege.REFERENCES, t));
		row.put("INDEX", mask(VortexAgentDbPrivilege.INDEX, t));
		row.put("ALTER", mask(VortexAgentDbPrivilege.ALTER, t));
		row.put("CREATE_TEMP_TABLES", mask(VortexAgentDbPrivilege.CREATE_TEMP_TABLES, t));
		row.put("CREATE_VIEW", mask(VortexAgentDbPrivilege.CREATE_VIEW, t));
		row.put("SHOW_VIEW", mask(VortexAgentDbPrivilege.SHOW_VIEW, t));
		row.put("CREATE_ROUTINE", mask(VortexAgentDbPrivilege.CREATE_ROUTINE, t));
		row.put("EVENT", mask(VortexAgentDbPrivilege.EVENT, t));
		row.put("TRIGGER", mask(VortexAgentDbPrivilege.TRIGGER, t));
		row.put("LOCK_TABLES", mask(VortexAgentDbPrivilege.LOCK_TABLES, t));
		row.put("EXECUTE", mask(VortexAgentDbPrivilege.EXECUTE, t));
		row.put("ALTER_ROUTINE", mask(VortexAgentDbPrivilege.ALTER_ROUTINE, t));
		row.put("OTHER", mask(VortexAgentDbPrivilege.OTHER, t));
		//row.put("dbid", a.getDatabaseId());
		row.put("dbname", node2.getDbName());
		row.put("host", node2.getHostName());
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == dbIDSocket) {
			VortexDbInterPortletMessage msg = (VortexDbInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(msg.getColumnIdForType()), msg.getIds()));
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
}
