package com.f1.ami.amihibernate;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.hql.internal.ast.SqlGenerator;
import org.hibernate.hql.internal.ast.tree.FromElement;

import antlr.collections.AST;

public class AmiSqlGenerator extends SqlGenerator {

	public AmiSqlGenerator(SessionFactoryImplementor factory) {
		super(factory);
	}
	@Override
	protected void out(AST n) {
		//		System.out.println("AST: " + n.getText());
		if (n instanceof FromElement) {
			FromElement from = (FromElement) n;
			String originalText = from.getOriginalText();
			String tableAlias = from.getTableAlias();
			out(originalText);
			out(" as ");
			out(tableAlias);
		} else
			super.out(n);
	}

}
