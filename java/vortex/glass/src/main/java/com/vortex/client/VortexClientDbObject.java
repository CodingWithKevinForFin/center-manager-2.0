package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbObject extends VortexClientEntity<VortexAgentDbObject> {

	private VortexClientDbDatabase database;

	public VortexClientDbObject(VortexAgentDbObject data) {
		super(VortexAgentEntity.TYPE_DB_OBJECT, data);
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
