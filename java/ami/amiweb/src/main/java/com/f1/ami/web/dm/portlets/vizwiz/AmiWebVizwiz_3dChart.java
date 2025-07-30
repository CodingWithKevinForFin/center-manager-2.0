package com.f1.ami.web.dm.portlets.vizwiz;

import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.charts.AmiWebChartEditSeriesPortlet;
import com.f1.ami.web.charts.AmiWebSurfaceRenderingLayer;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.surface.AmiWebSurfaceEditAxisPortlet;
import com.f1.ami.web.surface.AmiWebSurfaceEditSeriesPortlet;
import com.f1.ami.web.surface.AmiWebSurfacePortlet;
import com.f1.ami.web.surface.AmiWebSurfaceSeries;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.impl.TabPortlet;

public class AmiWebVizwiz_3dChart extends AmiWebVizwiz<AmiWebSurfacePortlet> {
	private final String[] requiredFields = { "xPos", "yPos", "zPos" };
	private TabPortlet creatorTabsPortlet;
	private AmiWebChartEditSeriesPortlet<AmiWebSurfaceSeries> seriesEditor;

	public AmiWebVizwiz_3dChart(AmiWebService service, String layoutAlias) {
		super(service, "3d Chart");
		AmiWebSurfacePortlet tm = (AmiWebSurfacePortlet) service.getDesktop().newPortlet(AmiWebSurfacePortlet.Builder.ID, layoutAlias);
		setPreviewPortlet(tm);
		//		AmiCenterGetAmiSchemaResponse response = getService().nw(AmiCenterGetAmiSchemaResponse.class);
		this.creatorTabsPortlet = new TabPortlet(generateConfig());
		this.creatorTabsPortlet.getTabPortletStyle().setBackgroundColor("#4c4c4c");
		this.creatorTabsPortlet.addChild("X axis", (new AmiWebSurfaceEditAxisPortlet(generateConfig(), tm.getAxisX())).hideCloseButtons(true));
		this.creatorTabsPortlet.addChild("Y axis", (new AmiWebSurfaceEditAxisPortlet(generateConfig(), tm.getAxisY())).hideCloseButtons(true));
		this.creatorTabsPortlet.addChild("Z axis", (new AmiWebSurfaceEditAxisPortlet(generateConfig(), tm.getAxisZ())).hideCloseButtons(true));
		this.creatorTabsPortlet.addChild("Style", (new AmiWebEditStylePortlet(tm.getStylePeer(), generateConfig())).hideCloseButtons(true));
		this.creatorTabsPortlet.setIsCustomizable(false);
		addRefreshButton();
	}
	@Override
	public Portlet getCreatorPortlet() {
		return creatorTabsPortlet;
	}

	@Override
	public boolean initDm(AmiWebDm dm, Portlet initForm, String dmTableName) {
		AmiWebSurfacePortlet previewPortlet = getPreviewPortlet();
		previewPortlet.addUsedDm(dm.getAmiLayoutFullAliasDotId(), dmTableName);
		AmiWebDmTableSchema dmTable = dm.getResponseOutSchema().getTable(dmTableName);
		AmiWebSurfaceRenderingLayer layer = new AmiWebSurfaceRenderingLayer(previewPortlet);
		AmiWebSurfaceSeries se = new AmiWebSurfaceSeries(getService(), previewPortlet, dmTable, layer);
		layer.setSeries(se);
		previewPortlet.addSeries(layer, 0);
		this.creatorTabsPortlet.addChild(0, "Formula", (this.seriesEditor = new AmiWebSurfaceEditSeriesPortlet(generateConfig()).setContainer(layer, se)).showButtons(false), true);
		seriesEditor.setUserRequiredFields(requiredFields);
		previewPortlet.onDmDataChanged(dm);
		previewPortlet.setAmiTitle(dmTableName, false);
		return true;
	}

	public boolean preview() {
		seriesEditor.fillDefaultFields();
		return this.seriesEditor.preview();
	}

}
