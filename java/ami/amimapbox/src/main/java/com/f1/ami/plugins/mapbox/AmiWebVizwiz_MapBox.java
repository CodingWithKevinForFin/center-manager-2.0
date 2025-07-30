package com.f1.ami.plugins.mapbox;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebPanelPlugin;
import com.f1.ami.web.AmiWebPluginPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.SH;

public class AmiWebVizwiz_MapBox extends AmiWebVizwiz<AmiWebMapBoxPanel> {

	private AmiWebPanelPlugin plugin;
	private TabPortlet creatorTabsPortlet;
	private AmiWebMapBoxLayerSettingsPortlet layerSettings;

	public AmiWebVizwiz_MapBox(AmiWebService service, AmiWebPanelPlugin plugin, AmiWebPluginPortlet target) {
		super(service, "Map");
		this.plugin = plugin;
		this.setPreviewPortlet((AmiWebMapBoxPanel) target);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(this.getPreviewPortlet().getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addRefreshButton();
	}
	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmTableName) {
		AmiWebMapBoxPanel t = getPreviewPortlet();
		if (initForm != null) {
			String value = (String) ((FormPortlet) initForm).getField("MBT").getValue();
			t.setMapboxAccessToken(value);
		}
		t.addUsedDm(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		this.layerSettings = new AmiWebMapBoxLayerSettingsPortlet(generateConfig(), t, new AmiWebMapBoxLayer(t, t.nextLayerId(), dm.getAmiLayoutFullAliasDotId(), dmTableName),
				false).populateDefaultSettings().hideButtonsForm(true);
		Tab tab = this.creatorTabsPortlet.addChild(0, "Settings", this.layerSettings);
		this.creatorTabsPortlet.setActiveTab(tab.getPortlet());
		t.onDmDataChanged(dm);
		t.setAmiTitle(dmTableName, false);
		return true;
	}
	@Override
	public Portlet getInitForm(AmiWebDm dm, String tableName) {
		String token = getService().getPortletManager().getTools().getOptional(AmiWebMapBoxHtmlPortlet.PROPERTY_AMI_MAPBOX_TOKEN, String.class);
		if (SH.isnt(token)) {
			getService().getPortletManager()
					.showAlert(AmiWebMapBoxHtmlPortlet.PROPERTY_AMI_MAPBOX_TOKEN + " has not been set so please supply a mapbox token. (This will be stored in the .ami layout)");
			FormPortlet r = new FormPortlet(generateConfig());
			FormPortletTextField field = r.addField(new FormPortletTextField("Access Token: ").setId("MBT").setWidth(FormPortletField.WIDTH_STRETCH));
			field.setValue("");
			return r;
		} else
			return null;
	}

	@Override
	public boolean preview() {
		return this.layerSettings.applySettings();
	}
}
