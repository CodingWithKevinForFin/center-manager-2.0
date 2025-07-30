package com.f1.ami.plugins.mapbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebPanelPlugin;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebPluginPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmUtils;
import com.f1.ami.web.style.AmiWebStyleConsts;
import com.f1.ami.web.style.AmiWebStyledPortlet;
import com.f1.base.LongIterator;
import com.f1.base.Table;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.LongSet;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebMapBoxPanel extends AmiWebPluginPortlet implements AmiWebStyledPortlet {

	final private AmiWebMapBoxHtmlPortlet htmlPortlet;
	private BasicIndexedList<Integer, AmiWebMapBoxLayer> layers = new BasicIndexedList<Integer, AmiWebMapBoxLayer>();

	public AmiWebMapBoxPanel(PortletConfig config, AmiWebPanelPlugin plugin) {
		super(config, plugin);
		setChild(this.htmlPortlet = new AmiWebMapBoxHtmlPortlet(generateConfig(), this));
		getManager().onPortletAdded(this.htmlPortlet);
		this.htmlPortlet.updateHtml();
		getStylePeer().initStyle();
	}

	public boolean isFitPoints() {
		return this.htmlPortlet.isFitPoints();
	}
	public void setFitPoints(boolean b) {
		this.htmlPortlet.setFitPoints(b);
	}

	@Override
	public void populateConfigMenu(WebMenu headMenu) {
		BasicWebMenu edtLayer = new BasicWebMenu("Edit Layer", this.layers.getSize() > 0);
		BasicWebMenu delLayer = new BasicWebMenu("Remove Layer", this.layers.getSize() > 0);
		for (AmiWebMapBoxLayer i : layers.values()) {
			edtLayer.add(new BasicWebMenuLink(i.getDescription(), true, "edt_layer_" + i.getId()));
			delLayer.add(new BasicWebMenuLink(i.getDescription(), true, "del_layer_" + i.getId()));
		}

		headMenu.add(new BasicWebMenuLink("Set Mapbox Access Token...", true, "accesstoken"));
		headMenu.add(new BasicWebMenuLink("Add Layer...", true, "add_layer").setBackgroundImage(AmiWebConsts.ICON_ADD));
		headMenu.add(edtLayer);
		headMenu.add(delLayer);
	}
	public java.util.Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		r.put("mbat", this.htmlPortlet.getMapboxAccessToken());
		List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
		for (AmiWebMapBoxLayer i : this.layers.values())
			t.add(i.getConfiguration(getAmiLayoutFullAlias()));
		r.put("lrs", t);
		r.put("fp", this.htmlPortlet.isFitPoints());
		return r;
	}
	@Override
	public void init(java.util.Map<String, Object> configuration, java.util.Map<String, String> origToNewIdMapping, StringBuilder sb) {
		this.htmlPortlet.initMapBoxWithToken(CH.getOr(Caster_String.INSTANCE, configuration, "mbat", null));
		List<Map<String, Object>> t = (List<Map<String, Object>>) CH.getOr(Caster_Simple.OBJECT, configuration, "lrs", null);
		this.htmlPortlet.setFitPoints(CH.getOr(Caster_Boolean.INSTANCE, configuration, "fp", true));
		if (t != null) {
			for (Map<String, Object> i : t) {
				Integer id = CH.getOrThrow(Caster_Integer.INSTANCE, i, "id");
				AmiWebMapBoxLayer layer = new AmiWebMapBoxLayer(this, id);
				layer.init(getAmiLayoutFullAlias(), i);
				this.layers.add(id, layer);
				layer.addToDomManager();
			}
		}
		super.init(configuration, origToNewIdMapping, sb);
		this.htmlPortlet.updateHtml();
	}

	public boolean onAmiContextMenu(String id) {
		if ("accesstoken".equals(id)) {
			getManager().showDialog("MapBox Access Token",
					new ConfirmDialogPortlet(generateConfig(), "", ConfirmDialogPortlet.TYPE_OK_CANCEL, this,
							new FormPortletTextField("Access Token").setWidth(FormPortletTextField.WIDTH_STRETCH)).setInputFieldValue(this.htmlPortlet.getMapboxAccessToken())
									.setCallback("SET_ACCESS_TOKEN"));
			return true;
		} else if (id.startsWith("del_layer_")) {
			int layerId = SH.parseInt(SH.stripPrefix(id, "del_layer_", true));
			AmiWebMapBoxLayer layer = this.layers.get(layerId);
			getManager().showDialog("Remove Layer",
					new ConfirmDialogPortlet(generateConfig(), "Remove Layer <B>" + layer.getDescription(), ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("DEL_LAYER")
							.setCorrelationData(layer));
			return true;
		} else if (id.startsWith("edt_layer_")) {
			int layerId = SH.parseInt(SH.stripPrefix(id, "edt_layer_", true));
			AmiWebMapBoxLayer layer = this.layers.get(layerId);
			getManager().showDialog("Edit Map Layer", new AmiWebMapBoxLayerSettingsPortlet(generateConfig(), this, layer, true));
			return true;
		} else if ("add_layer".equals(id)) {
			String dmAliasDotName = CH.first(getUsedDmAliasDotNames());
			String dmTableName = CH.first(getUsedDmTables(dmAliasDotName));
			getManager().showDialog("Add Map Layer",
					new AmiWebMapBoxLayerSettingsPortlet(generateConfig(), this, new AmiWebMapBoxLayer(this, nextLayerId(), dmAliasDotName, dmTableName), false));
			return true;
		} else if ("style".equals(id)) {
			AmiWebUtils.showStyleDialog("Map Style", this, new AmiWebEditStylePortlet(this.getStylePeer(), generateConfig()), generateConfig());
			return true;
		} else
			return super.onAmiContextMenu(id);
	}
	@Override
	public void clearAmiData() {
		for (int i = 0; i < this.layers.getSize(); i++)
			this.layers.getAt(i).setData(null);
		this.selected.clear();
		this.htmlPortlet.onDataChanged();
	}
	public int nextLayerId() {
		for (int i = 0;; i++)
			if (!this.layers.containsKey(i))
				return i;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SET_ACCESS_TOKEN".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id))
				setMapboxAccessToken((String) source.getInputFieldValue());
			return true;
		} else if ("DEL_LAYER".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.equals(id))
				this.removeLayer((AmiWebMapBoxLayer) source.getCorrelationData());
			return true;
		}
		return super.onButton(source, id);
	}
	public void setMapboxAccessToken(String token) {
		this.htmlPortlet.setMapboxAccessToken(token);
	}

	@Override
	public void onDmDataChanged(AmiWebDm datamodel) {
		for (AmiWebMapBoxLayer layer : this.layers.values())
			if (OH.eq(datamodel.getAmiLayoutFullAliasDotId(), layer.getDmAliasDotName()))
				layer.setData(datamodel.getResponseTableset().getTableNoThrow(layer.getDmTableName()));
		this.selected.clear();//TODO: only clear out layer that changed
		this.htmlPortlet.onDataChanged();
		showWaitingSplash(false);
	}

	@Override
	public void getUsedColors(Set<String> sink) {
		for (AmiWebMapBoxLayer i : this.layers.values()) {
			AmiWebUtils.getColors(i.getBorderColorFormula().getFormula(false), sink);
			AmiWebUtils.getColors(i.getFillColorFormula().getFormula(false), sink);
		}
		super.getUsedColors(sink);
	}

	public void addLayer(AmiWebMapBoxLayer layer) {
		int position = this.layers.getPositionNoThrow(layer.getId());
		if (position != -1)
			this.layers.update(layer.getId(), layer);
		else {
			this.layers.add(layer.getId(), layer);
			layer.addToDomManager();
		}
		layer.setData(this.getService().getDmManager().getDmByAliasDotName(layer.getDmAliasDotName()).getResponseTableset().getTable(layer.getDmTableName()));
		onLayersChanged();
	}
	public void removeLayer(AmiWebMapBoxLayer layer) {
		this.layers.remove(layer.getId());
		layer.removeFromDomManager();
		onLayersChanged();
	}

	private void onLayersChanged() {
		for (int i = 0; i < this.layers.getSize(); i++)
			this.layers.getAt(i).setzPosition(i);
		this.htmlPortlet.onDataChanged();
	}

	public int getLayersCount() {
		return this.layers.getSize();
	}
	public AmiWebMapBoxLayer getLayerAt(int i) {
		return this.layers.getAt(i);
	}

	public AmiWebMapBoxLayer getLayerById(int layerId) {
		return this.layers.getNoThrow(layerId);
	}

	@Override
	public boolean hasSelectedRows(AmiWebDmLink link) {
		return !selected.isEmpty();
	}

	@Override
	public Table getSelectableRows(AmiWebDmLink link, byte type) {
		AmiWebMapBoxLayer layer = this.layers.getAt(0);//TODO: get appropriate layer for link;
		Table data = layer.getData();
		switch (type) {
			case ALL:
				return new BasicTable(data);
			case NONE:
				return new BasicTable(data.getColumns());
			case SELECTED:
				BasicTable r = new BasicTable(data.getColumns());
				for (LongIterator i = selected.iterator(); i.hasNext();) {
					long l = i.nextLong();
					int layerId = AmiWebMapBoxHtmlPortlet.long2LayerId(l);
					int pointId = AmiWebMapBoxHtmlPortlet.long2PointId(l);
					if (layerId == layer.getId())
						r.getRows().addRow(data.getRows().get(pointId).getValuesCloned());
				}
				return r;
			default:
				throw new RuntimeException("Bad type: " + type);
		}
	}

	//returns true if changed
	public boolean setIsSelected(int layerId, int pointId, boolean b) {
		final long l = AmiWebMapBoxHtmlPortlet.layerIdpointId2Long(layerId, pointId);
		if (b ? selected.add(l) : selected.remove(l)) {
			onSelectedChanged();
			return true;
		} else
			return false;

	}
	private void onSelectedChanged() {
		this.htmlPortlet.onSelectedChanged();
		for (AmiWebDmLink link : getDmLinksFromThisPortlet()) {
			AmiWebDmUtils.sendRequest(getService(), link);
		}
	}
	public boolean getIsSelected(int layerId, int pointId) {
		return selected.contains(AmiWebMapBoxHtmlPortlet.layerIdpointId2Long(layerId, pointId));
	}

	private LongSet selected = new LongSet();

	public boolean setSelected(int layerId, int pointId) {
		return false;
	}

	@Override
	public void clearUserSelection() {
		if (this.selected.isEmpty())
			return;
		this.selected.clear();
		onSelectedChanged();
	}

	public int getSelectedCount() {
		return this.selected.size();
	}

	protected LongSet getSelected() {
		return this.selected;
	}

	@Override
	public String getStyleType() {
		return AmiWebStyleTypeImpl_Mapbox.TYPE_MAPBOX;
	}
	@Override
	public void onStyleValueChanged(short key, Object old, Object nuw) {
		super.onStyleValueChanged(key, old, nuw);

		switch (key) {
			case AmiWebStyleConsts.CODE_SEL_CL:
				this.htmlPortlet.setSelectedColor((String) nuw);
				break;
			case AmiWebStyleConsts.CODE_SEL_BDR_CL:
				this.htmlPortlet.setSelectedBorderColor((String) nuw);
				break;
		}
	}

	public AmiWebDmTableSchema getDm() {
		Collection<AmiWebDmTableSchema> dm = AmiWebUtils.getUsedTableSchemas(this);
		return CH.first(dm);
	}

	@Override
	public Set<String> getUsedDmVariables(String dmName, String dmTable, Set<String> r) {
		for (AmiWebMapBoxLayer i : this.layers.values())
			if (OH.eq(dmName, i.getDmAliasDotName()) && OH.eq(dmTable, i.getDmTableName()))
				i.getDependencies((Set) r);
		return r;
	}

	@Override
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		super.onDmNameChanged(oldAliasDotName, dm);
		for (AmiWebMapBoxLayer i : this.layers.values())
			i.onDmNameChanged(oldAliasDotName, dm);
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		List<AmiWebDomObject> r = super.getChildDomObjects();
		CH.addAll(r, this.layers.values());
		return r;
	}
	@Override
	public String getPanelType() {
		return "map";
	}

	@Override
	public AmiWebPanelSettingsPortlet showSettingsPortlet() {
		return new AmiWebMapboxSettingsPortlet(generateConfig(), this);
	}
	@Override
	public void updateAri() {
		super.updateAri();
		for (Entry<Integer, AmiWebMapBoxLayer> i : this.layers)
			i.getValue().updateAri();
	}

	public void flagFormulasChanged() {
		this.htmlPortlet.onDataChanged();
	}

}
