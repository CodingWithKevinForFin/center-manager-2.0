package com.f1.suite.web.portal.impl.visual;

public interface GraphListener {

	void onSelectionChanged(GraphPortlet graphPortlet);
	void onContextMenu(GraphPortlet graphPortlet, String action);
	void onUserClick(GraphPortlet graphPortlet, GraphPortlet.Node nodeOrNull, int button, boolean ctrl, boolean shft);
	void onUserDblClick(GraphPortlet graphPortlet, Integer id);
	void onKeyDown(String keyCode, String ctrl);

}
