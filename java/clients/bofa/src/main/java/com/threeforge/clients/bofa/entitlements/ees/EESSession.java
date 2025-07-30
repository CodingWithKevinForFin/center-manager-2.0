package com.threeforge.clients.bofa.entitlements.ees;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.ami.amicommon.customobjects.AmiScriptAccessible;
import com.f1.utils.LH;
import com.f1.utils.OH;

@AmiScriptAccessible(name = "BofaEES")
public class EESSession implements Runnable {
	private static Logger log = LH.get();
	private boolean debug;
	private String subject;
	private String host;
	private String port;
	private String database;
	private String dbUser;
	private String dbPassword;
	private String dbTableName;
	private List<String> dbTableColumns;
	private List<String> permittedValues;
	private EESSession eesSession;
	private int pollingIntervalSeconds;
	private volatile boolean running = false;

	// EES resources
	private List<String> groups;
	private List<String> roles;
	private boolean isInRole;
	private boolean isInGroup;
	private List<String> regions;
	private List<String> lobs;
	private List<String> groupRoles;

	public EESSession(String subject, String host, String port, String database, String dbUser, String dbPassword, String dbTableName, List<String> dbTableColumns,
			int pollingIntervalSeconds, boolean debug) {
		this.pollingIntervalSeconds = pollingIntervalSeconds;
		this.subject = subject;
		this.host = host;
		this.port = port;
		this.database = database;
		this.dbUser = dbUser;
		this.dbPassword = dbPassword;
		this.dbTableName = dbTableName;
		this.dbTableColumns = dbTableColumns;
		this.pollingIntervalSeconds = pollingIntervalSeconds;
		this.debug = debug;

		this.groups = new ArrayList<String>();
		this.roles = new ArrayList<String>();
		this.lobs = new ArrayList<String>();
		this.regions = new ArrayList<String>();
		this.groupRoles = new ArrayList<String>();
	}

	@AmiScriptAccessible(name = "getSubject", params = {})
	public String getSubject() {
		return this.subject;
	}
	@AmiScriptAccessible(name = "getRoles", params = {})
	public List<String> getRoles() {
		return this.roles;
	}
	@AmiScriptAccessible(name = "getGroups", params = {})
	public List<String> getGroups() {
		return this.groups;
	}
	@AmiScriptAccessible(name = "getRegions", params = {})
	public List<String> getRegions() {
		return this.regions;
	}
	@AmiScriptAccessible(name = "getlobs", params = {})
	public List<String> getlobs() {
		return this.lobs;
	}
	@AmiScriptAccessible(name = "getGroupRoles", params = {})
	public List<String> getGroupRoles() {
		return this.groupRoles;
	}
	@AmiScriptAccessible(name = "getPermittedValues", params = {})
	public List<String> getPermittedValues() {
		return this.permittedValues;
	}
	@AmiScriptAccessible(name = "getPollingIntervalSeconds", params = {})
	public int getPollingIntervalSeconds() {
		return this.pollingIntervalSeconds;
	}
	@AmiScriptAccessible(name = "isInRole", params = { "role" })
	public boolean isInRole(String role) {
		//TODO: ask mark for clarification. search in roles or permitted values?
		return this.roles.contains(role);
	}
	@AmiScriptAccessible(name = "isInGroup", params = { "group" })
	public boolean isInGroup(String group) {
		//TODO: ask mark for clarification saerch in groups or permitted values?
		return this.groups.contains(group);
	}
	public void setRunning(boolean running) {
		this.running = running;
	}
	public void run() {
		this.running = true;
		while (running) {
			if (!this.running)
				break;
			runInner();
			OH.sleep(5000);
		}
	}
	public void runInner() {
		try {
			if (pollEES()) {
				LH.info(log, "Polling EES sucessful for " + this.subject);
			} else {
				LH.info(log, "Polling EES failed for " + this.subject);
			}
		} catch (Exception e) {
		}
	}
	private boolean pollEES() {
		Connection conn = null;
		try {
			conn = getConnection();
			ResultSet rs = conn.createStatement().executeQuery(getQuery());
			if (cacheDataFromDB(rs)) {
				LH.info(log, "successfully cached data from AMIDB");
				return true;
			} else
				LH.info(log, "error caching data from AMIDB");
		} catch (SQLException e) {
			LH.severe(log, "Error establishing jdbc connection with amidb ", e);
		} catch (ClassNotFoundException e) {
			LH.severe(log, "Could not find amidb jdbc driver class", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				LH.info(log, "Error closing database connection", e);
			}
		}
		return false;
	}
	private boolean cacheDataFromDB(ResultSet rs) {

		try {
			clearCachedData();
			while (rs.next()) {
				groups.add(rs.getString("usergroup_name"));
				roles.add(rs.getString("role_name"));
				regions.add(rs.getString("region"));
				lobs.add(rs.getString("lob"));
				groupRoles.add(rs.getString("grouprole"));
			}
			return true;
		} catch (SQLException e) {
			LH.severe(log, "Failed to read data from resultset " + rs);
			return false;
		}
	}
	private void clearCachedData() {
		groups.clear();
		roles.clear();
		regions.clear();
		lobs.clear();
		groupRoles.clear();
	}
	private String getQuery() {
		String query = "USE ds=" + database + " EXECUTE SELECT usernbk, region, lob, usergroup_name, role_name, grouprole FROM " + dbTableName + " WHERE usernbk == \""
				+ this.subject + "\";";
		return query;

	}
	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.f1.ami.amidb.jdbc.AmiDbJdbcDriver");
		String url = getJdbcUrl();
		return DriverManager.getConnection(url);
	}
	private String getJdbcUrl() {
		return "jdbc:amisql:" + this.host + ":" + this.port + "?username=" + this.dbUser + "&password=" + this.dbPassword;
	}
}
