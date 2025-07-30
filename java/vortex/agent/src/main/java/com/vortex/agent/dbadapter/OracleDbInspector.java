package com.vortex.agent.dbadapter;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.CH;
import com.f1.utils.DBH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;

public class OracleDbInspector extends DbInspector {

	private static String DRIVER_NAME = "oracle.driver.name";
	private static String URI = "oracle.uri";
	private static String USER_NAME = "oracle.username";
	private static String PASSWD = "oracle.password";
	private static String MASTER_DB = "oracle.db.name";

	public Map<String, VortexAgentDbDatabase> inspectDatabase(Connection connection) throws SQLException {

		Map<String, VortexAgentDbDatabase> r = new HashMap<String, VortexAgentDbDatabase>();
		StringBuilder sink = new StringBuilder();
		Map<String, Timestamp> timestampsBySchema = new HashMap<String, Timestamp>();

		try {

			SH.clear(sink).append("SELECT * FROM SYS.ALL_USERS order by USERNAME");
			Table schemas = exec(connection, sink.toString());
			for (Row row : schemas.getRows()) {
				String name = row.get("USERNAME", String.class);

				timestampsBySchema.put(name, row.get("CREATED", Timestamp.class));

				VortexAgentDbDatabase db = nw(VortexAgentDbDatabase.class);
				db.setName(name);
				db.setTables(new HashMap<String, VortexAgentDbTable>());
				db.setObjects(new ArrayList<VortexAgentDbObject>());
				db.setPrivileges(new ArrayList<VortexAgentDbPrivilege>());
				r.put(name, db);
			}

			// TABLE_TYPE,ENGINE
			SH.clear(sink).append("select t.table_name, t.OWNER, c.comments from sys.all_all_tables t, SYS.ALL_TAB_COMMENTS c where t.table_name=c.table_name order by TABLE_NAME");
			Table tables = exec(connection, sink.toString());
			for (Row row : tables.getRows()) {

				String schemaName = row.get("OWNER", String.class);
				VortexAgentDbDatabase db = r.get(schemaName);
				if (db == null) {
					LH.info(log, "database not found for table: ", row);
					continue;
				}

				String name = row.get("TABLE_NAME", String.class);
				String comment = row.get("COMMENTS", String.class);
				Map<String, VortexAgentDbTable> dbTables = db.getTables();
				VortexAgentDbTable tb = dbTables.get(name);
				if (tb == null) {
					tb = nw(VortexAgentDbTable.class);
					tb.setName(name);
					tb.setColumns(new HashMap<String, VortexAgentDbColumn>());
					Timestamp createdTime = timestampsBySchema.get(schemaName);
					// Long createdTime = row.get("created", Long.class);
					if (createdTime != null)
						tb.setCreateTime(createdTime.getTime());

					if (comment != null)
						tb.setComments(comment);

					dbTables.put(name, tb);
				} else if (comment != null)
					tb.setComments(tb.getComments() + SH.NEWLINE + comment);
			}

			// ORDINAL_POSITION,COLUMN_COMMENT,CHARACTER_MAXIMUM_LENGTH,column_type
			SH.clear(sink)
					.append("SELECT c1.OWNER,c1.COLUMN_NAME,c1.TABLE_NAME,c1.NULLABLE,c1.DATA_TYPE,c1.DATA_PRECISION,c1.DATA_SCALE,c2.COMMENTS FROM SYS.ALL_TAB_COLUMNS c1, sys.all_col_comments c2 where c1.table_name=c2.table_name and c1.column_name=c2.column_name ");
			Table columns = exec(connection, sink.toString());

			for (Row row : columns.getRows()) {
				String schemaName = row.get("OWNER", String.class);
				String tableName = row.get("TABLE_NAME", String.class);
				VortexAgentDbDatabase db = r.get(schemaName);
				if (db == null) {
					LH.info(log, "db not found for column: ", row);
					continue;
				}
				VortexAgentDbTable tb = db.getTables().get(tableName);
				if (tb == null) {
					LH.info(log, "table not found for column: ", row);
					continue;
				}

				String colName = row.get("COLUMN_NAME", String.class);
				String comments = row.get("COMMENTS", String.class);

				Map<String, VortexAgentDbColumn> dbCols = tb.getColumns();

				VortexAgentDbColumn cl = dbCols.get(colName);
				if (cl == null) {
					cl = nw(VortexAgentDbColumn.class);
					// String columnType = row.get("COLUMN_TYPE", String.class);
					String columnType = row.get("DATA_TYPE", String.class);
					// cl.setPosition(row.get("ORDINAL_POSITION", Integer.class));
					byte mask = 0;
					if ("YES".equals(row.get("NULLABLE", String.class)))
						mask |= VortexAgentDbColumn.MASK_NULLABLE;
					if (columnType.endsWith("unsigned"))
						mask |= VortexAgentDbColumn.MASK_UNSIGNED;

					cl.setMask(mask);
					BigDecimal precision = row.get("DATA_PRECISION", BigDecimal.class);
					if (precision != null)
						cl.setPrecision(precision.shortValue());
					BigDecimal scale = row.get("DATA_SCALE", BigDecimal.class);
					if (scale != null)
						cl.setScale(scale.shortValue());
					cl.setName(colName);

					// Short size = row.get("CHARACTER_MAXIMUM_LENGTH", Short.class);
					// if (size != null)
					// cl.setSize(size);
					Tuple2<Byte, Long> type = null;
					String dataType = row.get("DATA_TYPE", String.class);

					if (dataType.startsWith("TIMESTAMP")) {
						type = DATA_TYPES.get("TIMESTAMP");
					} else if (dataType.startsWith("INTERVAL DAY")) {
						type = DATA_TYPES.get("INTERVAL DAY");
					} else if (dataType.startsWith("INTERVAL YEAR")) {
						type = DATA_TYPES.get("INTERVAL YEAR");
					} else {
						type = DATA_TYPES.get(dataType);
						if (type == null)
							type = DATA_TYPES.get("OTHER");
					}

					cl.setType(type.getA());
					if (type.getB() != null)
						cl.setSize(type.getB());
					if (cl.getType() == VortexAgentDbColumn.TYPE_ENUM || cl.getType() == VortexAgentDbColumn.TYPE_SET) {
						String[] parts = SH.split(',', SH.beforeLast(SH.afterFirst(columnType, '('), ')'));
						Set s = new HashSet<String>();
						for (String part : parts)
							s.add(SH.trim('\'', part));
						cl.setPermissibleValues(SH.join(',', CH.sort(s)));
					}

					if (comments != null)
						cl.setComments(comments);

					cl.setDescription("DataType " + dataType);

					dbCols.put(colName, cl);
				} else if (comments != null)
					cl.setComments(cl.getComments() + SH.NEWLINE + comments);
			}

			createAgentDbObjects(r, exec(connection, "select 2 type,OWNER schema,TRIGGER_NAME name,TRIGGER_TYPE||'-'||ACTION_TYPE definition from SYS.ALL_TRIGGERS"));

			createAgentDbObjects(r,
					exec(connection, "select 1 type,OWNER schema,OBJECT_NAME name,SUBOBJECT_NAME definition from SYS.ALL_OBJECTS where upper(OBJECT_TYPE) = upper('PROCEDURE')"));

			createAgentDbObjects(r,
					exec(connection, "select 3 type, OWNER schema,CONSTRAINT_NAME name,DELETE_RULE||'-'||TABLE_NAME||'-'||R_CONSTRAINT_NAME definition from SYS.ALL_CONSTRAINTS"));

			/*
			 * Table<Row> procedures = exec( connection, "select " + AgentDbObject.TRIGGER +
			 * " as 'type',TRIGGER_SCHEMA as 'schema',TRIGGER_NAME as 'name',ACTION_CONDITION+ACTION_STATEMENT as 'definition' from information_schema.TRIGGERS UNION ALL select " +
			 * AgentDbObject.PROCEDURE + ",ROUTINE_SCHEMA,ROUTINE_NAME,ROUTINE_DEFINITION FROM information_schema.ROUTINES UNION ALL select " + AgentDbObject.CONSTRAINT +
			 * ",CONSTRAINT_SCHEMA,CONSTRAINT_NAME,DELETE_RULE+UPDATE_RULE+MATCH_OPTION+TABLE_NAME+REFERENCED_TABLE_NAME from information_schema.REFERENTIAL_CONSTRAINTS");
			 */

			SH.clear(sink).append("select GRANTEE,OWNER,TABLE_NAME,PRIVILEGE from DBA_TAB_PRIVS");
			Table privileges = exec(connection, sink.toString());

			// Table<Row> privileges = exec(connection, "select GRANTEE,OWNER,TABLE_NAME,PRIVILEGE from information_schema.SCHEMA_PRIVILEGES");
			Map<Tuple3<String, String, String>, VortexAgentDbPrivilege> m = new HashMap<Tuple3<String, String, String>, VortexAgentDbPrivilege>();
			for (Row row : privileges.getRows()) {
				String user = row.get("GRANTEE", String.class);
				String schema = row.get("OWNER", String.class);
				String table = row.get("TABLE_NAME", String.class);
				String privilege = row.get("PRIVILEGE", String.class);
				Integer type = PRIV_TYPES.get(privilege);
				Tuple3<String, String, String> k = new Tuple3<String, String, String>(user, schema, table);
				VortexAgentDbPrivilege pr = m.get(k);
				if (pr == null) {
					pr = nw(VortexAgentDbPrivilege.class);
					pr.setUser(user);
					if (table != null)
						pr.setTableName(table);
					m.put(k, pr);
				}
				if (type == null) {
					type = PRIV_TYPES.get("OTHER");
					pr.setDescription("PRIVILEGE " + privilege);
				}
				pr.setType(pr.getType() | type);
			}
			for (Entry<Tuple3<String, String, String>, VortexAgentDbPrivilege> e : m.entrySet()) {
				String schema = e.getKey().getB();
				VortexAgentDbDatabase db = r.get(schema);
				if (db == null) {
					LH.info(log, "database not found for privileges: ", e);
					continue;
				}
				db.getPrivileges().add(e.getValue());
			}
		} finally {
			IOH.close(connection);
		}
		return r;

	}

	private void createAgentDbObjects(Map<String, VortexAgentDbDatabase> r, Table procedures) {
		for (Row row : procedures.getRows()) {
			byte type = row.get("TYPE", Byte.class);
			String schema = row.get("SCHEMA", String.class);
			VortexAgentDbDatabase db = r.get(schema);
			if (db == null) {
				LH.info(log, "database not found for definition: ", row);
				continue;
			}
			String name = row.get("NAME", String.class);
			String definition = row.get("DEFINITION", String.class);
			VortexAgentDbObject def = nw(VortexAgentDbObject.class);
			def.setDefinition(definition);
			def.setName(name);
			def.setType(type);
			db.getObjects().add(def);
		}
	}

	static final Map<String, Tuple2<Byte, Long>> DATA_TYPES = new HashMap<String, Tuple2<Byte, Long>>();
	static final Map<String, Integer> PRIV_TYPES = new HashMap<String, Integer>();
	public static final String ID = "Oracle";
	static {
		// DATA_TYPES.put("tinyint", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_INT, 1L));
		// DATA_TYPES.put("smallint", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_INT, 2L));
		// DATA_TYPES.put("mediumint", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_INT, 3L));
		DATA_TYPES.put("NUMBER", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("INT", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("INTEGER", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("SMALLINT", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("LONG", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 8L));
		DATA_TYPES.put("PLS_INTEGER", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 8L));
		DATA_TYPES.put("FLOAT", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 4L));
		// DATA_TYPES.put("LONG", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("DOUBLE PRECISION", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("REAL", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("DECIMAL", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FIXEDPOINT, null));
		DATA_TYPES.put("NUMERIC", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FIXEDPOINT, null));
		DATA_TYPES.put("DATE", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_DATE, 3L));
		DATA_TYPES.put("datetime", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_DATETIME, 8L));
		DATA_TYPES.put("TIMESTAMP", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_TIMESTAMP, 4L));
		DATA_TYPES.put("INTERVAL DAY", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_TIME, 11L));
		DATA_TYPES.put("INTERVAL YEAR", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_YEAR, 5L));

		DATA_TYPES.put("CHAR", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_CHAR, null));
		DATA_TYPES.put("NCHAR", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_CHAR, null));
		DATA_TYPES.put("VARCHAR", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));
		DATA_TYPES.put("NVARCHAR2", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));
		DATA_TYPES.put("VARCHAR2", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));
		DATA_TYPES.put("ROWID", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));
		DATA_TYPES.put("XMLTYPE", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));

		// DATA_TYPES.put("tinyblock", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 8 - 1));
		// DATA_TYPES.put("tinytext", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 8 - 1));
		DATA_TYPES.put("RAW", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("LONG RAW", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("BLOB", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("CLOB", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("NCLOB", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("BFILE", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		// DATA_TYPES.put("text", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		// DATA_TYPES.put("mediumblob", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 24 - 1));
		// DATA_TYPES.put("mediumtext", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 24 - 1));
		// DATA_TYPES.put("longblob", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		// DATA_TYPES.put("longtext", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		// DATA_TYPES.put("enum", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_SET, null));
		// DATA_TYPES.put("set", new Tuple2<Byte, Long>(AgentDbColumn.TYPE_ENUM, null));

		DATA_TYPES.put("OTHER", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_OTHER, null));

		PRIV_TYPES.put("ALTER", VortexAgentDbPrivilege.ALTER);
		PRIV_TYPES.put("ALTER ROUTINE", VortexAgentDbPrivilege.ALTER_ROUTINE);
		PRIV_TYPES.put("CREATE", VortexAgentDbPrivilege.CREATE);
		PRIV_TYPES.put("CREATE ROUTINE", VortexAgentDbPrivilege.CREATE_ROUTINE);
		PRIV_TYPES.put("CREATE TEMPORARY TABLES", VortexAgentDbPrivilege.CREATE_TEMP_TABLES);
		PRIV_TYPES.put("CREATE VIEW", VortexAgentDbPrivilege.CREATE_VIEW);
		PRIV_TYPES.put("DELETE", VortexAgentDbPrivilege.DELETE);
		PRIV_TYPES.put("EXECUTE", VortexAgentDbPrivilege.EXECUTE);
		PRIV_TYPES.put("DROP", VortexAgentDbPrivilege.DROP);
		PRIV_TYPES.put("EVENT", VortexAgentDbPrivilege.EVENT);
		PRIV_TYPES.put("INDEX", VortexAgentDbPrivilege.INDEX);
		PRIV_TYPES.put("INSERT", VortexAgentDbPrivilege.INSERT);
		PRIV_TYPES.put("LOCK TABLES", VortexAgentDbPrivilege.LOCK_TABLES);
		PRIV_TYPES.put("REFERENCES", VortexAgentDbPrivilege.REFERENCES);
		PRIV_TYPES.put("SELECT", VortexAgentDbPrivilege.SELECT);
		PRIV_TYPES.put("SHOW VIEW", VortexAgentDbPrivilege.SHOW_VIEW);
		PRIV_TYPES.put("TRIGGER", VortexAgentDbPrivilege.TRIGGER);
		PRIV_TYPES.put("UPDATE", VortexAgentDbPrivilege.UPDATE);
		PRIV_TYPES.put("OTHER", VortexAgentDbPrivilege.OTHER);
	}

	private Table exec(Connection connection, String sql) throws SQLException {
		return (Table) DBH.toTable(connection.prepareStatement(sql).executeQuery());
	}

	public static Connection getConnection(PropertyController props) {
		try {
			String driverName = props.getRequired(DRIVER_NAME);
			String dbName = props.getRequired(MASTER_DB);
			String user = props.getRequired(USER_NAME);
			String pwd = props.getRequired(PASSWD);
			Class.forName(driverName);
			return DriverManager.getConnection(getURI(dbName), user, pwd);
		} catch (Exception sqlEx) {
			sqlEx.printStackTrace();
		}

		return null;
	}

	private static String getURI(String dbName) {
		StringBuilder builder = new StringBuilder();
		builder.append(URI).append("/").append(dbName);

		return builder.toString();
	}

}
