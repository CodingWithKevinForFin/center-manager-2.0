package com.f1.ami.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletManager;

public class AmiWebPanelManager {
	final private AmiWebService service;

	public AmiWebPanelManager(AmiWebService service) {
		this.service = service;
	}
	public List<AmiWebPortlet> getAmiPanels() {
		PortletManager pm = service.getPortletManager();
		Set<String> ids = pm.getPortletIds();
		List<AmiWebPortlet> r = new ArrayList<AmiWebPortlet>();
		for (String id : ids) {
			Portlet p = pm.getPortlet(id);
			if (p instanceof AmiWebPortlet)
				r.add((AmiWebPortlet) p);
		}
		return r;
	}
	public void onInitDone() {
		List<AmiWebPortlet> panels = this.getAmiPanels();
		for (int i = 0; i < panels.size(); i++) {
			AmiWebPortlet panel = panels.get(i);
			if (panel instanceof AmiWebAbstractPortlet) {
				((AmiWebAbstractPortlet) panel).onInitDone();
			}
		}
	}

}
