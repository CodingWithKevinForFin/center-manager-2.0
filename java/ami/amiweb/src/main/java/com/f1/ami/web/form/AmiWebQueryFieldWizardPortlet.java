package com.f1.ami.web.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.field.BaseEditFieldPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.base.Row;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebQueryFieldWizardPortlet extends GridPortlet implements TileFormatter, TilesListener, FormPortletListener, AmiWebQueryFormListener, ConfirmDialogListener {

	private static final String ROWID_NAME = "Name";
	private static final String ROWID_EDITOR = "Editor";
	private static final String ROWID_TYPEID = "TypeId";
	private static final String ROWID_ICON = "Icon";
	private static final String DIALOG_CALLBACK_SWITCH_FIELD = "APPLY";
	private static final String DIALOG_CALLBACK_CLOSE = "CLOSE";

	private final TilesPortlet tilesPortlet;
	private BaseEditFieldPortlet activeEditor;
	private FormPortlet buttonsForm;
	private FormPortletButton submitButton;
	private FormPortletButton cancelButton;
	private final InnerPortlet editorPanel;

	private Map<String, Object> origConfig = new HashMap<String, Object>();
	private final Map<String, Row> editorTypeId2Rows = new HashMap<String, Row>();
	private AmiWebQueryFormPortlet queryFormPortlet;
	private QueryField<?> currentQueryField;
	private QueryField<?> origField;
	private int fieldX;
	private int fieldY;

	private FormPortlet topPanel;
	private FormPortletSelectField<String> formFieldsSelectField;
	private AmiWebService service;
	private FormPortletButtonField importExportButtonField;

	private String editedFieldAri;
	private String editorTypeId;

	// Base constructor - Not called directly outside this class
	private AmiWebQueryFieldWizardPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());

		this.topPanel = new FormPortlet(generateConfig());
		this.topPanel.getFormPortletStyle().setCssStyle("_bg=#4c4c4c|_fg=white");
		this.formFieldsSelectField = new FormPortletSelectField<String>(String.class, "Field:");
		this.importExportButtonField = new FormPortletButtonField("Export/Import Field");
		this.topPanel.addField(this.formFieldsSelectField);
		this.topPanel.addField(this.importExportButtonField);
		this.formFieldsSelectField.setLeftPosPx(40);
		this.formFieldsSelectField.setTopPosPx(15);
		this.formFieldsSelectField.setWidthPx(FormPortletField.DEFAULT_WIDTH);
		this.formFieldsSelectField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.importExportButtonField.setValue("Export/Import");
		this.importExportButtonField.setLabelHidden(true);
		this.importExportButtonField.setLeftPosPx(250);
		this.importExportButtonField.setTopPosPx(15);
		this.importExportButtonField.setWidthPx(FormPortletField.DEFAULT_WIDTH - 80);
		this.importExportButtonField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
		this.topPanel.addFormPortletListener(this);

		this.tilesPortlet = new TilesPortlet(generateConfig());

		BasicSmartTable tilesTable = new BasicSmartTable(new BasicTable(new String[] { ROWID_NAME, ROWID_EDITOR, ROWID_TYPEID, ROWID_ICON }));
		int tilesHeight = 75;

		int col0 = 0;
		int row0 = 0;
		int row1 = 1;
		int row2 = 2;
		int row3 = 3;

		this.addChild(this.topPanel, col0, row0);
		this.addChild(this.tilesPortlet, col0, row1);
		this.tilesPortlet.setTable(tilesTable);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#e2e2e2");
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_WIDTH, 100);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_HEIGHT, tilesHeight);
		this.tilesPortlet.addOption(TilesPortlet.OPTION_TILE_PADDING, 8);
		//grid
		this.setRowSize(row0, 50);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int height = root.getHeight();
		// screen height is actually 1050 but browser height is 899
		if (height < 899) {
			// one row
			this.setRowSize(row1, tilesHeight + 15);
		} else {
			// two rows
			this.setRowSize(row1, tilesHeight + 100);
		}
		this.tilesPortlet.setMultiselectEnabled(false);
		this.tilesPortlet.setTileFormatter(this);
		this.tilesPortlet.addTilesListener(this);
		this.tilesPortlet.setRepaintOnSelectChanged(true);
		//ADD
		this.editorPanel = this.addChild(new HtmlPortlet(generateConfig()).setCssStyle("_bg=#e2e2e2"), col0, row2, 1, 1);
		this.buttonsForm = new FormPortlet(generateConfig());
		this.buttonsForm.addFormPortletListener(this);
		//ADD
		this.addChild(this.buttonsForm, col0, row3, 1, 1);
		this.setRowSize(row3, 40);

		this.submitButton = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonsForm.addButton(new FormPortletButton("Cancel"));

		//TODO:
		AmiWebService service = AmiWebUtils.getService(this.getManager());
		for (AmiWebFormFieldFactory<?> i : service.getFieldFactoriesSorted())
			this.editorTypeId2Rows.put(i.getEditorTypeId(), this.tilesPortlet.addRow(i.getUserLabel(), null, i.getEditorTypeId(), i.getIcon()));
	}
	private void editField(AmiWebQueryFormPortlet queryFormPortlet, QueryField<?> field, String editorTypeId, int fieldX, int fieldY) {
		//TODO: Set Field
		this.activeEditor = null;
		this.origConfig.clear(); // cancel?
		this.queryFormPortlet = queryFormPortlet;
		this.currentQueryField = field;
		this.origField = field;
		this.fieldX = fieldX;
		this.fieldY = fieldY;
		this.queryFormPortlet.addQueryFormListener(this);
		this.editorTypeId = editorTypeId;
		//TODO: Set Field end
	}
	public void startEdit() {
		if (this.currentQueryField != null) {
			this.queryFormPortlet.getFieldJson(this.origConfig, this.origField);
			if (!this.currentQueryField.isTransient())
				this.currentQueryField.resetOverridePosition();
			initializeEditors(this.currentQueryField.getFactory().getEditorTypeId());
		} else {
			initializeEditors(editorTypeId);
		}
		this.editedFieldAri = this.currentQueryField.getAri();

		this.updateFieldsList();
	}
	private void updateFieldsList() {
		this.formFieldsSelectField.clearOptions();
		if (this.queryFormPortlet != null) {
			//			Set<String> id = this.queryFormPortlet.getFieldsById().keySet();
			//			Set<String> queryFieldIds = this.queryFormPortlet.getQueryFieldIds();
			Set<String> queryFieldNames = this.queryFormPortlet.getQueryFieldNames();
			for (String fieldName : queryFieldNames) {
				this.formFieldsSelectField.addOption(fieldName, fieldName);
			}

			this.formFieldsSelectField.sortOptionsByName();
			//Set the selected field
			if (this.currentQueryField != null) {
				String id = this.currentQueryField.getName();
				if (this.formFieldsSelectField.containsOption(id)) {
					this.formFieldsSelectField.setValue(id);
				}
			}

		}

	}
	// Add 
	public AmiWebQueryFieldWizardPortlet(PortletConfig config, AmiWebQueryFormPortlet queryFormPortlet, String editorTypeId) {
		this(config);
		this.editField(queryFormPortlet, null, editorTypeId, -1, -1);
	}
	// Add 
	public AmiWebQueryFieldWizardPortlet(PortletConfig config, AmiWebQueryFormPortlet queryFormPortlet, String editorTypeId, int fieldX, int fieldY, boolean visible) {
		this(config);
		this.editField(queryFormPortlet, null, editorTypeId, fieldX, fieldY);
	}
	// Edit
	public AmiWebQueryFieldWizardPortlet(PortletConfig config, AmiWebQueryFormPortlet queryFormPortlet, QueryField<?> field) {
		this(config);
		this.editField(queryFormPortlet, field, null, field.getRealizedLeftPosPx(), field.getRealizedTopPosPx());
	}

	private void initializeEditors(String editorType) {
		setActiveEditor(getOrCreateEditor(editorType));
		this.tilesPortlet.setActiveTileByPosition(this.editorTypeId2Rows.get(editorType).getLocation());
	}
	private BaseEditFieldPortlet<?> getOrCreateEditor(String editorType) {
		Row row = this.editorTypeId2Rows.get(editorType);
		BaseEditFieldPortlet<?> r = (BaseEditFieldPortlet<?>) row.get(ROWID_EDITOR);
		if (r == null) {
			AmiWebService service = AmiWebUtils.getService(this.getManager());
			AmiWebFormFieldFactory<?> factory = service.getFormFieldFactory(editorType);
			r = factory.createEditor(this.queryFormPortlet, this.fieldX, this.fieldY);
			getManager().onPortletAdded(r);
			r.getSettingsForm().addFormPortletListener(this);
			row.put(ROWID_EDITOR, r);
		}
		return r;
	}
	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		if (this.activeEditor == null)
			return null;
		return this.activeEditor.getCallbacksEditor();
	}

	private void setActiveEditor(BaseEditFieldPortlet<?> editor) {
		BaseEditFieldPortlet<?> oldEditor = this.activeEditor;
		this.activeEditor = editor;
		this.editorPanel.setPortlet(editor);
		//		this.editorPanel.setPortlet(new HtmlPortlet(generateConfig()));

		QueryField<?> newQueryField;
		if (this.currentQueryField == null) {
			// user adds a new field
			newQueryField = this.activeEditor.createField();
			this.activeEditor.setQueryField((QueryField) newQueryField);
			newQueryField.updateFormSize();
			AmiWebEditableFormPortletManager em = this.queryFormPortlet.getEditableForm().getEditableManager();
			AmiWebFormFieldFactory factory = this.activeEditor.getFactory();
			int x = this.fieldX == -1 ? FormPortletField.DEFAULT_LEFT_POS_PX : this.fieldX;
			int y = this.fieldY == -1 ? FormPortletField.DEFAULT_PADDING_PX : this.fieldY;
			int w = factory.getDefaultWidth();
			int h = factory.getDefaultHeight();
			if (em.getSnapsize() != -1) {
				w = MH.roundBy(w, em.getSnapsize(), MH.ROUND_UP);
				h = MH.roundBy(h, em.getSnapsize(), MH.ROUND_UP);
			}
			newQueryField.setWidthPx(w);
			newQueryField.setLeftPosPx(x);
			newQueryField.setTopPosPx(y);
			newQueryField.setHeightPx(h);
			this.activeEditor.readCommonFromField(newQueryField);
			this.currentQueryField = newQueryField;
			return;
		} else {
			QueryField<?> oldQueryField = this.currentQueryField;
			if (oldEditor != null) {
				//Switching Field Types
				//				String origName = oldQueryField.getName();
				// transfer values from old editor to current editor
				oldEditor.writeCommonToField(oldQueryField);
				this.activeEditor.readCommonFromField(oldQueryField);
				//				oldQueryField.setVarName(origName);//This is necessary, in case the varname changed, the delete needs the old id
				// create a new field and populate with values held in current editor
				newQueryField = this.activeEditor.createField();
				copyStyle(oldQueryField, newQueryField);
				this.activeEditor.setQueryField((QueryField) newQueryField);
				this.activeEditor.writeCommonToField(newQueryField);
				this.currentQueryField = newQueryField;
				if (oldEditor.getField() != null)
					oldEditor.getField().onRemoving();
			} else {
				// opening up an existing editor
				this.activeEditor.setQueryField(this.currentQueryField);
				this.activeEditor.readFromField(this.currentQueryField);
				this.activeEditor.readCommonFromField(this.currentQueryField);
			}
		}
	}
	public static final void copyStyle(QueryField source, QueryField target) {
		if (source == null || target == null || source == target)
			return;
		AmiWebStyledPortletPeer sPeer = source.getStylePeer();
		AmiWebStyledPortletPeer tPeer = target.getStylePeer();
		for (Short s : sPeer.getDeclaredKeys()) {
			AmiWebStyleOption option = tPeer.getStyleType().getOptionByKey(s);
			if (option != null) {
				Object v = sPeer.getValue(s);
				tPeer.putValue(s, v);
			}
		}
	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {
		if (!AmiUtils.isValidVariableName(this.activeEditor.getVarNameFieldValue(), false, false, true)) {
			getManager().showAlert("Please enter valid variable name before changing editors");
			table.setActiveTileByPosition(this.editorTypeId2Rows.get(this.activeEditor.getFactory().getEditorTypeId()).getLocation());
			return;
		}
		BaseEditFieldPortlet<?> editor = getOrCreateEditor((String) row.get(ROWID_TYPEID));
		if (editor == this.activeEditor)
			return;
		setActiveEditor(editor);
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
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		if (tile.get("Icon", Caster_String.INSTANCE) != null) {
			String icon = tile.get("Icon", Caster_String.INSTANCE);
			styleSink.append(
					"_fs=12|_fm=bold|_bg=white|_fg=#004400|style.backgroundRepeat=no-repeat|style.justifyContent=center|style.backgroundPosition=top center|style.display=flex|style.alignItems=flex-end|style.backgroundImage=url('rsc/ami/"
							+ icon + "')|style.backgroundSize=50% 70%");
		}
		sink.append(tile.get("Name", Caster_String.INSTANCE));
	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {

	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ESCAPE.equals(keyEvent.getKey()))
			return true;
		return super.onUserKeyEvent(keyEvent);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 1200;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 650;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (this.queryFormPortlet.isClosed())
			close();
		boolean changed = this.isChanged();
		if (button == this.submitButton) {
			if (!AmiUtils.isValidVariableName(this.activeEditor.getVarNameFieldValue(), false, false, true)) {
				getManager().showAlert("Please enter valid variable name before submitting");
				return;
			}
			if (changed) {
				// if orig field is null, then no need to call onRemoving
				if (this.origField != null && this.currentQueryField != null)
					this.currentQueryField.onRemoving();
				if (!this.activeEditor.submit()) // do not close editor if there are one or more errors
					return;
			}
			// if there is no change, no need to submit
		} else if (button == this.cancelButton && changed) {
			this.cancelChanges();
		}
		close();
	}
	private void cancelChanges() {
		if (origField != null) {
			QueryField<?> removeField = this.currentQueryField;
			QueryField<?> newField = origField;
			// remove the currentQueryField and replace it with origField
			newField = queryFormPortlet.replaceQueryField(removeField.getId(), newField);

			removeField.removeFromDomManager();
			newField.addToDomManager();
			this.origField.init(this.origConfig);
			// no need to reinit original field, dm link will still show up without it
			//			this.currentQueryField.init(origConfig);
			this.currentQueryField = null; // not sure why this is needed
		} else {
			QueryField<?> fieldToRemove = this.currentQueryField;
			this.currentQueryField = null;
			queryFormPortlet.removeQueryField(fieldToRemove, true);
			fieldToRemove.onRemoving();
			fieldToRemove.removeFromDomManager();
		}
	}
	@Override
	public void onClosed() {
		for (Row row : this.editorTypeId2Rows.values()) {
			BaseEditFieldPortlet<?> editor = (BaseEditFieldPortlet<?>) row.get(ROWID_EDITOR);
			if (editor != null)
				editor.close();
		}
		super.onClosed();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.importExportButtonField) {
			AmiWebViewQueryFieldConfigurationPortlet viewConfig = new AmiWebViewQueryFieldConfigurationPortlet(generateConfig(), this);
			getManager().showDialog("Export/Import configuration", viewConfig);
		} else if (field == this.formFieldsSelectField) {
			String name = this.currentQueryField.getName();
			String newValue = (String) field.getValue();
			if (SH.equals(name, newValue)) {
				return;
			}
			boolean isChanged = this.isChanged();

			if (isChanged == true) {
				String text = "Do you want to apply changes before switching fields?";
				ConfirmDialogPortlet confirmDialogPortlet = new ConfirmDialogPortlet(generateConfig(), text, ConfirmDialogPortlet.TYPE_OK_CUSTOM, this)
						.setCallback(DIALOG_CALLBACK_SWITCH_FIELD);
				confirmDialogPortlet.addButton(ConfirmDialogPortlet.ID_YES, "Apply");
				confirmDialogPortlet.addButton(ConfirmDialogPortlet.ID_NO, "Revert");
				confirmDialogPortlet.addButton("CANCEL", "Cancel");
				getManager().showDialog("Apply Changes", confirmDialogPortlet);
			} else {
				String oldAri = this.currentQueryField.getAri();
				this.switchFields(oldAri);
			}

		}
	}
	private void switchFields(String oldAri) {
		String newValue = this.formFieldsSelectField.getValue();
		// queryFormPortlet contains the configs for all fields
		QueryField<?> queryField = this.queryFormPortlet.getFieldByVarName(newValue);
		String newAri = queryField.getAri();
		AmiWebQueryFieldWizardPortlet editor = this.service.getAmiQueryFormEditorsManager().getEditorByAri(newAri);
		if (editor == null) {
			//			if (this.origField != null) {
			// close callback (and sets orig field's dm to null, for dm fields), this affects the saved configs in queryFormPortlet
			//				this.origField.onRemoving();
			//			}
			// below method reassigns origField and currentQueryField (changes reference)
			this.editField(this.queryFormPortlet, queryField, null, queryField.getRealizedLeftPosPx(), queryField.getRealizedTopPosPx());
			this.startEdit();
			this.service.getAmiQueryFormEditorsManager().switchEditorField(oldAri, newAri);
		} else {
			//			if (this.origField != null)
			//				this.origField.onRemoving();
			this.close();
			PortletHelper.ensureVisible(editor);
		}

	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if (DIALOG_CALLBACK_SWITCH_FIELD.equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				if (!AmiUtils.isValidVariableName(this.activeEditor.getVarNameFieldValue(), false, false, true)) {
					String name2 = this.origField.getName();
					if (name2 == null)
						name2 = this.currentQueryField.getName();
					this.formFieldsSelectField.setValue(name2);
					this.switchBack();
					getManager().showAlert("Please enter valid variable name before submitting");
					return true;
				}
				if (this.activeEditor.submit()) {
					//					if (this.origField != null)
					//						this.origField.onRemoving();
					String oldAri = this.currentQueryField.getAri();
					this.switchFields(oldAri);
					return true;
				}
			} else if (ConfirmDialogPortlet.ID_NO.equals(id)) {
				// revert to previous settings before we switch field
				String oldAri = this.currentQueryField.getAri();
				this.cancelChanges();
				// below will erase dm name from orig field, which directly affects queryForm configs
				this.switchFields(oldAri);
				return true;
			} else if ("CANCEL".equals(id)) {
				this.switchBack();
				return true;
			}
		}
		if (DIALOG_CALLBACK_CLOSE.equals(source.getCallback())) {
			// isChanged() fired
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				//				this.currentQueryField.onRemoving();
				// revert to origConfig
				this.cancelChanges();
				this.close();
			}
			return true;
		}
		return false;
	}

	private void switchBack() {
		String name = null;
		if (this.origField != null)
			name = this.origField.getName();
		if (name == null)
			name = this.currentQueryField.getName();
		this.formFieldsSelectField.setValue(name);
	}

	private boolean isChanged() {
		if (this.origField == null) {
			return true;
		} else {
			HashMap<String, Object> current = new HashMap<String, Object>();
			// applies user inputs to the current opened editor, then get the json to compare
			// this fires setDmName, which could modify dm link
			this.activeEditor.writeCommonToField(this.currentQueryField);
			this.activeEditor.writeToField(this.currentQueryField);
			// get the config from the current fields
			this.queryFormPortlet.getFieldJson(current, this.currentQueryField);

			return !OH.eq(this.origConfig, current);
		}
	}

	public Map<String, Object> exportQueryFieldConfig(Map<String, Object> configuration) {
		if (this.queryFormPortlet == null || this.currentQueryField == null)
			return configuration;
		this.queryFormPortlet.getFieldJson(configuration, this.currentQueryField);
		return configuration;
	}
	public boolean importQueryFieldConfig(Map<String, Object> configuration) {
		configuration.put("n", this.currentQueryField.getName());
		this.currentQueryField.init(configuration);
		this.activeEditor.readFromField(this.currentQueryField);
		this.activeEditor.readCommonFromField(this.currentQueryField);
		return true;
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}

	public boolean submit() {
		return this.activeEditor.submit();
	}
	public QueryField<?> getEditedFieldForFieldEditor() {
		if (this.origField != null)
			return this.origField;
		else if (this.currentQueryField != null)
			return this.currentQueryField;
		return null;
	}
	public String getEditedFieldAriForFieldEditor() {
		return this.editedFieldAri;
	}
	@Override
	public void onFieldAriChanged(QueryField<?> queryField, String oldAri) {
		if (this.currentQueryField == queryField)
			return;
		else
			this.updateFieldsList();
	}
	@Override
	public void onFieldRemoved(QueryField<?> queryField) {
		if (this.currentQueryField == queryField) {
			this.close();
			return;
		}
		String fieldName = queryField.getName();
		this.formFieldsSelectField.removeOption(fieldName);

	}
	@Override
	public void onFieldAdded(QueryField<?> queryField) {
		if (this.currentQueryField == queryField)
			return;
		String fieldName = queryField.getName();
		this.formFieldsSelectField.addOption(fieldName, fieldName);

	}
	@Override
	public void close() {
		if (this.queryFormPortlet != null)
			this.queryFormPortlet.removeQueryFormListener(this);
		super.close();
	}
	public void onCloseButton() {
		// writes user inputs to current query field 
		if (this.isChanged()) {
			String text = "Closing will revert changes, do you want to continue?";
			ConfirmDialogPortlet confirmDialogPortlet = new ConfirmDialogPortlet(generateConfig(), text, ConfirmDialogPortlet.TYPE_OK_CUSTOM, this)
					.setCallback(DIALOG_CALLBACK_CLOSE);
			confirmDialogPortlet.addButton(ConfirmDialogPortlet.ID_YES, "Revert");
			confirmDialogPortlet.addButton(ConfirmDialogPortlet.ID_NO, "Cancel");
			getManager().showDialog("Close Field Editor", confirmDialogPortlet);
		} else {
			//			this.currentQueryField.onRemoving();
			// shouldn't call this if nothing has been changed
			//			this.cancelChanges();
			this.close();
		}
	}
}
