package com.vortex.client;

import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbColumn extends VortexClientEntity<VortexAgentDbColumn> {

	private VortexClientDbTable table;

	public VortexClientDbColumn(VortexAgentDbColumn data) {
		super(VortexAgentEntity.TYPE_DB_COLUMN, data);
	}

	public VortexClientDbTable getTable() {
		return table;
	}

	public void setTable(VortexClientDbTable table) {
		this.table = table;
	}
	public String getDbName() {
		return table.getDbName();
	}

	public String getTableName() {
		return table.getData().getName();
	}

	public long getDatabaseId() {
		return table.getDatabase().getId();
	}

}
