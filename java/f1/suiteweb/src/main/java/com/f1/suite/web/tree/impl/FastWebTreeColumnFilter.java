package com.f1.suite.web.tree.impl;

import java.util.Map;
import java.util.Map.Entry;

import com.f1.suite.web.tree.WebTreeAggregateNodeFormatter;
import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeGroupingNodeFormatter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.utils.OH;

public class FastWebTreeColumnFilter implements WebTreeFilter {

	final private WebTreeNodeFormatter[] formatters;
	final private WebTreeFilteredInFilter[] values;
	final private FastWebTree tree;

	public FastWebTreeColumnFilter(FastWebTree fastWebTree, Map<Integer, WebTreeFilteredInFilter> filteredIn) {
		this.tree = fastWebTree;
		this.formatters = new WebTreeNodeFormatter[filteredIn.size()];
		this.values = new WebTreeFilteredInFilter[filteredIn.size()];
		int pos = 0;
		for (Entry<Integer, WebTreeFilteredInFilter> entry : filteredIn.entrySet()) {
			final FastWebTreeColumn column = fastWebTree.getColumn(entry.getKey());
			this.formatters[pos] = column.getFormatter();
			this.values[pos] = entry.getValue();
			pos++;
		}
	}

	@Override
	public boolean shouldKeep(WebTreeNode node) {
		for (int i = 0; i < formatters.length; i++)
			if (!values[i].shouldKeep(node))
				return false;
		return true;
	}
	// runs shouldKeep for one of the filters
	public boolean shouldKeepAt(WebTreeNode node, int filterIndex) {
		OH.assertLt(filterIndex, formatters.length);
		OH.assertGe(filterIndex, 0);
		WebTreeNodeFormatter nf = formatters[filterIndex];
		if (nf instanceof WebTreeGroupingNodeFormatter) {
			// Do nothing
		} else if (nf instanceof WebTreeAggregateNodeFormatter) {
			if (!node.isLeaf())
				return false;
		}
		if (!values[filterIndex].shouldKeep(node))
			return false;
		return true;
	}
	// returns the number of filters
	public int size() {
		return formatters.length;
	}

}
