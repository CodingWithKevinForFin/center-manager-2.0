package com.f1.ami.web.form;

import java.util.Map;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebEditAmiScriptCallbacksPortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextEditField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;

public class EditWebButtonForm extends GridPortlet implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener {

	/**
	 * 
	 */
	private final FormPortletTextField idField;
	private final FormPortletTextField nameField;
	private final FormPortletButton submit;
	//	private final AmiWebFormPortletAmiScriptField scriptField;
	private final FormPortletTextField buttonStyleField;
	private String editId;
	private AmiWebButton button;
	private AmiWebQueryFormPortlet queryFormPortlet;
	private FormPortlet form;
	private AmiWebEditAmiScriptCallbacksPortlet callbacks;

	public EditWebButtonForm(AmiWebQueryFormPortlet queryFormPortlet, PortletConfig config, AmiWebButton button) {
		super(config);
		this.form = new FormPortlet(this.generateConfig());
		this.queryFormPortlet = queryFormPortlet;
		AmiWebAmiScriptCallbacks script;
		if (button != null) {
			script = button.getScript();
		} else {
			script = new AmiWebButton(null, null, AmiWebUtils.getService(this.getManager()), this.queryFormPortlet).getScript();
			script.setAmiLayoutAlias(queryFormPortlet.getAmiLayoutFullAlias());
		}
		this.callbacks = new AmiWebEditAmiScriptCallbacksPortlet(generateConfig(), script);
		this.addChild(this.callbacks, 0, 0);
		this.addChild(this.form, 0, 1);
		this.setRowSize(1, 80);
		this.idField = form.addField(new FormPortletTextField("Button ID:"));
		this.nameField = form.addField(new FormPortletTextField("Name:"));
		this.buttonStyleField = form.addField(new FormPortletTextField("Button Style:"));
		this.idField.setLeftTopWidthHeightPx(80, 4, 150, 22);
		this.nameField.setLeftTopWidthHeightPx(300, 4, 150, 22);
		this.buttonStyleField.setLeftTopWidthHeightPx(575, 4, 150, 22);

		//		this.scriptField = new AmiWebFormPortletAmiScriptField("Script:", getManager(), queryFormPortlet.getAmiLayoutFullAlias());
		//		this.scriptField.addVariable("layout", AmiWebLayoutFile.class);
		//		this.scriptField.addVariable("session", AmiWebService.class);
		//		this.scriptField.addVariable("this", AmiWebButton.class);
		//		this.scriptField.setHeight(FormPortletField.HEIGHT_STRETCH);
		this.buttonStyleField.setVisible(true);
		this.submit = this.form.addButton(new FormPortletButton("Submit"));
		this.button = button;
		if (button != null) {
			this.editId = button.getId();
			this.idField.setValue(button.getId());
			this.idField.setDisabled(true);
			this.nameField.setValue(button.getName());
			//			this.scriptField.setValue(button.getScript());
			this.buttonStyleField.setValue(button.getButton().getCssStyle());
		}
		this.form.addFormPortletListener(this);
		this.form.addMenuListener(this);
		this.form.setMenuFactory(this);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (idField.getValue() == null || nameField.getValue() == null || "".equals(idField.getValue()) || "".equals(nameField.getValue())) {
			getManager().showAlert("Button must have ID and NAME");
			return;
		}
		if (!callbacks.apply())
			return;
		queryFormPortlet.setDialogWidth(this.getWidth());
		queryFormPortlet.setDialogHeight(this.getHeight());
		int positionLeft = PortletHelper.getAbsoluteLeft(this);
		int positionTop = PortletHelper.getAbsoluteTop(this);
		queryFormPortlet.setDialogLeft(positionLeft);
		queryFormPortlet.setDialogTop(positionTop);
		StringBuilder errorSink = new StringBuilder();
		AmiWebButton b = queryFormPortlet.addAmiWebButton(this.editId, this.idField.getValue(), this.nameField.getValue(), this.buttonStyleField.getValue(), errorSink);
		if (this.button != null)
			this.button.removeFromDomManager();
		b.updateAri();
		b.addToDomManager();

		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
		} else {
			this.callbacks.applyTo(b.getScript(), null);
			this.callbacks.close();
			this.close();
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		//		if (field == scriptField)
		//			((AmiWebFormPortletAmiScriptField) field).onSpecialKeyPressed(formPortlet, field, keycode, mask, cursorPosition);

	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (AmiWebMenuUtils.processContextMenuAction(this.queryFormPortlet.getService(), action, (FormPortletTextEditField) node))
			return;
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebMenuUtils.createOperatorsMenu(r, queryFormPortlet.getService(), this.queryFormPortlet.getAmiLayoutFullAlias());
		AmiWebMenuUtils.createMemberMethodMenu(r, queryFormPortlet.getService(), this.queryFormPortlet.getAmiLayoutFullAlias());
		boolean hasSpecial = false;
		return r;
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 1000;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 500;
	}
	public AmiWebEditAmiScriptCallbacksPortlet getCallbacksEditor() {
		return this.callbacks;
	}

}
