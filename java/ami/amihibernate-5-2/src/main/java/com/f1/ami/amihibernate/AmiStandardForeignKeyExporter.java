package com.f1.ami.amihibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.tool.schema.internal.StandardForeignKeyExporter;

public class AmiStandardForeignKeyExporter extends StandardForeignKeyExporter {
	private static final String COLUMN_MISMATCH_MSG = "Number of referencing columns [%s] did not "
			+ "match number of referenced columns [%s] in foreign-key [%s] from [%s] to [%s]";
	private Dialect dialect;

	public AmiStandardForeignKeyExporter(Dialect dialect) {
		super(dialect);
		this.dialect = dialect;
	}
	@Override
	public String[] getSqlCreateStrings(ForeignKey foreignKey, Metadata metadata) {
		// TODO Auto-generated method stub

		return new String[] { "" };
		//		String[] sql = super.getSqlCreateStrings(foreignKey, metadata);
		//		return sql;
	}
	@Override
	public String[] getSqlDropStrings(ForeignKey foreignKey, Metadata metadata) {
		// TODO Auto-generated method stub
		//		String[] sql = super.getSqlDropStrings(foreignKey, metadata);
		return new String[] { "" };
	}

}
