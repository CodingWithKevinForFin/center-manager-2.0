package com.f1.suite.web.portal.impl.visual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableListener;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.table.RowFilter;
import com.f1.utils.structs.table.SmartTable;

public class TilesPortlet extends AbstractPortlet implements TableListener {

	public static final String OPTION_TILE_WIDTH = "tileWidth";
	public static final String OPTION_TILE_HEIGHT = "tileHeight";
	public static final String OPTION_TILE_PADDING = "tilePadding";
	public static final String OPTION_ALIGN = "align";
	public static final String OPTION_CSS_STYLE = "cssStyle";

	public static final String VALUE_ALIGN_LEFT = "left";
	public static final String VALUE_ALIGN_CENTER = "center";
	public static final String VALUE_ALIGN_RIGHT = "right";
	public static final String VALUE_ALIGN_JUSTIFY = "justify";

	public static final PortletSchema<TilesPortlet> SCHEMA = new BasicPortletSchema<TilesPortlet>("Tiles", "TilesPortlet", TilesPortlet.class, false, true);

	public TilesPortlet(PortletConfig portletConfig) {
		super(portletConfig);
	}
	public void setTable(SmartTable table) {
		if (this.tiles != null)
			throw new IllegalStateException();
		this.tiles = table;
		this.tiles.addTableListener(this);
	}

	private TileFormatter tileFormatter = new DefaultTileFormatter();
	final private List<TilesListener> listeners = new ArrayList<TilesListener>();

	private int nextId = 1;

	private SmartTable tiles = null;
	private boolean dataChanged;
	private boolean optionsChanged;
	private Map<String, Object> options = new HashMap<String, Object>();
	private int knownSize = 0;
	private int clipTop = 0;
	private int clipBottom = 0;
	private Row activeTile;
	private String selectedRowsText = "";
	private TilesContextMenuFactory menuFactory;
	final private IdentityHashSet<Row> selectedRows = new IdentityHashSet<Row>();
	private boolean selectsChanged;

	public Object addOption(String key, Object value) {
		onOptionsChanged();
		return options.put(key, value);
	}
	public Object removeOption(String key) {
		onOptionsChanged();
		return options.remove(key);
	}
	public void clearOptions() {
		this.options.clear();
		onOptionsChanged();
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}

	public Row addRow(Object... data) {
		return tiles.getRows().addRow(data);
	}
	public Row addRowAt(int position, Object... data) {
		return tiles.getRows().insertRow(position, data);
	}

	public void removeTile(Row tile) {
		this.tiles.getRows().remove(tile.getLocation());
	}

	public Row getTileAt(int id) {
		return tiles.getRows().get(id);
	}

	public Iterable<? extends Row> getTiles() {
		return tiles.getRows();
	}
	public int getTilesCount() {
		return tiles.getRows().size();
	}

	public Row getAtPosition(int position) {
		return tiles.getRows().get(position);
	}

	public void moveTile(Row tile, int newPosition) {
		if (tile.getLocation() == newPosition)
			return;
		OH.assertBetween(newPosition, 0, tiles.getRows().size() - 1);
		if (tile.getLocation() >= this.clipTop || newPosition >= this.clipTop) {
			if (tile.getLocation() <= this.clipBottom || newPosition < this.clipBottom) {
				this.dataChanged = true;
			}
		}
		this.tiles.getRows().remove(tile.getLocation());
		this.tiles.getRows().add(newPosition, tile);
		flagPendingAjax();
	}

	private void onUpdated(Row node) {
		if (OH.isBetween(node.getLocation(), this.clipTop, this.clipBottom - 1))
			this.dataChanged = true;
		flagPendingAjax();
	}
	private void onCleared() {
		this.knownSize = 0;
		this.dataChanged = true;
		flagPendingAjax();
	}
	public void clear() {
		this.tiles.clear();
		this.selectedRows.clear();
		this.selectedRowsText = "";
		onCleared();
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			int cnt = tiles.getRows().size();
			if (cnt != this.knownSize)
				callJsFunction("setTilesCount").addParam(this.knownSize = cnt).end();

			//Children
			if (dataChanged) {
				callJsFunction("clearData").end();
				callJsFunction("initTiles").addParam(multiSelectEnabled).end();
				JsFunction js = callJsFunction("addChildren");
				JsonBuilder json = js.startJson();
				json.startList();
				for (int i = this.clipTop; i < this.clipBottom; i++) {
					if (i >= cnt)
						break;
					toJson(tiles.getRows().get(i), json);
				}
				json.endList();
				json.close();
				js.end();
			}

			if (optionsChanged) {
				JsFunction func = callJsFunction("setOptions");
				JsonBuilder json = func.startJson();
				json.addQuoted(options);
				json.close();
				func.end();
			}
			if (selectsChanged) {
				JsFunction func = callJsFunction("setActiveTilePos");
				func.addParam(activeTile == null ? -1 : activeTile.getLocation());
				func.end();
				func = callJsFunction("setSelectedTiles");
				func.addParamQuoted(selectedRowsText);
				func.end();
				this.selectsChanged = false;
			}

			callJsFunction("repaint").end();
			dataChanged = false;
			optionsChanged = false;
		}
	}

	StringBuilder tmpSb = new StringBuilder();
	StringBuilder tmpSbStyle = new StringBuilder();
	private Object tileFormatterColumnId;
	private int tileFormatterColumnLoc;
	private boolean multiSelectEnabled = true;
	private boolean repaintOnSelectChanged = true;
	private RowFilter filter;

	private void toJson(Row node, JsonBuilder sink) {
		tmpSb.setLength(0);
		tmpSbStyle.setLength(0);
		sink.startMap();
		sink.addKeyValue("i", node.getUid());
		tileFormatter.formatTile(this, node, getSelectedTiles().contains(node), this.activeTile == node, SH.clear(tmpSb), SH.clear(tmpSbStyle));
		sink.addKeyValueQuoted("n", tmpSb);
		sink.addKeyValueQuoted("s", tmpSbStyle);
		sink.addKeyValueQuoted("p", node.getLocation());
		sink.endMap();
	}
	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("clipzone".equals(callback)) {
			int top = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "top");
			int bottom = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "bottom");
			this.onClipzoneChanged(top, bottom);
		} else if ("select".equals(callback)) {
			int active = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "active");
			String selected = CH.getOrThrow(Caster_String.INSTANCE, attributes, "selected");
			setActiveTileByPosition(active, false);
			if (active != -1) {
				if (!listeners.isEmpty() && getActiveTile() != null)
					for (TilesListener listener : this.listeners)
						listener.onTileClicked(this, getActiveTile());
			}
			setSelectedRows(selected, false);
		} else if ("showMenu".equals(callback)) {
			if (menuFactory != null) {
				WebMenu menu = menuFactory.createMenu(this);
				if (menu != null) {
					JsFunction jsf = callJsFunction("showContextMenu");
					jsf.addParamJson(PortletHelper.menuToJson(getManager(), menu));
					jsf.end();
				}
			}
		} else if ("dblclick".equals(callback)) {
			int pos = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pos");
			if (CH.isntEmpty(listeners)) {
				for (TilesListener listener : listeners) {
					listener.onDoubleclick(this, pos == -1 ? null : getTileAt(pos));
				}
			}
		} else if ("menuitem".equals(callback)) {
			final WebMenuLink menuAction = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "action"));
			if (menuAction != null && CH.isntEmpty(listeners)) {
				for (TilesListener listener : listeners)
					listener.onContextMenu(this, menuAction.getAction());
			}
		} else
			super.handleCallback(callback, attributes);
	}

	public void setActiveTileByPosition(int activeRowPos) {
		this.setActiveTileByPosition(activeRowPos, true);
	}
	private void setActiveTileByPosition(int activeRowPos, boolean fire) {
		if (activeRowPos < 0 || activeRowPos >= getTilesCount()) {
			if (this.activeTile == null)
				return;
			this.activeTile = null;
			setSelectedRows("");
		} else {
			Row t = getTileAt(activeRowPos);
			if (t == this.activeTile)
				return;
			this.activeTile = t;
			if (!getSelectedTiles().contains(getTileAt(activeRowPos)))
				setSelectedRows(SH.toString(activeRowPos));
		}
		if (fire)
			selectsChanged = true;
	}
	public void setSelectedRows(String selectedRowsText) {
		this.setSelectedRows(selectedRowsText, false);

	}
	public void setSelectedRows(String selectedRowsText, boolean fire) {
		if (OH.eq(this.selectedRowsText, selectedRowsText))
			return;
		this.selectedRowsText = selectedRowsText;
		buildSelectedRows(1000);
		if (!listeners.isEmpty())
			for (TilesListener listener : this.listeners)
				listener.onSelectedChanged(this);
		if (repaintOnSelectChanged) {
			dataChanged = true;
			flagPendingAjax();
		}
		if (fire)
			this.selectsChanged = true;
	}

	private void buildSelectedRows(int max) {
		this.selectedRows.clear();
		String[] parts = SH.split(',', selectedRowsText);
		int ranges[] = new int[parts.length * 2];
		int cnt = 0, i = 0;
		for (String s : parts) {
			int start, end;
			if (s.indexOf('-') == -1) {
				start = end = Integer.parseInt(s);
			} else {
				start = Integer.parseInt(SH.beforeFirst(s, '-'));
				end = Integer.parseInt(SH.afterFirst(s, '-'));
			}
			cnt += end - start + 1;
			if (cnt > max)
				return;
			ranges[i++] = start;
			ranges[i++] = end;
		}
		int rowsCount = getTilesCount();
		if (i == ranges.length) {//made it to the end
			for (i = 0; i < ranges.length; i += 2) {
				int loc = ranges[i], end = ranges[i + 1];
				while (loc <= end && loc < rowsCount)
					this.selectedRows.add(getTileAt(loc++));
			}
		}
	}

	//do not modify return value
	public Set<Row> getSelectedTiles() {
		if (selectedRows.isEmpty() != SH.isnt(selectedRowsText))
			buildSelectedRows(getTilesCount());
		return selectedRows;
	}

	public boolean hasSelectedTiles() {
		return selectedRowsText.length() > 0;
	}

	private void onClipzoneChanged(int top, int bottom) {
		if (this.clipTop == top && this.clipBottom == bottom)
			return;
		this.clipTop = top;
		this.clipBottom = bottom;
		this.dataChanged = true;
		flagPendingAjax();
	}

	private void onOptionsChanged() {
		this.optionsChanged = true;
		flagPendingAjax();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			onCleared();
			flagPendingAjax();
			knownSize = 0;
			dataChanged = true;
			optionsChanged = true;
			selectsChanged = true;
		} else {
			onCleared();
		}
	}
	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}
	public Row getActiveTile() {
		return this.activeTile;
	}
	@Override
	public void onCell(Row row, int cell, Object oldValue, Object newValue) {
		if (OH.isBetween(row.getLocation(), this.clipTop, this.clipBottom - 1))
			this.dataChanged = true;
		flagPendingAjax();

	}
	@Override
	public void onColumnAdded(Column nuw) {
		onColumnsOrFormattersChanged();
	}
	@Override
	public void onColumnRemoved(Column old) {
		onColumnsOrFormattersChanged();
	}
	@Override
	public void onColumnChanged(Column old, Column nuw) {
		onColumnsOrFormattersChanged();
	}
	@Override
	public void onRowAdded(Row add) {
		if (add.getLocation() <= this.clipBottom)
			this.dataChanged = true;
		flagPendingAjax();

	}
	@Override
	public void onRowRemoved(Row tile, int index) {
		if (tile == activeTile)
			activeTile = null;
		if (index <= this.clipBottom)
			this.dataChanged = true;
		flagPendingAjax();

	}
	public TileFormatter getTileFormatter() {
		return tileFormatter;
	}

	public Object getTileFormatterColumnId() {
		return tileFormatterColumnId;
	}

	public void setTileFormatter(TileFormatter formatter) {
		this.tileFormatter = formatter;
		onColumnsOrFormattersChanged();
	}
	private void onColumnsOrFormattersChanged() {
		this.dataChanged = true;
		flagPendingAjax();
	}
	public TilesContextMenuFactory getContextMenuFactory() {
		return menuFactory;
	}
	public void setContextMenuFactory(TilesContextMenuFactory menuFactory) {
		this.menuFactory = menuFactory;
	}
	public List<TilesListener> getListeners() {
		return listeners;
	}
	public void addTilesListener(TilesListener listener) {
		this.listeners.add(listener);
	}
	public boolean removeTilesListener(TilesListener listener) {
		return this.listeners.remove(listener);
	}
	public SmartTable getTable() {
		return this.tiles;
	}
	public TilesPortlet setMultiselectEnabled(boolean b) {
		if (b == this.multiSelectEnabled)
			return this;
		this.multiSelectEnabled = b;
		this.dataChanged = true;
		if (getSelectedTiles().size() > 1)
			this.setSelectedRows(SH.toString(getActiveTile().getLocation()), true);
		flagPendingAjax();
		return this;
	}

	public boolean getMultiselectEnalbed() {
		return this.multiSelectEnabled;
	}
	public boolean isRepaintOnSelectChanged() {
		return repaintOnSelectChanged;
	}
	public TilesPortlet setRepaintOnSelectChanged(boolean repaintOnSelectChanged) {
		if (this.repaintOnSelectChanged == repaintOnSelectChanged)
			return this;
		this.repaintOnSelectChanged = repaintOnSelectChanged;
		return this;
	}
	public void setFilter(RowFilter filter) {
		this.filter = filter;
		this.tiles.setTableFilter(filter);
		this.dataChanged = true;
		this.tiles.redoRows();
		flagPendingAjax();
	}
	public RowFilter getFilter() {
		return this.filter;
	}

}
