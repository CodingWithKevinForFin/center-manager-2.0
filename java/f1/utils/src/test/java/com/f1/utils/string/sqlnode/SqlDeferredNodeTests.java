package com.f1.utils.string.sqlnode;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.node.AppendNode;
import com.f1.utils.string.node.StringTemplateNode;

public class SqlDeferredNodeTests {

    @Test
    public void TestSqlDeferredNodeCtor() {
        AppendNode appendNode = new AppendNode(0, "append_text", 'c');
        StringTemplateNode templateNode = new StringTemplateNode(0, appendNode, false);
        SqlDeferredNode node = new SqlDeferredNode(templateNode);
        Assert.assertNotNull(node);
    }

    @Test
    public void TestSqlDeferredNodeToString() {
        AppendNode appendNode = new AppendNode(0, "append_text", 'c');
        StringTemplateNode templateNode = new StringTemplateNode(0, appendNode, false);
        SqlDeferredNode node = new SqlDeferredNode(templateNode);
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals("cappend_textc", sb.toString());
    }

    @Test
    public void TestSqlDeferredNodeGetPosition() {
        final int position = 10;
        AppendNode appendNode = new AppendNode(0, "append_text", 'c');
        StringTemplateNode templateNode = new StringTemplateNode(position, appendNode, false);
        SqlDeferredNode node = new SqlDeferredNode(templateNode);
        Assert.assertEquals(position, node.getPosition());
    }

    @Test
    public void TestSqlDeferredNodeGetInner() {
        AppendNode appendNode = new AppendNode(0, "append_text", 'c');
        StringTemplateNode templateNode = new StringTemplateNode(0, appendNode, false);
        SqlDeferredNode node = new SqlDeferredNode(templateNode);
        Assert.assertEquals(templateNode, node.getInner());
    }

}
