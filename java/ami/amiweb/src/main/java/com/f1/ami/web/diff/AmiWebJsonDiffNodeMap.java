package com.f1.ami.web.diff;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.CH;
import com.f1.utils.structs.Tuple2;

public class AmiWebJsonDiffNodeMap extends AmiWebJsonDiffNode<Map> {

	final private Map<String, AmiWebJsonDiffNode<?>> entries;
	final private byte isSame;
	final private byte thisSameness;

	public AmiWebJsonDiffNodeMap(AmiWebJsonDifferPortlet owner, String parentKey, AmiWebJsonDiffNode parent, Map<String, Object> orig, Map<String, Object> left,
			Map<String, Object> right) {
		super(owner, parentKey, parent, orig, left, right);
		byte isSame = SAME_AL;
		boolean sameOl = left == null && orig == null;
		boolean sameOr = right == null && orig == null;
		boolean sameLr = left == null && right == null;
		this.thisSameness = getNullSameness(orig, left, right);

		if (left == null)
			left = Collections.EMPTY_MAP;
		if (right == null)
			right = Collections.EMPTY_MAP;
		if (orig == null)
			orig = Collections.EMPTY_MAP;
		entries = new TreeMap<String, AmiWebJsonDiffNode<?>>();
		Map<String, Tuple2<Object, Tuple2<Object, Object>>> joined = CH.join(orig, CH.join(left, right));
		for (String key : CH.sort(joined.keySet())) {
			Tuple2<Object, Tuple2<Object, Object>> value = joined.get(key);
			AmiWebJsonDiffNode<?> node = owner.build(key, this, value.getA(), value.getB().getA(), value.getB().getB());
			isSame &= node.getSameness();
			entries.put(key, node);
		}
		if (sameOl)
			isSame |= SAME_BL;
		if (sameOr)
			isSame |= SAME_BR;
		if (sameLr)
			isSame |= SAME_LR;
		this.isSame = isSame;

	}

	@Override
	public byte getType() {
		return TYPE_MAP;
	}

	@Override
	public byte getSameness() {
		return isSame;
	}

	@Override
	protected void addToTree(WebTreeNode root, boolean onlyChanges) {
		for (Entry<String, AmiWebJsonDiffNode<?>> entry : this.entries.entrySet()) {
			AmiWebJsonDiffNode dn = entry.getValue();
			if (onlyChanges && dn.getSameness() == SAME_AL)
				continue;
			WebTreeNode node;
			if (dn.isHidden()) {
				node = root;
				dn.addToTree(node, onlyChanges);
			} else {
				node = createNode(dn.getLabel(), root, dn, onlyChanges);
			}
		}
	}

	@Override
	public Map<String, Object> buildJsonFromChildren() {
		Map<String, Object> r = new HashMap<String, Object>();
		for (Entry<String, AmiWebJsonDiffNode<?>> i : this.entries.entrySet())
			r.put(i.getKey(), i.getValue().getOut());
		return r;
	}

	@Override
	protected boolean canHaveChildren() {
		return true;
	}

	@Override
	public Collection<AmiWebJsonDiffNode<?>> getChildren() {
		return this.entries.values();
	}

	@Override
	public byte getThisSameness() {
		return this.thisSameness;
	}
}
