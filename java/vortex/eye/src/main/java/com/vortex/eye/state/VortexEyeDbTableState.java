package com.vortex.eye.state;

import java.util.HashMap;
import java.util.Map;

import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;

public class VortexEyeDbTableState {

	private Map<String, VortexAgentDbColumn> columns = new HashMap<String, VortexAgentDbColumn>();
	private VortexAgentDbTable table;

	public VortexEyeDbTableState(VortexAgentDbTable table) {
		this.table = table;
	}
	public VortexAgentDbTable getTable() {
		return table;
	}
	public void setTable(VortexAgentDbTable table) {
		this.table = table;
	}

	public void addColumn(VortexAgentDbColumn column) {
		this.columns.put(column.getName(), column);
	}

	public VortexAgentDbColumn getColumn(String name) {
		return columns.get(name);
	}
}
