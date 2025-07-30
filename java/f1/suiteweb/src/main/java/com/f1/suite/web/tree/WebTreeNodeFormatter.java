package com.f1.suite.web.tree;

import java.util.Comparator;

public interface WebTreeNodeFormatter extends Comparator<WebTreeNode> {

	void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style);
	void formatToText(WebTreeNode node, StringBuilder sink);
	@Override
	int compare(WebTreeNode o1, WebTreeNode o2);
	Object getValue(WebTreeNode node);
	Object getValueDisplay(WebTreeNode node);
	// TODO: add getValueDisplay(WebTreeNode node) from WebTreeNodeFormatterExt and remove that interface
	// Object getValueDisplay(WebTreeNode node);
	// this is for the WebTreeFilteredInFilter which ends up picking up the wrong value
	String formatToText(Object data);
}
