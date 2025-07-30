package com.f1.ami.web;

import java.util.HashSet;
import java.util.Set;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebAggregateTableSettingsPortlet extends AmiWebAbstractTableSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private FormPortlet formBottom;
	private FormPortlet formA;

	private FormPortletSelectField<Byte> haltOnHidden;
	private FormPortletSelectField<Integer> updateTime;
	private AmiWebAggregateObjectTablePortlet target;
	private FormPortletMultiSelectField<String> typesField;
	//	private AmiCenterGetAmiSchemaResponse schema;
	private FormPortletToggleButtonsField<Boolean> enableSummaryToggle;
	private FormPortletToggleButtonsField<Boolean> groupVisibleOnlyToggle;
	private FormPortletToggleButtonsField<String> defaultRelationshipFilter;

	public AmiWebAggregateTableSettingsPortlet(PortletConfig config, AmiWebObjectTablePortlet target) {
		super(config, target);
		init();
		//		target.getService().sendRequestToBackend(getPortletId(), nw(AmiCenterGetAmiSchemaRequest.class));
	}
	@Override
	protected void initForms() {
		this.target = (AmiWebAggregateObjectTablePortlet) getPortlet();
		formA = getSettingsForm();
		addChild(formA);

		formA.addField(new FormPortletTitleField("On-Hidden Behavior"));
		haltOnHidden = formA.addField(new FormPortletSelectField<Byte>(Byte.class, ""));
		formA.addField(new FormPortletTitleField("Update 'Current Time' value"));
		updateTime = formA.addField(new FormPortletSelectField<Integer>(Integer.class, ""));
		formA.addField(new FormPortletTitleField("Default Relationship Filter"));
		defaultRelationshipFilter = formA.addField(new FormPortletToggleButtonsField<String>(String.class, ""));
		defaultRelationshipFilter.addOption(null, "Show All").addOption("false", "Hide All");
		defaultRelationshipFilter.setValue(target.getDefaultRelationshipFilter());
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_TRUE, "Stop processing when hidden, reconstruct when displayed");
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_FALSE, "Continue processing when hidden. Start on startup");
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_UNTIL_VISIBLE, "Continue processing when hidden. Start on first visible");
		haltOnHidden.setValue(target.getIsHaltOnHidden());
		haltOnHidden.setWidth(410);

		updateTime.addOption(500, "Twice Per Second");
		updateTime.addOption(1000, "Every Second");
		for (int i = 2; i <= 30; i++)
			updateTime.addOption(i * 1000, "Every " + i + " Seconds");
		updateTime.setValueNoThrow(target.getCurrentTimeUpdateFrequencyMs());

		formA.addField(new FormPortletTitleField("Grouping Columns for Aggregation:"));
		this.groupVisibleOnlyToggle = formA
				.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, "").addOption(Boolean.FALSE, "Hidden And Visible").addOption(Boolean.TRUE, "Only Visible"));
		formA.addField(new FormPortletTitleField("Summary of rows"));
		this.enableSummaryToggle = formA.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, ""));
		this.enableSummaryToggle.addOption(true, "Enabled");
		this.enableSummaryToggle.addOption(false, "Disabled");
		this.enableSummaryToggle.setValue(target.isRollupEnabled());
		this.groupVisibleOnlyToggle.setValue(target.isAggregateOnVisibleColumnsOnly());

		formA.addField(new FormPortletTitleField("Types"));
		typesField = formA.addField(new FormPortletMultiSelectField<String>(String.class, "").setSize(300).setWidth(400));

		Set<String> types = new HashSet<String>();
		types.addAll(target.getLowerRealtimeIds());
		for (String s : CH.sort(types, SH.COMPARATOR_CASEINSENSITIVE_STRING))
			typesField.addOption(s, s);

		typesField.setValue(target.getLowerRealtimeIds());

		formBottom = addChild(new FormPortlet(generateConfig()), 0, 1);
		formA.getFormPortletStyle().setLabelsWidth(160);
		setRowSize(1, 40);
		formBottom.addFormPortletListener(this);
		this.formBottom.addButton(getSubmitButton());
		this.formBottom.addButton(getCancelButton());

		getManager().onPortletAdded(this);
		super.initForms();
	}

	@Override
	protected void submitChanges() {
		Set<String> selected = typesField.getValue();
		if (CH.isEmpty(selected)) {
			getManager().showAlert("Must select atleast one type");
			return;
		}
		target.setDataTypes(selected);
		target.setCurrentTimeUpdateFrequency(updateTime.getValue());
		target.setIsHaltOnHidden(haltOnHidden.getValue());
		target.setRollupEnabled(enableSummaryToggle.getValue());
		target.setAggregateOnVisibleColumnsOnly(groupVisibleOnlyToggle.getValue());
		target.setDefaultRelationshipFilter(this.defaultRelationshipFilter.getValue());
		this.groupVisibleOnlyToggle.setValue(target.isAggregateOnVisibleColumnsOnly());
		super.submitChanges();
	}

	@Override
	protected boolean verifyChanges() {
		Set<String> selected = typesField.getValue();
		if (CH.isEmpty(selected)) {
			getManager().showAlert("Must select atleast one type");
			return false;
		}
		return super.verifyChanges();
	}

	private void init() {
		//		todo();
		//		if (action instanceof AmiCenterGetAmiSchemaResponse) {
		//			this.schema = (AmiCenterGetAmiSchemaResponse) action;
		//		AmiWebManager am = target.getService().getAgentManager();
		//		for (AmiCenterSchemaTypeRecord record : schema.getTypeCounts()) {
		//			String key = am.getAmiKeyStringFromPool(record.getObjectType());
		for (String key : target.getService().getWebManagers().getAllTableTypes(this.target.getAmiLayoutFullAlias())) {
			if (this.typesField.containsOption(key))
				continue;
			this.typesField.addOption(key, key);
		}
	}
	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		if (isColorFormulaField(field)) {
			BasicWebMenu r = new BasicWebMenu();
			WebMenu variables = new BasicWebMenu("Grouping Variables", true);
			for (String c : ((AmiWebAggregateObjectTablePortlet) this.target).getGroupByColumnIds()) {
				String title = this.target.getCustomDisplayColumn(c).getTitle(false);
				variables.add(new BasicWebMenuLink(title, true, "var_" + title).setAutoclose(false).setCssStyle("_fm=courier"));
			}
			AmiWebMenuUtils.createVariablesMenu(r, true, this.target);
			variables.sort();
			r.add(new BasicWebMenuDivider());
			r.add(variables);
			AmiWebMenuUtils.createOperatorsMenu(r, this.target.getService(), this.target.getAmiLayoutFullAlias());
			r.add(new BasicWebMenuDivider());
			AmiWebMenuUtils.createColorsMenu(r, this.target.getStylePeer());
			return r;
		} else {
			return super.createMenu(formPortlet, field, cursorPosition);
		}
	}
	//	protected String replaceTitlesWithVarsInFormula(String value) {
	//		return this.target.replaceTitlesWithVarsInFormula("", value);
	//	}
}
