package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

public class ExecuteNodeTests {

    @Test
    public void TestExecuteNodeCtor() {
        ExecuteNode node = new ExecuteNode(0, "value");
        Assert.assertNotNull(node);
    }

    @Test
    public void TestExecuteNodeToString() {
        ExecuteNode node = new ExecuteNode(0, "value");
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals(" EXECUTE value", sb.toString());
    }

    @Test
    public void TestExecuteNodeToString2() {
        ExecuteNode node = new ExecuteNode(0, "value");
        Assert.assertFalse(node.toString().isEmpty());
    }
}
