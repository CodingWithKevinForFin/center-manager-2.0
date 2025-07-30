/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public interface DbStatementPreparer {

	public List<PreparedStatement> prepareStatement(Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception;

}
