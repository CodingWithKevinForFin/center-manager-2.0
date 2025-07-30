package com.f1.ami.web;

import java.util.HashSet;
import java.util.Set;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletMultiSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.utils.CH;
import com.f1.utils.SH;

public class AmiWebRealTimeTableSettingsPortlet extends AmiWebAbstractTableSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory {

	private FormPortlet formBottom;
	private FormPortlet formA;

	private FormPortletSelectField<Byte> haltOnHidden;
	private FormPortletSelectField<Integer> updateTime;
	private AmiWebObjectTablePortlet target;
	private FormPortletMultiSelectField<String> typesField;
	//private AmiCenterGetAmiSchemaResponse schema;
	private FormPortletToggleButtonsField<Boolean> enableSummaryToggle;
	private FormPortletToggleButtonsField<String> defaultRelationshipFilter;

	public AmiWebRealTimeTableSettingsPortlet(PortletConfig config, AmiWebObjectTablePortlet target) {
		super(config, target);
		init();
		//		todo();
		//		target.getService().sendRequestToBackend(getPortletId(), nw(AmiCenterGetAmiSchemaRequest.class));
	}
	@Override
	protected void initForms() {
		this.target = (AmiWebObjectTablePortlet) getPortlet();
		formA = getSettingsForm();
		addChild(formA, 0, 0);

		formA.addField(new FormPortletTitleField("On-Hidden Behavior"));
		haltOnHidden = formA.addField(new FormPortletSelectField<Byte>(Byte.class, ""));
		formA.addField(new FormPortletTitleField("Update 'Current Time' value"));
		updateTime = formA.addField(new FormPortletSelectField<Integer>(Integer.class, ""));
		formA.addField(new FormPortletTitleField("Default Relationship Filter"));
		defaultRelationshipFilter = formA.addField(new FormPortletToggleButtonsField<String>(String.class, ""));
		defaultRelationshipFilter.addOption(null, "Show All").addOption("false", "Hide All");
		defaultRelationshipFilter.setValue(target.getDefaultRelationshipFilter());
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_TRUE, "Stop processing when hidden, reconstruct when displayed");
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_FALSE, "Always process, even when hidden");
		haltOnHidden.addOption(AmiWebObjectTablePortlet.HAH_UNTIL_VISIBLE, "Don't process until visible but continue processing when rehidden");
		haltOnHidden.setValue(target.getIsHaltOnHidden());
		haltOnHidden.setWidth(410);

		updateTime.addOption(500, "Twice Per Second");
		updateTime.addOption(1000, "Every Second");
		for (int i = 2; i <= 30; i++)
			updateTime.addOption(i * 1000, "Every " + i + " Seconds");
		updateTime.setValueNoThrow(target.getCurrentTimeUpdateFrequencyMs());

		formA.addField(new FormPortletTitleField("Summary of rows"));
		this.enableSummaryToggle = formA.addField(new FormPortletToggleButtonsField<Boolean>(Boolean.class, ""));
		this.enableSummaryToggle.addOption(true, "Enabled");
		this.enableSummaryToggle.addOption(false, "Disabled");
		this.enableSummaryToggle.setValue(target.isRollupEnabled());

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
	protected boolean verifyChanges() {
		Set<String> selected = typesField.getValue();
		if (CH.isEmpty(selected)) {
			getManager().showAlert("Must select atleast one type");
			return false;
		}
		return super.verifyChanges();
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
		target.setDefaultRelationshipFilter(this.defaultRelationshipFilter.getValue());
		super.submitChanges();
	}

	//	@Override
	//	public void onBackendResponse(ResultMessage<Action> result) {
	//		Action action = result.getAction();
	//		if (action instanceof AmiCenterGetAmiSchemaResponse) {
	public void init() {
		//			this.schema = (AmiCenterGetAmiSchemaResponse) action;
		for (String key : target.getService().getWebManagers().getAllTableTypes(this.target.getAmiLayoutFullAlias())) {
			if (this.typesField.containsOption(key))
				continue;
			this.typesField.addOption(key, key);
		}
	}
	//	}
	//	@Override
	//	protected AmiCenterGetAmiSchemaResponse getSchema() {
	//		return this.schema;
	//	}
}
