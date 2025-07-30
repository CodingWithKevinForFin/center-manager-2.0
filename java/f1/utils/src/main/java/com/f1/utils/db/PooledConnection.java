package com.f1.utils.db;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class PooledConnection implements DatabaseConnection {

	final private Connection inner;
	final private PooledDataSource datasource;
	volatile private boolean inpool = true;
	private SQLException lastException;
	private List<Statement> statements = new ArrayList<Statement>(1);
	private List<ResultSet> resultSets = new ArrayList<ResultSet>(1);
	private boolean wasConnectionTimeout;
	private long borrowedTime;
	private StackTraceElement[] callStack;
	private static final Logger log = LH.get();

	public SQLException getLastException() {
		return lastException;
	}

	public PooledConnection(Connection inner, PooledDataSource datasource) {
		this.inner = inner;
		this.datasource = datasource;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return inner.unwrap(iface);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return inner.isWrapperFor(iface);
	}

	public Statement createStatement() throws SQLException {
		try {
			return inner.createStatement();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		try {
			return new PooledPreparedStatement(this, inner.prepareStatement(sql), sql);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		try {
			return new PooledCallableStatement(this, inner.prepareCall(sql), sql);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public String nativeSQL(String sql) throws SQLException {
		try {
			return inner.nativeSQL(sql);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (datasource.isSkipAutoCommit())
			return;
		try {
			inner.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public boolean getAutoCommit() throws SQLException {
		if (datasource.isSkipAutoCommit())
			return false;
		return inner.getAutoCommit();
	}

	public void commit() throws SQLException {
		if (datasource.isSkipAutoCommit())
			return;
		try {
			inner.commit();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void rollback() throws SQLException {
		if (datasource.isSkipAutoCommit())
			throw new UnsupportedOperationException("autocommit is set to skip, can't roll back");
		try {
			inner.rollback();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	@Override
	public void close() throws SQLException {
		try {
			for (Statement statement : statements)
				IOH.close(statement);
			statements.clear();
			for (ResultSet rs : resultSets)
				IOH.close(rs);
			resultSets.clear();
			if (!wasConnectionTimeout)
				datasource.returnConnection(this);
			else
				closeConnectionNoThrow();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}
	protected void closeConnection() throws SQLException {
		inner.close();
	}
	protected void closeConnectionNoThrow() {
		IOH.close(inner);
	}

	public boolean isClosed() throws SQLException {
		return inner.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		try {
			return inner.getMetaData();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		try {
			inner.setReadOnly(readOnly);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public boolean isReadOnly() throws SQLException {
		return inner.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		try {
			inner.setCatalog(catalog);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public String getCatalog() throws SQLException {
		try {
			return inner.getCatalog();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setTransactionIsolation(int level) throws SQLException {
		try {
			inner.setTransactionIsolation(level);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public int getTransactionIsolation() throws SQLException {
		try {
			return inner.getTransactionIsolation();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public SQLWarning getWarnings() throws SQLException {
		try {
			return inner.getWarnings();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void clearWarnings() throws SQLException {
		try {
			inner.clearWarnings();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return inner.createStatement(resultSetType, resultSetConcurrency);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return inner.prepareStatement(sql, resultSetType, resultSetConcurrency);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		try {
			return new PooledCallableStatement(this, inner.prepareCall(sql, resultSetType, resultSetConcurrency), sql);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		try {
			return inner.getTypeMap();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		try {
			inner.setTypeMap(map);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setHoldability(int holdability) throws SQLException {
		try {
			inner.setHoldability(holdability);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public int getHoldability() throws SQLException {
		try {
			return inner.getHoldability();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Savepoint setSavepoint() throws SQLException {
		try {
			return inner.setSavepoint();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		try {
			return inner.setSavepoint(name);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		try {
			inner.rollback(savepoint);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		try {
			inner.releaseSavepoint(savepoint);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return inner.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return inner.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		try {
			return new PooledCallableStatement(this, inner.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability), sql);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		try {
			return inner.prepareStatement(sql, autoGeneratedKeys);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		try {
			return inner.prepareStatement(sql, columnIndexes);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		try {
			return inner.prepareStatement(sql, columnNames);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Clob createClob() throws SQLException {
		try {
			return inner.createClob();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Blob createBlob() throws SQLException {
		try {
			return inner.createBlob();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public NClob createNClob() throws SQLException {
		try {
			return inner.createNClob();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public SQLXML createSQLXML() throws SQLException {
		try {
			return inner.createSQLXML();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public boolean isValid(int timeout) throws SQLException {
		try {
			return inner.isValid(timeout);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		inner.setClientInfo(name, value);
	}

	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		inner.setClientInfo(properties);
	}

	public String getClientInfo(String name) throws SQLException {
		try {
			return inner.getClientInfo(name);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Properties getClientInfo() throws SQLException {
		try {
			return inner.getClientInfo();
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		try {
			return inner.createArrayOf(typeName, elements);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		try {
			return inner.createStruct(typeName, attributes);
		} catch (SQLException e) {
			lastException = e;
			throw e;
		}
	}

	protected PooledDataSource getDatasource() {
		return datasource;
	}

	protected Connection getInner() {
		return inner;
	}

	public void setInPool(boolean inPool) {
		if (this.inpool != inPool) {
			this.inpool = inPool;
			if (this.datasource.getMonitorForConnectionLeaks()) {
				long now = System.currentTimeMillis();
				if (inPool) {
					this.callStack = null;
				} else {
					this.borrowedTime = now;
					this.callStack = Thread.currentThread().getStackTrace();
				}
			}
		}
		this.lastException = null;
	}
	@Override
	public boolean getIsInPool() {
		return inpool;
	}

	@Override
	public Database getDatabase() {
		return datasource;
	}

	public void setSchema(String schema) throws SQLException {
	}

	public String getSchema() throws SQLException {
		return null;
	}

	public void abort(Executor executor) throws SQLException {
	}

	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
	}

	public int getNetworkTimeout() throws SQLException {
		return 0;
	}

	protected void addStatement(Statement statement) {
		statements.add(statement);
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public ResultSet addResultSet(ResultSet resultSet) {
		resultSets.add(resultSet);
		return resultSet;
	}

	public void setConnectionLost(boolean b) {
		this.wasConnectionTimeout = true;
	}

	protected void monitorForLeak(long cutoffTimeMs) {
		if (inpool)
			return;
		if (this.borrowedTime < cutoffTimeMs) {
			StackTraceElement[] cs = this.callStack;
			if (cs != null)
				LH.warning(log, "Connection Leak from:\n  leak> ", SH.join("\n  leak> ", (Object[]) cs));
			this.callStack = null;//only report once
		}

	}
}
