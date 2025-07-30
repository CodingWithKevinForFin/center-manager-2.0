package com.f1.ami.web;

import java.util.Map;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.OH;
import com.f1.utils.SH;

public abstract class AmiWebPanelSettingsPortlet extends GridPortlet implements FormPortletListener {

	private final FormPortletTitleField titleHeaderField = new FormPortletTitleField("Identifiers:");
	private final FormPortletTextField titleField = new FormPortletTextField("Title:");
	private final FormPortletTextField panelIdField = new FormPortletTextField("Panel ID:");
	private final FormPortletTextField aliasField = new FormPortletTextField("Owning Layout:").setDisabled(true);
	private final FormPortletTextField userPrefIdField = new FormPortletTextField("User Preferences ID:");
	private final FormPortlet form;
	private final AmiWebAbstractPortlet portlet;
	private final FormPortletButton submitButton = new FormPortletButton("Submit");
	private final FormPortletButton cancelButton = new FormPortletButton("Cancel");
	public boolean submitted = false;
	private final FormPortletSelectField<Byte> downstreamRealtimeModeDefault = new FormPortletSelectField<Byte>(Byte.class, "Default (on login)");
	private final FormPortletSelectField<Byte> downstreamRealtimeModeCurrent = new FormPortletSelectField<Byte>(Byte.class, "Current (not saved)");

	public AmiWebPanelSettingsPortlet(PortletConfig config, AmiWebAbstractPortlet portlet) {
		super(config);
		this.portlet = portlet;
		this.form = new FormPortlet(generateConfig());
		this.form.addField(this.titleHeaderField);
		this.form.addField(this.aliasField);
		this.form.addField(this.titleField);
		this.form.addField(this.panelIdField);
		this.form.addField(this.userPrefIdField);
		this.titleField.setValue(this.portlet.getAmiTitle(false));
		this.panelIdField.setValue(this.portlet.getAmiPanelId());
		this.aliasField.setValue(AmiWebUtils.formatLayoutAlias(this.portlet.getAmiLayoutFullAlias()));
		this.userPrefIdField.setValue(this.portlet.getAmiUserPrefId());
		if (portlet instanceof AmiWebRealtimePortlet) {
			FormPortletTitleField rtHeader = new FormPortletTitleField("REAL-TIME DOWNSTREAM MODE");
			this.downstreamRealtimeModeDefault.addOption(AmiWebRealtimePortlet.DOWN_STREAM_MODE_OFF, "Off");
			this.downstreamRealtimeModeDefault.addOption(AmiWebRealtimePortlet.DOWN_STREAM_MODE_SELECTED_OR_ALL, "Selected or All");
			this.downstreamRealtimeModeDefault.setValue(((AmiWebRealtimePortlet) portlet).getDownstreamRealtimeMode().getValue());
			this.downstreamRealtimeModeCurrent.addOption(AmiWebRealtimePortlet.DOWN_STREAM_MODE_OFF, "Off");
			this.downstreamRealtimeModeCurrent.addOption(AmiWebRealtimePortlet.DOWN_STREAM_MODE_SELECTED_OR_ALL, "Selected or All");
			this.downstreamRealtimeModeCurrent.setValue(((AmiWebRealtimePortlet) portlet).getDownstreamRealtimeMode().get());
			this.form.addField(rtHeader);
			this.form.addField(this.downstreamRealtimeModeDefault);
			this.form.addField(this.downstreamRealtimeModeCurrent);
		}
		initForms();
		this.form.addFormPortletListener(this);
	}
	@Override
	protected void onVisibilityChanged(boolean isVisible) {
		if (isVisible && this.portlet.isTransient()) {
			for (FormPortlet i : PortletHelper.findPortletsByType(this, FormPortlet.class)) {
				for (String s : i.getFields())
					i.getField(s).setDisabled(true);
			}
		}
		super.onVisibilityChanged(isVisible);
	}

	protected FormPortlet getSettingsForm() {
		return this.form;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.submitButton) {
			if (verifyChanges()) {
				submitChanges();
				close();
			}
		} else if (button == this.cancelButton) {
			setSubmitted(false);
			onCancelled();
		}
	}
	protected void onCancelled() {
		close();
	}

	protected void submitChanges() {
		this.portlet.setAmiTitle(SH.trim(this.titleField.getValue()), false);
		String pannelId = SH.trim(this.panelIdField.getValue());
		if (OH.ne(pannelId, this.portlet.getAmiPanelId())) {
			portlet.setAdn(AmiWebUtils.getFullAlias(this.portlet.getAmiLayoutFullAlias(), pannelId));
		}
		String userPrefId = SH.trim(this.userPrefIdField.getValue());
		if (OH.ne(userPrefId, this.portlet.getAmiUserPrefId()))
			portlet.getService().registerAmiUserPrefId(userPrefId, this.portlet);
		if (portlet instanceof AmiWebRealtimePortlet) {
			((AmiWebRealtimePortlet) portlet).setDownstreamRealtimeMode(this.downstreamRealtimeModeDefault.getValue());
			((AmiWebRealtimePortlet) portlet).setDownstreamRealtimeModeOverride(this.downstreamRealtimeModeCurrent.getValue());
		}
		setSubmitted(true);
		close();
	}

	protected boolean verifyChanges() {
		String panelId = SH.trim(this.panelIdField.getValue());
		if (SH.isnt(panelId)) {
			getManager().showAlert("Panel ID required");
			this.panelIdField.focus();
			return false;
		} else if (!AmiWebUtils.isValidPanelId(panelId)) {
			this.panelIdField.focus();
			getManager().showAlert("Panel ID is not valid (Must be alpha numeric): " + panelId);
			return false;
		} else if (OH.ne(panelId, this.portlet.getAmiPanelId())) {
			if (portlet.getService().getPortletByPanelId(this.portlet.getAmiLayoutFullAlias(), panelId) != null) {
				this.panelIdField.focus();
				getManager().showAlert("Panel ID Already exists: " + panelId);
				return false;
			}
			if (portlet.getService().getLayoutFilesManager().getLayoutByFullAlias(this.portlet.getAmiLayoutFullAlias()).getHiddenPanelIds().contains(panelId)) {
				this.panelIdField.focus();
				getManager().showAlert("Panel ID Already exists (in hidden panel): " + panelId);
				return false;
			}
		}
		String userPrefId = SH.trim(this.userPrefIdField.getValue());
		if (SH.isnt(userPrefId)) {
			getManager().showAlert("User Preferences ID required");
			this.userPrefIdField.focus();
			return false;
		}
		if (OH.ne(userPrefId, portlet.getAmiUserPrefId()) && portlet.getService().getPortletByUserPrefId(this.portlet.getAmiLayoutFullAlias(), userPrefId) != null) {
			getManager().showAlert("User Preference ID already exists: '" + userPrefId + "'");
			this.userPrefIdField.focus();
			return false;
		}

		return true;
	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 500;
	}

	protected FormPortletButton getSubmitButton() {
		return this.submitButton;
	}
	protected FormPortletButton getCancelButton() {
		return this.cancelButton;
	}
	protected void initForms() {
		if (getChildrenCount() == 0) {
			addChild(this.form);
			this.form.addButton(this.submitButton);
			this.form.addButton(this.cancelButton);
		}
	}
	protected AmiWebAbstractPortlet getPortlet() {
		return this.portlet;
	}
	protected void hideButtons(boolean hide) {
		this.form.clearButtons();
		if (!hide) {
			this.form.addButton(this.submitButton);
			this.form.addButton(this.cancelButton);
		}
	}
	protected FormPortletTextField getTitleField() {
		return this.titleField;
	}
	protected FormPortletTitleField getTitleHeaderField() {
		return this.titleHeaderField;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public boolean isSubmitted() {
		return submitted;
	}

	public void setSubmitted(boolean submitted) {
		this.submitted = submitted;
	}

}
