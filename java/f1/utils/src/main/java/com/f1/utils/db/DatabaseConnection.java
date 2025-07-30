package com.f1.utils.db;

import java.sql.Connection;

public interface DatabaseConnection extends Connection {

	public boolean getIsInPool();

	public Database getDatabase();
}
