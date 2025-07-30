package com.f1.utils.sql;

import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.WhereNode;

public interface QueryClause {

	int getOperation();
	boolean isUnionByName();
	QueryClause getUnion();
	AsNode[] getTables();
	int getPosition();
	AsNode[] getSelects();
	WhereNode getWhere();

}
