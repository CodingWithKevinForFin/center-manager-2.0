package com.f1.ami.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.amiscript.AmiDebugMessageListener;
import com.f1.ami.web.AmiWebAutosaveManager.AutoSaveFile;
import com.f1.ami.web.auth.AmiWebStatesManager;
import com.f1.ami.web.charts.AmiWebChartAxisPortlet;
import com.f1.ami.web.charts.AmiWebChartPlotPortlet;
import com.f1.ami.web.charts.AmiWebManagedPortlet;
import com.f1.ami.web.cloud.AmiWebCloudLayoutTree;
import com.f1.ami.web.diff.AmiWebJsonDifferPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManagerImpl;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebAddPanelPortlet;
import com.f1.ami.web.dm.portlets.AmiWebAddRealtimePanelPortlet;
import com.f1.ami.web.dm.portlets.AmiWebAddVisualizationPortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDatasourceTilesPortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmTreePortlet;
import com.f1.ami.web.filter.AmiWebFilterWizPortlet;
import com.f1.ami.web.form.AmiWebQueryFieldWizardPortlet;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.ami.web.headless.AmiWebHeadlessManager;
import com.f1.ami.web.headless.AmiWebHeadlessSession;
import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.ami.web.menu.AmiWebCustomContextMenuSettingsPortlet;
import com.f1.ami.web.pages.AmiWebPages;
import com.f1.ami.web.scm.AmiWebFileBrowserPortlet;
import com.f1.ami.web.scm.AmiWebFileBrowserPortletListener;
import com.f1.ami.web.scm.AmiWebScmBrowserPortlet;
import com.f1.ami.web.scm.AmiWebScmSettingsPortlet;
import com.f1.ami.web.style.AmiWebNewLayoutStylePortlet;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyleManagerPortlet;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Global;
import com.f1.container.ContainerTools;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.WebMenuLinkListener;
import com.f1.suite.web.menu.impl.AbstractWebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.BasicPortletDownload;
import com.f1.suite.web.portal.ColorUsingPortlet;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletBuilder;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.BasicPortletConfig;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.suite.web.portal.impl.BasicPortletMenuManager;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DesktopManager;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.PopupWindowListener;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.DesktopPortletListener;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.DropDownMenuPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet.Callback;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.RootPortletDialogListener;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.WebDropDownMenuFactory;
import com.f1.suite.web.portal.impl.WebDropDownMenuListener;
import com.f1.suite.web.portal.impl.WebHtmlContextMenuListener;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletFileUploadField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.WebAbsoluteLocation;
import com.f1.suite.web.portal.impl.form.WizardPortlet;
import com.f1.suite.web.portal.style.PortletStyleManager;
import com.f1.suite.web.portal.style.PortletStyleManager_Dialog;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.suite.web.portal.style.PortletStyleManager_Menu;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.EH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.json.JsonBuilder;

public class AmiWebDesktopPortlet extends GridPortlet implements WebDropDownMenuListener, WebDropDownMenuFactory, ConfirmDialogListener, ColorPickerListener,
		WebHtmlContextMenuListener, PortletListener, WebMenuListener, DesktopPortletListener, AmiWebCenterListener, DesktopManager, ColorUsingPortlet, RootPortletDialogListener,
		AmiWebStyledPortlet, AmiDebugMessageListener, AmiWebFileBrowserPortletListener, HtmlPortletListener {

	private static final int DEFAULT_HEIGHT = 700;
	private static final int DEFAULT_WIDTH = 1250;
	public static final int MAX_HEIGHT = 1392;
	public static final int MAX_WIDTH = 1856;
	public static final int MIN_HEIGHT = 500;
	public static final int MIN_WIDTH = 800;

	private static final Logger log = LH.get();

	private static final int DEFAULT_MAX_DEBUG_MESSAGES = 100;
	public static final byte MENUBAR_TOP = 1;
	public static final byte MENUBAR_TOP_LEFT = 2;
	public static final byte MENUBAR_TOP_CENTER = 3;
	public static final byte MENUBAR_TOP_RIGHT = 4;
	public static final byte MENUBAR_BOTTOM = 5;
	public static final byte MENUBAR_BOTTOM_LEFT = 6;
	public static final byte MENUBAR_BOTTOM_CENTER = 7;
	public static final byte MENUBAR_BOTTOM_RIGHT = 8;

	private String portletClipboard = null;
	private AmiWebInnerDesktopPortlet desktop;
	private boolean inEditMode = false;
	final private GridPortlet desktopContainer;
	final private boolean simulatorEnabled;
	final private AmiWebService service;
	final private boolean isLocked;
	final private AmiWebDesktopLinkHelper linkHelper;
	//	final private HtmlPortlet dashboardDivider;
	private byte menuBarPosition = MENUBAR_BOTTOM;
	private boolean isLicenseDisabled;
	//	private String titleBarBorderColor;
	private String currentPortletId;
	private String helpBgColor;
	private String helpFontColor;
	private boolean isPopoutEnabled = true;
	private String debugInfoSetting = AmiWebConsts.DEBUG_ONLY;
	private String debugWarningSetting = AmiWebConsts.DEBUG_ONLY;
	private Map<Class<? extends AmiWebSpecialPortlet>, AmiWebSpecialPortlet> specialPortlets = new HashMap<Class<? extends AmiWebSpecialPortlet>, AmiWebSpecialPortlet>();

	public <T extends AmiWebSpecialPortlet> T getSpecialPortlet(Class<T> clazz) {
		AmiWebSpecialPortlet r = specialPortlets.get(clazz);
		return r == null ? null : clazz.cast(r);
	}

	private void setSpecialPortlet(AmiWebSpecialPortlet t) {
		if (t != null)
			specialPortlets.put(t.getClass(), t);
	}

	private AmiWebStyledPortletPeer stylePeer;

	private String usrWinCl;
	private String usrWinUpCl;
	private String usrWinDownCl;
	private String usrWinTxtCl;
	private String usrWinBtnCl;
	private String usrWinBtnUpCl;
	private String usrWinBtnDownCl;
	private String usrWinBtnIconCl;
	private Integer usrWinHeaderSz;
	private Integer usrWinBdrSz;
	private String usrWinFormBgCl;
	private String usrWinFormBtnPanelCl;
	private Integer usrWinInnerBdrSz;
	private Integer usrWinOuterBdrSz;
	private String deskBgCl;
	final private AmiWebDesktopBar desktopBar;
	final private AmiWebLayoutManager layoutManager;
	final private BasicPortletMenuManager menuManager;

	final private AmiWebVarsManager varsManager;

	private boolean askAboutRestoreUnsavedWork;

	private static final String WINDOW_ATTRIBUTE_MENU_LOCATION = "mloc";
	private final PortletStyleManager_Menu menuStyle = new PortletStyleManager_Menu();

	private HtmlPortlet logo;

	public AmiWebDesktopPortlet(PortletConfig manager, Map<String, String> params) {
		super(manager);
		this.service = (AmiWebService) getManager().getService(AmiWebService.ID);
		this.service.setDesktop(this);
		this.layoutManager = service.getLayoutManager();
		this.menuManager = (BasicPortletMenuManager) manager.getPortletManager().getMenuManager();
		this.linkHelper = new AmiWebDesktopLinkHelper(this, "__amid_");
		this.varsManager = this.service.getVarsManager();
		onUserRequestFocus(null);

		this.desktopContainer = new GridPortlet(generateConfig());
		//		this.dashboardDivider = new HtmlPortlet(generateConfig(), "", "ami_dashboard_divider");

		isLocked = !this.varsManager.isUserAdmin() && !this.varsManager.isUserDev();

		this.desktopBar = new AmiWebDesktopBar(this, generateConfig());
		this.logo = new HtmlPortlet(generateConfig());
		this.logo.setCssClass("amiweblogo");
		this.getManager().onPortletAdded(this.logo);
		this.getManager().onPortletAdded(this.desktopBar);
		this.logo.addListener(this);
		ContainerTools tools = this.getManager().getTools();
		this.simulatorEnabled = tools.getOptional(AmiWebProperties.PROPERTY_AMI_SIMULATOR_ENABLED, false);
		for (AmiWebManager i : this.service.getWebManagers().getManagers())
			i.addClientConnectedListener(this);
		this.menuBarPosition = MENUBAR_TOP;
		this.isLicenseDisabled = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_DISABLE_LICENSE_WIZARD, true);
		buildDesktop();
		this.getManager().addPortletListener(this);
		this.stylePeer = new AmiWebStyledPortletPeer(this, this.service);
		this.stylePeer.initStyle();
		getManager().onPortletAdded(desktopContainer);
		if (!isLocked) {
			this.debugInfoSetting = this.varsManager.getSetting(AmiWebConsts.USER_SETTING_DEBUG_INFO);
			if (this.debugInfoSetting == null) // If user hasn't selected a mode yet
				this.debugInfoSetting = AmiWebConsts.DEBUG_ONLY;
			this.debugWarningSetting = this.varsManager.getSetting(AmiWebConsts.USER_SETTING_DEBUG_WARNING);
			if (this.debugWarningSetting == null) // If user hasn't selected a mode yet
				this.debugWarningSetting = AmiWebConsts.DEBUG_ONLY;
		} else {
			this.debugInfoSetting = AmiWebConsts.NEVER;
			this.debugWarningSetting = AmiWebConsts.NEVER;
		}
		if (!isLocked) {
			this.service.getDebugManager().setMaxMessages(AmiDebugMessage.SEVERITY_INFO,
					tools.getOptional(AmiWebProperties.PROPERTY_AMI_MAX_INFO_MESSAGES, DEFAULT_MAX_DEBUG_MESSAGES));
			this.service.getDebugManager().setMaxMessages(AmiDebugMessage.SEVERITY_WARNING,
					tools.getOptional(AmiWebProperties.PROPERTY_AMI_MAX_WARN_MESSAGES, DEFAULT_MAX_DEBUG_MESSAGES));
		}
		this.service.getDebugManager().addDebugMessageListener(this);
		updateDebugManagerSeverities();
		if (!params.containsKey(AmiWebConsts.URL_PARAM_LAYOUT)) {
			loadDefaultLayout();
			this.service.processUrlParams(params);
		} else {
			this.service.processUrlParams(params);
			loadDefaultLayout();
		}
		initDesktop();
	}

	private void loadDefaultLayout() {
		String defaultLayoutSource = varsManager.getUserDefaultLayoutSource();
		if (this.desktop == null) {
			if (defaultLayoutSource != null) {
				String defaultLayoutName = varsManager.getUserDefaultLayoutName();
				service.getLayoutFilesManager().loadLayoutDialog(defaultLayoutName, null, defaultLayoutSource);
			} else {
				if (!isLocked) {
					List<AutoSaveFile> history = this.service.getAutosaveManager().getHistory();
					if (CH.isntEmpty(history) && "shutdown".equals(CH.last(history).getReason()))
						this.askAboutRestoreUnsavedWork = true;
				}
				String currentLayout = this.varsManager.getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT);
				if (currentLayout != null && OH.ne(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, currentLayout)) {
					String currentLayoutSource = this.varsManager.getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT_SOURCE);
					if (OH.ne(AmiWebConsts.LAYOUT_SOURCE_TMP, currentLayoutSource))
						service.getLayoutFilesManager().loadLayoutDialog(currentLayout, null, currentLayoutSource);
				}
			}
		}
		if (this.desktop == null)
			this.service.getLayoutFilesManager().setLayout(new AmiWebLayoutFile(this.service.getLayoutFilesManager(), AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, null));

	}

	private void updateDebugManagerSeverities() {
		this.service.getDebugManager().setShouldDebug(AmiDebugMessage.SEVERITY_INFO, evalDebugMode(this.debugInfoSetting));
		this.service.getDebugManager().setShouldDebug(AmiDebugMessage.SEVERITY_WARNING, evalDebugMode(this.debugWarningSetting));
	}

	public boolean evalDebugMode(String s) {
		if (isLocked || OH.eq(s, AmiWebConsts.NEVER))
			return false;
		else if (OH.eq(s, AmiWebConsts.ALWAYS))
			return true;
		else if (OH.eq(s, AmiWebConsts.DEBUG_ONLY))
			return inEditMode;
		else
			throw new RuntimeException("Bad mode: " + s);
	}
	private void buildDesktop() {

		this.removeAllChildren();
		this.clearRowSizes();
		switch (menuBarPosition) {
			case MENUBAR_TOP:
				addChild(desktopContainer, 0, 1);
				addChild(getDesktopBar(), 0, 0);
				setRowSize(0, 28);
				break;
			case MENUBAR_BOTTOM:
				addChild(desktopContainer, 0, 0);
				addChild(getDesktopBar(), 0, 1);
				setRowSize(1, 28);
				break;
			case MENUBAR_TOP_LEFT:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setStartPx(0).setSizePx(100), new WebAbsoluteLocation().setStartPx(0).setSizePx(28), 1);
				break;
			case MENUBAR_TOP_CENTER:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setStartPct(.5).setStartPx(-50).setSizePx(100), new WebAbsoluteLocation().setStartPx(0).setSizePx(28),
						1);
				break;
			case MENUBAR_TOP_RIGHT:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setEndPx(0).setSizePx(100), new WebAbsoluteLocation().setStartPx(0).setSizePx(28), 1);
				break;
			case MENUBAR_BOTTOM_LEFT:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setStartPx(0).setSizePx(100), new WebAbsoluteLocation().setEndPx(0).setSizePx(28), 1);
				break;
			case MENUBAR_BOTTOM_CENTER:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setStartPct(.5).setStartPx(-50).setSizePx(100), new WebAbsoluteLocation().setEndPx(0).setSizePx(28), 1);
				break;
			case MENUBAR_BOTTOM_RIGHT:
				addChild(desktopContainer, 0, 0);
				addChildOverlay(getLogoCutout(), new WebAbsoluteLocation().setEndPx(0).setSizePx(100), new WebAbsoluteLocation().setEndPx(0).setSizePx(28), 1);
				break;
		}
		updateHelpBubble();

	}
	private HtmlPortlet getLogoCutout() {
		return this.logo;
	}

	void initDesktop() {

		if (desktop.getVisible())
			desktop.layoutChildren();
		updateHelpBubble();
		this.flagDesktopBackgroundNeedUpdate();

	}

	private boolean checkForNewLayout = true;

	private String lastCurrentLayoutSource;

	@Override
	protected void initJs() {
		this.currentPortletId = null;
		flagStatusPanelNeedsUpdate();
		flagUpdateWindowLinks();
		flagUpdateDialogArrow();
		this.updateDashboard();
		super.initJs();
	}
	public void showNewLayoutStylePortlet() {
		if (OH.eq(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, this.service.getLayoutFilesManager().getLayoutName()) && !isLocked && desktop.getWindows().isEmpty())
			getManager().showDialog("Choose Layout Style", new AmiWebNewLayoutStylePortlet(generateConfig()));
	}

	private void updateHelpBubble() {
		if (this.desktop == null)
			return;
		if (isLocked || !this.service.getPrimaryWebManager().getIsEyeConnected())
			return;
		boolean noWindows = false;
		for (Portlet i : desktop.getChildren().values())
			if (!isSpecialPortlet(i)) {
				noWindows = true;
				break;
			}
		final String html;
		if (menuBarPosition != MENUBAR_TOP) {
			html = "";
		} else if (noWindows)
			html = "";
		else if (!inEditMode)
			html = "<div class='ami_help_bubble' style='right:10px'>Hint: Press this button to turn '<B>Layout Editor</B>' mode on.</div>";
		else
			html = "<div class='ami_help_bubble' style='left:135px'>Hint: Use the '<B>Windows</B>' dropdown menu to add your first window.</div>";
		desktop.addOption(DesktopPortlet.OPTION_BACKGROUND_INNNER_HTML, html);
	}

	@Override
	public void onContextMenu(DropDownMenuPortlet portlet, String action) {
		service.getSecurityModel().assertPermitted(this, action,
				"logout,_about,open_layout_*,importcloud_*,data_schema,_fullscreen,show_window_*,user_settings,export_userprefs,import_userprefs,save_userprefs,load_userprefs,reset_userprefs,upload_userprefs,download_userprefs,cust_menu_action_*,new_session,view_sessions,end_session,open_sessions,session_*");
		if ("manage_windows".equals(action)) {
			getManager().showDialog("Manage Windows", new AmiWebManagerWindowsPortlet(generateConfig(), this));
		} else if ("new_window".equals(action)) {
			AmiWebBlankPortlet bp = newAmiWebAmiBlankPortlet(getDesktop().getAmiLayoutFullAlias());
			createNewWindow(bp);
		} else if (action.startsWith("show_window_")) {
			String window = SH.stripPrefix(action, "show_window_", true);
			desktop.bringToFront(window);
		} else if ("new_session".equals(action)) {
			launchNewSession("");
		} else if ("open_sessions".equals(action)) {
			getManager().getPendingJs().append("window.open('" + AmiWebPages.URL_PORTALS + "');");
		} else if ("end_session".equals(action)) {
			if (!isLocked && service.getLayoutFilesManager().hasChangedSinceLastSave() && !service.isHeadlessSession()) {
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
						"Unsaved changes to '<B>" + service.getLayoutFilesManager().getLayoutName() + "</B>' will be lost. Continue?", ConfirmDialog.TYPE_OK_CANCEL);
				if (service.getLayoutFilesManager().getLayoutSource() != null)
					dialog.addButton("view_unsaved_changes", "View Unsaved Changes");
				dialog.setCallback("ENDSESSION");
				dialog.addDialogListener(this);
				getManager().showDialog("Unsaved Changes", dialog);
			} else {
				endSession();
			}
		} else if ("logout".equals(action)) {

			if (!isLocked && service.getLayoutFilesManager().hasChangedSinceLastSave() && !service.isHeadlessSession()) {
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(),
						"Unsaved changes to '<B>" + service.getLayoutFilesManager().getLayoutName() + "</B>' will be lost. Continue?", ConfirmDialog.TYPE_OK_CANCEL);
				if (service.getLayoutFilesManager().getLayoutSource() != null)
					dialog.addButton("view_unsaved_changes", "View Unsaved Changes");
				dialog.setCallback("LOGOUT");
				dialog.addDialogListener(this);
				getManager().showDialog("Unsaved Changes", dialog);
			} else {
				logout();
			}
		} else if ("developer_settings".equals(action)) {
			AmiWebDeveloperSettingsPortlet dsp = new AmiWebDeveloperSettingsPortlet(generateConfig());
			getManager().showDialog("Developer Settings", dsp, 531, 500).setStyle(service.getUserDialogStyleManager());
		} else if ("user_settings".equals(action)) {
			getManager().showDialog("User Settings", new AmiWebUserSettingsPortlet(generateConfig()), 410, 530).setStyle(service.getUserDialogStyleManager());
		} else if ("scm_settings".equals(action)) {
			getManager().showDialog("Source Control Management Settings", new AmiWebScmSettingsPortlet(generateConfig(), this.service))
					.setStyle(service.getUserDialogStyleManager());
		} else if (service.getPreferencesManager().onAmiWebDesktopAction(action)) {
			//handled
		} else if ("new".equals(action)) {
			service.getLayoutFilesManager().loadLayoutDialog(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, null, AmiWebConsts.LAYOUT_SOURCE_TMP);
		} else if ("export".equals(action)) {
			AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
			viewConfigPortlet.enableBrowser(true);
			viewConfigPortlet.setConfiguration(this.service.getLayoutFilesManager().getLayoutConfiguration(false));
			getManager().showDialog("Export configuration", viewConfigPortlet, layoutManager.getDialogWidth(), layoutManager.getDialogHeight())
					.setStyle(service.getUserDialogStyleManager());
		} else if ("export_root".equals(action)) {
			AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
			viewConfigPortlet.enableBrowser(true);
			viewConfigPortlet.setConfiguration(this.service.getLayoutFilesManager().getLayout().buildCurrentJson(getService()));
			getManager().showDialog("Export configuration", viewConfigPortlet, layoutManager.getDialogWidth(), layoutManager.getDialogHeight())
					.setStyle(service.getUserDialogStyleManager());
		} else if ("export_file".equals(action)) {
			AmiWebLayoutFilesManager r = service.getLayoutFilesManager();
			String configText = AmiWebLayoutHelper.toJson(r.getLayoutConfiguration(false), service.getLayoutFilesManager().getUserExportMode());
			getManager().pushPendingDownload(new BasicPortletDownload(service.getLayoutFilesManager().getLayoutName(), configText.getBytes()));
		} else if ("import_file".equals(action)) {
			FormPortlet fp = new FormPortlet(generateConfig());
			fp.addField(new FormPortletFileUploadField("Ami Layout"));
			getManager().showDialog("Layout File Upload", fp).setStyle(service.getUserDialogStyleManager());
		} else if ("import".equals(action)) {
			AmiWebLayoutHelper.ImportPortlet fp = new AmiWebLayoutHelper.ImportPortlet(layoutManager, getManager().generateConfig(), null, null);
			getManager().showDialog("Import configuration to blank Portlet", fp, layoutManager.getDialogWidth(), layoutManager.getDialogHeight())
					.setStyle(service.getUserDialogStyleManager());
		} else if ("download_layout".equals(action)) {
			int width = 700;
			int height = 250;
			AmiWebDownloadLayoutPortlet dlp = new AmiWebDownloadLayoutPortlet(generateConfig());
			getManager().showDialog("Download Layout", dlp, width, height).setStyle(service.getUserDialogStyleManager());
		} else if ("upload_layout".equals(action)) {
			int width = 500;
			int height = 150;
			AmiWebUploadLayoutPortlet ulp = new AmiWebUploadLayoutPortlet(generateConfig());
			getManager().showDialog("Upload Layout", ulp, width, height).setStyle(service.getUserDialogStyleManager());
		} else if ("saveas".equals(action)) {
			getManager().showDialog("Save To My Layouts", new AmiWebLayoutHelper.FileSavePortlet(layoutManager, getManager().generateConfig()))
					.setStyle(service.getUserDialogStyleManager());
		} else if ("diff_saved".equals(action)) {
			AmiWebDiffersPortlet dp = new AmiWebDiffersPortlet(this.service, generateConfig());
			dp.addTabCompareToSaved(this.service.getLayoutFilesManager().getLayout());
			getManager().showDialog("Layout Diff", dp).setStyle(service.getUserDialogStyleManager());
		} else if ("jsondiff_saved".equals(action)) {
			showJsonDiffer(false);
		} else if ("rebuild_layout".equals(action)) {
			this.service.getAutosaveManager().onRebuildLayout();
			this.service.getLayoutFilesManager().rebuildLayout();
		} else if ("reload_layout".equals(action)) {
			getManager().showDialog("Confirm reload",
					new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to reload from disk.  Unsaved changes will be lost", ConfirmDialog.TYPE_OK_CANCEL, this)
							.setCallback("RELOAD"));
		} else if ("scm_tool".equals(action)) {
			if (this.service.getScmAdapter() != null) {
				showScmBrowserPortlet();
			} else {
				String path = this.service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_DEVELOPER_SCM_PATH);
				if (SH.is(path))
					this.service.initScm(null);
				else
					getManager().showAlert("You must first configure source control settings Under: <P><i>Account -> Source Control Settings");
			}
		} else if ("__global_style".equals(action)) {
			getManager().showDialog("Edit Dashboard Styles", new AmiWebEditStylePortlet(this.stylePeer, generateConfig()), 550, getHeight() - 200);
		} else if ("_about".equals(action)) {
			AmiWebAboutFieldsPortlet.showAbout(getManager());
		} else if ("license_info".equals(action)) {
			try {
				File target = new File("f1license.txt");
				if (target.exists())
					if (SH.isnt(IOH.readText(target)))
						getManager().showAlert(
								"<b>Empty</b> license file. You can create a license key by clicking on <b>Help > Enter/Update License</b> or visiting our website and logging into your 3forge account.");
					else {
						AmiWebLicenseInfoPortlet lip = new AmiWebLicenseInfoPortlet(generateConfig(), target);
						getManager().showDialog("License Info", lip, 800, 250).setStyle(service.getUserDialogStyleManager());
					}
				else
					getManager().showAlert("License file <b>not found</b>. It should be named <b>" + target.getName() + "</b> and can be created at the following location: <b>"
							+ System.getProperty("user.dir") + "</b>");
			} catch (Exception e) {
				LH.warning(log, "Error getting license file info.");
			}
		} else if ("license_input".equals(action)) {
			int width = 700;
			int height = 220;
			AmiWebLicenseDialogPortlet ldp = new AmiWebLicenseDialogPortlet(generateConfig());
			getManager().showDialog("Update/Install License", ldp, width, height).setStyle(service.getUserDialogStyleManager());
		} else if ("_amiscriptdoc".equals(action)) {
			showDocumentationPortlet();
		} else if ("_fullscreen".equals(action)) {
		} else if ("data_schema".equals(action)) {
			showCenters();
		} else if ("data_models".equals(action)) {
			showDatamodelGraph();
			if (getSpecialPortlet(AmiWebAddPanelPortlet.class) != null)
				showAddPanelPortlet(currentPortletId);
		} else if ("amidb_cmd".equals(action)) {
			showAmidbShellPortlet();
		} else if ("var_table".equals(action)) {
			getManager().showDialog("Session Variables", new AmiWebVarsTablePortlet(generateConfig(), this.service.getVarsManager()));
		} else if ("style_manager".equals(action)) {
			showStyleManagerPortlet();
		} else if ("resource_manager".equals(action)) {
			showResourceManagerPortlet();
		} else if ("dashboard_settings".equals(action)) {
			showDashboardSettingsPortlet();
		} else if ("layout_method_add".equals(action)) {
			showCustomMethodsPortlet();
		} else if ("layout_callbacks".equals(action)) {
			showCustomCallbacksPortlet();
		} else if (SH.startsWith(action, "layout_callbacks_guiservice_")) {
			String guiServiceId = SH.stripPrefix(action, "layout_callbacks_guiservice_", true);
			this.service.getGuiServiceAdaptersManager().getCallbacks(guiServiceId).showEditor();
		} else if ("layout_custom_css".equals(action)) {
			showCustomCssPortlet();
		} else if ("data_upload".equals(action)) {
			showDataUploadPortlet();
			//		} else if ("data_debug".equals(action)) {
			//			showDebugPortlet();
		} else if ("inflate_stacktrace".equals(action)) {
			getManager().showDialog("Inflate Stacktrace", new AmiWebDebugInflateStackTracePortlet(generateConfig()));
		} else if ("included_files".equals(action)) {
			if (!showSpecialPortlet(AmiWebIncludedFilesPortlet.class))
				addSpecialPortlet(new AmiWebIncludedFilesPortlet(generateConfig()), "Included Layouts", 1000, 600);
		} else if ("set_defaults".equals(action)) {
			int width = 1000;
			int height = 500;
			AmiWebSetDefaultsPortlet sdp = new AmiWebSetDefaultsPortlet(generateConfig(), this.desktop);
			getManager().showDialog("Set Defaults", sdp, width, height);
			//		} else if ("rt_objects".equals(action)) {
			//			AmiWebChooseDataForm p = new AmiWebChooseDataForm(generateConfig(), service.getDesktop(), null, true);
			//			getManager().showDialog("Choose Ami Data", p);
		} else if ("show_objects".equals(action)) {
			showViewObjectsPortlet();
		} else if ("data_simulator".equals(action)) {
			showDataSimulatorPortlet();
		} else if ("open_abs".equals(action)) {
			if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(service.getLayoutFilesManager().getLayoutSource()))
				getManager()
						.showDialog("Open Layout",
								new AmiWebFileBrowserPortlet(generateConfig(), this, this.service.getLayoutFilesManager().getLayoutName(),
										AmiWebFileBrowserPortlet.TYPE_SELECT_FILE, "*.ami", null, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE))
						.setStyle(service.getUserDialogStyleManager());
			else
				getManager().showDialog("Open Layout",
						new AmiWebFileBrowserPortlet(generateConfig(), this, null, AmiWebFileBrowserPortlet.TYPE_SELECT_FILE, "*.ami", null, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE))
						.setStyle(service.getUserDialogStyleManager());
		} else if ("save".equals(action)) {
			service.getLayoutFilesManager().saveLayout();
		} else if ("save_diff".equals(action)) {
			showJsonDiffer(true);
		} else if ("file_info".equals(action)) {
			showFileInfo();
		} else if ("saveas_abs".equals(action)) {
			String name = this.service.getLayoutFilesManager().getLayoutName();
			if (SH.is(name) && !AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(this.service.getLayoutFilesManager().getLayoutSource())) {
				String path = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_DEFAULT_FILE_BROWSER_PATH);
				if (path != null)
					name = path + "/" + name;
			}
			getManager()
					.showDialog("Save Layout",
							new AmiWebFileBrowserPortlet(generateConfig(), this, name, AmiWebFileBrowserPortlet.TYPE_SAVE_FILE, "*.ami", null, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE))
					.setStyle(service.getUserDialogStyleManager());
		} else if (action.startsWith("open_layout_")) {
			String name = SH.stripPrefix(action, "open_layout_", true);
			service.getLayoutFilesManager().loadLayoutDialog(name, null, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
		} else if (action.equals("open_details")) {
			int width = 531;
			int height = 500;
			AmiWebManageLayoutsPortlet mlp = new AmiWebManageLayoutsPortlet(generateConfig(), this.service.getLayoutFilesManager().getLocalLayoutNames());
			getManager().showDialog("Layout File Manager", mlp, width, height).setStyle(service.getUserDialogStyleManager());
		} else if ("autosave_manager".equals(action)) {
			RootPortletDialog dialog = getManager().showDialog("File Recovery", new AmiWebAutosaveManagerPortlet(generateConfig()));
			dialog.setStyle(service.getUserDialogStyleManager());
			dialog.setShadeOutside(false);
			dialog.setCloseOnClickOutside(true);
		} else if (action.startsWith("exportcloud_")) {
			AmiWebLayoutFilesManager r = service.getLayoutFilesManager();
			getManager().showDialog("Export to Cloud",
					new AmiWebExportToCloudPortlet(generateConfig(),
							AmiWebLayoutHelper.toJson(r.getLayoutConfiguration(false), service.getLayoutFilesManager().getUserExportMode()), service.getCloudManager(),
							service.getLayoutFilesManager().getLayoutName(), this.service),
					400, 400);
		} else if ("import_cloud".equals(action)) {
			getManager()
					.showDialog("Open From Cloud",
							new AmiWebFileBrowserPortlet(generateConfig(), this, "", AmiWebFileBrowserPortlet.TYPE_SELECT_FILE, "*.ami",
									this.service.getCloudManager().getLayoutsRootDirectory().getFullPath(), AmiWebConsts.LAYOUT_SOURCE_CLOUD))
					.setStyle(service.getUserDialogStyleManager());
		} else if ("export_cloud".equals(action)) {
			//			String path = SH.replaceAll(IOH.getFullPath(this.service.getCloudManager().getLayoutsRootDirectory()), '\\', '/');
			String path = SH.replaceAll(this.service.getCloudManager().getLayoutsRootDirectory().getFullPath(), '\\', '/');
			String name = SH.replaceAll(this.service.getLayoutFilesManager().getLayoutName(), '\\', '/');
			if (SH.is(name)) {
				if (!AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(this.service.getLayoutFilesManager().getLayoutSource())) {
					name = SH.afterLast(name, '/', name);
				}
			}
			getManager()
					.showDialog("Save To Cloud",
							new AmiWebFileBrowserPortlet(generateConfig(), this, name, AmiWebFileBrowserPortlet.TYPE_SAVE_FILE, "*.ami", path, AmiWebConsts.LAYOUT_SOURCE_CLOUD))
					.setStyle(service.getUserDialogStyleManager());
		} else if (action.startsWith("importcloud_")) {
			String name = SH.stripPrefix(action, "importcloud_", true);
			service.getLayoutFilesManager().loadLayoutDialog(name, null, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
		} else if (action.equals("shutdown") && this.varsManager.isUserAdmin()) {
			getManager().showDialog("Confirm Shutdown",
					new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to shutdown AMI? This will terminate all users sessions", ConfirmDialog.TYPE_OK_CANCEL, this)
							.setCallback("SHUTDOWN"));
		} else if (action.startsWith("deletecloud_")) {
			String name = SH.stripPrefix(action, "deletecloud_", true);
			ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete:<BR>&nbsp;<BR><B> " + name, ConfirmDialog.TYPE_YES_NO);
			dialog.setCallback("DELETE_FROM_CLOUD");
			dialog.setCorrelationData(name);
			dialog.addDialogListener(this);
			getManager().showDialog("Delete", dialog);
		} else if (action.equals("admin_tool")) {
			getManager().showDialog("Admin Tool", new AmiWebAdminToolPortlet(generateConfig(), this.getManager().getTools(), service.getCloudManager()), 930, 500);
		} else if (AmiWebCustomContextMenuManager.ACTION_MENU_ADD.equals(action)) {
			this.desktop.getCustomContextMenu().handleCustomizeCallback(action);
		} else if (SH.startsWith(action, AmiWebCustomContextMenuManager.CUSTOM_MENU_ACTION_DELETE)) {
			this.desktop.getCustomContextMenu().handleCustomizeCallback(action);
		} else if (SH.startsWith(action, AmiWebCustomContextMenuManager.CUSTOM_MENU_ACTION_EDIT)) {
			this.desktop.getCustomContextMenu().handleCustomizeCallback(action);
		} else if (SH.startsWith(action, AmiWebCustomContextMenuManager.CUSTOM_MENU_ACTION_EXPORT)) {
			this.desktop.getCustomContextMenu().handleCustomizeCallback(action);
		} else if (SH.startsWith(action, AmiWebCustomContextMenuManager.CUSTOM_MENU_ACTION_IMPORT)) {
			this.desktop.getCustomContextMenu().handleCustomizeCallback(action);
		} else if (this.desktop.getCustomContextMenu().isCustomContextMenuAction(action)) {
			this.desktop.getCustomContextMenu().processCustomContextMenuAction(action);
		} else if (action.startsWith("headless_")) {
			String headlessId = SH.stripPrefix(action, "headless_", true);
			switchToHeadlessSession(headlessId);
		} else if (action.startsWith("session_")) {
			String headlessId = SH.stripPrefix(action, "session_", true);
			switchToSession(headlessId);
		}
	}

	private void showJsonDiffer(boolean hasSave) {
		AmiWebLayoutFile t = this.service.getLayoutFilesManager().getLayout();
		AmiWebJsonDifferPortlet dp = new AmiWebJsonDifferPortlet(this.service, generateConfig());
		dp.setHasSaveButton(hasSave);
		dp.addComparison(t);
		dp.buildTree();
		RootPortletDialog dialog = getManager().showDialog("Layout Diff", dp);
		dialog.setStyle(service.getUserDialogStyleManager());
		dialog.setCloseOnClickOutside(false);
		dialog.setEscapeKeyCloses(false);
	}

	public void launchNewSession(String layout) {
		getManager().getPendingJs().append(
				"window.open('" + BasicPortletManager.URL_START + "?" + AmiWebConsts.URL_PARAM_LAYOUT + "=" + layout + "&" + BasicPortletManager.KEEP_EXISTING_OPEN + "=true');");
	}

	public void logout() {
		getManager().getState().getWebStatesManager().getSession().kill();
	}
	private void endSession() {
		getManager().close();
	}

	private void showFileInfo() {
		String source = this.service.getLayoutFilesManager().getLayoutSource();
		String name = this.service.getLayoutFilesManager().getLayoutName();
		AmiWebLayoutFile layout = this.service.getLayoutFilesManager().getLayout();
		AmiWebFile file = this.service.getLayoutFilesManager().getLayoutFile(layout);
		StringBuilder sb = new StringBuilder("Logical Location: <B>");
		sb.append(source).append(":");
		WebHelper.escapeHtml(name, sb).append("</B><P>");
		if (file instanceof AmiWebFile_Remote) {
			AmiWebFile_Remote rfile = (AmiWebFile_Remote) file;
			sb.append("Stored on webmanager: <B>").append(rfile.getClient().getHostName()).append("</B><P>");
		} else {
			sb.append("Stored on webserver: <B>").append(EH.getLocalHost()).append("</B><P>");
		}
		sb.append("Full Path: <B>").append(file.getAbsolutePath()).append("</B><P>");
		sb.append("URL: <B>");
		sb.append(service.getUrl()).append('?').append(AmiWebConsts.URL_PARAM_LAYOUT).append('=').append(layout.getSource()).append('_').append(layout.getLocation());
		sb.append("</B><P>");
		sb.append("last Modified: <B>");
		service.getFormatterManager().getDatetimeMillisFormatter().format(file.lastModified(), sb);
		sb.append("</B><P>");
		sb.append("Size On Disk: <B>");
		service.getFormatterManager().getIntegerFormatter().format(file.length(), sb);
		sb.append(" bytes</B><P>");
		ConfirmDialogPortlet adp = new ConfirmDialogPortlet(generateConfig(), sb.toString(), ConfirmDialog.TYPE_MESSAGE);
		getManager().showDialog("Current Layout Information", adp, 800, 350);
	}
	public Window createNewWindow(AmiWebPortlet bp) {
		outer: for (int i = 1;; i++) {
			String title = i == 1 ? "Window" : ("Window - " + i);
			for (String child : desktop.getChildren().keySet())
				if (title.equals(desktop.getWindow(child).getName()))
					continue outer;
			bp.setTitle(title);
			this.desktop.addChild(title, bp);
			Window window = this.desktop.getWindow(bp.getPortletId());
			window.setName(title);
			window.setAllowPop(this.isPopoutEnabled, true);
			window.setDefaultLocationToCurrent();
			window.setDefaultStateToCurrent();
			window.setDefaultZIndexToCurrent();
			return window;
		}
	}
	private boolean showSpecialPortlet(Class<? extends AmiWebSpecialPortlet> clazz) {
		AmiWebSpecialPortlet t = getSpecialPortlet(clazz);
		if (t == null)
			return false;
		this.desktop.bringToFront(t.getPortletId());
		return true;
	}

	private void showDashboardSettingsPortlet() {
		AmiWebDashboardSettingsPortlet dashboardSettingsPortlet = new AmiWebDashboardSettingsPortlet(generateConfig(), this.service);
		getManager().showDialog("Dashboard Settings", dashboardSettingsPortlet);
	}

	public void addSpecialPortlet(AmiWebSpecialPortlet t, String label, int w, int h) {
		setSpecialPortlet(t);
		applyEditModeStyle(this.desktop.addChild(label, t), w, h);
		getManager().onPortletAdded(t);
	}
	private void addSpecialPortlet(AmiWebSpecialPortlet t, String label) {
		setSpecialPortlet(t);
		applyEditModeStyle(this.desktop.addChild(label, t));
		getManager().onPortletAdded(t);
	}
	public void showDatamodelGraph() {
		if (!showSpecialPortlet(AmiWebDmTreePortlet.class)) {
			addSpecialPortlet(new AmiWebDmTreePortlet(generateConfig(), this.service, "", true, null), "AMI Data Modeler", 1250, 700);
		}
	}
	public void showAmidbShellPortlet() {
		if (!showSpecialPortlet(AmiWebAmiDbShellPortlet.class))
			addSpecialPortlet(new AmiWebAmiDbShellPortlet(this.service, generateConfig()), "AMIDB Shell Tool", 1280, 700);
	}
	public void showStyleManagerPortlet() {
		if (!showSpecialPortlet(AmiWebStyleManagerPortlet.class))
			addSpecialPortlet(new AmiWebStyleManagerPortlet(generateConfig()), "AMI styler", 1000, 700);
	}
	public void showResourceManagerPortlet() {
		if (!showSpecialPortlet(AmiWebResourceManagerPortlet.class))
			addSpecialPortlet(new AmiWebResourceManagerPortlet(generateConfig()), "AMI Resource Manager");
	}

	public void showAddPanelPortlet(String cpid) {
		if (!showSpecialPortlet(AmiWebAddPanelPortlet.class)) {
			AmiWebAddPanelPortlet t = new AmiWebAddPanelPortlet(generateConfig(), cpid, null, null, null);
			addSpecialPortlet(t, "AMI Panel", 1450, 700);
			t.getChooseDataPortlet().getHeader().updateBlurbPortletLayout("AMI Table Chooser", AmiWebDmChooseDatasourceTilesPortlet.ADDPANEL_HELP_HTML);
		}
	}
	public void showAddRealtimePanelPortlet(String cpid, String alias) {
		if (!showSpecialPortlet(AmiWebAddRealtimePanelPortlet.class)) {
			AmiWebAddRealtimePanelPortlet t = new AmiWebAddRealtimePanelPortlet(generateConfig(), this.getService(), cpid, alias);
			addSpecialPortlet(t, "AMI Realtime Panel", 1450, 700);
		}
	}
	public void showAddVisualizationPortlet(AmiWebDm dm) {
		if (dm.getResponseOutSchema().getTableNamesSorted().isEmpty())
			getManager().showAlert("Datamodel does not have any tables");
		else if (!showSpecialPortlet(AmiWebAddVisualizationPortlet.class))
			addSpecialPortlet(new AmiWebAddVisualizationPortlet(generateConfig(), dm, ""), "Add Visualization", 1450, 700);
	}
	public AmiWebMethodsPortlet showCustomMethodsPortlet() {
		if (!showSpecialPortlet(AmiWebMethodsPortlet.class))
			addSpecialPortlet(new AmiWebMethodsPortlet(generateConfig(), this.service), "Custom Methods", 1000, getManager().getRoot().getHeight() - 75);
		return getSpecialPortlet(AmiWebMethodsPortlet.class);
	}
	public void showScmBrowserPortlet() {
		if (!showSpecialPortlet(AmiWebScmBrowserPortlet.class))
			addSpecialPortlet(new AmiWebScmBrowserPortlet(generateConfig(), this.service), "Source Control Browser");
	}

	public void showDocumentationPortlet() {
		if (!showSpecialPortlet(AmiWebDocumentationPortlet.class))
			addSpecialPortlet(new AmiWebDocumentationPortlet(generateConfig(), getService()), "Documentation", 1000, 600);
	}

	public void showViewObjectsPortlet() {
		if (!showSpecialPortlet(AmiWebViewObjectsPortlet.class)) {
			addSpecialPortlet(new AmiWebViewObjectsPortlet(generateConfig(), getService()), "Object Browser", 1150, 600);
		}
	}
	public void showCustomCssPortlet() {
		if (!showSpecialPortlet(AmiWebCustomCssTabsPortlet.class))
			addSpecialPortlet(new AmiWebCustomCssTabsPortlet(generateConfig(), this.service), "Custom Css Styles", 1000, getManager().getRoot().getHeight() - 75);
	}
	public AmiWebEditCustomCallbacksPortlet showCustomCallbacksPortlet() {
		if (!showSpecialPortlet(AmiWebEditCustomCallbacksPortlet.class))
			addSpecialPortlet(new AmiWebEditCustomCallbacksPortlet(generateConfig(), service), "Script Callbacks Settings", 1200, 800);
		return getSpecialPortlet(AmiWebEditCustomCallbacksPortlet.class);
	}
	public void showDataUploadPortlet() {
		if (!showSpecialPortlet(AmiWebUploadDataPortlet.class))
			addSpecialPortlet(new AmiWebUploadDataPortlet(generateConfig()), "Data Uploader", 1000, 800);
	}

	public void showDataSimulatorPortlet() {
		if (!showSpecialPortlet(AmiWebSimulatorPortlet.class))
			addSpecialPortlet(new AmiWebSimulatorPortlet(generateConfig()), "Data Simulator", 400, getManager().getRoot().getHeight() - 75);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return onButton2(source, id);
	}

	private void switchToHeadlessSession(String pgId) {
		getManager().getPendingJs().append("window.open('" + AmiWebPages.URL_OWN_HEADLESS + "?" + BasicPortletManager.PAGEID + "=").append(pgId).append("');");

	}
	private void switchToSession(String pgId) {
		getManager().getPendingJs().append("window.location='" + BasicPortletManager.URL_PORTAL + "?" + BasicPortletManager.PAGEID + "=").append(pgId).append("';");

	}
	//TODO: Move method to AmiWebLayoutHelper
	public String getAmiLayoutFullAliasForPortletId(String portletId) {
		return ((AmiWebAliasPortlet) getManager().getPortlet(portletId)).getAmiLayoutFullAlias();
	}
	public String getParentAmiLayoutFullAliasForPortletId(String portletId) {
		return ((AmiWebAliasPortlet) getManager().getPortlet(portletId)).getAmiParent().getAmiLayoutFullAlias();
	}

	void resetDesktop() {
		for (AmiWebSpecialPortlet i : CH.l(this.specialPortlets.values()))
			i.close();
		this.specialPortlets.clear();
		this.portletClipboard = null;
		this.portletCutboard = null;

	}
	public void createFilterPortlet(String currentPortletId) {
		AmiWebAliasPortlet parent = (AmiWebAliasPortlet) getManager().getPortlet(currentPortletId);
		getManager().showDialog("Create Filter", new AmiWebFilterWizPortlet(generateConfig(), parent, null, REPLACE));
	}
	private void buildMenus() {
	}

	public Portlet deletePanel(String portletId, boolean delete) {
		AmiWebAliasPortlet removed = (AmiWebAliasPortlet) getManager().getPortlet(portletId);
		PortletContainer parent = removed.getParent();
		if (removed.isTransient()) {
			AmiWebAliasPortlet replacement = removed.getNonTransientPanel();
			if (replacement != null) {
				parent.replaceChild(removed.getPortletId(), replacement);
				return removed;
			}
		}
		if (!delete)
			this.service.getLayoutFilesManager().getLayoutByFullAlias(removed.getAmiLayoutFullAlias()).onHidePanel(removed);
		else
			this.service.getLayoutFilesManager().getLayoutByFullAlias(removed.getAmiLayoutFullAlias()).onDeletePanel(removed);
		if (parent instanceof DividerPortlet) {
			Collection<Portlet> values = parent.getChildren().values();
			Portlet replacement = CH.first(values);
			if (replacement == removed)
				replacement = CH.last(values);
			parent.removeChild(replacement.getPortletId());
			AmiWebAbstractContainerPortlet amiContainer = AmiWebUtils.getParentAmiContainer(parent);
			AmiWebAbstractContainerPortlet parentContainer = AmiWebUtils.getParentAmiContainer(amiContainer);
			if (parentContainer == null)
				amiContainer.getParent().replaceChild(amiContainer.getPortletId(), replacement);
			else
				parentContainer.getInnerContainer().replaceChild(amiContainer.getPortletId(), replacement);
			amiContainer.close();
			this.service.getLayoutFilesManager().getLayoutByFullAlias(amiContainer.getAmiLayoutFullAlias()).onDeletePanel(amiContainer);
		} else if (parent instanceof RootPortlet) {
			this.desktop.removeChild(parent.getPortletId());
			removed.close();
		} else {
			removed.close();
		}
		//Moved delete relationship to onPortletClosed

		return removed;

	}

	public PortletStyleManager_Menu getMenuStyle() {
		return this.menuStyle;
	}

	@Override
	public WebMenu createMenu(DropDownMenuPortlet dropdown, String id, WebMenuLink menu) {
		BasicWebMenu r = new BasicWebMenu();
		if (!inEditMode) {
			r.setStyle(getMenuStyle());
			for (String child : desktop.getChildren().keySet()) {
				Window win = desktop.getWindow(child);
				String poppedOutText = win.isPoppedOut() ? " [popped out]" : "";
				if (isSpecialPortlet(win.getPortlet()))
					continue;
				if (!win.isHidden(true)) {
					String name = win.getName();
					if (OH.eq(menu.getText(), getWindowMenuLocation(win)))
						r.addChild(new BasicWebMenuLink(name + poppedOutText, true, "show_window_" + child));
				}
			}
			Collections.sort(r.getChildren(), BasicWebMenu.TEXT_COMPARATOR);
			if (!r.getChildren().isEmpty())
				r.addChild(new BasicWebMenuDivider());
		}
		if ("file".equals(id)) {
			r.setStyle(getMenuStyle());
			if (this.varsManager.isUserDev() || this.varsManager.isUserAdmin()) {
				BasicWebMenu w = new BasicWebMenu("Open", true).setBackgroundImage(AmiWebConsts.ICON_OPEN);
				BasicWebMenu k = new BasicWebMenu("Save As", true).setBackgroundImage(AmiWebConsts.ICON_SAVE);
				boolean added = false;
				if (!isLocked) {
					r.addChild(new BasicWebMenuLink("New", true, "new").setBackgroundImage(AmiWebConsts.ICON_NEW));
					String source = this.service.getLayoutFilesManager().getLayoutSource();
					boolean enabled = OH.ne(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, service.getLayoutFilesManager().getLayoutName());
					boolean hasInfo = false;
					if (enabled) {
						if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(source) || AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(source) || AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(source)
								|| source == null) {
							hasInfo = true;
						}
						r.addChild(new BasicWebMenuLink("Save After Review", hasInfo, "save_diff").setBackgroundImage(AmiWebConsts.ICON_SAVE));
						r.addChild(new BasicWebMenuLink("Save", hasInfo, "save").setBackgroundImage(AmiWebConsts.ICON_SAVE));

					}
					r.addChild(k);
					r.addChild(w);
					r.addChild(new BasicWebMenuLink("Info", hasInfo && enabled, "file_info").setBackgroundImage(AmiWebConsts.ICON_ABOUT));

					r.addChild(new BasicWebMenuLink("Rebuild", true, "rebuild_layout").setBackgroundImage(AmiWebConsts.ICON_SETTINGS));
					r.addChild(new BasicWebMenuLink("Reload From Disk", true, "reload_layout").setBackgroundImage(AmiWebConsts.ICON_SETTINGS));
					r.addChild(new BasicWebMenuLink("View Changes...", true, "jsondiff_saved").setBackgroundImage(AmiWebConsts.ICON_DIFF));
					r.addChild(new BasicWebMenuLink("View Changes (Raw text)...", true, "diff_saved").setBackgroundImage(AmiWebConsts.ICON_DIFF));
					r.addChild(new BasicWebMenuLink("Recovery...", true, "autosave_manager").setBackgroundImage(AmiWebConsts.ICON_ROTATE_COUNTER));
					w.addChild(new BasicWebMenuLink("Absolute File", true, "open_abs").setBackgroundImage(AmiWebConsts.ICON_ABS_OPEN));
					w.addChild(new BasicWebMenuLink("My Layouts", true, "open_details").setBackgroundImage(AmiWebConsts.ICON_MYLAYOUT_OPEN));
					k.addChild(new BasicWebMenuLink("Absolute File", true, "saveas_abs").setBackgroundImage(AmiWebConsts.ICON_ABS_SAVEAS));
					k.addChild(new BasicWebMenuLink("My Layouts", true, "saveas").setBackgroundImage(AmiWebConsts.ICON_MYLAYOUT_SAVEAS));
					added = true;
				}
				AmiWebCloudLayoutTree layouts = this.service.getCloudManager().getCloudLayouts();
				if (layouts != null) {
					if (!added) {
						r.addChild(w);
					}
					w.addChild(new BasicWebMenuLink("Cloud", true, "import_cloud").setBackgroundImage(AmiWebConsts.ICON_IMPORT_CLOUD));
					if (!isLocked) {
						if (!added) {
							r.addChild(k);
						}
						k.addChild(new BasicWebMenuLink("Cloud", true, "export_cloud").setBackgroundImage(AmiWebConsts.ICON_PUBLISH_CLOUD));
					}
					r.addChild(new BasicWebMenuDivider());
				}
				if (!isLocked) {
					r.addChild(new BasicWebMenuLink("Export Root layout...", true, "export_root").setBackgroundImage(AmiWebConsts.ICON_EXPORT_ROOT));
					r.addChild(new BasicWebMenuLink("Export...", true, "export").setBackgroundImage(AmiWebConsts.ICON_EXPORT_ROOT_ALL));
					r.addChild(new BasicWebMenuLink("Import...", true, "import").setBackgroundImage(AmiWebConsts.ICON_IMPORT));
					r.addChild(new BasicWebMenuLink("Download...", true, "download_layout").setBackgroundImage(AmiWebConsts.ICON_DOWNLOAD));
					r.addChild(new BasicWebMenuLink("Upload...", true, "upload_layout").setBackgroundImage(AmiWebConsts.ICON_UPLOAD));
				}
				if (this.varsManager.isUserAdmin() && !isLocked) {
					r.addChild(new BasicWebMenuDivider());
					r.addChild(new BasicWebMenuLink("Shutdown", true, "shutdown").setBackgroundImage(AmiWebConsts.ICON_SHUTDOWN));
				}
			} else {
				AmiWebCloudLayoutTree layouts = this.service.getCloudManager().getCloudLayouts();
				if (layouts != null)
					buildCloudImport(r, layouts, "importcloud_", false, false);
			}
		} else if ("window".equals(id)) {
			r.setStyle(getMenuStyle());
			if (inEditMode) {
				for (String child : desktop.getChildren().keySet()) {
					Window win = desktop.getWindow(child);
					if (isSpecialPortlet(win.getPortlet()))
						continue;
					if (this.service.getAmiQueryFormEditorsManager().isEditor(win.getPortlet()))
						continue;
					String name = WebHelper.escapeHtml(win.getName());
					String poppedOutText = win.isPoppedOut() ? " [popped out]" : "";
					if (!win.isHidden(false)) {
						String loc = getWindowMenuLocation(win);
						if ("Windows".equals(loc)) {
							r.addChild(new BasicWebMenuLink(name + poppedOutText, true, "show_window_" + child));
						} else
							r.addChild(new BasicWebMenuLink(name + " [in menu '" + loc + "']" + poppedOutText, true, "show_window_" + child));
					} else {
						r.addChild(new BasicWebMenuLink(name + " [hidden]" + poppedOutText, true, "show_window_" + child));
					}
				}
				Collections.sort(r.getChildren(), BasicWebMenu.TEXT_COMPARATOR);
				r.addChild(new BasicWebMenuDivider());
			}
			boolean isFullscreen = ((RootPortlet) getManager().getRoot()).isFullScreen();
			if (showMenuOption(AmiWebProperties.PROPERTY_AMI_SHOW_MENU_OPTION_FULL_SCREEN, AmiWebProperties.VALUE_ALWAYS))
				r.addChild(((AbstractWebMenuItem) new BasicWebMenuLink(isFullscreen ? "Exit Full Screen" : "Full Screen", true, "_fullscreen")
						.setOnClickJavascript("toggleFullScreen();")).setBackgroundImage(AmiWebConsts.ICON_FULL_SCREEN));
			if (getInEditMode()) {
				r.addChild(new BasicWebMenuDivider());
				if (!this.desktop.isReadonlyLayout()) {
					r.addChild(new BasicWebMenuLink("New Window", true, "new_window").setBackgroundImage(AmiWebConsts.ICON_NEW_WINDOW));
					r.addChild(new BasicWebMenuLink("Manage Windows...", true, "manage_windows").setBackgroundImage(AmiWebConsts.ICON_MANAGE_WINDOWS));
				} else {
					r.addChild(new BasicWebMenuLink("(Read only layout, Can't add windows)", false, "new_window").setBackgroundImage(AmiWebConsts.ICON_NEW_WINDOW));
				}
			}
		} else if ("account".equals(id)) {
			r.setStyle(getMenuStyle());
			AmiWebStatesManager wsm = service.getWebState().getWebStatesManager();
			int sessionsCount = wsm.getNonHeadlessSessionsCount();
			if (sessionsCount < 2) {
				r.addChild(new BasicWebMenuLink("Logout", true, "logout").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
			} else {
				r.addChild(new BasicWebMenuLink("Logout (Will close all " + sessionsCount + " sessions)", true, "logout").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
			}

			if (service.isHeadlessSession())
				r.addChild(new BasicWebMenuLink("Release ownership of this headless session", true, "end_session").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
			else if (service.getVarsManager().getMaxSessions() > 1) {
				r.addChild(new BasicWebMenuLink("End this Session (Don't log out)", true, "end_session").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
			}
			if (service.getVarsManager().getMaxSessions() > 1) {
				r.addChild(new BasicWebMenuLink("Start new Session", wsm.canAddSession(), "new_session").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
				r.addChild(new BasicWebMenuLink("View Sessions", true, "open_sessions").setBackgroundImage(AmiWebConsts.ICON_LOGOUT));
				AmiWebStatesManager states = (AmiWebStatesManager) getManager().getState().getWebStatesManager();
				BasicWebMenu otherSessionsMenu = new BasicWebMenu("Jump to Session", true).setBackgroundImage(AmiWebConsts.ICON_LOGOUT);
				for (String pgid : states.getPgIds()) {
					if (OH.eq(getManager().getState().getPgId(), pgid))
						continue;
					WebState state = states.getState(pgid);
					if (state == null)
						continue;
					PortletManager pm = state.getPortletManager();
					if (pm == null)
						continue;
					AmiWebService aws = (AmiWebService) pm.getService(AmiWebService.ID);
					String layoutName = aws.getLayoutFilesManager().getLayoutName();
					otherSessionsMenu.add(new BasicWebMenuLink(layoutName, true, "session_" + pgid));
				}
				if (otherSessionsMenu.getChildrenCount() > 0)
					r.addChild(otherSessionsMenu);
			}
			if (!this.isLocked) {
				AmiWebHeadlessManager hm = getManager().getTools().getServices().getService(AmiWebHeadlessManager.SERVICE_ID, AmiWebHeadlessManager.class);
				Set<String> names = hm.getSessionNames();
				if (names.size() > 0) {
					BasicWebMenu headlessMenu = new BasicWebMenu("Take ownership of headless session", true).setBackgroundImage(AmiWebConsts.ICON_LOGOUT);
					r.addChild(headlessMenu);
					for (String name : names) {
						AmiWebHeadlessSession session2 = hm.getSessionByName(name);
						WebStatesManager wsm2 = session2 == null || session2.getWebState() == null ? null : session2.getWebState().getWebStatesManager();
						if (wsm2 == this.getManager().getState().getWebStatesManager())
							headlessMenu.add(new BasicWebMenuLink(name + " (already owned by me)", false, "headless_" + hm.getSessionByName(name).getPgid()));
						else if (wsm2 != null)
							headlessMenu.add(new BasicWebMenuLink(name + " (Being Viewed by " + wsm2.getUserName() + ")", true, "headless_" + hm.getSessionByName(name).getPgid()));
						else if (!session2.isAlive())
							headlessMenu.add(new BasicWebMenuLink(name + " (Not alive)", false, "headless_" + session2.getPgid()));
						else
							headlessMenu.add(new BasicWebMenuLink(name, true, "headless_" + session2.getPgid()));
					}
				}
			}
			r.addChild(new BasicWebMenuDivider());
			r.addChild(new BasicWebMenuLink("My Settings...", true, "user_settings").setBackgroundImage(AmiWebConsts.ICON_USER_SETTINGS));
			if (!isLocked) {
				r.addChild(new BasicWebMenuLink("My Developer Settings...", true, "developer_settings").setBackgroundImage(AmiWebConsts.ICON_DEV_SETTINGS));
				r.addChild(new BasicWebMenuLink("My Source Control Settings...", true, "scm_settings").setBackgroundImage(AmiWebConsts.ICON_SC_SETTINGS));
			}
			service.getPreferencesManager().createMenu(r);
		} else if ("data".equals(id)) {
			if (!isLocked) {
				r.addChild(new BasicWebMenuLink("Data Modeler...", true, "data_models").setBackgroundImage(AmiWebConsts.ICON_DATAMODEL));
				r.addChild(new BasicWebMenuLink("AMIDB Shell Tool...", true, "amidb_cmd").setBackgroundImage(AmiWebConsts.ICON_SETTINGS));
				BasicWebMenu t1 = new BasicWebMenu("Editors & Viewers", true).setBackgroundImage(AmiWebConsts.ICON_EDITORS);
				boolean needsDivider = false;
				if (this.service.getAmiWebViewMethodsManager().getEditorsCount() > 0) {
					Collection<AmiWebViewMethodPortlet> editors = this.service.getAmiWebViewMethodsManager().getEditors();
					for (AmiWebViewMethodPortlet editor : editors) {
						String name = "View Method - " + editor.getTitle();
						t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + editor.getPortletId(), 0));
					}
					needsDivider = true;
				}
				if (this.service.getAmiWebCallbackEditorsManager().getEditorsCount() > 0) {
					Set<String> editorsIds = this.service.getAmiWebCallbackEditorsManager().getEditorsPortletIds();
					for (String portletId : editorsIds) {
						AmiWebEditAmiScriptCallbackDialogPortlet editor = this.service.getAmiWebCallbackEditorsManager().getEditorByPortletId(portletId);
						String ari = editor.getCallbacks().getThis().getAri();
						String name = "Amiscript Callbacks - " + ari;
						t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + portletId, 0));
					}
					needsDivider = true;
				}
				if (this.service.getAmiWebCustomContextMenuEditorsManager().getEditorsCount() > 0) {
					Set<String> editorIds = this.service.getAmiWebCustomContextMenuEditorsManager().getEditorIds();
					for (String ari : editorIds) {
						AmiWebCustomContextMenuSettingsPortlet editor = this.service.getAmiWebCustomContextMenuEditorsManager().getEditorByAri(ari);
						String portletId = editor.getPortletId();
						String name = "Amiscript Custom Context Menu - " + ari;
						t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + portletId, 0));
					}
				}
				if (this.service.getAmiWebDmEditorsManager().getEditorsCount() > 0) {
					if (needsDivider)
						t1.add(new BasicWebMenuDivider(2));
					Set<String> editorsIds = this.service.getAmiWebDmEditorsManager().getEditorsPortletIds();
					for (String portletId : editorsIds) {
						AmiWebAddPanelPortlet editor = this.service.getAmiWebDmEditorsManager().getEditorByPortletId(portletId);
						String name = null;
						AmiWebDm editedDm = editor.getEditedDm();
						if (editedDm != null) {
							String aliasDotName = editedDm.getAmiLayoutFullAliasDotId();
							name = "Datamodel: " + aliasDotName;
							t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + portletId, 0));
						} else {
							name = "New Datamodel: " + editor.getNameFieldValue();
							t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + portletId, 1));
						}
					}
				}
				if (this.service.getAmiQueryFormEditorsManager().getEditorsCount() > 0) {
					if (needsDivider)
						t1.add(new BasicWebMenuDivider(2));
					Set<String> editorsIds = this.service.getAmiQueryFormEditorsManager().getEditorsPortletIds();
					for (String portletId : editorsIds) {
						AmiWebQueryFieldWizardPortlet editor = this.service.getAmiQueryFormEditorsManager().getEditorByPortletId(portletId);
						QueryField<?> editedFieldForFieldEditor = editor.getEditedFieldForFieldEditor();
						AmiWebQueryFormPortlet form = editedFieldForFieldEditor.getForm();
						String formPnlId = form.getAmiLayoutFullAliasDotId();
						String fieldName = editedFieldForFieldEditor.getName();
						String name = "Form: " + formPnlId + " Field: " + fieldName;
						t1.addChild(new BasicWebMenuLink(name, true, "show_window_" + portletId, 3));
					}
				}
				t1.sort();
				r.addChild(t1);
				r.addChild(new BasicWebMenuLink("Dashboard Objects...", true, "show_objects").setBackgroundImage(AmiWebConsts.ICON_DASH_OBJECTS));
				r.addChild(new BasicWebMenuLink("Included Layouts...", true, "included_files").setBackgroundImage(AmiWebConsts.ICON_INCLUDED_LAYOUTS));
				r.addChild(new BasicWebMenuLink("Session Variables...", true, "var_table").setBackgroundImage(AmiWebConsts.ICON_SESSION_VARS));
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Dashboard Style...", true, "__global_style").setBackgroundImage(AmiWebConsts.ICON_DASH_STYLES));
				r.addChild(new BasicWebMenuLink("Dashboard Settings...", true, "dashboard_settings").setBackgroundImage(AmiWebConsts.ICON_DASH_SETTINGS));
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Custom Methods...", true, "layout_method_add").setBackgroundImage(AmiWebConsts.ICON_CUSTOM_METHODS));
				r.addChild(new BasicWebMenuLink("Custom Callbacks...", true, "layout_callbacks").setBackgroundImage(AmiWebConsts.ICON_CUSTOM_CALLBACKS));
				r.addChild(new BasicWebMenuLink("Custom Css...", true, "layout_custom_css").setBackgroundImage(AmiWebConsts.ICON_CUSTOM_CSS));

				AmiWebGuiServiceAdaptersManager gsam = this.getService().getGuiServiceAdaptersManager();
				boolean hasGuiServiceCustomCallbacks = false;
				for (String i : gsam.getAdapterIds()) {
					if (!gsam.getCallbacks(i).getAmiScriptCallbackDefinitions().isEmpty()) {
						hasGuiServiceCustomCallbacks = true;
						break;
					}
				}
				if (hasGuiServiceCustomCallbacks) {
					BasicWebMenu t = new BasicWebMenu("Custom Gui Service Callbacks", true);
					r.addChild(t);
					for (String i : gsam.getAdapterIds()) {
						if (!gsam.getCallbacks(i).getAmiScriptCallbackDefinitions().isEmpty()) {
							t.addChild(new BasicWebMenuLink(gsam.getDescription(i) + " Callbacks...", true, "layout_callbacks_guiservice_" + i));
						}
					}
				}
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Style Manager...", true, "style_manager").setBackgroundImage(AmiWebConsts.ICON_STYLE_MANAGER));
				r.addChild(new BasicWebMenuLink("Defaults Manager...", true, "set_defaults").setBackgroundImage(AmiWebConsts.ICON_SET_DEFAULTS));
				//				r.addChild(new BasicWebMenuLink("Realtime Objects Manager...", true, "rt_objects").setBackgroundImage(AmiWebConsts.ICON_SET_DEFAULTS));
				r.addChild(new BasicWebMenuDivider());
				r.addChild(new BasicWebMenuLink("Resource Manager...", true, "resource_manager").setBackgroundImage(AmiWebConsts.ICON_RSC_MANAGER));
				if (this.varsManager.isUserAdmin()) {
					r.addChild(new BasicWebMenuLink("Admin Tool...", true, "admin_tool").setBackgroundImage(AmiWebConsts.ICON_USER_SETTINGS));
				}
				if (!isLocked && (simulatorEnabled || service.isDebug())) {
					BasicWebMenu t = (BasicWebMenu) new BasicWebMenu("Advanced", true).setBackgroundImage(AmiWebConsts.ICON_ADVANCED);
					t.addChild(new BasicWebMenuLink("Upload Data", true, "data_upload").setBackgroundImage(AmiWebConsts.ICON_UPLOAD));
					t.addChild(new BasicWebMenuDivider());
					t.addChild(new BasicWebMenuLink("Simulator", true, "data_simulator").setBackgroundImage(AmiWebConsts.ICON_SIMULATOR));
					//					t.addChild(new BasicWebMenuLink("Debug", true, "data_debug").setBackgroundImage(AmiWebConsts.ICON_DEBUG));
					t.addChild(new BasicWebMenuLink("Inflate Support Stacktrace", true, "inflate_stacktrace").setBackgroundImage(AmiWebConsts.ICON_DEBUG));
					r.addChild(t);
				}
				r.addChild(new BasicWebMenuDivider());
				// custom context menus
				BasicWebMenu custMenus = this.desktop.getCustomContextMenu().generateCustomizeMenu();
				r.addChild(custMenus);

			}
		} else if ("help".equals(id)) {
			r.setStyle(getMenuStyle());
			r.addChild(new BasicWebMenuLink("About", true, "_about").setBackgroundImage(AmiWebConsts.ICON_ABOUT));
			if (inEditMode && !isLicenseDisabled) {
				r.addChild(new BasicWebMenuLink("License Info", true, "license_info").setBackgroundImage(AmiWebConsts.ICON_LICENSE_INFO));
				r.addChild(new BasicWebMenuLink("Enter/Update License", true, "license_input").setBackgroundImage(AmiWebConsts.ICON_LICENSE));
			}
			if (!isLocked)
				r.addChild(new BasicWebMenuLink("AMI Script Doc", true, "_amiscriptdoc").setBackgroundImage(AmiWebConsts.ICON_DOCUMENTATION));
			if (showMenuOption(AmiWebProperties.PROPERTY_AMI_SHOW_MENU_OPTION_DATA_STATISTICS, AmiWebProperties.VALUE_ALWAYS))
				r.addChild(new BasicWebMenuLink("Data Statistics", true, "data_schema").setBackgroundImage(AmiWebConsts.ICON_STATISTICS));
		} else if ("usermenus".equals(id)) {
			r.setStyle(getMenuStyle());
			WebMenu customMenu;
			try {
				customMenu = this.desktop.getCustomContextMenu().generateMenu();
				List<WebMenuItem> children = customMenu.getChildren();
				for (int i = 0; i < children.size(); i++) {
					children.get(i).setParent(null);
					r.add(children.get(i));
				}
			} catch (Exception e) {
				service.getPortletManager().showAlert("Error in custom menu.", e);
			}
		}
		return r;
	}
	private boolean showMenuOption(String name, String deflt) {
		String value = this.getManager().getTools().getOptional(name, deflt);
		if ("always".equalsIgnoreCase(value))
			return true;
		else if ("never".equalsIgnoreCase(value))
			return false;
		else if ("dev".equalsIgnoreCase(value))
			return inEditMode && (this.varsManager.isUserDev() || this.varsManager.isUserAdmin());
		else if ("admin".equalsIgnoreCase(value))
			return inEditMode && (this.varsManager.isUserAdmin());
		else
			LH.warning(log, "Bad value for ", name, ": ", value, "  (valid options: always,never,dev,admin)");
		return true;
	}
	public void buildCloudImport(BasicWebMenu sink, AmiWebCloudLayoutTree cloudLayouts, String prefix, boolean includeEmpty, boolean permitAll) {
		for (Entry<String, AmiWebCloudLayoutTree> entry : cloudLayouts.getChildren().entrySet()) {
			BasicWebMenu node = new BasicWebMenu(entry.getKey(), true);
			buildCloudImport(node, entry.getValue(), prefix, includeEmpty, permitAll);
			boolean isEmpty = node.getChildren().isEmpty();
			if (includeEmpty && isEmpty)
				sink.add(new BasicWebMenuLink(entry.getKey(), true, prefix + entry.getValue().getId()));
			else if (!isEmpty)
				sink.add(node);
		}
		for (Entry<String, String> entry : cloudLayouts.getLayoutNamesAndId().entrySet()) {
			boolean found = permitAll || service.getVarsManager().isPermittedLayout(AmiWebConsts.LAYOUT_SOURCE_CLOUD, entry.getValue());
			if (found) {
				BasicWebMenuLink node = new BasicWebMenuLink(entry.getKey(), true, prefix + entry.getValue());
				node.setBackgroundImage(AmiWebConsts.ICON_AMI_FILE);
				sink.add(node);
			}
		}
	}

	public void showWarningsDialog(boolean warnings, boolean debug) {
		AmiWebWarningDialogPortlet warningsPortlet = getSpecialPortlet(AmiWebWarningDialogPortlet.class);
		if (warningsPortlet == null) {
			warningsPortlet = new AmiWebWarningDialogPortlet(generateConfig(), service, service.getDebugManager(), warnings, debug);
			setSpecialPortlet(warningsPortlet);
			applyEditModeStyle(this.desktop.addChild("Logger", warningsPortlet));
			getManager().onPortletAdded(warningsPortlet);
		} else {
			this.desktop.bringToFront(warningsPortlet.getPortletId());
			warningsPortlet.showWarnings(warnings);
			warningsPortlet.showInfo(debug);
		}
	}
	public void cancelLink() {
		linkHelper.resetLinkingTarget();
		this.currentPortletId = null;
		new JsFunction(getManager().getPendingJs(), null, "amiStopLink").addParam(null).end();
		updateDashboard();
	}
	public void setInEditMode(boolean b) {
		if (b == inEditMode)
			return;
		if (b && isLocked)
			throw new IllegalStateException();
		inEditMode = b;
		PortletStyleManager styleManager = service.getPortletManager().getStyleManager();
		styleManager.setUseDefaultStyling(inEditMode);
		service.getUserFormStyleManager().setUseDefaultStyling(inEditMode);
		service.getUserDialogStyleManager().setUseDefaultStyling(inEditMode);
		updateDebugManagerSeverities();
		if (!inEditMode) {
			linkHelper.resetLinkingTarget();
			this.currentPortletId = null;
			new JsFunction(getManager().getPendingJs(), null, "amiStopLink").addParam(null).end();
		}
		this.getDesktopBar().updateConfigButton(getIsLocked(), getInEditMode());
		updateDashboard();
		updateHelpBubble();
		List<AmiWebDesktopListener> divs = new ArrayList<AmiWebDesktopListener>();
		PortletHelper.findPortletsByType(desktop, AmiWebDesktopListener.class, divs);
		for (AmiWebDesktopListener i : divs)
			i.onEditModeChanged(this, inEditMode);
		this.service.getAutosaveManager().onEditModeChanged(this, inEditMode);
		this.flagDesktopBackgroundNeedUpdate();

	}

	private boolean statusPanelNeedsUpdate;
	private boolean windowLinksNeedsUpdate;
	private boolean desktopBackgroundNeedUpdate;
	private String portletCutboard;
	private boolean dialogArrowNeedsUpdate;

	protected void reset() {
		this.currentPortletId = null;
		for (byte type : AmiDebugMessage.SEVERITY_TYPES)
			this.service.getDebugManager().clearMessages(type);
		this.portletClipboard = null;
		this.portletCutboard = null;
		this.linkHelper.resetLinkingTarget();
		this.updateDashboard();
	}
	void assignGlobalStyles(Map metadata) {
		if (this.stylePeer != null)
			this.stylePeer.close();
		this.stylePeer = new AmiWebStyledPortletPeer(this, this.service);
		this.stylePeer.initStyle();

		//this is for backwards compatibility
		this.stylePeer.initStyle((Map<String, Object>) metadata.get("amiStyle"));
	}

	protected void updateStatusPanel() {
		this.statusPanelNeedsUpdate = false;
		this.getDesktopBar().updateDashboard(this.linkHelper.isLinking());
		this.getDesktopBar().updateStatusPanel(this.debugWarningSetting, this.debugInfoSetting);
		String style = "_bg=" + this.getLayoutTitleBarColor() + "|style.borderStyle=solid|style.borderColor=" + this.getLayoutTitleBarBorderColor();
		String html = "<div class='ami_dashboard ami_3forge_logo' style='width:100%;height:100%'><img onclick='" + logo.generateCallback("logo_clicked")
				+ "' width='65px' height='20px' src='" + this.desktopBar.getLogoUrl() + "'>";
		if (!isLocked)
			html += "&nbsp;<img onclick='" + logo.generateCallback("show_menubar") + "' width='20px' height='20px' src='rsc/ami/menu.svg'>";
		html += "</div>";
		this.logo.setHtml(html);
		switch (menuBarPosition) {
			case MENUBAR_TOP:
			case MENUBAR_BOTTOM:
				break;
			case MENUBAR_TOP_LEFT:
				this.logo.setCssStyle(style + "|style.borderWidth=0px 1px 1px 0px|style.borderRadius=0px 0px 5px 0px");
				break;
			case MENUBAR_TOP_CENTER:
				this.logo.setCssStyle(style + "|style.borderWidth=0px 1px 1px 1px|style.borderRadius=0px 0px 5px 5px");
				break;
			case MENUBAR_TOP_RIGHT:
				this.logo.setCssStyle(style + "|style.borderWidth=0px 0px 1px 1px|style.borderRadius=0px 0px 0px 5px");
				break;
			case MENUBAR_BOTTOM_LEFT:
				this.logo.setCssStyle(style + "|style.borderWidth=1px 1px 0px 0px|style.borderRadius=0px 5px 0px 0px");
				break;
			case MENUBAR_BOTTOM_CENTER:
				this.logo.setCssStyle(style + "|style.borderWidth=1px 1px 0px 1px|style.borderRadius=5px 5px 0px 0px");
				break;
			case MENUBAR_BOTTOM_RIGHT:
				this.logo.setCssStyle(style + "|style.borderWidth=1px 0px 0px 1px|style.borderRadius=5px 0px 0px 0px");
				break;
		}
	}
	protected void updateDashboard() {
		if (this.desktop == null)
			return;//still in startup
		boolean changed = false;
		for (String s : this.desktop.getChildren().keySet()) {
			Window window = this.desktop.getWindow(s);
			if (!isSpecialPortlet(window.getPortlet())) {
				boolean isTransient = !(window.getPortlet() instanceof AmiWebDomObject) || ((AmiWebDomObject) window.getPortlet()).isTransient();
				window.setAllowTitleEdit(inEditMode && !isTransient, true);
				if (window.isHidden(true)) {
					if (!window.getCloseable(true) || window.getAllowMin(true) != this.inEditMode) {
						window.setCloseable(true, true);
						window.setAllowMin(this.inEditMode, true);
						changed = true;
					}
				} else if (window.getCloseable(true) != (inEditMode || this.desktop.isPlaceholder(window))) {
					window.setCloseable(inEditMode || this.desktop.isPlaceholder(window), true);
					changed = true;
				}
			}
		}
		for (AmiWebTabPortlet p : PortletHelper.findPortletsByType(this.desktop, AmiWebTabPortlet.class)) {
			p.onEditModeChanged(inEditMode);
		}
		if (changed && getVisible())
			this.desktop.layoutChildren();
		flagStatusPanelNeedsUpdate();
		flagUpdateWindowLinks();
		{
			this.service.getAmiQueryFormEditorsManager().flagUpdateDialogArrow();
			if (this.service.getAmiQueryFormEditorsManager().isDialogArrowNeedsUpdate())
				this.flagPendingAjax();
		}
		this.getDesktopBar().updateDropDown(getInEditMode());
	}
	public void flagUpdateWindowLinks() {
		if (this.windowLinksNeedsUpdate)
			return;
		this.windowLinksNeedsUpdate = true;
		flagStatusPanelNeedsUpdate();
		flagPendingAjax();
	}
	private void flagUpdateDialogArrow() {
		if (this.dialogArrowNeedsUpdate)
			return;
		this.dialogArrowNeedsUpdate = true;
		flagPendingAjax();
	}
	void flagStatusPanelNeedsUpdate() {
		if (this.statusPanelNeedsUpdate)
			return;
		this.statusPanelNeedsUpdate = true;
		flagPendingAjax();

	}
	private void flagDesktopBackgroundNeedUpdate() {
		if (this.desktopBackgroundNeedUpdate)
			return;
		this.desktopBackgroundNeedUpdate = true;
		flagPendingAjax();
	}

	@Override
	public void onAmiDebugMessage(AmiDebugManager manager, AmiDebugMessage message) {
		this.flagStatusPanelNeedsUpdate();
	}
	@Override
	public void onAmiDebugMessagesRemoved(AmiDebugManager manager, AmiDebugMessage message) {
		this.flagStatusPanelNeedsUpdate();
	}

	@Override
	public void onContextMenu(HtmlPortlet htmlPortlet, String action) {
	}

	@Override
	public void drainJavascript() {
		if (this.windowLinksNeedsUpdate) {
			this.windowLinksNeedsUpdate = false;
			updateHelpBubble();
			callJsFunction_amiEditDesktopEdit();
			callJsFunction_amiEditDesktop();
		}
		if (this.desktopBackgroundNeedUpdate) {
			this.desktopBackgroundNeedUpdate = false;
			if (inEditMode) {
				desktop.addOption(DesktopPortlet.OPTION_DESKTOP_STYLE, "style.background=" + this.getDeskBgCl() + " url('rsc/ami/desktop_edit.png')");
				this.linkHelper.buildAmiLinkingJs(getManager().getPendingJs());

			} else {
				desktop.addOption(DesktopPortlet.OPTION_DESKTOP_STYLE, "style.background=" + this.getDeskBgCl());
			}
		}

		if (this.statusPanelNeedsUpdate)
			updateStatusPanel();
		if (this.dialogArrowNeedsUpdate) {
			updateDialogArrow();
		}
		if (this.service.getAmiQueryFormEditorsManager().isDialogArrowNeedsUpdate())
			this.service.getAmiQueryFormEditorsManager().updateDialogArrow();
		super.drainJavascript();
	}
	private void callJsFunction_amiEditDesktopEdit() {
		new JsFunction(getManager().getPendingJs(), null, "amiEditDesktopMode").addParam(inEditMode).end();
	}
	private void callJsFunction_amiEditDesktop() {
		JsFunction jsfunc = new JsFunction(getManager().getPendingJs(), null, "amiEditDesktop").addParam(inEditMode).addParam(getJsObjectName());
		JsonBuilder json = jsfunc.startJson();
		json.startMap();
		getEditablePortletIds(desktop, json);
		json.endMap();
		json.end();
		boolean help = (desktop.getChildrenCount() == 1 && CH.first(desktop.getChildren().values()) instanceof AmiWebBlankPortlet && inEditMode);
		if (help)
			jsfunc.addParamQuoted("Hint: Green buttons will appear over configurable items when '<B>Layout Editor</B>' is on. Click the button to see options");
		else
			jsfunc.addParamQuoted(null);
		json = jsfunc.startJson();
		json.startList();
		if (inEditMode) {
			TreeMap<Integer, Window> windows = new TreeMap<Integer, Window>();
			for (String id : desktop.getChildren().keySet()) {
				Window win = desktop.getWindow(id);
				if (win == null)
					continue;
				if (!win.isMinimized()) {
					windows.put(win.getZindex(), win);
				}
			}
			for (Window child : windows.values()) {
				json.startMap();
				json.addKeyValue("x", child.getLeft());
				json.addKeyValue("y", child.getTop());
				json.addKeyValue("w", child.getWidth());
				json.addKeyValue("h", child.getHeight());
				json.addKey("links");
				json.startList();
				buildLinks(child.getPortlet(), json);
				json.endList();
				json.endMap();
			}
		}
		json.endList();
		json.end();

		//process window icons
		jsfunc.addParamQuoted(this.desktop.getPortletId());
		json = jsfunc.startJson();
		json.startList();
		for (Window i : this.desktop.getWindows()) {
			Portlet portlet = i.getPortlet();
			if (isSpecialPortlet(portlet) || service.getAmiQueryFormEditorsManager().isEditor(portlet) || !(portlet instanceof AmiWebPortlet))
				continue;
			AmiWebPortlet ap = (AmiWebPortlet) portlet;
			json.startMap();
			json.addKeyValueQuoted("id", i.getPortletId());
			json.addKeyValueQuoted("type", AmiWebManagerWindowsPortlet.getType(i, true));
			json.addKeyValue("d", ap.isTransient() || i.currentIsDefault());
			json.endMap();
		}
		json.endList();
		json.end();

		jsfunc.end();
	}
	private void updateDialogArrow() {
		if (arrowDialog != null) {
			StringBuilder pendingJs = getManager().getPendingJs();
			JsFunction js = new JsFunction(pendingJs, null, "amiDialogArrow");
			if (this.arrowField != null && this.arrowField.getFieldHorizontalCenterPosPx() != null && this.arrowField.getFieldVerticalCenterPosPx() != null) {
				Portlet portlet;
				if (this.arrowPortlet instanceof AmiWebAbstractPortlet) {
					portlet = ((AmiWebAbstractPortlet) this.arrowPortlet).getInnerPortlet();
				} else
					portlet = this.arrowPortlet;
				js.addParam(PortletHelper.getAbsoluteLeft(portlet) + this.arrowField.getFieldHorizontalCenterPosPx());
				js.addParam(PortletHelper.getAbsoluteTop(portlet) + this.arrowField.getFieldVerticalCenterPosPx());
			} else if (this.arrowX != -1 && this.arrowY != -1) {
				js.addParam(this.arrowX);
				js.addParam(this.arrowY);
			} else {
				js.addParam(AmiWebUtils.getAmiButtonX(arrowPortlet));
				js.addParam(AmiWebUtils.getAmiButtonY(arrowPortlet));
			}
			js.addParam(arrowDialog.getOuterLeft());
			js.addParam(arrowDialog.getOuterTop());
			js.addParam(arrowDialog.getOuterRight());
			js.addParam(arrowDialog.getOuterBottom());
			js.end();
		}

		this.dialogArrowNeedsUpdate = false;

	}
	private void getEditablePortletIds(Portlet pc, JsonBuilder json) {
		if (!pc.getVisible())
			return;
		if (pc instanceof AmiWebTabPortlet || pc instanceof AmiWebDividerPortlet || pc instanceof DesktopPortlet
				|| (pc instanceof AmiWebManagedPortlet && ((AmiWebManagedPortlet) pc).getShowConfigButtons())) {
			json.addKey(pc.getPortletId());
			json.startMap();
			if (pc instanceof AmiWebAbstractContainerPortlet) {
				json.addKeyValueQuoted("innerId", ((AmiWebAbstractContainerPortlet) pc).getInnerContainer().getPortletId());
			}
			if (pc instanceof AmiWebPortlet) {
				AmiWebPortlet aw = (AmiWebPortlet) pc;
				if (aw.isTransient())
					json.addKeyValue("isTransient", aw.isTransient());
				if (aw.isReadonlyLayout())
					json.addKeyValue("isReadOnly", aw.isReadonlyLayout());
				json.addKeyValueQuoted("label", aw.getAmiLayoutFullAliasDotId());
			} else
				json.addKeyValueQuoted("label", "");
			if (pc instanceof AmiWebQueryFormPortlet) {
				AmiWebQueryFormPortlet fp = (AmiWebQueryFormPortlet) pc;
				json.addKeyValue("designMode", fp.getInEditorMode());
			}
			if (pc instanceof AmiWebDividerPortlet) {
				AmiWebDividerPortlet pc2 = (AmiWebDividerPortlet) pc;
				//				AmiWebInnerDividerPortlet div = pc2.getInnerContainer();
				json.addKeyValueQuoted("type", "divider");
				if (!pc2.getIsLocked(true))
					json.addKeyValueQuoted("locked", "none");
				else if (pc2.getAlign(true) == AmiWebDividerPortlet.ALIGN_START)
					json.addKeyValueQuoted("locked", "top");
				else if (pc2.getAlign(true) == AmiWebDividerPortlet.ALIGN_END)
					json.addKeyValueQuoted("locked", "bottom");
				else
					json.addKeyValueQuoted("locked", "ratio");
				json.addKeyValue("isDefault", pc2.isTransient() || pc2.isCurrentOffsetDefault());
			} else if (pc instanceof AmiWebTabPortlet) {
				AmiWebTabPortlet tab = (AmiWebTabPortlet) pc;
				json.addKeyValueQuoted("type", "tab");
				json.addKeyValue("right", tab.getInnerContainer().getTabPortletStyle().getIsOnRight());
				json.addKeyValue("bottom", tab.getInnerContainer().getTabPortletStyle().getIsOnBottom());
				json.addKeyValue("d", !tab.isTransient() && tab.hasOverrides());
				json.addKeyValue("beginningPadding", tab.getInnerContainer().getTabPortletStyle().getInitialPadding());
			} else {
				json.addKeyValueQuoted("type", "other");
				if (pc instanceof AmiWebChartPlotPortlet) {
					AmiWebChartPlotPortlet awcpp = (AmiWebChartPlotPortlet) pc;
					if (awcpp.isTransient())
						json.addKeyValue("isTransient", awcpp.isTransient());
					if (awcpp.getChart().isReadonlyLayout())
						json.addKeyValue("isReadOnly", awcpp.getChart().isReadonlyLayout());
				} else if (pc instanceof AmiWebChartAxisPortlet) {
					AmiWebChartAxisPortlet awcap = (AmiWebChartAxisPortlet) pc;
					if (awcap.isTransient())
						json.addKeyValue("isTransient", awcap.isTransient());
					if (awcap.getChart().isReadonlyLayout())
						json.addKeyValue("isReadOnly", awcap.getChart().isReadonlyLayout());

				}
			}
			json.endMap();
		}
		if (pc instanceof PortletContainer)
			for (Portlet child : ((PortletContainer) pc).getChildren().values())
				getEditablePortletIds(child, json);

	}
	private void buildLinks(Portlet portletToLink, JsonBuilder sink) {
		if (portletToLink instanceof AmiWebAbstractContainerPortlet) {
			for (Portlet child : (((AmiWebAbstractContainerPortlet) portletToLink).getInnerContainer()).getChildren().values()) {
				buildLinks(child, sink);
			}
		} else if (portletToLink instanceof AmiWebPortlet) {
			AmiWebPortlet portlet = (AmiWebPortlet) portletToLink;

			for (AmiWebDmLink link : portlet.getDmLinksFromThisPortlet())
				buildLink(link, sink, true);
			for (AmiWebDmLink link : portlet.getDmLinksToThisPortlet())
				buildLink(link, sink, false);
		}
	}
	private void buildLink(AmiWebDmLink link, JsonBuilder sink, boolean isInitiator) {
		AmiWebPortlet portlet = isInitiator ? link.getSourcePanelNoThrow() : link.getTargetPanelNoThrow();
		AmiWebPortlet remotePortlet = !isInitiator ? link.getSourcePanelNoThrow() : link.getTargetPanelNoThrow();
		if (remotePortlet == null) {
			return;
		}
		if (portlet == null) {
			return;
		}
		final Tab toTab, frTab;
		final Portlet toPortlet, frPortlet;
		boolean sameWindow = PortletHelper.findCommonParent(portlet, remotePortlet) != desktop;
		if (remotePortlet.getVisible() && portlet.getVisible()) {
			toTab = frTab = null;
			if (sameWindow) { //both visible, same window
				if (!isInitiator)
					return;
				frPortlet = portlet;
				toPortlet = remotePortlet;
			} else {
				//both visible, different windows
				if (!isCloser(portlet, remotePortlet))
					return; //front window wins
				if (isInitiator) {
					frPortlet = portlet;
					toPortlet = remotePortlet;
				} else {
					toPortlet = portlet;
					frPortlet = remotePortlet;
				}
			}
		} else if (portlet.getVisible()) {
			PortletContainer visibleParent = PortletHelper.findFirstVisibleParent(remotePortlet);
			if (visibleParent instanceof AmiWebTabPortlet) {
				Tab visibleTab = null;
				for (Tab tab : ((AmiWebTabPortlet) visibleParent).getInnerContainer().getTabs()) {
					if (PortletHelper.isParentOfChild(tab.getPortlet(), remotePortlet)) {
						visibleTab = tab;
						break;
					}
				}
				if (sameWindow) {
					if (isInitiator) { //point to tab on same window
						frTab = null;
						frPortlet = portlet;
						toTab = visibleTab;
						toPortlet = null;
					} else { //point from tab on same window
						frTab = visibleTab;
						frPortlet = null;
						toTab = null;
						toPortlet = portlet;
					}
				} else {
					if (isCloser(portlet, remotePortlet)) {
						if (isInitiator) {
							frPortlet = portlet;
							toTab = visibleTab;
							frTab = null;
							toPortlet = null;
						} else {//point from a tab to another window
							toPortlet = portlet;
							frTab = visibleTab;
							toTab = null;
							frPortlet = null;
						}
					} else
						return;
				}
			} else
				return;
		} else if (remotePortlet.getVisible()) {
			PortletContainer visibleParent = PortletHelper.findFirstVisibleParent(portlet);
			if (visibleParent instanceof AmiWebTabPortlet) {
				Tab visibleTab = null;
				for (Tab tab : ((AmiWebTabPortlet) visibleParent).getInnerContainer().getTabs()) {
					if (PortletHelper.isParentOfChild(tab.getPortlet(), portlet)) {
						visibleTab = tab;
						break;
					}
				}
				if (!sameWindow && isCloser(portlet, remotePortlet)) {
					if (isInitiator) {
						frTab = visibleTab;
						toPortlet = remotePortlet;
						toTab = null;
						frPortlet = null;
					} else {
						frTab = null;
						toPortlet = null;
						toTab = visibleTab;
						frPortlet = remotePortlet;
					}
				} else
					return;
			} else
				return;
		} else
			return;
		sink.startMap();
		if (frPortlet != null) {
			sink.addKeyValueQuoted("frPortlet", frPortlet.getPortletId());
		} else if (frTab != null) {
			sink.addKeyValueQuoted("frTab", frTab.getPortlet().getParent().getPortletId());
			sink.addKeyValueQuoted("frTabIndex", frTab.getLocation());
		}
		if (toPortlet != null) {
			sink.addKeyValueQuoted("toPortlet", toPortlet.getPortletId());
		} else if (toTab != null) {
			sink.addKeyValueQuoted("toTab", toTab.getPortlet().getParent().getPortletId());
			sink.addKeyValueQuoted("toTabIndex", toTab.getLocation());
		}
		sink.endMap();
	}
	private boolean isCloser(Portlet p1, Portlet p2) {
		int thisZ = this.desktop.getWindow(PortletHelper.findPortletWithParentByType(p1, DesktopPortlet.class).getPortletId()).getZindex();
		int thatZ = this.desktop.getWindow(PortletHelper.findPortletWithParentByType(p2, DesktopPortlet.class).getPortletId()).getZindex();
		return thisZ >= thatZ;
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {
		if (inEditMode) {
			if (newPortlet instanceof AmiWebPortlet) {
				if (newPortlet.getParent() == this.getDesktop())
					updateDashboard();
			} else if (newPortlet instanceof AmiWebQueryFieldWizardPortlet) {
				updateDashboard();
			}
		}
	}

	@Override
	public void onPortletClosed(Portlet newPortlet) {
		if (OH.eq(this.portletCutboard, newPortlet.getPortletId()))
			this.portletCutboard = null;
		if (OH.eq(this.portletClipboard, newPortlet.getPortletId()))
			this.portletClipboard = null;
		if (newPortlet instanceof AmiWebPortlet) {
			if (inEditMode)
				updateDashboard();
			AmiWebPortlet ap = (AmiWebPortlet) newPortlet;
			this.service.onAmiPortletClosed(ap);
			((AmiWebDmManagerImpl) this.service.getDmManager()).onAmiPortletClosed(ap);
			boolean hidden = this.service.getLayoutFilesManager().getLayoutByFullAlias(ap.getAmiLayoutFullAlias()).getHiddenPanelByPanelId(ap.getAmiPanelId()) != null;
			this.service.fireAmiWebPanelRemoved(ap, hidden);
			if (newPortlet instanceof AmiWebPortlet) {
				AmiWebPortlet p = (AmiWebPortlet) newPortlet;
				Collection<AmiWebDmLink> fromLinks = p.getDmLinksFromThisPortlet();
				Collection<AmiWebDmLink> toLinks = p.getDmLinksToThisPortlet();
				if (!fromLinks.isEmpty() || !toLinks.isEmpty()) {
					List<AmiWebDmLink> links = new ArrayList<AmiWebDmLink>(fromLinks.size() + toLinks.size());
					links.addAll(fromLinks);
					links.addAll(toLinks);
					if (!hidden) {
						for (AmiWebDmLink i : links)
							service.getDmManager().removeDmLink(i.getLinkUid());
					} else {
						for (AmiWebDmLink i : links)
							i.bind();
					}
				}
			}
		} else if (isSpecialPortlet(newPortlet)) {
			if (this.specialPortlets.get(newPortlet.getClass()) == newPortlet)
				this.specialPortlets.remove(newPortlet.getClass());
		} else if (inEditMode && (newPortlet instanceof AmiWebQueryFieldWizardPortlet || newPortlet instanceof AmiWebEditStylePortlet)) {
			updateDashboard();
		}
	}

	public String getPortletClipboard() {
		return this.portletClipboard;
	}
	public String getPortletCutboard() {
		return this.portletCutboard;
	}
	public boolean isSpecialPortlet(Portlet newPortlet) {
		return newPortlet instanceof AmiWebSpecialPortlet;
	}
	@Override
	protected void layoutChildren() {
		flagPendingAjax();
		super.layoutChildren();
	}

	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}

	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}

	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
	}

	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}

	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
	}

	public static char WIN_TYPE_MAXIMIZED = AmiWebManagerWindowsPortlet.TYPE_MAXIMIZED;
	public static char WIN_TYPE_REGULAR = AmiWebManagerWindowsPortlet.TYPE_REGULAR;
	public static char WIN_TYPE_HIDDEN = AmiWebManagerWindowsPortlet.TYPE_HIDDEN;

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("update_dashboard".equals(callback)) {
			updateDashboard();
		} else if ("set_default_window".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			Window window = desktop.getWindow(id);
			window.setDefaultLocationToCurrent();
			window.setDefaultStateToCurrent();
			flagUpdateWindowLinks();
		} else if ("set_default_tab".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			AmiWebTabPortlet tab = (AmiWebTabPortlet) getManager().getPortlet(id);
			tab.setOverrideToDefault();
			flagUpdateWindowLinks();
		} else if ("set_default".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			Portlet portlet = getManager().getPortlet(id);
			if (portlet instanceof AmiWebDividerPortlet) {
				AmiWebDividerPortlet divider = (AmiWebDividerPortlet) portlet;
				divider.setDefaultOffsetPctToCurrent();
				flagUpdateWindowLinks();
			} else if (portlet instanceof AmiWebTabPortlet) {
				AmiWebTabPortlet tab = (AmiWebTabPortlet) portlet;
				tab.setOverrideToDefault();
				flagUpdateWindowLinks();
			}
		} else if ("edit_window".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			Window window = desktop.getWindow(id);
			WebMenu menu = new BasicWebMenu();
			AmiWebAbstractPortlet p = (AmiWebAbstractPortlet) window.getPortlet();
			menu.add(new BasicWebMenuLink("CONFIGURE WINDOW", false, "").setCssStyle(AmiWebConsts.TITLE_CSS));
			if (p.isTransient()) {
				menu.add(new BasicWebMenuLink("* TRANSIENT", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
			} else if (getService().getLayoutFilesManager().getLayout().isReadonly()) {
				menu.add(new BasicWebMenuLink("* READONLY", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
			} else {
				menu.add(new BasicWebMenuLink("Rename...", true, "window_rename_" + id));
				menu.add(new BasicWebMenuLink("Style (All Windows)...", true, "__global_style").setBackgroundImage(AmiWebConsts.ICON_STYLING));
				BasicWebMenu type = new BasicWebMenu("Type...", true);
				type.setBackgroundImage(AmiWebConsts.ICON_SETTINGS);
				char winType = getWindowType(window, false);
				type.add(new BasicWebMenuLink("Maximized, No Header", true, "window_settype_max_" + id)
						.setCssStyle(winType == WIN_TYPE_MAXIMIZED ? "className=ami_menu_checked" : ""));
				type.add(new BasicWebMenuLink("Regular", true, "window_settype_reg_" + id).setCssStyle(winType == WIN_TYPE_REGULAR ? "className=ami_menu_checked" : ""));
				type.add(new BasicWebMenuLink("Hidden", true, "window_settype_hid_" + id).setCssStyle(winType == WIN_TYPE_HIDDEN ? "className=ami_menu_checked" : ""));
				menu.add(type);
				menu.add(new BasicWebMenuLink("Location In Menu Bar...", true, "window_menubar_location_" + id));
				boolean change = !window.currentIsDefault();
				menu.add(new BasicWebMenuLink("Set Current as Default", change, "window_default_loc_" + id));
				menu.add(new BasicWebMenuLink("Reset to Default", change, "window_reset_default_loc_" + id));
				//				menu.add(new BasicWebMenuLink("Set Z-Index as Default (All Windows)", true, "window_default_zindex"));
				//				menu.add(new BasicWebMenuLink("Clear Default Location", window.hasDefaultLocation(), "window_clear_default_loc_" + id));
				//				menu.add(new BasicWebMenuLink("Clear Default Z-Index (All Windows)", window.hasDefaultZIndex(), "window_clear_default_zindex"));
				//				menu.add(new BasicWebMenuLink("Edit Window Default Settings", true, "window_default_settings_" + id));
			}
			getManager().showContextMenu(menu, this, window.getLeft() + window.getWidth() / 2, window.getTop() + 17);

		} else if ("edit_portlet".equals(callback)) {
			String id = CH.getOrThrow(Caster_String.INSTANCE, attributes, "id");
			this.currentPortletId = id;
			if (this.linkHelper.handlerCallback(callback, attributes))
				return;
			showContextMenuForPortlet(id, -1, -1);
		} else
			super.handleCallback(callback, attributes);
	}
	public static char getWindowType(Window w, boolean currentState) {
		return AmiWebManagerWindowsPortlet.getType(w, currentState);
	}
	public void showContextMenuForPortlet(String id, int x, int y) {
		this.currentPortletId = id;
		final Portlet portlet = getManager().getPortlet(id);

		BasicWebMenu headMenu = populateMenuForPortlet(portlet, new BasicWebMenu());

		RootPortlet rootPortlet = PortletHelper.findParentByType(portlet, RootPortlet.class);
		rootPortlet.showContextMenu(headMenu, this, x, y);
	}

	public static BasicWebMenu populateMenuForPortlet(Portlet portlet, BasicWebMenu headMenu) {

		if (portlet instanceof WebMenuLinkListener)
			headMenu.setListener((WebMenuLinkListener) portlet);
		if (portlet instanceof AmiWebManagedPortlet) {
			headMenu.add(new BasicWebMenuLink(AmiWebDesktopPortlet.describeAliasPanelId((AmiWebManagedPortlet) portlet), false, "").setCssStyle(AmiWebConsts.TITLE_CSS));
		}
		if (portlet instanceof AmiWebDomObject)
			if (((AmiWebDomObject) portlet).isTransient())
				headMenu.add(new BasicWebMenuLink("* TRANSIENT", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));
		if (portlet instanceof AmiWebAliasPortlet && ((AmiWebAliasPortlet) portlet).isReadonlyLayout())
			headMenu.add(new BasicWebMenuLink("* READONLY", false, "").setCssStyle(AmiWebConsts.TITLE_CSS2));

		if (portlet instanceof AmiWebManagedPortlet) {
			AmiWebManagedPortlet amiManagedPortlet = (AmiWebManagedPortlet) portlet;
			headMenu.add(new BasicWebMenuDivider());
			amiManagedPortlet.populateConfigMenu(headMenu);
			amiManagedPortlet.populateLowerConfigMenu(headMenu);
		}
		return headMenu;
	}

	public static String describeAliasPanelId(AmiWebManagedPortlet p) {
		StringBuilder sb = new StringBuilder();
		sb.append(p.getConfigMenuTitle().toUpperCase());
		if (p instanceof AmiWebPortlet) {
			AmiWebPortlet mp = (AmiWebPortlet) p;
			sb.append(" (").append(mp.getAmiLayoutFullAliasDotId());
			sb.append(")");
		}
		return sb.toString();
	}

	@Override
	public void onMenuItem(String id) {
		getService().getSecurityModel().assertPermitted(this, id, "show_window_*,cust_menu_action_*");
		if ("visit_3forge_website".equals(id)) {
			String url = "https://3forge.com/products.html";
			new JsFunction(getManager().getPendingJs(), "window", "open").addParamQuoted(url).end();
		} else if (id.startsWith("window_")) {
			if (id.startsWith("window_rename_")) {
				String windowId = SH.stripPrefix(id, "window_rename_", true);
				String windowName = this.desktop.getWindow(windowId).getName();
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "", ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
						new FormPortletTextField("Rename Window: ").setValue(windowName));
				dialog.setCorrelationData(windowId).setCallback("WINDOW_RENAME");
				getManager().showDialog("Edit Window Name", dialog);
			} else if (id.startsWith("window_settype_reg_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_settype_reg_", true));
				AmiWebManagerWindowsPortlet.applyType(AmiWebManagerWindowsPortlet.TYPE_REGULAR, w, true);
			} else if (id.startsWith("window_settype_max_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_settype_max_", true));
				AmiWebManagerWindowsPortlet.applyType(AmiWebManagerWindowsPortlet.TYPE_MAXIMIZED, w, true);
			} else if (id.startsWith("window_settype_hid_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_settype_hid_", true));
				AmiWebManagerWindowsPortlet.applyType(AmiWebManagerWindowsPortlet.TYPE_HIDDEN, w, true);
			} else if (id.startsWith("window_menubar_location_")) {
				String windowId = SH.stripPrefix(id, "window_menubar_location_", true);
				Window w = desktop.getWindow(windowId);
				ConfirmDialogPortlet panel = new ConfirmDialogPortlet(generateConfig(), "Edit Window's Menu Bar Location:", ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
						new FormPortletTextField("Menu Name:"));
				panel.setInputFieldValue(getWindowMenuLocation(w));
				panel.setCallback("WINDOW_MENUBAR_LOCATION");
				panel.setCorrelationData(windowId);
				getManager().showDialog("Edit Window Location", panel);
			} else if (id.startsWith("window_reset_default_loc_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_reset_default_loc_", true));
				w.setPosition(w.getDefaultLeft(), w.getDefaultTop(), w.getDefaultWidth(), w.getDefaultHeight());
			} else if (id.startsWith("window_default_loc_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_default_loc_", true));
				w.setDefaultLocationToCurrent();
				w.setDefaultStateToCurrent();
				flagUpdateWindowLinks();
			} else if (id.startsWith("window_default_settings_")) {
				Window w = desktop.getWindow(SH.stripPrefix(id, "window_default_settings_", true));
				getManager().showDialog("Edit Default Window Settings", new AmiWebDesktopWindowDefaultSettingsPortlet(generateConfig(), w));
			}

		} else if ("__global_style".equals(id)) {
			getManager().showDialog("Edit Dashboard Styles", new AmiWebEditStylePortlet(this.stylePeer, generateConfig()), 550, getHeight() - 200);
		} else if (this.desktop.getCustomContextMenu().isCustomContextMenuAction(id)) {
			this.desktop.getCustomContextMenu().processCustomContextMenuAction(id);
		} else {
			Portlet portlet = getManager().getPortlet(currentPortletId);
			if (portlet instanceof AmiWebPortlet)
				((AmiWebPortlet) portlet).onAmiContextMenu(id);
			else if (portlet instanceof AmiWebManagedPortlet)
				((AmiWebManagedPortlet) portlet).onAmiContextMenu(id);
			flagUpdateWindowLinks();
		}
	}

	public void renameAlias(String cpid) {
		AmiWebAliasPortlet target = (AmiWebAliasPortlet) this.getManager().getPortlet(cpid);
		AmiWebMovePortletToLayoutPortlet itp = new AmiWebMovePortletToLayoutPortlet(generateConfig(), target);
		getManager().showDialog("Move to Layout", itp);
	}
	public void replacePortlet(String old, AmiWebAliasPortlet nuw) {
		if (nuw.getParent() != null)
			nuw.getParent().removeChild(nuw.getPortletId());
		AmiWebAliasPortlet portlet = (AmiWebAliasPortlet) getManager().getPortlet(old);
		if (OH.ne("", portlet.getAmiLayoutFullAlias())) {
			service.getLayoutFilesManager().getLayoutByFullAlias(portlet.getAmiLayoutFullAlias()).onPanelReplaced(portlet);
		}
		portlet.getParent().replaceChild(old, nuw);

		deletePanel(portlet.getPortletId(), "".equals(portlet.getAmiLayoutFullAlias()));
	}
	public boolean onCopy(AmiWebAbstractPortlet portlet) {
		String cpid = portlet.getPortletId();
		this.portletClipboard = cpid;
		this.portletCutboard = null;
		return true;
	}
	public boolean onCut(AmiWebAbstractPortlet portlet) {
		String cpid = portlet.getPortletId();
		Portlet removed = getManager().getPortlet(cpid);
		if (desktop.isInTearout(removed) && (removed.getParent() == this.desktop || removed.getParent() instanceof RootPortlet)) {
			getManager().showAlert("You cannot cut this panel because it is currently undocked. Redock this panel first and then cut it.");
			return true;
		}
		this.portletCutboard = cpid;
		this.portletClipboard = null;
		return true;
	}
	public boolean onPaste(String direction, boolean includeHangingLinks, AmiWebAbstractPortlet target) {
		Map<String, Object> config = null;
		Boolean isCopy = null;
		String sourcePortlet = null;
		if (portletClipboard != null) {
			isCopy = true;
			sourcePortlet = this.portletClipboard;
		} else if (portletCutboard != null) {
			isCopy = false;
			includeHangingLinks = true;
			sourcePortlet = this.portletCutboard;
		} else {
			getManager().showAlert("Please put a panel into the clipboard");
			return true;
		}

		Portlet source = getManager().getPortlet(sourcePortlet);
		if (isCopy == false && PortletHelper.isParentOfChild(source, target)) {
			getManager().showAlert("Cannot cut and panel over itself");
			return true;
		}
		if (isCopy == false && source == target) {
			getManager().showAlert("Cannot paste panel over itself");
			return true;
		}

		if (source == null) {
			getManager().showAlert("Source Portlet no longer available");
			return true;
		}
		// Get the configuration
		config = AmiWebLayoutHelper.exportConfiguration(getService(), sourcePortlet, includeHangingLinks, false, false);

		// Add additional panels 
		if (direction != null) {
			AmiWebAliasPortlet srcPortlet = (AmiWebAliasPortlet) source;
			AmiWebBlankPortlet blankSource = newAmiWebAmiBlankPortlet(srcPortlet.getAmiLayoutFullAlias());
			AmiWebLayoutHelper.putTargetInDivider(this, blankSource, direction, target.getPortletId());
			target = blankSource;
		}
		// If is cut, replace old portlet with a blank portlet
		if (isCopy == false) {
			AmiWebAliasPortlet srcPortlet = (AmiWebAliasPortlet) source;
			this.replacePortlet(srcPortlet.getPortletId(), newAmiWebAmiBlankPortlet(srcPortlet.getAmiLayoutFullAlias()));
		}

		AmiWebAliasPortlet imported = (AmiWebAliasPortlet) AmiWebLayoutHelper.importConfiguration(service, config, target.getPortletId(), false);
		this.replacePortlet(target.getPortletId(), imported);
		target.close();
		if (isCopy == false) {
			this.portletClipboard = this.portletCutboard;
			this.portletCutboard = null;
		}
		return false;

	}

	public void showDividerEditor(AmiWebDividerPortlet newChild) {
		if (AmiWebConsts.STYLE_EDITOR_SHOW.equals(this.varsManager.getSetting(AmiWebConsts.USER_SETTING_SHOW_SETTING_DIVIDER))) {
			getService().getSecurityModel().assertPermitted(this, "settings");
			getManager().showDialog("Settings", newChild.showSettingsPortlet());
		}
	}
	public void displayRecreateMessage(String callback, String cpid) {
		Portlet p = getManager().getPortlet(cpid);
		String title = p.getTitle();
		if (p instanceof AmiWebPortlet) {
			if (((AmiWebPortlet) p).isReadonlyLayout()) {
				getManager().showAlert("Can not delete readonly panel");
				return;
			}
			title = ((AmiWebPortlet) p).getAmiPanelId();
		}
		ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to recreate '<B>" + title + "</B>' panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL, this);
		cdp.setCallback(callback);
		cdp.setCorrelationData(cpid);
		getManager().showDialog("Recreate Table / Display...", cdp);
	}
	public void addFilter(String position, AmiWebDmPortlet p) {
		if (p.getService().getDmManager().getDmByAliasDotName(CH.first(p.getUsedDmAliasDotNames())) == null) {
			getManager().showAlert("Panel must have underlying datamodel");
			return;
		}
		byte pos = parsePosition(position);
		String dmid = CH.first(p.getUsedDmAliasDotNames());
		String tb = CH.first(p.getUsedDmTables(dmid));
		AmiWebDm dm = p.getService().getDmManager().getDmByAliasDotName(dmid);
		AmiWebDmTableSchema table = dm.getResponseOutSchema().getTable(tb);
		getManager().showDialog("Add Filter", new AmiWebFilterWizPortlet(generateConfig(), p, table, pos));
	}
	public static byte parsePosition(String position) {
		if ("left".equalsIgnoreCase(position)) {
			return LEFT;
		} else if ("above".equalsIgnoreCase(position)) {
			return ABOVE;
		} else if ("right".equalsIgnoreCase(position)) {
			return RIGHT;
		} else if ("below".equalsIgnoreCase(position)) {
			return BELOW;
		} else if ("popout".equalsIgnoreCase(position)) {
			return POPOUT;
		} else if ("replace".equalsIgnoreCase(position)) {
			return REPLACE;
		} else
			return -1;
	}

	public final static byte LEFT = 1;
	public final static byte RIGHT = 2;
	public final static byte ABOVE = 3;
	public final static byte BELOW = 4;
	public final static byte POPOUT = 5;
	public final static byte REPLACE = 6;

	public AmiWebDividerPortlet addAdjacentTo(String sourceId, Portlet target, byte position) {
		return addAdjacentTo(sourceId, target, position, false);
	}
	public AmiWebDividerPortlet addAdjacentTo(String sourceId, Portlet target, byte position, boolean isTransient) {
		if (position == POPOUT) {
			createNewWindow((AmiWebPortlet) target);
			return null;
		}
		final Portlet p1 = target;
		final boolean isFirst = position == LEFT || position == ABOVE;
		boolean vertical = position == LEFT || position == RIGHT;
		final AmiWebAliasPortlet removed = (AmiWebAliasPortlet) getManager().getPortlet(sourceId);
		final String alias = removed.getAmiParent().getAmiLayoutFullAlias();
		final PortletContainer parent = removed.getParent();
		final AmiWebDividerPortlet divider = newDividerPortlet(generateConfig(), vertical, isFirst ? p1 : removed, isFirst ? removed : p1, alias, isTransient);
		parent.replaceChild(removed.getPortletId(), divider);
		getManager().onPortletAdded(divider);
		int size = vertical ? divider.getWidth() : divider.getHeight();
		if (size > 300) {
			if (isFirst)
				divider.getInnerContainer().setOffsetFromTopPx(200);
			else
				divider.getInnerContainer().setOffsetFromBottomPx(200);
		}
		divider.setDefaultOffsetPctToCurrent();
		return divider;
	}
	public AmiWebDividerPortlet addAdjacentToStacked(Portlet source, Portlet target, byte position, boolean homogeneous) {
		boolean vertical = position == LEFT || position == RIGHT; // []|[]
		AmiWebAbstractContainerPortlet parent = AmiWebUtils.getParentAmiContainer(source.getParent());
		if (parent instanceof AmiWebDividerPortlet) { // source is in a divider
			DividerPortlet divider = ((AmiWebDividerPortlet) parent).getInnerContainer();
			if ((vertical && divider.isVertical()) || (!vertical && !divider.isVertical())) { // source divider along same axis as position
				// at "end" of divider matching position? 
				boolean before = position == LEFT || position == ABOVE;
				Portlet end = before ? AmiWebUtils.getFirstInDividers(AmiWebUtils.getRootDividerAlongAxis((AmiWebDividerPortlet) parent))
						: AmiWebUtils.getLastInDividers(AmiWebUtils.getRootDividerAlongAxis((AmiWebDividerPortlet) parent));
				if (source == end) { // yes
					return addAdjacentTo(source.getPortletId(), target, position);
				} else { // no
					// same type required? 
					if (homogeneous) { // yes
						// check if all portlets in adjacent stack are of the same type as target
						Portlet adjacent = AmiWebUtils.getAdjacentPortletAlongDividerAxis(source, before);
						// yes
						if (AmiWebUtils.allPortletsAlongDividerAxisMatchType(target.getClass(), adjacent)) {
							// add to stack
							AmiWebDividerPortlet newDivider = addPanelToStack(adjacent, target, position, false);
							AmiWebUtils.distributeDividers(AmiWebUtils.getRootDividerAlongAxis(newDivider));
							return newDivider;
						}
						// no 
						// start new stack
						else {
							return addAdjacentTo(source.getPortletId(), target, position);
						}
					} else { // no
						// add to stack
						Portlet adjacent = AmiWebUtils.getAdjacentPortletAlongDividerAxis(source, before);
						AmiWebDividerPortlet newDivider = addPanelToStack(adjacent, target, position, false);
						AmiWebUtils.distributeDividers(AmiWebUtils.getRootDividerAlongAxis(newDivider));
						return newDivider;
					}
				}
			} else { // source divider NOT along same axis as position
				return addAdjacentTo(source.getPortletId(), target, position);
			}
		} else { // source is NOT in a divider
			return addAdjacentTo(source.getPortletId(), target, position);
		}
	}
	private AmiWebDividerPortlet addPanelToStack(Portlet adjacent, Portlet target, byte position, boolean first) {
		boolean vertical = position == ABOVE || position == BELOW; // Orientation of dividers in stack should be orthogonal to the "vertical" variable defined in addAdjacentToStacked(...)
		byte newPos = vertical ? (first ? LEFT : RIGHT) : (first ? ABOVE : BELOW);
		if (adjacent instanceof AmiWebDividerPortlet && vertical == ((AmiWebDividerPortlet) adjacent).isVertical()) {
			if (first) {
				return addAdjacentTo(AmiWebUtils.getFirstInDividers((AmiWebDividerPortlet) adjacent).getPortletId(), target, newPos);
			} else {
				return addAdjacentTo(AmiWebUtils.getLastInDividers((AmiWebDividerPortlet) adjacent).getPortletId(), target, newPos);
			}
		} else {
			return addAdjacentTo(adjacent.getPortletId(), target, newPos);
		}
	}

	public AmiWebAliasPortlet newPortlet(String builderId, String layoutFullAlias) {
		PortletManager manager = getManager();
		PortletBuilder<?> portletBuilder = manager.getPortletBuilder(builderId);
		AmiWebAliasPortlet r = (AmiWebAliasPortlet) portletBuilder
				.buildPortlet(new BasicPortletConfig(getManager(), manager.generateId(), portletBuilder.getPortletBuilderId(), false));
		registerNewPortlet(r, layoutFullAlias);
		return r;
	}

	public void registerNewPortlet(AmiWebAliasPortlet r, String layoutFullAlias) {
		if (r instanceof AmiWebAliasPortlet) {
			String generateId = AmiWebService.generateId(r);
			if (r.isTransient())
				generateId = r.getService().getVarsManager().toTransientId(generateId);
			else
				generateId = r.getService().getVarsManager().fromTransientId(generateId);
			r.setAdn(AmiWebUtils.getFullAlias(layoutFullAlias, service.getNextPanelId(layoutFullAlias, generateId)));
			AmiWebAliasPortlet awp = (AmiWebAliasPortlet) r;
			awp.onAmiInitDone();
			if (r instanceof AmiWebPortlet) {
				this.service.fireAmiWebPanelAdded((AmiWebPortlet) r);
			}
		}
		if (r instanceof AmiWebAliasPortlet) {
			AmiWebAliasPortlet awp = (AmiWebAliasPortlet) r;
			service.registerAmiUserPrefId(awp.getAmiUserPrefId(), awp);
		}
		getManager().onPortletAdded(r);
	}
	public AmiWebBlankPortlet newAmiWebAmiBlankPortlet(String layoutFullAlias) {
		return newAmiWebAmiBlankPortlet(layoutFullAlias, false);
	}
	public AmiWebBlankPortlet newAmiWebAmiBlankPortlet(String layoutFullAlias, boolean isTransient) {
		AmiWebBlankPortlet r = (AmiWebBlankPortlet) getManager().buildPortlet(AmiWebBlankPortlet.Builder.ID);
		r.setTransient(isTransient);
		registerNewPortlet(r, layoutFullAlias);
		return r;
	}

	public AmiWebDividerPortlet newDividerPortlet(PortletConfig generateConfig, boolean vertical, Portlet first, Portlet second, String layoutFullAlias, boolean isTransient) {
		AmiWebDividerPortlet r = (AmiWebDividerPortlet) getManager().buildPortlet(AmiWebDividerPortlet.Builder.ID);
		r.getInnerContainer().setVertical(vertical);
		r.getInnerContainer().setFirst(first);
		r.getInnerContainer().setSecond(second);
		if (isTransient)
			r.setTransientNoRecurse();
		registerNewPortlet(r, layoutFullAlias);
		return r;
	}

	public AmiWebTabPortlet newTabPortlet(String layoutFullAlias) {
		AmiWebTabPortlet r = (AmiWebTabPortlet) getManager().buildPortlet(AmiWebTabPortlet.Builder.ID);
		r.getInnerContainer().setIsCustomizable(false);
		registerNewPortlet(r, layoutFullAlias);
		return r;
	}

	public void onUserWrapChild(String childPortletId, AmiWebAbstractContainerPortlet newChild) {
		Portlet removed = getManager().getPortlet(childPortletId);
		PortletContainer parent = removed.getParent();
		parent.replaceChild(childPortletId, newChild);
		if (newChild instanceof AmiWebDividerPortlet) {
			((AmiWebDividerPortlet) newChild).getInnerContainer().setSecond(newChild);
		} else
			newChild.getInnerContainer().addChild(removed);
		getManager().onPortletAdded(newChild);
	}

	public void moveToNewWindow(String childPortletId) {
		AmiWebAliasPortlet temp = (AmiWebAliasPortlet) getManager().getPortlet(childPortletId);
		PortletContainer parent = temp.getParent();
		if (parent instanceof AmiWebTabPortlet && parent.getChildrenCount() > 1) {
			parent.removeChild(childPortletId);
		} else {
			AmiWebAliasPortlet t = (AmiWebAliasPortlet) temp.getAmiParent();
			parent.replaceChild(childPortletId, newAmiWebAmiBlankPortlet(t.getAmiLayoutFullAlias()));
		}
		desktop.addChild(temp.getTitle(), temp);
	}

	@Override
	public void onMenuDismissed() {
		linkHelper.handleMenuDismissed();
		if (inEditMode)
			updateDashboard();
	}

	@Override
	public void onLocationChanged(Portlet portlet) {
		if (portlet instanceof AmiWebPortlet) {
			AmiWebPortlet ap = (AmiWebPortlet) portlet;
			if (inEditMode)
				updateDashboard();
			this.service.fireAmiWebPanelLocationChanged(ap);
		}
	}

	@Override
	public void onWindowMoved(DesktopPortlet portlet, Window window) {
		if (inEditMode)
			updateDashboard();
		else {
			this.service.getAmiQueryFormEditorsManager().flagUpdateDialogArrow();
			if (this.service.getAmiQueryFormEditorsManager().isDialogArrowNeedsUpdate())
				this.flagPendingAjax();
		}
		//		}
	}

	public boolean getInEditMode() {
		return inEditMode;
	}

	//	@Override
	//	public void onCenterDisconnected(AmiWebManager manager, IterableAndSize<AmiWebObject> removed) {
	//		updateDashboard();
	//	}

	//	@Override
	//	public void onCenterSnapshotProcessed(AmiWebManager manager) {
	//		updateDashboard();
	//	}

	@Override
	public void onCenterConnectionStateChanged(AmiWebManager amiClientManager, byte state) {
		updateDashboard();

	}
	@Override
	public void onColorChanged(ColorPickerPortlet target, String oldColor, String newColor) {
		DividerPortlet divider = ((AmiWebDividerPortlet) target.getCorrelationData()).getInnerContainer();
		divider.setColor(newColor);
	}
	@Override
	public void onOkayPressed(ColorPickerPortlet target) {
		target.close();

	}
	@Override
	public void onCancelPressed(ColorPickerPortlet target) {
		DividerPortlet divider = ((AmiWebDividerPortlet) target.getCorrelationData()).getInnerContainer();
		divider.setColor(target.getDefaultColor());
		target.close();
	}

	public AmiWebService getService() {
		return this.service;
	}

	private boolean justLoaded;

	public String getLayoutTitleBarColor() {
		return this.getDesktopBar().getLayoutTitleBarColor();
	}
	public void setLayoutTitleBarColor(String layoutTitleBarColor) {
		if (this.getDesktopBar().setLayoutTitleBarColor(layoutTitleBarColor))
			this.flagStatusPanelNeedsUpdate();
	}
	public String getLayoutTitleBarBorderColor() {
		return this.getDesktopBar().getLayoutTitleBarBorderColor();
	}
	public void setLayoutTitleBarBorderColor(String layoutTitleBarColor) {
		if (this.getDesktopBar().setLayoutTitleBarBorderColor(layoutTitleBarColor))
			this.flagStatusPanelNeedsUpdate();
	}

	public void setLayoutTitleBarFontColor(String layoutTitleBarFontColor) {
		if (this.getDesktopBar().setLayoutTitleBarFontColor(layoutTitleBarFontColor))
			this.flagStatusPanelNeedsUpdate();
	}

	public void setMenubarPosition(byte value) {
		if (this.menuBarPosition == value)
			return;
		this.menuBarPosition = value;
		this.buildDesktop();
		this.flagStatusPanelNeedsUpdate();
	}

	public byte getMenubarPosition() {
		return this.menuBarPosition;
	}

	public void onFrontendCalled() {
		this.service.getAutosaveManager().onFrontendCalled();
		this.service.getWebStatsManager().onFrentendCalled();
		if (justLoaded) {
			justLoaded = false;
			String urlParamLayout = this.getManager().getUrlParams().get(AmiWebConsts.URL_PARAM_LAYOUT);
			if (urlParamLayout == null) {
				if (askAboutRestoreUnsavedWork) {
					List<AutoSaveFile> history = this.service.getAutosaveManager().getHistory();
					AutoSaveFile lastAutosave = history.get(history.size() - 1);
					String name = lastAutosave.getLayoutName();
					getManager().showDialog("Restore unsaved work",
							new ConfirmDialogPortlet(generateConfig(), "Restore unsaved work for '" + name + "'?", ConfirmDialogPortlet.TYPE_YES_NO, this).setCallback("RESTORE"));
				} else if (this.checkForNewLayout && this.service.getLayoutFilesManager().checkForAutoSaveLayout()) {
					showNewLayoutStylePortlet();
				}
			}
			this.checkForNewLayout = false;
			this.askAboutRestoreUnsavedWork = false;
			this.getCallbacks().execute("onStartup");
			String upns = this.getService().getVarsManager().getUserPrefNamespace();
			Boolean alwaysLoadPref = false;
			if (upns != null) {
				String aaup = this.varsManager.getAutoApplyUserPrefs();
				if (AmiWebConsts.ALWAYS.equals(aaup)) {
					alwaysLoadPref = true;
					this.getCallbacks().execute("onStartupComplete");//remember decision to always load prefs
				} else if (AmiWebConsts.ASK.equals(aaup)) {
					if (this.service.getUserFilesManager().getFile(AmiWebConsts.USER_SETTING_AMI_PREFS_PREFIX + upns).exists()) {
						ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), "Load your User Preferences?", ConfirmDialogPortlet.TYPE_YES_NO, this,
								new FormPortletCheckboxField("remember my decision")).setCallback("LOAD_USER_PREFS");
						cdp.getInputField().setLeftPosPct(.40).setTopPosPct(.40).setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
						cdp.updateButton(ConfirmDialogPortlet.ID_YES, "Yes, Load My Preferences");
						cdp.updateButton(ConfirmDialogPortlet.ID_NO, "No, Use Defaults");
						RootPortletDialog dialog = getManager().showDialog("User Preferences", cdp);
						dialog.addListener(this);

					} else
						this.getCallbacks().execute("onStartupComplete");//ask, but no preferences
				} else
					this.getCallbacks().execute("onStartupComplete"); //never ask
			} else
				this.getCallbacks().execute("onStartupComplete");//no namespace, hence no preferences
			this.service.getGuiServiceAdaptersManager().fireOnLayoutStartup();
			this.service.getDmManager().onFrontendCalled(true);
			if (alwaysLoadPref)
				this.service.getPreferencesManager().loadUserPrefs(false);
		} else
			this.service.getDmManager().onFrontendCalled(false);
	}
	public void onInitDone() {
		DesktopPortlet oldDesktop = (DesktopPortlet) (CH.first(getDesktopContainer().getChildren().values())); // get old desktop b/c new desktop hasn't been set yet
		for (Window w : oldDesktop.getWindows()) {
			int initWidth = w.getPortlet().getInitWidth();
			int initHeight = w.getPortlet().getInitHeight();
			if (initWidth != -1 && initHeight != -1) {
				w.setSize(initWidth, initHeight);
			}
		}
		this.updateDashboard();
		this.getInnerDesktop().onInitDone();
	}

	@Override
	public void onClosed() {
		this.service.getAutosaveManager().onClosed();
		super.onClosed();
	}

	public AmiWebInnerDesktopPortlet getInnerDesktop() {
		return this.desktop;
	}
	public void onDmsQueriesCountChanged(int count) {
		flagStatusPanelNeedsUpdate();
	}
	public String getHelpBgColor() {
		return helpBgColor;
	}
	public void setHelpBgColor(String helpBgColor) {
		this.helpBgColor = helpBgColor;
	}
	public String getHelpFontColor() {
		return helpFontColor;
	}
	public void setHelpFontColor(String helpFontColor) {
		this.helpFontColor = helpFontColor;
	}
	@Override
	public void onUserDeleteWindow(DesktopPortlet desktop, Window window) {
		if (window.isHidden(true) && !this.inEditMode) {
			window.setAllowMin(true, false);
			window.minimizeWindow();
			return;
		}
		if (isSpecialPortlet(window.getPortlet()) || window.getPortlet() instanceof AmiWebBlankPortlet) {
			if (window.getPortlet() instanceof AmiWebStyleManagerPortlet)
				((AmiWebStyleManagerPortlet) window.getPortlet()).onCloseButton();
			else if (window.getPortlet() instanceof WizardPortlet)
				((WizardPortlet) window.getPortlet()).onUserClose();
			else
				window.getPortlet().close();
			return;
		}
		if (window.getPortlet() instanceof AmiWebAliasPortlet)
			onUserDeletePortlet((AmiWebAliasPortlet) window.getPortlet());
		else if (window.getPortlet() instanceof AmiWebQueryFieldWizardPortlet)
			((AmiWebQueryFieldWizardPortlet) window.getPortlet()).onCloseButton();
		else
			window.getPortlet().close();
	}
	public void onUserDeletePortlet(AmiWebAliasPortlet portlet) {
		if (portlet.isReadonlyLayout()) {
			getManager().showAlert("Can not delete readonly panel");
			return;
		}
		Collection<AmiWebAliasPortlet> toDelete = PortletHelper.findPortletsByType(portlet, AmiWebAliasPortlet.class);
		Set<String> dependencies = new HashSet<String>();
		Set<String> toDeleteIds = new HashSet<String>();
		for (AmiWebAliasPortlet i : toDelete) {
			toDeleteIds.add(i.getAri());
			if (i instanceof AmiWebRealtimePortlet)
				dependencies.addAll(((AmiWebRealtimePortlet) i).getUpperRealtimeIds());
		}
		dependencies.removeAll(toDeleteIds);
		if (!dependencies.isEmpty()) {
			getManager().showAlert("Can not delete panels because there are dependencies: " + SH.join(", ", dependencies));
			return;
		}
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to delete '<B>" + portlet.getAmiLayoutFullAliasDotId() + "</B>' panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.addButton(ConfirmDialogPortlet.ID_YES, "Delete");

		dialog.setCallback("delete_panel");
		dialog.setCorrelationData(portlet.getPortletId());
		dialog.addDialogListener(this);
		if (portlet.isTransient())
			dialog.fireYesButton();
		else
			getManager().showDialog("Delete Panel", dialog);
	}
	public void onUserHidePortlet(AmiWebAliasPortlet portlet) {
		ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(generateConfig(), "Are you sure you want to Unlink '<B>" + portlet.getAmiLayoutFullAliasDotId() + "</B>' panel?",
				ConfirmDialogPortlet.TYPE_OK_CANCEL);
		dialog.addButton(ConfirmDialogPortlet.ID_YES, "Unlink");

		dialog.setCallback("hide_panel");
		dialog.setCorrelationData(portlet.getPortletId());
		dialog.addDialogListener(this);
		getManager().showDialog("Unlink Panel", dialog);

	}

	public boolean getIsLocked() {
		return this.isLocked;
	}
	@Override
	public void getUsedColors(Set<String> sink) {
		AmiWebUtils.getColors(this.helpBgColor, sink);
		AmiWebUtils.getColors(this.helpFontColor, sink);
		AmiWebUtils.getColors(this.getDesktopBar().getLayoutTitleBarColor(), sink);
		AmiWebUtils.getColors(this.getDesktopBar().getLayoutTitleBarFontColor(), sink);
		AmiWebUtils.getColors(this.getDesktopBar().getLayoutTitleBarBorderColor(), sink);
		if (this.desktop != null)
			this.desktop.getUsedColors(sink);
	}
	public AmiWebInnerDesktopPortlet getDesktop() {
		return this.desktop;
	}

	private RootPortletDialog arrowDialog;
	private Portlet arrowPortlet;
	private QueryField<?> arrowField;
	private int arrowX = -1;
	private int arrowY = -1;
	private String userPrefId;
	private boolean isDoingExportTransient;//This flag is checked during the export process and when false then non-transient panels are skipped

	public boolean getIsPopoutEnabled() {
		return this.isPopoutEnabled;
	}
	public void setIsPopoutEnabled(Boolean t) {
		if (t == null || this.isPopoutEnabled == t)
			return;
		this.isPopoutEnabled = t;
		if (this.desktop != null)
			for (Window window : this.desktop.getWindows())
				window.setAllowPop(t, false);
	}
	@Override
	public void onUserPopoutWindow(DesktopPortlet desktop, Window window) {
	}
	@Override
	public void onUserPopoutWindowClosed(PopupWindowListener popupWindowListener, Window window) {
	}

	public String getDebugInfoSetting() {
		return debugInfoSetting;
	}
	public void setDebugInfoSetting(String debugInfoSetting) {
		this.debugInfoSetting = debugInfoSetting;
		updateDebugManagerSeverities();
	}
	public String getDebugWarningSetting() {
		return debugWarningSetting;
	}
	public void setDebugWarningSetting(String debugWarningSetting) {
		this.debugWarningSetting = debugWarningSetting;
		updateDebugManagerSeverities();
	}
	public void setDialogArrow(Portlet origPortlet, RootPortletDialog dialog) {
		if (this.arrowDialog != null)
			throw new IllegalStateException();
		this.arrowDialog = dialog;
		this.arrowPortlet = origPortlet;
		flagUpdateDialogArrow();
		dialog.addListener(this);
	}
	public void setDialogArrow(Portlet origPortlet, RootPortletDialog dialog, QueryField<?> field) {
		this.arrowDialog = dialog;
		this.arrowField = field;
		this.arrowPortlet = origPortlet;
		flagUpdateDialogArrow();
		dialog.addListener(this);
	}
	public void setDialogArrow(Portlet origPortlet, RootPortletDialog dialog, int arrowX, int arrowY) {
		this.arrowDialog = dialog;
		this.arrowPortlet = origPortlet;
		this.arrowX = arrowX;
		this.arrowY = arrowY;
		flagUpdateDialogArrow();
		dialog.addListener(this);
	}
	@Override
	public void onDialogClickoutside(RootPortletDialog dialog) {

	}
	@Override
	public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b) {

	}
	@Override
	public void onDialogMoved(RootPortletDialog rootPortletDialog) {
		Portlet portlet = rootPortletDialog.getPortlet();
		if (portlet instanceof AmiWebQueryFieldWizardPortlet || portlet instanceof AmiWebEditStylePortlet)
			this.dialogArrowNeedsUpdate = true;
	}
	@Override
	public void onDialogClosed(RootPortletDialog rootPortletDialog) {
		if (rootPortletDialog == this.arrowDialog) {
			this.arrowDialog = null;
			this.arrowPortlet = null;
			this.arrowField = null;
			this.arrowX = -1;
			this.arrowY = -1;
			flagUpdateDialogArrow();
		}
		if (rootPortletDialog.getPortlet() instanceof ConfirmDialogPortlet) {
			ConfirmDialogPortlet cdp = (ConfirmDialogPortlet) rootPortletDialog.getPortlet();
			if ("LOAD_USER_PREFS".equals(cdp.getCallback()))
				this.getCallbacks().execute("onStartupComplete");
		}
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (keyEvent.isJustCtrlKey() && "d".equals(keyEvent.getKey())) {
			if (!isLocked)
				setInEditMode(!getInEditMode());
			return true;
		} else if (this.getDesktopBar().getDropDown().processUserKeyEventForMenuBar(keyEvent)) {
			return true;
		} else
			return super.onUserKeyEvent(keyEvent);
	}
	@Override
	public AmiWebStyledPortletPeer getStylePeer() {
		return this.stylePeer;
	}

	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		switch (key) {
			case AmiWebStyleConsts.CODE_BG_CL:
				setLayoutTitleBarColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WIN_BG_CL:
				if (desktop != null)
					desktop.setWindowColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WIN_FONT_CL:
				if (desktop != null)
					desktop.setWindowFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_BTN_BG_CL:
				if (desktop != null)
					desktop.setWindowButtonBg((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_BTN_ICON_CL:
				if (desktop != null)
					desktop.setWindowButtonIconColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_BTN_BDR_CL:
				if (desktop != null)
					desktop.setWindowButtonBorder((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_BTN_SHDW_CL:
				if (desktop != null)
					desktop.setWindowButtonShadowColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WIN_FONT_SZ:
				if (desktop != null)
					desktop.setTitleFontSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_WIN_FONT_FAM:
				if (desktop != null) {
					desktop.setTitleFontFamily((String) nuw);
					getService().getPortletManager().getStyleManager().getFormStyle().setDefaultFormFontFam((String) nuw);
				}
				break;
			case AmiWebStyleConsts.CODE_WIN_HEADER_PD:
				if (desktop != null)
					desktop.setTitlePadding(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_WIN_BDR_SIZE:
				if (desktop != null)
					desktop.setBorderSize(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_HELP_BG_CL:
				setHelpBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_HELP_FONT_CL:
				setHelpFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_POPOUTS:
				setIsPopoutEnabled(OH.noNull((Boolean) nuw, Boolean.TRUE));
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_CL:
				setUserWindowColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_UP_CL:
				setUserWindowUpColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_DOWN_CL:
				setUserWindowDownColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_TXT_CL:
				setUserWindowTextColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BTN_FONT_CL:
				getService().getPortletManager().getStyleManager().getFormStyle().setFormButtonFontColor((String) nuw);
				getService().getUserDialogStyleManager().getUserButtonStyle().setFormButtonFontColor((String) nuw);
				// allow multi select to pick up this style for scroll bar track color
				getService().getUserFormStyleManager().setFormButtonFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FD_BRD_CL:
				getService().getPortletManager().getStyleManager().getFormStyle().setFormBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BTN_CL:
				// allow multi select to pick up this style for scroll bar track color
				getService().getUserFormStyleManager().setFormButtonBackgroundColor((String) nuw);
				// for user setting button bg
				getService().getPortletManager().getStyleManager().getFormStyle().setFormButtonBackgroundColor((String) nuw);
				setUserWindowButtonColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BTN_UP_CL:
				setUserWindowButtonUpColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BTN_DOWN_CL:
				setUserWindowButtonDownColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BTN_ICON_CL:
				setUserWindowButtonIconColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_HEADER_SZ:
				setUsrWinHeaderSz(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_BDR_SZ:
				setUsrWinBdrSz(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FORM_BG_CL:
				setUsrWinFormBgCl((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FORM_BTN_PANEL_CL:
				setUsrWinFormBtnPanelCl((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FD_BG_CL:
				if (ColorHelper.checkColor((String) nuw))
					setUserWindowFieldBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FD_LBL_FN_CL:
				if (ColorHelper.checkColor((String) nuw))
					setUserWindowFieldLabelColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_FD_VAL_FG_CL:
				if (ColorHelper.checkColor((String) nuw))
					setUserWindowFieldFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_INNER_BDR_SZ:
				setUsrWinInnerBdrSz(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_USR_WIN_OUTER_BDR_SZ:
				setUsrWinOuterBdrSz(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_WAIT_LINE_CL:
				this.service.setWaitLineColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WAIT_BG_CL:
				this.service.setWaitBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WAIT_FILL_CL:
				this.service.setWaitFillColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_FONT_CL:
				setLayoutTitleBarFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DESK_BG_CL:
				setDeskBgCl((String) nuw);
				if (this.desktop != null) {
					// add correct background image based on the background color
					byte whichLogo = AmiUtils.getBestImageType(this.deskBgCl);
					switch (whichLogo) {
						case AmiUtils.COLOR1:
							this.desktop.addOption("bgImage", "rsc/ami/desktop_logo_center_color1.png");
							break;
						case AmiUtils.COLOR2:
							this.desktop.addOption("bgImage", "rsc/ami/desktop_logo_center_color2.png");
							break;
						case AmiUtils.WHITE:
							this.desktop.addOption("bgImage", "rsc/ami/desktop_logo_center_white.png");
							break;
						case AmiUtils.BLACK:
							this.desktop.addOption("bgImage", "rsc/ami/desktop_logo_center_black.png");
							break;
						default:
							this.desktop.addOption("bgImage", "rsc/ami/desktop_logo_center_color1.png");
							break;
					}
				}
				break;
			case AmiWebStyleConsts.CODE_WIN_CL_TP_LF:
				if (desktop != null)
					desktop.setWindowColorTopLeft((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_WIN_CL_BTM_RT:
				if (desktop != null)
					desktop.setWindowColorBottomRight((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TITLE_BAR_BDR_CL:
				setLayoutTitleBarBorderColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_BG_CL:
				setMenuBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_FONT_CL:
				setMenuFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_DIV_CL:
				setMenuDividerColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_DISABLED_BG_CL:
				setMenuDisabledBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_DISABLED_FONT_CL:
				setMenuDisabledFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_BORDER_TP_LF_CL:
				setMenuBorderTopLeftColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_BORDER_BTM_RT_CL:
				setMenuBorderBottomRightColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_HOVER_BG_CL:
				this.menuStyle.setHoverBgColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_MENU_HOVER_FONT_CL:
				this.menuStyle.setHoverFontColor((String) nuw);
				break;
			// alerts
			case AmiWebStyleConsts.CODE_DLG_FONT_SZ:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBodyFontSize(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_FONT_FAM:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBodyFontFam((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_FLD_BG_CL:
				if (ColorHelper.checkColor((String) nuw)) {
					PortletStyleManager_Dialog styleManager = getService().getPortletManager().getStyleManager().getDialogStyle();
					PortletStyleManager_Form buttonStyle = styleManager.getUserButtonStyle();
					// below two are needed to cover the button portlet, which is part of the body
					buttonStyle.setFormStyle("_bg=" + (String) nuw);
					buttonStyle.setFormButtonPanelStyle("_bg=" + (String) nuw);
					styleManager.setAlertBodyBackgroundColor((String) nuw);
				}
				break;
			case AmiWebStyleConsts.CODE_DLG_FLD_FONT_CL:
				if (ColorHelper.checkColor((String) nuw))
					getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBodyFontColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_BOLD:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBold((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_UNDERLINE:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertUnderline((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_ITALIC:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertItalic((Boolean) nuw);
				break;
			case AmiWebStyleConsts.CODE_TXT_ALIGN:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBodyAlign((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_WD:
				getService().getPortletManager().getStyleManager().getDialogStyle().setDialogWidth(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_HI:
				getService().getPortletManager().getStyleManager().getDialogStyle().setDialogHeight(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_TITLE_FONT_ALG:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertTitleAlignment((String) nuw);
				service.getUserDialogStyleManager().setAlertTitleAlignment((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_TITLE_FONT_FAM:
				service.getUserDialogStyleManager().setAlertTitleFontFamily((String) nuw);
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertTitleFontFamily((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_TITLE_FONT_SZ:
				service.getUserDialogStyleManager().setAlertTitleFontSize(OH.cast2(nuw, Integer.class));
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertTitleFontSize(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_BG_CL:
				PortletStyleManager_Form buttonStyle = getService().getPortletManager().getStyleManager().getDialogStyle().getAlertButtonStyle();
				if (buttonStyle != null)
					buttonStyle.setFormButtonBackgroundColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_PNL_BG_CL:
				PortletStyleManager_Form alertButtonStyle = getService().getPortletManager().getStyleManager().getDialogStyle().getAlertButtonStyle();
				if (alertButtonStyle != null)
					alertButtonStyle.setFormButtonPanelStyle("_bg=" + (String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_X_BUTTON_HI:
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonHeight(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_X_BUTTON_WD:
				service.getUserDialogStyleManager().setAlertXButtonWidth(OH.cast2(nuw, Integer.class));
				getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonWidth(OH.cast2(nuw, Integer.class));
				break;
			case AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_FONT_FAM:
				getService().getPortletManager().getStyleManager().getDialogStyle().getAlertButtonStyle().setFormButtonsFontFam((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_DLG_FORM_BUTTON_FONT_CL:
				getService().getPortletManager().getStyleManager().getDialogStyle().getAlertButtonStyle().setFormButtonFontColor((String) nuw);
				break;
			default:
				//				System.out.println("Unknown style: " + key);
				break;
		}
	}
	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Global.TYPE_GLOBAL;
	}
	public void setMenuBackgroundColor(String color) {
		menuStyle.setBgColor(color);
	}
	public void setMenuFontColor(String color) {
		menuStyle.setFontColor(color);
	}
	public void setMenuDisabledBgColor(String color) {
		this.menuStyle.setDisabledBgColor(color);
	}
	public void setMenuDisabledFontColor(String color) {
		this.menuStyle.setDisabledFontColor(color);
	}
	public void setMenuDividerColor(String color) {
		this.menuStyle.setDividerColor(color);
	}
	public void setMenuBorderTopLeftColor(String color) {
		this.menuStyle.setBorderTopLeftColor(color);
	}
	public void setMenuBorderBottomRightColor(String color) {
		this.menuStyle.setBorderBottomRightColor(color);
	}
	public String getMenuBackgroundColor() {
		return this.menuStyle.getBgColor();
	}
	public String getMenuDisabledBgColor() {
		return this.menuStyle.getDisabledBgColor();
	}
	public String getMenuDisabledFontColor() {
		return this.menuStyle.getDisabledFontColor();
	}
	public String getMenuFontColor() {
		return this.menuStyle.getFontColor();
	}
	public String getMenuDividerColor() {
		return this.menuStyle.getDividerColor();
	}
	public String getMenuBorderTopLeftColor() {
		return this.menuStyle.getBorderTopLeftColor();
	}
	public String getMenuBorderBottomRightColor() {
		return this.menuStyle.getBorderBottomRightColor();
	}
	public String getUserWindowColor() {
		return usrWinCl;
	}
	public void setUserWindowColor(String usrWinCl) {
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertBackgroundColor((String) usrWinCl);
		//		int usrWinClRgb = ColorHelper.parseRgb(usrWinCl);
		//		getService().getUserFormStyleManager().putDefaultFormFieldStyle(FormPortletButtonField.JSNAME,
		//				"style.background=linear-gradient(" + ColorHelper.generateGradientLimitPairRgb(usrWinClRgb, 0.4, true) + ")|_fg="
		//						+ ColorHelper.toRgbString(ColorHelper.colorDodgeRgb(usrWinClRgb)) + "|style.border=0px|_cn=none|style.borderRadius=8px");
		this.usrWinCl = usrWinCl;
	}
	public String getUserWindowUpColor() {
		return usrWinUpCl;
	}
	public void setUserWindowUpColor(String usrWinUpCl) {
		this.usrWinUpCl = usrWinUpCl;
		getService().getUserDialogStyleManager().getAlertDialogOptions().put(DesktopPortlet.OPTION_COLOR_WINDOW_UP, usrWinUpCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().getAlertDialogOptions().put(DesktopPortlet.OPTION_COLOR_WINDOW_UP, usrWinUpCl);
	}
	public String getUserWindowDownColor() {
		return usrWinDownCl;
	}
	public void setUserWindowDownColor(String usrWinDownCl) {
		this.usrWinDownCl = usrWinDownCl;
		getService().getUserDialogStyleManager().getAlertDialogOptions().put(DesktopPortlet.OPTION_COLOR_WINDOW_DOWN, usrWinDownCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().getAlertDialogOptions().put(DesktopPortlet.OPTION_COLOR_WINDOW_DOWN, usrWinDownCl);
	}
	public String getUserWindowTextColor() {
		return usrWinTxtCl;
	}
	public void setUserWindowFieldBackgroundColor(String color) {
		getService().getPortletManager().getStyleManager().getFormStyle().setDefaultFormBgColor(color);
	}

	public void setUserWindowFieldFontColor(String color) {
		getService().getPortletManager().getStyleManager().getFormStyle().setDefaultFormFontColor(color);
	}

	public void setUserWindowFieldLabelColor(String color) {
		getService().getPortletManager().getStyleManager().getFormStyle().setDefaultFormTitleColor(color);
	}
	public void setUserWindowTextColor(String usrWinTxtCl) {
		this.usrWinTxtCl = usrWinTxtCl;
		getService().getUserDialogStyleManager().setAlertTitleFontColor((String) usrWinTxtCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertTitleFontColor((String) usrWinTxtCl);
	}
	public String getUserWindowButtonColor() {
		return usrWinBtnCl;
	}
	public void setUserWindowButtonColor(String usrWinBtnCl) {
		this.usrWinBtnCl = usrWinBtnCl;
		getService().getUserDialogStyleManager().setAlertXButtonBgColor(usrWinBtnCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonBgColor(usrWinBtnCl);
	}
	public String getUserWindowButtonUpColor() {
		return usrWinBtnUpCl;
	}
	public void setUserWindowButtonUpColor(String usrWinBtnUpCl) {
		this.usrWinBtnUpCl = usrWinBtnUpCl;
		getService().getUserDialogStyleManager().setAlertXButtonBorderColor(usrWinBtnUpCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonBorderColor(usrWinBtnUpCl);
	}
	public String getUserWindowButtonDownColor() {
		return usrWinBtnDownCl;
	}
	public void setUserWindowButtonDownColor(String usrWinBtnDownCl) {
		this.usrWinBtnDownCl = usrWinBtnDownCl;
		getService().getUserDialogStyleManager().setAlertXButtonShadowColor(usrWinBtnDownCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonShadowColor(usrWinBtnDownCl);
	}
	public String getUserWindowButtonIconColor() {
		return usrWinBtnIconCl;
	}
	public void setUserWindowButtonIconColor(String usrWinBtnIconCl) {
		this.usrWinBtnIconCl = usrWinBtnIconCl;
		getService().getUserDialogStyleManager().setAlertXButtonIconColor(usrWinBtnIconCl);
		getService().getPortletManager().getStyleManager().getDialogStyle().setAlertXButtonIconColor(usrWinBtnIconCl);
	}
	public Integer getUsrWinHeaderSz() {
		return usrWinHeaderSz;
	}
	public void setUsrWinHeaderSz(Integer usrWinHeaderSz) {
		this.usrWinHeaderSz = usrWinHeaderSz;
		getService().getUserDialogStyleManager().setDialogHeaderSize(usrWinHeaderSz);
		// dialog doesn't have this
		getService().getPortletManager().getStyleManager().getDialogStyle().setDialogHeaderSize(usrWinHeaderSz);
	}
	public Integer getUsrWinBdrSz() {
		return usrWinBdrSz;
	}
	public void setUsrWinBdrSz(Integer usrWinBdrSz) {
		this.usrWinBdrSz = usrWinBdrSz;
		getService().getUserDialogStyleManager().setDialogBorderSize(usrWinBdrSz);
		getService().getPortletManager().getStyleManager().getDialogStyle().setDialogBorderSize(usrWinBdrSz);
	}
	public String getUsrWinFormBgCl() {
		return usrWinFormBgCl;
	}
	public void setUsrWinFormBgCl(String usrWinFormBgCl) {
		this.usrWinFormBgCl = usrWinFormBgCl;
		getService().getUserFormStyleManager().setFormStyle("_bg=" + usrWinFormBgCl);
		getService().getPortletManager().getStyleManager().getFormStyle().setFormStyle("_bg=" + usrWinFormBgCl);
	}
	public String getUsrWinFormBtnPanelCl() {
		return usrWinFormBtnPanelCl;
	}
	public void setUsrWinFormBtnPanelCl(String usrWinFormBtnPanelCl) {
		this.usrWinFormBtnPanelCl = usrWinFormBtnPanelCl;
		getService().getUserFormStyleManager().setFormButtonPanelStyle("_bg=" + usrWinFormBtnPanelCl);
		getService().getPortletManager().getStyleManager().getFormStyle().setFormButtonPanelStyle("_bg=" + usrWinFormBtnPanelCl);
	}
	public Integer getUsrWinInnerBdrSz() {
		return usrWinInnerBdrSz;
	}
	public void setUsrWinInnerBdrSz(Integer usrWinInnerBdrSz) {
		this.usrWinInnerBdrSz = usrWinInnerBdrSz;
		getService().getUserDialogStyleManager().getAlertDialogOptions().put(DesktopPortlet.OPTION_WINDOW_BORDER_INNER_SIZE, Caster_String.INSTANCE.cast(usrWinInnerBdrSz));
		getService().getPortletManager().getStyleManager().getDialogStyle().getAlertDialogOptions().put(DesktopPortlet.OPTION_WINDOW_BORDER_INNER_SIZE,
				Caster_String.INSTANCE.cast(usrWinInnerBdrSz));
	}
	public Integer getUsrWinOuterBdrSz() {
		return usrWinOuterBdrSz;
	}
	public void setUsrWinOuterBdrSz(Integer usrWinOuterBdrSz) {
		this.usrWinOuterBdrSz = usrWinOuterBdrSz;
		getService().getUserDialogStyleManager().getAlertDialogOptions().put(DesktopPortlet.OPTION_WINDOW_BORDER_OUTER_SIZE, Caster_String.INSTANCE.cast(usrWinOuterBdrSz));
		getService().getPortletManager().getStyleManager().getDialogStyle().getAlertDialogOptions().put(DesktopPortlet.OPTION_WINDOW_BORDER_OUTER_SIZE,
				Caster_String.INSTANCE.cast(usrWinOuterBdrSz));
	}

	public String getDeskBgCl() {
		return deskBgCl;
	}
	public void setDeskBgCl(String deskBgCl) {
		this.deskBgCl = deskBgCl;
		this.flagDesktopBackgroundNeedUpdate();
	}
	public void applyEditModeStyle(Window window) {
		int width = getWidth();
		int height = getHeight();
		int scaledWidth = DEFAULT_WIDTH, scaledHeight = DEFAULT_HEIGHT;
		if (width < 1350) {
			scaledWidth = (int) (width * 0.8);
		}
		if (height < 800) {
			scaledHeight = (int) (height * 0.8);
		}
		applyEditModeStyle(window, scaledWidth, scaledHeight);
	}
	public void applyEditModeStyle(Window window, int width, int height) {
		int screenWidth = getWidth() - 50;
		int screenHeight = getHeight() - 50;
		// scale only if hard coded dims exceed screen size
		if (screenWidth < width) {
			width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (screenWidth * 0.8));
		}
		if (screenHeight < height) {
			height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (screenHeight * 0.8));
		}
		window.setAllowTitleEdit(false, true);
		window.setCloseable(true, true);
		window.getPortlet().setSize(width, height);
		window.setSize(width, height);
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_TEXT, "#ffffff");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW, "#007608");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_DOWN, "#007608");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_UP, "#007608");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON, "#e2e2e2");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_UP, "#007608");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_DOWN, "#007608");
		window.addOption(DesktopPortlet.OPTION_COLOR_WINDOW_BUTTON_ICON, "#000000");
		window.addOption(DesktopPortlet.OPTION_WINDOW_FONTSTYLE, "style.textTransform=upperCase");
		if (window.getWidth() != 0 && window.getHeight() != 0) {
			window.setPosition((this.desktop.getWidth() - window.getWidth()) / 2, (this.desktop.getHeight() - window.getHeight()) / 2);
		}
	}
	public boolean managesSecurityFor(Portlet p) {
		return (p == this.desktop || p == this.desktop || p.getParent() == this);
	}
	@Override
	public void onUserCloseDialog(RootPortletDialog rootPortletDialog) {

	}
	public static String getWindowMenuLocation(DesktopPortlet.Window window) {
		String r = window.getAttribute(WINDOW_ATTRIBUTE_MENU_LOCATION);
		return r == null ? "Windows" : WebHelper.escapeHtml(r);
	}
	private static void setWindowMenuLocation(DesktopPortlet.Window window, String location) {
		if ("Windows".equals(location) || SH.isnt(location))
			window.removeAttribute(WINDOW_ATTRIBUTE_MENU_LOCATION);
		else
			window.putAttribute(WINDOW_ATTRIBUTE_MENU_LOCATION, location.trim());
	}

	public AmiWebMethodsPortlet getMethodPortlet() {
		return this.getSpecialPortlet(AmiWebMethodsPortlet.class);
	}
	public AmiWebCustomCssTabsPortlet getCustomCssStylePortlet() {
		return this.getSpecialPortlet(AmiWebCustomCssTabsPortlet.class);
	}
	public AmiWebDesktopLinkHelper getLinkHelper() {
		return this.linkHelper;
	}
	public GridPortlet getDesktopContainer() {
		return this.desktopContainer;
	}
	public void setDesktop(AmiWebInnerDesktopPortlet child) {
		if (child == desktop)
			return;
		if (desktop != null) {
			desktop.removeDesktopListener(this);
			desktop.setDeskopManager(null);
		}

		desktop = (AmiWebInnerDesktopPortlet) child;
		if (desktop != null) {
			desktop.addDesktopListener(this);
			desktop.setDeskopManager(this);
			this.service.registerAmiUserPrefId("Desktop", this.getDesktop());
			initDesktop();
			getDesktopContainer().addChild(desktop);
			getManager().onPortletAdded(desktop);
			desktop.onLayoutInit();
		}
		this.justLoaded = true;

	}
	public AmiWebAmiScriptCallbacks getCallbacks() {
		return this.service.getScriptManager(AmiWebLayoutFile.DEFAULT_ROOT_ALIAS).getLayoutCallbacks();
	}
	@Override
	public boolean onFileSelected(AmiWebFileBrowserPortlet target, String file) {
		if (target.getBasePath() != null) {//this is a cloud file chooser
			file = SH.stripPrefix(file, target.getBasePath(), true);
			file = SH.trim('/', file);
			if (target.getType() == AmiWebFileBrowserPortlet.TYPE_SAVE_FILE) {
				service.getLayoutFilesManager().saveLayoutAs(file, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
			} else {
				service.getLayoutFilesManager().loadLayoutDialog(file, null, AmiWebConsts.LAYOUT_SOURCE_CLOUD);
			}
			return true;
		} else {
			if (target.getType() == AmiWebFileBrowserPortlet.TYPE_SAVE_FILE) {
				this.service.getLayoutFilesManager().saveLayoutAs(file, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
			} else {
				service.getLayoutFilesManager().loadLayoutDialog(file, null, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE);
			}
		}
		return true;
	}

	public AmiWebDesktopBar getDesktopBar() {
		return desktopBar;
	}

	public void showCenters() {
		if (showMenuOption(AmiWebProperties.PROPERTY_AMI_SHOW_MENU_OPTION_DATA_STATISTICS, AmiWebProperties.VALUE_ALWAYS)) {
			AmiWebDataViewPortlet t = new AmiWebDataViewPortlet(generateConfig());
			getManager().showDialog("AMI Data Statistics", t).setStyle(getService().getUserDialogStyleManager());
			t.showCenters();
			t.refresh();
		}
	}
	public void showUserMemoryStats() {
		if (showMenuOption(AmiWebProperties.PROPERTY_AMI_SHOW_MENU_OPTION_DATA_STATISTICS, AmiWebProperties.VALUE_ALWAYS)) {
			AmiWebDataViewPortlet t = new AmiWebDataViewPortlet(generateConfig());
			getManager().showDialog("AMI Data Statistics", t).setStyle(getService().getUserDialogStyleManager());
			t.showMemoryStats();
			t.refresh();
		}
	}

	@Override
	public String toDerivedString() {
		return "DESKTOP_PANEL";
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(toDerivedString());
	}

	public boolean onButton2(ConfirmDialog source, String id) {
		getService().getSecurityModel().assertPermitted(this, source.getCallback(), "LOAD_USER_PREFS");
		if ("delete_panel".equals(source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				deletePanel((String) source.getCorrelationData(), true);
			}
		}
		if ("hide_panel".equals(source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				deletePanel((String) source.getCorrelationData(), false);
			}
		}
		if ("view_unsaved_changes".equals(id)) {
			service.getLayoutFilesManager().onButton2(source, id);
			return false;
		}
		if (ConfirmDialog.ID_YES.equals(id)) {
			if ("WINDOW_RENAME".equals(source.getCallback())) {
				String windowId = (String) source.getCorrelationData();
				this.desktop.getWindow(windowId).setName(SH.trim((String) source.getInputFieldValue()));
			} else if ("WINDOW_MENUBAR_LOCATION".equals(source.getCallback())) {
				String windowId = (String) source.getCorrelationData();
				String name = (String) source.getInputFieldValue();
				setWindowMenuLocation(this.desktop.getWindow(windowId), name);
				buildMenus();
			} else if ("RELOAD".equals(source.getCallback())) {
				//				this.service.getAutosaveManager().onRebuildLayout();
				this.service.getLayoutFilesManager().reloadLayout();
			} else if ("SHUTDOWN".equals(source.getCallback())) {
				if (this.varsManager.isUserAdmin()) {
					LH.info(log, "User ", getManager().describeUser(), " has requested a shutdown");
					System.exit(0);
				}
			} else if ("DELETE_FROM_CLOUD".equals(source.getCallback())) {
				this.service.getCloudManager().removeLayout((String) source.getCorrelationData());
			} else if ("LOGOUT".equals(source.getCallback())) {
				service.getAutosaveManager().onLogout();
				logout();
				return false;
			} else if ("ENDSESSION".equals(source.getCallback())) {
				service.getAutosaveManager().onLogout();
				endSession();
				return false;
			} else if ("EDIT_PANEL_ID".equals(source.getCallback())) {
				AmiWebPortlet portlet = (AmiWebPortlet) source.getCorrelationData();
				String value = SH.trim((String) source.getInputFieldValue());
				if (SH.isnt(value)) {
					getManager().showAlert("panel id required.");
					return false;
				} else if (value.startsWith("__")) {
					getManager().showAlert("panel id cannot start with '__'");
					return false;
				} else if (OH.ne(value, portlet.getAmiPanelId())) {
					if (service.getPortletByPanelId(portlet.getAmiLayoutFullAlias(), value) != null) {
						getManager().showAlert("panel id already exists");
					} else
						portlet.setAdn(AmiWebUtils.getFullAlias(portlet.getAmiLayoutFullAlias(), portlet.getAmiPanelId()));
				}
			} else if ("add_blank".equals(source.getCallback())) {
				String currentPortletId = (String) source.getCorrelationData();
				replacePortlet(currentPortletId, newAmiWebAmiBlankPortlet(getAmiLayoutFullAliasForPortletId(currentPortletId)));
			} else if ("add_form".equals(source.getCallback())) {
				String currentPortletId = (String) source.getCorrelationData();
				replacePortlet(currentPortletId, newPortlet(AmiWebQueryFormPortlet.Builder.ID, getAmiLayoutFullAliasForPortletId(currentPortletId)));
			} else if ("add_filter".equals(source.getCallback())) {
				String currentPortletId = (String) source.getCorrelationData();
				createFilterPortlet(currentPortletId);
			} else if (service.getPreferencesManager().onAmiWebDesktopAction(source.getCallback())) {
				//handled
			} else if (source.getCallback().startsWith("_remove_link_")) {
				String linkId = SH.stripPrefix(source.getCallback(), "_remove_link_", true);
				getService().getDmManager().removeDmLink(linkId);
			}
		}
		if ("LOAD_USER_PREFS".equals(source.getCallback())) {
			if (Boolean.TRUE.equals(source.getInputFieldValue())) {
				String s = ConfirmDialog.ID_YES.equals(id) ? AmiWebConsts.ALWAYS : AmiWebConsts.NEVER;
				this.service.getVarsManager().setAutoApplyuserPrefs(s);
				this.service.getVarsManager().putSetting(AmiWebConsts.USER_SETTING_AUTOAPPLY_USERPREFS, s);
			}
		}
		if (source.getCallback().equals("RESTORE")) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				List<AutoSaveFile> history = this.service.getAutosaveManager().getHistory();
				AutoSaveFile lastAutosave = history.get(history.size() - 1);
				try {
					String configText = this.service.getAutosaveManager().getLayout(lastAutosave.getNumber()).getLayout();
					service.getLayoutFilesManager().loadLayout(lastAutosave.getLayoutName(), configText, null);
				} catch (Exception e) {
					LH.warning(log, "error recovering layout '", lastAutosave.getLayoutName(), "'", e);
				}
			}
		}
		return true;
	}

	@Override
	public void onUserClick(HtmlPortlet portlet) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, Callback cb) {
		if ("show_menubar".equals(id)) {
			switch (this.menuBarPosition) {
				case MENUBAR_BOTTOM_CENTER:
				case MENUBAR_BOTTOM_LEFT:
				case MENUBAR_BOTTOM_RIGHT:
					this.getDesktopBar().showAsDialog(false);
					break;
				case MENUBAR_TOP_CENTER:
				case MENUBAR_TOP_LEFT:
				case MENUBAR_TOP_RIGHT:
					this.getDesktopBar().showAsDialog(true);
					break;
			}
		} else if ("logo_clicked".equals(id)) {
			WebMenu menu = new BasicWebMenu();
			if (getDesktop().getCustomContextMenu().getChildren(false).size() > 0) {
				WebMenu customMenu;
				try {
					customMenu = this.desktop.getCustomContextMenu().generateMenu();
					List<WebMenuItem> children = customMenu.getChildren();
					for (int i = 0; i < children.size(); i++) {
						children.get(i).setParent(null);
						menu.add(children.get(i));
					}
				} catch (Exception e) {
					service.getPortletManager().showAlert("Error in custom menu.", e);
				}
			}
			menu.add(new BasicWebMenuLink("<I>Visit 3Forge.com", true, "visit_3forge_website"));
			getManager().showContextMenu(menu, this);
		}
	}

	@Override
	public void onHtmlChanged(String old, String nuw) {

	}

	public static String menubarPositionToString(byte code) {
		switch (code) {
			case MENUBAR_BOTTOM_CENTER:
				return "bottomCenter";
			case MENUBAR_BOTTOM_LEFT:
				return "bottomLeft";
			case MENUBAR_BOTTOM_RIGHT:
				return "bottomRight";
			case MENUBAR_TOP_CENTER:
				return "topCenter";
			case MENUBAR_TOP_LEFT:
				return "topLeft";
			case MENUBAR_TOP_RIGHT:
				return "topRight";
			case MENUBAR_BOTTOM:
				return "bottom";
			case MENUBAR_TOP:
			default:
				return "top";
		}
	}
	public static byte parseMenuPosition(String str) {
		if ("bottomCenter".equals(str))
			return MENUBAR_BOTTOM_CENTER;
		else if ("bottomLeft".equals(str))
			return MENUBAR_BOTTOM_LEFT;
		else if ("bottomRight".equals(str))
			return MENUBAR_BOTTOM_RIGHT;
		else if ("topCenter".equals(str))
			return MENUBAR_TOP_CENTER;
		else if ("topLeft".equals(str))
			return MENUBAR_TOP_LEFT;
		else if ("topRight".equals(str))
			return MENUBAR_TOP_RIGHT;
		else if ("top".equals(str))
			return MENUBAR_TOP;
		else if ("bottom".equals(str))
			return MENUBAR_BOTTOM;
		else
			return MENUBAR_TOP;
	}

	public void setDefaultWindowZIndexToCurrent() {
		for (Window w2 : desktop.getWindows()) {
			w2.setDefaultZIndexToCurrent();
			w2.setDefaultStateToCurrent();
		}
		flagUpdateWindowLinks();
	}
	public boolean isDefaultWindowZIndexCurrent() {
		for (Window w2 : desktop.getWindows()) {
			Portlet p = w2.getPortlet();
			if (p instanceof AmiWebPortlet && ((AmiWebPortlet) p).isTransient())
				continue;
			if (OH.ne(w2.getCurrentState(), w2.getDefaultState()))
				return false;
			if (OH.eq(w2.getCurrentState(), DesktopPortlet.WINDOW_STATE_FLT) && OH.ne(w2.getDefaultZIndex(), (Integer) w2.getZindex()))
				return false;
		}
		return true;
	}

	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer peer) {
		this.desktopBar.setHtmlCssClass(peer.getParentStyle());
	}

	public void setIsDoingExportTransient(boolean b) {
		this.isDoingExportTransient = b;
	}

	public boolean getIsDoingExportTransient() {
		return this.isDoingExportTransient;
	}

	//	@Override
	//	public void onVarConstChanged(String var) {
	//	}

}
