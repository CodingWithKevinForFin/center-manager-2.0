package com.f1.suite.web.portal.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesContextMenuFactory;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.SmartTable;

public class ShortcutPortlet extends TilesPortlet implements TilesContextMenuFactory, TilesListener, TileFormatter, PortletListener {

	public ShortcutPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		SmartTable data = new BasicSmartTable(new BasicTable(new String[] { "name", "portletId", "color", "img" }));
		setTable(data);
		setTileFormatter(this);
		setContextMenuFactory(this);
		addTilesListener(this);
		addOption(OPTION_TILE_WIDTH, 200);
		addOption(OPTION_TILE_HEIGHT, 115);
		addOption(OPTION_TILE_PADDING, 12);
		getManager().addPortletListener(this);
	}
	public void addShortcut() {

	}
	public void formatTileHtml(TilesPortlet tp, Row tile, StringBuilder sink) {
		sink.append(tile.get("name", String.class));
	}
	public void formatTileStyle(TilesPortlet tp, Row tile, StringBuilder sink) {
		sink.append("_cna=shortcut|");
		if (SH.isnt(tile.get("portletId")))
			sink.append("style.background=url('rsc/red_checkers.gif') ").append(tile.get("color", String.class));
		else if (SH.isnt(tile.get("img")))
			sink.append("style.background=").append(tile.get("color", String.class));
		else {
			sink.append("style.backgroundImage=url('rsc/").append(tile.get("img", String.class)).append("')");
		}
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		formatTileHtml(tilesPortlet, tile, sink);
		formatTileStyle(tilesPortlet, tile, styleSink);

	}
	@Override
	public void formatTileDescription(TilesPortlet tp, Row tile, StringBuilder sink) {
	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {
		if ("add".equals(action)) {
			getManager().showDialog("Add Shortcut", new ShortcutFormPortlet(this, generateConfig()));
		} else if ("edit".equals(action) && tiles.getSelectedTiles().size() == 1) {
			Row tile = CH.first(tiles.getSelectedTiles());
			getManager().showDialog("Edit Shortcut", new ShortcutFormPortlet(this, generateConfig()).setShortcutToEdit(tile));
		} else if ("copy".equals(action) && tiles.getSelectedTiles().size() == 1) {
			Row tile = CH.first(tiles.getSelectedTiles());
			getManager().showDialog("Add Shortcut", new ShortcutFormPortlet(this, generateConfig()).setShortcutToCopy(tile));
		} else if ("remove".equals(action) && tiles.getSelectedTiles().size() == 1) {
			for (Row tile : tiles.getSelectedTiles())
				tiles.removeTile(tile);
		} else if ("up".equals(action) && tiles.getSelectedTiles().size() == 1) {
			Row row = CH.first(tiles.getSelectedTiles());
			int loc = row.getLocation();
			if (loc > 0) {
				tiles.removeTile(row);
				tiles.addRowAt(loc - 1, row.getValues());
			}
		} else if ("down".equals(action) && tiles.getSelectedTiles().size() == 1) {
			Row row = CH.first(tiles.getSelectedTiles());
			int loc = row.getLocation();
			if (loc < tiles.getTilesCount() - 1) {
				tiles.removeTile(row);
				tiles.addRowAt(loc + 1, row.getValues());
			}
		} else if ("bottom".equals(action)) {
			ArrayList<Row> targets = new ArrayList<Row>(tiles.getSelectedTiles());
			for (Row target : targets)
				tiles.removeTile(target);
			for (Row target : targets)
				tiles.addRow(target.getValues());
		} else if ("top".equals(action)) {
			ArrayList<Row> targets = new ArrayList<Row>(tiles.getSelectedTiles());
			for (Row target : targets)
				tiles.removeTile(target);
			int pos = 0;
			for (Row target : targets)
				tiles.addRowAt(pos++, target.getValues());
		} else if ("small".equals(action)) {
			addOption(OPTION_TILE_PADDING, 4);
			addOption(OPTION_TILE_WIDTH, 100);
			addOption(OPTION_TILE_HEIGHT, 115);
		} else if ("medium".equals(action)) {
			addOption(OPTION_TILE_PADDING, 8);
			addOption(OPTION_TILE_WIDTH, 150);
			addOption(OPTION_TILE_HEIGHT, 115);
		} else if ("large".equals(action)) {
			addOption(OPTION_TILE_PADDING, 12);
			addOption(OPTION_TILE_WIDTH, 200);
			addOption(OPTION_TILE_HEIGHT, 115);
		}
	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		if (row != null) {
			String id = row.get("portletId", String.class);
			if (SH.isnt(id))
				return;
			Portlet portlet = getManager().getPortletNoThrow(id);
			if (portlet == null) {
				getManager().showAlert("Shortcut broken");
				return;
			}
			PortletHelper.ensureVisible(portlet);
		}
	}
	@Override
	public void onSelectedChanged(TilesPortlet tiles) {
	}
	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {
	}
	@Override
	public WebMenu createMenu(TilesPortlet tiles) {
		int sel = tiles.getSelectedTiles().size();
		BasicWebMenu r = new BasicWebMenu();
		r.addChild(new BasicWebMenuLink("Create New Shortcut", true, "add"));
		if (sel == 1) {
			r.addChild(new BasicWebMenuLink("Edit Shortcut", true, "edit"));
			r.addChild(new BasicWebMenuLink("Copy Shortcut", true, "copy"));
			r.addChild(new BasicWebMenuLink("Move Up", true, "up"));
			r.addChild(new BasicWebMenuLink("Move Down", true, "down"));
		}
		if (sel > 0) {
			r.addChild(new BasicWebMenuLink("Move To Top", true, "top"));
			r.addChild(new BasicWebMenuLink("Move To Bottom", true, "bottom"));
			r.addChild(new BasicWebMenuLink("Remove Shortcut", true, "remove"));
		}
		r.addChild(new BasicWebMenuLink("Show As Small Icons", true, "small"));
		r.addChild(new BasicWebMenuLink("Show As Medium Icons", true, "medium"));
		r.addChild(new BasicWebMenuLink("Show As Large Icons", true, "large"));
		return r;
	}

	public static class Builder extends AbstractPortletBuilder<ShortcutPortlet> {

		private static final String ID = "Shortcuts";

		public Builder() {
			super(ShortcutPortlet.class);
		}

		@Override
		public ShortcutPortlet buildPortlet(PortletConfig portletConfig) {
			ShortcutPortlet portlet = new ShortcutPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Shortcut";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	private static class ShortcutFormPortlet extends FormPortlet {

		private FormPortletTextField name;
		private FormPortletSelectPortletField target;
		private FormPortletColorField background;
		private FormPortletButton button;
		private ShortcutPortlet portlet;
		private Row edit;
		private FormPortletTextField img;

		public ShortcutFormPortlet(ShortcutPortlet portlet, PortletConfig config) {
			super(config);
			this.portlet = portlet;
			this.target = addField(new FormPortletSelectPortletField("Target portlet"));
			this.name = addField(new FormPortletTextField("Shortcut Name")).setWidth(260);
			this.background = addField(new FormPortletColorField("Color"));
			this.img = addField(new FormPortletTextField("Image"));
			this.button = addButton(new FormPortletButton("Add Shortcut"));
		}

		public ShortcutFormPortlet setShortcutToCopy(Row data) {
			this.name.setValue(data.get("name", String.class));
			this.target.setValue(data.get("portletId", String.class));
			this.background.setValue(data.get("color", String.class));
			this.img.setValue(data.get("img", String.class));
			return this;
		}
		public ShortcutFormPortlet setShortcutToEdit(Row data) {
			this.edit = data;
			this.setShortcutToCopy(data);
			this.button.setName("Apply changes to shortcut");
			return this;
		}

		@Override
		public void onUserPressedButton(FormPortletButton bttn) {
			if (bttn == button) {
				if (edit != null) {
					edit.put("name", name.getValue());
					edit.put("portletId", target.getValue());
					edit.put("color", background.getValue());
					edit.put("img", img.getValue());
				} else
					portlet.addRow(name.getValue(), target.getValue(), this.background.getValue(), this.img.getValue());
				close();
			} else
				super.onUserPressedButton(bttn);
		}

		@Override
		public void onFieldChanged(FormPortletField<?> field) {
			super.onFieldChanged(field);
			if (field == this.target) {
				if (this.target.getValueAsPortlet() != null && SH.isnt(name.getValue()))
					name.setValue(SH.fromCamelHumps(" ", this.target.getValueAsPortlet().getTitle()));
			}
		}
	}

	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
		if (tile == null) {
			getManager().showDialog("Add Shortcut", new ShortcutFormPortlet(this, generateConfig()));
		} else {
			getManager().showDialog("Edit Shortcut", new ShortcutFormPortlet(this, generateConfig()).setShortcutToEdit(tile));
		}
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {
	}
	@Override
	public void onPortletClosed(Portlet newPortlet) {
		for (Row tile : getTiles()) {
			if (OH.eq(newPortlet.getPortletId(), tile.get("portletId"))) {
				tile.put("portletId", null);
			}
		}
	}
	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
		addOption(OPTION_TILE_WIDTH, CH.getOrThrow(Caster_Integer.PRIMITIVE, configuration, "tw"));
		addOption(OPTION_TILE_HEIGHT, CH.getOrThrow(Caster_Integer.PRIMITIVE, configuration, "th"));
		addOption(OPTION_TILE_PADDING, CH.getOrThrow(Caster_Integer.PRIMITIVE, configuration, "tp"));
		final List<Map<?, ?>> data = (List<Map<?, ?>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "data");
		clear();
		TableHelper.fromListOfMaps(data, getTable());
		for (Row row : getTable().getRows()) {
			String pid = row.get("portletId", String.class);
			String newpid = origToNewIdMapping.get(pid);
			row.put("portletId", newpid == null ? null : SH.parseInt(newpid));
		}
	}
	@Override
	public Map<String, Object> getConfiguration() {
		final Map<String, Object> r = super.getConfiguration();
		r.put("tw", (Integer) getOption(OPTION_TILE_WIDTH));
		r.put("th", (Integer) getOption(OPTION_TILE_HEIGHT));
		r.put("tp", (Integer) getOption(OPTION_TILE_PADDING));
		final List<Map<Object, Object>> rows = TableHelper.toListOfMaps(getTable());
		r.put("data", rows);
		return r;
	}
	@Override
	public void onLocationChanged(Portlet portlet) {
	}
}
