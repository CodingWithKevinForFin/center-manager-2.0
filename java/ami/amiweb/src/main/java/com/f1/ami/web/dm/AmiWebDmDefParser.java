package com.f1.ami.web.dm;

import java.util.Map;

import com.f1.ami.web.AmiWebLayoutHelper;
import com.f1.ami.web.AmiWebObjectDefParser;
import com.f1.ami.web.AmiWebService;
import com.f1.utils.CH;

public class AmiWebDmDefParser implements AmiWebObjectDefParser {
	private final AmiWebService service;
	private AmiWebDm dm;

	public AmiWebDmDefParser(AmiWebService service, AmiWebDm datamodel) {
		this.service = service;
		this.dm = datamodel;

	}
	public AmiWebDm getDatamodel() {
		return dm;
	}

	public void setDatamodel(AmiWebDm dm) {
		this.dm = dm;
	}
	@Override
	public String parseConfigFromLayoutConfig(String configText) {
		Map<String, AmiWebDmDef> dmConfigs = AmiWebLayoutHelper.getDmConfigs(AmiWebLayoutHelper.parseJsonSafe(configText, service.getPortletManager()));
		AmiWebDmDef def = CH.getOr(dmConfigs, dm.getDmName(), null);
		return def != null ? service.getLayoutFilesManager().toJson(def.getConfiguration()) : null;
	}
	public String getAdn() {
		return dm.getAmiLayoutFullAliasDotId();
	}
}
