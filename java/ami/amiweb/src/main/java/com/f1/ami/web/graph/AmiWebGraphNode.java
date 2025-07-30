package com.f1.ami.web.graph;

public interface AmiWebGraphNode<T> {
	byte EDGE_SOURCE_DATASOURCE = 1;
	byte EDGE_SOURCE_DATAMODEL = 2;
	byte EDGE_TARGET_DATAMODEL = 3;
	byte EDGE_TARGET_PANEL = 4;
	byte EDGE_SOURCE_PANEL = 5;
	byte EDGE_TARGET_LINK = 6;
	byte EDGE_SOURCE_LINK = 7;
	byte EDGE_PARENT_PANEL = 8;
	byte EDGE_CHILD_PANEL = 9;
	byte EDGE_TARGET_FILTER_DATAMODEL = 10;
	byte EDGE_SOURCE_FILTER_PANEL = 11;
	byte EDGE_SOURCE_REALTIME = 12;
	byte EDGE_TARGET_REALTIME = 13;

	public byte TYPE_PANEL = 1;
	public byte TYPE_LINK = 2;
	public byte TYPE_DATASOURCE = 3;
	public byte TYPE_DATAMODEL = 4;
	public byte TYPE_FEED = 5;
	public byte TYPE_PROCESSOR = 6;

	public long getUid();
	public byte getType();

	public String getId();
	public AmiWebGraphManager getManager();
	void setInner(T object);
	public T getInner();
	public String getLabel();
	public String getDescription();

	public String getRealtimeId();//Returns null if not realtime
	public boolean isTransient();

}
