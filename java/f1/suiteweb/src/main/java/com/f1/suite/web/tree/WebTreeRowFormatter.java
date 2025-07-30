package com.f1.suite.web.tree;

import java.util.Comparator;

public interface WebTreeRowFormatter extends Comparator<WebTreeNode> {

	void format(WebTreeNode node, StringBuilder style);

}
