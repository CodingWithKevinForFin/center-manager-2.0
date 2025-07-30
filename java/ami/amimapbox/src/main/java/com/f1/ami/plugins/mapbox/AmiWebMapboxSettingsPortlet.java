package com.f1.ami.plugins.mapbox;

import java.util.Set;

import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;

public class AmiWebMapboxSettingsPortlet extends AmiWebPanelSettingsPortlet implements FormPortletContextMenuListener, ChooseDmListener {

	private final AmiWebMapBoxPanel mapbox;
	private final FormPortletButtonField dmButton;
	private final FormPortletToggleButtonsField<Boolean> fitPoints;
	private final FormPortletToggleButtonsField<Boolean> clearOnDataStale;

	public AmiWebMapboxSettingsPortlet(PortletConfig config, AmiWebMapBoxPanel mapbox) {
		super(config, mapbox);
		FormPortlet settingsForm = getSettingsForm();
		this.mapbox = mapbox;
		this.fitPoints = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Reset zoom on data Change");
		this.fitPoints.addOption(true, "On");
		this.fitPoints.addOption(false, "Off");
		this.fitPoints.setValue(mapbox.isFitPoints());

		settingsForm.addField(new FormPortletTitleField("Underlying Datamodel"));
		this.dmButton = settingsForm.addField(new FormPortletButtonField(""));
		settingsForm.addField(this.fitPoints);

		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.setHelp("On: clears the data and shows hourglass when the underlying data model is running.<br>Off: keeps old (stale) data on screen while data model is running.");
		this.clearOnDataStale.addOption(true, "On");
		this.clearOnDataStale.addOption(false, "Off");
		this.clearOnDataStale.setValue(mapbox.isClearOnDataStale());
		settingsForm.addField(this.clearOnDataStale);

		//		settingsForm.addFormPortletListener(this);
		settingsForm.addMenuListener(this);
		updateDatamodelButton();
	}

	@Override
	protected void submitChanges() {
		this.mapbox.setFitPoints(this.fitPoints.getValue());
		this.mapbox.setClearOnDataStale(this.clearOnDataStale.getValue());
		super.submitChanges();
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		String currentDmName = CH.first(this.mapbox.getUsedDmAliasDotNames());
		String currentTableName = CH.first(this.mapbox.getUsedDmTables(currentDmName));
		this.mapbox.removeUsedDm(currentDmName, currentTableName);
		this.mapbox.addUsedDm(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDatamodelButton();
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			Set<String> dmNames = this.mapbox.getUsedDmAliasDotNames();
			String dmName = CH.first(dmNames);
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.mapbox.getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		}
	}

	private void updateDatamodelButton() {
		Set<String> dmNames = this.mapbox.getUsedDmAliasDotNames();
		String dmName = CH.first(dmNames);
		AmiWebDm dm = this.mapbox.getService().getDmManager().getDmByAliasDotName(dmName);
		if (dm != null) {
			Set<String> dmTables = this.mapbox.getUsedDmTables(dmName);
			String dmTable = CH.first(dmTables);
			String dmLabel = dm.getAmiLayoutFullAliasDotId();
			this.dmButton.setValue(dmLabel + " : " + dmTable);
		} else {
			this.dmButton.setValue("&lt;No datamodel&gt;");
		}
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 500;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 250;
	}
}
