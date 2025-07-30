package com.vortex.agent.dbadapter;

import java.sql.Connection;
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
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;
import com.f1.vortexcommon.msg.agent.VortexAgentDbColumn;
import com.f1.vortexcommon.msg.agent.VortexAgentDbDatabase;
import com.f1.vortexcommon.msg.agent.VortexAgentDbObject;
import com.f1.vortexcommon.msg.agent.VortexAgentDbPrivilege;
import com.f1.vortexcommon.msg.agent.VortexAgentDbTable;

public class MysqlDbInspector extends DbInspector {

	@Override
	public Map<String, VortexAgentDbDatabase> inspectDatabase(Connection connection) throws SQLException {

		Map<String, VortexAgentDbDatabase> r = new HashMap<String, VortexAgentDbDatabase>();
		try {

			Table schemas = exec(connection, "select SCHEMA_NAME as 'schema' from information_schema.SCHEMATA");
			for (Row row : schemas.getRows()) {
				VortexAgentDbDatabase db = nw(VortexAgentDbDatabase.class);
				db.setName(row.get("schema", String.class));
				db.setTables(new HashMap<String, VortexAgentDbTable>());
				db.setObjects(new ArrayList<VortexAgentDbObject>());
				db.setPrivileges(new ArrayList<VortexAgentDbPrivilege>());
				r.put(db.getName(), db);
			}

			Table tables = exec(connection,
					"select TABLE_SCHEMA as 'schema',TABLE_NAME as 'name',TABLE_TYPE,ENGINE,unix_timestamp(CREATE_TIME) as 'created',TABLE_COMMENT from information_schema.TABLES");
			for (Row row : tables.getRows()) {
				String schemaName = row.get("schema", String.class);
				VortexAgentDbDatabase db = r.get(schemaName);
				if (db == null) {
					LH.info(log, "database not found for table: ", row);
					continue;
				}
				VortexAgentDbTable tb = nw(VortexAgentDbTable.class);
				tb.setColumns(new HashMap<String, VortexAgentDbColumn>());
				Long createdTime = row.get("created", Long.class);
				if (createdTime != null)
					tb.setCreateTime(createdTime);
				tb.setComments(row.get("TABLE_COMMENT", String.class));
				tb.setName(row.get("name", String.class));
				CH.putOrThrow(db.getTables(), tb.getName(), tb);
			}

			Table columns = exec(
					connection,
					"select TABLE_SCHEMA as 'schema',COLUMN_NAME as 'name',TABLE_NAME,ORDINAL_POSITION,IS_NULLABLE,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,NUMERIC_PRECISION,NUMERIC_SCALE,COLUMN_TYPE,COLUMN_COMMENT from information_schema.COLUMNS");

			for (Row row : columns.getRows()) {
				VortexAgentDbColumn cl = nw(VortexAgentDbColumn.class);
				String schemaName = row.get("schema", String.class);
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
				String columnType = row.get("COLUMN_TYPE", String.class);
				cl.setComments(row.get("COLUMN_COMMENT", String.class));
				cl.setName(row.get("name", String.class));
				cl.setPosition(row.get("ORDINAL_POSITION", Integer.class));
				byte mask = 0;
				if ("YES".equals(row.get("IS_NULLABLE", String.class)))
					mask |= VortexAgentDbColumn.MASK_NULLABLE;
				if (columnType.endsWith("unsigned"))
					mask |= VortexAgentDbColumn.MASK_UNSIGNED;

				cl.setMask(mask);
				Short precision = row.get("NUMERIC_PRECISION", Short.class);
				if (precision != null)
					cl.setPrecision(precision);
				Short scale = row.get("NUMERIC_SCALE", Short.class);
				if (scale != null)
					cl.setScale(scale);
				Short size = row.get("CHARACTER_MAXIMUM_LENGTH", Short.class);
				if (size != null)
					cl.setSize(size);
				Tuple2<Byte, Long> type = CH.getOrThrow(DATA_TYPES, row.get("DATA_TYPE", String.class));
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
				CH.putOrThrow(tb.getColumns(), cl.getName(), cl);
			}

			Table procedures = exec(
					connection,
					"select "
							+ VortexAgentDbObject.TRIGGER
							+ " as 'type',TRIGGER_SCHEMA as 'schema',TRIGGER_NAME as 'name',concat( ACTION_CONDITION,'.',ACTION_STATEMENT) as 'definition' from information_schema.TRIGGERS UNION ALL select "
							+ VortexAgentDbObject.PROCEDURE
							+ ",ROUTINE_SCHEMA,ROUTINE_NAME as 'name',ROUTINE_DEFINITION FROM information_schema.ROUTINES UNION ALL select "
							+ VortexAgentDbObject.CONSTRAINT
							+ ",CONSTRAINT_SCHEMA,CONSTRAINT_NAME,concat( DELETE_RULE,'.',UPDATE_RULE,'.',MATCH_OPTION,'.',TABLE_NAME,'.',REFERENCED_TABLE_NAME) from information_schema.REFERENTIAL_CONSTRAINTS UNION ALL select "
							+ VortexAgentDbObject.INDEX
							+ ",INDEX_SCHEMA,concat(TABLE_NAME,'.',INDEX_NAME,'.',cast(SEQ_IN_INDEX as char)) as 'name',concat( (case NON_UNIQUE when 1 then 'not unique' else 'unique'  end),' ',cast(SEQ_IN_INDEX as char),'.',COLUMN_NAME,' (',cast(INDEX_TYPE as char),')')  as 'definition' from information_schema.STATISTICS");
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

			Table privileges = exec(connection, "select GRANTEE,TABLE_SCHEMA,TABLE_CATALOG,PRIVILEGE_TYPE from information_schema.SCHEMA_PRIVILEGES");
			Map<Tuple3<String, String, String>, VortexAgentDbPrivilege> m = new HashMap<Tuple3<String, String, String>, VortexAgentDbPrivilege>();
			for (Row row : privileges.getRows()) {
				String user = row.get("GRANTEE", String.class);
				String schema = row.get("TABLE_SCHEMA", String.class);
				String table = row.get("TABLE_CATALOG", String.class);
				Integer type = CH.getOrThrow(PRIV_TYPES, row.get("PRIVILEGE_TYPE", String.class));
				Tuple3<String, String, String> k = new Tuple3<String, String, String>(user, schema, table);
				VortexAgentDbPrivilege pr = m.get(k);
				if (pr == null) {
					pr = nw(VortexAgentDbPrivilege.class);
					pr.setUser(user);
					if (table != null)
						pr.setTableName(table);
					m.put(k, pr);
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
	public static final String ID = "MYSQL";
	static {
		DATA_TYPES.put("bit", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_INT, 1L));
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
		DATA_TYPES.put("varbinary", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_BLOB, 1L << 32 - 1));
		DATA_TYPES.put("enum", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_SET, null));
		DATA_TYPES.put("set", new Tuple2<Byte, Long>(VortexAgentDbColumn.TYPE_ENUM, null));

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
	}

	private Table exec(Connection connection, String sql) throws SQLException {
		return (Table) DBH.toTable(connection.prepareStatement(sql).executeQuery());
	}

}
