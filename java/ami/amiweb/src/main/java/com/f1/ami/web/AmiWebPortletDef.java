package com.f1.ami.web;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.utils.CH;

public class AmiWebPortletDef {

	final private Map<String, Object> portletConfig;
	final private Set<String> children;
	private String amiPanelId;
	final private AmiWebPortletBuilder<?> builder;
	final private AmiWebLayoutFile file;
	private String fullAdn;
	private List<String> usedDmAdns;
	private List<Callback> callbacks;

	public AmiWebPortletDef(AmiWebLayoutFile file, AmiWebPortletBuilder<?> builder, Map<String, Object> portletConfig) {
		this.file = file;
		this.builder = builder;
		this.portletConfig = portletConfig;
		this.amiPanelId = (String) CH.getOrThrow(portletConfig, "amiPanelId");
		this.fullAdn = AmiWebUtils.getFullAlias(file.getFullAlias(), amiPanelId);
		if (builder instanceof AmiWebPortletContainerBuilder) {
			final Map<String, Map> childrenByAdn = new HashMap<String, Map>();
			((AmiWebPortletContainerBuilder<?>) builder).extractChildPorletIds(portletConfig, childrenByAdn);
			children = new HashSet<String>(childrenByAdn.keySet());
		} else {
			children = Collections.emptySet();
		}
		if (builder instanceof AmiWebDmPortletBuilder) {
			final Map<String, Map> childrenByAdn = new HashMap<String, Map>();
			this.usedDmAdns = ((AmiWebDmPortletBuilder) builder).extractUsedDmAndTables(portletConfig);
		} else {
			this.usedDmAdns = Collections.emptyList();
		}
		this.callbacks = builder.getCallbacks(portletConfig);
	}
	public List<Callback> getCallbacks() {
		return this.callbacks;
	}
	public AmiWebPortletDef(AmiWebAliasPortlet portlet) {
		this.file = portlet.getService().getLayoutFilesManager().getLayoutByFullAlias(portlet.getAmiLayoutFullAlias());
		this.builder = (AmiWebPortletBuilder<?>) portlet.getManager().getPortletBuilder(portlet.getPortletConfig().getBuilderId());
		this.portletConfig = portlet.getConfiguration();
		this.amiPanelId = portlet.getAmiPanelId();
		this.fullAdn = portlet.getAmiLayoutFullAliasDotId();
		if (builder instanceof AmiWebPortletContainerBuilder) {
			final Map<String, Map> childrenByAdn = new HashMap<String, Map>();
			((AmiWebPortletContainerBuilder<?>) builder).extractChildPorletIds(portletConfig, childrenByAdn);
			children = new HashSet<String>(childrenByAdn.keySet());
		} else {
			children = Collections.emptySet();
		}
		if (builder instanceof AmiWebDmPortletBuilder) {
			final Map<String, Map> childrenByAdn = new HashMap<String, Map>();
			this.usedDmAdns = ((AmiWebDmPortletBuilder) builder).extractUsedDmAndTables(portletConfig);
		} else {
			this.usedDmAdns = Collections.emptyList();
		}
	}
	public AmiWebPortletDef(Map<String, Object> portletConfig) {
		this.portletConfig = portletConfig;
		this.amiPanelId = (String) CH.getOrThrow(portletConfig, "amiPanelId");
		this.children = null;
		this.builder = null;
		this.file = null;
		this.usedDmAdns = null;
	}
	public Set<String> getChildren() {
		return this.children;
	}
	public String getBuilderId() {
		return builder.getPortletBuilderId();
	}
	public Map<String, Object> getPortletConfig() {
		return portletConfig;
	}
	public String getAmiPanelId() {
		return this.amiPanelId;
	}
	public void bind() {

	}
	public AmiWebPortletBuilder<?> getBuilder() {
		return this.builder;
	}

	public int getUsedDmCount() {
		return this.usedDmAdns.size();
	}
	public String getUsedDmAt(int position) {
		return this.usedDmAdns.get(position);
	}
	public void replaceUsedDmAt(int position, String name) {
		((AmiWebDmPortletBuilder) builder).replaceUsedDmAndTable(this.portletConfig, position, name);
	}

	public void removeChild(String amiPanelId) {
		if (!((AmiWebPortletContainerBuilder<?>) builder).removePortletId(portletConfig, amiPanelId))
			throw new RuntimeException("Child not found in '" + this.getAmiPanelId() + "': '" + amiPanelId + "'");
		this.children.remove(amiPanelId);
	}
	public AmiWebLayoutFile getLayoutFile() {
		return this.file;
	}

	@Override
	public String toString() {
		return "PorletDef " + this.amiPanelId;
	}
	public int getChildrenCount() {
		return this.children.size();
	}
	public void setAmiPanelId(String amiPanelId) {
		this.amiPanelId = amiPanelId;
		this.portletConfig.put("amiPanelId", this.amiPanelId);
		this.fullAdn = AmiWebUtils.getFullAlias(file.getFullAlias(), amiPanelId);
	}
	public void replaceChild(String oldPanelId, String nuwPanelId) {
		CH.removeOrThrow(this.children, oldPanelId);
		CH.addOrThrow(this.children, nuwPanelId);
		if (!((AmiWebPortletContainerBuilder<?>) builder).replacePortletId(portletConfig, oldPanelId, nuwPanelId))
			throw new RuntimeException("Child not found in '" + this.getAmiPanelId() + "': '" + oldPanelId + "'");
	}

	public String getFullAdn() {
		return this.fullAdn;
	}

	public static class Callback {

		private Map<String, Object> config;

		public Callback(Map<String, Object> config) {
			this.config = config;
		}

		public String getAmiScript() {
			return (String) config.get("amiscript");
		}

		public List<Map<String, String>> getLinkedVariables() {
			return (List<Map<String, String>>) config.get("linkedVariables");
		}
		public void replaceLinkedVariable(Map<String, String> old2nuw) {
			List<Map<String, String>> linkedVariables = this.getLinkedVariables();
			if (CH.isntEmpty(linkedVariables))
				for (Map<String, String> i : linkedVariables) {
					String nuw = old2nuw.get(i.get("ari"));
					if (nuw != null)
						i.put("ari", nuw);
				}
		}

		public int getLinkedVariablesCount() {
			return CH.size(getLinkedVariables());
		}
		public String getLinkedVariableAri(int pos) {
			return this.getLinkedVariables().get(pos).get("ari");
		}
		public String setLinkedVariableAri(int pos, String ari) {
			return this.getLinkedVariables().get(pos).put("ari", ari);
		}

		public String getName() {
			return (String) config.get("name");
		}
	}

	public static void getCallbacks(Map<String, Object> portletConfig, List<Callback> r) {
		Map<String, Object> callbacks = (Map<String, Object>) portletConfig.get("callbacks");
		if (CH.isntEmpty(callbacks)) {
			List<Map<String, Object>> entries = (List<Map<String, Object>>) callbacks.get("entries");
			if (CH.isntEmpty(entries))
				for (Map<String, Object> m : entries)
					r.add(new AmiWebPortletDef.Callback(m));

		}
	}
}
