package com.f1.utils.ids;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

/**
 * Expects a table with one column and one row. The cell should be populated with the next id to be returned. After each call, the number will be incremented... Ideally this should
 * be used in conjunction with {@link BatchIdGenerator}
 * <P>
 * 
 * to create a table:
 * 
 * <pre>
 * CREATE TABLE ID_GENERATOR (VARCHAR(64) NAMESPACE, INT NEXT_ID)
 * </pre>
 * 
 * @author rcooke
 * 
 */
public class DbBackedIdGenerator implements IdGenerator<Long> {

	public static final String DEFAULT_TABLE = "ID_GENERATOR";
	public static final String DEFAULT_ID_COLUMN = "NEXT_ID";
	public static final String DEFAULT_NAMESPACE_COLUMN = "NAMESPACE";

	private static final Logger log = Logger.getLogger(DbBackedIdGenerator.class.getName());
	private static final long DEFAULT_FIRST_ID = 1;

	final private String tableName;
	final private String querySql;
	final private String updateSql;
	final private String insertSql;
	final private DataSource dataSource;

	final private String namespace;
	final private long firstId;

	public DbBackedIdGenerator(DataSource dataSource, String tableName, String idColumnName, String namespaceColumnName, String namespace) {
		this(dataSource, tableName, idColumnName, namespaceColumnName, namespace, DEFAULT_FIRST_ID);

	}
	public DbBackedIdGenerator(DataSource dataSource, String tableName, String idColumnName, String namespaceColumnName, String namespace, long firstId) {
		Set<Character> valid = new HashSet<Character>(SH.ALPHA_NUMERIC_SET);
		CH.s(valid, '_', '.');
		if (SH.indexOfNot(tableName, 0, valid) != -1)
			throw new RuntimeException("illegal table name: " + tableName);
		if (SH.indexOfNot(idColumnName, 0, valid) != -1)
			throw new RuntimeException("illegal table name: " + idColumnName);
		this.tableName = tableName;
		this.namespace = namespace;
		this.querySql = "SELECT " + idColumnName + " FROM " + tableName + " WHERE " + namespaceColumnName + "=?";
		this.updateSql = "UPDATE " + tableName + " SET " + idColumnName + "=?" + " WHERE " + namespaceColumnName + "=? AND " + idColumnName + "=?";
		this.insertSql = "INSERT INTO " + tableName + " (" + idColumnName + "," + namespaceColumnName + ") VALUES(?,?)";
		this.dataSource = dataSource;
		this.firstId = firstId;
		LH.fine(log, "ID Generation update SQL: ", this.updateSql);
		LH.fine(log, "ID Generation query  SQL: ", this.querySql);
		LH.fine(log, "ID Generation insert SQL: ", this.insertSql);
		Connection connection = null;
		try {
			connection = getConnection();
			query(connection);
		} catch (Exception e) {
			throw new RuntimeException("error testing id database", e);
		} finally {
			IOH.commit(connection, true);
			IOH.close(connection);
		}
	}

	private long query(Connection connection) throws IOException, SQLException {
		ResultSet resultSet = null;
		try {
			connection.setAutoCommit(false);
			PreparedStatement statement = connection.prepareStatement(this.querySql);
			statement.setString(1, namespace);
			resultSet = statement.executeQuery();
			if (!resultSet.next()) {
				IOH.close(resultSet);
				try {
					statement = connection.prepareStatement(this.insertSql);
					statement.setLong(1, firstId);
					statement.setString(2, namespace);
					statement.execute();
				} catch (Exception e) {
					throw new SQLException("Error executing: " + this.insertSql, e);
				}
				statement = connection.prepareStatement(this.querySql);
				statement.setString(1, namespace);
				resultSet = statement.executeQuery();
				if (!resultSet.next())
					throw new RuntimeException("table still doesn't have any entries after inserting a new row: " + this.tableName);
			}
			long r = resultSet.getLong(1);
			if (resultSet.next())
				throw new RuntimeException("table should only have one row: " + this.tableName);
			return r;
		} finally {
			IOH.commit(connection, true);
			IOH.close(resultSet);
		}
	}

	private boolean update(Connection connection, long current, long next) throws IOException, SQLException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(this.updateSql);
			statement.setLong(1, next);
			statement.setString(2, namespace);
			statement.setLong(3, current);
			return statement.executeUpdate() == 1;
		} finally {
			IOH.commit(connection, true);
			IOH.close(statement);
		}
	}

	@Override
	public Long createNextId() {
		List<Long> sink = new ArrayList<Long>(1);
		createNextIds(1, sink);
		return sink.get(0);
	}

	@Override
	public void createNextIds(int count, Collection<? super Long> sink) {
		long start;
		synchronized (this) {
			int errorsCount = 0;
			for (;;) {
				Connection connection = null;
				try {
					connection = getConnection();
					start = query(connection);
					if (update(connection, start, start + count))
						break;
				} catch (Exception e) {
					if (errorsCount++ > 2)
						throw new RuntimeException("error getting next id from database", e);
					else
						LH.info(log, "Error getting connection, trying again... ==> ", e.getMessage());
				} finally {
					IOH.close(connection);
				}
			}
		}
		for (int i = 0; i < count; i++)
			sink.add(start + i);
	}

	private Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static class Factory implements com.f1.base.Factory<String, DbBackedIdGenerator> {

		final private String tableName;
		final private String idColumnName;
		final private String namespaceColumnName;
		final private DataSource dataSource;
		final private long firstId;

		public Factory(DataSource dataSource) {
			this(dataSource, DEFAULT_TABLE);
		}

		public Factory(DataSource dataSource, String tableName) {
			this(dataSource, tableName, DEFAULT_ID_COLUMN, DEFAULT_NAMESPACE_COLUMN, DEFAULT_FIRST_ID);
		}

		public Factory(DataSource dataSource, String tableName, String idColumnName, String namespaceColumnName) {
			this(dataSource, tableName, idColumnName, namespaceColumnName, DEFAULT_FIRST_ID);
		}
		public Factory(DataSource dataSource, String tableName, String idColumnName, String namespaceColumnName, long firstId) {
			this.dataSource = dataSource;
			this.tableName = tableName;
			this.idColumnName = idColumnName;
			this.namespaceColumnName = namespaceColumnName;
			this.firstId = firstId;
		}

		@Override
		public DbBackedIdGenerator get(String key) {
			return new DbBackedIdGenerator(dataSource, tableName, idColumnName, namespaceColumnName, key, firstId);
		}

	}

}
