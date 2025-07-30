package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbPrivilege extends VortexClientEntity<VortexAgentDbPrivilege> {

	private VortexClientDbDatabase database;

	public VortexClientDbPrivilege(VortexAgentDbPrivilege data) {
		super(VortexAgentEntity.TYPE_DB_PRIVILEDGE, data);
	}

	public VortexClientDbDatabase getDatabase() {
		return database;
	}

	public void setDatabase(VortexClientDbDatabase database) {
		this.database = database;
	}
	public String getDbName() {
		return database.getDbName();
	}

}
