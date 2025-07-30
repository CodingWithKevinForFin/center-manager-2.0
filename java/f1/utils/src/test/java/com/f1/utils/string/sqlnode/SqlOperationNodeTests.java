package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.CastNode;
import com.f1.utils.string.node.ConstNode;
import com.f1.utils.string.node.VariableNode;

public class SqlOperationNodeTests {

    @Test
    public void TestSqlOperationNodeCtor() {
        final int position = 999;
        VariableNode name = new VariableNode(0, "name");
        VariableNode next = new VariableNode(0, "next");
        SqlOperationNode node = new SqlOperationNode(position, name, next, SqlExpressionParser.ID_SELECT);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlOperationNodeGetName() {
        final int position = 999;
        VariableNode name = new VariableNode(0, "name");
        VariableNode next = new VariableNode(0, "next");
        SqlOperationNode node = new SqlOperationNode(position, name, next, SqlExpressionParser.ID_SELECT);
        Assert.assertEquals("name", node.getNameAsString());
    }

    @Test
    public void TestSqlOperationNodeGetName2() {
        final int position = 999;
        ConstNode name = new ConstNode(0, "name");
        VariableNode next = new VariableNode(0, "next");
        SqlOperationNode node = new SqlOperationNode(position, name, next, SqlExpressionParser.ID_SELECT);
        Assert.assertEquals("\"name\"", node.getNameAsString());
    }

    @Test(expected = ExpressionParserException.class)
    public void TestSqlOperationNodeGetName3() {
        final int position = 999;
        CastNode name = new CastNode(0, "castTo", null);
        VariableNode next = new VariableNode(0, "next");
        SqlOperationNode node = new SqlOperationNode(position, name, next, SqlExpressionParser.ID_SELECT);
        node.getNameAsString();
    }

    @Test
    public void TestSqlOperationNodeToString() {
        final int position = 999;
        VariableNode name = new VariableNode(0, "name");
        VariableNode next = new VariableNode(0, "next");
        SqlOperationNode node = new SqlOperationNode(position, name, next, SqlExpressionParser.ID_SELECT);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("SELECT name  next", sb.toString());
    }

}
