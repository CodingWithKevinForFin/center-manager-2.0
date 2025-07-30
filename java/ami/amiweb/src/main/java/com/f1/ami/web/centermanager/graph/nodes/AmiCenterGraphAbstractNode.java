package com.f1.ami.web.centermanager.graph.nodes;

import com.f1.ami.web.centermanager.graph.AmiWebCenterGraphManager;
import com.f1.ami.web.centermanager.utils.AmiCenterManagerUtils;

public class AmiCenterGraphAbstractNode implements AmiCenterGraphNode {
	protected String label;
	protected boolean readOnly = false;
	final private AmiWebCenterGraphManager manager;
	final private long uid;
	private byte type;

	public AmiCenterGraphAbstractNode(AmiWebCenterGraphManager manager, long uid, String label) {
		this.manager = manager;
		this.uid = uid;
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public long getUid() {
		return this.uid;
	}

	@Override
	public byte getType() {
		return type;
	}

	@Override
	public boolean isReadonly() {
		return false;
	}

	public void setReadonly(boolean readonly) {
		this.readOnly = readonly;
	}

	@Override
	public String toString() {
		return AmiCenterManagerUtils.toCenterObjectString(getType(), true) + "::" + this.label;
	}

}
