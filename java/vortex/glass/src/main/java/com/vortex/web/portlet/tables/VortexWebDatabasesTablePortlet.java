package com.vortex.web.portlet.tables;

import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbDatabase;
import com.vortex.client.VortexClientDbServer;
import com.vortex.web.messages.VortexDbInterPortletMessage;

public class VortexWebDatabasesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbDatabase, VortexClientDbDatabase> {

	private BasicPortletSocket dbIDSocket;
	private BasicPortletSocket sendDbIdSocket;
	public VortexWebDatabasesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_DATABASE);
		// TODO Auto-generated constructor stub
		Class[] clazz = { Boolean.class, Long.class, Integer.class, String.class, String.class, String.class, String.class, Long.class, String.class, Long.class };
		String[] ids = { "update", DBID, REV, "name", MUID, "url", "psw", NOW, HOST, DSID };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Databases");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());

		table.addColumn(false, "Database Id", DBID, service.getIdFormatter("DBDB-"));
		table.addColumn(false, "DB Server Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(false, "Update Time", NOW, service.getDateTimeWebCellFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(150).setCssColumn("bold");
		table.addColumn(true, "URL", "url", service.getBasicFormatter()).setWidth(600);
		table.addColumn(false, "Password", "psw", service.getBasicFormatter()).setCssColumn("red italic");

		super.setTable(table);
		this.dbIDSocket = addSocket(false, "dbId", "Send Dataserver ID", false, null, CH.s(VortexDbInterPortletMessage.class));
		this.sendDbIdSocket = addSocket(true, "sendDbId", "Send Database ID", true, CH.s(VortexDbInterPortletMessage.class), null);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			for (VortexClientDbDatabase dbDatabase : dbserver.getDatabases())
				onMachineEntityAdded(dbDatabase);
	}

	@Override
	public void onClosed() {
		super.onClosed();
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		return null;
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Update Databases", true, "update"));
		//children.add(new BasicWebMenuLink("Show Database Tables", true, "table"));
		//children.add(new BasicWebMenuLink("Show Database Objects", true, "objects"));
		//children.add(new BasicWebMenuLink("Show Database Columns", true, "cols"));
		//children.add(new BasicWebMenuLink("Show Database Privileges", true, "privs"));
		//children.add(new BasicWebMenuLink("Show All", true, "showall"));
		//WebMenu r = new BasicWebMenu("test", true, children);
		//return r;
	}

	@Override
	public void onContextMenu(WebTable table, String action) {
		/*
		action = parseContext(action);
		List<Row> selectedRows = table.getSelectedRows();
		Set<Long> ids = new HashSet<Long>();
		List<Byte> builders = new ArrayList<Byte>();
		Map<String, String> dbInfo = new HashMap<String, String>();
		Map<String, String> machine = new HashMap<String, String>();
		for (Row row : selectedRows) {
			ids.add((Long) row.get("id"));
			dbInfo.put((String) row.get("url"), (String) row.get("psw"));
			machine.put((String) row.get("url"), (String) row.get("muid"));
		}
		if ("update".equals(action)) {
			if (ids.size() > 0)
				service.updateDatabases(dbInfo, machine, getPortletId());
			return;
		} else if ("table".equals(action)) {
			builders.add((byte) 1);
		} else if ("objects".equals(action)) {
			builders.add((byte) 2);
		} else if ("privs".equals(action)) {
			builders.add((byte) 3);
		} else if ("cols".equals(action)) {
			builders.add((byte) 4);
		} else if ("showall".equals(action)) {
			builders.add((byte) 1);
			builders.add((byte) 2);
			builders.add((byte) 3);
			builders.add((byte) 4);
		}

		service.displayDbs(ids, builders, this);
		*/

	}
	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
		/*
		if ("update".equals(col.getColumnId())) {
			Set<Long> ids = new HashSet<Long>();
			Map<String, String> dbInfo = new HashMap<String, String>();
			Map<String, String> machine = new HashMap<String, String>();
			ids.add((Long) row.get("id"));
			dbInfo.put((String) row.get("url"), (String) row.get("psw"));
			machine.put((String) row.get("url"), (String) row.get("muid"));
			service.updateDatabases(dbInfo, machine, getPortletId());
		}
		*/
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabasesTablePortlet> {

		public static final String ID = "databasesTablePortlet";

		public Builder() {
			super(VortexWebDatabasesTablePortlet.class);
		}

		@Override
		public VortexWebDatabasesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabasesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Databases";
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
	protected Row createAndAddRow(VortexClientDbDatabase node) {
		VortexAgentDbDatabase a = node.getData();
		VortexAgentDbServer server = node.getDbServer().getData();
		return addRow(false, a.getId(), a.getRevision(), a.getName(), server.getMachineUid(), server.getUrl(), calcPassword(server.getPassword()), a.getNow(), node.getHostName(),
				node.getDbServer().getId());

	}

	@Override
	protected void updateRow(Row row, VortexClientDbDatabase node) {
		VortexAgentDbDatabase a = node.getData();
		VortexAgentDbServer server = node.getDbServer().getData();
		row.put("update", row.get("update"));
		//row.put("dbid", a.getId());
		row.put("rev", a.getRevision());
		row.put("name", a.getName());
		//row.put("muid", server.getMachineUid());
		row.put("url", server.getUrl());
		row.put("psw", calcPassword(server.getPassword()));
		row.put("now", a.getNow());
		//row.put("miid", a.getMachineInstanceId());
		row.put("host", node.getHostName());
	}

	private String calcPassword(String password) {
		return "[password not available]";
	}
	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket remoteSocket, InterPortletMessage message) {
		if (localSocket == dbIDSocket) {
			VortexDbInterPortletMessage msg = (VortexDbInterPortletMessage) message;
			getTable().setExternalFilter(new WebTableFilteredSetFilter(getTable().getColumn(msg.getColumnIdForType()), msg.getIds()));
			onVortexRowsChanged();
		} else
			super.onMessage(localSocket, remoteSocket, message);
	}
	@Override
	protected void onVortexRowsChanged() {
		if (!sendDbIdSocket.hasConnections())
			return;
		FastWebTable t = getTable();
		if (sendDbIdSocket.hasConnections()) {
			List<? extends Row> sel = t.hasSelectedRows() ? t.getSelectedRows() : t.getTable().getRows();
			LongSet selections = new LongSet();
			for (Row addRow : sel)
				selections.add((Long) addRow.get(DBID));
			sendDbIdSocket.sendMessage(new VortexDbInterPortletMessage(VortexDbInterPortletMessage.TYPE_DATABASEID, selections));
		}
	}
}
