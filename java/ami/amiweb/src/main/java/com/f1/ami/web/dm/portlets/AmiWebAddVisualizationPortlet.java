package com.f1.ami.web.dm.portlets;

import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebPanelPluginWrapper;
import com.f1.ami.web.AmiWebPluginPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_3dChart;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_Chart;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_Form;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_Heatmap;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_Table;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz_TreeGrid;
import com.f1.ami.web.style.AmiWebStyleManager;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.Row;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.RootPortletDialogListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.OH;

public class AmiWebAddVisualizationPortlet extends GridPortlet
		implements TilesListener, FormPortletListener, ConfirmDialogListener, RootPortletDialogListener, AmiWebSpecialPortlet {

	private static final String BG_GREY = "_bg=#4c4c4c";
	private static final String TABLE_FORM_FIELD_STYLE = BG_GREY + "|_fm=courier,bold|_fg=#ffffff|_fs=18px|style.border=0px";
	private AmiWebVizwiz vizwiz;
	private String vizwizTableName;
	private byte vizwizType;
	private Portlet initForm;
	private GridPortlet initGrid;
	private AmiWebDm dm;
	final private AmiWebDmPanelTypesPortlet types;
	final private DividerPortlet divPortlet;
	final private GridPortlet previewGrid;
	final private GridPortlet creatorGrid;
	final private AmiWebService service;
	final private FormPortlet tableForm;
	final private FormPortletSelectField<String> tablesField;
	final private HtmlPortlet blankPreview;
	final private HtmlPortlet blankCreator;
	final private FormPortlet buttonsPanel;
	final private InnerPortlet previewPanel;
	final private FormPortletSelectField<String> aliasField;
	final private FormPortletTextField panelIdField;
	final private InnerPortlet creatorPanel;
	private FormPortlet initFormButtons;
	private FormPortletButton initFormCreateButton;
	private FormPortletButton submitButton;
	private FormPortlet bottomButtonsPortlet;
	private String selectedTable;

	public AmiWebAddVisualizationPortlet(PortletConfig config, AmiWebDm dm, String baseAlias) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());

		{//Init Portlets
			this.types = new AmiWebDmPanelTypesPortlet(generateConfig(), false, true);
			this.previewGrid = new GridPortlet(generateConfig());
			this.creatorGrid = new GridPortlet(generateConfig());
			this.divPortlet = new DividerPortlet(generateConfig(), true, this.creatorGrid, this.previewGrid);
			this.blankCreator = new HtmlPortlet(generateConfig());
			this.buttonsPanel = new FormPortlet(generateConfig());
			this.blankPreview = new HtmlPortlet(generateConfig());
			this.tableForm = new FormPortlet(generateConfig());

			this.tablesField = new FormPortletSelectField<String>(String.class, "TABLE:");
			this.panelIdField = new FormPortletTextField("PANEL ID:");
			this.aliasField = new FormPortletSelectField<String>(String.class, "OWNING LAYOUT:");
			this.tableForm.addField(tablesField);
			this.tableForm.addField(aliasField);
			this.tableForm.addField(panelIdField);
		}

		{//Init Grid
			this.creatorPanel = this.creatorGrid.addChild(blankCreator.setCssStyle(BG_GREY), 0, 0, 1, 1);
			this.previewGrid.addChild(buttonsPanel, 0, 0, 1, 1).setPaddingL(4);
			this.previewPanel = this.previewGrid.addChild(blankPreview.setCssStyle(BG_GREY), 0, 1, 1, 1);
			this.previewGrid.setRowSize(0, 90);
			this.addChild(tableForm, 0, 0, 1, 1).setPadding(20, 0, 20, 20);
			this.addChild(types, 1, 0, 1, 1).setPadding(20);
			this.addChild(this.divPortlet, 0, 1, 2, 1);
			this.setRowSize(0, 160);
			this.setColSize(0, 300);
		}

		{//init styles
			this.divPortlet.setColor("#4c4c4c");
			this.blankPreview.setCssStyle(BG_GREY + "|_fm=center|_fs=18|_fg=#ffffff").setHtml("<P>Preview Panel<P><I>(choose a visualization type above)");
			this.previewPanel.setCssStyle("style.boxShadow=0px 0px  10px 2px #000000").setPadding(10, 10, 10, 10);
			this.previewGrid.setCssStyle(BG_GREY);
			this.buttonsPanel.getFormPortletStyle().setCssStyle(BG_GREY);
			this.buttonsPanel.getFormPortletStyle().setButtonPanelStyle("_bg=#4c4c4c|_fm=left");
			this.buttonsPanel.getFormPortletStyle().setButtonsStyle("_bg=#007608|_fg=#FFFFFF|style.border=1px solid #a8a8a8|style.minWidth=75px|_fn=arial|_fs=17");
			this.buttonsPanel.getFormPortletStyle().setButtonPaddingT(30);
			this.buttonsPanel.getFormPortletStyle().setButtonPaddingB(0);
			this.tableForm.getFormPortletStyle().setLabelsWidth(300);
			this.tablesField.setCssStyle(TABLE_FORM_FIELD_STYLE);
			this.tablesField.setLabelCssStyle(TABLE_FORM_FIELD_STYLE);
			this.panelIdField.setCssStyle(TABLE_FORM_FIELD_STYLE);
			this.panelIdField.setLabelCssStyle(TABLE_FORM_FIELD_STYLE);
			this.aliasField.setCssStyle(TABLE_FORM_FIELD_STYLE);
			this.aliasField.setLabelCssStyle(TABLE_FORM_FIELD_STYLE);
			this.tablesField.setHeight(18);
			this.tableForm.getFormPortletStyle().setCssStyle(BG_GREY + "|_fm=courier|_fg=#e2e2e2|_fs=18px");
			this.setCssStyle(BG_GREY);
		}

		{//Init listeners
			this.buttonsPanel.addFormPortletListener(this);
			this.types.addTilesListener(this);
			this.tableForm.addFormPortletListener(this);
		}

		if (dm != null) {
			this.bottomButtonsPortlet = new FormPortlet(generateConfig());
			addChild(this.bottomButtonsPortlet, 0, 2, 2, 1);
			setRowSize(2, 40);
			this.submitButton = this.bottomButtonsPortlet.addButton(new FormPortletButton("Submit"));
			this.bottomButtonsPortlet.addFormPortletListener(this);
			showChooseTableDialog(dm);
			setDm(dm);
		}
	}

	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}

	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		if (this.types == table && this.types.getSelectedType() != -1)
			confirmRebuildViz();
	}

	@Override
	public void onSelectedChanged(TilesPortlet tiles) {
	}

	public void rebuildVizwiz() {
		if (this.types.getSelectedType() != -1 && service.getLayoutFilesManager().getLayoutByFullAlias(aliasField.getValue()).isReadonly()) {
			getManager().showAlert("Layout is readonly: " + WebHelper.escapeHtml(AmiWebUtils.formatLayoutAlias(aliasField.getValue()))
					+ "<BR>(change Owning layout using the drop down on the upper left)");
			clearVizwiz();
			return;
		}
		this.creatorPanel.setPortlet(blankCreator);
		this.divPortlet.setOffsetFromTopPx(400);
		this.previewPanel.setPortlet(blankPreview);
		this.buttonsPanel.clearButtons();
		this.previewGrid.setRowSize(0, 0);
		if (this.vizwiz != null) {
			vizwiz.getPreviewPortlet().close();
			vizwiz.getCreatorPortlet().close();
		}
		this.vizwiz = null;
		AmiWebVizwiz<?> wiz;
		switch (this.types.getSelectedType()) {
			case AmiWebDmPanelTypesPortlet.TYPE_STATIC:
				wiz = new AmiWebVizwiz_Table(service, aliasField.getValue());
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_TREEMAP_STATIC:
				wiz = new AmiWebVizwiz_Heatmap(service, aliasField.getValue());
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_TREE:
				wiz = new AmiWebVizwiz_TreeGrid(service, aliasField.getValue());
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_CHART_3D:
				wiz = new AmiWebVizwiz_3dChart(service, aliasField.getValue());
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_CHART_STATIC:
				wiz = new AmiWebVizwiz_Chart(service, aliasField.getValue());
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_PLUGIN:
				final String pluginId = this.types.getSelectedPluginId();
				AmiWebPanelPluginWrapper plugin = service.getPanelPlugins().get(pluginId);
				AmiWebPluginPortlet t = (AmiWebPluginPortlet) service.getDesktop().newPortlet(plugin.getPortletBuilderId(), aliasField.getValue());
				wiz = plugin.getPlugin().createVizwiz(service, t);
				break;
			case AmiWebDmPanelTypesPortlet.TYPE_FORM:
				wiz = new AmiWebVizwiz_Form(service, aliasField.getValue());
				break;
			default:
				return;
		}
		AmiWebStyledPortlet t = wiz.getPreviewPortlet();
		t.getStylePeer().setParentStyle(AmiWebStyleManager.LAYOUT_DEFAULT_ID);
		setVizwiz(wiz);
	}
	private void setVizwiz(AmiWebVizwiz<?> vizwiz) {
		this.vizwiz = vizwiz;
		this.vizwizTableName = this.tablesField.getValue();
		this.vizwizType = this.types.getSelectedType();
		this.initForm = this.vizwiz.getInitForm(this.dm, this.vizwizTableName);
		if (this.initForm != null) {
			this.initGrid = new GridPortlet(generateConfig());
			this.initGrid.addChild(this.initForm, 0, 0);
			this.initFormButtons = new FormPortlet(generateConfig());
			this.initGrid.addChild(this.initFormButtons, 0, 1);
			this.initGrid.setRowSize(1, 40);
			this.initFormCreateButton = this.initFormButtons.addButton(new FormPortletButton("Create " + this.vizwiz.getName()).setId("CREATE"));
			this.initFormButtons.addButton(new FormPortletButton("Cancel").setId("CANCEL"));
			RootPortletDialog dialog = getManager().showDialog("Create " + this.vizwiz.getName(), this.initGrid, this.initForm.getSuggestedWidth(getManager().getPortletMetrics()),
					this.initForm.getSuggestedHeight(getManager().getPortletMetrics()) + 100);
			dialog.addListener(this);
			this.initFormButtons.addFormPortletListener(this);
			if (this.vizwiz instanceof AmiWebVizwiz_Chart)
				((AmiWebVizwiz_Chart) this.vizwiz).getChartTiles().addTilesListener(this);
		} else
			initVizwiz();
	}
	private boolean initVizwiz() {
		if (!this.vizwiz.initDm(this.dm, this.initForm, this.tablesField.getValue()))//TODO: don't hard code to first
			return false;
		this.creatorPanel.setPortlet(this.vizwiz.getCreatorPortlet());
		AmiWebPortlet pp = this.vizwiz.getPreviewPortlet();
		this.previewPanel.setPortlet(pp);

		this.divPortlet.setOffsetFromTopPx(this.vizwiz.getCreatorPortletWidth());
		getManager().onPortletAdded(vizwiz.getCreatorPortlet());
		List<FormPortletButton> buttons = this.vizwiz.getButtons();
		if (!buttons.isEmpty()) {
			this.previewGrid.setRowSize(0, 60);
			for (FormPortletButton button : buttons)
				this.buttonsPanel.addButton(button);
		}
		return true;
	}

	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {
	}

	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {
		if (this.vizwiz instanceof AmiWebVizwiz_Chart) {
			onButtonPressed(this.initFormButtons, this.initFormCreateButton);
		}
	}
	public void setDm(AmiWebDm dm) {
		if (this.vizwiz != null) {
			clearVizwiz();
		}
		this.dm = dm;
		if (dm == null)
			return;
		this.types.setActiveTileByPosition(-1);
		this.types.setSelectedRows("");
		tablesField.clearOptions();
		final AmiWebDmTablesetSchema schema = this.dm.getResponseOutSchema();
		for (String i : schema.getTableNamesSorted())
			tablesField.addOption(i, i);
		if (this.selectedTable != null)
			tablesField.setValueNoThrow(this.selectedTable);
		this.panelIdField.setValue(dm.getDmName());
		for (String s : this.service.getLayoutFilesManager().getAvailableAliasesUp(CH.s(dm.getAmiLayoutFullAlias())))
			if (!this.aliasField.containsOption(s))
				this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.aliasField.setValue(dm.getAmiLayoutFullAlias());
		rebuildVizwiz();
		int fieldWidthPx = 280;
		int fieldHeightPx = 10;
		int fieldSpacingPx = 10;
		if (dm != null) {
			FormPortletTitleField vizWizTitle = this.tableForm.addField(new FormPortletTitleField("VISUALIZATION WIZARD"));
			vizWizTitle.setLeftPosPx(0);
			vizWizTitle.setTopPosPx(0);
			vizWizTitle.setWidthPx(fieldWidthPx);
			vizWizTitle.setHeightPx(fieldHeightPx);
		}
		FormPortletTextField datamodelTitle = this.tableForm.addField(new FormPortletTextField("Datamodel:"));
		datamodelTitle.setValue(dm.getDmName());
		datamodelTitle.setLeftPosPx(120);
		datamodelTitle.setTopPosPx(20);
		datamodelTitle.setWidthPx(150);
		datamodelTitle.setHeightPx(25);
		datamodelTitle.setCssStyle(TABLE_FORM_FIELD_STYLE);
		datamodelTitle.setLabelCssStyle(TABLE_FORM_FIELD_STYLE);
		datamodelTitle.setDisabled(true);
		this.tablesField.setLeftPosPx(120);
		this.tablesField.setTopPosPx(fieldHeightPx + datamodelTitle.getTopPosPx() + fieldSpacingPx - 2);
		this.tablesField.setWidthPx(150);
		this.tablesField.setHeightPx(25);
		this.aliasField.setLeftPosPx(120);
		this.aliasField.setTopPosPx(fieldHeightPx + this.tablesField.getTopPosPx() + fieldSpacingPx + 10);
		this.aliasField.setWidthPx(150);
		this.aliasField.setHeightPx(25);
		this.panelIdField.setLeftPosPx(120);
		this.panelIdField.setTopPosPx(fieldHeightPx + this.aliasField.getTopPosPx() + fieldSpacingPx - 2);
		this.panelIdField.setWidthPx(150);
		this.panelIdField.setHeightPx(25);
		for (FormPortletField<?> f : this.tableForm.getFormFields())
			f.setCssStyle(TABLE_FORM_FIELD_STYLE);

	}
	private void clearVizwiz() {
		if (vizwiz != null && vizwiz.getPreviewPortlet() != null) {
			this.creatorPanel.setPortlet(blankCreator);
			this.previewPanel.setPortlet(blankPreview);
			vizwiz.getPreviewPortlet().close();
			vizwiz.getCreatorPortlet().close();
		}
		this.vizwiz = null;
	}
	public Portlet removePreviewPortlet() {
		if (this.vizwiz == null)
			return null;
		AmiWebPortlet t = this.vizwiz.removePreviewPortlet();
		t.setDefaultPref(t.getUserPref());
		String panelId = service.getNextPanelId(this.aliasField.getValue(), this.panelIdField.getValue());
		t.setAdn(AmiWebUtils.getFullAlias(this.aliasField.getValue(), panelId));
		service.registerAmiUserPrefId(panelId, t);
		return t;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (portlet == this.buttonsPanel) {
			this.vizwiz.onButton(button);
			return;
		} else if (button == this.submitButton) {
			if (this.vizwiz == null) {
				getManager().showAlert("Please choose a visualization type first");
				return;
			}
			if (!verifyIds())
				return;
			// force user to fill in required fields
			if (!this.vizwiz.preview())
				return;
			AmiWebPortlet p = (AmiWebPortlet) removePreviewPortlet();
			AmiWebUtils.createNewDesktopPortlet(this.service.getDesktop(), p);
			close();
			return;
		}
		if ("CREATE".equals(button.getId())) {
			if (!initVizwiz())
				return;
		} else if ("CANCEL".equals(button.getId())) {
			this.types.setSelectedType((byte) -1);
			clearVizwiz();
		}
		portlet.getParent().close();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.tablesField) {
			if (OH.ne(this.vizwizTableName, this.tablesField.getValue()))
				confirmRebuildViz();
		} else if (field == this.aliasField) {
			rebuildVizwiz();
		}
	}

	private void confirmRebuildViz() {
		if (this.vizwiz != null) {
			ConfirmDialogPortlet d = new ConfirmDialogPortlet(generateConfig(), "Reset Visualization? Changes to current visualization will be lost",
					ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("RESET_VIZ");
			getManager().showDialog("Confirm", d);
		} else
			rebuildVizwiz();
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("RESET_VIZ".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				rebuildVizwiz();
			} else {
				this.tablesField.setValue(this.vizwizTableName);
				this.types.setSelectedType(this.vizwizType);
			}
			return true;
		} else if ("CHOOSE_TABLE".equals(source.getCallback())) {
			setSelectedTable((String) source.getInputFieldValue());
			setDm(this.dm);
			return true;
		}
		return false;
	}
	public AmiWebVizwiz getVizwiz() {
		return vizwiz;
	}

	public String getSelectedTable() {
		return selectedTable;
	}
	public void setSelectedTable(String selectedTable) {
		this.selectedTable = selectedTable;
	}

	@Override
	public void onDialogClickoutside(RootPortletDialog dialog) {

	}

	@Override
	public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b) {

	}

	@Override
	public void onDialogMoved(RootPortletDialog rootPortletDialog) {

	}

	@Override
	public void onDialogClosed(RootPortletDialog rootPortletDialog) {

	}

	@Override
	public void onUserCloseDialog(RootPortletDialog rootPortletDialog) {
		if (rootPortletDialog.getPortlet() != null && rootPortletDialog.getPortlet().equals(this.initGrid)) {
			clearVizwiz();
		}
	}
	public void showChooseTableDialog(AmiWebDm dm) {
		List<String> tableList = dm.getResponseOutSchema().getTableNamesSorted();
		if (tableList.size() > 1) {
			FormPortletSelectField<String> selTableField = new FormPortletSelectField<String>(String.class, "Table:");
			RootPortletDialog dialog = getManager().showDialog("Choose Datasource Table",
					new ConfirmDialogPortlet(generateConfig(), "The chosen datamodel contains multiple tables. Please choose a table.", ConfirmDialogPortlet.TYPE_MESSAGE, this,
							selTableField).setCallback("CHOOSE_TABLE").updateButton(ConfirmDialogPortlet.ID_CLOSE, "Choose table"));
			dialog.setHasCloseButton(false);
			dialog.setEscapeKeyCloses(false);
			for (String s : tableList)
				selTableField.addOption(s, s);
		} else if (tableList.size() == 0) {
			getManager().showAlert("Datamodel does not produce any tables");
		}
	}

	public boolean verifyIds() {
		String panelId = this.panelIdField.getValue();
		if (!AmiWebUtils.isValidPanelId(panelId)) {
			getManager().showAlert("Panel ID is not valid (Must be alpha numeric): " + panelId);
			return false;
		}
		return true;
	}
}
