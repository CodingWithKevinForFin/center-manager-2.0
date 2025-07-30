package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class OnNodeTests {

    @Test
    public void TestOnNodeCtor() {
        OnNode node = new OnNode(0, new VariableNode(0, "value"), new VariableNode(0, "on"));
        Assert.assertNotNull(node);
    }

    @Test
    public void TestOnNodeGetPosition() {
        final int position = 999;
        OnNode node = new OnNode(position, new VariableNode(0, "value"), new VariableNode(0, "on"));
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestOnNodeToString() {
        OnNode node = new OnNode(0, new VariableNode(0, "value"), new VariableNode(0, "on"));
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("value ON on", sb.toString());
    }

    @Test
    public void TestOnNodeToString2() {
        OnNode node = new OnNode(0, new VariableNode(0, "value"), new VariableNode(0, "on"));
        Assert.assertFalse(node.toString().isEmpty());
    }
}
