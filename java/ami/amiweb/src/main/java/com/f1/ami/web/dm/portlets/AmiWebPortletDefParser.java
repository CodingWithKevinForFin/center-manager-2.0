package com.f1.ami.web.dm.portlets;

import java.util.Map;

import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebObjectDefParser;
import com.f1.ami.web.AmiWebPortletDef;
import com.f1.ami.web.AmiWebService;
import com.f1.utils.CH;

public class AmiWebPortletDefParser implements AmiWebObjectDefParser {
	AmiWebService service;
	private AmiWebPortletDef pd;
	private AmiWebAliasPortlet ap;

	public AmiWebPortletDefParser(AmiWebService service, Object obj) {
		this.service = service;
		if (obj instanceof AmiWebPortletDef)
			setHiddenPanel((AmiWebPortletDef) obj);
		else
			setVisiblePanel((AmiWebAliasPortlet) obj);
	}

	@Override
	public String parseConfigFromLayoutConfig(String configText) {
		Map<String, AmiWebPortletDef> portletConfigs = AmiWebLayoutHelper.getPortletConfigs((AmiWebLayoutHelper.parseJsonSafe(configText, service.getPortletManager())));
		AmiWebPortletDef def;
		if (getHiddePanel() != null)
			def = CH.getOr(portletConfigs, getHiddePanel().getAmiPanelId(), null);
		else
			def = CH.getOr(portletConfigs, getVisiblePanel().getAmiPanelId(), null);
		return def != null ? service.getLayoutFilesManager().toJson(def.getPortletConfig()) : null;
	}

	public AmiWebPortletDef getHiddePanel() {
		return pd;
	}

	public void setHiddenPanel(AmiWebPortletDef pd) {
		this.pd = pd;
	}

	public AmiWebAliasPortlet getVisiblePanel() {
		return ap;
	}

	public void setVisiblePanel(AmiWebAliasPortlet ap) {
		this.ap = ap;
	}

	public String getAdn() {
		return pd != null ? pd.getFullAdn() : ap.getAmiLayoutFullAliasDotId();
	}

	public String getThisConfig() {
		if (getHiddePanel() != null)
			return service.getLayoutFilesManager().toJson(this.pd.getPortletConfig());
		else
			return service.getLayoutFilesManager().toJson(this.ap.getConfiguration());
	}
}
