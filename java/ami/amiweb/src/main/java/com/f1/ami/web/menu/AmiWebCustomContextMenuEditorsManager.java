package com.f1.ami.web.menu;

import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebService;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.utils.concurrent.HasherMap;

public class AmiWebCustomContextMenuEditorsManager implements PortletListener {
	final private AmiWebService service;
	private Map<String, AmiWebCustomContextMenuSettingsPortlet> editors;

	public AmiWebCustomContextMenuEditorsManager(AmiWebService service) {
		this.service = service;
		this.editors = new HasherMap<String, AmiWebCustomContextMenuSettingsPortlet>();
	}

	public AmiWebCustomContextMenuSettingsPortlet getEditorByAri(String ari) {
		return this.editors.get(ari);
	}

	public AmiWebCustomContextMenuSettingsPortlet showEditor(String ari) {
		if (this.editors.containsKey(ari)) {
			AmiWebCustomContextMenuSettingsPortlet editor = this.getEditorByAri(ari);
			PortletHelper.ensureVisible(editor);
			return editor;
		} else {
			AmiWebDomObject obj = service.getDomObjectsManager().getManagedDomObject(ari);
			if (obj == null)
				return null;
			if (!(obj instanceof AmiWebCustomContextMenu))
				return null;
			AmiWebCustomContextMenu menu = (AmiWebCustomContextMenu) obj;
			//			AmiWebDomObject targetPortlet = menu.getTargetPortlet();

			AmiWebCustomContextMenuManager manager = menu.getOwner();
			AmiWebCustomContextMenuSettingsPortlet newEditor = new AmiWebCustomContextMenuSettingsPortlet(service.getPortletManager().generateConfig(), menu, manager);
			Window w = this.service.getDesktop().getDesktop().addChild("Edit Custom Context Menu", newEditor);
			this.service.getDesktop().applyEditModeStyle(w);
			this.service.getPortletManager().onPortletAdded(newEditor);
			newEditor.addPortletListener(this);
			this.editors.put(ari, newEditor);

			return newEditor;

		}

	}

	public int getEditorsCount() {
		return this.editors.size();
	}

	public Set<String> getEditorIds() {
		return this.editors.keySet();
	}

	private void removeEditor(AmiWebCustomContextMenuSettingsPortlet editor) {
		String ari = editor.getEditedAri();
		if (this.editors.containsKey(ari))
			this.editors.remove(ari);
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {

	}

	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (oldPortlet instanceof AmiWebCustomContextMenuSettingsPortlet) {
			AmiWebCustomContextMenuSettingsPortlet editor = (AmiWebCustomContextMenuSettingsPortlet) oldPortlet;
			this.removeEditor(editor);
		}
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

	public void addEditor(AmiWebCustomContextMenuManager targetPortlet) {
		AmiWebCustomContextMenuSettingsPortlet newEditor = new AmiWebCustomContextMenuSettingsPortlet(this.service.getPortletManager().generateConfig(), null, targetPortlet);
		String ari = newEditor.getEditedAri();
		Window w = this.service.getDesktop().getDesktop().addChild("Edit Custom Context Menu", newEditor);
		this.service.getDesktop().applyEditModeStyle(w);
		this.service.getPortletManager().onPortletAdded(newEditor);
		newEditor.addPortletListener(this);
		this.editors.put(ari, newEditor);
	}

	public void clear() {
		this.editors.clear();
	}

}
