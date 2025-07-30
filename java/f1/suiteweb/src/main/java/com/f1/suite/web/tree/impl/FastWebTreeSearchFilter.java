package com.f1.suite.web.tree.impl;

import com.f1.suite.web.tree.WebTreeFilter;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.utils.TextMatcher;

public class FastWebTreeSearchFilter implements WebTreeFilter {

	final private TextMatcher search;
	final private FastWebTree tree;
	private WebTreeNodeFormatter formatter;

	public FastWebTreeSearchFilter(FastWebTree fastWebTree, TextMatcher search) {
		this.tree = fastWebTree;
		this.formatter = tree.getFormatter();
		this.search = search;
	}

	private StringBuilder buf = new StringBuilder();

	@Override
	public boolean shouldKeep(WebTreeNode node) {
		buf.setLength(0);
		formatter.formatToText(node, buf);
		if (node.isLeaf())
			for (int i = 0; i < tree.getVisibleColumnsCount(); i++) {
				tree.getVisibleColumn(i + 1).getFormatter().formatToText(node, buf.append('\t'));
			}
		boolean r = search.matches(buf);
		buf.setLength(0);
		return r;
	}

}
