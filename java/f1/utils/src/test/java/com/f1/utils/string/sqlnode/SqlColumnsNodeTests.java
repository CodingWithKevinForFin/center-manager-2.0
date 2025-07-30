package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

public class SqlColumnsNodeTests {

    @Test
    public void TestSqlColumnsNodeCtor() {
        final int operation = 999;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        VariableNode next = new VariableNode(0, "next");
        SqlColumnsNode node = new SqlColumnsNode(0, columns, next, operation);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlColumnsNodeGetEndPosition() {
        final int operation = 999;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        VariableNode next = new VariableNode(0, "next");
        SqlColumnsNode node = new SqlColumnsNode(0, columns, next, operation);
        Assert.assertEquals(4, node.getEndPosition());
    }

    @Test
    public void TestSqlColumnsNodeGetEndPosition2() {
        final int operation = 999;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i * 10, "node" + i);

        VariableNode next = new VariableNode(0, "next");
        Node[] sqlColumnNodes = new Node[5];
        for (int i = 0; i < 5; ++i)
            sqlColumnNodes[i] = new SqlColumnsNode(i, columns, next, operation);
        SqlColumnsNode node = new SqlColumnsNode(0, sqlColumnNodes, next, operation);
        Assert.assertEquals(40, node.getEndPosition());
    }

    @Test
    public void TestSqlColumnsNodeGetEndPosition3() {
        final int position = 9;
        final int operation = SqlExpressionParser.ID_UPDATE;
        Node[] columns = new Node[0];
        VariableNode next = new VariableNode(0, "next");
        SqlColumnsNode node = new SqlColumnsNode(position, columns, next, operation);
        Assert.assertEquals("UPDATE".length() + position, node.getEndPosition());
    }

    @Test
    public void TestSqlColumnsNodeGetEndPosition4() {
        final int position = 9;
        final int operation = -1;
        Node[] columns = new Node[0];
        VariableNode next = new VariableNode(0, "next");
        SqlColumnsNode node = new SqlColumnsNode(position, columns, next, operation);
        Assert.assertEquals(position, node.getEndPosition());
    }

    @Test
    public void TestSqlColumnsNodeToString() {
        final int operation = SqlExpressionParser.ID_UPDATE;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        VariableNode next = new VariableNode(0, "next");
        SqlColumnsNode node = new SqlColumnsNode(0, columns, next, operation);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("UPDATE node0,node1,node2,node3,node4 next", sb.toString());
    }
}
