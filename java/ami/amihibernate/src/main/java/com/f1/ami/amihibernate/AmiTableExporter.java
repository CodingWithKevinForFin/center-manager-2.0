package com.f1.ami.amihibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.MappingException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.Dialect;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.schema.internal.StandardTableExporter;

public class AmiTableExporter extends StandardTableExporter {

	public AmiTableExporter(Dialect dialect) {
		super(dialect);
	}

	@Override
	public String[] getSqlCreateStrings(Table table, Metadata metadata, SqlStringGenerationContext context) {
		final QualifiedName tableName = new QualifiedNameParser.NameParts(Identifier.toIdentifier(table.getCatalog(), table.isCatalogQuoted()),
				Identifier.toIdentifier(table.getSchema(), table.isSchemaQuoted()), table.getNameIdentifier());

		try {
			String formattedTableName = context.format(tableName);
			StringBuilder buf = new StringBuilder(tableCreateString(table.hasPrimaryKey())).append(' ').append(formattedTableName).append(" (");

			boolean isPrimaryKeyIdentity = table.hasPrimaryKey() && table.getIdentifierValue() != null
					&& table.getIdentifierValue().isIdentityColumn(((MetadataImplementor) metadata).getMetadataBuildingOptions().getIdentifierGeneratorFactory(), dialect);
			// this is the much better form moving forward as we move to metamodel
			//boolean isPrimaryKeyIdentity = hasPrimaryKey
			//				&& table.getPrimaryKey().getColumnSpan() == 1
			//				&& table.getPrimaryKey().getColumn( 0 ).isIdentity();

			// Try to find out the name of the primary key in case the dialect needs it to create an identity
			String pkColName = null;
			if (table.hasPrimaryKey()) {
				Column pkColumn = table.getPrimaryKey().getColumns().iterator().next();
				pkColName = pkColumn.getQuotedName(dialect);
			}

			boolean isFirst = true;
			for (Column col : table.getColumns()) {
				if (isFirst) {
					isFirst = false;
				} else {
					buf.append(", ");
				}

				String colName = col.getQuotedName(dialect);
				buf.append(colName);

				if (isPrimaryKeyIdentity && colName.equals(pkColName)) {
					// to support dialects that have their own identity data type
					if (dialect.getIdentityColumnSupport().hasDataTypeInIdentityColumn()) {
						buf.append(' ').append(col.getSqlType(metadata.getDatabase().getTypeConfiguration(), dialect, metadata));
					}
					String identityColumnString = dialect.getIdentityColumnSupport().getIdentityColumnString(col.getSqlTypeCode(metadata));
					buf.append(' ').append(identityColumnString);
				} else {
					final String columnType = col.getSqlType(metadata.getDatabase().getTypeConfiguration(), dialect, metadata);
					if (col.getGeneratedAs() == null || dialect.hasDataTypeBeforeGeneratedAs()) {
						buf.append(' ').append(columnType);
					}

					String defaultValue = col.getDefaultValue();
					if (defaultValue != null) {
						buf.append(" default ").append(defaultValue);
					}

					String generatedAs = col.getGeneratedAs();
					if (generatedAs != null) {
						buf.append(dialect.generatedAs(generatedAs));
					}

					if (col.isNullable()) {
						buf.append(dialect.getNullColumnString(columnType));
					} else {
						buf.append(" NoNull");
					}

				}

				if (col.isUnique()) {
					String keyName = Constraint.generateName("UK_", table, col);
					UniqueKey uk = table.getOrCreateUniqueKey(keyName);
					uk.addColumn(col);
					buf.append(dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(col, context));
				}

				String checkConstraint = col.checkConstraint();
				if (checkConstraint != null && dialect.supportsColumnCheck()) {
					buf.append(checkConstraint);
				}

				String columnComment = col.getComment();
				if (columnComment != null) {
					buf.append(dialect.getColumnComment(columnComment));
				}
			}

			buf.append(dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment(table, context));

			applyTableCheck(table, buf);

			buf.append(')');

			if (table.getComment() != null) {
				buf.append(dialect.getTableComment(table.getComment()));
			}

			//			applyTableTypeString(buf);

			if (table.hasPrimaryKey()) {
				//				buf.append("; ").append(table.getPrimaryKey().sqlConstraintString(dialect));

				PrimaryKey primaryKey = table.getPrimaryKey();
				String name = primaryKey.getName();
				buf.append("; ").append("create index ").append(name).append(" on ").append(formattedTableName).append('(');
				Iterator<Column> iterator = primaryKey.getColumns().iterator();
				while (iterator.hasNext()) {
					buf.append(iterator.next().getQuotedName(dialect));
					buf.append(" HASH");
					if (iterator.hasNext())
						buf.append(", ");
				}
				buf.append(") use CONSTRAINT=\"PRIMARY\"");
			}
			List<String> sqlStrings = new ArrayList<>();
			sqlStrings.add(buf.toString());

			applyComments(table, formattedTableName, sqlStrings);

			applyInitCommands(table, sqlStrings, context);

			return sqlStrings.toArray(StringHelper.EMPTY_STRINGS);
		} catch (

		Exception e) {
			throw new MappingException("Error creating SQL create commands for table : " + tableName, e);
		}
	}

}
