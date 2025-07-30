package com.f1.ami.web.amiscript;

import com.f1.ami.web.AmiWebCommandWrapper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.RootPortletDialog;
import com.f1.suite.web.portal.impl.RootPortletDialogListener;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;

public class AmiAlertPromptControlPause extends FlowControlPause implements ConfirmDialogListener, RootPortletDialogListener {

	private AmiWebCommandWrapper cmd;
	private int timeout;
	private String response;
	private AmiWebScriptRunner scriptRunner;

	public AmiAlertPromptControlPause(DerivedCellCalculator position) {
		super(position);
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void processResponse(String button) {
		this.response = button;
		this.scriptRunner.setState(AmiWebScriptRunner.STATE_RESPONSE_READY);
	}

	public String getResponse() {
		return this.response;
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		processResponse(id);
		return true;
	}

	@Override
	public void onDialogClickoutside(RootPortletDialog dialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogVisible(RootPortletDialog rootPortletDialog, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogMoved(RootPortletDialog rootPortletDialog) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialogClosed(RootPortletDialog rootPortletDialog) {
		if (this.response == null)
			processResponse(null);
	}

	@Override
	public void onUserCloseDialog(RootPortletDialog rootPortletDialog) {
	}

	public void setScriptRunner(AmiWebScriptRunner scriptRunner) {
		this.scriptRunner = scriptRunner;
	}

}
