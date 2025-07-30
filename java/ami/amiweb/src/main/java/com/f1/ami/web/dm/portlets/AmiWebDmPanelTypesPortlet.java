package com.f1.ami.web.dm.portlets;

import java.util.Map;

import com.f1.ami.web.AmiWebPanelPluginWrapper;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.SH;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebDmPanelTypesPortlet extends TilesPortlet implements TileFormatter {

	public static final byte TYPE_SIMPLE = 1;
	public static final byte TYPE_STATIC = 2;
	public static final byte TYPE_AGG = 3;
	public static final byte TYPE_TREEMAP = 4;
	public static final byte TYPE_CHART_STATIC = 5;
	public static final byte TYPE_CHART_3D = 6;
	public static final byte TYPE_FORM = 7;
	public static final byte TYPE_TREEMAP_STATIC = 8;
	public static final byte TYPE_TREE = 9;
	public static final byte TYPE_PLUGIN = 10;
	public static final byte TYPE_CHART_RADIAL = 11;

	private TilesPortlet displayTypePanel;

	public AmiWebDmPanelTypesPortlet(PortletConfig portletConfig, boolean realtime, boolean statik) {
		super(portletConfig);
		setTable(new BasicSmartTable(new BasicTable(new String[] { "name", "id", "img", "pluginId", "cn" })));
		setTileFormatter(this);
		if (realtime) {
			addRow("Real Time Table", TYPE_SIMPLE, "icon_table_rt.png", null, "ami_display_realtimeTable");
			addRow("Aggregate Table", TYPE_AGG, "icon_table_ag.png", null, "ami_display_aggregateTable");
			addRow("Heat Map", TYPE_TREEMAP, "icon_table_tm.png", null, "ami_display_heatmap");
		}
		if (statik) {
			addRow("Table", TYPE_STATIC, "icon_table_st.png", null, "ami_display_staticTable");
			addRow("2D Chart", TYPE_CHART_STATIC, "icon_chart_st.png", null, "ami_display_2DChart");
			addRow("3D Chart", TYPE_CHART_3D, "icon_3d_st.png", null, "ami_display_3DChart");
			addRow("HTML/Canvas", TYPE_FORM, "icon_form.png", null, "ami_display_userForm");
			addRow("Heat Map", TYPE_TREEMAP_STATIC, "icon_table_tm.png", null, "ami_display_staticHeatmap");
			addRow("Tree Grid", TYPE_TREE, "icon_tree.png", null, "ami_display_tree");
			Map<String, AmiWebPanelPluginWrapper> plugins = AmiWebUtils.getService(this.getManager()).getPanelPlugins();
			for (AmiWebPanelPluginWrapper i : plugins.values())
				addRow(i.getPlugin().getDisplayName(), TYPE_PLUGIN, i.getPlugin().getDisplayIconFileName(), i.getPlugin().getPluginId(), i.getPlugin().getCssClassName());
		}
		addOption(TilesPortlet.OPTION_TILE_WIDTH, 150);
		addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#4c4c4c");
		addOption(TilesPortlet.OPTION_TILE_HEIGHT, 110);
		addOption(TilesPortlet.OPTION_TILE_PADDING, 2);
		addOption(TilesPortlet.OPTION_ALIGN, TilesPortlet.VALUE_ALIGN_JUSTIFY);
		setMultiselectEnabled(false);

	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		Byte id = tile.get("id", byte.class);
		String img = tile.get("img", String.class);
		String cn = tile.get("cn", String.class);
		sink.append("<div class=\"ami_tile_display " + cn + "\">");
		sink.append("</div>");
		sink.append("<div class=\"ami_tile_footer\">");
		sink.append(tile.get("name"));
		sink.append("</div>");
		if (selected) {
			styleSink.append("_cna=ami_tile|_cna=ami_tile_selected");
		} else {
			styleSink.append("_cna=ami_tile");
		}
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {
	}

	public byte getSelectedType() {
		Row t = getActiveTile();
		return t == null ? -1 : t.get("id", Byte.class);
	}
	public void setSelectedType(byte type) {
		if (type == -1) {
			setActiveTileByPosition(-1);
			return;
		}
		for (Row i : getTable().getRows()) {
			byte t = i.get("id", Byte.class);
			if (type == t) {
				setActiveTileByPosition(i.getLocation());
				break;
			}
		}
	}
	public String getSelectedPluginId() {
		Row t = getActiveTile();
		return t == null ? null : t.get("pluginId", String.class);
	}
	public static String getDescription(byte type) {
		switch (type) {
			case TYPE_SIMPLE:
				return "Table";
			case TYPE_STATIC:
				return "Table";
			case TYPE_AGG:
				return "Aggregation";
			case TYPE_TREEMAP:
				return "TreeMap";
			case TYPE_TREEMAP_STATIC:
				return "StaticTreeMap";
			case TYPE_CHART_STATIC:
				return "Chart";
			case TYPE_CHART_3D:
				return "Chart3d";
			case TYPE_FORM:
				return "Form";
			case TYPE_TREE:
				return "Tree";
			case TYPE_PLUGIN:
				return "Plugin";
			default:
				return SH.toString(type);
		}
	}

}
