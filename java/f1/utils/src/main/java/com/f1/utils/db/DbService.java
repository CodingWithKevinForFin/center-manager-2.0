package com.f1.utils.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.f1.base.ObjectGenerator;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.base.Valued;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.DetailedException;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class DbService {
	private static final Logger log = Logger.getLogger(DbService.class.getName());

	final private DataSource datasource;
	final private Map<String, FileDbStatement> statements = new HashMap<String, FileDbStatement>();

	final private ObjectGenerator generator;

	private int logLargeQueryFrequency = DBH.DEFAULT_LOG_LARGE_QUERY_PERIOD;

	private DbStatementFactory defaultDbStatementFactory = null;

	public DbService(DataSource datasource, ObjectGenerator generator) {
		this.datasource = datasource;
		this.generator = generator;
	}

	public <T> T nw(Class<T> clazz) {
		return this.generator.nw(clazz);
	}

	public Connection getConnection() throws SQLException {
		return datasource.getConnection();
	}

	public void execute(String string, Map<Object, Object> params, Connection connection) throws Exception {
		execute(string, params, connection, this.defaultDbStatementFactory);
	}
	public void execute(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		try {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Executing Statement ", string, ": ", params);
			getStatement(string).execute(params, connection, factory);
		} catch (Exception e) {
			throw new DetailedException("Error executing on db", e).set("db", datasource).set("target", string).set("params", params);
		}
	}

	public int executeUpdate(String string, Map<Object, Object> params, Connection connection) throws Exception {
		return executeUpdate(string, params, connection, this.defaultDbStatementFactory);
	}
	public int executeUpdate(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		try {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Executing Statement ", string, ": ", params);
			return getStatement(string).executeUpdate(params, connection, factory);
		} catch (Exception e) {
			throw new DetailedException("Error executing on db", e).set("db", datasource).set("target", string).set("params", params);
		}
	}

	public ResultSet executeQuery(String string, Map<Object, Object> params, Connection connection) throws Exception {
		return executeQuery(string, params, connection, this.defaultDbStatementFactory);
	}
	public ResultSet executeQuery(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Executing Query ", string, ": ", params);
		return getStatement(string).executeQuery(params, connection, factory);
	}

	public DbStatement getStatement(String string) {
		return CH.getOrThrow(statements, string);
	}

	public void add(File fileOrDirectory, String suffix) throws IOException {
		fileOrDirectory = fileOrDirectory.getCanonicalFile();
		if (fileOrDirectory.isFile()) {
			try {
				addFile(fileOrDirectory, suffix);
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				throw OH.toRuntime(e);
			}
		} else if (fileOrDirectory.isDirectory()) {
			try {
				File[] listFiles = fileOrDirectory.listFiles();
				if (listFiles != null) {
					for (File f : listFiles) {
						addFile(f, suffix);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("error adding files from directory: " + IOH.getFullPath(fileOrDirectory), e);
			}

		} else
			throw new FileNotFoundException(IOH.getFullPath(fileOrDirectory));
	}

	private void addFile(File f, String suffix) throws Exception {
		if (f.getName().endsWith(suffix)) {
			String name = SH.afterLast(f.getName(), File.pathSeparatorChar);
			name = SH.stripSuffix(name, suffix, true);
			CH.putOrThrow(statements, name, new FileDbStatement(f, generator));
		}

	}

	public <T extends Valued> List<T> executeQuery(String query, Map<Object, Object> params, Class<T> returnType, Connection connection) throws Exception {
		ResultSet result = executeQuery(query, params, connection);
		try {
			return DBH.toValuedList(result, getGenerator(returnType), Collections.EMPTY_MAP, logLargeQueryFrequency, new ArrayList<T>());
		} catch (Exception e) {
			throw new DetailedException("Error converting resultset to f1 objects", e).set("query key", query).set("return type", returnType).set("sql file",
					getStatement(query).describe());
		} finally {
			IOH.close(result);
		}
	}
	public <T extends Valued> List<T> executeQuery(String query, Map<Object, Object> params, Class<T> returnType, Connection connection,
			Map<String, ResultSetGetter<?>> customConverters) throws Exception {
		ResultSet result = executeQuery(query, params, connection);
		try {
			return DBH.toValuedList(result, getGenerator(returnType), customConverters, logLargeQueryFrequency, new ArrayList<T>());
		} catch (Exception e) {
			throw new DetailedException("Error converting resultset to f1 objects", e).set("query key", query).set("return type", returnType).set("sql file",
					getStatement(query).describe());
		} finally {
			IOH.close(result);
		}
	}

	public <T> ObjectGeneratorForClass<T> getGenerator(Class<T> type) {
		return generator.getGeneratorForClass(type);
	}

	public DbStatement toStatement(String string) throws Exception {
		return new BasicDbStatement(string, generator);
	}

	public void executeSql(String string, Map<Object, Object> params, Connection connection) throws Exception {
		executeSql(string, params, connection, this.defaultDbStatementFactory);
	}
	public void executeSql(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		try {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Executing Statement ", string, ": ", params);
			toStatement(string).execute(params, connection, factory);
		} catch (Exception e) {
			throw new DetailedException("Error executing on db", e).set("db", datasource).set("target", string).set("params", params);
		}
	}

	public int executeSqlUpdate(String string, Map<Object, Object> params, Connection connection) throws Exception {
		return executeSqlUpdate(string, params, connection, this.defaultDbStatementFactory);
	}
	public int executeSqlUpdate(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		try {
			if (log.isLoggable(Level.FINE))
				LH.fine(log, "Executing Statement ", string, ": ", params);
			return toStatement(string).executeUpdate(params, connection, factory);
		} catch (Exception e) {
			throw new DetailedException("Error executing on db", e).set("db", datasource).set("target", string).set("params", params);
		}
	}

	public ResultSet executeSqlQuery(String string, Map<Object, Object> params, Connection connection) throws Exception {
		return this.executeSqlQuery(string, params, connection, this.defaultDbStatementFactory);

	}
	public ResultSet executeSqlQuery(String string, Map<Object, Object> params, Connection connection, DbStatementFactory factory) throws Exception {
		if (log.isLoggable(Level.FINE))
			LH.fine(log, "Executing Query ", string, ": ", params);
		return toStatement(string).executeQuery(params, connection, factory);
	}

	public <T extends Valued> List<T> executeSqlQuery(String query, Map<Object, Object> params, Class<T> returnType, Connection connection) throws Exception {
		ResultSet result = executeSqlQuery(query, params, connection);
		try {
			return DBH.toValuedList(result, getGenerator(returnType), Collections.EMPTY_MAP, logLargeQueryFrequency, new ArrayList<T>());
		} catch (Exception e) {
			throw new DetailedException("Error converting resultset to f1 objects", e).set("query key", query).set("return type", returnType);
		} finally {
			IOH.close(result);
		}
	}
	public <T extends Valued> List<T> executeSqlQuery(String query, Map<Object, Object> params, Class<T> returnType, Connection connection,
			Map<String, ResultSetGetter<?>> customConverters) throws Exception {
		ResultSet result = executeSqlQuery(query, params, connection);
		try {
			return DBH.toValuedList(result, getGenerator(returnType), customConverters, logLargeQueryFrequency, new ArrayList<T>());
		} catch (Exception e) {
			throw new DetailedException("Error converting resultset to f1 objects", e).set("query key", query).set("return type", returnType);
		} finally {
			IOH.close(result);
		}
	}

	public int getLogLargeQueryFrequency() {
		return logLargeQueryFrequency;
	}

	public void setLogLargeQueryFrequency(int logLargeQueryFrequency) {
		this.logLargeQueryFrequency = logLargeQueryFrequency;
	}

	public DbStatementFactory getDefaultDbStatementFactory() {
		return defaultDbStatementFactory;
	}

	public void setDefaultDbStatementFactory(DbStatementFactory dbStatementFactory) {
		this.defaultDbStatementFactory = dbStatementFactory;
	}

}
