package com.f1.ami.amihibernate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Constraint;
import org.hibernate.mapping.PrimaryKey;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.tool.schema.internal.StandardTableExporter;

import com.f1.utils.SH;

public class AmiTableExporter extends StandardTableExporter {

	public AmiTableExporter(Dialect dialect) {
		super(dialect);
	}

	@Override
	public String[] getSqlCreateStrings(Table table, Metadata metadata) {
		final QualifiedName tableName = new QualifiedNameParser.NameParts(Identifier.toIdentifier(table.getCatalog(), table.isCatalogQuoted()),
				Identifier.toIdentifier(table.getSchema(), table.isSchemaQuoted()), table.getNameIdentifier());

		try {
			String formattedTableName = tableName.render();
			StringBuilder buf = new StringBuilder(tableCreateString(table.hasPrimaryKey())).append(' ').append(formattedTableName).append(" (");

			boolean isPrimaryKeyIdentity = table.hasPrimaryKey() && table.getIdentifierValue() != null
					&& table.getIdentifierValue().isIdentityColumn(metadata.getIdentifierGeneratorFactory(), dialect);
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

			final Iterator columnItr = table.getColumnIterator();
			boolean isFirst = true;
			while (columnItr.hasNext()) {
				final Column col = (Column) columnItr.next();

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
						buf.append(' ').append(col.getSqlType(dialect, metadata));
					}
					String identityColumnString = dialect.getIdentityColumnSupport().getIdentityColumnString(col.getSqlTypeCode(metadata));
					buf.append(' ').append(identityColumnString);
				} else {
					String columnType = col.getSqlType(dialect, metadata);
					buf.append(' ').append(columnType);

					String defaultValue = col.getDefaultValue();
					if (defaultValue != null) {
						buf.append(" default ").append(defaultValue);
					}

					//					String generatedAs = col.getGeneratedAs();
					//					if (generatedAs != null) {
					//						buf.append(dialect.generatedAs(generatedAs));
					//					}

					if (col.isNullable()) {
						buf.append(dialect.getNullColumnString());
					} else {
						buf.append(" NoNull");
					}

				}

				if (col.isUnique()) {
					String keyName = Constraint.generateName("UK_", table, col);
					UniqueKey uk = table.getOrCreateUniqueKey(keyName);
					uk.addColumn(col);
					buf.append(dialect.getUniqueDelegate().getColumnDefinitionUniquenessFragment(col));
				}

				String checkConstraint = col.getCheckConstraint();
				if (checkConstraint != null && dialect.supportsColumnCheck()) {
					buf.append(checkConstraint);
				}

				String columnComment = col.getComment();
				if (columnComment != null) {
					buf.append(dialect.getColumnComment(columnComment));
				}

			}

			buf.append(dialect.getUniqueDelegate().getTableCreationUniqueConstraintsFragment(table));

			applyTableCheck(table, buf);

			buf.append(')');

			buf.append(" use ");

			buf.append("PersistEngine=\"").append(dialect.getTableTypeString()).append("\"");
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
				String autoGenType = "NONE";
				if (iterator.hasNext()) {
					Column next = iterator.next();

					buf.append(next.getQuotedName(dialect));
					buf.append(" HASH");
					if (iterator.hasNext())
						buf.append(", ");
					// Autogen type
					String type = SH.toLowerCase(next.getSqlType());
					if (SH.equals("long", type))
						autoGenType = "INC";
					else if (SH.equals("int", type))
						autoGenType = "INC";
					else if (SH.equals("integer", type))
						autoGenType = "INC";
					else if (SH.equals("string", type))
						autoGenType = "RAND";
					else if (SH.equals("uuid", type))
						autoGenType = "RAND";
					else if (SH.equals("float", type))
						autoGenType = "RAND";
					else if (SH.equals("double", type))
						autoGenType = "RAND";
					else
						throw new RuntimeException("For PrimaryKey " + name + " on table " + formattedTableName + ": AutoGen not supported for column type " + type);
					if (iterator.hasNext())
						throw new RuntimeException("For PrimaryKey " + name + " on table " + formattedTableName + ": AutoGen not supported for multi column primary keys  ");

				}
				buf.append(") use CONSTRAINT=\"PRIMARY\"");
				if (isPrimaryKeyIdentity) {
					buf.append(" AutoGen=\"").append(autoGenType).append("\"");
				}
				buf.append(";");
			}
			List<String> sqlStrings = new ArrayList<>();
			sqlStrings.add(buf.toString());

			applyComments(table, tableName, sqlStrings);

			applyInitCommands(table, sqlStrings);

			return sqlStrings.toArray(new String[sqlStrings.size()]);

		} catch (

		Exception e) {
			throw new RuntimeException("Error creating SQL create commands for table : " + tableName, e);
		}
	}

}
