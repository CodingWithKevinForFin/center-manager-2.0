package com.f1.ami.web.tree;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;

public class AmiWebStaticTreeEditSettingsPortlet extends AmiWebTreeSettingsPortlet<AmiWebStaticTreePortlet>
		implements FormPortletContextMenuListener, FormPortletContextMenuFactory, ChooseDmListener {

	private final FormPortletButtonField dmButton;
	private final FormPortletToggleButtonsField<Boolean> clearOnDataStale;

	public AmiWebStaticTreeEditSettingsPortlet(PortletConfig config, AmiWebStaticTreePortlet treegrid) {
		super(config, treegrid);

		FormPortlet settingsForm = getSettingsForm();
		settingsForm.addField(new FormPortletTitleField("Underlying Datamodel"));
		this.dmButton = settingsForm.addField(new FormPortletButtonField("")).setHeight(35);

		this.clearOnDataStale = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Clear on data stale");
		this.clearOnDataStale.setHelp("On: clears the data and shows hourglass when the underlying data model is running.<br>Off: keeps old (stale) data on screen while data model is running.");
		this.clearOnDataStale.addOption(true, "On");
		this.clearOnDataStale.addOption(false, "Off");
		this.clearOnDataStale.setValue(treegrid.isClearOnDataStale());
		settingsForm.addField(this.clearOnDataStale);

		updateDatamodelButton();

	}
	@Override
	protected boolean verifyChanges() {
		return super.verifyChanges();
	}
	@Override
	protected void submitChanges() {
		this.treegrid.setClearOnDataStale(this.clearOnDataStale.getValue());
		super.submitChanges();
		treegrid.rebuildAmiData();
	}

	private void updateDatamodelButton() {
		AmiWebDmTableSchema dm = this.treegrid.getDm();
		if (dm != null) {
			String table = dm.getName();
			String dmLabel = dm.getDm().getAmiLayoutFullAliasDotId();
			this.dmButton.setValue(dmLabel + " : " + table);
		} else
			dmButton.setValue("&lt;No datamodel&gt;");
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		//		AmiWebDmTableSchema dm = this.treegrid.getDm();
		//		String currentTableName = dm.getName();
		//		this.treegrid.removeUsedDm(dm.getDm().getAliasDotName(), currentTableName);
		//		this.treegrid.addUsedDm(selectedDmTable.getDm().getAliasDotName(), selectedDmTable.getName());
		this.treegrid.setUsedDatamodel(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDatamodelButton();
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			String dmName = null;
			if (this.treegrid != null && this.treegrid.getDm() != null && this.treegrid.getDm().getDm() != null) {
				dmName = this.treegrid.getDm().getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.treegrid.getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		} else {
			AmiWebMenuUtils.processContextMenuAction(getPortlet().getService(), action, node);
		}
	}

}
