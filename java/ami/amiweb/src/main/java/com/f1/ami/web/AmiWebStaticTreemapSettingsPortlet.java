package com.f1.ami.web;

import java.util.Set;

import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;

public class AmiWebStaticTreemapSettingsPortlet extends AmiWebTreemapSettingsPortlet implements FormPortletContextMenuListener, ChooseDmListener {

	private final AmiWebTreemapStaticPortlet staticTreemap;
	private final FormPortletButtonField dmButton;

	public AmiWebStaticTreemapSettingsPortlet(PortletConfig config, AmiWebTreemapStaticPortlet staticTreemap) {
		super(config, staticTreemap);
		this.staticTreemap = staticTreemap;

		FormPortlet settingsForm = getSettingsForm();
		settingsForm.addField(new FormPortletTitleField("Underlying Datamodel"), 0);
		this.dmButton = settingsForm.addField(new FormPortletButtonField(""), 1);

		settingsForm.addMenuListener(this);
		updateDatamodelButton();
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			Set<String> dmNames = this.staticTreemap.getUsedDmAliasDotNames();
			String dmAliasDotName = CH.first(dmNames);
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmAliasDotName, this, false, this.staticTreemap.getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		}
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.staticTreemap.setUsedDatamodel(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDatamodelButton();
	}

	private void updateDatamodelButton() {
		Set<String> dmNames = this.staticTreemap.getUsedDmAliasDotNames();
		String dmAliasDotName = CH.first(dmNames);
		AmiWebDm dm = this.staticTreemap.getService().getDmManager().getDmByAliasDotName(dmAliasDotName);
		if (dm != null) {
			Set<String> dmTables = this.staticTreemap.getUsedDmTables(dmAliasDotName);
			String dmTable = CH.first(dmTables);
			String dmLabel = dm.getAmiLayoutFullAliasDotId();
			this.dmButton.setValue(dmLabel + " : " + dmTable);
		} else {
			this.dmButton.setValue("&lt;No datamodel&gt;");
		}
	}

}
