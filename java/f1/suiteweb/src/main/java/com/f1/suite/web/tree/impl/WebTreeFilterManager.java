package com.f1.suite.web.tree.impl;

import java.util.Map;

public interface WebTreeFilterManager {

	//	WebTreeFilter getFilter();
	//	void setFilter(WebTreeFilter filter);
	//	Set<String> getFilteredInValues(int columnId);
	//	void setFilteredIn(FastWebTree fastWebTree, int columnId, Set<String> filter);

	void showFilter(FastWebTree portlet, int columnIndex);

	String getSearch(FastWebTree portlet);

	void setSearch(FastWebTree portlet, String expression);
	void setFilterInColumn(FastWebTree portlet, int columnsIndex, String expression);

	Map<Integer, String> getFilteredInColumns(FastWebTree portlet);

}
