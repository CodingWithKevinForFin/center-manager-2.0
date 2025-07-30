package com.f1.ami.web.diff;

import java.util.Collection;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public abstract class AmiWebJsonDiffNode<T> {
	final public static byte CHANGE_NONE = 2;

	final public static byte STATE_BASE = 1;
	final public static byte STATE_LEFT = 2;
	final public static byte STATE_RIGHT = 4;
	final public static byte STATE_MANUAL = 8;
	final public static byte STATE_MERGE = 16;
	final public static byte STATE_DISABLED = 32;
	final public static byte STATE_NOCHANGE = 64;

	final public static byte SAME_BL = 1;
	final public static byte SAME_BR = 2;
	final public static byte SAME_LR = 4;
	final public static byte SAME_AL = SAME_BL | SAME_BR | SAME_LR;

	final public static byte TYPE_MAP = 1;
	final public static byte TYPE_LST = 2;
	final public static byte TYPE_OBJ = 3;
	final public static byte TYPE_AMISCRIPT = 5;
	final private T orig, left, right;
	private byte userChoice;
	private Object customResult;
	private String customResultText;
	final private AmiWebJsonDiffNode<?> parent;
	private String key;
	private String label;
	private boolean hidden;
	private String path;
	private WebTreeNode treeNode;
	private AmiWebJsonDifferPortlet owner;

	public AmiWebJsonDiffNode(AmiWebJsonDifferPortlet owner, String key, AmiWebJsonDiffNode parent, T orig, T left, T right) {
		if (parent == null || "".equals(parent.getPath()))
			this.path = key;
		else
			this.path = parent.getPath() + '.' + key;
		this.owner = owner;
		this.key = key;
		this.parent = parent;
		this.orig = orig;
		this.left = left;
		this.right = right;
	}

	protected void setNode(WebTreeNode node) {
		this.treeNode = node;
	}

	protected WebTreeNode getNode() {
		return this.treeNode;
	}

	public String getPath() {
		return path;
	}

	public String getKey() {
		return key;
	}

	public AmiWebJsonDiffNode getParent() {
		return this.parent;
	}

	protected byte getUserChoiceMaterialized() {
		if (parent == null)
			return this.userChoice;
		byte r = parent.getUserChoiceMaterialized();
		if (r == STATE_MERGE)
			return this.userChoice;
		else
			return r;
	}

	protected abstract void addToTree(WebTreeNode root, boolean onlyChanges);

	abstract public byte getType();
	abstract public byte getSameness();
	abstract public byte getThisSameness();
	abstract protected T buildJsonFromChildren();

	protected T buildOutFromChildren() {
		return buildJsonFromChildren();
	}
	final public T getOrig() {
		return orig;
	}
	final public T getLeft() {
		return left;
	}
	final public T getRight() {
		return right;
	}
	final public Object getOut() {
		switch (userChoice) {
			case STATE_MANUAL:
				return this.customResult;
			case STATE_LEFT:
				return getLeft();
			case STATE_RIGHT:
				return getRight();
			case STATE_BASE:
				return getOrig();
			case STATE_MERGE:
				return this.buildOutFromChildren();
			case STATE_DISABLED:
				return null;
			case STATE_NOCHANGE:
				return this.getOrig();
			default:
				throw new IllegalStateException();
		}
	}
	//	final public T getOrigJson() {
	//		return orig;
	//	}
	//	final public T getLeftJson() {
	//		return left;
	//	}
	//	final public T getRightJson() {
	//		return right;
	//	}
	//	final public Object getOutJson() {
	//		switch (userChoice) {
	//			case STATE_MANUAL:
	//				return this.customResult;
	//			case STATE_LEFT:
	//				return getLeftJson();
	//			case STATE_RIGHT:
	//				return getRightJson();
	//			case STATE_BASE:
	//				return getOrigJson();
	//			case STATE_MERGE:
	//				return this.buildJsonFromChildren();
	//			case STATE_DISABLED:
	//				return null;
	//			case STATE_NOCHANGE:
	//				return getOrigJson();
	//			default:
	//				throw new IllegalStateException();
	//		}
	//	}

	public byte getUserChoice() {
		return this.userChoice;
	}
	public boolean setUserChoice(byte uc) {
		switch (uc) {
			case STATE_NOCHANGE:
				if (getSameness() != SAME_AL)
					return false;
				break;
			case STATE_MERGE:
				if (!canHaveChildren())
					return false;
				break;
			case STATE_BASE:
			case STATE_LEFT:
			case STATE_RIGHT:
				if (getSameness() == SAME_AL)
					uc = STATE_NOCHANGE;
				break;
		}
		this.userChoice = uc;
		if (this.treeNode != null)
			this.treeNode.getTreeManager().onNodeDataChanged(this.treeNode);
		return true;
	}

	protected abstract boolean canHaveChildren();

	public void setManualText(String value) {
		this.customResultText = value;
		try {
			this.customResult = ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(value);
		} catch (Exception e) {
			this.customResult = null;
		}
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String toString() {
		return this.getClass() + ":" + this.getPath();
	}

	abstract public Collection<AmiWebJsonDiffNode<?>> getChildren();
	static protected WebTreeNode createNode(String text, WebTreeNode root, AmiWebJsonDiffNode entry, boolean onlyChanges) {
		WebTreeNode node = root.getTreeManager().createNode(text, root, false, entry);
		entry.setNode(node);
		if (entry.getSameness() != SAME_AL) {
			if (entry.getThisSameness() == 0 && entry.getChildren().isEmpty())
				node.setIcon("portlet_icon_error");
			else if (entry.getSameness() == SAME_BL && entry.getOrig() == null)
				node.setIcon("portlet_icon_diff_left");
			else if (entry.getSameness() == SAME_BL && entry.getRight() == null)
				node.setIcon("portlet_icon_diff_right");
			else if (entry.getThisSameness() != SAME_AL)
				node.setIcon("portlet_icon_info");
			int cnt = 0;
			if (entry.getOrig() != null)
				cnt += 1;
			if (entry.getLeft() != null && entry.owner.showLeft())
				cnt += 1;
			if (entry.getRight() != null)
				cnt += 1;
			if (cnt > 1)
				node.setIsExpanded(true);
		} else
			node.setIcon("");
		entry.addToTree(node, onlyChanges);
		return node;
	}
	protected static byte getNullSameness(Object orig, Object left, Object right) {
		byte r = 0;
		if ((orig == null) == (left == null))
			r |= SAME_BL;
		if ((orig == null) == (left == null))
			r |= SAME_BR;
		if ((left == null) == (right == null))
			r |= SAME_LR;
		return r;
	}
}
