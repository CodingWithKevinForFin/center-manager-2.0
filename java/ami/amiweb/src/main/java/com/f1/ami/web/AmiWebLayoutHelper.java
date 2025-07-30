package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebPortletDef.Callback;
import com.f1.ami.web.dm.AmiWebDmDef;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkDef;
import com.f1.ami.web.dm.AmiWebDmLinkImpl;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DesktopPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.DetailedException;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class AmiWebLayoutHelper {

	private static final Logger log = LH.get();
	private static final String METADATA_KEYS[] = new String[] { "titleBarHtml", "browserTitle", "vars", "amiScriptMethods", "amiCustomCss", "callbacks", "userPrefNamespace" };
	public static final String DEFAULT_ROOT_ALIAS = "";
	private static final int LAYOUT_NAME_LENGTH = 64;

	public static boolean isNonEmptyLayout(Map<String, Object> configuration) {
		try {
			List portlets = (List) configuration.get("portletConfigs");
			if (portlets.size() > 1)
				return true;
			Map metadata = (Map) configuration.get("metadata");
			for (String key : METADATA_KEYS) {
				Object o = metadata.get(key);
				if (o == null)
					continue;
				else if (o instanceof String) {
					if (SH.is(o))
						return true;
					else
						continue;
				} else if (o instanceof Map) {
					if (CH.isntEmpty(((Map) o)))
						return true;
					else
						continue;
				}
			}
			Map dm = (Map) configuration.get("dm");
			List dms = (List) configuration.get("dms");
			if (CH.isntEmpty(dms))
				return true;
			return false;
		} catch (Exception e) {
			LH.warning(log, "Error testing if layout is empty", e);
			return true;
		}
	}
	static public void manageBackwardsCompatibility(PortletManager pm, Map<String, Object> configuration) {
		if (configuration == null)
			return;
		//Get version of layout
		Object vversion = AmiWebLayoutVersionHelper.getOrNoThrow(configuration, "metadata.fileVersion", 1);

		if (OH.eq(vversion, 1)) {
			AmiWebLayoutVersionHelper.manageVersion_01(pm, configuration);
		} else if (OH.eq(vversion, 2)) {
			AmiWebLayoutVersionHelper.manageVersion_02(pm, configuration);
		}
		int version = SH.parseInt(vversion.toString());
		if (version < 4) {
			AmiWebLayoutVersionHelper.fixUseDs(pm, configuration);
			AmiWebLayoutVersionHelper.fieldStyleBackwardsCompat(configuration);
		}

	}

	public static boolean isValidLayoutName(String fileName, AmiWebService service) {
		if (SH.isnt(fileName)) {
			service.getPortletManager().showAlert("Layout name required");
			return false;
		} else if (SH.indexOf(fileName, ',', 0) != -1 || SH.indexOf(fileName, '/', 0) != -1 || SH.indexOf(fileName, '\\', 0) != -1) {
			service.getPortletManager().showAlert("Layout name cannot contain these characters: <B>, / \\ </B>");
			return false;
		} else if (SH.length(fileName) > LAYOUT_NAME_LENGTH) {
			service.getPortletManager().showAlert("Layout name cannot be greater than " + LAYOUT_NAME_LENGTH + " characters long");
			return false;
		} else if (OH.eq(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, fileName)) {
			service.getPortletManager().showAlert("Layout name cannot be " + SH.quote(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME));
			return false;
		} else if (!IOH.isValidFilename(fileName)) {
			service.getPortletManager().showAlert("File name contains invalid character(s)");
			return false;
		}
		return true;
	}

	public static class FileSavePortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {
		final private AmiWebService service;
		private FormPortlet form;
		private FormPortletButton saveButton;
		private FormPortletButton cancelButton;
		private FormPortletTextField nameField;
		private AmiWebLayoutManager layoutManager;

		public FileSavePortlet(AmiWebLayoutManager layoutManager, PortletConfig config) {
			super(config);
			this.service = AmiWebUtils.getService(this.getManager());
			this.layoutManager = layoutManager;
			form = addChild(new FormPortlet(generateConfig()), 0, 0);
			String name = service.getLayoutFilesManager().getLayoutName();
			name = SH.afterLast(name, '/', name);

			nameField = form.addField(new FormPortletTextField("File Name:")).setValue(name);
			nameField.focus();
			saveButton = form.addButton(new FormPortletButton("Save"));
			cancelButton = form.addButton(new FormPortletButton("Cancel"));
			form.addFormPortletListener(this);
			setSuggestedSize(400, 100);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == cancelButton)
				close();
			else if (button == saveButton) {
				String name = nameField.getValue();
				name = SH.trim(name);
				if (!isValidLayoutName(name, service))
					return;
				else {
					Map<String, AmiWebFile> layoutNames = service.getLayoutFilesManager().getLocalLayoutNames();
					if (layoutNames.containsKey(name)) {
						String message = "Overwrite existing file '<B>" + this.nameField.getValue() + "</B>'?";
						ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), message, ConfirmDialog.TYPE_YES_NO);
						cdp.addDialogListener(this);
						getManager().showDialog("File exists", cdp);
					} else {
						this.service.getLayoutFilesManager().saveLayoutAs(name, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
						close();
					}
				}
			}
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
			if (keycode == SH.CHAR_RETURN)
				onButtonPressed(formPortlet, saveButton);
		}

		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				String name = nameField.getValue();
				this.service.getLayoutFilesManager().saveLayoutAs(name, AmiWebConsts.LAYOUT_SOURCE_LOCAL);
				close();
			}
			return true;
		}

		private AmiWebService getService() {
			return this.service;
		}
	}

	public static class ImportPortlet extends FormPortlet implements ConfirmDialogListener {
		final private AmiWebService service;
		private FormPortletButton importButton;
		private FormPortletButton cancelButton;
		private FormPortletTextAreaField textField;
		private AmiWebBlankPortlet targetBlankPortlet;
		private AmiWebLayoutManager layoutManager;

		public ImportPortlet(AmiWebLayoutManager manager, PortletConfig config, String text, AmiWebBlankPortlet targetBlankPortlet) {
			super(config);
			this.service = AmiWebUtils.getService(this.getManager());
			this.layoutManager = manager;
			this.targetBlankPortlet = targetBlankPortlet;
			this.getFormPortletStyle().setLabelsWidth(0);
			FormPortletTitleField title;
			if (text == null) {
				title = addField(new FormPortletTitleField("Enter your configuration in the text area below"));
				textField = addField(new FormPortletTextAreaField(""));
				textField.focus();
				importButton = addButton(new FormPortletButton("Import"));
				cancelButton = addButton(new FormPortletButton("Cancel"));
			} else {
				title = addField(new FormPortletTitleField("Copy and paste out the selected text"));
				textField = addField(new FormPortletTextAreaField("")).setValue(text);
				textField.setSelection(0, text.length());
				cancelButton = addButton(new FormPortletButton("Dismiss"));
			}
			textField.setLabelHidden(true);
			textField.setTopPosPx(36);
			textField.setBottomPosPct(0.10);
			textField.setLeftPosPx(16);
			textField.setRightPosPx(16);
			title.setLabelHidden(true);
			title.setLeftPosPx(16);
			title.setRightPosPct(0.0);
			title.setTopPosPx(8);
			title.setHeightPx(24);
		}

		@Override
		protected void onUserPressedButton(FormPortletButton formPortletButton) {
			if (formPortletButton == cancelButton)
				close();
			else if (formPortletButton == importButton) {
				onImportButton();
			}
		}

		private void onImportButton() {
			String configText = textField.getValue();
			Map<String, Object> configuration = AmiWebLayoutHelper.parseJsonSafe(configText, getManager());

			if (configuration == null) {
				return;
			} else {
				String type;
				try {
					type = (String) RootAssister.INSTANCE.getNestedValue(configuration, "portletConfigs.0.portletBuilderId", true);
				} catch (Exception e) {
					type = null;
				}

				if (this.targetBlankPortlet != null) {
					try {
						if (DesktopPortlet.Builder.ID.equals(type)) {
							showAlert("This is a top level layout. Use File -> Import instead");
							return;
						} else {

							Portlet imported = importConfiguration(getService(), configuration, targetBlankPortlet.getPortletId(), false);
							targetBlankPortlet.getParent().replaceChild(targetBlankPortlet.getPortletId(), imported);
							targetBlankPortlet.close();
							close();
							getService().getDesktop().updateDashboard();
						}
					} catch (Exception e) {
						LH.warning(log, "Error loading config: ", configText, e);
						showAlert("Error importing layout: " + e.getMessage());
					}
				} else {
					if (configuration.containsKey("metadata") || configuration.containsKey("layouts")) {
						LH.info(log, "User Importing layout of ", SH.length(configText), " char(s)");
						ConfirmDialog dialog = service.getLayoutFilesManager().loadLayoutDialog(AmiWebLayoutManager.DEFAULT_LAYOUT_NAME, configText, null);
						if (dialog != null)
							dialog.addDialogListener(this);
						else
							close();
					} else {
						if (AmiWebLayoutHelper.importWindowConfig(getService(), "Imported", configuration, false) != null)
							close();
					}
				}
			}
			return;
		}
		private void showAlert(String string) {
		}

		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			if (ConfirmDialogPortlet.ID_YES.equals(id))
				close();
			return true;
		}

		private AmiWebService getService() {
			return this.service;
		}

	}

	/**
	 * @param service
	 * @param configuration
	 * @param targetPortletId
	 * @param isTransient
	 * @return
	 */
	static public AmiWebAliasPortlet importConfiguration(AmiWebService service, Map<String, Object> configuration, String targetPortletId, boolean isTransient) {
		PortletManager manager = service.getPortletManager();
		AmiWebAliasPortlet target = (AmiWebAliasPortlet) manager.getPortlet(targetPortletId);
		// Get target portlet to copy
		try {
			StringBuilder warningsSink = new StringBuilder();
			AmiWebLayoutFile layout = service.getLayoutFilesManager().getLayoutByFullAlias(target.getAmiLayoutFullAlias());
			//##############
			String fullAlias = layout.getFullAlias();
			//##############
			Map<String, String> mapping = new HashMap<String, String>();
			HashMap<String, AmiWebPortletDef> sink = new HashMap<String, AmiWebPortletDef>();
			readPortletConfigs(service, configuration, sink, layout);

			Map<String, String> dmNameMappings = new HashMap<String, String>();
			List<Map<String, Object>> datamodels = (List<Map<String, Object>>) configuration.get("dms");
			if (CH.isntEmpty(datamodels)) {
				for (Map<String, Object> i : datamodels) {
					String origName = (String) i.get("lbl");
					AmiWebDmsImpl dm = service.getDmManager().importDms(fullAlias, i, warningsSink, isTransient);
					String newName = dm.getDmName();
					dmNameMappings.put(origName, newName);
				}
			}
			//Get used panel ids
			Set<String> usedPanelIds = new HashSet<String>();
			for (AmiWebPortletDef p : sink.values()) {
				if (SH.equals("amiblank", p.getBuilderId()))
					usedPanelIds.add(p.getAmiPanelId());
			}
			usedPanelIds.addAll(layout.getHiddenPanelIds());
			usedPanelIds.addAll(service.getPanelIdsByFullAlias(fullAlias));

			String topId = CH.getOrThrow(Caster_String.INSTANCE, configuration, "topAmiPanelId");
			AmiWebPortletDef top = null;
			for (AmiWebPortletDef p : sink.values()) {
				if (OH.eq(topId, p.getAmiPanelId()))
					top = p;
				//Get new ids
				String name = AmiWebUtils.getNameFromAdn(p.getAmiPanelId());
				if (isTransient)
					name = service.getVarsManager().toTransientId(name);
				else
					name = service.getVarsManager().fromTransientId(name);
				String pid = SH.getNextId(name, usedPanelIds);
				usedPanelIds.add(pid);
				CH.putOrThrow(mapping, p.getAmiPanelId(), pid);

				// Go through datamodels and update dmadns with relative adns
				for (int i = 0; i < p.getUsedDmCount(); i++) {
					String dmadn = p.getUsedDmAt(i);
					if (dmadn != null) {
						if (AmiWebUtils.isParentAlias(fullAlias, dmadn)) {
							p.replaceUsedDmAt(i, AmiWebUtils.getRelativeAlias(fullAlias, dmadn));
						} else {
							warningsSink.append("Panel '" + AmiWebUtils.getFullAlias(p.getLayoutFile().getFullAlias(), p.getAmiPanelId()) + "' is referencing the '" + dmadn
									+ "' datmodel which is out of scope for layout '" + fullAlias + "'<br>");
							p.replaceUsedDmAt(i, "MISSING_" + AmiWebUtils.getNameFromAdn(dmadn));
						}
					}
				}
			}

			List<Callback> callbacks = new ArrayList<AmiWebPortletDef.Callback>();

			//Update AmiPanelIds with new panelId, update childPanelIds 
			for (AmiWebPortletDef def : sink.values()) {
				def.setAmiPanelId(mapping.get(def.getAmiPanelId()));
				for (int i = 0; i < def.getUsedDmCount(); i++) {
					String dm = def.getUsedDmAt(i);
					String newName = dmNameMappings.get(dm);
					if (newName != null)
						def.replaceUsedDmAt(i, newName);
				}
				for (String s : CH.l(def.getChildren())) {
					String replacement = CH.getOrThrow(mapping, s);
					if (OH.ne(s, replacement))
						def.replaceChild(s, replacement);
				}
				layout.addHiddenPanel(def);
				callbacks.addAll(def.getCallbacks());
			}
			Map<String, String> relationshipMappings = new HashMap<String, String>();
			if (CH.isntEmpty(callbacks)) {
				for (Callback i : callbacks) {
					for (int n = 0; n < i.getLinkedVariablesCount(); n++) {
						String ari = i.getLinkedVariableAri(n);
						ari = AmiWebAmiObjectsVariablesHelper.replaceIds(ari, mapping, dmNameMappings, relationshipMappings);
						i.setLinkedVariableAri(n, ari);
					}
				}
			}
			if (top == null)
				throw new RuntimeException("Missing top portlet: " + topId);
			AmiWebAliasPortlet importedPortlet = build(top, service, warningsSink, isTransient);

			List<Map<String, Object>> links = (List<Map<String, Object>>) configuration.get("ami_links");
			List<AmiWebDmLinkImpl> newLinks = new ArrayList<AmiWebDmLinkImpl>(links.size());
			if (CH.isntEmpty(links)) {
				for (Map<String, Object> link : links) {
					String sourceLayoutAlias = (String) link.get("SourceLayoutAlias");
					String targetLayoutAlias = (String) link.get("TargetLayoutAlias");
					String fullspadn = AmiWebUtils.getFullAlias(sourceLayoutAlias, (String) link.get("spadn"));
					String fulltpadn = AmiWebUtils.getFullAlias(targetLayoutAlias, (String) link.get("tpadn"));
					String newsrc_portletid = mapping.get(fullspadn);
					String newtgt_portletid = mapping.get(fulltpadn);

					String findSourceId = null;
					String findTargetId = null;

					//At least one src or target should be populated
					if (newsrc_portletid == null && newtgt_portletid == null) {
						continue;
					}
					if (newsrc_portletid == null) {
						// External Relationship
						findSourceId = fullspadn;
					} else {
						newsrc_portletid = AmiWebUtils.getFullAlias(fullAlias, newsrc_portletid);
						findSourceId = newsrc_portletid;
					}
					if (newtgt_portletid == null) {
						// External Relationship
						findTargetId = fulltpadn;
					} else {
						newtgt_portletid = AmiWebUtils.getFullAlias(fullAlias, newtgt_portletid);
						findTargetId = newtgt_portletid;
					}

					String relid = CH.getOr(Caster_String.INSTANCE, link, AmiWebDmLink.CONFIG_RELID, "REL1");
					if (isTransient)
						relid = service.getVarsManager().toTransientId(relid);
					else
						relid = service.getVarsManager().fromTransientId(relid);
					String nextRelationshipId = service.getDmManager().getNextRelationshipId(fullAlias, relid);
					link.put(AmiWebDmLink.CONFIG_RELID, nextRelationshipId);
					AmiWebDmLinkImpl l = new AmiWebDmLinkImpl(service.getDmManager(), "", null, "", (byte) 0);
					if (isTransient)
						l.setTransient(true);
					l.init(link, mapping, warningsSink);
					String sourceDmAliasDotName = l.getSourceDmAliasDotName();
					String targetDmAliasDotName = l.getTargetDmAliasDotName();
					if (dmNameMappings.containsKey(sourceDmAliasDotName))
						sourceDmAliasDotName = dmNameMappings.get(sourceDmAliasDotName);
					if (dmNameMappings.containsKey(targetDmAliasDotName))
						targetDmAliasDotName = dmNameMappings.get(targetDmAliasDotName);
					l.setSourcePanelAliasDotId(findSourceId);
					l.setTargetPanelAliasDotId(findTargetId);
					if (sourceDmAliasDotName != null)
						l.setSourceDm(AmiWebUtils.getFullAlias(sourceLayoutAlias, sourceDmAliasDotName), l.getSourceDmTableName());
					if (targetDmAliasDotName != null)
						l.setTargetDm(AmiWebUtils.getFullAlias(targetLayoutAlias, targetDmAliasDotName));
					//					l.setRelationshipId(service.getDmManager().getNextRelationshipId(fullAlias, l.getRelationshipId()));
					l.setAmiLayoutFullAlias(fullAlias);
					l.setLinkUid(manager.generateId());
					service.getDmManager().addDmLink(l);
					newLinks.add(l);
				}
			}
			if (warningsSink.length() > 0)
				service.getPortletManager().showAlert(warningsSink.toString());

			// Initialize linked variables in callbacks when importing
			service.getAmiPanelManager().onInitDone();
			for (AmiWebAliasPortlet p : PortletHelper.findPortletsByType(importedPortlet, AmiWebAliasPortlet.class))
				AmiWebUtils.recompileAmiscript(p);
			for (AmiWebDmLinkImpl i : newLinks)
				i.getAmiScriptCallbacks().recompileAmiscript();
			return importedPortlet;
		} catch (Exception e) {
			LH.warning(log, "Error pasting ", e);
			manager.showAlert("Error importing layout: " + e.getMessage(), e);
			return null;
		}

	}
	static public Portlet putTargetInDivider(AmiWebDesktopPortlet desktop, Portlet fillerPortlet, String direction, String targetPortletId) {
		StringBuilder warningsSink = new StringBuilder();
		if (warningsSink.length() > 0)
			desktop.getManager().showAlert(warningsSink.toString());
		try {
			AmiWebAliasPortlet target = (AmiWebAliasPortlet) desktop.getManager().getPortlet(targetPortletId);
			PortletContainer targetParent = target.getParent();

			boolean isFirst = "left".equals(direction) || "above".equals(direction);
			Portlet firstPortlet = isFirst ? fillerPortlet : target;
			Portlet secondPortlet = isFirst ? target : fillerPortlet;
			boolean dir = "left".equals(direction) || "right".equals(direction);

			String amiLayoutFullAlias = target.getAmiLayoutFullAlias();
			AmiWebBlankPortlet blankTarget = desktop.newAmiWebAmiBlankPortlet(amiLayoutFullAlias);
			targetParent.replaceChild(target.getPortletId(), blankTarget);
			AmiWebDividerPortlet newChild = desktop.newDividerPortlet(desktop.generateConfig(), dir, firstPortlet, secondPortlet, amiLayoutFullAlias, false);
			targetParent.replaceChild(blankTarget.getPortletId(), newChild);
			blankTarget.close();
			newChild.setDefaultOffsetPctToCurrent();
			return newChild;
		} catch (Exception e) {
			LH.warning(log, "Error placing target in divider ", e);
			return null;
		}
	}
	public static Map<String, Object> exportConfiguration(AmiWebService service, String sourcePortletId, boolean includeExternalRelationships, boolean includeDatamodels,
			boolean includeTransients) {
		if (includeTransients)
			service.getDesktop().setIsDoingExportTransient(true);
		try {
			final List<Map<String, Object>> portletConfigs = new ArrayList<Map<String, Object>>();
			Set<String> adns = new HashSet<String>();
			Set<String> dms = new HashSet<String>();
			AmiWebPortletDef top = exportConfiguration((AmiWebAliasPortlet) service.getPortletManager().getPortlet(sourcePortletId), portletConfigs, adns, dms, includeTransients);
			List<Map<String, Object>> links = new ArrayList<Map<String, Object>>();
			for (AmiWebDmLink link : service.getDmManager().getDmLinks()) {
				String sourcePanelAliasDotId = link.getSourcePanelAliasDotId();
				String targetPanelAliasDotId = link.getTargetPanelAliasDotId();
				boolean hasSource = adns.contains(sourcePanelAliasDotId);
				boolean hasTarget = adns.contains(targetPanelAliasDotId);
				if (includeExternalRelationships ? (hasSource || hasTarget) : (hasSource && hasTarget)) {
					Map<String, Object> configuration = link.getConfiguration();
					String commonAlias = AmiWebUtils.getCommanAliasRoot(sourcePanelAliasDotId, targetPanelAliasDotId);
					configuration.put("SourceLayoutAlias", commonAlias);
					configuration.put("TargetLayoutAlias", commonAlias);
					links.add(configuration);
				}
			}
			Map<String, Object> config = new HashMap<String, Object>();
			config.put("ami_links", links);
			Collections.sort(portletConfigs, PORTLETID_COMPARATOR);
			config.put("portletConfigs", portletConfigs);
			if (includeDatamodels) {
				List<Object> dmsList = new ArrayList<Object>();
				for (String i : dms)
					dmsList.add(service.getDmManager().getDmByAliasDotName(i).getConfiguration());
				config.put("dms", dmsList);
			}
			config.put("topAmiPanelId", top.getAmiPanelId());
			return config;
		} finally {
			if (includeTransients)
				service.getDesktop().setIsDoingExportTransient(false);
		}
	}
	private static AmiWebPortletDef exportConfiguration(AmiWebAliasPortlet portlet, List<Map<String, Object>> portletConfigs, Set<String> adns, Set<String> dms,
			boolean includeTransients) {
		if (!includeTransients && portlet.isTransient()) {
			portlet = portlet.getNonTransientPanel();
			if (portlet == null)
				return null;
		}
		AmiWebPortletDef def = new AmiWebPortletDef(portlet);
		String alias = def.getLayoutFile().getFullAlias();
		if (portlet instanceof AmiWebDmPortlet)
			dms.addAll(((AmiWebDmPortlet) portlet).getUsedDmAliasDotNames());
		if (OH.ne(alias, "")) {
			def.setAmiPanelId(AmiWebUtils.getFullAlias(alias, def.getAmiPanelId()));
			if (def.getChildrenCount() > 0)
				for (String i : CH.l(def.getChildren()))
					def.replaceChild(i, AmiWebUtils.getFullAlias(alias, i));
			for (int i = 0; i < def.getUsedDmCount(); i++)
				def.replaceUsedDmAt(i, AmiWebUtils.getFullAlias(alias, def.getUsedDmAt(i)));
		}
		portletConfigs.add(toPortletConfig(def));
		adns.add(portlet.getAmiLayoutFullAliasDotId());
		for (AmiWebAliasPortlet i : portlet.getAmiChildren())
			exportConfiguration(i, portletConfigs, adns, dms, includeTransients);
		return def;
	}

	public static void getAllPortletsUnder(Portlet portlet, List<AmiWebAliasPortlet> sink) {
		if (portlet.getPortletConfig().getBuilderId() != null && portlet instanceof AmiWebAliasPortlet)
			sink.add((AmiWebAliasPortlet) portlet);
		if (portlet instanceof PortletContainer) {
			final PortletContainer pc = (PortletContainer) portlet;
			for (Portlet p : pc.getChildren().values())
				getAllPortletsUnder(p, sink);
		}
	}

	static public Portlet importWindowConfig(AmiWebService service, String windowName, Map<String, Object> configuration, boolean isTransient) {
		if (windowName == null)
			windowName = "untitled";
		if (configuration.containsKey("metadata"))
			throw new RuntimeException("Contents are a layout, expecting a panel");
		StringBuilder warningsSink = new StringBuilder();
		AmiWebDesktopPortlet desktop = service.getDesktop();
		AmiWebBlankPortlet blank = desktop.newAmiWebAmiBlankPortlet("");//TODO:
		blank.setTransient(isTransient);

		desktop.getDesktop().addChild(windowName, blank);
		AmiWebAliasPortlet imported = AmiWebLayoutHelper.importConfiguration(service, configuration, blank.getPortletId(), isTransient);
		if (imported != null) {
			blank.getParent().replaceChild(blank.getPortletId(), imported);
			blank.close();
		}
		if (warningsSink.length() > 0 && service.getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
			service.getDebugManager().addMessage(new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_LAYOUT, service.getAri(), null, "Error importing panel",
					CH.m("Warnings", warningsSink.toString()), null));
		desktop.updateDashboard();
		return imported;
	}

	public static Comparator<Map<String, Object>> PORTLETID_COMPARATOR = new Comparator<Map<String, Object>>() {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			Map t1 = (Map) o1.get("portletConfig");
			Map t2 = (Map) o2.get("portletConfig");
			if (t1 == null || t2 == null)
				return OH.compare(t1 == null, t2 == null);
			return OH.compare((String) t1.get("amiPanelId"), (String) t2.get("amiPanelId"));
		}
	};

	public static Map<String, Object> toPortletConfig(AmiWebAliasPortlet p) {
		final Map<String, Object> m = new LinkedHashMap<String, Object>();
		final PortletConfig pc = p.getPortletConfig();
		m.put("portletBuilderId", pc.getBuilderId());
		m.put("portletConfig", p.getConfiguration());
		return m;
	}
	public static Map<String, Object> toPortletConfig(AmiWebPortletDef p) {
		final Map<String, Object> m = new LinkedHashMap<String, Object>();
		m.put("portletBuilderId", p.getBuilderId());
		m.put("portletConfig", p.getPortletConfig());
		return m;
	}

	public static void readPortletConfigs(AmiWebService service, Map<String, Object> configuration, HashMap<String, AmiWebPortletDef> portlets, AmiWebLayoutFile layout) {
		OH.assertEq(portlets.size(), 0);
		if (configuration.isEmpty())
			return;
		final List<Map<String, Object>> portletConfigs = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, configuration, "portletConfigs");
		for (Map<String, Object> e : portletConfigs) {
			try {
				final String builderId = (String) CH.getOrThrow(e, "portletBuilderId");
				final Map<String, Object> portletConfig = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(e, "portletConfig"));
				AmiWebPortletDef t = new AmiWebPortletDef(layout, (AmiWebPortletBuilder) service.getPortletManager().getPortletBuilder(builderId), portletConfig);
				portlets.put(t.getAmiPanelId(), t);
			} catch (Exception ex) {
				throw new DetailedException("Error with portlet", ex).set("config", e);
			}
		}
	}

	public static Map<String, AmiWebPortletDef> getPortletConfigs(Map<String, Object> layoutConfig) {
		Map<String, AmiWebPortletDef> sink = new HashMap<String, AmiWebPortletDef>();
		final List<Map<String, Object>> portletConfigs = (List<Map<String, Object>>) CH.getOrThrow(Caster_Simple.OBJECT, layoutConfig, "portletConfigs");
		for (Map<String, Object> e : portletConfigs) {
			try {
				final Map<String, Object> portletConfig = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(e, "portletConfig"));
				AmiWebPortletDef t = new AmiWebPortletDef(portletConfig);
				sink.put(t.getAmiPanelId(), t);
			} catch (Exception ex) {
				throw new DetailedException("Error with portlet", ex).set("config", e);
			}
		}
		return sink;
	}

	public static Map<String, AmiWebDmDef> getDmConfigs(Map<String, Object> layoutConfig) {
		Map<String, AmiWebDmDef> sink = new HashMap<String, AmiWebDmDef>();
		final List<Map<String, Object>> dmsConfigList = getDmsConfigList(layoutConfig);
		final List<Map<String, Object>> dmtConfigList = getDmtConfigList(layoutConfig);
		if (dmsConfigList != null) {
			for (Map<String, Object> dmsConfig : dmsConfigList) {
				try {
					AmiWebDmDef dmDef = new AmiWebDmDef(dmsConfig);
					dmDef.setType(AmiWebDmDef.TYPE_DMS);
					sink.put(dmDef.getDmName(), dmDef);
				} catch (Exception e) {
					throw new DetailedException("Error with dms ", e).set("config", dmsConfig);
				}
			}
		} else if (dmtConfigList != null) {
			for (Map<String, Object> dmtConfig : dmtConfigList) {
				try {
					AmiWebDmDef dmDef = new AmiWebDmDef(dmtConfig);
					dmDef.setType(AmiWebDmDef.TYPE_DMT);
					sink.put(dmDef.getDmName(), dmDef);
				} catch (Exception e) {
					throw new DetailedException("Error with dmt ", e).set("config", dmtConfig);
				}
			}
		}
		return sink;
	}
	public static Map<String, AmiWebDmLinkDef> getDmLinkConfigs(Map<String, Object> layoutConfig) {
		Map<String, AmiWebDmLinkDef> sink = new HashMap<String, AmiWebDmLinkDef>();
		final List<Map<String, Object>> dmLinkConfigList = getDmLinkConfigList(layoutConfig);
		if (dmLinkConfigList != null) {
			for (Map<String, Object> dmLinkConfig : dmLinkConfigList) {
				try {
					AmiWebDmLinkDef linkDef = new AmiWebDmLinkDef(dmLinkConfig);
					sink.put(linkDef.getRelId(), linkDef);
				} catch (Exception e) {
					throw new DetailedException("Error with AmiWebLink ", e).set("config", dmLinkConfig);
				}
			}
		}
		return sink;
	}
	private static List<Map<String, Object>> getDmsConfigList(Map<String, Object> layoutConfig) {
		Map metadata = (Map) Caster_Simple.OBJECT.cast(CH.getOrThrow(layoutConfig, "metadata"));
		Map<String, Object> dm = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(metadata, "dm"));
		return (List<Map<String, Object>>) Caster_Simple.OBJECT.cast(CH.getOr(dm, "dms", null));
	}
	private static List<Map<String, Object>> getDmtConfigList(Map<String, Object> layoutConfig) {
		Map metadata = (Map) Caster_Simple.OBJECT.cast(CH.getOrThrow(layoutConfig, "metadata"));
		Map<String, Object> dm = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(metadata, "dm"));
		return (List<Map<String, Object>>) Caster_Simple.OBJECT.cast(CH.getOr(dm, "dmt", null));
	}
	private static List<Map<String, Object>> getDmLinkConfigList(Map<String, Object> layoutConfig) {
		Map metadata = (Map) layoutConfig.get("metadata");
		Map<String, Object> dm = (Map<String, Object>) Caster_Simple.OBJECT.cast(CH.getOrThrow(metadata, "dm"));
		return (List<Map<String, Object>>) Caster_Simple.OBJECT.cast(CH.getOr(dm, "lnk", null));
	}

	private static class Builder {

		final private AmiWebPortletDef portletDef;
		final private List<Builder> children;
		final private AmiWebService service;
		final private AmiWebLayoutFile layoutFile;
		private AmiWebAliasPortlet portlet;

		public Builder(AmiWebPortletDef def, AmiWebService service, StringBuilder warningsSink) {
			this.service = service;
			this.portletDef = def;
			this.layoutFile = def.getLayoutFile();
			if (def.getChildrenCount() == 0) {
				children = Collections.emptyList();
			} else {
				children = new ArrayList<AmiWebLayoutHelper.Builder>(def.getChildrenCount());
				for (String adn : CH.l(def.getChildren())) {
					final AmiWebLayoutFile lm = layoutFile.getChildByRelativeAlias(AmiWebUtils.getAliasFromAdn(adn));
					if (lm == null) {
						warningsSink.append(layoutFile.describe() + " references panel of missing layout: '" + adn + "'<br>");
						def.removeChild(adn);
						continue;
					}
					final AmiWebPortletDef p = lm.getHiddenPanelByPanelId(AmiWebUtils.getNameFromAdn(adn));
					if (p == null) {
						warningsSink.append(layoutFile.describe() + " references missing panel: '" + adn + "'<br>");
						def.removeChild(adn);
						continue;
					}
					this.children.add(new Builder(p, service, warningsSink));
				}
			}
		}

		public void build(StringBuilder warningsSink, boolean isTransient) {
			final PortletManager manager = service.getPortletManager();
			portlet = (AmiWebAliasPortlet) portletDef.getBuilder().buildPortlet(new BasicPortletConfig(manager, manager.generateId(), portletDef.getBuilderId(), true));
			portlet.setAdn(AmiWebUtils.getFullAlias(layoutFile.getFullAlias(), portletDef.getAmiPanelId()));
			portlet.setTransient(isTransient);
			manager.onPortletAdded(portlet);
			if (portlet instanceof AmiWebPortlet)
				service.fireAmiWebPanelAdded((AmiWebPortlet) portlet);
			for (Builder builder : this.children)
				builder.build(warningsSink, isTransient);
		}
		public void callInit(StringBuilder warningsSink) {
			try {
				portlet.init(portletDef.getPortletConfig(), Collections.EMPTY_MAP, warningsSink);
			} catch (Exception ex) {
				service.getPortletManager().showAlert("Error importing layout: " + ex.getMessage(), ex);
				LH.warning(log, "Error building config, init failed for portlet: ", portlet.getClass().getName(), ", adn=", portlet.getAmiLayoutFullAliasDotId(), ", config=",
						portletDef.getPortletConfig(), ex);
			}
			for (Builder builder : this.children)
				builder.callInit(warningsSink);
		}
		public void callInitDone(StringBuilder warningsSink) {
			try {
				portlet.onAmiInitDone();
			} catch (Exception ex) {
				service.getPortletManager().showAlert("Error importing layout: " + ex.getMessage(), ex);
				LH.warning(log, "Error building config, onAmiInitDone failed for portlet: ", portlet.getClass().getName(), ", adn=", portlet.getAmiLayoutFullAliasDotId(), ex);
			}
			for (Builder builder : this.children)
				builder.callInitDone(warningsSink);
		}

	}

	public static AmiWebAliasPortlet build(AmiWebPortletDef def, AmiWebService service, StringBuilder warningsSink, boolean isTrnsient) {
		Builder builder = new Builder(def, service, warningsSink);
		def.getLayoutFile().onShowPanel(def.getAmiPanelId());
		builder.build(warningsSink, isTrnsient);
		builder.callInit(warningsSink);
		builder.callInitDone(warningsSink);
		return builder.portlet;
	}

	public static Map<String, Object> parseJsonSafe(CharSequence configText, PortletManager manager) {
		Map<String, Object> configuration = null;
		if (SH.isnt(configText)) {
			return configuration;
		}
		try {
			configuration = (Map<String, Object>) ObjectToJsonConverter.INSTANCE_CLEAN.stringToObject(configText);

		} catch (Exception e) {
			manager.showAlert("Error: Configuration contains invalid JSON", e);
			return null;
		}
		if (configuration == null) {
			manager.showAlert("Error: Configuration contains invalid JSON");
		}
		return configuration;
	}
	public static boolean isParsingSafeSuccessFul(String configText, PortletManager manager) {
		Map<String, Object> configuration = AmiWebLayoutHelper.parseJsonSafe(configText, manager);
		if (OH.eq(configuration, null))
			return false;
		return true;
	}
	public static String toJson(Object config, byte mode) {
		return ObjectToJsonConverter.getInstanceWithSorting(mode).objectToString(config);
	}
	public static String toJson(Object config, AmiWebService service) {
		return ObjectToJsonConverter.getInstanceWithSorting(service.getLayoutFilesManager().getUserExportMode()).objectToString(config);
	}
	public static Map<String, Object> exportUserPreferences(AmiWebService service, String layout, String upid) {
		for (Map<String, Object> pref : service.getUserPrefs()) {
			String id = (String) pref.get("upid");
			String l = CH.getOr(Caster_String.INSTANCE, pref, "layout", "");
			if (upid.equals(id) && l.equals(layout))
				return pref;
		}
		return null;
	}

}
