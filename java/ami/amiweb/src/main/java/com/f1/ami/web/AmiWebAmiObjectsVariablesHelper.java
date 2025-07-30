package com.f1.ami.web;

import java.util.Map;

import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class AmiWebAmiObjectsVariablesHelper {

	private static IntKeyMap<String> TYPE2ICON = new IntKeyMap<String>();
	private static IntKeyMap<String> TYPE2STYLE = new IntKeyMap<String>();

	static {
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_SESSION, AmiWebConsts.ICON_FILES);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_LAYOUT, AmiWebConsts.ICON_LAYOUT2);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_DATAMODEL, AmiWebConsts.DM_TREE_ICON_DM);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_PANEL, AmiWebConsts.DM_TREE_ICON_PANEL_ST);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_FORMBUTTON, AmiWebConsts.ICON_FIELD_BUTTON);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_RELATIONSHIP, AmiWebConsts.DM_TREE_ICON_RELATIONSHIP);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_FIELD, AmiWebConsts.ICON_FIELD);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_FIELD_VALUE, AmiWebConsts.ICON_VALUE);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_MENUITEM, AmiWebConsts.ICON_MENUITEM);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_CHART_AXIS, AmiWebConsts.ICON_AXIS);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_CHART_PLOT, AmiWebConsts.ICON_PLOT);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_CHART_LAYER, AmiWebConsts.ICON_LAYER);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_PROCESSOR, AmiWebConsts.DM_TREE_ICON_PROCESSOR);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_COLUMN, AmiWebConsts.ICON_COLUMN);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_GROUPING, AmiWebConsts.ICON_GROUPING);
		TYPE2ICON.put(AmiWebDomObject.ARI_CODE_TAB_ENTRY, AmiWebConsts.DM_TREE_ICON_TAB);
		for (Node<String> i : TYPE2ICON)
			TYPE2STYLE.put(i.getKey(), "_bgi=url(" + i.getValue() + ")");
	}

	public static String getAmiIconForDomObjectType(byte ari) {
		return TYPE2ICON.get(ari);
	}
	public static String getAmiIconStyleForDomObjectType(byte ari) {
		return TYPE2STYLE.get(ari);
	}

	public static String getAmiIconForDomObjectType(String ari) {
		return getAmiIconForDomObjectType(parseType(ari));
	}
	public static String getAmiIconStyleForDomObjectType(String ari) {
		return getAmiIconStyleForDomObjectType(parseType(ari));
	}
	public static String getAmiIconStyleForDomObjectType(AmiWebDomObject domObj) {
		return getAmiIconStyleForDomObjectType(domObj.getAriType());
	}
	public static String getAmiIconForDomObjectType(AmiWebDomObject domObj) {
		return getAmiIconForDomObjectType(domObj.getAriType());
	}

	public static String getDomEventNameForType(byte type) {
		if (AmiWebDomObject.DOM_EVENT_CODE_ONCHANGE == type)
			return AmiWebDomObject.DOM_EVENT_NAME_ONCHANGE;
		else
			return AmiWebDomObject.DOM_EVENT_NAME_NONE;
	}
	public static String getParent(String ari) {
		String r = "";
		String identifier = SH.afterFirst(ari, ':');

		byte type = parseType(ari);
		switch (type) {
			case AmiWebDomObject.ARI_CODE_SESSION:
				return null;
			case AmiWebDomObject.ARI_CODE_LAYOUT:
				return AmiWebDomObject.ARI_TYPE_SESSION + ":session";
			case AmiWebDomObject.ARI_CODE_DATAMODEL:
			case AmiWebDomObject.ARI_CODE_PANEL:
			case AmiWebDomObject.ARI_CODE_PROCESSOR:
			case AmiWebDomObject.ARI_CODE_RELATIONSHIP:
				return AmiWebDomObject.ARI_TYPE_LAYOUT + ":" + AmiWebUtils.getAliasFromAdn(identifier);
			case AmiWebDomObject.ARI_CODE_FORMBUTTON:
			case AmiWebDomObject.ARI_CODE_FIELD:
			case AmiWebDomObject.ARI_CODE_COLUMN:
			case AmiWebDomObject.ARI_CODE_GROUPING:
			case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
			case AmiWebDomObject.ARI_CODE_MENUITEM:
			case AmiWebDomObject.ARI_CODE_CHART_AXIS:
			case AmiWebDomObject.ARI_CODE_TAB_ENTRY:
			case AmiWebDomObject.ARI_CODE_CHART_PLOT: {
				String pnlId = SH.beforeFirst(identifier, '?');
				return AmiWebDomObject.ARI_TYPE_PANEL + ":" + pnlId;
			}
			case AmiWebDomObject.ARI_CODE_CHART_LAYER: {
				String pnlIdPlusGridLocation = SH.beforeFirst(identifier, '+');
				return AmiWebDomObject.ARI_TYPE_CHART_PLOT + ":" + pnlIdPlusGridLocation;
			}
		}
		return r;
	}

	public static String getRelativeAri(String alias, String fullAri) {
		if (SH.equals(AmiWebLayoutFile.DEFAULT_ROOT_ALIAS, alias))
			return fullAri;
		String ariType = SH.beforeFirst(fullAri, ':');
		String identifier = SH.afterFirst(fullAri, ':');

		String relativeAri = SH.join(':', ariType, AmiWebUtils.getRelativeAlias(alias, identifier));
		return relativeAri;
	}
	public static String getFullAri(String alias, String relativeAri) {
		if (SH.equals(AmiWebLayoutFile.DEFAULT_ROOT_ALIAS, alias))
			return relativeAri;
		String ariType = SH.beforeFirst(relativeAri, ':');
		String identifier = SH.afterFirst(relativeAri, ':');

		return SH.join(':', ariType, AmiWebUtils.getFullAlias(alias, identifier));
	}
	public static String getAriType(String alias) {
		return SH.beforeFirst(alias, ':');
	}

	public static AmiWebDomObject getAmiWebDomObjectFromFullAri(String ari, AmiWebService service) {
		return service.getDomObjectsManager().getManagedDomObject(ari);
	}

	public static AmiWebDomObject getAmiWebDomObjectFromRelativeAri(String alias, String relativeAri, AmiWebService service) {
		return getAmiWebDomObjectFromFullAri(getFullAri(alias, relativeAri), service);
	}

	public static String replaceIds(String ari, Map<String, String> panelIds, Map<String, String> datamodelIds, Map<String, String> relationshipIds) {
		byte type = parseType(ari);
		switch (type) {
			case AmiWebDomObject.ARI_CODE_PROCESSOR:
			case AmiWebDomObject.ARI_CODE_SESSION:
			case AmiWebDomObject.ARI_CODE_LAYOUT:
				return ari;

			case AmiWebDomObject.ARI_CODE_DATAMODEL:
				return replaceIds(ari, datamodelIds);
			case AmiWebDomObject.ARI_CODE_PANEL:
			case AmiWebDomObject.ARI_CODE_MENUITEM:
			case AmiWebDomObject.ARI_CODE_FORMBUTTON:
			case AmiWebDomObject.ARI_CODE_FIELD:
			case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
			case AmiWebDomObject.ARI_CODE_CHART_AXIS:
			case AmiWebDomObject.ARI_CODE_CHART_PLOT:
			case AmiWebDomObject.ARI_CODE_CHART_LAYER:
			case AmiWebDomObject.ARI_CODE_TAB_ENTRY:
			case AmiWebDomObject.ARI_CODE_COLUMN:
			case AmiWebDomObject.ARI_CODE_GROUPING:
				return replaceIds(ari, panelIds);
			case AmiWebDomObject.ARI_CODE_RELATIONSHIP:
				return replaceIds(ari, relationshipIds);
			default:
				throw new RuntimeException("bad type: " + ari);
		}

	}
	private static String replaceIds(String ari, Map<String, String> ids) {
		if (CH.isEmpty(ids))
			return ari;
		int i = ari.indexOf(':') + 1;
		int j = ari.indexOf('?') == -1 ? ari.length() - 1 : ari.indexOf('?');
		String key = ari.substring(i, j);
		String replacement = ids.get(key);
		if (replacement != null)
			return SH.splice(ari, i, j - i, replacement);
		return ari;
	}
	public static byte parseType(String ariType) {
		if (ariType == null)
			return -1;
		int pos = ariType.indexOf(':');
		if (pos == -1)
			pos = ariType.length();
		switch (pos) {
			case 4:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_JOIN))
					return AmiWebDomObject.ARI_CODE_JOIN;
			case 5:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_PANEL))
					return AmiWebDomObject.ARI_CODE_PANEL;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_FIELD))
					return AmiWebDomObject.ARI_CODE_FIELD;
				break;
			case 6:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_LAYOUT))
					return AmiWebDomObject.ARI_CODE_LAYOUT;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_COLUMN))
					return AmiWebDomObject.ARI_CODE_COLUMN;
				break;
			case 7:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_SESSION))
					return AmiWebDomObject.ARI_CODE_SESSION;
				break;
			case 8:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_MENUITEM))
					return AmiWebDomObject.ARI_CODE_MENUITEM;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_GROUPING))
					return AmiWebDomObject.ARI_CODE_GROUPING;
				break;
			case 9:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_DATAMODEL))
					return AmiWebDomObject.ARI_CODE_DATAMODEL;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_TAB_ENTRY))
					return AmiWebDomObject.ARI_CODE_TAB_ENTRY;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_PROCESSOR))
					return AmiWebDomObject.ARI_CODE_PROCESSOR;
				break;
			case 10:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_FORMBUTTON))
					return AmiWebDomObject.ARI_CODE_FORMBUTTON;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_CHART_AXIS))
					return AmiWebDomObject.ARI_CODE_CHART_AXIS;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_CHART_PLOT))
					return AmiWebDomObject.ARI_CODE_CHART_PLOT;
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_FIELD_VALUE))
					return AmiWebDomObject.ARI_CODE_FIELD_VALUE;
				break;
			case 11:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_CHART_LAYER))
					return AmiWebDomObject.ARI_CODE_CHART_LAYER;
				break;
			case 12:
				if (ariType.startsWith(AmiWebDomObject.ARI_TYPE_RELATIONSHIP))
					return AmiWebDomObject.ARI_CODE_RELATIONSHIP;
				break;
		}
		return -1;//
		//		throw new RuntimeException("Invalid ari: '" + ariType + "'");
	}
	public static String formatType(byte type) {
		switch (type) {
			case AmiWebDomObject.ARI_CODE_COLUMN:
				return AmiWebDomObject.ARI_TYPE_COLUMN;
			case AmiWebDomObject.ARI_CODE_GROUPING:
				return AmiWebDomObject.ARI_TYPE_GROUPING;
			case AmiWebDomObject.ARI_CODE_PROCESSOR:
				return AmiWebDomObject.ARI_TYPE_PROCESSOR;
			case AmiWebDomObject.ARI_CODE_SESSION:
				return AmiWebDomObject.ARI_TYPE_SESSION;
			case AmiWebDomObject.ARI_CODE_LAYOUT:
				return AmiWebDomObject.ARI_TYPE_LAYOUT;
			case AmiWebDomObject.ARI_CODE_DATAMODEL:
				return AmiWebDomObject.ARI_TYPE_DATAMODEL;
			case AmiWebDomObject.ARI_CODE_PANEL:
				return AmiWebDomObject.ARI_TYPE_PANEL;
			case AmiWebDomObject.ARI_CODE_MENUITEM:
				return AmiWebDomObject.ARI_TYPE_MENUITEM;
			case AmiWebDomObject.ARI_CODE_FORMBUTTON:
				return AmiWebDomObject.ARI_TYPE_FORMBUTTON;
			case AmiWebDomObject.ARI_CODE_FIELD:
				return AmiWebDomObject.ARI_TYPE_FIELD;
			case AmiWebDomObject.ARI_CODE_FIELD_VALUE:
				return AmiWebDomObject.ARI_TYPE_FIELD_VALUE;
			case AmiWebDomObject.ARI_CODE_CHART_AXIS:
				return AmiWebDomObject.ARI_TYPE_CHART_AXIS;
			case AmiWebDomObject.ARI_CODE_CHART_PLOT:
				return AmiWebDomObject.ARI_TYPE_CHART_PLOT;
			case AmiWebDomObject.ARI_CODE_CHART_LAYER:
				return AmiWebDomObject.ARI_TYPE_CHART_LAYER;
			case AmiWebDomObject.ARI_CODE_TAB_ENTRY:
				return AmiWebDomObject.ARI_TYPE_TAB_ENTRY;
			case AmiWebDomObject.ARI_CODE_RELATIONSHIP:
				return AmiWebDomObject.ARI_TYPE_RELATIONSHIP;
			case AmiWebDomObject.ARI_CODE_JOIN:
				return AmiWebDomObject.ARI_TYPE_JOIN;
			default:
				throw new RuntimeException("bad type: " + type);
		}

	}
}
