package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.VariableNode;

//NOTE: No toString defined
public class SqlUnionNodeTests {

    @Test
    public void TestSqlUnionNodeCtor() {
        SqlUnionNode node = new SqlUnionNode(0, new VariableNode(0, "next"), false);
        Assert.assertNotNull(node);
    }

}
