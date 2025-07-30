package com.f1.utils.string.sqlnode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class SqlCallNodeTests {

    @Test
    public void TestSqlCallNodeCtor() {
        final String methodName = "METHOD_NAME";
        List<Node> params = new ArrayList<Node>();
        Node next = new VariableNode(0, "next");
//        int position, String methodName, Node[] nodes, Node next
        SqlCallNode node = new SqlCallNode(0, methodName, params.toArray(new Node[params.size()]), next);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlCallNodeGetMethodName() {
        final String methodName = "METHOD_NAME";
        List<Node> params = new ArrayList<Node>();
        for (int i = 0; i < 5; ++i)
            params.add(new VariableNode(i, "param" + i));
        Node next = new VariableNode(0, "next");
        SqlCallNode node = new SqlCallNode(0, methodName, params.toArray(new Node[params.size()]), next);
        Assert.assertEquals(methodName, node.getMethodName());
    }

    @Test
    public void TestSqlCallNodeGetParams() {
        final String methodName = "METHOD_NAME";
        List<Node> params = new ArrayList<Node>();
        for (int i = 0; i < 5; ++i)
            params.add(new VariableNode(i, "param" + i));
        Node next = new VariableNode(0, "next");
        SqlCallNode node = new SqlCallNode(0, methodName, params.toArray(new Node[params.size()]), next);
        Assert.assertEquals(5, node.getParamsCount());
    }

    @Test
    public void TestSqlCallNodeToString() {
        final String methodName = "METHOD_NAME";
        List<Node> params = new ArrayList<Node>();
        for (int i = 0; i < 5; ++i)
            params.add(new VariableNode(i, "param" + i));
        Node next = new VariableNode(0, "next");
        SqlCallNode node = new SqlCallNode(0, methodName, params.toArray(new Node[params.size()]), next);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("METHOD_NAME(param0,param1,param2,param3,param4) next", sb.toString());
    }

    @Test
    public void TestSqlCallNodeToString2() {
        final String methodName = "METHOD_NAME";
        Node next = new VariableNode(0, "next");
        SqlCallNode node = new SqlCallNode(0, methodName, null, next);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("METHOD_NAME()", sb.toString());
    }

}
