package com.f1.utils.string.sqlnode;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.string.node.VariableNode;

public class InsertNodeTests {

    @Test
    public void TestInsertNodeCtor() {
        InsertNode node = new InsertNode(0, 0, 0, null, 0, null, null, null, null, null, false, false);
        Assert.assertNotNull(node);
    }

    // NOTE: InsertNode has no toString() method like other nodes
    @Test
    public void TestInsertNodeToString() {

        final String output = "SELECT AND SELECT INTO tablename (columns0,columns1,columns2,columns3,columns4) ON (syncOns0,syncOns1,syncOns2,syncOns3,syncOns4) FOR varname = start TO end STEP step next VALUES FROM BYNAME nextLIMIT columns0,columns1,columns2,columns3,columns4 next";
        ArrayList<Node> syncOns = new ArrayList<Node>();
        ArrayList<Node> columns = new ArrayList<Node>();

        SqlForNode forNode = new SqlForNode(0,
                new VariableNode(0, "varname"),
                new VariableNode(0, "start"),
                new VariableNode(0, "end"),
                new VariableNode(0, "step"),
                new VariableNode(0, "next"));

        for (int i = 0; i < 5; ++i) {
            syncOns.add(new VariableNode(i, "syncOns" + i));
            columns.add(new VariableNode(i, "columns" + i));
        }

        Node[] columnArr = new Node[columns.size()];
        columnArr = columns.toArray(columnArr);

        Node[] syncOnsArr = new Node[syncOns.size()];
        syncOnsArr = syncOns.toArray(syncOnsArr);

        SqlColumnsNode limitNode = new SqlColumnsNode(0, columnArr,
                new VariableNode(0, "next"), SqlExpressionParser.ID_LIMIT);

        InsertNode node = new InsertNode(0, SqlExpressionParser.ID_SELECT, 0,
                "tablename", 0, columnArr, forNode, null,
                syncOnsArr, limitNode, true, true);

        node.setNext(new VariableNode(0, "next"));
        StringBuilder sb = new StringBuilder();
        node.toString(sb);
        Assert.assertEquals(output, sb.toString());
    }

}
