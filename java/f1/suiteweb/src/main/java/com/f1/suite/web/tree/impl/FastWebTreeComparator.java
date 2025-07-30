package com.f1.suite.web.tree.impl;

import java.util.Comparator;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;

public class FastWebTreeComparator implements Comparator<WebTreeNode> {

	final private FastWebTreeColumn[] columns;
	final private boolean[] ascendings;
	final private WebTreeNodeFormatter[] formatters;

	public FastWebTreeComparator(FastWebTreeColumn[] columns, boolean[] ascendings) {
		this.columns = columns;
		this.ascendings = ascendings;
		this.formatters = new WebTreeNodeFormatter[this.columns.length];
		for (int i = 0; i < ascendings.length; i++)
			this.formatters[i] = columns[i].getFormatter();
	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		for (int i = 0; i < ascendings.length; i++) {
			int r = formatters[i].compare(o1, o2);
			if (r != 0)
				return r > 0 == ascendings[i] ? 1 : -1;
		}
		return 0;
	}

}
