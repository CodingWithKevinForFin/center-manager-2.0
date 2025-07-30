package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebStatsManager.Stats;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.bootstrap.F1Constants;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.suite.web.portal.impl.DropDownMenuPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.HtmlPortletListener;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.WebDropDownMenuFactory;
import com.f1.suite.web.portal.impl.WebDropDownMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.HtmlPortletCustomCallbackListener;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.Formatter;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebDesktopBar extends GridPortlet
		implements HtmlPortletListener, WebDropDownMenuListener, WebDropDownMenuFactory, AmiWebFormulasListener, HtmlPortletCustomCallbackListener {

	public static final String UNSAVED_HTML = "<I style='color:#007700'>new</I>";
	private static final Logger log = LH.get();
	private static final double GB = 1024 * 1024 * 1024;
	private AmiWebDesktopPortlet amiDesktop;
	final private AmiWebService service;

	final private HtmlPortlet logo;
	final private DropDownMenuPortlet dropDown;
	final private HtmlPortlet statusPanel;
	final private HtmlPortlet dashboard;
	final private HtmlPortlet configButton;
	final static String IMG_PATH_USER = "rsc/ami/user-mode_1.svg";
	final static String IMG_PATH_DEV = "rsc/ami/dev-mode_1.svg";

	private String version;
	private String layoutTitleBarColor;
	private String layoutTitleBarFontColor;

	private String dashboardDefaultHtml;
	private Object jus;
	//	private boolean isJustIcon;
	private RootPortletDialog dialog;
	private String layoutTitleBarBorderColor;

	public AmiWebDesktopBar(AmiWebDesktopPortlet amiDesktop, PortletConfig config) {
		super(config);
		this.service = amiDesktop.getService();
		this.amiDesktop = amiDesktop;
		this.version = config.getPortletManager().getTools().getOptional(F1Constants.PROPERTY_BUILD_VERSION);
		if (version == null)
			version = "standard";
		logo = new HtmlPortlet(generateConfig());
		setLogoSrc("rsc/ami/menubar_logo3_color1.svg");
		logo.setCssClass("amiweblogo");
		this.logo.addListener(this);
		dropDown = new DropDownMenuPortlet(generateConfig());
		dropDown.addOption(DropDownMenuPortlet.OPTION_ALIGN, "left");
		statusPanel = new HtmlPortlet(generateConfig(), "", "ami_dashboard ami_dashboard_title bold");
		dashboard = new HtmlPortlet(generateConfig(), "", "ami_dashboard ami_dashboard_title ");
		configButton = new HtmlPortlet(generateConfig(), "");

		dashboard.addListener(this);
		statusPanel.addListener(this);

		dropDown.addMenuContextListener(this);
		dropDown.setContextMenuFactory(this);

		configButton.addListener(this);

		this.updateConfigButton(amiDesktop.getIsLocked(), false);
		this.dashboard.addCustomCallbackListener(this);
		//		addToDomManager();
		this.buildDashboard();
	}

	@Override
	public void close() {
		super.close();
		this.amiDesktop = null;
	}
	public void updateStatusPanel(String debugWarningSetting, String debugInfoSetting) {
		switch (service.getWebManagers().getConnectionState()) {
			case AmiWebSnapshotManager.STATE_DISCONNECTED:
				statusPanel.setCssClass("ami_dashboard ami_dashboard_title bold");
				if (this.amiDesktop.getIsLocked())
					this.statusPanel.setHtml("<span class='red' style='font-size:12px;width:218px'>Primary Center Disconnected</span>");
				else
					this.statusPanel.setHtml("<div onclick='" + statusPanel.generateCallback("show_centers")
							+ "' class='red' style='font-size:12px;top:0px;width:218px'>Primary Center Disconnected:<BR>"
							+ service.getWebManagers().getPrimaryWebManager().getCenterDef().getDescription() + "</div>");
				break;
			case AmiWebSnapshotManager.STATE_INIT:
				statusPanel.setCssClass("ami_dashboard ami_dashboard_title bold");
				this.statusPanel.setHtml("Searching for Backend...");
				break;
			case AmiWebSnapshotManager.STATE_REQUEST_SENT:
				statusPanel.setCssClass("ami_dashboard ami_dashboard_title bold");
				this.statusPanel.setHtml("Loading Data...");
				break;
			case AmiWebSnapshotManager.STATE_REQUEST_NOT_SENT:
			case AmiWebSnapshotManager.STATE_CONNECTED:
				int errors = this.service.getDebugManager().getMessages(AmiDebugMessage.SEVERITY_WARNING).size();
				int infos = this.service.getDebugManager().getMessages(AmiDebugMessage.SEVERITY_INFO).size();
				statusPanel.setCssClass("ami_dashboard ami_dashboard_warning");
				StringBuilder sb = new StringBuilder();
				int cnt = service.getDmManager().getCurrentlyRunningQueriesCount();
				if (!amiDesktop.isDefaultWindowZIndexCurrent() && amiDesktop.getInEditMode())
					sb.append("<div style='position:relative;float:left;cursor:pointer;width:30px;height:30px' class='ami_desktop_edit_button_windows_default_location' onclick=")
							.append(this.statusPanel.generateCallback("set_zindex_to_default")).append("></div>");
				sb.append("<div style='position:relative;float:left;cursor:pointer;width:30px;height:16px'>");
				if (cnt > 0) {
					Set<AmiWebDmsImpl> dms = service.getDmManager().getCurrentlyRunningDms();
					if (amiDesktop.getInEditMode())
						sb.append("<img onclick='").append(this.statusPanel.generateCallback("show_running"))
								.append("'  style='cursor:pointer;width:16px;height:16px' src='rsc/ami/loadingbox.gif' title='");
					else
						sb.append("<img style='cursor:pointer;width:16px;height:16px' src='rsc/ami/loadingbox.gif' title='");

					for (AmiWebDm i : dms)
						sb.append(i.getDmName()).append("\n");
					sb.append("'>(");
					sb.append(cnt);
					sb.append(')');
				}
				sb.append("</div>");
				if (amiDesktop.evalDebugMode(debugWarningSetting)) {
					if (errors > 0) { // yellow sign icon
						if (sb.length() > 0)
							sb.append("&nbsp;&nbsp");
						sb.append("<img onclick='").append(this.statusPanel.generateCallback("show_warnings"))
								.append("' style='cursor:pointer;' src='rsc/portlet_icon_warning.gif'>(");
						if (errors < 100)
							sb.append(errors);
						else
							sb.append("99+");
						sb.append(')');
					}
				}
				if (amiDesktop.evalDebugMode(debugInfoSetting)) {
					if (infos > 0) { // bug icon
						if (sb.length() > 0)
							sb.append("&nbsp;&nbsp");
						sb.append("<img onclick='").append(statusPanel.generateCallback("show_infos")).append("' style='cursor:pointer' src='rsc/portlet_icon_debug.gif'>(");
						if (infos < 100)
							sb.append(infos);
						else
							sb.append("99+");
						sb.append(')');
					}
				}

				Formatter pcf = service.getFormatterManager().getPercentFormatter(0);
				Formatter gbf = service.getFormatterManager().getDecimalFormatter(1);
				sb.append("&nbsp;&nbsp");
				sb.append("<span class='web-stats-container' style='cursor:pointer;' onclick='").append(statusPanel.generateCallback("show_stats")).append("' title='");
				for (Stats i : service.getWebStatsManager().getStats()) {
					sb.append(i.getDescription());
					sb.append(" Memory - ");
					pcf.format(i.getMemoryPct(), sb);
					sb.append(" (").append(gbf.format(i.getMemory() / GB)).append(" / ").append(gbf.format(i.getMaxMemory() / GB)).append(" gb)");
					sb.append("\n");
					sb.append(i.getDescription());
					sb.append(" Users - ");
					pcf.format(i.getUserPct(), sb);
					sb.append(" (").append(i.getUsers()).append(" / ").append(i.getMaxUsers()).append(")");
					sb.append("\n");
				}
				sb.append("'>");
				sb.append(service.getWebStatsManager().getWorstMemoryIconHTML());
				sb.append(service.getWebStatsManager().getWorstUserIconHTML());
				//				pcf.format(service.getWebStatsManager().getWorstUserStats(), sb);
				//				sb.append(",");
				//				pcf.format(service.getWebStatsManager().getWorstMemoryStats(), sb);
				sb.append("</span>");

				List<String> disconnected = null;
				for (AmiWebSnapshotManager i : service.getWebManagers().getSnapshotManagers()) {
					switch (i.getConnectionState()) {
						case AmiWebSnapshotManager.STATE_DISCONNECTED:
						case AmiWebSnapshotManager.STATE_INIT:
						case AmiWebSnapshotManager.STATE_REQUEST_SENT:
							if (disconnected == null)
								disconnected = new ArrayList<String>();
							disconnected.add(i.getWebManager().getCenterDef().getDescription());
					}
				}
				if (disconnected != null) {
					sb.append("&nbsp;&nbsp");
					if (this.amiDesktop.getIsLocked())
						sb.append("<img title='" + disconnected
								+ " center(s) are disconnected' style='cursor:pointer;width:16px;height:16px' src='rsc/portlet_icon_centerstatus.svg'>(");
					else {
						sb.append("<img title='" + SH.join(" DISCONNECTED\n", disconnected) + " DISCONNECTED' onclick='").append(statusPanel.generateCallback("show_centers"))
								.append("' style='cursor:pointer;width:16px;height:16px' src='rsc/portlet_icon_centerstatus.svg'>(");
					}
					sb.append(disconnected.size());
					sb.append(')');
				}
				this.statusPanel.setHtml(sb.toString());
				break;
		}

	}

	public void updateConfigButton(boolean isLocked, boolean inEditMode) {
		if (!isLocked) {
			this.configButton.setHtml("<div class='config-btn-container'><div>Ctrl+d</div> <div onclick='" + configButton.generateCallback("toggle_edit_mode")
					+ "' class='config-img'><img src=" + (inEditMode ? IMG_PATH_DEV : IMG_PATH_USER) + " width='50' > </div></div> ");
		} else {
			configButton.setHtml("<div class='ami_dashboard' style='width:100%;height:100%'></div>");
		}
	}

	public void updateDashboard(boolean isLinking) {
		if (this.amiDesktop == null)
			return;
		if (!isLinking) {
			boolean isDevMode = this.service.getDesktop().getInEditMode();
			String layoutName = service.getLayoutFilesManager().getLayoutName();
			String layoutSource = SH.toLowerCase(service.getLayoutFilesManager().getLayoutSource());
			String source = service.getLayoutFilesManager().getLayoutSource();
			StringBuilder title = new StringBuilder();
			if (layoutName != null) {
				title.append("Name: ").append(layoutName).append("\n");
				if (source != null)
					title.append("Location: ").append(layoutSource).append("\n");
			} else {
				title.append("(unsaved layout)");
			}

			final boolean top;
			switch (this.amiDesktop.getMenubarPosition()) {
				case AmiWebDesktopPortlet.MENUBAR_TOP:
				case AmiWebDesktopPortlet.MENUBAR_TOP_CENTER:
				case AmiWebDesktopPortlet.MENUBAR_TOP_LEFT:
				case AmiWebDesktopPortlet.MENUBAR_TOP_RIGHT:
					top = true;
					break;
				default:
					top = false;
			}
			this.setCssStyle("_bg=" + this.getLayoutTitleBarColor() + "|_fg=" + this.getLayoutTitleBarFontColor() + "|style.borderStyle=solid|style.borderColor="
					+ getLayoutTitleBarBorderColor() + "|style.borderWidth=" + (top ? "0px 0px 1px 0px" : "1px 0px 0px 0px"));
			dropDown.addOption(DropDownMenuPortlet.OPTION_GO_UP, !top);
			//change logo-theme based on layoutTitleBarColor
			setLogoSrc(getLogoUrl());
			this.dropDown.setCssStyle("_fg=" + this.getLayoutTitleBarFontColor() + "|className=ami_dashboard ami_dashboard_menu");
			if (isDevMode) {
				this.dashboard.setHtml(SH.is(this.dashboardDefaultHtml) ? this.dashboardDefaultHtml
						: "<center class='user-selectable' title='" + title + "'>" + (layoutName == null ? UNSAVED_HTML : (layoutSource + ":" + layoutName)));
			} else {
				String name = SH.afterLast(SH.replaceAll(layoutName, '\\', '/'), '/', layoutName);
				if (name != null) {
					name = SH.stripSuffix(name, ".ami", false);
					name = AmiWebUtils.toPrettyName(name);
				} else
					name = UNSAVED_HTML;
				this.dashboard.setHtml(SH.is(this.dashboardDefaultHtml) ? this.dashboardDefaultHtml : "<center class='user-selectable' title='" + title + "'>" + name);
			}
			//			this.dashboard.setCssStyle("style.background=" + this.getLayoutTitleBarColor());
			//			this.setCssStyle("style.background=" + this.getLayoutTitleBarColor());
			this.configButton.setCssStyle("style.background=" + this.getLayoutTitleBarColor());
		} else
			this.dashboard.setHtml("Select the portlet you wish to link from <span onclick='" + this.dashboard.generateCallback("cancel_link")
					+ "' style='background:red;border:2px outset grey;cursor:pointer;padding:2px'>CANCEL LINK SEQUENCE</span>");
	}

	public String getLogoUrl() {
		switch (AmiUtils.getBestImageType(getLayoutTitleBarColor())) {
			case AmiUtils.COLOR1:
				return "rsc/ami/menubar_logo3_color1.svg";
			case AmiUtils.COLOR2:
				return "rsc/ami/menubar_logo3_color2.svg";
			case AmiUtils.WHITE:
				return "rsc/ami/menubar_logo3_white.svg";
			case AmiUtils.BLACK:
				return "rsc/ami/menubar_logo3_black.svg";
			default:
				return "rsc/ami/menubar_logo3_color1.svg";
		}
	}
	public void updateDropDown(boolean inEditMode) {
		dropDown.clearMenus();
		dropDown.addMenu("file", "File");
		dropDown.addMenu("account", "Account");
		dropDown.addMenu("window", "Windows");
		dropDown.addMenu("help", "Help");

		int width = 250;
		if (inEditMode) {
			dropDown.addMenu("data", "Dashboard");
			width += getManager().getPortletMetrics().getWidth("Dashboard", "", 14) + 20;
		} else {
			if (amiDesktop == null)
				return;
			DesktopPortlet desktop = amiDesktop.getDesktop();
			for (String child : desktop.getChildren().keySet()) {
				Window win = desktop.getWindow(child);
				if (amiDesktop.isSpecialPortlet(win.getPortlet()))
					continue;
				if (!win.isHidden(true)) {
					String location = AmiWebDesktopPortlet.getWindowMenuLocation(win);
					if (dropDown.findMenuByName(location) == null) {
						dropDown.addMenu(location, location);
						width += getManager().getPortletMetrics().getWidth(location, "", 14) + 20;
					}
				}
			}
		}
		if (this.amiDesktop != null && this.amiDesktop.getDesktop().getCustomContextMenu().getChildren(false).size() > 0) {
			String title = SH.is(service.getVarsManager().getCustomUserMenuTitle()) ? service.getVarsManager().getCustomUserMenuTitle()
					: AmiWebVarsManager.DEFAULT_CUSTOM_USER_MENU_TITLE;
			title = WebHelper.escapeHtml(title);
			dropDown.addMenu("usermenus", title);
			width += getManager().getPortletMetrics().getWidth(title, "", 14) + 20;
		}

		//		if (!isJustIcon)
		setColSize(1, width);

	}
	private void buildDashboard() {
		removeAllChildren();
		clearColumnSizes();
		addChild(logo, 0, 0);
		//		if (!isJustIcon) {
		addChild(dropDown, 1, 0);
		addChild(dashboard, 2, 0);
		addChild(statusPanel, 3, 0);
		addChild(configButton, 4, 0);
		setColSize(0, 100);
		setColSize(3, 200);
		setColSize(4, 100);
		//		}

	}
	@Override
	public void onUserClick(HtmlPortlet portlet) {
		if (this.amiDesktop.getInEditMode() && (portlet == this.statusPanel || portlet == this.dashboard)) {
			closeDialog();
			getManager().showDialog("Edit Title Bar HTML", new DesktopBarHtmlEditor(this, generateConfig()), 600, 600);
		}
	}
	@Override
	public void onUserCallback(HtmlPortlet htmlPortlet, String id, int mouseX, int mouseY, HtmlPortlet.Callback attributes) {
		service.getSecurityModel().assertPermitted(this, id, "show_stats,logo_clicked");
		if ("toggle_edit_mode".equals(id)) {
			closeDialog();
			amiDesktop.setInEditMode(!amiDesktop.getInEditMode());
		} else if ("logo_clicked".equals(id)) {
			if (this.dialog != null) {
				closeDialog();
			} else {
				String url = "https://3forge.com/products.html";
				new JsFunction(getManager().getPendingJs(), "window", "open").addParamQuoted(url).end();
			}
		} else if ("show_warnings".equals(id)) {
			closeDialog();
			amiDesktop.showWarningsDialog(true, false);
		} else if ("show_infos".equals(id)) {
			closeDialog();
			amiDesktop.showWarningsDialog(false, true);
		} else if ("show_centers".equals(id)) {
			closeDialog();
			amiDesktop.showCenters();
		} else if ("show_stats".equals(id)) {
			closeDialog();
			amiDesktop.showUserMemoryStats();
		} else if ("show_running".equals(id)) {
			closeDialog();
			amiDesktop.showWarningsDialog(true, true);
		} else if ("cancel_link".equals(id)) {
			closeDialog();
			amiDesktop.cancelLink();
		} else if ("set_zindex_to_default".equals(id)) {
			amiDesktop.setDefaultWindowZIndexToCurrent();
		}

	}

	public DropDownMenuPortlet getDropDown() {
		return this.dropDown;
	}

	@Override
	public void onHtmlChanged(String old, String nuw) {
	}

	@Override
	public void onContextMenu(DropDownMenuPortlet tree, String action) {
		closeDialog();
		this.amiDesktop.onContextMenu(tree, action);
	}

	private void closeDialog() {
		if (this.dialog != null) {
			this.dialog.close();
			this.dialog = null;
		}
	}

	@Override
	public WebMenu createMenu(DropDownMenuPortlet dropdown, String id, WebMenuLink menu) {
		return this.amiDesktop.createMenu(dropdown, id, menu);
	}

	public String getLayoutTitleBarColor() {
		return layoutTitleBarColor;
	}
	public String getVersion() {
		return version;
	}
	public HtmlPortlet getLogo() {
		return logo;
	}
	public void setLogoSrc(String newLogosrc) {
		String html = "<div class='ami_dashboard ami_3forge_logo' style='width:100%;height:100%' onclick='" + logo.generateCallback("logo_clicked")
				+ "'><img width='65px' height='20px' src='" + newLogosrc + "'></div>";
		this.logo.setHtml(html);
	}
	public boolean setLayoutTitleBarColor(String layoutTitleBarColor) {
		if (OH.eq(this.layoutTitleBarColor, layoutTitleBarColor))
			return false;
		this.layoutTitleBarColor = layoutTitleBarColor;
		return true;
	}

	public boolean setLayoutTitleBarBorderColor(String layoutTitleBarBorderColor) {
		if (OH.eq(this.layoutTitleBarBorderColor, layoutTitleBarBorderColor))
			return false;
		this.layoutTitleBarBorderColor = layoutTitleBarBorderColor;
		return true;
	}

	public String getLayoutTitleBarBorderColor() {
		return layoutTitleBarBorderColor;
	}

	public String getLayoutTitleBarFontColor() {
		return layoutTitleBarFontColor;
	}

	public boolean setLayoutTitleBarFontColor(String layoutTitleBarFontColor) {
		if (OH.eq(this.layoutTitleBarFontColor, layoutTitleBarFontColor))
			return false;
		this.layoutTitleBarFontColor = layoutTitleBarFontColor;
		return true;
	}

	@Override
	public Map<String, Object> getConfiguration() {
		return super.getConfiguration();
	}
	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
	}

	private class DesktopBarHtmlEditor extends FormPortlet implements FormPortletListener {

		private final FormPortletButton submitButton;
		private final FormPortletButton previewButton;
		private final FormPortletButton cancelButton;
		private String originalHtml;
		private final AmiWebDesktopBar desktopBar;
		private final AmiWebFormPortletAmiScriptField htmlField;

		public DesktopBarHtmlEditor(AmiWebDesktopBar desktopBar, PortletConfig config) {
			super(config);
			this.desktopBar = desktopBar;
			FormPortletTitleField titleField = addField(new FormPortletTitleField("Edit HTML (Press alt-enter for preview)"));
			titleField.setLeftPosPx(20);
			titleField.setTopPosPx(FormPortletField.DEFAULT_PADDING_PX);
			titleField.setWidthPx(300);
			titleField.setHeightPx(FormPortletField.DEFAULT_HEIGHT);
			this.htmlField = addField(new AmiWebFormPortletAmiScriptField("", getManager(), ""));
			this.htmlField.setLeftPosPx(20);
			this.htmlField.setRightPosPx(20);
			this.htmlField.setTopPosPx(50);
			this.htmlField.setBottomPosPx(20);
			final AmiWebFormula tbf = service.getLayoutFilesManager().getLayout().getTitleBarHtmlFormula();
			this.htmlField.setValue(tbf.getFormula(false));
			this.previewButton = addButton(new FormPortletButton("Preview HTML"));
			this.submitButton = addButton(new FormPortletButton("Submit"));
			this.cancelButton = addButton(new FormPortletButton("Cancel"));
			addFormPortletListener(this);

			this.originalHtml = tbf.getFormula(false);
		}
		@Override
		public boolean onUserKeyEvent(KeyEvent keyEvent) {
			if (KeyEvent.ESCAPE.equals(keyEvent.getKey())) {
				return true;
			}
			return super.onUserKeyEvent(keyEvent);
		}
		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == this.previewButton) {
				preview();
			} else {
				if (button == this.submitButton) {
					preview();
				} else if (button == this.cancelButton) {
					final AmiWebFormula tbf = service.getLayoutFilesManager().getLayout().getTitleBarHtmlFormula();
					tbf.setFormula(this.originalHtml, false);
				}
				close();
			}
		}
		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

		}
		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
			if (keycode == 13 && mask != 0) {
				preview();
			}
		}
		private void preview() {
			final AmiWebFormula tbf = service.getLayoutFilesManager().getLayout().getTitleBarHtmlFormula();
			tbf.setFormula(this.htmlField.getValue(), false);
		}
	}

	@Override
	public void onFormulaChanged(AmiWebFormula formula, DerivedCellCalculator old, DerivedCellCalculator nuw) {
		final AmiWebFormula tbf = this.service.getLayoutFilesManager().getLayout().getTitleBarHtmlFormula();
		if (formula == tbf) {
			DerivedCellCalculator calc = tbf.getFormulaCalc();
			if (calc != null) {
				//				MapsBackedMap<String, Object> t = new MapsBackedMap<String, Object>(false, this.service.getGlobalVars(), (Map) CH.m("this", this));
				this.dashboardDefaultHtml = this.service.cleanHtml((String) calc.get(service.createStackFrame(service)));
			} else
				this.dashboardDefaultHtml = null;

			updateDashboard(false);
		}

	}

	//	public void setJustIcon(boolean b) {
	//		if (this.isJustIcon == b)
	//			return;
	//		this.isJustIcon = b;
	//		buildDashboard();
	//	}

	public void showAsDialog(boolean top) {
		OH.assertNull(this.getParent());
		RootPortletDialog dialog = getManager().showDialog("", this, getManager().getRoot().getWidth(), 28);
		dialog.getPortlet().setSize(getManager().getRoot().getWidth(), 28);
		dialog.setHeaderSize(0);
		dialog.setHasCloseButton(false);
		dialog.setBorderSize(0);
		dialog.setCloseOnClickOutside(true);
		dialog.getOptions().put("windowBorderInnerSize", 0);
		dialog.getOptions().put("windowBorderOuterSize", 0);
		dialog.setPosition(0, top ? 0 : getManager().getRoot().getHeight() - 28);
		dialog.setShadeOutside(false);
		this.dialog = dialog;
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		return this.amiDesktop.onUserKeyEvent(keyEvent);
	}

	@Override
	public void onCustomCallback(HtmlPortlet formPortlet, String customType, Object customParamsJson, Map<String, String> rawAttributes) {
		if ("amiCustomCallback".contentEquals(customType)) {
			List args = (List) customParamsJson;
			String type = AmiUtils.s(args.get(0));
			List remainingArgs = args.subList(1, args.size());
			this.amiDesktop.getCallbacks().execute(AmiWebScriptManager.CALLBACK_ONAMIJSCALLBACKINTITLEBAR.getMethodName(), type, remainingArgs);
		}
	}

}
