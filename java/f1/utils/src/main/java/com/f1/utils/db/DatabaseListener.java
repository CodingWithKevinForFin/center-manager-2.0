package com.f1.utils.db;

public interface DatabaseListener {

	public void onQuery(String sql, Object params[], DatabaseConnection connection);
}
