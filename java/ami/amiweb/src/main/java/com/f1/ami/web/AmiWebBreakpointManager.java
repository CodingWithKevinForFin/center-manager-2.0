package com.f1.ami.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.web.amiscript.AmiWebAmiScriptDerivedCellParser.AmiWebDeclaredMethodFactory;
import com.f1.ami.web.amiscript.AmiWebScriptRunner;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.BreakpointManager;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMethod;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebBreakpointManager implements BreakpointManager {

	private AmiWebService service;
	private boolean canHaveBreakpoints = false;
	private AmiWebDesktopPortlet desktop;

	//add constants for debug actions: step into, step over, step out, step to cursor
	public static final byte DEBUG_CONTINUE = 1;
	public static final byte DEBUG_STEP_OVER = 2;
	public static final byte DEBUG_STEP_TO_CURSOR = 3;
	public static final byte DEBUG_UNINITIALIZED = -1;

	private Map<AmiWebScriptRunner, AmiWebBreakpointEditor> editorsWithBreakpoints = new HashMap<AmiWebScriptRunner, AmiWebBreakpointEditor>();
	private AmiWebBreakpointEditor currentEditor;
	private DerivedCellCalculator lastStatement = null;

	public AmiWebBreakpointManager(AmiWebService service) {
		this.service = service;
	}
	public void setDesktop(AmiWebDesktopPortlet desktop) {
		this.desktop = desktop;
		this.canHaveBreakpoints = !this.desktop.getIsLocked();
	}
	public AmiWebService getService() {
		return this.service;
	}
	@Override
	public boolean isBreakpoint(CalcFrameStack sf, DerivedCellCalculator statment) {
		if (statment == lastStatement) {
			lastStatement = null;
			return false;
		}
		lastStatement = null;

		this.currentEditor = null;
		CalcFrameStack t = sf;
		while (t != null) {
			if (t instanceof AmiCalcFrameStack) {
				AmiCalcFrameStack executeInstance = (AmiCalcFrameStack) t;
				if (executeInstance.getThis() instanceof AmiWebDomObject) {
					AmiWebEditAmiScriptCallbackPortlet editor = service.findEditor((AmiWebDomObject) executeInstance.getThis(), executeInstance.getCallbackName());
					if (editor != null && editor.isBreakpoint(statment)) {
						this.currentEditor = editor;
						this.lastStatement = statment;
						return true;
					}
				}
				return false;
			}
			if (sf.getCalc() instanceof DerivedCellCalculatorMethod) {
				DerivedCellCalculatorMethod m = (DerivedCellCalculatorMethod) sf.getCalc();
				if (m.getMethodFactory() instanceof AmiWebDeclaredMethodFactory) {
					AmiWebDeclaredMethodFactory f = (AmiWebDeclaredMethodFactory) m.getMethodFactory();
					AmiWebMethodsPortlet methodsPortlet = this.service.getDesktop().getMethodPortlet();
					if (methodsPortlet != null && methodsPortlet.isBreakpoint(f.getLayoutAlias(), statment)) {
						currentEditor = methodsPortlet.getMethodPortlet(f.getLayoutAlias());
						this.lastStatement = statment;
						return true;
					}
					return false;
				}
				return false;
			}
			t = t.getParent();
		}
		return false;
	}

	public void clearHighlights() {
		AmiWebMethodsPortlet methodsPortlet = this.service.getDesktop().getMethodPortlet();
		if (methodsPortlet != null)
			methodsPortlet.clearHighlights();

	}

	public void onStatus(AmiWebScriptRunner runner, byte state) {
		if (state == AmiWebScriptRunner.STATE_DEBUG) {
			if (this.currentEditor != null) {
				this.currentEditor.showDebug(runner);
				this.editorsWithBreakpoints.put(runner, currentEditor);
				currentEditor = null;
			}
		} else {
			AmiWebBreakpointEditor editor = this.editorsWithBreakpoints.remove(runner);
			if (this.editorsWithBreakpoints.isEmpty()) {
				AmiWebMethodsPortlet methodsPortlet = this.service.getDesktop().getMethodPortlet();
				if (methodsPortlet != null) {
					methodsPortlet.removeContinueButton();
					methodsPortlet.removeStepoverButton();
				}
			}
		}

	}

	//called in checking custom method, // TODO
	public void continueDebug() {
		clearHighlights();
		for (AmiWebScriptRunner i : CH.l(this.editorsWithBreakpoints.keySet()))
			continueDebug(i);
	}

	public void continueDebug(AmiWebScriptRunner i) {
		AmiWebBreakpointEditor editor = this.editorsWithBreakpoints.remove(i);
		if (editor != null)
			editor.clearHighlights();
		i.setState(AmiWebScriptRunner.STATE_RESPONSE_READY);
	}

	public boolean hasDebugs() {
		return !this.editorsWithBreakpoints.isEmpty();
	}

	@Override
	public boolean hasBreakpoints() {
		return canHaveBreakpoints && (this.desktop.getMethodPortlet() != null || this.service.getAmiScriptEditorsCount() > 0);
	}
	public void clear() {
		this.currentEditor = null;
		this.editorsWithBreakpoints.clear();

	}

	public void onEditorClosed(AmiWebBreakpointEditor editor) {
		for (Entry<AmiWebScriptRunner, AmiWebBreakpointEditor> e : editorsWithBreakpoints.entrySet()) {
			if (e.getValue() == editor) {
				this.editorsWithBreakpoints.remove(e.getKey());
				return;
			}
		}
	}
}
