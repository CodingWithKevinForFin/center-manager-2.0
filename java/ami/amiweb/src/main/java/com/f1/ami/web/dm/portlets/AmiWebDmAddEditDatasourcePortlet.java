package com.f1.ami.web.dm.portlets;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiDatasourcePlugin;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterManageDatasourceResponse;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.portlets.AmiWebHeaderSearchHandler;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebObject;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.Action;
import com.f1.base.Row;
import com.f1.base.TableList;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.TextMatcherRowFilter;

public class AmiWebDmAddEditDatasourcePortlet extends GridPortlet
		implements FormPortletContextMenuFactory, FormPortletContextMenuListener, FormPortletListener, TilesListener, TileFormatter, AmiWebHeaderSearchHandler {

	private FormPortlet permissionsForm;
	private FormPortlet advancedForm;
	private FormPortlet form;
	private FormPortlet buttonForm;
	private TabPortlet tabs;
	private FormPortletTextField nameField;
	private FormPortletTextField urlField;
	private FormPortletTextField usernameField;
	private FormPortletTextField passwordField;
	private FormPortletSelectField<String> relayId;
	//	private FormPortletTextField permittedOverridesField;
	private FormPortletButton submitButton;
	private FormPortletCheckboxField forceCheckbox;
	private FormPortletButton cancelButton;
	private FormPortletTitleField titleField;
	private final FormPortletDivField urlHelpDiv;
	private final AmiWebHeaderPortlet helpHeader;
	private AmiWebDatasourceWrapper ds;
	private long id;
	//	private AmiWebDmGraphPortlet graphPortlet;
	private boolean editDs;
	private String oldName;
	private static final Logger log = LH.get();
	private TilesPortlet tiles;
	private static final String TILE_NAME = "name";
	private static final String TILE_OBJECT_ID = "object_id";
	private static final String TILE_ICON = "icon";
	private static final int ROW_NUM_HELP_HEADER = 0;
	private static final int ROW_NUM_TILES = 1;
	private static final int ROW_NUM_FORM = 2;
	private static final int ROW_NUM_BUTTONS = 3;
	private final String helpHeaderTitle;
	private final static int TILE_HEIGHT_PX = 95;
	private final static int TILE_WIDTH_PX = 120;
	private final static int ICON_HEIGHT_PX = 75;
	private final static int ICON_WIDTH_PX = 100;
	private static final Comparator<? super Row> SORTER = new Comparator<Row>() {
		@Override
		public int compare(Row r1, Row r2) {
			return SH.COMPARATOR_CASEINSENSITIVE_STRING.compare((String) r1.get(TILE_NAME), (String) r2.get(TILE_NAME));
		}
	};
	private final Iterable<AmiWebObject> datasourceTypes;
	private final Iterable<AmiWebObject> relays;
	private final AmiWebService service;
	private int width;
	private int height;
	private String previousSearch;
	private TableList searchResults;

	public AmiWebDmAddEditDatasourcePortlet(PortletConfig config, AmiWebDatasourceWrapper ds, boolean editDs, boolean copyDs) {
		super(config);
		String action = editDs ? "Update" : (copyDs ? "Copy" : "Add");
		this.service = AmiWebUtils.getService(getManager());
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (root.getWidth() * 0.4));
		height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (root.getHeight() * 0.7));
		datasourceTypes = service.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_DATASOURCE_TYPE).getAmiObjects();
		relays = service.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_RELAY).getAmiObjects();
		String adapter;
		this.ds = ds;
		//		if (graphPortlet.getSelectedNodesList().size() > 0)
		//			this.ds = ((Data) graphPortlet.getSelectedNodesList().get(0).getData()).getDs();
		if ((editDs || copyDs) && this.ds != null) {
			adapter = this.ds.getAdapter();
		} else
			adapter = null;

		//		this.graphPortlet = graphPortlet;
		this.editDs = editDs;

		this.helpHeader = new AmiWebHeaderPortlet(generateConfig());
		this.helpHeader.setShowSearch(true);
		this.helpHeader.setLegendWidth(30);
		this.helpHeader.updateBlurbPortletLayout(this.helpHeaderTitle = (editDs ? "Edit " : (copyDs ? "Copy " : "Add ")) + "Datasource", "");
		int headerHeight = (int) (height * 0.08);
		this.helpHeader.setInformationHeaderHeight(headerHeight);
		this.helpHeader.setShowBar(true);
		addChild(this.helpHeader, 0, ROW_NUM_HELP_HEADER);
		this.helpHeader.setSearchHandler(this);
		this.tiles = new TilesPortlet(generateConfig());
		this.tiles.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#e2e2e2");
		addChild(this.tiles, 0, ROW_NUM_TILES);
		BasicSmartTable basic = new BasicSmartTable(new BasicTable(new String[] { TILE_NAME, TILE_OBJECT_ID, TILE_ICON }));
		this.tiles.setTable(basic);
		this.tiles.setTileFormatter(this);
		this.tiles.addTilesListener(this);
		this.tiles.setMultiselectEnabled(false);
		basic.sortRows((Comparator<Row>) SORTER, true);
		for (AmiWebObject obj : this.datasourceTypes) { // Add tile for each datasource type
			if (!"__AMI".equals(obj.getObjectId())) // Don't allow for addition of AMI datasources (yet)
				this.tiles.addRow(obj.getParam("Description"), obj.getObjectId(), obj.getParam("Icon"));
		}
		if (adapter != null) {
			TableList rowList = this.tiles.getTable().getRows();
			int rowIdx;
			for (rowIdx = 0; rowIdx < rowList.size(); rowIdx++) {
				if (adapter.equals(rowList.get(rowIdx).get(TILE_OBJECT_ID)))
					break;
			}
			this.tiles.setActiveTileByPosition(rowIdx);
		}
		int scaledTWidth = MH.min(140, (int) (width * 0.17));
		int scaledTHeight = MH.min(120, (int) (height * 0.17));
		this.tiles.addOption(TilesPortlet.OPTION_TILE_WIDTH, scaledTWidth);
		this.tiles.addOption(TilesPortlet.OPTION_TILE_HEIGHT, scaledTHeight);
		this.tiles.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#F0F0F0");

		this.tabs = addChild(new TabPortlet(generateConfig()), 0, ROW_NUM_FORM);
		this.tabs.getTabPortletStyle().setBackgroundColor("#F0F0F0");
		this.buttonForm = addChild(new FormPortlet(generateConfig()), 0, ROW_NUM_BUTTONS);
		this.tabs.setIsCustomizable(false);
		form = new FormPortlet(generateConfig());
		this.permissionsForm = new FormPortlet(generateConfig());
		this.advancedForm = new FormPortlet(generateConfig());
		this.tabs.addChild("Configuration", form);
		this.tabs.addChild("Security", permissionsForm);
		this.tabs.addChild("Advanced", advancedForm);
		int dsDisplaySize = (int) (height * 0.3);
		this.setRowSize(ROW_NUM_HELP_HEADER, this.helpHeader.getHeaderHeight());
		this.setRowSize(ROW_NUM_TILES, dsDisplaySize);
		this.setRowSize(ROW_NUM_BUTTONS, 40);

		Row activeTile = this.tiles.getActiveTile();
		this.titleField = new FormPortletTitleField("");
		this.titleField.setValue((activeTile == null ? "" : activeTile.get(TILE_NAME)) + " Configuration");
		form.addField(this.titleField);
		nameField = form.addField(new FormPortletTextField(AmiConsts.RESERVED_PARAMS.get(AmiConsts.PARAM_DATASOURCE_NAME) + ": "));
		urlField = form.addField(new FormPortletTextField(AmiConsts.RESERVED_PARAMS.get(AmiConsts.PARAM_DATASOURCE_URL) + ": ")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		this.urlHelpDiv = (FormPortletDivField) this.form.addField(new FormPortletDivField("")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		updateHelp((editDs || copyDs) && this.ds != null && !"__AMI".equals(this.ds.getAdapter()));
		usernameField = form.addField(new FormPortletTextField(AmiConsts.RESERVED_PARAMS.get(AmiConsts.PARAM_DATASOURCE_USER) + ": ")).setValue("");
		passwordField = form.addField(new FormPortletTextField(AmiConsts.RESERVED_PARAMS.get(AmiConsts.PARAM_DATASOURCE_PASSWORD) + ": ")).setPassword(true).setValue("");

		this.relayId = form.addField(new FormPortletSelectField<String>(String.class, "Relay To Run on: "));
		this.forceCheckbox = form.addField(new FormPortletCheckboxField("Skip Test and force " + action));
		this.forceCheckbox.setHelp("For Advanced users: This allows you to add a datasource that is invalid");
		this.forceCheckbox.setLabelPaddingPx(5);
		this.forceCheckbox.setBottomPosPx(10);
		this.forceCheckbox.setRightPosPx(10);
		this.forceCheckbox.setLabelWidthPx(400);
		this.relayId.addOption(null, "<No Relay, Run on Center>");
		for (AmiWebObject i : this.relays) {
			String relayId = (String) i.getParam(AmiConsts.PARAM_RELAY_RELAY_ID);
			String hostname = (String) i.getParam(AmiConsts.PARAM_RELAY_HOSTNAME);
			this.relayId.addOptionNoThrow(relayId, relayId + " (" + hostname + ") ");
		}

		this.permissionsForm.addField(new FormPortletDivField("")).setWidth(FormPortletTextField.WIDTH_STRETCH).setValue("Select fields that can be overridden at runtime");
		for (String s : AmiConsts.PERMITTED_DS_OVERRIDE_OPTIONS) {
			FormPortletCheckboxField field = new FormPortletCheckboxField(s);
			field.setId(s);
			this.permissionsForm.addField(field);

		}
		//		permittedOverridesField = this.permissionsForm.addField(new FormPortletTextField(AmiConsts.RESERVED_PARAMS.get(AmiConsts.PARAM_DATASOURCE_PERMITTED_OVERRIDES) + ": "))
		//				.setValue("");

		if ((editDs || copyDs) && this.ds != null) { // populate text fields
			urlField.setValue(this.ds.getUrl());
			usernameField.setValue(this.ds.getUser());
			//			permittedOverridesField.setValue(this.ds.getPermittedOverrides());
			if (!relayId.containsOption(this.ds.getRelayId()))
				this.relayId.addOption(this.ds.getRelayId(), this.ds.getRelayId() + " (relay not available) ");
			relayId.setValue(this.ds.getRelayId());
			if (editDs) {
				oldName = this.ds.getName();
				nameField.setValue(this.ds.getName());
				id = this.ds.getId();
			} else {
				id = -1;
				Set<String> names = new HashSet<String>();
				for (AmiWebDatasourceWrapper i : this.service.getSystemObjectsManager().getDatasources()) {
					names.add(i.getName());
				}
				String copyName = SH.getNextId(this.ds.getName(), names);
				nameField.setValue(copyName);
			}
			Set<String> permitted = SH.splitToSet(",", OH.noNull(this.ds.getPermittedOverrides(), ""));
			for (String p : permitted) {
				FormPortletCheckboxField field = (FormPortletCheckboxField) this.permissionsForm.getField(p);
				field.setValue(true);
			}
			Map<String, String> options = SH.splitToMap(',', '=', '\\', OH.noNull(this.ds.getOptions(), ""));
			for (Entry<String, String> p : options.entrySet()) {
				FormPortletTextField field = (FormPortletTextField) this.advancedForm.getField(p.getKey());
				if (field != null)
					field.setValue(p.getValue());
			}
		} else {
			id = -1;
		}

		form.setMenuFactory(this);
		form.addMenuListener(this);
		form.addFormPortletListener(this);
		buttonForm.setMenuFactory(this);
		buttonForm.addMenuListener(this);
		buttonForm.addFormPortletListener(this);

		this.submitButton = buttonForm.addButton(new FormPortletButton(action + " Datasource"));
		this.cancelButton = buttonForm.addButton(new FormPortletButton("Cancel"));
		setSuggestedSize(width, height);
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton)
			close();
		else if (button == this.submitButton) {
			if (this.tiles.getActiveTile() == null) {
				getManager().showAlert("Please select a database type");
				return;
			}
			String name = this.nameField.getValue();
			if (name == null || "".equals(name)) {
				getManager().showAlert("Please specify a datasource name");
				return;
			}
			if (!AmiUtils.isValidVariableName(name, false, false, false)) {
				getManager().showAlert("Invalid datasource name: <B>" + name + "</B>");
				return;
			}

			// Check for duplicate datasource names
			Set<String> names = new HashSet<String>();
			for (AmiWebDatasourceWrapper i : this.service.getSystemObjectsManager().getDatasources())
				names.add(i.getName());
			if (names.contains(name) && !(editDs && name.equals(oldName))) {
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "A datasource with the name \"" + name + "\" already exists. Please choose another name.",
						ConfirmDialogPortlet.TYPE_MESSAGE);
				getManager().showDialog("Duplicate datasource name", dialog);
				return;
			}

			AmiCenterManageDatasourceRequest req = nw(AmiCenterManageDatasourceRequest.class);
			req.setId(id);
			req.setName(nameField.getValue());
			req.setAdapter((String) this.tiles.getActiveTile().get(TILE_OBJECT_ID));
			req.setUrl(urlField.getValue());
			req.setUsername(usernameField.getValue());
			req.setPassword(passwordField.getValue());
			StringBuilder permitted = new StringBuilder();
			for (String f : CH.sort(this.permissionsForm.getFields())) {
				if (Boolean.TRUE.equals(this.permissionsForm.getField(f).getValue())) {
					if (permitted.length() > 0)
						permitted.append(',');
					permitted.append(f);
				}
			}
			req.setPermittedOverrides(permitted.toString());
			Map<String, String> options = new TreeMap<String, String>();
			for (String f : CH.sort(this.advancedForm.getFields())) {
				String value = (String) this.advancedForm.getField(f).getValue();
				if (SH.is(value))
					options.put(f, value);
			}
			req.setOptions(SH.joinMap(',', '=', '\\', options));
			req.setDelete(false);
			req.setEdit(editDs);
			req.setSelectedName(oldName);
			req.setSkipTest(this.forceCheckbox.getBooleanValue());
			req.setRelayId(this.relayId.getValue());

			AmiWebUtils.getService(getManager()).sendRequestToBackend(this, req);
			this.buttonForm.clearButtons();
			this.buttonForm.addButton(new FormPortletButton("Waiting for Response...")).setEnabled(false);

		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (formPortlet == this.helpHeader.getSearchFormPortlet() && keycode == 13 && OH.ne(previousSearch, helpHeader.getSearchInputValue())) {
			doSearch();
		}
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {

	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getAction();
		LH.info(log, "Backend action: " + action);
		if (action instanceof AmiCenterManageDatasourceResponse) {
			AmiCenterManageDatasourceResponse mgDsResp = (AmiCenterManageDatasourceResponse) action;
			if (mgDsResp.getAddSuccessful()) {
				getManager().showAlert("Success: " + (this.editDs ? "Updated" : "Added") + " datasource. Found " + mgDsResp.getTables().size() + " tables.");
				close();
			} else {
				Exception e = mgDsResp.getException();
				if (e != null)
					getManager().showAlert(e.getMessage(), e);
				else
					getManager().showAlert(mgDsResp.getMessage());
				this.buttonForm.clearButtons();
				this.buttonForm.addButton(this.submitButton);
				this.buttonForm.addButton(this.cancelButton);
			}
		}
	}

	private static final String BASE_ICON_STYLE = "_fs=12|_fm=bold|_bg=white|_fg=#004400|style.backgroundRepeat=no-repeat" + //
			"|style.justifyContent=center|style.backgroundPosition=top center" + //
			"|style.display=flex|style.alignItems=flex-end" + //
			"|style.backgroundSize=" + ((ICON_WIDTH_PX * 100.0) / TILE_WIDTH_PX) + "% " + ((ICON_HEIGHT_PX * 100.0) / TILE_HEIGHT_PX) + "%";

	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		styleSink.append("style.background=#ffffff|");
		if (tile.get(TILE_ICON) != null) {
			String icon = tile.get(TILE_ICON, Caster_String.INSTANCE);
			styleSink.append(BASE_ICON_STYLE).append("|style.backgroundImage=url(");
			styleSink.append(SH.quote("rsc/ami/database_icons/" + icon));
			styleSink.append(')');
			sink.append(tile.get(TILE_NAME, Caster_String.INSTANCE));
		} else {
			sink.append("<BR><BR>").append(tile.get(TILE_NAME)).append("</B></B>");
		}
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {

	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		this.titleField.setValue((row == null ? "" : row.get(TILE_NAME)) + " Configuration");
		updateHelp(true);
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
	private void updateHelp(boolean showHelp) {
		this.urlHelpDiv.setVisible(showHelp);
		if (showHelp) {
			for (AmiWebObject obj : datasourceTypes) {
				if (obj.getObjectId().equals(this.tiles.getActiveTile().get(TILE_OBJECT_ID))) {
					Map<String, Object> propertiesMap = AmiWebDmUtils.getDatasourceProperties(getManager(), obj);
					Map<String, String> datasourceHelp = (Map<String, String>) propertiesMap.get(AmiDatasourcePlugin.HELP);
					if (datasourceHelp != null)
						this.urlHelpDiv.setValue((String) datasourceHelp.get(AmiDatasourcePlugin.HELP_URL));
					else {
						this.urlHelpDiv.setValue("");
					}
					Map<String, String> optionsValues = new HashMap<String, String>();
					for (String f : CH.sort(this.advancedForm.getFields())) {
						String value = (String) this.advancedForm.getField(f).getValue();
						if (SH.is(value))
							optionsValues.put(f, value);
					}
					this.advancedForm.clearFields();
					Map<String, String> options = (Map<String, String>) propertiesMap.get(AmiDatasourcePlugin.HELP_OPTIONS);
					if (options != null) {
						options = new TreeMap<String, String>(options);
						for (Entry<String, String> e : options.entrySet()) {
							FormPortletTextField field = new FormPortletTextField(AmiWebUtils.toPrettyName(e.getKey()));
							field.setId(e.getKey());
							this.advancedForm.addField(field);
							field.setWidth(FormPortletField.WIDTH_STRETCH);
							field.setHelp(e.getKey() + " - " + e.getValue());
							if (optionsValues.containsKey(e.getKey()))
								field.setValue(optionsValues.get(e.getKey()));
							field.setPassword(e.getKey().contains(AmiConsts.PASSWORD_KEYWORD));
						}
					}
				}
			}
		} else {
			this.helpHeader.updateBlurbPortletLayout(this.helpHeaderTitle, "");
		}
	}
	@Override
	public void doSearch() {
		String value = this.helpHeader.getSearchInputValue();
		tiles.setFilter(SH.isnt(value) ? null : new TextMatcherRowFilter(this.tiles.getTable().getColumn(TILE_NAME), SH.m(value.trim())));
		searchResults = tiles.getTable().getRows();
		previousSearch = value;
		if (!searchResults.isEmpty()) {
			tiles.setActiveTileByPosition(0);
			this.onTileClicked(this.tiles, this.tiles.getActiveTile());
		}
	}

	@Override
	public void doSearchNext() {
		if (searchResults != null && !searchResults.isEmpty()) {
			Row activeTile = tiles.getActiveTile();
			int i = searchResults.indexOf(activeTile);
			if (i != -1) {
				tiles.setActiveTileByPosition(MH.mod(i + i, searchResults.size()));
				this.onTileClicked(this.tiles, activeTile);
			}
		}

	}
	@Override
	public void doSearchPrevious() {
		if (searchResults != null && !searchResults.isEmpty()) {
			Row activeTile = tiles.getActiveTile();
			int i = searchResults.indexOf(activeTile);
			if (i != -1)
				tiles.setActiveTileByPosition(MH.mod(i - 1, searchResults.size()));
			this.onTileClicked(this.tiles, activeTile);
		}

	}
}
