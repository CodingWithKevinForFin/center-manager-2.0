package com.f1.suite.web.portal.impl.visual;

import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;

public class TestTilesPortlet extends GridPortlet implements FormPortletListener, TilesContextMenuFactory, TilesListener, TileFormatter {

	private TilesPortlet tiles;
	private FormPortlet form;
	private FormPortletTextField titleField;
	private FormPortletTextField descriptionField;
	private FormPortletNumericRangeField tileWidthField;
	private FormPortletNumericRangeField tileHeightField;
	private FormPortletNumericRangeField tilePaddingField;
	private FormPortletButton addButton;
	private FormPortletButton removeButton;
	private FormPortletButton updateButton;
	private FormPortletNumericRangeField positionField;
	private FormPortletButton moveButton;
	private FormPortletNumericRangeField countField;
	private FormPortletColorField colorField;

	public TestTilesPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		tiles = new TilesPortlet(generateConfig());
		form = new FormPortlet(generateConfig());
		addChild(form, 0, 0);
		addChild(tiles, 1, 0);
		form.addFormPortletListener(this);
		titleField = form.addField(new FormPortletTextField("title")).setValue("Some Tile");
		descriptionField = form.addField(new FormPortletTextField("desc"));
		tileWidthField = form.addField(new FormPortletNumericRangeField("tile width").setRange(50, 500).setValue(100).setDecimals(0));
		tileHeightField = form.addField(new FormPortletNumericRangeField("tile height").setRange(50, 500).setValue(100).setDecimals(0));
		tilePaddingField = form.addField(new FormPortletNumericRangeField("tile padding").setRange(0, 50).setValue(5).setDecimals(0));
		positionField = form.addField(new FormPortletNumericRangeField("position").setRange(0, 0).setValue(0).setDecimals(0));
		countField = form.addField(new FormPortletNumericRangeField("count").setRange(1, 100).setValue(1).setDecimals(0));
		colorField = form.addField(new FormPortletColorField("color").setValue("#AABBAA"));

		addButton = form.addButton(new FormPortletButton("add Tile"));
		removeButton = form.addButton(new FormPortletButton("Remove Tiles"));
		updateButton = form.addButton(new FormPortletButton("Update Tiles"));
		moveButton = form.addButton(new FormPortletButton("Move"));
		tiles.setTable(new BasicSmartTable(new BasicTable(new String[] { "name", "style" })));
		tiles.addTilesListener(this);
		tiles.setContextMenuFactory(this);
		tiles.setTileFormatter(this);
		updateOptions();
		setColSize(0, 200);
	}

	public static class Builder extends AbstractPortletBuilder<TestTilesPortlet> {

		private static final String ID = "testTiles";

		public Builder() {
			super(TestTilesPortlet.class);
		}

		@Override
		public TestTilesPortlet buildPortlet(PortletConfig portletConfig) {
			TestTilesPortlet portlet = new TestTilesPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Test Tiles";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == addButton) {
			int count = countField.getIntValue();
			for (int i = 0; i < count; i++) {
				Row tile = tiles.getActiveTile();
				tiles.addRowAt(tile == null ? tiles.getTilesCount() : tile.getLocation(), titleField.getValue() + " - " + i, colorField.getValue());
			}
		} else if (button == removeButton) {
			for (Row tile : tiles.getSelectedTiles()) {
				tiles.removeTile(tile);
			}
		} else if (button == updateButton) {
			for (Row tile : tiles.getSelectedTiles()) {
				tile.putAt(0, titleField.getValue());
			}
		} else if (button == moveButton) {
			Row tile = tiles.getActiveTile();
			if (tile != null)
				tiles.moveTile(tile, positionField.getIntValue());
		}
		positionField.setRange(0, Math.max(0, tiles.getTilesCount() - 1));

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == tileWidthField || field == tileHeightField || field == tilePaddingField)
			updateOptions();
		else if (field == colorField) {
			for (Row row : tiles.getSelectedTiles())
				row.put("style", colorField.getValue());
		}
	}
	private void updateOptions() {
		tiles.addOption(TilesPortlet.OPTION_TILE_HEIGHT, tileHeightField.getIntValue());
		tiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, tileWidthField.getIntValue());
		tiles.addOption(TilesPortlet.OPTION_TILE_PADDING, tilePaddingField.getIntValue());
	}
	@Override
	public WebMenu createMenu(TilesPortlet table) {
		return new BasicWebMenu(new BasicWebMenuLink("Delete Tiles", true, "delete"), new BasicWebMenuLink("Copy Tiles", true, "copy"));
	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {
		if ("delete".equals(action)) {
			for (Row tile : tiles.getSelectedTiles()) {
				tiles.removeTile(tile);
			}
		} else if ("copy".equals(action)) {
			for (Row tile : tiles.getSelectedTiles()) {
				tiles.addRow(tile.getValues().clone());
			}
		}

	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
	}
	@Override
	public void onSelectedChanged(TilesPortlet tiles) {
	}
	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {

	}
	@Override
	public void formatTileDescription(TilesPortlet tp, Row tile, StringBuilder sink) {
	}
	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		sink.append(tile.get("name"));
		styleSink.append("style.background=").append(tile.get("style"));
	}
}
