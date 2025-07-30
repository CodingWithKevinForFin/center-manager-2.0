package com.f1.utils.string.node;

import java.util.List;

import com.f1.utils.MH;
import com.f1.utils.SH;
import com.f1.utils.string.Node;

final public class MethodDeclarationNode implements Node {

	public static final byte MODIFIER_VIRTUAL = 1;
	public static final byte CODE = Node.METHOD_DECLARATION;
	final private String methodName;
	final private List<DeclarationNode> params;
	final private String returnType;
	final private Node body;
	private byte modifiers;
	final private String bodyText;
	final private int bodyStart;
	final private int bodyEnd;

	public MethodDeclarationNode(int position, String returnType, String methodName, List<DeclarationNode> params, String bodyText, int bodyStart, int bodyEnd, Node body) {
		this.returnType = returnType;
		this.position = position;
		this.methodName = methodName;
		this.params = params;
		this.body = body;
		this.bodyText = bodyText;
		this.bodyStart = bodyStart;
		this.bodyEnd = bodyEnd;
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
		sink.append(returnType).append(' ');
		sink.append(methodName).append('(');
		if (params != null)
			SH.join(',', params, sink);
		sink.append(')');
		if (body != null)
			body.toString(sink);
		return sink;
	}

	public void addModifier(byte modifier) {
		this.modifiers |= modifier;
	}

	public byte getModifiers() {
		return this.modifiers;
	}

	public boolean isVirtual() {
		return MH.anyBits(this.modifiers, MODIFIER_VIRTUAL);
	}

	public String geBodytText() {
		return this.bodyText;
	}
	public int getBodytStart() {
		return this.bodyStart;
	}
	public int getBodytEnd() {
		return this.bodyEnd;
	}

	public int getParamsCount() {
		return this.params == null ? 0 : this.params.size();
	}

	public String getMethodName() {
		return methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public DeclarationNode getParamAt(int i) {
		return this.params.get(i);
	}

	public Node getBody() {
		return this.body;
	}

	@Override
	public byte getNodeCode() {
		return CODE;
	}

	@Override
	public int getInnerNodesCount() {
		return this.body == null ? this.params.size() : (this.params.size() + 1);
	}

	@Override
	public Node getInnerNode(int n) {
		return n < this.params.size() ? this.params.get(n) : this.body;
	}

}
