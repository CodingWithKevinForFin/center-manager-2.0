package com.f1.ami.web.form;

import java.util.Map;

import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.form.AmiWebEditableFormPortletManager.Guide;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;

public class AmiWebEditableFormPortlet extends FormPortlet {

	private AmiWebEditableFormPortletManager editor;
	private boolean editorInit;
	private boolean inEditorMode;
	private AmiWebService service;

	public AmiWebEditableFormPortlet(PortletConfig config) {
		super(config);
		this.service = AmiWebUtils.getService(getManager());
		this.editor = new AmiWebEditableFormPortletManager(this);
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if (!this.editor.handleCallback(callback, attributes))
			super.handleCallback(callback, attributes);
	}

	@Override
	public void initJs() {
		editorInit = false;
		super.initJs();
		StringBuilder js = getManager().getPendingJs();
		js.append("new FormEditor(");
		callJsFunction("getForm").close();
		js.append(");");
		this.editorInit = true;
		callJsSetSize();
		this.editor.initJs();
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (this.getVisible())
			this.editor.drainJavascript();
	}

	@Override
	public void flagLayoutChanged() {
		super.flagLayoutChanged();
		if (inEditorMode)
			this.editor.onLayoutChanged();
	}

	@Override
	public void onFieldChanged(FormPortletField<?> field) {
		super.onFieldChanged(field);
		if (inEditorMode && field.hasChanged(FormPortletField.MASK_POSITIONS))
			this.editor.onLayoutChanged();
	}

	//	@Override
	//	protected void setSizeChanged(boolean sizeChanged) {
	//		super.setSizeChanged(sizeChanged);
	//		if (sizeChanged)
	//			this.editor.onLayoutChanged();
	//	}

	public boolean getInEditorMode() {
		return this.inEditorMode;
	}

	public void setInEditorMode(boolean b) {
		this.inEditorMode = b;
		this.editor.setIsVisible(this.inEditorMode && service.getDesktop().getInEditMode());
		service.getDesktop().flagUpdateWindowLinks();
	}
	public void onEditModeChanged() {
		setInEditorMode(service.getDesktop().getInEditMode());
	}

	public AmiWebEditableFormPortletManager getEditableManager() {
		return editor;
	}

	public Guide addHorizontalGuide(int offset) {
		return addHorizontalGuide(offset, (byte) -1);
	}
	public Guide addHorizontalGuide(int offset, byte alignment) {
		return addGuide(offset, alignment, false);
	}
	public Guide addVerticalGuide(int offset) {
		return addVerticalGuide(offset, (byte) -1);
	}
	public Guide addVerticalGuide(int offset, byte alignment) {
		return addGuide(offset, alignment, true);
	}
	private Guide addGuide(int offset, byte alignment, boolean isVertical) {
		Guide guide = new Guide(this.editor.getNextGuideId(), isVertical);
		guide.setForm(this);
		guide.updateFormSize();
		if (alignment == -1) {
			guide.setLocation(offset);
		} else {
			guide.setLocation(offset, alignment);
		}
		this.editor.addGuide(guide);
		return guide;
	}

	@Override
	public void flagPendingAjax() {
		super.flagPendingAjax();
	}

}
