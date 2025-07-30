package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

public class SqlShowNodeTests {

    @Test
    public void TestSqlShowNodeCtor() {
        SqlShowNode node = new SqlShowNode(0, "target",
                0, true, new VariableNode(0, "name"),
                new VariableNode(0, "from"),
                new VariableNode(0, "next"));
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlShowNodeToString() {
        SqlShowNode node = new SqlShowNode(0, "target",
                0, true, new VariableNode(0, "name"),
                new VariableNode(0, "from"),
                new VariableNode(0, "next"));
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("SHOW FULL target name FROM from next", sb.toString());
    }
}
