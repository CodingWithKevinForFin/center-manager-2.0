package com.f1.utils.sql;

import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.WhereNode;

public class PrepareClause implements QueryClause {

	final private SqlColumnsNode prep;
	final private AsNode[] tables;
	final private AsNode[] selects;
	final private Node[] orderBys;
	final private Node[] partitions;
	final private Limits limits;
	final private WhereNode where;
	final private QueryClause union;
	final private boolean unionByName;

	public PrepareClause(SqlColumnsNode prep, AsNode[] tables, AsNode[] selects, Node[] orderBys, Node[] partitions, Limits limits, WhereNode where, QueryClause union,
			boolean unionByName) {
		this.prep = prep;
		this.selects = selects;
		this.orderBys = orderBys;
		this.partitions = partitions;
		this.limits = limits;
		this.where = where;
		this.union = union;
		this.tables = tables;
		this.unionByName = unionByName;
	}

	@Override
	public int getOperation() {
		return SqlExpressionParser.ID_PREPARE;
	}

	@Override
	public boolean isUnionByName() {
		return this.unionByName;
	}

	@Override
	public QueryClause getUnion() {
		return this.union;
	}

	@Override
	public AsNode[] getTables() {
		return tables;
	}

	@Override
	public int getPosition() {
		return prep.getPosition();
	}

	public SqlColumnsNode getPrep() {
		return prep;
	}

	@Override
	public AsNode[] getSelects() {
		return selects;
	}

	public Node[] getOrderBys() {
		return orderBys;
	}

	public Node[] getPartitions() {
		return partitions;
	}

	public Limits getLimits() {
		return limits;
	}

	@Override
	public WhereNode getWhere() {
		return where;
	}

}
