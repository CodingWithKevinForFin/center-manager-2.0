package com.f1.ami.web;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public class AmiWebMethodsPortlet extends GridPortlet implements AmiWebSpecialPortlet, FormPortletListener, ConfirmDialogListener {

	final private TabPortlet tabs;
	final private Map<String, AmiWebMethodPortlet> methodPortlets = new LinkedHashMap<String, AmiWebMethodPortlet>();
	final private FormPortlet buttonsForm;
	final private FormPortletButton submitButton;
	final private FormPortletButton cancelButton;
	final private FormPortletButton applyButton;
	final private FormPortletButton continueButton;
	final private FormPortletButton stepoverButton; //add
	final private AmiWebService service;

	//add
	private byte debugState;

	public AmiWebMethodsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		addChild(tabs = new TabPortlet(generateConfig()));
		tabs.setIsCustomizable(false);
		for (String s : service.getLayoutFilesManager().getFullAliasesByPriority()) {
			AmiWebLayoutFile lo = service.getLayoutFilesManager().getLayoutByFullAlias(s);
			if (lo.getDuplicateStatus() == AmiWebLayoutFile.SECONDARY)
				continue;
			AmiWebMethodPortlet child = new AmiWebMethodPortlet(generateConfig(), this, service.getDesktop(), s);
			methodPortlets.put(s, child);
			Tab tab = tabs.addChild("".equals(s) ? "<root>" : s, child);
			child.setTab(tab);
		}
		this.buttonsForm = addChild(new FormPortlet(generateConfig()), 0, 1);
		this.setRowSize(1, 35);
		this.submitButton = this.buttonsForm.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.buttonsForm.addButton(new FormPortletButton("Cancel"));
		this.applyButton = this.buttonsForm.addButton(new FormPortletButton("Apply"));
		this.continueButton = new FormPortletButton("Continue");
		this.stepoverButton = new FormPortletButton("Step Over");//add
		this.debugState = AmiWebBreakpointManager.DEBUG_UNINITIALIZED;//add, init flag
		this.buttonsForm.addFormPortletListener(this);
		if (this.service.getBreakpointManager().hasDebugs()) {
			addContinueButton();
			addStepoverButton();
		}

	}

	public boolean hasPendingChanges() {
		for (AmiWebMethodPortlet i : this.methodPortlets.values())
			if (i.hasPendingChanges())
				return true;
		return false;
	}

	public boolean apply(AmiDebugManager debugManager, StringBuilder errorSink) {
		for (AmiWebMethodPortlet i : this.methodPortlets.values())
			if (i.hasPendingChanges())
				if (!i.apply(debugManager, errorSink)) {
					this.tabs.bringToFront(i.getPortletId());
					return false;
				}
		return true;

	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == submitButton) {
			if (apply(null, null))
				close();
		} else if (button == cancelButton) {
			for (AmiWebMethodPortlet i : this.methodPortlets.values()) {
				if (i.hasChanged()) {
					getManager().showDialog("Cancel",
							new ConfirmDialogPortlet(generateConfig(), "Abandon changes?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("CLOSE"));
					return;
				}
			}
			close();
		} else if (button == applyButton) {
			apply(null, null);
		} else if (button == continueButton) {
			this.debugState = AmiWebBreakpointManager.DEBUG_CONTINUE;
			this.service.getBreakpointManager().continueDebug();
		} else if (button == stepoverButton) { //ADD logic here
			this.debugState = AmiWebBreakpointManager.DEBUG_STEP_OVER;
			this.service.getBreakpointManager().continueDebug();
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CLOSE".equals(source.getCallback())) {
			if (OH.eq(id, ConfirmDialogPortlet.ID_YES)) {
				for (AmiWebMethodPortlet i : this.methodPortlets.values())
					i.getAmiScriptEditor().setValue(i.getOrigAmiScript());
				for (AmiWebMethodPortlet i : this.methodPortlets.values())
					i.revertChanges();
				close();
			}
		}
		return true;
	}

	public boolean isBreakpoint(String layoutAlias, DerivedCellCalculator statment) {
		AmiWebMethodPortlet t = methodPortlets.get(layoutAlias);
		return t != null && t.isBreakpoint(statment);
	}

	public void clearHighlights() {
		for (AmiWebMethodPortlet i : this.methodPortlets.values())
			i.clearHighlights();
	}

	public void addContinueButton() {
		if (!this.buttonsForm.hasButton(this.continueButton.getId()))
			this.buttonsForm.addButton(this.continueButton);
	}

	public void removeContinueButton() {
		if (this.buttonsForm.hasButton(this.continueButton.getId()))
			this.buttonsForm.removeButton(this.continueButton);
	}

	//add
	public void addStepoverButton() {
		if (!this.buttonsForm.hasButton(this.stepoverButton.getId()))
			this.buttonsForm.addButton(this.stepoverButton);
	}

	//add
	public void removeStepoverButton() {
		if (this.buttonsForm.hasButton(this.stepoverButton.getId()))
			this.buttonsForm.removeButton(this.stepoverButton);
	}

	public AmiWebMethodPortlet getMethodPortlet(String layout) {
		return this.methodPortlets.get(layout);
	}
	public void recompileAmiScript() {
		for (AmiWebMethodPortlet i : this.methodPortlets.values()) {
			i.recompileAmiScript();
		}
	}
	public TabPortlet getTabs() {
		return tabs;
	}

	public byte getDebugState() {
		return debugState;
	}

	public void rebuildUsagesTree() {
		for (AmiWebMethodPortlet i : this.methodPortlets.values())
			i.rebuildUsagesTree();
	}
}
