package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class AsNodeTests {

    @Test
    public void TestAsNodeCtor() {
        AsNode node = new AsNode(0, null, null, false);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestAsNodeGetPosition() {
        final int position = 999;
        AsNode node = new AsNode(position, null, null, false);
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestAsNodeToString() {
        VariableNode as = new VariableNode(0, "as");
        VariableNode value = new VariableNode(1, "value");
        AsNode node = new AsNode(0, value, as, false);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("value AS as", sb.toString());
    }

    @Test
    public void TestAsNodeToString2() {
        VariableNode as = new VariableNode(0, "as");
        VariableNode value = new VariableNode(1, "value");
        AsNode node = new AsNode(0, value, as, false);
        Assert.assertFalse(node.toString().isEmpty());
    }
}
