package com.f1.utils.string.sqlnode;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.string.node.VariableNode;

public class AdminNodeTests {

    @Test
    public void TestAdminNodeCtor() {
        AdminNode node = new AdminNode(0, 0, 0, null, null);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestAdminNodeCtor2() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestAdminNodeGetOptions() {
        SqlNode options = new SqlNode(0, null, 0);
        AdminNode node = new AdminNode(0, 0, 0, null, null, options);
        Assert.assertEquals(options, node.getOptions());
    }

    @Test
    public void TestAdminNodeGetSetIfType() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.NO_IF);
        Assert.assertEquals(AdminNode.NO_IF, node.getIfType());
        node.setIfCondition(AdminNode.IF_EXISTS);
        Assert.assertEquals(AdminNode.IF_EXISTS, node.getIfType());
        node.setIfCondition(AdminNode.IF_NOT_EXISTS);
        Assert.assertEquals(AdminNode.IF_NOT_EXISTS, node.getIfType());
    }

    @Test()
    public void TestAdminNodeGetIfExistsOfThrow() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.NO_IF);
        Assert.assertEquals(false, node.getIfExistsOrThrow());
    }

    @Test()
    public void TestAdminNodeGetIfExistsOfThrow2() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_EXISTS);
        Assert.assertEquals(true, node.getIfExistsOrThrow());
    }

    @Test(expected = ExpressionParserException.class)
    public void TestAdminNodeGetIfExistsOfThrow3() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_NOT_EXISTS);
        node.getIfExistsOrThrow();
    }

    @Test
    public void TestAdminNodeGetIfNotExistsOfThrow() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.NO_IF);
        Assert.assertEquals(false, node.getIfNotExistsOrThrow());
    }

    @Test
    public void TestAdminNodeGetIfNotExistsOfThrow2() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_NOT_EXISTS);
        Assert.assertEquals(true, node.getIfNotExistsOrThrow());
    }

    @Test(expected = ExpressionParserException.class)
    public void TestAdminNodeGetIfNotExistsOfThrow3() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_EXISTS);
        node.getIfNotExistsOrThrow();
    }

    @Test
    public void TestAdminNodeToString() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_EXISTS);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("UNKNOWN: 0 UNKNOWN: 0 IF EXISTS", sb.toString());
        node.setIfCondition(AdminNode.IF_NOT_EXISTS);
    }

    @Test
    public void TestAdminNodeToString2() {
        AdminNode node = new AdminNode(0, 0, 0, null, null, null);
        node.setIfCondition(AdminNode.IF_NOT_EXISTS);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("UNKNOWN: 0 UNKNOWN: 0 IF NOT EXISTS", sb.toString());
    }
}
