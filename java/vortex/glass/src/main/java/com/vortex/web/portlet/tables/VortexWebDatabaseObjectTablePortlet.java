package com.vortex.web.portlet.tables;

import java.util.ArrayList;
import java.util.List;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.CH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbObject;
import com.vortex.client.VortexClientDbServer;
import com.vortex.web.messages.VortexDbInterPortletMessage;

public class VortexWebDatabaseObjectTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbObject, VortexClientDbObject> {

	private Object dbIDSocket;
	public VortexWebDatabaseObjectTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_OBJECT);
		// TODO Auto-generated constructor stub
		Class[] clazz = { Long.class, Integer.class, String.class, Byte.class, String.class, Long.class, String.class, String.class, Long.class };
		String[] ids = { OBID, REV, "name", "obtype", "def", DBID, "dbname", HOST, DSID };
		BasicTable inner = new BasicTable(clazz, ids);
		inner.setTitle("Database Objects");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());
		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setWidth(200).setCssColumn("bold");
		table.addColumn(false, "Revision", REV, service.getNumberFormatter());
		table.addColumn(false, "Database Id", DBID, service.getIdFormatter("DBDB-"));
		table.addColumn(false, "DB Server Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(false, "DB Object Id", OBID, service.getIdFormatter("DBDO-"));
		table.addColumn(true, "Definition", "def", service.getBasicFormatter());
		table.addColumn(true, "Obj Type", "obtype", service.getDbObjectTypeFormatter());
		table.addColumn(true, "Database Name", "dbname", service.getBasicFormatter());

		super.setTable(table);
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			for (VortexClientDbObject obj : dbserver.getDbObjects())
				onMachineEntityAdded(obj);
		this.dbIDSocket = addSocket(false, "dbId", "Send Table ID", false, null, CH.s(VortexDbInterPortletMessage.class));
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
	public void onClosed() {
		super.onClosed();
	}

	@Override
	public BasicWebMenu createMenu(WebTable table) {
		List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		children.add(new BasicWebMenuLink("Update Parent Database", true, "update"));
		children.add(new BasicWebMenuLink("Show Database", true, "database"));
		children.add(new BasicWebMenuLink("Show Database Tables", true, "table"));
		children.add(new BasicWebMenuLink("Show Database Columns", true, "cols"));
		children.add(new BasicWebMenuLink("Show Database Privileges", true, "privs"));
		BasicWebMenu r = new BasicWebMenu("test", true, children);
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
			List<Long> ids = new ArrayList<Long>();
			AgentDbDatabase a = null;
			List<Byte> builders = new ArrayList<Byte>();
			for (Row row : selectedRows) {
				ids.add((Long) row.get("id"));
			}
			if ("history".equals(action)) {
				if (null != a)
					service.getHistory(AgentHistoryRequest.TYPE_DATABASE, ids, getPortletId(), false, false);
				return;
			} else if ("update".equals(action)) {
				if (null != a)
					//				service.updateDatabases(ids);
					return;
			} else if ("table".equals(action)) {
				builders.add((byte) 1);
			} else if ("database".equals(action)) {
				builders.add((byte) 0);
			} else if ("privs".equals(action)) {
				builders.add((byte) 3);
			} else if ("cols".equals(action)) {
				builders.add((byte) 4);
			} else if ("showall".equals(action)) {
				builders.add((byte) 1);
				builders.add((byte) 0);
				builders.add((byte) 3);
				builders.add((byte) 4);
			}

			service.displayDbs(ids, builders, this);
		}
		*/

	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabaseObjectTablePortlet> {

		public static final String ID = "databaseObjectsTablePortlet";

		public Builder() {
			super(VortexWebDatabaseObjectTablePortlet.class);
		}

		@Override
		public VortexWebDatabaseObjectTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabaseObjectTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Database Objects";
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
	protected Row createAndAddRow(VortexClientDbObject node) {
		VortexAgentDbObject a = node.getData();
		return addRow(a.getId(), a.getRevision(), a.getName(), a.getType(), a.getDefinition(), a.getDatabaseId(), node.getDbName(), node.getHostName(), node.getDatabase()
				.getDbServer().getId());
	}

	@Override
	protected void updateRow(Row row, VortexClientDbObject node) {
		VortexAgentDbObject a = node.getData();
		//row.put("obid", a.getId());
		row.put("rev", a.getRevision());
		row.put("name", a.getName());
		row.put("obtype", a.getType());
		row.put("def", a.getDefinition());
		//row.put("dbid", a.getDatabaseId());
		row.put("dbname", node.getDbName());
		row.put("host", node.getHostName());
	}

}
