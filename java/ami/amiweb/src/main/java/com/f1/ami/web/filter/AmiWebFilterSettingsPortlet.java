package com.f1.ami.web.filter;

import java.util.Collection;
import java.util.Map;

import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet;
import com.f1.ami.web.dm.portlets.AmiWebDmChooseDmTablePorlet.ChooseDmListener;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;

public class AmiWebFilterSettingsPortlet extends AmiWebPanelSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory, ChooseDmListener {

	private AmiWebFilterPortlet target;
	private FormPortletButtonField dmButton;
	final private FormPortletTextField formatField;
	final private FormPortletTextField sortField;
	final private FormPortletTextField colorField;
	final private FormPortletCheckboxField isApplyField;
	final private FormPortletCheckboxField clearFilterOnRequeryField;
	private AmiWebDmTableSchema usedDm;
	private FormPortletButtonField addDatamodelButton;
	private IntKeyMap<FormPortletButtonField> linkButtons = new IntKeyMap<FormPortletButtonField>();
	private FormPortletSelectField<Byte> displayTypeField;
	private final FormPortletNumericRangeField maxCheckboxesField = new FormPortletNumericRangeField("Max Number of Checkboxes:", 1, 10000, 0);

	public AmiWebFilterSettingsPortlet(PortletConfig config, AmiWebFilterPortlet target) {
		super(config, target);
		this.target = target;
		FormPortlet settingsForm = getSettingsForm();
		settingsForm.addField(new FormPortletTitleField("Underlying Data Model:"));
		dmButton = settingsForm.addField(new FormPortletButtonField("")).setHeight(35);
		String formatExpression = target.getFormatExpression(false);
		String sortExpression = target.getSortExpression(false);
		String colorExpression = target.getColorExpression(false);
		byte displayType = target.getDisplayType(false);
		this.isApplyField = settingsForm.addField(new FormPortletCheckboxField("Auto-Apply Filter:").setValue(this.target.getIsApplyToSourceTable()));
		this.clearFilterOnRequeryField = settingsForm.addField(new FormPortletCheckboxField("Clear On Requery:").setValue(this.target.getClearOnRequery()));
		settingsForm.addField(new FormPortletTitleField("Required:"));
		this.displayTypeField = settingsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "Type:"));
		this.formatField = settingsForm.addField(new FormPortletTextField("Values:").setValue(formatExpression)).setWidth(FormPortletField.WIDTH_STRETCH);
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RANGE, "Range");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RANGE_SLIDER, "Range Slider");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_SEARCH, "Search");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES, "Checkboxes");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_SELECT_SINGLE, "Dropdown");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_MULTICHECKBOX, "Multi Checkbox");
		this.displayTypeField.addOption(AmiWebFilterPortlet.DISPLAY_TYPE_RADIOS, "Radios");
		byte displayTypeFieldValue = displayType == 0 ? AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES : displayType;
		this.displayTypeField.setValue(displayTypeFieldValue);
		this.maxCheckboxesField.setSliderHidden(true);
		this.maxCheckboxesField.setValue(this.target.getMaxOptions(false));

		settingsForm.addField(this.maxCheckboxesField);
		updateCheckboxLimitField();
		formatField.setHasButton(true);
		settingsForm.addField(new FormPortletTitleField("Optional:"));
		this.colorField = settingsForm.addField(new FormPortletTextField("Color:").setValue(colorExpression)).setWidth(FormPortletField.WIDTH_STRETCH);
		colorField.setHasButton(true);
		this.colorField.setVisible(displayTypeFieldValue == AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES);
		this.sortField = settingsForm.addField(new FormPortletTextField("Sorting:").setValue(sortExpression)).setWidth(FormPortletField.WIDTH_STRETCH);
		sortField.setHasButton(true);
		settingsForm.addField(new FormPortletTitleField("Linked To..."));

		for (AmiWebFilterLink i : this.target.getLinks().values()) {
			AmiWebDm t = this.target.getService().getDmManager().getDmByAliasDotName(i.getTargetDmAliasDotName());
			if (t != null) {
				FormPortletButtonField btn = settingsForm.addField(new FormPortletButtonField("").setValue(t.getDmName() + " : " + i.getDmTableName()));
				btn.setCorrelationData(i.clone());
				this.linkButtons.put(i.getId(), btn);
			}
		}

		this.addDatamodelButton = settingsForm.addField(new FormPortletButtonField("").setValue("<i>Link to additional datamodel..."));

		settingsForm.getFormPortletStyle().setLabelsWidth(150);
		this.setSuggestedSize(500, 500);

		Collection<AmiWebDmTableSchema> dm = AmiWebUtils.getUsedTableSchemas(this.target);
		this.usedDm = CH.first(dm);
		updateDatamodelButton();

		settingsForm.addMenuListener(this);
		settingsForm.setMenuFactory(this);
	}

	@Override
	public void onClosed() {
		if (isSubmitted()) {
			return;
		}
		String title = getTitle();
		// managed is to ensure cancel works
		boolean managed = this.target.getService().getDomObjectsManager().isManaged(this.target.getAri());
		if (title == "Create Filter" && managed) {
			this.target.close();
		}

	}

	private void updateCheckboxLimitField() {
		this.maxCheckboxesField.setVisible(target.needMaxOptions());
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		BasicWebMenu r = new BasicWebMenu();
		AmiWebMenuUtils.createOperatorsMenu(r, this.target.getService(), this.target.getAmiLayoutFullAlias());
		r.add(new BasicWebMenuDivider());
		if (this.usedDm != null)
			AmiWebMenuUtils.createVariablesMenu(r, false, this.target);
		else {
			r.add(new BasicWebMenu("Variables", true, new BasicWebMenuLink("(Select underlying datamodel first)", false, "")));
		}
		if (this.colorField == field) {
			AmiWebMenuUtils.createColorsMenu(r, this.getPortlet().getStylePeer());
		}
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		if (node == this.formatField || node == this.colorField || node == this.sortField) {
			AmiWebMenuUtils.processContextMenuAction(this.target.getService(), action, (FormPortletTextField) node);
		} else if (node == this.dmButton) {
			String dmName = null;
			if (this.usedDm != null && this.usedDm.getDm() != null) {
				dmName = this.usedDm.getDm().getDmName();
			}
			AmiWebDmChooseDmTablePorlet t = new AmiWebDmChooseDmTablePorlet(generateConfig(), dmName, this, false, this.getTarget().getAmiLayoutFullAlias());
			getManager().showDialog("Select Datamodel", t);
		} else if (node == this.addDatamodelButton) {
			AmiWebFilterLinkSettingsPortlet t = new AmiWebFilterLinkSettingsPortlet(generateConfig(), this, -1, null, null, null);
			getManager().showDialog("Add datamodel", t);
		} else {
			for (FormPortletButtonField i : this.linkButtons.values()) {
				if (i == node) {
					AmiWebFilterLink link = (AmiWebFilterLink) i.getCorrelationData();
					AmiWebFilterLinkSettingsPortlet t = new AmiWebFilterLinkSettingsPortlet(generateConfig(), this, link.getId(), link.getTargetDmAliasDotName(),
							link.getDmTableName(), link.getFormula());
					getManager().showDialog("Add datamodel", t);
					break;
				}
			}
		}
	}

	@Override
	protected void onCancelled() {
		if (!filterHasDesktopWindow()) {
			this.target.close();
		}
		super.onCancelled();
	}
	@Override
	protected boolean verifyChanges() {
		if (this.usedDm == null) {
			getManager().showDialog("Select Datamodel",
					new ConfirmDialogPortlet(generateConfig(), "No datamodel selected. Please choose a datamodel to filter", ConfirmDialogPortlet.TYPE_MESSAGE));
			return false;
		}
		if (SH.isnt(this.formatField.getValue())) {
			getManager().showAlert("Required Field Missing: " + SH.beforeFirst(this.formatField.getTitle(), ':'));
			return false;
		}
		// Create new filter window if it doesn't already exist
		this.target.setUsedDatamodel(usedDm.getDm().getAmiLayoutFullAliasDotId(), usedDm.getName());
		StringBuilder sb = new StringBuilder();
		IntKeyMap<AmiWebFilterLink> links = new IntKeyMap<AmiWebFilterLink>();
		for (Node<FormPortletButtonField> i : this.linkButtons) {
			AmiWebFilterLink link = (AmiWebFilterLink) i.getValue().getCorrelationData();
			links.put(link.getId(), link);
		}
		this.target.setDisplayType(this.displayTypeField.getValue(), false);
		if (!this.target.setExpressions(formatField.getValue(), sortField.getValue(), colorField.getValue(), links, sb)) {
			getManager().showAlert(sb.toString());
			return false;
		}
		return super.verifyChanges();
	}
	@Override
	protected void submitChanges() {
		if (this.usedDm == null) {
			getManager().showDialog("Select Datamodel",
					new ConfirmDialogPortlet(generateConfig(), "No datamodel selected. Please choose a datamodel to filter", ConfirmDialogPortlet.TYPE_MESSAGE));
			return;
		}
		if (SH.isnt(this.formatField.getValue())) {
			getManager().showAlert("Required Field Missing: " + SH.beforeFirst(this.formatField.getTitle(), ':'));
			return;
		}
		// Create new filter window if it doesn't already exist
		if (!filterHasDesktopWindow()) {
			AmiWebUtils.createNewDesktopPortlet(this.target.getService().getDesktop(), this.target);
		}
		this.target.setUsedDatamodel(usedDm.getDm().getAmiLayoutFullAliasDotId(), usedDm.getName());
		StringBuilder sb = new StringBuilder();
		IntKeyMap<AmiWebFilterLink> links = new IntKeyMap<AmiWebFilterLink>();
		for (Node<FormPortletButtonField> i : this.linkButtons) {
			AmiWebFilterLink link = (AmiWebFilterLink) i.getValue().getCorrelationData();
			links.put(link.getId(), link);
		}
		this.target.setDisplayType(this.displayTypeField.getValue(), false);
		if (!this.target.setExpressions(formatField.getValue(), sortField.getValue(), colorField.getValue(), links, sb)) {
			getManager().showAlert(sb.toString());
			return;
		}
		if (target.needMaxOptions()) {
			this.target.setMaxOptions(this.maxCheckboxesField.getValue().intValue(), false);
		}

		this.target.setIsApplyToSourceTable(this.isApplyField.getBooleanValue());
		this.target.setClearOnRequery(this.clearFilterOnRequeryField.getBooleanValue());
		this.target.onDmDataBeforeFilterChanged(null);
		super.submitChanges();
	}
	private boolean filterHasDesktopWindow() {
		return !(PortletHelper.findParentByType(this.target, AmiWebDesktopPortlet.class) == null);
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (this.displayTypeField == field) {
			this.colorField.setVisible(this.displayTypeField.getValue().byteValue() == AmiWebFilterPortlet.DISPLAY_TYPE_CHECKBOXES);
			updateCheckboxLimitField();
		}

		else
			super.onFieldValueChanged(portlet, field, attributes);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public AmiWebDmTableSchema getDm() {
		return this.usedDm;
	}

	@Override
	public void onDmSelected(AmiWebDmTableSchema selectedDmTable) {
		this.usedDm = selectedDmTable;
		updateDatamodelButton();
	}
	private void updateDatamodelButton() {
		if (usedDm != null) {
			dmButton.setValue(usedDm.getDm().getAmiLayoutFullAliasDotId() + " : " + usedDm.getName());
		} else {
			dmButton.setValue("&lt;No datamodel&gt;");
		}
	}

	public AmiWebFilterPortlet getTarget() {
		return this.target;
	}
	public boolean updateLink(int linkId, AmiWebDmTableSchema usedDm, String formula, StringBuilder errorSink) {
		String dmName = usedDm.getDm().getAmiLayoutFullAliasDotId();
		String dmAliasDotName = usedDm.getDm().getAmiLayoutFullAliasDotId();
		String dmTableName = usedDm.getName();
		for (Node<FormPortletButtonField> btn : this.linkButtons) {
			AmiWebFilterLink other = (AmiWebFilterLink) btn.getValue().getCorrelationData();
			if (other.getId() != linkId && OH.eq(dmAliasDotName, other.getTargetDmAliasDotName()) && OH.eq(dmTableName, other.getDmTableName())) {
				errorSink.append("You already have a filter on:<BR><B> ").append(dmName).append(" : ").append(dmTableName);
				return false;
			}
		}
		if (linkId == -1) {
			Integer i = OH.max(this.target.getLinks().keys());
			if (i == null)
				i = 0;
			AmiWebFilterLink link = new AmiWebFilterLink(i + 1, dmAliasDotName, dmTableName, formula, this.target);
			FormPortletButtonField btn = getSettingsForm().addFieldBefore(this.addDatamodelButton, new FormPortletButtonField("").setValue(dmName + " : " + dmTableName));
			btn.setCorrelationData(link);
			this.linkButtons.put(link.getId(), btn);
		} else {
			FormPortletButtonField btn = this.linkButtons.get(linkId);
			AmiWebFilterLink link = (AmiWebFilterLink) btn.getCorrelationData();
			link.setDmAliasDotName(dmAliasDotName);
			link.setDmTableName(dmTableName);
			link.setFormula(formula);
			btn.setValue(dmName + " : " + dmTableName);
		}
		return true;

	}
	public void removeLink(int linkId) {
		getSettingsForm().removeField(this.linkButtons.remove(linkId));
	}
}
