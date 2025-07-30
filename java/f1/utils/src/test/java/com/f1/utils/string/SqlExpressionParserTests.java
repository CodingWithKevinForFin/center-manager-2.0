package com.f1.utils.string;

import org.junit.Test;

import com.f1.utils.string.sqlnode.SqlColumnsNode;
import com.f1.utils.string.sqlnode.SqlNode;
import com.f1.utils.string.sqlnode.WhereNode;

public class SqlExpressionParserTests {

	@Test
	public void testSelect2() {
		SqlExpressionParser exp = new SqlExpressionParser();
		exp.parse("{select a from tabl where a>100 && b<50;}");
	}
	@Test
	public void testSelect() {
		SqlExpressionParser exp = new SqlExpressionParser();
		debug(exp.parse("select a,b from tabl order by a asc,a * b asc,c"), "");
	}
	private void parseExpression(String expected, String string) {
		SqlExpressionParser exp = new SqlExpressionParser();
		Node result = exp.parse(string);
		debug(result, "");
	}

	private void debug(Node node, String prefix) {
		if (node == null)
			return;
		String prefix2 = prefix + "  ";
		if (node instanceof SqlColumnsNode) {
			SqlNode n = (SqlNode) node;
			System.out.println(prefix + SqlExpressionParser.toOperationString(n.getOperation()));
			if (n instanceof SqlColumnsNode) {
				SqlColumnsNode cn = (SqlColumnsNode) n;
				if (cn.getColumns() != null) {
					for (Node n2 : cn.getColumns())
						debug(n2, prefix2);
				}
			}
			if (n instanceof WhereNode)
				debug(((WhereNode) n).getCondition(), prefix2);
			debug(n.getNext(), prefix2);
		} else
			System.out.println(prefix + node.getClass().getSimpleName() + " ==> " + node);
	}
}

