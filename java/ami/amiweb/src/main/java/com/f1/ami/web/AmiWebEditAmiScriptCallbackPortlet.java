package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiFlowControlPauseSql;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.ami.web.AmiWebAmiObjectsVariablesPortlet.AmiObjectVariable;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.amiscript.AmiWebScriptRunnerListener;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmRequest;
import com.f1.ami.web.dm.AmiWebDmTablesetSchema;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.dm.portlets.AmiWebDmViewDataPortlet;
import com.f1.ami.web.dm.portlets.AmiWebLogViewerPortlet;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.Form;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.Tableset;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DebugPause;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebEditAmiScriptCallbackPortlet extends GridPortlet implements FormPortletListener, AmiDebugMessageListener, FormPortletContextMenuFactory,
		FormPortletContextMenuListener, AmiWebScriptRunnerListener, AmiWebEditSchemaPortletListener, AmiWebAmiObjectsVariablesListener, AmiWebBreakpointEditor {

	private static final int MIN_SCREEN_HEIGHT = 900;
	private static final int MIN_SCREEN_WIDTH = 1440;
	private static final Logger log = LH.get();
	final private AmiWebService service;
	final private AmiWebFormPortletAmiScriptField amiscriptField;
	final private String callbackName;
	final private FormPortlet formPortlet;
	final private FormPortletButtonField applyButton;
	final private FormPortletButtonField testButton;
	private FormPortletButtonField stepoverButton;

	//add debug state
	private byte debugState;

	final private HtmlPortlet errorPortlet;
	final private TabPortlet outputTabsPortlet;
	final private DividerPortlet dividerPortlet;
	final private DividerPortlet dividerPortlet2;
	final private FormPortletTextField timeoutField;
	final private FormPortletTextField limitField;
	final private FormPortletButtonField editCustomInputButton;
	final private FormPortletSelectField<String> datasourceField;
	final private AmiWebDebugManagerImpl debugManager;
	final private AmiWebWarningDialogPortlet eventViewerPortlet;
	final private AmiWebDebugPortlet debuggerTree;
	final private AmiWebLogViewerPortlet logViewerPortlet;
	final private AmiWebDmViewDataPortlet tableViewerPortlet;
	final private AmiWebAmiObjectsVariablesPortlet variablesPortlet;
	final private String origVal;
	final private Tab errorsTab, logViewerTab, eventViewerTab, tableViewerTab, schemaTab, debugTab;
	final private AmiWebAmiScriptCallback editedCallback;
	final private AmiWebAmiScriptCallback originalCallback;
	final private TreeSet<String> availableTestingRelationships = new TreeSet<String>();
	final private AmiWebEditSchemaPortlet schemaPortlet;
	final private Set<String> usedDatasources = new HashSet<String>();
	final private FormPortletMultiCheckboxField<String> usedDatamodels;
	final private FormPortletCheckboxField keepTablesetData;
	final private FormPortletCheckboxField dynamicDatamodel;

	final private boolean isReadonly;
	private Tab owningTab;
	private String lastVal;
	private boolean isModified;
	public static final byte NOT_RUNNING = 0;
	public static final byte RUNNING = 1;
	public static final byte DEBUG = 2;
	private byte isRunning = NOT_RUNNING;
	private boolean hasError;
	private AmiWebScriptRunner runner;
	private Tableset tableset;
	private AmiWebDomObject thiz;
	private String amiLayoutAlias;
	private FormPortlet scriptPortlet;
	private HashSet<String> restrictedDatamodels;
	private String callbackTitle;
	private AmiWebScriptRunner debugScriptRunner;
	private int[] lineEnds;
	private TestPause pause;
	private Object returnValue;
	private AmiDebugMessage latestError;
	private AmiWebEditAmiscriptTestPreparer testPreparer;

	public void setAvailableTestingRelationships(Set<String> aliasDotRelId) {
		this.availableTestingRelationships.clear();
		this.availableTestingRelationships.addAll(aliasDotRelId);
	}

	public AmiWebEditAmiScriptCallbackPortlet(PortletConfig config, AmiWebService service, AmiWebAmiScriptCallback callback, String layoutAlias, AmiWebDomObject thiz) {
		super(config);
		this.service = service;
		this.originalCallback = callback;
		this.amiLayoutAlias = layoutAlias;
		this.thiz = thiz;
		this.debugState = AmiWebBreakpointManager.DEBUG_UNINITIALIZED;
		this.editedCallback = new AmiWebAmiScriptCallback(this.amiLayoutAlias, service, this.originalCallback);
		//		this.service.getDomObjectsManager().addCallback(this.editedCallback);
		//		LH.info(log, "CALLBACK CREATED FOR EDITING :" + this.callback, " original callback: ", callback, " name: ", callback.getName());
		this.callbackName = this.editedCallback.getName();
		rebuildCallbackTitle();
		this.debugManager = new AmiWebDebugManagerImpl(this.service);
		this.debugManager.setShouldDebug(AmiDebugMessage.SEVERITY_INFO, true);
		this.debugManager.setShouldDebug(AmiDebugMessage.SEVERITY_WARNING, true);
		this.eventViewerPortlet = new AmiWebWarningDialogPortlet(generateConfig(), service, debugManager, true, true);
		this.eventViewerPortlet.removeButtons();
		this.logViewerPortlet = new AmiWebLogViewerPortlet(generateConfig());
		this.debugManager.addDebugMessageListener(this.logViewerPortlet);
		this.debugManager.addDebugMessageListener(this);//must be after the log view listener
		this.tableViewerPortlet = new AmiWebDmViewDataPortlet(generateConfig());
		this.schemaPortlet = new AmiWebEditSchemaPortlet(this);
		this.schemaPortlet.setSchema(this.editedCallback.getReturnSchema());
		this.schemaPortlet.addSchemaChangeListener(this);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = root.getWidth();
		int height = root.getHeight();
		this.scriptPortlet = new FormPortlet(generateConfig());
		this.formPortlet = new FormPortlet(generateConfig());
		this.outputTabsPortlet = new TabPortlet(generateConfig());
		this.outputTabsPortlet.getTabPortletStyle().setTabPaddingTop(0);
		this.outputTabsPortlet.getTabPortletStyle().setTabPaddingBottom(0);
		this.outputTabsPortlet.getTabPortletStyle().setTabPaddingStart(0);
		this.outputTabsPortlet.getTabPortletStyle().setTabSpacing(4);
		this.outputTabsPortlet.getTabPortletStyle().setFontSize(14);
		this.outputTabsPortlet.getTabPortletStyle().setLeftRounding(2);
		this.outputTabsPortlet.getTabPortletStyle().setRightRounding(2);
		this.outputTabsPortlet.getTabPortletStyle().setTabHeight(22);
		this.outputTabsPortlet.getTabPortletStyle().setBackgroundColor("#AAAAAA");
		this.errorPortlet = new HtmlPortlet(generateConfig());
		this.debuggerTree = new AmiWebDebugPortlet(generateConfig());
		errorsTab = this.outputTabsPortlet.addChild("  Errors  ", this.errorPortlet);
		logViewerTab = this.outputTabsPortlet.addChild("  Logs  ", this.logViewerPortlet);
		eventViewerTab = this.outputTabsPortlet.addChild("  Event Viewer  ", this.eventViewerPortlet);
		debugTab = this.outputTabsPortlet.addChild("  Debugger  ", this.debuggerTree);
		schemaTab = this.outputTabsPortlet.addChild("  Schema  ", this.schemaPortlet);
		tableViewerTab = this.outputTabsPortlet.addChild("  Tables  ", this.tableViewerPortlet);
		this.tableViewerPortlet.hideButtons();
		this.outputTabsPortlet.setIsCustomizable(false);

		this.isReadonly = this.service.getLayoutFilesManager().getLayoutByFullAlias(layoutAlias).isReadonly();
		this.amiscriptField = scriptPortlet.addField(new AmiWebFormPortletAmiScriptField("", config.getPortletManager(), layoutAlias));
		amiscriptField.setCssStyle("style.border=0px");
		amiscriptField.setLeftTopRightBottom(0, 0, 0, 0);

		String val = SH.noNull(this.editedCallback.getAmiscript(false));
		this.origVal = val;
		this.lastVal = val;
		this.isModified = false;
		amiscriptField.setValue(val);
		rebuildAutocompleteValues();

		double defaultTimeout = service.getDefaultTimeoutMs() / 1000d;
		int defaultLimit = service.getDefaultLimit();
		this.keepTablesetData = formPortlet.addField(new FormPortletCheckboxField("Keep Output: "));
		this.dynamicDatamodel = formPortlet.addField(new FormPortletCheckboxField("Dynamic DM: "));
		if (width < MIN_SCREEN_WIDTH && height < MIN_SCREEN_HEIGHT) {
			this.applyButton = formPortlet.addField(new FormPortletButtonField("").setValue("Apply"));
			this.testButton = formPortlet.addField(new FormPortletButtonField("").setValue("Test"));
			applyButton.setRightPosPx(121).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
			testButton.setRightPosPx(184).setBottomPosPx(3).setWidthPx(50).setHeightPx(22);
			this.datasourceField = formPortlet.addField(new FormPortletSelectField<String>(String.class, "DS:"));
			datasourceField.setLeftPosPx(285).setBottomPosPx(3).setWidthPx(110).setHeightPx(22);
			this.usedDatamodels = formPortlet.addField(new FormPortletMultiCheckboxField<String>(String.class, "DMs: "));
			usedDatamodels.setLeftPosPx(435).setBottomPosPx(4).setWidthPx(95).setHeightPx(20);
			keepTablesetData.setLeftPosPx(620).setBottomPosPx(4).setWidthPx(30).setHeightPx(20);
			dynamicDatamodel.setLeftPosPx(740).setBottomPosPx(4).setWidthPx(30).setHeightPx(20);

		} else {
			this.applyButton = formPortlet.addField(new FormPortletButtonField("").setValue("Apply (ctrl+enter)"));
			this.testButton = formPortlet.addField(new FormPortletButtonField("").setValue("Test (alt+enter)"));
			applyButton.setRightPosPx(121).setBottomPosPx(3).setWidthPx(105).setHeightPx(22);
			testButton.setRightPosPx(234).setBottomPosPx(3).setWidthPx(110).setHeightPx(22);
			this.datasourceField = formPortlet.addField(new FormPortletSelectField<String>(String.class, "Datasource:"));
			datasourceField.setLeftPosPx(350).setBottomPosPx(3).setWidthPx(110).setHeightPx(22);
			this.usedDatamodels = formPortlet.addField(new FormPortletMultiCheckboxField<String>(String.class, "Datamodels:"));
			usedDatamodels.setLeftPosPx(550).setBottomPosPx(4).setWidthPx(95).setHeightPx(20);
			keepTablesetData.setLeftPosPx(750).setBottomPosPx(4).setWidthPx(30).setHeightPx(20);
			dynamicDatamodel.setLeftPosPx(870).setBottomPosPx(4).setWidthPx(30).setHeightPx(20);
		}
		this.timeoutField = formPortlet.addField(new FormPortletTextField("Timeout (sec):"));
		this.timeoutField.setHelp("Override&nbsp;Timeout&nbsp;(in&nbsp;seconds)<br><b>Blank</b> - No Timeout<br><b>'default'</b> - dashboard default(" + defaultTimeout
				+ ")<br><b>Non-negative number</b> - of seconds");
		this.limitField = formPortlet.addField(new FormPortletTextField("Limit:"));
		this.limitField.setHelp("Override&nbsp;max&nbsp;rows&nbsp;a&nbsp;SQL&nbsp;query&nbsp;will&nbsp;return<BR><b>Blank</b> - No Limit <BR><b>'default'</b> - dashboard default("
				+ defaultLimit + ")<BR><b>Non-negative number</b> - Number of rows");

		this.datasourceField.setHelp("The default datasource to use in an EXECUTE clause<BR>(adding USE <i>ds=SOME_DATASOURCE</i> EXECUTE will override the default) ");

		this.usedDatamodels.setHelp("The datamodels to import tables from before running this script");

		this.keepTablesetData.setHelp("If set, then the output tables are not cleared when the callback is rerun");
		this.dynamicDatamodel.setHelp("If not checked, the blender gets schema-mapped result from underlying datamodel(s)");
		datasourceField.addOption(null, "<No Datasource>");
		for (AmiWebDatasourceWrapper s2 : this.service.getSystemObjectsManager().getDatasources()) {
			datasourceField.addOption(s2.getName(), s2.getName() + " ( " + s2.getAdapter() + " )");
		}
		for (AmiWebDm i : this.service.getDmManager().getDatamodels())
			if (AmiWebUtils.isParentAliasOrSame(this.amiLayoutAlias, i.getAmiLayoutFullAlias()))
				this.usedDatamodels.addOption(i.getAmiLayoutFullAliasDotId(), i.getAmiLayoutFullAliasDotId());
		this.usedDatamodels.setValueNoThrow(this.editedCallback.getInputDatamodels());
		this.keepTablesetData.setValueNoThrow(this.editedCallback.getKeepTablesetOnRerun());
		this.dynamicDatamodel.setValueNoThrow(this.editedCallback.getIsDynamicDatamodel());
		this.datasourceField.sortOptionsByName();
		String defaultDatasource = this.editedCallback.getDefaultDatasource();
		if (this.datasourceField.getOptionNoThrow(defaultDatasource) == null)
			this.datasourceField.addOption(defaultDatasource, defaultDatasource + " <INVALID>");
		this.datasourceField.setValue(defaultDatasource);
		this.datasourceField.setLabelWidthPx(100);
		this.timeoutField.setDefaultValue(SH.toString(defaultTimeout));
		this.limitField.setDefaultValue(SH.toString(defaultLimit));

		timeoutField.setLeftPosPx(100).setBottomPosPx(3).setWidthPx(55).setHeightPx(22);
		limitField.setLeftPosPx(200).setBottomPosPx(3).setWidthPx(55).setHeightPx(22).setLabelWidthPx(164);

		this.editCustomInputButton = this.formPortlet.addField(new FormPortletButtonField(""));

		this.editCustomInputButton.setValue("Set Test Input...");
		this.applyButton.setZIndex(1);
		this.testButton.setZIndex(1);
		this.editCustomInputButton.setZIndex(1);
		editCustomInputButton.setRightPosPx(8).setBottomPosPx(3).setWidthPx(105).setHeightPx(22);

		if (isReadonly) {
			this.applyButton.setDisabled(true);
			this.testButton.setDisabled(true);
			this.timeoutField.setDisabled(true);
			this.limitField.setDisabled(true);
			this.datasourceField.setDisabled(true);
			this.editCustomInputButton.setDisabled(true);
			this.usedDatamodels.setDisabled(true);
			this.keepTablesetData.setDisabled(true);
			this.amiscriptField.setDisabled(true);
			this.dynamicDatamodel.setDisabled(true);
		}
		setLimit(this.editedCallback.getLimit());
		setTimeout(this.editedCallback.getTimeoutMs());
		setIsRunning(NOT_RUNNING);
		addChild(this.formPortlet, 0, 1);
		this.setRowSize(1, 30);
		{
			this.variablesPortlet = new AmiWebAmiObjectsVariablesPortlet(generateConfig(), layoutAlias);
			this.variablesPortlet.setEnableDatamodelColumns(this.getDatamodel() != null);
			this.variablesPortlet.setListener(this);
			this.variablesPortlet.initFromCallback(this.editedCallback);
		}

		this.dividerPortlet2 = new DividerPortlet(generateConfig(), true, this.scriptPortlet, this.variablesPortlet);
		this.dividerPortlet2.setThickness(4);
		this.dividerPortlet2.setColor("#AAAAAA");
		this.dividerPortlet2.setExpandBias(0.7, 0.3);

		this.dividerPortlet = addChild(new DividerPortlet(generateConfig(), false, this.dividerPortlet2, this.outputTabsPortlet), 0, 0);
		this.dividerPortlet.setExpandBias(0.6, 0.4);
		this.dividerPortlet.setThickness(4);
		this.dividerPortlet.setColor("#AAAAAA");
		this.formPortlet.addFormPortletListener(this);
		this.formPortlet.setMenuFactory(this);
		this.formPortlet.addMenuListener(this);
		this.scriptPortlet.addFormPortletListener(this);
		this.scriptPortlet.setMenuFactory(this);
		this.scriptPortlet.addMenuListener(this);
		this.amiscriptField.setDisabled(isReadonly);
		int scaledWidth2 = (int) (width * 0.8);
		int scaledHeight2 = (int) (height * 0.8);
		this.setSuggestedSize(scaledWidth2, scaledHeight2);
		ensureCompiled();
	}
	private void rebuildAutocompleteValues() {
		rebuildAutocompleteValues(this.editedCallback.getVarNameToAriMap().entrySet());
	}

	private void rebuildAutocompleteValues(Set<Entry<String, String>> linkedVariables) {
		this.amiscriptField.clearVariables();
		amiscriptField.addVariable("tableset", Tableset.class);
		if (this.thiz != null)
			amiscriptField.setThis(thiz);
		CalcTypes paramTypesMapping = this.editedCallback.getParamsDef().getParamTypesMapping();
		for (String e : paramTypesMapping.getVarKeys())
			amiscriptField.addVariable(e, paramTypesMapping.getType(e));
		for (Entry<String, String> var : linkedVariables) {
			String varname = var.getKey();
			String ari = var.getValue();
			AmiWebDomObject variable = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
			if (variable == null)
				continue;
			Class<?> classType = variable.getDomClassType();
			amiscriptField.addVariable(varname, classType);
		}
	}

	private void rebuildCallbackTitle() {
		StringBuilder sb = new StringBuilder();
		sb.append("  ").append(this.callbackName).append('(');
		for (int i = 0; i < this.editedCallback.getParamsDef().getParamsCount(); i++) {
			if (i > 0)
				sb.append(',');
			sb.append(this.editedCallback.getParamsDef().getParamName(i));
		}
		sb.append(")  ");
		this.callbackTitle = sb.toString();
	}

	private void setLimit(int limit) {
		if (limit == AmiConsts.DEFAULT)
			this.limitField.setValue("default");
		else if (limit == -1)
			this.limitField.setValue("");
		else
			this.limitField.setValue(SH.toString(limit));

	}

	private void setTimeout(int ms) {
		if (ms == AmiConsts.DEFAULT)
			this.timeoutField.setValue("default");
		else if (ms == -1)
			this.timeoutField.setValue("");
		else {
			int t = ms;
			if (t % 1000 == 0)
				this.timeoutField.setValue(SH.toString(ms / 1000));
			else
				this.timeoutField.setValue(SH.toString(ms / 1000d));
		}
	}
	public void setOwningTab(Tab t) {
		this.owningTab = t;
		updateTabName();
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.amiscriptField) {
			if (isModified != (OH.ne(this.amiscriptField.getValue(), this.lastVal))) {
				isModified = !isModified;
				updateTabName();
			}
		} else if (field == this.usedDatamodels || field == this.datasourceField) {
			isModified = true;
		} else if (field == this.applyButton) {
			apply();
		} else if (field == this.testButton) {
			if (isRunning == RUNNING) {
				cancelTest();
			} else if (isRunning == NOT_RUNNING) {
				runTest();
			} else {
				this.debugState = AmiWebBreakpointManager.DEBUG_CONTINUE;
				this.service.getBreakpointManager().continueDebug(this.debugScriptRunner);
				//				this.debuggerTree.clear();
				//				this.debugScriptRunner.setState(AmiWebScriptRunner.STATE_RESPONSE_READY);
			}
		} else if (field == this.editCustomInputButton) {
			InputsPortlet p = new InputsPortlet(generateConfig());
			RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
			int width = MH.min(600, (int) (root.getWidth() * 0.4));
			int height = MH.min(400, (int) (root.getHeight() * 0.4));
			getManager().showDialog("Test Inputs", p, width, height);
		} else if (field == this.stepoverButton) {//add
			OH.assertTrue(isRunning == DEBUG);//step into/step over/continue should exist together, and only exist when isRunning==DEBUG
			this.debugState = AmiWebBreakpointManager.DEBUG_STEP_OVER;
			this.service.getBreakpointManager().continueDebug(this.debugScriptRunner);
		}

	}

	private void cancelTest() {
		if (this.runner != null)
			this.runner.halt();
		this.runner = null;
		setIsRunning(NOT_RUNNING);
		log(AmiWebLogViewerPortlet.TYPE_ADMIN, "User Cancelled");

	}

	public boolean submitChanges() {
		this.originalCallback.copyFrom(this.editedCallback);
		return true;
	}

	public void exportTo(AmiWebAmiScriptCallback sink, AmiWebDmsImpl dm) {
		this.editedCallback.setAmiscript(this.amiscriptField.getValue(), false);
		ensureCompiled();
		applyDefaults();
		this.lastVal = this.amiscriptField.getValue();
		this.editedCallback.setReturnSchema(this.schemaPortlet.createSchema(this.editedCallback));
		sink.copyFrom(this.editedCallback);
	}
	public boolean apply() {
		if (isReadonly)
			return true;
		clearOutput(false);
		if (!this.schemaPortlet.parseSchemas())
			return false;
		rebuildCallbackTitle();
		this.hasError = false;
		this.errorPortlet.setHtml("");
		this.editedCallback.clearVariables();
		if (!applyDefaults())
			return false;
		if (ensureCompiled() == AmiWebAmiScriptCallback.COMPILE_ERROR)
			return false;
		this.lastVal = this.amiscriptField.getValue();
		isModified = hasError;
		updateTabName();
		return !hasError;
	}

	private boolean applyDefaults() {
		try {
			String defaultDatasource = this.datasourceField.getValue();
			this.editedCallback.setTimeoutMs(getTimeout());
			this.editedCallback.setLimit(getLimit());
			this.editedCallback.setDefaultDatasource(defaultDatasource);

			AmiWebDm datamodel = this.getDatamodel();
			if (datamodel != null) {
				Set<String> sink = new HashSet<String>();
				AmiWebDmUtils.getAllUpperDm(service.getDmManager(), datamodel.getAmiLayoutFullAliasDotId(), sink);
				for (String i : this.usedDatamodels.getValue()) {
					if (this.service.getDmManager().getDmByAliasDotName(i) == null) {
						hasError = true;
						this.latestError = new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_AMISQL, null, null, "Datamodel not found: " + i, null, null);
						showError();
						return false;
					}
					if (sink.contains(i)) {
						hasError = true;
						this.latestError = new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_AMISQL, null, null,
								"Datamodel has a circular dependency: " + i, null, null);
						showError();
						return false;
					}
				}
			}
			this.editedCallback.setInputDatamodels(this.usedDatamodels.getValue());
			this.editedCallback.setKeepTablesetOnRerun(this.keepTablesetData.getBooleanValue());
			this.editedCallback.setIsDynamicDatamodel(this.dynamicDatamodel.getBooleanValue());
			if (this.editedCallback.getParamsDef().getParamTypesMapping().getType("WHERE") != null) {
				String trueConst = AmiWebDmUtils.getDefaultTrueConst(this.getManager(), CH.l(defaultDatasource));
				this.editedCallback.getParamDefaults().put("WHERE", trueConst);
			}
		} catch (Exception e) {
			getManager().showAlert("For " + this.editedCallback.getParamsDef() + ": " + e.getMessage(), e);
			return false;
		}
		return true;
	}

	public byte ensureCompiled() {
		clearOutput(false);
		if (!this.variablesPortlet.validateVariables()) {
			this.hasError = true;
			showError();
			return AmiWebAmiScriptCallback.COMPILE_ERROR;
		}
		this.editedCallback.setAmiscript(this.amiscriptField.getValue(), false);
		this.rebuildAutocompleteValues();
		byte r = this.editedCallback.ensureCompiled(this.debugManager);
		if (r == AmiWebAmiScriptCallback.COMPILE_ERROR) {
			this.hasError = true;
			showError();
		} else {
			hasError = false;
		}
		return r;
	}

	private int getLimit() {
		String val = this.limitField.getValue();
		if (SH.isnt(val))
			return -1;
		else if ("DEFAULT".equalsIgnoreCase(val))
			return AmiConsts.DEFAULT;
		int r;
		try {
			r = SH.parseInt(SH.trim(val));
		} catch (Exception e) {
			throw new RuntimeException("Limit must be a number, 'default' or blank");
		}
		if (r < -1)
			throw new RuntimeException("Limit can not be negative (except for -1): " + r);
		return r;
	}
	private int getTimeout() {
		String val = this.timeoutField.getValue();
		if (SH.isnt(val))
			return -1;
		else if ("DEFAULT".equalsIgnoreCase(val))
			return AmiConsts.DEFAULT;
		double r;
		try {
			r = SH.parseDouble(SH.trim(val));
		} catch (Exception e) {
			throw new RuntimeException("Timeout must be a number, 'default' or blank");
		}
		if (r < 0)
			throw new RuntimeException("Timeout can not be negative: " + r);
		return (int) (r * 1000);
	}

	private void updateTabName() {
		this.owningTab.setTitle(!isModified ? ("  " + callbackTitle + "  ") : ("  " + callbackTitle + "*  "));
		BasicMethodFactory methodFactory = service.getScriptManager(this.amiLayoutAlias).getMethodFactory();
		this.owningTab.setHover(this.editedCallback.getParamsDef().toString(methodFactory));
		if (SH.is(this.amiscriptField.getValue())) {
			this.owningTab.setSelectTextColor("#0000FF");
			this.owningTab.setUnselectTextColor("#0000FF");
		} else {
			this.owningTab.setSelectTextColor(null);
			this.owningTab.setUnselectTextColor(null);
		}
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		if (keycode == 13 && mask == Form.KEY_CTRL) {

			apply();
			return;
		}
		if (keycode == 13 && mask == Form.KEY_ALT) {
			onFieldValueChanged(null, this.testButton, null);
			return;
		}
		//add
		if (keycode == 82 && mask == Form.KEY_CTRL && this.isRunning == DEBUG) {//ctrl+R
			runToCursor();
			return;
		}
		if (field == this.amiscriptField) {
			if (keycode == 32 && mask == Form.KEY_CTRL) {
				Set<Entry<String, String>> variables = this.variablesPortlet.getVariableNameToAriEntrySet();
				this.rebuildAutocompleteValues(variables);
			}
			((AmiWebFormPortletAmiScriptField) field).onSpecialKeyPressed(formPortlet, field, keycode, mask, cursorPosition);
		}

	}

	public void runToCursor() {
		this.debugState = AmiWebBreakpointManager.DEBUG_STEP_TO_CURSOR;
		this.service.getBreakpointManager().continueDebug(this.debugScriptRunner);
	}

	public void runTest() {
		this.getBodyField().clearAnnotation();
		if (apply())
			test();
	}
	private void test() {
		CalcFrame vars = new BasicCalcFrame(this.editedCallback.getParamsDef().getParamTypesMapping());
		StringBuilder errorSink = new StringBuilder();
		AmiWebDm datamodel = this.getDatamodel();
		switch (this.editedCallback.getTestInputType()) {
			case AmiWebAmiScriptCallback.TEST_INPUT_VARS_DFLT:
				for (String name : this.editedCallback.getParamsDef().getParamDescriptions())
					vars.putValue(name, this.editedCallback.getParamDefaults().get(name));
				break;
			case AmiWebAmiScriptCallback.TEST_INPUT_VARS_LINK: {
				AmiWebDmLink link = this.service.getDmManager().getDmLinkByAliasDotRelationshipId(this.editedCallback.getTestInputRef());
				if (link == null) {
					getManager().showAlert("Test Input points to missing link: " + this.editedCallback.getTestInputRef() + "  (Use 'Set Test Input...' button to fix this)");
					return;
				}
				Table values = AmiWebDmUtils.getSourceValues(service, link);
				AmiWebDmRequest req = AmiWebDmUtils.buildReq(debugManager, service, link, values, errorSink, service.createStackFrame(thiz));
				if (req == null) {
					getManager().showAlert("Test Input points to invalid link: " + this.editedCallback.getTestInputRef() + "  (Use 'Set Test Input...' button to fix this)");
					return;
				}
				String defaultTrue;
				if (datamodel != null) {
					defaultTrue = AmiUtils.s(datamodel.getInputDefaults().getVariables().get(AmiWebDmsImpl.WHERE));
				} else
					defaultTrue = null;
				for (Entry<String, Object> i : req.getVariablesForOnProcess(defaultTrue).entrySet())
					vars.putValue(i.getKey(), i.getValue());

				//TODO:
				break;
			}
			case AmiWebAmiScriptCallback.TEST_INPUT_VARS_CUST: {
				for (String name : this.editedCallback.getParamsDef().getParamDescriptions()) {
					String value = this.editedCallback.getTestInputValues().get(name);
					Object value2 = service.getScriptManager(this.amiLayoutAlias).parseAndExecuteAmiScript(value, errorSink, null, debugManager, AmiDebugMessage.TYPE_TEST, thiz,
							this.callbackName);
					if (errorSink.length() > 0) {
						getManager().showAlert("Test Input invalid for paramater '" + name + "'  (Use 'Set Test Input...' button to fix this): " + errorSink);
						return;
					}
					vars.putValue(name, value2);
				}
				break;
			}
		}
		test(vars);
	}

	private CalcFrame mapTestVars(Map vars) {
		ParamsDefinition paramsDef = this.editedCallback.getParamsDef();
		CalcFrame vars2 = new BasicCalcFrame(paramsDef.getParamTypesMapping());
		AmiWebScriptManagerForLayout sm = service.getScriptManager(this.editedCallback.getAmiLayoutAlias());
		for (int n = 0; n < paramsDef.getParamsCount(); n++) {
			Class<?> paramType = paramsDef.getParamType(n);
			String name = paramsDef.getParamName(n);
			String type = sm.forType(paramType);
			Object val = vars.get(name);
			Object val2 = OH.castNoThrow(val, paramType);
			if (val2 == null && val != null && !"null".equals(val))
				log(AmiWebLogViewerPortlet.TYPE_INPUT, "[REPLACING TEST INPUT WITH INVALID TYPE TO NULL]: " + type + " " + name + "=" + val);
			else
				log(AmiWebLogViewerPortlet.TYPE_INPUT, type + " " + name + "=" + val2);
			vars2.putValue(name, val2);
		}
		return vars2;
	}

	public Object testInBlock(CalcFrameStack ei, CalcFrame var) {
		clearOutput(true);
		if (!applyDefaults())
			return null;//new FlowControlThrow(null, "COMPILE_ERROR_SETTING_DEFAULTS");
		this.usedDatasources.clear();
		if (this.datasourceField.getValue() != null)
			this.usedDatasources.add(this.datasourceField.getValue());
		this.outputTabsPortlet.selectTab(this.logViewerTab.getLocation());
		buildLineEnds();
		if (ei.getTop() instanceof AmiWebTopCalcFrameStack) {
			//			this.runner = ((AmiWebScriptExecuteInstance) ei.getOrigValues()).getRunner();
			//			setIsRunning(RUNNING);
			//			this.runner.addListener(this);
		}
		if (!this.editedCallback.hasError(false))
			return this.editedCallback.executeInBlock(ei, var);
		return null;
	}

	private void buildLineEnds() {
		this.lineEnds = SH.indexOfAll(this.editedCallback.getAmiscript(false) + '\n', '\n');
	}

	public Object test(CalcFrame vars2) {
		clearOutput(true);
		if (!applyDefaults())
			return null;//new FlowControlThrow(null, "COMPILE_ERROR_SETTING_DEFAULTS");
		this.usedDatasources.clear();
		if (this.datasourceField.getValue() != null)
			this.usedDatasources.add(this.datasourceField.getValue());
		//		BasicFrame vars2 = mapTestVars(vars);
		this.outputTabsPortlet.selectTab(this.logViewerTab.getLocation());
		buildLineEnds();
		if (!this.editedCallback.hasError(false) && this.editedCallback.hasCode()) {
			this.runner = this.editedCallback.executeReturnRunner(this.debugManager, vars2);
			if (runner != null) {
				if (this.testPreparer != null)
					this.testPreparer.prepareTest(this, this.runner);
				setIsRunning(RUNNING);
				runner.addListener(this);
				this.debugState = AmiWebBreakpointManager.DEBUG_CONTINUE;//add
				runner.runStep();
			}
		}
		return this.returnValue;
	}

	private void setIsRunning(byte status) {
		this.isRunning = status;
		if (status == RUNNING) {
			this.service.getBreakpointManager().clearHighlights();
			//			this.amiscriptField.clearHighlight();
			this.testButton.setCssStyle("_fm=bold|_fg=#FFFFFF|style.border=0px|style.borderRadius=5px|_cn=ami_cancel_query_button");
			this.testButton.setValue("Cancel");
			//			this.amiscriptField.clearHighlight();
		} else if (status == NOT_RUNNING) {
			this.service.getBreakpointManager().clearHighlights();
			//			this.amiscriptField.clearHighlight();
			RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
			int width = root.getWidth();
			int height = root.getHeight();
			this.testButton.setCssStyle("_fm=bold|_fg=#FFFFFF|style.border=0px|_cn=ami_test_query_button");
			//if status is not running, get rid of step over buttons
			if (stepoverButton != null) {
				formPortlet.removeField(stepoverButton);
				stepoverButton = null;
			}
			if (width < MIN_SCREEN_WIDTH && height < MIN_SCREEN_HEIGHT) {
				this.testButton.setValue("Test");
			} else {
				this.testButton.setValue("Test (alt+enter)");
			}

			this.debuggerTree.clear();
		} else if (status == DEBUG) {
			this.testButton.setCssStyle("_fm=bold|_fg=#FFFFFF|style.border=0px|_cn=ami_test_query_button");
			this.testButton.setValue("Continue");
			//add step into and step over, note that step into/step over/ button should always exist together
			if (stepoverButton == null) {
				stepoverButton = new FormPortletButtonField("").setValue("Step Over");
				formPortlet.addField(stepoverButton);
			}
			RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
			int width = root.getWidth();
			int height = root.getHeight();
			if (width < MIN_SCREEN_WIDTH && height < MIN_SCREEN_HEIGHT) {
				stepoverButton.setRightPosPx(250).setBottomPosPx(3).setWidthPx(50).setHeightPx(22);
				stepoverButton.setZIndex(1);

			} else {
				stepoverButton.setRightPosPx(350).setBottomPosPx(3).setWidthPx(110).setHeightPx(22);
				stepoverButton.setZIndex(1);
			}
		}

	}

	private void clearOutput(boolean includeTables) {
		this.returnValue = null;
		this.debugManager.clearMessages(AmiDebugMessage.SEVERITY_INFO);
		this.debugManager.clearMessages(AmiDebugMessage.SEVERITY_WARNING);
		this.logViewerPortlet.clear();
		if (includeTables)
			this.tableViewerPortlet.clear();
		this.errorsTab.setTitle("  Errors  ");
		this.logViewerTab.setTitle("  Logs  ");
		this.eventViewerTab.setTitle("  Event Viewer  ");
		this.debugTab.setTitle("  Debugger ");
		this.latestError = null;
		if (includeTables)
			this.tableViewerTab.setTitle("  Tables  ");
	}
	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		int count = manager.getMessages(AmiDebugMessage.SEVERITY_INFO).size() + manager.getMessages(AmiDebugMessage.SEVERITY_WARNING).size();
		if (count > 0)
			this.eventViewerTab.setTitle("  Event Viewer(" + count + ")  ");
		if (message.getException() instanceof ExpressionParserException)
			this.latestError = message;
		updateLogTitleCount();
	}

	private void showError() {
		if (latestError == null)
			return;
		Throwable exc = latestError.getException();
		ExpressionParserException epe = (ExpressionParserException) exc;
		StringBuilder before = new StringBuilder();
		String after;
		String exception;
		String text;
		if (epe != null) {
			this.amiscriptField.setCursorPosition(epe.getPosition());
			int line = SH.getLinePosition(this.amiscriptField.getValue(), epe.getPosition()).getA();
			this.amiscriptField.scrollToRow(line);
			if (OH.eq(this.amiscriptField.getValue(), epe.getExpression()) && !epe.getisRuntime()) {
				this.amiscriptField.flashRows(line, line, "red");
				this.amiscriptField.setAnnotation(line, "error", epe.getMessageRecurse());
			}

			if (epe.getExpression() == null) {
				Map<Object, Object> details = latestError.getDetails();
				String expression = (String) details.get("AmiScript");
				if (expression != null)
					epe.setExpression(expression);
			}
			before.append("Ami Script Error in ").append(latestError.getTargetAri()).append(" ").append(latestError.getTargetCallback()).append("\n\n")
					.append(epe.toLegibleStringBefore());
			exception = epe.toLegibleStringException(true);
			after = epe.toLegibleStringAfter();
			StringBuilder errorMessage = new StringBuilder();

			errorMessage.append("<div class='ami_epe_before'>").append(WebHelper.escapeHtmlNewLineToBr(before.toString())).append("</div>");
			errorMessage.append("<div class='ami_epe_exception'>").append(WebHelper.escapeHtmlNewLineToBr(exception)).append("</div>");
			errorMessage.append("<div class='ami_epe_after'>").append(WebHelper.escapeHtmlNewLineToBr(after)).append("</div>");

			text = errorMessage.toString();
		} else
			text = latestError.getMessage();

		this.hasError = true;
		this.errorPortlet.setHtml(text);
		this.errorPortlet.setCssStyle("_fg=#880000|_bg=#FFFFFF|_fm=left|_fm=monospace" + (SH.is(text) ? "|style.border=1px solid #880000" : "|style.border=4px blue none"));
		this.outputTabsPortlet.selectTab(this.errorsTab.getLocation());
		this.errorsTab.setTitle("  Errors(1)  ");
	}

	private void updateLogTitleCount() {
		int count2 = this.logViewerPortlet.getLogsCount();
		if (count2 > 0) {
			this.logViewerTab.setTitle("  Logs(" + count2 + ")  ");
		}
	}
	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (cursorPosition != -1)
			this.amiscriptField.resetAutoCompletion();
		return null;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.amiscriptField)
			AmiWebMenuUtils.processContextMenuAction(service, action, (FormPortletTextEditField) node);
	}
	public void setAmiScript(String text) {
		if (text == null)
			text = "";
		this.isModified = true;
		this.amiscriptField.setValue(text);
	}
	public String getAmiScript() {
		return this.amiscriptField.getValue();
	}
	public void setThis(AmiWebDomObject o) {
		thiz = o;
		if (o != null)
			amiscriptField.setThis(o);
		else
			amiscriptField.removeVariable("this");
	}
	@Override
	public void onScriptRunStateChanged(AmiWebScriptRunner amiWebScriptRunner, byte oldState, byte state) {
		if (amiWebScriptRunner != this.runner)
			return;
		if (state == AmiWebScriptRunner.STATE_DONE) {
			this.tableset = amiWebScriptRunner.getVars().getTableset();
			try {
				this.schemaPortlet.updateSchema(new AmiWebDmTablesetSchema(this.service, this.editedCallback, tableset));
			} catch (Exception e) {
				setIsRunning(NOT_RUNNING);
				log(AmiWebLogViewerPortlet.TYPE_EXCEPTION, AmiUtils.s(e));
				this.debugManager.addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_AMISQL, null, null, "Invalid Output Schema", null, e));
			}
			if (!this.runner.isHalted()) {
				this.returnValue = this.runner.getReturnValue();
				log(AmiWebLogViewerPortlet.TYPE_RETURN, AmiUtils.s(this.returnValue));
			}
			setIsRunning(NOT_RUNNING);
			highlightErrors();
			this.returnValue = this.runner.getFlowControlThrow();
			this.runner = null;
		} else if (state == AmiWebScriptRunner.STATE_ERROR) {
			log(AmiWebLogViewerPortlet.TYPE_EXCEPTION, AmiUtils.s(this.runner.getThrown()));
			setIsRunning(NOT_RUNNING);
			highlightErrors();
			this.returnValue = this.runner.getFlowControlThrow();
			this.runner = null;
		} else if (state == AmiWebScriptRunner.STATE_DEBUG) {
			setIsRunning(DEBUG);
			this.debugScriptRunner = amiWebScriptRunner;
			DebugPause pause = (DebugPause) this.debugScriptRunner.getStep();
			this.returnValue = pause;
			PauseStack stack = pause.getStack();
			BasicMethodFactory mf = service.getScriptManager(this.getAmiLayoutAlias()).getMethodFactory();
			this.debuggerTree.setStack(stack, mf, this.getAmiScript());
		} else if (state == AmiWebScriptRunner.STATE_REQUEST_SENT) {
			this.onFlowControlPauseStart(amiWebScriptRunner, amiWebScriptRunner.getStep());
		}
	}

	private void highlightErrors() {
		FlowControlThrow ftc = this.runner.getFlowControlThrow();
		if (ftc != null) {
			final DerivedCellCalculator pos = ftc.getPosition();
			if (pos != null)
				this.amiscriptField.setCursorPosition(pos.getPosition());
			final int line = SH.getLinePosition(this.amiscriptField.getValue(), ftc.getTailFrame().getPosition().getPosition()).getA();
			this.amiscriptField.scrollToRow(line);
			AmiWebUtils.toHtml(service, ftc, this.errorPortlet);
			String s = AmiWebUtils.toHtml(service, ftc, null);
			this.amiscriptField.setAnnotation(line, "warning", s);
			this.hasError = true;
			this.outputTabsPortlet.setActiveTab(this.errorPortlet);
			showError();
		}
	}
	public void showDebug(AmiWebScriptRunner runner) {
	}
	private void log(byte type, String message) {
		this.logViewerPortlet.log(type, message);
		updateLogTitleCount();

	}

	public String getName() {
		return callbackName;
	}
	public AmiWebFormPortletAmiScriptField getBodyField() {
		return amiscriptField;
	}
	public boolean isModified() {
		return isModified;
	}
	public String getOrigVal() {
		return origVal;
	}

	public class InputsPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuListener, FormPortletContextMenuFactory {

		private FormPortlet form;
		private FormPortletSelectField<Byte> typeField;
		private FormPortletSelectField<String> relationshipsField;
		private List<FormPortletTextField> customFields = new ArrayList<FormPortletTextField>();
		private List<FormPortletTextField> defaultFields = new ArrayList<FormPortletTextField>();
		private FormPortletButton cancelButton;
		private FormPortletButton submitButton;

		public InputsPortlet(PortletConfig config) {
			super(config);
			this.form = new FormPortlet(generateConfig());
			this.addChild(form);
			this.typeField = this.form.addField(new FormPortletSelectField<Byte>(Byte.class, "Test Input: "));
			this.typeField.addOption(AmiWebAmiScriptCallback.TEST_INPUT_VARS_CUST, "Custom");
			this.typeField.addOption(AmiWebAmiScriptCallback.TEST_INPUT_VARS_DFLT, "Default");
			if (!availableTestingRelationships.isEmpty())
				this.typeField.addOption(AmiWebAmiScriptCallback.TEST_INPUT_VARS_LINK, "Relationship");
			this.relationshipsField = new FormPortletSelectField<String>(String.class, "Relationship: ");
			for (String s : availableTestingRelationships)
				this.relationshipsField.addOption(s, s);

			this.typeField.setValueNoThrow(editedCallback.getTestInputType());
			this.relationshipsField.setValueNoThrow(editedCallback.getTestInputRef());
			ParamsDefinition def = editedCallback.getParamsDef();
			for (int i = 0; i < def.getParamsCount(); i++) {
				String name = def.getParamName(i);
				Class<?> type = def.getParamType(i);
				String typeName = service.getMethodFactory().forType(type);
				FormPortletTextField field = new FormPortletTextField(typeName + " " + name + "=");
				field.setWidth(FormPortletTextField.WIDTH_STRETCH);
				this.customFields.add(field);
				field.setHasButton(true);
				field.setValue(editedCallback.getTestInputValues().get(name));
				field.setCorrelationData(name);
				FormPortletTextField field2 = new FormPortletTextField(typeName + " " + name + "=");
				field2.setDisabled(true);
				field2.setValue(OH.noNull(editedCallback.getParamDefaults().get(name), "null"));
				field2.setWidth(FormPortletTextField.WIDTH_STRETCH);
				this.defaultFields.add(field2);
			}
			this.form.addFormPortletListener(this);
			this.form.addMenuListener(this);
			this.form.setMenuFactory(this);
			this.submitButton = this.form.addButton(new FormPortletButton("Submit"));
			this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
			updateForm();
		}

		private void updateForm() {
			this.form.removeFieldNoThrow(this.relationshipsField);
			for (FormPortletTextField i : this.customFields)
				this.form.removeFieldNoThrow(i);
			for (FormPortletTextField i : this.defaultFields)
				this.form.removeFieldNoThrow(i);
			switch (this.typeField.getValue()) {
				case AmiWebAmiScriptCallback.TEST_INPUT_VARS_LINK:
					this.form.addField(this.relationshipsField);
					break;
				case AmiWebAmiScriptCallback.TEST_INPUT_VARS_CUST:
					for (FormPortletTextField i : this.customFields)
						this.form.addField(i);
					break;
				case AmiWebAmiScriptCallback.TEST_INPUT_VARS_DFLT:
					for (FormPortletTextField i : this.defaultFields)
						this.form.addField(i);
					break;
			}
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (this.cancelButton == button)
				close();
			else {
				editedCallback.setTestInputType(this.typeField.getValue());
				switch (this.typeField.getValue()) {
					case AmiWebAmiScriptCallback.TEST_INPUT_VARS_LINK:
						editedCallback.setTestInputRef(this.relationshipsField.getValue());
						break;
					case AmiWebAmiScriptCallback.TEST_INPUT_VARS_CUST:
						Map<String, String> testInputValues = new HashMap<String, String>();
						for (FormPortletTextField i : this.customFields)
							testInputValues.put((String) i.getCorrelationData(), i.getValue());
						editedCallback.setTestInputValues(testInputValues);
						break;
				}
				close();
			}
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
			if (field == this.typeField) {
				updateForm();
			}
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

		@Override
		public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
			AmiWebMenuUtils.processContextMenuAction(service, action, node);
		}

		@Override
		public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
			BasicWebMenu r = new BasicWebMenu();
			AmiWebMenuUtils.createOperatorsMenu(r, service, getAmiLayoutAlias());
			AmiWebMenuUtils.createMemberMethodMenu(r, service, getAmiLayoutAlias());
			return r;
		}

	}

	@Override
	public void onSchemeUpdated(AmiWebEditSchemaPortlet amiWebEditSchemaPortlet) {
		if (this.schemaPortlet.getTablesCount() == 0)
			this.schemaTab.setTitle("  Schema  ");
		else
			this.schemaTab.setTitle("  Schema(" + this.schemaPortlet.getTablesCount() + ")  ");
		if (this.tableset == null || this.tableset.getTableNames().size() == 0)
			this.tableViewerTab.setTitle("  Tables  ");
		else
			this.tableViewerTab.setTitle("  Tables(" + tableset.getTableNames().size() + ")  ");
		if (tableset != null && CH.isntEmpty(tableset.getTableNames())) {
			this.outputTabsPortlet.selectTab(this.tableViewerTab.getLocation());
			for (String i : tableset.getTableNamesSorted())
				this.tableViewerPortlet.addTable(tableset.getTable(i));
		}
	}

	public boolean applyTo(AmiWebAmiScriptCallback sink, AmiWebDmsImpl dm) {
		if (!this.apply())
			return false;
		this.editedCallback.setReturnSchema(this.schemaPortlet.createSchema(this.editedCallback));
		sink.copyFrom(this.editedCallback);
		return true;
	}
	public boolean loadFrom(AmiWebAmiScriptCallback source) {
		this.hasError = false;
		this.errorPortlet.setHtml("");
		String val = source.getAmiscript(false);
		this.lastVal = val;
		source.ensureCompiled(this.debugManager);
		this.amiscriptField.setValue(val);
		setTimeout(source.getTimeoutMs());
		setLimit(source.getLimit());
		this.datasourceField.setValue(source.getDefaultDatasource());
		this.usedDatamodels.setValueNoThrow(source.getInputDatamodels());
		this.variablesPortlet.initFromCallback(source);
		this.keepTablesetData.setValue(source.getKeepTablesetOnRerun());
		this.dynamicDatamodel.setValue(source.getIsDynamicDatamodel());

		this.isModified = this.hasError;
		updateTabName();
		return true;
	}

	private void onFlowControlPauseStart(AmiWebScriptRunner amiWebScriptRunner, FlowControlPause fcp) {
		if (fcp instanceof AmiFlowControlPauseSql) {
			AmiFlowControlPauseSql i = (AmiFlowControlPauseSql) fcp;
			if (i.getDatasourceName() != null)
				this.usedDatasources.add(i.getDatasourceName());
		}

	}

	public Set<String> getUsedDatasources() {
		return usedDatasources;
	}

	public void setRestrictedDatamodels(Set<String> sink) {
		this.restrictedDatamodels = new HashSet<String>(sink);
		for (String s : sink)
			if (AmiWebUtils.isParentAliasOrSame(this.amiLayoutAlias, s))
				this.usedDatamodels.removeOption(s);
	}

	@Override
	public Set<String> getUsedVariableNames(Set<String> sink) {
		return this.editedCallback.getUsedVariableNames(sink);
	}
	@Override
	public String getNextVariableName(String suggestedName) {
		return this.editedCallback.getNextVariableName(this.amiscriptField.getNextVariableName(suggestedName));
	}
	@Override
	public void onVariableAdded(String variableName, String ari) {
		AmiWebDomObject variable = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
		if (variable != null) {
			Class<?> classType = variable.getDomClassType();
			this.amiscriptField.addVariable(variableName, classType);
			this.editedCallback.addVariable(variableName, ari);
			this.editedCallback.linkVariable(variableName, variable);
		}
	}

	@Override
	public void onVariableRemoved(String variableName, String ari) {
		this.amiscriptField.removeVariable(variableName);
		this.editedCallback.unlinkVariable(variableName);
		this.editedCallback.removeVariable(variableName);

	}

	@Override
	public void onVariableUpdated(String oldVariableName, String newVariableName, String ari) {
		AmiWebDomObject variable = AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(ari, this.service);
		if (!SH.equals(oldVariableName, newVariableName)) {
			this.amiscriptField.removeVariable(oldVariableName);
			this.editedCallback.unlinkVariable(oldVariableName);
			this.editedCallback.removeVariable(oldVariableName);
			Class<?> classType = variable.getDomClassType();
			this.amiscriptField.addVariable(newVariableName, classType);
			this.editedCallback.addVariable(newVariableName, variable.getAri());
			this.editedCallback.linkVariable(newVariableName, variable);
		}

	}
	/*
	@Override
	public void onVariableAdded(String variableName, AmiWebDomObject variable) {
		Class<?> classType = variable.getDomClassType();
		this.amiscriptField.addVariable(variableName, classType);
		this.editedCallback.addVariable(variableName, variable.getAri());
		this.editedCallback.linkVariable(variableName, variable);
	
	}
	
	@Override
	public void onVariableRemoved(String variableName, AmiWebDomObject variable) {
		this.amiscriptField.removeVariable(variableName);
		this.editedCallback.unlinkVariable(variableName);
		this.editedCallback.removeVariable(variableName);
	
	}
	
	@Override
	public void onVariableUpdated(String oldVariableName, String newVariableName, AmiWebDomObject variable) {
		if (!SH.equals(oldVariableName, newVariableName)) {
			this.amiscriptField.removeVariable(oldVariableName);
			this.editedCallback.unlinkVariable(oldVariableName);
			this.editedCallback.removeVariable(oldVariableName);
			Class<?> classType = variable.getDomClassType();
			this.amiscriptField.addVariable(newVariableName, classType);
			this.editedCallback.addVariable(newVariableName, variable.getAri());
			this.editedCallback.linkVariable(newVariableName, variable);
		}
	}
	*/
	@Override
	public void onVariableUpdateOption(String variableName, String optionType, Object value) {
		if (SH.equals(AmiObjectVariable.OPTION_FORMAT, optionType)) {
			this.editedCallback.setCustomFormat(variableName, (Integer) value);
		} else if (SH.equals(AmiObjectVariable.OPTION_DOM_EVENT, optionType)) {
			this.editedCallback.setDomEvent(variableName, (Byte) value);
		}

	}
	public AmiWebDm getDatamodel() {
		return this.editedCallback.getReturnSchema().getDatamodel();
	}

	@Override
	public void onClosed() {
		super.onClosed();
		this.editedCallback.close();
	}
	public AmiWebAmiScriptCallback getCallback() {
		return this.editedCallback;
	}

	public boolean hasChanged() {
		if (OH.ne(this.getBodyField().getValue(), this.getOrigVal()))
			return true;
		if (OH.ne(this.editedCallback.getVariables(), this.originalCallback.getVariables()))
			return true;
		if (OH.ne(this.editedCallback.getAriToDomEvent(), this.originalCallback.getAriToDomEvent()))
			return true;
		if (OH.ne(this.editedCallback.getAriToCustomFormat(), this.originalCallback.getAriToCustomFormat()))
			return true;
		if (OH.ne(this.getTimeout(), this.originalCallback.getTimeoutMs()))
			return true;
		if (OH.ne(this.getLimit(), this.originalCallback.getLimit()))
			return true;
		if (OH.ne(this.datasourceField.getValue(), this.originalCallback.getDefaultDatasource()))
			return true;
		if (OH.ne(this.usedDatamodels.getValue(), this.originalCallback.getInputDatamodels()))
			return true;
		return false;
	}

	public String getAmiLayoutAlias() {
		return amiLayoutAlias;
	}

	public void setAmiLayoutAlias(String amiLayoutAlias) {
		this.amiLayoutAlias = amiLayoutAlias;
		this.variablesPortlet.setBaseAlias(amiLayoutAlias);
		this.amiscriptField.setAmiLayoutFullAlias(amiLayoutAlias);
	}

	public void addVariablesPortletToDomManager() {
		this.service.getDomObjectsManager().addGlobalListener(this.variablesPortlet);
	}
	public void removeVariablesPortletFromDomManager() {
		this.service.getDomObjectsManager().removeGlobalListener(this.variablesPortlet);
	}

	public boolean isBreakpoint(DerivedCellCalculator statment) {
		if (this.lineEnds == null)
			buildLineEnds();
		int line = AH.indexOfSortedGreaterThanEqualTo(statment.getPosition(), this.lineEnds);
		if (this.debugState == AmiWebBreakpointManager.DEBUG_CONTINUE) { //if debugstate==continue
			if (this.amiscriptField.getBreakpoints().contains(line)) {
				this.amiscriptField.highlightRow(line);
				PortletHelper.ensureVisible(this);
				return true;
			} else {
				return false;
			}
		} else if (this.debugState == AmiWebBreakpointManager.DEBUG_STEP_OVER) {//if debugstate== step over
			this.amiscriptField.highlightRow(line);
			PortletHelper.ensureVisible(this);
			return true;
		} else if (this.debugState == AmiWebBreakpointManager.DEBUG_STEP_TO_CURSOR) {
			int curPos = this.amiscriptField.getCursorPosition();
			int curPosLine = AH.indexOfSortedGreaterThanEqualTo(curPos, this.lineEnds);
			if (line == curPosLine) {
				this.amiscriptField.highlightRow(line);
				PortletHelper.ensureVisible(this);
				return true;
			} else if (this.amiscriptField.getBreakpoints().contains(line)) {
				this.amiscriptField.highlightRow(line);
				PortletHelper.ensureVisible(this);
				return true;
			}
			return false;
		} else
			return false;
	}

	public class TestPause extends FlowControlPause {

		public TestPause(DerivedCellCalculator position) {
			super(position);
		}

		@Override
		public Object resume() {
			return super.resume();
		}

	}

	public AmiWebScriptRunner getRunner() {
		return this.runner;
	}

	@Override
	public void clearHighlights() {
		this.amiscriptField.clearHighlight();
	}

	@Override
	public String toString() {
		return super.toString() + " (" + this.callbackName + ")";
	}

	public boolean hasError() {
		return this.hasError;
	}

	public String getDatasource() {
		return this.datasourceField.getValue();
	}

	public void setTestPreparer(AmiWebEditAmiscriptTestPreparer testPreparer) {
		this.testPreparer = testPreparer;
	}

	public void recompileAmiscript() {
		this.editedCallback.recompileAmiscript();
	}

	public void setCursorPosition(int cursorPosition) {
		this.amiscriptField.setCursorPosition(cursorPosition);
		int line = SH.getLinePosition(this.amiscriptField.getValue(), cursorPosition).getA();
		this.amiscriptField.scrollToRow(line);
		this.amiscriptField.flashRows(line, line, "yellow");
	}

	public void setDefaultDatasource(String dd) {
		this.datasourceField.setValueNoThrow(dd);
	}

	public void setInputDatamodels(Set<String> names) {
		this.usedDatamodels.setValueNoThrow(names);
	}

}
