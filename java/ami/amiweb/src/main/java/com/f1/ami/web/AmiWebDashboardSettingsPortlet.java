package com.f1.ami.web;

import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiWebDashboardSettingsPortlet extends GridPortlet implements FormPortletListener, ConfirmDialogListener {

	private FormPortlet form;
	private FormPortletTextField prefsKeyField;
	private FormPortletToggleButtonsField<Byte> cpimField;
	private final FormPortletTextField browserTitleField;
	private final FormPortletTextField transientIdPrefix;
	private final FormPortletTextField customUserMenuTitleField;
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private AmiWebService service;
	private FormPortletNumericRangeField idleUserWarnForMillisField;
	private FormPortletNumericRangeField idleUserWarnAfterMillisField;
	private FormPortletToggleButtonsField<Boolean> idleUserMode;
	private FormPortletSelectField<Byte> menubarPositionField;
	private byte originalMenubarPosition;

	public AmiWebDashboardSettingsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.form = new FormPortlet(generateConfig());
		this.form.getFormPortletStyle().setLabelsWidth(230);
		this.form.addField(new FormPortletTitleField(""));
		this.prefsKeyField = this.form.addField(new FormPortletTextField("User Preferences Namespace: "));
		this.prefsKeyField.setValue(this.service.getVarsManager().getUserPrefNamespace());
		this.cpimField = this.form.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Allow Custom Preferences Import: "));
		this.cpimField.addOption(AmiWebVarsManager.CUST_PREF_IMPORT_MODE_REJECT, "Reject");
		this.cpimField.addOption(AmiWebVarsManager.CUST_PREF_IMPORT_MODE_IGNORE, "Ignore");
		this.cpimField.addOption(AmiWebVarsManager.CUST_PREF_IMPORT_MODE_ACCEPT, "Accept");
		this.cpimField.setValue(this.service.getVarsManager().getCustomPrefsImportMode());
		this.browserTitleField = this.form.addField(new FormPortletTextField("Browser Title: "));
		this.browserTitleField.setValue(this.service.getVarsManager().getBrowserTitle());
		this.menubarPositionField = this.form.addField(new FormPortletSelectField<Byte>(Byte.class, "Menubar Position: "));
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_TOP, "Top Full");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_TOP_LEFT, "Top Left");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_TOP_CENTER, "Top Center");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_TOP_RIGHT, "Top Right");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_BOTTOM, "Bottom Full");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_BOTTOM_LEFT, "Bottom Left");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_BOTTOM_CENTER, "Bottom Center");
		this.menubarPositionField.addOption(AmiWebDesktopPortlet.MENUBAR_BOTTOM_RIGHT, "Bottom Right");
		this.originalMenubarPosition = service.getDesktop().getMenubarPosition();
		this.menubarPositionField.setValue(this.originalMenubarPosition);
		this.customUserMenuTitleField = this.form.addField(new FormPortletTextField("User Menu Title: "));
		this.customUserMenuTitleField.setValue(this.service.getVarsManager().getCustomUserMenuTitle());
		this.transientIdPrefix = this.form.addField(new FormPortletTextField("TransientId Prefix: "));
		this.idleUserMode = this.form.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Logout Idle Session: "));
		this.idleUserMode.addOption(false, "OFF");
		this.idleUserMode.addOption(true, "ON");
		this.idleUserWarnAfterMillisField = this.form.addField(new FormPortletNumericRangeField("Show Idle Warning After(Seconds): "));
		this.idleUserWarnAfterMillisField.setRange(5, 86400).setStep(5);
		this.idleUserWarnAfterMillisField.setHelp("The amount of time elapsed on an idle session before the user is shown an 'idle session warning'");
		this.idleUserWarnForMillisField = this.form.addField(new FormPortletNumericRangeField("Show Idle Warning For(Seconds): "));
		this.idleUserWarnForMillisField.setHelp("The amount of time to show the 'idle session warning' before closing the session and automatically logging out the user");
		this.idleUserWarnForMillisField.setRange(0, 3600).setStep(5);
		this.browserTitleField.setValue(this.service.getVarsManager().getBrowserTitle());
		this.transientIdPrefix.setValue(this.service.getVarsManager().getTransientIdPrefix());
		this.submitButton = this.form.addButton(new FormPortletButton("Submit"));
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		AmiWebIdleSessionManager s = this.service.getIdleSessionManager();
		if (s.getIdleUserWarnAfterMillis() <= 0) {
			this.idleUserMode.setValue(false);
		} else
			this.idleUserMode.setValue(true);
		this.idleUserWarnForMillisField.setValue(s.getIdleUserWarnForMillis() / 1000);
		this.idleUserWarnAfterMillisField.setValue(s.getIdleUserWarnAfterMillis() / 1000);
		updateIdleUserMode();
		this.addChild(form);
		this.form.addFormPortletListener(this);
		setSuggestedSize(500, 420);
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == cancelButton)
			close();
		else if (button == this.submitButton) {
			String transientIdPrefix = SH.trim(this.transientIdPrefix.getValue());
			if (SH.is(transientIdPrefix) && !AmiUtils.isValidVariableName(transientIdPrefix, false, false)) {
				getManager().showAlert("Invalid Transient Id Prefix. Must be a valid variable name");
				return;
			}
			String browserTitle = this.browserTitleField.getValue();
			PortletManager portletManager = this.service.getPortletManager();
			browserTitle = SH.is(browserTitle) ? browserTitle : portletManager.getDefaultBrowserTitle();
			this.service.getVarsManager().setBrowserTitle(browserTitle);
			this.service.getVarsManager().setCustomUserMenuTitle(this.customUserMenuTitleField.getValue());
			this.service.getVarsManager().setCustomPrefsImportMode(this.cpimField.getValue());
			this.service.getVarsManager().setTransientIdPrefix(transientIdPrefix);
			this.originalMenubarPosition = this.menubarPositionField.getValue();

			// User prefs
			String existing = this.service.getVarsManager().getUserPrefNamespace();
			String nuw = SH.trim(this.prefsKeyField.getValue());
			if (SH.isnt(nuw))
				nuw = null;
			else if (!AmiUtils.isValidVariableName(nuw, false, false)) {
				getManager().showAlert("Invalid User Preferences namespace. Must be valid variable name");
				return;
			}
			AmiWebIdleSessionManager s = this.service.getIdleSessionManager();
			if (!this.idleUserMode.getValue()) {
				s.setIdleUserWarnForMillis(0);
				s.setIdleUserWarnAfterMillis(0);
			} else {
				s.setIdleUserWarnAfterMillis(this.idleUserWarnAfterMillisField.getIntValue() * 1000);
				s.setIdleUserWarnForMillis(this.idleUserWarnForMillisField.getIntValue() * 1000);
			}
			if (OH.ne(nuw, existing)) {
				if (existing == null) {
					this.service.getVarsManager().setUserPrefNamespace(nuw);
					close();
				} else {
					getManager().showDialog("Confirm",
							new ConfirmDialogPortlet(generateConfig(),
									"Changing the User Preferences Namespace means that existing user preferences will no longer be applied to this layout.  Continue?",
									ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("CONFIRM"));
				}

			} else {
				close();
			}
			this.service.getDesktop().updateDashboard();
		}
	}

	@Override
	public void onClosed() {
		this.service.getDesktop().setMenubarPosition(this.originalMenubarPosition);
		super.onClosed();
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.idleUserMode) {
			updateIdleUserMode();
			if (this.idleUserWarnAfterMillisField.getValue() == 0 && this.idleUserWarnForMillisField.getValue() == 0) {
				this.idleUserWarnAfterMillisField.setValue(600);
				this.idleUserWarnForMillisField.setValue(60);
			}
		}
		if (field == this.menubarPositionField) {
			this.service.getDesktop().setMenubarPosition(this.menubarPositionField.getValue());
		}
	}

	private void updateIdleUserMode() {
		boolean disabled = !this.idleUserMode.getValue();
		this.form.removeFieldNoThrow(this.idleUserWarnAfterMillisField);
		this.form.removeFieldNoThrow(this.idleUserWarnForMillisField);
		if (!disabled) {
			this.form.addFieldAfter(this.idleUserMode, this.idleUserWarnForMillisField);
			this.form.addFieldAfter(this.idleUserMode, this.idleUserWarnAfterMillisField);
		}

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("CONFIRM".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id)) {
				String nuw = SH.trim(this.prefsKeyField.getValue());
				if (SH.isnt(nuw))
					nuw = null;
				this.service.getVarsManager().setUserPrefNamespace(nuw);
				close();
			}
			return true;
		}
		return false;
	}
}
