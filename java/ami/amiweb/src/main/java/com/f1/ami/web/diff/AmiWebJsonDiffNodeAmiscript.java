package com.f1.ami.web.diff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.diff.SequenceMerger;
import com.f1.utils.diff.SequenceMerger.MergeBlock;

public class AmiWebJsonDiffNodeAmiscript extends AmiWebJsonDiffNode<List<String>> {
	final private byte isSame;
	final private byte thisSameness;
	private ArrayList<AmiWebJsonDiffNode<?>> entries;

	public AmiWebJsonDiffNodeAmiscript(AmiWebJsonDifferPortlet owner, String key, AmiWebJsonDiffNode parent, List<String> orig, List<String> left, List<String> right) {
		super(owner, key, parent, orig, left, right);
		byte isSame = SAME_AL;
		int max = Math.max(orig.size(), Math.max(left.size(), right.size()));
		this.entries = new ArrayList<AmiWebJsonDiffNode<?>>(max);
		for (int i = 0; i < max; i++) {
			AmiWebJsonDiffNode node = owner.build("#", this, CH.getOr(orig, i, null), CH.getOr(left, i, null), CH.getOr(right, i, null));
			isSame &= node.getSameness();
			entries.add(node);
		}
		this.thisSameness = getNullSameness(orig, left, right);
		this.isSame = isSame;
	}

	@Override
	public byte getType() {
		return TYPE_AMISCRIPT;
	}

	@Override
	public byte getSameness() {
		return isSame;
	}
	@Override
	public byte getThisSameness() {
		return thisSameness;
	}

	@Override
	protected void addToTree(WebTreeNode root, boolean onlyChanges) {
		for (int i = 0; i < this.entries.size(); i++) {
			AmiWebJsonDiffNode entry = this.entries.get(i);
			createNode("[" + i + "]", root, entry, onlyChanges);
		}
	}

	private List<String> split(List<String> parts) {
		if (parts == null)
			return null;
		ArrayList<String> r = new ArrayList<String>();
		for (String i : parts) {
			for (String s : SH.splitToList("\n", i))
				r.add(s + "\n");
		}
		return r;

	}

	@Override
	protected List<String> buildJsonFromChildren() {
		List<String> r = new ArrayList<String>();
		for (AmiWebJsonDiffNode<?> i : this.entries) {
			String val = (String) i.getOut();
			if (val != null)
				for (String s : SH.splitLines(val))
					r.add(s);
		}
		for (int i = 0; i < r.size() - 1; i++)
			r.set(i, r.get(i) + '\n');
		return r;
	}
	protected boolean canHaveChildren() {
		return true;
	}

	@Override
	public Collection<AmiWebJsonDiffNode<?>> getChildren() {
		return this.entries;
	}

	static public AmiWebJsonDiffNodeAmiscript newDiffNodeAmiscript(AmiWebJsonDifferPortlet owner, String key, AmiWebJsonDiffNode parent, List<Object> org, List<Object> lft,
			List<Object> rgt) {
		String[] orgArray = org == null ? OH.EMPTY_STRING_ARRAY : AH.toArray((List) org, String.class);
		String[] lftArray = lft == null ? OH.EMPTY_STRING_ARRAY : AH.toArray((List) lft, String.class);
		String[] rgtArray = rgt == null ? OH.EMPTY_STRING_ARRAY : AH.toArray((List) rgt, String.class);
		SequenceMerger<String> sm = new SequenceMerger<String>(orgArray, lftArray, rgtArray);
		ArrayList<String> oBlocks = new ArrayList<String>();
		ArrayList<String> lBlocks = new ArrayList<String>();
		ArrayList<String> rBlocks = new ArrayList<String>();

		for (MergeBlock<String> i : sm.getBlocks()) {
			oBlocks.add(SH.join("", i.getOrigValues()));
			lBlocks.add(SH.join("", i.getLeftValues()));
			rBlocks.add(SH.join("", i.getRightValues()));
		}
		return new AmiWebJsonDiffNodeAmiscript(owner, key, parent, oBlocks, lBlocks, rBlocks);
	}

}
