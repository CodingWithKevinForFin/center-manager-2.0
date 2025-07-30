package com.f1.ami.web.centermanager.graph.nodes;

public interface AmiCenterGraphNode {
	public byte TYPE_CENTER = 0;
	public byte TYPE_TABLE = 1;
	public byte TYPE_TRIGGER = 2;
	public byte TYPE_TIMER = 3;
	public byte TYPE_PROCEDURE = 4;
	public byte TYPE_METHOD = 5;
	public byte TYPE_DBO = 6;
	public byte TYPE_INDEX = 7;

	public String getLabel();
	public long getUid();
	public byte getType();
	public boolean isReadonly();
}
