package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class ValuesNodeTests {

    @Test
    public void TestValuesNodeCtor() {
        final int position = 999;
        final int colCount = 10;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        ValuesNode node = new ValuesNode(position, colCount, columns);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestValuesNodeToString() {
        final int position = 999;
        final int colCount = 5;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        ValuesNode node = new ValuesNode(position, colCount, columns);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("(node0, node1, node2, node3, node4)", sb.toString());
    }

    @Test
    public void TestValuesNodeToString2() {
        final int position = 999;
        final int colCount = 4;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        ValuesNode node = new ValuesNode(position, colCount, columns);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("(node0, node1, node2, node3), (node4)", sb.toString());
    }

    @Test
    public void TestValuesNodeToString3() {
        final int position = 999;
        final int colCount = 4;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        ValuesNode node = new ValuesNode(position, colCount, columns);
        Assert.assertFalse(node.toString().isEmpty());
    }

    @Test
    public void TestValuesNodeGetPosition() {
        final int position = 999;
        final int colCount = 4;
        Node[] columns = new Node[5];
        for (int i = 0; i < 5; ++i)
            columns[i] = new VariableNode(i, "node" + i);
        ValuesNode node = new ValuesNode(position, colCount, columns);
        Assert.assertEquals(position, node.getPosition());
    }
}
