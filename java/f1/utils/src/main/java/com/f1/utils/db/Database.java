package com.f1.utils.db;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

public interface Database extends DataSource {
	byte STATE_CONNECTED = 1;
	byte STATE_NOT_CONNECTED = 2;

	public String getUrl();

	public Collection<? extends DatabaseConnection> getDatabaseConnections();

	public void addDatabaseListener(DatabaseListener listener);
	public void removedDatabaseListener(DatabaseListener listener);
	public List<DatabaseListener> getDatabaseListeners();

}
