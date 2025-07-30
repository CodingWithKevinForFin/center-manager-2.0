package com.vortex.client;

import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbDatabase extends VortexClientEntity<VortexAgentDbDatabase> {

	private VortexClientDbServer dbServer;

	public VortexClientDbDatabase(VortexAgentDbDatabase data) {
		super(VortexAgentEntity.TYPE_DB_DATABASE, data);
	}

	final private LongKeyMap<VortexClientDbTable> tables = new LongKeyMap<VortexClientDbTable>();
	final private LongKeyMap<VortexClientDbObject> objects = new LongKeyMap<VortexClientDbObject>();
	final private LongKeyMap<VortexClientDbPrivilege> privileges = new LongKeyMap<VortexClientDbPrivilege>();

	public void addTable(VortexClientDbTable table) {
		tables.put(table.getId(), table);
	}

	public VortexClientDbTable removeTable(long id) {
		return tables.remove(id);
	}

	public Iterable<VortexClientDbTable> getTables() {
		return tables.values();
	}

	public void addPrivilege(VortexClientDbPrivilege table) {
		privileges.put(table.getId(), table);
	}

	public VortexClientDbPrivilege removePrivilege(long id) {
		return privileges.remove(id);
	}

	public Iterable<VortexClientDbPrivilege> getPrivileges() {
		return privileges.values();
	}

	public void addObject(VortexClientDbObject table) {
		objects.put(table.getId(), table);
	}

	public VortexClientDbObject removeObject(long id) {
		return objects.remove(id);
	}

	public Iterable<VortexClientDbObject> getObjects() {
		return objects.values();
	}

	public String getDbName() {
		return getData().getName();
	}

	public VortexClientDbServer getDbServer() {
		return dbServer;
	}

	public void setDbServer(VortexClientDbServer dbServer) {
		this.dbServer = dbServer;
	}

}
