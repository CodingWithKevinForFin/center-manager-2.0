package com.f1.ami.amidb.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicTable;

public class AmiDbJdbcDatabaseMetaData implements DatabaseMetaData {

	private static final int DRIVER_MAJOR_VERSION = 1;
	private static final int DRIVER_MINOR_VERSION = 0;
	private static final String DRIVER_VERSION = DRIVER_MAJOR_VERSION + "." + DRIVER_MINOR_VERSION;
	private static final String PRODUCT_NAME = "AMI";
	private static final String PRODUCT_VERSION = DRIVER_VERSION;
	private static final String DRIVER_NAME = "AMI";
	private static final String RESERVED_WORD = SH.replaceAll(
			"ADD ALTER ANALYZE AND AS ASC BEFORE BINARY BOOLEAN BREAK BY CALL CASE CATCH CREATE CONCURRENT CONTINUE DEFAULT DELETE DESC DESCRIBE DISABLE DO DOUBLE DROP ELSE ENABLE EXECUTE EXTERN FLOAT FOR FROM GROUP HAVING IF IN INDEX INSERT INT INTEGER INTO JOIN LEFT LIMIT LONG MODIFY NEW OFTYPE ON ONLY OR ORDER PARTITION PREPARE PRIORITY PROCEDURE PUBLIC RENAME RETURN RIGHT SELECT SET SHOW STEP STRING SWITCH SYNC TABLE TABLES TIMER TRIGGER TO TRUNCATE UNION UNPACK UPDATE UTC USE VALUES WHERE WHILE WINDOW",
			' ', ',');
	private final Connection connection;
	private final String url;
	private final String username;
	private Logger log = LH.get();

	public AmiDbJdbcDatabaseMetaData(Connection connection, String url, String username) {
		this.connection = connection;
		this.url = url;
		this.username = username;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public boolean allProceduresAreCallable() throws SQLException {
		return true;
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}

	@Override
	public String getURL() throws SQLException {
		return url;
	}

	@Override
	public String getUserName() throws SQLException {
		return username;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return false;
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return true;
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return PRODUCT_NAME;
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return PRODUCT_VERSION;
	}

	@Override
	public String getDriverName() throws SQLException {
		return DRIVER_NAME;
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return DRIVER_VERSION;
	}

	@Override
	public int getDriverMajorVersion() {
		return DRIVER_MAJOR_VERSION;
	}

	@Override
	public int getDriverMinorVersion() {
		return DRIVER_MINOR_VERSION;
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return "`";
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		return RESERVED_WORD;

	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		return null;
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		return null;
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return null;
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return true;
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return true;
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return "AMI";
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return "PROCEDURE";
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return null;
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return false;
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return null;
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxConnections() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return true;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return Integer.MAX_VALUE;
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return 0;
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		LH.info(log, "AMI-getProcedures called");
		return null;
	}

	@Override
	public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern) throws SQLException {
		LH.info(log, "AMI-getProcedureColumns called");
		return null;
	}

	@Override
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
		LH.info(log, "AMI-getTables called");
		return connection.prepareStatement(
				" select \"AMI\" as TABLE_CAT, (String)\"AMI\"    as TABLE_SCHEM, TableName as TABLE_NAME, \"TABLE\" as TABLE_TYPE,\"\" as REMARKS,(String)null as TYPE_CAT,(String)null as TYPE_SCHEM,(String)null as TYPE_NAME,(String)null as SELF_REFERENCING_COL_NAME,(String)null as REF_GENERATION from __TABLE")
				.executeQuery();
	}

	@Override
	public ResultSet getSchemas() throws SQLException {
		LH.info(log, "AMI-getSchemas called");
		BasicTable bt = new BasicTable(String.class, "TABLE_CATALOG", String.class, "TABLE_SCHEM");
		bt.getRows().addRow("AMI", "AMI");
		return new TableResultSet(null, bt);
	}

	@Override
	public ResultSet getCatalogs() throws SQLException {
		LH.info(log, "AMI-getCatalogs called");
		BasicTable bt = new BasicTable(String.class, "TABLE_CAT");
		bt.getRows().addRow("AMI");
		return new TableResultSet(null, bt);
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		LH.info(log, "AMI-getTableTypes called");
		return null;
	}

	@Override
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		LH.info(log, "AMI-getColumns called for " + catalog + "," + schemaPattern + "," + tableNamePattern + "," + columnNamePattern);
		String tableName = toPattern(tableNamePattern);
		String columnName = toPattern(columnNamePattern);
		String query = "select \"AMI\" as TABLE_CAT,(String)\"AMI\"    as TABLE_SCHEM,TableName as TABLE_NAME,ColumnName as COLUMN_NAME,"
				+ "switch(DataType,12,\"String\",12,\"Long\",-5,\"Integer\",4,\"Character\",1,\"Float\",7,\"Double\",8,\"UTC\",93,\"UTCN\",93,\"Boolean\",-7,\"Binary\",-1,\"Short\",4,\"Byte\",4) as DATA_TYPE,"
				+ "switch(DataType,\"VARCHAR\",\"String\",\"VARCHAR\",\"Long\",\"BIGINT\",\"Integer\",\"INT\",\"Character\",\"CHAR\",\"Float\",\"FLOAT\",\"Double\",\"DOUBLE\",\"UTC\",\"TIMESTAMP\",\"UTCN\",\"TIMESTAMP\",\"Boolean\",\"BIT\",\"Binary\",\"TEXT\",\"Short\",\"INT\",\"Byte\",\"INT\") as TYPE_NAME,"
				+ "100 as COLUMN_SIZE,65535 as BUFFER_LENGTH,(int)null as DECIMAL_DIGITS,10 as NUM_PREC_RADIX,1 as NULLABLE,\"\" as REMARKS,(String)null as COLUMN_DEF,0 as SQL_DATA_TYPE,0 as SQL_DATETIME_SUB,(int)null as CHAR_OCTET_LENGTH,Position as ORDINAL_POSITION,\"YES\" as IS_NULLABLE,(String)null as SCOPE_CATALOG,(String)null as SCOPE_SCHEMA,(String)null as SCOPE_TABLE,(int)null as SOURCE_DATA_TYPE,\"NO\" as IS_AUTOINCREMENT,\"NO\" as IS_GENERATEDCOLUMN from __COLUMN where TableName~~\""
				+ tableName + "\" and ColumnName~~\"" + columnName + "\"";
		return connection.prepareStatement(query).executeQuery();
	}
	private String toPattern(String pattern) {
		if (pattern == null)
			return "*";
		return "^" + SH.replaceAll(SH.trim(pattern), "%", "*") + "$";
	}

	@Override
	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		LH.info(log, "AMI-getColumnPrivileges  called");
		return null;
	}

	@Override
	public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		LH.info(log, "AMI-getTablePrivileges  called");
		return null;
	}

	@Override
	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		LH.info(log, "AMI-getBestRowIdentifier  called");
		return null;
	}

	@Override
	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		LH.info(log, "AMI-getVersionColumns  called");
		return null;
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		LH.info(log, "AMI-getPrimaryKeys  called");
		BasicTable bt = new BasicTable(String.class, "TABLE_CAT", String.class, "TABLE_SCHEM", String.class, "TABLE_NAME", String.class, "COLUMN_NAME", Short.class, "KEY_SEQ",
				String.class, "PK_NAME");
		return new TableResultSet(null, bt);
	}

	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		LH.info(log, "AMI-getImportedKeys  called");
		BasicTable bt = new BasicTable( //
				String.class, "PKTABLE_CAT" //
				, String.class, "PKTABLE_SCHEM" //
				, String.class, "PKTABLE_NAME" //
				, String.class, "PKCOLUMN_NAME"//
				, String.class, "FKTABLE_CAT" //
				, String.class, "FKTABLE_SCHEM" //
				, String.class, "FKTABLE_NAME" //
				, String.class, "FKCOLUMN_NAME"//
				, Short.class, "KEY_SEQ" // 
				, Short.class, "UPDATE_RULE" //
				, Short.class, "DELETE_RULE" //
				, String.class, "FK_NAME" //
				, String.class, "PK_NAME" //
				, Short.class, "DEFERRABILITY"); //
		return new TableResultSet(null, bt);
	}

	@Override
	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		LH.info(log, "AMI-getExportedKeys  called");
		return null;
	}

	@Override
	public ResultSet getCrossReference(String parentCatalog, String parentSchema, String parentTable, String foreignCatalog, String foreignSchema, String foreignTable)
			throws SQLException {
		LH.info(log, "AMI-getCrossReference  called");
		return null;
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		LH.info(log, "AMI-getTypeInfo  called");
		return null;
	}

	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		LH.info(log, "AMI-getIndexInfo  called");
		return null;
	}

	@Override
	public boolean supportsResultSetType(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return true;
	}

	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return true;
	}

	@Override
	public boolean updatesAreDetected(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean deletesAreDetected(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean insertsAreDetected(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
		LH.info(log, "AMI-getUDTs  called");
		return null;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.connection;
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		LH.info(log, "AMI-getSuperTypes  called");
		return null;
	}

	@Override
	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		LH.info(log, "AMI-getSuperTables  called");
		return null;
	}

	@Override
	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
		LH.info(log, "AMI-getAttributes  called");
		return null;
	}

	@Override
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return false;
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return 0;
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		return DRIVER_MAJOR_VERSION;
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		return DRIVER_MINOR_VERSION;
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		return DRIVER_MAJOR_VERSION;
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		return DRIVER_MINOR_VERSION;
	}

	@Override
	public int getSQLStateType() throws SQLException {
		return 0;
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		return false;
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return null;
	}

	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		LH.info(log, "AMI-getSchemas2 called");
		BasicTable bt = new BasicTable(String.class, "TABLE_CATALOG", String.class, "TABLE_SCHEM");
		return new TableResultSet(null, bt);
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return false;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		LH.info(log, "AMI-getClientInfoProperties  called");
		return null;
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		LH.info(log, "AMI-getFunctions  called");
		return null;
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
		LH.info(log, "AMI-getFunctionColumns  called");
		return null;
	}

	public ResultSet getPseudoColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		return null;
	}

	public boolean generatedKeyAlwaysReturned() throws SQLException {
		return false;
	}

}
