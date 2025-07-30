package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;

public class AmiWebCallbackEditorsManager implements PortletListener {

	private final AmiWebService service;
	private Map<String, AmiWebEditAmiScriptCallbackDialogPortlet> dmWizardsByAri;
	private Map<String, AmiWebEditAmiScriptCallbackDialogPortlet> dmWizardsByPortletId;

	public AmiWebCallbackEditorsManager(AmiWebService service) {
		this.service = service;
		this.dmWizardsByAri = new HashMap<String, AmiWebEditAmiScriptCallbackDialogPortlet>();
		this.dmWizardsByPortletId = new HashMap<String, AmiWebEditAmiScriptCallbackDialogPortlet>();
	}

	private PortletConfig generateConfig() {
		return this.service.getPortletManager().generateConfig();
	}
	public AmiWebEditAmiScriptCallbackDialogPortlet getEditorByPortletId(String ari) {
		return this.dmWizardsByPortletId.get(ari);
	}

	public int getEditorsCount() {
		return this.dmWizardsByPortletId.size();
	}
	public Set<String> getEditorsPortletIds() {
		return this.dmWizardsByPortletId.keySet();
	}

	public AmiWebEditAmiScriptCallbackDialogPortlet showEditDmPortlet(AmiWebService service, AmiWebAmiScriptCallbacks callbacks) {
		String ari = callbacks.getThis().getAri();
		if (this.dmWizardsByAri.containsKey(ari)) {
			AmiWebEditAmiScriptCallbackDialogPortlet wiz = this.dmWizardsByAri.get(ari);
			//Either or
			PortletHelper.ensureVisible(wiz);
			return wiz;
		} else {
			AmiWebEditAmiScriptCallbackDialogPortlet newAddPanelPortlet = new AmiWebEditAmiScriptCallbackDialogPortlet(generateConfig(), callbacks);
			String portletId = newAddPanelPortlet.getPortletId();
			Window w = this.service.getDesktop().getDesktop().addChild("Amiscript Callbacks", newAddPanelPortlet);

			this.service.getDesktop().applyEditModeStyle(w, 1450, 700);
			this.service.getPortletManager().onPortletAdded(newAddPanelPortlet);

			newAddPanelPortlet.addPortletListener(this);
			this.dmWizardsByAri.put(ari, newAddPanelPortlet);
			this.dmWizardsByPortletId.put(portletId, newAddPanelPortlet);
			return newAddPanelPortlet;
		}

	}

	@Override
	public void onPortletAdded(Portlet newPortlet) {

	}
	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (oldPortlet instanceof AmiWebEditAmiScriptCallbackDialogPortlet) {
			AmiWebEditAmiScriptCallbackDialogPortlet wiz = (AmiWebEditAmiScriptCallbackDialogPortlet) oldPortlet;
			this.removeEditor(wiz);
		}
	}
	private void removeEditor(AmiWebEditAmiScriptCallbackDialogPortlet wiz) {
		String ari = wiz.getCallbacks().getThis().getAri();
		if (this.dmWizardsByAri.containsKey(ari))
			this.dmWizardsByAri.remove(ari);
		String portletId = wiz.getPortletId();
		if (this.dmWizardsByPortletId.containsKey(portletId))
			this.dmWizardsByPortletId.remove(portletId);

	}

	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {

	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {

	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {

	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {

	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {

	}
	@Override
	public void onLocationChanged(Portlet portlet) {

	}

	public void clear() {
		this.dmWizardsByAri.clear();
		this.dmWizardsByPortletId.clear();
	}

}
