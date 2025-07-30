package com.f1.ami.web.dm;

import java.util.Map;

import com.f1.utils.CH;

public class AmiWebDmLinkDef {
	private Map<String, Object> config;
	private String relId;
	private String sourceDmAdn;
	private String targetDmAdn;
	private String sourcePanelAdn;
	private String targetPanelAdn;
	private String title;

	public AmiWebDmLinkDef(Map<String, Object> config) {
		this.config = config;
		this.relId = (String) CH.getOrThrow(config, "relid");
		this.sourceDmAdn = (String) CH.getOrThrow(config, "sdmadn");
		this.targetDmAdn = (String) CH.getOrThrow(config, "tdmadn");
		this.sourcePanelAdn = (String) CH.getOrThrow(config, "spadn");
		this.targetPanelAdn = (String) CH.getOrThrow(config, "tpadn");
		this.title = (String) CH.getOrThrow(config, "title");
	}

	public Map<String, Object> getConfiguration() {
		return config;
	}

	public void setConfiguration(Map<String, Object> config) {
		this.config = config;
	}

	public String getRelId() {
		return relId;
	}

	public void setRelId(String relId) {
		this.relId = relId;
	}

	public String getSourceDmAdn() {
		return sourceDmAdn;
	}

	public void setSourceDmAdn(String sourceDmAdn) {
		this.sourceDmAdn = sourceDmAdn;
	}

	public String getTargetDmAdn() {
		return targetDmAdn;
	}

	public void setTargetDmAdn(String targetDmAdn) {
		this.targetDmAdn = targetDmAdn;
	}

	public String getSourcePanelAdn() {
		return sourcePanelAdn;
	}

	public void setSourcePanelAdn(String sourcePanelAdn) {
		this.sourcePanelAdn = sourcePanelAdn;
	}

	public String getTargetPanelAdn() {
		return targetPanelAdn;
	}

	public void setTargetPanelAdn(String targetPanelAdn) {
		this.targetPanelAdn = targetPanelAdn;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
