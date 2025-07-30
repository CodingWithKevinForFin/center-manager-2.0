package com.vortex.web.diff;

import java.util.HashMap;
import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.structs.Tuple2;

public abstract class AbstractDiffableNode implements DiffableNode {

	private Tuple2<Byte, String> key;
	private Map<Tuple2<Byte, String>, DiffableNode> children = new HashMap<Tuple2<Byte, String>, DiffableNode>();

	public AbstractDiffableNode(byte diffType, String name) {
		this.key = new Tuple2<Byte, String>(diffType, name);
	}
	@Override
	public byte getDiffType() {
		return key.getA().byteValue();
	}

	public <T extends DiffableNode> T addChild(T value) {
		CH.putOrThrow(children, value.getDiffKey(), value);
		return value;
	}
	@Override
	public String getDiffName() {
		return key.getB();
	}

	@Override
	public Tuple2<Byte, String> getDiffKey() {
		return key;
	}

	@Override
	public Map<Tuple2<Byte, String>, DiffableNode> getDiffChildren() {
		return children;
	}

	@Override
	public DiffableNode getDiffChild(Tuple2<Byte, String> key) {
		return children.get(key);
	}

	@Override
	public String getIcon() {
		return "icon_file_node";
	}

}
