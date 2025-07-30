package com.f1.suite.web.portal.impl.visual;

import com.f1.base.Row;

public interface TilesListener {

	public void onContextMenu(TilesPortlet tiles, String action);

	public void onTileClicked(TilesPortlet table, Row row);

	public void onSelectedChanged(TilesPortlet tiles);

	public void onVisibleRowsChanged(TilesPortlet tiles);

	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile);
}
