package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.ami.web.menu.AmiWebCustomContextMenuManager;
import com.f1.base.IterableAndSize;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.EmptyIterable;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.SingletonIterable;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebLayoutFile implements AmiWebDomObject, ToDerivedString {
	public static final byte ONLY = 1;
	public static final byte PRIMARY = 2;
	public static final byte SECONDARY = 3;

	public static final String DEFAULT_ROOT_ALIAS = AmiWebLayoutHelper.DEFAULT_ROOT_ALIAS;
	final private BasicIndexedList<String, AmiWebLayoutFile> childrenByAlias = new BasicIndexedList<String, AmiWebLayoutFile>();
	private String origLocation;
	private String origSource;
	private boolean origIsLocationRelative;
	private String location;
	private String source;
	private Map lastLoadedJson;
	private String lastLoadedText;
	private String rawTextFromDisk;
	final private String alias;
	private String currentText;
	private AmiWebLayoutFile parent;
	private String fullAlias;
	private String origBrowserTitle;
	private String origTransientIdPrefix;
	private String origCustomUserMenuTitle;
	private String origCustomPrefImportMode;
	private String origUpserPrefNamespace;
	private String origTitleBarHtml;
	private HashMap<String, AmiWebPortletDef> hiddenPanels = new LinkedHashMap<String, AmiWebPortletDef>();
	private Map<String, Object> origStylesConfig;
	private Map<String, Object> origGuiServicesConfig;
	private boolean isLocationRelative;
	private List<Map<String, Object>> origDs;
	private boolean isUserReadonly;
	private boolean isFileReadonly;
	private byte duplicateStatus = ONLY;//this same file is in use more than once (aka diamond pattern)
	private AmiWebLayoutFilesManager manager;
	private final AmiWebService service;
	final private AmiWebFormulasImpl formulas;
	final private AmiWebFormula titleBarHtmlFormula;
	private byte origMenubarPosition = AmiWebDesktopPortlet.MENUBAR_TOP;
	final private AmiWebCss css;
	//	private boolean hasChangedSinceLastSaved; //TODO: Add

	public AmiWebLayoutFile(AmiWebLayoutFile other) {
		this(other.getParent(), other);
	}
	private AmiWebLayoutFile(AmiWebLayoutFile parent, AmiWebLayoutFile other) {
		this.service = other.service;
		this.css = new AmiWebCss(service.getCustomCssManager());
		this.formulas = new AmiWebFormulasImpl(this);
		this.titleBarHtmlFormula = this.formulas.addFormulaTemplate("titleBarHtml");
		if (parent == null)
			this.formulas.addFormulasListener(service.getDesktop().getDesktopBar());
		this.manager = other.manager;
		this.parent = parent;
		this.location = other.location;
		this.isLocationRelative = other.isLocationRelative;
		if (this.isLocationRelative && this.parent == null)
			throw new IllegalStateException("Can not be relative if parent");

		this.source = other.source;
		this.origLocation = this.location;
		this.origIsLocationRelative = this.isLocationRelative;
		this.origSource = this.source;
		this.lastLoadedJson = other.lastLoadedJson;
		this.lastLoadedText = other.lastLoadedText;
		this.alias = other.alias;
		this.currentText = other.currentText;
		this.fullAlias = other.fullAlias;
		this.origBrowserTitle = other.origBrowserTitle;
		this.origTransientIdPrefix = other.origTransientIdPrefix;
		this.origCustomUserMenuTitle = other.origCustomUserMenuTitle;
		this.origCustomPrefImportMode = other.origCustomPrefImportMode;
		this.origUpserPrefNamespace = other.origUpserPrefNamespace;
		this.origTitleBarHtml = other.origTitleBarHtml;
		this.origStylesConfig = other.origStylesConfig;
		this.origGuiServicesConfig = other.origGuiServicesConfig;
		this.hiddenPanels = other.hiddenPanels;//TODO: should this be a deep copy?
		this.isUserReadonly = other.isUserReadonly;
		this.isFileReadonly = other.isFileReadonly;
		this.duplicateStatus = other.duplicateStatus;
		for (AmiWebLayoutFile i : other.getChildren())
			addChild(new AmiWebLayoutFile(this, i));
		this.updateAri();
		//		this.service.getDomObjectsManager().addManagedDomObject(this);
	}
	public AmiWebLayoutFile(AmiWebLayoutFilesManager manager, String location, String source) {
		this(manager, DEFAULT_ROOT_ALIAS, location, false, false, source, null);
	}

	public AmiWebLayoutFile(AmiWebLayoutFilesManager manager, String alias, String location, boolean isLocationRelative, boolean readonly, String source, AmiWebLayoutFile parent) {
		this.service = manager.getService();
		this.css = new AmiWebCss(service.getCustomCssManager());
		this.formulas = new AmiWebFormulasImpl(this);
		this.titleBarHtmlFormula = this.formulas.addFormulaTemplate("titleBarHtml");
		if (parent == null)
			this.formulas.addFormulasListener(service.getDesktop().getDesktopBar());
		this.manager = manager;
		if (EH.isWindows()) {
			location = SH.replaceAll(location, '\\', '/');
		}
		this.location = location;
		this.isLocationRelative = isLocationRelative;
		this.isUserReadonly = readonly;
		this.alias = alias;
		this.source = source;
		this.origLocation = this.location;
		this.origIsLocationRelative = this.isLocationRelative;
		this.origSource = this.source;
		this.parent = parent;
		OH.assertEq(alias.equals(DEFAULT_ROOT_ALIAS), parent == null);
		updateFullAliases();
		this.updateAri();
		//		this.service.getDomObjectsManager().addManagedDomObject(this);
	}

	public String getSource() {
		return source;
	}
	public String getOrigSource() {
		return origSource;
	}
	public String getLocation() {
		return location;
	}
	public boolean getIsLocationRelative() {
		return isLocationRelative;
	}
	public String getAlias() {
		return alias;
	}
	public int getChildrenCount() {
		return this.childrenByAlias.getSize();
	}
	public AmiWebLayoutFile getChildAt(int pos) {
		return this.childrenByAlias.getAt(pos);
	}
	public void addChild(AmiWebLayoutFile child, int location) {
		this.childrenByAlias.add(child.getAlias(), child, location);
	}
	public void addChild(AmiWebLayoutFile child) {
		if (getChildByAlias(child.getAlias()) != null)
			throw new RuntimeException("Duplicate alias: " + child.getAlias());
		this.childrenByAlias.add(child.getAlias(), child);
	}

	public AmiWebLayoutFile getChildByAlias(String alias) {
		return this.childrenByAlias.getNoThrow(alias);
	}
	public AmiWebLayoutFile getChildByRelativeAlias(String alias) {
		if (DEFAULT_ROOT_ALIAS.equals(alias))
			return this;
		String nest = SH.afterFirst(alias, '.', null);
		if (nest == null)
			return this.childrenByAlias.getNoThrow(alias);
		else {
			AmiWebLayoutFile t = this.childrenByAlias.getNoThrow(SH.beforeFirst(alias, '.', null));
			return t == null ? null : t.getChildByRelativeAlias(nest);
		}
	}
	public void removeChild(AmiWebLayoutFile child) {
		this.childrenByAlias.remove(child.getAlias());
	}
	public void removeChildAt(int i) {
		this.childrenByAlias.removeAt(i);
	}

	public int getChildPosition(AmiWebLayoutFile t) {
		return this.childrenByAlias.getPosition(t.getAlias());
	}

	public Set<String> getChildAliases() {
		return this.childrenByAlias.keySet();
	}

	public String getLastLoadedText() {
		return this.lastLoadedText;
	}

	public static void getAllPortletsUnder(Portlet portlet, List<AmiWebAliasPortlet> sink, String amiAlias) {
		if (portlet instanceof AmiWebAliasPortlet && OH.eq(amiAlias, ((AmiWebAliasPortlet) portlet).getAmiLayoutFullAlias())) {
			sink.add((AmiWebAliasPortlet) portlet);
		}
		if (portlet instanceof PortletContainer) {
			final PortletContainer pc = (PortletContainer) portlet;
			for (Portlet p : pc.getChildren().values())
				getAllPortletsUnder(p, sink, amiAlias);
		}
	}
	public Map getJson(AmiWebService service) {
		String layoutName = service.getLayoutFilesManager().getLayoutName();
		if (SH.equals(this.alias, AmiWebLayoutFile.DEFAULT_ROOT_ALIAS) && SH.equals(this.location, layoutName))
			return service.getLayoutFilesManager().getLayoutConfiguration(true);
		if (this.lastLoadedJson != null)
			return this.lastLoadedJson;
		String layoutData = Tuple2.getB(service.getLayoutFilesManager().loadLayoutData(this.location, this.source));
		return (Map) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(layoutData);
	}
	public Map buildCurrentJson(AmiWebService service) {
		AmiWebDesktopPortlet amiDesktop = service.getDesktop();
		List<AmiWebAliasPortlet> activePortlets = new ArrayList<AmiWebAliasPortlet>();
		getAllPortletsUnder(amiDesktop.getDesktop(), activePortlets, this.fullAlias);
		final List<Map<String, Object>> portletConfigs = new ArrayList<Map<String, Object>>();
		Map<String, Object> r = new LinkedHashMap<String, Object>();
		for (AmiWebAliasPortlet p : activePortlets)
			if (!p.isTransient())
				portletConfigs.add(AmiWebLayoutHelper.toPortletConfig(p));
		for (AmiWebPortletDef i : this.hiddenPanels.values())
			portletConfigs.add(AmiWebLayoutHelper.toPortletConfig(i));
		Collections.sort(portletConfigs, AmiWebLayoutHelper.PORTLETID_COMPARATOR);
		List<Map> included = new ArrayList<Map>();
		for (AmiWebLayoutFile i : this.childrenByAlias.values())
			included.add(CH.mSkipNull("type", i.getSource(), "alias", i.getAlias(), "location", i.getLocation(), "relative", i.getIsLocationRelative(), "readonly",
					i.getUserReadonly()));

		r.put("portletConfigs", portletConfigs);
		r.put("includeFiles", included);
		AmiWebVarsManager varsManager = service.getVarsManager();
		Map<String, Object> metadata = new HashMap<String, Object>();
		AmiWebScriptManagerForLayout sm = service.getScriptManager(fullAlias);

		metadata.put("rt", service.getWebManagers().getConfiguration(getFullAlias()));
		metadata.put("dm", service.getDmManager().getConfiguration(getFullAlias()));
		if (DEFAULT_ROOT_ALIAS.equals(this.alias)) {
			metadata.put("menubarPosition", AmiWebDesktopPortlet.menubarPositionToString(service.getDesktop().getMenubarPosition()));
			AmiWebUtils.putSkipEmpty(metadata, "browserTitle", varsManager.getBrowserTitle());
			metadata.put("transientIdPrefix", varsManager.getTransientIdPrefix());
			AmiWebUtils.putSkipEmpty(metadata, "customUserMenuTitle", varsManager.getCustomUserMenuTitle());
			metadata.put("customPrefsImportMode", AmiWebVarsManager.customPrefsImportModeToString(varsManager.getCustomPrefsImportMode()));
			AmiWebUtils.putSkipEmpty(metadata, "titleBarHtml", titleBarHtmlFormula.getFormulaConfig());
			AmiWebUtils.putSkipEmpty(metadata, "userPrefNamespace", varsManager.getUserPrefNamespace());
			metadata.put("stm", service.getStyleManager().getConfiguration());//TODO:
			AmiWebUtils.putSkipEmpty(metadata, "guiServices", service.getGuiServiceAdaptersManager().getConfiguration());
		} else {
			metadata.put("menubarPosition", AmiWebDesktopPortlet.menubarPositionToString(this.origMenubarPosition));
			AmiWebUtils.putSkipEmpty(metadata, "browserTitle", this.origBrowserTitle);
			metadata.put("transientIdPrefix", varsManager.getTransientIdPrefix());
			AmiWebUtils.putSkipEmpty(metadata, "customUserMenuTitle", this.origCustomUserMenuTitle);
			metadata.put("customPrefsImportMode", this.origCustomPrefImportMode);
			AmiWebUtils.putSkipEmpty(metadata, "titleBarHtml", this.origTitleBarHtml);
			AmiWebUtils.putSkipEmpty(metadata, "userPrefNamespace", this.origUpserPrefNamespace);
			metadata.put("stm", this.origStylesConfig);
			AmiWebUtils.putSkipEmpty(metadata, "guiServices", this.origGuiServicesConfig);
			Map<String, Object> dm = (Map<String, Object>) metadata.get("dm");
			if (dm == null) {
				dm = new HashMap<String, Object>();
				metadata.put("dm", dm);
			}
			dm.put("ds", this.origDs);
		}
		AmiWebUtils.putSkipEmpty(metadata, "idle", service.getIdleSessionManager().getConfiguration());
		AmiWebUtils.putSkipEmpty(metadata, "callbacks", sm.getLayoutCallbacks().getConfiguration());
		AmiWebUtils.putAmiScript(metadata, "amiCustomCss", this.css.getCustomCss());
		AmiWebUtils.putSkipEmpty(metadata, "vars", new HashMap<String, String>(sm.getLayoutVariableScripts()));
		AmiWebUtils.putAmiScript(metadata, "amiScriptMethods", sm.getDeclaredMethodsScript());

		metadata.put("fileVersion", 4);
		r.put("metadata", metadata);
		return r;
	}
	public void applyMetadataToLayout(AmiWebService service) {
		Map metadata = this.lastLoadedJson == null ? null : (Map) this.lastLoadedJson.get("metadata");
		if (metadata == null) {
			metadata = Collections.EMPTY_MAP;
		}
		AmiWebScriptManagerForLayout sm = service.getScriptManager(getFullAlias());
		try {
			final Map<String, Object> stylesConfig = (Map<String, Object>) metadata.get("stm");
			final Map<String, String> vars = (Map<String, String>) metadata.get("vars");
			Object menuBarPosObj = metadata.get("menubarPosition");
			final byte menubarPosition;
			if (menuBarPosObj instanceof Number)//backwards compatibility
				menubarPosition = ((Number) menuBarPosObj).byteValue();
			else
				menubarPosition = AmiWebDesktopPortlet.parseMenuPosition(OH.castNoThrow(menuBarPosObj, String.class));
			final String browserTitle = (String) metadata.get("browserTitle");
			final String transientIdPrefix;
			if (metadata.isEmpty()) {
				// file -> new
				transientIdPrefix = AmiWebVarsManager.DEFAULT_TRANSIENT_ID_PREFIX;
			} else if (!metadata.containsKey("transientIdPrefix")) {
				// loading a layout from a version before we added transient ID
				transientIdPrefix = "";
			} else {
				transientIdPrefix = (String) metadata.get("transientIdPrefix");
			}
			final String customUserMenuTitle = (String) metadata.get("customUserMenuTitle");
			final String cpim = (String) metadata.get("customPrefsImportMode");
			final String titleBarHtml = (String) metadata.get("titleBarHtml");
			this.origMenubarPosition = menubarPosition;
			this.origStylesConfig = stylesConfig;
			this.origBrowserTitle = browserTitle;
			this.origTransientIdPrefix = transientIdPrefix;
			this.origCustomPrefImportMode = cpim;
			this.origTitleBarHtml = titleBarHtml;
			final String customCss = AmiWebUtils.getAmiScript(metadata, "amiCustomCss", null);
			final AmiWebDesktopPortlet amiDesktop = service.getDesktop();
			final AmiWebDebugManagerImpl dm = service.getDebugManager();

			StringBuilder sb = new StringBuilder();
			if (vars != null)
				for (Map.Entry<String, String> e : vars.entrySet())
					sm.putLayoutVariableScript(e.getKey(), e.getValue(), sb);

			this.css.setCustomCss(customCss, dm);
			if (DEFAULT_ROOT_ALIAS.equals(getFullAlias())) {
				service.getVarsManager().setTransientIdPrefix(transientIdPrefix);
				service.initDefaultStyle();
				if (stylesConfig != null) {
					service.getStyleManager().init(stylesConfig);
				} else {
					service.initDefaultLayoutStyle();
				}
				service.getStyleManager().fillInMissingParentStyles();
				service.getGuiServiceAdaptersManager().init();
				service.getDesktop().setMenubarPosition(this.origMenubarPosition);
				service.getVarsManager().setBrowserTitle(browserTitle);
				service.getVarsManager().setCustomUserMenuTitle(customUserMenuTitle);
				service.getVarsManager()
						.setCustomPrefsImportMode(cpim == null ? AmiWebVarsManager.CUST_PREF_IMPORT_MODE_REJECT : AmiWebVarsManager.parseCustomPresImportMode(cpim));
				titleBarHtmlFormula.setFormula(SH.is(titleBarHtml) ? titleBarHtml : null, false);
				amiDesktop.assignGlobalStyles(metadata);
			}
		} catch (RuntimeException e) {
			throw new RuntimeException("Error for file: " + this.describe(), e);
		}
		for (AmiWebLayoutFile i : this.childrenByAlias.values()) {
			AmiWebLayoutHelper.manageBackwardsCompatibility(service.getPortletManager(), i.getLastLoadedJson());
			i.applyMetadataToLayout(service);
		}
		try {
			StringBuilder sb = new StringBuilder();
			final Map<String, Object> dmConfig = (Map<String, Object>) metadata.get("dm");
			final Map<String, Object> rtConfig = (Map<String, Object>) metadata.get("rt");
			final Map<String, Object> callbacks = (Map<String, Object>) metadata.get("callbacks");
			final Map<String, Object> idle = (Map<String, Object>) metadata.get("idle");
			final Map<String, Object> guiServiceConfig = (Map) metadata.get("guiServices");
			final String upns = (String) metadata.get("userPrefNamespace");
			final String customMethods = AmiWebUtils.getAmiScript(metadata, "amiScriptMethods", null);
			this.origDs = dmConfig == null ? null : (List<Map<String, Object>>) dmConfig.get("ds");
			final AmiWebDebugManagerImpl dm = service.getDebugManager();
			this.origGuiServicesConfig = guiServiceConfig;
			this.origUpserPrefNamespace = upns;
			if (!sm.setDeclaredMethods(customMethods, dm, new StringBuilder())) {
				sm.setDeclaredMethodsNoCompile(customMethods);
				if (service.getDesktop().getIsLocked())
					service.getPortletManager().showAlert("<b>Failed</b> to compile this dashboard's custom methods");
				else
					service.getPortletManager().showAlert(
							"<b>Failed</b> to compile this dashboard's custom methods<BR>Please fix then recompile the amiscript by clicking Submit under <b>Dashboard -> Custom Methods</b>");
			}
			if (DEFAULT_ROOT_ALIAS.equals(getFullAlias())) {
				service.getGuiServiceAdaptersManager().init(DEFAULT_ROOT_ALIAS, guiServiceConfig, sb);
				service.getDesktop().getDesktopBar().onFormulaChanged(this.titleBarHtmlFormula, null, this.titleBarHtmlFormula.getFormulaCalc());
			}
			sm.getLayoutCallbacks().init(null, getFullAlias(), callbacks, new StringBuilder());
			service.getVarsManager().setUserPrefNamespace(upns);
			// add rt resources first to avoid having duplicate rt resource
			service.getWebManagers().init(this.getFullAlias(), rtConfig, sb);
			service.getDmManager().init(this.getFullAlias(), dmConfig, null, sb);
			service.getIdleSessionManager().init(idle);
		} catch (Exception e) {
			throw new RuntimeException("Error for file: " + this.describe(), e);
		}
	}
	public void onInitDone() {
		if (DEFAULT_ROOT_ALIAS.equals(getFullAlias())) {
			this.service.getGuiServiceAdaptersManager().onInitDone();
		}
		this.service.getScriptManager(this.fullAlias).getLayoutCallbacks().initCallbacksLinkedVariables();
		for (AmiWebLayoutFile i : this.childrenByAlias.values()) {
			i.onInitDone();
		}
	}
	public void applyPortletsToLayout(AmiWebService service) {
		try {
			if (lastLoadedJson == null)
				return;
			this.hiddenPanels.clear();
			AmiWebLayoutHelper.readPortletConfigs(service, this.lastLoadedJson, this.hiddenPanels, this);
			for (AmiWebLayoutFile i : this.childrenByAlias.values())
				i.applyPortletsToLayout(service);
		} catch (Exception e) {
			throw new RuntimeException("Error with: " + this.describe(), e);
		}
	}
	public String describe() {
		if (isLocationRelative)
			return source + ":" + location + " (relative)";
		else
			return source + ":" + location;
	}
	public Map getLastLoadedJson() {
		return lastLoadedJson;
	}

	public void setJson(String text, boolean wasFromDisk) {
		if (OH.ne(this.lastLoadedText, text)) {
			this.lastLoadedJson = (Map) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(text);
			if (this.lastLoadedJson.containsKey("layouts"))
				throw new RuntimeException("This is an export of multiple ami layouts, cannot load as a single layout");
			this.lastLoadedText = text;
			if (this.currentText == null) {
				this.currentText = lastLoadedText;
			}
		}
		if (wasFromDisk)
			setRawTextFromDisk(text);
	}

	public AmiWebLayoutFile getParent() {
		return this.parent;
	}
	public String getFullAlias() {
		return this.fullAlias;
	}
	public IterableAndSize<AmiWebLayoutFile> getChildren() {
		return this.childrenByAlias.values();
	}
	public Iterable<AmiWebLayoutFile> getChildrenRecursive(boolean includeMe) {
		if (this.getChildrenCount() == 0)
			return includeMe ? new SingletonIterable<AmiWebLayoutFile>(this) : EmptyIterable.INSTANCE;
		return getChildrenRecursive(includeMe, new ArrayList<AmiWebLayoutFile>());
	}
	public List<AmiWebLayoutFile> getChildrenRecursive(boolean includeMe, List<AmiWebLayoutFile> sink) {
		if (includeMe)
			sink.add(this);
		for (AmiWebLayoutFile f : this.getChildren())
			f.getChildrenRecursive(true, sink);
		return sink;
	}
	protected void updateFullAliases() {
		if (parent == null)
			this.fullAlias = DEFAULT_ROOT_ALIAS;
		else
			this.fullAlias = parent.getParent() == null ? alias : (parent.getFullAlias() + '.' + alias);
		for (AmiWebLayoutFile child : this.getChildren())
			child.updateFullAliases();
	}

	public void getFullAliasMap(IndexedList<String, AmiWebLayoutFile> sink) {
		sink.add(this.fullAlias, this);
		for (AmiWebLayoutFile child : this.getChildren())
			child.getFullAliasMap(sink);

	}

	public void setLocationAndSource(String location, boolean isRelative, String source) {
		location = SH.replaceAll(location, '\\', '/');
		if (OH.eq(this.location, location) && OH.eq(this.source, source))
			return;
		this.location = location;
		this.source = source;
		this.isLocationRelative = isRelative;
		this.origIsLocationRelative = this.isLocationRelative;
		this.origLocation = this.location;
		this.origSource = this.source;
		for (AmiWebLayoutFile s : this.childrenByAlias.values())
			if (s.isLocationRelative && OH.ne(s.source, source)) {
				s.origSource = s.source;
				s.source = source;
			}

	}
	// TODO: This is currently a partial check needs to be a stronger check.
	public boolean hasChangedSinceLastSave() {
		return OH.ne(this.source, this.origSource) || OH.ne(this.isLocationRelative, this.origIsLocationRelative) || OH.ne(this.location, this.origLocation);
	}
	public void rebuildJsonFromCurrentLayout(AmiWebService service) {
		setJson(service.getLayoutFilesManager().toJson(buildCurrentJson(service)), false);
		for (AmiWebLayoutFile i : getChildren())
			i.rebuildJsonFromCurrentLayout(service);
	}

	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	public Map<String, AmiWebPortletDef> getRootHiddenPanels() {
		Set<String> referenced = new HashSet<String>();
		for (AmiWebPortletDef i : this.hiddenPanels.values())
			referenced.addAll(i.getChildren());
		Map<String, AmiWebPortletDef> r = new HashMap<String, AmiWebPortletDef>();
		for (AmiWebPortletDef i : this.hiddenPanels.values())
			if (!referenced.contains(i.getAmiPanelId()))
				r.put(i.getAmiPanelId(), i);
		return r;
	}

	//Walk down the portlet container tree deleting until the file changes, and the just hide. When deleting, we need to check parent layout for references too and delete there as well.
	public void onDeletePanel(AmiWebAliasPortlet portlet) {
		for (AmiWebLayoutFile t = this; t != null; t = t.getParent())
			t.onDeletePanel2(portlet);

		if (portlet instanceof AmiWebAbstractContainerPortlet)
			for (AmiWebAliasPortlet i : ((AmiWebAbstractContainerPortlet) portlet).getAmiChildren())
				if (OH.eq(i.getAmiLayoutFullAlias(), portlet.getAmiLayoutFullAlias()))
					onDeletePanel(i);
				else
					getChildByRelativeAlias(AmiWebUtils.getRelativeAlias(this.fullAlias, i.getAmiLayoutFullAlias())).onHidePanel(i);
	}
	private void onDeletePanel2(AmiWebAliasPortlet portlet) {
		String relativeAdn = AmiWebUtils.getRelativeAlias(this.fullAlias, portlet.getAmiLayoutFullAliasDotId());
		AmiWebPortletDef d = findHiddenParent(relativeAdn);
		if (d != null) {
			d.removeChild(relativeAdn);
		}
	}
	public void onHidePanel(AmiWebAliasPortlet portlet) {
		OH.assertEq(portlet.getAmiLayoutFullAlias(), this.fullAlias);
		AmiWebPortletBuilder<?> portletBuilder = (AmiWebPortletBuilder<?>) portlet.getManager().getPortletBuilder(portlet.getPortletConfig().getBuilderId());
		AmiWebPortletDef r = new AmiWebPortletDef(this, portletBuilder, portlet.getConfiguration());
		CH.putOrThrow(this.hiddenPanels, r.getAmiPanelId(), r);
		this.manager.fireOnHiddenPanelAdded(this, r);
		if (portlet instanceof AmiWebAbstractContainerPortlet)
			for (AmiWebAliasPortlet i : ((AmiWebAbstractContainerPortlet) portlet).getAmiChildren())
				getChildByRelativeAlias(AmiWebUtils.getRelativeAlias(this.fullAlias, i.getAmiLayoutFullAlias())).onHidePanel(i);
	}
	public void onShowPanel(String panelId) {
		AmiWebPortletDef pd = CH.removeOrThrow(this.hiddenPanels, panelId);
		if (pd != null)
			this.manager.fireOnHiddenPanelRemoved(this, pd);
		for (String i : pd.getChildren())
			getChildByRelativeAlias(AmiWebUtils.getAliasFromAdn(i)).onShowPanel(AmiWebUtils.getNameFromAdn(i));
	}
	public AmiWebPortletDef getHiddenPanelByPanelId(String panelId) {
		return this.hiddenPanels.get(panelId);
	}
	public Set<String> getHiddenPanelIds() {
		return this.hiddenPanels.keySet();
	}
	public void onAmiPanelIdChanged(String oldPanelId, String adn) {
		AmiWebPortletDef existing = this.hiddenPanels.remove(oldPanelId);
		if (AmiWebUtils.isParentAlias(this.fullAlias, adn)) {
			String amiPanelId = AmiWebUtils.getRelativeAlias(this.fullAlias, adn);
			if (existing != null) {
				if (amiPanelId.indexOf('.') != -1)
					throw new IllegalStateException("not expecting: " + amiPanelId);
				existing.setAmiPanelId(amiPanelId);
				CH.putOrThrow(this.hiddenPanels, amiPanelId, existing);
				this.manager.fireOnHiddenPanelIdChanged(this, existing, oldPanelId, adn);
			}
			AmiWebPortletDef i = findHiddenParent(oldPanelId);
			if (i != null)
				i.replaceChild(oldPanelId, amiPanelId);
		}
		if (parent != null) {
			parent.onAmiPanelIdChanged(AmiWebUtils.getFullAlias(this.alias, oldPanelId), adn);
		}
	}

	//if the panel being replaced has a 
	public void onPanelReplaced(AmiWebAliasPortlet portlet) {
		OH.assertEq(portlet.getAmiLayoutFullAlias(), this.fullAlias);
		if (portlet instanceof AmiWebBlankPortlet) {
			AmiWebPortletDef i = findHiddenParent(portlet.getAmiPanelId());
			if (i != null)
				onHidePanel(portlet);
		}
	}
	public AmiWebPortletDef findHiddenParent(String panelId) {
		for (AmiWebPortletDef i : this.hiddenPanels.values())
			if (i.getChildren().contains(panelId))
				return i;
		return null;
	}
	public void addHiddenPanel(AmiWebPortletDef def) {
		CH.putOrThrow(this.hiddenPanels, def.getAmiPanelId(), def);
		this.manager.fireOnHiddenPanelAdded(this, def);
	}
	public String getAbsoluteLocation() {
		// TODO: merge getFullAbsoluteLocation, currently cloud layouts don't return full abs path
		return getAbsoluteLocation(this.parent, this.isLocationRelative, this.location);
	}

	/*
	 * This supports mixed loading, loading cloud layouts absolutely, or vice versa
	 */
	//	public String getLocationForLoadingLayouts() {
	//		//Depends on source, location, fullAbsLocation
	//		if (this.isLocationRelative) {
	//			if (this.parent != null) {
	//				String sourceType = this.getSource();
	//				if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(sourceType)) {
	//				} else if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(sourceType)) {
	//					//backwards compatibility fix: /child.ami or ./child.ami
	//					String loc = IOH.toUnixFormatForce(this.getLocation());
	//					if (SH.startsWith(loc, "./"))
	//						loc = SH.substring(loc, 2, loc.length());
	//					if (SH.startsWith(loc, "/"))
	//						loc = SH.substring(loc, 1, loc.length());
	//
	//					String pFullLoc = this.parent.getFullAbsoluteLocation();
	//					String dir = IOH.getFileDirectoryLinux(pFullLoc);
	//					System.out.println("--\t" + dir);
	//					String fullPath = IOH.joinLinux(dir, loc);
	//
	//					String cloudDir = this.service.getCloudManager().getLayoutsRootDirectory().getFullPath();
	//					String absLocationToCloud = IOH.getRelativePathLinux(cloudDir, fullPath);
	//					return absLocationToCloud;
	//				} else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(sourceType)) {
	//					//backwards compatibility fix: /child.ami or ./child.ami
	//					String loc = IOH.toUnixFormatForce(this.getLocation());
	//					if (SH.startsWith(loc, "./"))
	//						loc = SH.substring(loc, 2, loc.length());
	//					if (SH.startsWith(loc, "/"))
	//						loc = SH.substring(loc, 1, loc.length());
	//
	//					String pFullLoc = this.parent.getFullAbsoluteLocation();
	//					String dir = IOH.getFileDirectoryLinux(pFullLoc);
	//					String fullPath = IOH.joinLinux(dir, loc);
	//					return fullPath;
	//				} else if (AmiWebConsts.LAYOUT_SOURCE_SHARED.equals(sourceType)) {
	//					// ???
	//				}
	//				return this.getAbsoluteLocation();
	//			}
	//			return this.getAbsoluteLocation();
	//		} else {
	//			return this.getAbsoluteLocation();
	//		}
	//	}
	public String getFullAbsoluteLocation() {
		// Need to do this for cloud layouts
		if (this.isLocationRelative == false && SH.equals(AmiWebConsts.LAYOUT_SOURCE_CLOUD, this.source)) {
			AmiWebFile f = this.service.getCloudManager().getFile(this.location);
			return IOH.toUnixFormatForce(f.getAbsolutePath());
		}
		return getAbsoluteLocation(this.parent, this.isLocationRelative, this.location);
	}
	public boolean getUserReadonly() {
		return isUserReadonly;
	}
	public void setUserReadonly(boolean isUserReadonly) {
		this.isUserReadonly = isUserReadonly;
	}

	public boolean isReadonly() {
		return this.isUserReadonly || this.isFileReadonly || this.duplicateStatus == SECONDARY;
	}
	public byte getDuplicateStatus() {
		return duplicateStatus;
	}
	public void setDuplicateStatus(byte duplicateStatus) {
		this.duplicateStatus = duplicateStatus;
	}

	public static String getAbsoluteLocation(AmiWebLayoutFile parent, boolean isRelative, String location) {
		if (!isRelative)
			return location;
		String parentDirectory = SH.beforeLast(parent.getAbsoluteLocation(), '/', "");
		String fullPath = parentDirectory + "/" + location;
		return IOH.getCanonical(fullPath);
	}

	private String ari;

	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.ari = AmiWebDomObject.ARI_TYPE_LAYOUT + ":" + this.getFullAlias();
		this.service.getDomObjectsManager().fireAriChanged(this, oldAri);
		List<AmiWebDomObject> childDomObjects = this.getChildDomObjects();
		for (int i = 0; i < childDomObjects.size(); i++) {
			this.getChildDomObjects().get(i).updateAri();
		}
	}
	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_LAYOUT;
	}

	@Override
	public String getDomLabel() {
		if (SH.equals("", this.alias))
			return SH.beforeFirst(this.service.getLayoutFilesManager().getLayoutName(), ".ami");
		return this.alias;
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = new ArrayList<AmiWebDomObject>();
		// Add Datamodel Dom Objects
		String layoutAlias = this.getFullAlias();
		AmiWebService service = this.manager.getService();
		Collection<AmiWebDmsImpl> datamodels = this.manager.getService().getDmManager().getDatamodels();
		for (AmiWebDmsImpl datamodel : datamodels) {
			if (SH.equals(layoutAlias, datamodel.getAmiLayoutFullAlias())) {
				r.add(datamodel);
			}
		}

		// Add Panel Dom Objects
		Set<String> panelIdsByFullAlias = service.getPanelIdsByFullAlias(layoutAlias);
		for (String padn : panelIdsByFullAlias) {
			String fpadn = AmiWebUtils.getFullAlias(layoutAlias, padn);
			AmiWebAbstractPortlet pnl = (AmiWebAbstractPortlet) service.getPortletByAliasDotPanelId(fpadn);
			r.add(pnl);
		}

		Set<String> dmLinksByFullAlias = service.getDmManager().getDmLinkIdsByFullAlias(layoutAlias);
		for (String dmadn : dmLinksByFullAlias) {
			String fdmadn = AmiWebUtils.getFullAlias(layoutAlias, dmadn);
			AmiWebDmLink link = service.getDmManager().getDmLinkByAliasDotRelationshipId(fdmadn);
			r.add(link);
		}
		for (AmiWebRealtimeProcessor i : service.getWebManagers().getRealtimeProcessors()) {
			if (OH.eq(layoutAlias, i.getAlias()))
				r.add(i);
		}
		if (service.getDesktop().getInnerDesktop() != null) {
			AmiWebCustomContextMenuManager ccm = service.getDesktop().getInnerDesktop().getCustomContextMenu();
			r.addAll(ccm.getChildren(false));
		}
		return r;

	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		return (AmiWebDomObject) this.manager.getService();
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
		return this.service.getScriptManager(this.getFullAlias()).getLayoutCallbacks();
	}
	public void setFileReadonly(boolean b) {
		this.isFileReadonly = b;
	}
	public boolean getFileReadonly() {
		return this.isFileReadonly;
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;

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
		service.getDomObjectsManager().fireAdded(this);
		if (this.isManagedByDomManager == true) {
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public String getAmiLayoutFullAlias() {
		return this.fullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.fullAlias;
	}
	@Override
	public AmiWebFormulasImpl getFormulas() {
		return this.formulas;
	}
	@Override
	public AmiWebService getService() {
		return this.service;
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}
	public AmiWebFormula getTitleBarHtmlFormula() {
		return titleBarHtmlFormula;
	}
	public AmiWebCss getCss() {
		return this.css;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}
	public void reloadCurrentLayout() {
		setJson(service.getLayoutFilesManager().loadLayoutData(this.getAbsoluteLocation(), this.getSource()).getB(), true);
		for (AmiWebLayoutFile i : getChildren())
			i.reloadCurrentLayout();

	}
	public String getRawTextFromDisk() {
		return rawTextFromDisk;
	}
	public void setRawTextFromDisk(String rawTextFromDisk) {
		this.rawTextFromDisk = rawTextFromDisk;
	}

}
