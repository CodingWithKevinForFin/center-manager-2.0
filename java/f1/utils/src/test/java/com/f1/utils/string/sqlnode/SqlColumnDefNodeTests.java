package com.f1.utils.string.sqlnode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class SqlColumnDefNodeTests {

    @Test
    public void TestSqlColumnDefNodeCtor() {
        VariableNode next = new VariableNode(0, "next");
        VariableNode name = new VariableNode(0, "name");
        VariableNode type = new VariableNode(0, "type");
        Map<String, Node> options = Collections.emptyMap();
        UseNode use = new UseNode(0, options, next);
        SqlColumnDefNode node = new SqlColumnDefNode(0, next, name, type, use);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlColumnDefNodeToString() {
        VariableNode next = new VariableNode(0, "next");
        VariableNode name = new VariableNode(0, "name");
        VariableNode type = new VariableNode(0, "type");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode use = new UseNode(0, options, next);
        SqlColumnDefNode node = new SqlColumnDefNode(0, next, name, type, use);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("name type USE option3=node3,option4=node4,option1=node1,option2=node2,option0=node0 next",
                sb.toString());
    }

    @Test
    public void TestSqlColumnDefNodeGetName() {
        VariableNode next = new VariableNode(0, "next");
        VariableNode name = new VariableNode(0, "name");
        VariableNode type = new VariableNode(0, "type");
        Map<String, Node> options = Collections.emptyMap();
        UseNode use = new UseNode(0, options, next);
        SqlColumnDefNode node = new SqlColumnDefNode(0, next, name, type, use);
        Assert.assertEquals(name, node.getName());
    }

    @Test
    public void TestSqlColumnDefNodeGetType() {
        VariableNode next = new VariableNode(0, "next");
        VariableNode name = new VariableNode(0, "name");
        VariableNode type = new VariableNode(0, "type");
        Map<String, Node> options = Collections.emptyMap();
        UseNode use = new UseNode(0, options, next);
        SqlColumnDefNode node = new SqlColumnDefNode(0, next, name, type, use);
        Assert.assertEquals(type, node.getType());
    }

    @Test
    public void TestSqlColumnDefNodeGetUse() {
        VariableNode next = new VariableNode(0, "next");
        VariableNode name = new VariableNode(0, "name");
        VariableNode type = new VariableNode(0, "type");
        Map<String, Node> options = Collections.emptyMap();
        UseNode use = new UseNode(0, options, next);
        SqlColumnDefNode node = new SqlColumnDefNode(0, next, name, type, use);
        Assert.assertEquals(use, node.getUse());
    }

}
