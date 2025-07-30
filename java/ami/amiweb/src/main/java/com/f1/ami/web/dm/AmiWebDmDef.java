package com.f1.ami.web.dm;

import java.util.Map;

import com.f1.utils.CH;

public class AmiWebDmDef {
	final public static String TYPE_DMS = "dms";
	final public static String TYPE_DMT = "dmt";
	final private Map<String, Object> config;
	private String dmName;
	private String type;

	public AmiWebDmDef(Map<String, Object> dmConfig) {
		this.config = dmConfig;
		this.dmName = (String) CH.getOrThrow(dmConfig, "lbl");
	}

	public Map<String, Object> getConfiguration() {
		return this.config;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmsName) {
		this.dmName = dmsName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
