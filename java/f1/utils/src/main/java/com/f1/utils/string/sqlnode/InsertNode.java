package com.f1.utils.string.sqlnode;

import com.f1.utils.SH;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;

final public class InsertNode extends SqlColumnsNode {
	private final String tablename;
	private final SqlForNode forloop;
	private final int tablenamePosition;
	private final Node[] syncOns;
	private final SqlColumnsNode limit;
	private final int tablenameScope;
	private final boolean byName;
	private final boolean returnGeneratedKeys;

	public InsertNode(int position, int operation, int tablePosition, String tablename, int tableNameScope, Node[] columns, SqlForNode forloop, Node values, Node[] syncOns,
			SqlColumnsNode limitNode, boolean byName, boolean returnGeneratedKeys) {
		super(position, columns, values, operation);
		this.tablenamePosition = tablePosition;
		this.tablename = tablename;
		this.tablenameScope = tableNameScope;
		this.byName = byName;
		this.forloop = forloop;
		this.syncOns = syncOns;
		this.limit = limitNode;
		this.returnGeneratedKeys = returnGeneratedKeys;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append(SqlExpressionParser.toOperationString(getOperation()));
		if (returnGeneratedKeys)
			sink.append(" AND SELECT");
		sink.append(" INTO ").append(this.tablename);
		if (getColumns() != null)
			SH.join(',', getColumns(), sink.append(" (")).append(")");
		if (syncOns != null)
			SH.join(',', syncOns, sink.append(" ON (")).append(")");
		if (forloop != null)
			forloop.toString(sink.append(' '));
		sink.append(" VALUES");

		if (byName)
			sink.append(" FROM BYNAME");
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		if (getLimit() != null)
			getLimit().toString(sink);
		return sink;
	}

	public String getTablename() {
		return tablename;
	}

	public SqlForNode getForloop() {
		return forloop;
	}

	public int getTablenamePosition() {
		return tablenamePosition;
	}

	public SqlColumnsNode getLimit() {
		return limit;
	}

	public int getTablenameScope() {
		return tablenameScope;
	}

	public boolean isByName() {
		return byName;
	}

	public boolean isReturnGeneratedKeys() {
		return returnGeneratedKeys;
	}

	public int getSynsOnsCount() {
		return this.syncOns == null ? 0 : this.syncOns.length;
	}
	public Node getSyncOn(int n) {
		return this.syncOns[n];
	}
	@Override
	public int getInnerNodesCount() {
		int n = getColumnsCount();
		if (syncOns != null)
			n += syncOns.length;
		if (forloop != null)
			n++;
		if (limit != null)
			n++;
		if (getNext() != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		if (n < getColumnsCount())
			return getColumnAt(n);
		n -= getColumnsCount();
		if (syncOns != null) {
			if (n < syncOns.length)
				return syncOns[n];
			n -= syncOns.length;
		}
		if (forloop != null && n-- == 0)
			return forloop;
		if (limit != null && n-- == 0)
			return limit;
		return getNext();
	}

}
