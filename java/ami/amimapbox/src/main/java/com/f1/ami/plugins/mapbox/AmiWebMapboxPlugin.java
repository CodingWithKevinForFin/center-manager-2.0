package com.f1.ami.plugins.mapbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.AmiWebPanelPlugin;
import com.f1.ami.web.AmiWebPluginPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.portlets.vizwiz.AmiWebVizwiz;
import com.f1.ami.web.style.AmiWebStyleType;
import com.f1.container.ContainerTools;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.casters.Caster_Simple;

public class AmiWebMapboxPlugin implements AmiWebPanelPlugin {

	private ContainerTools tools;
	private PropertyController props;
	private AmiWebStyleTypeImpl_Mapbox styleType = new AmiWebStyleTypeImpl_Mapbox();

	@Override
	public void init(ContainerTools tools, PropertyController props) {
		this.tools = tools;
		this.props = props;
	}

	@Override
	public AmiWebMapBoxPanel createPanel(PortletConfig config) {
		return new AmiWebMapBoxPanel(config, this);
	}

	@Override
	public String getDisplayName() {
		return "Map";
	}

	@Override
	public String getCssClassName() {
		return "ami_display_map";
	}
	@Override
	public String getPluginId() {
		return "mapbox";
	}

	@Override
	public String getDisplayIconFileName() {
		return "mapbox_icon.png";
	}

	@Override
	public String getBootstrapHtml() {
		return "<script type=\"text/javascript\" src=\"rsc/javascript/leaflet.js\"></script><link rel=\"stylesheet\" href=\"rsc//leaflet.css\" />";
	}

	@Override
	public AmiWebVizwiz createVizwiz(AmiWebService service, AmiWebPluginPortlet target) {
		return new AmiWebVizwiz_MapBox(service, this, target);
	}

	@Override
	public AmiWebStyleType getStyleType() {
		return this.styleType;
	}

	@Override
	public List<String> extraceUsedDms(Map<String, Object> portletConfig) {
		List<Map<String, Object>> series = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, portletConfig, "lrs", null);
		List<String> r = new ArrayList<String>(series.size());
		for (int i = 0; i < series.size(); i++)
			r.add((String) series.get(i).get("dmadn"));
		return r;
	}

	@Override
	public void replaceUsedDmAt(Map<String, Object> portletConfig, int position, String name) {
		List<Map<String, Object>> t = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, portletConfig, "lrs", null);
		t.get(position).put("dmadn", name);

	}

}
