package com.f1.ami.sqltests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;

public class AmiTestHelper {
	private static final String CLASS_NAME = "com.f1.ami.amidb.jdbc.AmiDbJdbcDriver";
	private static final int definedByColTablesPos = 6;
	private static final int definedByColProceduresPos = 6;
	private static final int definedByColTriggersPos = 6;
	private static final int definedByColTimersPos = 6;
	private static final int nameColTablesPos = 1;
	private Connection connection;

	/**
	 * Get AMI DB connection
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public AmiTestHelper() throws SQLException, ClassNotFoundException {
		Class.forName(CLASS_NAME);
	}

	public void openConnection(String urlPrefix, String user, String password) throws SQLException {
		closeConnection();
		String url = urlPrefix + "?username=" + user + "&password=" + password;
		connection = DriverManager.getConnection(url);
	}

	public void closeConnection() throws SQLException {
		if (!isClosed()) {
			connection.close();
		}
	}

	/**
	 * Checks whether this AmiTestHelper object has been closed.
	 * 
	 * @return true if this AmiTestHelper object is closed; false if it is still open
	 * @throws SQLException
	 */
	public boolean isClosed() throws SQLException {
		return connection == null || connection.isClosed();
	}

	/**
	 * Check if AMID DB doesn't have any user objects
	 * 
	 * @return true if empty
	 * @throws SQLException
	 */
	public boolean isAmiDBEmpty() throws SQLException {
		boolean userTables = isUserTypePresent("show tables", definedByColTablesPos);
		boolean userProcedures = isUserTypePresent("show procedures", definedByColProceduresPos);
		boolean userTriggers = isUserTypePresent("show triggers", definedByColTriggersPos);
		boolean userTimers = isUserTypePresent("show timers", definedByColTimersPos);

		return (!userTables && !userProcedures && !userTriggers && !userTimers);
	}

	/**
	 * Checks against well-known AMI DB system users
	 * 
	 * @param query
	 *            the SQL query for DB Objects
	 * @param colNum
	 *            the column number for "defined by"
	 * @return true if users objects are present
	 * @throws SQLException
	 */
	private boolean isUserTypePresent(String query, int colNum) throws SQLException {
		final String[] systemUsers = { "AMI", "SYSTEM" };

		ResultSet rs = connection.createStatement().executeQuery(query);
		while (rs.next()) {
			String definedBy = Caster_String.INSTANCE.cast(rs.getObject(colNum));
			boolean systemTable = false;
			for (String s : systemUsers) {
				if (s.equals(definedBy)) {
					systemTable = true; // the system one
					break;
				}
			}
			if (!systemTable) { // found at least one user 
				rs.close();
				return true;
			}
		}
		rs.close();
		return false;
	}

	/**
	 * Get the list of column names for table
	 * 
	 * @param tableName
	 *            table name
	 * @return the list of column names
	 * @throws SQLException
	 */
	public List<String> getColumnNames(String tableName) throws SQLException {
		try {
			String query = "select * from " + tableName + " where false";
			ResultSet rs = connection.createStatement().executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			List<String> list = new ArrayList<String>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				list.add(rsmd.getColumnName(i));
			}
			return list;
		} catch (SQLException e) { // Do not propagate exception for unknown table, return null
			if (e.getMessage().startsWith("Unknown table:"))
				return null;
			throw e;
		}
	}

	/**
	 * Check if table a temporary one
	 * 
	 * @param tableName
	 *            the table name
	 * @return true if table is temporary
	 * @throws SQLException
	 */
	public boolean isTemporaryTable(String tableName) throws SQLException {
		Statement st = connection.createStatement();
		st.setQueryTimeout(30);
		ResultSet rs = st.executeQuery("show tables");
		//ResultSet rs = connection.createStatement().executeQuery("show tables");
		while (rs.next()) {
			String name = (String) rs.getObject(nameColTablesPos);
			if (tableName.equals(name)) {
				String definedBy = (String) rs.getObject(definedByColTablesPos);
				return definedBy.equals("TEMPORARY");
			}
		}
		return false;
	}

	/**
	 * send a sql command
	 * 
	 * @param sql command
	 * @return ResultSet of the sql statement
	 * @throws SQLException
	 */
	public ResultSet sendCommand(String command) throws SQLException {
		Statement st = connection.createStatement();
		st.setQueryTimeout(30);
		ResultSet rs = st.executeQuery(command);
		return rs;
	}
	
	/**
	 * Calculates the column checksum
	 * 
	 * @param tableName
	 *            table name
	 * @param columnName
	 *            column name
	 * @return the checksum
	 * @throws SQLException
	 */
	public long getChecksum(String tableName, String columnName) throws SQLException {
		long chksum = 0;
		String query = "select cksum(\"" + columnName + "\") from " + tableName;
		ResultSet rs = connection.createStatement().executeQuery(query);
		while (rs.next()) {
			chksum += (Long) rs.getObject(1);
		}
		return chksum;
	}

	/**
	 * Executes the given SQL statement and ignore the result
	 * 
	 * @param sql
	 *            SQL Statement
	 * @throws SQLException
	 */
	public void executeSQL(String sql) throws SQLException {
		connection.createStatement().executeQuery(sql);
	}

	public boolean executeSQLNegative(String sql) {
		try {
			connection.createStatement().executeQuery(sql);
		} catch (SQLException e) {
			return true;
		}
		return false;
	}

	/**
	 * Check if table user is system one. THe system tables are defined by "AMI" or "SYSTEM" users.
	 * 
	 * @param definedby
	 *            name of the user defined a table
	 * @return true is table is a user one
	 */
	private boolean isUserTable(String definedby) {
		final String[] systemUsers = { "AMI", "SYSTEM" };

		for (String s : systemUsers) {
			if (s.equals(definedby))
				return false;
		}
		return true;
	}

	/**
	 * Get the list of all user tables as <table name>/<temporary> pairs
	 * 
	 * @return the list of tables
	 * @throws SQLException
	 */
	private List<TableInfo> getUserTables() throws SQLException {

		List<TableInfo> tables = new ArrayList<TableInfo>();
		ResultSet rs = connection.createStatement().executeQuery("show tables");
		while (rs.next()) {
			String name = (String) rs.getObject(nameColTablesPos);
			String definedby = (String) rs.getObject(definedByColTablesPos);
			name = demangleTableName(name);
			if (isUserTable(definedby)) {
				boolean temporary = definedby.equals("TEMPORARY");
				TableInfo tableinfo = new TableInfo(name, temporary);
				//tables.add(getColumnInfo(tableinfo));
				tables.add(tableinfo);
			}
		}
		return tables;
	}

	/**
	 * If AMI DB table name has spaces inside it should be enclosed in back ticks
	 * 
	 * @param name
	 *            the table name
	 * @return resulting table name
	 */
	public static String demangleTableName(String name) {
		name = SH.trimWhitespace(name);
		if (name.contains(" ")) {
			name = "`" + name + "`";
		}
		return name;
	}

	/**
	 * Get the columns definition for particular table
	 * 
	 * @param tableinfo
	 *            the TableInfo instance to update
	 * @return the updated instance of TableInfo
	 * @throws SQLException
	 */
	private TableInfo getColumnInfo(TableInfo tableinfo) throws SQLException {
		ResultSet rs = connection.createStatement().executeQuery("show table " + tableinfo.name);
		while (rs.next()) {
			String name = (String) rs.getObject(1);
			String type = (String) rs.getObject(2);
			Integer position = (Integer) rs.getObject(3);
			Boolean notnull = (Boolean) rs.getObject(4);
			String options = (String) rs.getObject(5);
			tableinfo.columns.add(new ColumnInfo(name, type, position, notnull, options));
		}
		return tableinfo;
	}

	/**
	 * Get the list of all user objects, i.e. triggers,procedures,timers
	 * 
	 * @return the list of names
	 * @throws SQLException
	 */
	private List<String> getUserObjects(String objectName, int definedByColNum) throws SQLException {
		Set<String> names = new HashSet<String>();
		ResultSet rs = connection.createStatement().executeQuery("show " + objectName);
		while (rs.next()) {
			String name = (String) rs.getObject(nameColTablesPos);
			String definedby = (String) rs.getObject(definedByColNum);
			if (isUserTable(definedby)) {
				names.add(name);
			}
		}
		return new ArrayList<String>(names);
	}

	/**
	 * Get the list of all user tables as <table name>/<temporary> pairs
	 * 
	 * @return the list of tables
	 * @throws SQLException
	 */
	public List<String> getUserTableNames() throws SQLException {
		List<TableInfo> tables = getUserTables();
		Set<String> tableNames = new HashSet<String>();
		for (TableInfo ti : tables) {
			tableNames.add(ti.name);
		}
		return new ArrayList<String>(tableNames);
	}

	/**
	 * Drop all the user objects in database
	 * 
	 * @throws SQLException
	 */
	public void dropUserObjects() throws SQLException {
		// Triggers
		List<String> triggers = getUserObjects("triggers", definedByColTriggersPos);
		for (String t : triggers) {
			String query = "drop trigger " + t;
			connection.createStatement().executeQuery(query);
		}

		// Procedures
		List<String> procedures = getUserObjects("procedures", definedByColProceduresPos);
		for (String p : procedures) {
			String query = "drop procedure " + p;
			connection.createStatement().executeQuery(query);
		}

		// Timers
		List<String> timers = getUserObjects("timers", definedByColTimersPos);
		for (String t : timers) {
			String query = "drop timer " + t;
			connection.createStatement().executeQuery(query);
		}

		// Tables
		List<TableInfo> tables = getUserTables();
		for (TableInfo ti : tables) {
			String query = ti.temporary ? "drop table " + ti.name : "drop public table " + ti.name;
			connection.createStatement().executeQuery(query);
		}
	}

	private static class TableInfo {
		String name;
		boolean temporary;
		final List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

		TableInfo(String name, boolean temporary) {
			this.name = name;
			this.temporary = temporary;
		}
	}

	private static class ColumnInfo {
		String name;
		String type;
		int position;
		boolean notnull;
		String options;

		ColumnInfo(String name, String type, int position, boolean notnull, String options) {
			this.name = name;
			this.type = type;
			this.position = position;
			this.notnull = notnull;
			this.options = options;
		}

	}
}
