package com.f1.utils.string;

import java.util.Map;

import com.f1.base.DateMillis;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.CastNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.KeywordNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.VariableNode;

public class ExpressionParserHelper {

	public static final CharMatcher WS = new BasicCharMatcher("\r\n\t ", false);
	public static final CharMatcher NUMBERS = new BasicCharMatcher("0-9", false);
	public static final CharMatcher NUMBERS16 = new BasicCharMatcher("0-9a-fA-F", false);
	private static final char[] COMMENT_SLASH_SLASH = "//".toCharArray();
	private static final char[] COMMENT_SLASH_STAR = "/*".toCharArray();
	private static final char[] COMMENT_STAR_SLASH = "*/".toCharArray();
	private static final Map<String, Node> CONSTS = CH.m(RH.TRUE, new ConstNode(Boolean.TRUE), RH.FALSE, new ConstNode(Boolean.FALSE), RH.NULL, new ConstNode(null), RH.NEW,
			new KeywordNode(RH.NEW), RH.IMPORT, new KeywordNode(RH.IMPORT), RH.CLASS, new ConstNode(RH.CLASS), RH.FOR, new KeywordNode(RH.FOR), RH.DO, new KeywordNode(RH.DO),
			RH.RETURN, new KeywordNode(RH.RETURN), RH.BREAK, new KeywordNode(RH.BREAK), RH.CONTINUE, new KeywordNode(RH.CONTINUE), "pause", new KeywordNode("pause"), "throw",
			new KeywordNode("throw"));
	public static final CharMatcher SPECIAL_CHARS_AND_DOT = new BasicCharMatcher("+\\-*/^()[],;<>= !&|~%:.}{\n\r?", true);
	public static final CharMatcher SPECIAL_CHARS_AND_DOT_NO_EOF = new BasicCharMatcher("+\\-*/^()[],;<>= !&|~%:.}{\n\r?", false);
	public static final CharMatcher SPECIAL_CHARS = new BasicCharMatcher("+\\-*/^()[],;<>= !&|~%:}{\n\r?", true);
	public static final String DIRECTIVE = "DIRECTIVE";
	protected static final char[] ELSE = "else".toCharArray();
	//	protected static final char[] ELSE_ = "else ".toCharArray();
	//	protected static final char[] ELSEP = "else(".toCharArray();
	protected static final char[] FOR = "for".toCharArray();
	//	protected static final char[] FOR_ = "for ".toCharArray();
	//	protected static final char[] FORP = "for(".toCharArray();
	protected static final char[] IF = "if".toCharArray();
	//	protected static final char[] IF_ = "if ".toCharArray();
	//	protected static final char[] IFP = "if(".toCharArray();
	protected static final char[] DO = "do".toCharArray();
	//	protected static final char[] DO_ = "do ".toCharArray();
	//	protected static final char[] DOP = "do{".toCharArray();
	protected static final char[] WHILE = "while".toCharArray();
	protected static final char[] WHILE_ = "while ".toCharArray();
	//	protected static final char[] WHILEP = "while(".toCharArray();
	protected static final char[] EXTERN = "extern".toCharArray();
	protected static final char[] NEW = "new".toCharArray();
	protected static final char[] NOT = "not".toCharArray();
	protected static final char[] AND = "and".toCharArray();
	protected static final char[] THROW = "throw".toCharArray();
	protected static final char[] IMPORT = "import".toCharArray();
	protected static final char[] INSTANCEOF = "instanceof".toCharArray();
	protected static final char[] VIRTUAL = "virtual".toCharArray();
	protected static final char[] PAUSE = "pause ".toCharArray();
	public static final Node[] EMPTY_NODE_ARRAY = new Node[0];

	static public ConstNode parseConstNode(StringBuilder sb, int position) {
		final int sblen = sb.length();
		ConstNode v = null;
		if (sblen > 0) {
			try {
				if (sb.charAt(sblen - 1) == 'T')
					v = new ConstNode(position, new DateMillis(SH.parseLong(sb, 0, sblen - 1, 10)));
				else {
					v = new ConstNode(position, SH.parseConstant(sb));
				}
			} catch (ExpressionParserException e) {
				throw e;
			} catch (RuntimeException e) {
				throw new ExpressionParserException(position, e.getMessage(), e);
			}
			sb.setLength(0);
		}
		return v;
	}

	public static ConstNode parseNumber(CharReader c, StringBuilder sb) {
		SH.clear(sb);
		return parseNumber(c, c.readChar(), sb);
	}
	public static ConstNode parseNumber(CharReader c, char firstChar, StringBuilder sb) {
		int pos = c.getCountRead();
		boolean isFloat = false;
		sb.append(firstChar);
		if (firstChar == '0') {
			switch (c.peakOrEof()) {
				case 'x':
				case 'X': {
					sb.append(c.readChar());
					c.readWhileAny(NUMBERS16, sb);
					switch (c.peakOrEof()) {
						case 'l':
						case 'L':
							sb.append(c.readChar());
					}
					return parseConstNode(sb, pos);
				}
			}
		}
		c.readWhileAny(NUMBERS, sb);
		if (c.peakOrEof() == '.') {
			sb.append(c.readChar());
			c.readWhileAny(NUMBERS, sb);
			switch (c.peakOrEof()) {
				case 'i':
				case 'I':
				case 'j':
				case 'J':
					sb.append(c.readChar());
			}
			isFloat = true;
		}
		if (c.peakOrEof() == 'e' || c.peakOrEof() == 'E') {
			sb.append(c.readChar());
			if (c.peakOrEof() == '-' || c.peakOrEof() == '+') {
				sb.append(c.readChar());
			}
			c.readWhileAny(NUMBERS, sb);
			isFloat = true;
		}
		if (!isFloat) {
			switch (c.peakOrEof()) {
				case 'l':
				case 'L':
				case 'i':
				case 'I':
				case 'b':
				case 'B':
				case 's':
				case 'S':
				case 'j':
				case 'J':
				case 'd':
				case 'D':
				case 'f':
				case 'F':
				case 'u':
				case 'U':
					sb.append(c.readChar());
			}
		} else {
			switch (c.peakOrEof()) {
				case 'd':
				case 'D':
				case 'f':
				case 'F':
				case 'u':
				case 'U':
					sb.append(c.readChar());
			}
		}
		return parseConstNode(sb, pos);
	}

	public static Node getConst(int position, String s) {
		Node r = CONSTS.get(s);
		if (r == null)
			return null;
		if (r instanceof ConstNode)
			return new ConstNode(position, ((ConstNode) r).getValue());
		else if (r instanceof KeywordNode)
			return new KeywordNode(position, ((KeywordNode) r).getKeyword());
		else
			return r;
	}
	static public String toDescription(String termination) {
		if (termination.length() == 1) {
			switch (termination.charAt(0)) {
				case '"':
					return "quote(\")";
				case '\'':
					return "single quote(\')";
				case '`':
					return "backtick(`)";
			}
		}
		return termination;
	}
	static public boolean equalsIgnoreCaseFast(char[] chars, CharSequence s) {
		for (int i = chars.length; i != 0;)
			if (chars[--i] != Character.toLowerCase(s.charAt(i)))
				return false;
		return true;
	}
	public static void sws(CharReader c) {
		c.skip(WS);
		while (c.peakOrEof() == '/') {
			if (c.expectSequenceNoThrow(COMMENT_SLASH_SLASH)) {
				c.readUntil('\n', null);
				if (!c.isEof()) {
					c.expect('\n');
					c.skip('\r');
				}
			} else if (c.expectSequenceNoThrow(COMMENT_SLASH_STAR)) {
				c.readUntilSequence(COMMENT_STAR_SLASH, null);
				c.expectSequence(COMMENT_STAR_SLASH);
			} else
				break;
			c.skip(WS);
		}
	}
	static public <T extends Node> T castNode(Node value, Class<T> c) {
		if (value == null)
			return null;
		if (value instanceof ExpressionNode && c != ExpressionNode.class)
			value = ((ExpressionNode) value).getValue();
		if (c.isInstance(value))
			return (T) value;
		throw new ExpressionParserException(value.getPosition(),
				"Expecting " + SH.stripSuffix(c.getSimpleName(), "Node", false) + ", not " + SH.stripSuffix(OH.getSimpleClassName(value), "Node", false));
	}
	static public <T extends Node> T castNodeNotRequired(Node value, Class<T> c) {
		if (value instanceof ExpressionNode && c != ExpressionNode.class)
			value = ((ExpressionNode) value).getValue();
		if (value == null)
			return null;
		if (c.isInstance(value))
			return (T) value;
		throw new ExpressionParserException(value.getPosition(),
				"Expecting " + SH.stripSuffix(c.getSimpleName(), "Node", false) + ", not " + SH.stripSuffix(OH.getSimpleClassName(value), "Node", false));
	}

	static public <T extends Node> T castNode(Node value, Class<T> c, String name) {
		Node orig = value;
		while (value instanceof ExpressionNode && c != ExpressionNode.class)
			value = ((ExpressionNode) value).getValue();
		if (c.isInstance(value))
			return (T) value;
		throw new ExpressionParserException(orig == null ? -1 : orig.getPosition(), "Expecting " + name);
	}
	public static boolean rightmostIsOperation(Node root) {
		while (root != null) {
			if (root instanceof ConstNode)
				return false;
			else if (root instanceof ExpressionNode)
				root = ((ExpressionNode) root).getValue();
			else if (root instanceof CastNode)
				root = ((CastNode) root).getParam();
			else if (root instanceof OperationNode) {
				root = ((OperationNode) root).getRight();
				if (root == null)
					return true;
			} else if (root instanceof VariableNode) {
				return false;
			} else if (root instanceof MethodNode) {
				return false;
			} else if (root instanceof BlockNode) {
				return false;
			} else
				throw new RuntimeException(root.getClass().getName());
		}
		return false;
	}

	static final public boolean toVarnameWithDots(Node en, StringBuilder sb) {
		if (en == null)
			return false;
		Node t = en;
		for (;;) {
			if (t instanceof OperationNode) {
				OperationNode on = (OperationNode) t;
				if (on.getOp() == OperationNode.OP_PERIOD && on.getRight() instanceof VariableNode) {
					t = on.getLeft();
				} else
					return false;
			} else if (t instanceof VariableNode)
				break;
			else
				return false;
		}
		t = en;
		toCast2(t, sb);
		return true;
	}

	static final private void toCast2(Node t, StringBuilder sb) {
		if (t instanceof VariableNode)
			sb.append(((VariableNode) t).getVarname());
		else {
			OperationNode on = (OperationNode) t;
			toCast2(on.getLeft(), sb);
			sb.append('.').append(((VariableNode) on.getRight()).getVarname());
		}
	}

	protected Node processEmptyOperation(int position, byte op, CharReader c) {
		throw new ExpressionParserException(position, "invalid expression near operation: " + OperationNode.toString(op));
	}

	public boolean isValidVarName(String value) {
		if (value == null)
			return false;
		int l = value.length();
		if (l == 0)
			return false;
		char c = value.charAt(0);
		if (OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && c != '_')
			return false;
		for (int i = 1; i < l; i++) {
			c = value.charAt(i);
			if (OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && OH.isntBetween(c, '0', '9') && c != '_')
				return false;
		}
		return !SqlExpressionParser.isReserved(value);
	}
}
