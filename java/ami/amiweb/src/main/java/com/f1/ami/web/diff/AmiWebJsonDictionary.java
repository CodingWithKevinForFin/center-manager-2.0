package com.f1.ami.web.diff;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AmiWebJsonDictionary {

	final Map<String, String> listKeys = new HashMap<String, String>();
	final Map<String, String> labels = new HashMap<String, String>();
	final Set<String> hiddenKeys = new HashSet<String>();
	final Set<String> amiscripts = new HashSet<String>();

	public AmiWebJsonDictionary() {
		listKeys.put("portletConfigs.#.portletConfig.fields", "l");
		listKeys.put("portletConfigs", "portletConfig.amiPanelId");
		listKeys.put("metadata.dm.dms", "lbl");
		listKeys.put("metadata.dm.lnk", "title");
		listKeys.put("portletConfigs.#.portletConfig.windows", "title");
		listKeys.put("metadata.stm.styles", "id");
		listKeys.put("metadata.dm.lnk.#.awcs", "vn");
		listKeys.put("portletConfigs.#.portletConfig.callbacks.entries", "name");
		listKeys.put("portletConfigs.#.portletConfig.amiCols", "id");
		listKeys.put("metadata.callbacks.entries", "name");
		listKeys.put("portletConfigs.#.portletConfig.dm", "dmadn");
		listKeys.put("metadata.dm.dms.#.callbacks.entries", "name");
		listKeys.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl", "nm");
		listKeys.put("metadata.dm.dms.#.datasources", "");
		listKeys.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.cols", "nm");
		listKeys.put("portletConfigs.#.portletConfig.rtSources", "");
		listKeys.put("metadata.callbacks.entries.#.linkedVariables", "ari");
		listKeys.put("includeFiles", "alias");
		listKeys.put("portletConfigs.#.portletConfig.vcols", "id");
		labels.put("portletConfigs", "panels");
		labels.put("metadata.dm.dms", "dataModels");
		labels.put("metadata.stm", "styles");
		labels.put("metadata.dm.lnk", "relationships");
		labels.put("metadata.dm.lnk.#.awcs", "wheres");
		labels.put("metadata.dm.lnk.#.awcs.#.pf", "prefix");
		labels.put("metadata.dm.lnk.#.awcs.#.sf", "suffix");
		labels.put("metadata.amiCustomCss", "customCss");
		labels.put("metadata.amiScriptMethods", "customMethods");
		labels.put("portletConfigs.#.portletBuilderId", "panelType");
		labels.put("portletConfigs.#.portletConfig.upid", "userPreferenceId");
		labels.put("portletConfigs.#.portletConfig.fields.#.l", "label");
		labels.put("portletConfigs.#.portletConfig.fields.#.n", "name");
		labels.put("portletConfigs.#.portletConfig.fields.#.t", "type");
		labels.put("metadata.stm.styles.#.lb", "name");
		labels.put("metadata.stm.styles.#.pt", "parentId");
		labels.put("portletConfigs.#.portletConfig.amiStyle", "style");
		labels.put("portletConfigs.#.portletConfig.amiStyle.pt", "parentId");
		labels.put("portletConfigs.#.portletConfig.fields.#.style.pt", "parentId");
		labels.put("portletConfigs.#.portletConfig.amiCols", "columns");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.fw", "fixedWidth");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.de", "description");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.eo", "editOptions");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.eof", "editOptionsFormula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.cl", "clickable");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.et", "editType");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.ei", "editID");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.sr", "sort");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.hs", "headerStyle");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.tf", "targetFormula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.sy", "styleFormula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.bg", "backgroundFormula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.fg", "colorFormula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.fm", "formula");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.pc", "precision");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.tp", "format");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.tl", "title");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.dfd", "calendarDisableFutureDays");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.lnd", "calendarEnableLastDays");
		labels.put("portletConfigs.#.portletConfig.amiCols.#.oc", "oneClick");
		labels.put("portletConfigs.#.portletConfig.amiPanelId", "panelId");
		labels.put("portletConfigs.#.portletConfig.amiTitle", "title");
		labels.put("portletConfigs.#.portletConfig.dm", "usedDatamodels");
		labels.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.nm", "name");
		labels.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.oc", "devPromptOnChange");
		labels.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.cols", "columns");
		labels.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.cols.#.nm", "name");
		labels.put("metadata.dm.dms.#.callbacks.entries.#.schema.tbl.#.cols.#.tp", "type");
		labels.put("metadata.dm.dms.#.lbl", "name");
		labels.put("metadata.dm.dms.#.lbl", "name");
		labels.put("metadata.dm.dms.#.test_input_type", "devTestType");
		labels.put("metadata.dm.dms.#.test_input_vars", "devTestVars");
		labels.put("portletConfigs.#.portletConfig.titlePnl.title", "header");
		labels.put("portletConfigs.#.portletConfig.rtSources", "feeds");
		labels.put("portletConfigs.#.portletConfig.hah", "haltOnHidden");
		labels.put("portletConfigs.#.portletConfig.windows.#.leftDflt", "left");
		labels.put("portletConfigs.#.portletConfig.windows.#.topDflt", "top");
		labels.put("portletConfigs.#.portletConfig.windows.#.widthDflt", "width");
		labels.put("portletConfigs.#.portletConfig.windows.#.heightDflt", "height");
		labels.put("portletConfigs.#.portletConfig.windows.#.stateDflt", "state");
		labels.put("portletConfigs.#.portletConfig.windows.#.zindexDflt", "zindex");
		hiddenKeys.add("metadata");
		hiddenKeys.add("portletConfigs.#.portletConfig");
		hiddenKeys.add("metadata.dm");
		hiddenKeys.add("metadata.callbacks.entries");
		hiddenKeys.add("metadata.stm.styles");
		hiddenKeys.add("portletConfigs.#.portletConfig.callbacks.entries");
		hiddenKeys.add("metadata.callbacks.entries");
		hiddenKeys.add("metadata.dm.dms.#.callbacks.entries");
		hiddenKeys.add("metadata.dm.dms.#.callbacks.entries.#.schema.tbl");
		hiddenKeys.add("portletConfigs.#.portletConfig.titlePnl");
		amiscripts.add("amiscript");
		amiscripts.add("amiScriptMethods");
	}

	public String getListKey(String path) {
		return this.listKeys.get(path);
	}

	public String getLabel(String path) {
		return this.labels.get(path);
	}

	public boolean isHiddenKey(String path) {
		return this.hiddenKeys.contains(path);
	}
	public boolean isAmiscriptKey(String path) {
		return this.amiscripts.contains(path);
	}
}
