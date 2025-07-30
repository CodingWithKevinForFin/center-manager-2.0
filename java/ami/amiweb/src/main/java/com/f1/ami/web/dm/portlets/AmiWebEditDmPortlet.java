package com.f1.ami.web.dm.portlets;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebEditAmiScriptCallbackPortlet;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebEditAmiscriptTestPreparer;
import com.f1.ami.web.AmiWebManagers;
import com.f1.ami.web.AmiWebRealtimeObjectManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSystemObjectsListener;
import com.f1.ami.web.AmiWebSystemObjectsManager;
import com.f1.ami.web.AmiWebTableSchemaWrapper;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmRealtimeEvent;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.Table;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.columnar.ReadonlyTable;

final public class AmiWebEditDmPortlet extends GridPortlet implements FormPortletListener, AmiWebEditAmiscriptTestPreparer, AmiWebSystemObjectsListener {

	final private FormPortlet settings;
	final private AmiWebEditAmiScriptCallbacksPortlet editor;
	final private AmiWebService service;
	final private FormPortletSelectField<String> aliasField;
	final private FormPortletTextField nameField;
	private String baseAlias;
	final private FormPortletSelectField<Byte> queryOnModeField;
	final private FormPortletNumericRangeField conflateQuery;
	final private FormPortletNumericRangeField autoRequery;
	final private FormPortletButtonField crontabField;
	//	final private FormPortletButtonField inputsField;
	//	private Linkedcom.f1.utils.BasicTypes inputTypes = new Linkedcom.f1.utils.BasicTypes();
	private LinkedHashSet<String> linkVars = new LinkedHashSet<String>();
	private FormPortletMultiCheckboxField<String> usedRtSources;
	private String crontabTimezone;
	private String crontab;

	public AmiWebEditDmPortlet(PortletConfig config, String baseAlias) {
		super(config);
		this.baseAlias = baseAlias;
		this.service = AmiWebUtils.getService(getManager());
		this.settings = new FormPortlet(generateConfig());
		this.editor = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), null);

		this.aliasField = this.settings.addField(new FormPortletSelectField<String>(String.class, "Layout: "));
		this.settings.getFormPortletStyle().setScrollBarWidth(6);
		this.nameField = this.settings.addField(new FormPortletTextField("Name: "));
		this.nameField.setHelp("The name of this datamodel");
		this.aliasField.setHelp("The Layout that owns this datamodel");
		this.aliasField.setTopPosPx(4).setLeftPosPx(55).setWidthPx(60).setHeightPx(20);
		this.nameField.setTopPosPx(4).setLeftPosPx(165).setWidthPx(110).setHeightPx(20);
		for (String i : this.service.getLayoutFilesManager().getAvailableAliasesDown(baseAlias))
			aliasField.addOption(i, AmiWebUtils.formatLayoutAlias(i));
		aliasField.setValue(baseAlias);
		FormPortletMultiCheckboxField<String> formPortletMultiCheckboxField = new FormPortletMultiCheckboxField<String>(String.class, "Subscribe: ");
		formPortletMultiCheckboxField.setCssStyle("_cna=maxWidth");
		this.usedRtSources = this.settings.addField(formPortletMultiCheckboxField);
		this.usedRtSources.setHelp("The Realtime Panels/Feeds/Processors that this datamodel subscribes to");
		this.usedRtSources.setTopPosPx(4).setLeftPosPx(350).setWidthPx(120).setHeightPx(20);
		Set<String> processors = this.service.getWebManagers().getAllTableTypes(this.aliasField.getValue());
		for (String key : processors)
			this.usedRtSources.addOption(key, key);
		service.getSystemObjectsManager().addListener(this);
		addChild(this.settings, 0, 1);
		setRowSize(1, 30);
		addChild(this.editor, 0, 0);
		this.settings.addFormPortletListener(this);
		this.queryOnModeField = this.getSettingsForm().addField(new FormPortletSelectField<Byte>(Byte.class, "Auto-Run: "));
		this.queryOnModeField//
				.addOption(AmiWebDmsImpl.QUERY_ON_NONE, "Off")//
				.addOption(AmiWebDmsImpl.QUERY_ON_VISIBLE_ONCE, "On Visible (First time)")//
				.addOption(AmiWebDmsImpl.QUERY_ON_VISIBLE, "On Visible (Each time)")//
				.addOption(AmiWebDmsImpl.QUERY_ON_STARTUP, "On Startup")//
				.addOption(AmiWebDmsImpl.QUERY_ON_STARTUP_AND_VISIBLE, "On Startup And Visible (Each time)");
		this.queryOnModeField.setHelp("<u>Controls when the datamodel is run</u>" //
				+ "<br><B>Off</B> - do no run automatically" //
				+ "<br><B>On Visible (First time)</B> - The first time any panel on this datamodel becomes visible" //
				+ "<br><B>On Visible (Each time)</B> - Any time a panel on this datamodel becomes visible"//
				+ "<br><B>On Startup</B> - When the dashboard is loaded (ex, user logs in)"//
				+ "<br><B>On Startup and Visible (Each Time)</B> - When the dashboard is loaded and any time a panel on this datamodel becomes visible");
		this.conflateQuery = this.getSettingsForm().addField(new FormPortletNumericRangeField("Conflate Run (sec): "));
		this.conflateQuery
				.setHelp("The most frequently that this datamodel will run, in seconds. <BR><i>This prevents rapid, successive user actions from<br> causing lots of invocations");
		this.autoRequery = this.getSettingsForm().addField(new FormPortletNumericRangeField("Auto Rerun (sec): "));
		//		this.inputsField = this.getSettingsForm().addField(new FormPortletButtonField(""));
		this.autoRequery.setHelp("How often this datamodel should automatically rerun, in seconds. <BR><i>This applies after the first run of the datamodel");
		this.conflateQuery.setSliderHidden(true).setNullable(true).setRange(1, 3600).setDefaultValue(5.0);
		this.autoRequery.setSliderHidden(true).setNullable(true).setRange(.1, 3600).setDecimals(1).setDefaultValue(5.0);
		this.queryOnModeField.setValue(AmiWebDmsImpl.QUERY_ON_STARTUP);
		this.queryOnModeField.setTopPosPx(4).setLeftPosPx(550).setWidthPx(120).setHeightPx(20);
		this.conflateQuery.setTopPosPx(4).setLeftPosPx(805).setWidthPx(35).setHeightPx(20);
		this.autoRequery.setTopPosPx(4).setLeftPosPx(970).setWidthPx(35).setHeightPx(20);
		this.crontabField = getSettingsForm().addField(new FormPortletButtonField("").setValue("timers (0)"));
		this.crontabField.setTopPosPx(4).setLeftPosPx(1050).setWidthPx(80).setHeightPx(20);
		//		this.inputsField.setTopPosPx(4).setRightPosPx(8).setWidthPx(105).setHeightPx(20);
		//		this.inputsField.setValue("OnProcess Args");
	}
	public AmiWebDmsImpl createEmptyDm() {
		AmiWebDmsImpl r = new AmiWebDmsImpl(this.getService().getDmManager(), this.getBaseAlias(), null);
		return r;
	}

	FormPortlet getSettingsForm() {
		return this.settings;
	}

	public void setDatamodel(AmiWebDmsImpl datamodel, boolean isCopy) {
		this.editor.setCallbacks(datamodel.getCallbacks());
		this.baseAlias = this.editor.getAmiLayoutAlias();
		//		this.inputTypes.clear();
		//		this.inputTypes.putAll(datamodel.getOnProcessParamTypes());
		List<AmiWebDmLink> dmLinksToDmAliasDotName = this.getService().getDmManager().getDmLinksToDmAliasDotName(datamodel.getAmiLayoutFullAliasDotId());
		this.linkVars.clear();
		linkVars.add("WHERE");
		for (AmiWebDmLink link : dmLinksToDmAliasDotName)
			for (String name : link.getWhereClauseVarNames())
				linkVars.add(name);
		Set<String> sink = new HashSet<String>();
		if (!isCopy)
			AmiWebDmUtils.getAllUpperDm(service.getDmManager(), datamodel.getAmiLayoutFullAliasDotId(), sink);
		this.editor.setRestrictedDatamodels(sink);
		AmiWebEditAmiScriptCallbackPortlet callbackEditor = this.editor.getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS);
		callbackEditor.getUsedDatasources().addAll(datamodel.getUsedDatasources());
		this.nameField.setValue(datamodel.getDmName());
		this.aliasField.setValueNoThrow(datamodel.getAmiLayoutFullAlias());
		if (datamodel.getMinRequeryMs() == 0)
			this.conflateQuery.setValue(null);
		else
			this.conflateQuery.setValue(datamodel.getMinRequeryMs() / 1000d);
		if (datamodel.getMaxRequeryMs() == 0)
			this.autoRequery.setValue(null);
		else
			this.autoRequery.setValue(datamodel.getMaxRequeryMs() / 1000d);
		this.queryOnModeField.setValue(datamodel.getQueryOnMode());
		this.usedRtSources.setValueNoThrow(datamodel.getRtSources());
		this.editor.getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).setTestPreparer(this);
		List<AmiWebDmLink> links = this.service.getDmManager().getDmLinksToDmAliasDotName(datamodel.getAmiLayoutFullAliasDotId());
		Set<String> linkids = new HashSet<String>(links.size());
		for (AmiWebDmLink i : links)
			linkids.add(i.getAmiLayoutFullAliasDotId());
		callbackEditor.setAvailableTestingRelationships(linkids);
		this.getEditor().setActiveTab(AmiWebDmsImpl.CALLBACK_ONPROCESS);
		this.setCrontab(datamodel.getCrontab());
		this.setCrontabTimezone(datamodel.getCrontabTimezone());
	}

	public String getDmName() {
		return this.nameField.getValue();
	}

	public String getDmAlias() {
		return this.aliasField.getValue();
	}

	public boolean applyToDatamodel(AmiWebDmsImpl sink) {
		if (!this.editor.applyTo(sink.getCallbacks(), sink))
			return false;
		this.editor.removeVariableTreeFromDomManager();
		this.editor.addVariableTreeToDomManager();
		if (this.conflateQuery.getValue() == null)
			sink.setMinRequeryMs(0);
		else
			sink.setMinRequeryMs((int) (this.conflateQuery.getValue() * 1000d));
		if (this.autoRequery.getValue() == null)
			sink.setMaxRequeryMs(0);
		else
			sink.setMaxRequeryMs((int) (this.autoRequery.getValue() * 1000d));
		sink.setQueryOnMode(this.queryOnModeField.getValue());
		sink.setRtSources(Collections.EMPTY_SET);
		sink.setRtSources(this.usedRtSources.getValue());//force resnapshot
		sink.setUsedDatasources(this.editor.getUsedDatasources());
		return true;
	}
	public boolean revertChanges(AmiWebDmsImpl sink) {
		String amiLayoutFullAlias = sink.getAmiLayoutFullAlias();
		this.editor.setAmiLayoutAlias(amiLayoutFullAlias);
		return true;
	}
	public String getAliasDotName() {
		return AmiWebUtils.getFullAlias(this.aliasField.getValue(), this.nameField.getValue());
	}

	public boolean getIsTestRun() {
		return !this.editor.hasPendingChanges();
	}

	//should use runTestOnPendingPortlets() instead
	public void runTest() {
		this.editor.getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).runTest();
	}

	//should runTest on the cb panels that are pending, iff AmiWebAddPanelPortlet::"no_test".equals(cb)
	public void runTestOnPendingPortlets() {
		for (AmiWebEditAmiScriptCallbackPortlet i : this.editor.getPortletsWithPendingChanges())
			i.runTest();
	}

	public AmiWebService getService() {
		return service;
	}
	public String getBaseAlias() {
		return baseAlias;
	}
	public AmiWebEditAmiScriptCallbacksPortlet getEditor() {
		return editor;
	}

	public void initDatasources(AmiWebDmsImpl tmpDm, List<AmiDatasourceTable> selected, List<AmiWebDatasourceWrapper> datasources, List<? extends AmiWebDm> dms,
			List<String> usedRtSources) {
		boolean multiDs = datasources.size() > 1;
		this.nameField.setValue(tmpDm.getDmName());
		this.usedRtSources.setValue(usedRtSources == null ? null : new HashSet<String>(usedRtSources));

		Set<String> datasourceNames = new HashSet<String>();
		for (AmiWebDatasourceWrapper i : datasources) {
			datasourceNames.add(i.getName());
		}

		if (!datasources.isEmpty())
			this.getEditor().getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).setDefaultDatasource(datasources.get(0).getName());
		else
			this.getEditor().getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).setDefaultDatasource(null);
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		Set<String> usedTableNames = new HashSet<String>();
		if (dms != null) {
			Set<String> names = new HashSet<String>();
			for (AmiWebDm i : dms) {
				names.add(i.getAmiLayoutFullAliasDotId());
				usedTableNames.addAll(i.getResponseOutSchema().getTableNamesSorted());
			}

			this.getEditor().getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).setInputDatamodels(names);
		}
		if (CH.isntEmpty(usedRtSources)) {
			sb.append("  for(RealtimeEvent event=rtevents;event!=null;event=event.getNext()){\n");
			sb.append("    //TODO: handle realtime event.\n");
			sb.append("  }\n");
		} else if (CH.isEmpty(usedTableNames) && CH.isEmpty(selected)) {
			sb.append("  CREATE TABLE Sample(Symbol String, Quantity Long);\n");
			sb.append("  INSERT INTO Sample Values(\"MSFT\",100),(\"IBM\",200),(\"AAPL\",400);\n");
		}
		for (String tableName : usedTableNames) {
			tableName = AmiWebUtils.toValidVarname(tableName);
			sb.append("  CREATE TABLE ").append(tableName).append(" AS").append(" SELECT * FROM ").append(tableName).append(" WHERE ${WHERE};\n");
		}
		for (AmiDatasourceTable table : selected) {
			String tableName;
			if (table.getCreateTableClause() == null)
				tableName = AmiUtils.toValidVarName(OH.noNull(table.getName(), "t"));
			else
				tableName = table.getCreateTableClause();
			tableName = SH.getNextId(tableName, usedTableNames);
			usedTableNames.add(tableName);
			String customUse = table.getCustomUse();
			String use;
			if (multiDs && customUse != null)
				use = "ds=" + SH.doubleQuote(table.getDatasourceName()) + " " + customUse;
			else if (multiDs)
				use = "ds=" + SH.doubleQuote(table.getDatasourceName());
			else if (customUse != null)
				use = customUse;
			else
				use = null;
			if (AmiWebConsts.DSLAYOUT_NAME.equals(table.getDatasourceName())) {
				String escapeVarName = AmiUtils.escapeVarName(table.getName());
				sb.append("  // Subscription will automatically create and maintain the readonly table: ").append(escapeVarName).append("\n");
				sb.append("  // Example Usage: CREATE TABLE MySummary AS SELECT count(*) from ").append(escapeVarName).append(";\n");
				sb.append("  \n");
			} else {
				sb.append("  CREATE TABLE ").append(tableName).append(" AS");
				if (use != null)
					sb.append(" USE ").append(use);
				sb.append(" EXECUTE ").append(table.getCustomQuery()).append(";\n");
			}
		}
		sb.append("}\n");
		this.editor.getCallbackEditor(AmiWebDmsImpl.CALLBACK_ONPROCESS).setTestPreparer(this);
		this.getEditor().setCallback(AmiWebDmsImpl.CALLBACK_ONPROCESS, sb.toString());
		if (CH.isntEmpty(dms))
			this.queryOnModeField.setValue(AmiWebDmsImpl.QUERY_ON_NONE);
		else
			this.queryOnModeField.setValue(AmiWebDmsImpl.QUERY_ON_STARTUP);
		this.getEditor().setActiveTab(AmiWebDmsImpl.CALLBACK_ONPROCESS);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		// TODO Auto-generated method stub

	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.aliasField) {
			String layoutAlias = this.aliasField.getValue();
			this.baseAlias = layoutAlias;
			this.editor.setAmiLayoutAlias(layoutAlias);
			this.usedRtSources.clear();
			Set<String> processors = this.service.getWebManagers().getAllTableTypes(this.aliasField.getValue());
			for (String key : processors)
				this.usedRtSources.addOption(key, key);
		} else if (field == this.crontabField) {
			getManager().showDialog("Datamodel Timers", new AmiWebCrontabPortlet(generateConfig(), this), 600, 700);
		}
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public void prepareTest(AmiWebEditAmiScriptCallbackPortlet cbe, AmiWebScriptRunner runner) {
		AmiWebDmRealtimeEvent events = ((AmiWebDmsImpl) cbe.getDatamodel()).drainRealtimeEvent();
		runner.getWriteableVars().putValue(AmiWebDmsImpl.RTEVENTS, events);
		LinkedHashSet<String> rtSources = this.usedRtSources.getValue();
		Set<String> justSubscribed = new HashSet<String>();
		for (String s : rtSources) {
			AmiWebRealtimeObjectManager rtTypes = this.getService().getWebManagers().getAmiObjectsByType(s);
			if (rtTypes.getRealtimeObjectsOutputSchema().isVarsEmpty())
				continue;
			if (SH.startsWith(s, AmiWebManagers.FEED)) {
				if (service.getWebManagers().getAmiObjectsByTypeIfExists(s) == null) {
					justSubscribed.add(s);
				}
			}
			Table table = AmiWebUtils.toTable(this.service.getWebManagers(), s);
			runner.getVars().getTableset().putTable(table.getTitle(), new ReadonlyTable(table));
		}
		if (justSubscribed.size() > 0)
			getManager()
					.showAlert("Starting new subscription to " + SH.join(',', justSubscribed) + " so the data will be available momentarily and you will need to rerun the test");
	}
	public String getCrontabTimezone() {
		return this.crontabTimezone;
	}
	public String getCrontab() {
		return this.crontab;
	}
	public void setCrontabTimezone(String s) {
		this.crontabTimezone = s;
	}
	public void setCrontab(String s) {
		if (SH.isnt(s)) {
			this.crontab = null;
			this.crontabField.setValue("Timers (0)");
		} else {
			this.crontab = s;
			int count = SH.getCount("&", s) + 1;
			this.crontabField.setValue("Timers (" + count + ")");
		}
	}
	@Override
	public void onDatasourceAdded(AmiWebDatasourceWrapper gui) {

	}
	@Override
	public void onDatasourceUpdated(AmiWebDatasourceWrapper gui) {

	}
	@Override
	public void onDatasourceRemoved(AmiWebDatasourceWrapper gui) {
	}
	@Override
	public void onTableAdded(AmiWebTableSchemaWrapper table) {
		String name = AmiWebManagers.FEED + table.getName();
		this.usedRtSources.addOption(name, name);
	}
	@Override
	public void onTableRemoved(AmiWebTableSchemaWrapper table) {
		String name = AmiWebManagers.FEED + table.getName();
		this.usedRtSources.removeOption(name);
	}
	@Override
	public void onGuiClearing(AmiWebSystemObjectsManager gui) {
	}

	public void removeTableListener() {
		service.getSystemObjectsManager().removeListener(this);
	}
}