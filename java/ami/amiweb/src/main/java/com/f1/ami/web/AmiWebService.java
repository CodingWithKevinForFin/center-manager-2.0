package com.f1.ami.web;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiScmAdapter;
import com.f1.ami.amicommon.AmiScmException;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.centerclient.AmiCenterClientGetSnapshotResponse;
import com.f1.ami.amicommon.centerclient.AmiCenterClientMsgStatusMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessages;
import com.f1.ami.amicommon.centerclient.AmiCenterClientSnapshot;
import com.f1.ami.amicommon.centerclient.AmiCenterClientStats;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.ami.amicommon.msg.AmiCenterResponse;
import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amicommon.msg.AmiRelaySendEmailRequest;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.web.amiscript.AmiWebCommandResponse;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.ami.web.amiscript.AmiWebTopCalcFrameStack;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.ami.web.auth.AmiWebState;
import com.f1.ami.web.bpipe.plugin.BPIPEPlugin;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.charts.AmiWebChartImagesManager;
import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterPlugin;
import com.f1.ami.web.datafilter.AmiWebDataSessionImpl;
import com.f1.ami.web.dm.AmiWebDmEditorsManager;
import com.f1.ami.web.dm.AmiWebDmLayoutManagerImpl;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.form.AmiWebQueryFormEditorsManager;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormButtonFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormCheckBoxFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormColorGradientPickerFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormColorPickerFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormDateFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormDateRangeFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormDateTimeFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormDivFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormFileUploadFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormImageFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormMultiCheckBoxFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormMultiSelectFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormPasswordFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormRadioButtonFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormRangeFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormSelectFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormSubRangeFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormTextAreaFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormTextFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormTimeFieldFactory;
import com.f1.ami.web.form.factory.AmiWebFormTimeRangeFieldFactory;
import com.f1.ami.web.graph.AmiWebGraphManager;
import com.f1.ami.web.headless.AmiWebHeadlessWebState;
import com.f1.ami.web.menu.AmiWebCustomContextMenuEditorsManager;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleImpl;
import com.f1.ami.web.style.AmiWebStyleManager;
import com.f1.ami.web.style.impl.AmiWebStyleOption;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_3dChart;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Chart;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_ChartAxis;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Divider;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Field;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Filter;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Form;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormButtonField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormCheckboxField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormColorPickerField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateRangeField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDateTimeField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormDivField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormGradientColorPickerField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormImageField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormMultiCheckboxField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormMultiSelectField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormPasswordField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormRadioField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormRangeSliderField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormSelectField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormSliderField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTextField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTextareaField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTimeField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormTimeRangeField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_FormUploadField;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Global;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Heatmap;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Panel;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_Graph;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_Legend;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_RenderingLayer_RadialGraph;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Scroll;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Table;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Tabs;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Treegrid;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.ami.web.tree.AmiWebTreePortlet;
import com.f1.ami.web.userpref.AmiWebUserPreferencesPlugin;
import com.f1.base.Action;
import com.f1.base.CalcFrame;
import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Password;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ResultMessage;
import com.f1.http.HttpRequestResponse;
import com.f1.stringmaker.impl.BasicStringMakerSession;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.BackendResponseListener;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletManagerListener;
import com.f1.suite.web.portal.PortletNotificationListener;
import com.f1.suite.web.portal.PortletService;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.PortletNotification;
import com.f1.suite.web.portal.impl.form.FormPortletColorField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CachedResource;
import com.f1.utils.FastPrintStream;
import com.f1.utils.GuidHelper;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.event.SimpleEventReaper;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.MapInMap;
import com.f1.utils.structs.SetsBackedSet;
import com.f1.utils.structs.Tuple3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.SingletonCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiWebService implements PortletService, AmiWebCenterListener, PortletManagerListener, PortletNotificationListener, ConfirmDialogListener, ToDerivedString, AmiService,
		AmiWebDomObject, HtmlPortletListener {
	private static final Logger log = LH.get();

	public static final String ID = "AMI_SERVICE";

	private static final String HOUR_GLASS = "<div id=\"loading-div\"><svg version=\"1.1\" id=\"loading\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" x=\"0px\" y=\"0px\" viewBox=\"0 0 314 397\" style=\"enable-background:new 0 0 314 397;\" xml:space=\"preserve\">"
			+ "<rect stroke=\"$CLR_LN\" fill=\"$CLR_BG\" x=\"27\" y=\"24\" style=\"stroke-width:13;stroke-miterlimit:10;\" width=\"264\" height=\"26\"/> <rect stroke=\"$CLR_LN\" fill=\"$CLR_BG\" x=\"27\" y=\"348\" style=\"stroke-width:13;stroke-miterlimit:10;\" width=\"264\" height=\"26\"/>"
			+ "<path stroke=\"$CLR_LN\" fill=\"$CLR_BG\" style=\"stroke-width:13;stroke-miterlimit:10;\" d=\"M258.8,53H156.6H54.5 c0.6,72.4,72.3,107.6,73.5,146c-1.2,38.4-72.9,73.6-73.5,146h102.2h102.2c-0.6-72.4-72.6-107.1-73.8-146 C186.3,160.1,258.2,125.4,258.8,53z\"/>"
			+ "<path class=\"appear-sand\" fill=\"$CLR_FL\" d=\"M157.4,187c0-29,65.3-54.9,65.6-108h-66.8H88.5c0.2,53.1,66.5,79.4,66.5,108H157.4z\"/> <path class=\"disappear-sand\" fill=\"$CLR_FL\" d=\"M157.4,211c0,29,65.3,54.9,65.6,108h-66.8H88.5c0.2-53.1,66.5-79.4,66.5-108H157.4z\"/> </svg></div>";

	private static final String HOUR_GLASS2 = "<div id=\"pulse-div\"><div id=\"pulse-gif\"></div></div>"; //TODO: to be checked in later
	private static final Set<Class<? extends Action>> INTERESTED = (Set) CH.s(AmiCenterChanges.class, AmiCenterResponse.class, AmiCenterClientMsgStatusMessage.class,
			AmiCenterClientGetSnapshotResponse.class, AmiCenterClientObjectMessages.class, AmiCenterClientSnapshot.class, AmiWebServiceAction.class);

	private static final Comparator<AmiWebObject> OBJECT_ID_COMPARATOR = new Comparator<AmiWebObject>() {

		@Override
		public int compare(AmiWebObject o1, AmiWebObject o2) {
			return OH.compare(o1.getId(), o2.getId());
		}
	};

	private List<AmiWebPanelsListener> panelsListeners = new ArrayList<AmiWebPanelsListener>();
	final private Map<String, AmiWebPanelPluginWrapper> panelPlugins = new TreeMap<String, AmiWebPanelPluginWrapper>();
	final private Map<String, CachedResource> resources = new HashMap<String, CachedResource>();
	final private AmiWebPortletManagerSecurityModel securityModel;
	//	private int defaultCmdTimeoutMs;
	final private MapInMap<String, String, AmiWebAliasPortlet> amiPanelIdToAmiWebPortlet = new MapInMap<String, String, AmiWebAliasPortlet>();
	final private Map<String, AmiWebAliasPortlet> amiPanelAliasDotIdToAmiWebPortlet = new HashMap<String, AmiWebAliasPortlet>();
	final private MapInMap<String, String, AmiWebAliasPortlet> amiUserPrefIdToAmiWebPortlet = new MapInMap<String, String, AmiWebAliasPortlet>();
	private AmiWebDesktopPortlet desktop;
	final private AmiWebFileSystem fileSystem;
	final private Map<String, AmiWebNotification> notificationMetadata = new HashMap<String, AmiWebNotification>();

	//managers
	final private AmiWebManagers agentManager;
	final private AmiWebVarsManager varsManager;
	final private PortletManager manager;
	final private AmiWebCloudManager cloudManager;
	final private AmiWebFormatterManager formatterManager;
	final private AmiWebSystemObjectsManager systemObjectsManager;
	final private AmiWebDmManagerImpl dmManager;
	final private AmiWebDmLayoutManagerImpl dmLayoutManager;
	final private AmiWebStyleManager styleManager;
	final private AmiWebDebugManagerImpl debugManager;
	final private AmiWebScriptManager scriptManager;
	final private AmiWebChartImagesManager chartImagesManager;
	final private AmiWebCustomCssManager customCssManager;
	final private AmiWebGuiServiceAdaptersManager guiServiceAdaptersManager;
	final private AmiEncrypter encrypter;
	final private AmiWebFontsManager fontsManager;
	final private AmiWebLayoutFilesManager layoutFilesManager;
	final private AmiWebAutosaveManager autosaveManager;
	final private AmiWebPreferencesManager preferencesManager;
	final private AmiWebDomObjectsManager domObjectsManager;
	final private AmiWebPanelManager amiPanelManager;
	final private AmiWebQueryFormEditorsManager amiWebQueryFormEditorsManager;
	final private AmiWebDmEditorsManager amiWebDmEditorsManager;
	final private AmiWebCallbackEditorsManager amiWebCallbackEditorsManager;
	final private AmiWebCustomContextMenuEditorsManager amiWebCustomContextMenuEditorsManager;
	final private AmiWebBreakpointManager breakpointManager;
	final private AmiWebViewMethodsManager amiWebViewMethodsManager;
	final private AmiWebIdleSessionManager idleSessionManager;
	final private AmiWebUserFilesManager userFilesManager;
	final private AmiWebUserSettingsManager userSettingsManager;
	final private Map<String, String> urlParams = new HashMap<String, String>();

	final private PortletStyleManager_Dialog userDialogStyleManager = new PortletStyleManager_Dialog();
	final private PortletStyleManager_Form userFormStyleManager = new PortletStyleManager_Form();
	final private Map<String, AmiWebEditAmiScriptCallbacksPortlet> amiscriptEditors = new HashMap<String, AmiWebEditAmiScriptCallbacksPortlet>();
	final private Map<String, AmiWebFormFieldFactory<?>> fieldFactories = new LinkedHashMap<String, AmiWebFormFieldFactory<?>>();
	final private List<String> shellHistory = new ArrayList<String>();

	final private AmiWebDataFilter dataFilter;
	final private boolean isDebug;
	final private AmiWebLayoutManager layoutManager;
	final private Map<String, AmiScmPlugin> scmPlugins;

	final private AmiWebDevTools devtools;
	final private AmiCenterDefinition[] centerDefinitions;
	final private AmiWebStatsManager webStatsManager;
	final private AmiWebUserConfigStore userConfigStore;
	final private AmiWebGraphManager graphManager;
	final private boolean allowJavascriptEmbeddedInHtml;

	private AmiWebCompilerListener EMPTY[] = new AmiWebCompilerListener[0];
	private AmiWebCompilerListener compilerListeners[] = EMPTY;
	private String scmBasePath;
	private SimpleEventReaper eventReaper;
	private String sessionLabel;

	private AmiWebResourcesManager resourcesManager;
	private BPIPEPlugin bpipe;

	private byte activityLogLevel = 0;

	public AmiWebService(PortletManager manager, AmiWebCloudManager cloud, Map<String, AmiScriptClassPluginWrapper> customClassPlugins,
			Map<String, AmiWebPanelPluginWrapper> panelPlugins, CalcFrame amiscriptProperties, AmiWebDataFilterPlugin dataFilterPlugin,
			AmiWebUserPreferencesPlugin userPreferencesPlugin, Map<String, AmiScmPlugin> scmPlugins, AmiEncrypter encrypter, Collection<AmiWebGuiServicePlugin> gsa,
			Map<String, AmiWebRealtimeProcessorPlugin> realtimeProcessorPlugins, AmiWebFontsManager fontsManager, AmiCenterDefinition[] centerDefinitions,
			AmiCenterClientStats webstats, AmiWebFileSystem fileSystem, AmiWebFile webResourcesDir, String userConfigBasePath) {
		initFormFieldFactories();
		this.fileSystem = fileSystem;

		this.centerDefinitions = centerDefinitions;
		this.devtools = new AmiWebDevTools(this);
		this.encrypter = encrypter;
		this.scmPlugins = scmPlugins;
		this.manager = manager;
		this.fileSystem.init(this);
		this.cloudManager = cloud;
		this.cloudManager.init(this);
		this.userFilesManager = new AmiWebUserFilesManager(this.manager, this.fileSystem, this.fileSystem.getFile(userConfigBasePath));
		this.userSettingsManager = new AmiWebUserSettingsManager(this.manager, this.userFilesManager);
		this.userConfigStore = new AmiWebUserConfigStore(this.userFilesManager, this.userSettingsManager);
		this.fontsManager = fontsManager;
		this.securityModel = new AmiWebPortletManagerSecurityModel(this);
		this.layoutManager = new AmiWebLayoutManager(this);
		this.panelPlugins.putAll(panelPlugins);
		this.isDebug = manager.getTools().getOptional(AmiWebProperties.PROPERTY_AMI_DEBUG, Boolean.FALSE);
		String temp = manager.getTools().getOptional(AmiWebProperties.PROPERTY_ACTIVITY_LOG, "off");
		if (OH.eq(temp, "on")) {
			this.setActivityLogLevel((byte) 1);
		} else if (OH.eq(temp, "verbose")) {
			this.setActivityLogLevel((byte) 2);
		}
		this.allowJavascriptEmbeddedInHtml = manager.getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_ALLOW_JAVASCRIPT_EMBEDDED_IN_HTML, Boolean.FALSE);
		this.customCssManager = new AmiWebCustomCssManager(this);
		this.chartImagesManager = new AmiWebChartImagesManager(this, manager.getTools().getContainer().getThreadPoolController().getThreadPool("AMI_IMAGES"));
		this.varsManager = new AmiWebVarsManager(manager, this, amiscriptProperties, this.userSettingsManager);
		this.autosaveManager = new AmiWebAutosaveManager(this);
		this.scriptManager = new AmiWebScriptManager(this, customClassPlugins);
		this.agentManager = new AmiWebManagers(this);
		this.resourcesManager = new AmiWebResourcesManager(this, webResourcesDir);
		this.systemObjectsManager = this.agentManager.getPrimarySystemObjectsManager();
		this.dmManager = new AmiWebDmManagerImpl(this);
		this.dmLayoutManager = new AmiWebDmLayoutManagerImpl(this.dmManager);
		this.dmLayoutManager.addListener(this.dmManager);
		this.manager.addPortletNotificationListener(this);
		this.debugManager = new AmiWebDebugManagerImpl(this);
		this.formatterManager = new AmiWebFormatterManager(this);
		this.layoutFilesManager = new AmiWebLayoutFilesManager(this);
		this.preferencesManager = new AmiWebPreferencesManager(this);
		this.domObjectsManager = new AmiWebDomObjectsManager(this);
		this.amiPanelManager = new AmiWebPanelManager(this);
		this.amiWebQueryFormEditorsManager = new AmiWebQueryFormEditorsManager(this);
		this.amiWebDmEditorsManager = new AmiWebDmEditorsManager(this);
		this.amiWebCallbackEditorsManager = new AmiWebCallbackEditorsManager(this);
		this.amiWebViewMethodsManager = new AmiWebViewMethodsManager(this);
		this.amiWebCustomContextMenuEditorsManager = new AmiWebCustomContextMenuEditorsManager(this);
		this.webStatsManager = new AmiWebStatsManager(this, webstats);
		this.idleSessionManager = new AmiWebIdleSessionManager(this);

		long eventReaperDefaultTimeout = manager.getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_EVENT_REAPER_DEFAULT_TIMEOUT, SimpleEventReaper.DEFAULT_TIMEOUT);
		this.eventReaper = new SimpleEventReaper(eventReaperDefaultTimeout);

		getPrimaryWebManager().addClientConnectedListener(this);
		manager.addPortletManagerListener(this);

		this.styleManager = new AmiWebStyleManager(this);
		this.styleManager.addType(AmiWebStyleTypeImpl_Panel.INSTANCE);//TODO: should all use INSTANCE const
		this.styleManager.addType(AmiWebStyleTypeImpl_Treegrid.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_3dChart.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Chart.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Divider.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Form.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Field.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Heatmap.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Table.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Tabs.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_ChartAxis.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_RenderingLayer_Graph.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_RenderingLayer_RadialGraph.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_RenderingLayer_Legend.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Global.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Filter.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_Scroll.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormButtonField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormCheckboxField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormColorPickerField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormDateField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormDateRangeField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormDateTimeField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormDivField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormGradientColorPickerField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormImageField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormMultiCheckboxField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormMultiSelectField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormPasswordField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormRadioField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormRangeSliderField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormSelectField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormSliderField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormTextareaField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormTextField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormTimeField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormTimeRangeField.INSTANCE);
		this.styleManager.addType(AmiWebStyleTypeImpl_FormUploadField.INSTANCE);
		this.breakpointManager = new AmiWebBreakpointManager(this);
		for (AmiWebPanelPluginWrapper plugin : this.panelPlugins.values())
			this.styleManager.addType(plugin.getPlugin().getStyleType());
		initUserFormStyleManager();
		initUserDialogStyleManager();
		this.graphManager = new AmiWebGraphManager(this);
		clear();
		this.initManagers();
		if (dataFilterPlugin != null) {
			AmiWebDataSessionImpl session = new AmiWebDataSessionImpl(this);
			this.dataFilter = dataFilterPlugin.createDataFilter(session);
			if (this.getPortletManager().getIsClosed())
				throw new RuntimeException("Portlet manager closed by data filter");
			this.dataFilter.onLogin();
			LH.info(log, getPortletManager().describeUser(), ": Plugin ", dataFilterPlugin, " Generated filter: ", dataFilter);
			this.agentManager.setDataFilter(dataFilter);
		} else
			this.dataFilter = null;
		createBloombergSession();
		this.agentManager.setRealtimeProcessorPlugins(realtimeProcessorPlugins);
		this.guiServiceAdaptersManager = new AmiWebGuiServiceAdaptersManager(this, gsa);
		manager.setUserConfigStore(this.userConfigStore);
	}

	public void initManagers() {
		// 1) Setup DmManager
		this.getDmManager().addDmManagerListener(this.dmLayoutManager);
		this.addAmiWebPanelsListener(this.dmLayoutManager);

		// 2) Setup GraphManager
		this.getDmManager().addDmManagerListener(this.graphManager);
		this.addAmiWebPanelsListener(this.graphManager);
		AmiWebSystemObjectsManager[] systemObjectManagers = this.getWebManagers().getSystemObjectManagers();
		// ensure all centers' rt tables are visible in Real time feeds
		for (AmiWebSystemObjectsManager asom : systemObjectManagers) {
			asom.addListener(this.graphManager);
		}
		this.getPortletManager().addPortletListener(this.graphManager);
		this.getWebManagers().addListener(this.graphManager);

		// 3) Call Init after adding all listeners
		this.graphManager.init();
	}
	public void clearManagers() {
		// 1) Clear GraphManager
		this.getWebManagers().removeListener(this.graphManager);
		this.getPortletManager().removePortletlistener(this.graphManager);
		AmiWebSystemObjectsManager[] systemObjectManagers = this.getWebManagers().getSystemObjectManagers();
		for (AmiWebSystemObjectsManager asom : systemObjectManagers) {
			asom.removeListener(this.graphManager);
		}
		this.getDmManager().removeDmManagerListener(this.graphManager);
		this.removeAmiWebPanelsListener(this.graphManager);

		// 2) Clear DmManager
		this.getDmManager().removeDmManagerListener(this.dmLayoutManager);
		this.removeAmiWebPanelsListener(this.dmLayoutManager);
	}

	public boolean initScm(String password) {
		AmiWebVarsManager store = this.getVarsManager();
		String type = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_TYPE);
		if (SH.is(type)) {
			AmiScmPlugin plugin = getScmPlugins().get(type);
			if (plugin == null) {
				getPortletManager()
						.showAlert("Source Control Adapter '" + type + "' not found<BR>(Plugins added using the " + AmiWebProperties.PROPERTY_AMI_SCM_PLUGINS + " property)");
				return false;
			}
			String username = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_USERNAME);
			String passwordSaveMode = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_SAVE_PASSWORD_MODE);
			if (password == null && AmiWebConsts.ENCRYPTED_NONE.equals(passwordSaveMode)) {
				getPortletManager().showDialog("Source Control Password",
						new ConfirmDialogPortlet(getPortletManager().generateConfig(), "Please enter " + plugin.getScmDescription() + " password for " + username,
								ConfirmDialogPortlet.TYPE_OK_CANCEL, this, new FormPortletTextField("Password: ").setPassword(true)).setCallback("SCM_PASSWORD"));
				return false;
			} else {
				AmiScmAdapter adapter = plugin.createScmAdapter();
				String url = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_URL);
				String client = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_CLIENT);
				if (password == null) {
					password = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PASSWORD);
					if (AmiWebConsts.ENCRYPTED_ENCRYPTED.equals(passwordSaveMode))
						password = encrypter.decrypt(password);
				}
				String options = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_OPTIONS);
				String path = store.getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PATH);
				try {
					adapter.init(this.getPortletManager().getTools(), url, client, username, SH.toCharArray(password), path, options);
					this.scmAdapter = adapter;
					this.scmBasePath = path;
					return true;
				} catch (AmiScmException e) {
					getPortletManager().showAlert("SCM Connection Failed: <B>" + e.getMessage(), e);
					return false;
				}
			}
		}
		return false;
	}
	private void initUserFormStyleManager() {
		this.userFormStyleManager.setDefaultFormButtonsHeight(28);
		this.userFormStyleManager.setDefaultFormButtonsPaddingTop(10);
		this.userFormStyleManager.setDefaultFormButtonsPaddingBottom(4);
		this.userFormStyleManager.setDefaultFormButtonsSpacing(8);
		this.userFormStyleManager.setDefaultFormLabelsWidth(164);
		this.userFormStyleManager.setDefaultFormLabelsStyle("_fs=11px|_fm=bold,arial|style.padding=0px 6px 0px 0px");
		this.userFormStyleManager.setDefaultFormButtonsStyle("_bg=#e2e2e2|style.border=1px solid #aaaaaa|style.minWidth=95px|_fn=arial|_fs=17|_fg=#000000");
		this.userFormStyleManager.putDefaultFormFieldStyle(FormPortletTitleField.JSNAME,
				"_fs=11px|style.textTransform=upperCase|_fg=#000000|style.padding=4px 0px 0px 0px|style.display=block");
		this.userFormStyleManager.putDefaultFormFieldWidth(FormPortletTextField.JSNAME, 200);
		this.userFormStyleManager.putDefaultFormFieldStyle(FormPortletTextField.JSNAME, "style.border=1px solid #aaaaaa|_fs=11px");
		this.userFormStyleManager.putDefaultFormFieldStyle(FormPortletSelectField.JSNAME, "style.border=1px solid #aaaaaa|_fs=11px");
		this.userFormStyleManager.putDefaultFormFieldStyle(FormPortletTextAreaField.JSNAME, "style.border=1px solid #aaaaaa|_fs=11px|style.resize=none");
		this.userFormStyleManager.putDefaultFormFieldStyle(FormPortletColorField.JSNAME, "style.border=1px solid #aaaaaa|_fs=11px");
		this.userFormStyleManager.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_GRIP_COLOR, "#007608");
		this.userFormStyleManager.putDefaultFormFieldOption(FormPortletNumericRangeField.JSNAME, FormPortletNumericRangeField.OPTION_SCROLL_TRACK_LEFT_COLOR, "#007608");
		this.userFormStyleManager.putDefaultFormFieldHeight(FormPortletTitleField.JSNAME, 27);
		this.userFormStyleManager.setDefaultFormWidthStretchPadding(6);
		this.userFormStyleManager.setDefaultFormFieldHeight(25);
		this.userFormStyleManager.setDefaultFormFieldSpacing(6);
	}
	private void initUserDialogStyleManager() {
		this.userDialogStyleManager.getDefaultDialogOptions().put(DesktopPortlet.OPTION_WINDOW_FONTSTYLE, "_fs=11px");
	}

	public String getResource(String fileName) {
		try {
			CachedResource r = resources.get(fileName);
			if (r == null)
				resources.put(fileName, r = new CachedResource("com/f1/ami/web/" + fileName, 1000));
			return r.getData().getText();
		} catch (Exception e) {
			throw OH.toRuntime(e);
		}
	}

	public BasicWebCellFormatter getBasicNotNullFormatter(String notNull) {
		return new AmiWebBasicWebCellFormatter().setNullValue(notNull);
	}
	public AmiWebManagers getWebManagers() {
		return this.agentManager;
	}
	public AmiWebManager getPrimaryWebManager() {
		return this.agentManager.getPrimaryWebManager();
	}

	// ######### SERVICE IMPLEMENTATION ##########
	@Override
	public String getServiceId() {
		return ID;
	}

	@Override
	public void close() {
		this.agentManager.onClosed();
	}

	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return INTERESTED;
	}

	public AmiWebScriptManager getScriptManager() {
		return this.scriptManager;
	}

	public void processCommands(int mask) {
		for (AmiWebCommandWrapper cmd : getSystemObjectsManager().getAmiCommands()) {
			if (MH.allBits(cmd.getCallbacksMask(), mask)) {
				HashMap t = new HashMap();
				if (cmd.matchesFilter(EmptyCalcTypes.INSTANCE, EmptyCalcFrame.INSTANCE, this) && cmd.matchesWhere(EmptyCalcTypes.INSTANCE, EmptyCalcFrame.INSTANCE, this))
					AmiWebUtils.showRunCommandDialog(null, this, cmd, null, null, null);
			}
		}
	}

	public <T> T nw(Class<T> clazz) {
		return manager.getGenerator().nw(clazz);
	}

	public String getUserName() {
		return manager.getUserName();
	}

	//	@Override
	//	public void onCenterDisconnected(AmiWebManager manager, IterableAndSize<AmiWebObject> objects) {
	//
	//	}

	//	@Override
	//	public void onCenterSnapshotProcessed(AmiWebManager manager) {
	//		if (manager.getCenterDef().isPrimary())
	//			this.processCommands(AmiRelayCommandDefMessage.CALLBACK_USER_LOGIN);
	//	}

	@Override
	public void onCenterConnectionStateChanged(AmiWebManager manager, byte state) {
		if (manager.getCenterDef().isPrimary() && manager.getIsEyeConnected())
			this.processCommands(AmiRelayCommandDefMessage.CALLBACK_USER_LOGIN);
	}

	@Override
	public void onPortletManagerClosed() {
		if (this.dataFilter != null)
			this.dataFilter.onLogout();
		if (this.getBPIPE() != null) {
			this.startBpipeLogoutThread();
		}
		this.agentManager.onClosed();
		this.autosaveManager.onPortletManagerClosed();
	}

	public AmiWebCloudManager getCloudManager() {
		return this.cloudManager;
	}

	public PortletManager getPortletManager() {
		return this.manager;
	}

	public void onAdnChanging(AmiWebAbstractPortlet portlet, String oldAdn, String newAdn) {
		if (oldAdn != null) {
			amiPanelIdToAmiWebPortlet.removeMulti(AmiWebUtils.getAliasFromAdn(oldAdn), AmiWebUtils.getNameFromAdn(oldAdn));
			amiPanelAliasDotIdToAmiWebPortlet.remove(oldAdn);
		}
		amiPanelIdToAmiWebPortlet.putMulti(AmiWebUtils.getAliasFromAdn(newAdn), AmiWebUtils.getNameFromAdn(newAdn), portlet);
		amiPanelAliasDotIdToAmiWebPortlet.put(newAdn, portlet);
		if (oldAdn != null) {
			getLayoutFilesManager().onAdnChanged(oldAdn, newAdn, portlet);
		}
	}
	public void onAdnChanged(AmiWebAbstractPortlet portlet, String oldAdn, String newAdn) {
		if (oldAdn != null) {
			this.fireAmiWebPanelIdChanged(portlet, oldAdn, newAdn);
			this.getDesktop().updateDashboard();
		}
		if (portlet instanceof AmiWebRealtimePortlet) {
			if (oldAdn == null)
				this.agentManager.onRealtimePortelAdded(newAdn, (AmiWebRealtimePortlet) portlet);
			else
				this.agentManager.onRealtimePortelAdnChaned(oldAdn, newAdn, (AmiWebRealtimePortlet) portlet);
		}
	}

	public static String generateId(Portlet amiWebPortlet) {
		String id;
		if (amiWebPortlet instanceof AmiWebBlankPortlet) {
			id = "Blank1";
		} else if (amiWebPortlet instanceof AmiWebDividerPortlet) {
			id = "Div1";
		} else if (amiWebPortlet instanceof AmiWebTabPortlet) {
			id = "Tabs1";
		} else if (amiWebPortlet instanceof AmiWebFilterPortlet) {
			id = "Filter1";
		} else if (amiWebPortlet instanceof AmiWebScrollPortlet) {
			id = "Scroll1";
		} else if (amiWebPortlet instanceof AmiWebQueryFormPortlet) {
			id = "Html1";
		} else if (amiWebPortlet instanceof AmiWebAbstractTablePortlet) {
			id = "Table1";
		} else if (amiWebPortlet instanceof AmiWebChartGridPortlet) {
			id = "Chart1";
		} else if (amiWebPortlet instanceof AmiWebTreemapPortlet) {
			id = "Heatmap1";
		} else if (amiWebPortlet instanceof AmiWebTreePortlet) {
			id = "Treegrid1";
		} else if (amiWebPortlet instanceof AmiWebSurfacePortlet) {
			id = "Chart3d1";
		} else {
			id = "PNL1";
		}
		return id;
	}
	public String registerAmiUserPrefId(String userPrefId, AmiWebAliasPortlet amiWebPortlet) {
		String oldUserPrefId = amiWebPortlet.getAmiUserPrefId();
		if (oldUserPrefId != null) {
			amiUserPrefIdToAmiWebPortlet.removeMulti(amiWebPortlet.getAmiLayoutFullAlias(), oldUserPrefId);//TODO:
		}
		if (SH.isnt(userPrefId))
			userPrefId = generateId(amiWebPortlet);
		String alias = (amiWebPortlet instanceof AmiWebAliasPortlet) ? ((AmiWebAliasPortlet) amiWebPortlet).getAmiLayoutFullAlias() : "";
		userPrefId = getNextUserPrefId(alias, userPrefId);
		amiUserPrefIdToAmiWebPortlet.putMulti(alias, userPrefId, amiWebPortlet);
		amiWebPortlet.setAmiUserPrefId(userPrefId);
		return userPrefId;
	}
	public AmiWebAliasPortlet getPortletByPanelId(String fullAlias, String panelId) {
		return this.amiPanelIdToAmiWebPortlet.getMulti(fullAlias, panelId);
	}
	public AmiWebAliasPortlet getPortletByAliasDotPanelId(String adn) {
		return this.amiPanelAliasDotIdToAmiWebPortlet.get(adn);
	}
	public AmiWebAliasPortlet getPortletByUserPrefId(String fullAlias, String userPrefId) {
		return this.amiUserPrefIdToAmiWebPortlet.getMulti(fullAlias, userPrefId);
	}
	public AmiWebAliasPortlet getPortletByUserPrefIdOrThrow(String fullAlias, String userPrefId) {
		return CH.getOrThrow(this.amiUserPrefIdToAmiWebPortlet.getOrCreate(fullAlias), userPrefId);
	}

	public void onAmiPortletClosed(AmiWebPortlet portlet) {
		this.amiPanelIdToAmiWebPortlet.removeMulti(portlet.getAmiLayoutFullAlias(), portlet.getAmiPanelId());
		this.amiPanelAliasDotIdToAmiWebPortlet.remove(portlet.getAmiLayoutFullAliasDotId());
		if (portlet instanceof AmiWebAliasPortlet) {
			final String amiUserPrefId = ((AmiWebPortlet) portlet).getAmiUserPrefId();
			if (amiUserPrefId != null)
				amiUserPrefIdToAmiWebPortlet.removeMulti(portlet.getAmiLayoutFullAlias(), amiUserPrefId);
		}
		if (portlet instanceof AmiWebRealtimePortlet)
			this.agentManager.onRealtimePortelRemoved(portlet.getAmiLayoutFullAliasDotId());
	}

	private boolean isFirstPageRefresh = true;

	private String pageUrl;

	public String getPageUrl() {
		return this.pageUrl;
	}

	@Override
	public void onPageRefreshed(PortletManager portletManager) {
		if (isFirstPageRefresh) {
			// first time refresh (ex. login), let layout handle adapter init because both will call adapter init
			isFirstPageRefresh = false;
			initScm(null);
		} else {
			// beyond first refresh, call init
			guiServiceAdaptersManager.setAdaptersLoaded(false);
			guiServiceAdaptersManager.callJsInit();
		}
		BasicPortletManager bpm = (BasicPortletManager) portletManager;
		varsManager.onPageRefreshed(bpm);
	}

	public void setDesktop(AmiWebDesktopPortlet amiWebDesktopPortlet) {
		this.desktop = amiWebDesktopPortlet;
		this.breakpointManager.setDesktop(this.desktop);
	}
	/*
	 * AmiWebDesktopPortlet is initialized after the AmiWebDestopPortlet calls service.setDesktop
	 */
	public AmiWebDesktopPortlet getDesktop() {
		return this.desktop;
	}
	public AmiCenterPassToRelayRequest sendEmailToBackEnd(String body, String subject, List<String> toList, String from, boolean isHtml, List<String> attachmentNames,
			List<byte[]> attachmentDatas, String username, Password password, int timeout, BackendResponseListener listener, String relayToUse) {
		AmiRelaySendEmailRequest msg = nw(AmiRelaySendEmailRequest.class);
		msg.setBody(body);
		msg.setSubject(subject);
		msg.setToList(toList);
		msg.setFrom(from);
		msg.setIsHtml(isHtml);
		msg.setAttachmentNames(attachmentNames);
		msg.setAttachmentDatas(attachmentDatas);
		msg.setTimeoutMs(timeout);
		msg.setSendEmailUid(GuidHelper.getGuid(62));
		msg.setUsername(username);
		msg.setPassword(password);
		AmiCenterPassToRelayRequest req = nw(AmiCenterPassToRelayRequest.class);
		req.setAgentRequest(msg);
		req.setRelayMiid(getRelayMiid(relayToUse));
		AmiWebSendEmailListener fp = new AmiWebSendEmailListener(listener);
		sendRequestToBackend(fp, req);
		return req;
	}

	//will return the first relay that is found for a given name.
	private long getRelayMiid(String names) {
		if (SH.isnt(names))
			return -1;
		AmiWebObjects relayTables = this.getPrimaryWebManager().getAmiObjectsByType(AmiConsts.TYPE_RELAY);
		List<AmiWebObject> amiObjects = CH.l(relayTables.getAmiObjects());
		CH.sort(amiObjects, OBJECT_ID_COMPARATOR);
		if (relayTables != null)
			for (String name : SH.split(",", names)) {
				for (AmiWebObject i : amiObjects)
					if (OH.eq(name, i.getValue("RelayId")))
						return i.getId();
			}
		return -1L;
	}

	public class AmiWebSendEmailListener implements BackendResponseListener {
		private BackendResponseListener listener;

		public AmiWebSendEmailListener(BackendResponseListener listener) {
			this.listener = listener;
		}

		@Override
		public void onBackendResponse(ResultMessage<Action> result) {
			if (this.listener != null)
				this.listener.onBackendResponse(result);
		}

	}

	public AmiCenterPassToRelayRequest sendCommandToBackEnd(AmiWebCommandWrapper command, Table table, int timeout, BackendResponseListener listener) {
		//Create Command Backend Listener Dialog
		AmiWebCallCommandBackendListener fp = new AmiWebCallCommandBackendListener(manager.generateConfig(), listener);
		//		RootPortlet rp = (RootPortlet) this.getPortletManager().getRoot();
		//		RootPortletDialog dialog = rp.addDialog(SH.is(command.getTitle()) ? command.getTitle() : command.getCmdId(), fp);
		//		dialog.setStyle(AmiWebUtils.getService(manager).getUserDialogStyleManager());
		//		dialog.setHasCloseButton(true);
		//		this.getPortletManager().onPortletAdded(fp);

		//Create AmiCommandRequest msg
		AmiRelayRunAmiCommandRequest msg = nw(AmiRelayRunAmiCommandRequest.class);
		msg.setArguments(new HashMap<String, Object>());
		msg.setCommandDefinitionId(command.getCmdId());
		msg.setCommandId(command.getObject().getId());
		msg.setTimeoutMs(timeout);
		msg.setCommandUid(GuidHelper.getGuid(62));
		msg.setSessionId(varsManager.getSessionId());
		msg.setRelayConnectionId(OH.noNull(command.getRelayConnectionId(), -1));
		msg.setIsManySelect(command.getIsManySelect() || table.getSize() > 1);

		//Set msg object Ids
		String[] oIds = new String[table.getRows().size()];
		if (oIds.length != 0)
			oIds[0] = null;
		msg.setObjectIds(oIds);

		//Set ami object Ids
		Column idCol = table.getColumnsMap().get(AmiConsts.TABLE_PARAM_ID);
		if (idCol != null) {
			int pos = idCol.getLocation();
			long[] aIds = new long[table.getRows().size()];
			for (int i = 0; i < table.getRows().size(); i++)
				aIds[i] = OH.noNull(table.getRows().get(i).getAt(pos, Caster_Long.INSTANCE), -1L);
			if (hasIds(aIds))
				msg.setAmiObjectIds(aIds);
		}

		// Set fields 
		String[] fields = command.getFields();
		List<Map<String, Object>> fieldValues;
		if (AH.isEmpty(fields))
			fieldValues = (List) TableHelper.toListOfMaps(table);
		else
			fieldValues = TableHelper.toListOfMapsSelectedCols(table, fields);

		msg.setFields(fieldValues);

		//Embed msg in AmiCenterPassToRelayRequest
		AmiCenterPassToRelayRequest req = nw(AmiCenterPassToRelayRequest.class);
		req.setAgentRequest(msg);
		req.setRelayMiid(OH.noNull(command.getRelayId(), -1L));
		sendRequestToBackend(fp, req);
		return req;
	}

	public class AmiWebCallCommandBackendListener implements BackendResponseListener {

		private BackendResponseListener listener;

		public AmiWebCallCommandBackendListener(PortletConfig config, BackendResponseListener listener) {
			//			super(config);
			this.listener = listener;
			//			AmiWebService service = (AmiWebService) getManager().getService(AmiWebService.ID);
			//			FormPortlet fp = new FormPortlet(generateConfig());
			//			fp.setStyle(service.getUserFormStyleManager());
			//			fp.addButton(new FormPortletButton("Waiting for Response...")).setEnabled(false);
			//			this.addChild(fp);
			//			setSuggestedSize(600, 100);
		}
		@Override
		public void onBackendResponse(ResultMessage<Action> result) {
			//			this.close();
			if (this.listener != null)
				this.listener.onBackendResponse(result);
			else {
				AmiCenterPassToRelayRequest req = (AmiCenterPassToRelayRequest) result.getRequestMessage().getAction();
				AmiCenterPassToRelayResponse res = (AmiCenterPassToRelayResponse) result.getAction();
				AmiWebCommandResponse response = new AmiWebCommandResponse(AmiWebService.this, (AmiRelayRunAmiCommandRequest) req.getAgentRequest(),
						(AmiRelayRunAmiCommandResponse) res.getAgentResponse());
				getDesktop().getCallbacks().execute("onAsyncCommandResponse", response);
			}
		}

	}

	public AmiRelayRunAmiCommandRequest sendCommandToBackEnd(String portletId, AmiWebCommandWrapper command, Map<String, Object> arguments, int timeout,
			List<Map<String, Object>> fieldValues, AmiWebObject targets[], Row[] targetRows) {
		AmiRelayRunAmiCommandRequest msg = nw(AmiRelayRunAmiCommandRequest.class);
		msg.setArguments(arguments);
		msg.setCommandDefinitionId(command.getCmdId());
		msg.setCommandId(command.getObject().getId());
		msg.setTimeoutMs(timeout);
		msg.setCommandUid(GuidHelper.getGuid(62));
		msg.setSessionId(varsManager.getSessionId());
		msg.setRelayConnectionId(OH.noNull(command.getRelayConnectionId(), -1));
		if (AH.isntEmpty(targets)) {
			String[] oIds = new String[targets.length];
			long[] aIds = new long[targets.length];
			String[] types = new String[targets.length];

			boolean hasTargets = false;
			for (int i = 0; i < targets.length; i++) {
				AmiWebObject target = targets[i];
				if (target != null) {
					hasTargets = true;
					oIds[i] = target.getObjectId();
					aIds[i] = target.getId();
					types[i] = target.getTypeName();
				}
			}

			msg.setObjectIds(oIds);
			if (hasTargets) {
				msg.setAmiObjectIds(aIds);
				msg.setObjectTypes(types);
			}
		}
		if (AH.isntEmpty(targetRows)) {
			Column idCol = targetRows[0].getTable().getColumnsMap().get(AmiConsts.TABLE_PARAM_ID);
			if (idCol != null) {
				int pos = idCol.getLocation();
				long[] aIds = new long[targetRows.length];
				for (int i = 0; i < targetRows.length; i++)
					aIds[i] = OH.noNull(targetRows[i].getAt(pos, Caster_Long.INSTANCE), -1L);
				if (hasIds(aIds))
					msg.setAmiObjectIds(aIds);
			}
		}
		msg.setIsManySelect(command.getIsManySelect());
		if (fieldValues != null) {
			msg.setFields(fieldValues);
		}
		AmiCenterPassToRelayRequest req = nw(AmiCenterPassToRelayRequest.class);
		req.setAgentRequest(msg);
		req.setRelayMiid(OH.noNull(command.getRelayId(), -1L));
		sendRequestToBackend(portletId, req);
		return msg;
	}
	@Override
	public void onMetadataChanged(PortletManager basicPortletManager) {
	}

	public BasicStringMakerSession newBasicStringMakerSession() {
		BasicStringMakerSession r = new BasicStringMakerSession(Collections.EMPTY_MAP);
		r.setSupportDotsInVarNames(true);
		return r;
	}

	public AmiWebDmManager getDmManager() {
		return this.dmManager;
	}
	public AmiWebDmLayoutManagerImpl getDmLayoutManager() {
		return this.dmLayoutManager;
	}

	public String getNextPanelId(String fullAlias, String id) {
		SetsBackedSet<String> t = new SetsBackedSet<String>(false, this.amiPanelIdToAmiWebPortlet.getOrCreate(fullAlias).keySet(),
				this.layoutFilesManager.getLayoutByFullAlias(fullAlias).getHiddenPanelIds());
		id = SH.getNextId(id, t);
		return id;
	}
	public String getNextUserPrefId(String fullAlias, String id) {
		return SH.getNextId(id, this.amiUserPrefIdToAmiWebPortlet.getOrCreate(fullAlias).keySet());
	}

	public void clear() {
		LH.info(log, this.getPortletManager().describeUser(), " Resetting Services");
		this.setSessionLabel(null);
		this.scriptManager.clear();
		this.amiPanelIdToAmiWebPortlet.clear();
		this.amiPanelAliasDotIdToAmiWebPortlet.clear();
		this.amiUserPrefIdToAmiWebPortlet.clear();
		this.agentManager.clear();
		this.dmManager.clear();
		this.dmLayoutManager.clear();
		this.styleManager.clear();
		this.initDefaultStyle();
		this.initDefaultLayoutStyle();
		this.varsManager.clear();
		this.customCssManager.clear();
		this.domObjectsManager.clear();
		this.manager.getRoot().setTitle(this.manager.getDefaultBrowserTitle());
		this.amiscriptEditors.clear();
		this.amiWebDmEditorsManager.clear();
		this.amiWebQueryFormEditorsManager.clear();
		this.amiWebCallbackEditorsManager.clear();
		this.amiWebCustomContextMenuEditorsManager.clear();
		this.breakpointManager.clear();
		this.graphManager.clear();
		this.getIdleSessionManager().clear();
		for (String i : CH.l(this.getPortletManager().getPortletIds())) {
			Portlet portlet = this.getPortletManager().getPortletNoThrow(i);
			if (portlet instanceof AmiWebPortlet) {
				LH.warning(log, "Stale ami panel removed: ", portlet);
				portlet.close();
			}
		}
		LH.info(log, this.getPortletManager().describeUser(), " Reset Services");
	}
	public void initDefaultStyle() {
		String json = getResource("AMI_DEFAULT.amistyle.json");
		this.styleManager.clear();
		try {
			Map<String, Object> configuration = ((List<Map<String, Object>>) manager.getJsonConverter().stringToObject(json)).get(0);
			AmiWebStyleImpl dflt = new AmiWebStyleImpl(this.styleManager, configuration);
			dflt.setReadOnly(true);
			Map<String, Object> vl = (Map<String, Object>) configuration.get("vl");
			for (String type : dflt.getTypes()) {
				Map<String, Object> m = (Map<String, Object>) vl.get(type);
				if (m == null) {
					LH.warning(log, manager.describeUser(), " ==> Critical error found in AMI_DEFAULT.amistyle.json result, missing type: ", type);
				} else {
					for (short key : dflt.getDeclaredKeys(type))
						if (!m.containsKey(AmiWebStyleConsts.GET(key)))
							LH.warning(log, "Critical error found in AMI_DEFAULT.amistyle.json result, missing type: ", type + " --> " + AmiWebStyleConsts.GET(key));
				}
			}
			for (AmiWebStyleOption s : this.styleManager.getOptionsByVarname().values()) {
				if (dflt.resolveValue(s.getNamespace(), s.getKey()) == null)
					LH.warning(log, "Missing field in AMI_DEFAULT.amistyle.json: ", s.getNamespace(), ":", s.getSaveKey());
			}
			this.styleManager.addStyle(dflt);
		} catch (Exception e) {
			LH.warning(log, manager.describeUser(), " ==> Critical error found in AMI_DEFAULT.amistyle.json and as a result the style manager can not be initialized properly ", e);
		}
		for (File file : AmiUtils.findFiles(this.manager.getTools().getOptional(AmiWebProperties.PROPERTY_STYLE_FILES), false, false)) {
			try {
				String text = IOH.readText(file);
				List l = (List) manager.getJsonConverter().stringToObject(text);
				for (Object m : l) {
					AmiWebStyleImpl style = new AmiWebStyleImpl(this.styleManager, (Map<String, Object>) m);
					style.setUrl(IOH.getFullPath(file)).setReadOnly(true);
					this.styleManager.addStyle(style);
				}
			} catch (Exception e) {
				LH.warning(log, manager.describeUser(), " ==> Skipping invalid style file: ", IOH.getFullPath(file), e);
			}
		}
	}
	public void initDefaultLayoutStyle() {
		AmiWebStyleImpl style = new AmiWebStyleImpl(this.styleManager, AmiWebStyleManager.LAYOUT_DEFAULT_ID, AmiWebStyleManager.LAYOUT_DEFAULT_LABEL);
		style.setParentStyle(AmiWebStyleManager.FACTORY_DEFAULT_ID);
		this.styleManager.addStyle(style);
	}

	public AmiWebFileSystem getAmiFileSystem() {
		return this.fileSystem;
	}

	private void fireOnNotificationHandled(String string, AmiWebNotification md) {
		if (md != null)
			this.desktop.getCallbacks().execute("onNotificationHandled", md.getNotification().getId(), string, md.getAttachment());
	}
	@Override
	public void onNotificationClosed(PortletManager manager, PortletNotification notification) {
		fireOnNotificationHandled("CLOSED", this.notificationMetadata.remove(notification.getId()));
	}
	@Override
	public void onNotificationUserClicked(PortletManager manager, PortletNotification notification) {
		fireOnNotificationHandled("CLICKED", this.notificationMetadata.remove(notification.getId()));
	}
	@Override
	public void onNotificationDenied(PortletManager manager, PortletNotification notification) {
		fireOnNotificationHandled("DENIED", this.notificationMetadata.remove(notification.getId()));
	}
	public AmiWebNotification showNotification(String title, String body, String imageUrl, Map options, Map attachment) {
		PortletNotification notification = this.manager.showNotification(title, body, imageUrl, options);
		AmiWebNotification t = new AmiWebNotification(notification, attachment);
		notificationMetadata.put(notification.getId(), t);
		return t;
	}

	public Map<String, AmiWebPanelPluginWrapper> getPanelPlugins() {
		return this.panelPlugins;
	}

	private boolean justLoadedLayoutFromUrlParams = false;

	private boolean fireOnUrlParams = false;

	@Override
	public void onPageLoading(PortletManager basicPortletManager, Map<String, String> attributes, HttpRequestResponse action) {
		this.pageUrl = action.getRequestUrl();
		guiServiceAdaptersManager.fireOnPageLoading();
		FastPrintStream out = action.getOutputStream();
		for (AmiWebPanelPluginWrapper i : this.panelPlugins.values())
			out.println(i.getPlugin().getBootstrapHtml());
		out.println("<script type=\"text/javascript\" src=\"amiweb/ace-minified.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_desktop.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_scripteditor.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_canvasmgr.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_plot.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_axis.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_barchart.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_legendchart.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_radialchart.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/ami_form_editor.js?\"></script>");
		out.println("<script type=\"text/javascript\" src=\"amiweb/custom.js?\"></script>");
		//out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"amiweb/custom.css?\"/>");

		out.print("<style>");
		for (Entry<String, byte[]> e : this.fontsManager.getFontData().entrySet()) {
			Font font = this.fontsManager.getFont(e.getKey());
			out.println("@font-face {");
			out.println("  font-family: '" + font.getFamily() + "';");
			out.println("  font-weight: " + (font.isBold() ? "bold" : "normal") + ";");
			out.println("  font-style: " + (font.isItalic() ? "italic" : "normal") + ";");
			out.println("  src: url(data:application/x-font-woff;charset=utf-8;base64," + EncoderUtils.encode64(e.getValue()) + ");");
			out.println("}");
		}
		out.print("</style>");
		out.print("<style id='ami_custom_styles'></style>");
		guiServiceAdaptersManager.callJsLoadLibraries(out);
		out.print("<script>");
		customCssManager.callJs(out);
		out.print("</script>");
		for (AmiWebNotification md : this.notificationMetadata.values())
			fireOnNotificationHandled("CLEARED", md);
		this.notificationMetadata.clear();
		if (!justLoadedLayoutFromUrlParams)
			processUrlParams(attributes);
		justLoadedLayoutFromUrlParams = false;
	}

	public void processUrlParams(Map<String, String> attributes) {
		String layout = attributes.get(AmiWebConsts.URL_PARAM_LAYOUT);
		if (SH.is(layout)) {
			layout = backwardsCompColonToUnderbar(layout, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
			layout = backwardsCompColonToUnderbar(layout, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
			layout = backwardsCompColonToUnderbar(layout, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
			layout = backwardsCompColonToUnderbar(layout, AmiWebConsts.LAYOUT_SOURCE_SHARED);
			String source = SH.toUpperCase(SH.beforeFirst(layout, '_'));
			String name = SH.afterFirst(layout, '_');
			AmiWebLayoutFilesManager lfm = getLayoutFilesManager();
			if (lfm.getLayout() == null || OH.ne(lfm.getLayout().getSource(), source) || OH.ne(lfm.getLayout().getLocation(), name)) {
				lfm.loadLayout(name, null, source);
				justLoadedLayoutFromUrlParams = true;
			}
		}
		if (OH.ne(this.urlParams, attributes)) {
			this.urlParams.clear();
			this.urlParams.putAll(attributes);
			this.fireOnUrlParams = true;
		}
	}
	private String backwardsCompColonToUnderbar(String layout, String prefix) {
		if (SH.startsWith(layout, prefix) && SH.startsWith(layout, prefix + ":"))
			return prefix + "_" + SH.stripPrefix(layout, prefix + ":", true);
		return layout;

	}

	public AmiWebStyleManager getStyleManager() {
		return this.styleManager;
	}

	public Iterable<Tuple3<String, String, AmiWebAliasPortlet>> getUserPrefIds() {
		return this.amiUserPrefIdToAmiWebPortlet.entrySetMulti();
	}

	public AmiWebDebugManagerImpl getDebugManager() {
		return this.debugManager;
	}

	public PortletStyleManager_Dialog getUserDialogStyleManager() {
		return userDialogStyleManager;
	}

	public PortletStyleManager_Form getUserFormStyleManager() {
		return userFormStyleManager;
	}

	public AmiWebPortletManagerSecurityModel getSecurityModel() {
		return this.securityModel;
	}

	private String waitLineColor;
	private String waitBgColor;
	private String waitFillColor;
	private String waitSvg = null;

	public String getWaitSvg() {
		if (waitSvg == null) {
			waitSvg = HOUR_GLASS;
			waitSvg = SH.replaceAll(waitSvg, "$CLR_FL", this.waitFillColor);
			waitSvg = SH.replaceAll(waitSvg, "$CLR_LN", this.waitLineColor);
			waitSvg = SH.replaceAll(waitSvg, "$CLR_BG", this.waitBgColor);
		}
		return waitSvg;
	}
	public String getWaitLineColor() {
		return waitLineColor;
	}

	public void setWaitLineColor(String waitLineColor) {
		this.waitLineColor = waitLineColor;
		this.waitSvg = null;
	}

	public String getWaitBgColor() {
		return waitBgColor;
	}

	public void setWaitBgColor(String waitBgColor) {
		this.waitBgColor = waitBgColor;
		this.waitSvg = null;
	}

	public String getWaitFillColor() {
		return waitFillColor;
	}

	public void setWaitFillColor(String waitFillColor) {
		this.waitFillColor = waitFillColor;
		this.waitSvg = null;
	}

	//	public int getDefaultCmdTimeoutMs() {
	//		return this.defaultCmdTimeoutMs;
	//	}
	//
	//	public void setDefaultCmdTimeoutMs(int defaultCmdTimeoutMs) {
	//		this.defaultCmdTimeoutMs = defaultCmdTimeoutMs;
	//	}

	public AmiWebSystemObjectsManager getSystemObjectsManager() {
		return this.systemObjectsManager;
	}

	public AmiWebVarsManager getVarsManager() {
		return this.varsManager;
	}

	public AmiWebFormatterManager getFormatterManager() {
		return this.formatterManager;
	}

	public void registerFactory(AmiWebFormFieldFactory<?> factory) {
		CH.putOrThrow(fieldFactories, factory.getType(), factory);
	}
	public AmiWebFormFieldFactory<?> getFormFieldFactory(String type) {
		return CH.getOrThrow(fieldFactories, type);

	}
	public Collection<AmiWebFormFieldFactory<?>> getFieldFactories() {
		return this.fieldFactories.values();
	}
	public Collection<AmiWebFormFieldFactory<?>> getFieldFactoriesSorted() {
		Collection<AmiWebFormFieldFactory<?>> fieldFactoriesSorted = new ArrayList<AmiWebFormFieldFactory<?>>();
		for (String key : CH.sort(this.fieldFactories.keySet()))
			fieldFactoriesSorted.add(this.fieldFactories.get(key));
		return fieldFactoriesSorted;
	}

	private void initFormFieldFactories() {
		registerFactory(new AmiWebFormButtonFieldFactory());
		registerFactory(new AmiWebFormPasswordFieldFactory());
		registerFactory(new AmiWebFormCheckBoxFieldFactory());
		registerFactory(new AmiWebFormMultiCheckBoxFieldFactory());
		registerFactory(new AmiWebFormRadioButtonFieldFactory());
		registerFactory(new AmiWebFormDateFieldFactory());
		registerFactory(new AmiWebFormDateRangeFieldFactory());
		registerFactory(new AmiWebFormTimeFieldFactory());
		registerFactory(new AmiWebFormTimeRangeFieldFactory());
		registerFactory(new AmiWebFormDateTimeFieldFactory());
		registerFactory(new AmiWebFormDivFieldFactory());
		registerFactory(new AmiWebFormFileUploadFieldFactory());
		registerFactory(new AmiWebFormRangeFieldFactory());
		registerFactory(new AmiWebFormSubRangeFieldFactory());
		registerFactory(new AmiWebFormSelectFieldFactory());
		registerFactory(new AmiWebFormMultiSelectFieldFactory());
		registerFactory(new AmiWebFormTextFieldFactory());
		registerFactory(new AmiWebFormTextAreaFieldFactory());
		registerFactory(new AmiWebFormImageFieldFactory());
		registerFactory(new AmiWebFormColorPickerFieldFactory());
		registerFactory(new AmiWebFormColorGradientPickerFieldFactory());
	}

	@Override
	public void onFrontendCalled(PortletManager manager, Map<String, String> attributes, HttpRequestAction action) {
		AmiSsoSession ss = this.varsManager.getSsoSession();
		if (!ss.isAlive()) {
			this.desktop.logout();
			return;
		}
		if (this.fireOnUrlParams) {
			desktop.getCallbacks().execute("onUrlParams", new HashMap<String, String>(getPortletManager().getUrlParams()));
			this.fireOnUrlParams = false;
		}
		this.eventReaper.findAndReapExpiredEvents();
		if (this.customCssManager.hasPendingAjax())
			this.customCssManager.drainPendingAjax();
		this.desktop.onFrontendCalled();
		if (!this.timedEvents.isEmpty()) {
			long now = manager.getNow();
			int i = 0;
			for (; i < this.timedEvents.size(); i++) {
				AmiWebTimedEvent event = this.timedEvents.get(i);
				if (event.getTime() > now)
					break;
				try {
					event.execute(this);
				} catch (Throwable t) {
					LH.warning(log, manager.describeUser() + " ==> timed event at '" + new DateMillis(event.getTime()).toLegibleString() + "' failed: ", t);
					if (getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
						getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_EVENT_SCHEDULED, this.getAri(), null,
								"Event " + event.getId(), CH.m("Event Id", event.getId()), t));
				}
			}
			if (i > 0)
				CH.removeAll(this.timedEvents, 0, i);
		}
		this.getIdleSessionManager().onFrontendCalled(attributes);
		this.userSettingsManager.saveSettings();
		if (this.usagesChangedFlag) {
			this.usagesChangedFlag = false;
			AmiWebMethodsPortlet mp = this.getDesktop().getMethodPortlet();
			if (mp != null)
				mp.rebuildUsagesTree();
		}
	}

	@Override
	public void onBackendCalled(PortletManager manager, Action action) {
	}

	@Override
	public void onInit(PortletManager manager, Map<String, Object> configuration, String rootId) {
		this.agentManager.onInit(manager, configuration, rootId);
		processUserSettings();
	}

	public void processUserSettings() {
		this.varsManager.applyUserVariables();
		this.formatterManager.createFormatters();
	}

	@Override
	public void onBackendAction(Action action) {
		if (action instanceof AmiWebServiceAction) {
			onAmiWebServiceAction((AmiWebServiceAction) action);
		} else
			this.agentManager.onBackendAction(action);
	}

	private void onAmiWebServiceAction(AmiWebServiceAction action) {
		try {
			if (AmiWebServiceAction.ACTION_RELOAD_LAYOUT.equals(action.getAction()))
				this.layoutFilesManager.reloadLayout();
		} catch (Exception e) {
			getPortletManager().showAlert("Error for " + action.getAction() + ": " + e.getMessage(), e);
		}
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> action) {
		//		this.agentManager.onBackendResponse(action);
	}

	//	public AmiWebSnapshotManager getPrimarySnapshotManager() {
	//		return this.agentManager.getPrimarySnapshotManager();
	//	}

	public int getDefaultLimit() {
		return getScriptManager().getDefaultLimit();
	}

	//this is used to conflate unchanging realtime requests
	final private Map<String, Map<String, Object>> lastRequest = new HashMap<String, Map<String, Object>>();

	public Map<String, Object> getLastRealtimeRequest(String dmUid) {
		return lastRequest.get(dmUid);
	}

	public void putLastRealtimeRequest(String dmUid, HasherMap<String, Object> hasherMap) {
		if (hasherMap == null)
			lastRequest.remove(dmUid);
		else
			lastRequest.put(dmUid, new HashMap<String, Object>(hasherMap));
	}

	@Override
	public String toString() {
		return "session(" + this.getPortletManager().describeUser() + ")";
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SCM_PASSWORD".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				return initScm((String) source.getInputFieldValue());
			}
		}

		return true;
	}

	public AmiWebDataFilter getDataFilter() {
		return this.dataFilter;
	}

	public BPIPEPlugin getBPIPE() {
		return this.bpipe;
	}

	private List<AmiWebTimedEvent> timedEvents = new ArrayList<AmiWebTimedEvent>();
	private long nextEventId = 0;

	private AmiScmAdapter scmAdapter;

	public void addTimedEvent(long time, String layoutAlias, String script, DerivedCellCalculator calc) {
		addTimedEvent(time, new AmiWebTimedEvent_Calc(layoutAlias, script, calc));
	}

	public void addTimedEvent(long time, String layoutAlias, String script, int timeout, int limit, String defaultDs, DerivedCellCalculator calc) {
		addTimedEvent(time, new AmiWebTimedEvent_Calc(layoutAlias, script, timeout, limit, defaultDs, calc));
	}
	public void addTimedEvent(long time, AmiWebTimedEvent event) {
		if (time <= 0)
			time = getPortletManager().getNow() - time;
		event.init(time, nextEventId++);
		if (getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_INFO))
			getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_INFO, AmiDebugMessage.TYPE_EVENT_SCHEDULED, this.getAri(), null,
					"Event " + event.getId() + " Scheduled for " + DateMillis.format(time), CH.m("Script", event.describe(), "Event Id", event.getId(), "Time", event.getTime()),
					null));
		CH.insertSorted(timedEvents, event);
	}

	public List<Map<String, Object>> getUserPrefs() {
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		for (Tuple3<String, String, AmiWebAliasPortlet> t : this.getUserPrefIds()) {
			AmiWebAliasPortlet panel = t.getC();
			Map<String, Object> prefs = panel.getUserPref();
			if (CH.isEmpty(prefs))
				continue;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String alias = panel.getAmiLayoutFullAlias();
			if (SH.is(alias))
				map.put("layout", alias);
			map.put("upid", panel.getAmiUserPrefId());
			map.put("pref", prefs);
			r.add(map);
		}
		final Set<String> customPrefs = getVarsManager().getCustomPreferenceIds();
		if (!customPrefs.isEmpty()) {
			for (String id : customPrefs) {
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("cpid", id);
				map.put("pref", RootAssister.INSTANCE.clone(this.varsManager.getCustomPreference(id)));
				r.add(map);
			}
		}
		return r;
	}
	public void applyUserPrefs(List<Map<String, Object>> list) {
		for (Map<String, Object> map : list) {
			String upid = (String) map.get("upid");
			if (upid != null) {
				String layout = (String) CH.getOr(Caster_String.INSTANCE, map, "layout", "");
				Map<String, Object> pref = (Map<String, Object>) map.get("pref");
				AmiWebAliasPortlet panel = this.getPortletByUserPrefId(layout, upid);
				if (panel != null)
					panel.applyUserPref(pref);
			}
		}
	}
	public List<Map<String, Object>> getDefaultPrefs() {
		List<Map<String, Object>> r = new ArrayList<Map<String, Object>>();
		for (Tuple3<String, String, AmiWebAliasPortlet> t : this.getUserPrefIds()) {
			AmiWebAliasPortlet panel = t.getC();
			Map<String, Object> prefs = panel.getDefaultPref();
			if (CH.isEmpty(prefs))
				continue;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			String alias = panel.getAmiLayoutFullAlias();
			if (SH.is(alias))
				map.put("layout", alias);
			map.put("upid", panel.getAmiUserPrefId());
			map.put("pref", prefs);
			r.add(map);
		}
		return r;
	}

	public AmiWebChartImagesManager getChartImagesManager() {
		return chartImagesManager;
	}

	//	public DerivedCellTimeoutController getDefaultTimeoutController() {
	//		return this.scriptManager.getTimeoutController();
	//	}

	public boolean isDebug() {
		return this.isDebug;
	}

	public AmiWebCustomCssManager getCustomCssManager() {
		return this.customCssManager;
	}
	//	public static String getResourcesHTTPPath() {
	//		return "resources";
	//	}
	public AmiWebLayoutManager getLayoutManager() {
		return this.layoutManager;
	}

	//	@Override
	//	public String toDerivedString() {
	//		return toDerivedString(new StringBuilder()).toString();
	//	}
	//
	//	@Override
	//	public StringBuilder toDerivedString(StringBuilder sb) {
	//		return sb.append("session(userName=").append(getPortletManager().getUser().getUserName()).append(",sessionId=")
	//				.append(getPortletManager().getState().getWebState().getSession().getSessionId()).append(')');
	//	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	public Map<String, AmiScmPlugin> getScmPlugins() {
		return this.scmPlugins;
	}

	public AmiEncrypter getEncrypter() {
		return this.encrypter;
	}

	public void setScmAdapter(String path, AmiScmAdapter adapter) {
		this.scmBasePath = path;
		this.scmAdapter = adapter;
	}

	public AmiScmAdapter getScmAdapter() {
		return this.scmAdapter;
	}
	public String getScmBasePath() {
		return this.scmBasePath;
	}

	public AmiWebGuiServiceAdaptersManager getGuiServiceAdaptersManager() {
		return guiServiceAdaptersManager;
	}

	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		String subtype = CH.getOrThrow(attributes, "subtype");
		if ("guiservice".equals(subtype)) {
			this.guiServiceAdaptersManager.handleCallback(attributes, action);
		}
	}

	public AmiWebFontsManager getFontsManager() {
		return fontsManager;
	}

	@Override
	public MethodFactoryManager getMethodFactory() {
		return getScriptManager("").getMethodFactory();
	}

	@Override
	public SqlProcessor getSqlProcessor() {
		return getScriptManager("").getSqlProcessor();
	}
	//	@Override
	//	public com.f1.base.Types getGlobalVarTypes() {
	//		return EmptyMapping.INSTANCE;
	//	}
	//	@Override
	//	public Map<String, Object> getGlobalVars() {
	//	}
	//	@Override
	//	public DerivedCellTimeoutController getTimeoutController() {
	//		return this.scriptManager.getTimeoutController();
	//	}

	public AmiWebPortlet getPortletByPortletId(String portletId) {
		final Portlet r = this.manager.getPortletNoThrow(portletId);
		return r instanceof AmiWebPortlet ? (AmiWebPortlet) r : null;
	}
	public AmiWebPortlet getPortletByPortletIdOrThrow(String portletId) {
		return (AmiWebPortlet) this.manager.getPortlet(portletId);
	}

	public AmiWebLayoutFilesManager getLayoutFilesManager() {
		return layoutFilesManager;
	}

	public AmiWebAutosaveManager getAutosaveManager() {
		return autosaveManager;
	}

	public AmiWebPreferencesManager getPreferencesManager() {
		return preferencesManager;
	}

	public Set<String> getPanelIdsByFullAlias(String fullAlias) {
		Map<String, AmiWebAliasPortlet> t = this.amiPanelIdToAmiWebPortlet.get(fullAlias);
		return t == null ? Collections.EMPTY_SET : t.keySet();
	}

	public void fireAmiWebPanelAdded(AmiWebPortlet portlet) {
		if (!this.panelsListeners.isEmpty())
			for (int i = 0, l = this.panelsListeners.size(); i < l; i++) {
				try {
					this.panelsListeners.get(i).onAmiWebPanelAdded(portlet);
				} catch (Exception e) {
					LH.warning(log, manager.describeUser(), " ", e);
				}
			}
	}
	public void fireAmiWebPanelRemoved(AmiWebPortlet portlet, boolean isHide) {
		if (!this.panelsListeners.isEmpty())
			for (int i = 0, l = this.panelsListeners.size(); i < l; i++) {
				try {
					this.panelsListeners.get(i).onAmiWebPanelRemoved(portlet, isHide);
				} catch (Exception e) {
					LH.warning(log, manager.describeUser(), " ", e);
				}
			}
	}

	public void addAmiWebPanelsListener(AmiWebPanelsListener listener) {
		try {
			CH.addIdentityOrThrow(this.panelsListeners, listener);
			if (LH.isFine(log))
				LH.fine(log, "Service Add Panels Listener: ", SH.toObjectStringSimple(listener), " Count: ", this.panelsListeners.size());
		} catch (Exception e) {
			LH.warning(log, this.getUserName() + " Error adding listener: " + listener, e);
		}
	}
	public void removeAmiWebPanelsListener(AmiWebPanelsListener listener) {
		try {
			CH.removeOrThrow(this.panelsListeners, listener);
			if (LH.isFine(log))
				LH.fine(log, "Service Remove Panels Listener: ", SH.toObjectStringSimple(listener), " Count: ", this.panelsListeners.size());
		} catch (Exception e) {
			LH.warning(log, this.getUserName() + " Error removing listener: " + listener, e);
		}
	}

	public void fireAmiWebPanelLocationChanged(AmiWebPortlet ap) {
		if (!this.panelsListeners.isEmpty())
			for (int i = 0, l = this.panelsListeners.size(); i < l; i++) {
				try {
					this.panelsListeners.get(i).onAmiWebPanelLocationChanged(ap);
				} catch (Exception e) {
					LH.warning(log, manager.describeUser(), " ", e);
				}
			}
	}
	public void fireAmiWebPanelIdChanged(AmiWebPortlet portlet, String oldAdn, String newAdn) {
		if (!this.panelsListeners.isEmpty())
			for (int i = 0, l = this.panelsListeners.size(); i < l; i++) {
				try {
					this.panelsListeners.get(i).onAmiWebPanelIdChanged(portlet, oldAdn, newAdn);
				} catch (Exception e) {
					LH.warning(log, manager.describeUser(), " ", e);
				}
			}
	}
	public String formatPortletBuilderIdToPanelType(String builderId) {
		return this.getPortletManager().getPortletBuilder(builderId).getPortletBuilderName();
	}
	public AmiWebDevTools getDevTools() {
		return this.devtools;
	}

	public void recompileAmiScript() {
		//I believe this is duplicative because the domObjectsManager already contains the panels and layout callbacks
		//so these callbacks are jit down below.
		//		for (AmiWebAliasPortlet i : amiPanelAliasDotIdToAmiWebPortlet.values())
		//			i.recompileAmipscript();
		//		for (String s : this.layoutFilesManager.getFullAliasesByPriority())
		//			getScriptManager(s).getLayoutCallbacks().recompileAmiscript();
		//		this.dmManager.recompileAmiscript();
		//		this.desktop.getInnerDesktop().getCustomContextMenu().recompileAmiscript();
		AmiWebMethodsPortlet sp = this.desktop.getSpecialPortlet(AmiWebMethodsPortlet.class);
		if (sp != null)
			sp.recompileAmiScript();
		this.amiWebViewMethodsManager.recompileAmiScript();
		this.getGuiServiceAdaptersManager().recompileAmiscript();
		for (AmiWebDomObject i : CH.l(this.domObjectsManager.getManagedDomObject())) {
			AmiWebAmiScriptCallbacks cb = i.getAmiScriptCallbacks();
			if (cb != null)
				cb.recompileAmiscript();
			AmiWebFormulas fs = i.getFormulas();
			if (fs != null)
				fs.recompileAmiscript();
		}
		fireOnRecompiled();
	}

	private String ari = AmiWebDomObject.ARI_TYPE_SESSION + ":" + "session";

	@Override
	public String getAri() {
		return ari;
	}
	@Override
	public void updateAri() {
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_SESSION;
	}

	@Override
	public String getDomLabel() {
		return "session";
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return "";
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return getDomLabel();
	}

	@Override
	public AmiWebService getService() {
		return this;
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = new ArrayList<AmiWebDomObject>();
		Iterable<AmiWebLayoutFile> childrenRecursive = this.getLayoutFilesManager().getLayout().getChildrenRecursive(true);
		CH.addAll(r, childrenRecursive);
		r.addAll(this.getDesktop().getInnerDesktop().getCustomContextMenu().getChildren(false));
		return r;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return null;
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebService.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	public AmiWebQueryFormEditorsManager getAmiQueryFormEditorsManager() {
		return this.amiWebQueryFormEditorsManager;
	}

	public AmiWebDmEditorsManager getAmiWebDmEditorsManager() {
		return this.amiWebDmEditorsManager;
	}
	public AmiWebCallbackEditorsManager getAmiWebCallbackEditorsManager() {
		return this.amiWebCallbackEditorsManager;
	}
	public AmiWebCustomContextMenuEditorsManager getAmiWebCustomContextMenuEditorsManager() {
		return this.amiWebCustomContextMenuEditorsManager;
	}
	public AmiWebViewMethodsManager getAmiWebViewMethodsManager() {
		return this.amiWebViewMethodsManager;
	}

	public int getEditorsCount() {
		int r = this.amiWebQueryFormEditorsManager.getEditorsCount();
		r += this.amiWebDmEditorsManager.getEditorsCount();
		r += this.amiWebCallbackEditorsManager.getEditorsCount();
		r += this.amiWebCustomContextMenuEditorsManager.getEditorsCount();
		r += this.amiWebViewMethodsManager.getEditorsCount();
		return r;
	}

	public AmiWebPanelManager getAmiPanelManager() {
		return amiPanelManager;
	}

	public AmiWebDomObjectsManager getDomObjectsManager() {
		return domObjectsManager;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	public AmiCenterDefinition[] getCenterIds() {
		return centerDefinitions;
	}

	public void sendRequestToBackend(String portletId, AmiCenterRequest request) {
		this.agentManager.getPrimarySnapshotManager().sendRequestToBackend(portletId, request, 10);
	}

	public void sendRequestToBackend(BackendResponseListener listener, AmiCenterRequest request) {
		this.agentManager.getPrimarySnapshotManager().sendRequestToBackend(listener, request, 10);
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}
	private static boolean hasIds(long[] ids) {
		for (long l : ids)
			if (l > 0)
				return true;
		return false;
	}

	public AmiWebStatsManager getWebStatsManager() {
		return this.webStatsManager;
	}

	private long nextObjectUid = -1;

	private CalcFrame thisConstFrame = new SingletonCalcFrame("this", getClass(), this);

	private TopCalcFrameStack stackFrame;

	private boolean usagesChangedFlag = false;

	public long getNextAmiObjectUId() {
		return nextObjectUid--;
	}

	@Override
	public void addToDomManager() {
	}
	@Override
	public void removeFromDomManager() {
	}

	public void onAmiScriptEditorClosed(AmiWebEditAmiScriptCallbacksPortlet editor) {
		amiscriptEditors.remove(editor.getPortletId());
		for (AmiWebEditAmiScriptCallbackPortlet i : editor.getEditors())
			this.breakpointManager.onEditorClosed(i);
	}
	public void onAmiScriptEditorOpened(AmiWebEditAmiScriptCallbacksPortlet editor) {
		amiscriptEditors.put(editor.getPortletId(), editor);
	}

	public int getAmiScriptEditorsCount() {
		return this.amiscriptEditors.size();
	}

	public AmiWebEditAmiScriptCallbackPortlet findEditor(AmiWebDomObject thiz, String name) {
		for (AmiWebEditAmiScriptCallbacksPortlet i : this.amiscriptEditors.values())
			if (i.getCallbacks() != null && thiz == i.getCallbacks().getThis())
				return i.getCallbackEditor(name);
		return null;

	}

	public AmiWebBreakpointManager getBreakpointManager() {
		return this.breakpointManager;
	}

	public AmiWebUserFilesManager getUserFilesManager() {
		return this.userFilesManager;
	}

	public int getDefaultTimeoutMs() {
		return this.scriptManager.getAmiScriptTimeout();
	}

	public AmiWebIdleSessionManager getIdleSessionManager() {
		return this.idleSessionManager;
	}

	public AmiWebScriptManagerForLayout getScriptManager(String layoutAlias) {
		return this.scriptManager.getLayout(layoutAlias);
	}

	public AmiWebGraphManager getGraphManager() {
		return this.graphManager;
	}

	public List<String> getShellHistory() {
		return shellHistory;
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
	}

	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, Callback cb) {
		if (OH.eq(id, "open_ari")) {
			int cursorpos = (Integer) cb.getAttribute("cursorpos");
			String t = (String) cb.getAttribute("ari");
			if (t.startsWith("CUSTOM_METHODS:")) {
				String layout = SH.afterFirst(t, ":");
				AmiWebMethodsPortlet cmp = this.desktop.showCustomMethodsPortlet();
				AmiWebMethodPortlet t2 = cmp.getMethodPortlet(layout);
				PortletHelper.ensureVisible(t2);
				t2.setCursorPosition(cursorpos);
				return;
			}
			String ari = SH.beforeFirst(t, AmiWebConsts.CALLBACK_PREFIX_DELIM);
			String callback = SH.afterFirst(t, AmiWebConsts.CALLBACK_PREFIX_DELIM);
			//			getPortletManager().showAlert(cb.getAttributes().toString());
			AmiWebEditAmiScriptCallbackPortlet editor = this.getDomObjectsManager().showCallbackEditor(ari, callback);
			if (editor != null)
				editor.setCursorPosition(cursorpos);
		}
	}

	@Override
	public void onHtmlChanged(String old, String nuw) {
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	public void fireOnCallbackChanged(AmiWebAmiScriptCallback callback, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		for (AmiWebCompilerListener listener : this.compilerListeners)
			listener.onCallbackChanged(callback, old, nuw);
		onMethodUsagesChanged();
	}
	public void fireOnFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		for (AmiWebCompilerListener listener : this.compilerListeners)
			listener.onFormulaChanged(formula, old, nuw);
		onMethodUsagesChanged();
	}
	private void fireOnRecompiled() {
		for (AmiWebCompilerListener listener : this.compilerListeners)
			listener.onRecompiled();
		onMethodUsagesChanged();
	}

	public void addCompilerListener(AmiWebCompilerListener listener) {
		this.compilerListeners = AH.append(this.compilerListeners, listener);
	}
	public void removeCompilerListener(AmiWebCompilerListener listener) {
		this.compilerListeners = AH.remove(this.compilerListeners, listener);
	}

	public CharSequence cleanHtml(CharSequence html) {
		if (this.allowJavascriptEmbeddedInHtml || SH.isnt(html))
			return html;
		try {
			//			return WebHelper.escapeHtml(html);
			return AmiWebHtmlJavascriptCleaner.clean(html, true);
		} catch (Exception e) {
			LH.warning(log, manager.describeUser(), " ==> Error Cleaning HTML: ", html, e);
			return "ERROR CLEANING HTML";
		}
	}
	public String cleanHtml(String html) {
		if (this.allowJavascriptEmbeddedInHtml || SH.isnt(html))
			return html;
		try {
			//			return WebHelper.escapeHtml(html);
			return AmiWebHtmlJavascriptCleaner.clean(html, true);
		} catch (Exception e) {
			LH.warning(log, manager.describeUser(), " ==> Error Cleaning HTML: ", html, e);
			return "ERROR CLEANING HTML";
		}
	}

	public SimpleEventReaper getEventReaper() {
		return eventReaper;
	}

	public boolean isHeadlessSession() {
		return getPortletManager().getState() instanceof AmiWebHeadlessWebState;
	}

	public String getUrl() {
		return SH.beforeFirst(getPageUrl(), "?");
	}

	public AmiWebState getWebState() {
		return (AmiWebState) this.getPortletManager().getState();
	}

	public String getSessionLabel() {
		return sessionLabel;
	}

	public void setSessionLabel(String sessionLabel) {
		this.getWebState().setLabel(sessionLabel);
		this.sessionLabel = sessionLabel;
	}

	public AmiWebResourcesManager getResourcesManager() {
		return this.resourcesManager;
	}

	public ReusableCalcFrameStack createStackFrameReusable(AmiWebDomObject obj) {
		AmiWebScriptManagerForLayout layout = this.scriptManager.getLayout(obj.getAmiLayoutFullAlias());
		AmiWebTopCalcFrameStack r = new AmiWebTopCalcFrameStack(new TablesetImpl(), getDefaultLimit(), null, null, null, layout.getMethodFactory(), EmptyCalcFrame.INSTANCE,
				thisConstFrame, null, this.getService(), null, AmiDebugMessage.TYPE_FORMULA, obj, obj.getAmiLayoutFullAlias(), layout.getConstsMap(obj));
		return new ReusableCalcFrameStack(r);
	}
	public TopCalcFrameStack createStackFrame(AmiWebDomObject obj) {
		AmiWebScriptManagerForLayout layout = this.scriptManager.getLayout(obj.getAmiLayoutFullAlias());
		return new AmiWebTopCalcFrameStack(new TablesetImpl(), getDefaultLimit(), new DerivedCellTimeoutController(getDefaultTimeoutMs()), null, this.breakpointManager,
				layout.getMethodFactory(), EmptyCalcFrame.INSTANCE, thisConstFrame, null, this.getService(), null, AmiDebugMessage.TYPE_FORMULA, obj, obj.getAmiLayoutFullAlias(),
				layout.getConstsMap(obj));
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

	public void onMethodUsagesChanged() {
		if (!desktop.getIsLocked())
			this.usagesChangedFlag = true;
	}

	@Override
	public String getformatDate(String format, long time, String timezone) {
		return this.formatterManager.getformatDate(format, time, timezone);
	}
	@Override
	public String getformatDate(String format, DateMillis time, String timezone) {
		return this.formatterManager.getformatDate(format, time, timezone);
	}

	@Override
	public String getformatDate(String format, DateNanos time, String timezone) {
		return this.formatterManager.getformatDate(format, time, timezone);
	}

	public TimeZone getTimezone() {
		return this.getVarsManager().getTimeZone();
	}
	public String getTimezoneId() {
		return this.getVarsManager().getTimeZone().getID();
	}

	@Override
	public String getformatDate(String format, long time, CalcFrameStack sf) {
		AmiWebService service = (AmiWebService) AmiUtils.getExecuteInstance2(sf).getService();
		return getformatDate(format, time, service.getTimezoneId());
	}

	@Override
	public String getformatDate(String format, DateMillis time, CalcFrameStack sf) {
		AmiWebService service = (AmiWebService) AmiUtils.getExecuteInstance2(sf).getService();
		return getformatDate(format, time, service.getTimezoneId());
	}

	@Override
	public String getformatDate(String format, DateNanos time, CalcFrameStack sf) {
		AmiWebService service = (AmiWebService) AmiUtils.getExecuteInstance2(sf).getService();
		return getformatDate(format, time, service.getTimezoneId());
	}

	public void setUrlParams(LinkedHashMap nuw) {
		this.urlParams.clear();
		this.urlParams.putAll(nuw);
	}

	public byte getActivityLogLevel() {
		return activityLogLevel;
	}

	public void setActivityLogLevel(byte activityLogLevel) {
		this.activityLogLevel = activityLogLevel;
	}

	public void createBloombergSession() {
		if (!SH.equals(manager.getTools().getOptional("bpipe_plugin_enabled"), "true")) {
			this.bpipe = null;
			return;
		} else {
			HttpRequestResponse r = this.getPortletManager().getLastRequestAction();
			this.bpipe = new BPIPEPlugin(this, r);
			if (this.bpipe.onLogin() == false) {
				LH.severe(log, "FAILED TO START BPIPE SESSION");
				this.bpipe = null;
			}
		}
	}

	private void startBpipeLogoutThread() {
		Thread bpipeLogoutThread = new Thread(new Runnable() {
			@Override
			public void run() {
				bpipe.onLogout();
			}
		});
		bpipeLogoutThread.start();
	}
}
