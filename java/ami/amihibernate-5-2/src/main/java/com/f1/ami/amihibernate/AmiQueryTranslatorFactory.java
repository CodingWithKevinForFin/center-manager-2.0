package com.f1.ami.amihibernate;

import java.util.Map;

import org.hibernate.engine.query.spi.EntityGraphQueryHint;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.ASTQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;

public class AmiQueryTranslatorFactory extends ASTQueryTranslatorFactory {
	@Override
	public QueryTranslator createQueryTranslator(String queryIdentifier, String queryString, Map filters, SessionFactoryImplementor factory,
			EntityGraphQueryHint entityGraphQueryHint) {
		return new AmiQueryTranslatorImpl(queryIdentifier, queryString, filters, factory, entityGraphQueryHint);
	}

}
