package com.f1.ami.web.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;

public class AmiWebJsonDiffNodeList extends AmiWebJsonDiffNode<List> implements Comparator<Object> {
	final private List<AmiWebJsonDiffNode<?>> entries;
	final private List<String> keys;
	final private byte sameness;
	final private String sortingKey;
	final private byte thisSameness;

	public AmiWebJsonDiffNodeList(AmiWebJsonDifferPortlet owner, String parentKey, AmiWebJsonDiffNode parent, List<Object> orig, List<Object> left, List<Object> right,
			String sortingKey) {
		super(owner, parentKey, parent, orig, left, right);
		this.thisSameness = getNullSameness(orig, left, right);
		if (orig == null)
			orig = Collections.EMPTY_LIST;
		if (left == null)
			left = Collections.EMPTY_LIST;
		if (right == null)
			right = Collections.EMPTY_LIST;
		this.sortingKey = sortingKey;
		if (sortingKey != null) {
			BasicMultiMap.List<String, Object> origByKey = new BasicMultiMap.List<String, Object>(new TreeMap<String, List<Object>>());
			BasicMultiMap.List<String, Object> leftByKey = new BasicMultiMap.List<String, Object>(new TreeMap<String, List<Object>>());
			BasicMultiMap.List<String, Object> rghtByKey = new BasicMultiMap.List<String, Object>(new TreeMap<String, List<Object>>());
			for (Object o : orig)
				origByKey.putMulti((String) RootAssister.INSTANCE.getNestedValue(o, sortingKey, true), o);
			for (Object o : left)
				leftByKey.putMulti((String) RootAssister.INSTANCE.getNestedValue(o, sortingKey, true), o);
			for (Object o : right)
				rghtByKey.putMulti((String) RootAssister.INSTANCE.getNestedValue(o, sortingKey, true), o);

			Map<String, Tuple2<List<Object>, Tuple2<List<Object>, List<Object>>>> joined = CH.join(origByKey, CH.join(leftByKey, rghtByKey));
			this.entries = new ArrayList<AmiWebJsonDiffNode<?>>(joined.size());
			this.keys = new ArrayList<String>(joined.size());
			byte isSame = SAME_AL;
			for (String key : CH.sort(joined.keySet())) {
				Tuple2<List<Object>, Tuple2<List<Object>, List<Object>>> value = joined.get(key);
				List oList = value.getA();
				List lList = value.getB().getA();
				List rList = value.getB().getB();
				int size = MH.max(CH.size(oList), CH.size(lList), CH.size(rList));
				for (int i = 0; i < size; i++) {
					AmiWebJsonDiffNode<?> node = owner.build("#", this, CH.getOr(oList, i, null), CH.getOr(lList, i, null), CH.getOr(rList, i, null));
					isSame &= node.getSameness();
					entries.add(node);
					keys.add(key);
				}
			}
			this.sameness = isSame;
		} else {
			this.keys = null;
			int max = Math.max(orig.size(), Math.max(left.size(), right.size()));
			this.entries = new ArrayList<AmiWebJsonDiffNode<?>>(max);
			byte isSame = SAME_AL;
			for (int i = 0; i < max; i++) {
				AmiWebJsonDiffNode node = owner.build("#", this, CH.getOr(orig, i, null), CH.getOr(left, i, null), CH.getOr(right, i, null));
				isSame &= node.getSameness();
				entries.add(node);
			}
			this.sameness = isSame;
		}
	}

	@Override
	public byte getType() {
		return TYPE_MAP;
	}

	@Override
	public byte getSameness() {
		return sameness;
	}

	@Override
	protected void addToTree(WebTreeNode root, boolean onlyChanges) {
		for (int i = 0; i < this.entries.size(); i++) {
			AmiWebJsonDiffNode entry = this.entries.get(i);
			if (onlyChanges && entry.getSameness() == SAME_AL)
				continue;
			createNode(keys == null ? "[" + i + "]" : ("<i>" + keys.get(i)), root, entry, onlyChanges);
		}
	}

	@Override
	public List<Object> buildJsonFromChildren() {
		List<Object> r = new ArrayList<Object>();
		for (AmiWebJsonDiffNode<?> i : this.entries) {
			Object val = i.getOut();
			if (val != null)
				r.add(val);
		}
		if (this.sortingKey != null) {
			Collections.sort(r, this);
		}
		return r;
	}
	@Override
	protected boolean canHaveChildren() {
		return true;
	}

	@Override
	public Collection<AmiWebJsonDiffNode<?>> getChildren() {
		return this.entries;
	}

	@Override
	public int compare(Object o1, Object o2) {
		Comparable key1 = (Comparable) RootAssister.INSTANCE.getNestedValue(o1, sortingKey, true);
		Comparable key2 = (Comparable) RootAssister.INSTANCE.getNestedValue(o2, sortingKey, true);
		return OH.compare(key1, key2);
	}
	@Override
	public byte getThisSameness() {
		return this.thisSameness;
	}
}
