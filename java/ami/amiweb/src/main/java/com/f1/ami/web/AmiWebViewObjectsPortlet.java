package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.scm.AmiWebScmHistoryPortlet;
import com.f1.base.Row;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabListener;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFieldAutoCompleteExtension;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.WebTableFilteredSetFilter;
import com.f1.utils.AH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple3;

public class AmiWebViewObjectsPortlet extends GridPortlet implements AmiWebSpecialPortlet, FormPortletListener, ConfirmDialogListener, TabListener {
	private static final Logger log = LH.get();

	public static final String REL_TAB_TITLE = "Relationships";
	public static final String DM_TAB_TITLE = "Datamodels";
	public static final String PANEL_TAB_TITLE = "Panels";
	public static final String CUSTOM_METHOD_TAB_TITLE = "Custom Methods";
	public static final int DEFAULT_SUGGESTION_LIMIT = 50;

	// context menu actions for scm and viewing/diffing cofigurations
	public static final String ACTION_SHOW_HISTORY = "show_scm_history";
	public static final String ACTION_DIFF_AGAINST_CHANGELIST = "diff_against_changelist";
	public static final String ACTION_DIFF_DIFFERENT_CHANGELIST = "diff_different_changelist";
	public static final String ACTION_SHOW_COFIGURATION = "show_config";
	public static final String ACTION_DIFF_CONFIGURATIONS = "diff_config";

	private final AmiWebService service;
	private final AmiScmAdapter scmAdapter;
	private final AmiWebDmManager dmManager;
	private final AmiWebLayoutFilesManager layoutFilesManager;
	private TabPortlet tabsContainer;
	private FormPortlet form; // form on the bottom with close button
	private FormPortletButton closeButton;
	private final DividerPortlet divider;
	private final FormPortlet searchForm; // form on top for advanced search
	private final FormPortletTextField searchField;
	private final FormPortletFieldAutoCompleteExtension<FormPortletTextEditField> autocompleteExt;
	private List<String> suggestions = new ArrayList<String>();
	private final FormPortletTitleField messageField;
	private final FormPortletButtonField searchButton;
	private final FormPortletButtonField clearSearchButton;

	private AmiWebScmHistoryPortlet history;

	private String previousSearchValue;
	private AmiWebViewRelationshipsPortlet rp;
	private AmiWebViewDataModelsPortlet dp;
	private AmiWebViewPanelsPortlet pp;
	private AmiWebViewCustomMethodsPortlet mp;

	int filteredRelCount;
	int filteredDmCount;
	int filteredPnlCount;
	int filteredCustMethodsCount;
	boolean isFilterActive = false;

	public AmiWebViewObjectsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.scmAdapter = this.service.getScmAdapter();
		this.dmManager = this.service.getDmManager();
		this.layoutFilesManager = this.service.getLayoutFilesManager();
		tabsContainer = new TabPortlet(generateConfig());
		tabsContainer.setIsCustomizable(false);

		AmiWebViewRelationshipsPortlet rp = new AmiWebViewRelationshipsPortlet(generateConfig(), service, this);
		tabsContainer.addChild(REL_TAB_TITLE, rp);
		this.rp = rp;

		AmiWebViewDataModelsPortlet dp = new AmiWebViewDataModelsPortlet(generateConfig(), service, this);
		tabsContainer.addChild(DM_TAB_TITLE, dp);
		this.dp = dp;

		AmiWebViewPanelsPortlet pp = new AmiWebViewPanelsPortlet(generateConfig(), service, this);
		tabsContainer.addChild(PANEL_TAB_TITLE, pp);
		this.pp = pp;

		AmiWebViewCustomMethodsPortlet mp = new AmiWebViewCustomMethodsPortlet(generateConfig(), service, this);
		tabsContainer.addChild(CUSTOM_METHOD_TAB_TITLE, mp);
		this.mp = mp;

		updateTabsTitle();

		this.divider = new DividerPortlet(generateConfig(), false);

		this.searchForm = new FormPortlet(generateConfig());
		this.searchField = new FormPortletTextField("Search").setValue("");

		// search suggestion autocomplete
		this.autocompleteExt = new FormPortletFieldAutoCompleteExtension<FormPortletTextEditField>(this.searchField);
		this.autocompleteExt.setShowOptionsImmediately(true);
		this.autocompleteExt.setSuggestions(this.suggestions);
		setSuggestionLimit(DEFAULT_SUGGESTION_LIMIT);

		this.messageField = new FormPortletTitleField("");
		this.previousSearchValue = null;
		this.clearSearchButton = new FormPortletButtonField("").setValue("Clear");
		this.searchButton = new FormPortletButtonField("").setValue("Search");
		positionSearchFields();
		this.searchForm.addField(searchField);
		this.searchForm.addField(searchButton);
		this.searchForm.addField(clearSearchButton);

		this.history = new AmiWebScmHistoryPortlet(generateConfig());

		this.divider.addChild(tabsContainer);
		this.divider.addChild(history);
		divider.setOffset(.65);

		this.form = new FormPortlet(generateConfig());
		this.closeButton = new FormPortletButton("Close");

		// add history table if scm is enabled
		if (scmAdapter != null) {
			this.addChild(searchForm, 0, 0);
			this.addChild(divider, 0, 1); // contains tabs and history portlet
			this.addChild(form, 0, 2);
			this.setRowSize(0, 40); // searchForm
			this.setRowSize(2, 40); // form on the bottom;
		} else {
			this.addChild(searchForm, 0, 0);
			this.addChild(tabsContainer, 0, 1);
			this.addChild(form, 0, 2);
			this.setRowSize(0, 40);
			this.setRowSize(2, 40);
		}

		this.form.addButton(closeButton);
		this.form.addFormPortletListener(this);
		this.searchForm.addFormPortletListener(this);
		this.tabsContainer.addTabListener(this);
	}
	public int getSuggestionLimit() {
		return autocompleteExt.getLimit();
	}
	public void setSuggestionLimit(int limit) {
		this.autocompleteExt.setLimit(limit);
	}
	public void resetFilteredCount() {
		this.filteredRelCount = 0;
		this.filteredDmCount = 0;
		this.filteredPnlCount = 0;
		this.filteredCustMethodsCount = 0;
	}
	private void positionSearchFields() {
		this.searchField.setLeftTopWidthHeightPx(50, 5, 300, 30);
		this.searchButton.setLeftTopWidthHeightPx(this.searchField.getLeftPosPx() + this.searchField.getWidthPx() + 10, 5, 60, 30);
		this.clearSearchButton.setLeftTopWidthHeightPx(this.searchButton.getLeftPosPx() + this.searchButton.getWidthPx() + 10, 5, 60, 30);
	}
	private void addFilterMessage() {
		if (!searchForm.hasField(this.messageField)) {
			this.messageField.setValue("Results paused while the search is active");
			this.messageField.setLeftTopWidthHeightPx(this.clearSearchButton.getLeftPosPx() + this.clearSearchButton.getWidthPx() + 10, 10, 300, 30);
			this.messageField.setCssStyle("_fs=12|_fg=#727272");
			this.searchForm.addField(this.messageField);
		}
	}
	public void addTableOptions(FastTablePortlet ftp) {
		ftp.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, true);
		ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, false);
		ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HEIGHT, 22);
		ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_SZ, 12);
		ftp.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);

	}
	public AmiScmAdapter getScmAdapter() {
		return this.scmAdapter;
	}
	private void removeFilterMessage() {
		if (searchForm.hasField(this.messageField))
			this.searchForm.removeField(this.messageField);
	}
	public TabPortlet getTabsContainer() {
		return this.tabsContainer;
	}
	public AmiWebViewRelationshipsPortlet getRelationshipsPortlet() {
		return this.rp;
	}
	public AmiWebViewDataModelsPortlet getDatamodelsPortlet() {
		return this.dp;
	}
	public AmiWebViewPanelsPortlet getPanelsPortlet() {
		return this.pp;
	}
	public AmiWebViewCustomMethodsPortlet getCustomMethodsPortlet() {
		return this.mp;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return false;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (OH.eq(button, this.closeButton))
			this.close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (OH.eq(field, clearSearchButton)) {
			this.searchField.setValue(null);
			clearSearch();
			resetFilteredCount();
			this.isFilterActive = false;
			updateTabsTitle();
			unsetSearchFieldStyle();
			removeFilterMessage();
		} else if (OH.eq(field, searchButton)) {
			onSpecialKeyPressed(portlet, this.searchField, 13, 0, -1);
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == Form.KEYCODE_ENTER) {
			String searchText = SH.trim(field.getValue().toString());
			if (SH.isnt(searchText) || SH.isEmpty(searchText)) {
				this.searchField.setValue(null);
				clearSearch();
				this.isFilterActive = false;
				resetFilteredCount();
				unsetSearchFieldStyle();
				removeFilterMessage();
				updateTabsTitle();
			} else if (!searchText.equalsIgnoreCase(previousSearchValue)) {
				handleSearchSuggestion(searchText);
				resetFilteredCount();
				this.isFilterActive = true;
				applySearch();
				updateTabsTitle();
				setSearchFieldStyle();
				addFilterMessage();
			}
			// otherwise do nothing: search text same as before.
		}
	}
	public void handleSearchSuggestion(String suggestion) {
		if (!this.autocompleteExt.getSuggestions().contains(suggestion)) {
			this.suggestions.add(0, suggestion);
			if (this.suggestions.size() > getSuggestionLimit()) {
				this.suggestions.remove(this.suggestions.size() - 1);
				OH.assertEq(this.suggestions.size(), getSuggestionLimit());
			}
			this.autocompleteExt.setSuggestions(this.suggestions);
		}
	}
	private void setSearchFieldStyle() {
		this.searchField.setBgColor("#FF7F00");
		this.searchField.setFontColor("#FFFFFF");
	}
	private void unsetSearchFieldStyle() {
		this.searchField.setBgColor("#FFFFFF");
		this.searchField.setFontColor("#000000");
	}
	private void clearSearch() {
		getRelationshipsPortlet().getTablePortlet().getTable().setExternalFilter(null);
		getDatamodelsPortlet().getTablePortlet().getTable().setExternalFilter(null);
		getPanelsPortlet().getTablePortlet().getTable().setExternalFilter(null);
		getCustomMethodsPortlet().getTablePortlet().getTable().setExternalFilter(null);
		getHistoryPortlet().setConfigSearchText(null);
	}
	public void applySearch() {
		this.service.getDesktop().setIsDoingExportTransient(true);
		try {
			searchOnRelationships(getEscapedSearchText());
			searchOnDataModels(getEscapedSearchText());
			searchOnPanels(getEscapedSearchText());
			searchOnCustomMethods(getEscapedSearchText());
			getHistoryPortlet().setConfigSearchText(getEscapedSearchText());
		} finally {
			this.service.getDesktop().setIsDoingExportTransient(false);
		}
	}
	private void searchOnRelationships(String searchText) {
		Set<String> relIdForFilter = new HashSet<String>();
		for (AmiWebDmLink link : dmManager.getDmLinks()) {
			Map<String, Object> config = link.getConfiguration();
			String jsonText = layoutFilesManager.toJson(config);
			if (SH.indexOfIgnoreCase(jsonText, searchText, 0) != -1) {
				Row matchedRow = getRelationshipsPortlet().getRowByLinkUid(link.getLinkUid());
				if (matchedRow != null) {
					relIdForFilter.add((String) matchedRow.get(AmiWebViewRelationshipsPortlet.COLUMN_REL_ID));
					this.filteredRelCount++;
				} else
					LH.warning(log, "Row not found for link: " + link);
			}
		}
		WebColumn columnForFilter = getRelationshipsPortlet().getTablePortlet().getTable().getColumn(AmiWebViewRelationshipsPortlet.COLUMN_REL_ID);
		getRelationshipsPortlet().getTablePortlet().getTable().setExternalFilter(new WebTableFilteredSetFilter(columnForFilter, relIdForFilter));
	}
	private void searchOnDataModels(String searchText) {
		Set<String> dmAdnForFilter = new HashSet<String>();
		for (AmiWebDm dm : dmManager.getDatamodels()) {
			Map<String, Object> json = dm.getConfiguration();
			String jsonText = layoutFilesManager.toJson(json);
			if (SH.indexOfIgnoreCase(jsonText, searchText, 0) != -1) {
				Row matchedRow = getDatamodelsPortlet().getRowByDmAdn(dm.getAmiLayoutFullAliasDotId());
				if (matchedRow != null) {
					dmAdnForFilter.add((String) matchedRow.get(AmiWebViewDataModelsPortlet.COLUMN_DM_ADN));
					this.filteredDmCount++;
				} else
					LH.warning(log, "Row not found for datamodel: " + dm);
			}
		}
		WebColumn columnForFilter = getDatamodelsPortlet().getTablePortlet().getTable().getColumn(AmiWebViewDataModelsPortlet.COLUMN_DM_ADN);
		getDatamodelsPortlet().getTablePortlet().getTable().setExternalFilter(new WebTableFilteredSetFilter(columnForFilter, dmAdnForFilter));
	}
	private void searchOnPanels(String searchText) {
		Set<String> panelIdForFilter = new HashSet<String>();
		for (AmiWebAliasPortlet visiblePanel : AmiWebUtils.getVisiblePanels(service)) {
			Map<String, Object> config = visiblePanel.getConfiguration();
			String jsonText = layoutFilesManager.toJson(config);
			if (SH.indexOfIgnoreCase(jsonText, searchText, 0) != -1) {
				Row matchedRow = getPanelsPortlet().getRowByPortletId(visiblePanel.getPortletId());
				if (matchedRow != null) {
					panelIdForFilter.add((String) matchedRow.get(AmiWebViewPanelsPortlet.COLUMN_PANEL_ID));
					this.filteredPnlCount++;
				} else
					LH.warning(log, "Row not found for visible panel: " + visiblePanel);
			}
		}

		for (AmiWebPortletDef pd : AmiWebUtils.getHiddenPanels(service, true)) {
			Map<String, Object> json = pd.getPortletConfig();
			String jsonText = layoutFilesManager.toJson(json);
			if (SH.indexOfIgnoreCase(jsonText, searchText, 0) != -1) {
				Row matchedRow = getPanelsPortlet().getRowByPanelId(pd.getAmiPanelId());
				if (matchedRow != null) {
					panelIdForFilter.add((String) matchedRow.get(AmiWebViewPanelsPortlet.COLUMN_PANEL_ID));
					this.filteredPnlCount++;
				} else
					LH.warning(log, "Row not found for hidden panel: " + pd);
			}
		}
		WebColumn columnForFilter = getPanelsPortlet().getTablePortlet().getTable().getColumn(AmiWebViewPanelsPortlet.COLUMN_PANEL_ID);
		getPanelsPortlet().getTablePortlet().getTable().setExternalFilter(new WebTableFilteredSetFilter(columnForFilter, panelIdForFilter));
	}
	private void searchOnCustomMethods(String searchText) {
		Set<String> rowIdsFilterVals = new HashSet<String>();
		for (Tuple3<String, String, Row> t : getCustomMethodsPortlet().getOwningLayoutAndCustomMethodNames()) {
			String layoutName = t.getA();
			String methodName = t.getB();
			String jsonText = getCustomMethodsPortlet().getCustomMethodCodeJsonForMethod(layoutName, methodName);
			if (SH.indexOfIgnoreCase(jsonText, searchText, 0) != -1) {
				Row matchedRow = getCustomMethodsPortlet().getRowByLayoutAndMethodName(layoutName, methodName);
				if (matchedRow != null) {
					rowIdsFilterVals.add(layoutName + "." + methodName);
					this.filteredCustMethodsCount++;
				} else
					LH.warning(log, "Row not found for custom method for the layout: " + layoutName);
			}
		}
		WebColumn columnForFilter = getCustomMethodsPortlet().getTablePortlet().getTable().getColumn(AmiWebViewCustomMethodsPortlet.COLUMN_ROW_ID);
		getCustomMethodsPortlet().getTablePortlet().getTable().setExternalFilter(new WebTableFilteredSetFilter(columnForFilter, rowIdsFilterVals));
	}
	public void updateTabsTitle() {
		Tab relTab = tabsContainer.getTabForPortlet(getRelationshipsPortlet());
		Tab dmTab = tabsContainer.getTabForPortlet(getDatamodelsPortlet());
		Tab panelTab = tabsContainer.getTabForPortlet(getPanelsPortlet());
		Tab custMethodsTab = tabsContainer.getTabForPortlet(getCustomMethodsPortlet());

		if (isFilterActive) {
			relTab.setTitle(REL_TAB_TITLE + " (" + this.filteredRelCount + ")");
			dmTab.setTitle(DM_TAB_TITLE + " (" + this.filteredDmCount + ")");
			panelTab.setTitle(PANEL_TAB_TITLE + " (" + this.filteredPnlCount + ")");
			custMethodsTab.setTitle(CUSTOM_METHOD_TAB_TITLE + " (" + this.filteredCustMethodsCount + ")");
		} else {
			relTab.setTitle(REL_TAB_TITLE + " (" + getRelationshipsPortlet().getRelCount() + ")");
			dmTab.setTitle(DM_TAB_TITLE + " (" + getDatamodelsPortlet().getDmCount() + ")");
			panelTab.setTitle(PANEL_TAB_TITLE + " (" + getPanelsPortlet().getPanelsCount() + ")");
			custMethodsTab.setTitle(CUSTOM_METHOD_TAB_TITLE + " (" + getCustomMethodsPortlet().getCustomMethodsCount() + ")");
		}
		Tab lonelyTab = this.isFilterActive ? getLonelyTabAfterSearch() : getLonelyTab();
		if (lonelyTab != null)
			this.tabsContainer.setActiveTab(lonelyTab.getPortlet());
	}
	private Tab getLonelyTabAfterSearch() {
		int[] filteredCounts = new int[] { this.filteredRelCount, this.filteredDmCount, this.filteredPnlCount, this.filteredCustMethodsCount };
		AH.sort(filteredCounts);
		int size = filteredCounts.length;
		OH.assertEq(size, 4);
		if (filteredCounts[size - 1] != 0 && filteredCounts[size - 2] == 0) {
			int lonelyTabRowCount = filteredCounts[size - 1];
			if (lonelyTabRowCount == this.filteredRelCount)
				return tabsContainer.getTabForPortlet(this.rp);
			else if (lonelyTabRowCount == this.filteredDmCount)
				return tabsContainer.getTabForPortlet(this.dp);
			else if (lonelyTabRowCount == this.filteredPnlCount)
				return tabsContainer.getTabForPortlet(this.pp);
			else if (lonelyTabRowCount == this.filteredCustMethodsCount)
				return tabsContainer.getTabForPortlet(this.mp);
		}
		return null;
	}
	private Tab getLonelyTab() {
		int[] objectCounts = new int[] { getRelationshipsPortlet().getRelCount(), getDatamodelsPortlet().getDmCount(), getPanelsPortlet().getPanelsCount(),
				getCustomMethodsPortlet().getCustomMethodsCount() };
		objectCounts = AH.sort(objectCounts);
		int size = objectCounts.length;
		OH.assertEq(size, 4);
		if (objectCounts[size - 1] != 0 && objectCounts[size - 2] == 0) {
			int lonelyTabRowCount = objectCounts[size - 1];
			if (lonelyTabRowCount == getRelationshipsPortlet().getRelCount())
				return tabsContainer.getTabForPortlet(this.rp);
			else if (lonelyTabRowCount == getDatamodelsPortlet().getDmCount())
				return tabsContainer.getTabForPortlet(this.dp);
			else if (lonelyTabRowCount == getPanelsPortlet().getPanelsCount())
				return tabsContainer.getTabForPortlet(this.pp);
			else if (lonelyTabRowCount == getCustomMethodsPortlet().getCustomMethodsCount())
				return tabsContainer.getTabForPortlet(this.mp);
		}
		return null;
	}
	@Override
	public void onClosed() {
		resetFilteredCount();
		getRelationshipsPortlet().resetRelCount();
		getDatamodelsPortlet().resetDmCount();
		getPanelsPortlet().resetPanelsCount();
		getCustomMethodsPortlet().resetCustomMethodsCount();
		super.onClosed();
	}
	public String getSearchText() {
		if (SH.isnt(this.searchField.getValue()) || SH.isEmpty(this.searchField.getValue()))
			return null;
		return this.searchField.getValue();
	}
	public String getEscapedSearchText() {
		if (SH.isnt(this.searchField.getValue()) || SH.isEmpty(this.searchField.getValue()))
			return null;
		return SH.escape(this.searchField.getValue(), '"', '\\');
	}
	public void showObjectHistory(String fileName) {
		history.getHistory(new ArrayList<String>(Arrays.asList(fileName)));
	}
	@Override
	public void onTabSelected(TabPortlet tabPortlet, Tab tab) {
		clearHistoryTable();
	}
	public void clearHistoryTable() {
		this.history.getTablePortlet().clearRows();
	}
	public FastWebTable getHistoryTable() {
		return this.history.getTablePortlet().getTable();
	}
	public AmiWebScmHistoryPortlet getHistoryPortlet() {
		return this.history;
	}
	@Override
	public void onTabRemoved(TabPortlet tabPortlet, Tab tab) {
	}
	@Override
	public void onTabAdded(TabPortlet tabPortlet, Tab tab) {
	}
	@Override
	public void onTabMoved(TabPortlet tabPortlet, int origPosition, Tab tab) {
	}
	@Override
	public void onTabClicked(TabPortlet tabPortlet, Tab curTab, Tab prevTab, boolean onArrow) {
	}
}
