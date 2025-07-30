package com.vortex.client;

import com.f1.utils.structs.LongKeyMap;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;

public class VortexClientDbTable extends VortexClientEntity<VortexAgentDbTable> {

	public VortexClientDbTable(VortexAgentDbTable data) {
		super(VortexAgentEntity.TYPE_DB_TABLE, data);
	}

	final private LongKeyMap<VortexClientDbColumn> columns = new LongKeyMap<VortexClientDbColumn>();
	private VortexClientDbDatabase database;

	public void addColumn(VortexClientDbColumn table) {
		columns.put(table.getId(), table);
	}

	public VortexClientDbColumn removeColumn(long id) {
		return columns.remove(id);
	}

	public Iterable<VortexClientDbColumn> getColumns() {
		return columns.values();
	}

	public VortexClientDbDatabase getDatabase() {
		return database;
	}
	public void setDatabase(VortexClientDbDatabase db) {
		this.database = db;
	}

	public String getDbName() {
		return database.getDbName();
	}

}
