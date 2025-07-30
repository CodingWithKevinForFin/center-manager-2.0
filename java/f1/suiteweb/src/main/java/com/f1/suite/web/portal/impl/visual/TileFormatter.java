package com.f1.suite.web.portal.impl.visual;

import com.f1.base.Row;

public interface TileFormatter {

	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink);
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink);
}
