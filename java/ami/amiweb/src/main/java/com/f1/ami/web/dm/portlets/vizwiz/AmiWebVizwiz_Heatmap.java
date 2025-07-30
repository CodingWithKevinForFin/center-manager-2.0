package com.f1.ami.web.dm.portlets.vizwiz;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebTreemapPortlet;
import com.f1.ami.web.AmiWebTreemapPortlet.AmiTreemapConfigPortlet;
import com.f1.ami.web.AmiWebTreemapStaticPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.TabPortlet;

public class AmiWebVizwiz_Heatmap extends AmiWebVizwiz<AmiWebTreemapStaticPortlet> {

	private TabPortlet creatorTabsPortlet;
	private AmiTreemapConfigPortlet configPortlet;

	public AmiWebVizwiz_Heatmap(AmiWebService service, String layoutAlias) {
		super(service, "Heatmap");
		AmiWebTreemapStaticPortlet tm = (AmiWebTreemapStaticPortlet) service.getDesktop().newPortlet(AmiWebTreemapStaticPortlet.Builder.ID, layoutAlias);
		setPreviewPortlet(tm);
		//		AmiCenterGetAmiSchemaResponse response = getService().nw(AmiCenterGetAmiSchemaResponse.class);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.creatorTabsPortlet.addChild("Config",
				(this.configPortlet = new AmiWebTreemapPortlet.AmiTreemapConfigPortlet(generateConfig(), tm)).hideHeader(true).hideButtonsForm(true));
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(tm.getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addRefreshButton();
	}

	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmName) {
		AmiWebTreemapStaticPortlet previewPortlet = getPreviewPortlet();
		previewPortlet.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), dmName);
		previewPortlet.onDmDataChanged(dm);
		previewPortlet.setAmiTitle(dmName, false);
		return true;
	}

	@Override
	public boolean preview() {
		return this.configPortlet.applySettings();
	}
}
