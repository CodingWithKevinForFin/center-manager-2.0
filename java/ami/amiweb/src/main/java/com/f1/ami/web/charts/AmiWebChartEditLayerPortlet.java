package com.f1.ami.web.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class AmiWebChartEditLayerPortlet<T extends AmiWebChartSeries> extends GridPortlet implements TileFormatter, TilesListener {

	private T series;
	private AmiWebChartRenderingLayer container;

	private List<AmiWebChartEditSeriesPortlet<T>> editors = new ArrayList<AmiWebChartEditSeriesPortlet<T>>();
	private TilesPortlet tilesPortlet;
	private InnerPortlet editorPanel;
	private AmiWebChartEditSeriesPortlet<T> activeEditor;
	private Map<String, Row> editorTypeIds2rows = new HashMap<String, Row>();
	private DividerPortlet divPortlet;
	private GridPortlet tabsAndSeriesGrid;
	private AmiWebChartEditRenderingLayerPortlet<?> layerPortlet;

	public AmiWebChartEditLayerPortlet(PortletConfig config, AmiWebChartRenderingLayer container, T series, AmiWebChartEditRenderingLayerPortlet<?> editor) {
		super(config);
		this.container = container;
		this.series = series;
		SmartTable table = new BasicSmartTable(new BasicTable(new String[] { "Name", "Editor", "TypeId", "Icon" }));
		this.tilesPortlet = new TilesPortlet(generateConfig());
		this.tilesPortlet.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#e2e2e2");
		this.tilesPortlet.setTable(table);
		this.tilesPortlet.setTileFormatter(this);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_HEIGHT, 70);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_WIDTH, 90);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_PADDING, 3);
		this.tilesPortlet.setMultiselectEnabled(false);
		this.tilesPortlet.addTilesListener(this);
		this.tabsAndSeriesGrid = new GridPortlet(generateConfig());
		this.tabsAndSeriesGrid.addChild(tilesPortlet, 0, 0);
		this.tabsAndSeriesGrid.setRowSize(0, 150);
		this.editorPanel = this.tabsAndSeriesGrid.addChild(new HtmlPortlet(generateConfig()).setCssStyle("_bg=#e2e2e2"), 0, 1, 1, 1);
		this.layerPortlet = editor;
		this.divPortlet = new DividerPortlet(generateConfig(), true, this.layerPortlet, tabsAndSeriesGrid);
		this.addChild(this.divPortlet);

	}
	public void addEditor(AmiWebChartEditSeriesPortlet<T> editor, String icon, String tileText) {
		editor.setContainer(container, series);
		Row row = this.tilesPortlet.addRow(tileText, editor, editor.getEditorTypeId(), icon);
		this.editorTypeIds2rows.put(editor.getEditorTypeId(), row);
		this.getManager().onPortletAdded(editor);
		this.editors.add(editor);
		if (OH.eq(this.series.getEditorTypeId(), editor.getEditorTypeId())) {
			this.tilesPortlet.setActiveTileByPosition(row.getLocation());
			onTileClicked(this.tilesPortlet, row);
		}

	}

	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		if (tile.get("Icon", Caster_String.INSTANCE) != null) {
			String icon = tile.get("Icon", Caster_String.INSTANCE);
			styleSink.append(
					"_fs=12|_fm=bold|_bg=white|_fg=#004400|style.backgroundRepeat=no-repeat|style.justifyContent=center|style.backgroundPosition=top center|style.display=flex|style.alignItems=flex-end|style.backgroundImage=url('rsc/ami/chart_style_icons/"
							+ icon + "')|style.backgroundSize=50% 70%");
			sink.append(tile.get("Name", Caster_String.INSTANCE));
		} else
			sink.append(tile.get("Name"));
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {
	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		AmiWebChartEditSeriesPortlet<T> editor = (AmiWebChartEditSeriesPortlet<T>) row.get("Editor");
		this.activeEditor = editor;
		editorPanel.setPortlet(editor);
		// comment out line below so add viz remembers the values next time it comes back
		editor.updateFields();
	}
	@Override
	public void onSelectedChanged(TilesPortlet tiles) {

	}
	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {

	}
	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {

	}
	public boolean preview() {
		if (this.activeEditor != null)
			return this.activeEditor.preview();
		else
			return false;
	}
	public void setActiveEditor(String id) {
		Row row = CH.getOrThrow(this.editorTypeIds2rows, id);
		this.tilesPortlet.setActiveTileByPosition(row.getLocation());
		onTileClicked(this.tilesPortlet, row);
	}
	public AmiWebChartEditRenderingLayerPortlet<?> getEditRenderingLayerPortlet() {
		return layerPortlet;
	}
	public AmiWebChartEditSeriesPortlet<T> getActiveEditor() {
		return this.activeEditor;
	}
}
