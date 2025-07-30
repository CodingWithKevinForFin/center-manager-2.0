package com.f1.ami.plugins.db2;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Character;
import com.f1.utils.casters.Caster_String;

public class AmiDb2DatasourceAdapter extends JdbcAdapter {
	private static final String DB2_INFOTABLE = "SYSIBM.SYSTABLES";
	private static final String DB2_INFOCOLUMNS = "SYSIBM.SYSCOLUMNS";
	private static final String DB2_TABLESCHEMA = "CREATOR";
	private static final String DB2_TABLENAME = "NAME";
	private static final String DB2_COLNAME = "NAME";
	private static final String DB2_COLTYPE = "COLTYPE";
	private static final String DB2_COLTABLENAME = "TBNAME";
	private static final String DB2_COLTABLESCHEMA = "TBCREATOR";

	private static final String DB2_TABLESCHEMA_QUERY = "SELECT " + DB2_TABLESCHEMA + ", " + DB2_TABLENAME + ", TYPE FROM " + DB2_INFOTABLE;
	private static final String DB2_COLSCHEMA_QUERY = "SELECT " + DB2_COLNAME + ", " + DB2_COLTYPE + " FROM " + DB2_INFOCOLUMNS + " WHERE ";

	private static final Logger log = LH.get();
	private static final Map<String, String> OPERATIONS = CH.m("==", "=", "&&", "AND", "||", "OR", "*=", "RLIKE");

	private static final String SELECT_ALL_FROM_CLAUSE = "SELECT * FROM ";

	public static Map<String, String> buildOptions() {
		Map<String, String> r = JdbcAdapter.buildOptions();
		return r;
	}

	@Override
	protected String buildJdbcDriverClass() {
		return "com.ibm.db2.jcc.DB2Driver";
	}

	@Override
	protected Map<String, Object> buildJdbcArguments() {
		return Collections.EMPTY_MAP;
	}

	@Override
	protected String buildJdbcUrlSubprotocol() {
		return "jdbc:db2://";
	}

	@Override
	protected String buildJdbcUrl() {
		return getUrl() + ":user=" + getUsernameEncoded() + ";password=****;";
	}

	@Override
	protected String buildJdbcUrlPassword() {
		return super.getPasswordEncoded();
	}

	@Override
	protected String createLimitClause(String select, int limit) {
		return select + " FETCH FIRST " + limit + " ROWS ONLY";
	}

	@Override
	protected StringBuilder createLimitClause2(StringBuilder query, int limit, Integer offset) {
		//TODO: offset if necessary
		return query.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
	}

	@Override
	protected String getSchemaName(StringBuilder sb, AmiDatasourceTable table) {
		return getSchemaName('.', '"', sb, table.getName(), table.getCollectionName());
	}
	@Override
	protected StringBuilder createShowTablesQuery(StringBuilder sb, int limit) {
		sb.append(DB2_TABLESCHEMA_QUERY);
		createLimitClause2(sb, limit, null);
		return sb;
	}

	@Override
	protected AmiDatasourceTable createAmiDatasourceTable(Row row, StringBuilder sb) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		//Get name of Table and the Collection(Schema,Owner) it's in
		String name = SH.trim(row.get(DB2_TABLENAME, Caster_String.INSTANCE));
		String collectionName = SH.trim(row.get(DB2_TABLESCHEMA, Caster_String.INSTANCE));
		char type = row.get("TYPE", Caster_Character.INSTANCE);

		if (type == 'V')
			return null;
		if ("SYSIBM".equals(collectionName))
			return null;
		if ("SYSTOOLS".equals(collectionName))
			return null;

		table.setName(name);
		table.setCollectionName(collectionName);

		String fullname = getSchemaName(SH.clear(sb), table);
		createSelectQuery(sb, fullname);
		table.setCustomQuery(SH.toStringAndClear(sb));

		return table;
	}

}
