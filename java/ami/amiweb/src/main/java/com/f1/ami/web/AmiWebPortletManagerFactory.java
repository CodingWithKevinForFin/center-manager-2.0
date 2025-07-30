package com.f1.ami.web;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.AmiEncrypter;
import com.f1.ami.amicommon.AmiScmPlugin;
import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.amicommon.centerclient.AmiCenterClientStats;
import com.f1.ami.amicommon.customobjects.AmiScriptClassPluginWrapper;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.ami.web.amiscript.AmiWebFileSystem_Local;
import com.f1.ami.web.charts.AmiWebChartGridPortlet;
import com.f1.ami.web.cloud.AmiWebBasicCloudManager;
import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.ami.web.datafilter.AmiWebDataFilterPlugin;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.realtimetree.AmiWebRealtimeTreePortlet;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.ami.web.tree.AmiWebStaticTreePortlet;
import com.f1.ami.web.userpref.AmiWebUserPreferencesPlugin;
import com.f1.container.ContainerTools;
import com.f1.http.HttpRequestResponse;
import com.f1.suite.web.PortalHttpStateCreator;
import com.f1.suite.web.PortletManagerFactory;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.utils.LH;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.SH;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebPortletManagerFactory extends PortletManagerFactory {

	private static final String DEFAULT_CLOUD_DIRECTORY = "data/cloud";
	//	private static final String DEFAULT_USERS_FILE = "data/users.properties";
	private static final Logger log = LH.get();
	final private TreeMap<String, AmiWebPanelPluginWrapper> plugins;
	private AmiWebDataFilterPlugin dataFilterPlugin;
	private AmiCenterDefinition[] centerDefinitions;
	final private AmiWebManagerClient amiWebManagerClient;
	private PortalHttpStateCreator creator;

	public AmiWebPortletManagerFactory(ContainerTools tools, AmiWebManagerClient wmc, LocaleFormatter f) {
		super(tools, AmiStartup.getBuildProperty("version"), f);
		this.plugins = new TreeMap<String, AmiWebPanelPluginWrapper>();
		this.amiWebManagerClient = wmc;
		init();
	}
	public void addPlugins(Iterable<AmiWebPanelPlugin> plugins) {
		String amiplugins = "Ami Plugins";
		for (AmiWebPanelPlugin s : plugins) {
			final AmiWebPanelPluginWrapper t = new AmiWebPanelPluginWrapper(s);
			t.setPath(amiplugins);
			this.plugins.put(s.getPluginId(), t);
		}
	}

	@Override
	public void applyServices(PortletManager manager) {
		String path = getTools().getOptional(AmiWebProperties.PROPERTY_USERS_PATH, "data/users");
		AmiWebFileSystem fs = createFileSystem();
		AmiWebCloudManager cloud = null;

		try {
			cloud = new AmiWebBasicCloudManager(this.cloudDirectory, fs);
		} catch (IOException e) {
			LH.warning(log, "Error initializing cloud manager for directory: ", this.cloudDirectory);
		}

		AmiWebService amiService = new AmiWebService(manager, cloud, this.amiCustomClassPlugins, this.plugins, this.amiscriptProperties, this.dataFilterPlugin,
				this.userPreferencesPlugin, this.scmPlugins, encrypter, this.guiServicePlugins.values(), this.realtimeProcessorPlugins, this.fontsManager, centerDefinitions,
				webStats, fs, getResourcesRoot(fs), path);
		amiService.processUserSettings();
		manager.setSecurityModel(amiService.getSecurityModel());
		manager.registerService(amiService);
	}
	public AmiWebFileSystem createFileSystem() {
		return this.amiWebManagerClient == null ? new AmiWebFileSystem_Local() : new AmiWebFileSystem_Remote(this.amiWebManagerClient);
	}
	public AmiWebFile getResourcesRoot(AmiWebFileSystem fs) {
		String dir = this.getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_RESOURCES_DIR, "web_resources");
		return fs.getFile(dir);
	}

	//	private SsoPortletManagerFactory ssoHandler;
	private double configFramesPerSecond;
	private boolean configIsSDebug;
	private String cloudDirectory;
	//	private String usersFile;
	private Map<String, AmiScriptClassPluginWrapper> amiCustomClassPlugins;
	private MutableCalcFrame amiscriptProperties;
	private AmiWebUserPreferencesPlugin userPreferencesPlugin;
	private Map<String, AmiScmPlugin> scmPlugins;
	private AmiEncrypter encrypter;
	private Map<String, AmiWebGuiServicePlugin> guiServicePlugins;
	private AmiWebFontsManager fontsManager;
	private AmiCenterClientStats webStats;
	private Map<String, AmiWebRealtimeProcessorPlugin> realtimeProcessorPlugins;

	private long ajaxLoadingTimeoutMs = BasicPortletManager.DEFAULT_AJAX_LOADING_TIMEOUT_MS;
	private String portalDialogHeaderTitle = BasicPortletManager.DEFAULT_PORTAL_DIALOG_HEADER_TITLE;

	@Override
	public void init() {
		super.init();
		cloudDirectory = getTools().getOptional(AmiWebProperties.PROPERTY_AMI_CLOUD_DIR, DEFAULT_CLOUD_DIRECTORY);
		//		usersFile = getTools().getOptional(AmiWebProperties.PROPERTY_AMI_USERS_FILE, DEFAULT_USERS_FILE);
		//		LH.info(log, "Cloud directory: " + IOH.getFullPath(cloudDirectory));
		//		LH.info(log, "User file: " + IOH.getFullPath(usersFile));
		this.configFramesPerSecond = getTools().getOptional(AmiWebProperties.PROPERTY_AMI_FRAMES_PER_SECOND, 15d);
		this.configIsSDebug = getTools().getOptional(AmiWebProperties.PROPERTY_AMI_DEBUG, false);
		this.ajaxLoadingTimeoutMs = SH.parseDurationTo(
				getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_SHOW_WAIT_ICON_AFTER_DURATION, BasicPortletManager.DEFAULT_AJAX_LOADING_TIMEOUT_MS + " milliseconds"),
				TimeUnit.MILLISECONDS);
		this.portalDialogHeaderTitle = getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_PORTAL_DIALOG_HEADER_TITLE, BasicPortletManager.DEFAULT_PORTAL_DIALOG_HEADER_TITLE);
	}
	public void initCloudDirectory(AmiWebFileSystem fileSystem) {
		if (cloudDirectory != null) {
			try {
				fileSystem.getFile(cloudDirectory).mkdirForce();
			} catch (IOException e) {
				LH.warning(log, "Could not create cloud dir: " + cloudDirectory, e);
			}
		}
	}

	@Override
	public void applyBuilders(PortletManager portletManager) {
		super.applyBuilders(portletManager);
		String amipath = "Ami Applications";
		portletManager.addPortletBuilder(new AmiWebObjectTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebObjectTablePortlet.Builder.OLD_ID, new AmiWebObjectTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebAggregateObjectTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebAggregateObjectTablePortlet.Builder.OLD_ID, new AmiWebAggregateObjectTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebDatasourceTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebDatasourceTablePortlet.Builder.OLD_ID, new AmiWebDatasourceTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebDatasourceTablePortlet.Builder.OLD_ID2, new AmiWebDatasourceTablePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebInnerDesktopPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(DesktopPortlet.Builder.ID, new AmiWebInnerDesktopPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebBlankPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebChartGridPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebChartGridPortlet.Builder.OLD_ID, new AmiWebChartGridPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebSurfacePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebQueryFormPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebQueryFormPortlet.Builder.OLD_ID, new AmiWebQueryFormPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebStaticTreePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebTreemapStaticPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebTreemapStaticPortlet.Builder.OLD_ID, new AmiWebTreemapStaticPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebTreemapRealtimePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebTreemapRealtimePortlet.Builder.OLD_ID, new AmiWebTreemapRealtimePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebRealtimeTreePortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebDataViewPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebTabPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebUploadDataPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder("hdiv", new AmiWebDividerPortlet.HBuilder().setPath(amipath));
		portletManager.addPortletBuilder("vdiv", new AmiWebDividerPortlet.VBuilder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebDividerPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebFilterPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(AmiWebFilterPortlet.Builder.OLD_ID, new AmiWebFilterPortlet.Builder().setPath(amipath));
		portletManager.addPortletBuilder(new AmiWebScrollPortlet.Builder().setPath(amipath));
		//		if (configIsSDebug)
		//			portletManager.addPortletBuilder(new AmiWebDebugPortlet.Builder().setPath(amipath));
		for (AmiWebPanelPluginWrapper s : plugins.values()) {
			portletManager.addPortletBuilder(s);
		}
		//		if (ssoHandler != null)
		//			ssoHandler.applyBuilders(portletManager);
	}
	@Override
	public BasicPortletManager createPortletManager(HttpRequestResponse request, WebState state) {
		BasicPortletManager r = super.createPortletManager(request, state);
		r.setPollingMs((int) (1000 / configFramesPerSecond));
		r.setAjaxLoadingCheckPeriodMs(this.ajaxLoadingTimeoutMs / 10);
		r.setAjaxLoadingTimeoutMs(this.ajaxLoadingTimeoutMs);
		r.setPortalDialogHeaderTitle(this.portalDialogHeaderTitle);

		WebStatesManager webState = state.getWebStatesManager();
		final String user = state.getUserName();
		if (webState != null) {
			long login = webState.getLoginTime();
			long timeout = webState.getSession().getTimeout();
			String remoteAddress = webState.getRemoteAddress();
			Object uid = webState.getSession().getSessionId();
			LH.info(log, "Created Portlet Manager for ", user + " from ", remoteAddress, " with uid ", uid, ". pollingDelayMs=", r.getPollingMs(), ", login time: ", login,
					", timeout: ", timeout);
		} else {
			LH.info(log, "Created HEADLESS Portlet Manager for ", user, ". pollingDelayMs=", r.getPollingMs());
		}

		return r;
	}
	@Override
	public void applyDefaultLayout(PortletManager portletManager, Map<String, String> urlParams) {
		if (portletManager.getRoot().getChildren().isEmpty()) {
			AmiWebDesktopPortlet dt = new AmiWebDesktopPortlet(portletManager.generateConfig(), urlParams);
			portletManager.getRoot().addChild(dt);
			portletManager.onPortletAdded(dt);
		}
	}
	public void setAmiScriptCustomClasslugins(Map<String, AmiScriptClassPluginWrapper> plugins) {
		this.amiCustomClassPlugins = plugins;
	}

	//	public SsoPortletManagerFactory getSsoHandler() {
	//		return ssoHandler;
	//	}
	//
	//	public void setSsoHandler(SsoPortletManagerFactory ssoHandler) {
	//		this.ssoHandler = ssoHandler;
	//	}
	public void setAmiScriptVariables(MutableCalcFrame amiScriptProperties2) {
		this.amiscriptProperties = amiScriptProperties2;

	}
	public void setDataFilterPlugin(AmiWebDataFilterPlugin dataFilterPlugin) {
		this.dataFilterPlugin = dataFilterPlugin;
	}
	public void setUserPreferencesPlugin(AmiWebUserPreferencesPlugin upp) {
		this.userPreferencesPlugin = upp;
	}
	public void setScmPlugins(Map<String, AmiScmPlugin> amiScmPlugins) {
		this.scmPlugins = amiScmPlugins;
	}
	public void setGuiServicePlugins(Map<String, AmiWebGuiServicePlugin> guisServicePlugins) {
		this.guiServicePlugins = guisServicePlugins;
	}
	public void setEncrypter(AmiEncrypter encrypter) {
		this.encrypter = encrypter;
	}
	public void setFontManager(AmiWebFontsManager fonts) {
		this.fontsManager = fonts;

	}
	public void setWebStats(AmiCenterClientStats webStats) {
		this.webStats = webStats;
	}
	public void setAmiRealtimeProcessorPlugins(Map<String, AmiWebRealtimeProcessorPlugin> rtProcessorPlugins) {
		this.realtimeProcessorPlugins = rtProcessorPlugins;
	}
	public PortalHttpStateCreator getCreator() {
		return this.creator;
	}
	public void setCenterDefinitions(AmiCenterDefinition[] centerDefinitions) {
		this.centerDefinitions = centerDefinitions;
	}
	public void setCreator(PortalHttpStateCreator creator) {
		this.creator = creator;
	}
}
