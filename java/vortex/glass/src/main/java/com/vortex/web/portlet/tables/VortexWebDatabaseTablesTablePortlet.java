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
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbServer;
import com.vortex.client.VortexClientDbTable;
import com.vortex.web.messages.VortexDbInterPortletMessage;

public class VortexWebDatabaseTablesTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbTable, VortexClientDbTable> {

	private BasicPortletSocket dbIDSocket;
	private BasicPortletSocket sendDbIdSocket;
	public VortexWebDatabaseTablesTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_TABLE);
		// TODO Auto-generated constructor stub
		Class[] clazz = { Long.class, Integer.class, String.class, String.class, String.class, Long.class, Long.class, String.class, String.class, Long.class };
		String[] ids = { TBID, REV, "name", "desc", "comm", "create", DBID, "dbname", HOST, DSID };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Database Tables");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());

		table.addColumn(false, "Database Id", DBID, service.getIdFormatter("DBDB-"));
		table.addColumn(false, "DB Server Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(true, "Database Name", "dbname", service.getBasicFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(150).setCssColumn("bold");
		table.addColumn(false, "DB Table Id", TBID, service.getIdFormatter("DBTB-"));
		table.addColumn(false, "Description", "desc", service.getBasicFormatter());
		table.addColumn(false, "Comments", "comm", service.getBasicFormatter());
		table.addColumn(false, "Create Time", "create", service.getDateTimeWebCellFormatter());

		super.setTable(table);
		this.dbIDSocket = addSocket(false, "dbId", "Send Table ID", false, null, CH.s(VortexDbInterPortletMessage.class));
		this.sendDbIdSocket = addSocket(true, "sendDbId", "Send Table ID", true, CH.s(VortexDbInterPortletMessage.class), null);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			for (VortexClientDbTable dbtable : dbserver.getDbTables())
				onMachineEntityAdded(dbtable);
	}

	@Override
	public WebMenu createMenu(WebTable table) {
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Update Parent Database", true, "update"));
		//children.add(new BasicWebMenuLink("Show Database", true, "database"));
		//children.add(new BasicWebMenuLink("Show Database Objects", true, "objects"));
		//children.add(new BasicWebMenuLink("Show Database Columns", true, "cols"));
		//children.add(new BasicWebMenuLink("Show Database Privileges", true, "privs"));
		//WebMenu r = new BasicWebMenu("test", true, children);
		//return r;
		return null;
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
			Set<Long> ids = new HashSet<Long>();
			AgentDbDatabase a = null;
			List<Byte> builders = new ArrayList<Byte>();
			Set<Long> dbids = new HashSet<Long>();
			for (Row row : selectedRows) {
				ids.add((Long) row.get("id"));
				dbids.add((Long) row.get("dbid"));
			}
			if ("database".equals(action)) {
				builders.add((byte) 0);
			} else if ("objects".equals(action)) {
				builders.add((byte) 2);
			} else if ("privs".equals(action)) {
				builders.add((byte) 3);
			} else if ("cols".equals(action)) {
				builders.add((byte) 5);
				service.displayDbs(ids, builders, this);
				return;
			} else if ("showall".equals(action)) {
				builders.add((byte) 2);
				builders.add((byte) 3);
				builders.add((byte) 5);
			}

			service.displayDbs(dbids, builders, this);
		}

		*/
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabaseTablesTablePortlet> {

		public static final String ID = "databaseTablesTablePortlet";

		public Builder() {
			super(VortexWebDatabaseTablesTablePortlet.class);
		}

		@Override
		public VortexWebDatabaseTablesTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabaseTablesTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Database Tables";
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
	protected Row createAndAddRow(VortexClientDbTable node) {
		VortexClientDbTable node2 = (VortexClientDbTable) node;
		VortexAgentDbTable a = node2.getData();
		return addRow(a.getId(), a.getRevision(), a.getName(), a.getDescription(), a.getComments(), a.getCreateTime(), a.getDatabaseId(), node2.getDbName(), node.getHostName(),
				node.getDatabase().getDbServer().getId());
	}

	@Override
	protected void updateRow(Row row, VortexClientDbTable node) {
		VortexAgentDbTable a = (VortexAgentDbTable) node.getData();
		//row.put("tbid", a.getId());
		row.put("rev", a.getRevision());
		row.put("name", a.getName());
		row.put("desc", a.getDescription());
		row.put("comm", a.getComments());
		row.put("create", a.getCreateTime());
		//row.put("dbid", a.getDatabaseId());
		row.put("host", node.getHostName());
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
				selections.add((Long) addRow.get(TBID));
			sendDbIdSocket.sendMessage(new VortexDbInterPortletMessage(VortexDbInterPortletMessage.TYPE_TABLEID, selections));
		}
	}

}
