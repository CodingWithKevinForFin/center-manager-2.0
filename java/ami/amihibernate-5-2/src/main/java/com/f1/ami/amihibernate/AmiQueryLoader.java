package com.f1.ami.amihibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.ast.tree.SelectClause;
import org.hibernate.loader.hql.QueryLoader;

public class AmiQueryLoader extends QueryLoader {

	public AmiQueryLoader(QueryTranslatorImpl queryTranslator, SessionFactoryImplementor factory, SelectClause selectClause) {
		super(queryTranslator, factory, selectClause);
		// TODO Auto-generated constructor stub
	}

}
