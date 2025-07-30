package com.f1.suite.web.portal.impl.visual;

import com.f1.suite.web.portal.impl.visual.GraphPortlet.Edge;
import com.f1.suite.web.portal.impl.visual.GraphPortlet.Node;

public interface GraphMovementListener {

	void onNodeAdded(GraphPortlet graphPortlet, Node node);
	void onNodeUpdated(GraphPortlet graphPortlet, Node node);
	void onNodeRemoved(GraphPortlet graphPortlet, Node node);
	void onNodesCleared();

	void onEdgeAdded(GraphPortlet graphPortlet, Edge node);
	void onEdgeUpdated(GraphPortlet graphPortlet, Edge node);
	void onEdgeRemoved(GraphPortlet graphPortlet, Edge node);
	void onNodeMoved(GraphPortlet graphPortlet, int oldX, int oldY, Node node);
	void onEdgesCleared();
}
