package com.f1.ami.web.form;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebInnerDesktopPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.DesktopPortlet.Window;

public class AmiWebQueryFormEditorsManager implements PortletListener {
	final private AmiWebService service;
	//Map of field ari to editor
	private Map<String, AmiWebQueryFieldWizardPortlet> fieldWizardsByAri;
	private Map<String, AmiWebQueryFieldWizardPortlet> fieldWizardsByPortletId;
	private boolean dialogArrowNeedsUpdate;

	public AmiWebQueryFormEditorsManager(AmiWebService service) {
		this.fieldWizardsByAri = new HashMap<String, AmiWebQueryFieldWizardPortlet>();
		this.fieldWizardsByPortletId = new HashMap<String, AmiWebQueryFieldWizardPortlet>();
		this.service = service;
	}
	private PortletConfig generateConfig() {
		return this.service.getPortletManager().generateConfig();
	}

	private PortletManager getManager() {
		return this.service.getPortletManager();
	}
	public AmiWebQueryFieldWizardPortlet getEditorByAri(String ari) {
		return this.fieldWizardsByAri.get(ari);
	}
	public AmiWebQueryFieldWizardPortlet getEditorByPortletId(String portletId) {
		return this.fieldWizardsByPortletId.get(portletId);
	}
	public void switchEditorField(String oldAri, String newAri) {
		//Changes the target field being edited to that of new ari
		if (this.fieldWizardsByAri.containsKey(oldAri)) {
			AmiWebQueryFieldWizardPortlet oldEditor = this.fieldWizardsByAri.remove(oldAri);
			if (oldEditor != null)
				this.fieldWizardsByAri.put(newAri, oldEditor);
			this.updateDialogArrow(oldEditor);
		}
	}

	public void flagUpdateDialogArrow() {
		if (this.dialogArrowNeedsUpdate)
			return;
		if (this.fieldWizardsByAri.size() == 0)
			return;
		this.dialogArrowNeedsUpdate = true;
	}
	public int getEditorsCount() {
		return this.fieldWizardsByPortletId.size();
	}
	public Set<String> getEditorsPortletIds() {
		return fieldWizardsByPortletId.keySet();
	}

	public boolean isEditor(Portlet portlet) {
		return portlet instanceof AmiWebQueryFieldWizardPortlet;
	}
	private void updateDialogArrow(AmiWebQueryFieldWizardPortlet editor) {

		AmiWebQueryFieldWizardPortlet wiz = (AmiWebQueryFieldWizardPortlet) editor;
		QueryField<?> queryField = wiz.getEditedFieldForFieldEditor();
		AmiWebQueryFormPortlet form = queryField.getForm();
		if (queryField == null || form == null) {
			this.dialogArrowNeedsUpdate = false;
			return;
		}
		this.dialogArrowNeedsUpdate = false;

		//		int outerLeft = inner.getActiveWindow().getOuterLeft();
		//		int outerTop = inner.getActiveWindow().getOuterTop();
		//		int outerRight = inner.getActiveWindow().getOuterRight();
		//		int outerBottom = inner.getActiveWindow().getOuterBottom();
		Integer fieldHorizontalCenterPosPx = queryField.getFieldHorizontalCenterPosPx();
		Integer fieldVerticalCenterPosPx = queryField.getFieldVerticalCenterPosPx();
		Integer formx = PortletHelper.getAbsoluteLeft(form);
		Integer formy = PortletHelper.getAbsoluteTop(form);

		Integer px = formx + fieldHorizontalCenterPosPx;
		Integer py = formy + fieldVerticalCenterPosPx;
		Integer dleft = PortletHelper.getAbsoluteLeft(wiz) - 6;
		Integer dtop = PortletHelper.getAbsoluteTop(wiz) - PortletHelper.getAbsoluteTop(wiz.getParent());
		Integer dright = dleft + wiz.getWidth() + 6 + 6;
		Integer dbottom = dtop + wiz.getHeight() + 6 + 22;

		StringBuilder pendingJs = getManager().getPendingJs();
		JsFunction js = new JsFunction(pendingJs, null, "amiDialogArrow");
		js.addParam(px);
		js.addParam(py);
		js.addParam(dleft);
		js.addParam(dtop);
		js.addParam(dright);
		js.addParam(dbottom);
		js.end();

	}
	public void updateDialogArrow() {
		if (this.fieldWizardsByAri.size() == 0) {
			this.dialogArrowNeedsUpdate = false;
			return;
		}
		Portlet focusedPortlet = this.service.getPortletManager().getFocusedPortlet();
		AmiWebInnerDesktopPortlet inner = null;

		if (focusedPortlet instanceof AmiWebQueryFieldWizardPortlet) {
			inner = (AmiWebInnerDesktopPortlet) focusedPortlet.getParent();
		} else if (focusedPortlet instanceof AmiWebInnerDesktopPortlet) {
			inner = (AmiWebInnerDesktopPortlet) focusedPortlet;
			Window activeWindow = inner.getActiveWindow();
			if (activeWindow == null) {
				this.dialogArrowNeedsUpdate = false;
				return;
			}

			focusedPortlet = activeWindow.getPortlet();
		}

		if (focusedPortlet instanceof AmiWebQueryFieldWizardPortlet) {
			this.updateDialogArrow((AmiWebQueryFieldWizardPortlet) focusedPortlet);
		} else
			this.dialogArrowNeedsUpdate = false;
	}

	// 1 Add from button form
	public void showAddNewFieldEditor(AmiWebQueryFormPortlet form, String editorTypeId) {
		AmiWebQueryFieldWizardPortlet newEditor = new AmiWebQueryFieldWizardPortlet(generateConfig(), form, editorTypeId);
		Window w = this.service.getDesktop().getDesktop().addChild("Add Field", newEditor);
		newEditor.startEdit();
		this.service.getDesktop().applyEditModeStyle(w);
		this.service.getPortletManager().onPortletAdded(newEditor);

		newEditor.addPortletListener(this);
		this.fieldWizardsByAri.put(newEditor.getEditedFieldAriForFieldEditor(), newEditor);
		this.fieldWizardsByPortletId.put(newEditor.getPortletId(), newEditor);

	}
	// 2 Add from field menu
	public void showAddNewFieldEditor(AmiWebQueryFormPortlet form, String editorTypeId, int x, int y) {
		AmiWebQueryFieldWizardPortlet newEditor = new AmiWebQueryFieldWizardPortlet(generateConfig(), form, editorTypeId, x, y, true);
		Window w = this.service.getDesktop().getDesktop().addChild("Add Field", newEditor);
		newEditor.startEdit();
		this.service.getDesktop().applyEditModeStyle(w);
		this.service.getPortletManager().onPortletAdded(newEditor);
		form.getEditableForm().setInEditorMode(true);

		newEditor.addPortletListener(this);
		this.fieldWizardsByAri.put(newEditor.getEditedFieldAriForFieldEditor(), newEditor);
		this.fieldWizardsByPortletId.put(newEditor.getPortletId(), newEditor);
	}
	// 3 Edit existing field
	public AmiWebQueryFieldWizardPortlet showEditExistingFieldEditor(AmiWebQueryFormPortlet form, QueryField<?> field) {
		String ari = field.getAri();
		if (this.fieldWizardsByAri.containsKey(ari)) {
			AmiWebQueryFieldWizardPortlet wiz = this.fieldWizardsByAri.get(ari);
			PortletHelper.ensureVisible(wiz);
			return wiz;
		} else {

			AmiWebQueryFieldWizardPortlet newEditor = new AmiWebQueryFieldWizardPortlet(generateConfig(), form, field);
			Window w = this.service.getDesktop().getDesktop().addChild("Edit Field", newEditor);
			newEditor.startEdit();
			this.service.getDesktop().applyEditModeStyle(w);
			this.service.getPortletManager().onPortletAdded(newEditor);
			//			form.getEditableForm().setInEditorMode(true);

			newEditor.addPortletListener(this);
			this.fieldWizardsByAri.put(ari, newEditor);
			this.fieldWizardsByPortletId.put(newEditor.getPortletId(), newEditor);
			return newEditor;
		}
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {

	}
	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		if (oldPortlet instanceof AmiWebQueryFieldWizardPortlet) {
			AmiWebQueryFieldWizardPortlet wiz = (AmiWebQueryFieldWizardPortlet) oldPortlet;
			this.removeEditor(wiz);
		}

	}

	private void removeEditor(AmiWebQueryFieldWizardPortlet wiz) {
		String ari = wiz.getEditedFieldAriForFieldEditor();
		if (ari != null && this.fieldWizardsByAri.containsKey(ari))
			this.fieldWizardsByAri.remove(ari);
		String portletId = wiz.getPortletId();
		if (this.fieldWizardsByPortletId.containsKey(portletId))
			this.fieldWizardsByPortletId.remove(portletId);
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
	public boolean isDialogArrowNeedsUpdate() {
		return dialogArrowNeedsUpdate;
	}
	public void clear() {
		this.fieldWizardsByAri.clear();
		this.fieldWizardsByPortletId.clear();
	}

}
