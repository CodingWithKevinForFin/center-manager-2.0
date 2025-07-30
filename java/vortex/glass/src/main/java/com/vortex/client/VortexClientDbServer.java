package com.vortex.client;

import com.f1.utils.SH;
import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbServer extends VortexClientEntity<VortexAgentDbServer> {

	final private LongKeyMap<VortexClientDbDatabase> databases = new LongKeyMap<VortexClientDbDatabase>();
	final private LongKeyMap<VortexClientDbTable> tables = new LongKeyMap<VortexClientDbTable>();
	final private LongKeyMap<VortexClientDbColumn> columns = new LongKeyMap<VortexClientDbColumn>();
	final private LongKeyMap<VortexClientDbPrivilege> privileges = new LongKeyMap<VortexClientDbPrivilege>();
	final private LongKeyMap<VortexClientDbObject> objects = new LongKeyMap<VortexClientDbObject>();

	public VortexClientDbServer(VortexAgentDbServer data) {
		super(VortexAgentEntity.TYPE_DB_SERVER, data);
	}

	public String getDbTypeString() {
		switch (getData().getDbType()) {
			case VortexAgentDbServer.TYPE_MYSQL:
				return "Mysql";
			default:
				return "Unknown_dbtype_" + SH.toString(getData().getDbType());

		}
	}

	//DATABASES
	public void addDbDatabase(VortexClientDbDatabase node) {
		node.setMachine(getMachine());
		//WebAgentDbServer server = machineDbServers.get(node.getData().getDbServerId());
		//if (server == null)
		//throw new IllegalStateException("database missing server: " + server.getData());
		//node.setMachine(this);
		node.setDbServer(this);
		databases.put(node.getData().getId(), node);
	}
	public VortexClientDbDatabase removeDatabase(long id) {
		VortexClientDbDatabase r = databases.remove(id);
		if (r != null) {
			r.unbind();
			for (VortexClientDbTable table : r.getTables()) {
				removeDbTable(table.getId());
				//TODO: notify
			}
		}
		return r;
	}
	public VortexClientDbDatabase getDatabase(long id) {
		return databases.get(id);
	}
	public Iterable<VortexClientDbDatabase> getDatabases() {
		return databases.values();
	}

	//TABLES
	public void addDbTable(VortexClientDbTable node) {
		node.setMachine(getMachine());
		VortexClientDbDatabase db = databases.get(node.getData().getDatabaseId());
		if (db == null)
			throw new IllegalStateException("table missing database: " + node.getData());
		//node.setMachine(this);
		node.setDatabase(db);
		db.addTable(node);
		tables.put(node.getId(), node);
	}
	public VortexClientDbTable removeDbTable(long id) {
		VortexClientDbTable r = tables.remove(id);
		if (r != null) {
			VortexClientDbDatabase db = r.getDatabase();
			if (db != null) {
				db.removeTable(id);
				r.setDatabase(null);
				//TODO: notify
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientDbTable getDbTable(long id) {
		return tables.get(id);
	}
	public Iterable<VortexClientDbTable> getDbTables() {
		return tables.values();
	}

	//COLUMNS
	public void addDbColumn(VortexClientDbColumn node) {
		node.setMachine(getMachine());
		VortexClientDbTable table = tables.get(node.getData().getTableId());
		if (table == null)
			throw new IllegalStateException("column missing table: " + node.getData());
		//node.setMachine(this);
		node.setTable(table);
		table.addColumn(node);
		columns.put(node.getId(), node);
	}
	public VortexClientDbColumn removeDbColumn(long id) {
		VortexClientDbColumn r = columns.remove(id);
		if (r != null) {
			VortexClientDbTable table = r.getTable();
			if (table != null) {
				table.removeColumn(id);
				r.setTable(null);
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientDbColumn getDbColumn(long id) {
		return columns.get(id);
	}
	public Iterable<VortexClientDbColumn> getDbColumns() {
		return columns.values();
	}

	//PRIVILEGE
	public void addDbPrivilege(VortexClientDbPrivilege node) {
		node.setMachine(getMachine());
		VortexClientDbDatabase db = databases.get(node.getData().getDatabaseId());
		if (db == null)
			throw new IllegalStateException("db privilege missing database: " + node.getData());
		//node.setMachine(this);
		node.setDatabase(db);
		db.addPrivilege(node);
		privileges.put(node.getId(), node);
	}
	public VortexClientDbPrivilege removeDbPrivilege(long id) {
		VortexClientDbPrivilege r = privileges.remove(id);
		if (r != null) {
			VortexClientDbDatabase db = r.getDatabase();
			if (db != null) {
				db.removePrivilege(id);
				r.setDatabase(null);
				//TODO: notify
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientDbPrivilege getDbPrivilege(long id) {
		return privileges.get(id);
	}
	public Iterable<VortexClientDbPrivilege> getDbPrivileges() {
		return privileges.values();
	}

	//DB OBJECT
	public void addDbObject(VortexClientDbObject node) {
		node.setMachine(getMachine());
		VortexClientDbDatabase db = databases.get(node.getData().getDatabaseId());
		if (db == null)
			throw new IllegalStateException("db object missing database: " + node.getData());
		//node.setMachine(this);
		node.setDatabase(db);
		db.addObject(node);
		objects.put(node.getId(), node);
	}
	public VortexClientDbObject removeDbObject(long id) {
		VortexClientDbObject r = objects.remove(id);
		if (r != null) {
			VortexClientDbDatabase db = r.getDatabase();
			if (db != null) {
				db.removeObject(id);
				r.setDatabase(null);
				//TODO: notify
			}
			r.unbind();
		}
		return r;
	}
	public VortexClientDbObject getDbObject(long id) {
		return objects.get(id);
	}
	public Iterable<VortexClientDbObject> getDbObjects() {
		return objects.values();
	}

	public String getDescription() {
		return "DBDS-" + getId() + " " + getData().getDescription();
	}

}
