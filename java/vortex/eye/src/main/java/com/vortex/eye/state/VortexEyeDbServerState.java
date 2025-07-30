package com.vortex.eye.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbServer;

public class VortexEyeDbServerState {

	private VortexAgentDbServer dbServer;
	private Map<String, VortexEyeDbDatabaseState> databases = new HashMap<String, VortexEyeDbDatabaseState>();

	public VortexEyeDbServerState(VortexAgentDbServer server) {
		this.dbServer = server;
	}

	public VortexAgentDbServer getDbServer() {
		return dbServer;
	}

	public void setDbServer(VortexAgentDbServer dbServer) {
		this.dbServer = dbServer;
	}

	public VortexEyeDbDatabaseState addDatabase(VortexAgentDbDatabase db) {
		VortexEyeDbDatabaseState r = new VortexEyeDbDatabaseState(db);
		databases.put(db.getName(), r);
		return r;
	}

	public Collection<VortexEyeDbDatabaseState> getDatabases() {
		return databases.values();
	}

	public VortexEyeDbDatabaseState getDatabase(String name) {
		return databases.get(name);
	}

	public VortexEyeDbDatabaseState removeDatabase(String name) {
		return databases.remove(name);
	}

}
