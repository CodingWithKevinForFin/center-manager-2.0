package com.f1.suite.web.tree.impl;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.utils.Formatter;
import com.f1.utils.OH;

public class BasicWebTreeNodeFormatter implements WebTreeNodeFormatter {

	private Formatter formatter;

	public BasicWebTreeNodeFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
		formatter.format(node.getName(), sink);
	}
	@Override
	public void formatToText(WebTreeNode node, StringBuilder sink) {
		formatter.format(node.getName(), sink);
	}

	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		return OH.compare(o1.getName(), o2.getName());
	}

	@Override
	public Object getValue(WebTreeNode node) {
		return node.getName();
	}

	@Override
	public Object getValueDisplay(WebTreeNode node) {
		return this.getValue(node);
	}

	@Override
	public String formatToText(Object data) {
		return OH.toString(data);
	}

}
