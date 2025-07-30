package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

public class SqlNodeTests {

    @Test
    public void TestSqlNodeCtor() {
        final int position = 999;
        VariableNode next = new VariableNode(0, "next");
        SqlNode node = new SqlNode(position, next, SqlExpressionParser.ID_SELECT);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlNodeGetPosition() {
        final int position = 999;
        VariableNode next = new VariableNode(0, "next");
        SqlNode node = new SqlNode(position, next, SqlExpressionParser.ID_SELECT);
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestSqlNodeToString() {
        final int position = 999;
        VariableNode next = new VariableNode(0, "next");
        SqlNode node = new SqlNode(position, next, SqlExpressionParser.ID_SELECT);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("SELECT next", sb.toString());
    }

    @Test
    public void TestSqlNodeToString2() {
        final int position = 999;
        VariableNode next = new VariableNode(0, "next");
        SqlNode node = new SqlNode(position, next, SqlExpressionParser.ID_SELECT);
        Assert.assertFalse(node.toString().isEmpty());
    }
}
