package com.vortex.agent.dbadapter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.f1.container.ContainerScope;
import com.f1.container.impl.AbstractContainerScope;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;

public abstract class DbInspector extends AbstractContainerScope {

	//abstract public Map<String, AgentDbDatabase> inspectDatabase(DataSource connection) throws SQLException;
	abstract public Map<String, VortexAgentDbDatabase> inspectDatabase(Connection connection) throws SQLException;

	public static DbInspector get(ContainerScope cs, String string) {
		return (DbInspector) cs.getServices().getService("DBINSPECTOR_" + string);
	}

	public static void put(ContainerScope cs, String string, DbInspector dbi) {
		cs.getServices().putService("DBINSPECTOR_" + string, dbi);
	}
}
