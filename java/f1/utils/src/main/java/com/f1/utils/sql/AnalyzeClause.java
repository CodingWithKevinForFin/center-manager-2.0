package com.f1.utils.sql;

import java.util.Map;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.WhereNode;

public class AnalyzeClause implements QueryClause {

	final private SqlColumnsNode analyze;
	final private Map<String, WindowDef> windows;
	final private AsNode[] tables;
	final private AsNode[] selects;
	final private QueryClause union;
	final private boolean unionByName;

	public AnalyzeClause(SqlColumnsNode analyze, AsNode[] tables, AsNode[] selects, Map<String, WindowDef> windows, QueryClause union, boolean unionByName) {
		this.analyze = analyze;
		this.tables = tables;
		this.selects = selects;
		this.windows = windows;
		this.union = union;
		this.unionByName = unionByName;
	}

	static public class WindowDef {

		final private String name;
		final private Node onNode;
		final private Node[] orderBys;
		final private Node[] partitionBys;
		final private int indexId;

		public WindowDef(String name, Node onNode, Node[] orderBys, Node[] partitionBys, int indexId) {
			this.name = name;
			this.onNode = onNode;
			this.orderBys = orderBys;
			this.partitionBys = partitionBys;
			this.indexId = indexId;
		}

		public String getName() {
			return name;
		}

		public Node getOnNode() {
			return onNode;
		}

		public Node[] getOrderBys() {
			return orderBys;
		}

		public Node[] getPartitionBys() {
			return partitionBys;
		}

		public int getIndexId() {
			return indexId;
		}

	}

	@Override
	public int getOperation() {
		return SqlExpressionParser.ID_ANALYZE;
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
		return this.tables;
	}

	@Override
	public int getPosition() {
		return analyze.getPosition();
	}

	public SqlColumnsNode getAnalyze() {
		return analyze;
	}

	public Map<String, WindowDef> getWindows() {
		return windows;
	}

	@Override
	public AsNode[] getSelects() {
		return selects;
	}

	@Override
	public WhereNode getWhere() {
		return null;
	}

}
