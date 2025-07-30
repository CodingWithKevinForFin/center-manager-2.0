package com.f1.suite.web.portal.impl.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.utils.EmptyIterable;
import com.f1.utils.OH;

//Make TreemapNode implement Comparable interface so that we can compare treemapNode on node.getv
public class TreemapNode implements Comparable<TreemapNode> {
	final private int id;
	final private TreemapPortlet treemap;
	final private int depth;
	final private String groupId;
	final private TreemapNode parent;

	private Map<String, TreemapNode> nodes = null;
	private double value;
	private boolean isSelected;
	private String tooltip;
	private String label;
	private String bgColor;
	private String borderColor = null;
	private String textColor;
	private Object correlationData;
	private double heat;

	protected TreemapNode(TreemapPortlet parent, int id) {
		this.treemap = parent;
		this.groupId = null;
		this.id = id;
		this.parent = null;
		this.depth = -1;
	}
	public TreemapNode(TreemapNode parent, int id, String groupId, double value, double heat, String bgColor, String textColor, String label, String tooltip) {
		this.groupId = groupId;
		this.id = id;
		this.value = value;
		this.heat = heat;
		this.label = label;
		this.bgColor = bgColor;
		this.textColor = textColor;
		this.parent = parent;
		this.tooltip = tooltip;
		this.treemap = parent.getTreemap();
		treemap.onHeatChanged(this, Double.NaN, heat);
		this.depth = parent.getDepth() + 1;
	}
	public int getDepth() {
		return depth;
	}
	public String getGroupId() {
		return groupId;
	}
	public int getId() {
		return id;
	}
	public double getValue() {
		return value;
	}
	public String getBgColor() {
		return bgColor;
	}
	public double getHeat() {
		return heat;
	}
	public String getTextColor() {
		return textColor;
	}
	public TreemapNode getParent() {
		return parent;
	}
	public void setLabel(String label) {
		if (OH.eq(this.label, label))
			return;
		String old = this.label;
		this.label = label;
		treemap.onLabelChanged(this, old, label);
	}

	public String getLabel() {
		return this.label;
	}
	public void setValue(double value) {
		if (value == this.value)
			return;
		double old = this.value;
		this.value = value;
		this.treemap.onValueChanged(this, old, value);
	}
	public void setHeat(double heat) {
		if (OH.eq(heat, this.heat))
			return;
		double old = this.heat;
		this.heat = heat;
		treemap.onHeatChanged(this, old, heat);
	}
	public void setBgColor(String bgColor) {
		if (OH.eq(bgColor, this.bgColor))
			return;
		String old = this.bgColor;
		this.bgColor = bgColor;
		treemap.onBgColorChanged(this, old, bgColor);
	}
	public void setTextColor(String textColor) {
		if (OH.eq(textColor, this.textColor))
			return;
		String old = this.textColor;
		this.textColor = textColor;
		treemap.onTextColorChanged(this, old, textColor);
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		if (isSelected == this.isSelected)
			return;
		this.isSelected = isSelected;
		treemap.onSelectChanged(this, true);
	}
	public void setSelectedNoFire(boolean isSelected) {
		if (isSelected == this.isSelected)
			return;
		this.isSelected = isSelected;
		treemap.onSelectChanged(this, false);
	}
	public Object getCorrelationData() {
		return correlationData;
	}
	public void setCorrelationData(Object correlationData) {
		this.correlationData = correlationData;
	}
	public String getTooltip() {
		return tooltip;
	}
	public void setTooltip(String tooltip) {
		if (OH.eq(tooltip, this.tooltip))
			return;

		String old = this.tooltip;
		this.tooltip = tooltip;
		treemap.onTooltipChanged(this, old, tooltip);
	}

	public TreemapPortlet getTreemap() {
		return treemap;
	}

	protected TreemapNode removeChild(String groupId) {
		if (this.nodes == null)
			return null;
		TreemapNode r = this.nodes.remove(groupId);
		return r;
	}

	public Iterable<TreemapNode> getChildren() {
		return this.nodes == null ? EmptyIterable.INSTANCE : this.nodes.values();
	}

	protected void addNode(TreemapNode node) {
		if (nodes == null)
			this.nodes = new HashMap<String, TreemapNode>();
		TreemapNode existing = this.nodes.put(node.getGroupId(), node);
		if (existing != null) {
			this.nodes.put(existing.getGroupId(), existing);
			throw new RuntimeException(getGroupPath(new StringBuilder("Already exists: ")).append("-->").append(node.getGroupId()).toString());
		}
	}

	public boolean isRoot() {
		return this.parent == null;
	}
	private StringBuilder getGroupPath(StringBuilder sink) {
		if (isRoot())
			return sink;
		if (!parent.isRoot())
			parent.getGroupPath(sink).append("-->");
		sink.append(this.groupId);
		return sink;
	}
	public boolean isEmpty() {
		return nodes == null || nodes.size() == 0;
	}
	public double incrementValue(double value2) {
		setValue(this.value + value2);
		return this.value;
	}
	protected void clearChildren() {
		if (this.nodes != null)
			this.nodes.clear();
	}
	public TreemapNode getChild(String name) {
		return this.nodes == null ? null : this.nodes.get(name);
	}

	//to do, add compareTo()
	@Override
	public int compareTo(TreemapNode o) {
		return Double.compare(this.getValue(), o.getValue());
	}
	//to do, add sorted children
	public List<TreemapNode> childrenSorted() {
		Collection<TreemapNode> c = nodes.values();
		List<TreemapNode> sortedChildren = new ArrayList<TreemapNode>();
		sortedChildren.addAll(c);
		Collections.sort(sortedChildren);
		Collections.reverse(sortedChildren);
		return sortedChildren;

	}

	//to do,add sumChildrenValue()
	public double sumChildrenValue() {
		Collection<TreemapNode> children = nodes.values();
		double totalChildrenSize = 0;
		for (TreemapNode child : children) {
			totalChildrenSize += child.getValue();
		}
		return totalChildrenSize;
	}

	//to do, add fields: [x,y,w,h,innerX,innerY,innerW,innerH]
	private int x = -1, y = -1, w = -1, h = -1, innerX = -1, innerY = -1, innerW = -1, innerH = -1;

	//getter & setter
	public int getInnerX() {
		return innerX;
	}
	public void setInnerX(int innerX) {
		this.innerX = innerX;
	}

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getW() {
		return w;
	}
	public void setW(int w) {
		this.w = w;
	}
	public int getH() {
		return h;
	}
	public void setH(int h) {
		this.h = h;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getInnerY() {
		return innerY;
	}
	public void setInnerY(int innerY) {
		this.innerY = innerY;
	}
	public int getInnerW() {
		return innerW;
	}
	public void setInnerW(int innerW) {
		this.innerW = innerW;
	}
	public int getInnerH() {
		return innerH;
	}
	public void setInnerH(int innerH) {
		this.innerH = innerH;
	}

	//to do, add childrenSticky getter and setter
	private HashMap<Integer, HashMap<String, Number>> childrenSticky;

	public HashMap<Integer, HashMap<String, Number>> getChildrenSticky() {
		return childrenSticky;
	}

	public void setChildrenSticky(HashMap<Integer, HashMap<String, Number>> m) {
		this.childrenSticky = m;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
}
