package com.vortex.agent.dbadapter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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

public class PostgreSqlDbInspector extends DbInspector {

	private static String DRIVER_NAME = "postgres.driver.name";
	private static String URI = "postgres.uri";
	private static String USER_NAME = "postgres.username";
	private static String PASSWD = "postgres.password";
	private static String MASTER_DB = "postgres.db.name";

	@Override
	public Map<String, VortexAgentDbDatabase> inspectDatabase(Connection connection) throws SQLException {

		// TODO has to be done for all the databases in Postgres... - results will be only for current database including schemas.

		Map<String, VortexAgentDbDatabase> r = new HashMap<String, VortexAgentDbDatabase>();
		try {

			Table schemas = exec(connection, "select schema_name from information_schema.SCHEMATA");
			for (Row row : schemas.getRows()) {
				VortexAgentDbDatabase db = nw(VortexAgentDbDatabase.class);
				db.setName(row.get("schema_name", String.class));
				db.setTables(new HashMap<String, VortexAgentDbTable>());
				db.setObjects(new ArrayList<VortexAgentDbObject>());
				db.setPrivileges(new ArrayList<VortexAgentDbPrivilege>());
				r.put(db.getName(), db);
			}

			// ENGINE,unix_timestamp(CREATE_TIME) as 'created',TABLE_COMMENT
			StringBuilder sb = new StringBuilder();
			sb.append("SELECT t.TABLE_SCHEMA as schema,t.TABLE_NAME as name,t.TABLE_TYPE as type, pgd.description as comment " + "FROM pg_catalog.pg_statio_all_tables as st "
					+ "inner join pg_catalog.pg_description pgd " + "on (pgd.objoid=st.relid and pgd.objsubid=0) " + "inner join information_schema.tables t "
					+ "on (t.table_schema=st.schemaname and t.table_name=st.relname)");
			Table tables = exec(connection, sb.toString());
			for (Row row : tables.getRows()) {
				String schemaName = row.get("schema", String.class);
				VortexAgentDbDatabase db = r.get(schemaName);
				if (db == null) {
					LH.info(log, "database not found for table: ", row);
					continue;
				}
				VortexAgentDbTable tb = nw(VortexAgentDbTable.class);
				tb.setColumns(new HashMap<String, VortexAgentDbColumn>());
				//				Long createdTime = row.get("created", Long.class);
				//				if (createdTime != null)
				//					tb.setCreateTime(createdTime);
				tb.setComments(row.get("comment", String.class));
				tb.setName(row.get("name", String.class));
				CH.putOrThrow(db.getTables(), tb.getName(), tb);
			}

			// COLUMN_TYPE
			String query = "SELECT c.table_schema as schema,c.table_name as tableName,c.column_name as name, c.ORDINAL_POSITION,c.IS_NULLABLE,c.DATA_TYPE,c.CHARACTER_MAXIMUM_LENGTH,c.NUMERIC_PRECISION,c.NUMERIC_SCALE, pgd.description as comment "
					+ "FROM pg_catalog.pg_statio_all_tables as st "
					+ "inner join pg_catalog.pg_description pgd "
					+ "on (pgd.objoid=st.relid) "
					+ "inner join information_schema.columns c " + "on (pgd.objsubid=c.ordinal_position and c.table_schema=st.schemaname and c.table_name=st.relname)";
			Table columns = exec(connection, query);

			for (Row row : columns.getRows()) {
				VortexAgentDbColumn cl = nw(VortexAgentDbColumn.class);
				String schemaName = row.get("schema", String.class);
				String tableName = row.get("tablename", String.class);
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
				String columnType = row.get("data_type", String.class);
				cl.setComments(row.get("comment", String.class));
				cl.setName(row.get("name", String.class));
				cl.setPosition(row.get("ordinal_position", Integer.class));
				byte mask = 0;
				if ("YES".equals(row.get("is_nullable", String.class)))
					mask |= VortexAgentDbColumn.MASK_NULLABLE;
				if (columnType.endsWith("unsigned"))
					mask |= VortexAgentDbColumn.MASK_UNSIGNED;

				cl.setMask(mask);
				Short precision = row.get("numeric_precision", Short.class);
				if (precision != null)
					cl.setPrecision(precision);
				Short scale = row.get("numeric_scale", Short.class);
				if (scale != null)
					cl.setScale(scale);
				Short size = row.get("character_maximum_length", Short.class);
				if (size != null)
					cl.setSize(size);

				Tuple2<Byte, Long> type = null;
				String dataType = row.get("data_type", String.class);
				type = DATA_TYPES.get(dataType);
				if (type == null)
					type = DATA_TYPES.get("other");

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

				cl.setDescription("DataType " + dataType);
			}

			// TABLE_NAME+REFERENCED_TABLE_NAME

			Table procedures = exec(
					connection,
					"select "
							+ VortexAgentDbObject.TRIGGER
							+ " as type,TRIGGER_SCHEMA as schema,TRIGGER_NAME as name,ACTION_CONDITION || ACTION_STATEMENT as definition from information_schema.TRIGGERS UNION ALL select "
							+ VortexAgentDbObject.PROCEDURE + ",ROUTINE_SCHEMA,ROUTINE_NAME,ROUTINE_DEFINITION FROM information_schema.ROUTINES UNION ALL select "
							+ VortexAgentDbObject.CONSTRAINT
							+ ",CONSTRAINT_SCHEMA,CONSTRAINT_NAME,DELETE_RULE || UPDATE_RULE || MATCH_OPTION from information_schema.REFERENTIAL_CONSTRAINTS");
			for (Row row : procedures.getRows()) {
				byte type = row.get("type", Byte.class);
				String schema = row.get("schema", String.class);
				VortexAgentDbDatabase db = r.get(schema);
				if (db == null) {
					LH.info(log, "database not found for definition: ", row);
					continue;
				}
				String name = row.get("name", String.class);
				String definition = row.get("definition", String.class);
				VortexAgentDbObject def = nw(VortexAgentDbObject.class);
				def.setDefinition(definition);
				def.setName(name);
				def.setType(type);
				db.getObjects().add(def);
			}

			Table privileges = exec(connection, "select GRANTEE,TABLE_SCHEMA,TABLE_CATALOG,PRIVILEGE_TYPE from information_schema.table_privileges");
			Map<Tuple3<String, String, String>, VortexAgentDbPrivilege> m = new HashMap<Tuple3<String, String, String>, VortexAgentDbPrivilege>();
			for (Row row : privileges.getRows()) {
				String user = row.get("grantee", String.class);
				String schema = row.get("table_schema", String.class);
				String table = row.get("table_catalog", String.class);
				String privilege = row.get("privilege_type", String.class);
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

	static final Map<String, Tuple2<Byte, Long>> DATA_TYPES = new HashMap<String, Tuple2<Byte, Long>>();
	static final Map<String, Integer> PRIV_TYPES = new HashMap<String, Integer>();
	public static final String ID = "PostgreSql";
	static {
		DATA_TYPES.put("tinyint", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 1L));
		DATA_TYPES.put("smallint", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 2L));
		DATA_TYPES.put("mediumint", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 3L));
		DATA_TYPES.put("int", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("integer", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 4L));
		DATA_TYPES.put("bigint", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 8L));
		DATA_TYPES.put("float", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 4L));
		DATA_TYPES.put("double", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("double precision", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("real", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FLOAT, 8L));
		DATA_TYPES.put("decimal", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FIXEDPOINT, null));
		DATA_TYPES.put("numeric", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_FIXEDPOINT, null));
		DATA_TYPES.put("date", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_DATE, 3L));
		DATA_TYPES.put("datetime", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_DATETIME, 8L));
		DATA_TYPES.put("timestamp", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_TIMESTAMP, 4L));
		DATA_TYPES.put("time", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_TIME, 3L));
		DATA_TYPES.put("year", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_YEAR, 1L));
		DATA_TYPES.put("char", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_CHAR, null));
		DATA_TYPES.put("varchar", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, null));
		DATA_TYPES.put("tinyblock", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 8 - 1));
		DATA_TYPES.put("tinytext", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 8 - 1));
		DATA_TYPES.put("blob", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_VARCHAR, 1L << 16 - 1));
		DATA_TYPES.put("text", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 16 - 1));
		DATA_TYPES.put("mediumblob", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 24 - 1));
		DATA_TYPES.put("mediumtext", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 24 - 1));
		DATA_TYPES.put("longblob", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		DATA_TYPES.put("longtext", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		DATA_TYPES.put("enum", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_SET, null));
		DATA_TYPES.put("set", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_ENUM, null));
		DATA_TYPES.put("other", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_OTHER, null));

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
			Class.forName(driverName);
			String dbName = props.getRequired(MASTER_DB);
			String user = props.getRequired(USER_NAME);
			String pwd = props.getRequired(PASSWD);
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

	/*public static Connection getConnection() {
		try {
			Class.forName ("org.postgresql.Driver");
			return DriverManager.getConnection ("jdbc:postgresql://localhost/TestDb", "postgres", "simple");
		}
		catch (Exception sqlEx) {
			 sqlEx.printStackTrace ();
		} 
		
		return null;
	}*/

	/*public static void main (String [] args) {
		 Connection conn = null;
		 try {
			 conn = getConnection(null);
			 PostgreSqlDbInspector dbInspector = new PostgreSqlDbInspector();
			 dbInspector.inspectDatabase(conn);
		 } catch (Exception sqlEx) {
			 sqlEx.printStackTrace ();
		 } finally {
			 try {
				 if (conn != null) conn.close();
			 } catch (SQLException e) {
				 e.printStackTrace ();
			 }
		 }
	 }*/

}
