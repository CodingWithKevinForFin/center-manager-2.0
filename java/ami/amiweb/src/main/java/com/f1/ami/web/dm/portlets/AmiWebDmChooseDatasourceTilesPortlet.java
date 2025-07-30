package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.JdbcAdapter;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.portlets.AmiWebHeaderSearchHandler;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSystemObjectsManager;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.Action;
import com.f1.base.Column;
import com.f1.base.Row;
import com.f1.base.TableListenable;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.TextMatcherRowFilter;

public class AmiWebDmChooseDatasourceTilesPortlet extends GridPortlet implements TileFormatter, TilesListener, AmiWebHeaderSearchHandler {

	private static final int DEFAULT_PREVIEWCOUNT = 10;
	private TilesPortlet tiles;
	private Map<Row, AmiDatasourceTable> tiles2DsTables;
	private Map<String, AmiDatasourceTable> tableNames2PreviewSchema;
	private Set<String> requestedTablesToWaitFor;
	private AmiWebService service;
	private List<AmiWebDatasourceWrapper> currentDsList;
	private AmiWebHeaderPortlet header;
	private ConfirmDialogPortlet dialog;
	private TabPortlet previewTabs;
	private DividerPortlet divider;
	private GridPortlet previewGrid;
	private FormPortlet previewForm;

	private static final String TILE_NAME = "name";
	private static final String TILE_COLLECTION = "collectionName";
	private static final String TILE_PREPEND_SCHEMA = "prependSchema";
	private static final String TILE_FULLNAME = "fullName";
	private static final String TILE_NUM_COLS = "numCols";
	private static final String TILE_DATASOURCE = "datasource";
	private static final Comparator<? super AmiDatasourceTable> SORTER = new Comparator<AmiDatasourceTable>() {

		@Override
		public int compare(AmiDatasourceTable o1, AmiDatasourceTable o2) {
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getName(), o2.getName());
		}
	};
	private static final Comparator<Row> SORTER_ROW = new Comparator<Row>() {
		@Override
		public int compare(Row r1, Row r2) {
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare((String) r1.get(TILE_DATASOURCE) + (String) r1.get(TILE_NAME),
					(String) r2.get(TILE_DATASOURCE) + (String) r2.get(TILE_NAME));
		}
	};
	public static final String ADDPANEL_HELP_HTML = "Select the table that the <span style=\"color:#99d898\"> <B>datamodel</B></span> and visualization should be based on. Use <B>Ctrl+Click</B> to select multiple tables.";
	public static final String HELP_HTML = "Select the table that the <span style=\"color:#99d898\"> <B>datamodel</B></span> should be based on. Use <B>Ctrl+Click</B> to select multiple tables.";

	public AmiWebDmChooseDatasourceTilesPortlet(PortletConfig portletConfig) {
		super(portletConfig);

		this.service = AmiWebUtils.getService(getManager());
		header = new AmiWebHeaderPortlet(generateConfig());
		this.tiles = new TilesPortlet(generateConfig());
		this.previewTabs = new TabPortlet(generateConfig());
		this.previewTabs.setIsCustomizable(false);
		this.previewTabs.getTabPortletStyle().setBackgroundColor("#AAAAAA");
		this.previewTabs.getTabPortletStyle().setTabPaddingBottom(0);
		this.previewTabs.getTabPortletStyle().setSelectedColor("#555555");
		this.previewTabs.getTabPortletStyle().setSelectTextColor("#FFFFFF");
		this.previewTabs.getTabPortletStyle().setTabPaddingTop(0);
		addChild(header, 0, 0, 2, 1);
		this.previewGrid = new GridPortlet(generateConfig());
		this.previewForm = new FormPortlet(generateConfig());
		this.previewForm.getFormPortletStyle().setCssStyle("_bg=#AAAAAA|_fg=#FFFFFF|_fm=center");
		this.previewForm.setHtmlLayout("Table Data Preview - First " + DEFAULT_PREVIEWCOUNT + " Rows");
		previewGrid.addChild(previewForm, 0, 0);
		previewGrid.addChild(this.previewTabs, 0, 1);
		previewGrid.setRowSize(0, 18);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (root.getWidth() * 0.8));
		int height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (root.getHeight() * 0.8));
		this.divider = new DividerPortlet(generateConfig(), false, this.tiles, this.previewGrid);
		this.divider.setColor("#888888");
		addChild(this.divider, 0, 1, 1, 1);

		this.setColSize(1, 0);
		this.tiles.setTable(new BasicSmartTable(new BasicTable(new String[] { TILE_NAME, TILE_COLLECTION, TILE_FULLNAME, TILE_PREPEND_SCHEMA, TILE_DATASOURCE })));
		this.tiles.setTileFormatter(this);
		this.tiles.addTilesListener(this);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, 200);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_HEIGHT, 55);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_PADDING, 6);
		this.tiles.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#Ffffff");
		this.tiles.setMultiselectEnabled(true);

		this.tiles2DsTables = new HashMap<Row, AmiDatasourceTable>();
		this.tableNames2PreviewSchema = new HashMap<String, AmiDatasourceTable>();
		this.requestedTablesToWaitFor = new HashSet<String>();
		this.header.setSearchHandler(this);
		this.header.updateBlurbPortletLayout("AMI Table Chooser", AmiWebDmChooseDatasourceTilesPortlet.HELP_HTML);
		getManager().onPortletAdded(this);
		this.setSuggestedSize(width, height);
	}
	public void addTilesListener(TilesListener listener) {
		this.tiles.addTilesListener(listener);
	}
	public void removeTilesListener(TilesListener listener) {
		this.tiles.removeTilesListener(listener);
	}
	public void init(List<AmiWebDatasourceWrapper> dsList) {
		populate(dsList);
	}

	public void populate(AmiWebDatasourceWrapper ds) {
		populate(CH.l(ds));
	}
	public void populate(List<AmiWebDatasourceWrapper> dsList) {
		this.tiles.clear();
		this.previewTabs.removeAllChildren();

		AmiWebDatasourceWrapper ds;
		AmiWebSystemObjectsManager[] systemObjectManagers = this.service.getWebManagers().getSystemObjectManagers();
		for (int j = 0; j < dsList.size(); j++) {
			ds = dsList.get(j);
			if (ds == null)
				return;
			AmiWebDatasourceWrapper w = null;
			// search in all centers for this ds
			for (AmiWebSystemObjectsManager asom : systemObjectManagers) {
				w = asom.getDatasource(ds.getId());
				if (w != null)
					break;
			}
			if (w == null) {
				getManager().showAlert("Datasource " + ds.getId() + " not found on connected centers.");
				continue;
			}
			AmiCenterQueryDsRequest request = nw(AmiCenterQueryDsRequest.class);
			request.setType(AmiCenterQueryDsRequest.TYPE_SHOW_TABLES);
			request.setTimeoutMs(service.getDefaultTimeoutMs());
			request.setPermissions((byte) (AmiCenterQueryDsRequest.PERMISSIONS_READ | AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE));
			request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND);
			request.setDatasourceName(w.getName());
			this.service.sendRequestToBackend(getPortletId(), request);
		}
		this.currentDsList = dsList;
	}
	private AmiWebDatasourceWrapper findDsByName(String name) {
		if (SH.isnt(name)) {
			return null;
		}
		AmiWebDatasourceWrapper ds;
		for (int i = 0; i < this.currentDsList.size(); i++) {
			ds = this.currentDsList.get(i);
			if (name.equals(ds.getName())) {
				return ds;
			}
		}
		return null;
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		AmiCenterResponse action = (AmiCenterResponse) result.getAction();
		if (action instanceof AmiCenterQueryDsResponse) {
			AmiCenterQueryDsRequest req = (AmiCenterQueryDsRequest) result.getRequestMessage().getAction();
			AmiCenterQueryDsResponse resp = (AmiCenterQueryDsResponse) action;
			AmiWebDatasourceWrapper ds = findDsByName(req.getDatasourceName());
			String dsName = ds.getName();
			boolean multipleDatasources = this.currentDsList.size() > 1;
			if (req.getType() == AmiCenterQueryDsRequest.TYPE_SHOW_TABLES) {
				if (!action.getOk()) {
					getManager().showAlert(action.getMessage(), action.getException());
				} else {
					List<AmiDatasourceTable> dsTables = resp.getPreviewTables();
					Map<String, String> options = JdbcAdapter.splitOptionsToMap(ds.getOptions());
					boolean prependSchema = CH.getOr(Caster_Boolean.INSTANCE, options, JdbcAdapter.OPTION_PREPEND_SCHEMA, true);
					addTables(dsTables, dsName, multipleDatasources, prependSchema);

				}
			} else {
				if (OH.eq(req.getDatasourceName(), ds.getName())) {
					if (!action.getOk()) {
						ArrayList<AmiDatasourceTable> tables = (ArrayList<AmiDatasourceTable>) req.getTablesForPreview();
						for (int i = 0; i < tables.size(); i++) {
							this.requestedTablesToWaitFor.remove(tables.get(i).getName());
						}
						getManager().showAlert(action.getMessage(), action.getException());
					} else {
						List<AmiDatasourceTable> tables = resp.getPreviewTables();
						if (tables != null) {
							for (int i = 0; i < tables.size(); i++) {
								AmiDatasourceTable table = tables.get(i);
								this.tableNames2PreviewSchema.put(generateTableKey(table), table);
								this.requestedTablesToWaitFor.remove(table.getName());
							}

							refreshPreview();
							isReadyForSelectedSchema();
						}
					}

				}
			}
		}
	}
	private void addTables(List<AmiDatasourceTable> dsTables, String dsName, boolean includeDsName, boolean prependSchema) {
		if (dsTables != null) {
			Row curRow;
			int w = 100;
			for (AmiDatasourceTable t : dsTables) {
				String collectionName = t.getCollectionName();
				String name = t.getName();
				t.setDatasourceName(dsName);
				w = Math.max(w, getManager().getPortletMetrics().getWidth(name, "_fm=arial", 16) + 6);
				if (collectionName != null) {
					w = Math.max(w, getManager().getPortletMetrics().getWidth(collectionName, "_fm=arial", 16) + 6);
					curRow = this.tiles.addRow(name, collectionName, collectionName + " " + (includeDsName ? dsName + "." : "") + name, prependSchema, dsName);
				} else
					curRow = this.tiles.addRow(name, collectionName, (includeDsName ? dsName + "." : "") + name, false, dsName);

				this.tiles2DsTables.put(curRow, t);
			}
			this.tiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, Math.min(w, 500));
			this.tiles.getTable().sortRows(SORTER_ROW, true);
			this.header.updateBarPortletLayout("Datasource: " + dsName + "<BR>" + this.tiles.getTilesCount() + " Tables Found");
		}

	}
	private void refreshPreview() {
		Map<String, Portlet> existingPreviews = new HashMap<String, Portlet>();
		for (Tab t : this.previewTabs.getTabs())
			existingPreviews.put(t.getTitle(), t.getPortlet());
		List<AmiDatasourceTable> t = getSelectedSchema();
		boolean hasMultipleDatasources = this.currentDsList.size() > 1;
		for (int i = 0; i < t.size(); i++) {
			AmiDatasourceTable table = t.get(i);
			if (existingPreviews.remove(table.getName()) == null && table.getPreviewData() != null) {
				FastTablePortlet tablePortlet = new FastTablePortlet(generateConfig(), (TableListenable) new BasicTable(table.getPreviewData()), "Preview Records");
				tablePortlet.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, "true");
				tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_COLOR, "#ffffcc");
				tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BAR_FONT_COLOR, "#FF7E00");
				tablePortlet.addOption(FastTablePortlet.OPTION_TITLE_BAR_FONT_COLOR, "#FF7E00");
				tablePortlet.addOption(FastTablePortlet.OPTION_SEARCH_BAR_COLOR, "#FFFFDD");
				int n = 0;
				for (Column column : table.getPreviewData().getColumns()) {
					String columnId = column.getId();
					BasicWebColumn col = tablePortlet.getTable().addColumnAt(true, columnId, columnId, this.service.getFormatterManager().getFormatter(column.getType()), n++);
					tablePortlet.autoSizeColumn(col);
				}
				tablePortlet.setFormStyle(getManager().getStyleManager().getFormStyle());
				tablePortlet.setDialogStyle(getManager().getStyleManager().getDialogStyle());
				String name = table.getPreviewData().getTitle();
				if (SH.isnt(name))
					name = (hasMultipleDatasources ? table.getDatasourceName() + "." : "") + table.getName();
				if (table.getPreviewTableSize() != null)
					name = (hasMultipleDatasources ? table.getDatasourceName() + "." : "") + name + " ("
							+ service.getFormatterManager().getIntegerWebCellFormatter().formatCellToText(table.getPreviewTableSize()) + " rows)";
				this.previewTabs.addChild(name, tablePortlet);
				getManager().onPortletAdded(tablePortlet);
			}
		}
		for (Portlet p : existingPreviews.values())
			p.close();
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		if ((Boolean) tile.get(TILE_PREPEND_SCHEMA) == false) {
			if (this.currentDsList.size() > 1) {
				sink.append("<BR>").append(tile.get(TILE_DATASOURCE)).append(".").append(tile.get(TILE_NAME)).append("</B>");
			} else {
				sink.append("<BR>").append(tile.get(TILE_NAME)).append("</B>");
			}
		} else {
			sink.append("<div class='ami_addDatamodelTiles'>").append(tile.get(TILE_COLLECTION)).append("<BR>").append(tile.get(TILE_NAME)).append("</div>");
		}
		if (selected)
			styleSink.append(
					"_fs=16|_fg=#ffffff|style.borderRadius=5px|_bg=#f16900|style.border=none|style.fontWeight=bold|style.textShadow=1px 1px 4px #000000|style.background=linear-gradient(to bottom, #f16900, #f5af19)");
		else
			styleSink.append(
					"_fs=16|_fg=#ffffff|style.borderRadius=5px|style.border=none|style.textShadow=1px 1px 4px #00000|style.background=linear-gradient(to bottom, #0e91bb, #77cefa)");
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {

	}

	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}

	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
	}

	@Override
	public void onSelectedChanged(TilesPortlet tiles) {
		List<AmiDatasourceTable> selectedTiles = getSelected();
		List<AmiDatasourceTable> requestTables = new ArrayList<AmiDatasourceTable>();
		for (int i = 0; i < selectedTiles.size(); i++) {
			AmiDatasourceTable table = selectedTiles.get(i);
			String tableName = table.getName();
			if (tableNames2PreviewSchema.containsKey(generateTableKey(table)))
				continue;
			else if (requestedTablesToWaitFor.add(tableName)) {
				requestTables.add(table);
			}
		}
		if (requestTables.size() != 0) {
			for (AmiDatasourceTable t : requestTables) {
				AmiCenterQueryDsRequest request = nw(AmiCenterQueryDsRequest.class);
				request.setDatasourceName(t.getDatasourceName());
				request.setType(AmiCenterQueryDsRequest.TYPE_PREVIEW);
				request.setTimeoutMs(service.getDefaultTimeoutMs());
				request.setOriginType(AmiCenterQueryDsRequest.ORIGIN_FRONTEND);
				request.setTablesForPreview(CH.l(requestTables));
				request.setPreviewCount(DEFAULT_PREVIEWCOUNT);
				request.setPermissions((byte) (AmiCenterQueryDsRequest.PERMISSIONS_READ | AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE));
				this.service.sendRequestToBackend(getPortletId(), request);
			}
		} else
			refreshPreview();
	}

	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {
	}
	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
	}

	private List<AmiDatasourceTable> getSelected() {
		Set<Row> selected = tiles.getSelectedTiles();
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(selected.size());
		for (Row row : selected)
			r.add(this.tiles2DsTables.get(row));
		Collections.sort(r, SORTER);
		return r;
	}
	public void isReadyForAddDatamodelStage() {
		boolean ready = requestedTablesToWaitFor.size() == 0;
		List<AmiDatasourceTable> selected = getSelected();
		for (int i = 0; i < selected.size(); i++) {
			if (requestedTablesToWaitFor.contains(selected.get(i).getName())) {
				ready = false;
				break;
			}
		}

		if (ready) {
			if (this.dialog != null) {
				this.dialog.close();
				this.dialog = null;
			}
		}
	}
	private void isReadyForSelectedSchema() {
		if (requestedTablesToWaitFor.size() == 0) {
			if (this.dialog != null) {
				this.dialog.close();
				this.dialog = null;
			}
		}
	}
	public ConfirmDialogPortlet prepareForSelectedSchema() {
		if (this.dialog == null) {
			this.dialog = new ConfirmDialogPortlet(generateConfig(), "Waiting for table schemas.", ConfirmDialogPortlet.TYPE_WAIT_WITH_CANCEL);
			this.dialog.setCallback("SCHEMA_READY");
			getManager().showDialog("Waiting", this.dialog);
		}
		return this.dialog;
	}
	public List<AmiDatasourceTable> getSelectedSchema() {
		Set<Row> selected = tiles.getSelectedTiles();
		List<AmiDatasourceTable> r = new ArrayList<AmiDatasourceTable>(selected.size());
		for (Row row : selected) {
			AmiDatasourceTable amiDatasourceTable = this.tiles2DsTables.get(row);
			AmiDatasourceTable t = this.tableNames2PreviewSchema.get(generateTableKey(amiDatasourceTable));
			if (t != null)
				r.add(t);
		}
		Collections.sort(r, SORTER);
		return r;
	}
	private static String generateTableKey(AmiDatasourceTable table) {
		return table.getDatasourceName() + "|" + table.getCollectionName() + "|" + table.getName();
	}
	public AmiWebDatasourceWrapper getDatasource() {
		return this.currentDsList.get(0);
	}
	public List<AmiWebDatasourceWrapper> getDatasources() {
		return this.currentDsList;
	}
	public void setDatasources(List<AmiWebDatasourceWrapper> dsList) {
		this.currentDsList = dsList;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {

	}
	@Override
	public void doSearch() {
		String value = this.header.getSearchInputValue();
		tiles.setFilter(SH.isnt(value) ? null : new TextMatcherRowFilter(this.tiles.getTable().getColumn(TILE_FULLNAME), SH.m(value.trim())));

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && formPortlet == this.header.getSearchFormPortlet()) {
			doSearch();
		}
	}

	public AmiWebHeaderPortlet getHeader() {
		return this.header;

	}

	@Override
	public void doSearchNext() {
	}
	@Override
	public void doSearchPrevious() {
	}

}
