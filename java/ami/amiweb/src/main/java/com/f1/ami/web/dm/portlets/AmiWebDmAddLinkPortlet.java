package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebDmPortlet;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebLayoutFilesManager;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebRealtimePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkImpl;
import com.f1.ami.web.dm.AmiWebDmLinkWhereClause;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmRequest;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebDmAddLinkPortlet extends GridPortlet
		implements FormPortletContextMenuFactory, FormPortletContextMenuListener, FormPortletListener, AmiWebDmLinkWhereFieldListener {
	private static final Logger log = LH.get();
	final private AmiWebPortlet targetPortlet;
	final private AmiWebPortlet sourcePortlet;

	final private FormPortlet buttonsFp;
	final private FormPortletSelectField<Short> actionType;
	final private FormPortletTextField contextMenuField;
	final private FormPortletTextField relIdField;
	final private FormPortletSelectField<String> relAliasField;
	final private FormPortletButton cancelButton;
	final private FormPortlet optionsFp;
	final private FormPortletButton updateButton;
	final private AmiWebService service;
	final private AmiWebDmLink link;
	final private FormPortletSelectField<Short> typeField;
	private com.f1.base.CalcTypes sourceVariables;
	private com.f1.base.CalcTypes targetVariables;
	private FormPortletSelectField<Short> noSelectionField;
	final private AmiWebEditAmiScriptCallbacksPortlet callbacksPortlet;
	private AmiWebHeaderPortlet header;
	private FormPortletTitleField namingTitleField;
	private FormPortletTitleField behaviourTitleField;
	private FormPortletTextField joinOn;
	private AmiWebDmWhereFieldsForm whereFieldsForm;
	private AmiWebDm targetDm;
	private AmiWebDmManager dmManager;
	private AmiWebDatasourceWrapper targetDs;
	private FormPortletSelectField<String> underlyingDmField;
	private FormPortletButton testButton;
	private boolean isAdd;
	private GridPortlet optionsTab;
	private TabPortlet tabsPortlet;

	public AmiWebDmAddLinkPortlet(PortletConfig config, AmiWebDmLink link, com.f1.base.CalcTypes sourceVariables, com.f1.base.CalcTypes targetVariables, AmiWebDatasourceWrapper targetDs) {
		super(config);
		this.isAdd = link.getLinkUid() == null;
		this.service = AmiWebUtils.getService(config.getPortletManager());
		if (isAdd) {
			link.setLinkUid(getManager().generateId());
			link.setOptions((short) (AmiWebDmLink.OPTION_EMPTYSEL_IGNORE | AmiWebDmLink.OPTION_ON_SELECT));
			service.getDmManager().addDmLink(link);
		}
		this.link = link;

		this.header = new AmiWebHeaderPortlet(generateConfig());
		this.header.setShowSearch(false);
		String description = describePortlet(link.getSourcePanelAliasDotId()) + " &#x2192; " + describePortlet(link.getTargetPanelAliasDotId());
		this.header.updateBlurbPortletLayout("Relationship: " + description, "");
		this.header.setShowLegend(false);
		this.header.setInformationHeaderHeight(70);
		this.header.setShowBar(false);
		this.sourceVariables = sourceVariables;
		this.targetVariables = targetVariables;
		this.sourcePortlet = link.getSourcePanelNoThrow();
		this.targetPortlet = link.getTargetPanelNoThrow();
		this.optionsFp = new FormPortlet(generateConfig());
		this.namingTitleField = this.optionsFp.addField(new FormPortletTitleField("Naming"));
		this.relAliasField = this.optionsFp.addField(new FormPortletSelectField<String>(String.class, "Owning Layout:"));
		if (!isAdd && this.service.getLayoutFilesManager().getLayoutByFullAlias(this.link.getAmiLayoutFullAlias()).isReadonly())
			this.relAliasField.setDisabled(true);

		this.relAliasField.setWidth(300);
		AmiWebLayoutFilesManager layoutFilesManager = this.service.getLayoutFilesManager();
		for (String s : layoutFilesManager.getAvailableAliasesUp(CH.s(sourcePortlet.getAmiLayoutFullAlias(), targetPortlet.getAmiLayoutFullAlias()))) {
			this.relAliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		}
		this.relIdField = this.optionsFp.addField(new FormPortletTextField("Relationship Id:").setWidth(300));
		this.contextMenuField = this.optionsFp.addField(new FormPortletTextField("Relationship Name:").setWidth(300));
		this.dmManager = service.getDmManager();
		AmiWebPortlet portlet = link.getTargetPanelNoThrow();
		this.targetDm = this.dmManager.getDmByAliasDotName(link.getTargetDmAliasDotName());
		// Add field for user to choose underlying DMS for a relationship to a DMT
		if (this.targetDm instanceof AmiWebDmsImpl) { // New relationship to DMT
			addChooseDmsField((AmiWebDmsImpl) this.targetDm);
		} else if (portlet instanceof AmiWebDmPortlet) { // Edit relationship to DMTr
			AmiWebDmPortlet dmPortlet = (AmiWebDmPortlet) portlet;
			AmiWebDm dm = this.dmManager.getDmByAliasDotName(CH.first(dmPortlet.getUsedDmAliasDotNames()));
			addChooseDmsField((AmiWebDmsImpl) dm);
		}
		this.whereFieldsForm = new AmiWebDmWhereFieldsForm(this, targetDm, targetDs, generateConfig(), this.link);
		this.setEnableAdditionalWhereClauses(true);
		this.behaviourTitleField = this.optionsFp.addField(new FormPortletTitleField("Behavior"));
		this.typeField = this.optionsFp.addField(new FormPortletSelectField<Short>(Short.class, "Run Relationship:"));
		this.typeField.setWidth(380);
		this.typeField.addOption(AmiWebDmLink.OPTION_ON_SELECT, "In real-time when user highlights rows causing query to change");
		this.typeField.addOption(AmiWebDmLink.OPTION_ON_SELECT_FORCE, "In real-time when user highlights rows");
		this.typeField.addOption(AmiWebDmLink.OPTION_ON_RIGHT_CLICK_MENU, "Only when user selects from right-click menu");
		this.typeField.addOption(AmiWebDmLink.OPTION_ON_USER_DBL_CLICK, "Only when user double clicks on the row");
		this.typeField.addOption(AmiWebDmLink.OPTION_ON_AMISCRIPT, "Only on amiscript (see Relationship::execute method)");
		this.actionType = new FormPortletSelectField<Short>(Short.class, "Display Option:");
		this.optionsFp.addField(actionType);
		this.actionType.addOption(AmiWebDmLink.OPTION_BRING_TO_FRONT, "Bring target panel to front");
		this.actionType.addOption((short) 0, "Leave target panel as is");
		this.noSelectionField = this.optionsFp.addField(new FormPortletSelectField<Short>(Short.class, "When Nothing Selected: "));
		this.noSelectionField.addOption(AmiWebDmLink.OPTION_EMPTYSEL_IGNORE, "Do nothing");
		this.noSelectionField.addOption(AmiWebDmLink.OPTION_EMPTYSEL_CLEAR, "Clear");
		this.noSelectionField.addOption(AmiWebDmLink.OPTION_EMPTYSEL_SHOWALL, "Show everything");
		this.noSelectionField.addOption(AmiWebDmLink.OPTION_EMPTYSEL_ALLSEL, "Same as selecting everything");
		this.targetDs = targetDs;

		this.buttonsFp = new FormPortlet(generateConfig());

		this.buttonsFp.getFormPortletStyle().setLabelsWidth(200);
		this.buttonsFp.setMenuFactory(this);
		this.buttonsFp.addMenuListener(this);
		this.buttonsFp.addFormPortletListener(this);

		this.optionsFp.addMenuListener(this);
		this.optionsFp.setMenuFactory(this);
		this.optionsFp.addFormPortletListener(this);
		this.whereFieldsForm.setMenuFactory(this);
		this.whereFieldsForm.addMenuListener(this);

		this.updateButton = buttonsFp.addButton(new FormPortletButton(this.link != null ? "Update Relationship" : "Create Relationship"));
		this.cancelButton = buttonsFp.addButton(new FormPortletButton("Cancel"));
		this.testButton = buttonsFp.addButton(new FormPortletButton("Evaluate Variables"));
		this.callbacksPortlet = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), this.link.getAmiScript());

		//Init other
		if (!this.isAdd) {
			this.relAliasField.setValue(this.link.getAmiLayoutFullAlias());
			this.relIdField.setValue(this.link.getRelationshipId());
			this.contextMenuField.setValue(this.link.getTitle());
			this.actionType.setValue((short) (link.getOptions() & AmiWebDmLink.OPTION_BRING_TO_FRONT));
			this.typeField.setValue((short) (link.getOptions() & AmiWebDmLink.OPTIONS_MASK_FOR_UPDATE_RELATIONSHIP));
			this.noSelectionField.setValue((short) (link.getOptions() & AmiWebDmLink.OPTIONS_FOR_EMPTYSEL));
		} else {
			this.contextMenuField.setValue("Show " + this.targetPortlet.getAmiTitle(false));
			this.relIdField.setValue(this.link.getRelationshipId());
			this.relAliasField.setValue(this.link.getAmiLayoutFullAlias());
			this.typeField.setValue(AmiWebDmLink.OPTION_ON_SELECT);
		}

		this.optionsTab = new GridPortlet(generateConfig());
		optionsTab.addChild(this.whereFieldsForm, 0, 0);
		optionsTab.addChild(this.optionsFp, 0, 1);
		this.tabsPortlet = new TabPortlet(generateConfig());
		this.tabsPortlet.setIsCustomizable(false);
		this.tabsPortlet.addChild("Configure", optionsTab);
		this.tabsPortlet.addChild("Callbacks", this.callbacksPortlet);

		this.addChild(tabsPortlet, 0, 0);
		this.addChild(this.buttonsFp, 0, 1);

		setRowSize(1, buttonsFp.getButtonPanelHeight());//buttons
		setSuggestedSize(1000, 700);
		whereFieldsForm.initWhereFieldsFromDmLink();
		onFieldValueChanged(optionsFp, this.typeField, null);

		this.updateSuggestedSizeOfWhereFieldForm();
	}
	private String describePortlet(String portletId) {
		AmiWebPortlet r = service.getPortletByPortletId(portletId);
		return r == null ? portletId : r.getAmiPanelId();
	}
	private void addChooseDmsField(AmiWebDm dmt) {
		List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		AmiWebDmUtils.getUnderlyingDatamodels(dmt, datamodels);
		this.underlyingDmField = this.optionsFp.addField(new FormPortletSelectField<String>(String.class, "Underlying Datamodel"));
		for (AmiWebDm dms : datamodels) {
			if (!this.underlyingDmField.containsOption(dms.getAmiLayoutFullAliasDotId()))
				this.underlyingDmField.addOption(dms.getAmiLayoutFullAliasDotId(), dms.getDmName());
		}
		if (dmt != this.targetDm) { // This should only be true when we are editing a relationship (i.e. we set the value to the existing target DMS)
			this.underlyingDmField.setValue(this.targetDm.getAmiLayoutFullAliasDotId());
		}
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == cancelButton) {
			close();
			return;
		}
		if (service.getLayoutFilesManager().getLayoutByFullAlias(this.relAliasField.getValue()).isReadonly()) {
			getManager().showAlert("Readonly layout: " + WebHelper.escapeHtml(AmiWebUtils.formatLayoutAlias(this.relAliasField.getValue())));
			return;
		}
		if (button == testButton) {
			StringBuilder errorSink = new StringBuilder();
			AmiWebDmLinkImpl link = createLink(errorSink, true);
			if (errorSink.length() > 0) {
				getManager().showAlert(errorSink.toString());
				return;
			}
			Table values = AmiWebDmUtils.getSourceValues(service, link);
			AmiWebDmRequest req = AmiWebDmUtils.buildReq(service.getDebugManager(), service, link, values, errorSink, service.createStackFrame(link));
			if (errorSink.length() > 0) {
				getManager().showAlert(errorSink.toString());
				return;
			}
			SimpleFastTextPortlet sftp = new SimpleFastTextPortlet(generateConfig());
			int max = 20;
			for (Entry<String, Object> i : req.getVariables().entrySet()) {
				String string = AmiUtils.s(i.getValue());
				String[] parts = SH.splitLines(string);
				if (parts.length > 1) {
					sftp.appendLine(i.getKey() + "=", parts[0], "_fg=#006600");
					max = Math.max(parts[0].length(), max);
					for (int n = 1; n < parts.length; n++) {
						sftp.appendLine("", parts[n], "_fg=#006600");
						max = Math.max(parts[n].length(), max);
					}
				} else {
					max = Math.max(string.length(), max);
					sftp.appendLine(i.getKey() + "=", string, "_fg=#006600");
				}
				sftp.appendLine("", "", "");
			}
			sftp.setMaxCharsPerLine(max);
			sftp.setLabelStyle("_fm=bold|_fg=#000066");
			RootPortletDialog dialog = getManager().showDialog("Relationship values", sftp, 600, 300);
			link.close();
			dialog.setCloseOnClickOutside(true);
			return;
		}
		StringBuilder errorSink = new StringBuilder();
		if (this.link.getLinkUid() != null) {
			String newRelId = this.relIdField.getValue();
			String oldRelId = this.link.getRelationshipId();
			// checks if there is an existing rel with the new relId. if so it does not create a new one and returns immediately
			AmiWebDmLink existing = service.getDmManager().getDmLinkByAliasDotRelationshipId(AmiWebUtils.getFullAlias(this.relAliasField.getValue(), newRelId));
			if (existing != null && existing != link) {
				errorSink.append("Duplicate Relationship Id: <B>" + newRelId);
				getManager().showAlert(errorSink.toString());
				return;
			}
			this.service.getDmManager().removeDmLink(this.link.getLinkUid());
			if (OH.ne(oldRelId, newRelId)) {
				this.link.setRelationshipId(newRelId);
			}
		}
		AmiWebDmLinkImpl dmLink = createLink(errorSink, false);
		if (dmLink == null) {
			getManager().showAlert(errorSink.toString());
			return;
		}
		this.service.getDmManager().addDmLink(dmLink);
		isAdd = false;
		close();
	}
	public AmiWebDmLinkImpl createLink(StringBuilder errorSink, boolean isTest) {
		final String title = contextMenuField.getValue();
		if (SH.isnt(title)) {
			errorSink.append("Title required");
			return null;
		}
		if (this.underlyingDmField != null && this.underlyingDmField.getValue() == null) {
			errorSink.append("Please select an underlying datamodel to query.");
			return null;
		}
		String id = SH.trim(relIdField.getValue());
		if (SH.isnt(id)) {
			id = AmiWebUtils.toSuggestedVarname(title);
			id = service.getDmManager().getNextRelationshipId(this.relAliasField.getValue(), id);
		} else {
			if (!AmiUtils.isValidVariableName(id, false, false)) {
				errorSink.append("Relationship Id is not valid: <B>" + id);
				return null;
			}
		}
		AmiWebDmLink existing = service.getDmManager().getDmLinkByAliasDotRelationshipId(AmiWebUtils.getFullAlias(this.relAliasField.getValue(), id));
		if (existing != null && existing != link) {
			errorSink.append("Duplicate Relationship Id: <B>" + id);
			return null;
		}
		final String linkId = link.getLinkUid() != null ? link.getLinkUid() : this.getManager().generateId();
		final AmiWebDmLinkImpl dmLink = new AmiWebDmLinkImpl(service.getDmManager(), link.getAmiLayoutFullAlias(), linkId, title,
				(short) (actionType.getValue() | typeField.getValue() | noSelectionField.getValue()));
		dmLink.setSourcePanelAliasDotId(link.getSourcePanelAliasDotId());
		dmLink.setTargetPanelAliasDotId(link.getTargetPanelAliasDotId());
		dmLink.setTargetDm(link.getTargetDmAliasDotName());
		dmLink.setSourceDm(link.getSourceDmAliasDotName(), link.getSourceDmTableName());
		if (!isTest)
			dmLink.setRelationshipId(id);
		dmLink.setAmiLayoutFullAlias(this.relAliasField.getValue());
		this.whereFieldsForm.addWhereFieldsToDmLink(dmLink, errorSink);
		this.callbacksPortlet.applyTo(dmLink.getAmiScript(), null);
		//		dmLink.setAmiScript(this.callbacksPortlet.getValue());
		return dmLink;
	}
	@Override
	public void onClosed() {
		if (isAdd) {
			this.service.getDmManager().removeDmLink(this.link.getLinkUid());
		}
		super.onClosed();
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (formPortlet != whereFieldsForm)
			return null;
		if (field.getName().equals(AmiWebDmWhereFieldsForm.TYPE_WHERECLAUSE)) {
			BasicWebMenu r = new BasicWebMenu();
			if (targetDs != null) {
				AmiWebDmUtils.createDatasourceOperatorsMenus(r, getManager(), targetDs);
			} else
				AmiWebDmUtils.createDatasourceOperatorsMenus(r, getManager(), targetDm);

			AmiWebMenuUtils.createOperatorsMenu(r, service, this.link.getAmiLayoutFullAlias());
			r.add(AmiWebMenuUtils.createVariablesMenu("Source Columns", "Source_", this.sourceVariables));
			r.add(AmiWebMenuUtils.createVariablesMenu("Target Columns", "Target_", this.targetVariables));
			r.add(AmiWebMenuUtils.createGlobalVariablesMenu("Variables", "Global_", service, this.link));

			this.whereFieldsForm.createWhereFieldsContextMenu(field, r);
			return r;
		}
		return null;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField field) {
		if (portlet == this.whereFieldsForm) {
			String txt = AmiWebMenuUtils.parseContextMenuAction(service, action);
			if (txt != null) {
				if (action.startsWith("var_") && !(targetPortlet instanceof AmiWebRealtimePortlet)) {
					String var = SH.stripPrefix(action, "var_", true);
					if (SH.startsWith(var, "Global_")) {
						String varname = SH.stripPrefix(var, "Global_", true);
						if (this.service.getScriptManager(this.link.getAmiLayoutFullAlias()).getConstType(this.link, varname) == String.class)
							((FormPortletTextEditField) field).insertAtCursor("\"${" + AmiUtils.escapeVarName(varname) + "}\"");
						else
							((FormPortletTextEditField) field).insertAtCursor("${" + AmiUtils.escapeVarName(varname) + "}");
					} else if (SH.startsWith(var, "Source_")) {
						String varname = SH.stripPrefix(var, "Source_", true);
						String quotes;
						if (OH.eq(sourceVariables.getType(varname), String.class)) {
							AmiWebDm targetDm = this.service.getDmManager().getDmByAliasDotName(this.link.getTargetDmAliasDotName());
							List<String> quotesSink = new ArrayList<String>();
							AmiWebDmUtils.getUnderlyingDatasourceQuotes(getManager(), targetDm, quotesSink);
							if (quotesSink.size() != 0)
								quotes = quotesSink.get(0);
							else if (this.targetDs != null) {
								quotes = AmiWebDmUtils.getDatasourceQuoteType(getManager(), this.targetDs);
								if (quotes == null)
									quotes = "\"";
							} else
								quotes = "\"";
						} else
							quotes = "";
						((FormPortletTextEditField) field).insertAtCursor(quotes + "${" + AmiUtils.escapeVarName(var) + "}" + quotes);
					} else if (SH.startsWith(var, "Target_")) {
						String varname = SH.stripPrefix(var, "Target_", true);
						((FormPortletTextEditField) field).insertAtCursor(AmiUtils.escapeVarName(varname));
					} else
						((FormPortletTextEditField) field).insertAtCursor(txt);
				} else
					((FormPortletTextEditField) field).insertAtCursor(txt);
			} else {
				this.whereFieldsForm.onWhereFieldsFormContextMenu(action);
			}
			whereFieldsForm.onFieldValueChanged(portlet, field, null);
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.typeField) {
			short value = this.typeField.getValue();
			boolean t = !MH.anyBits(value, AmiWebDmLink.OPTION_ON_SELECT | AmiWebDmLink.OPTION_ON_SELECT_FORCE | AmiWebDmLink.OPTION_ON_AMISCRIPT);
			this.noSelectionField.setDisabled(t);
			if (t)
				this.noSelectionField.setValue(AmiWebDmLink.OPTION_EMPTYSEL_IGNORE);
		} else if (this.underlyingDmField != null && field == this.underlyingDmField) {
			AmiWebDm dm = this.service.getDmManager().getDmByAliasDotName(this.underlyingDmField.getValue());
			if (dm == null)
				this.link.setTargetDm(null);
			else {
				this.targetDm = dm;
				this.link.setTargetDm(dm.getAmiLayoutFullAliasDotId());
			}
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (!(field instanceof FormPortletTextEditField)) {
			if (keycode == SH.CHAR_CR) {
				onButtonPressed(formPortlet, this.updateButton);
			}
		}
	}

	public com.f1.base.CalcTypes getTargetVariables() {
		return targetVariables;
	}
	public void setTargetVariables(com.f1.base.CalcTypes targetVariables) {
		this.targetVariables = targetVariables;
	}
	public com.f1.base.CalcTypes getSourceVariables() {
		return sourceVariables;
	}
	public void setSourceVariables(com.f1.base.CalcTypes sourceVariables) {
		this.sourceVariables = sourceVariables;
	}
	public void setWhere(String varname, String whereClause) {
		int pos = this.whereFieldsForm.indexOfVarname(varname);
		if (pos >= 0) {
			this.whereFieldsForm.setWhereClause(whereClause, pos);
		} else {
			AmiWebDmLinkWhereClause whereClauseObject = this.whereFieldsForm.getWhereClauseObject();
			this.whereFieldsForm.addWhereField(whereClauseObject);
		}
	}
	private void updateSuggestedSizeOfWhereFieldForm() {
		this.optionsTab.setRowSize(0, whereFieldsForm.getSuggestedHeight(null));
	}

	@Override
	public void onWhereFieldAdded(AmiWebDmWhereFieldsForm wherefield, int position) {
		updateSuggestedSizeOfWhereFieldForm();
	}
	@Override
	public void onWhereFieldRemoved(AmiWebDmWhereFieldsForm wherefield, String varname) {
		updateSuggestedSizeOfWhereFieldForm();
	}

	public void setEnableAdditionalWhereClauses(boolean enableAdditionalWhereClauses) {
		this.whereFieldsForm.setEnableAdditionalWhereClauses(enableAdditionalWhereClauses);
	}
	public void setTargetDs(AmiWebDatasourceWrapper targetDs) {
		this.targetDs = targetDs;
	}
	public AmiWebDmLink getCurrentLink() {
		return this.link;
	}
	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.callbacksPortlet;
	}

}
