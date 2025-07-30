package com.f1.ami.amihibernate;

import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.MappingException;
import org.hibernate.boot.TempTableDdlTransactionHandling;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.ColumnAliasExtractor;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelper;
import org.hibernate.engine.jdbc.env.spi.IdentifierHelperBuilder;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.hql.spi.id.IdTableSupportStandardImpl;
import org.hibernate.hql.spi.id.MultiTableBulkIdStrategy;
import org.hibernate.hql.spi.id.local.AfterUseAction;
import org.hibernate.hql.spi.id.local.LocalTemporaryTableBulkIdStrategy;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.Table;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.JoinFragment;
import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;
import org.hibernate.tool.schema.internal.StandardSequenceExporter;
import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;

public class AmiDialect extends MySQLDialect {

	private static final String AS = "as";
	private static final String FROM = "from";
	private static final String WHERE = "where";
	private static final char SPACE = ' ';
	public static final String OPERATOR_EQUALS = "==";
	private String tableTypeStorageEngine;

	public AmiDialect() {
		super();
		this.sb = new StringBuilder();
		this.sbParsed = new StringBuilder();
		this.scr = new StringCharReader();

		String storageEngine = Environment.getProperties().getProperty(Environment.STORAGE_ENGINE);
		if (storageEngine == null) {
			tableTypeStorageEngine = System.getProperty(Environment.STORAGE_ENGINE);
		}
		if (storageEngine == null)
			this.tableTypeStorageEngine = "FAST";
		this.uniqueDelegate = new AmiUniqueDelegate(this);
	}

	@Override
	public String getCastTypeName(int sqlTypeCode) {
		switch (sqlTypeCode) {
			case Types.BOOLEAN:
				return "Boolean";
			case Types.FLOAT:
				return "Float";
			case Types.DOUBLE:
				return "Double";
			case Types.NUMERIC:
			case Types.DECIMAL:
				return "BigDecimal";
			case Types.BIT:
			case Types.TINYINT:
				return "Byte";
			case Types.SMALLINT:
				return "Short";

			case Types.INTEGER:
				return "Integer";
			case Types.BIGINT:
				return "Long";
			case Types.REAL:
				return "Complex";

			case Types.DATE:
			case Types.TIME:
			case Types.TIME_WITH_TIMEZONE:
			case Types.TIMESTAMP:
			case Types.TIMESTAMP_WITH_TIMEZONE:
				//			case Types.TIMESTAMP_UTC:
				return "UTC";

			case Types.BLOB:
			case Types.CLOB:
			case Types.NCLOB:
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				//			case Types.LONG32VARBINARY:
				return "Binary";

			//			case Types.UUID:
			//				return "UUID";
			//			case Types.JSON:
			case Types.VARCHAR:
			case Types.NVARCHAR:
			case Types.CHAR:
			case Types.NCHAR:
			case Types.LONGVARCHAR:
			case Types.LONGNVARCHAR:
				//			case Types.LONG32VARCHAR:
				//			case Types.LONG32NVARCHAR:
				return "String";
			default:
				throw new IllegalArgumentException("Type not valid: " + sqlTypeCode);
		}
	}

	protected void registerColumnType(int code) {
		super.registerColumnType(code, getCastTypeName(code));
	}
	@Override
	protected void registerColumnType(int code, String name) {
		//Prevent default
	}
	@Override
	protected void registerColumnType(int code, long capacity, String name) {
		//Prevent default
	}
	@Override
	protected void registerHibernateType(int code, String name) {
		// TODO Auto-generated method stub
		super.registerHibernateType(code, name);
	}
	@Override
	protected void registerHibernateType(int code, long capacity, String name) {
		// TODO Auto-generated method stub
		super.registerHibernateType(code, capacity, name);
	}

	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		//		final DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		registerColumnType((Types.BOOLEAN));
		registerColumnType((Types.BIT));
		registerColumnType((Types.TINYINT));
		registerColumnType((Types.SMALLINT));
		registerColumnType((Types.INTEGER));
		registerColumnType((Types.BIGINT));

		registerColumnType((Types.FLOAT));
		registerColumnType((Types.REAL));
		registerColumnType((Types.DOUBLE));

		registerColumnType((Types.NUMERIC));
		registerColumnType((Types.DECIMAL));

		registerColumnType((Types.DATE));
		registerColumnType((Types.TIME));
		registerColumnType((Types.TIME_WITH_TIMEZONE));
		registerColumnType((Types.TIMESTAMP));
		registerColumnType((Types.TIMESTAMP_WITH_TIMEZONE));
		//		registerColumnType((Types.TIMESTAMP_UTC));
		registerColumnType((Types.CHAR));
		registerColumnType((Types.NCHAR));
		registerColumnType((Types.VARCHAR));
		registerColumnType((Types.NVARCHAR));
		registerColumnType((Types.CLOB));
		registerColumnType((Types.NCLOB));
		registerColumnType((Types.BLOB));
		//		registerColumnType((Types.JSON));
		//		registerColumnType((Types.UUID));

		registerColumnType((Types.BINARY));
		registerColumnType((Types.VARBINARY));
	}
	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		this.registerColumnTypes(typeContributions, serviceRegistry);
		typeContributions.contributeType(AmiStringType.INSTANCE);
	}

	//	@Override
	//	public int getPreferredSqlTypeCodeForBoolean() {
	//		return Types.BOOLEAN;
	//	}

	//	@Override
	//	public void initializeFunctionRegistry(QueryEngine queryEngine) {
	//		// TODO Auto-generated method stub
	//		super.initializeFunctionRegistry(queryEngine);
	//		//		CommonFunctionFactory functionFactory = new CommonFunctionFactory(queryEngine);
	//		BasicType<Object> objectType = queryEngine.getTypeConfiguration().getBasicTypeForJavaType(Object.class);
	//		queryEngine.getSqmFunctionRegistry().namedDescriptorBuilder("noNull").setInvariantType(objectType).setMinArgumentCount(2)
	//				.setParameterTypes(FunctionParameterType.ANY, FunctionParameterType.ANY).register();
	//	}

	private final AmiTableExporter amiTableExporter = new AmiTableExporter(this);

	@Override
	public Exporter<Table> getTableExporter() {
		return amiTableExporter;
	}

	public IdentityColumnSupport getIdentityColumnSupport() {
		return new AmiIdentityColumnSupport();
		//		return new MySQLIdentityColumnSupport();
	}
	@Override
	public String getForUpdateString() {
		// TODO Auto-generated method stub
		//		return super.getForUpdateString();
		//TODO: NOT SUPPORTED
		return "";
		//		return " TODO: for update";
	}
	@Override
	public String getSelectGUIDString() {
		return "select new UUID()";
	}
	//	@Override
	//	public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
	//		return new StandardSqlAstTranslatorFactory() {
	//			@Override
	//			protected <T extends JdbcOperation> SqlAstTranslator<T> buildTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
	//				return new AmiSqlAstTranslator<>(sessionFactory, statement);
	//			}
	//		};
	//	}

	@Override
	public char closeQuote() {
		return '"';
	}
	@Override
	public char openQuote() {
		return '"';
	}

	@Override
	public String getCurrentTimestampSelectString() {
		return "select timestamp()";
	}
	//	@Override
	//	public String getTemporaryTableCreateCommand() {
	//		return "create table if not exists";
	//	}
	//	@Override
	//	public String getTemporaryTableDropCommand() {
	//		return "drop table";
	//	}
	@Override
	public String getCreateTableString() {
		return "create public table if not exists";
	}

	@Override
	public String getAlterTableString(String tableName) {
		final StringBuilder sb = new StringBuilder("alter public table ");
		if (supportsIfExistsAfterAlterTable()) {
			sb.append("if exists ");
		}
		sb.append(SH.afterFirst(tableName, "AMI.AMI."));
		return sb.toString();
		//		return super.getAlterTableString(tableName);
	}
	@Override
	public String getAddColumnString() {
		// TODO Auto-generated method stub
		return "add";
	}
	//	@Override
	//	public String castPattern(CastType from, CastType to) {
	//		//TODO: add more
	//		return "(?2 (?1))";
	//	}
	@Override
	public String getDropTableString(String tableName) {
		final StringBuilder buf = new StringBuilder("drop public table ");
		if (supportsIfExistsBeforeTableName()) {
			buf.append("if exists ");
		}
		buf.append(tableName).append(getCascadeConstraintsString());
		if (supportsIfExistsAfterTableName()) {
			buf.append(" if exists");
		}
		return buf.toString();
	}
	//	@Override
	//	public boolean supportsNamedParameters(DatabaseMetaData databaseMetaData) throws SQLException {
	//		// TODO Auto-generated method stub
	//		return true;
	//	}

	@Override
	public String getLowercaseFunction() {
		return "strLower";
	}
	@Override
	public String getCaseInsensitiveLike() {
		return "~~";
	}
	@Override
	public boolean supportsCaseInsensitiveLike() {
		return true;
	}

	@Override
	public NameQualifierSupport getNameQualifierSupport() {
		// TODO Auto-generated method stub
		return super.getNameQualifierSupport();
	}

	@Override
	public boolean canCreateCatalog() {
		return false;
	}
	@Override
	public boolean canCreateSchema() {
		return false;
	};
	//	@Override
	//	public HqlTranslator getHqlTranslator() {
	//		// TODO Auto-generated method stub
	//		return super.getHqlTranslator();
	//	}
	//	@Override
	//	public SqmTranslatorFactory getSqmTranslatorFactory() {
	//		// TODO Auto-generated method stub
	//		return super.getSqmTranslatorFactory();
	//	}
	//	@Override
	//	public SequenceSupport getSequenceSupport() {
	//		// TODO Auto-generated method stub
	//		return super.getSequenceSupport();
	//	}
	@Override
	public String getNativeIdentifierGeneratorStrategy() {
		// TODO Auto-generated method stub
		return super.getNativeIdentifierGeneratorStrategy();
	}
	@Override
	public String getQuerySequencesString() {
		// TODO Auto-generated method stub
		return super.getQuerySequencesString();
	}
	@Override
	public ResultSet getResultSet(CallableStatement ps) throws SQLException {
		// TODO Auto-generated method stub
		return super.getResultSet(ps);
	}
	@Override
	public ResultSet getResultSet(CallableStatement statement, int position) throws SQLException {
		// TODO Auto-generated method stub
		return super.getResultSet(statement, position);
	}
	@Override
	public ResultSet getResultSet(CallableStatement statement, String name) throws SQLException {
		// TODO Auto-generated method stub
		return super.getResultSet(statement, name);
	}
	//	@Override
	//	public NationalizationSupport getNationalizationSupport() {
	//		// TODO Auto-generated method stub
	//		return super.getNationalizationSupport();
	//	}
	@Override
	public MultiTableBulkIdStrategy getDefaultMultiTableBulkIdStrategy() {
		return new LocalTemporaryTableBulkIdStrategy(new IdTableSupportStandardImpl() {
			@Override
			public String getCreateIdTableCommand() {
				return "create table if not exists";
			}

			@Override
			public String getDropIdTableCommand() {
				return "drop table";
			}
		}, AfterUseAction.DROP, TempTableDdlTransactionHandling.NONE);
	}
	@Override
	public ColumnAliasExtractor getColumnAliasExtractor() {
		// TODO Auto-generated method stub
		return super.getColumnAliasExtractor();
	}
	@Override
	public String addSqlHintOrComment(String sql, QueryParameters parameters, boolean commentsEnabled) {
		// TODO Auto-generated method stub
		return super.addSqlHintOrComment(sql, parameters, commentsEnabled);
	}

	private static final AbstractLimitHandler LIMIT_HANDLER = new AbstractLimitHandler() {
		public String processSql(String sql, org.hibernate.engine.spi.RowSelection selection) {
			return sql;
		};
		@Override
		public boolean supportsLimit() {
			return true;
		}

	};

	@Override
	public LimitHandler getLimitHandler() {
		return LIMIT_HANDLER;
	}
	@Override
	public String getLimitString(String sql, boolean hasOffset) {
		return super.getLimitString(sql, hasOffset);
	}

	private StringBuilder sbParsed;
	private StringBuilder sb;
	private StringCharReader scr;

	@Override
	public String transformSelectString(String select) {
		// Simple Transform Select String
		// Read to whitespace for Sql Command (Select, Update Delete) 
		// Read until `From` 
		// Process table as and where clause
		// TODO: improve this by reading each sql node, ex columns 
		return select;
		//		SH.clear(sbParsed);
		//		SH.clear(sb);
		//		scr.reset(SH.trim(select));
		//		// Whitespace
		//		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		//		sbParsed.append(SH.toStringAndClear(sb));
		//
		//		// Select Update Delete
		//		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		//		String sqlCommand = SH.toStringAndClear(sb); // select update delete
		//		sbParsed.append(sqlCommand);
		//
		//		// Columns
		//		scr.setCaseInsensitive(true);
		//		scr.readUntilSequence(FROM, sb);
		//		String beforeFromColumns = SH.toStringAndClear(sb);
		//		sbParsed.append(beforeFromColumns);
		//
		//		// From
		//		scr.expectSequence(FROM);
		//		scr.setCaseInsensitive(false);
		//		sbParsed.append(FROM);
		//
		//		// Whitespace
		//		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		//		sbParsed.append(SH.toStringAndClear(sb));
		//
		//		// TableName
		//		scr.readUntilAny(StringCharReader.WHITE_SPACE, true, '\\', sb);
		//		String tableName = SH.toStringAndClear(sb); // select update delete
		//		sbParsed.append(tableName);
		//
		//		// Table Alias
		//		scr.setCaseInsensitive(true);
		//		scr.readUntilSequence(WHERE, sb);
		//		if (SH.is(sb)) {
		//			String beforeWhereTableAlias = SH.toStringAndClear(sb);
		//			sbParsed.append(SPACE).append(AS);
		//			sbParsed.append(beforeWhereTableAlias);
		//		}
		//
		//		// Whitespace
		//		scr.readWhileAny(StringCharReader.WHITE_SPACE, sb);
		//		sbParsed.append(SH.toStringAndClear(sb));
		//
		//		// WHERE
		//		boolean hasWhere = scr.expectSequenceNoThrow(WHERE);
		//		scr.setCaseInsensitive(false);
		//		if (hasWhere) {
		//			scr.readUntil(StringCharReader.EOF, sb);
		//			String afterWhere = SH.toStringAndClear(sb);
		//			sbParsed.append(WHERE);
		//			sbParsed.append(SH.replaceAll(afterWhere, "=?", "==?"));
		//		} else {
		//			scr.readUntil(StringCharReader.EOF, sb);
		//			sbParsed.append(SH.toStringAndClear(sb));
		//		}
		//
		//		System.out.println("### Transformed: " + sbParsed);
		//		return SH.toStringAndClear(sbParsed);
	}
	@Override
	public String getAddUniqueConstraintString(String constraintName) {
		// TODO Auto-generated method stub
		return super.getAddUniqueConstraintString(constraintName);
	}
	@Override
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		// TODO Auto-generated method stub
		return super.getAddPrimaryKeyConstraintString(constraintName);
	}
	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String foreignKeyDefinition) {
		// TODO Auto-generated method stub
		//		return super.getAddForeignKeyConstraintString(constraintName, foreignKeyDefinition);
		return "";
	}
	@Override
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey, boolean referencesPrimaryKey) {
		// TODO Auto-generated method stub
		//		return super.getAddForeignKeyConstraintString(constraintName, foreignKey, referencedTable, primaryKey, referencesPrimaryKey);
		return "";
	}

	private StandardForeignKeyExporter foreignKeyExporter = new AmiStandardForeignKeyExporter(this);
	private StandardUniqueKeyExporter uniqueKeyExporter = new AmiStandardUniqueKeyExporter(this);
	private final UniqueDelegate uniqueDelegate;

	@Override
	public Exporter<ForeignKey> getForeignKeyExporter() {
		return foreignKeyExporter;
	}
	@Override
	public Exporter<Constraint> getUniqueKeyExporter() {
		return this.uniqueKeyExporter;
	}

	@Override
	public UniqueDelegate getUniqueDelegate() {
		return this.uniqueDelegate;
	}

	@Override
	public Class getNativeIdentifierGeneratorClass() {
		// TODO Auto-generated method stub
		return super.getNativeIdentifierGeneratorClass();
	}
	@Override
	public String getSelectSequenceNextValString(String sequenceName) throws MappingException {
		// TODO Auto-generated method stub
		return super.getSelectSequenceNextValString(sequenceName);
	}

	private StandardSequenceExporter sequenceExporter = new StandardSequenceExporter(this);

	@Override
	public Exporter<Sequence> getSequenceExporter() {
		// TODO Auto-generated method stub
		return sequenceExporter;
	}
	@Override
	public String cast(String value, int jdbcTypeCode, int length) {
		// TODO Auto-generated method stub
		return cast(value, jdbcTypeCode, length, Column.DEFAULT_PRECISION, Column.DEFAULT_SCALE);
	}
	@Override
	public String cast(String value, int jdbcTypeCode, int length, int precision, int scale) {
		// TODO Auto-generated method stub
		if (jdbcTypeCode == Types.CHAR) {
			return "cast(" + value + " as char(" + length + "))";
		} else {
			return "cast(" + value + "as " + getTypeName(jdbcTypeCode, length, precision, scale) + ")";
		}
		//		return super.cast(value, jdbcTypeCode, length, precision, scale);
	}
	@Override
	public String cast(String value, int jdbcTypeCode, int precision, int scale) {
		// TODO Auto-generated method tub
		return cast(value, jdbcTypeCode, Column.DEFAULT_LENGTH, precision, scale);
	}
	@Override
	public String toBooleanValueString(boolean bool) {
		// TODO Auto-generated method stub
		return super.toBooleanValueString(bool);
	}
	@Override
	public SqlTypeDescriptor remapSqlTypeDescriptor(SqlTypeDescriptor sqlTypeDescriptor) {
		// TODO Auto-generated method stub
		return super.remapSqlTypeDescriptor(sqlTypeDescriptor);
	}
	@Override
	public IdentifierHelper buildIdentifierHelper(IdentifierHelperBuilder builder, DatabaseMetaData dbMetaData) throws SQLException {
		// TODO Auto-generated method stub
		return super.buildIdentifierHelper(builder, dbMetaData);
	}
	@Override
	public String getSequenceNextValString(String sequenceName) throws MappingException {
		// TODO Auto-generated method stub
		return super.getSequenceNextValString(sequenceName);
	}

	@Override
	public String getTableTypeString() {
		return this.tableTypeStorageEngine;
	}
	@Override
	public JoinFragment createOuterJoinFragment() {
		return new AmiANSIJoinFragment();
	}

}
