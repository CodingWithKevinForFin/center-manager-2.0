package com.f1.ami.web.filter;

import java.util.Map;

import com.f1.ami.web.AmiWebAliasPortlet;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.structs.IntKeyMap;

public class AmiWebFilterWizPortlet extends GridPortlet implements FormPortletListener, ChooseDmListener, FormPortletContextMenuListener {

	private FormPortlet form;
	private FormPortletSelectField<Byte> displayTypeField;
	private FormPortletSelectField<String> display;
	private byte pos;
	private final FormPortletButton cancelButton;
	private final FormPortletButton createButton;
	private FormPortletButtonField dmButton;
	private AmiWebDmTableSchema dmTable;
	private FormPortletSelectField<String> aliasField;
	private AmiWebService service;
	private AmiWebAliasPortlet parent;
	private AmiWebAliasPortlet source;

	public AmiWebFilterWizPortlet(PortletConfig config, AmiWebAliasPortlet source, AmiWebDmTableSchema dm, byte pos) {
		super(config);
		this.service = AmiWebUtils.getService(config.getPortletManager());
		this.parent = source.getAmiParent();
		this.source = source;
		this.pos = pos;
		this.form = new FormPortlet(generateConfig());
		this.aliasField = this.form.addField(new FormPortletSelectField<String>(String.class, "Owning Layout:"));
		this.dmButton = this.form.addField(new FormPortletButtonField("Data To Filter:")).setHeight(35);
		this.displayTypeField = this.form.addField(new FormPortletSelectField<Byte>(Byte.class, "Type:"));
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES, "Checkboxes");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS, "Radios");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_SELECT_SINGLE, "Dropdown");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RANGE, "Range");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RANGE_SLIDER, "Range Slider");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_SEARCH, "Search");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX, "Multi Checkbox");
		this.displayTypeField.setValue(AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES);
		this.createButton = this.form.addButton(new FormPortletButton("Create Filter"));
		this.cancelButton = this.form.addButton(new FormPortletButton("Cancel"));
		this.form.addMenuListener(this);

		this.display = this.form.addField(new FormPortletSelectField<String>(String.class, "Filter On:"));
		this.dmTable = dm;
		this.display.sortOptionsByName();
		this.display.addOption(null, "--  Advanced --", null);
		this.setSuggestedSize(400, 200);
		this.addChild(form);
		this.form.addFormPortletListener(this);
		for (String s : service.getLayoutFilesManager().getAvailableAliasesDown(parent.getAmiLayoutFullAlias()))
			this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
		this.aliasField.setValue(source != null ? source.getAmiLayoutFullAlias() : parent.getAmiLayoutFullAlias());
		updateDatamodelButton();
	}
	private void updateDatamodelButton() {
		if (this.dmTable != null) {
			String table = this.dmTable.getName();
			String dmLabel = this.dmTable.getDm().getAmiLayoutFullAliasDotId();
			dmButton.setValue(dmLabel + " : " + table);
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
		this.display.clearOptions();
		if (this.dmTable != null)
			for (String s : this.dmTable.getColumnNames())
				this.display.addOption(s, s);
		this.display.sortOptionsByName();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.cancelButton) {
			close();
		} else if (button == this.createButton) {
			if (this.dmTable == null) {
				getManager().showAlert("Please choose a datamodel first");
				return;
			}
			String alias = this.aliasField.getValue();
			AmiWebScriptManagerForLayout sm = service.getScriptManager(alias);
			if (service.getLayoutFilesManager().getLayoutByFullAlias(alias).isReadonly()) {
				getManager().showAlert("Readonly layout: " + WebHelper.escapeHtml(AmiWebUtils.formatLayoutAlias(alias)));
				return;
			}
			if (!AmiWebUtils.isParentAliasOrSame(alias, this.dmTable.getDm().getAmiLayoutFullAlias())) {
				getManager().showAlert("Selected Datamodel is not visible from layout: " + WebHelper.escapeHtml(AmiWebUtils.formatLayoutAlias(alias)));
				return;
			}

			AmiWebDm dm = this.dmTable.getDm();
			AmiWebDmTableSchema table = this.dmTable;
			byte displayType = this.displayTypeField.getValue();
			Class dataType = table.getClassType(this.display.getValue());

			AmiWebFilterPortlet filter = (AmiWebFilterPortlet) service.getDesktop().newPortlet(AmiWebFilterPortlet.Builder.ID, alias);
			filter.setDisplayType(displayType, false);
			if (!filter.setDataType(dataType)) {
				String dataTypeString = sm.forType(dataType);
				this.getManager().showAlert("Invalid column data type (" + dataTypeString + ") for selected filter type");
			} else {
				filter.setUsedDatamodel(dm.getAmiLayoutFullAliasDotId(), table.getName());
				filter.setIsApplyToSourceTable(true);
				if (this.display.getValue() != null) {
					filter.setAmiTitle(AmiWebUtils.toPrettyName(this.display.getValue()), false);
					filter.setExpressions(this.display.getValue(), null, null, new IntKeyMap(), new StringBuilder());
					filter.onDmDataBeforeFilterChanged(null);
				}
				if (AmiWebDesktopPortlet.REPLACE == this.pos) {
					this.service.getDesktop().replacePortlet(this.source.getPortletId(), filter);
				} else if (AmiWebDesktopPortlet.POPOUT == this.pos) {
					this.service.getDesktop().addAdjacentTo(this.source.getPortletId(), filter, this.pos);
				} else {
					this.service.getDesktop().addAdjacentToStacked(this.source, filter, this.pos, true);
				}
				close();
			}
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {

	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.dmButton) {
			String dmName = null;
			if (this.dmTable != null && this.dmTable.getDm() != null) {
				dmName = this.dmTable.getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.parent.getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		}
	}
	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.dmTable = selectedDmTable;
		updateDatamodelButton();
	}
}
