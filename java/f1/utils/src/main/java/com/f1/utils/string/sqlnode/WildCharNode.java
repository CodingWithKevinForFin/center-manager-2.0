package com.f1.utils.string.sqlnode;

import java.util.List;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class WildCharNode implements Node {

	private static final byte CODE = Node.WILDCARD;
	final private List<VariableNode> exceptList;
	final private int position;
	final private VariableNode tableName;

	public WildCharNode(int position, VariableNode tableName, List<VariableNode> exceptList) {
		this.position = position;
		this.tableName = tableName;
		this.exceptList = exceptList;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (tableName != null)
			tableName.toString(sink).append(".* ");
		else
			sink.append("\"*\"");
		if (CH.isntEmpty(exceptList)) {
			sink.append(" EXCEPT ");
			SH.join(' ', exceptList, sink);
		}
		return sink;
	}

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public String getTableName() {
		return this.tableName == null ? null : this.tableName.getVarname();
	}

	public List<VariableNode> getExcepts() {
		return this.exceptList;
	}

	public int getExceptsCount() {
		return this.exceptList == null ? 0 : this.exceptList.size();
	}
	public VariableNode getExceptAt(int n) {
		return this.exceptList.get(n);
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return exceptList == null ? 1 : (1 + exceptList.size());
	}

	@Override
	public Node getInnerNode(int n) {
		if (n == 0)
			return tableName;
		else
			return this.exceptList.get(n - 1);
	}
}
