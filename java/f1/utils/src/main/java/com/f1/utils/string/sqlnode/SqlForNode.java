package com.f1.utils.string.sqlnode;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

final public class SqlForNode extends SqlNode {
	private final Node start, end, step;
	private final VariableNode varname;

	public SqlForNode(int position, VariableNode varname, Node start, Node end, Node step, Node next) {
		super(position, next, SqlExpressionParser.ID_FOR);
		this.varname = varname;
		this.start = start;
		this.end = end;
		this.step = step;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append("FOR ");
		getVarname().toString(sink).append(" = ");
		start.toString(sink).append(" TO ");
		end.toString(sink);
		if (step != null)
			step.toString(sink.append(" STEP "));
		if (getNext() != null)
			getNext().toString(sink.append(' '));
		return sink;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	public Node getStart() {
		return start;
	}

	public Node getEnd() {
		return end;
	}

	public Node getStep() {
		return step;
	}

	public VariableNode getVarname() {
		return varname;
	}

	@Override
	public int getInnerNodesCount() {
		int n = 3;
		if (step != null)
			n++;
		if (getNext() != null)
			n++;
		return n;
	}

	@Override
	public Node getInnerNode(int n) {
		switch (n) {
			case 0:
				return varname;
			case 1:
				return start;
			case 2:
				return end;
			case 3:
				if (step != null)
					return step;
		}
		return getNext();
	}
}
