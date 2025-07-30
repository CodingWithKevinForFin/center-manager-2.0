package com.f1.ami.web.graph;

public class AmiWebGraphListenerDebug implements AmiWebGraphListener {

	@Override
	public void onAdded(AmiWebGraphNode node) {
		System.out.println("NODE_ADDED " + format(node));
	}
	@Override
	public void onRemoved(AmiWebGraphNode node) {
		System.out.println("NODE_REMOV " + format(node));
	}

	@Override
	public void onIdChanged(AmiWebGraphNode node, String oldId, String newId) {
		System.out.println("ID_CHANGED " + format(node) + ": " + oldId + " ==> " + newId);
	}

	@Override
	public void onInnerChanged(AmiWebGraphNode node, Object old, Object nuw) {
		System.out.println("INNER_CHGD " + format(node) + ": " + old + " ==> " + nuw);
	}

	@Override
	public void onEdgeAdded(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		System.out.println("EDGE_ADDED " + AmiWebGraphManager.formatEdgeType(type) + " " + format(src) + " ==> " + format(tgt));

	}

	@Override
	public void onEdgeRemoved(byte type, AmiWebGraphNode src, AmiWebGraphNode tgt) {
		System.out.println("EDGE_REMOV " + AmiWebGraphManager.formatEdgeType(type) + " " + format(src) + " ==> " + format(tgt));

	}

	static private String format(AmiWebGraphNode node) {
		return AmiWebGraphManager.formatType(node.getType()) + "[" + node.getId() + "]";
	}
}
