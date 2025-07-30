package com.f1.ami.web.graph;

public interface AmiWebGraphListener {

	void onAdded(AmiWebGraphNode<?> node);
	void onRemoved(AmiWebGraphNode<?> removed);
	void onIdChanged(AmiWebGraphNode<?> node, String oldId, String newId);
	void onInnerChanged(AmiWebGraphNode<?> node, Object old, Object nuw);
	void onEdgeAdded(byte type, AmiWebGraphNode<?> src, AmiWebGraphNode<?> tgt);
	void onEdgeRemoved(byte type, AmiWebGraphNode<?> src, AmiWebGraphNode<?> tgt);

}
