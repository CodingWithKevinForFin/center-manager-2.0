package com.f1.ami.web.dm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.portlets.AmiWebAddPanelPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;

public class AmiWebDmEditorsManager {

	private final AmiWebService service;
	private Map<String, AmiWebAddPanelPortlet> dmWizardsByAri;
	private Map<String, AmiWebAddPanelPortlet> dmWizardsByPortletId;

	public AmiWebDmEditorsManager(AmiWebService service) {
		this.service = service;
		this.dmWizardsByAri = new HashMap<String, AmiWebAddPanelPortlet>();
		this.dmWizardsByPortletId = new HashMap<String, AmiWebAddPanelPortlet>();
	}

	private PortletConfig generateConfig() {
		return this.service.getPortletManager().generateConfig();
	}
	public AmiWebAddPanelPortlet getEditorByPortletId(String ari) {
		return this.dmWizardsByPortletId.get(ari);
	}

	public int getEditorsCount() {
		return this.dmWizardsByPortletId.size();
	}
	public Set<String> getEditorsPortletIds() {
		return this.dmWizardsByPortletId.keySet();
	}

	public AmiWebAddPanelPortlet showAddDmPortlet(AmiWebService service, List<AmiWebDatasourceWrapper> l, List<AmiWebDmsImpl> m, List<String> realtimes) {
		//	public AmiWebAddPanelPortlet showAddDmPortlet(AmiWebService service) {
		AmiWebAddPanelPortlet newEditor = new AmiWebAddPanelPortlet(generateConfig(), null, l, m, realtimes);

		Window w = this.service.getDesktop().getDesktop().addChild("Add Datamodel", newEditor);
		this.service.getDesktop().applyEditModeStyle(w);
		this.service.getPortletManager().onPortletAdded(newEditor);

		//The below is not needed, there is no need to add it to dmWizards, because users can't edit a dm that hasn't been added
		String portletId = newEditor.getPortletId();
		this.dmWizardsByPortletId.put(portletId, newEditor);
		return newEditor;
	}

	public AmiWebAddPanelPortlet getDmEditor(String dmAri) {
		return this.dmWizardsByAri.get(dmAri);
	}
	public AmiWebAddPanelPortlet showEditDmPortlet(AmiWebService service, AmiWebDmsImpl amiWebDm) {
		String ari = amiWebDm.getAri();
		AmiWebAddPanelPortlet r = getDmEditor(ari);
		if (r != null) {
			PortletHelper.ensureVisible(r);
			return r;
		}
		for (AmiWebAddPanelPortlet i : this.dmWizardsByPortletId.values()) {
			if (i.getEditedDm() == amiWebDm) {
				PortletHelper.ensureVisible(i);
				return i;
			}
		}

		AmiWebAddPanelPortlet newAddPanelPortlet = new AmiWebAddPanelPortlet(generateConfig(), amiWebDm, false);
		String portletId = newAddPanelPortlet.getPortletId();
		Window w = this.service.getDesktop().getDesktop().addChild("Edit Datamodel", newAddPanelPortlet);

		this.service.getDesktop().applyEditModeStyle(w, 1450, 700);
		this.service.getPortletManager().onPortletAdded(newAddPanelPortlet);

		this.dmWizardsByAri.put(ari, newAddPanelPortlet);
		this.dmWizardsByPortletId.put(portletId, newAddPanelPortlet);
		return newAddPanelPortlet;
	}

	public void onPortletClosed(AmiWebAddPanelPortlet oldPortlet) {
		this.removeEditor(oldPortlet);
	}

	private void removeEditor(AmiWebAddPanelPortlet wiz) {
		String ari = wiz.getEditedDmAri();
		if (this.dmWizardsByAri.containsKey(ari))
			this.dmWizardsByAri.remove(ari);
		String portletId = wiz.getPortletId();
		if (this.dmWizardsByPortletId.containsKey(portletId))
			this.dmWizardsByPortletId.remove(portletId);

	}

	public void clear() {
		this.dmWizardsByAri.clear();
		this.dmWizardsByPortletId.clear();
	}

}
