package com.f1.utils.sql;

import com.f1.utils.AH;
import com.f1.utils.sql.SqlProcessorUtils.Limits;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.WhereNode;

public class SelectClause implements QueryClause {
	final private SqlColumnsNode select;
	final private SqlColumnsNode from;
	final private WhereNode where;
	final private SqlColumnsNode groupBy;
	final private SqlColumnsNode having;
	final private QueryClause union;
	final private boolean unionByName;
	final private AsNode[] columns;
	final private AsNode[] tables;
	final private Node whereCondition;
	final private Node[] orderBys;
	final private boolean[] orderBysAsc;
	final private Limits limits;
	final private Node join;
	final private OnNode[] unpack;
	final private int joinType;
	final private Node joinNearest;

	public SelectClause(SqlColumnsNode select, SqlColumnsNode from, SqlColumnsNode join, SqlColumnsNode joinOn, SqlColumnsNode joinNearest, WhereNode where, SqlColumnsNode groupBy,
			SqlColumnsNode having, SqlColumnsNode orderBy, SqlColumnsNode limit, SqlColumnsNode unpack, QueryClause union, boolean unionByName) {
		this.select = select;
		this.from = from;
		this.unionByName = unionByName;
		this.where = where;
		this.groupBy = groupBy;
		this.having = having;
		this.union = union;
		if (unpack != null) {
			this.unpack = AH.castTo(unpack.getColumns(), OnNode.class);
		} else
			this.unpack = null;

		//			Node[] cols = this.select.columns;
		if (this.select.getColumnsCount() == 0 || this.select.getColumnAt(0) == null)
			throw new ExpressionParserException(select.getPosition(), "Must specify at least one column");
		columns = SqlProcessorUtils.toAsNode(this.select.getColumns());

		if (from == null) {
			this.tables = new AsNode[0];
			this.joinType = -1;
			this.joinNearest = null;
			this.join = null;
		} else {
			//				cols = this.from.columns;
			if (this.from.getColumnsCount() == 0)
				throw new ExpressionParserException(select.getPosition(), "Must specify at least one table");
			for (int i = 0; i < this.from.getColumnsCount(); i++)
				if (this.from.getColumnAt(i) == null)
					throw new ExpressionParserException(from.getPosition(), "Missing table list in from");

			if (join != null) {
				if (this.from.getColumnsCount() != 1 || join.getColumnsCount() != 1)
					throw new ExpressionParserException(join.getPosition(), "Must specify excatly 2 tables on outer join clauses");
				tables = new AsNode[2];
				tables[0] = SqlProcessorUtils.toAsNode(this.from.getColumnAt(0));
				tables[1] = SqlProcessorUtils.toAsNode(join.getColumnAt(0));
				this.join = joinOn == null ? null : joinOn.getColumnAt(0);
				this.joinType = join.getOperation();
				this.joinNearest = joinNearest == null ? null : joinNearest.getColumnAt(0);
			} else {
				this.join = null;
				tables = SqlProcessorUtils.toAsNode(this.from.getColumns());
				joinType = -1;
				this.joinNearest = null;
			}
		}
		if (where != null) {
			WhereNode w = where;
			this.whereCondition = w.getCondition();
		} else
			whereCondition = null;
		if (orderBy != null) {
			this.orderBys = new Node[orderBy.getColumnsCount()];
			this.orderBysAsc = new boolean[this.orderBys.length];
			for (int i = 0; i < this.orderBys.length; i++) {
				SqlColumnsNode node = (SqlColumnsNode) orderBy.getColumnAt(i);
				orderBysAsc[i] = node.getOperation() == SqlExpressionParser.ID_ASC;
				orderBys[i] = node.getNext();
			}
		} else {
			this.orderBys = null;
			this.orderBysAsc = null;
		}
		if (limit == null)
			limits = null;
		else
			limits = new Limits(limit);
	}

	public Node getWhereCondition() {
		return this.whereCondition;
	}

	public AsNode[] getTables() {
		return tables;
	}

	public SqlColumnsNode getSelect() {
		return select;
	}
	public SqlColumnsNode getFrom() {
		return from;
	}

	@Override
	public WhereNode getWhere() {
		return where;
	}
	public SqlColumnsNode getGroupBy() {
		return groupBy;
	}
	public Node[] getGroupBys() {
		return this.groupBy == null ? null : this.groupBy.getColumns();
	}
	public Node[] getOrderBys() {
		return this.orderBys;
	}
	public boolean[] getOrderByAsc() {
		return this.orderBysAsc;
	}
	public SqlColumnsNode getHaving() {
		return having;
	}
	public QueryClause getUnion() {
		return union;
	}

	public Limits getLimits() {
		return limits;
	}
	public Node getJoin() {
		return join;
	}
	public Node getJoinNearest() {
		return joinNearest;
	}
	public int getJoinType() {
		return joinType;
	}
	public OnNode[] getUnpacks() {
		return unpack;
	}

	public boolean isUnionByName() {
		return unionByName;
	}

	@Override
	public int getOperation() {
		return SqlExpressionParser.ID_SELECT;
	}

	@Override
	public int getPosition() {
		return this.select.getPosition();
	}

	@Override
	public AsNode[] getSelects() {
		return this.columns;
	}
}
