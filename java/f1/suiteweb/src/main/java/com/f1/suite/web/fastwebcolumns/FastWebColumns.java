package com.f1.suite.web.fastwebcolumns;

import com.f1.suite.web.tree.impl.FastWebColumn;

/**
 * interface for FastTable, FastWebTree, AmiWebVizwiz_TreeGrid etc
 * 
 */
public interface FastWebColumns extends FastWebColumnsEvents {

	int getVisibleColumnsCount();

	FastWebColumn getVisibleColumn(int i);

	int getHiddenColumnsCount();

	FastWebColumn getHiddenColumn(int i);

	void hideColumn(String columnId);

	void showColumn(String columnId, int location);

	public int getPinnedColumnsCount();

	public void setPinnedColumnsCount(int count);

	int getColumnPosition(Object columnId);
	FastWebColumn getFastWebColumn(Object columnId);

	void snapToColumn(Object columnId);
	public int getVisibleColumnsLimit();
	public void setVisibleColumnsLimit(int columnsLimit);
}
