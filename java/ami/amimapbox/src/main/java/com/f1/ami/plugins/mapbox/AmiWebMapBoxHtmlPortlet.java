package com.f1.ami.plugins.mapbox;

import java.util.Map;

import com.f1.suite.web.JsFunction;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.CH;
import com.f1.utils.LongArrayList;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.LongSet;

public class AmiWebMapBoxHtmlPortlet extends AbstractPortlet {

	public static final String PROPERTY_AMI_MAPBOX_TOKEN = "ami.mapbox.token";
	public static final PortletSchema<AmiWebMapBoxHtmlPortlet> SCHEMA = new BasicPortletSchema<AmiWebMapBoxHtmlPortlet>("MapBox", "MapBoxPortlet", AmiWebMapBoxHtmlPortlet.class,
			false, true);
	private AmiWebMapBoxPanel owner;

	public AmiWebMapBoxHtmlPortlet(PortletConfig manager, AmiWebMapBoxPanel amiWebMapBoxPanel) {
		super(manager);
		this.owner = amiWebMapBoxPanel;
	}

	private boolean needsSelectedUpdate = false;
	private LongSet selectedOnFrontEnd = new LongSet();
	private boolean needsInit;
	private boolean needsResize;
	private boolean needsDataUpdate;
	private boolean needsUpdateToken;
	private String mapboxAccessToken;
	private boolean need;
	private String selectedColor;
	private String selectedBorderColor;
	private boolean fitPoints = true;

	void updateHtml() {
		this.flagPendingAjax();
	}
	@Override
	public void setSize(int width, int height) {
		if (this.getWidth() == width && getHeight() == height)
			return;
		super.setSize(width, height);
		this.needsResize = true;
		flagPendingAjax();
	}

	@Override
	protected void onVisibilityChanged(boolean isVisible) {
		if (isVisible) {
			this.needsInit = true;
			this.needsDataUpdate = true;
			flagPendingAjax();
		}
		super.onVisibilityChanged(isVisible);
	}

	@Override
	public void drainJavascript() {
		super.drainJavascript();
		StringBuilder js = this.getManager().getPendingJs();
		String mapboxAccessToken = this.mapboxAccessToken;
		if (SH.isnt(mapboxAccessToken))
			mapboxAccessToken = getManager().getTools().getOptional(PROPERTY_AMI_MAPBOX_TOKEN, String.class);
		if (this.getVisible() && mapboxAccessToken != null) {
			if (needsInit) {
				callJsFunction("initMapBox").end();
				this.needsUpdateToken = true;
			}
			if (needsUpdateToken) {
				callJsFunction("showNeedsAccessToken").addParam(mapboxAccessToken.equals("")).end();
				callJsFunction("tileLayer").addParamQuoted(mapboxAccessToken).end();
				this.needsUpdateToken = false;
			}
			if (needsResize)
				callJsFunction("resizeMapBox").end();
			if (needsDataUpdate) {
				callJsFunction("clearPointsMapBox").end();
				this.selectedOnFrontEnd.clear();
				this.needsSelectedUpdate = true;
				for (int i = 0; i < this.owner.getLayersCount(); i++) {
					AmiWebMapBoxLayer layer = this.owner.getLayerAt(i);
					int size = layer.getDataSize();
					if (size > 0) {
						JsonBuilder json = new JsonBuilder();
						json.startMap();
						json.addKeyValue("layerId", layer.getId());
						json.addKey("lats").add(layer.getDataLatitudes(), 0, size);
						json.addKey("lons").add(layer.getDataLongitudes(), 0, size);
						json.addKey("opacities").add(layer.getDataOpacities(), 0, size);
						json.addKey("sizes").add(layer.getDataSizes(), 0, size);
						json.addKey("colors").addQuoted(layer.getDataFillColors(), 0, size);
						json.addKey("borderColors").addQuoted(layer.getDataBorderColors(), 0, size);
						json.addKey("labels").addQuoted(layer.getDataLabels(), 0, size);
						json.addKeyValue("labelLimit", layer.getLabelLimit());

						//only send style values up to label limit
						json.addKey("labelFontFamilies").addQuoted(layer.getDataLabelFontFamilies(), 0, layer.getDataLabelFontFamilies().length);
						json.addKey("labelFontSizes").add(layer.getDataLabelFontSizes(), 0, layer.getDataLabelFontSizes().length);
						json.addKey("labelFontColors").addQuoted(layer.getDataLabelFontColors(), 0, layer.getDataLabelFontColors().length);
						json.addKey("labelPositions").addQuoted(layer.getDataLabelPositions(), 0, layer.getDataLabelPositions().length);
						json.endMap();
						callJsFunction("addPointsMapBox").addParam(json).end();
					}
				}
				if (isFitPoints())
					callJsFunction("fitPointsMapBox").end();
			}
			if (this.needsSelectedUpdate) {
				final LongSet t = this.owner.getSelected();
				LongArrayList toRemove = new LongArrayList();
				LongArrayList toAdd = new LongArrayList();
				this.selectedOnFrontEnd.getNotIn(t, toRemove);
				t.getNotIn(this.selectedOnFrontEnd, toAdd);

				int cnt = toRemove.size() + toAdd.size();
				if (cnt > 0) {
					int layerIds[] = new int[cnt];
					int pointIds[] = new int[cnt];
					String colors[] = new String[cnt];
					String borderColors[] = new String[cnt];
					int n = 0;
					for (int i = 0; i < toAdd.size(); i++) {
						long l = toAdd.getLong(i);
						this.selectedOnFrontEnd.add(l);
						layerIds[n] = long2LayerId(l);
						pointIds[n] = long2PointId(l);
						colors[n] = this.selectedColor;
						borderColors[n] = this.selectedBorderColor;
						n++;
					}
					for (int i = 0; i < toRemove.size(); i++) {
						long l = toRemove.getLong(i);
						this.selectedOnFrontEnd.remove(l);
						int layerId = layerIds[n] = long2LayerId(l);
						int pointId = pointIds[n] = long2PointId(l);
						colors[n] = this.selectedColor;
						borderColors[n] = this.selectedBorderColor;
						colors[n] = this.owner.getLayerAt(layerId).getDataFillColors()[pointId];
						borderColors[n] = this.owner.getLayerAt(layerId).getDataBorderColors()[pointId];
						n++;
					}
					JsonBuilder json = new JsonBuilder();
					json.startMap();
					json.addKey("amiLayerIds").add(layerIds);
					json.addKey("amiPointIds").add(pointIds);
					json.addKey("colors").addQuoted(colors);
					json.addKey("borderColors").addQuoted(borderColors);
					json.endMap();
					callJsFunction("updatePointsMapBox").addParam(json).end();
				}

			}
			this.needsInit = this.needsDataUpdate = this.needsResize = this.needsSelectedUpdate = false;
		}
	}
	public String getMapboxAccessToken() {
		return mapboxAccessToken;
	}
	public void initMapBoxWithToken(String mapboxAccessToken) {
		this.mapboxAccessToken = mapboxAccessToken;
		this.needsInit = true;
		this.needsUpdateToken = true;
	}
	public void setMapboxAccessToken(String mapboxAccessToken) {
		this.mapboxAccessToken = mapboxAccessToken;
		this.needsUpdateToken = true;
		flagPendingAjax();
	}
	public void onDataChanged() {
		this.needsDataUpdate = true;
		flagPendingAjax();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("pointClicked".equals(callback)) {
			boolean ctrlKey = CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "ctrlKey");
			boolean shiftKey = CH.getOrThrow(Caster_Boolean.INSTANCE, attributes, "shiftKey");
			int layerId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "layerId");
			int pointId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pointId");
			if (shiftKey) {
				this.owner.setIsSelected(layerId, pointId, true);
			} else if (ctrlKey) {
				this.owner.setIsSelected(layerId, pointId, !this.owner.getIsSelected(layerId, pointId));
			} else {
				if (this.owner.getSelectedCount() != 1 || !this.owner.getIsSelected(layerId, pointId)) {
					this.owner.clearUserSelection();
					this.owner.setIsSelected(layerId, pointId, true);
				}
			}
		} else if ("pointHover".equals(callback)) {
			int layerId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "layerId");
			int pointId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "pointId");
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			AmiWebMapBoxLayer layer = this.owner.getLayerById(layerId);
			if (layer == null)
				return;
			String tooltip = owner.getService().cleanHtml(layer.getTooltipAt(pointId));
			if (tooltip == null)
				return;
			StringBuilder js = getManager().getPendingJs();
			JsFunction func = new JsFunction(js, null, "g(\'" + this.getPortletId() + "\').setHoverMapBox");
			func.addParam(x).addParam(y).addParam(layerId).addParam(pointId).addParamQuoted(tooltip).end();
		} else
			super.handleCallback(callback, attributes);
	}
	public static long layerIdpointId2Long(int layerId, int pointId) {
		return layerId * 10000000 + pointId;
	}
	public static int long2LayerId(long l) {
		return (int) (l / 10000000);
	}
	public static int long2PointId(long l) {
		return (int) (l % 10000000);
	}
	public void onSelectedChanged() {
		this.needsSelectedUpdate = true;
		flagPendingAjax();
	}

	public String getSelectedColor() {
		return selectedColor;
	}
	public void setSelectedColor(String selectedColor) {
		this.selectedColor = selectedColor;
	}
	public String getSelectedBorderColor() {
		return selectedBorderColor;
	}
	public void setSelectedBorderColor(String selectedBorderColor) {
		this.selectedBorderColor = selectedBorderColor;
	}

	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}
	public boolean isFitPoints() {
		return fitPoints;
	}
	public void setFitPoints(boolean fitPoints) {
		this.fitPoints = fitPoints;
	}

}
