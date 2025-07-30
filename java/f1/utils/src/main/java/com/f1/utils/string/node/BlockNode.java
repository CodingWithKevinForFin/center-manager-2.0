package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class BlockNode implements Node {

	public static final byte CODE = Node.BLOCK;
	final private List<Node> nodes;
	final private CatchNode catchNodes[];
	final private boolean isImplicit, isConcurrent;

	public BlockNode(int position, List<Node> expressions, CatchNode catchNodes[], boolean isImplicit, boolean isConcurrent) {
		this.position = position;
		this.nodes = expressions;
		this.catchNodes = catchNodes;
		this.isImplicit = isImplicit;
		this.isConcurrent = isConcurrent;
	}

	public BlockNode(int position, List<Node> expressions) {
		this.position = position;
		this.nodes = expressions;
		this.catchNodes = null;
		this.isImplicit = false;
		this.isConcurrent = false;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	final private int position;

	@Override
	public int getPosition() {
		return position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		sink.append('{');
		SH.join(';', nodes, sink);
		sink.append("; }");
		if (catchNodes != null)
			for (CatchNode i : catchNodes)
				i.toString(sink);
		return sink;
	}

	public boolean isImplicit() {
		return isImplicit;
	}

	public int getNodesCount() {
		return this.nodes == null ? 0 : this.nodes.size();
	}
	public Node getNodeAt(int n) {
		return this.nodes.get(n);
	}

	public int getCatchNodesCount() {
		return this.catchNodes == null ? 0 : this.catchNodes.length;
	}

	public boolean isConcurrent() {
		return this.isConcurrent;
	}
	public CatchNode getCatchNodeAt(int n) {
		return this.catchNodes[n];
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		if (this.catchNodes == null)
			return this.nodes.size();
		return this.nodes.size() + this.catchNodes.length;
	}

	@Override
	public Node getInnerNode(int n) {
		return n < this.nodes.size() ? this.nodes.get(n) : this.catchNodes[n - this.nodes.size()];
	}
}
