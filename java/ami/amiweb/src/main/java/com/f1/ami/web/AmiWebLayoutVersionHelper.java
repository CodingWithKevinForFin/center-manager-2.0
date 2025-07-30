package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Field;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Form;
import com.f1.ami.web.style.impl.AmiWebStyleTypeImpl_Panel;
import com.f1.base.Caster;
import com.f1.suite.web.portal.PortletManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.jsonmap.JsonMapHelper;

public class AmiWebLayoutVersionHelper {
	private static final String AMI_SCRIPT_METHODS = "amiScriptMethods";
	private static final String AMISCRIPT = "amiscript";
	private static final Logger log = LH.get();
	private static final String DEFAULT_WHERE = "WHERE";
	private static final String PORTLET_BUILDER_ID_MAPPING = "portletBuilderIdMapping";
	private static final String DM_UID_TO_DM_ID_MAPPING = "dmUidToDmIdMapping";
	private static final String PORTLETID_TO_PANELID_MAPPING = "portletIdToPanelIdMapping";
	private static final Pattern GETPANELID_MATCHER = Pattern.compile("(.*getPanel\\(\")([^\"]+\\.[^\"]+)(\"\\).*)", Pattern.DOTALL);
	private static final Pattern GETFIELDVALUE_MATCHER = Pattern.compile("(.*getFieldValue\\(\")([^\"]+\\.[^\"]+)(\",.*)", Pattern.DOTALL);
	private static final String[] STYLEPATH = new String[] { "metadata", "stm", "styles" };

	static public void manageVersion_01(PortletManager pm, Map<String, Object> fullConfiguration) {
		if (fullConfiguration.isEmpty())
			return;
		// Included Files wasn't added on this version skip
		Map<String, Object> mapping = new HashMap<String, Object>();
		getOldToNewIdMapping(pm, fullConfiguration, mapping);

		manage_metadata_01(pm, "metadata", fullConfiguration, mapping);
		manage_portletConfigs_01(pm, "portletConfigs", fullConfiguration, mapping);
		//		prnt(fullConfiguration);
	}
	static public void manageVersion_02(PortletManager pm, Map<String, Object> fullConfiguration) {
		if (fullConfiguration.isEmpty())
			return;
		putOrThrow(fullConfiguration, "metadata.fileVersion", 3);
		backwardsCompatibilityFixForDmArguments(pm, fullConfiguration);
	}

	// use ds=AMI -> use ds= "AMI"
	static public void fixUseDs(PortletManager pm, Map<String, Object> fullConfiguration) {
		if (fullConfiguration.isEmpty())
			return;
		digDeeperMap(fullConfiguration);
		putOrThrow(fullConfiguration, "metadata.fileVersion", 4);
	}

	static public void backwardsCompatibilityFixForDmArguments(PortletManager pm, Map<String, Object> fullConfiguration) {
		Map<String, Map> dmsArgs = new LinkedHashMap<String, Map>();
		// Go through all dms
		List<Object> dmsList = getL(fullConfiguration, "metadata.dm.dms");
		if (dmsList != null)
			for (int i = 0; i < dmsList.size(); i++) {
				Map dms = (Map) dmsList.get(i);
				// Go through all params and add them to the dm args map
				List<Object> onProcessParams = getL(dms, "onProcessParams");
				if (onProcessParams != null) {
					Map<String, String> dmArgs = new LinkedHashMap<String, String>();
					for (int j = 0; j < onProcessParams.size(); j++) {
						Map onProcessParam = (Map) onProcessParams.get(j);

						String name = (String) get(onProcessParam, "name");
						String type = (String) get(onProcessParam, "type");
						dmArgs.put(name, type);
					}
					String dmid = (String) get(dms, "lbl");
					dmsArgs.put(dmid, dmArgs);
				}
			}

		List<Object> dmLinks = getL(fullConfiguration, "metadata.dm.lnk");
		if (dmLinks != null) {
			// Loop through all relationships
			for (int i = 0; i < dmLinks.size(); i++) {
				Map rel = (Map) dmLinks.get(i);
				List<Object> awcs = getL(rel, "awcs");
				if (awcs != null) {
					List<String> vns = new ArrayList<String>();
					// Loop through all relationship variables and add it to the dm args map
					for (int j = 0; j < awcs.size(); j++) {
						Map awcs_ = (Map) awcs.get(j);
						String vn = (String) get(awcs_, "vn");
						if (!SH.equals(DEFAULT_WHERE, vn)) {
							vns.add(vn);
						}
					}

					String tdmadn = (String) get(rel, "tdmadn");
					Map dmArgs = dmsArgs.get(tdmadn);
					if (dmArgs == null) {
						dmArgs = new LinkedHashMap<String, String>();
						dmsArgs.put(tdmadn, dmArgs);
					}

					for (int j = 0; j < vns.size(); j++) {
						String vn = vns.get(j);
						if (!dmArgs.containsKey(vn)) // If it doesn't contain we need to add it
							dmArgs.put(vn, "String");
					}
				}
			}
		}

		if (dmsList != null)
			for (int i = 0; i < dmsList.size(); i++) {
				Map dms = (Map) dmsList.get(i);
				List<Object> callbackEntries = getL(dms, "callbacks.entries");
				if (callbackEntries != null) {
					String dmid = (String) get(dms, "lbl");
					Map<String, String> dmArgs = dmsArgs.get(dmid);
					if (dmArgs == null)
						continue;
					for (int j = 0; j < callbackEntries.size(); j++) {
						Map callback = (Map) callbackEntries.get(j);
						String name = (String) get(callback, "name");
						if (SH.equals("onProcess", name)) {
							List<String> amiscript = (List) getL(callback, AMISCRIPT);
							// if it is null lets seed it
							int idx = 0;
							if (amiscript == null) {
								amiscript = new ArrayList<String>();
								callback.put(AMISCRIPT, amiscript);
							}
							if (amiscript.size() > 0) {
								if (SH.equals("{\n", amiscript.get(0))) {
									idx = 1;
								}
							}

							for (String varname : dmArgs.keySet()) {
								String type = dmArgs.get(varname);
								String varScript = type + " " + varname + " = wheres.get(\"" + varname + "\");\n";
								amiscript.add(idx++, varScript);
								LH.info(log, "Adding backwards compatibility script for dm: " + dmid + " : " + varScript);

							}

							// Finish stop
							break;
						}
					}
				}
			}
	}

	static public void getOldToNewIdMapping(PortletManager pm, Map<String, Object> configuration, Map<String, Object> mappingSink) {
		HashMap<String, String> portletBuilderIdMapping = new HashMap<String, String>();
		portletBuilderIdMapping.put("desktop", "amidesktop");
		portletBuilderIdMapping.put("VortexWebDatasourceTablePortlet", "amistatictable");
		portletBuilderIdMapping.put("AmiTreeMapStaticPortlet", "amistatictreemap");
		portletBuilderIdMapping.put("queryform", "amiform");
		portletBuilderIdMapping.put("filter", "amifilter");
		portletBuilderIdMapping.put("amichartgride", "amichartgrid");
		//Realtime
		portletBuilderIdMapping.put("VortexWebAmiObjectTablePortlet", "amirealtimetable");
		portletBuilderIdMapping.put("VortexWebAmiAggregateObject2TablePortlet", "Amirealtimeaggtable");
		portletBuilderIdMapping.put("AmiTreemapPortlet", "amirealtimetreemap");
		mappingSink.put(PORTLET_BUILDER_ID_MAPPING, portletBuilderIdMapping);

		//Path:metadata.dm
		//Gets dm UID to DMID mapping
		HashMap<String, String> dmUidToDmidMapping = new HashMap<String, String>();
		List<Object> dmsList = getL(configuration, "metadata.dm.dms");
		if (dmsList != null)
			for (int i = 0; i < dmsList.size(); i++) {
				String old_dmid = (String) get(dmsList, "" + i + ".dmid");
				String new_dmid = (String) get(dmsList, "" + i + ".lbl");
				dmUidToDmidMapping.put(old_dmid, new_dmid);
			}

		//Get dmt UID to DMID mapping
		List<Object> dmtList = getL(configuration, "metadata.dm.dmt");
		if (dmtList != null)
			for (int i = 0; i < dmtList.size(); i++) {
				String old_dmid = (String) get(dmtList, "" + i + ".dmid");
				String new_dmid = (String) get(dmtList, "" + i + ".lbl");
				dmUidToDmidMapping.put(old_dmid, new_dmid);
			}
		mappingSink.put(DM_UID_TO_DM_ID_MAPPING, dmUidToDmidMapping);

		//Path:portletConfigs.
		Set<String> existingPanelIds = new HashSet<String>();
		for (Object portlet : getL(configuration, "portletConfigs")) {
			String panelId = (String) get(portlet, "portletConfig.amiPanelId");
			if (panelId != null)
				existingPanelIds.add(panelId);
		}

		HashMap<String, String> pnlUidToPnlIdMapping = new HashMap<String, String>();
		for (Object portlet : getL(configuration, "portletConfigs")) {
			String portletId = (String) get(portlet, "portletId");
			String panelId = (String) get(portlet, "portletConfig.amiPanelId");

			if (panelId == null) {
				String portletBuilderId = (String) get(portlet, "portletBuilderId");
				if (SH.equals("hdiv", portletBuilderId) || SH.equals("vdiv", portletBuilderId)) {
					panelId = nextId("Div1", existingPanelIds);
				} else if (SH.equals("amiblank", portletBuilderId)) {
					panelId = nextId("Blank1", existingPanelIds);
				} else
					panelId = nextId("PNL1", existingPanelIds);
			}
			String newPanelId = updateIdsWithPeriods(panelId);

			pnlUidToPnlIdMapping.put(portletId, newPanelId);
		}
		mappingSink.put(PORTLETID_TO_PANELID_MAPPING, pnlUidToPnlIdMapping);
	}
	static public void manage_metadata_01(PortletManager pm, String path, Object o, Map<String, Object> mapping) {
		//Path:metadata
		Map<String, Object> configuration = getM(o, path);
		//xCustomCss
		//xAmiScriptMethods
		updateAmiScript(o, "metadata.amiScriptMethods");

		//ixBrowserTitle
		//xCallBacks
		Map<String, Object> m = getM(o, "metadata.callbacks");
		if (m != null)
			for (String callbackType : m.keySet()) {
				updateAmiScript(o, SH.join(".", "metadata.callbacks", callbackType));
			}

		//[metadata][guiServices][adapters][callbacks]
		List<Object> guiServicesAdapters = getL(o, "metadata.guiServices.adapters");
		if (guiServicesAdapters != null) {
			for (int i = 0; i < guiServicesAdapters.size(); i++) {
				Object gsa = guiServicesAdapters.get(i);
				Map<String, Object> guiCallbacks = getM(gsa, "callbacks");
				for (String callbackType : guiCallbacks.keySet()) {
					updateAmiScript(guiCallbacks, callbackType);
				}
			}
		}

		//CustomPrefsImportMode
		if (!configuration.containsKey("customPrefsImportMode"))
			putOrThrow(configuration, "customPrefsImportMode", "reject");
		manage_portletConfigs_metadata_dm_01(pm, "metadata.dm", o, mapping);
		for (Object link : getL(o, "metadata.dm.lnk")) {
			//Convert key sdm to new key sdmadn
			//
			convertIdFromOldKeyToNewKeyIfNotExist(link, "sdm", "sdmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);
			convertIdFromOldKeyToNewKeyIfNotExist(link, "tdm", "tdmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);

			//Panels	
			convertIdFromOldKeyToNewKeyIfNotExist(link, "spid", "spadn", mapping, null, false);
			convertIdFromOldKeyToNewKeyIfNotExist(link, "tpid", "tpadn", mapping, null, false);
			putOrThrow(link, "spadn", updateIdsWithPeriods((String) get(link, "spadn")));
			putOrThrow(link, "tpadn", updateIdsWithPeriods((String) get(link, "tpadn")));

			updateAmiScript(link, "as");
		}
		//File Version
		putOrThrow(configuration, "fileVersion", 2);
		//Gui Servicees
		//stm // Style manager
		//Title Bar Html
		//userPrefNamespace
		//vars
	}
	private static void updateAmiScript(Object o, String path) {
		updateAmiScript(o, path, true);
	}
	private static void updateAmiScript(Object o, String path, boolean keepOld) {
		String amiScriptMethods = (String) get(o, path);
		if (amiScriptMethods == null)
			return;
		StringBuilder sb = new StringBuilder();
		String newAmiScriptMethods = amiScriptMethods;
		newAmiScriptMethods = SH.replaceAll(newAmiScriptMethods, "session.getDatamodel(", "layout.getDatamodel(");
		newAmiScriptMethods = SH.replaceAll(newAmiScriptMethods, "session.getPanel(", "layout.getPanel(");
		newAmiScriptMethods = SH.replaceAll(newAmiScriptMethods, "session.getRelationship(", "layout.getRelationship(");
		newAmiScriptMethods = SH.replaceAll(newAmiScriptMethods, "session.getFieldValue(", "layout.getFieldValue(");

		//Replace panelId's with periods with panelId's with underscores instead
		for (Matcher m = GETPANELID_MATCHER.matcher(newAmiScriptMethods); m.matches();)
			m.reset(newAmiScriptMethods = m.group(1) + m.group(2).replace('.', '_') + m.group(3));

		for (Matcher m = GETFIELDVALUE_MATCHER.matcher(newAmiScriptMethods); m.matches();)
			m.reset(newAmiScriptMethods = m.group(1) + m.group(2).replace('.', '_') + m.group(3));

		sb.append(newAmiScriptMethods);

		if (keepOld && !SH.equals(newAmiScriptMethods, amiScriptMethods)) {
			sb.append("\n");
			sb.append("// ########################################\n");
			sb.append("// Disclaimer: AMI has automatically replaced session with layout\n");
			sb.append("// ########################################\n");
			String[] oldlines = SH.split('\n', amiScriptMethods);
			for (String line : oldlines) {
				sb.append("// ");
				sb.append(line);
				sb.append("\n");
			}
			sb.append("// ##################END###################\n");

		}
		putOrThrow(o, path, sb.toString());
	}
	private static Object convertIdFromOldKeyToNewKeyIfNotExist(Object config, String oldKey, String newKey, Map<String, Object> mapping, String mappingType, boolean force) {
		boolean isCopyValue = mappingType == null;
		boolean useOrigKey = newKey == null;

		Object oldId = (String) get(config, oldKey);
		Object newId = (String) get(config, newKey);
		//If using old config or force is enabled
		if (force || (oldId != null && newId == null)) {
			Object r = isCopyValue ? oldId : get(mapping, SH.join(".", mappingType, oldId));

			if (r != null) {
				putOrThrow(config, useOrigKey ? oldKey : newKey, r);
				return r;
			} else
				return oldId;
		}
		return oldId;
	}
	static public void manage_portletConfigs_01(PortletManager pm, String path, Object o, Map<String, Object> mapping) {
		List<Object> configuration = getL(o, path);
		if (configuration == null)
			return;
		HashMap<String, String> pnlUidToPnlIdMapping = new HashMap<String, String>();

		for (Object portlet : configuration) {
			Map<String, Object> portletConfig = (Map<String, Object>) portlet;
			String builderId = (String) get(portletConfig, "portletBuilderId");
			convertIdFromOldKeyToNewKeyIfNotExist(portletConfig, "portletBuilderId", null, mapping, PORTLET_BUILDER_ID_MAPPING, false);
			if (SH.equals("desktop", builderId)) {
				putOrThrow(portletConfig, "portletConfig.amiPanelId", "@DESKTOP");

				if (getOrNoThrow(portletConfig, "portletConfig.width", null) != null)
					putOrThrow(portletConfig, "portletConfig.width", null);
				if (getOrNoThrow(portletConfig, "portletConfig.height", null) != null)
					putOrThrow(portletConfig, "portletConfig.height", null);

				convertIdFromOldKeyToNewKeyIfNotExist(portletConfig, "portletConfig.active", null, mapping, PORTLETID_TO_PANELID_MAPPING, false);

				for (Object window : getL(portletConfig, "portletConfig.windows")) {
					convertIdFromOldKeyToNewKeyIfNotExist(window, "portlet", null, mapping, PORTLETID_TO_PANELID_MAPPING, false);
					putOrThrow(window, "portlet", updateIdsWithPeriods((String) get(window, "portlet")));
				}
			} else if (SH.equals("VortexWebAmiObjectTablePortlet", builderId)) {
				updateAmiScript(portlet, "portletConfig.editAmiScript");
				for (Object amiCol : getL(portletConfig, "portletConfig.amiCols")) {
					updateAmiScript(amiCol, "tf");
				}
			} else if (SH.equals("VortexWebAmiAggregateObject2TablePortlet", builderId)) {
				updateAmiScript(portlet, "portletConfig.editAmiScript");
				for (Object amiCol : getL(portletConfig, "portletConfig.amiCols")) {
					updateAmiScript(amiCol, "tf");
				}
			} else if (SH.equals("VortexWebDatasourceTablePortlet", builderId)) {
				updateAmiScript(portlet, "portletConfig.editAmiScript");
				for (Object amiCol : getL(portletConfig, "portletConfig.amiCols")) {
					updateAmiScript(amiCol, "tf");
				}

			} else if (SH.equals("AmiTreemapPortlet", builderId)) {
			} else if (SH.equals("queryform", builderId)) {
				for (Object button : getL(portletConfig, "portletConfig.buttons")) {
					updateAmiScript(button, "s");
				}
				for (Object field : getL(portletConfig, "portletConfig.fields")) {
					convertIdFromOldKeyToNewKeyIfNotExist(field, "dmid", null, mapping, DM_UID_TO_DM_ID_MAPPING, false);

					Integer labelSide = (Integer) get(field, "labelSide");
					Integer labelSideAlignment = (Integer) get(field, "labelSideAlignment");
					if (labelSide == null && labelSideAlignment == null) {
						Integer labelAlignment = (Integer) get(field, "labelAlignment");
						if (labelAlignment != null) {
							switch (labelAlignment) {
								case 0:
									labelSide = 0;
									labelSideAlignment = 0;
									break;
								case 1:
									labelSide = 0;
									labelSideAlignment = 1;
									break;
								case 2:
									labelSide = 0;
									labelSideAlignment = 2;
									break;
								case 3:
									labelSide = 3;
									labelSideAlignment = 0;
									break;
								case 4:
									labelSide = 3;
									labelSideAlignment = 1;
									break;
								case 5:
									labelSide = 3;
									labelSideAlignment = 2;
									break;
								case 6:
									labelSide = 1;
									labelSideAlignment = 2;
									break;
								case 7:
									labelSide = 1;
									labelSideAlignment = 1;
									break;
								case 8:
									labelSide = 1;
									labelSideAlignment = 0;
									break;
								case 9:
									labelSide = 2;
									labelSideAlignment = 2;
									break;
								case 10:
									labelSide = 2;
									labelSideAlignment = 1;
									break;
								case 11:
									labelSide = 2;
									labelSideAlignment = 0;
									break;

							}
							putOrThrow(field, "labelSide", labelSide);
							putOrThrow(field, "labelSideAlignment", labelSideAlignment);
						}
					}
					updateAmiScript(field, "onevents.onChange");
					updateAmiScript(field, "onevents.onEnterKey");

				}
			} else if (SH.equals("filter", builderId)) {
				for (Object link : getL(portletConfig, "portletConfig.links")) {
					convertIdFromOldKeyToNewKeyIfNotExist(link, "dmid", "dmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);
				}
			} else if (SH.equals("amichartgride", builderId)) {
				for (Object plotConfig : getM(portletConfig, "portletConfig.plotConfig").values()) {
					for (Object layer : getL(plotConfig, "layers")) {
						convertIdFromOldKeyToNewKeyIfNotExist(layer, "dmName", "dmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);
						List<Object> l = getL(layer, "layers");
						if (l != null)
							for (Object layer2 : l) {
								updateAmiScript(layer2, "tooltip");
							}
					}
				}
			} else if (SH.equals("amisurface", builderId)) {
				for (Object series : getL(portletConfig, "portletConfig.series")) {
					convertIdFromOldKeyToNewKeyIfNotExist(series, "dmid", null, mapping, DM_UID_TO_DM_ID_MAPPING, false);
				}
			} else if (SH.equals("AmiTreeMapStaticPortlet", builderId)) {
			} else if (SH.equals("mapbox", builderId)) {
				for (Object lrs : getL(portletConfig, "portletConfig.lrs")) {
					convertIdFromOldKeyToNewKeyIfNotExist(lrs, "did", "dmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);
				}

			} else if (SH.equals("tab", builderId)) {
				List<Object> l = getL(portletConfig, "portletConfig.tabscnf");
				Map<Object, Object> tabIdToCnf = null;
				if (l != null) {
					tabIdToCnf = new HashMap<Object, Object>();
					for (Object tabscnf : l) {
						convertIdFromOldKeyToNewKeyIfNotExist(tabscnf, "dm", "dmadn", mapping, DM_UID_TO_DM_ID_MAPPING, false);
						updateAmiScript(tabscnf, "nf", false);
						updateAmiScript(tabscnf, "sc", false);
						updateAmiScript(tabscnf, "st", false);
						updateAmiScript(tabscnf, "uc", false);
						updateAmiScript(tabscnf, "ut", false);
						tabIdToCnf.put(get(tabscnf, "id"), tabscnf);
					}
				}
				for (Object tabs : getL(portletConfig, "portletConfig.tabs")) {
					convertIdFromOldKeyToNewKeyIfNotExist(tabs, "portlet", "child", mapping, PORTLETID_TO_PANELID_MAPPING, false);
					if (get(tabs, "nf") == null) {
						Object tabId = get(tabs, "tabId");
						String titleExp2 = (String) get(tabs, "titleExp2");
						String title2 = (String) get(tabs, "titleExp2");
						String title = (String) get(tabs, "title");
						if (tabIdToCnf != null) {
							Map<String, Object> tabCnf = (Map<String, Object>) tabIdToCnf.get(tabId);
							putOrThrow(tabs, "nf", get(tabCnf, "nf"));
						} else if (titleExp2 != null) {
							putOrThrow(tabs, "nf", titleExp2);
						} else if (title2 != null) {
							putOrThrow(tabs, "nf", SH.doubleQuote(title2));
						} else if (title != null) {
							putOrThrow(tabs, "nf", SH.doubleQuote(title));
						}
					}

					if (tabIdToCnf != null) {
						Map<String, Object> target = (Map<String, Object>) tabs;
						Map<String, Object> toAdd = (Map<String, Object>) tabIdToCnf.get(get(tabs, "tabId"));
						CH.putAllMissing(target, toAdd);
					}
				}
			} else if (SH.equals("vdiv", builderId) || SH.equals("hdiv", builderId)) {
				boolean isVertical = (Boolean) get(portletConfig, "portletConfig.isVertical");
				putOrThrow(portletConfig, "portletBuilderId", isVertical ? "vdiv" : "hdiv");
				convertIdFromOldKeyToNewKeyIfNotExist(portletConfig, "portletConfig.child1", null, mapping, PORTLETID_TO_PANELID_MAPPING, false);
				convertIdFromOldKeyToNewKeyIfNotExist(portletConfig, "portletConfig.child2", null, mapping, PORTLETID_TO_PANELID_MAPPING, false);
			} else if (SH.equals("amiblank", builderId)) {

			} else {
			}

			Map<String, Object> callbacks = getM(portletConfig, "portletConfig.callbacks");
			if (callbacks != null) {
				for (String callback : callbacks.keySet()) {
					updateAmiScript(callbacks, callback);
				}
			}

			//Update menu Amiscript
			Map<String, Object> customMenus = getM(portletConfig, "portletConfig.customMenu");
			if (customMenus != null)
				for (Object customMenu : customMenus.values()) {
					updateAmiScript(customMenu, "item.amiscript");
					updateAmiScript(customMenu, "item.display", false);
					updateAmiScript(customMenu, "item.icon", false);
					updateAmiScript(customMenu, "item.status", false);
				}

			// Update PanelId If null
			if (get(portletConfig, "portletConfig.amiPanelId") == null) {
				convertIdFromOldKeyToNewKeyIfNotExist(portletConfig, "portletId", "portletConfig.amiPanelId", mapping, PORTLETID_TO_PANELID_MAPPING, false);
			}
			putOrThrow(portletConfig, "portletConfig.amiPanelId", updateIdsWithPeriods((String) get(portletConfig, "portletConfig.amiPanelId")));
			// Update dms
			Object o1 = get(portletConfig, "portletConfig.dm");
			if (o1 instanceof Map) {
				Map<String, Object> old_dm = (Map<String, Object>) o1;
				if (old_dm != null) {
					List<Object> new_dm = new ArrayList<Object>();
					if (old_dm.size() > 0) {
						HashMap<String, String> dmUidToDmidMapping = (HashMap<String, String>) get(mapping, AmiWebLayoutVersionHelper.DM_UID_TO_DM_ID_MAPPING);
						for (String old_dmid : old_dm.keySet()) {
							Map<String, Object> dm = new HashMap<String, Object>();
							List<Object> dmtbid = (List<Object>) old_dm.get(old_dmid);
							dm.put("dmtbid", dmtbid);
							dm.put("dmadn", getDmadn(mapping, old_dmid));
							new_dm.add(dm);
						}
					}
					putOrThrow(portletConfig, "portletConfig.dm", new_dm);
				}
			}
		}
	}
	private static Object getDmadn(Map<String, Object> mapping, String dmUid) {
		return get(mapping, SH.join(".", DM_UID_TO_DM_ID_MAPPING, dmUid));
	}
	private static Object getPanelId(Map<String, Object> mapping, String portletId) {
		return get(mapping, SH.join(".", PORTLETID_TO_PANELID_MAPPING, portletId));
	}
	private static String updateIdsWithPeriods(String id) {
		if (id == null)
			return null;
		if (id.indexOf('.') != -1)
			return SH.replaceAll(id, '.', '_');
		else
			return id;

	}
	static public void manage_portletConfigs_metadata_dm_01(PortletManager pm, String path, Object o, Map<String, Object> mapping) {
		//Path:metadata.dm
		Map<String, Object> configuration = getM(o, path);

		//Converts old Map of lmg.dms to new List of dms with new ids
		HashMap<String, String> dmUidToDmidMapping = (HashMap<String, String>) get(mapping, AmiWebLayoutVersionHelper.DM_UID_TO_DM_ID_MAPPING);
		Object o1 = get(configuration, "lmg.dm");
		if (o1 instanceof Map) {
			Map<String, Object> old_lmg_dm = (Map<String, Object>) o1;
			List<Object> new_lmg_dm = new ArrayList<Object>();
			for (String old_dmid : old_lmg_dm.keySet()) {
				Map<String, Object> dm = (Map<String, Object>) old_lmg_dm.get(old_dmid);
				dm.put("dmid", dmUidToDmidMapping.get(old_dmid));
				dm.put("dmadn", dmUidToDmidMapping.get(old_dmid));
				new_lmg_dm.add(dm);
			}
			putOrThrow(configuration, "lmg.dm", new_lmg_dm);
		}

		//TODO: Convert old dmt to new dmt
		Object o3 = get(configuration, "lmg.dt");
		if (o3 instanceof Map) {
			Map<String, Object> old_lmg_dm = (Map<String, Object>) o3;
			List<Object> new_lmg_dm = new ArrayList<Object>();
			for (String old_dmid : old_lmg_dm.keySet()) {
				Map<String, Object> dm = (Map<String, Object>) old_lmg_dm.get(old_dmid);
				dm.put("dmid", dmUidToDmidMapping.get(old_dmid));
				dm.put("dmadn", dmUidToDmidMapping.get(old_dmid));
				new_lmg_dm.add(dm);

			}
			putOrThrow(configuration, "lmg.dt", new_lmg_dm);
		}

		//Converts old Map of lmg.pt to new List of dms with new ids
		Object o2 = get(configuration, "lmg.pt");
		if (o2 instanceof Map) {
			Map<String, Object> old_lmg_pt = (Map<String, Object>) o2;
			List<Object> new_lmg_pt = new ArrayList<Object>();
			for (String pt_dmid : old_lmg_pt.keySet()) {
				Map<String, Object> dm = (Map<String, Object>) old_lmg_pt.get(pt_dmid);
				dm.put("dmadn", updateIdsWithPeriods(pt_dmid));
				new_lmg_pt.add(dm);
			}
			putOrThrow(configuration, "lmg.pt", new_lmg_pt);
		}

		Object o4 = get(configuration, "lmg.ds");
		if (o3 instanceof Map) {
			Map<String, Object> old_lmg_ds = (Map<String, Object>) o4;
			List<Object> new_lmg_ds = new ArrayList<Object>();
			for (String dsid : old_lmg_ds.keySet()) {
				Map<String, Object> ds = (Map<String, Object>) old_lmg_ds.get(dsid);
				ds.put("name", dsid);
				new_lmg_ds.add(ds);
			}
			putOrThrow(configuration, "dm.ds", new_lmg_ds);
		}

		//Move node positions into respective configs
		HashMap<String, Object> lmgIdToPosMapping = new HashMap<String, Object>();
		for (Object lmgPos : getL(configuration, "lmg.dm")) {
			String dmadn = (String) get(lmgPos, "dmadn");
			putOrThrow(lmgIdToPosMapping, SH.join(".", "lmgdm", dmadn), lmgPos);
		}
		for (Object lmgPos : getL(configuration, "lmg.dt")) {
			String dmadn = (String) get(lmgPos, "dmadn");
			putOrThrow(lmgIdToPosMapping, SH.join(".", "lmgdt", dmadn), lmgPos);
		}
		for (Object lmgPos : getL(configuration, "lmg.pt")) {
			String dmadn = (String) get(lmgPos, "dmadn");
			putOrThrow(lmgIdToPosMapping, SH.join(".", "lmgpt", dmadn), lmgPos);
		}

		//Update dms
		for (Object dms : getL(o, "metadata.dm.dms")) {
			//Move dmPos dms
			String dmadn = (String) get(dms, "lbl");
			Object dmPos = get(dms, "dmPos");
			if (dmadn != null && dmPos == null) {
				putOrThrow(dms, "dmPos", get(lmgIdToPosMapping, SH.join('.', "lmgdm", dmadn)));
			}

			//Update amiscript dms
			updateAmiScript(dms, "amisc");

		}

		//Update dmts
		for (Object dmt : getL(o, "metadata.dm.dmt")) {
			//Move dmPos dmts
			String dmadn = (String) get(dmt, "lbl");
			Object dmPos = get(dmt, "dmPos");
			if (dmadn != null && dmPos == null) {
				putOrThrow(dmt, "dmPos", get(lmgIdToPosMapping, SH.join('.', "lmgdt", dmadn)));
			}

			//Update dmt's lower dms
			List<Object> lowers = getL(dmt, "lower");
			for (int i = 0; i < lowers.size(); i++) {
				Object object = lowers.get(i);
				String lower = (String) get(mapping, SH.join(".", DM_UID_TO_DM_ID_MAPPING, object));
				putOrThrow(dmt, SH.join(".", "lower", i), lower);
			}

			//Update amiscript dmts
			updateAmiScript(dmt, "amisc");
		}
		//Move dmPos panels
		for (Object portlets : getL(o, "portletConfigs")) {
			String amiPanelId = (String) get(portlets, "portletConfig.amiPanelId");
			Object dmPos = get(portlets, "portletConfig.dmPos");
			if (amiPanelId != null && dmPos == null) {
				putOrThrow(portlets, "portletConfig.dmPos", get(lmgIdToPosMapping, SH.join('.', "lmgpt", amiPanelId)));
			}

		}

	}
	static private void prnt(Object o) {
		System.out.println(ObjectToJsonConverter.INSTANCE_SEMI_SORTING.objectToString(o));
	}
	static public Object getOrNoThrow(Object configuration, String path, Object dflt) {
		Object r = RootAssister.INSTANCE.getNestedValue(configuration, path, false);
		return r == null ? dflt : r;
	}
	static private List<Object> getL(Object configuration, String path) {
		return (List<Object>) RootAssister.INSTANCE.getNestedValue(configuration, path, false);
	}
	static private Map<String, Object> getM(Object configuration, String path) {
		return (Map<String, Object>) RootAssister.INSTANCE.getNestedValue(configuration, path, false);
	}
	static public Object get(Object configuration, String path) {
		return RootAssister.INSTANCE.getNestedValue(configuration, path, false);
	}
	static public <T> T get(Caster<T> caster, Object configuration, String path, T dflt) {
		Object v = RootAssister.INSTANCE.getNestedValue(configuration, path, false);
		final T r = caster.cast(v, false, false);
		return r == null ? dflt : r;
	}
	static private void putOrThrow(Object configuration, String fullKey, Object value) {
		if (fullKey == null)
			throw new NullPointerException("key");
		String path = SH.beforeLast(fullKey, '.', null);
		String key = SH.afterLast(fullKey, '.', fullKey);
		boolean hasParent = path == null || SH.equals("", path);
		Object configObject = hasParent ? configuration : RootAssister.INSTANCE.getNestedValue(configuration, path, true);
		if (configObject == null) {
			if (!SH.isInt(key))
				putOrThrow(configuration, path, configObject = new HashMap<String, Object>());
			else
				putOrThrow(configuration, path, configObject = new ArrayList<Object>());
		}
		if (configObject instanceof Map) {
			Map<String, Object> r = (Map<String, Object>) configObject;
			r.put(key, value);
		} else if (configObject instanceof List) {
			List<Object> r = (List<Object>) configObject;
			r.set(Caster_Integer.PRIMITIVE.cast(key), value);
		}
	}
	static private String nextId(String id, Set<String> sink) {
		return nextId(AmiUtils.toValidVarName(id), sink, 0);
	}
	static private String nextId(String id, Set<String> sink, int i) {
		if (sink.contains(id) == false) {
			sink.add(id);
			return id;
		} else {
			String newId = id;
			while (sink.contains(newId = SH.join("", id, i++))) {

			}
			sink.add(newId);
			return newId;
		}

	}

	//	private static final Pattern P = Pattern.compile("(.*getPanelId\\(\")([^\"]+\\.[^\"]+)(\"\\).*)");
	private static final Pattern P = Pattern.compile("(.*getPanel\\(\")([^\"]+\\.[^\"]+)(\"\\).*)", Pattern.DOTALL);
	//	private static final Pattern P = Pattern.compile("(.*getPanelId\\(\\\\?\")([^\"]+\\.[^\"]+)(\\\\?\"\\).*)");

	public static void main(String a[]) {
		String s = "getPanelId(\"test.test\"); and test.getPanel(\"test.this.out\"); asda";
		s = "FormPanel ff=layout.getPanelId(\"RISK_VIOLATIONS.Approve.Update\");\nff.getField(\"WithValue\").setValue(null);\n\t\t  TablePanel tp = layout.getPanel(\\\"RISK_VIOLATIONS.Approve.Top\\\");\\";
		s = "\t\t   TablePanel tp = layout.getPanel(\"RISK_VIOLATIONS.Approve.Top\");\nTable table = tp.asTable(true,tp.getSelectedRows().size() == 0 ? true : false,false,true,true);\nboolean returnValue=validateApprove(table);\nif(returnValue){\n  submit(\"RiskViolation.Approve\", new Table(table));\n  closeActionWindow(this.getPanel());\n  Map m=layout.getDatamodel(\"RISK_VIOLATIONS_WITH_ORDER_DETAILS\").getValue(\"editableColumns\");\n  m.clear();\n  FormPanel ff=layout.getPanel(\"RISK_VIOLATIONS.Approve.Update\");\n  ff.resetFields();\n}";
		s = " FormPanel fp=layout.getPanel(\"FIXSESSION.SequenceNumber.Bottom\");";

		s = "String editableColumn=layout.getFieldValue(\"RISK_VIOLATIONS.Approve.Update\",\"UpdateColumn\");\nString columnValue=";
		s = "String editableColumn=layout.getFieldValue(\"RISK_VIOLATIONS.Approve.Update\",\"UpdateColumn\");\nString columnValue=layout.getFieldValue(\"RISK_VIOLATIONS.Approve.Update\",\"WithValue\");\nDataModel dm=layout.getDatamodel(\"RISK_VIOLATIONS_WITH_ORDER_DETAILS\");\nMap m;\nif(dm.getValue(\"editableColumns\")==null)\n{\n   m=new Map();\n}\nelse\n{\n   m=dm.getValue(\"editableColumns\");\n}\nm.put(editableColumn,columnValue);\ndm.setValue(\"editableColumns\",m);\ndm.reprocess();\n\n";
		//		s = "String editableColumn=layout.getFieldValue(\"RISK_VIOLATIONS.Approve.UpdateUpdateColumn\");\nString columnValue=";
		//		s = "FormPanel ff layout.getPanelId(\"RISK_VIOLATIONS.Approve.Update\");ff.getField(\"WithValue\").setValue(null);";
		Pattern p2 = Pattern.compile("(.*getFieldValue\\(\")([^\"]+\\.[^\"]+)(\",.*)", Pattern.DOTALL);
		for (Matcher m = p2.matcher(s); m.matches();)
			m.reset(s = m.group(1) + m.group(2).replace('.', '_') + m.group(3));
		System.out.println(s);
	}
	public static String updateFullAri(String fullAri, AmiWebService service) {

		//Looking for TAB_ENTRY:panelid?nn   because this is the old styl where nn is the location, now it uses the tab id
		if (SH.startsWith(fullAri, "TAB_ENTRY:")) {
			int n = fullAri.indexOf('?');
			if (n != -1 && n < fullAri.length() - 1 && OH.isBetween(fullAri.charAt(n + 1), '0', '9')) {
				String s = fullAri.substring(0, n);
				s = SH.stripPrefix(s, "TAB_ENTRY:", true);
				AmiWebAliasPortlet panel = service.getPortletByAliasDotPanelId(s);
				if (panel instanceof AmiWebTabPortlet) {
					AmiWebTabPortlet tab = (AmiWebTabPortlet) panel;
					int tabLocation = SH.parseInt(fullAri.substring(n + 1));
					if (tabLocation >= 0 && tabLocation <= tab.getTabsCount()) {
						String r = tab.getTabAt(tabLocation).getAri();
						LH.info(log, "Updated ari for ", fullAri, " to ", r);
						return r;
					}

				}
			}
		}
		return fullAri;
	}

	private static void digDeeperList(List<Object> c) {
		if (c.isEmpty())
			return;
		for (int i = 0; i < c.size(); i++) {
			Object o = c.get(i);
			if (o instanceof String) {
				c.set(i, AmiUtils.fixUseDsLine((String) o));
			} else if (o instanceof Map)
				digDeeperMap((Map) (o));
		}
	}
	private static void digDeeperMap(Map<String, Object> m) {
		// first get to the bottom
		for (Entry<String, Object> e : m.entrySet()) {
			Object val = e.getValue();
			if (val instanceof Map) {
				digDeeperMap((Map) val);
			} else if (val instanceof List) {
				digDeeperList((List) val);
			} else if (val instanceof String) {
				// replace
				m.put(e.getKey(), AmiUtils.fixUseDsLine((String) val));
			}
		}

	}
	// migrate field styles to panel from layout version <4 to 4; no op if file version is >= 4
	public static void fieldStyleBackwardsCompat(Map<String, Object> configuration) {
		List<Map<String, Object>> styles = (List<Map<String, Object>>) JsonMapHelper.INSTANCE_JSON_NESTED_GETTER_SINGLE.getAlt(configuration, STYLEPATH);
		if (styles == null)
			return;
		Map<String, Object> temp = null;
		for (Map<String, Object> m : styles) {
			Map<String, Map<String, Object>> vl = (Map<String, Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, m, "vl", Collections.emptyMap());
			if (vl.isEmpty())
				continue;
			Map<String, Object> fieldStyles = vl.get(AmiWebStyleTypeImpl_Field.TYPE_FIELD);
			if (fieldStyles == null) {
				fieldStyles = new HashMap<String, Object>();
				// field was introduced in dec 2023
				// jan 2025 stable has field as a style type for the first time
				vl.put(AmiWebStyleTypeImpl_Field.TYPE_FIELD, fieldStyles);
			}
			// prioritize checking HTML/Canvas, then check panel
			if (vl.containsKey(AmiWebStyleTypeImpl_Form.TYPE_FORM)) {
				// check if form has the 3 styles for migration
				temp = vl.get(AmiWebStyleTypeImpl_Form.TYPE_FORM);
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL));
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL));
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL));

			}
			if (vl.containsKey(AmiWebStyleTypeImpl_Panel.TYPE_PANEL)) {
				temp = vl.get(AmiWebStyleTypeImpl_Panel.TYPE_PANEL);
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FLD_BG_CL));
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FLD_FONT_CL));
				if (temp.containsKey(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL))
					fieldStyles.putIfAbsent(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL, temp.get(AmiWebStyleConsts.PROPERTY_NAME_FONT_CL));
			}

		}
	}
}
