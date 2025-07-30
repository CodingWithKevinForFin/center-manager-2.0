package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayResponse;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.dm.portlets.AmiWebAddRealtimePanelPortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmTreePortlet;
import com.f1.ami.web.dm.portlets.AmiWebDmViewDataPortlet;
import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.base.Action;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.container.ResultMessage;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuItem;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.WebMenuLinkListener;
import com.f1.suite.web.menu.impl.AbstractWebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.peripheral.MouseEvent;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.BasicWebContextMenuFactory;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.WebMenuListener;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameTuple2;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebAbstractPortlet extends GridPortlet
		implements AmiWebPortlet, ConfirmDialogListener, ToDerivedString, WebMenuLinkListener, WebMenuListener, BasicWebContextMenuFactory, AmiWebDomObject {
	public static final ParamsDefinition CALLBACK_DEF_ONSIZE = new ParamsDefinition("onSize", Object.class, "Integer width,Integer height");
	public static final ParamsDefinition CALLBACK_DEF_ONVISIBLE = new ParamsDefinition("onVisible", Object.class, "Boolean visible");
	public static final ParamsDefinition CALLBACK_DEF_ONKEY = new ParamsDefinition("onKey", Object.class, "com.f1.suite.web.peripheral.KeyEvent event");
	public static final ParamsDefinition CALLBACK_DEF_ONMOUSE = new ParamsDefinition("onMouse", Object.class, "com.f1.suite.web.peripheral.MouseEvent event");
	static {
		CALLBACK_DEF_ONSIZE.addDesc("Called when the size of this panel changes");
		CALLBACK_DEF_ONSIZE.addParamDesc(0, "new width of the panel, in pixels");
		CALLBACK_DEF_ONSIZE.addParamDesc(1, "new height of the panel, in pixels");
		CALLBACK_DEF_ONSIZE.addRetDesc("return value is ignored");

		CALLBACK_DEF_ONVISIBLE.addDesc("Called when the panel's visibility changes");
		CALLBACK_DEF_ONVISIBLE.addParamDesc(0, "true=visible,false=hidden");
		CALLBACK_DEF_ONVISIBLE.addRetDesc("return value is ignored");

		CALLBACK_DEF_ONKEY.addDesc(
				"Called when a keyboard key is pressed while this panel is focused. Note, event calls cascade up from child to parent panels, depending on what the child's onKey(...) callback returns");
		CALLBACK_DEF_ONKEY.addParamDesc(0, "The key event");
		CALLBACK_DEF_ONKEY.addRetDesc("return \"stop\" to not have the parent panel receive the event. Otherwise, the event gets fired on the parent panel.");

		CALLBACK_DEF_ONMOUSE.addDesc(
				"Called when a mouse key is pressed while this panel is focused. Note, event calls cascade up from child to parent panels, depending on what the child's onMouse(...) callback returns");
		CALLBACK_DEF_ONMOUSE.addParamDesc(0, "The mouse event");
		CALLBACK_DEF_ONMOUSE.addRetDesc("return \"stop\" to not have the parent panel receive the event. Otherwise, the event gets fired on the parent panel.");
	}
	private static final String VARNAME_PANEL_ID = "panel.id";
	private static final String VARNAME_USER_PREF_ID = "panel.upid";
	private static final String VARNAME_PANEL_TITLE = "panel.title";
	private static final String VARNAME_PANEL_TYPES = "panel.types";
	private static final String VARNAME_PANEL_PID = "panel.pid";
	private static final String VARNAME_PANEL_TYPE = "panel.type";
	protected final AmiWebFormulasImpl formulas;

	protected static final int DATAMODEL_DEFAULT = 1;
	static final String DATAMODEL_DEFAULT_OLD = "DEFAULT";

	private static final Logger log = LH.get();

	final private AmiWebService service;
	private AmiWebOverrideValue<String> amiTitle = new AmiWebOverrideValue<String>("");

	private Integer fontSize = 10;
	private String fontFamily;

	private boolean isInitDone = false;
	private boolean showConfigButtons = true;

	private AmiWebCustomContextMenuManager customContextMenu;

	private int paddingLeftPx;
	private int paddingRightPx;
	private int paddingTopPx;
	private int paddingBottomPx;
	private int paddingRadiusTopLeftPx;
	private int paddingRadiusTopRightPx;
	private int paddingRadiusBottomLeftPx;
	private int paddingRadiusBottomRightPx;
	private int paddingShadowHPx;
	private int paddingShadowVPx;
	private int paddingShadowSizePx;
	private String paddingShadowColor;
	private String paddingBorderColor;
	private String paddingColor;
	private int paddingBorderSizePx;

	protected final AmiWebAmiScriptCallbacks callbacks;

	protected void setChild(Portlet p) {
		setChild(p, 0, 0);
		this.getInnerPortlet().setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
		this.getInnerPortlet().setHtmlCssClass(this.getHtmlCssClass());
	}
	protected void setChild(Portlet p, int x, int y) {
		this.gridPortlet.addChild(p, x, y + 1);
	}

	final private static com.f1.utils.structs.table.stack.BasicCalcTypes PORTLET_VAR_CONTS_TYPES = new BasicCalcTypes();
	static {
		PORTLET_VAR_CONTS_TYPES.putType(VARNAME_PANEL_TITLE, String.class);
		PORTLET_VAR_CONTS_TYPES.putType(VARNAME_PANEL_TYPES, String.class);
		PORTLET_VAR_CONTS_TYPES.putType("panel.visualization", String.class);
		PORTLET_VAR_CONTS_TYPES.putType(VARNAME_PANEL_ID, String.class);
		PORTLET_VAR_CONTS_TYPES.putType(VARNAME_PANEL_PID, String.class);
		PORTLET_VAR_CONTS_TYPES.putType(VARNAME_USER_PREF_ID, String.class);
	}

	final private CalcFrame portletVarConsts;
	final private MutableCalcFrame portletVarUserDefined;
	final private CalcFrame portletVars;

	final private AmiWebTitlePortlet titlePortlet;
	final private GridPortlet gridPortlet;
	final private GridPortlet outerGridPortlet;
	private Map<String, Object> origUserPrefs;

	public AmiWebAbstractPortlet(PortletConfig config) {
		super(config);
		this.formulas = new AmiWebFormulasImpl(this);
		this.gridPortlet = new GridPortlet(generateConfig());
		this.outerGridPortlet = new GridPortlet(generateConfig());
		this.titlePortlet = new AmiWebTitlePortlet(generateConfig());
		this.addChild(this.getOuterGridPortlet(), 0, 0);
		this.getOuterGridPortlet().addChild(this.getGridPortlet(), 0, 0);
		this.getGridPortlet().addChild(this.titlePortlet, 0, 0);
		this.getGridPortlet().setRowSize(0, 0);
		this.service = AmiWebUtils.getService(getManager());
		this.callbacks = new AmiWebAmiScriptCallbacks(service, this);
		List<ParamsDefinition> cd = this.service.getScriptManager().getCallbackDefinitions(this.getClass());
		for (ParamsDefinition i : cd)
			this.callbacks.registerCallbackDefinition(i);
		//		this.callbacks.registerCallbackDefinition(DEF_ONVISIBLE);
		//		this.callbacks.registerCallbackDefinition(DEF_ONSIZE);
		//		this.callbacks.registerCallbackDefinition(DEF_ONKEY);

		this.portletVarConsts = new BasicCalcFrame(PORTLET_VAR_CONTS_TYPES);
		this.portletVarConsts.putValue("panel.visualization", getPanelType());

		this.portletVarUserDefined = new MutableCalcFrame();

		this.portletVars = new CalcFrameTuple2(portletVarUserDefined, portletVarConsts);
		this.stylePeer = new AmiWebStyledPortletPeer(this, service);
		this.portletVarConsts.putValue(VARNAME_PANEL_PID, getPortletId());
		this.portletVarConsts.putValue(VARNAME_PANEL_TYPES, "");
		this.portletVarConsts.putValue(VARNAME_PANEL_TITLE, getAmiTitle(false));
		this.portletVarConsts.putValue(VARNAME_PANEL_ID, getAmiPanelId());
		this.portletVarConsts.putValue(VARNAME_PANEL_TYPE, getPanelType());
		this.customContextMenu = new AmiWebCustomContextMenuManager(this);
		this.stackFrame = service.createStackFrameReusable(this);
	}
	@Override
	final public AmiWebService getService() {
		return service;
	}

	protected void updatePanelTypesVar(Iterable<String> types) {
		this.portletVarConsts.putValue(VARNAME_PANEL_TYPES, SH.join(',', types));
	}

	//	@Override
	//	final public AmiWebManager getAgentManager() {
	//		return agentManager;
	//	}

	@Override
	final public String getAmiTitle(boolean isOverride) {
		return amiTitle.getValue(isOverride);
	}

	public String getTitle(boolean isOverride) {
		return this.titlePortlet.getTitle(isOverride);
	}

	@Override
	public void setAmiTitle(String title, boolean isOverride) {
		this.amiTitle.setValue(title, isOverride);
		if (this.amiTitle.isOverride() == false) {
			this.portletVarConsts.putValue(VARNAME_PANEL_TITLE, title);
		}
		this.titlePortlet.setTitle(title, isOverride);
		flagPendingAjax();
	}

	@Override
	final public void populateLowerConfigMenu(WebMenu sink) {
		boolean isntBlank = !(this instanceof AmiWebBlankPortlet);
		sink.add(new BasicWebMenuDivider());
		sink.add(new BasicWebMenuLink("Settings...", true, "settings").setBackgroundImage(AmiWebConsts.ICON_SETTINGS));
		if (isntBlank) {
			sink.add(new BasicWebMenuLink("Style...", true, "style").setBackgroundImage(AmiWebConsts.ICON_STYLING));
			if (!this.callbacks.getAmiScriptCallbackDefinitions().isEmpty())
				sink.add(new BasicWebMenuLink("AmiScript Callbacks...", true, "script").setBackgroundImage(AmiWebConsts.ICON_STATISTICS));
			if (!(this instanceof AmiWebFilterPortlet) && !(this instanceof AmiWebDividerPortlet))
				sink.add(this.customContextMenu.generateCustomizeMenu());
			if (!(this instanceof AmiWebAbstractContainerPortlet)) {
				service.getDesktop().getLinkHelper().buildStartLinkMenu(sink, this);
			}
		}
		if (getIsFreeFloatingPortlet())
			sink.add(new BasicWebMenuLink("Show Panel in Data Modeler", true, "show_dmg").setBackgroundImage(AmiWebConsts.ICON_DATAMODEL));
		sink.add(new BasicWebMenuDivider());

		if (this.isTransient()) {
			if (this.getNonTransientPanel() == null)
				sink.add(new BasicWebMenuLink("Delete Transient Panels", true, "delete").setBackgroundImage(AmiWebConsts.ICON_DELETE));
			else
				sink.add(new BasicWebMenuLink("Delete Transient Panels, Restoring Inner non-transient Panel", true, "delete").setBackgroundImage(AmiWebConsts.ICON_DELETE));
			sink.add(new BasicWebMenuLink("Export Transient Panels", true, "export_transient").setBackgroundImage(AmiWebConsts.ICON_EXPORT));
		} else if (this.isReadonlyLayout()) {
			AmiWebAliasPortlet parent = this.getAmiParent();
			boolean parentRo = parent.isReadonlyLayout();
			if (!parentRo && isntBlank)
				sink.add(new BasicWebMenuLink("Unlink Highlighted Panels", true, "hide"));
		}
		if (!(this instanceof AmiWebAbstractContainerPortlet) && !(this instanceof AmiWebFilterPortlet)) {
			if (!isTransient() && this instanceof AmiWebDmPortlet) {
				BasicWebMenu filter = new BasicWebMenu("Create Filter", true);
				filter.setBackgroundImage(AmiWebConsts.ICON_FILTER_ORANGE);
				if (!this.getAmiParent().isReadonlyLayout()) {
					filter.add(new BasicWebMenuLink("Below...", true, "filter_below"));
					filter.add(new BasicWebMenuLink("To the Left...", true, "filter_left"));
					filter.add(new BasicWebMenuLink("To the Right...", true, "filter_right"));
					filter.add(new BasicWebMenuLink("Above ...", true, "filter_above"));
				}
				if (!service.getLayoutFilesManager().getLayout().isReadonly())
					filter.add(new BasicWebMenuLink("Popout window ...", true, "filter_popout"));
				if (!filter.getChildren().isEmpty())
					sink.add(filter);
			}
		}
		if (this instanceof AmiWebDmPortlet) {
			AmiWebDmPortlet dmp = (AmiWebDmPortlet) this;
			List<WebMenuLink> links = new ArrayList<WebMenuLink>();
			AmiWebDmManager dmm = service.getDmManager();
			for (String t : dmp.getUsedDmAliasDotNames()) {
				AmiWebDm dm = dmm.getDmByAliasDotName(t);
				if (dm != null) {
					links.add(new BasicWebMenuLink(dm.getDmName(), true, "edit_dm_" + dm.getAmiLayoutFullAliasDotId()));
				}
			}
			sink.add(new BasicWebMenu("Edit Underlying Datamodel...", !links.isEmpty(), links));
			boolean hasFilter = false;
			for (String dmName : dmp.getUsedDmAliasDotNames()) {
				AmiWebDm dm = dmm.getDmByAliasDotName(dmName);
				if (dm != null && dm.getFilters().size() > 0) {
					hasFilter = true;
					break;
				}
			}
			boolean hasDm = !dmp.getUsedDmAliasDotNames().isEmpty();
			if (hasFilter) {
				BasicWebMenu viewMenu = new BasicWebMenu("View Underlying Data...", hasDm);
				viewMenu.add(new BasicWebMenuLink("Before Filters", hasDm, "view_data_before"));
				viewMenu.add(new BasicWebMenuLink("After Filters", hasDm, "view_data_after"));
				sink.add(viewMenu);
			} else {
				sink.add(new BasicWebMenuLink("View Underlying Data...", hasDm, "view_data"));
			}
		}
		if (sink.getListener() == null)
			sink.setListener(this);

		if (this.getIsFreeFloatingPortlet()) {
			sink.add(new BasicWebMenuDivider());
			if (!isTransient() && !isReadonlyLayout()) {
				AmiWebAliasPortlet parent = getAmiParent();
				boolean parentRo = parent.isReadonlyLayout();
				if (isntBlank && !parentRo) {
					BasicWebMenu createMenu = new BasicWebMenu();
					createMenu = new BasicWebMenu("Replace With...", true);
					createMenu.add(new BasicWebMenuLink("Table / Visualization / Form", true, "recreate_data_st").setBackgroundImage(AmiWebConsts.ICON_VIZ));//TODO:
					createMenu.add(new BasicWebMenuLink("Realtime Table / Visualization", true, "recreate_data_rt").setBackgroundImage(AmiWebConsts.ICON_VIZ));//TODO:
					createMenu.add(new BasicWebMenuLink("HTML Panel", true, "recreate_form").setBackgroundImage(AmiWebConsts.ICON_FORM));
					createMenu.add(new BasicWebMenuLink("Filter", true, "recreate_filter").setBackgroundImage(AmiWebConsts.ICON_FILTER));
					createMenu.add(new BasicWebMenuLink("Blank Portlet", isntBlank, "recreate_blank").setBackgroundImage(AmiWebConsts.ICON_NEW));
					BasicWebMenu linksMenu = new BasicWebMenu("Hidden Panel", true);
					for (AmiWebLayoutFile child : service.getLayoutFilesManager().getLayout().getChildrenRecursive(true))
						for (AmiWebPortletDef c : child.getRootHiddenPanels().values())
							if (AmiWebInnerDesktopPortlet.Builder.ID.equals(c.getBuilderId()))
								populate(linksMenu, child, c.getAmiPanelId(), -1);//-1, skips desktop
							else
								populate(linksMenu, child, c.getAmiPanelId(), 0);
					if (linksMenu.getChildren().isEmpty()) // if there isn't any hidden panel, disable the menu option
						linksMenu.setEnabled(false);
					createMenu.add(linksMenu);
					sink.add(createMenu);
				}
				if (!parentRo) {
					WebMenu layoutMenu = (WebMenu) new BasicWebMenu("Add Blank Panel", true).setBackgroundImage(AmiWebConsts.ICON_NEW_PANEL);
					layoutMenu.add(new BasicWebMenuLink("Left", true, "addpanel_left").setBackgroundImage(AmiWebConsts.ICON_ADD_PANEL_LEFT));
					layoutMenu.add(new BasicWebMenuLink("Right", true, "addpanel_right").setBackgroundImage(AmiWebConsts.ICON_ADD_PANEL_RIGHT));
					layoutMenu.add(new BasicWebMenuLink("Above", true, "addpanel_above").setBackgroundImage(AmiWebConsts.ICON_ADD_PANEL_TOP));
					layoutMenu.add(new BasicWebMenuLink("Below", true, "addpanel_below").setBackgroundImage(AmiWebConsts.ICON_ADD_PANEL_BOTTOM));
					sink.add(layoutMenu);
					sink.add(new BasicWebMenuLink("Place Highlighted In Scroll Panel", true, "wrap_in_scroll"));
					sink.add(new BasicWebMenuLink("Place Highlighted In Tab", true, "wrap_in_tab").setBackgroundImage(AmiWebConsts.ICON_PLACE_IN_TAB));
					sink.add(new BasicWebMenuLink("Place Highlighted In Window", true, "mov_to_new_window").setBackgroundImage(AmiWebConsts.ICON_PLACE_WINDOW));
				}

				sink.add(new BasicWebMenuDivider());
				if (!parentRo && isntBlank) {
					sink.add(new BasicWebMenuLink("Move to Different Layout File....", this.service.getLayoutFilesManager().hasMultipleLayouts(), "rename_alias")
							.setBackgroundImage(AmiWebConsts.ICON_PLACE_WINDOW));
					sink.add(new BasicWebMenuLink("Cut Highlighted Panels", isntBlank, "cut").setBackgroundImage(AmiWebConsts.ICON_CUT));
					sink.add(new BasicWebMenuLink("Copy Highlighted Panels", isntBlank, "copy").setBackgroundImage(AmiWebConsts.ICON_COPY));
				}
				if (!isReadonlyLayout())
					sink.add(new BasicWebMenuLink("Delete Highlighted Panels", true, "delete").setBackgroundImage(AmiWebConsts.ICON_DELETE));
				if (!parentRo)
					sink.add(new BasicWebMenuLink("Unlink Highlighted Panels", true, "hide"));
				sink.add(new BasicWebMenuLink("Export Highlighted Panels", true, "export").setBackgroundImage(AmiWebConsts.ICON_EXPORT));
				if (hasTransient(this))
					sink.add(new BasicWebMenuLink("Export Highlighted Panels (including Transients)", true, "export_transient").setBackgroundImage(AmiWebConsts.ICON_EXPORT));
				String portletCutboard = this.getService().getDesktop().getPortletCutboard();
				String portletClipboard = this.getService().getDesktop().getPortletClipboard();
				if (portletClipboard == null && portletCutboard == null) {
					sink.add(new BasicWebMenuLink("Paste", false, "paste").setBackgroundImage(AmiWebConsts.ICON_PASTE));
				} else if (isntBlank) {
					WebMenu pasteMenu = new BasicWebMenu("Paste", true);
					pasteMenu.add(new BasicWebMenuLink("To the Left", true, "pasteat_left"));
					pasteMenu.add(new BasicWebMenuLink("To the Right", true, "pasteat_right"));
					pasteMenu.add(new BasicWebMenuLink("Above", true, "pasteat_above"));
					pasteMenu.add(new BasicWebMenuLink("Below", true, "pasteat_below"));
					pasteMenu.add(new BasicWebMenuLink("Replace", true, "pastereplace"));
					sink.add(pasteMenu);

					if (portletCutboard == null) {
						WebMenu pasteMenuRel = new BasicWebMenu("Paste w/ External Relationships", true);
						pasteMenuRel.add(new BasicWebMenuLink("To the Left", true, "pastewrelat_left"));
						pasteMenuRel.add(new BasicWebMenuLink("To the Right", true, "pastewrelat_right"));
						pasteMenuRel.add(new BasicWebMenuLink("Above", true, "pastewrelat_above"));
						pasteMenuRel.add(new BasicWebMenuLink("Below", true, "pastewrelat_below"));
						pasteMenuRel.add(new BasicWebMenuLink("Replace", true, "pastewrelreplace"));
						sink.add(pasteMenuRel);
					}
				} else {
					sink.add(new BasicWebMenuLink("Paste", true, "paste"));
					if (portletCutboard == null)
						sink.add(new BasicWebMenuLink("Paste w/ External Relationships", true, "pastewrel"));
				}
			}
		}

	}
	static private boolean hasTransient(AmiWebAliasPortlet t) {
		if (t.isTransient())
			return true;
		if (t instanceof AmiWebAbstractContainerPortlet) {
			AmiWebAbstractContainerPortlet c = (AmiWebAbstractContainerPortlet) t;
			for (AmiWebAliasPortlet i : c.getAmiChildren())
				if (hasTransient(i))
					return true;
		}
		return false;
	}
	protected void populate(WebMenu sink, AmiWebLayoutFile file, String amiPanelId, int depth) {
		AmiWebPortletDef s = file.getHiddenPanelByPanelId(amiPanelId);
		if (s != null) {
			if (depth >= 0) {
				String prefix = depth == 0 ? "" : (SH.repeat("&nbsp;", depth * 2) + " &#8658;&nbsp;");
				AbstractWebMenuItem t = new BasicWebMenuLink(prefix + AmiWebUtils.getFullAlias(s.getLayoutFile().getFullAlias(), s.getAmiPanelId()) + "(" + s.getBuilderId() + ")",
						!hasChildInUse(s, service), "link_portlet_" + file.getFullAlias() + "." + s.getAmiPanelId());
				sink.add(t);
			}
			for (String adn : s.getChildren()) {
				if (adn.indexOf('.') != -1)
					continue;
				String fa = AmiWebUtils.getNameFromAdn(adn);
				AmiWebLayoutFile lm = file.getChildByRelativeAlias(AmiWebUtils.getAliasFromAdn(adn));
				populate(sink, lm, fa, depth + 1);
			}
		} else {
			if (depth >= 0) {
				AmiWebAliasPortlet portlet = service.getPortletByPanelId(file.getFullAlias(), amiPanelId);
				if (portlet != null) {
					String prefix = depth == 0 ? "" : (SH.repeat("&nbsp;", depth * 2) + " &#8658;&nbsp;");
					AbstractWebMenuItem t = new BasicWebMenuLink(prefix + portlet.getAmiLayoutFullAliasDotId() + "(" + portlet.getPortletConfig().getBuilderId() + ", in use)",
							false, "link_portlet_" + file.getFullAlias() + "." + portlet.getAmiPanelId());
					sink.add(t);
				}
			}
		}
	}
	private static boolean hasChildInUse(AmiWebPortletDef s, AmiWebService service) {
		if (s == null)
			return true;
		AmiWebLayoutFile file = s.getLayoutFile();
		if (service.getPortletByPanelId(file.getFullAlias(), s.getAmiPanelId()) != null)
			return true;
		for (String adn : s.getChildren()) {
			String fa = AmiWebUtils.getFullAlias(file.getAlias(), adn);
			AmiWebLayoutFile lm = file.getChildByRelativeAlias(AmiWebUtils.getAliasFromAdn(adn));
			if (hasChildInUse(lm.getHiddenPanelByPanelId(AmiWebUtils.getNameFromAdn(fa)), service))
				return true;
		}
		return false;
	}
	@Override
	public boolean onMenuItem(WebMenuLink item) {
		if (this.onAmiContextMenu(item.getAction())) {
			service.getDesktop().updateDashboard();
			return true;
		} else {
			return true;
		}
	}
	@Override
	public boolean onAmiContextMenu(String id) {
		AmiWebDesktopPortlet d = service.getDesktop();
		String cpid = this.getPortletId();
		if (id.startsWith("filter_")) {
			AmiWebDmPortlet p = (AmiWebDmPortlet) this;
			String position = SH.stripPrefix(id, "filter_", true);
			d.addFilter(position, p);
			return true;
		} else if ("show_dmg".equals(id)) {
			d.showDatamodelGraph();
			d.getSpecialPortlet(AmiWebDmTreePortlet.class).showInDataModeler(this.getAmiLayoutFullAliasDotId());
			return true;
		} else if (OH.eq(id, "recreate_blank")) {
			d.displayRecreateMessage("add_blank", cpid);
			return true;
		} else if (OH.eq(id, "recreate_form")) {
			d.displayRecreateMessage("add_form", cpid);
			return true;
		} else if (OH.eq(id, "recreate_filter")) {
			d.displayRecreateMessage("add_filter", cpid);
			return true;
		} else if ("add_blank".equals(id)) {
			d.replacePortlet(cpid, d.newAmiWebAmiBlankPortlet(d.getAmiLayoutFullAliasForPortletId(cpid)));
			return true;
		} else if (SH.startsWith(id, "link_portlet_")) {
			String t = SH.stripPrefix(id, "link_portlet_", true);
			AmiWebAliasPortlet r = service.getLayoutFilesManager().buildPortlet(t);
			//	service.getDomObjectsManager().initVariables();
			this.service.getAmiPanelManager().onInitDone();
			d.replacePortlet(cpid, r);
			for (AmiWebAliasPortlet i : PortletHelper.findPortletsByType(r, AmiWebAliasPortlet.class))
				AmiWebUtils.recompileAmiscript(i);
			return true;
		} else if (OH.eq(id, "recreate_data_rt")) {
			getManager().showDialog("Choose Ami Data", new AmiWebAddRealtimePanelPortlet(generateConfig(), service, cpid, getAmiLayoutFullAlias()));
			return true;
		} else if (OH.eq(id, "recreate_data_st")) {
			d.showAddPanelPortlet(cpid);
			return true;
		} else if (OH.eq(id, "addpanel_left")) {
			d.showDividerEditor(d.addAdjacentTo(cpid, d.newAmiWebAmiBlankPortlet(d.getParentAmiLayoutFullAliasForPortletId(cpid)), AmiWebDesktopPortlet.LEFT));
			return true;
		} else if (OH.eq(id, "addpanel_right")) {
			d.showDividerEditor(d.addAdjacentTo(cpid, d.newAmiWebAmiBlankPortlet(d.getParentAmiLayoutFullAliasForPortletId(cpid)), AmiWebDesktopPortlet.RIGHT));
			return true;
		} else if (OH.eq(id, "addpanel_above")) {
			d.showDividerEditor(d.addAdjacentTo(cpid, d.newAmiWebAmiBlankPortlet(d.getParentAmiLayoutFullAliasForPortletId(cpid)), AmiWebDesktopPortlet.ABOVE));
			return true;
		} else if (OH.eq(id, "addpanel_below")) {
			d.showDividerEditor(d.addAdjacentTo(cpid, d.newAmiWebAmiBlankPortlet(d.getParentAmiLayoutFullAliasForPortletId(cpid)), AmiWebDesktopPortlet.BELOW));
			return true;
		} else if (OH.eq(id, "wrap_in_scroll")) {
			AmiWebScrollPortlet r = (AmiWebScrollPortlet) getManager().buildPortlet(AmiWebScrollPortlet.Builder.ID);
			d.registerNewPortlet(r, d.getAmiLayoutFullAliasForPortletId(cpid));
			d.onUserWrapChild(cpid, r);
			return true;
		} else if (OH.eq(id, "wrap_in_tab")) {
			AmiWebTabPortlet newChild = d.newTabPortlet(d.getAmiLayoutFullAliasForPortletId(cpid));
			d.onUserWrapChild(cpid, newChild);
			newChild.showTabsStyleEditorIfSet();
			return true;
		} else if (OH.eq(id, "mov_to_new_window")) {
			d.moveToNewWindow(cpid);
			return true;
		} else if (OH.eq(id, "rename_alias")) {
			d.renameAlias(cpid);
			return true;
		} else if (d.getLinkHelper().handleMenuItem(id, cpid)) {
			return true;
		} else if (OH.eq(id, "cut")) {
			return d.onCut(this);
		} else if (OH.eq(id, "copy")) {
			return d.onCopy(this);
		} else if (SH.startsWith(id, "paste")) {
			boolean includeHangingLinks = SH.startsWith(id, "pastewrel");
			String loc = null;
			if (SH.startsWith(id, "pasteat_")) {
				loc = SH.stripPrefix(id, "pasteat_", true);
			} else if (SH.startsWith(id, "pastewrelat_")) {
				loc = SH.stripPrefix(id, "pastewrelat_", true);
			}
			return d.onPaste(loc, includeHangingLinks, this);
		} else if (SH.startsWith(id, "delete")) {
			Portlet removed = getManager().getPortlet(cpid);
			if (d.getDesktop().isInTearout(removed) && (removed.getParent() == d.getDesktop() || removed.getParent() instanceof RootPortlet)) {
				getManager().showAlert("You cannot delete this panel because it is currently undocked. Redock this panel first and then delete it.");
			} else
				d.onUserDeletePortlet((AmiWebAliasPortlet) removed);
			return true;
		} else if (SH.startsWith(id, "hide")) {
			Portlet removed = getManager().getPortlet(cpid);
			if (d.getDesktop().isInTearout(removed) && (removed.getParent() == d.getDesktop() || removed.getParent() instanceof RootPortlet)) {
				getManager().showAlert("You cannot hide this panel because it is currently undocked. Redock this panel first and then hide it.");
			} else
				d.onUserHidePortlet((AmiWebAliasPortlet) removed);
			return true;
		} else if (OH.eq(id, "export")) {
			Map<String, Object> config = AmiWebLayoutHelper.exportConfiguration(service, cpid, true, false, false);
			AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
			viewConfigPortlet.setConfiguration(config);
			getManager().showDialog("Export configuration", viewConfigPortlet);
			return true;
		} else if (OH.eq(id, "export_transient")) {
			Map<String, Object> config = AmiWebLayoutHelper.exportConfiguration(service, cpid, true, false, true);
			AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
			viewConfigPortlet.setConfiguration(config);
			getManager().showDialog("Export configuration", viewConfigPortlet);
			return true;
		} else if ("settings".equals(id)) {
			getService().getSecurityModel().assertPermitted(this, "settings");
			getManager().showDialog("Settings", showSettingsPortlet());
			return true;
		} else if (SH.startsWith(id, "edit_dm_")) {
			String dmid = SH.stripPrefix(id, "edit_dm_", true);
			AmiWebDmManager dmm = service.getDmManager();
			AmiWebDmsImpl dm = dmm.getDmByAliasDotName(dmid);
			AmiWebUtils.showEditDmPortlet(service, dm, "Edit Datamodel");
			return true;

		} else if ("view_data_before".equals(id)) {
			AmiWebDmManager dmm = this.service.getDmManager();
			List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
			AmiWebDmPortlet dmp = (AmiWebDmPortlet) this;
			for (String dmName : dmp.getUsedDmAliasDotNames())
				datamodels.add(dmm.getDmByAliasDotName(dmName));
			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, (AmiWebDmPortlet) this, AmiWebDmViewDataPortlet.RESPONSEDATA_BEFORE_FILTER);
			getManager().showDialog("View Data", window);
			return true;
		} else if ("view_data_after".equals(id)) {
			AmiWebDmManager dmm = this.service.getDmManager();
			List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
			AmiWebDmPortlet dmp = (AmiWebDmPortlet) this;
			for (String dmName : dmp.getUsedDmAliasDotNames())
				datamodels.add(dmm.getDmByAliasDotName(dmName));
			AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, (AmiWebDmPortlet) this, AmiWebDmViewDataPortlet.RESPONSEDATA_AFTER_FILTER);
			getManager().showDialog("View Data", window);
			return true;
		} else if ("view_data".equals(id)) {
			showUnderlyingData();
			return true;
		} else if (this.customContextMenu.handleCustomizeCallback(id)) {
			return true;

		} else if ("script".equals(id)) {
			this.callbacks.showEditor();
			return true;
		} else if ("style".equals(id)) {
			AmiWebUtils.showStyleDialog("Edit Style", this, new AmiWebEditStylePortlet(this.getStylePeer(), generateConfig()), generateConfig());
			return true;
		}
		return false;
	}
	public void showUnderlyingData() {
		AmiWebDmManager dmm = this.service.getDmManager();
		List<AmiWebDm> datamodels = new ArrayList<AmiWebDm>();
		AmiWebDmPortlet dmp = (AmiWebDmPortlet) this;
		for (String dmName : dmp.getUsedDmAliasDotNames()) {
			AmiWebDmsImpl dm = dmm.getDmByAliasDotName(dmName);
			if (dm == null) {
				getManager().showAlert("Underlying datamodel not found: " + dmName);
				return;
			}
			datamodels.add(dm);
		}
		AmiWebDmViewDataPortlet window = new AmiWebDmViewDataPortlet(generateConfig(), datamodels, (AmiWebDmPortlet) this, AmiWebDmViewDataPortlet.RESPONSEDATA_DEFAULT);
		getManager().showDialog("View Data", window);
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.aliasDotId = AmiWebUtils.getFullAlias(this.amiLayoutAlias, getAmiPanelId());
		String title = CH.getOr(Caster_String.INSTANCE, configuration, "amiTitle", null);
		String amiPanelId = (String) configuration.get("amiPanelId");
		String userPrefId = (String) configuration.get("upid");
		if (userPrefId == null)
			userPrefId = amiPanelId;//backwards compatibility
		userPrefId = getService().registerAmiUserPrefId(userPrefId, this);
		if (userPrefId != null)
			setAmiUserPrefId(userPrefId);

		if (title != null) {
			setAmiTitle(title, false);
		}
		Map<String, Object> configuration2 = (Map<String, Object>) configuration.get("titlePnl");
		if (configuration2 != null)
			this.titlePortlet.init(configuration2, origToNewIdMapping, sb);

		LH.fine(log, "Loading AMI Panel: ", getAmiTitle(false));
		this.stylePeer.initStyle((Map<String, Object>) configuration.get("amiStyle"));

		// Build custom context menu
		if (this.customContextMenu != null)
			this.customContextMenu.close();
		this.customContextMenu = new AmiWebCustomContextMenuManager(this);
		this.customContextMenu.init(configuration.get("customMenu"));

		super.init(configuration, origToNewIdMapping, sb);
		Map<String, Object> callbacks2 = (Map<String, Object>) configuration.get("callbacks");
		this.callbacks.init(null, getAmiLayoutFullAlias(), callbacks2, sb);
		this.onAdnChanged(null, this.aliasDotId);
	}
	public void onInitDone() {
		this.callbacks.initCallbacksLinkedVariables();
		this.customContextMenu.onInitDone();
	}
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		Map<String, Object> titlePnl = this.titlePortlet.getConfiguration();
		AmiWebUtils.putSkipEmpty(r, "amiTitle", getAmiTitle(false));
		AmiWebUtils.putSkipEmpty(r, "titlePnl", titlePnl);
		r.put("amiPanelId", getAmiPanelId());
		r.put("upid", getAmiUserPrefId());
		AmiWebUtils.putSkipEmpty(r, "amiStyle", this.stylePeer.getStyleConfiguration());
		AmiWebUtils.putSkipEmpty(r, "callbacks", this.callbacks.getConfiguration());
		AmiWebUtils.putSkipEmpty(r, "customMenu", this.customContextMenu.getConfiguration());
		return r;
	}
	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		Action action = result.getActionNoThrowable();
		if (action instanceof AmiCenterPassToRelayResponse) {
			AmiRelayResponse agentResponse = ((AmiCenterPassToRelayResponse) action).getAgentResponse();
			if (agentResponse instanceof AmiRelayRunAmiCommandResponse) {
				AmiRelayRunAmiCommandRequest cmdRequest = (AmiRelayRunAmiCommandRequest) ((AmiCenterPassToRelayRequest) result.getRequestMessage().getAction()).getAgentRequest();
				AmiRelayRunAmiCommandResponse cmdResponse = (AmiRelayRunAmiCommandResponse) agentResponse;
				//				if (cmdResponse.getAmiStringPoolMap() != null)//need to ensure new keys get added before processing response
				//					service.getPrimaryWebManager().addAmiKeyStringPoolMappings(cmdResponse.getAmiStringPoolMap());
				if (SH.is(cmdResponse.getAmiMessage())) {
					getManager().showAlert(cmdResponse.getAmiMessage());
				}
				if (cmdResponse.getAmiScript() != null)
					getScriptManager().parseAndExecuteAmiScript(cmdResponse.getAmiScript(), null, null, getService().getDebugManager(), AmiDebugMessage.TYPE_CMD_RESPONSE, this,
							cmdRequest.getCommandDefinitionId());
			}
		}
	}

	protected void rebuildAmiData() {
	}

	public void setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
		this.flagPendingAjax();
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
		this.flagPendingAjax();
	}

	public String getFontFamily() {
		return fontFamily;
	}

	@Override
	public void onAmiInitDone() {
		if (this.isInitDone)
			throw new IllegalStateException("already init done");
		this.isInitDone = true;
		this.origUserPrefs = this.getUserPref();
	}

	@Override
	public boolean getIsFreeFloatingPortlet() {
		return true;
	}

	public boolean isInitDone() {
		return isInitDone;
	}

	@Override
	public CalcTypes getPortletVarTypes() {
		return portletVars;
	}

	@Override
	final public CalcFrame getPortletVars() {
		return portletVars;
	}

	private String panelId;
	private String userPrefId;

	@Override
	final public String getAmiPanelId() {
		return panelId;
	}

	@Override
	final public void setAmiUserPrefId(String userPrefId) {
		if (OH.eq(this.userPrefId, userPrefId))
			return;
		this.userPrefId = userPrefId;
		this.portletVarConsts.putValue(VARNAME_USER_PREF_ID, userPrefId);
	}

	@Override
	final public String getAmiUserPrefId() {
		return userPrefId;
	}

	@Override
	public boolean putPortletVar(String key, Object value, Class type) {
		if (value == null && type == null) {
			if (this.portletVarUserDefined.removeTypeValue(key) == null)
				return false;
			this.portletVarUserDefined.removeValue(key);
		} else {
			if (value != null && !type.isInstance(value))
				throw new ClassCastException("For " + key + ", Not a " + type.getName() + ": " + value);
			if (OH.eq(value, this.portletVarUserDefined.getValue(key)) && OH.eq(type, this.portletVarUserDefined.getType(key)))
				return false;
			this.portletVarUserDefined.putTypeValue(key, type, value);
		}
		return true;
	}

	protected boolean inEditMode() {
		AmiWebDesktopPortlet desktop = PortletHelper.findParentByType(this, AmiWebDesktopPortlet.class);
		return desktop == null || desktop.getInEditMode() && !isTransient() && !isReadonlyLayout();
	}

	@Override
	public boolean runAmiLink(String name) {
		return false;
	}

	@Override
	public boolean runAmiLinkId(String id) {
		return false;
	}
	@Override
	public String toString() {
		return super.toString() + " - " + getConfigMenuTitle() + " (" + getAmiPanelId() + ")";
	}

	protected void putUserDefinedVariable(String name, Class<?> type) {
		this.userDefinedVariables.putType(name, type);
	}

	@Override
	public com.f1.base.CalcTypes getUserDefinedVariables() {
		return userDefinedVariables;
	}
	public void setUserDefinedVariables(com.f1.base.CalcTypes userDef) {
		this.userDefinedVariables.clear();
		this.userDefinedVariables.putAll(userDef);
	}

	public void removeUserDefinedVariable(String fieldName) {
		this.userDefinedVariables.removeType(fieldName);
	}

	final private BasicCalcTypes userDefinedVariables = new BasicCalcTypes();
	final private BasicCalcFrame emptyCalcFrame = new BasicCalcFrame(userDefinedVariables);

	public BasicCalcFrame emptyCalcFrame() {
		return this.emptyCalcFrame;
	}

	final private AmiWebStyledPortletPeer stylePeer;

	@Override
	public void onLinkingChanged(AmiWebDmLink link) {

	}

	public com.f1.base.CalcTypes getSpecialVariables() {
		return EmptyCalcTypes.INSTANCE;
	}

	public String getSpecialVariableTitleFor(String name) {
		return name;
	}

	@Override
	public AmiWebDmLink getCurrentLinkFilteringThis() {
		return null;
	}

	@Override
	public AmiWebStyledPortletPeer getStylePeer() {
		return this.stylePeer;
	}
	@Override
	public void onStyleValueChanged(short value, Object old, Object nuw) {
		switch (value) {
			case AmiWebStyleConsts.CODE_TITLE_PNL_FONT_SZ:
				Integer fontSize = Caster_Integer.INSTANCE.cast(nuw);
				this.titlePortlet.setTitleFontSize(fontSize);
				this.getGridPortlet().setRowSize(0, (int) (1.3 * fontSize));
				break;
			case AmiWebStyleConsts.CODE_TITLE_PNL_FONT_FAM:
				this.titlePortlet.setTitleFontFam((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TITLE_PNL_ALIGN:
				this.titlePortlet.setTitleAlign((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_TITLE_PNL_FONT_CL:
				this.titlePortlet.setTitleColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_PD_LF_PX:
				setPaddingLeftPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_RT_PX:
				setPaddingRightPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_TP_PX:
				setPaddingTopPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_BTM_PX:
				setPaddingBottomPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_RAD_TP_LF_PX:
				setPaddingRadiusTopLeftPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_RAD_TP_RT_PX:
				setPaddingRadiusTopRightPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_RAD_BTM_LF_PX:
				setPaddingRadiusBottomLeftPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_RAD_BTM_RT_PX:
				setPaddingRadiusBottomRightPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_CL:
				setPaddingColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_PD_SHADOW_HZ_PX:
				setPaddingShadowHPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_SHADOW_VT_PX:
				setPaddingShadowVPx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_SHADOW_SZ_PX:
				setPaddingShadowSizePx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_SHADOW_CL:
				setPaddingShadowColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_PD_BDR_SZ_PX:
				setPaddingBorderSizePx(Caster_Integer.INSTANCE.cast(nuw));
				break;
			case AmiWebStyleConsts.CODE_PD_BDR_CL:
				setPaddingBorderColor((String) nuw);
				break;
		}
		if (this instanceof AmiWebStyledScrollbarPortlet) {
			switch (value) {
				case AmiWebStyleConsts.CODE_SCROLL_GRIP_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollGripColor((String) nuw);
					break;
				case AmiWebStyleConsts.CODE_SCROLL_TRACK_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollTrackColor((String) nuw);
					break;
				case AmiWebStyleConsts.CODE_SCROLL_BTN_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollButtonColor((String) nuw);
					break;
				case AmiWebStyleConsts.CODE_SCROLL_ICONS_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollIconsColor((String) nuw);
					break;
				case AmiWebStyleConsts.CODE_SCROLL_BDR_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollBorderColor(Caster_String.INSTANCE.cast(nuw));
					break;
				case AmiWebStyleConsts.CODE_SCROLL_WD:
					((AmiWebStyledScrollbarPortlet) this).setScrollBarWidth(Caster_Integer.INSTANCE.cast(nuw));
					break;
				case AmiWebStyleConsts.CODE_SCROLL_BAR_RADIUS:
					((AmiWebStyledScrollbarPortlet) this).setScrollBarRadius(Caster_Integer.INSTANCE.cast(nuw));
					break;
				case AmiWebStyleConsts.CODE_SCROLL_BAR_HIDE_ARROWS:
					((AmiWebStyledScrollbarPortlet) this).setScrollBarHideArrows(Caster_Boolean.INSTANCE.cast(nuw));
					break;
				case AmiWebStyleConsts.CODE_SCROLL_BAR_CORNER_CL:
					((AmiWebStyledScrollbarPortlet) this).setScrollBarCornerColor(Caster_String.INSTANCE.cast(nuw));
					break;
			}
		}
	}
	@Override
	public void onClosed() {
		this.removeFromDomManager();
		this.stylePeer.close();
		this.callbacks.close();
		this.customContextMenu.close();
		super.onClosed();
	}

	@Override
	public void drainJavascript() {
		if (paddingChanged) {
			int t = this.paddingBorderSizePx;
			rebuilddPadding(this.paddingTopPx, this.paddingRightPx, this.paddingBottomPx, this.paddingLeftPx, t, t, t, t);
			this.paddingChanged = false;
		}
		super.drainJavascript();
	}
	protected void rebuilddPadding(int paddingTopPx, int paddingRightPx, int paddingBottomPx, int paddingLeftPx, int innerPaddingTopPx, int innerPaddingRightPx,
			int innerPaddingBottomPx, int innerPaddingLeftPx) {
		if (this.getChildrenCount() > 0) {
			InnerPortlet outerPanel = this.getPanelAt(0, 0);
			InnerPortlet innerPanel = this.getOuterGridPortlet().getPanelAt(0, 0);
			innerPanel.setPadding(innerPaddingTopPx, innerPaddingRightPx, innerPaddingBottomPx, innerPaddingLeftPx);
			this.getOuterGridPortlet().setCssStyle("style.overflow=visible|_bg=" + paddingBorderColor + "|style.borderRadius=" + this.paddingRadiusTopLeftPx + "px "
					+ this.paddingRadiusTopRightPx + "px " + this.paddingRadiusBottomRightPx + "px " + this.paddingRadiusBottomLeftPx + "px");
			outerPanel.setPadding(paddingTopPx, paddingRightPx, paddingBottomPx, paddingLeftPx);
			StringBuilder t = new StringBuilder();
			setCssStyle(t.append("_bg=").append(this.paddingColor).toString());
			SH.clear(t);
			final boolean hasBorder = this.paddingBorderSizePx > 0 && SH.is(this.paddingBorderColor);
			final boolean hasShadow = this.paddingShadowSizePx > 0 && SH.is(this.paddingShadowColor);
			if (hasBorder || hasShadow) {
				t.append("_bg=").append(this.paddingBorderColor);
				t.append("|style.borderRadius=").append(this.paddingRadiusTopLeftPx).append("px ").append(this.paddingRadiusTopRightPx).append("px ")
						.append(this.paddingRadiusBottomRightPx).append("px ").append(this.paddingRadiusBottomLeftPx).append("px");
				t.append("|style.boxShadow=");
				if (hasShadow) {
					t.append(this.paddingShadowHPx).append("px ").append(this.paddingShadowVPx).append("px ").append(this.paddingShadowSizePx).append("px ")
							.append(this.paddingShadowSizePx / 4).append("px ").append(this.paddingShadowColor);
					outerPanel.setCssStyle(t.toString());
				} else
					outerPanel.setCssStyle("style.boxShadow=none");
			} else
				outerPanel.setCssStyle("style.boxShadow=none");
		}
	}
	public int getPaddingLeftPx() {
		return paddingLeftPx;
	}
	public AmiWebAbstractPortlet setPaddingLeftPx(int paddingLeftPx) {
		if (this.paddingLeftPx == paddingLeftPx)
			return this;
		this.paddingLeftPx = paddingLeftPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingRightPx() {
		return paddingRightPx;
	}
	public AmiWebAbstractPortlet setPaddingRightPx(int paddingRightPx) {
		if (this.paddingRightPx == paddingRightPx)
			return this;
		this.paddingRightPx = paddingRightPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingTopPx() {
		return paddingTopPx;
	}
	public AmiWebAbstractPortlet setPaddingTopPx(int paddingTopPx) {
		if (this.paddingTopPx == paddingTopPx)
			return this;
		this.paddingTopPx = paddingTopPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingBottomPx() {
		return paddingBottomPx;
	}
	public AmiWebAbstractPortlet setPaddingBottomPx(int paddingBottomPx) {
		if (this.paddingBottomPx == paddingBottomPx)
			return this;
		this.paddingBottomPx = paddingBottomPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingRadiusTopLeftPx() {
		return paddingRadiusTopLeftPx;
	}
	public AmiWebAbstractPortlet setPaddingRadiusTopLeftPx(int paddingRadiusTopLeftPx) {
		if (this.paddingRadiusTopLeftPx == paddingRadiusTopLeftPx) {
			return this;
		}
		this.paddingRadiusTopLeftPx = paddingRadiusTopLeftPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingRadiusTopRightPx() {
		return paddingRadiusTopRightPx;
	}
	public AmiWebAbstractPortlet setPaddingRadiusTopRightPx(int paddingRadiusTopRightPx) {
		if (this.paddingRadiusTopRightPx == paddingRadiusTopRightPx) {
			return this;
		}
		this.paddingRadiusTopRightPx = paddingRadiusTopRightPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingRadiusBottomLeftPx() {
		return paddingRadiusBottomLeftPx;
	}
	public AmiWebAbstractPortlet setPaddingRadiusBottomLeftPx(int paddingRadiusBottomLeftPx) {
		if (this.paddingRadiusBottomLeftPx == paddingRadiusBottomLeftPx) {
			return this;
		}
		this.paddingRadiusBottomLeftPx = paddingRadiusBottomLeftPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingRadiusBottomRightPx() {
		return paddingRadiusBottomRightPx;
	}
	public AmiWebAbstractPortlet setPaddingRadiusBottomRightPx(int paddingRadiusBottomRightPx) {
		if (this.paddingRadiusBottomRightPx == paddingRadiusBottomRightPx) {
			return this;
		}
		this.paddingRadiusBottomRightPx = paddingRadiusBottomRightPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingShadowHPx() {
		return paddingShadowHPx;
	}
	public AmiWebAbstractPortlet setPaddingShadowHPx(int paddingShadowHPx) {
		if (this.paddingShadowHPx == paddingShadowHPx)
			return this;
		this.paddingShadowHPx = paddingShadowHPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingShadowVPx() {
		return paddingShadowVPx;
	}
	public AmiWebAbstractPortlet setPaddingShadowVPx(int paddingShadowVPx) {
		if (this.paddingShadowVPx == paddingShadowVPx)
			return this;
		this.paddingShadowVPx = paddingShadowVPx;
		onPaddingChanged();
		return this;
	}
	public int getPaddingShadowSizePx() {
		return paddingShadowSizePx;
	}
	public AmiWebAbstractPortlet setPaddingShadowSizePx(int paddingShadowSizePx) {
		if (this.paddingShadowSizePx == paddingShadowSizePx)
			return this;
		this.paddingShadowSizePx = paddingShadowSizePx;
		onPaddingChanged();
		return this;
	}
	public String getPaddingShadowColor() {
		return paddingShadowColor;
	}
	public AmiWebAbstractPortlet setPaddingShadowColor(String paddingShadowColor) {
		if (this.paddingShadowColor == paddingShadowColor)
			return this;
		this.paddingShadowColor = paddingShadowColor;
		onPaddingChanged();
		return this;
	}
	public String getPaddingBorderColor() {
		return paddingBorderColor;
	}
	public AmiWebAbstractPortlet setPaddingBorderColor(String paddingBorderColor) {
		if (this.paddingBorderColor == paddingBorderColor)
			return this;
		this.paddingBorderColor = paddingBorderColor;
		this.titlePortlet.setPaddingBorderColor(paddingBorderColor);
		onPaddingChanged();
		return this;
	}
	public String getPaddingColor() {
		return paddingColor;
	}
	public AmiWebAbstractPortlet setPaddingColor(String paddingColor) {
		if (this.paddingColor == paddingColor)
			return this;
		this.paddingColor = paddingColor;
		onPaddingChanged();
		return this;
	}
	public int getPaddingBorderSizePx() {
		return paddingBorderSizePx;
	}
	public AmiWebAbstractPortlet setPaddingBorderSizePx(int paddingBorderSize) {
		if (this.paddingBorderSizePx == paddingBorderSize)
			return this;
		this.paddingBorderSizePx = paddingBorderSize;
		onPaddingChanged();
		return this;
	}
	protected void onPaddingChanged() {
		this.paddingChanged = true;
		flagPendingAjax();
	}
	@Override
	public void addChild(Portlet child) {
		super.addChild(child);
		onPaddingChanged();
	}
	protected void showWaitingSplash(boolean b) {
		InnerPortlet panel = this.getPanelAt(0, 0);
		if (panel != null)
			panel.setOverlayHtml(b ? this.service.getWaitSvg() : null);
	}

	private boolean paddingChanged = true;

	public boolean getShowConfigButtons() {
		return showConfigButtons;
	}

	public void setShowConfigButtons(boolean showConfigButtons) {
		this.showConfigButtons = showConfigButtons;
	}
	public GridPortlet getOuterGridPortlet() {
		return outerGridPortlet;
	}
	public GridPortlet getGridPortlet() {
		return gridPortlet;
	}
	public void setPanelTitle(String title) {
		this.titlePortlet.setTitle(title);
	}
	public Portlet getInnerPortlet() {
		if (this.gridPortlet.getChildsHeight() < 2)
			return null;
		return this.gridPortlet.getChildAt(0, 1);
	}
	public Portlet removeInnerPortlet() {
		return this.gridPortlet.removeChildAt(0, 1);
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return true;
	}

	@Override
	public WebMenu createMenu(Object correlationData) {
		BasicWebMenu r = new BasicWebMenu();
		addCustomMenuItems(r);
		return r;
	}

	@Override
	public void onMenuItem(String id) {
		if (isCustomContextMenuAction(id)) {
			this.processCustomContextMenuAction(id);
		}
	}

	@Override
	public void onMenuDismissed() {

	}

	@Override
	public AmiWebCustomContextMenuManager getCustomContextMenu() {
		return this.customContextMenu;
	}

	public void addCustomMenuItems(WebMenu sink) {
		sink.setStyle(this.getService().getDesktop().getMenuStyle());
		WebMenu customMenu;
		try {
			customMenu = this.customContextMenu.generateMenu();
		} catch (RuntimeException e) {
			getManager().showAlert("Error in custom menu.", e);
			return;
		}
		List<WebMenuItem> children = customMenu.getChildren();
		for (int i = 0; i < children.size(); i++) {
			children.get(i).setParent(null);
			sink.add(children.get(i));
		}
		//		if (children.size() > 0)
		//			sink.add(new BasicWebMenuDivider());
	}

	@Override
	public Map<String, Object> getUserPref() {
		LinkedHashMap<String, Object> r = new LinkedHashMap<String, Object>();
		return r;
	}

	@Override
	public void applyUserPref(Map<String, Object> values) {
	}

	@Override
	public Map<String, Object> getDefaultPref() {
		return this.origUserPrefs;
	}

	@Override
	public void setDefaultPref(Map<String, Object> defaultPref) {
		this.origUserPrefs = new HashMap<String, Object>();
		this.origUserPrefs.putAll(defaultPref);
	}

	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	public String getAmiScriptClassName() {
		return "Panel";
	}

	private String amiLayoutAlias = "";
	private String aliasDotId = null;

	@Override
	public String getAmiLayoutFullAlias() {
		return amiLayoutAlias;
	}
	public String getAmiLayoutFullAliasDotId() {
		return this.aliasDotId;
	}

	private List<AmiWebDmLink> dmLinksFromThisPortlet = new ArrayList<AmiWebDmLink>();
	private List<AmiWebDmLink> dmLinksToThisPortlet = new ArrayList<AmiWebDmLink>();
	private String ari;
	private boolean isTransient;

	@Override
	public Collection<AmiWebDmLink> getDmLinksFromThisPortlet() {
		return this.dmLinksFromThisPortlet;
	}
	@Override
	public Collection<AmiWebDmLink> getDmLinksToThisPortlet() {
		return this.dmLinksToThisPortlet;
	}
	@Override
	public void addDmLinkFromThisPortlet(AmiWebDmLink link) {
		this.dmLinksFromThisPortlet = new ArrayList(this.dmLinksFromThisPortlet);
		CH.addIdentityOrThrow(this.dmLinksFromThisPortlet, link);
	}
	@Override
	public void removeDmLinkFromThisPortlet(AmiWebDmLink link) {
		this.dmLinksFromThisPortlet = new ArrayList(this.dmLinksFromThisPortlet);
		CH.removeOrThrow(this.dmLinksFromThisPortlet, link);
	}
	@Override
	public void addDmLinkToThisPortlet(AmiWebDmLink link) {
		this.dmLinksToThisPortlet = new ArrayList(this.dmLinksToThisPortlet);
		CH.addIdentityOrThrow(this.dmLinksToThisPortlet, link);
	}
	@Override
	public void removeDmLinkToThisPortlet(AmiWebDmLink link) {
		this.dmLinksToThisPortlet = new ArrayList(this.dmLinksToThisPortlet);
		CH.removeOrThrow(this.dmLinksToThisPortlet, link);
	}

	@Override
	public AmiWebAliasPortlet getAmiParent() {
		return PortletHelper.findParentByType(this.getParent(), AmiWebAliasPortlet.class);
	};

	@Override
	public Collection<AmiWebAliasPortlet> getAmiChildren() {
		return Collections.emptyList();
	}

	@Override
	final public boolean setAdn(String adn) {
		if (OH.eq(this.aliasDotId, adn))
			return false;
		String oldAri = getAri();
		String oldAliasDotId = this.aliasDotId;
		this.service.onAdnChanging(this, this.aliasDotId, adn);
		this.aliasDotId = adn;
		this.amiLayoutAlias = AmiWebUtils.getAliasFromAdn(adn);
		this.panelId = AmiWebUtils.getNameFromAdn(adn);
		this.portletVarConsts.putValue(VARNAME_PANEL_ID, panelId);
		this.service.onAdnChanged(this, oldAliasDotId, adn);
		for (AmiWebDmLink i : this.dmLinksFromThisPortlet)
			i.onAmiPanelAdnChanged(this, oldAliasDotId, this.aliasDotId);
		for (AmiWebDmLink i : this.dmLinksToThisPortlet)
			i.onAmiPanelAdnChanged(this, oldAliasDotId, this.aliasDotId);
		this.callbacks.setAmiLayoutAlias(this.amiLayoutAlias);
		this.service.getDomObjectsManager().fireAriChanged(this, oldAri);
		onAdnChanged(oldAliasDotId, adn);
		this.addToDomManager();
		return true;
	}
	protected void onAdnChanged(String old, String adn) {
		this.updateAri();
		Portlet innerPortlet = this.getInnerPortlet();
		if (innerPortlet != null)
			innerPortlet.setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
	}

	@Override
	public boolean isReadonlyLayout() {
		return this.service.getLayoutFilesManager().getLayoutByFullAlias(getAmiLayoutFullAlias()).isReadonly();
	}

	@Override
	protected void onVisibilityChanged(boolean isVisible) {
		super.onVisibilityChanged(isVisible);
		this.callbacks.execute("onVisible", isVisible);
		boolean loggable = !this.getService().getDesktop().getInEditMode() && this.getService().getActivityLogLevel() > 0;
		// log if in user mode and prop set
		if (loggable) {
			String userName = this.getUserName();
			String ari = this.getAri();
			String sessionId = this.getService().getWebState().getSessionId();
			AmiWebActivityLogger.logPanel(userName, sessionId, isVisible, ari);
		}
	}
	@Override
	protected void onSizeChanged(int width, int height) {
		super.onSizeChanged(width, height);
		this.callbacks.execute("onSize", width, height);
	}

	//	@Override
	//	public void recompileAmiscript() {
	//		this.callbacks.recompileAmiscript();
	//		this.customContextMenu.recompileAmiscript();
	//		this.formulas.recompileAmiscript();
	//
	//	}
	public boolean isCustomContextMenuAction(String action) {
		return this.customContextMenu.isCustomContextMenuAction(action);
	}
	public void processCustomContextMenuAction(String action) {
		this.customContextMenu.processCustomContextMenuAction(action);
	}

	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.ari = AmiWebDomObject.ARI_TYPE_PANEL + ":" + this.getAmiLayoutFullAliasDotId();
		if (OH.ne(this.ari, oldAri)) {
			this.service.getDomObjectsManager().fireAriChanged(this, oldAri);
			this.customContextMenu.onPanelAriChanged();
		}
	}
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_PANEL;
	}

	@Override
	public String getDomLabel() {
		return this.getAmiPanelId();
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		ArrayList<AmiWebDomObject> r = new ArrayList<AmiWebDomObject>();
		r.addAll(this.customContextMenu.getChildren(false));
		return r;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getService().getLayoutFilesManager().getLayoutByFullAlias(this.getAmiLayoutFullAlias());
	}

	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.callbacks;
	};

	@Override
	public boolean isTransient() {
		return this.isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	private boolean isManagedByDomManager = false;
	private ReusableCalcFrameStack stackFrame;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		this.service.getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		Object ret = this.callbacks.execute("onKey", keyEvent);
		if (ret != null && "stop".equalsIgnoreCase(OH.toString(ret)))
			return false;
		return super.onUserKeyEvent(keyEvent);
	}

	public String logMe() {
		return getUserName() + "->" + this.getAri();
	}

	@Override
	public boolean onUserMouseEvent(MouseEvent mouseEvent) {
		Object ret = this.callbacks.execute("onMouse", mouseEvent);
		if (ret != null && "stop".equalsIgnoreCase(OH.toString(ret)))
			return false;
		return super.onUserMouseEvent(mouseEvent);
	}

	@Override
	public AmiWebScriptManagerForLayout getScriptManager() {
		return this.getService().getScriptManager(this.getAmiLayoutFullAlias());
	}

	public abstract AmiWebPanelSettingsPortlet showSettingsPortlet();
	@Override
	public AmiWebFormulasImpl getFormulas() {
		return this.formulas;
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return AmiWebUtils.getAvailableVariables(service, this);
	}

	@Override
	public AmiWebAliasPortlet getNonTransientPanel() {
		return isTransient() ? null : this;
	}

	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer peer) {
		if (this.getInnerPortlet() != null)
			this.getInnerPortlet().setHtmlCssClass(peer.getParentStyle());
		this.setHtmlCssClass(peer.getParentStyle());
	}

	//	@Override
	//	final public void onVarConstChanged(String var) {
	//		onVarsChanged(this, var);
	//	}

	@Override
	public ReusableCalcFrameStack getStackFrame() {
		return this.stackFrame;
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

	@Override
	public void onUserRequestFocus(MouseEvent e) {
		Portlet inner = getInnerPortlet();
		if (inner != null)
			inner.onUserRequestFocus(e);
		else
			super.onUserRequestFocus(e);
	}
}
