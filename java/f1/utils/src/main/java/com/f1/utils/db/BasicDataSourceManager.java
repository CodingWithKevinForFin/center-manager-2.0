/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.utils.AH;
import com.f1.utils.CH;

public class BasicDataSourceManager implements DatabaseManager {

	private static final DatabaseManagerListener[] EMPTY = new DatabaseManagerListener[0];
	private Map<String, Database> dataSources = new HashMap<String, Database>();
	private DatabaseManagerListener[] listeners = EMPTY;

	@Override
	public Database getDatabase(String id) {
		return CH.getOrThrow(dataSources, id);
	}

	@Override
	public void addDataBase(String id, Database database) {
		CH.putOrThrow(dataSources, id, database);
		for (DatabaseManagerListener listener : listeners)
			listener.onNewDatabase(id, database);
	}

	@Override
	public Set<String> getDatabases() {
		return dataSources.keySet();
	}

	@Override
	public Connection getConnection(String dataSourceId) throws SQLException {
		return getDatabase(dataSourceId).getConnection();
	}

	@Override
	public void addDatabaseManagerListener(DatabaseManagerListener listener) {
		listeners = AH.append(listeners, listener);
	}

	@Override
	public DatabaseManagerListener[] getDatabaseManagerListeners() {
		return listeners;
	}

}
