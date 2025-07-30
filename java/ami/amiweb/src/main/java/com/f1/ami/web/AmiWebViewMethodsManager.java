package com.f1.ami.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebViewMethodsManager implements PortletListener {
	final private AmiWebService service;
	private Map<LinkedHasherSet<ParamsDefinition>, AmiWebViewMethodPortlet> editors;

	public AmiWebViewMethodsManager(AmiWebService service) {
		this.service = service;
		this.editors = new HasherMap<LinkedHasherSet<ParamsDefinition>, AmiWebViewMethodPortlet>();
	}

	public AmiWebViewMethodPortlet getEditorByAri(LinkedHasherSet<ParamsDefinition> defs) {
		return this.editors.get(defs);
	}

	public AmiWebViewMethodPortlet showEditor(String layoutAlias, List<ParamsDefinition> list) {
		LinkedHasherSet<ParamsDefinition> defs = new LinkedHasherSet<ParamsDefinition>(ParamsDefinition.HASHER_DEF_IGNORE_RETURNTYPE);
		defs.addAll(list);
		if (this.editors.containsKey(defs)) {
			AmiWebViewMethodPortlet editor = this.getEditorByAri(defs);
			PortletHelper.ensureVisible(editor);
			return editor;
		} else {
			AmiWebViewMethodPortlet newEditor = new AmiWebViewMethodPortlet(service.getPortletManager().generateConfig(), service, layoutAlias, defs);
			int c = 1 + SH.getCount("\n", newEditor.getText());
			StringBuilder name = new StringBuilder();
			if (list.size() == 1)
				name.append(list.get(0));
			else {

				for (int i = 0; i < list.size(); i++) {
					if (i > 0)
						name.append(", ");
					name.append(list.get(i).getMethodName());
				}
			}
			newEditor.setTitle(SH.ddd(name.toString(), 50));
			Window w = this.service.getDesktop().getDesktop().addChild("View Method - " + newEditor.getTitle(), newEditor);

			this.service.getDesktop().applyEditModeStyle(w);
			this.service.getPortletManager().onPortletAdded(newEditor);
			newEditor.addPortletListener(this);
			this.editors.put(defs, newEditor);
			w.setHeight(Math.min(c * 16, w.getHeight()));

			return newEditor;

		}

	}

	public int getEditorsCount() {
		return this.editors.size();
	}

	private void removeEditor(AmiWebViewMethodPortlet editor) {
		LinkedHasherSet<ParamsDefinition> ari = editor.getParamDefinitions();
		if (this.editors.containsKey(ari))
			this.editors.remove(ari);
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {

	}

	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (oldPortlet instanceof AmiWebViewMethodPortlet) {
			AmiWebViewMethodPortlet editor = (AmiWebViewMethodPortlet) oldPortlet;
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

	public Collection<AmiWebViewMethodPortlet> getEditors() {
		return this.editors.values();
	}

	public void recompileAmiScript() {
		for (AmiWebViewMethodPortlet i : this.editors.values())
			i.recompileAmiScript();
	}

}
