package com.f1.ami.web.dm.portlets;

import java.util.List;

import com.f1.ami.web.graph.AmiWebGraphNode;

public interface AmiWebDmTreeListener {

	public void onDoubleClicked(List<AmiWebGraphNode<?>> nodes);

}
