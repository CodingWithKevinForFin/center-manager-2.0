package com.f1.ami.web.centermanager.graph.nodes;

import com.f1.ami.web.centermanager.AmiWebCenterGraphManager;

public class AmiCenterGraphNode_Center extends AmiCenterGraphAbstractNode {
	public static final byte CODE = AmiCenterGraphNode.TYPE_CENTER;
	private byte status = -1;
	public static final byte CONNECTED = 1;
	public static final byte DISCONNECTED = 0;

	public AmiCenterGraphNode_Center(AmiWebCenterGraphManager manager, long uid, String label, byte status) {
		super(manager, uid, label);
		this.status = status;
	}

	public AmiCenterGraphNode_Center(AmiWebCenterGraphManager manager, long uid, String label, byte status, boolean readOnly) {
		this(manager, uid, label, status);
		this.readOnly = readOnly;
	}

	@Override
	public byte getType() {
		return CODE;
	}
}
