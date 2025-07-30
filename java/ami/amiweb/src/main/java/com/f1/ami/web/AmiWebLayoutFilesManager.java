package com.f1.ami.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.ami.web.AmiWebAutosaveManager.AutoSaveFile;
import com.f1.ami.web.amiscript.AmiWebFileSystem;
import com.f1.ami.web.diff.AmiWebJsonDifferPortlet;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_File;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.Tuple3;

public class AmiWebLayoutFilesManager implements ConfirmDialogListener {

	private static final String DIALOG_CHECK_AUTOSAVE = "DIALOG_CHECK_AUTOSAVE";
	private static final Logger log = LH.get();
	final private AmiWebService service;
	private IndexedList<String, AmiWebLayoutFile> layoutFiles = new BasicIndexedList<String, AmiWebLayoutFile>();
	private AmiWebLayoutFile layout;
	private boolean isLayoutClosing = false;
	private String layoutName = null;
	private String layoutSource;
	private List<AmiWebHiddenPanelsListener> listeners = new ArrayList<AmiWebHiddenPanelsListener>();
	private String sharedLayoutDir;
	final private AmiWebFileSystem fs;
	final private boolean forceUrlLayout;

	public AmiWebLayoutFilesManager(AmiWebService s) {
		this.service = s;
		this.forceUrlLayout = this.service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_WEB_URL_ALWAYS_INCLUDE_LAYOUT, Boolean.FALSE);
		this.fs = service.getAmiFileSystem();
		File sharedLayoutsDir = service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_SHARED_LAYOUTS_DIR, Caster_File.INSTANCE);
		if (sharedLayoutsDir != null) {
			String t = SH.replaceAll(IOH.getFullPath(sharedLayoutsDir), '\\', '/');
			if (!t.endsWith("/"))
				t = t + '/';
			this.sharedLayoutDir = t;
		}
	}
	public String getLayoutName() {
		return layoutName;
	}
	public String getLayoutSource() {
		return this.layoutSource;
	}
	public AmiWebLayoutFile getLayout() {
		return this.layout;
	}
	public void rebuildLayout() {
		this.layout.rebuildJsonFromCurrentLayout(this.service);
		setLayout(this.layout);
	}
	//	public void setJson(String text) {
	//		this.layout.setJson(text);
	//		setLayout(this.layout);
	//	}

	public boolean getIsLayoutClosing() {
		return this.isLayoutClosing;
	}

	//returns: isWritable, data
	public Tuple2<Boolean, String> loadLayoutData(String name, String sourceType) {
		if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(sourceType)) {
			AmiWebUserFilesManager cs = service.getUserFilesManager();
			String key = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + name;
			return new Tuple2<Boolean, String>(cs.getFile(key).canWrite(), cs.loadFile(key));
		} else if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(sourceType)) {
			return new Tuple2<Boolean, String>(this.service.getCloudManager().isLayoutWriteable(name), this.service.getCloudManager().loadLayout(name));
		} else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(sourceType)) {
			try {
				AmiWebFile file = fs.getFile(name);
				return new Tuple2<Boolean, String>(file.canWrite(), file.readText());
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		} else if (AmiWebConsts.LAYOUT_SOURCE_SHARED.equals(sourceType)) {
			AmiWebFile file = fs.getFile(toSharedLayoutName(name));
			try {
				return new Tuple2<Boolean, String>(file.canWrite(), file.readText());
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		} else if (AmiWebConsts.LAYOUT_SOURCE_TMP.equals(sourceType)) {
			if (name == null)
				return null;
			AmiWebTempFilesManager tmpFileManager = AmiWebTempFilesManager.getTempFilesManager(this.service.getPortletManager().getState().getWebStatesManager());
			String text = tmpFileManager.getLayout(name);
			return new Tuple2<Boolean, String>(false, text);
		} else if (sourceType == null) {
			return null;
		}
		throw new RuntimeException("Uknown source type: " + sourceType);
	}
	public void setWriteable(AmiWebLayoutFile target, boolean b) {
		String sourceType = target.getSource();
		String name = target.getAbsoluteLocation();
		if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(sourceType)) {
			AmiWebUserFilesManager cs = service.getUserFilesManager();
			String key = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + target.getAbsoluteLocation();
			cs.setFileWriteable(key, b);
		} else if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(sourceType)) {
			this.service.getCloudManager().setLayoutWriteable(name, b);
		} else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(sourceType)) {
			AmiWebFile file = fs.getFile(name);
			file.setWritable(b);
		} else if (AmiWebConsts.LAYOUT_SOURCE_SHARED.equals(sourceType)) {
			AmiWebFile file = fs.getFile(toSharedLayoutName(name));
			file.setWritable(b);
		} else if (AmiWebConsts.LAYOUT_SOURCE_TMP.equals(sourceType)) {
			throw new RuntimeException("Can not set TMP layout to writeable");
		} else if (sourceType != null)
			throw new RuntimeException("Uknown source type: " + sourceType);
	}
	private String toSharedLayoutName(String name) {
		return this.sharedLayoutDir + name;
	}
	public AmiWebLayoutFile loadLayout(AmiWebLayoutFile parent, String alias, String location, boolean isLocationRelative, boolean isReadonly, String type, Set<String> stack,
			Map<String, String> layoutConfigs) throws Exception {
		if (stack.size() >= 5)
			throw new RuntimeException("Layout reference stack overflow: 5 levels max");

		// If the layout is relative, find the layout relative, but if the parent is located in the cloud, prevent unnecessary changing types
		if (parent != null && isLocationRelative == true) {
			if (!SH.equals(type, parent.getSource()))
				if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(type)) {
					// Change type if the layout isn't in the cloud dir
					String cloudDir = IOH.toUnixFormatForce(this.service.getCloudManager().getLayoutsRootDirectory().getFullPath());
					if (!SH.endsWith(cloudDir, '/'))
						cloudDir += '/';
					String parentLoc = IOH.toUnixFormatForce(parent.getFullAbsoluteLocation());
					if (!SH.startsWith(parentLoc, cloudDir))
						type = parent.getSource();
					else {
						// Get the correct location for the relative cloud file
						String parentRel = IOH.getRelativePathLinux(cloudDir, parentLoc);
						String dir = IOH.getFileDirectoryLinux(parentRel);

						String loc = IOH.toUnixFormatForce(location);
						if (SH.startsWith(loc, "./"))
							loc = SH.substring(loc, 2, loc.length());
						if (SH.startsWith(loc, "/"))
							loc = SH.substring(loc, 1, loc.length());
						location = dir + loc;

					}
				} else
					type = parent.getSource();
		}
		String fullPath = type + ":" + location;
		String absolutePath = type + ":" + AmiWebLayoutFile.getAbsoluteLocation(parent, isLocationRelative, location);
		if (!stack.add(absolutePath))
			throw new RuntimeException("Circular reference: " + SH.join(" ==> ", stack) + " ==> " + fullPath);
		boolean canWrite;
		final AmiWebLayoutFile r;
		try {
			String data;
			r = new AmiWebLayoutFile(this, alias, location, isLocationRelative, isReadonly, type, parent);
			if (layoutConfigs == null) {
				Tuple2<Boolean, String> t = loadLayoutData(r.getAbsoluteLocation(), type);
				if (t == null) {
					canWrite = true;
					data = null;
				} else {
					canWrite = t.getA();
					data = t.getB();
				}
				r.setJson(data, true);
			} else {
				canWrite = true;
				data = layoutConfigs.get(type + ":" + location);
				if (data == null)
					return null;
				r.setJson(data, false);
				if (!AmiWebConsts.LAYOUT_SOURCE_TMP.equals(type)) {
					Tuple2<Boolean, String> origFromDisk = loadLayoutData(r.getAbsoluteLocation(), type);
					r.setRawTextFromDisk(origFromDisk == null ? null : origFromDisk.getB());
				} else
					r.setRawTextFromDisk(null);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error processing file '" + fullPath + "': " + e.getMessage(), e);
		}
		r.setFileReadonly(!canWrite);
		Map lastLoadedJson = r.getLastLoadedJson();
		if (lastLoadedJson != null) {
			List<Map> includes = (List<Map>) lastLoadedJson.get("includeFiles");
			if (includes != null) {
				for (Map include : includes) {
					String alias2 = (String) include.get("alias");
					String type2 = (String) include.get("type");
					String location2 = (String) include.get("location");
					boolean relative2 = (boolean) CH.getOr(Caster_Boolean.INSTANCE, include, "relative", Boolean.FALSE);
					boolean readonly = (boolean) CH.getOr(Caster_Boolean.INSTANCE, include, "readonly", Boolean.FALSE);
					if (SH.isnt(alias2))
						throw new RuntimeException("Error processing file '" + fullPath + "': includeFiles entry missing 'alias'");
					if (SH.isnt(type2))
						throw new RuntimeException("Error processing file '" + fullPath + "': includeFiles entry missing 'type'");
					if (SH.isnt(location2))
						throw new RuntimeException("Error processing file '" + fullPath + "': includeFiles entry missing 'location'");
					AmiWebLayoutFile createChild = loadLayout(r, alias2, location2, relative2, readonly, type2, stack, layoutConfigs);
					if (createChild == null) {
						getManager().showAlert(fullPath + " references missing layout: <BR>" + type2 + ":" + location2);
						continue;
					}
					try {
						r.addChild(createChild);
					} catch (Exception e) {
						throw new RuntimeException("Error processing file '" + fullPath + "': " + e.getMessage(), e);
					}
				}
			}
		}
		stack.remove(absolutePath);
		return r;
	}
	public void saveLayoutAs(String name, String source) {
		this.getLayout().setLocationAndSource(name, false, source);
		onLayoutChanged();
		this.saveLayout();
		this.setCurrentLayoutName(name, source);
	}

	private void onLayoutChanged() {
		this.getService().getWebState().setLayout(this.layout.getLocation());
	}
	public void setLayout(AmiWebLayoutFile file) {
		this.setCurrentLayoutName(file.getLocation(), file.getSource());
		if (service.getWebManagers().getConnectionState() == AmiWebSnapshotManager.STATE_CONNECTED) {
			service.processCommands(AmiRelayCommandDefMessage.CALLBACK_USER_LOGOUT);
		}
		final AmiWebDesktopPortlet amiDesktop = service.getDesktop();
		amiDesktop.resetDesktop();
		final AmiWebInnerDesktopPortlet existingDesktop = amiDesktop.getDesktop();
		if (existingDesktop != null) {
			existingDesktop.redockAllPortlets();
			try {
				this.isLayoutClosing = true;
				for (String pid : CH.l(existingDesktop.getChildren().keySet())) {
					Portlet removeChild = existingDesktop.removeChild(pid);
					if (removeChild != null)
						removeChild.close();
				}
			} finally {
				this.isLayoutClosing = false;
			}
			if (existingDesktop != null) {
				existingDesktop.removeDesktopListener(service.getDesktop());
				existingDesktop.close();
				amiDesktop.setDesktop(null);
			}
		}
		try {
			if (layout != null && layout != file)
				this.layout.getFormulas().removeFormulasListener(amiDesktop.getDesktopBar());
			service.clearManagers();
			service.clear();
			service.initManagers();
			this.service.getPortletManager().getMetadataConfig().clear();
			amiDesktop.reset();
			amiDesktop.assignGlobalStyles(Collections.EMPTY_MAP);
			OH.assertNull(file.getParent());
			this.layout = file;
			if ((forceUrlLayout || this.getService().getPortletManager().getUrlParams().containsKey(AmiWebConsts.URL_PARAM_LAYOUT))) {
				if (this.layout.getSource() == null)
					this.getService().getPortletManager().setUrlParams(new LinkedHashMap<String, String>());
				else
					this.getService().getPortletManager().putUrlParam(AmiWebConsts.URL_PARAM_LAYOUT, this.layout.getSource() + '_' + this.layout.getLocation());
			}
			onLayoutChanged();
			this.layoutFiles.clear();
			file.getFullAliasMap(this.layoutFiles);
			AmiWebLayoutHelper.manageBackwardsCompatibility(service.getPortletManager(), file.getLastLoadedJson());
			file.applyMetadataToLayout(this.service);
			file.applyPortletsToLayout(this.service);
			this.service.getDomObjectsManager().initVariables();
			service.getScriptManager().bindVirtuals();
			if (file.getLastLoadedJson() != null) {
				AmiWebPortletDef desktopDef = file.getHiddenPanelByPanelId(AmiWebInnerDesktopPortlet.DESKTOP);
				if (desktopDef != null) {
					StringBuilder warningsSink = new StringBuilder();
					amiDesktop.setDesktop((AmiWebInnerDesktopPortlet) AmiWebLayoutHelper.build(desktopDef, service, warningsSink, false));
					if (warningsSink.length() > 0)
						getManager().showAlert(warningsSink.toString());
				}
			} else {
				amiDesktop.setDesktop((AmiWebInnerDesktopPortlet) service.getPortletManager().buildPortlet(AmiWebInnerDesktopPortlet.Builder.ID));
			}
		} catch (Exception e) {
			service.getPortletManager().showAlert("Critical Error building layout", e);
		}
		if (amiDesktop.getDesktop() == null) {
			amiDesktop.setDesktop((AmiWebInnerDesktopPortlet) service.getPortletManager().buildPortlet(AmiWebInnerDesktopPortlet.Builder.ID));
		}
		this.service.registerAmiUserPrefId("Desktop", amiDesktop.getDesktop());

		this.onInitDone();
		this.service.getAutosaveManager().onLayoutLoaded();
	}
	public void onInitDone() {
		this.layout.onInitDone();
		this.service.getAmiPanelManager().onInitDone();
		this.service.getDmManager().onInitDone();
		this.service.getWebManagers().onInitDone();
		this.service.getDesktop().onInitDone();
		this.service.recompileAmiScript();
	}
	public Map<String, AmiWebFile> getLocalLayoutNames() {
		AmiWebUserFilesManager store = service.getUserFilesManager();
		TreeMap<String, AmiWebFile> r = new TreeMap<String, AmiWebFile>();
		for (Entry<String, AmiWebFile> f : store.getAmiFiles().entrySet()) {
			String s = f.getKey();
			if (s.startsWith(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX))
				r.put(s.substring(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX.length()), f.getValue());
		}
		return r;
	}

	public byte getUserExportMode() {
		String s = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_EXPORT);
		if (AmiWebDeveloperSettingsPortlet.EXPORT_COMPACT.equals(s))
			return ObjectToJsonConverter.MODE_COMPACT;
		else if (AmiWebDeveloperSettingsPortlet.EXPORT_LEGIBLE.equals(s))
			return ObjectToJsonConverter.MODE_SEMI;
		else
			return ObjectToJsonConverter.MODE_CLEAN;
	}
	public Set<String> getFullAliasesByPriority() {
		return this.layoutFiles.keySet();
	}
	public AmiWebLayoutFile getLayoutByFullAlias(String fullAlias) {
		OH.assertNotNull(fullAlias);
		return this.layoutFiles.getNoThrow(fullAlias);
	}

	public boolean hasChangedSinceLastSave() {
		try {
			return this.hasChangedSinceLastSave(this.layout);
		} catch (Exception e) {
			LH.warning(log, "For ", this.service.getPortletManager().describeUser() + " error on layout", e);
			return false;
		}
	}

	public Map<String, Object> getLayoutConfiguration(boolean includeFullPath) {
		List<Map<String, Object>> layouts = new ArrayList<Map<String, Object>>();
		exportAllLayouts(this.getLayout(), layouts, includeFullPath);
		String location = layout.getLocation();
		String source = layout.getSource();
		if (!includeFullPath && !layout.getIsLocationRelative() && AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(source)) {
			location = SH.afterLast(location, '/');
		}
		return CH.m("layouts", layouts, "rootLayout", CH.m("type", source, "location", location));
	}

	public Set<String> getAvailableAliasesUp(Set<String> fullAliases) {
		Set<String> r = new HashSet<String>();
		getAvailableAliasesUp(this.layout, fullAliases, r);
		return r;
	}

	public Set<String> getAvailableAliasesDown(String fullAlias) {
		AmiWebLayoutFile l = getLayoutByFullAlias(fullAlias);
		Set<String> r = new LinkedHashSet<String>();
		getAvailableAliasesDown(l, r);
		return r;
	}
	// Used for unhiding panels from child layouts called via developer menu option
	public AmiWebAliasPortlet buildPortlet(String adn) {
		String childAlias = AmiWebUtils.getAliasFromAdn(adn);
		String childPanelId = AmiWebUtils.getNameFromAdn(adn);
		final AmiWebLayoutFile t2 = getLayoutByFullAlias(childAlias);
		final AmiWebPortletDef portletDef = t2.getHiddenPanelByPanelId(childPanelId);
		final StringBuilder warningsSink = new StringBuilder();
		final AmiWebAliasPortlet r = AmiWebLayoutHelper.build(portletDef, service, warningsSink, false);
		for (AmiWebDmLink i : service.getDmManager().getDmLinks())
			i.bind();
		return r;
	}

	public ConfirmDialog loadLayoutDialog(String name, String configuration, String source) {
		try {
			AmiWebVarsManager varsManager = this.service.getVarsManager();
			if ((varsManager.isUserAdmin() || varsManager.isUserDev())
					&& (this.layout != null && this.hasChangedSinceLastSave() && layout.getSource() != AmiWebConsts.LAYOUT_SOURCE_TMP)) {
				ConfirmDialogPortlet dialog = new ConfirmDialogPortlet(getManager().generateConfig(),
						"Unsaved changes to '<B>" + this.getLayoutName() + "</B>' will be lost. Continue?", ConfirmDialog.TYPE_OK_CANCEL);
				if (layoutSource != null)
					dialog.addButton("view_unsaved_changes", "View Unsaved Changes");
				dialog.setCorrelationData(new Tuple3<String, String, String>(name, configuration, source));
				dialog.setCallback("RESET_LAYOUT");
				dialog.addDialogListener(this);
				getManager().showDialog("Unsaved Changes", dialog);
				return dialog;
			} else {
				this.loadLayout(name, configuration, source);
				return null;
			}
		} catch (Exception e) {
			getManager().showAlert("There was an error loading your layout: " + name, e);
			LH.warning(log, "Error loading layout: ", name, e);
			return null;
		}
	}
	public String toJson(Object config) {
		return AmiWebLayoutHelper.toJson(config, service);
	}
	public void setLayoutTitleError(String string) {
		this.layoutName = ("<span class='red'>" + string);
		this.service.getDesktop().updateDashboard();
	}
	protected void setCurrentLayoutName(String name, String source) {
		AmiWebVarsManager vars = service.getVarsManager();
		if (OH.ne(this.layoutName, name) || OH.ne(this.layoutSource, source)) {
			this.layoutName = name;
			this.layoutSource = source;
			service.getDesktop().updateDashboard();
			if (this.layoutName != null && OH.ne(AmiWebConsts.LAYOUT_SOURCE_TMP, this.layoutSource)) {
				vars.putSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT, this.layoutName);
				vars.putSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT_SOURCE, this.layoutSource);
			}
		}
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return onButton2(source, id);
	}
	private PortletManager getManager() {
		return this.service.getPortletManager();
	}
	public static void handleDiamond(AmiWebLayoutFile r) {
		BasicMultiMap.List<Tuple2<String, String>, AmiWebLayoutFile> existing = new BasicMultiMap.List<Tuple2<String, String>, AmiWebLayoutFile>();
		for (AmiWebLayoutFile i : r.getChildrenRecursive(true))
			existing.putMulti(new Tuple2<String, String>(i.getSource(), i.getAbsoluteLocation()), i);
		for (List<AmiWebLayoutFile> i : existing.values()) {
			if (i.size() == 1) {
				i.get(0).setDuplicateStatus(AmiWebLayoutFile.ONLY);
			} else {
				i.get(0).setDuplicateStatus(AmiWebLayoutFile.PRIMARY);
				for (int n = 1; n < i.size(); n++)
					i.get(n).setDuplicateStatus(AmiWebLayoutFile.SECONDARY);
			}
		}

	}
	private AmiWebLayoutFile loadLayoutByConfig(String location, String configuration, Map<String, Object> config, String fsource, boolean fromDisk) throws Exception {
		AmiWebLayoutFile r;
		if (config.containsKey("layouts")) {
			List<Map<String, Object>> layouts = (List<Map<String, Object>>) config.get("layouts");
			Map<String, Object> root = (Map<String, Object>) config.get("rootLayout");
			String rootType = (String) root.get("type");
			String rootLocation = (String) root.get("location");
			Map<String, String> existingLayouts = new HashMap<String, String>(layouts.size());
			for (Map<String, Object> layout : layouts) {
				String data = ObjectToJsonConverter.INSTANCE_SEMI.objectToString(layout.get("data"));
				String source = (String) layout.get("type");
				String loc = (String) layout.get("location");
				existingLayouts.put(source + ':' + loc, data);

			}
			r = loadLayout(null, "", rootLocation, false, false, rootType, new HashSet<String>(), existingLayouts);
		} else if (config.containsKey("includeFiles")) {
			if (fsource == null) {//this was imported
				Map<String, String> existingLayouts = new HashMap<String, String>(1);
				String data = ObjectToJsonConverter.INSTANCE_SEMI.objectToString(config);
				existingLayouts.put(AmiWebConsts.LAYOUT_SOURCE_LOCAL + ":" + location, data);
				r = loadLayout(null, "", location, false, false, AmiWebConsts.LAYOUT_SOURCE_LOCAL, new HashSet<String>(), existingLayouts);
			} else {
				r = loadLayout(null, "", location, false, false, fsource, new HashSet<String>(), null);
			}
		} else {
			r = new AmiWebLayoutFile(this, location, fsource);
			r.setJson(configuration, fromDisk);
		}
		handleDiamond(r);
		return r;
	}
	public boolean loadLayout(String name, String configuration, String source) {
		if (source != null && OH.ne(source, AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE) && OH.ne(source, AmiWebConsts.LAYOUT_SOURCE_LOCAL)
				&& OH.ne(source, AmiWebConsts.LAYOUT_SOURCE_CLOUD) && OH.ne(source, AmiWebConsts.LAYOUT_SOURCE_SHARED) && OH.ne(source, AmiWebConsts.LAYOUT_SOURCE_TMP)) {
			this.service.getPortletManager().showAlert("Bad source '" + source + "'. Expecting LOCAL, CLOUD, ABSOLUTE or SHARED");
			return false;
		}
		if (!this.service.getVarsManager().isPermittedLayout(source, name)) {
			this.service.getPortletManager().showAlert("Access denied to " + source + ":" + name + "<P>(Must be included in LAYOUTS)");
			return false;
		}
		try {
			Map<String, Object> config;
			boolean fromDisk;
			if (configuration == null) {
				fromDisk = true;
				Tuple2<Boolean, String> t = loadLayoutData(name, source);
				if (t == null) {
					setLayout(loadLayout(null, "", name, false, false, source, new HashSet<String>(), null));
					this.service.getDesktop().showNewLayoutStylePortlet();
					return true;
				}
				if (t.getValue() == null) {
					this.service.getPortletManager().showAlert("File not found: " + source + ":" + name);
					return false;
				}
				configuration = t.getB();
				if (configuration == null)
					return false;
				config = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(configuration);
				if (config.containsKey("layouts") && !service.getDesktop().getIsLocked()) {
					getManager().showAlert("This file is an ami export and may contain multiple layouts. When saving, you should choose a new location.");
					layoutName = AmiWebLayoutManager.DEFAULT_LAYOUT_NAME;
				}
			} else {
				fromDisk = false;
				config = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(configuration);
			}
			AmiWebLayoutFile r = this.loadLayoutByConfig(name, configuration, config, source, fromDisk);
			this.setLayout(r);
			handleDiamond(r);
			return true;
		} catch (Exception e) {
			LH.warning(log, "Critical error loading " + source + ": " + name, e);
			getManager().showAlert("Critical error loading " + source + ":" + name + "<BR>" + e.getMessage(), e);
			return false;
		}
	}

	private void getAvailableAliasesDown(AmiWebLayoutFile l, Set<String> r) {
		r.add(l.getFullAlias());
		for (AmiWebLayoutFile i : l.getChildren())
			getAvailableAliasesDown(i, r);
	}
	private void getAvailableAliasesUp(AmiWebLayoutFile layout, Set<String> fullAliases, Set<String> sink) {
		String parentAlias = layout.getFullAlias();
		for (String s : fullAliases)
			if (OH.ne(s, parentAlias) && !AmiWebUtils.isParentAlias(parentAlias, s))
				return;
		sink.add(parentAlias);
		for (AmiWebLayoutFile i : layout.getChildren())
			getAvailableAliasesUp(i, fullAliases, sink);
	}
	public void saveLayout() {
		if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(layout.getSource()) && !layout.getIsLocationRelative() && SH.indexOf(layout.getAbsoluteLocation(), '/', 0) == -1
				&& SH.indexOf(layout.getAbsoluteLocation(), '\\', 0) == -1) {
			getManager().showAlert("No Path specified, you must <i>Save as</i> first");
		} else if (hasExternalPartyChangedLayouts()) {
			AmiWebJsonDifferPortlet dp = new AmiWebJsonDifferPortlet(this.service, this.service.getPortletManager().generateConfig());
			dp.addComparison(this.layout);
			dp.buildTree();
			RootPortletDialog dialog = getManager().showDialog("Merge Changes", dp);
			dialog.setStyle(service.getUserDialogStyleManager());
			dialog.setCloseOnClickOutside(false);
			dialog.setEscapeKeyCloses(false);
			getManager().showAlert("Another user or session has modified your layout and you will need to merge changes");
		} else if (!hasMultipleLayouts()) {
			saveSingleLayoutFile(layout);
		} else {
			List<AmiWebLayoutFile> changedFiles = new ArrayList<AmiWebLayoutFile>();
			List<AmiWebLayoutFile> unchangedFiles = new ArrayList<AmiWebLayoutFile>();

			// includes root file
			for (AmiWebLayoutFile i : layout.getChildrenRecursive(true))
				(hasChangedSinceLastSaveLayoutHelper(i) ? changedFiles : unchangedFiles).add(i);

			AmiWebSaveMultiLayoutPortlet awsmp = new AmiWebSaveMultiLayoutPortlet(getManager().generateConfig(), service, layout, changedFiles, unchangedFiles);
			getManager().showDialog("Save Included Layouts", awsmp, 400, 300);
		}
	}
	private boolean hasExternalPartyChangedLayouts() {
		for (AmiWebLayoutFile i : layout.getChildrenRecursive(true)) {
			String left = Tuple2.getB(this.loadLayoutData(i.getAbsoluteLocation(), i.getSource()));
			String right = i.getRawTextFromDisk();
			if (SH.is(left) && OH.ne(left, right))
				return true;
		}
		return false;
	}
	public void saveSingleLayoutFile(AmiWebLayoutFile layout) {
		if (layout.isReadonly()) {
			getManager().showAlert("Cannot save changes to a readonly layout: " + layout.getAbsoluteLocation());
			return;
		}
		Map json = layout.buildCurrentJson(service);
		String jsonText = this.toJson(json);
		String name = layout.getAbsoluteLocation();
		String sourceType = layout.getSource();

		saveFile(sourceType, name, jsonText);
		layout.setJson(jsonText, true);
	}
	public void saveFile(String sourceType, String name, String jsonText) {
		if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(sourceType)) {
			this.service.getUserFilesManager().saveFile(AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + name, jsonText);
		} else if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(sourceType)) {
			this.service.getCloudManager().saveLayout(name, jsonText);
		} else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(sourceType)) {
			try {
				fs.getFile(name).getParentFile().mkdirForce();
				fs.getFile(name).writeText(jsonText);
			} catch (IOException e) {
				throw OH.toRuntime(e);
			}
		} else if (AmiWebConsts.LAYOUT_SOURCE_TMP.equals(sourceType)) {
			AmiWebTempFilesManager.getTempFilesManager(this.service.getPortletManager().getState().getWebStatesManager()).putLayout(name, jsonText);
		} else
			throw new RuntimeException("Uknown source type: " + sourceType);
		this.service.getAutosaveManager().onLayoutSaved();//TODO: probably should be passing in the jsontext?
	}

	private void exportAllLayouts(AmiWebLayoutFile layout, List<Map<String, Object>> layoutsSink, boolean includeFullPath) {
		Map data = layout.buildCurrentJson(service);
		String location = layout.getLocation();
		String source = layout.getSource();
		if (!includeFullPath && !layout.getIsLocationRelative() && AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(source))
			location = SH.afterLast(location, '/');
		layoutsSink.add((Map) CH.m("type", source, "location", location, "data", data));
		for (AmiWebLayoutFile i : layout.getChildren())
			exportAllLayouts(i, layoutsSink, includeFullPath);
	}

	//Steps to know if a file has changed // This check isn't recursive
	// 1) Check orig with new -> source, relative, location, if they have changed then it has changed
	// 2) Check if build json returns the same value

	private boolean hasChangedSinceLastSaveLayoutHelper(AmiWebLayoutFile t) {
		if (t.hasChangedSinceLastSave()) // This function is a precursor check not a complete check yet
			return true;
		Tuple2<Boolean, String> tuple = this.loadLayoutData(t.getAbsoluteLocation(), t.getSource());
		String left = tuple == null ? null : tuple.getB();
		String right = this.toJson(t.buildCurrentJson(this.service));
		if (OH.ne(left, right))
			return true;
		return false;
	}
	private boolean hasChangedSinceLastSave(AmiWebLayoutFile t) {
		if (this.hasChangedSinceLastSaveLayoutHelper(t))
			return true;
		for (AmiWebLayoutFile c : t.getChildren())
			if (hasChangedSinceLastSave(c))
				return true;
		return false;

	}
	public void onAdnChanged(String oldAdn, String newAdn, AmiWebAliasPortlet portlet) {
		String oldAlias = AmiWebUtils.getAliasFromAdn(oldAdn);
		String oldName = AmiWebUtils.getNameFromAdn(oldAdn);
		getLayoutByFullAlias(oldAlias).onAmiPanelIdChanged(oldName, newAdn);
	}
	public boolean hasMultipleLayouts() {
		return this.layout.getChildrenCount() > 0;
	}
	public void fireOnHiddenPanelAdded(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
		if (!this.listeners.isEmpty())
			for (int i = 0, l = this.listeners.size(); i < l; i++) {
				try {
					this.listeners.get(i).onHiddenPanelAdded(amiWebLayoutFile, def);
				} catch (Exception e) {
					LH.warning(log, "Error for listener on def: " + def, e);
				}
			}
	}
	public void fireOnHiddenPanelRemoved(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def) {
		if (!this.listeners.isEmpty())
			for (int i = 0, l = this.listeners.size(); i < l; i++) {
				try {
					this.listeners.get(i).onHiddenPanelRemoved(amiWebLayoutFile, def);
				} catch (Exception e) {
					LH.warning(log, "Error for listener on def: " + def, e);
				}
			}
	}
	void fireOnHiddenPanelIdChanged(AmiWebLayoutFile amiWebLayoutFile, AmiWebPortletDef def, String oldPanelId, String newPanelId) {
		if (!this.listeners.isEmpty())
			for (int i = 0, l = this.listeners.size(); i < l; i++) {
				try {
					this.listeners.get(i).onHiddenPanelIdChanged(amiWebLayoutFile, oldPanelId, newPanelId);
				} catch (Exception e) {
					LH.warning(log, "Error for listener on def: " + def, e);
				}
			}
	}

	public void addHiddenPanelListener(AmiWebHiddenPanelsListener listener) {
		CH.addIdentityOrThrow(this.listeners, listener);
	}
	public void removeHiddenPanelListener(AmiWebHiddenPanelsListener listener) {
		CH.removeOrThrow(this.listeners, listener);
	}

	public AmiWebService getService() {
		return this.service;
	}
	/*
	public String getLayoutNameHtml() {
		//TODO: Remove Not Used;
		return this.layoutName;
	}
	*/
	public String getSharedLayoutDir() {
		return this.sharedLayoutDir;
	}
	public AmiWebLayoutFile getLayoutFileFromAutosave(AutoSaveFile autosave) throws Exception {
		String autoSavedContentStr = this.service.getAutosaveManager().getLayout(autosave.getNumber()).getLayout();
		Map<String, Object> autoSavedConfig = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(autoSavedContentStr);
		AmiWebLayoutFile autoSavedLayout = this.loadLayoutByConfig(autosave.getLayoutName(), autoSavedContentStr, autoSavedConfig, null, false);
		return autoSavedLayout;

	}
	public AmiWebFile getLayoutFile(AmiWebLayoutFile lo) {
		String name = lo.getLocation();
		String sourceType = lo.getSource();
		if (AmiWebConsts.LAYOUT_SOURCE_LOCAL.equals(sourceType)) {
			AmiWebUserFilesManager cs = service.getUserFilesManager();
			String key = AmiWebConsts.USER_SETTING_AMI_LAYOUT_PREFIX + name;
			return cs.getFile(key);
		} else if (AmiWebConsts.LAYOUT_SOURCE_CLOUD.equals(sourceType)) {
			return this.service.getCloudManager().getFile(name);
		} else if (AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE.equals(sourceType)) {
			return fs.getFile(name);
		} else if (AmiWebConsts.LAYOUT_SOURCE_SHARED.equals(sourceType)) {
			return fs.getFile(this.toSharedLayoutName(name));
		} else
			return null;

	}
	public long getLastModifiedTimeFromLayoutAndChildren(AmiWebLayoutFile lo) {
		AmiWebFile lf = getLayoutFile(lo);

		long t = lf != null ? lf.lastModified() : -1;

		for (AmiWebLayoutFile child : lo.getChildren()) {
			long ct = getLastModifiedTimeFromLayoutAndChildren(child);
			if (ct >= t)
				t = ct;
		}
		return t;
	}
	public boolean checkForAutoSaveLayout() {
		try {
			//Find last autosave
			AmiWebVarsManager varsManager = this.service.getVarsManager();
			if (!varsManager.isUserDev())
				return false;
			String currentLayout = varsManager.getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_CURRENT);
			AutoSaveFile lastAutosave = this.service.getAutosaveManager().getLastAutoSave(currentLayout);
			if (lastAutosave == null)
				return false;

			// Get user saved layout

			AmiWebLayoutFile userSavedLayout = this.getLayout();
			//			Tuple2<Boolean, String> tuple = this.loadLayoutData(currentLayout, currentLayoutSource);
			//			if (tuple == null)
			//				return false;
			//			String userSavedContentStr = tuple.getB();
			//			Map<String, Object> userSavedConfig = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(userSavedContentStr);
			//			AmiWebLayoutFile userSavedLayout = this.loadLayoutByConfig(currentLayout, userSavedContentStr, userSavedConfig, currentLayoutSource);

			// Check if current layout is (new) untitled and is empty
			if (OH.eq(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, this.getLayoutName()) && service.getDesktop().getDesktop().getWindows().isEmpty())
				return false;
			//Compare last user saved time with auto saved time

			long lastUserSavedTimestamp = this.getLastModifiedTimeFromLayoutAndChildren(userSavedLayout);
			long lastAutoSaveTimestamp = lastAutosave.getTimestamp();
			if (lastAutoSaveTimestamp > lastUserSavedTimestamp) {
				//Get AutoSaved Layout
				AmiWebLayoutFile autoSavedLayout = this.getLayoutFileFromAutosave(lastAutosave);
				AmiWebDiffersPortlet dp = new AmiWebDiffersPortlet(this.service, this.service.getDesktop().generateConfig());

				// Check if the layout chains are the same
				boolean isSame = dp.addTab(userSavedLayout, autoSavedLayout, "User Saved", "AutoSaved");
				if (isSame)
					return false;

				// If not show dialog
				String showPrompt = service.getVarsManager().getSetting(AmiWebConsts.USER_SETTING_SHOW_AUTOSAVE_PROMPT);

				if (AmiWebConsts.DEVELOPER_AUTOSAVE_SHOW.equals(showPrompt) || showPrompt == null) {
					String text = "There exists a newer version of this layout in your autosave.<br> Do you want to load the Autosaved File " + lastAutosave.getNumber() + "?";
					ConfirmDialogPortlet alertDialogPortlet = new ConfirmDialogPortlet(service.getDesktop().generateConfig(), text, ConfirmDialog.TYPE_OK_CUSTOM, this)
							.setCallback(DIALOG_CHECK_AUTOSAVE);
					alertDialogPortlet.addButton(ConfirmDialog.ID_YES, "Yes");
					alertDialogPortlet.addButton(ConfirmDialog.ID_NO, "No");
					alertDialogPortlet.addButton("DIFF", "View Changes");
					alertDialogPortlet.setCorrelationData(new Tuple3<AmiWebLayoutFile, AmiWebLayoutFile, Integer>(userSavedLayout, autoSavedLayout, lastAutosave.getNumber()));
					this.service.getDesktop().getManager().showDialog("Check Autosave", alertDialogPortlet);
					return true;
				} else if (showPrompt.equals(AmiWebConsts.DEVELOPER_AUTOSAVE_HIDE)) {
					return false;

				} else {
					return false;
				}
			}
		} catch (Exception e) {
			getManager().showAlert("Error while checking for autosaved layout: " + e.getMessage());
			LH.warning(log, "Error while checking for autosaved layout: ", e);
		}
		return false;

	}

	public boolean onButton2(ConfirmDialog source, String id) {
		service.getSecurityModel().assertPermitted(this.service.getDesktop(), source.getCallback(), "RESET_LAYOUT");
		if (SH.equals(DIALOG_CHECK_AUTOSAVE, source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				Tuple3<AmiWebLayoutFile, AmiWebLayoutFile, Integer> t = (Tuple3<AmiWebLayoutFile, AmiWebLayoutFile, Integer>) source.getCorrelationData();
				Integer autoSaveId = t.getC();
				if (autoSaveId == null) {
					getManager().showAlert("Invalid Autosave id: " + autoSaveId);
					return false;
				}
				AutoSaveFile layout = this.service.getAutosaveManager().getLayout(autoSaveId);
				this.loadLayoutDialog(layout.getLayoutName(), layout.getLayout(), null);
				return true;
			} else if (ConfirmDialog.ID_NO.equals(id)) {
				return true;
			} else if (SH.equals("DIFF", id)) {
				Tuple3<AmiWebLayoutFile, AmiWebLayoutFile, Integer> t = (Tuple3<AmiWebLayoutFile, AmiWebLayoutFile, Integer>) source.getCorrelationData();
				AmiWebLayoutFile userSavedLayout = t.getA();
				AmiWebLayoutFile autoSavedLayout = t.getB();
				AmiWebDiffersPortlet dp = new AmiWebDiffersPortlet(this.service, this.service.getDesktop().generateConfig());
				dp.addTab(userSavedLayout, autoSavedLayout, "User Saved", "AutoSaved");
				getManager().showDialog("Layout Diff", dp);
				return false;
			}
		} else if (ConfirmDialog.ID_YES.equals(id)) {
			if ("RESET_LAYOUT".equals(source.getCallback())) {
				Tuple3<String, String, String> t = (Tuple3<String, String, String>) source.getCorrelationData();
				String name = t.getA();
				String configuration = t.getB();
				String layoutSource = t.getC();
				this.loadLayout(name, configuration, layoutSource);
			}
		} else if ("view_unsaved_changes".equals(id)) {
			try {
				Tuple2<Boolean, String> tuple = this.loadLayoutData(this.layout.getAbsoluteLocation(), this.layout.getSource());
				String unsavedContentStr = this.toJson(this.layout.buildCurrentJson(this.service));
				String savedContentStr = tuple.getB();
				AmiWebUtils.diffConfigurations(service, savedContentStr, unsavedContentStr, "Saved Layout", "Unsaved Layout", null);
			} catch (Exception e) {
				LH.warning(log, "Error displaying unsaved changes ", e);
			}
			return false;
		}
		return true;
	}
	public void reloadLayout() {
		this.layout.reloadCurrentLayout();
		setLayout(this.layout);
	}
}
