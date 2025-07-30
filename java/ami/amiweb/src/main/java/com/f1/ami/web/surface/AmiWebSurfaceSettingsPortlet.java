package com.f1.ami.web.surface;

import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class AmiWebSurfaceSettingsPortlet extends AmiWebPanelSettingsPortlet {
	private final FormPortletToggleButtonsField<Boolean> clearOnDataStale;
	private final AmiWebSurfacePortlet surface;

	public AmiWebSurfaceSettingsPortlet(PortletConfig config, AmiWebSurfacePortlet portlet) {
		super(config, portlet);
		this.surface = portlet;

		FormPortlet settingsForm = getSettingsForm();
		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.setHelp("On: clears the data and shows hourglass when the underlying data model is running.<br>Off: keeps old (stale) data on screen while data model is running.");
		this.clearOnDataStale.addOption(true, "On");
		this.clearOnDataStale.addOption(false, "Off");
		this.clearOnDataStale.setValue(surface.isClearOnDataStale());
		settingsForm.addField(this.clearOnDataStale);
		//		settingsForm.addFormPortletListener(this);
	}
	@Override
	protected void submitChanges() {
		this.surface.setClearOnDataStale(this.clearOnDataStale.getValue());
		super.submitChanges();
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 400;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 200;
	}

}
