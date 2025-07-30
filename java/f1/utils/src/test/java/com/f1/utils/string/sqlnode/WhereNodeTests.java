package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class WhereNodeTests {

    @Test
    public void TestWhereNodeCtor() {
        VariableNode condition = new VariableNode(0, "condition");
        VariableNode next = new VariableNode(1, "next");
        WhereNode node = new WhereNode(9, condition, next);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestWhereNodeToString() {
        VariableNode condition = new VariableNode(0, "condition");
        VariableNode next = new VariableNode(1, "next");
        WhereNode node = new WhereNode(9, condition, next);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("WHERE condition next", sb.toString());
    }
}
