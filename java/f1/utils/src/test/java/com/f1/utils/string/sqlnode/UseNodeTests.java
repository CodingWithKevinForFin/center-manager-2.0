package com.f1.utils.string.sqlnode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class UseNodeTests {

    @Test
    public void TestUseNodeCtor() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestUseNodeToString() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals(" USE option3=node3,option4=node4,option1=node1,option2=node2,option0=node0 next",
                sb.toString());
    }

    @Test
    public void TestUseNodeToString2() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertFalse(node.toString().isEmpty());
    }

    @Test
    public void TestUseNodeGetOptions() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertEquals(options.keySet(), node.getOptions());
    }

    @Test
    public void TestUseNodeGetOptionsMap() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertEquals(options, node.getOptionsMap());
    }

    @Test
    public void TestUseNodeGetOption() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertEquals("default", node.getOption(String.class, "error", "default"));
    }

    @Test(expected = ExpressionParserException.class)
    public void TestUseNodeGetOption2() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        node.getOption(Integer.class, "option0", 1);
    }

    @Test
    public void TestUseNodeGetOption3() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertEquals("node1", node.getOption(String.class, "option1", "default"));
    }

    @Test
    public void TestUseNodeGetOption4() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        for (int i = 0; i < 5; ++i)
            options.put("option" + i, new VariableNode(i, "node" + i));
        UseNode node = new UseNode(0, options, next);
        Assert.assertEquals(options.get("option1"), node.getOption("option1"));
    }

    @Test
    public void TestUseNodeAssertValidOptions() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        Set<String> s = new HashSet<String>();
        for (int i = 0; i < 5; ++i) {
            options.put("option" + i, new VariableNode(i, "node" + i));
            s.add("option" + i);
        }
        UseNode node = new UseNode(0, options, next);
        node.assertValidOptions(s);
    }

    @Test(expected = ExpressionParserException.class)
    public void TestUseNodeAssertValidOptions2() {
        VariableNode next = new VariableNode(0, "next");
        Map<String, Node> options = new HashMap<String, Node>();
        Set<String> s = new HashSet<String>();
        for (int i = 0; i < 5; ++i) {
            options.put("option" + i, new VariableNode(i, "node" + i));
        }
        s.add("error");
        UseNode node = new UseNode(0, options, next);
        node.assertValidOptions(s);
    }
}
