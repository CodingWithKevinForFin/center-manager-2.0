package com.vortex.eye.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;

public class VortexEyeDbDatabaseState {

	private Map<String, VortexEyeDbTableState> tables = new HashMap<String, VortexEyeDbTableState>();
	private Map<String, VortexAgentDbObject> objects = new HashMap<String, VortexAgentDbObject>();
	private Map<String, VortexAgentDbPrivilege> privileges = new HashMap<String, VortexAgentDbPrivilege>();
	private VortexAgentDbDatabase database;
	public VortexEyeDbDatabaseState(VortexAgentDbDatabase db) {
		this.database = db;
	}
	public VortexAgentDbDatabase getDatabase() {
		return database;
	}
	public void setDatabase(VortexAgentDbDatabase database) {
		this.database = database;
	}

	public VortexEyeDbTableState getTable(String name) {
		return tables.get(name);
	}

	public VortexEyeDbTableState addTable(VortexAgentDbTable table) {
		VortexEyeDbTableState r = new VortexEyeDbTableState(table);
		tables.put(table.getName(), r);
		return r;
	}

	public void addObject(VortexAgentDbObject object) {
		this.objects.put(object.getName(), object);
	}
	public VortexAgentDbObject getObject(String name) {
		return this.objects.get(name);
	}

	public void addPrivilege(VortexAgentDbPrivilege object) {
		this.privileges.put(object.getDescription(), object);
	}
	public VortexAgentDbPrivilege getPrivilege(String name) {
		return this.privileges.get(name);
	}

	public Collection<VortexEyeDbTableState> getTables() {
		return tables.values();
	}
	public Collection<VortexAgentDbObject> getObjects() {
		return objects.values();
	}
	public Collection<VortexAgentDbPrivilege> getPrivileges() {
		return privileges.values();
	}
}
