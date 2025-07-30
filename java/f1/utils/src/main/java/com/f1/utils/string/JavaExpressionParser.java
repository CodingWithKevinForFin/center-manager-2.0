package com.f1.utils.string;

import java.util.ArrayList;
import java.util.List;

import com.f1.utils.AH;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.RH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.node.AppendNode;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.CastNode;
import com.f1.utils.string.node.CatchNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.DeclarationNode;
import com.f1.utils.string.node.DoWhileNode;
import com.f1.utils.string.node.ExpressionNode;
import com.f1.utils.string.node.ExternNode;
import com.f1.utils.string.node.FlowControlNode;
import com.f1.utils.string.node.ForEachNode;
import com.f1.utils.string.node.ForNode;
import com.f1.utils.string.node.GroupNode;
import com.f1.utils.string.node.IfElseNode;
import com.f1.utils.string.node.MapNode;
import com.f1.utils.string.node.MethodDeclarationNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.NewNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.StringTemplateNode;
import com.f1.utils.string.node.ThrowNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.node.WhileNode;

public class JavaExpressionParser extends ExpressionParserHelper implements ExpressionParser {
	public static final byte STATE_JAVA = 1;
	public static final byte STATE_STRING_TEMPLATE = 2;

	private static final int NOT_OPERATOR = -1;

	private byte[] stateStack = new byte[128];
	private int stateStackSize = 0;
	private boolean allowEof = true;
	private boolean allowImplicitBlock = true;

	final public void setAllowEof(boolean allowEof) {
		this.allowEof = allowEof;
	}

	final public boolean getAllowEof() {
		return allowEof;
	}

	final public void pushState(byte state) {
		if (stateStackSize == stateStack.length)
			throw new RuntimeException("State Stackoverflow");
		stateStack[stateStackSize++] = state;
	}

	final public void popState(byte expected) {
		assertState(expected);
		stateStackSize--;
	}

	final public void assertState(byte expected) {
		OH.assertEq(getState(), expected);
	}

	final public byte getState() {
		return stateStack[stateStackSize - 1];
	}

	@Override
	final public Node parse(CharSequence text) {
		OH.assertEq(this.stateStackSize, 0);
		pushState(STATE_JAVA);
		StringCharReader reader = new StringCharReader(text);
		reader.setToStringIncludesLocation(true);
		reader.setCaseInsensitive(true);
		try {
			List<Node> remaining = null;
			Node r = null;
			for (;;) {
				sws(reader);
				if (reader.isEof())
					break;
				final Node t = parseStatement(reader);
				if (t == null)
					continue;
				if (remaining != null)
					remaining.add(t);
				else if (r == null)
					r = t;
				else {
					remaining = new ArrayList<Node>();
					remaining.add(r);
					remaining.add(t);
					r = null;
				}
			}
			return remaining != null ? new BlockNode(0, remaining, null, allowImplicitBlock, false) : r;
		} catch (ExpressionParserException e) {
			if (e.getExpression() == null)
				e.setExpression(reader.getAsText());
			throw e;
		} catch (ArrayIndexOutOfBoundsException e) {
			ExpressionParserException e2 = reader.newExpressionParserException("Unexpected end of expression");
			if (e2.getExpression() == null)
				e2.setExpression(reader.getAsText());
			throw e2;
		} catch (Exception e) {
			ExpressionParserException e2 = reader.newExpressionParserException("Unknown Parser error " + e.getMessage(), e);
			if (e2.getExpression() == null)
				e2.setExpression(reader.getAsText());
			throw e2;
		} finally {
			onParseComplete();
		}
	}

	protected void onParseComplete() {
		popState(STATE_JAVA);
		OH.assertEq(this.stateStackSize, 0);
	}

	protected String toStateString(byte type) {
		switch (type) {
			case STATE_JAVA:
				return "JAVA";
			case STATE_STRING_TEMPLATE:
				return "STRING_TEMPLATE";
			default:
				return "STATE" + SH.toString(type);
		}
	}

	final protected int getOperatorPriorityNoThrow(byte operator) {
		switch (operator) {
			case OperationNode.OP_BRACKET_BRACKET:
				return 8;
			case OperationNode.OP_MINUS_EQ:
			case OperationNode.OP_PLUS_EQ:
			case OperationNode.OP_STAR_EQ:
			case OperationNode.OP_SLASH_EQ:
			case OperationNode.OP_PERCENT_EQ:
			case OperationNode.OP_EQ:
				return 10;
			case OperationNode.OP_QMARK:
				return 11;
			case OperationNode.OP_COLON:
				return 12;
			case OperationNode.OP_PIPE_PIPE:
				return 13;
			case OperationNode.OP_AMP_AMP:
				return 14;
			case OperationNode.OP_PIPE:
				return 15;
			case OperationNode.OP_HAT:
				return 16;
			case OperationNode.OP_AMP:
				return 17;
			case OperationNode.OP_EQ_EQ:
			case OperationNode.OP_BANG_EQ:
			case OperationNode.OP_EQ_TILDE:
			case OperationNode.OP_BANG_TILDE:
			case OperationNode.OP_TILDE_TILDE:
				return 18;
			case OperationNode.OP_LT:
			case OperationNode.OP_GT:
			case OperationNode.OP_LT_EQ:
			case OperationNode.OP_GT_EQ:
			case OperationNode.OP_INSTANCEOF:
				return 19;
			case OperationNode.OP_PLUS:
			case OperationNode.OP_MINUS:
			case OperationNode.OP_VIRTUAL:
				return 20;
			case OperationNode.OP_STAR:
			case OperationNode.OP_SLASH:
			case OperationNode.OP_PERCENT:
				return 21;
			case OperationNode.OP_BANG:
			case OperationNode.OP_TILDE:
			case OperationNode.OP_PLUS_PLUS:
			case OperationNode.OP_MINUS_MINUS:
			case OperationNode.OP_IN:
			case OperationNode.OP_NEW:
			case OperationNode.OP_NOT:
			case OperationNode.OP_THROW:
				return 22;
			case OperationNode.OP_PERIOD:
			case OperationNode.OP_SBRACKET:
			case OperationNode.OP_SPARENTHESIS:
				return 23;
		}
		return NOT_OPERATOR;
	}
	final protected byte getOperator(CharSequence s) {
		switch (s.length()) {
			case 1:
				switch (s.charAt(0)) {
					case '=':
						return OperationNode.OP_EQ;
					case '?':
						return OperationNode.OP_QMARK;
					case ':':
						return OperationNode.OP_COLON;
					case '|':
						return OperationNode.OP_PIPE;
					case '^':
						return OperationNode.OP_HAT;
					case '&':
						return OperationNode.OP_AMP;
					case '<':
						return OperationNode.OP_LT;
					case '>':
						return OperationNode.OP_GT;
					case '+':
						return OperationNode.OP_PLUS;
					case '-':
						return OperationNode.OP_MINUS;
					case '*':
						return OperationNode.OP_STAR;
					case '/':
						return OperationNode.OP_SLASH;
					case '%':
						return OperationNode.OP_PERCENT;
					case '!':
						return OperationNode.OP_BANG;
					case '~':
						return OperationNode.OP_TILDE;
					case '.':
						return OperationNode.OP_PERIOD;
					case '[':
						return OperationNode.OP_SBRACKET;
					case '(':
						return OperationNode.OP_SPARENTHESIS;
				}
				break;
			case 2:
				switch (s.charAt(0) | (s.charAt(1) << 16)) {
					case '-' | ('=' << 16):
						return OperationNode.OP_MINUS_EQ;
					case '+' | ('=' << 16):
						return OperationNode.OP_PLUS_EQ;
					case '*' | ('=' << 16):
						return OperationNode.OP_STAR_EQ;
					case '/' | ('=' << 16):
						return OperationNode.OP_SLASH_EQ;
					case '%' | ('=' << 16):
						return OperationNode.OP_PERCENT_EQ;
					case '|' | ('|' << 16):
					case 'o' | ('r' << 16):
					case 'O' | ('r' << 16):
					case 'o' | ('R' << 16):
					case 'O' | ('R' << 16):
						return OperationNode.OP_PIPE_PIPE;
					case '&' | ('&' << 16):
						return OperationNode.OP_AMP_AMP;
					case '=' | ('=' << 16):
						return OperationNode.OP_EQ_EQ;
					case '!' | ('=' << 16):
						return OperationNode.OP_BANG_EQ;
					case '=' | ('~' << 16):
						return OperationNode.OP_EQ_TILDE;
					case '!' | ('~' << 16):
						return OperationNode.OP_BANG_TILDE;
					case '~' | ('~' << 16):
						return OperationNode.OP_TILDE_TILDE;
					case '<' | ('=' << 16):
						return OperationNode.OP_LT_EQ;
					case '>' | ('=' << 16):
						return OperationNode.OP_GT_EQ;
					case '+' | ('+' << 16):
						return OperationNode.OP_PLUS_PLUS;
					case '-' | ('-' << 16):
						return OperationNode.OP_MINUS_MINUS;
					case 'i' | ('n' << 16):// in
					case 'I' | ('n' << 16):// In
					case 'i' | ('N' << 16):// iN
					case 'I' | ('N' << 16):// IN
						return OperationNode.OP_IN;
					case '[' | (']' << 16):
						return OperationNode.OP_BRACKET_BRACKET;
				}
				break;
			case 3:
				if (equalsIgnoreCaseFast(AND, s))
					return OperationNode.OP_AMP_AMP;
				if (equalsIgnoreCaseFast(NEW, s))
					return OperationNode.OP_NEW;
				if (equalsIgnoreCaseFast(NOT, s))
					return OperationNode.OP_NOT;
				break;
			case 5:
				if (equalsIgnoreCaseFast(THROW, s))
					return OperationNode.OP_THROW;
				break;
			case 6:
				if (equalsIgnoreCaseFast(IMPORT, s))
					return OperationNode.OP_IMPORT;
				break;
			case 7:
				if (equalsIgnoreCaseFast(VIRTUAL, s))
					return OperationNode.OP_VIRTUAL;
				break;
			case 10:
				if (equalsIgnoreCaseFast(INSTANCEOF, s))
					return OperationNode.OP_INSTANCEOF;
				break;
		}
		return -1;
	}

	protected final int getOperatorPriority(byte operator, String description) {
		int r = getOperatorPriorityNoThrow(operator);
		if (r == NOT_OPERATOR)
			throw new RuntimeException(description + ": " + operator);
		return r;
	}

	//A statement is a self-contained "line" or block of lines, ex:
	//  int n=15,m=20;
	//  for loops, if else statements.
	protected Node parseStatement(CharReader c) {
		try {
			sws(c);
			switch (c.peakOrEof()) {
				case CharReader.EOF:
					return null;
				case 'b':
				case 'B':
					if (c.read(RH.BREAK, SPECIAL_CHARS))
						return parseFlowControl(RH.BREAK, c);
					break;
				case 'c':
				case 'C':
					if (c.read(RH.CONTINUE, SPECIAL_CHARS))
						return parseFlowControl(RH.CONTINUE, c);
					break;
				case 'd':
				case 'D':
					if (c.read(DO, SPECIAL_CHARS))
						return parseDoWhile(c);
					break;
				case 'e':
				case 'E':
					if (c.read(EXTERN, SPECIAL_CHARS))
						return parseExtern(c);
					break;
				case 'f':
				case 'F':
					if (c.read(FOR, SPECIAL_CHARS))
						return parseFor(c);
					break;
				case 'i':
				case 'I':
					if (c.read(IF, SPECIAL_CHARS))
						return parseIf(c);
					//					if (c.read(RH.IMPORT, StringCharReader.WHITE_SPACE))
					//						return parseImport(c);
					break;
				case 'n':
				case 'N':
					if (c.read(NEW, SPECIAL_CHARS)) {
						return parseNew(c);
					}
					break;
				case 'p':
				case 'P':
					if (c.read(PAUSE, SPECIAL_CHARS))
						return parseFlowControl("pause", c);
					break;
				case 'r':
				case 'R':
					if (c.read(RH.RETURN, SPECIAL_CHARS))
						return parseFlowControl(RH.RETURN, c);
					break;
				case 't':
				case 'T':
					if (c.read(RH.THROW, SPECIAL_CHARS))
						return parseThrow(c);
					break;
				case 'w':
				case 'W':
					if (c.read(WHILE, SPECIAL_CHARS))
						return parseWhile(c);
					break;
				case 'v':
				case 'V':
					if (c.read(VIRTUAL, SPECIAL_CHARS))
						return parseVolatile(c);
					break;
				case '{':
					final Node r = parseBlock(c, true, false, true);
					sws(c);
					switch (c.peakOrEof()) {
						case '+':
						case '-':
						case '*':
						case '/':
						case '^':
						case '%':
						case '~':
						case '&':
						case '|':
						case '=':
						case '?':
						case '[':
						case ':':
						case '.':
							return parseExpression(r, c);
					}
					return r;
			}

			Node r = parseToken(c);
			int ch = c.peakOrEof();
			switch (ch) {
				case ';':
					c.expect(';');
					break;
				case '}':
				case ',':
					if (r instanceof DeclarationNode) {
						DeclarationNode tr = (DeclarationNode) r;
						//handle declaration with commas, ex: int a,b,c;
						while (c.expectNoThrow(',')) {
							Node t = parseStatement(c);
							String name = t instanceof VariableNode ? castNode(t, VariableNode.class).getVarname() : castNode(t, OperationNode.class).getLeft().toString();
							tr.setNext(new DeclarationNode(t.getPosition(), tr.getVartype(), name, t));
							tr = tr.getNext();
							sws(c);
						}
					} else if (r == null) {
						if (getState() == STATE_STRING_TEMPLATE && ch == '}')
							break;
						throw c.newExpressionParserException("Unexpected char: " + (char) ch);
					} else if (r instanceof OperationNode && ((OperationNode) r).getOp() == OperationNode.OP_COLON) {
						//This means we have a map entry
						if (ch == ',')
							c.expect(ch);
					}
					break;
				case CharReader.EOF:
					if (allowEof)
						break;
					//otherwise, continue to default clause and throw error
				default:
					if (!(r instanceof MethodDeclarationNode))
						throw onUnexpectedToken(r, c, ch);
			}
			return r;
		} catch (IndexOutOfBoundsException i) {
			throw c.newExpressionParserException("Unexpected end of statement", i);
		}
	}

	private MethodDeclarationNode parseVolatile(CharReader c) {
		//		c.expectSequence(VIRTUAL);
		Node r = parseToken(c);
		MethodDeclarationNode mn = castNode(r, MethodDeclarationNode.class);
		mn.addModifier(MethodDeclarationNode.MODIFIER_VIRTUAL);
		return mn;
	}

	private final ExternNode parseExtern(CharReader c) {
		int posExt = c.getCountRead() - 6;
		sws(c);
		StringBuilder buf = new StringBuilder();
		VariableNode name = parseVariableNode(c, buf);
		sws(c);
		int bracketCount = 0;
		while (c.expectNoThrow('{'))
			bracketCount++;
		if (bracketCount < 2)
			throw c.newExpressionParserException("Expecting leading {{");
		char[] chars = AH.fill(new char[bracketCount], '}');
		int posCode = c.getCountRead();
		int result = c.readUntilSequenceAndSkip(chars, SH.clear(buf));
		if (result == -1)
			throw c.newExpressionParserException("Missing trailing " + new String(chars));
		String code = buf.toString();
		c.skipChars(bracketCount);
		return new ExternNode(posExt, name, bracketCount, posCode, code);
	}

	final public Node parseBlock(CharReader c, boolean checkForCatch, boolean isConcurrent, boolean allowMap) {
		int position = c.getCountRead();
		c.expect('{');
		List<Node> nodes = new ArrayList<Node>();
		while (true) {
			sws(c);
			switch (c.peakOrEof()) {
				case CharReader.EOF:
					throw c.newExpressionParserException("Missing '}' at end of statement");
				case '}':
					c.expect('}');
					if (allowMap) {
						if (nodes.size() == 0)
							return new MapNode(position, ExpressionParserHelper.EMPTY_NODE_ARRAY);
						Node first = nodes.get(0);
						if (first instanceof OperationNode && ((OperationNode) first).getOp() == OperationNode.OP_COLON) {
							Node[] keyValues = new Node[nodes.size() * 2];
							for (int i = 0; i < nodes.size(); i++) {
								Node t = nodes.get(i);
								if (!(t instanceof OperationNode))
									throw new ExpressionParserException(t.getPosition(), "Invalid map entry, expecting: key : value");
								OperationNode node = (OperationNode) t;
								if (node.getOp() != OperationNode.OP_COLON)
									throw new ExpressionParserException(node.getPosition(), "Invalid map entry, expecting: key : value");
								keyValues[i * 2] = node.getLeft();
								keyValues[i * 2 + 1] = node.getRight();
							}
							return new MapNode(position, keyValues);
						}
					}
					if (checkForCatch) {
						sws(c);
						List<CatchNode> catches = null;
						while (c.peakSequence("catch")) {
							int pos = c.getCountRead();
							c.expectSequence("catch");
							sws(c);
							c.expect('(');
							sws(c);
							StringBuilder buf = new StringBuilder();
							sws(c);
							VariableNode type = parseVariableNode(c, buf);
							sws(c);
							VariableNode name = parseVariableNode(c, buf);
							sws(c);
							c.expect(')');
							sws(c);
							BlockNode catchBlock = castNode(parseBlock(c, false, false, false), BlockNode.class);
							sws(c);
							if (catches == null)
								catches = new ArrayList();
							catches.add(new CatchNode(pos, type, name, catchBlock));
						}
						return new BlockNode(position, nodes, catches == null ? null : AH.toArray(catches, CatchNode.class), false, isConcurrent);
					} else
						return new BlockNode(position, nodes);
			}
			nodes.add(parseStatement(c));
		}
	}

	protected ExpressionParserException onUnexpectedToken(Node r, CharReader c, int ch) {
		if (!(r instanceof ExpressionNode && ((ExpressionNode) r).getValue() instanceof MethodDeclarationNode))
			return c.newExpressionParserException("Unexpected token: '" + (char) ch + "'");
		return c.newExpressionParserException("Dangling text: " + (char) ch);
	}

	final protected Node parseToken(CharReader c) {
		Node r = parseExpression(null, c);
		return r;
	}

	protected MethodNode parseMethod(int position, String methodName, CharReader c) {
		List<Node> params = new ArrayList<Node>();
		boolean first = true;
		while (true) {
			sws(c);
			switch (c.peakOrEof()) {
				case ')':
					c.readChar();
					return new MethodNode(position, methodName, params);
				case ',': {
					if (first)
						throw c.newExpressionParserException("Missing expression");
					c.readChar();
					Node param = parseToken(c);
					if (param == null)
						throw c.newExpressionParserException("Missing expression");
					params.add(param);
					break;
				}
				case CharReader.EOF:
					c.peak();// throws EOF
				default: {
					if (!first)
						throw c.newExpressionParserException("Missing closing parenthesis");
					Node param = parseToken(c);
					if (param == null)
						throw c.newExpressionParserException("Missing expression");
					params.add(param);
					first = false;
				}
			}
		}
	}

	protected MethodDeclarationNode parseMethodDeclaration(String returnType, String methodName, CharReader c) {
		int position = c.getCountRead();
		List<DeclarationNode> params = new ArrayList<DeclarationNode>();
		boolean first = true;
		outer: while (true) {
			sws(c);
			switch (c.peakOrEof()) {
				case ')':
					c.readChar();
					break outer;
				case ',':
					c.readChar();
					params.add(castNode(parseToken(c), DeclarationNode.class));
					break;
				case CharReader.EOF:
					c.peak();// throws EOF
				default:
					if (!first)
						throw new ExpressionParserException(c.getCountRead(), "Unexpected token: " + c.readChar());
					params.add(castNode(parseToken(c), DeclarationNode.class));
					first = false;
			}
		}
		sws(c);
		if (c.peak() == ';')
			return new MethodDeclarationNode(position, returnType, methodName, params, null, -1, -1, null);
		else {
			int start = c.getCountRead();
			Node node = parseStatement(c);
			int end = c.getCountRead();
			return new MethodDeclarationNode(position, returnType, methodName, params, c.getText(), start, end, node);
		}
	}

	protected IfElseNode parseIf(CharReader c) {
		int position = c.getCountRead() - 2;
		sws(c);
		c.expect('(');
		Node ifClause = parseToken(c);
		c.expect(')');
		int pos = c.getCountRead();
		Node ifBlock = parseFlowBlock(c);
		if (ifBlock == null)
			throw new ExpressionParserException(pos, "if condition missing statement");
		sws(c);
		Node elseBlock = null;
		if (getState() == STATE_STRING_TEMPLATE && c.peakSequence("{else}")) {
			c.expectSequence("{else}");
			elseBlock = parseString(c, new StringBuilder(), "${}", false);
			c.expectSequence("${");
			if (elseBlock == null)
				throw new ExpressionParserException(pos, "else condition missing statement");
			sws(c);
		}
		if (c.read(ELSE, SPECIAL_CHARS)) {
			elseBlock = parseFlowBlock(c);
		}
		return new IfElseNode(position, ifClause, ifBlock, elseBlock);
	}

	private final WhileNode parseWhile(CharReader c) {
		int position = c.getCountRead();
		sws(c);
		c.expect('(');
		Node whileClause = parseToken(c);
		c.expect(')');
		int pos = c.getCountRead();
		Node block = parseFlowBlock(c);
		if (block == null)
			throw new ExpressionParserException(pos, "while loop missing statement");
		return new WhileNode(position, whileClause, block);
	}

	final private DoWhileNode parseDoWhile(CharReader c) {
		int position = c.getCountRead() - 2;
		Node block = parseStatement(c);
		sws(c);
		c.expectSequence(WHILE);
		sws(c);
		c.expect('(');
		Node whileClause = parseToken(c);
		c.expect(')');
		sws(c);
		c.expect(';');
		return new DoWhileNode(position, whileClause, block);
	}

	//	protected Node parseFlowControl(CharReader c) {
	//		StringBuilder sink = new StringBuilder();
	//		c.readUntilAny(SPECIAL_CHARS_AND_DOT, allowEof, '\\', sink);
	//		int position = c.getCountRead();
	//		Node block = parseToken(c);
	//		sws(c);
	//		c.expect(';');
	//		return new FlowControlNode(position, sink.toString(), block);
	//	}
	protected Node parseFlowControl(String s, CharReader c) {
		int position = c.getCountRead() - s.length();
		Node block = parseToken(c);
		sws(c);
		c.expect(';');
		return new FlowControlNode(position, s, block);
	}

	protected Node parseFor(CharReader c) {
		int position = c.getCountRead() - 3;
		sws(c);
		c.expect('(');
		Node inits = parseToken(c);
		sws(c);
		if (c.peak() == ')') {
			c.expect(')');
			if (inits == null)
				throw c.newExpressionParserException("for(...) clause must not be empty");
			if (inits instanceof DeclarationNode) {
				DeclarationNode dn = (DeclarationNode) inits;
				OperationNode o = castNode(dn.getParam(), OperationNode.class);
				if (o.getOp() != OperationNode.OP_COLON)
					throw c.newExpressionParserException("for(...) expecting variable:array style expression, found operator" + o.getOpString());
				int pos = c.getCountRead();
				Node block = parseFlowBlock(c);// change to handle block
				if (block == null)
					throw new ExpressionParserException(pos, "for(...) each loop missing statement");
				return new ForEachNode(position, new DeclarationNode(dn.getPosition(), dn.getVartype(), dn.getVarname(), null), o.getRight(), block);
			} else {
				throw c.newExpressionParserException("for(...) expecting declaration found: " + inits);
			}
		} else {
			c.expect(';');
			Node conditions = parseToken(c);
			c.expect(';');
			Node ops = parseToken(c);
			c.expect(')');
			int pos = c.getCountRead();
			Node block = parseFlowBlock(c);// change to handle block
			if (block == null)
				throw new ExpressionParserException(pos, "for loop missing statement");
			return new ForNode(position, inits, conditions, ops, block);
		}
	}

	protected ThrowNode parseThrow(CharReader c) {
		int position = c.getCountRead();
		Node value = parseToken(c);
		if (value == null)
			throw c.newExpressionParserException("Expecting value after throw clause");
		return new ThrowNode(position, value);
	}

	protected NewNode parseNew(CharReader c) {
		int position = c.getCountRead() - 3;
		StringBuilder sb = new StringBuilder();
		sws(c);
		c.readUntilAny(SPECIAL_CHARS, allowEof, '\\', sb);
		sws(c);
		char ch2 = c.readChar();
		String className = sb.toString();
		switch (ch2) {
			case '(': {
				List<Node> params = new ArrayList<Node>();
				boolean first = true;
				while (true) {
					sws(c);
					switch (c.peak()) {
						case ')':
							c.readChar();
							return new NewNode(position, className, params);
						case ',':
							c.readChar();
							params.add(parseToken(c));
							break;
						default:
							if (!first)
								throw new RuntimeException();
							params.add(parseToken(c));
							first = false;
					}
				}
			}
			//			case '[':
			//				List<Node> dimensions = new ArrayList<Node>();
			//				while (true) {
			//					sws(c);
			//					if (c.peak() == ']')
			//						dimensions.add(null);
			//					else
			//						dimensions.add(parseToken(c));
			//					c.expect(']');
			//					sws(c);
			//					char c2 = c.peak();
			//					switch (c2) {
			//						case '[':
			//							c.expect('[');
			//							continue;
			//						case '{':
			//							c.expect('{');
			//							ArrayNode arrayNode = parseArray(c);
			//							c.expect('}');
			//							return new NewNode(position, className, null, dimensions);
			//						default:
			//							return new NewNode(position, className, null, dimensions);
			//					}
			//				}
			default:
				throw c.newExpressionParserException("Unexpected token");
		}
	}

	//	protected ArrayNode parseArray(CharReader c) {
	//		int position = c.getCountRead();
	//		List<Node> params = new ArrayList<Node>();
	//		boolean first = true;
	//		while (true) {
	//			sws(c);
	//			switch (c.peak()) {
	//				case '}':
	//					return new ArrayNode(position, params);
	//				case ',':
	//					c.readChar();
	//					params.add(parseToken(c));
	//					break;
	//				default:
	//					if (!first)
	//						throw c.newExpressionParserException("Bad array declaration syntax");
	//					params.add(parseToken(c));
	//					first = false;
	//			}
	//		}
	//	}

	final protected Node parseExpressionParen(CharReader c) {
		//		int position = c.getCountRead();
		c.expect('(');
		Node node = null;
		while (true) {
			sws(c);
			switch (c.peakOrEof()) {
				case CharReader.EOF:
					throw c.newExpressionParserException("Missing ) at the end of statement");
				case ')':
					c.expect(')');
					return node;
			}
			if (node != null)
				throw c.newExpressionParserException("the expression contains multiple statements only expecting one");
			node = parseToken(c);
		}
	}
	final protected Node parseExpression(Node root, CharReader c) {
		for (;;) {
			sws(c);
			if (c.isEof())
				return root;
			Node tt = innerParseExpression(root, c);
			if (tt != null)
				return tt;
			byte op = -1;
			Node v = null;
			int oper = c.peakOrEof();
			char operation = (char) oper;
			if (oper == CharReader.EOF || isEndOfExpression(operation, c))
				return root;
			StringBuilder sb = new StringBuilder();
			final int position = c.getCountRead();
			switch (operation) {
				case '(':
					onEnteringParenthesis();
					try {
						List<Node> nodes = null;
						c.expect(operation);
						for (;;) {
							sws(c);
							if (c.peak() == '{') {
								v = parseStatement(c);
							} else {
								v = parseToken(c);
								if (v == null)
									throw c.newExpressionParserException("body missing");
								//							((ExpressionNode) v).hasParenthesis = true;
							}
							if (c.expectNoThrow(',')) {
								if (nodes == null)
									nodes = new ArrayList<Node>();
								nodes.add(v);
							} else {
								if (nodes != null)
									nodes.add(v);
								break;
							}
						}
						if (c.expectNoThrow(';'))
							sws(c);
						if (c.readCharOrEof() != ')')
							throw c.newExpressionParserException("missing closing parantheses ')'");
						if (nodes != null)
							v = new GroupNode(position, nodes);
						else {
							v = new ExpressionNode(position, v);
						}

					} finally {
						onExitingParenthesis();
					}
					break;
				case '[':
					c.expect(operation);
					Node t = root;
					while (t instanceof OperationNode)
						t = ((OperationNode) t).getRight();
					if (t == null) {
						List params = new ArrayList();
						boolean first = true;
						outer: while (true) {
							sws(c);
							switch (c.peakOrEof()) {
								case ']':
									c.readChar();
									v = new ArrayNode(position, params);
									break outer;
								case ',':
									if (first)
										throw new ExpressionParserException(c.getCountRead(), "Unexpected token: " + c.readChar());
									c.readChar();
									params.add(parseToken(c));
									break;
								case CharReader.EOF:
									c.peak();// throws EOF
								default:
									if (!first)
										throw new ExpressionParserException(c.getCountRead(), "Unexpected token: " + c.readChar());
									params.add(parseToken(c));
									first = false;
							}
						}
					} else {
						Node tok = parseToken(c);
						sws(c);
						v = new OperationNode(position, root, tok, OperationNode.OP_SBRACKET);
						c.expect(']');
					}
					break;
				case '{':
					v = parseBlock(c, false, false, true);
					break;
				case '"':
					if (c.peakSequence("\"\"\"")) {// """......""" syntax
						c.expect(operation);
						c.expectSequence("\"\"");
						if (c.readUntilSequence("\"\"\"", sb) == -1)
							throw c.newExpressionParserException("missing closing triple-quote (\"\"\")");
						c.expectSequence("\"\"\"");
						while (c.peakOrEof() == '"') {
							sb.append('"');
							c.expect('"');
						}
						v = new ConstNode(position, sb.toString());
					} else {
						c.expect('\"');
						v = parseString(c, sb, "\"", false);
						c.expect('\"');
					}
					break;
				case '\'':
					c.expect(operation);
					c.readUntil('\'', '\\', sb.append('\''));
					sb.append('\'');
					if (c.readCharOrEof() == CharReader.EOF)
						throw c.newExpressionParserException("missing closing quote (\')");
					Object cnst;
					try {
						cnst = SH.parseConstant(sb);
					} catch (Exception e) {
						throw new ExpressionParserException(position, e.getMessage(), e);
					}
					v = new ConstNode(position, cnst);
					break;
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					c.expect(operation);
					v = parseNumber(c, operation, sb);
					break;
				case '-':
					c.expect(operation);
					char c2 = c.peak();
					switch (c2) {
						case '.':
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							if (root != null && !rightmostIsOperation(root))
								op = OperationNode.OP_MINUS;
							else
								v = parseNumber(c, operation, sb);
							break;
						case '=':
							c.readChar();
							op = OperationNode.OP_MINUS_EQ;
							break;
						case '-':
							c.readChar();
							v = new OperationNode(position, null, null, OperationNode.OP_MINUS_MINUS);
							break;
						default:
							if (root == null)
								v = new OperationNode(position, null, null, OperationNode.OP_MINUS);
							else
								op = OperationNode.OP_MINUS;
					}
					break;
				case '+':
					c.expect(operation);
					c2 = c.peak();
					switch (c2) {
						case '.':
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							if (root != null && !rightmostIsOperation(root))
								op = OperationNode.OP_PLUS;
							else
								v = parseNumber(c, operation, sb);
							break;
						case '=':
							c.readChar();
							op = OperationNode.OP_PLUS_EQ;
							break;
						case '+':
							c.readChar();
							v = new OperationNode(position, null, null, OperationNode.OP_PLUS_PLUS);
							break;
						default:
							op = OperationNode.OP_PLUS;
					}
					break;
				case '?':
					c.readChar();
					op = OperationNode.OP_QMARK;
					break;
				case ':':
					c.readChar();
					op = OperationNode.OP_COLON;
					break;
				case '`':
					v = parseVariableNode(c, sb);
					break;
				case '*':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_STAR_EQ : OperationNode.OP_STAR;
					break;
				case '/':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_SLASH_EQ : OperationNode.OP_SLASH;
					break;
				case '^':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_HAT_EQ : OperationNode.OP_HAT;
					break;
				case '%':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_PERCENT_EQ : OperationNode.OP_PERCENT;
					break;
				case '~':
					c.readChar();
					op = c.expectNoThrow('~') ? OperationNode.OP_TILDE_TILDE : (c.expectNoThrow('=') ? OperationNode.OP_TILDE_EQ : OperationNode.OP_TILDE);
					break;
				case '=':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_EQ_EQ : (c.expectNoThrow('~') ? OperationNode.OP_EQ_TILDE : OperationNode.OP_EQ);
					break;
				case '!':
					c.readChar();
					if (c.expectNoThrow('='))
						op = OperationNode.OP_BANG_EQ;
					else if (c.expectNoThrow('~'))
						op = OperationNode.OP_BANG_TILDE;
					else
						v = new OperationNode(position, null, null, OperationNode.OP_BANG);
					break;
				case '&':
					c.readChar();
					op = c.expectNoThrow('&') ? OperationNode.OP_AMP_AMP : (c.expectNoThrow('=') ? OperationNode.OP_AMP_EQ : OperationNode.OP_AMP);
					break;
				case '|':
					c.readChar();
					op = c.expectNoThrow('|') ? OperationNode.OP_PIPE_PIPE : (c.expectNoThrow('=') ? OperationNode.OP_PIPE_EQ : OperationNode.OP_PIPE);
					break;
				case '<':
					c.readChar();
					op = c.expectNoThrow('=') ? OperationNode.OP_LT_EQ : (c.expectNoThrow('<') ? OperationNode.OP_LT_LT : OperationNode.OP_LT);
					break;
				case '>':
					c.readChar();
					if (c.expectNoThrow('=')) {
						op = OperationNode.OP_GT_EQ;
					} else if (c.expectNoThrow('>')) {
						if (c.expectNoThrow('>'))
							op = OperationNode.OP_GT_GT_GT;
						else
							op = OperationNode.OP_GT_GT;
					} else
						op = OperationNode.OP_GT;
					break;
				case '.':
					c.readChar();
					int operation2 = c.peakOrEof();
					switch (operation2) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							v = parseNumber(c, operation, sb);
							break;
						default:
							op = OperationNode.OP_PERIOD;
					}
					break;
				case 'f':
				case 'F':
					if (c.read(RH.FALSE, SPECIAL_CHARS))
						v = new ConstNode(position, Boolean.FALSE);
					break;
				case 'n':
				case 'N':
					if (c.read(RH.NEW, SPECIAL_CHARS))
						v = parseNew(c);
					else if (c.read(RH.NULL, SPECIAL_CHARS))
						v = new ConstNode(position, null);
					break;
				case 't':
				case 'T':
					if (c.read(RH.TRUE, SPECIAL_CHARS))
						v = new ConstNode(position, Boolean.TRUE);
					break;
			}
			if (v == null && op == -1) {
				c.readUntilAnySkipEscaped(allowEof ? SPECIAL_CHARS_AND_DOT : SPECIAL_CHARS_AND_DOT_NO_EOF, '\\', sb.append(c.expect(operation)));
				sws(c);
				byte t = getOperator(sb);
				if (t != -1 && t != OperationNode.OP_PERIOD && t != OperationNode.OP_NEW && t != OperationNode.THROW) {
					op = t;
					SH.clear(sb);
				} else {
					String s = t != -1 ? OperationNode.toString(t) : sb.toString();
					if (c.peakOrEof() == '(') {
						sb.setLength(0);
						if (toVarnameWithDots(root, sb)) {
							c.readChar();
							v = parseMethodDeclaration(SH.toStringAndClear(sb), s, c);
							return v;
						} else {
							c.readChar();
							v = parseMethod(position, s, c);
						}
						//					} else if (c.peakOrEof() == '[') {
						//						c.expect('[');
						//						Node param = parseToken(c);
						//						c.expect(']');
						//						VariableNode left = new VariableNode(position, s);
						//						v = new OperationNode(position, left, param, OperationNode.OP_SBRACKET);
					} else {
						v = new VariableNode(position, s);
					}
					sb.setLength(0);
				}
			}
			if (v != null) {
				if (root == null)
					root = v;
				else {
					Node r = processVar(position, v, root, c, sb);
					if (r instanceof CastNode)
						root = r;
					else if (r != null)
						return r;
				}
			} else if (op != -1) {
				if (root == null)
					return processEmptyOperation(position, op, c);
				else {
					Node n = root;
					if (n instanceof OperationNode) {
						OperationNode t = (OperationNode) n;
						for (;;) {
							if ((OH.eq(t.getOp(), op) || getOperatorPriority(t.getOp(), "left") >= getOperatorPriority(op, "right")) && (t.getOp() != OperationNode.OP_COLON)) {
								t.reset(position, new OperationNode(t.getPosition(), t.getLeft(), t.getRight(), t.getOp()), null, op);
								break;
							} else if (!(t.getRight() instanceof OperationNode)) {
								t.setRight(new OperationNode(position, t.getRight(), v, op));
								break;
							} else
								t = (OperationNode) t.getRight();
						}
					} else {
						OperationNode r = new OperationNode(position, root, null, op);
						root = r;
					}
				}
			} else
				throw new IllegalStateException();
		}

	}

	protected Node innerParseExpression(Node root, CharReader c) {
		return null;
	}

	protected void onExitingParenthesis() {
	}
	protected void onEnteringParenthesis() {
	}

	protected VariableNode parseVariableNode(CharReader c, StringBuilder sb) {
		return parseVariableNode(c, sb, true);
	}
	protected VariableNode parseVariableNode(CharReader c, StringBuilder sb, boolean required) {
		int pos = c.getCountRead();
		boolean backtick = c.peakOrEof() == '`';
		String parseVariable = parseVariable(c, sb);
		if (SH.isnt(parseVariable))
			if (required)
				throw new ExpressionParserException(c.getCountRead(), "Variable required");
			else
				return null;
		return new VariableNode(pos, parseVariable, backtick && !isValidVarName(parseVariable));
	}

	protected String parseVariable(CharReader c, StringBuilder sb) {
		SH.clear(sb);
		if (c.isEof())
			return null;
		if (c.peak() == '`') {
			c.expect('`');
			c.readUntilSkipEscaped('`', '\\', sb);
			c.expect('`');
		} else {
			//			c.readUntilAny(SPECIAL_CHARS_AND_DOT, true, '\\', sb);
			c.readUntilAnySkipEscaped(SPECIAL_CHARS_AND_DOT, '\\', sb);
			if (sb.length() > 0 && OH.isBetween(sb.charAt(0), '0', '9'))
				throw c.newExpressionParserException("Invalid name, can not start with digit: " + sb);
			if (sb.length() == 0)
				return null;
		}
		return SH.toStringAndClear(sb);
	}

	protected Node processVar(int position, Node v, final Node root, CharReader c, StringBuilder sb) {
		if (v instanceof OperationNode) {
			OperationNode on = (OperationNode) v;
			if (root instanceof OperationNode) {
				OperationNode t = (OperationNode) root;
				for (;;) {
					if (getOperatorPriority(t.getOp(), "left not found") >= getOperatorPriority(on.getOp(), "right not found")) {
						t.reset(t.getPosition(), new OperationNode(position, t.getLeft(), t.getRight(), t.getOp()), on.getRight(), on.getOp());
						return null;
					}
					if (!(t.getRight() instanceof OperationNode)) {
						on.setLeft(t.getRight());
						t.setRight(v);
						return null;
					}
					t = (OperationNode) t.getRight();
				}
			} else {
				on.setLeft(root);
				return parseExpression(v, c);
			}
		} else if (toVarnameWithDots(root, sb)) {
			Node val = parseExpression(v, c);
			String name;
			if (v instanceof NewNode)
				return v;
			if (val instanceof VariableNode)
				name = castNode(val, VariableNode.class).getVarname();
			else if (val instanceof OperationNode) {
				OperationNode op = (OperationNode) val;
				if (op.getOp() != OperationNode.OP_EQ && op.getOp() != OperationNode.OP_COLON)
					throw new ExpressionParserException(op.getPosition(), "Expecting assignment (=) not " + op.getOpString());
				name = op.getLeft().toString();
			} else if (val instanceof DeclarationNode)
				name = castNode(val, DeclarationNode.class).getVarname();
			else
				throw new ExpressionParserException(val.getPosition(),
						"Expecting Operation, Declaration or Variable, not " + SH.stripSuffix(OH.getSimpleClassName(val), "Node", false));
			return new DeclarationNode(root.getPosition(), SH.toStringAndClear(sb), name, val);
		} else if (root instanceof ExpressionNode) {
			ExpressionNode en = (ExpressionNode) root;
			return new CastNode(en.position, en.getValue().toString(), v);
		} else {
			OperationNode t = null;
			if (root instanceof ExpressionNode) {
				ExpressionNode en = (ExpressionNode) root;
				sb.setLength(0);
				if (toVarnameWithDots(en.getValue(), sb)) {
					CastNode r = new CastNode(en.position, SH.toStringAndClear(sb), v);
					return parseExpression(r, c);
				}
			}
			t = castNode(root, OperationNode.class);
			while (t.getRight() != null) {
				Node right = t.getRight();
				if (right instanceof ExpressionNode) {
					ExpressionNode en = (ExpressionNode) right;
					sb.setLength(0);
					if (toVarnameWithDots(en.getValue(), sb)) {
						t.setRight(new CastNode(en.position, SH.toStringAndClear(sb), v));
						return null;
					}
				}
				t = castNode(t.getRight(), OperationNode.class);
			}
			t.setRight(v);
			return null;
		}
	}

	protected boolean isEndOfExpression(char operation, CharReader c) {
		switch (operation) {
			case /* { */'}':
			case /* [ */']':
			case /* ( */')':
			case ';':
			case ',':
				return true;
			default:
				return false;
		}
	}

	final private Node parseFlowBlock(CharReader c) {
		Node r = parseStatement(c);
		if (r == null && getState() == STATE_STRING_TEMPLATE) {
			sws(c);
			if (c.expectNoThrow('}')) {
				StringBuilder buf = new StringBuilder();
				r = parseString(c, buf, "${}", false);
				if (!c.peakSequence("{else}"))
					c.expectSequence("${");
			}
		}
		return r;
	}

	final public Node parseTemplate(CharSequence template) {
		OH.assertEq(this.stateStackSize, 0);
		pushState(STATE_STRING_TEMPLATE);
		try {
			StringCharReader cr = new StringCharReader(template);
			cr.setCaseInsensitive(true);
			return parseString(cr, new StringBuilder(), null, true);
		} finally {
			popState(STATE_STRING_TEMPLATE);
		}
	}
	protected Node parseString(CharReader cr, StringBuilder sb, String termination, boolean allowEof) {
		boolean isNested = false;
		for (int i = 0; i < this.stateStackSize; i++)
			if (this.stateStack[i] == STATE_STRING_TEMPLATE) {
				isNested = true;
				break;
			}
		SH.clear(sb);
		int position = cr.getCountRead();
		StringTemplateNode r = null, tail = null;
		for (;;) {
			if ((termination != null && cr.peakSequence(termination)) || (cr.isEof() && (termination == null || allowEof))) {
				int pos = cr.getCountRead() - sb.length();
				if (r != null) {
					tail.setNext(new StringTemplateNode(pos, new AppendNode(pos, SH.toStringAndClear(sb), StringTemplateNode.NO_ESCAPE), isNested));
					tail = tail.getNext();
					return r;
				} else {
					return new ConstNode(pos, sb.toString());
				}
			}
			if (cr.isEof()) {
				throw new ExpressionParserException(position, "Missing closing " + toDescription(termination));
			}
			char c = cr.readChar();
			switch (c) {
				case '\\': {
					if (cr.isEof())
						throw new ExpressionParserException(position, "Missing closing " + toDescription(termination));
					c = cr.readChar();
					switch (c) {
						case 'u':
						case 'U':
							int start = sb.length();
							try {
								sb.append(cr.readChar());
								sb.append(cr.readChar());
								sb.append(cr.readChar());
								sb.append(cr.readChar());
								c = (char) SH.parseInt(sb, start, start + 4, 16);
							} catch (Exception e) {
								throw new ExpressionParserException(position + start, "Invalid Unicode, expecting 4 hexidecimal digits", e);
							}
							sb.setLength(start);
						case '$':
						case ';':
							break;
						case '\n':
						case '\r':
							sws(cr);
							continue;
						default:
							c = SH.toSpecial(c);
							if (c == SH.CHAR_NOT_SPECIAL)
								throw new ExpressionParserException(cr.getCountRead() - 1, "invalid escaped char");
					}
					break;
				}
				case '$':
					if (cr.peakOrEof() != '{')
						break;
					char lastChar = sb.length() == 0 ? StringTemplateNode.NO_ESCAPE : sb.charAt(sb.length() - 1);
					switch (lastChar) {
						case '\'':
						case '\"':
						case '`':
							sb.setLength(sb.length() - 1);
							break;
						default:
							lastChar = StringTemplateNode.NO_ESCAPE;
					}
					String text = SH.toStringAndClear(sb);
					int pos = cr.getCountRead() - text.length();
					StringTemplateNode t = new StringTemplateNode(pos, new AppendNode(pos, text, StringTemplateNode.NO_ESCAPE), isNested);
					if (r == null)
						r = tail = t;
					else {
						tail.setNext(t);
						tail = t;
					}
					sws(cr);
					if ("${}".equals(termination) && (cr.peakSequence("{else}"))) {
						return r;
					}
					pos = cr.getCountRead();
					Node node;
					try {
						pushState(STATE_STRING_TEMPLATE);
						node = parseBlock(cr, false, false, false);
					} finally {
						popState(STATE_STRING_TEMPLATE);
					}
					SH.clear(sb);
					if (lastChar != StringTemplateNode.NO_ESCAPE) {
						cr.expectNoThrow('\\');
						if (!cr.expectNoThrow(lastChar)) {
							tail.setNext(new StringTemplateNode(pos, new AppendNode(pos, SH.toString(lastChar), StringTemplateNode.NO_ESCAPE), isNested));
							tail = tail.getNext();
							tail.setNext(new StringTemplateNode(pos, node, StringTemplateNode.NO_ESCAPE, isNested));
							tail = tail.getNext();
						} else {
							tail.setNext(new StringTemplateNode(pos, node, lastChar, isNested));
							tail = tail.getNext();
						}
					} else {
						tail.setNext(new StringTemplateNode(pos, node, StringTemplateNode.NO_ESCAPE, isNested));
						tail = tail.getNext();
					}
					continue;
				case '\n':
					if (stateStack[0] != STATE_STRING_TEMPLATE) {
						if (OH.ne(termination, "\"")) {
							break;
						}
						if (allowEof && r == null) {
							return null;//TODO: I don't think this is right... We should return a StringTemplateNode
						}
					}
			}
			sb.append(c);
		}
	}

	public void setAllowImplicitBlock(boolean b) {
		this.allowImplicitBlock = b;
	}
	public boolean getAllowImplicitBlock() {
		return this.allowImplicitBlock;
	}
}
