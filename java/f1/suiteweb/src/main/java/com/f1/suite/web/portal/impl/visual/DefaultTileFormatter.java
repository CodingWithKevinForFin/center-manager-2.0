package com.f1.suite.web.portal.impl.visual;

import com.f1.base.Row;
import com.f1.utils.SH;

public class DefaultTileFormatter implements TileFormatter {

	@Override
	public void formatTileDescription(TilesPortlet portlet, Row tile, StringBuilder sink) {
	}

	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		SH.join("<br>", tile.getValues(), sink);
	}

}
