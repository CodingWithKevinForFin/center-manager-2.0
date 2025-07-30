package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class SqlForNodeTests {

    @Test
    public void SqlForNodeCtor() {
        final int position = 999;
        VariableNode varname = new VariableNode(0, "varname");
        VariableNode start = new VariableNode(1, "start");
        VariableNode end = new VariableNode(2, "end");
        VariableNode step = new VariableNode(3, "step");
        VariableNode next = new VariableNode(4, "next");
        SqlForNode node = new SqlForNode(position, varname, start, end, step, next);
        Assert.assertNotNull(node);
    }

    @Test
    public void SqlForNodeToString() {
        final int position = 999;
        VariableNode varname = new VariableNode(0, "varname");
        VariableNode start = new VariableNode(1, "start");
        VariableNode end = new VariableNode(2, "end");
        VariableNode step = new VariableNode(3, "step");
        VariableNode next = new VariableNode(4, "next");
        SqlForNode node = new SqlForNode(position, varname, start, end, step, next);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("FOR varname = start TO end STEP step next", sb.toString());
    }

    @Test
    public void SqlForNodeToString2() {
        final int position = 999;
        VariableNode varname = new VariableNode(0, "varname");
        VariableNode start = new VariableNode(1, "start");
        VariableNode end = new VariableNode(2, "end");
        VariableNode step = new VariableNode(3, "step");
        VariableNode next = new VariableNode(4, "next");
        SqlForNode node = new SqlForNode(position, varname, start, end, step, next);
        Assert.assertFalse(node.toString().isEmpty());
    }

}
