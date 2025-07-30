package com.f1.ami.web.dm;

import java.util.Map;

import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebObjectDefParser;
import com.f1.ami.web.AmiWebService;
import com.f1.utils.CH;

public class AmiWebDmLinkDefParser implements AmiWebObjectDefParser {
	private final AmiWebService service;
	private AmiWebDmLink link;

	public AmiWebDmLinkDefParser(AmiWebService service, AmiWebDmLink link) {
		this.service = service;
		this.link = link;
	}
	public String getRelId() {
		return link.getRelationshipId();
	}
	@Override
	public String parseConfigFromLayoutConfig(String configText) {
		Map<String, AmiWebDmLinkDef> dmLinkConfigs = AmiWebLayoutHelper.getDmLinkConfigs(AmiWebLayoutHelper.parseJsonSafe(configText, service.getPortletManager()));
		AmiWebDmLinkDef def = CH.getOr(dmLinkConfigs, getRelationship().getRelationshipId(), null);
		return def != null ? service.getLayoutFilesManager().toJson(def.getConfiguration()) : null;
	}
	public AmiWebDmLink getRelationship() {
		return link;
	}
	public void setRelationship(AmiWebDmLink link) {
		this.link = link;
	}
}
