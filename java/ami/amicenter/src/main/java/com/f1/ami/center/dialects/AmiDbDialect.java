package com.f1.ami.center.dialects;

import com.f1.base.Table;

public interface AmiDbDialect {
	public static final String PROPERTY_DEBUG = "ami.db.dialect.debug";

	public String prepareQuery(String statement);
	public Table prepareResult(Table r);
}
