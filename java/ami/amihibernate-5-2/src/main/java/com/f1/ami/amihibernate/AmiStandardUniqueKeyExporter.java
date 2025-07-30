package com.f1.ami.amihibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.dialect.Dialect;
import org.hibernate.mapping.Constraint;
import org.hibernate.tool.schema.internal.StandardUniqueKeyExporter;

public class AmiStandardUniqueKeyExporter extends StandardUniqueKeyExporter {
	private Dialect dialect;

	public AmiStandardUniqueKeyExporter(Dialect dialect) {
		super(dialect);
		this.dialect = dialect;
	}
	@Override
	public String[] getSqlCreateStrings(Constraint constraint, Metadata metadata) {
		return super.getSqlCreateStrings(constraint, metadata);
	}
	@Override
	public String[] getSqlDropStrings(Constraint constraint, Metadata metadata) {
		return super.getSqlDropStrings(constraint, metadata);
	}

}
