package com.f1.ami.web.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebAbstractPortletBuilder;
import com.f1.ami.web.AmiWebDesktopListener;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebDmPortletBuilder;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectsManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulasListener;
import com.f1.ami.web.AmiWebLinkableVarsPortlet;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebPortletDef;
import com.f1.ami.web.AmiWebPortletDef.Callback;
import com.f1.ami.web.AmiWebSetFieldPositionPortlet;
import com.f1.ami.web.AmiWebStyledScrollbarPortlet;
import com.f1.ami.web.AmiWebUsedDmSingleton;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmError;
import com.f1.ami.web.dm.AmiWebDmFilter;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManagerListener;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.form.AmiWebEditableFormPortletManager.Guide;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.queryfield.AbstractDmQueryField;
import com.f1.ami.web.form.queryfield.DivQueryField;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.form.queryfield.SelectQueryField;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Form;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletDownload;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuForButtonFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuForButtonListener;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletCustomCallbackListener;
import com.f1.suite.web.portal.impl.form.FormPortletEventListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.ToDoException;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.ComparableComparator;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcFrameTuple2;
import com.f1.utils.structs.table.stack.CalcTypesTuple2;
import com.f1.utils.structs.table.stack.CalcTypesTuple3;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AmiWebQueryFormPortlet extends AmiWebAbstractPortlet implements FormPortletListener, FormPortletEventListener, ConfirmDialogListener, FormPortletContextMenuListener,
		FormPortletContextMenuForButtonFactory, FormPortletContextMenuForButtonListener, AmiWebDmManagerListener, AmiWebDmPortlet, AmiWebDesktopListener, AmiWebDmListener,
		AmiWebLinkableVarsPortlet, WebMenuListener, AmiWebStyledScrollbarPortlet, FormPortletCustomCallbackListener, AmiWebFormulasListener {

	private static final Logger log = LH.get();

	public static final ParamsDefinition CALLBACK_ONAMIJSCALLBACK = new ParamsDefinition("onAmiJsCallback", Object.class, "String action,java.util.List params");

	static {
		CALLBACK_ONAMIJSCALLBACK.addDesc(
				"Executed when the javascript function amiJsCallback(...) is executed within the browser.  See Green Button -> Edit HTML -> Right click Menu -> Embed javascript call to onAmiJsCallback");
		CALLBACK_ONAMIJSCALLBACK.addParamDesc(0,
				"The 2nd param passed into the javascript method amiJsCallback(...). For example amiJsCallback(this,\"sample\") would result in \"sample\" being passed in");
		CALLBACK_ONAMIJSCALLBACK.addParamDesc(1,
				"A list of all remaining params passed into the javascript, exlcuding the first tow. For example, amiJsCallback(this,\"sample\",2,5,\"hello\",{n:20}) would result in List(2,5,\"hello\",Map(n,20)");
	}

	public static final String SUFFIX_MAX = "_max";
	public static final String SUFFIX_MIN = "_min";
	public static final String SUFFIX_FILETEXT = "_text";
	public static final String SUFFIX_FILEDATA = "_data";
	public static final String SUFFIX_FILEDATA64 = "_data64";
	public static final String SUFFIX_FILETYPE = "_type";
	public static final String SUFFIX_FILENAME = "_name";
	public static final String SUFFIX_START = "_start";
	public static final String SUFFIX_END = "_end";
	public static final String TEXT_ALIGN_RIGHT = "right";
	public static final String TEXT_ALIGN_LEFT = "left";
	public static final String TEXT_ALIGN_CENTER = "center";
	private static final String TEXT_ALIGN_JUSTIFY = "justify";
	protected static final String MOUSE_POS_X_Y = "_mousePosXY_";

	private static final String FORMULA_HTML_TEMPLATE = "html_template";

	final AmiWebEditableFormPortlet editableForm;
	Map<String, QueryField<?>> fieldsById = new HashMap<String, QueryField<?>>();
	final OneToOne<String, String> fieldIdsToNames = new OneToOne<String, String>();
	Map<String, AbstractDmQueryField<?>> dmFieldsById = new HashMap<String, AbstractDmQueryField<?>>();
	Map<String, DivQueryField> divFieldsById = new HashMap<String, DivQueryField>();
	private IndexedList<String, Integer> fieldsZIndexesById = new BasicIndexedList<String, Integer>();
	private Map<String, QueryField<?>> fieldsByVarName = new HashMap<String, QueryField<?>>();
	public Map<String, String> fieldAriToDm = new HashMap<String, String>(); // ensure opening the editor repeatedly does not affect dm ref count
	public Map<String, Integer> inUseDm = new HashMap<String, Integer>();
	Map<String, AmiWebButton> buttons = new HashMap<String, AmiWebButton>();
	private Set<String> referencedVarsInTemplate = new HashSet<String>();
	final private AmiWebFormula htmlFormula;
	private AmiWebUsedDmSingleton dmSingleton;
	private AmiWebQueryFormContextMenuManager contextMenuManager;

	private String backgroundColor;
	private int dialogHeight = -1;
	private int dialogWidth = -1;
	private int dialogLeft = -1;
	private int dialogTop = -1;

	private Set<DivQueryField> divFieldsNeedingUpdate = new IdentityHashSet<DivQueryField>();

	public AmiWebButton getAmiWebButton(String buttonId) {
		return this.buttons.get(buttonId);
	}
	public AmiWebButton addAmiWebButton(String editId, String id, String name, String cssStyle, StringBuilder sink) {
		if (editId != null) {
			AmiWebButton btn = buttons.remove(editId);
			editableForm.removeButton(btn.getButton());
			btn.close();
		} else {
			if (buttons.containsKey(id)) {
				sink.append("Duplicate Button Id: ").append(id);
				return null;
			}
		}

		AmiWebButton button = new AmiWebButton(id, name, this.getService(), this);
		button.getScript().setAmiLayoutAlias(getAmiLayoutFullAlias());
		button.getButton().setCssStyle(cssStyle);
		editableForm.addButton(button.getButton());
		buttons.put(id, button);
		if (editId != null)
			getService().getDomObjectsManager().fireAriChanged(button, editId);
		return button;
	}

	public void setHtmlRotate(Integer htmlRotate) {
		if (htmlRotate == null)
			return;
		this.editableForm.getFormPortletStyle().setHtmlRotate(htmlRotate);
	}

	public AmiWebFormula getHtmlFormula() {
		return this.htmlFormula;
	}
	public void setHtmlTemplate(String value, boolean override) {
		this.htmlFormula.setFormula(value, override);
	}
	public void updateFieldStyles() {
		this.editableForm.getFormPortletStyle().setCssStyle("style.background=" + this.backgroundColor);
	}
	AmiWebDmTableSchema getUsedDm() {
		return dmSingleton.getDmTableSchema();
	}

	public static QueryField getQueryField(FormPortletField<?> field) {
		return (QueryField) field.getCorrelationData();
	}
	public static boolean hasQueryField(FormPortletField<?> field) {
		return field.getCorrelationData() instanceof QueryField<?>;
	}

	public static SelectQueryField getMetadata(FormPortletSelectField<?> field) {
		if (field == null)
			return null;
		return (SelectQueryField) field.getCorrelationData();
	}
	private void processHtmlTemplate(CalcFrameStack sf) {
		DerivedCellCalculator calc = this.htmlFormula.getFormulaCalc();
		if (calc == null) {
			editableForm.setHtmlLayout(null);
		} else {
			String text = AmiUtils.s(calc.get(sf));
			editableForm.setHtmlLayout(getService().cleanHtml(text));
		}

	}
	public String getHtmlTemplate(boolean override) {
		return this.htmlFormula.getFormula(override);
	}

	public AmiWebQueryFormPortlet(PortletConfig manager) {
		super(manager);
		this.htmlFormula = this.formulas.addFormulaTemplate("html");
		this.formulas.addFormulasListener(this);
		this.contextMenuManager = new AmiWebQueryFormContextMenuManager(this);
		this.dmSingleton = new AmiWebUsedDmSingleton(getService().getDmManager(), this);
		this.editableForm = new AmiWebEditableFormPortlet(generateConfig());
		this.editableForm.addCustomCallbackListener(this);
		PortletStyleManager_Form styleManager = new PortletStyleManager_Form();
		// get date format from amiweb's user form style manager
		styleManager.setDateDisplayFormat(getService().getUserFormStyleManager().getDateDisplayFormat());
		styleManager.setTimeDisplayFormat(getService().getUserFormStyleManager().getTimeDisplayFormat());
		styleManager.setDefaultFormButtonPanelStyle("_fm=center");
		this.editableForm.setStyle(styleManager);
		setChild(editableForm);
		editableForm.addFormPortletListener(this);
		editableForm.getFormPortletStyle().setLabelsWidth(100);
		editableForm.addMenuListener(this);
		editableForm.setMenuFactoryForButton(this);
		editableForm.addMenuListenerForButton(this);
		AmiWebDmManager dm = getService().getDmManager();
		dm.addDmManagerListener(this);
		this.getStylePeer().initStyle();
	}

	private boolean isClosed = false;

	@Override
	public void onClosed() {
		for (QueryField<?> i : this.fieldsById.values()) {
			try {
				this.fireFieldRemoved(i);
				i.onRemoving();
				i.removeFromDomManager();
			} catch (Exception e) {
				LH.warning(log, "Error removing field ", i, e);
			}
		}
		for (AmiWebButton i : this.buttons.values()) {
			i.close();
			i.removeFromDomManager();
		}
		editableForm.removeFormPortletListener(this);
		editableForm.removeMenuListener(this);
		AmiWebDmManager dm = getService().getDmManager();
		dm.removeDmManagerListener(this);
		super.onClosed();
		this.isClosed = true;
	}
	public boolean isClosed() {
		return isClosed;
	}
	public <T extends QueryField<?>> T addQueryField(T field, boolean fireDomAdded) {
		return addQueryField(field, fireDomAdded, true);
	}
	public <T extends QueryField<?>> T addQueryField(T field, boolean fireDomAdded, boolean setZindex) {
		String name = field.getName();
		if (!field.isTransient() && !AmiUtils.isValidVariableName(name, false, false, true))
			throw new RuntimeException("Invalid variable name: " + field.getName());
		if (this.fieldIdsToNames.containsValue(field.getName()))
			throw new RuntimeException("duplicate field name: " + field.getName());
		for (int i = 0; i < field.getVarsCount(); i++)
			if (this.getUserDefinedVariables().getType(field.getVarNameAt(i)) != null)
				throw new RuntimeException("duplicate variable name: " + field.getVarNameAt(i));
		if (setZindex)
			field.getField().setZIndex(fieldsById.size() + 1);
		addFieldInner(field);
		if (fireDomAdded) {
			this.fireFieldAdded(field);
			AmiWebDomObjectsManager domManager = getService().getDomObjectsManager();
			domManager.fireAdded(field);
			for (AmiWebDomObject i : field.getChildDomObjects())
				domManager.fireAdded(i);
		}
		return field;
	}
	public <T extends QueryField<?>> T replaceQueryField(String id, T newField) {
		OH.assertNotNull(id);
		String name = newField.getName();
		if (!newField.isTransient() && !AmiUtils.isValidVariableName(name, false, false, true))
			throw new RuntimeException("Invalid variable name");
		// Check if field already exists 
		QueryField<?> oldField = fieldsById.get(id);
		if (oldField == null)
			throw new RuntimeException("Field not found: " + id);
		int zIndex = oldField.getField().getZIndex();
		removeQueryField(oldField, false);
		if (OH.ne(newField.getField().getId(), id))
			newField.getField().setId(id);
		newField.getField().setZIndex(zIndex);
		addFieldInner(newField);
		AmiWebDomObjectsManager domManager = getService().getDomObjectsManager();
		if (OH.ne(oldField.getName(), newField.getName()) || OH.ne(oldField.getFactory().getEditorTypeId(), newField.getFactory().getEditorTypeId())) {
			Set<String> oldSuffixes = oldField.getSuffixes();
			Set<String> nuwSuffixes = newField.getSuffixes();
			for (String suffix : CH.comm(oldSuffixes, nuwSuffixes, true, false, false))
				domManager.fireRemoved(oldField.getQueryFieldDomValueBySuffix(suffix));
			domManager.fireAriChanged(newField, oldField.getAri());
			for (String suffix : CH.comm(oldSuffixes, nuwSuffixes, false, false, true))
				domManager.fireAriChanged(newField.getQueryFieldDomValueBySuffix(suffix), oldField.getQueryFieldDomValueBySuffix(suffix).getAri());
			for (String suffix : CH.comm(oldSuffixes, nuwSuffixes, false, true, false))
				domManager.fireAdded(newField.getQueryFieldDomValueBySuffix(suffix));
		}

		try {
			newField.setValue(oldField.getValue());
		} catch (Exception e) {
			LH.info(log, "TODO: use setValueNoThrow", e);
		}

		return newField;
	}
	private void addFieldInner(QueryField field) {
		if (isTransient())
			field.setTransient(true);
		this.editableForm.addField(field.getField());
		String id = field.getId();
		this.fieldsZIndexesById.add(id, field.getField().getZIndex());
		this.fieldsById.put(id, field);
		this.fieldIdsToNames.put(id, field.getName());
		if (field instanceof AbstractDmQueryField)
			this.dmFieldsById.put(id, (AbstractDmQueryField) field);
		if (field instanceof DivQueryField)
			this.divFieldsById.put(id, (DivQueryField) field);
		this.fieldsByVarName.put(field.getName(), field);
		for (int i = 0; i < field.getVarsCount(); i++) {
			String varname = field.getVarNameAt(i);
			this.fieldsByVarName.put(varname, field);
			putPortletVar(field.getVarNameAt(i), field.getValue(i), false, field.getVarTypeAt(i));
			onVarChanged(varname);
		}
	}

	public void setBackgroundColor(String bgColor) {
		this.backgroundColor = bgColor;
	}

	public String getBackgroundColor() {
		return this.backgroundColor;
	}

	protected void onUserClick() {
	}

	public static class Builder extends AmiWebAbstractPortletBuilder<AmiWebQueryFormPortlet> implements AmiWebDmPortletBuilder<AmiWebQueryFormPortlet> {

		public static final String OLD_ID = "queryform";
		public static final String ID = "amiform";

		public Builder() {
			super(AmiWebQueryFormPortlet.class);
		}

		@Override
		public AmiWebQueryFormPortlet buildPortlet(PortletConfig portletConfig) {
			AmiWebQueryFormPortlet r = new AmiWebQueryFormPortlet(portletConfig);
			return r;
		}

		@Override
		public String getPortletBuilderName() {
			return "Form";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

		@Override
		public List<String> extractUsedDmAndTables(Map<String, Object> portletConfig) {
			return AmiWebUsedDmSingleton.extractUsedDmAndTables(portletConfig);
		}

		@Override
		public void replaceUsedDmAndTable(Map<String, Object> portletConfig, int position, String name) {
			AmiWebUsedDmSingleton.replaceUsedDmAndTable(portletConfig, position, name);
		}

		@Override
		public List<Callback> getCallbacks(Map<String, Object> portletConfig) {
			List<Callback> r = super.getCallbacks(portletConfig);
			List<Map<String, Object>> fields = (List<Map<String, Object>>) portletConfig.get("fields");
			if (CH.isntEmpty(fields)) {
				for (Map<String, Object> field : fields)
					AmiWebPortletDef.getCallbacks(field, r);
			}
			return r;
		}

	}

	public boolean checkCanRemoveCustomColumnById(String fid) {
		return true;
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		this.contextMenuManager.populateConfigMenu(headMenu);
	}
	@Override
	public boolean onAmiContextMenu(String id) {
		if (this.contextMenuManager.onAmiContextMenu(id)) {
			return true;
		} else {
			return super.onAmiContextMenu(id);
		}
	}

	protected void enterExitDesignModeMenuAction() {
		this.editableForm.setInEditorMode(!this.editableForm.getInEditorMode());
	}
	public boolean getInEditorMode() {
		return !this.isReadonlyLayout() && this.editableForm.getInEditorMode();
	}

	protected void removeFieldMenuAction(String id) {
		Set<String> selFieldIds = this.editableForm.getEditableManager().getSelectedFieldIds();
		Set<String> deleteLabels = new HashSet<String>();
		for (String fid : selFieldIds) {
			if (!checkCanRemoveCustomColumnById(fid))
				continue;
			deleteLabels.add(this.editableForm.getField(fid).getTitle());
		}
		if (selFieldIds.isEmpty()) { // When not in design mode... 
			if (SH.indexOf(id, MOUSE_POS_X_Y, 0) != -1) {
				ArrayList<Integer> formXYList = parseFormMousePos(id);
				if (formXYList != null) {
					QueryField<?> clickedField = AmiWebQueryFormPortletUtils.getClickedField(editableForm, formXYList.get(0), formXYList.get(1));
					if (clickedField != null)
						deleteLabels.add(clickedField.getField().getTitle());
				}
			} else {
				deleteLabels.add(this.editableForm.getField(SH.stripPrefix(id, "remfld_", false)).getTitle());
			}
		}
		ConfirmDialogPortlet cfd = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete the following field(s): <b>" + deleteLabels.toString() + "</b>",
				ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("confirm_delete_field");
		if (selFieldIds.isEmpty())
			cfd.setCorrelationData(id);
		getManager().showDialog("delete field", cfd);
	}
	protected void editFieldMenuAction(String id) {
		if (id.startsWith("editfld_")) { // Text field
			String fid = SH.stripPrefix(id, "editfld_", true);
			if (!checkCanRemoveCustomColumnById(fid))
				return;
			FormPortletField<?> field = editableForm.getField(fid);
			showFieldEditorWarning(getQueryField(field));
		}
	}
	private void showFieldEditorWarning(QueryField<?> field) {
		if (!field.isAtDefaultPosition() && !field.isTransient()) {
			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
					"Editing a field will move it back to its default position, over-writing any positioning set by AMI Script. Continue?", ConfirmDialogPortlet.TYPE_YES_NO, this)
							.setCallback("confirm_open_field_editor");
			dialog.setCorrelationData(field);
			getManager().showDialog("Warning", dialog);
		} else {
			showFieldEditor(field);
		}
	}
	private void showFieldEditor(QueryField<?> field) {
		this.getService().getAmiQueryFormEditorsManager().showEditExistingFieldEditor(this, field);
	}
	void addFieldMenuAction(String fieldId) {
		int x = FormPortletField.DEFAULT_LEFT_POS_PX; // left position (px)
		int y = FormPortletField.DEFAULT_PADDING_PX; // top position (px)
		if (fieldId.contains(MOUSE_POS_X_Y)) { // Get mouse position from fieldId, if it is included
			List<String> xyList = SH.splitToList("_", SH.afterLast(fieldId, MOUSE_POS_X_Y));
			if (xyList.size() > 0) {
				x = SH.parseInt(xyList.get(0));
				y = SH.parseInt(xyList.get(1));
			}
		}
		x = this.getEditableForm().getEditableManager().snapX(x);
		y = this.getEditableForm().getEditableManager().snapY(y);
		String fieldIdNoPos = SH.beforeFirst(fieldId, MOUSE_POS_X_Y);
		this.getService().getAmiQueryFormEditorsManager().showAddNewFieldEditor(this, fieldIdNoPos, x, y);
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		editableForm.clearFields();
		fieldsById.clear();
		dmFieldsById.clear();
		divFieldsById.clear();
		divFieldsNeedingUpdate.clear();
		fieldsByVarName.clear();
		// gets and sets the form-wide styling here for SUPER
		// no field exists at time of set
		super.init(configuration, origToNewIdMapping, sb);
		this.setSnapSize(CH.getOr(Caster_Integer.INSTANCE, configuration, "snap", -1));
		if (configuration.containsKey("html")) {//backwards compatibility for HTML
			String html = (String) configuration.get("html");
			if (html != null) {
				html = SH.replaceAll(html, '{', "\\{");
				html = SH.replaceAll(html, '}', "\\}");
			}
			this.setHtmlTemplate(html, false);
			return;
		}
		List<Map> fields = (List) configuration.get("fields");
		// newly created style peer doesn't know about the form styling
		if (fields != null)
			for (Map field : fields)
				importField(field, false, false);

		fieldsZIndexesById.sortByValues(ComparableComparator.instance(Integer.class));
		List<Map> guides = (List<Map>) configuration.get("guides");
		if (guides != null) {
			for (Map gm : guides) {
				Boolean isVertical = CH.getOr(gm, "isVertical", false);
				Integer offsetPx = CH.getOr(gm, "offsetPx", 0);
				Integer alignment = CH.getOr(gm, "alignment", 0);
				if (isVertical) {
					this.editableForm.addVerticalGuide(offsetPx, alignment.byteValue());
				} else {
					this.editableForm.addHorizontalGuide(offsetPx, alignment.byteValue());
				}
			}
		}

		List<Map> buttons = (List) configuration.get("buttons");
		if (buttons != null) {
			for (Map button : buttons) {
				String id = CH.getOr(Caster_String.INSTANCE, button, "id", null);
				String name = CH.getOr(Caster_String.INSTANCE, button, "n", null);
				String script = CH.getOr(Caster_String.INSTANCE, button, "s", null);
				String style = CH.getOr(Caster_String.INSTANCE, button, "style", null);
				AmiWebButton b = addAmiWebButton(null, id, name, style, sb);
				if (script != null) {
					b.getScript().setAmiScriptCallbackNoCompile(AmiWebButton.ON_PRESSED, script);
				} else {
					Map<String, Object> m = (Map<String, Object>) button.get("callbacks");
					b.getScript().init(null, this.getAmiLayoutFullAlias(), m, sb);
				}
			}
		}
		this.dmSingleton.init(getAmiLayoutFullAlias(), configuration);

		if (new Integer(360).equals(configuration.get("htmlRotateSetting")))
			configuration.put("htmlRotateSetting", 0);
		this.editableForm.getFormPortletStyle().setLabelsWidth(CH.getOr(Caster_Integer.INSTANCE, configuration, "labelWidth", 80));
		String ht = (String) configuration.get("htmlTemplate2");
		if (ht == null) {
			ht = (String) configuration.get("htmlTemplate");
			if (ht != null)
				ht = SH.replaceAll(ht, '{', "${");
		}
		this.setHtmlTemplate(ht, false);
		updateButtons();
		updateFieldStyles();//5 is effectively called here... 
	}
	public QueryField<?> importField(Map field, boolean force, boolean isTransient) {
		String type = CH.getOr(Caster_String.INSTANCE, field, "t", null);

		//Backwards compatibility
		if (QueryField.TYPE_ID_SELECT.equalsIgnoreCase(type)) {
			type = QueryField.TYPE_ID_SELECT;
		}
		AmiWebFormFieldFactory<?> factory = getService().getFormFieldFactory(type);
		// stylePeer init first time: apply default
		QueryField<?> queryField = factory.createQueryField(this);
		// second time: apply field specific styling
		queryField.init(field);
		if (isTransient) {
			queryField.setTransient(true);
			queryField.setVarName(getService().getVarsManager().toTransientId(queryField.getName()));
		} else {
			queryField.setTransient(false);
			queryField.setVarName(getService().getVarsManager().fromTransientId(queryField.getName()));
		}
		if (force && this.fieldsByVarName.containsKey(queryField.getName()))
			queryField.setVarName(SH.getNextId(queryField.getName(), this.fieldsByVarName.keySet()));
		queryField.updateAri();
		queryField = this.addQueryField(queryField, true, false);
		queryField.addToDomManager();

		Integer zIndex = CH.getOr(Caster_Integer.INSTANCE, field, "zidx", 0);

		fieldsZIndexesById.update(queryField.getId(), zIndex);
		this.editableForm.getEditableManager().getOrCreateRect(queryField.getField());
		return queryField;
	}
	@Override
	public void onInitDone() {
		super.onInitDone();
		for (QueryField<?> queryField : this.fieldsByVarName.values()) {
			queryField.onInitDone();
		}
		for (AmiWebButton button : this.buttons.values()) {
			button.onInitDone();
		}
	}

	@Override
	public void onAmiInitDone() {
		super.onAmiInitDone();
		this.resetFormFields();
	}
	@Override
	public void onConnect(PortletSocket localSocket, PortletSocket remoteSocket) {
		super.onConnect(localSocket, remoteSocket);
		updateButtons();
	}
	@Override
	public void onDisconnect(PortletSocket localSocket, PortletSocket remoteSocket) {
		super.onDisconnect(localSocket, remoteSocket);
		updateButtons();
	}
	private void saveFormFieldPositionProperty(Map<String, Object> fieldConfig, String propertyName, Number value) {
		if (value instanceof Integer && !((Integer) WebAbsoluteLocation.PX_NA).equals(value))
			fieldConfig.put(propertyName, value);
		else if (value instanceof Double && !((Double) WebAbsoluteLocation.PCT_NA).equals(value))
			fieldConfig.put(propertyName, value);
	}

	public Map<String, Object> getFieldJson(Map<String, Object> m, QueryField<?> q) {
		// Save field position and dimensions
		// Horizontal
		saveFormFieldPositionProperty(m, "widthPx", q.getWidthPx());
		saveFormFieldPositionProperty(m, "leftPosPx", q.getLeftPosPx());
		saveFormFieldPositionProperty(m, "rightPosPx", q.getRightPosPx());
		saveFormFieldPositionProperty(m, "widthPct", q.getWidthPct());
		saveFormFieldPositionProperty(m, "leftPosPct", q.getLeftPosPct());
		saveFormFieldPositionProperty(m, "rightPosPct", q.getRightPosPct());
		saveFormFieldPositionProperty(m, "hCenterOffsetPct", q.getHorizontalOffsetFromCenterPct());
		// Vertical
		saveFormFieldPositionProperty(m, "heightPx", q.getHeightPx());
		saveFormFieldPositionProperty(m, "topPosPx", q.getTopPosPx());
		saveFormFieldPositionProperty(m, "bottomPosPx", q.getBottomPosPx());
		saveFormFieldPositionProperty(m, "heightPct", q.getHeightPct());
		saveFormFieldPositionProperty(m, "topPosPct", q.getTopPosPct());
		saveFormFieldPositionProperty(m, "bottomPosPct", q.getBottomPosPct());
		saveFormFieldPositionProperty(m, "vCenterOffsetPct", q.getVerticalOffsetFromCenterPct());
		q.getJson(m);

		return m;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		List<Map> fields = new ArrayList<Map>();

		Map<String, QueryField<?>> sorted = new TreeMap<String, QueryField<?>>();
		for (QueryField<?> q : fieldsById.values())
			sorted.put(q.getName(), q);
		for (QueryField<?> q : sorted.values()) { // Save fields
			if (q.isTransient() && !isTransient())
				continue;
			HashMap<String, Object> m = new HashMap<String, Object>();
			this.getFieldJson(m, q);
			fields.add(m);
		}

		Guide g;
		HashMap<String, Object> gm;
		AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
		IntKeyMap<Guide> guidesIntKeyMap = editableManager.getGuides();
		List<Map> guides = new ArrayList<Map>();
		int[] guidesKeys = guidesIntKeyMap.getKeys();
		for (int i = 0; i < editableManager.getGuidesCount(); i++) {
			g = guidesIntKeyMap.get(guidesKeys[i]);
			gm = new HashMap<String, Object>();
			gm.put("isVertical", g.isVertical());
			gm.put("offsetPx", g.getRealizedOffsetPx());
			gm.put("alignment", g.getAlignment());
			guides.add(gm);
		}
		AmiWebUtils.putSkipEmpty(r, "guides", guides);

		List<Map> buttons = new ArrayList<Map>();
		for (String i : this.buttons.keySet()) {
			AmiWebButton button = this.buttons.get(i);
			Map m = CH.m("id", button.getId(), "n", button.getName(), "callbacks", button.getScript().getConfiguration(), "style", button.getButton().getCssStyle());
			buttons.add(m);
		}
		AmiWebUtils.putSkipEmpty(r, "buttons", buttons);
		AmiWebUtils.putSkipEmpty(r, "fields", fields);
		AmiWebUtils.putSkipEmpty(r, "htmlTemplate2", this.htmlFormula.getFormulaConfig());
		CH.putExcept(r, "snap", this.getSnapSize(), -1);
		this.dmSingleton.getConfiguration(getAmiLayoutFullAlias(), r);
		return r;

	}
	@Override
	public boolean isRealtime() {
		return false;
	}

	@Override
	public void clearAmiData() {
	}

	@Override
	public boolean runAmiLink(String name) {
		Collection<AmiWebDmLink> links = getDmLinksFromThisPortlet();
		for (AmiWebDmLink i : links) {
			if (name.equals(i.getTitle())) {
				AmiWebDmUtils.sendRequest(getService(), i);
			}
		}
		return false;
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return true;
	}
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		Map<String, Integer> ids = new HashMap<String, Integer>();
		List<Object> params = new ArrayList<Object>();
		BasicTable values = new BasicTable();
		CalcTypes linkableVars = getLinkableVars();
		for (String i : linkableVars.getVarKeys()) {
			Class<?> t = linkableVars.getType(i);
			values.addColumn(t, i);
		}
		if (type != NONE) {
			int position = 0;
			Row row = values.newEmptyRow();
			for (QueryField<?> queryField : fieldsById.values()) {
				for (int i = 0; i < queryField.getVarsCount(); i++) {
					Object value = queryField.getValue(i);
					ids.put(queryField.getVarNameAt(i), position++);
					params.add("".equals(value) ? null : value);
					if (values.getColumnsMap().containsKey(queryField.getVarNameAt(i)))
						row.put(queryField.getVarNameAt(i), queryField.getValue(i));
				}
			}
			values.getRows().add(row);
		}
		return values;
	};

	@Override
	public boolean runAmiLinkId(String id) {
		Collection<AmiWebDmLink> links = getDmLinksFromThisPortlet();
		for (AmiWebDmLink i : links) {
			if (id.equals(i.getRelationshipId())) {
				AmiWebDmUtils.sendRequest(getService(), i);
			}
		}
		return false;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if ("ISLINK".equals(button.getCorrelationData()))
			runAmiLink(button.getName());
		else {
			AmiWebButton amiButton = (AmiWebButton) button.getCorrelationData();
			if (amiButton != null) {
				amiButton.getScript().execute(AmiWebButton.ON_PRESSED);
			}
		}
	}

	public void updateButtons() {
		Set<String> titles = new TreeSet<String>();

		for (AmiWebDmLink i : getDmLinksFromThisPortlet()) {
			if (i.getTitle() != null)
				titles.add(i.getTitle());
		}

		this.editableForm.clearButtons();
		for (String s : titles)
			this.editableForm.addButton(new FormPortletButton(s).setCorrelationData("ISLINK"));
		for (AmiWebButton i : this.buttons.values()) {
			i.getButton().setId(null);
			this.editableForm.addButton(i.getButton());
			i.getButton();
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		onFieldValueChanged(field, true);
	}
	public void onFieldValueChanged(FormPortletField<?> field, boolean fire) {
		QueryField<?> queryField = getQueryField(field);
		queryField.onFieldValueChanged(field, fire);
		for (int i = 0; i < queryField.getVarsCount(); i++)
			putPortletVar(queryField.getVarNameAt(i), queryField.getValue(i), false, queryField.getVarTypeAt(i));
		if (fire)
			executeFieldScript(field, QueryField.EVENT_ONCHANGE);
	}
	@Override
	public void onFieldEvent(FormPortlet formPortlet, FormPortletField<?> field, String eventType, Map<String, String> attributes) {
		if (SH.equals(FormPortlet.CALLBACK_ONFOCUS, eventType)) {
			executeFieldScript(field, eventType);
		} else if (eventType.contentEquals(QueryField.EVENT_ONAUTOCOMPLETED)) {
			// only text field sends this
			executeFieldScript(field, eventType);
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == SH.CHAR_CR) {
			if (formPortlet.getButtons().size() == 1)
				onButtonPressed(formPortlet, formPortlet.getButton(CH.first(formPortlet.getButtons())));
			executeFieldScript(field, QueryField.EVENT_ONENTERKEY);
		}
	}

	private void executeFieldScript(FormPortletField<?> field, String eventType) {
		QueryField<?> queryField = getQueryField(field);
		if (queryField.getOnEventScripts().getCallback(eventType) != null)
			queryField.getOnEventScripts().execute(eventType);
		if (QueryField.EVENT_ONCHANGE.equals(eventType)) {
			for (AmiWebDomObject i : queryField.getChildDomObjects())
				getService().getDomObjectsManager().fireEvent(i, DOM_EVENT_CODE_ONCHANGE);
		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		super.onButton(source, id);
		if ("confirm_delete_field".equals(source.getCallback()) && ConfirmDialogPortlet.ID_YES.equals(id)) {
			Set<String> selFieldIds = this.editableForm.getEditableManager().getSelectedFieldIds();
			for (String fid : selFieldIds) {
				if (!checkCanRemoveCustomColumnById(fid))
					continue;
				QueryField queryField = getQueryField(editableForm.getField(fid));
				removeQueryField(queryField, true);
				queryField.onRemoving();
				queryField.removeFromDomManager();
			}
			if (selFieldIds.isEmpty()) { // When not in design mode... 
				if (SH.indexOf((String) source.getCorrelationData(), MOUSE_POS_X_Y, 0) != -1) {
					ArrayList<Integer> formXYList = parseFormMousePos((String) source.getCorrelationData());
					if (formXYList != null) {
						QueryField<?> clickedField = AmiWebQueryFormPortletUtils.getClickedField(editableForm, formXYList.get(0), formXYList.get(1));
						if (clickedField != null)
							removeQueryField(clickedField, true);
					}
				} else {
					removeQueryField(getQueryField(this.editableForm.getField(SH.stripPrefix((String) source.getCorrelationData(), "remfld_", false))), true);
				}
			}
		} else if ("delete_button".equals(source.getCallback()) && ConfirmDialogPortlet.ID_YES.equals(id)) {
			String fid = (String) source.getCorrelationData();
			removeButton(fid);
		} else if ("confirm_open_field_editor".equals(source.getCallback()) && ConfirmDialogPortlet.ID_YES.equals(id)) {
			showFieldEditor((QueryField<?>) source.getCorrelationData());
		}
		return true;
	}
	private void removeButton(String id) {
		editableForm.removeButton(buttons.get(id).getButton());
		AmiWebButton removed = buttons.remove(id);
		getService().getDomObjectsManager().fireRemoved(removed);
	}
	public void removeQueryField(QueryField<?> queryField, boolean fireDomRemoved) {
		this.editableForm.removeField(queryField.getField());
		AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
		editableManager.removeField(queryField.getField());
		String name = queryField.getName();
		String id = queryField.getId();
		this.fieldsById.remove(id);
		this.fieldIdsToNames.removeByKey(id);
		this.dmFieldsById.remove(id);
		this.divFieldsById.remove(id);
		this.fieldsZIndexesById.remove(id);
		this.divFieldsNeedingUpdate.remove(queryField);
		onVarChanged(name);
		for (int i = 0; i < queryField.getVarsCount(); i++) {
			String varName = queryField.getVarNameAt(i);
			removeFieldByVarName(varName);
			onVarChanged(varName);
		}
		updateZIndexes();
		if (fireDomRemoved) {
			AmiWebDomObjectsManager domManager = getService().getDomObjectsManager();
			domManager.fireRemoved(queryField);
			for (AmiWebDomObject i : queryField.getChildDomObjects())
				domManager.fireRemoved(i);
			this.fireFieldRemoved(queryField);
		}
	}

	public void updateZIndexes() {
		for (int i = 0; i < fieldsZIndexesById.getSize(); i++) {
			fieldsZIndexesById.update(fieldsZIndexesById.getKeyAt(i), i + 1);
			fieldsById.get(fieldsZIndexesById.getKeyAt(i)).getField().setZIndex(i + 1);
		}
	}
	@Override
	public String getConfigMenuTitle() {
		return "HTML";
	}

	@Override
	public String getPanelType() {
		return "form";
	}
	public QueryField<?> getFieldByVarName(String name) {
		return this.fieldsByVarName.get(name);
	}
	public void removeFieldByVarName(String name) {
		this.fieldsByVarName.remove(name);
	}
	public void setFieldValue(String id, Object object) {
		FormPortletField field = this.getFieldByVarName(id).getField();
		field.setValue(field.getCaster().cast(object));
	}
	@Override
	public boolean putPortletVar(String key, Object value, Class type) {
		return this.putPortletVar(key, value, true, type);
	}

	public boolean putPortletVar(String key, Object value, boolean updateField, Class type) {
		if (updateField) {
			QueryField field = this.getFieldByVarName(key);
			if (field != null)
				field.setValue(key, value);
		}
		onVarChanged(key);
		return super.putPortletVar(key, value, type);
	}
	private void flagTemplateNeedsProcessing() {
		flagPendingAjax();
		needsTemplateUpdate = true;
	}
	public Object getFieldValue(String id) {
		QueryField<?> field = this.getFieldByVarName(id);
		return field.getField().getValue();
	}

	private boolean needsTemplateUpdate;

	@Override
	public void drainJavascript() {
		CalcFrame vars = this.getTemplateVars();
		ReusableCalcFrameStack sf = getStackFrame();
		if (needsTemplateUpdate) {
			processHtmlTemplate(sf.reset(vars));
			needsTemplateUpdate = false;
		}
		if (!this.divFieldsNeedingUpdate.isEmpty()) {
			for (DivQueryField i : this.divFieldsNeedingUpdate)
				i.processHtmlTemplate(vars);
			this.divFieldsNeedingUpdate.clear();
		}
		super.drainJavascript();
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		onAmiContextMenu(action);

	}
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("onQueryButton".equals(callback)) {
			String query = (String) attributes.get("id");
			if (!runAmiLink(query)) {
				getManager().showAlert("Query not found:" + query);
			}
		} else if ("onButton".equals(callback)) {
			String id = (String) attributes.get("id");
			AmiWebButton button = buttons.get(id);
			if (button == null) {
				getManager().showAlert("Button not found: " + id);
				return;
			}
			button.execute();
		} else
			super.handleCallback(callback, attributes);
	}

	@Override
	public PortletDownload handleContentRequest(String callback, Map<String, String> attributes) {
		if ("rsc".equals(callback)) {
			throw new ToDoException("cant find where this gets hit");
		}
		return null;
	}

	@Override
	protected void initJs() {
		super.initJs();
		if (referencedVarsInTemplate.contains("page.uid"))
			flagTemplateNeedsProcessing();
	}

	public Map<String, Object> getFieldValues() {
		Map<String, Object> r = new HashMap<String, Object>();
		for (QueryField<?> entry : this.fieldsById.values()) {
			for (int i = 0; i < entry.getVarsCount(); i++)
				r.put(entry.getVarNameAt(i), entry.getValue(i));
		}
		return r;
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletButton button, int cursorPosition) {
		if (this.inEditMode()) {
			BasicWebMenu menu = new BasicWebMenu();
			Object correlationData = button.getCorrelationData();
			if ("ISLINK".equals(correlationData) || correlationData == null) {
				getManager().showAlert("This field cannot be edited because it was auto-generated by an existing query");
				return null;
			}
			AmiWebButton webButton = (AmiWebButton) correlationData;
			String i = webButton.getId();
			menu.add(new BasicWebMenuLink("Edit...", true, "edit_button_" + i));
			menu.add(new BasicWebMenuLink("Delete", true, "rem_button_" + i));
			return menu;
		} else
			return null;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletButton node) {
		if (action.startsWith("edit_button_")) {
			String fid = SH.stripPrefix(action, "edit_button_", true);
			showEditButtonPortlet(fid, true);

		} else if (action.startsWith("rem_button_")) {
			String fid = SH.stripPrefix(action, "rem_button_", true);
			AmiWebButton button = buttons.get(fid);
			ConfirmDialogPortlet cfd = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete button: <b>" + button.name + "</b>?",
					ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("delete_button").setCorrelationData(fid);
			getManager().showDialog("Delete Button", cfd);
		}
	}

	public EditWebButtonForm showEditButtonPortlet(String fid, boolean isEdit) {
		EditWebButtonForm editor;
		AmiWebUtils.showStyleDialog((isEdit ? "Edit" : "Add") + " Button", this.editableForm, editor = new EditWebButtonForm(this, generateConfig(), this.buttons.get(fid)),
				generateConfig());
		return editor;
	}
	protected void showRemoveButtonPortlet(String fid) {
		AmiWebButton button = buttons.get(fid);
		ConfirmDialogPortlet cfd = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete button: <b>" + button.name + "</b>?",
				ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("delete_button").setCorrelationData(fid);
		getManager().showDialog("Delete Button", cfd);

	}
	public int getDialogHeight() {
		return dialogHeight;
	}

	public void setDialogHeight(int dialogHeight) {
		this.dialogHeight = dialogHeight;
	}

	public int getDialogWidth() {
		return dialogWidth;
	}

	public void setDialogWidth(int dialogWidth) {
		this.dialogWidth = dialogWidth;
	}

	public int getDialogLeft() {
		return dialogLeft;
	}

	public void setDialogLeft(int dialogLeft) {
		this.dialogLeft = dialogLeft;
	}

	public int getDialogTop() {
		return dialogTop;
	}

	public void setDialogTop(int dialogTop) {
		this.dialogTop = dialogTop;
	}

	@Override
	public void onDmLinkAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		if (link.involvesAmiAliasDotPanelId(getAmiLayoutFullAliasDotId()))
			updateButtons();
	}
	@Override
	public void onDmLinkRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDmLink link) {
		if (link.involvesAmiAliasDotPanelId(getAmiLayoutFullAliasDotId()))
			updateButtons();
	}

	@Override
	public void onDmAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}

	@Override
	public void onDmRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}

	@Override
	public void onDmDependencyAdded(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}

	@Override
	public void onDmDependencyRemoved(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm upper, AmiWebDm lower) {
	}

	@Override
	public void onDmUpdated(AmiWebDmManager amiWebDmManagerImpl, AmiWebDm dm) {
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		if (this.dmSingleton.matches(datamodel)) {
			resetFormFields();
		}
	}
	private Tuple2<CalcFrame, CalcTypes> getDefaultValuesFromSchema() {
		CalcFrame values;
		CalcTypes types;
		AmiWebDmTableSchema schema = this.dmSingleton.getDmTableSchema();
		if (schema != null) {
			types = new CalcTypesTuple2(schema.getClassTypes(), getPortletVarTypes());
			Table table = this.dmSingleton.getTable();
			if (table != null && table.getSize() > 0) {
				Row row = table.getRows().get(0);
				values = new CalcFrameTuple2(row, getPortletVars());
			} else {
				CalcFrame emptyRow = new BasicCalcFrame(schema.getClassTypes());
				values = new CalcFrameTuple2(emptyRow, getPortletVars());
			}
		} else {
			types = new CalcTypesTuple2(getPortletVarTypes(), getPortletVarTypes());
			values = getPortletVars();
		}
		return new Tuple2<CalcFrame, CalcTypes>(values, types);
	}
	public void resetFormFields() {
		Tuple2<CalcFrame, CalcTypes> vals = getDefaultValuesFromSchema();
		AmiWebDmTableSchema schema = this.dmSingleton.getDmTableSchema();
		CalcFrame values = vals.getA();
		com.f1.base.CalcTypes types = vals.getB();
		StringBuilder sb = new StringBuilder();
		for (QueryField<?> field : this.fieldsById.values())
			resetField(values, types, sb, field);
		if (schema != null)
			for (Object key : schema.getColumnNames())
				onVarChanged((String) key);
	}
	public void resetField(StringBuilder sb, QueryField<?> field) {
		Tuple2<CalcFrame, CalcTypes> vals = getDefaultValuesFromSchema();
		CalcFrame values = vals.getA();
		com.f1.base.CalcTypes types = vals.getB();
		resetField(values, types, sb, field);
	}
	private void resetField(CalcFrame values, com.f1.base.CalcTypes types, StringBuilder sb, QueryField<?> field) {
		DerivedCellCalculator calc = field.getDefaultValueFormula().getFormulaCalc();
		if (calc != null) {
			Object value = null;
			try {
				value = calc.get(getStackFrame().reset(values));
				if (OH.ne(value, field.getValue()))
					if (field.setValue(value))
						onFieldValueChanged(field.getField(), false);
			} catch (Exception e) {
				if (getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					getService().getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_FORMULA, field.getAri(),
							QueryField.FORMULA_DEFAULT_VALUE, "Error populating field from default value", CH.m("value", value), e));
			}
		} else {
			Object value = field.getDefaultValue();
			if (OH.ne(value, field.getValue()))
				if (field.setValue(value))
					onFieldValueChanged(field.getField(), false);
		}
		onVarChanged(field.getVarNameAt(0));
	}

	private void onVarChanged(String varName) {
		if (!needsTemplateUpdate && this.referencedVarsInTemplate.contains(varName))
			flagTemplateNeedsProcessing();
		for (DivQueryField f : this.divFieldsById.values())
			if (f.onVarChanged(varName)) {
				divFieldsNeedingUpdate.add(f);
				flagPendingAjax();
			}
	}

	public void setUsedDatamodel(String dmName, String tableName) {
		this.dmSingleton.setUsedDm(dmName, tableName);
		AmiWebDm dm = this.getService().getDmManager().getDmByAliasDotName(dmName);
		if (dm != null) {
			this.onDmDataChanged(dm);
		}
		for (AmiWebDmLink i : getDmLinksFromThisPortlet())
			i.setSourceDm(dmName, tableName);
		for (AmiWebDmLink i : getDmLinksToThisPortlet())
			i.setTargetDm(dmName);
	}

	@Override
	public void onDmManagerInitDone() {
		this.resetFormFields();
	}
	@Override
	public void clearUserSelection() {
	}

	@Override
	public com.f1.base.CalcTypes getLinkableVars() {
		com.f1.utils.structs.table.stack.BasicCalcTypes r = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (QueryField<?> field : this.fieldsByVarName.values()) {
			for (int i = 0; i < field.getVarsCount(); i++) {
				String varname = field.getVarNameAt(i);
				r.putType(varname, field.getVarTypeAt(i));
			}
		}
		return r;
	}
	@Override
	public boolean hasVisiblePortletForDm(AmiWebDm datamodel) {
		return getVisible();
	}
	@Override
	public void processFilter(AmiWebDmLink link, Table table) {
		this.currentFilter = link;
	}
	@Override
	public void onDmRunningQuery(AmiWebDm datamodel, boolean isRequery) {
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.htmlFormula.getFormula(false), sink);
	}

	private AmiWebDmLink currentFilter;

	@Override
	public AmiWebDmLink getCurrentLinkFilteringThis() {
		return this.currentFilter;
	}
	public QueryField<?> getFieldValueNoThrow(String name) {
		return this.fieldsByVarName.get(name);
	}
	@Override
	public void onDmError(AmiWebDm datamodel, AmiWebDmError error) {

	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Form.TYPE_FORM;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);

		switch (key) {
			//			case AmiWebStyleConsts.CODE_FONT_CL:
			//			case AmiWebStyleConsts.CODE_FLD_BG_CL:
			//			case AmiWebStyleConsts.CODE_FLD_FONT_CL:
			//			case AmiWebStyleConsts.CODE_FONT_SZ:
			//			case AmiWebStyleConsts.CODE_TXT_ALIGN:
			//			case AmiWebStyleConsts.CODE_FONT_FAM:
			//			case AmiWebStyleConsts.CODE_BOLD:
			//			case AmiWebStyleConsts.CODE_ITALIC:
			//			case AmiWebStyleConsts.CODE_UNDERLINE:
			//			case AmiWebStyleConsts.CODE_LBL_PD:
			//			case AmiWebStyleConsts.CODE_FLD_LBL_STATUS:
			//			case AmiWebStyleConsts.CODE_FLD_LBL_SIDE:
			//			case AmiWebStyleConsts.CODE_FLD_LBL_ALIGN:
			//			case AmiWebStyleConsts.CODE_FLD_TRACK_CL:
			//			case AmiWebStyleConsts.CODE_FLD_GRIP_CL:
			//			case AmiWebStyleConsts.CODE_FLD_BDR_CL:
			//			case AmiWebStyleConsts.CODE_FLD_FONT_FAM:
			//			case AmiWebStyleConsts.CODE_FLD_FONT_SZ:
			//			case AmiWebStyleConsts.CODE_FLD_BDR_WD:
			//			case AmiWebStyleConsts.CODE_FLD_BDR_RAD:
			//			case AmiWebStyleConsts.CODE_CAL_BG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_BTN_BG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_YR_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_SEL_YR_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_MTN_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_SEL_MTN_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_SEL_MTN_BG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_WK_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_WK_BG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_WK_FD_CL:
			//			case AmiWebStyleConsts.CODE_CAL_DAY_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_BTN_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_X_DAY_FG_CL:
			//			case AmiWebStyleConsts.CODE_CAL_HOV_BG_CL:
			//			case AmiWebStyleConsts.CODE_FLD_CSS:
			//			case AmiWebStyleConsts.CODE_FLD_CSS_HELP:
			//			case AmiWebStyleConsts.CODE_FLD_CSS_LBL:
			//				for (QueryField<?> i : this.fieldsById.values())
			//					i.onFormStyleValueChanged(key, old, nuw);
			//				break;
			case AmiWebStyleConsts.CODE_BG_CL:
				setBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_ROTATE:
				setHtmlRotate(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_SHOW_BTM_BTNS:
				setShowBottomButtons((Boolean) nuw);
				break;
		}
		updateFieldStyles();
	}

	@Override
	public void onDmDependencyAdded(AmiWebDmManager manager, AmiWebDmPortlet target, String dmName, String tableName) {

	}

	@Override
	public void onDmDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmPortlet target, String dmName, String tableName) {

	}

	@Override
	public void onEditModeChanged(AmiWebDesktopPortlet amiWebDesktopPortlet, boolean inEditMode) {
		if (!isReadonlyLayout() && !isTransient())
			this.editableForm.onEditModeChanged();
	}

	@Override
	public boolean onUserMouseEvent(MouseEvent mouseEvent) {
		BasicWebMenu menu = null;
		if (mouseEvent.getMouseEvent() == MouseEvent.CONTEXTMENU && AmiWebQueryFormPortletUtils.isMouseEventInsideForm(this.editableForm, mouseEvent)) {
			menu = new BasicWebMenu();
			addCustomMenuItems(menu);
		}
		if (inEditMode()) {
			boolean inDesignMode = getInEditorMode();
			if (mouseEvent.getMouseEvent() == MouseEvent.CONTEXTMENU && AmiWebQueryFormPortletUtils.isMouseEventInsideForm(this.editableForm, mouseEvent)) {
				AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
				Set<String> selectedFieldIds = editableManager.getSelectedFieldIds();
				QueryField<?> field = AmiWebQueryFormPortletUtils.getClickedField(editableForm, mouseEvent);
				if (menu == null) {
					menu = new BasicWebMenu();
				}
				boolean hasSelectedFields = editableManager.hasSelectedFields();
				int x = mouseEvent.getMouseX() + editableForm.getClipLeft();
				int y = mouseEvent.getMouseY() + editableForm.getClipTop();
				String mousePosition = MOUSE_POS_X_Y + x + "_" + y;
				boolean useMoveMenuItem = field != null && !getInEditorMode();
				menu.add(new BasicWebMenuLink("Reset All Field Positions", !areAllFieldsAtDefaultPositions(), "reset_all_fields_pos"));
				if (hasSelectedFields) {
					menu.add(new BasicWebMenuLink("Reset Selected Field Positions", !areSelectedFieldsAtDefaultPositions(), "reset_sel_fields_pos"));
				}
				if (field != null && field.getField().isVisible()) {
					if (useMoveMenuItem) {
						menu.add(new BasicWebMenuLink("Move (enter design mode)", true, "enter_exit_design_mode"));
					}
					boolean multiple = selectedFieldIds.size() > 1;
					String id = field.getField().getId();
					if (!multiple) {
						menu.add(new BasicWebMenuLink("Edit", true, "editfld_" + id));
					}
					menu.add(new BasicWebMenuLink("Delete", true, "remfld_" + id + mousePosition));
					menu.add(new BasicWebMenuLink("Reposition field" + (multiple ? "s" : ""), inDesignMode, "reposition_fields" + mousePosition));
					menu.add(new BasicWebMenuDivider());
					menu.add(new BasicWebMenuLink("Copy Field" + (multiple ? "s" : ""), inDesignMode, "copy_fields" + mousePosition));
					menu.add(new BasicWebMenuLink("Export Field" + (multiple ? "s" : ""), inDesignMode, "export_fields"));
				}
				BasicWebMenuLink pasteLink = new BasicWebMenuLink("Paste Field" + (getClipboardFieldsCount() > 1 ? "s" : ""), inDesignMode && hasClipboardFields(),
						"paste_fields" + mousePosition);
				if ((!hasSelectedFields && AmiWebQueryFormPortletUtils.getClickedGuide(this.editableForm, mouseEvent) == null) || hasSelectedFields) {
					menu.add(pasteLink);
				}
				if (field != null && field.getField().isVisible()) {
					menu.add(new BasicWebMenuDivider());
					AmiWebQueryFormFieldArrangeUtils.createArrangeFieldsMenu(menu,
							hasSelectedFields && AmiWebQueryFormPortletUtils.getClickedGuide(this.editableForm, mouseEvent) == null && getInEditorMode());
					BasicWebMenu moveFields = new BasicWebMenu("Move Fields", hasSelectedFields);
					moveFields.add(new BasicWebMenuLink("To Front", true, "move_field_front"));
					moveFields.add(new BasicWebMenuLink("To Back", true, "move_field_back"));
					moveFields.add(new BasicWebMenuLink("Forward", true, "move_field_forward"));
					moveFields.add(new BasicWebMenuLink("Backward", true, "move_field_backward"));
					menu.add(moveFields);
				}
				menu.add(new BasicWebMenuDivider());
				BasicWebMenu addMenu = new BasicWebMenu("Add Field", true);
				AmiWebQueryFormContextMenuManager.createAddFieldsMenu(addMenu,
						mouseEvent.getMouseX() + editableForm.getClipLeft() - PortletHelper.getAbsoluteLeft(this.editableForm),
						mouseEvent.getMouseY() + editableForm.getClipTop() - PortletHelper.getAbsoluteTop(this.editableForm));
				addMenu.sort();
				menu.add(addMenu);
				int absX = PortletHelper.getAbsoluteLeft(this.editableForm);
				int absY = PortletHelper.getAbsoluteTop(this.editableForm);
				createGuideMenu(mouseEvent, menu, absX, absY);
				menu.add(new BasicWebMenuDivider());
				Guide guide = AmiWebQueryFormPortletUtils.getClickedGuide(this.editableForm, mouseEvent);
				if (guide != null) {
					menu.add(new BasicWebMenuLink("Remove Guide", true, "rem_guide_" + guide.getGuideId()));
					BasicWebMenu guideAlignMenu = new BasicWebMenu("Guide Alignment", true);
					guideAlignMenu.add(new BasicWebMenuLink("Lock to " + (guide.isVertical() ? "left" : "top"), true, "guide_align_lock_start" + mousePosition));
					guideAlignMenu.add(new BasicWebMenuLink("Lock to " + (guide.isVertical() ? "right" : "bottom"), true, "guide_align_lock_end" + mousePosition));
					guideAlignMenu.add(new BasicWebMenuLink("Ratio", true, "guide_align_ratio" + mousePosition));
					menu.add(guideAlignMenu);
				}
				if (!useMoveMenuItem) {
					menu.add(new BasicWebMenuLink((getInEditorMode() ? "Exit" : "Enter") + " design mode", true, "enter_exit_design_mode"));
				}
				if (field == null)
					menu.add(new BasicWebMenuLink("Import Fields...", true, "import_fields"));
			} else if (mouseEvent.getMouseEvent() == MouseEvent.DOUBLECLICK && AmiWebQueryFormPortletUtils.isMouseEventInsideForm(this.editableForm, mouseEvent)
					&& this.editableForm.getInEditorMode()) {
				FormPortletField<?> field = this.editableForm.getEditableManager().getActiveField();

				if (field != null && field.isVisible()) {
					showFieldEditorWarning(getQueryField(field));
				}
			}
		}
		if (menu != null) {
			getManager().showContextMenu(menu, this);
		}
		return super.onUserMouseEvent(mouseEvent);
	}
	public void createGuideMenu(MouseEvent mouseEvent, WebMenu sink, int portletAbsX, int portletAbsY) {
		BasicWebMenu addGuideMenu = new BasicWebMenu("Add Guide", true);
		int formX = mouseEvent.getMouseX() + this.editableForm.getClipLeft() - portletAbsX;
		int formY = mouseEvent.getMouseY() + this.editableForm.getClipTop() - portletAbsY;
		addGuideMenu.add(new BasicWebMenuLink("Vertical", true, "addguide_v" + formX));
		addGuideMenu.add(new BasicWebMenuLink("Horizontal", true, "addguide_h" + formY));
		sink.add(addGuideMenu);
	}

	@Override
	public void onMenuItem(String id) {
		if (SH.startsWith(id, AmiWebQueryFormContextMenuManager.ADD)) {
			String fid = SH.stripPrefix(id, AmiWebQueryFormContextMenuManager.ADD, true);
			addFieldMenuAction(fid);
		} else if (SH.startsWith(id, "addguide_v")) {
			this.editableForm.addVerticalGuide(SH.parseInt(SH.stripPrefix(id, "addguide_v", true)));
			this.editableForm.setInEditorMode(true);
		} else if (SH.startsWith(id, "addguide_h")) {
			this.editableForm.addHorizontalGuide(SH.parseInt(SH.stripPrefix(id, "addguide_h", true)));
			this.editableForm.setInEditorMode(true);
		} else if (id.startsWith("edit"))

		{
			editFieldMenuAction(id);
		} else if (id.startsWith("remfld_")) {
			removeFieldMenuAction(id);
		} else if ("enter_exit_design_mode".equals(id)) {
			enterExitDesignModeMenuAction();
		} else if (SH.startsWith(id, "rem_guide_")) {
			int gid = SH.parseInt(SH.stripPrefix(id, "rem_guide_", true));
			this.editableForm.getEditableManager().removeGuide(gid);
		} else if (SH.startsWith(id, "reposition_fields")) {
			ArrayList<Integer> formXYList = parseFormMousePos(id);
			getManager().showDialog("Set Field Position", new AmiWebSetFieldPositionPortlet(generateConfig(), this, this.editableForm.getEditableManager().getSelectedFields(),
					AmiWebQueryFormPortletUtils.getClickedField(editableForm, formXYList.get(0), formXYList.get(1)))).setShadeOutside(false);
		} else if (SH.startsWith(id, "copy_fields")) {
			ArrayList<Integer> formXYList = parseFormMousePos(id);
			AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
			copyFields(AmiWebQueryFormPortletUtils.getClickedField(editableForm, formXYList.get(0), formXYList.get(1)), editableManager.getSelectedFields());
		} else if (OH.eq(id, "export_fields")) {
			AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
			List<Map> l = new ArrayList<Map>();
			for (QueryField<?> i : editableManager.getSelectedFields()) {
				Map<String, Object> sink = new HashMap();
				getFieldJson(sink, i);
				l.add(sink);
			}
			AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
			viewConfigPortlet.setConfiguration((Map) CH.m("fields", l));
			getManager().showDialog("Export Fields Configuration", viewConfigPortlet);
		} else if ("import_fields".equals(id)) {
			AmiWebImportFieldsPortlet imp = new AmiWebImportFieldsPortlet(generateConfig(), this);
			getManager().showDialog("Export Fields Configuration", imp, 800, 800);
		} else if (SH.startsWith(id, "paste_fields")) {
			ArrayList<Integer> formXYList = parseFormMousePos(id);
			AmiWebQueryFormPortletUtils.pasteFields(this, formXYList.get(0), formXYList.get(1));
		} else if (SH.startsWith(id, "guide_align_")) {
			String alignMode = SH.stripPrefix(id, "guide_align_", true);
			ArrayList<Integer> formXYList = parseFormMousePos(id);
			Guide guide = AmiWebQueryFormPortletUtils.getClickedGuide(this.editableForm, formXYList.get(0), formXYList.get(1));
			if (SH.startsWith(alignMode, "lock_start")) {
				guide.switchToLockStartAlign();
			} else if (SH.startsWith(alignMode, "lock_end")) {
				guide.switchToLockEndAlign();
			} else if (SH.startsWith(alignMode, "ratio")) {
				guide.switchToRatioAlign();
			}
		} else if (SH.startsWith(id, "arrange_")) {
			AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
			Set<QueryField<?>> selected = editableManager.getSelectedFields();
			QueryField<?> active = AmiWebQueryFormPortlet.getQueryField(editableManager.getActiveField());

			AmiWebQueryFormFieldArrangeUtils.onArrangeMenuItem(id, active, selected);
			for (QueryField<?> queryField : selected) {
				editableManager.updateRectangle(queryField);
			}
		} else if (SH.startsWith(id, "move_field_")) {
			AmiWebEditableFormPortletManager editableManager = this.editableForm.getEditableManager();
			Set<QueryField<?>> selected = editableManager.getSelectedFields();
			String action = SH.stripPrefix(id, "move_field_", true);
			AmiWebQueryFormPortletUtils.moveFieldZIndex(this, selected, action);
		} else if ("reset_all_fields_pos".equals(id)) {
			resetAllQueryFieldPositions();
		} else if ("reset_sel_fields_pos".equals(id)) {
			resetSelectedQueryFieldPositions();
		} else if (isCustomContextMenuAction(id)) {
			processCustomContextMenuAction(id);
		}
	}
	private ArrayList<Integer> parseFormMousePos(String id) {
		ArrayList<Integer> formXYList = new ArrayList<Integer>();
		List<String> xyList = SH.splitToList("_", SH.afterLast(id, MOUSE_POS_X_Y));
		formXYList.add(SH.parseInt(xyList.get(0)) - PortletHelper.getAbsoluteLeft(this.editableForm));
		formXYList.add(SH.parseInt(xyList.get(1)) - PortletHelper.getAbsoluteTop(this.editableForm));
		return formXYList;
	}

	private final Set<QueryField<?>> clipboardFields = new HashSet<QueryField<?>>();
	private QueryField<?> clipboardClickedField;

	public void copyFields(QueryField<?> clickedField, Set<QueryField<?>> fields) {
		this.clipboardFields.clear();
		this.clipboardClickedField = clickedField;
		CH.addAll(this.clipboardFields, fields);
	}
	public int getClipboardFieldsCount() {
		return this.clipboardFields.size();
	}
	public boolean hasClipboardFields() {
		return this.clipboardFields.size() > 0;
	}
	public Set<QueryField<?>> getClipboardFields() {
		return this.clipboardFields;
	}
	public QueryField<?> getClipboardClickedField() {
		return clipboardClickedField;
	}
	@Override
	public void onMenuDismissed() {

	}

	public Set<String> getQueryFieldIds() {
		return this.fieldsById.keySet();
	}
	public Set<String> getQueryFieldLabels() {
		Set<String> labels = new HashSet<String>();
		for (FormPortletField<?> f : this.editableForm.getFormFields()) {
			labels.add(f.getTitle());
		}
		return labels;
	}

	public AmiWebEditableFormPortlet getEditableForm() {
		return editableForm;
	}

	@Override
	public void onFilterDependencyAdded(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {

	}

	@Override
	public void onFilterDependencyRemoved(AmiWebDmManagerImpl amiWebDmManagerImpl, AmiWebDmFilter target, String dmName, String tableName) {

	}
	@Override
	public void onDmDataBeforeFilterChanged(AmiWebDm datamodel) {
	}
	public Map<String, QueryField<?>> getFieldsById() {
		return this.fieldsById;
	}

	@Override
	public String getScrollGripColor() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollGripColor();
	}

	@Override
	public void setScrollGripColor(String scrollGripColor) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setScrollGripColor(scrollGripColor);
	}

	@Override
	public String getScrollTrackColor() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollTrackColor();
	}

	@Override
	public void setScrollTrackColor(String scrollTrackColor) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setScrollTrackColor(scrollTrackColor);
	}

	@Override
	public String getScrollButtonColor() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollButtonColor();
	}

	@Override
	public void setScrollButtonColor(String scrollButtonColor) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setScrollButtonColor(scrollButtonColor);
	}

	@Override
	public String getScrollIconsColor() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollIconsColor();
	}

	@Override
	public void setScrollIconsColor(String scrollIconsColor) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setScrollIconsColor(scrollIconsColor);
	}

	@Override
	public String getScrollBorderColor() {
		if (this.editableForm == null) {
			return null;
		}
		return this.editableForm.getFormPortletStyle().getScrollBorderColor();
	}
	@Override
	public void setScrollBorderColor(String color) {
		if (this.editableForm == null) {
			return;
		}
		this.editableForm.getFormPortletStyle().setScrollBorderColor(color);
	}

	@Override
	public Integer getScrollBarWidth() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollBarWidth();
	}

	@Override
	public void setScrollBarRadius(Integer scrollBarRadius) {
		if (this.editableForm == null) {
			return;
		}
		this.editableForm.getFormPortletStyle().setScrollBarRadius(scrollBarRadius);
	}

	@Override
	public Integer getScrollBarRadius() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getScrollBarRadius();
	}

	@Override
	public void setScrollBarWidth(Integer scrollBarWidth) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setScrollBarWidth(scrollBarWidth);
	}

	public Boolean getShowBottomButtons() {
		if (this.editableForm == null)
			return null;
		return this.editableForm.getFormPortletStyle().getShowBottomButtons();
	}

	public void setShowBottomButtons(Boolean showBottomButtons) {
		if (this.editableForm == null)
			return;
		this.editableForm.getFormPortletStyle().setShowBottomButtons(showBottomButtons == null ? false : showBottomButtons);
	}

	public IndexedList<String, Integer> getFieldZIndexes() {
		return this.fieldsZIndexesById;
	}
	public void setFieldZIndexes(IndexedList<String, Integer> newFieldZIndexes) {
		this.fieldsZIndexesById = newFieldZIndexes;
	}

	public void resetAllQueryFieldPositions() {
		resetQueryFieldPositions(this.fieldsById.values());
	}
	public void resetSelectedQueryFieldPositions() {
		resetQueryFieldPositions(this.editableForm.getEditableManager().getSelectedFields());
	}
	private static void resetQueryFieldPositions(Collection<QueryField<?>> queryFields) {
		for (QueryField<?> q : queryFields)
			q.resetOverridePosition();
	}
	public boolean areAllFieldsAtDefaultPositions() {
		return areFieldsAtDefaultPositions(this.fieldsById.values());
	}
	public boolean areSelectedFieldsAtDefaultPositions() {
		return areFieldsAtDefaultPositions(this.editableForm.getEditableManager().getSelectedFields());
	}
	private static boolean areFieldsAtDefaultPositions(Collection<QueryField<?>> queryFields) {
		for (QueryField<?> q : queryFields)
			if (!q.isAtDefaultPosition())
				return false;
		return true;
	}

	@Override
	public Set<String> getUsedDmVariables(String dmAliasDotName, String dmTable, Set<String> r) {
		if (this.dmSingleton.matches(dmAliasDotName, dmTable)) {
			for (QueryField<?> i : this.getFieldsById().values()) {
				i.getDependencies(r);
			}
		}
		return r;
	}

	@Override
	public String getAmiScriptClassName() {
		return "FormPanel";
	}

	@Override
	protected void layoutChildren() {
		super.layoutChildren();
		for (QueryField<?> qf : this.getFieldsById().values()) {
			qf.updateFormSize();
		}
		this.editableForm.flagLayoutChanged();
	}

	@Override
	public Set<String> getUsedDmAliasDotNames() {
		Set<String> r = new LinkedHashSet<String>();
		r.addAll(this.dmSingleton.getUsedDmAliasDotNames());
		for (AbstractDmQueryField<?> i : this.dmFieldsById.values()) {
			String t = i.getDmName();
			if (t == null)
				continue;
			r.add(t);
		}
		return r;
	}
	@Override
	public Set<String> getUsedDmTables(String aliasDotName) {
		Set<String> r = null;
		for (AbstractDmQueryField<?> i : this.dmFieldsById.values()) {
			if (OH.ne(aliasDotName, i.getDmName()))
				continue;
			if (r == null)
				r = new HashSet<String>();
			r.add(i.getDmTableName());
		}
		if (r == null)
			r = this.dmSingleton.getUsedDmTables(aliasDotName);
		else
			r.addAll(this.dmSingleton.getUsedDmTables(aliasDotName));
		return r;
	};

	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		this.dmSingleton.onDmNameChanged(oldAliasDotName, dm);
	}

	@Override
	public void onDmNameChanged(AmiWebDmManager amiWebDmManagerImpl, String oldAliasDotName, AmiWebDm dm) {
	};
	public void onDmFieldDependencyChanged(AbstractDmQueryField field, String oldDm, String newDm) {
		getService().getDmManager().onPanelDmDependencyChanged(this, oldDm, field.getDmTableName(), false);
		getService().getDmManager().onPanelDmDependencyChanged(this, newDm, field.getDmTableName(), true);
	}

	// tracker code
	public void removeDmDependency(AbstractDmQueryField field, String dmName) {
		String refDm = this.fieldAriToDm.remove(field.getAri());
		if (refDm != null) {
			Integer cnt = this.inUseDm.get(refDm);
			if (cnt != null) {
				cnt -= 1;
				this.inUseDm.put(refDm, cnt);
				if (cnt == 0) {
					// remove dm link
					getService().getDmManager().onPanelDmDependencyChanged(this, dmName, field.getDmTableName(), false);
				}
			} else {
				// what if cnt doesn't exist?
			}
		} else {
			// what if refDm doesn't exist?
		}
	}

	// tracker code
	public void addDmDependency(AbstractDmQueryField field, String dmName) {
		getService().getDmManager().onPanelDmDependencyChanged(this, dmName, field.getDmTableName(), true);
		String refDm = this.fieldAriToDm.get(field.getAri());
		if (refDm == null) {
			// from null -> dm
			this.fieldAriToDm.put(field.getAri(), dmName);
			Integer cnt = this.inUseDm.get(dmName);
			if (cnt != null)
				this.inUseDm.put(dmName, ++cnt);
			else
				this.inUseDm.put(dmName, 1);
		} else {
			//	will reach here when opening the field editor each time
			//			throw new IllegalStateException("dm already exist");
		}

	}

	@Override
	public void addDmLinkFromThisPortlet(AmiWebDmLink link) {
		super.addDmLinkFromThisPortlet(link);
		updateButtons();
	}

	@Override
	public void removeDmLinkFromThisPortlet(AmiWebDmLink link) {
		super.removeDmLinkFromThisPortlet(link);
		updateButtons();
	}

	public com.f1.base.CalcTypes getTemplateVarTypes() {
		BasicCalcTypes vars = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String f : this.editableForm.getFields()) {
			FormPortletField<?> field = this.editableForm.getField(f);
			vars.putType("inputs." + getQueryField(field).getName(), String.class);
		}
		AmiWebDmTableSchema table = getUsedDm();
		CalcTypes varTypes;
		if (table == null)
			varTypes = new CalcTypesTuple2(getPortletVarTypes(), vars);
		else
			varTypes = new CalcTypesTuple3(getPortletVarTypes(), table.getClassTypes(), vars);
		return varTypes;
	}

	public CalcFrame getTemplateVars() {
		MutableCalcFrame vars = new MutableCalcFrame();
		Table table = this.dmSingleton.getTable();
		if (table != null && table.getSize() > 0)
			vars.putAllTypeValues(table.getRow(0));
		for (String f : this.editableForm.getFields()) {//TODO:should only consider hidden fields
			FormPortletField<?> field = this.editableForm.getField(f);
			if (field.isVisible())
				continue;
			String key = "inputs." + getQueryField(field).getName();
			vars.putTypeValue(key, String.class, field.getHtmlLayoutSignature());

		}
		CalcFrame pv = this.getPortletVars();
		vars.putAllTypeValues(pv);
		return vars;
	}

	//	@Override
	//	public void recompileAmiscript() {
	//		super.recompileAmiscript();
	//		for (QueryField<?> i : this.fieldsById.values())
	//			i.recompileAmiscript();
	//	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		r.addAll(this.buttons.values());
		r.addAll(this.fieldsById.values());
		return r;
	}

	@Override
	public void updateAri() {
		super.updateAri();
		for (QueryField<?> i : this.fieldsById.values())
			i.updateAri();
		for (AmiWebButton b : this.buttons.values()) {
			b.updateAri();
		}
	}
	public Set<String> getQueryFieldNames() {
		return this.fieldIdsToNames.getValues();
	}
	public void onVarNameChanged(QueryField<?> queryField, String newVarName) {
		this.fieldIdsToNames.removeByKeyOrThrow(queryField.getId());
		this.fieldIdsToNames.put(queryField.getId(), newVarName);
		for (int i = 0; i < queryField.getVarsCount(); i++) {
			String string = queryField.getName() + queryField.getSuffixNameAt(i);
			this.fieldsByVarName.remove(string);
		}
		for (int i = 0; i < queryField.getVarsCount(); i++) {
			String string = newVarName + queryField.getSuffixNameAt(i);
			this.fieldsByVarName.put(string, queryField);
		}
	}

	final private List<AmiWebQueryFormListener> amiQueryFormListeners = new ArrayList<AmiWebQueryFormListener>();

	public void addQueryFormListener(AmiWebQueryFormListener portletListener) {
		if (this.amiQueryFormListeners.contains(portletListener))
			return;
		this.amiQueryFormListeners.add(portletListener);
	}

	public void removeQueryFormListener(AmiWebQueryFormListener portletListener) {
		this.amiQueryFormListeners.remove(portletListener);
	}

	public void fireFieldAriChanged(QueryField<?> queryField, String oldAri) {
		if (CH.isEmpty(this.amiQueryFormListeners))
			return;
		for (int i = 0; i < this.amiQueryFormListeners.size(); i++) {
			try {
				this.amiQueryFormListeners.get(i).onFieldAriChanged(queryField, oldAri);
			} catch (Exception e) {
				LH.warning(log, "Error with on field ari changed with " + this.amiQueryFormListeners.get(i) + " for " + queryField, e);
			}
		}
	}
	private void fireFieldAdded(QueryField<?> queryField) {
		if (CH.isEmpty(this.amiQueryFormListeners))
			return;
		for (int i = 0; i < this.amiQueryFormListeners.size(); i++) {
			try {
				this.amiQueryFormListeners.get(i).onFieldAdded(queryField);

			} catch (Exception e) {
				LH.warning(log, "Error with on field added with " + this.amiQueryFormListeners.get(i) + " for " + queryField, e);
			}
		}
	}
	private void fireFieldRemoved(QueryField<?> queryField) {
		if (CH.isEmpty(this.amiQueryFormListeners))
			return;
		for (int i = 0; i < this.amiQueryFormListeners.size(); i++) {
			try {
				this.amiQueryFormListeners.get(i).onFieldRemoved(queryField);
			} catch (Exception e) {
				LH.warning(log, "Error with on field removed with " + this.amiQueryFormListeners.get(i) + " for " + queryField, e);
			}
		}
	}
	@Override
	public void onCustomCallback(FormPortlet formPortlet, String customType, Object customParamsJson, Map<String, String> rawAttributes) {
		if ("amiCustomCallback".contentEquals(customType)) {
			List args = (List) customParamsJson;
			String type = AmiUtils.s(args.get(0));
			List remainingArgs = args.subList(1, args.size());
			getAmiScriptCallbacks().execute(CALLBACK_ONAMIJSCALLBACK.getMethodName(), type, remainingArgs);
		}
	}
	public int getSnapSize() {
		return this.getEditableForm().getEditableManager().getSnapsize();
	}
	public void setSnapSize(int snapSize) {
		this.getEditableForm().getEditableManager().setSnapsize(snapSize);
	}
	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebQueryFormSettingsPortlet(generateConfig(), this);
	}
	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		if (formula == this.htmlFormula) {
			DerivedCellCalculator calc = this.htmlFormula.getFormulaCalc();
			this.referencedVarsInTemplate.clear();
			DerivedHelper.getDependencyIds(calc, (Set) this.referencedVarsInTemplate);
			flagTemplateNeedsProcessing();
		}
	}

	@Override
	public void setScrollBarHideArrows(Boolean hide) {
		if (this.editableForm == null) {
			return;
		}
		this.editableForm.getFormPortletStyle().setScrollBarHideArrows(hide);

	}
	@Override
	public Boolean getScrollBarHideArrows() {
		return this.editableForm.getFormPortletStyle().getScrollBarHideArrows();
	}

	/**
	 * returns the dm referenced by this field, given its ari
	 */
	public String getFieldRefDm(String fieldAri) {
		return this.fieldAriToDm.get(fieldAri);
	}
	@Override
	public void setScrollBarCornerColor(String color) {
		if (this.editableForm == null) {
			return;
		}
		this.editableForm.getFormPortletStyle().setScrollBarCornerColor(color);

	}
	@Override
	public String getScrollBarCornerColor() {
		return this.editableForm.getFormPortletStyle().getScrollBarCornerColor();
	}
	public QueryField<?> getFocusedField() {
		FormPortletField<?> field = this.editableForm.getFocusField();
		return field == null ? null : this.fieldsById.get(field.getId());
	}

}
