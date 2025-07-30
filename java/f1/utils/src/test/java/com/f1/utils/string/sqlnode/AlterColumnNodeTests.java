package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

public class AlterColumnNodeTests {

    @Test
    public void TestAlterColumnNodeCtor() {
        AlterColumnNode node = new AlterColumnNode(0, 0, null, null, null, null, null, null);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestAlterColumnNodeGetPosition() {
        final int position = 999;
        AlterColumnNode node = new AlterColumnNode(position, 0, null, null, null, null, null, null);
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestAlterColumnNodeToString() {
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        AlterColumnNode node = new AlterColumnNode(0, SqlExpressionParser.ID_ADD, null, newName, newType, null, null,
                expression);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("ADD newName newType expression", sb.toString());
    }

    // NOTE: No null checks for colName and newName
    @Test
    public void TestAlterColumnNodeToString2() {
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        VariableNode colName = new VariableNode(3, "colName");
        AlterColumnNode node = new AlterColumnNode(0, SqlExpressionParser.ID_RENAME, colName, newName, newType, null,
                null,
                expression);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("RENAME colName TO newName", sb.toString());
    }

    @Test
    public void TestAlterColumnNodeToString3() {
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        VariableNode colName = new VariableNode(3, "colName");
        AlterColumnNode node = new AlterColumnNode(0, SqlExpressionParser.ID_DROP, colName, newName, newType, null,
                null,
                expression);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("DROP colName", sb.toString());
    }

    @Test
    public void TestAlterColumnNodeToString4() {
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        VariableNode colName = new VariableNode(3, "colName");
        AlterColumnNode node = new AlterColumnNode(0, SqlExpressionParser.ID_MODIFY, colName, newName, newType, null,
                null,
                expression);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("MODIFY colName AS newName newType", sb.toString());
    }

    // NOTE: Before missing whitespace
    @Test
    public void TestAlterColumnNodeToString5() {
        VariableNode before = new VariableNode(5, "before");
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        VariableNode colName = new VariableNode(3, "colName");
        AlterColumnNode node = new AlterColumnNode(0, -99999, colName, newName, newType, before,
                null,
                expression);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("UNKNOWN: -99999  BEFOREbefore", sb.toString());
    }

    @Test
    public void TestAlterColumnNodeToString6() {
        VariableNode before = new VariableNode(5, "before");
        VariableNode newName = new VariableNode(0, "newName");
        VariableNode newType = new VariableNode(1, "newType");
        VariableNode expression = new VariableNode(2, "expression");
        VariableNode colName = new VariableNode(3, "colName");
        AlterColumnNode node = new AlterColumnNode(0, -99999, colName, newName, newType, before,
                null,
                expression);
        Assert.assertFalse(node.toString().isEmpty());
    }

}
