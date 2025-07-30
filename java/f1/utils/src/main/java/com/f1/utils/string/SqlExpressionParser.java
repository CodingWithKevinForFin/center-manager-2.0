package com.f1.utils.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CharReader;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherSet;
import com.f1.utils.concurrent.LinkedHasherMap;
import com.f1.utils.impl.BasicCharMatcher;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.CharMatcher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.string.node.AppendNode;
import com.f1.utils.string.node.ArrayNode;
import com.f1.utils.string.node.BlockNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.DeclarationNode;
import com.f1.utils.string.node.MethodDeclarationNode;
import com.f1.utils.string.node.MethodNode;
import com.f1.utils.string.node.OperationNode;
import com.f1.utils.string.node.StringTemplateNode;
import com.f1.utils.string.node.VariableNode;
import com.f1.utils.string.sqlnode.AdminNode;
import com.f1.utils.string.sqlnode.AlterColumnNode;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.string.sqlnode.CreateTableNode;
import com.f1.utils.string.sqlnode.ExecuteNode;
import com.f1.utils.string.sqlnode.InsertNode;
import com.f1.utils.string.sqlnode.OnNode;
import com.f1.utils.string.sqlnode.SqlCallNode;
import com.f1.utils.string.sqlnode.SqlColumnDefNode;
import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlDeferredNode;
import com.f1.utils.string.sqlnode.SqlForNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.SqlOperationNode;
import com.f1.utils.string.sqlnode.SqlShowNode;
import com.f1.utils.string.sqlnode.SqlUnionNode;
import com.f1.utils.string.sqlnode.UseNode;
import com.f1.utils.string.sqlnode.ValuesNode;
import com.f1.utils.string.sqlnode.WhereNode;
import com.f1.utils.string.sqlnode.WildCharNode;
import com.f1.utils.structs.Tuple2;

public class SqlExpressionParser extends JavaExpressionParser {
	private static final BasicCharMatcher SEMI_COLON_MATCHER = new BasicCharMatcher(";", true);
	public static final byte STATE_SQL = 3;
	private static final char[] BY_CHARS = "BY".toCharArray();
	private static final char[] JOIN_CHARS = "JOIN".toCharArray();
	private static final char[] ONLY_CHARS = "ONLY".toCharArray();
	public static final int ID_INVALID = -1;
	public static final int ID_ADD = 34;
	public static final int ID_ALTER = 33;
	public static final int ID_ANALYZE = 37;
	public static final int ID_AND = 2;
	public static final int ID_AS = 1;
	public static final int ID_ASC = 26;
	public static final int ID_BEFORE = 68;
	public static final int ID_CALL = 60;
	public static final int ID_CONCURRENT = 65;
	public static final int ID_CREATE = 29;
	public static final int ID_DBO = 79;
	public static final int ID_DESC = 27;
	public static final int ID_DESCRIBE = 61;
	public static final int ID_DELETE = 3;
	public static final int ID_DISABLE = 63;
	public static final int ID_DROP = 30;
	public static final int ID_ENABLE = 64;
	public static final int ID_EXCEPT = 58;
	public static final int ID_EXECUTE = 47;
	public static final int ID_FOR = 45;
	public static final int ID_FROM = 4;
	public static final int ID_GROUPBY = 12;
	public static final int ID_HAVING = 13;
	public static final int ID_INDEX = 54;
	public static final int ID_INSERT = 5;
	public static final int ID_INTO = 6;
	public static final int ID_JOIN = 50;
	public static final int ID_LEFT_JOIN = 42;

	public static final int ID_LEFT_ONLY_JOIN = 52;
	public static final int ID_LIMIT = 28;
	public static final int ID_MODIFY = 32;
	public static final int ID_NEAREST = 57;
	public static final int ID_OR = 7;
	public static final int ID_ON = 40;
	public static final int ID_OUTER_JOIN = 49;
	public static final int ID_OUTER_ONLY_JOIN = 51;
	public static final int ID_ORDERBY = 21;
	public static final int ID_PARTITIONBY = 39;
	public static final int ID_PREPARE = 41;
	public static final int ID_PRIORITY = 59;
	public static final int ID_PROCEDURE = 70;
	public static final int ID_PUBLIC = 62;
	public static final int ID_RENAME = 35;
	public static final int ID_REPLICATION = 80;
	public static final int ID_RIGHT_JOIN = 48;
	public static final int ID_RIGHT_ONLY_JOIN = 53;
	public static final int ID_SELECT = 8;
	public static final int ID_SET = 9;
	public static final int ID_SHOW = 22;
	public static final int ID_STEP = 44;
	public static final int ID_SYNC = 67;
	public static final int ID_TABLE = 23;
	public static final int ID_TO = 36;
	public static final int ID_TIMER = 69;
	public static final int ID_TRIGGER = 55;
	public static final int ID_TRUNCATE = 66;
	public static final int ID_OFTYPE = 56;
	public static final int ID_UNION = 25;
	public static final int ID_UPDATE = 10;
	public static final int ID_UNPACK = 43;
	public static final int ID_USE = 46;
	public static final int ID_VALUES = 31;
	public static final int ID_WHERE = 11;
	public static final int ID_WINDOW = 38;
	public static final int ID_NOT = 71;
	public static final int ID_TEMPORARY = 72;
	public static final int ID_LOCAL = 73;
	public static final int ID_VARIABLE = 74;
	public static final int ID_DIAGNOSE = 75;
	public static final int ID_COLUMN = 76;
	public static final int ID_BYNAME = 77;
	public static final int ID_METHOD = 78;
	private boolean allowSqlInjection = true;
	private StringBuilder buf = new StringBuilder();
	private int parenthesisDepth = 0;
	long[] parenthesisStopWords = new long[10];
	private long stopKeywordIds;
	private boolean preocessingDeferredSql;

	public SqlExpressionParser() {
	}
	//	@Override
	//	protected int getOperatorPriorityNoThrow(CharSequence operator) {
	//		if (operator.length() == 2 && Character.toLowerCase(operator.charAt(0)) == 'i' && Character.toLowerCase(operator.charAt(1)) == 'n')// in
	//			return 22;//highest priority 
	//		if (operator.length() == 3 && Character.toLowerCase(operator.charAt(0)) == 'n' && Character.toLowerCase(operator.charAt(1)) == 'o'
	//				&& Character.toLowerCase(operator.charAt(2)) == 't')// not
	//			return 22;//highest priority 
	//		return super.getOperatorPriorityNoThrow(operator);
	//	}
	//
	//	@Override
	//	public String getOperator(CharSequence operator) {
	//		if (operator.length() == 2 && Character.toLowerCase(operator.charAt(0)) == 'i' && Character.toLowerCase(operator.charAt(1)) == 'n')// in
	//			return "in";
	//		if (operator.length() == 3 && Character.toLowerCase(operator.charAt(0)) == 'n' && Character.toLowerCase(operator.charAt(1)) == 'o'
	//				&& Character.toLowerCase(operator.charAt(2)) == 't')// not
	//			return "not";
	//		return super.getOperator(operator);
	//	}
	@Override
	protected String toStateString(byte type) {
		if (type == STATE_SQL)
			return "SQL";
		else
			return super.toStateString(type);
	}
	public static String toOperationString(int op) {
		switch (op) {
			case ID_JOIN:
				return "JOIN";
			case ID_LEFT_JOIN:
				return "LEFT JOIN";
			case ID_LEFT_ONLY_JOIN:
				return "LEFT ONLY JOIN";
			case ID_OUTER_ONLY_JOIN:
				return "OUTER ONLY JOIN";
			case ID_RIGHT_ONLY_JOIN:
				return "RIGHT ONLY JOIN";
			case ID_RIGHT_JOIN:
				return "RIGHT JOIN";
			case ID_OUTER_JOIN:
				return "OUTER JOIN";
			case ID_PREPARE:
				return "PREPARE";
			case ID_PUBLIC:
				return "PUBLIC";
			case ID_ANALYZE:
				return "ANALYZE";
			case ID_WINDOW:
				return "WINDOW";
			case ID_PARTITIONBY:
				return "PARTITION BY";//TODO: this should most likely be just "PARTITION"
			case ID_ON:
				return "ON";
			case ID_INVALID:
				return "<INVALID>";
			case ID_AS:
				return "AS";
			case ID_AND:
				return "AND";
			case ID_DELETE:
				return "DELETE";
			case ID_FROM:
				return "FROM";
			case ID_INSERT:
				return "INSERT";
			case ID_INTO:
				return "INTO";
			case ID_OR:
				return "OR";
			case ID_SELECT:
				return "SELECT";
			case ID_SET:
				return "SET";
			case ID_UPDATE:
				return "UPDATE";
			case ID_WHERE:
				return "WHERE";
			case ID_GROUPBY:
				return "GROUP BY";
			case ID_ORDERBY:
				return "ORDER BY";
			case ID_HAVING:
				return "HAVING";
			case ID_INDEX:
				return "INDEX";
			case ID_SHOW:
				return "SHOW";
			case ID_TABLE:
				return "TABLE";
			case ID_UNION:
				return "UNION";
			case ID_ASC:
				return "ASC";
			case ID_DBO:
				return "DBO";
			case ID_DESC:
				return "DESC";
			case ID_LIMIT:
				return "LIMIT";
			case ID_CREATE:
				return "CREATE";
			case ID_DROP:
				return "DROP";
			case ID_VALUES:
				return "VALUES";
			case ID_MODIFY:
				return "MODIFY";
			case ID_METHOD:
				return "METHOD";
			case ID_ALTER:
				return "ALTER";
			case ID_ADD:
				return "ADD";
			case ID_RENAME:
				return "RENAME";
			case ID_REPLICATION:
				return "REPLICATION";
			case ID_TO:
				return "TO";
			case ID_UNPACK:
				return "UNPACK";
			case ID_FOR:
				return "FOR";
			case ID_STEP:
				return "STEP";
			case ID_SYNC:
				return "SYNC";
			case ID_USE:
				return "USE";
			case ID_EXECUTE:
				return "EXECUTE";
			case ID_TRIGGER:
				return "TRIGGER";
			case ID_TRUNCATE:
				return "TRUNCATE";
			case ID_TIMER:
				return "TIMER";
			case ID_PRIORITY:
				return "PRIORITY";
			case ID_PROCEDURE:
				return "PROCEDURE";
			case ID_OFTYPE:
				return "OFTYPE";
			case ID_CALL:
				return "CALL";
			case ID_CONCURRENT:
				return "CONCURRENT";
			case ID_DESCRIBE:
				return "DESCRIBE";
			case ID_ENABLE:
				return "ENABLE";
			case ID_DISABLE:
				return "DISABLE";
			case ID_BEFORE:
				return "BEFORE";
			case ID_NEAREST:
				return "NEAREST";
			case ID_NOT:
				return "NOT";
			case ID_EXCEPT:
				return "EXCEPT";
			case ID_TEMPORARY:
				return "TEMPORARY";
			case ID_VARIABLE:
				return "VARIABLE";
			case ID_LOCAL:
				return "LOCAL";
			case ID_DIAGNOSE:
				return "DIAGNOSE";
			case ID_COLUMN:
				return "COLUMN";
			case ID_BYNAME:
				return "BYNAME";
			default:
				return "UNKNOWN: " + op;
		}
	}
	@Override
	protected void onParseComplete() {
		super.onParseComplete();
		clearStopKeywordIds();
	}
	private StringCharReader newStringCharReader(CharSequence text, int padding) {
		StringCharReader reader;
		if (padding == 0)
			reader = new StringCharReader(text);
		else {
			char[] inner = new char[padding + text.length()];
			for (int i = 0; i < padding; i++)
				inner[i] = ' ';
			for (int i = 0, len = text.length(); i < len; i++)
				inner[i + padding] = text.charAt(i);
			reader = new StringCharReader(inner);
		}
		reader.setCaseInsensitive(true);
		reader.setToStringIncludesLocation(true);
		return reader;
	}
	@Override
	public Node parseStatement(CharReader c) {
		sws(c);
		if (c.peakOrEof() == CharReader.EOF)
			return null;
		if (getState() != STATE_STRING_TEMPLATE) {
			int id = peekKeywordId(c);
			switch (id) {
				case ID_CONCURRENT: {
					Node r = parseConcurrentBlock(c);
					return r;
				}
				case ID_PREPARE:
				case ID_ANALYZE:
				case ID_ALTER:
				case ID_DELETE:
				case ID_CREATE:
				case ID_DROP:
				case ID_INSERT:
				case ID_SELECT:
				case ID_SYNC:
				case ID_USE:
				case ID_EXECUTE:
				case ID_TRUNCATE:
				case ID_RENAME:
				case ID_SHOW:
				case ID_CALL:
				case ID_DESCRIBE:
				case ID_ENABLE:
				case ID_DISABLE:
				case ID_DIAGNOSE:
				case ID_UPDATE: {
					return processSql(c, true);
				}
			}
		}
		return super.parseStatement(c);
	}
	private Node processSql(CharReader c, boolean consumeSemicolon) {
		try {
			pushState(STATE_SQL);
			if (!this.allowSqlInjection || preocessingDeferredSql) {
				Node r = parseToken(c);
				//				c.expectNoThrow(';');
				return r;
			}
			int pos = c.getCountRead();
			Node n = parseSqlString(c, buf);
			if (consumeSemicolon)
				c.expectNoThrow(';');//TODO: I believe this should really be an expect with throw
			if (n instanceof ConstNode) {
				ConstNode cn = (ConstNode) n;
				String value = (String) cn.getValue();
				StringCharReader cr = newStringCharReader(value, pos);
				Node r = parseToken(cr);
				if (cr.getAvailable() > 0)
					throw new ExpressionParserException(cr.getCountRead(), "Trailing text: " + cr.readChar());
				return r;
			} else
				return new SqlDeferredNode((StringTemplateNode) n);
		} finally {
			popState(STATE_SQL);
		}
	}

	private BlockNode parseConcurrentBlock(CharReader c) {
		int pos = c.getCountRead();
		expectKeywordId(c, ID_CONCURRENT);
		sws(c);
		return (BlockNode) parseBlock(c, true, true, false);
		//		c.expect('{');
		//		List<Node> nodes = new ArrayList<Node>();
		//		while (true) {
		//			sws(c);
		//			switch (c.peakOrEof()) {
		//				case CharReader.EOF:
		//					throw c.newExpressionParserException("Missing '}' at end of statement");
		//				case '}': {
		//					c.expect('}');
		//					return new SqlConcurrentBlockNode(pos, nodes);
		//				}
		//			}
		//			Node n = parseStatement(c);
		//			//			if (!c.expectNoThrow(';'))
		//			//				throw new ExpressionParserException(n == null ? c.getCountRead() : n.getPosition(), "Missing semicolon (;) after expression");
		//			//			if (n instanceof BlockNode && ((BlockNode) n).getNodesCount() == 0) {
		//			//				continue;
		//			//			} else if (n instanceof SqlDeferredNode || n instanceof SqlNode)
		//			nodes.add(n);
		//			//			else
		//			//				throw new ExpressionParserException(n == null ? c.getCountRead() : n.getPosition(), "Only SQL commands allowed inside CONCURRENT clause");
		//		}
	}
	@Override
	protected ExpressionParserException onUnexpectedToken(Node r, CharReader c, int ch) {
		if (r instanceof SqlColumnsNode) {
			return c.newExpressionParserException("Invalid use of reserved word");
		}
		return super.onUnexpectedToken(r, c, ch);
	}
	@Override
	public Node innerParseExpression(Node root, CharReader c) {
		c.mark();
		int position = c.getCountRead();
		if (getState() == STATE_SQL) {
			int keyWord = getKeywordId(c);
			if (keyWord != -1) {
				Node r = processKeyWord(position, keyWord, root, c);
				if (r != null) {
					return r;
				}
			}
		} else if (getState() == STATE_JAVA && root != null && root instanceof OperationNode) {
			OperationNode o = (OperationNode) root;
			if (o.getLeft() instanceof VariableNode && o.getRight() == null && o.getOp() != OperationNode.OP_PERIOD) {
				int keyWord = peekKeywordId(c);
				switch (keyWord) {
					case ID_PREPARE:
					case ID_ANALYZE:
					case ID_SELECT:
					case ID_USE:
					case ID_EXECUTE:
					case ID_SHOW:
					case ID_CALL:
					case ID_DIAGNOSE:
					case ID_DESCRIBE: {
						Node r = processSql(c, false);
						((OperationNode) root).setRight(r);
						return root;
					}
				}
			}
		}
		c.returnToMark();
		return null;
	}
	private int peekKeywordId(CharReader c) {
		c.mark();
		int keyWord = getKeywordId(c);
		c.returnToMark();
		return keyWord;
	}

	public static boolean isScopeKeyword(int keyWord) {
		switch (keyWord) {
			case ID_PUBLIC:
			case ID_TEMPORARY:
			case ID_VARIABLE:
				return true;
		}
		return false;
	}
	protected Node processKeyWord(int position, int keyWord, Node root, CharReader c) {
		if (root == null) {
			try {
				switch (keyWord) {
					case ID_PUBLIC:
					case ID_TEMPORARY:
					case ID_VARIABLE:
						return new SqlNode(position, parseToken(c), keyWord);
					case ID_AS:
						return (parseAs(position, c));
					case ID_USE:
						return (parseUse(position, c, true));
					case ID_EXECUTE:
						return (parseExecute(position, c));
					case ID_ENABLE:
					case ID_DISABLE:
						return parseEnable(position, keyWord, c);
					case ID_FROM:
						clearStopKeywordIds();
						addStopKeywordId(ID_JOIN);
						addStopKeywordId(ID_WHERE);
						addStopKeywordId(ID_LEFT_JOIN);
						addStopKeywordId(ID_RIGHT_JOIN);
						addStopKeywordId(ID_OUTER_JOIN);
						addStopKeywordId(ID_GROUPBY);
						addStopKeywordId(ID_ORDERBY);
						addStopKeywordId(ID_HAVING);
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_UNPACK);
						addStopKeywordId(ID_UNION);
						addStopKeywordId(ID_LIMIT);
						addStopKeywordId(ID_PARTITIONBY);
						addStopKeywordId(ID_WINDOW);
						addStopKeywordId(ID_PUBLIC);
						addStopKeywordId(ID_WINDOW);
						return (parseSql(position, keyWord, c));
					case ID_TRUNCATE:
						clearStopKeywordIds();
						return (parseTruncateTable(position, c));
					case ID_SELECT:
						clearStopKeywordIds();
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_FROM);
						addStopKeywordId(ID_UNION);
						addStopKeywordId(ID_EXCEPT);
						return (parseSql(position, keyWord, c));
					case ID_UPDATE:
						clearStopKeywordIds();
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_JOIN);
						addStopKeywordId(ID_LEFT_JOIN);
						addStopKeywordId(ID_RIGHT_JOIN);
						addStopKeywordId(ID_OUTER_JOIN);
						addStopKeywordId(ID_SET);
						return (parseSql(position, keyWord, c));
					case ID_SET:
						clearStopKeywordIds();
						addStopKeywordId(ID_WHERE);
						addStopKeywordId(ID_LIMIT);
						return (parseSql(position, keyWord, c));
					case ID_DELETE:
						return (parseDelete(position, c));
					case ID_WHERE:
						return (parseWhere(position, c));
					case ID_GROUPBY:
						return (parseGroupBy(position, c));
					case ID_ORDERBY:
						return (parseOrderBy(position, c));
					case ID_HAVING:
						return (parseSql(position, keyWord, c));
					case ID_TABLE:
						return null;
					case ID_UNION:
						return parseUnion(position, c);
					case ID_LIMIT:
						return (parseSql(position, keyWord, c));
					case ID_SHOW:
						clearStopKeywordIds();
						return parseShow(position, c);
					case ID_CREATE:
						clearStopKeywordIds();
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_USE);
						return parseCreate(position, c);
					case ID_RENAME:
						return (parseRename(position, c));
					case ID_DROP:
						return (parseDrop(position, c));
					case ID_INSERT:
					case ID_SYNC:
						clearStopKeywordIds();
						addStopKeywordId(ID_FOR);
						addStopKeywordId(ID_VALUES);
						return (parseInsertInto(position, c, keyWord));
					case ID_ALTER:
						clearStopKeywordIds();
						addStopKeywordId(ID_ADD);
						addStopKeywordId(ID_RENAME);
						addStopKeywordId(ID_MODIFY);
						addStopKeywordId(ID_DROP);
						return (parseAlter(position, c));
					case ID_CALL:
						return (parseCall(position, c));
					case ID_DESCRIBE:
						return (parseDescribe(position, c));
					case ID_DIAGNOSE:
						return (parseDiagnose(position, c));
					case ID_ADD:
						return (parseSql(position, keyWord, c));
					case ID_MODIFY:
						return (parseSql(position, keyWord, c));
					case ID_ANALYZE:
						clearStopKeywordIds();
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_FROM);
						return (parseSql(position, keyWord, c));
					case ID_PREPARE:
						clearStopKeywordIds();
						addStopKeywordId(ID_AS);
						addStopKeywordId(ID_FROM);
						return (parseSql(position, keyWord, c));
					case ID_WINDOW:
						clearStopKeywordIds();
						addStopKeywordId(ID_ON);
						addStopKeywordId(ID_PARTITIONBY);
						addStopKeywordId(ID_ORDERBY);
						addStopKeywordId(ID_WINDOW);
						addStopKeywordId(ID_UNION);
						addStopKeywordId(ID_LIMIT);
						return (parseSql(position, keyWord, c));
					case ID_PARTITIONBY:
						return (parsePartitionBy(position, c));
					case ID_LEFT_JOIN:
					case ID_JOIN:
					case ID_RIGHT_JOIN:
					case ID_OUTER_JOIN:
						addStopKeywordId(ID_NEAREST);
						addStopKeywordId(ID_ON);
						return (parseJoin(position, c, keyWord));
					case ID_NEAREST:
						return (parseSql(position, keyWord, c));
					case ID_ON:
						return (parseSql(position, keyWord, c));
					case ID_UNPACK:
						addStopKeywordId(ID_ON);
						return (parseUnpack(position, c));
					case ID_FOR:
						clearStopKeywordIds();
						addStopKeywordId(ID_TO);
						return (parseSqlFor(position, c));
					case ID_NOT:
						return new OperationNode(position, root, super.parseToken(c), OperationNode.OP_BANG);
				}
			} finally {
			}
		} else {
			switch (keyWord) {
				case ID_AND:
					return new OperationNode(position, root, super.parseToken(c), OperationNode.OP_AMP_AMP);
				case ID_OR:
					return new OperationNode(position, root, super.parseToken(c), OperationNode.OP_PIPE_PIPE);
			}
		}
		return null;
	}
	private Node parseUnion(int position, CharReader c) {
		sws(c);
		int command = getKeywordId(c);
		boolean byName;
		if (command == ID_BYNAME) {
			byName = true;
			sws(c);
			command = getKeywordId(c);
		} else
			byName = false;
		sws(c);
		Node n = processKeyWord(position, command, null, c);
		return new SqlUnionNode(position, n, byName);
	}
	private Node parseDiagnose(int position, CharReader c) {
		sws(c);
		List<Node> nodes = new ArrayList<Node>();
		int id = getKeywordId(c);
		SqlNode options;
		if (isScopeKeyword(id)) {
			options = new SqlNode(position, null, id);
			sws(c);
			c.mark();
			id = getKeywordId(c);
		} else
			options = null;
		if (id != ID_TABLE && id != ID_COLUMN && id != ID_INDEX)
			throw new ExpressionParserException(c.getCountRead(), "DIAGNOSE Expecting: TABLE, COLUMN or INDEX");
		for (;;) {
			sws(c);
			int namePos = c.getCountRead();
			VariableNode name = parseVariableNode(c, buf);
			sws(c);
			SqlOperationNode onNode = parseSqlOperationNode(c, ID_ON, false);
			if (onNode == null && (id == ID_COLUMN || id == ID_INDEX))
				throw new ExpressionParserException(c.getCountRead(), "Expecting ON (followed by table_name)");
			nodes.add(new SqlOperationNode(namePos, name, onNode, id));
			if (!c.expectNoThrow(','))
				break;
		}
		ArrayNode names = new ArrayNode(position, nodes);
		return new AdminNode(position, ID_DIAGNOSE, ID_TABLE, names, null, options);
	}
	private Node parseEnable(int position, int keyWord, CharReader c) {
		sws(c);
		int id = getKeywordId(c);
		sws(c);
		switch (id) {
			case ID_TRIGGER:
			case ID_DBO:
			case ID_TIMER: {
				List<Node> params = new ArrayList<Node>();
				ArrayNode names = new ArrayNode(position, params);
				for (;;) {
					sws(c);
					VariableNode val = parseVariableNode(c, this.buf);
					params.add(val);
					sws(c);
					if (!c.expectNoThrow(','))
						break;
				}
				return new AdminNode(position, keyWord, id, names, null);
			}
			default:
				throw c.newExpressionParserException("Expecting DBO, TRIGGER or TIMER");
		}
	}
	@Override
	protected Node processEmptyOperation(int position, byte op, CharReader c) {
		if (op == OperationNode.OP_STAR)
			return new ConstNode(position, "*");
		else
			return super.processEmptyOperation(position, op, c);
	}
	@Override
	protected boolean isEndOfExpression(char operation, CharReader c) {
		if (getState() == STATE_SQL) {
			int keywordId = peekKeywordId(c);
			if (isStopKeywordId(keywordId))
				return true;
		}
		return super.isEndOfExpression(operation, c);
	}
	private boolean isStopKeywordId(int keywordId) {
		return keywordId != ID_INVALID && ((1L << keywordId) & stopKeywordIds) != 0L;
	}
	private void clearStopKeywordIds() {
		this.stopKeywordIds = 0;
	}
	private void addStopKeywordId(int id) {
		if (id > 62)
			throw new IllegalArgumentException();
		this.stopKeywordIds |= (1L << id);
	}
	@Override
	protected Node processVar(int position, Node v, Node root, CharReader c, StringBuilder sb) {
		if (v instanceof VariableNode) {
			if (root != null && root instanceof SqlColumnsNode)
				return new AsNode(position, root, v, false);
			VariableNode v2 = (VariableNode) v;
			if (!v2.hasBacktick()) {
				String varname = v2.getVarname();
				if ("DESC".equalsIgnoreCase(varname))
					return new SqlColumnsNode(position, null, root, ID_DESC);
				if ("ASC".equalsIgnoreCase(varname))
					return new SqlColumnsNode(position, null, root, ID_ASC);
			}
		}
		return super.processVar(position, v, root, c, sb);
	}
	protected void expectKeywordId(CharReader c, int expected) {
		int position = c.getCountRead();
		int actual = getKeywordId(c);
		if (actual != expected) {
			if (actual != ID_INVALID)
				throw new ExpressionParserException(position, "Expecting keyword: " + toOperationString(expected) + "  (not " + toOperationString(actual) + ")");
			else
				throw new ExpressionParserException(position, "Expecting keyword: " + toOperationString(expected));
		}
	}
	protected boolean expectKeywordIdNoThrow(CharReader c, int expected) {
		c.mark();
		if (getKeywordId(c) == expected)
			return true;
		c.returnToMark();
		return false;
	}
	protected int getKeywordId(CharReader c) {
		c.readUntilAny(SPECIAL_CHARS_AND_DOT, true, SH.clear(buf));
		return getKeywordId(buf);
	}
	public static int getKeywordId(CharSequence buf) {
		if (buf.length() < 2)
			return ID_INVALID;
		char operation = buf.charAt(0);
		switch (operation | buf.length() << 8) {
			case 'A' | (2 << 8):
			case 'a' | (2 << 8):
				if (SH.equalsIgnoreCase("AS", buf))
					return ID_AS;
				break;
			case 'A' | (3 << 8):
			case 'a' | (3 << 8):
				if (SH.equalsIgnoreCase("ASC", buf))
					return ID_ASC;
				if (SH.equalsIgnoreCase("AND", buf))
					return ID_AND;
				if (SH.equalsIgnoreCase("ADD", buf))
					return ID_ADD;
				break;
			case 'A' | (5 << 8):
			case 'a' | (5 << 8):
				if (SH.equalsIgnoreCase("ALTER", buf))
					return ID_ALTER;
				break;
			case 'A' | (7 << 8):
			case 'a' | (7 << 8):
				if (SH.equalsIgnoreCase("ANALYZE", buf))
					return ID_ANALYZE;
				break;
			case 'B' | 6 << 8:
			case 'b' | 6 << 8:
				if (SH.equalsIgnoreCase("BEFORE", buf))
					return ID_BEFORE;
				if (SH.equalsIgnoreCase("BYNAME", buf))
					return ID_BYNAME;
				break;
			case 'C' | 4 << 8:
			case 'c' | 4 << 8:
				if (SH.equalsIgnoreCase("CALL", buf))
					return ID_CALL;
				break;
			case 'C' | 6 << 8:
			case 'c' | 6 << 8:
				if (SH.equalsIgnoreCase("CREATE", buf))
					return ID_CREATE;
				if (SH.equalsIgnoreCase("COLUMN", buf))
					return ID_COLUMN;
				break;
			case 'C' | 10 << 8:
			case 'c' | 10 << 8:
				if (SH.equalsIgnoreCase("CONCURRENT", buf))
					return ID_CONCURRENT;
				break;
			case 'D' | 3 << 8:
			case 'd' | 3 << 8:
				if (SH.equalsIgnoreCase("DBO", buf))
					return ID_DBO;
			case 'D' | 4 << 8:
			case 'd' | 4 << 8:
				if (SH.equalsIgnoreCase("DESC", buf))
					return ID_DESC;
				if (SH.equalsIgnoreCase("DROP", buf))
					return ID_DROP;
				break;
			case 'D' | 6 << 8:
			case 'd' | 6 << 8:
				if (SH.equalsIgnoreCase("DELETE", buf))
					return ID_DELETE;
				break;
			case 'D' | 7 << 8:
			case 'd' | 7 << 8:
				if (SH.equalsIgnoreCase("DISABLE", buf))
					return ID_DISABLE;
				break;
			case 'D' | 8 << 8:
			case 'd' | 8 << 8:
				if (SH.equalsIgnoreCase("DESCRIBE", buf))
					return ID_DESCRIBE;
				if (SH.equalsIgnoreCase("DIAGNOSE", buf))
					return ID_DIAGNOSE;
				break;
			case 'E' | 6 << 8:
			case 'e' | 6 << 8:
				if (SH.equalsIgnoreCase("EXCEPT", buf))
					return ID_EXCEPT;
				if (SH.equalsIgnoreCase("ENABLE", buf))
					return ID_ENABLE;
				break;
			case 'E' | 7 << 8:
			case 'e' | 7 << 8:
				if (SH.equalsIgnoreCase("EXECUTE", buf))
					return ID_EXECUTE;
				break;
			case 'F' | 3 << 8:
			case 'f' | 3 << 8:
				if (SH.equalsIgnoreCase("FOR", buf))
					return ID_FOR;
				break;
			case 'F' | 4 << 8:
			case 'f' | 4 << 8:
				if (SH.equalsIgnoreCase("FROM", buf))
					return ID_FROM;
				break;
			case 'G' | 5 << 8:
			case 'g' | 5 << 8:
				if (SH.equalsIgnoreCase("GROUP", buf))
					return ID_GROUPBY;
				break;
			case 'H' | 6 << 8:
			case 'h' | 6 << 8:
				if (SH.equalsIgnoreCase("HAVING", buf))
					return ID_HAVING;
				break;
			case 'I' | 4 << 8:
			case 'i' | 4 << 8:
				if (SH.equalsIgnoreCase("INTO", buf))
					return ID_INTO;
				break;
			case 'I' | 5 << 8:
			case 'i' | 5 << 8:
				if (SH.equalsIgnoreCase("INDEX", buf))
					return ID_INDEX;
				break;
			case 'I' | 6 << 8:
			case 'i' | 6 << 8:
				if (SH.equalsIgnoreCase("INSERT", buf))
					return ID_INSERT;
				break;
			case 'J' | 4 << 8:
			case 'j' | 4 << 8:
				if (SH.equalsIgnoreCase("JOIN", buf))
					return ID_JOIN;
				break;
			case 'L' | 4 << 8:
			case 'l' | 4 << 8:
				if (SH.equalsIgnoreCase("LEFT", buf))
					return ID_LEFT_JOIN;
			case 'L' | 5 << 8:
			case 'l' | 5 << 8:
				if (SH.equalsIgnoreCase("LIMIT", buf))
					return ID_LIMIT;
				if (SH.equalsIgnoreCase("LOCAL", buf))
					return ID_LOCAL;
				break;
			case 'M' | 6 << 8:
			case 'm' | 6 << 8:
				if (SH.equalsIgnoreCase("MODIFY", buf))
					return ID_MODIFY;
				if (SH.equalsIgnoreCase("METHOD", buf))
					return ID_METHOD;
				break;
			case 'N' | 3 << 8:
			case 'n' | 3 << 8:
				if (SH.equalsIgnoreCase("NOT", buf))
					return ID_NOT;
				break;
			case 'N' | 7 << 8:
			case 'n' | 7 << 8:
				if (SH.equalsIgnoreCase("NEAREST", buf))
					return ID_NEAREST;
				break;
			case 'O' | 2 << 8:
			case 'o' | 2 << 8:
				if (SH.equalsIgnoreCase("OR", buf))
					return ID_OR;
				if (SH.equalsIgnoreCase("ON", buf))
					return ID_ON;
				break;
			case 'O' | 5 << 8:
			case 'o' | 5 << 8:
				if (SH.equalsIgnoreCase("ORDER", buf))
					return ID_ORDERBY;
				if (SH.equalsIgnoreCase("OUTER", buf))
					return ID_OUTER_JOIN;
				break;
			case 'O' | 6 << 8:
			case 'o' | 6 << 8:
				if (SH.equalsIgnoreCase("OFTYPE", buf))
					return ID_OFTYPE;
				break;
			case 'P' | 6 << 8:
			case 'p' | 6 << 8:
				if (SH.equalsIgnoreCase("PUBLIC", buf))
					return ID_PUBLIC;
				break;
			case 'P' | 7 << 8:
			case 'p' | 7 << 8:
				if (SH.equalsIgnoreCase("PREPARE", buf))
					return ID_PREPARE;
				break;
			case 'P' | 8 << 8:
			case 'p' | 8 << 8:
				if (SH.equalsIgnoreCase("PRIORITY", buf))
					return ID_PRIORITY;
				break;
			case 'P' | 9 << 8:
			case 'p' | 9 << 8:
				if (SH.equalsIgnoreCase("PARTITION", buf))
					return ID_PARTITIONBY;
				if (SH.equalsIgnoreCase("PROCEDURE", buf))
					return ID_PROCEDURE;
				break;
			case 'R' | 5 << 8:
			case 'r' | 5 << 8:
				if (SH.equalsIgnoreCase("RIGHT", buf))
					return ID_RIGHT_JOIN;
				break;
			case 'R' | 6 << 8:
			case 'r' | 6 << 8:
				if (SH.equalsIgnoreCase("RENAME", buf))
					return ID_RENAME;
				break;
			case 'R' | 11 << 8:
			case 'r' | 11 << 8:
				if (SH.equalsIgnoreCase("REPLICATION", buf))
					return ID_REPLICATION;
				break;
			case 'S' | 3 << 8:
			case 's' | 3 << 8:
				if (SH.equalsIgnoreCase("SET", buf))
					return ID_SET;
				break;
			case 'S' | 4 << 8:
			case 's' | 4 << 8:
				if (SH.equalsIgnoreCase("SHOW", buf))
					return ID_SHOW;
				if (SH.equalsIgnoreCase("STEP", buf))
					return ID_STEP;
				if (SH.equalsIgnoreCase("SYNC", buf))
					return ID_SYNC;
				break;
			case 'S' | 6 << 8:
			case 's' | 6 << 8:
				if (SH.equalsIgnoreCase("SELECT", buf))
					return ID_SELECT;
				break;
			case 'T' | 2 << 8:
			case 't' | 2 << 8:
				if (SH.equalsIgnoreCase("TO", buf))
					return ID_TO;
				break;
			case 'T' | 5 << 8:
			case 't' | 5 << 8:
				if (SH.equalsIgnoreCase("TABLE", buf))
					return ID_TABLE;
				if (SH.equalsIgnoreCase("TIMER", buf))
					return ID_TIMER;
				break;
			case 'T' | 7 << 8:
			case 't' | 7 << 8:
				if (SH.equalsIgnoreCase("TRIGGER", buf))
					return ID_TRIGGER;
				break;
			case 'T' | 8 << 8:
			case 't' | 8 << 8:
				if (SH.equalsIgnoreCase("TRUNCATE", buf))
					return ID_TRUNCATE;
				break;
			case 'T' | 9 << 8:
			case 't' | 9 << 8:
				if (SH.equalsIgnoreCase("TEMPORARY", buf))
					return ID_TEMPORARY;
				break;
			case 'U' | 3 << 8:
			case 'u' | 3 << 8:
				if (SH.equalsIgnoreCase("USE", buf))
					return ID_USE;
				break;
			case 'U' | 5 << 8:
			case 'u' | 5 << 8:
				if (SH.equalsIgnoreCase("UNION", buf))
					return ID_UNION;
				break;
			case 'U' | 6 << 8:
			case 'u' | 6 << 8:
				if (SH.equalsIgnoreCase("UPDATE", buf))
					return ID_UPDATE;
				if (SH.equalsIgnoreCase("UNPACK", buf))
					return ID_UNPACK;
				break;
			case 'V' | 6 << 8:
			case 'v' | 6 << 8:
				if (SH.equalsIgnoreCase("VALUES", buf))
					return ID_VALUES;
			case 'V' | 8 << 8:
			case 'v' | 8 << 8:
				if (SH.equalsIgnoreCase("VARIABLE", buf))
					return ID_VARIABLE;
				break;
			case 'W' | 5 << 8:
			case 'w' | 5 << 8:
				if (SH.equalsIgnoreCase("WHERE", buf))
					return ID_WHERE;
				break;
			case 'W' | 6 << 8:
			case 'w' | 6 << 8:
				if (SH.equalsIgnoreCase("WINDOW", buf))
					return ID_WINDOW;
				break;
		}
		return ID_INVALID;
	}
	private AsNode parseAs(int position, CharReader c) {
		sws(c);
		return new AsNode(position, null, parseVariableNode(c, buf), true);
	}
	private WildCharNode parseWildChar(int position, VariableNode tableName, CharReader c) {
		List<VariableNode> exceptList = new ArrayList<VariableNode>();
		sws(c);
		c.mark();
		if (getKeywordId(c) != ID_EXCEPT) {
			c.returnToMark();
			return new WildCharNode(position, tableName, Collections.EMPTY_LIST);
		}
		sws(c);
		c.expect('(');
		outer: for (;;) {
			sws(c);
			exceptList.add(parseVariableNode(c, SH.clear(buf), true));
			sws(c);
			if (c.expectAny(new int[] { ',', ')' }) == ')')
				break;
		}
		return new WildCharNode(position, tableName, exceptList);
	}

	public static final CharMatcher WS_CLOSE = new BasicCharMatcher("\r\n\t), ", false);
	public static final char USE_ESPRESSION_OPEN = '(';

	private UseNode parseUse(int position, CharReader c, boolean readNext) {
		Map<CharSequence, Node> node = null;
		outer: for (;;) {
			sws(c);
			c.mark();
			c.readUntilAny(SPECIAL_CHARS, true, SH.clear(buf));
			if (buf.length() == 0)
				break;
			int code = getKeywordId(buf);
			switch (code) {
				case ID_INVALID:
				case ID_LIMIT:
				case ID_ON:
				case ID_PARTITIONBY:
				case ID_WHERE:
					break;
				default:
					c.returnToMark();
					break outer;
			}
			sws(c);
			String key = SH.toStringAndClear(buf);
			if (c.expectNoThrow('=')) {
				if (node != null && node.containsKey(key))
					throw new ExpressionParserException(position, "duplicate USE option: '" + key + "'");
				sws(c);
				int pos = c.getCountRead();
				// IF isStr ex PersitEngine="FAST" -> ConstNode
				// Else if isExpression ex _name=alex -> alex is expected to be a variable -> parse expression
				// Supported _notVal={ px*size } or something like this.
				boolean isStr = parseString(c, buf);

				int firstChar = c.peakOrEof();
				boolean isOpen = USE_ESPRESSION_OPEN == firstChar;
				Node value;

				if (isStr) {
					value = new ConstNode(pos, (SH.toStringAndClear(buf)));
				} else {
					if (!isOpen) {
						c.readUntilAny(WS_CLOSE, false, buf);
						value = parseExpression(null, newStringCharReader(SH.toStringAndClear(buf), pos));
					} else {
						value = parseExpressionParen(c);
					}
				}

				if (node == null)
					node = new LinkedHasherMap<CharSequence, Node>(CaseInsensitiveHasher.INSTANCE);
				node.put(key, value);
			} else {
				int pos = c.getCountRead();
				if (node == null)
					node = new LinkedHasherMap<CharSequence, Node>(CaseInsensitiveHasher.INSTANCE);
				node.put(key, new ConstNode(pos, "true"));//Should be const node
			}
		}
		Node next = readNext ? parseToken(c) : null;
		if (node == null)
			node = Collections.EMPTY_MAP;
		return new UseNode(position, (Map) node, next);
	}
	private ExecuteNode parseExecute(int pos, CharReader c) {
		sws(c);
		SH.clear(buf);
		//if this was runtime sql, always go to the end; otherwise just go to the semi-colon
		if (allowSqlInjection) {
			char last = 0;
			while (!c.isEof()) {
				char c2 = c.readChar();
				if (last == '\\' && (c2 == ';' || c2 == '$'))
					buf.setLength(buf.length() - 1);
				if (c2 == ';' && c.isEof())
					break;
				buf.append(c2);
				last = c2;
			}
		} else {
			char last = 0;
			while (!c.isEof()) {
				char c2 = c.readChar();
				if (last == '\\' && (c2 == ';'))
					buf.setLength(buf.length() - 1);
				else if (c2 == ';') {
					c.pushBack(';');
					break;
				}
				buf.append(c2);
				last = c2;

			}
		}
		return new ExecuteNode(pos, SH.toStringAndClear(buf));
	}

	private SqlColumnsNode parseGroupBy(int position, CharReader c) {
		sws(c);
		c.expectSequence(BY_CHARS);
		SqlColumnsNode r = parseSql(position, ID_GROUPBY, c);
		for (int i = 0; i < r.getColumnsCount(); i++) {
			Node col = r.getColumnAt(i);
			if (col == null)
				throw new ExpressionParserException(position, "Missing GROUP BY Clause");
		}
		return r;
	}

	private static final char[] DESC_CHARS = "DESC".toCharArray();
	private static final char[] ASC_CHARS = "ASC".toCharArray();

	private SqlColumnsNode parseOrderBy(int position, CharReader c) {
		sws(c);
		c.expectSequence(BY_CHARS);
		SqlColumnsNode r = parseSql(position, ID_ORDERBY, c);
		sws(c);
		if (c.peakSequence(DESC_CHARS)) {
			c.expectSequence(DESC_CHARS);
			r.setColumnAt(r.getColumnsCount() - 1, new SqlColumnsNode(c.getCountRead(), null, r.getColumnAt(r.getColumnsCount() - 1), ID_DESC));
		} else if (c.peakSequence(ASC_CHARS)) {
			r.setColumnAt(r.getColumnsCount() - 1, new SqlColumnsNode(c.getCountRead(), null, r.getColumnAt(r.getColumnsCount() - 1), ID_ASC));
			c.expectSequence(ASC_CHARS);
		}
		for (int i = 0; i < r.getColumnsCount(); i++) {
			Node col = r.getColumnAt(i);
			if (col instanceof SqlColumnsNode && (((SqlColumnsNode) col).getOperation() == ID_ASC || ((SqlColumnsNode) col).getOperation() == ID_DESC))
				continue;
			else if (col == null)
				throw new ExpressionParserException(position, "Missing ORDER BY Clause");
			r.setColumnAt(i, new SqlColumnsNode(c.getCountRead(), null, col, ID_ASC));
		}
		if (r.getNext() == null) {
			Node next = parseToken(c);
			r.setNext(next);
		}
		return r;
	}
	private SqlColumnsNode parsePartitionBy(int position, CharReader c) {
		sws(c);
		c.expectSequence(BY_CHARS);
		SqlColumnsNode r = parseSql(position, ID_PARTITIONBY, c);
		return r;
	}
	private SqlColumnsNode parseJoin(int position, CharReader c, int type) {
		sws(c);
		if (type != ID_JOIN) {
			if (c.peakSequence(ONLY_CHARS)) {
				c.expectSequence(ONLY_CHARS);
				switch (type) {
					case ID_LEFT_JOIN:
						type = ID_LEFT_ONLY_JOIN;
						break;
					case ID_OUTER_JOIN:
						type = ID_OUTER_ONLY_JOIN;
						break;
					case ID_RIGHT_JOIN:
						type = ID_RIGHT_ONLY_JOIN;
						break;
				}
			}
			sws(c);
			c.expectSequence(JOIN_CHARS);
		}
		Node table = parseToken(c);
		sws(c);
		if (expectKeywordIdNoThrow(c, ID_AS)) {
			Node as = parseToken(c);
			sws(c);
			table = new AsNode(c.getCountRead(), table, as, true);
		}
		expectKeywordId(c, ID_ON);
		sws(c);
		Node on = parseExpression(null, c);
		Node r = parseExpression(null, c);
		return new SqlColumnsNode(position, new Node[] { table }, new SqlColumnsNode(position, new Node[] { on }, r, ID_ON), type);
	}
	//useful for external callers, don't use internally
	public SqlColumnsNode parseSqlColumnsNdoe(int command, String text) {
		if (SH.isnt(text))
			return new SqlColumnsNode(0, new Node[0], null, command);
		StringCharReader c = newStringCharReader(text, 0);
		pushState(STATE_SQL);
		try {
			return parseSql(0, command, c);
		} finally {
			popState(STATE_SQL);
		}
	}
	protected SqlColumnsNode parseSql(int position, int command, CharReader c) {
		final List<Node> parts = new ArrayList<Node>();//TODO don't create array every time
		Node next;
		while (true) {
			sws(c);
			Node current;
			if (c.peakOrEof() == '{') {
				try {
					pushState(STATE_JAVA);
					current = parseStatement(c);
				} finally {
					popState(STATE_JAVA);
				}
			} else if (c.expectNoThrow('*')) {
				current = parseWildChar(position, null, c);
			} else {
				current = parseToken(c);
				if (current instanceof OperationNode) {
					OperationNode on = (OperationNode) current;
					if (on.getRight() == null && on.getOp() == OperationNode.OP_STAR) {
						if (on.getLeft() instanceof OperationNode) {
							OperationNode on2 = (OperationNode) on.getLeft();
							if (on2.getRight() == null && on2.getOp() == OperationNode.OP_PERIOD) {
								current = parseWildChar(position, (VariableNode) on2.getLeft(), c);
							}
						}
					}
				}
			}

			if (c.peakOrEof() == ',') {
				parts.add(current);
				c.expect(',');
				continue;
			}
			next = parseToken(c);
			if (next instanceof AsNode) {
				((AsNode) next).setValue(current);
				parts.add(next);
				sws(c);
				if (c.peakOrEof() == ',') {
					c.expect(',');
				} else {
					next = parseToken(c);
					break;
				}
			} else {
				parts.add(current);
				break;
			}
		}
		return new SqlColumnsNode(position, parts.toArray(new Node[parts.size()]), next, command);
	}
	private SqlColumnsNode parseCommaList(int position, int command, CharReader c) {
		final List<Node> parts = new ArrayList<Node>();//TODO don't create array every time
		Node next;
		while (true) {
			sws(c);
			Node current;
			if (c.peakOrEof() == '{') {
				try {
					pushState(STATE_JAVA);
					current = parseStatement(c);
				} finally {
					popState(STATE_JAVA);
				}
			} else
				current = parseToken(c);
			if (c.peakOrEof() == ',') {
				parts.add(current);
				c.expect(',');
				continue;
			}
			parts.add(current);
			break;
		}
		return new SqlColumnsNode(position, parts.toArray(new Node[parts.size()]), null, command);
	}
	private SqlForNode parseSqlFor(int position, CharReader c) {
		clearStopKeywordIds();
		addStopKeywordId(ID_TO);
		addStopKeywordId(ID_STEP);
		addStopKeywordId(ID_VALUES);
		addStopKeywordId(ID_FOR);
		sws(c);
		VariableNode varname = parseVariableNode(c, buf);
		sws(c);
		c.expect('=');
		sws(c);
		Node start = parseToken(c);
		expectKeywordId(c, ID_TO);
		sws(c);
		Node end = parseToken(c);
		sws(c);
		c.mark();
		Node step = null;
		if (getKeywordId(c) == ID_STEP) {
			sws(c);
			step = parseToken(c);
			sws(c);
		} else
			c.returnToMark();
		Node next = parseToken(c);
		return new SqlForNode(position, varname, start, end, step, next);
	}
	private SqlColumnsNode parseDelete(int position, CharReader c) {
		sws(c);
		expectKeywordId(c, ID_FROM);
		addStopKeywordId(ID_JOIN);
		addStopKeywordId(ID_WHERE);
		addStopKeywordId(ID_LEFT_JOIN);
		addStopKeywordId(ID_RIGHT_JOIN);
		addStopKeywordId(ID_OUTER_JOIN);
		addStopKeywordId(ID_ORDERBY);
		addStopKeywordId(ID_AS);
		addStopKeywordId(ID_LIMIT);
		addStopKeywordId(ID_PUBLIC);
		SqlColumnsNode r = parseSql(position, ID_DELETE, c);
		return r;
	}
	private WhereNode parseWhere(int position, CharReader c) {
		Node expression = parseToken(c);
		Node next = parseToken(c);
		return new WhereNode(position, expression, next);
	}
	private SqlShowNode parseShow(int position, CharReader c) {
		sws(c);
		boolean isFull = c.expectSequenceNoThrow("FULL");
		if (isFull)
			sws(c);
		c.mark();
		int scope = getKeywordId(c);
		if (isScopeKeyword(scope)) {
			sws(c);
		} else {
			scope = ID_INVALID;
			c.returnToMark();
		}
		String target = parseVariable(c, buf);
		Node name = null;
		VariableNode from = null;
		sws(c);
		c.mark();
		int keyWord = getKeywordId(c);
		if (keyWord == ID_FROM) {
			sws(c);
			from = parseVariableNode(c, buf, false);
			keyWord = peekKeywordId(c);
		} else
			c.returnToMark();
		Node next2;
		switch (keyWord) {
			case ID_WHERE:
			case ID_ORDERBY:
			case ID_UNION:
			case ID_LIMIT:
				name = null;
				break;
			default:
				position = c.getCountRead();//update position of the charreader
				name = parseVariableNode(c, buf, false);
				sws(c);
				if ("method".equalsIgnoreCase(target)) {
					String methodName = ((VariableNode) (name)).getVarname();
					c.expect('(');
					name = parseMethod(position, methodName, c);//name instanceof MethodNode
				}
				c.mark();
				keyWord = getKeywordId(c);
				if (keyWord == ID_FROM) {
					sws(c);
					from = parseVariableNode(c, buf, false);
				} else
					c.returnToMark();
		}
		addStopKeywordId(ID_WHERE);
		addStopKeywordId(ID_ORDERBY);
		addStopKeywordId(ID_UNION);
		addStopKeywordId(ID_LIMIT);
		next2 = parseToken(c);
		if (!"method".equalsIgnoreCase(target))
			return new SqlShowNode(position, target, scope, isFull, (VariableNode) name, from, next2);
		else
			return new SqlShowNode(position, target, scope, isFull, (MethodNode) name, from, next2);

	}
	private SqlNode parseCreate(int position, CharReader c) {
		sws(c);
		position = c.getCountRead();
		int type = getKeywordId(c);
		sws(c);
		SqlNode options;
		if (type == ID_VARIABLE) {
			throw new ExpressionParserException(position, "Create variable tables using the syntax: Table tableName = [sql statement]");
		} else if (isScopeKeyword(type)) {
			options = new SqlNode(position, null, type);
			position = c.getCountRead();
			type = getKeywordId(c);
			sws(c);
		} else
			options = null;
		boolean ifNotExists;
		if (c.expectSequenceNoThrow("IF")) {
			sws(c);
			c.expectSequence("NOT");
			sws(c);
			c.expectSequence("EXISTS");
			sws(c);
			ifNotExists = true;
		} else
			ifNotExists = false;
		AdminNode r;
		switch (type) {
			case ID_TABLE:
				return parseCreateTable(position, c, options, ifNotExists);
			case ID_INDEX:
				//				if (options != null)
				//					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.operation));
				r = parseCreateIndex(position, c, options);
				break;
			case ID_TRIGGER:
				if (options != null)
					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.getOperation()));
				r = parseCreateTrigger(position, c);
				break;
			case ID_DBO:
				if (options != null)
					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.getOperation()));
				r = parseCreateDbo(position, c);
				break;
			case ID_TIMER:
				if (options != null)
					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.getOperation()));
				r = parseCreateTimer(position, c);
				break;
			case ID_PROCEDURE:
				if (options != null)
					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.getOperation()));
				r = parseCreateProcedure(position, c);
				break;
			case ID_METHOD:
				if (options != null)
					throw new ExpressionParserException(position, "Illegal Option: " + toOperationString(options.getOperation()));
				r = parseCreateMethod(position, c);
				break;
			default:
				throw new ExpressionParserException(position, "Expecting: INDEX, METHOD, PROCEDURE, TABLE, TIMER or TRIGGER");
		}
		if (ifNotExists)
			r.setIfCondition(AdminNode.IF_NOT_EXISTS);
		return r;
	}
	private AdminNode parseCreateIndex(int position, CharReader c, SqlNode options) {
		int pos = c.getCountRead();
		VariableNode indexName = parseVariableNode(c, buf);
		sws(c);
		expectKeywordId(c, ID_ON);
		sws(c);
		String methodName = parseVariable(c, buf);
		sws(c);
		c.expect('(');
		List<Node> params = new ArrayList<Node>();
		boolean first = true;
		outer: for (;;) {
			int p = c.getCountRead();
			sws(c);
			String name = parseVariable(c, buf);
			sws(c);
			if (c.peak() == ',' || c.peak() == ')') {
				params.add(new VariableNode(p, name));
			} else {
				sws(c);
				String type = c.peak() == ',' || c.peak() == ')' ? null : parseVariable(c, buf);
				params.add(new DeclarationNode(p, name, type, null));
			}
			sws(c);
			switch (c.peak()) {
				case ')':
					c.expect(')');
					break outer;
				case ',':
					c.expect(',');
					continue;
				default:
					throw c.newExpressionParserException("Expecting comma (,) or closing parenthesis");
			}
		}
		MethodNode next = new MethodNode(position, methodName, params);
		sws(c);
		UseNode useNode;
		if (c.expectSequenceNoThrow("USE")) {
			useNode = parseUse(position, c, false);
		} else
			useNode = null;
		SqlOperationNode dn = new SqlOperationNode(pos, indexName, next, ID_ON);
		return new AdminNode(position, ID_CREATE, ID_INDEX, dn, useNode, options);
	}
	private AdminNode parseCreateTrigger(int position, CharReader c) {
		SqlOperationNode triggerNameNode = parseSqlOperationNode(c, -1, true);
		SqlOperationNode typeNode = parseSqlOperationNode(c, ID_OFTYPE, true);
		expectKeywordId(c, ID_ON);
		clearStopKeywordIds();
		addStopKeywordId(ID_USE);
		addStopKeywordId(ID_PRIORITY);
		SqlColumnsNode tableNameNode = parseCommaList(position, ID_ON, c);
		SqlOperationNode priorityNode = parseSqlOperationNode(c, ID_PRIORITY, false);
		triggerNameNode.setNext(typeNode);
		typeNode.setNext(tableNameNode);
		tableNameNode.setNext(priorityNode);
		UseNode useNode;
		if (c.expectSequenceNoThrow("USE")) {
			useNode = parseUse(position, c, false);
		} else
			useNode = null;
		return new AdminNode(position, ID_CREATE, ID_TRIGGER, triggerNameNode, useNode);
	}
	private AdminNode parseCreateTimer(int position, CharReader c) {
		SqlOperationNode triggerNameNode = parseSqlOperationNode(c, -1, true);
		SqlOperationNode typeNode = parseSqlOperationNode(c, ID_OFTYPE, true);
		expectKeywordId(c, ID_ON);
		sws(c);
		int pos = c.getCountRead();
		if (!parseString(c, SH.clear(buf)))
			throw c.newExpressionParserException("Expecting String");
		VariableNode time = new VariableNode(pos, buf.toString());//should be const node
		SqlOperationNode schedule = new SqlOperationNode(pos, time, null, ID_ON);
		SqlOperationNode priorityNode = parseSqlOperationNode(c, ID_PRIORITY, false);
		triggerNameNode.setNext(typeNode);
		typeNode.setNext(schedule);
		schedule.setNext(priorityNode);
		UseNode useNode;
		if (c.expectSequenceNoThrow("USE")) {
			useNode = parseUse(position, c, false);
		} else
			useNode = null;

		return new AdminNode(position, ID_CREATE, ID_TIMER, triggerNameNode, useNode);
	}
	private AdminNode parseCreateDbo(int position, CharReader c) {
		SqlOperationNode dboNameNode = parseSqlOperationNode(c, -1, true);
		SqlOperationNode typeNode = parseSqlOperationNode(c, ID_OFTYPE, true);
		SqlOperationNode priorityNode = parseSqlOperationNode(c, ID_PRIORITY, false);
		dboNameNode.setNext(typeNode);
		UseNode useNode;
		if (c.expectSequenceNoThrow("USE")) {
			useNode = parseUse(position, c, false);
		} else
			useNode = null;
		return new AdminNode(position, ID_CREATE, ID_DBO, dboNameNode, useNode);
	}
	private AdminNode parseCreateProcedure(int position, CharReader c) {
		SqlOperationNode procedureNameNode = parseSqlOperationNode(c, -1, true);
		SqlOperationNode typeNode = parseSqlOperationNode(c, ID_OFTYPE, true);
		procedureNameNode.setNext(typeNode);
		UseNode useNode;
		if (c.expectSequenceNoThrow("USE")) {
			useNode = parseUse(position, c, false);
		} else
			useNode = null;
		return new AdminNode(position, ID_CREATE, ID_PROCEDURE, procedureNameNode, useNode);
	}
	private AdminNode parseCreateMethod(int position, CharReader c) {
		Node node;
		boolean orig = isAllowSqlInjection();
		try {
			pushState(STATE_JAVA);
			setAllowSqlInjection(true);
			node = parseStatement(c);
		} finally {
			setAllowSqlInjection(orig);
			popState(STATE_JAVA);
		}
		if (node instanceof BlockNode) {
			BlockNode bn = castNode(node, BlockNode.class);
			for (int n = 0; n < bn.getNodesCount(); n++)
				castNode(bn.getNodeAt(n), MethodDeclarationNode.class);
			return new AdminNode(position, ID_CREATE, ID_METHOD, bn, null);
		} else {
			MethodDeclarationNode mdn = castNode(node, MethodDeclarationNode.class);
			return new AdminNode(position, ID_CREATE, ID_METHOD, new BlockNode(position, CH.l((Node) mdn)), null);
		}
	}
	private SqlOperationNode parseSqlOperationNode(CharReader c, int operation, boolean required) {
		sws(c);
		int pos = c.getCountRead();
		if (operation != -1) {
			if (!required && !c.peakSequence(toOperationString(operation)))
				return null;
			expectKeywordId(c, operation);
			sws(c);
		}
		Node name;
		if (OH.isBetween(c.peakOrEof(), '0', '9'))
			name = ExpressionParserHelper.parseNumber(c, buf);
		else
			name = parseVariableNode(c, buf);
		sws(c);
		return new SqlOperationNode(pos, name, null, operation);
	}
	private CreateTableNode parseCreateTable(int position, CharReader c, SqlNode options, boolean ifNotExists) {
		List<AdminNode> nodes = new ArrayList<AdminNode>();
		boolean needsAs = false;
		for (;;) {
			sws(c);
			VariableNode next = parseVariableNode(c, buf);
			sws(c);
			Node node;
			if (c.expectNoThrow('(')) {
				List<Node> params = new ArrayList<Node>();
				String methodName = next.toString();
				outer: for (;;) {
					sws(c);
					int p = c.getCountRead();
					VariableNode name = parseVariableNode(c, buf);
					sws(c);
					VariableNode type = parseVariableNode(c, buf);
					sws(c);
					UseNode use = parseUse(c.getCountRead(), c, false);
					sws(c);
					params.add(new SqlColumnDefNode(p, null, name, type, use));
					switch (c.peak()) {
						case ')':
							c.expect(')');
							break outer;
						case ',':
							c.expect(',');
							continue;
						default:
							throw c.newExpressionParserException("Expecting comma (,) or closing parenthesis or USE clause");
					}
				}
				node = new MethodNode(position, methodName, params);
			} else if (next instanceof VariableNode) {
				needsAs = true;
				node = next;
			} else
				throw new ExpressionParserException(next.getPosition(), "Expecting either expression: table_name or table_name(COLNAME COLTYPE,...)");
			sws(c);
			UseNode useNode;
			if (c.expectSequenceNoThrow("USE")) {
				useNode = parseUse(position, c, false);
				sws(c);
			} else
				useNode = null;
			AdminNode an = new AdminNode(position, ID_CREATE, ID_TABLE, node, useNode, options);
			if (ifNotExists)
				an.setIfCondition(AdminNode.IF_NOT_EXISTS);
			nodes.add(an);
			if (!c.expectNoThrow(','))
				break;
		}
		Node as;
		if (expectKeywordIdNoThrow(c, ID_AS)) {
			as = parseToken(c);
		} else if (c.expectNoThrow('=')) {
			as = parseToken(c);
		} else if (needsAs)
			throw new ExpressionParserException(c.getCountRead(), "Expecting equal(=) or AS keyword");
		else
			as = null;
		return new CreateTableNode(position, AH.toArray(nodes, AdminNode.class), as);
		//			if (c.peakSequence("AS")) {
		//				int pos = c.getCountRead();
		//				expectKeywordId(c, ID_AS);
		//				SqlNode t = castNode(parseToken(c), SqlNode.class);
		//				SqlOperationNode operationNode = new SqlOperationNode(pos, next, t, ID_AS);
		//				method.next = operationNode;
		//			}
		//		nodes.add(new AdminNode(position, ID_CREATE, ID_TABLE, method, useNode, options));
		//		} else if (next instanceof VariableNode) {
		//
		//			//			List<Node> namesArray = null;
		//			//			while (c.expectNoThrow(',')) {
		//			//				sws(c);
		//			//				if (namesArray == null) {
		//			//					namesArray = new ArrayList<Node>();
		//			//					namesArray.add(next);
		//			//				}
		//			//				namesArray.add(parseVariableNode(c, buf));
		//			//				sws(c);
		//			//			}
		//			int pos = c.getCountRead();
		//			int keyWord = getKeywordId(c);
		//			final UseNode useNode;
		//			if (keyWord == ID_USE) {
		//				useNode = parseUse(position, c, false);
		//				addStopKeywordId(ID_AS);
		//				expectKeywordId(c, ID_AS);
		//			} else {
		//				useNode = null;
		//				if (c.expectNoThrow('=')) {
		//					Node t = super.parseToken(c);
		//					if (t == null)
		//						throw new ExpressionParserException(pos, "Expecting statement after CREATE TABLE ... = ");
		//					OperationNode on = new OperationNode(pos, next, t, OperationNode.OP_EQ);
		//					return new AdminNode(pos, ID_CREATE, ID_TABLE, on, null, options);
		//				}
		//				if (keyWord != ID_AS)
		//					throw new ExpressionParserException(pos, "Expecting AS or USE keywords");
		//			}
		//			SqlNode t = castNode(parseToken(c), SqlNode.class);
		//			SqlOperationNode operationNode = new SqlOperationNode(pos, namesArray == null ? next : new ArrayNode(position, namesArray), t, ID_AS);
		//			return new AdminNode(position, ID_CREATE, ID_TABLE, operationNode, useNode, options);
		//		} else
		//			throw new ExpressionParserException(next.getPosition(), "expecting: table_name AS -- or -- table_name(COLNAME COLTYPE,...)");
	}
	private SqlColumnsNode parseUnpack(int position, CharReader c) {
		sws(c);
		Node[] columns = null;
		for (;;) {
			Node col = parseToken(c);
			expectKeywordId(c, ID_ON);
			Node val = parseToken(c);
			OnNode as = new OnNode(col.getPosition(), col, val);
			sws(c);
			columns = columns == null ? new Node[] { as } : AH.append(columns, as);
			if (c.peakOrEof() == ',')
				c.expect(',');
			else
				break;
		}
		Node next = parseToken(c);
		return new SqlColumnsNode(position, columns, next, ID_UNPACK);
	}
	private AdminNode parseAlter(int position, CharReader c) {
		sws(c);
		int pos = c.getCountRead();
		int type = getKeywordId(c);
		sws(c);
		SqlNode options;
		if (isScopeKeyword(type)) {
			options = new SqlNode(position, null, type);
			position = c.getCountRead();
			type = getKeywordId(c);
			sws(c);
		} else
			options = null;
		if (type != ID_TABLE && type != ID_DBO)
			throw new ExpressionParserException(pos, "expecting: DBO or TABLE");
		VariableNode next = parseVariableNode(c, buf);
		sws(c);
		List<Node> updates = parseAlterColumns(c);
		return new AdminNode(position, ID_ALTER, type, new MethodNode(position, next.toString(), updates), null, options);
	}
	private List<Node> parseAlterColumns(CharReader c) {
		final List<Node> parts = new ArrayList<Node>();
		int usePosition = -1;
		while (true) {
			int position = c.getCountRead();
			int type = getKeywordId(c);
			sws(c);
			VariableNode col = null;
			VariableNode newCol = null;
			VariableNode newType = null;
			VariableNode before = null;
			Node expression = null;
			UseNode use = null;
			switch (type) {
				case ID_ADD:
					newCol = parseVariableNode(c, buf);
					sws(c);
					if (c.expectNoThrow('=')) {
						expression = parseToken(c);
						if (expression == null)
							throw new ExpressionParserException(c.getCountRead(), "Expecting expression after =");
					} else {
						newType = parseVariableNode(c, buf);
						use = parseUse(c.getCountRead(), c, false);
						sws(c);
						c.mark();
						if (getKeywordId(c) == ID_BEFORE) {
							sws(c);
							before = parseVariableNode(c, buf);
							sws(c);
						} else
							c.returnToMark();
						if (c.expectNoThrow('=')) {
							expression = parseToken(c);
							if (expression == null)
								throw new ExpressionParserException(c.getCountRead(), "Expecting expression after =");
						}
					}
					break;
				case ID_RENAME:
					col = parseVariableNode(c, buf);
					sws(c);
					expectKeywordId(c, ID_TO);
					sws(c);
					newCol = parseVariableNode(c, buf);
					break;
				case ID_MODIFY:
					col = parseVariableNode(c, buf);
					sws(c);
					expectKeywordId(c, ID_AS);
					sws(c);
					newCol = parseVariableNode(c, buf);
					sws(c);
					newType = parseVariableNode(c, buf);
					use = parseUse(c.getCountRead(), c, false);
					sws(c);
					sws(c);
					break;
				case ID_DROP:
					col = parseVariableNode(c, buf);
					break;
				case ID_USE:
					usePosition = position;
					use = parseUse(position, c, false);
					break;
				default:
					throw new ExpressionParserException(position, "ALTER expression expecting ADD, RENAME, MODIFY or DROP: " + toOperationString(type));
			}
			parts.add(new AlterColumnNode(position, type, col, newCol, newType, before, use, expression));
			sws(c);
			if (c.peakOrEof() == ',') {
				c.expect(',');
				sws(c);
				continue;
			}
			break;
		}
		if (usePosition != -1 && parts.size() > 1)
			throw new ExpressionParserException(usePosition, "ALTER ... USE can not be used in conjunction with other ALTER statements");
		return parts;
	}
	private AdminNode parseRename(int position, CharReader c) {
		sws(c);
		int type = getKeywordId(c);
		SqlNode options;
		if (isScopeKeyword(type)) {
			options = new SqlNode(position, null, type);
			sws(c);
			position = c.getCountRead();
			type = getKeywordId(c);
			sws(c);
		} else
			options = null;
		if (type != ID_TABLE && type != ID_TRIGGER && type != ID_TIMER && type != ID_PROCEDURE && type != ID_DBO)
			throw new ExpressionParserException(c.getCountRead(), "expecting: DBO, TABLE, TRIGGER, TIMER, PROCEDURE");
		sws(c);
		Node from = parseVariableNode(c, buf);
		sws(c);
		int pos2 = c.getCountRead();
		expectKeywordId(c, ID_TO);
		sws(c);
		Node to = parseVariableNode(c, buf);
		return new AdminNode(position, ID_RENAME, type, new AsNode(pos2, from, to, true), null, options);
	}
	private AdminNode parseDrop(int position, CharReader c) {
		sws(c);
		int pos = c.getCountRead();
		int type = getKeywordId(c);
		sws(c);
		SqlNode options;
		if (isScopeKeyword(type)) {
			options = new SqlNode(position, null, type);
			position = c.getCountRead();
			type = getKeywordId(c);
			sws(c);
		} else
			options = null;

		boolean ifExists;
		if (c.expectSequenceNoThrow("IF")) {
			sws(c);
			c.expectSequence("EXISTS");

			sws(c);
			ifExists = true;
		} else
			ifExists = false;
		AdminNode r;
		switch (type) {
			case ID_TABLE:
				r = parseDropTable(position, c, options);
				break;
			case ID_INDEX:
				r = parseDropIndex(position, c, options);
				break;
			case ID_TRIGGER:
				if (options != null && options.getOperation() != ID_PUBLIC)
					throw new ExpressionParserException(pos, "Can Only Drop PUBLIC TRIGGER");
				r = parseDropTrigger(position, c);
				break;
			case ID_TIMER:
				if (options != null && options.getOperation() != ID_PUBLIC)
					throw new ExpressionParserException(pos, "Can Only Drop PUBLIC TIMER");
				r = parseDropTimer(position, c);
				break;
			case ID_PROCEDURE:
				if (options != null && options.getOperation() != ID_PUBLIC)
					throw new ExpressionParserException(pos, "Can Only Drop PUBLIC PROCEDURE");
				r = parseDropProcedure(position, c);
				break;
			case ID_METHOD:
				if (options != null && options.getOperation() != ID_PUBLIC)
					throw new ExpressionParserException(pos, "Can Only Drop PUBLIC METHOD");
				r = parseDropMethod(position, c);
				break;
			case ID_DBO:
				if (options != null && options.getOperation() != ID_PUBLIC)
					throw new ExpressionParserException(pos, "Can Only Drop PUBLIC DBO");
				r = parseDropDbo(position, c);
				break;
			default:
				throw new ExpressionParserException(pos, "Expecting: DBO, INDEX, METHOD, PROCEDURE, TABLE, TABLES, TIMER or TRIGGER");
		}
		if (ifExists)
			r.setIfCondition(AdminNode.IF_EXISTS);
		return r;
	}

	private AdminNode parseDropTable(int position, CharReader c, SqlNode options) {
		List<Node> params = new ArrayList<Node>();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_TABLE, names, null, options);
	}
	private AdminNode parseTruncateTable(int position, CharReader c) {
		List<Node> params = new ArrayList<Node>();
		SqlNode options = null;
		sws(c);
		c.mark();
		int id = getKeywordId(c);
		if (isScopeKeyword(id)) {
			options = new SqlNode(position, null, id);
			sws(c);
			c.mark();
			id = getKeywordId(c);
		}
		if (id == ID_TABLE) {
			sws(c);
		} else
			c.returnToMark();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_TRUNCATE, ID_TABLE, names, null, options);
	}
	private AdminNode parseDropIndex(int position, CharReader c, SqlNode options) {
		sws(c);
		List<Node> params = new ArrayList<Node>();
		do {
			VariableNode indexName = parseVariableNode(c, buf);
			sws(c);
			expectKeywordId(c, ID_ON);
			sws(c);
			VariableNode tableName = parseVariableNode(c, buf);
			SqlOperationNode node = new SqlOperationNode(position, indexName, tableName, ID_ON);
			params.add(node);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_INDEX, names, null, options);
	}
	private AdminNode parseDropTrigger(int position, CharReader c) {
		sws(c);
		List<Node> params = new ArrayList<Node>();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_TRIGGER, names, null);
	}
	private AdminNode parseDropTimer(int position, CharReader c) {
		sws(c);
		List<Node> params = new ArrayList<Node>();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_TIMER, names, null);
	}
	private AdminNode parseDropDbo(int position, CharReader c) {
		sws(c);
		List<Node> params = new ArrayList<Node>();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_DBO, names, null);
	}
	private AdminNode parseDropProcedure(int position, CharReader c) {
		sws(c);
		List<Node> params = new ArrayList<Node>();
		do {
			sws(c);
			params.add(parseVariableNode(c, buf));
			sws(c);
		} while (c.expectNoThrow(','));
		ArrayNode names = new ArrayNode(position, params);
		return new AdminNode(position, ID_DROP, ID_PROCEDURE, names, null);
	}
	private AdminNode parseDropMethod(int position, CharReader c) {
		String methodName = parseVariable(c, buf);
		sws(c);
		c.expect('(');
		MethodNode names = parseMethod(position, methodName, c);
		return new AdminNode(position, ID_DROP, ID_METHOD, names, null);
	}
	private SqlNode parseCall(int position, CharReader c) {
		sws(c);
		String methodName = parseVariable(c, buf);
		sws(c);
		c.expect('(');
		MethodNode r = parseMethod(position, methodName, c);
		sws(c);
		int id = peekKeywordId(c);
		SqlNode limitNode = null;
		if (id == ID_LIMIT)
			limitNode = (SqlColumnsNode) parseStatement(c);
		return new SqlCallNode(position, r.getMethodName(), r.getParamsToArray(), limitNode);
	}
	private AdminNode parseDescribe(int position, CharReader c) {
		sws(c);
		List<Node> nodes = new ArrayList<Node>();
		int id = getKeywordId(c);
		SqlNode options;
		if (isScopeKeyword(id)) {
			options = new SqlNode(position, null, id);
			sws(c);
			c.mark();
			id = getKeywordId(c);
		} else
			options = null;
		if (id == ID_INVALID)
			throw new ExpressionParserException(c.getCountRead(), "DESCRIBE Expecting: DBO, INDEX, PROCEDURE, TABLE, TIMER or TRIGGER");
		for (;;) {
			sws(c);
			int namePos = c.getCountRead();
			Node name;
			//add logic for DESCIRBE METHOD func(arg,arg..);
			if (id == ID_METHOD) {//Use MethodNode instead of VariableNode for method
				c.mark();
				String methodName = parseVariable(c, buf);
				c.expect('(');
				name = parseMethod(0, methodName, c);
			} else {
				name = parseVariableNode(c, buf);
				sws(c);
				c.mark();
			}
			SqlOperationNode onNode = parseSqlOperationNode(c, ID_ON, false);
			SqlOperationNode fromNode = parseSqlOperationNode(c, ID_FROM, false);
			if (onNode != null)
				onNode.setNext(fromNode);
			else
				onNode = fromNode;
			if (name instanceof VariableNode)
				nodes.add(new SqlOperationNode(namePos, (VariableNode) name, onNode, id));
			else //name instanceof MethodNode
				nodes.add(new SqlOperationNode(namePos, (MethodNode) name, onNode, id));
			if (!c.expectNoThrow(','))
				break;
		}
		ArrayNode names = new ArrayNode(position, nodes);
		return new AdminNode(position, ID_DESCRIBE, ID_TABLE, names, null, options);
	}
	private InsertNode parseInsertInto(int position, CharReader c, int operation) {
		sws(c);
		c.mark();
		int type = getKeywordId(c);
		final boolean returnGeneratedKeys;
		if (type == ID_AND) {
			sws(c);
			expectKeywordId(c, ID_SELECT);
			sws(c);
			c.mark();
			type = getKeywordId(c);
			returnGeneratedKeys = true;
		} else
			returnGeneratedKeys = false;

		if (type != ID_INTO)
			c.returnToMark();
		else
			sws(c);
		int tableScope;
		c.mark();
		type = getKeywordId(c);
		if (type == ID_INVALID) {
			c.returnToMark();
			tableScope = type;
		} else if (isScopeKeyword(type)) {
			tableScope = type;
			sws(c);
		} else {
			throw new ExpressionParserException(c.getCountRead(), "Syntax error: " + toOperationString(type));
		}
		VariableNode tableName = parseVariableNode(c, buf);
		sws(c);
		String table = tableName.getVarname();
		Node[] columns;
		Node values;
		if (c.peak() == '(') {
			c.expect('(');
			MethodNode t = parseMethod(position, table, c);
			columns = t.getParamsToArray();
		} else {
			columns = null;
		}
		sws(c);
		int pos = c.getCountRead();
		c.mark();
		type = getKeywordId(c);
		Node[] syncOns;
		final boolean byName;
		if (type == ID_BYNAME) {
			byName = true;
			sws(c);
			type = getKeywordId(c);
		} else
			byName = false;
		if (type == ID_ON && operation == ID_SYNC) {
			sws(c);
			c.expect('(');
			MethodNode t = parseMethod(position, table, c);
			syncOns = t.getParamsToArray();
			sws(c);
			pos = c.getCountRead();
			c.mark();
			type = getKeywordId(c);
		} else
			syncOns = null;
		SqlForNode forLoop = null;
		sws(c);
		if (type == ID_FOR) {
			if (byName)
				throw new ExpressionParserException(c.getCountRead(), "BYNAME with FOR is not valid");
			forLoop = parseSqlFor(pos, c);
			expectKeywordId(c, ID_VALUES);
			values = parseValues(pos, c);
		} else if (type == ID_VALUES) {
			if (byName)
				throw new ExpressionParserException(c.getCountRead(), "BYNAME with VALUES is not valid");
			values = parseValues(pos, c);
		} else if (type == ID_FROM) {
			values = (SqlNode) parseToken(c);
		} else if (type == ID_USE) {
			c.returnToMark();
			values = (SqlNode) parseToken(c);
		} else if (type == ID_SELECT || type == ID_SHOW || type == ID_DESCRIBE || type == ID_ANALYZE || type == ID_PREPARE || type == ID_DIAGNOSE) {
			c.returnToMark();
			values = (SqlNode) parseToken(c);
		} else
			throw new ExpressionParserException(c.getCountRead(), "Expecting VALUES or FROM");
		int id = peekKeywordId(c);
		SqlColumnsNode limitNode = null;
		if (id == ID_LIMIT)
			limitNode = (SqlColumnsNode) parseStatement(c);
		return new InsertNode(position, operation, tableName.getPosition(), table, tableScope, columns, forLoop, values, syncOns, limitNode, byName, returnGeneratedKeys);
	}
	private ValuesNode parseValues(int position, CharReader c) {
		sws(c);
		c.expect('(');
		int colCount = -1;
		final List<Node> values = new ArrayList<Node>();
		int cnt = 0;
		while (true) {
			Node current = parseToken(c);
			cnt++;
			switch (c.peakOrEof()) {
				case ',': {
					if (current == null)
						throw new ExpressionParserException(c.getCountRead(), "missing expression");
					c.expect(',');
					values.add(current);
					continue;
				}
				case ')': {
					if (current == null)
						throw new ExpressionParserException(c.getCountRead(), "missing expression");
					c.expect(')');
					values.add(current);
					if (colCount == -1)
						colCount = cnt;
					else if (colCount != cnt)
						throw new ExpressionParserException(c.getCountRead(), "Inconsistent number of columns: " + colCount + " vs. " + cnt);
					cnt = 0;
					sws(c);
					if (c.peakOrEof() == ',') {
						c.expect(',');
						sws(c);
						c.expect('(');
						sws(c);
					} else
						return new ValuesNode(position, colCount, values.toArray(new Node[values.size()]));
					continue;
				}
				default:
					throw new ExpressionParserException(c.getCountRead(), "Expecting )");
			}
		}
	}
	public static List<Tuple2<Node, String>> getColumns(SqlNode n) {
		SqlColumnsNode node = JavaExpressionParser.castNode(n, SqlColumnsNode.class);
		List<Tuple2<Node, String>> r = new ArrayList<Tuple2<Node, String>>(node.getColumnsCount());
		for (int i = 0; i < node.getColumnsCount(); i++) {
			Node column = node.getColumnAt(i);
			Node variableName;
			String as;
			if (column instanceof AsNode) {
				AsNode anode = (AsNode) column;
				variableName = anode.getValue();
				if (anode.getAs() instanceof VariableNode)
					as = ((VariableNode) anode.getAs()).getVarname();
				else
					throw new RuntimeException();
			} else if (column instanceof VariableNode) {
				VariableNode vnode = ((VariableNode) column);
				variableName = vnode;
				as = vnode.getVarname();
			} else {
				variableName = column;
				as = null;
			}
			r.add(new Tuple2<Node, String>(variableName, as));
		}
		return r;
	}
	public static SqlNode castNextToSqlNode(SqlColumnsNode node, int sqlId) {
		Node n = node.getNext();
		if (!(n instanceof SqlNode)) {
			if (n == null)
				throw new ExpressionParserException(node.getEndPosition() + SqlExpressionParser.toOperationString(node.getOperation()).length(),
						"Expecting " + toOperationString(sqlId));
			else
				throw new ExpressionParserException(n.getPosition(),
						"Expecting " + toOperationString(sqlId) + " not " + SH.stripSuffix(n.getClass().getSimpleName(), "Node", false));
		}
		SqlNode r = (SqlNode) n;
		if (r.getOperation() != sqlId)
			throw new ExpressionParserException(n.getPosition(),
					"Expecting " + toOperationString(sqlId) + "   (not " + SqlExpressionParser.toOperationString(r.getOperation()) + ")");
		return r;
	}
	public static SqlNode castNextToSqlNodeNoThrow(SqlColumnsNode node, int sqlId) {
		Node n = node.getNext();
		if (!(n instanceof SqlNode)) {
			return null;
		}
		SqlNode r = (SqlNode) n;
		if (r.getOperation() != sqlId)
			return null;
		return r;
	}
	public static SqlNode castToSqlNode(Node n, int sqlId) {
		if (!(n instanceof SqlNode))
			throw new ExpressionParserException(n.getPosition(),
					"Expecting " + toOperationString(sqlId) + "  (not " + SH.stripSuffix(n.getClass().getSimpleName(), "Node", false) + ")");
		SqlNode r = (SqlNode) n;
		if (r.getOperation() != sqlId)
			throw new ExpressionParserException(n.getPosition(),
					"Expecting " + toOperationString(sqlId) + "   (not " + SqlExpressionParser.toOperationString(r.getOperation()) + ")");
		return r;
	}
	public boolean isAllowSqlInjection() {
		return allowSqlInjection;
	}
	public void setAllowSqlInjection(boolean allowSqlInjection) {
		this.allowSqlInjection = allowSqlInjection;
	}
	protected void onExitingParenthesis() {
		this.stopKeywordIds = this.parenthesisStopWords[--parenthesisDepth];
	}
	protected void onEnteringParenthesis() {
		if (parenthesisDepth == parenthesisStopWords.length)
			parenthesisStopWords = Arrays.copyOf(parenthesisStopWords, parenthesisStopWords.length * 2);
		parenthesisStopWords[parenthesisDepth++] = this.stopKeywordIds;
		this.stopKeywordIds = 0;
	}

	private static final byte[] RESERVED_OPERATIONS = new byte[] { ID_CONCURRENT, ID_PREPARE, ID_ANALYZE, ID_ALTER, ID_DELETE, ID_CREATE, ID_DROP, ID_INSERT, ID_SELECT, ID_SYNC,
			ID_USE, ID_EXECUTE, ID_TRUNCATE, ID_RENAME, ID_SHOW, ID_CALL, ID_DESCRIBE, ID_ENABLE, ID_DISABLE, ID_UPDATE, ID_DIAGNOSE };

	private static final Set<CharSequence> RESERVED_WORDS = new HasherSet<CharSequence>(CaseInsensitiveHasher.INSTANCE);
	static {
		for (byte b : RESERVED_OPERATIONS)
			RESERVED_WORDS.add(toOperationString(b));
		RESERVED_WORDS.add("return");
		RESERVED_WORDS.add("switch");
		RESERVED_WORDS.add("if");
		RESERVED_WORDS.add("or");
		RESERVED_WORDS.add("and");
		RESERVED_WORDS.add("break");
		RESERVED_WORDS.add("throw");
		RESERVED_WORDS.add("catch");
		RESERVED_WORDS.add("else");
		RESERVED_WORDS.add("asc");
		RESERVED_WORDS.add("desc");
	}

	public static boolean isReserved(CharSequence text) {
		return RESERVED_WORDS.contains(text);
	}

	protected Node parseSqlString(CharReader cr, StringBuilder sb) {
		SH.clear(sb);
		StringTemplateNode r = null, tail = null;
		for (;;) {
			if (cr.peakOrEof() == ';' || cr.isEof()) {
				int pos = cr.getCountRead() - sb.length();
				if (r != null) {
					tail.setNext(new StringTemplateNode(pos, new AppendNode(pos, SH.toStringAndClear(sb), StringTemplateNode.NO_ESCAPE), false));
					tail = tail.getNext();
					return r;
				} else {
					return new ConstNode(pos, sb.toString());
				}
			}
			char c = cr.readChar();
			switch (c) {
				case '\\': {
					//      ;    -> [end]
					//      \;   -> ;
					//     \\;   -> \ [end]
					//     \\\;  -> \;
					//     \\\\; -> \\ [end]
					//     \x    ->  \x
					//     \     ->  \
					int slashCount = 1;
					while (true) {
						c = cr.peak();
						if (c == '\\') {
							cr.readChar();
							slashCount++;
						} else if (c == ';') {
							SH.repeat('\\', slashCount / 2, sb);
							if (slashCount % 2 == 0) {
								break;
							} else {
								cr.readChar();
								sb.append(c);
								break;
							}
						} else {
							cr.readChar();
							SH.repeat('\\', slashCount, sb);
							sb.append(c);
							break;
						}
					}
					continue;
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
					StringTemplateNode t = new StringTemplateNode(pos, new AppendNode(pos, text, StringTemplateNode.NO_ESCAPE), false);
					if (r == null)
						r = tail = t;
					else {
						tail.setNext(t);
						tail = t;
					}
					sws(cr);
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
						//						cr.expectNoThrow('\\');
						if (!cr.expectNoThrow(lastChar)) {
							tail.setNext(new StringTemplateNode(pos, new AppendNode(pos, SH.toString(lastChar), StringTemplateNode.NO_ESCAPE), false));
							tail = tail.getNext();
							tail.setNext(new StringTemplateNode(pos, node, StringTemplateNode.NO_ESCAPE, false));
							tail = tail.getNext();
						} else {
							tail.setNext(new StringTemplateNode(pos, node, lastChar, false));
							tail = tail.getNext();
						}
					} else {
						tail.setNext(new StringTemplateNode(pos, node, StringTemplateNode.NO_ESCAPE, false));
						tail = tail.getNext();
					}
					continue;
			}
			sb.append(c);
		}
	}
	public boolean parseString(CharReader c, StringBuilder sb) {
		if (!c.expectNoThrow('"'))
			return false;
		if (c.expectSequenceNoThrow("\"\"")) {// """......""" syntax
			int pos = c.getCountRead();
			if (c.readUntilSequenceAndSkip("\"\"\"", sb) == -1)
				throw new ExpressionParserException(pos, "missing closing triple-quote (\"\"\")");
		} else {
			c.readUntilSkipEscaped('"', '\\', buf);
			c.expect('"');
		}
		return true;
	}
	protected Node parseString(CharReader cr, StringBuilder sb, String termination, boolean allowEof) {
		if (!allowSqlInjection) {
			int pos = cr.getCountRead();
			SH.clear(buf);
			cr.readUntilSkipEscaped('"', '\\', buf);
			return new ConstNode(pos, SH.toStringAndClear(buf));
		}
		return super.parseString(cr, sb, termination, allowEof);
	}

	public void setProcessingDeferredSql(boolean b) {
		this.preocessingDeferredSql = b;
	}
	public boolean getProcessingDeferredSql() {
		return this.preocessingDeferredSql;
	}

	@Override
	public boolean isValidVarName(String value) {
		return super.isValidVarName(value) && !isReserved(value);
	}
	public static boolean isJoin(int operation) {
		switch (operation) {
			case ID_JOIN:
			case ID_LEFT_JOIN:
			case ID_LEFT_ONLY_JOIN:
			case ID_RIGHT_JOIN:
			case ID_RIGHT_ONLY_JOIN:
			case ID_OUTER_JOIN:
			case ID_OUTER_ONLY_JOIN:
				return true;
			default:
				return false;
		}
	}

}
