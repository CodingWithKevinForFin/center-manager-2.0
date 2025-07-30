/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public interface DatabaseManager {
	public Database getDatabase(String id);

	public void addDataBase(String id, Database manager);

	public Set<String> getDatabases();

	public Connection getConnection(String dataSourceId) throws SQLException;

	public void addDatabaseManagerListener(DatabaseManagerListener listener);

	public DatabaseManagerListener[] getDatabaseManagerListeners();

}
