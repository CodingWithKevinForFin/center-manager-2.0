package com.f1.ami.web.form;

import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.suite.web.portal.PortletListener;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.SH;

class AmiWebQueryFormSettingsPortlet extends AmiWebPanelSettingsPortlet
		implements ChooseDmListener, FormPortletContextMenuListener, PortletListener, FormPortletContextMenuFactory {

	private FormPortletButtonField dmButton;
	private FormPortletNumericRangeField snapSize;
	final private AmiWebQueryFormPortlet queryForm;

	public AmiWebQueryFormSettingsPortlet(PortletConfig config, AmiWebQueryFormPortlet amiWebQueryFormPortlet) {
		super(config, amiWebQueryFormPortlet);
		queryForm = amiWebQueryFormPortlet;
		this.snapSize = new FormPortletNumericRangeField("Snapsize");
		this.snapSize.setRange(4, 50).setDecimals(0).setStep(1).setNullable(true);
		if (queryForm.getSnapSize() == -1)
			this.snapSize.setValue(null);
		else
			this.snapSize.setValue(amiWebQueryFormPortlet.getSnapSize());
		FormPortlet settingsForm = getSettingsForm();
		settingsForm.addField(this.snapSize);
		settingsForm.addField(new FormPortletTitleField("Underlying Data Model"));
		dmButton = settingsForm.addField(new FormPortletButtonField("")).setHeight(35);
		settingsForm.addMenuListener(this);
		settingsForm.setMenuFactory(this);
		updateDatamodelButton();
	}

	@Override
	protected void submitChanges() {
		if (this.snapSize.getValue() == null)
			this.queryForm.setSnapSize(-1);
		else
			this.queryForm.setSnapSize(this.snapSize.getIntValue());
		super.submitChanges();
	}

	private void updateDatamodelButton() {
		AmiWebDmTableSchema dm = queryForm.getUsedDm();
		if (dm != null) {
			dmButton.setValue(dm.getDm().getAmiLayoutFullAliasDotId() + " : " + dm.getName());
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			String dmName = null;
			if (getDm() != null && getDm().getDm() != null) {
				dmName = getDm().getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.queryForm.getAmiLayoutFullAlias());
			t.setAllowNoSelection(true);
			getManager().showDialog("Select Datamodel", t);
		}
	}
	@Override
	public void onPortletAdded(Portlet newPortlet) {
	}
	@Override
	public void onPortletClosed(Portlet oldPortlet) {
		updateDatamodelButton();
	}

	@Override
	public void onSocketConnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onSocketDisconnected(PortletSocket initiator, PortletSocket remoteSocket) {
	}
	@Override
	public void onPortletParentChanged(Portlet newPortlet, PortletContainer oldParent) {
	}
	@Override
	public void onJavascriptQueued(Portlet portlet) {
	}
	@Override
	public void onPortletRenamed(Portlet portlet, String oldName, String newName) {
	}
	@Override
	public void onLocationChanged(Portlet portlet) {
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebDmTableSchema dm = getDm();
		if (dm != null) {
			for (String colName : CH.sort(dm.getColumnNames(), SH.COMPARATOR_CASEINSENSITIVE_STRING)) {
				r.add(new BasicWebMenuLink(colName, true, "_webCol_" + colName));
			}
		}
		return r;
	}
	public AmiWebDmTableSchema getDm() {
		return queryForm.getUsedDm();
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		if (selectedDmTable == null)
			this.queryForm.setUsedDatamodel(null, null);
		else
			this.queryForm.setUsedDatamodel(selectedDmTable.getDm().getAmiLayoutFullAliasDotId(), selectedDmTable.getName());
		updateDatamodelButton();

	}
	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 500;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 300;
	}
}
