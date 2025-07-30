package com.f1.utils.string.node;

import com.f1.utils.string.Node;

final public class OperationNode implements Node {
	public static final byte CODE = Node.OPERATION;

	public static final byte OP_AMP = 1;// &
	public static final byte OP_AMP_AMP = 2;// &&
	public static final byte OP_AMP_EQ = 3;// &
	public static final byte OP_BANG = 4;// !
	public static final byte OP_BANG_EQ = 5; // !=
	public static final byte OP_BANG_TILDE = 6; // !~
	public static final byte OP_BRACKET_BRACKET = 53; //[
	public static final byte OP_COLON = 7;// :
	public static final byte OP_EBRACKET = 8; //]
	public static final byte OP_EPARENTHESIS = 9; //]
	public static final byte OP_EQ = 10;// =
	public static final byte OP_EQ_EQ = 11;// ==
	public static final byte OP_EQ_TILDE = 12; // =~
	public static final byte OP_GT = 13; // <
	public static final byte OP_GT_EQ = 14; // <=
	public static final byte OP_GT_GT = 15; // >> 
	public static final byte OP_GT_GT_GT = 16; // >>>
	public static final byte OP_HAT = 17; // ^
	public static final byte OP_HAT_EQ = 18; // ^=
	public static final byte OP_IN = 19; // in
	public static final byte OP_LT = 20; // <
	public static final byte OP_LT_EQ = 21; // <=
	public static final byte OP_LT_LT = 22; // <<
	public static final byte OP_MINUS = 23;// -
	public static final byte OP_MINUS_EQ = 24; // -=
	public static final byte OP_MINUS_MINUS = 25;// --
	public static final byte OP_PIPE = 28;// |
	public static final byte OP_PIPE_PIPE = 29;// ||
	public static final byte OP_PLUS = 30;// +
	public static final byte OP_PLUS_EQ = 31; // +=
	public static final byte OP_PLUS_PLUS = 32;// ++
	public static final byte OP_PERCENT = 33;// .
	public static final byte OP_PERCENT_EQ = 34;// .
	public static final byte OP_PERIOD = 35;// .
	public static final byte OP_PIPE_EQ = 36; // |=
	public static final byte OP_QMARK = 37;// ?
	public static final byte OP_SBRACKET = 38; //[
	public static final byte OP_SPARENTHESIS = 39; //(
	public static final byte OP_SLASH = 40;// /
	public static final byte OP_SLASH_EQ = 41; // /=
	public static final byte OP_STAR = 42;// *
	public static final byte OP_STAR_EQ = 43; // *=
	public static final byte OP_TILDE = 44; // ~
	public static final byte OP_TILDE_EQ = 45; // ~=
	public static final byte OP_TILDE_TILDE = 46; // ~~
	public static final byte OP_NEW = 48;// new
	public static final byte OP_NOT = 54;// not
	public static final byte OP_THROW = 49;// throw
	public static final byte OP_IMPORT = 50;// import
	public static final byte OP_VIRTUAL = 51;// virtual
	public static final byte OP_INSTANCEOF = 52;// instanceof
	public static final byte OP_ARRAYDEREF = 53;// instanceof
	private Node left, right;
	private byte operation;

	public OperationNode(int position, Node left, Node right, byte operation) {
		this.position = position;
		this.left = left;
		this.right = right;
		this.operation = operation;
	}
	public void reset(int position, Node left, Node right, byte operation) {
		this.position = position;
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	@Override
	public String toString() {
		return toString(new StringBuilder()).toString();
	}

	private int position;

	@Override
	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public StringBuilder toString(StringBuilder sink) {
		if (operation == OP_COLON) {
			if (left != null)
				left.toString(sink);
			sink.append(toString(operation));
			if (right != null)
				right.toString(sink);
			return sink;
		} else {
			if (left != null)
				left.toString(sink);
			sink.append(' ').append(toString(operation)).append(' ');
			if (right != null)
				right.toString(sink);
			if ("[".equals(toString(operation)))
				sink.append(" ] ");
			return sink;
		}
	}

	public byte getOp() {
		return operation;
	}

	public static String toString(byte code) {
		switch (code) {
			case OP_AMP:
				return "&";
			case OP_AMP_AMP:
				return "&&";
			case OP_AMP_EQ:
				return "&=";
			case OP_BANG:
				return "!";
			case OP_BANG_EQ:
				return "!=";
			case OP_BANG_TILDE:
				return "!~";
			case OP_BRACKET_BRACKET:
				return "[]";
			case OP_COLON:
				return ":";
			case OP_EBRACKET:
				return "]";
			case OP_EPARENTHESIS:
				return ")";
			case OP_EQ:
				return "=";
			case OP_EQ_EQ:
				return "==";
			case OP_EQ_TILDE:
				return "=~";
			case OP_GT:
				return ">";
			case OP_GT_EQ:
				return ">=";
			case OP_GT_GT:
				return ">>";
			case OP_GT_GT_GT:
				return ">>>";
			case OP_HAT:
				return "^";
			case OP_HAT_EQ:
				return "^=";
			case OP_IN:
				return "in";
			case OP_LT:
				return "<";
			case OP_LT_EQ:
				return "<=";
			case OP_LT_LT:
				return "<<";
			case OP_MINUS:
				return "-";
			case OP_MINUS_EQ:
				return "-=";
			case OP_MINUS_MINUS:
				return "--";
			case OP_PIPE:
				return "|";
			case OP_PIPE_PIPE:
				return "||";
			case OP_PLUS:
				return "+";
			case OP_PLUS_EQ:
				return "+=";
			case OP_PLUS_PLUS:
				return "++";
			case OP_PERCENT:
				return "%";
			case OP_PERCENT_EQ:
				return "%=";
			case OP_PERIOD:
				return ".";
			case OP_PIPE_EQ:
				return "|=";
			case OP_QMARK:
				return "?";
			case OP_SBRACKET:
				return "[";
			case OP_SPARENTHESIS:
				return "(";
			case OP_SLASH:
				return "/";
			case OP_SLASH_EQ:
				return "/=";
			case OP_STAR:
				return "*";
			case OP_STAR_EQ:
				return "*=";
			case OP_TILDE:
				return "~";
			case OP_TILDE_EQ:
				return "~=";
			case OP_TILDE_TILDE:
				return "~~";
			case OP_NEW:
				return "new";
			case OP_NOT:
				return "not";
			case OP_THROW:
				return "throw";
			case OP_IMPORT:
				return "import";
			case OP_VIRTUAL:
				return "virtual";
			case OP_INSTANCEOF:
				return "instanceof";
			default:
				return "ERR:" + code;
		}

	}
	public Node getRight() {
		return right;
	}

	public Node getLeft() {
		return left;
	}
	public void setRight(Node v) {
		this.right = v;
	}
	public void setLeft(Node v) {
		this.left = v;
	}
	@Override
	public byte getNodeCode() {
		return CODE;
	}
	public String getOpString() {
		return toString(operation);
	}
	@Override
	public int getInnerNodesCount() {
		if (left != null)
			return right != null ? 2 : 1;
		else
			return right != null ? 1 : 0;
	}
	@Override
	public Node getInnerNode(int n) {
		return (left != null && n == 0) ? left : right;
	}

}
