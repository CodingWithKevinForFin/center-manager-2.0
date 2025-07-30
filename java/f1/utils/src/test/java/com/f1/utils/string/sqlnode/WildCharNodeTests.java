package com.f1.utils.string.sqlnode;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class WildCharNodeTests {

    @Test
    public void TestWildCharNodeCtor() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestWildCharNodeToString() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("tableName.*  EXCEPT except0 except1 except2 except3 except4", sb.toString());
    }

    @Test
    public void TestWildCharNodeToString2() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertFalse(node.toString().isEmpty());
    }

    @Test
    public void TestWildCharNodeToString3() {
        final int position = 999;
        VariableNode tableName = null;
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("\"*\" EXCEPT except0 except1 except2 except3 except4", sb.toString());
    }

    @Test
    public void TestWildCharNodeGetPosition() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestWildCharNodeGetTableName() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertEquals(tableName.getVarname(), node.getTableName());
    }

    @Test
    public void TestWildCharNodeGetTableName2() {
        final int position = 999;
        VariableNode tableName = null;
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertNull(node.getTableName());
    }

    @Test
    public void TestWildCharNodeGetExcepts() {
        final int position = 999;
        VariableNode tableName = new VariableNode(0, "tableName");
        List<VariableNode> exceptList = new ArrayList<VariableNode>();
        for (int i = 0; i < 5; ++i)
            exceptList.add(new VariableNode(i, "except" + i));
        WildCharNode node = new WildCharNode(position, tableName, exceptList);
        Assert.assertEquals(exceptList, node.getExcepts());
    }
}
