package com.vortex.web.portlet.trees;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.vortex.web.diff.DiffableNode;
import com.vortex.web.portlet.grids.VortexFileComparePortlet;

public class VortexWebDiffTreePortlet extends GridPortlet implements WebTreeContextMenuListener {

	private FastTreePortlet treePortlet;
	//private HtmlPortlet html1;
	//private HtmlPortlet html2;
	//private DividerPortlet div2;
	private DividerPortlet div;
	private boolean isDiff;
	private VortexFileComparePortlet diffPortlet;

	public VortexWebDiffTreePortlet(PortletConfig portletConfig, String leftTitle, DiffableNode left, String rightTitle, DiffableNode right) {
		super(portletConfig);

		this.isDiff = right != null;
		this.div = new DividerPortlet(generateConfig(), false);
		this.treePortlet = new FastTreePortlet(generateConfig());
		//this.html2 = new HtmlPortlet(generateConfig());
		//this.div2 = new DividerPortlet(generateConfig(), true);
		div.addChild(treePortlet);
		div.addChild(diffPortlet = new VortexFileComparePortlet(generateConfig()));

		String leftHtml = "<span class='purple'>" + leftTitle + "</span>";
		String rightHtml = "<span class='purple'>" + rightTitle + "</span>";

		diffPortlet.setTitles(leftHtml, rightHtml);

		//GridPortlet leftGrid = new GridPortlet(generateConfig());
		//this.html1 = new HtmlPortlet(generateConfig());

		//if (isDiff) {
		//leftGrid.addChild(new HtmlPortlet(generateConfig(), "<span class='purple'>" + leftTitle + "</span>", "diff_title"), 0, 0);
		//leftGrid.addChild(html1, 0, 1);
		//leftGrid.setRowSize(0, 20);

		//GridPortlet rightGrid = new GridPortlet(generateConfig());
		//rightGrid.addChild(new HtmlPortlet(generateConfig(), "<span class='green'>" + rightTitle + "</span>", "diff_title"), 0, 0);
		//rightGrid.setRowSize(0, 20);
		//rightGrid.addChild(html2, 0, 1);

		//div.addChild(div2);
		//div2.addChild(leftGrid);
		//div2.addChild(rightGrid);
		//} else {
		//div.addChild(html1);
		//}
		this.treePortlet.getTree().addMenuContextListener(this);
		this.treePortlet.getTree().setRootLevelVisible(false);
		this.treePortlet.getTree().setAutoExpandUntilMultipleNodes(false);
		addChild(div, 0, 0);
		addNode(this.treePortlet.getTreeManager().getRoot(), left, right, true);
	}
	//returns true if left and right match
	private boolean addNode(WebTreeNode parent, DiffableNode left, DiffableNode right, boolean isTop) {
		WebTreeNode node;
		boolean r = true;
		if (left == null && right == null) {
			return true;
		} else if (left == null) {
			node = this.treePortlet.getTreeManager().createNode(describe(right, false), parent, false).setCssClass("green");
			for (DiffableNode c : right.getDiffChildren().values())
				addNode(node, null, c, false);
			r = false;
			node.setIcon("portlet_icon_diff_left");
		} else if (right == null) {
			if (isDiff) {
				node = this.treePortlet.getTreeManager().createNode(describe(left, false), parent, false).setCssClass("purple");
				for (DiffableNode c : left.getDiffChildren().values())
					addNode(node, c, null, false);
				r = false;
				node.setIcon("portlet_icon_diff_right");
			} else {
				node = this.treePortlet.getTreeManager().createNode(describe(left, false), parent, false);
				for (DiffableNode c : left.getDiffChildren().values())
					addNode(node, c, null, false);
				r = false;
				String icon = left.getIcon();
				node.setIcon(OH.noNull(icon, "portlet_icon_file"));
			}
		} else if (OH.eq(right.getDiffName(), left.getDiffName()) || isTop) {
			if (isTop && OH.ne(left.getDiffName(), right.getDiffName())) {
				node = this.treePortlet.getTreeManager().createNode(describe(left, true) + " vs. " + describe(right, true), parent, false);
			} else {
				node = this.treePortlet.getTreeManager().createNode(describe(left, true), parent, false);
			}
			boolean dataMatch = true;
			try {
				if (!right.isEqualToNode(left)) {
					dataMatch = false;
					node.setCssClass("red");
					r = false;
				}
			} catch (Exception e) {
				//dataMatch = false;
				node.setCssClass("red italics");
				//r = false;
			}
			Set<Tuple2<Byte, String>> names = new TreeSet<Tuple2<Byte, String>>();
			names.addAll(left.getDiffChildren().keySet());
			names.addAll(right.getDiffChildren().keySet());
			for (Tuple2<Byte, String> name : names)
				if (!addNode(node, left.getDiffChild(name), right.getDiffChild(name), false))
					r = false;
			if (!r && dataMatch) {
				node.setIsExpanded(true);
				node.setCssClass("red");
			}
			if (r) {
				node.setIcon("portlet_icon_okay");
			} else
				node.setIcon("portlet_icon_warning");
		} else
			throw new IllegalStateException("names are different: " + right.getDiffName() + " != " + left.getDiffName());
		node.setData(new Tuple2<DiffableNode, DiffableNode>(left, right));
		return r;
	}
	private String describe(DiffableNode right, boolean includeChechsum) {
		//if (right.getData().length > 0)
		//return right.getName() + "  (" + right.getTypeAsString() + ", " + right.getData().length + " bytes)";
		//else
		return right.getDiffName();
	}
	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		List<WebTreeNode> selected = fastWebTree.getSelected();
		if (selected.size() == 1) {
			Tuple2<DiffableNode, DiffableNode> nodes = (Tuple2<DiffableNode, DiffableNode>) selected.get(0).getData();
			this.diffPortlet.setText(nodes.getA() == null ? null : nodes.getA().getContents(), nodes.getB() == null ? null : nodes.getB().getContents());
		}
	}

	static private String toHtml(String contents) {
		if (contents.length() > 1024 * 1024 * 2)
			return "<i>File to large: " + SH.formatMemory(contents.length()) + " </i>";
		return "<pre>" + WebHelper.formatForPre(contents.getBytes()) + "</pre>";
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
		// TODO Auto-generated method stub

	}
}
