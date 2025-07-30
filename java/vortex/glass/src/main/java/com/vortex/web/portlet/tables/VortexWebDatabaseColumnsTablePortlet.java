package com.vortex.web.portlet.tables;

import com.f1.base.Row;
import com.f1.suite.web.menu.impl.BasicWebMenu;
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
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.vortex.client.VortexClientDbColumn;
import com.vortex.client.VortexClientDbServer;
import com.vortex.web.messages.VortexDbInterPortletMessage;

public class VortexWebDatabaseColumnsTablePortlet extends VortexWebMachineAbstractTablePortlet<VortexAgentDbColumn, VortexClientDbColumn> {

	private BitMaskDescription bitMask = new BitMaskDescription("Columns");
	final private BasicPortletSocket dbIDSocket;

	public VortexWebDatabaseColumnsTablePortlet(PortletConfig config) {
		super(config, VortexAgentEntity.TYPE_DB_COLUMN);
		String[] ids = { CLID, REV, "name", "cltype", "desc", "mask", "size", "prec", "scale", "permv", "pos", "comm", DBID, "null", "unsign", TBID, "tbname", "dbname", HOST, DSID };
		BasicTable inner = new BasicTable(ids);
		inner.setTitle("Database Columns");
		SmartTable st = new BasicSmartTable(inner);
		FastWebTable table = new FastWebTable(st, getManager().getTextFormatter());

		bitMask.define(VortexAgentDbColumn.MASK_NULLABLE, "NULLABLE");
		bitMask.define(VortexAgentDbColumn.MASK_UNSIGNED, "UNSIGNED");

		table.addColumn(true, "Name", "name", service.getBasicFormatter()).setCssColumn("bold");
		table.addColumn(true, "Host", HOST, service.getHostnameFormatter());

		table.addColumn(false, "Revision", REV, service.getNumberFormatter());

		table.addColumn(false, "Database Id", DBID, service.getIdFormatter("DBDB-"));
		table.addColumn(false, "DB Server Id", DSID, service.getIdFormatter("DBDS-"));
		table.addColumn(false, "DB Table Id", TBID, service.getIdFormatter("DBTB-"));
		table.addColumn(false, "DB Column Id", CLID, service.getIdFormatter("DBCL-"));

		table.addColumn(true, "Database Name", "dbname", service.getBasicFormatter());
		table.addColumn(true, "Description", "desc", service.getBasicFormatter());
		table.addColumn(true, "Comments", "comm", service.getBasicFormatter());
		table.addColumn(true, "Table Name", "tbname", service.getBasicFormatter());
		table.addColumn(true, "Column Type", "cltype", service.getDbColumnTypeFormatter());

		table.addColumn(true, "Mask", "mask", service.getBasicFormatter());
		table.addColumn(true, "Size", "size", service.getBasicFormatter());
		table.addColumn(true, "Precision", "prec", service.getBasicFormatter());
		table.addColumn(true, "Scale", "scale", service.getBasicFormatter());
		table.addColumn(true, "Permissible Value", "permv", service.getBasicFormatter());
		table.addColumn(true, "Position", "pos", service.getBasicFormatter());
		table.addColumn(true, "Nullable", "null", service.getBasicFormatter());
		table.addColumn(true, "Unsigned", "unsign", service.getBasicFormatter());

		super.setTable(table);
		this.dbIDSocket = addSocket(false, "sendDbId", "Send Dataserver ID", false, null, CH.s(VortexDbInterPortletMessage.class));
		//table.addMenuListener(this);
		table.setMenuFactory(this);
		for (VortexClientDbServer dbserver : service.getAgentManager().getDbServers())
			for (VortexClientDbColumn column : dbserver.getDbColumns())
				onMachineEntityAdded(column);
	}

	private boolean mask(byte mask, byte val) {
		return (mask & val) != 0;
	}

	@Override
	public BasicWebMenu createMenu(WebTable table) {
		return null;
		//List<WebMenuItem> children = new ArrayList<WebMenuItem>();
		//children.add(new BasicWebMenuLink("Update Parent Database", true, "update"));
		//children.add(new BasicWebMenuLink("Show Database", true, "database"));
		//children.add(new BasicWebMenuLink("Show Database Tables", true, "table"));
		//children.add(new BasicWebMenuLink("Show Database Objects", true, "objects"));
		//children.add(new BasicWebMenuLink("Show Database Privileges", true, "privs"));
		//children.add(new BasicWebMenuLink("Show All", true, "showall"));
		//BasicWebMenu r = new BasicWebMenu("test", true, children);
		//return r;
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
			Set<Long> tbids = new HashSet<Long>();
			List<Byte> builders = new ArrayList<Byte>();
			for (Row row : selectedRows) {
				dbids.add((Long) row.get("dbid"));
				tbids.add((Long) row.get("tbid"));
			}
			if ("table".equals(action)) {
				builders.add((byte) 6);
				service.displayDbs(tbids, builders, this);
				return;
			} else if ("database".equals(action)) {
				builders.add((byte) 0);
			} else if ("objects".equals(action)) {
				builders.add((byte) 2);
			} else if ("privs".equals(action)) {
				builders.add((byte) 3);
			} else if ("showall".equals(action)) {
				builders.add((byte) 6);
				service.displayDbs(tbids, builders, this);
				builders.clear();
				builders.add((byte) 0);
				builders.add((byte) 2);
				builders.add((byte) 3);
			}
			service.displayDbs(dbids, builders, this);
		}
		*/
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	public static class Builder extends AbstractPortletBuilder<VortexWebDatabaseColumnsTablePortlet> {
		public static final String ID = "databaseColumnsTablePortlet";

		public Builder() {
			super(VortexWebDatabaseColumnsTablePortlet.class);
		}

		@Override
		public VortexWebDatabaseColumnsTablePortlet buildPortlet(PortletConfig portletConfig) {
			return new VortexWebDatabaseColumnsTablePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Database Columns";
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
	protected Row createAndAddRow(VortexClientDbColumn node) {
		VortexAgentDbColumn a = node.getData();
		byte m = a.getMask();
		return addRow(a.getId(), a.getRevision(), a.getName(), a.getType(), a.getDescription(), bitMask.toDetailedString(m), a.getSize(), a.getPrecision(), a.getScale(),
				a.getPermissibleValues(), a.getPosition(), a.getComments(), node.getDatabaseId(), mask(VortexAgentDbColumn.MASK_NULLABLE, m),
				mask(VortexAgentDbColumn.MASK_UNSIGNED, m), a.getTableId(), node.getTableName(), node.getDbName(), node.getHostName(), node.getTable().getDatabase().getDbServer()
						.getId());
	}
	@Override
	protected void updateRow(Row row, VortexClientDbColumn node) {
		VortexAgentDbColumn a = node.getData();
		byte m = a.getMask();
		//row.put("clid", a.getId());
		row.put("rev", a.getRevision());
		row.put("name", a.getName());
		row.put("cltype", a.getType());
		row.put("desc", a.getDescription());
		row.put("mask", bitMask.toDetailedString(m));
		row.put("size", a.getSize());
		row.put("prec", a.getPrecision());
		row.put("scale", a.getScale());
		row.put("permv", a.getPermissibleValues());
		row.put("pos", a.getPosition());
		row.put("comm", a.getComments());
		//row.put("dbid", node.getDatabaseId());
		row.put("null", mask(VortexAgentDbColumn.MASK_NULLABLE, m));
		row.put("unsign", mask(VortexAgentDbColumn.MASK_UNSIGNED, m));
		row.put("host", node.getHostName());
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
