package com.f1.ami.amihibernate;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.DatabaseVersion;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.NationalizationSupport;
import org.hibernate.dialect.SimpleDatabaseVersion;
import org.hibernate.dialect.identity.IdentityColumnSupport;
import org.hibernate.dialect.sequence.SequenceSupport;
import org.hibernate.engine.jdbc.env.spi.NameQualifierSupport;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Table;
import org.hibernate.query.hql.HqlTranslator;
import org.hibernate.query.spi.QueryEngine;
import org.hibernate.query.sqm.CastType;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.query.sqm.sql.SqmTranslatorFactory;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.sql.ast.SqlAstTranslator;
import org.hibernate.sql.ast.SqlAstTranslatorFactory;
import org.hibernate.sql.ast.spi.StandardSqlAstTranslatorFactory;
import org.hibernate.sql.ast.tree.Statement;
import org.hibernate.sql.exec.spi.JdbcOperation;
import org.hibernate.tool.schema.spi.Exporter;
import org.hibernate.type.BasicType;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.sql.internal.DdlTypeImpl;
import org.hibernate.type.descriptor.sql.spi.DdlTypeRegistry;

public class AmiDialect extends MySQLDialect {

	public AmiDialect() {
		super(new SimpleDatabaseVersion(2, 0, 0));
	}
	public AmiDialect(DatabaseVersion version) {
		super(version);
	}
	@Override
	protected void initDefaultProperties() {
		//		getDefaultProperties().setProperty(key, value)
		super.initDefaultProperties();
	}

	@Override
	protected String columnType(int sqlTypeCode) {
		// TODO Auto-generated method stub
		//		String type = super.columnType(sqlTypeCode);
		//		System.out.println("columnType: " + sqlTypeCode + " - "); // + type);
		switch (sqlTypeCode) {
			case SqlTypes.BOOLEAN:
				return "Boolean";
			case SqlTypes.FLOAT:
				return "Float";
			case SqlTypes.DOUBLE:
				return "Double";
			case SqlTypes.NUMERIC:
			case SqlTypes.DECIMAL:
				return "BigDecimal";
			case SqlTypes.BIT:
			case SqlTypes.TINYINT:
				return "Byte";
			case SqlTypes.SMALLINT:
				return "Short";

			case SqlTypes.INTEGER:
				return "Integer";
			case SqlTypes.BIGINT:
				return "Long";
			case SqlTypes.REAL:
				return "Complex";

			case SqlTypes.DATE:
			case SqlTypes.TIME:
			case SqlTypes.TIME_WITH_TIMEZONE:
			case SqlTypes.TIMESTAMP:
			case SqlTypes.TIMESTAMP_WITH_TIMEZONE:
			case SqlTypes.TIMESTAMP_UTC:
				return "UTC";

			case SqlTypes.BLOB:
			case SqlTypes.CLOB:
			case SqlTypes.NCLOB:
			case SqlTypes.BINARY:
			case SqlTypes.VARBINARY:
			case SqlTypes.LONGVARBINARY:
			case SqlTypes.LONG32VARBINARY:
				return "Binary";

			case SqlTypes.UUID:
				return "UUID";
			case SqlTypes.JSON:
			case SqlTypes.VARCHAR:
			case SqlTypes.NVARCHAR:
			case SqlTypes.CHAR:
			case SqlTypes.NCHAR:
			case SqlTypes.LONGVARCHAR:
			case SqlTypes.LONGNVARCHAR:
			case SqlTypes.LONG32VARCHAR:
			case SqlTypes.LONG32NVARCHAR:
				return "String";
			default:
				throw new IllegalArgumentException("Type not valid: " + sqlTypeCode);

		}
	}
	@Override
	protected String castType(int sqlTypeCode) {
		// TODO Auto-generated method stub
		switch (sqlTypeCode) {
			case SqlTypes.BOOLEAN:
				return "Boolean";
			case SqlTypes.FLOAT:
				return "Float";
			case SqlTypes.DOUBLE:
				return "Double";
			case SqlTypes.NUMERIC:
			case SqlTypes.DECIMAL:
				return "BigDecimal";
			case SqlTypes.BIT:
			case SqlTypes.TINYINT:
				return "Byte";
			case SqlTypes.SMALLINT:
				return "Short";

			case SqlTypes.INTEGER:
				return "Integer";
			case SqlTypes.BIGINT:
				return "Long";
			case SqlTypes.REAL:
				return "Complex";

			case SqlTypes.DATE:
			case SqlTypes.TIME:
			case SqlTypes.TIME_WITH_TIMEZONE:
			case SqlTypes.TIMESTAMP:
			case SqlTypes.TIMESTAMP_WITH_TIMEZONE:
			case SqlTypes.TIMESTAMP_UTC:
				return "UTC";

			case SqlTypes.BLOB:
			case SqlTypes.CLOB:
			case SqlTypes.NCLOB:
			case SqlTypes.BINARY:
			case SqlTypes.VARBINARY:
			case SqlTypes.LONGVARBINARY:
			case SqlTypes.LONG32VARBINARY:
				return "Binary";

			case SqlTypes.UUID:
				return "UUID";
			case SqlTypes.JSON:
			case SqlTypes.VARCHAR:
			case SqlTypes.NVARCHAR:
			case SqlTypes.CHAR:
			case SqlTypes.NCHAR:
			case SqlTypes.LONGVARCHAR:
			case SqlTypes.LONGNVARCHAR:
			case SqlTypes.LONG32VARCHAR:
			case SqlTypes.LONG32NVARCHAR:
				return "String";
			default:
				throw new IllegalArgumentException("Type not valid: " + sqlTypeCode);
		}
	}
	private DdlTypeImpl simpleSqlType(int sqlTypeCode) {
		return new DdlTypeImpl(sqlTypeCode, columnType(sqlTypeCode), castType(sqlTypeCode), this);
	}

	@Override
	protected void registerColumnTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		// TODO Auto-generated method stub
		final DdlTypeRegistry ddlTypeRegistry = typeContributions.getTypeConfiguration().getDdlTypeRegistry();
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.BOOLEAN));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.BIT));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TINYINT));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.SMALLINT));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.INTEGER));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.BIGINT));

		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.FLOAT));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.REAL));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.DOUBLE));

		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.NUMERIC));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.DECIMAL));

		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.DATE));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TIME));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TIME_WITH_TIMEZONE));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TIMESTAMP));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TIMESTAMP_WITH_TIMEZONE));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.TIMESTAMP_UTC));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.CHAR));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.NCHAR));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.VARCHAR));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.NVARCHAR));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.CLOB));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.NCLOB));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.BLOB));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.JSON));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.UUID));

		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.BINARY));
		ddlTypeRegistry.addDescriptor(simpleSqlType(SqlTypes.VARBINARY));
	}
	@Override
	public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
		this.registerColumnTypes(typeContributions, serviceRegistry);
	}

	@Override
	public int getPreferredSqlTypeCodeForBoolean() {
		return Types.BOOLEAN;
	}

	@Override
	public void initializeFunctionRegistry(QueryEngine queryEngine) {
		// TODO Auto-generated method stub
		super.initializeFunctionRegistry(queryEngine);
		//		CommonFunctionFactory functionFactory = new CommonFunctionFactory(queryEngine);
		BasicType<Object> objectType = queryEngine.getTypeConfiguration().getBasicTypeForJavaType(Object.class);
		queryEngine.getSqmFunctionRegistry().namedDescriptorBuilder("noNull").setInvariantType(objectType).setMinArgumentCount(2)
				.setParameterTypes(FunctionParameterType.ANY, FunctionParameterType.ANY).register();
	}

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
		return " TODO: for update";
	}
	@Override
	public String getSelectGUIDString() {
		return "select new UUID()";
	}
	@Override
	public SqlAstTranslatorFactory getSqlAstTranslatorFactory() {
		return new StandardSqlAstTranslatorFactory() {
			@Override
			protected <T extends JdbcOperation> SqlAstTranslator<T> buildTranslator(SessionFactoryImplementor sessionFactory, Statement statement) {
				return new AmiSqlAstTranslator<>(sessionFactory, statement);
			}
		};
	}

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
	@Override
	public String getTemporaryTableCreateCommand() {
		return "create table if not exists";
	}
	@Override
	public String getTemporaryTableDropCommand() {
		return "drop table";
	}
	@Override
	public String getCreateTableString() {
		return "create public table if not exists";
	}
	@Override
	public String castPattern(CastType from, CastType to) {
		//TODO: add more
		return "(?2 (?1))";
	}
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
	@Override
	public HqlTranslator getHqlTranslator() {
		// TODO Auto-generated method stub
		return super.getHqlTranslator();
	}
	@Override
	public SqmTranslatorFactory getSqmTranslatorFactory() {
		// TODO Auto-generated method stub
		return super.getSqmTranslatorFactory();
	}
	@Override
	public SequenceSupport getSequenceSupport() {
		// TODO Auto-generated method stub
		return super.getSequenceSupport();
	}
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
	@Override
	public NationalizationSupport getNationalizationSupport() {
		// TODO Auto-generated method stub
		return super.getNationalizationSupport();
	}
	@Override
	public String transformSelectString(String select) {
		// TODO Auto-generated method stub
		return super.transformSelectString(select);
	}
}
