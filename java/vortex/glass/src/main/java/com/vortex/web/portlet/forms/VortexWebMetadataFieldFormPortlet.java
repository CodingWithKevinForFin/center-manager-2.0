package com.vortex.web.portlet.forms;

import java.util.Map;

import com.f1.base.Action;
import com.f1.container.ResultMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.vortexcommon.msg.agent.VortexAgentEntity;
import com.f1.vortexcommon.msg.eye.VortexEyeMetadataField;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldRequest;
import com.f1.vortexcommon.msg.eye.reqres.VortexEyeManageMetadataFieldResponse;
import com.vortex.web.VortexWebEyeService;

public class VortexWebMetadataFieldFormPortlet extends VortexWebMetadataFormPortlet {

	final private FormPortletSelectField<Byte> typesField;
	final private FormPortletButton submitButton;
	final private VortexWebEyeService service;
	final private FormPortletCheckboxField machinesCheckbox;
	final private FormPortletCheckboxField deploymentsCheckbox;
	final private FormPortletCheckboxField buildProceduresCheckbox;
	final private FormPortletCheckboxField expectationsCheckbox;
	final private FormPortletTextField keyCodeField;
	final private FormPortletTextField titleField;
	//final private FormPortletCheckboxField requiredField;
	final private FormPortletTextField descriptionField;
	final private FormPortletNumericRangeField maxLengthField;
	final private FormPortletTextField minValueField;
	final private FormPortletTextField maxValueField;
	final private FormPortletTextAreaField enumsField;
	private VortexEyeMetadataField toEdit;
	final private FormPortletCheckboxField dataserversCheckbox;
	final private FormPortletCheckboxField deploymentSetsCheckbox;
	final private FormPortletCheckboxField buildResultsCheckbox;
	final private FormPortletCheckboxField backupDestinationCheckbox;
	final private FormPortletCheckboxField scheduledTaskCheckbox;
	final private FormPortletCheckboxField backupCheckbox;

	public VortexWebMetadataFieldFormPortlet(PortletConfig config) {
		super(config, "metadata.jpg");
		setIconToAdd();

		this.machinesCheckbox = addField(new FormPortletCheckboxField("Applicable to Machines"));
		this.expectationsCheckbox = addField(new FormPortletCheckboxField("Applicable to Alerts"));
		this.dataserversCheckbox = addField(new FormPortletCheckboxField("Applicable to Data Servers"));
		this.backupCheckbox = addField(new FormPortletCheckboxField("Applicable to Managed Directories"));

		this.deploymentsCheckbox = addField(new FormPortletCheckboxField("Applicable to Deployments"));
		this.buildProceduresCheckbox = addField(new FormPortletCheckboxField("Applicable to Build Procedures"));
		this.deploymentSetsCheckbox = addField(new FormPortletCheckboxField("Applicable to Deployment Sets"));
		this.buildResultsCheckbox = addField(new FormPortletCheckboxField("Applicable to Build Results"));
		this.backupDestinationCheckbox = addField(new FormPortletCheckboxField("Applicable to Backup Destinations"));
		this.scheduledTaskCheckbox = addField(new FormPortletCheckboxField("Applicable to Scheduled Tasks"));

		this.typesField = addField(new FormPortletSelectField<Byte>(Byte.class, "Types"));
		typesField.addOption(VortexEyeMetadataField.VALUE_TYPE_STRING, "String");
		typesField.addOption(VortexEyeMetadataField.VALUE_TYPE_BOOLEAN, "Boolean");
		typesField.addOption(VortexEyeMetadataField.VALUE_TYPE_DOUBLE, "Double");
		typesField.addOption(VortexEyeMetadataField.VALUE_TYPE_ENUM, "Enumeration");
		typesField.addOption(VortexEyeMetadataField.VALUE_TYPE_INT, "Integer");
		typesField.setValue(VortexEyeMetadataField.VALUE_TYPE_ENUM);

		this.keyCodeField = this.addField(new FormPortletTextField("Key Code")).setMaxChars(8).setHelp("Keep this short as this will be the tag that is stored");
		this.titleField = this.addField(new FormPortletTextField("Title")).setWidth(FormPortletTextField.WIDTH_STRETCH).setMaxChars(64)
				.setHelp("What the user will see in the column heading");
		this.descriptionField = this.addField(new FormPortletTextField("Description")).setWidth(FormPortletTextField.WIDTH_STRETCH);
		//this.requiredField = this.addField(new FormPortletCheckboxField("Required Field"));
		//this.defaultValueField = this.addField(new FormPortletTextField("Value for Existing Entities"));

		this.maxLengthField = this.addField(new FormPortletNumericRangeField("Max Length")).setRange(1, 100).setValue(4);
		this.minValueField = this.addField(new FormPortletTextField("Min Value"));
		this.maxValueField = this.addField(new FormPortletTextField("Max Value"));
		this.enumsField = this.addField(new FormPortletTextAreaField("Enums(key=description, 1 per line)"));
		this.formPortlet.setLabelsWidth(200);

		service = (VortexWebEyeService) getManager().getService(VortexWebEyeService.ID);
		submitButton = addButton(new FormPortletButton("submit metadata field"));
		setSize(500, 500);

		updateFields();
	}

	private void updateFields() {
		removeFieldNoThrow(maxLengthField);
		removeFieldNoThrow(minValueField);
		removeFieldNoThrow(maxValueField);
		removeFieldNoThrow(enumsField);
		//if (requiredField.getBooleanValue())
		//addFieldNoThrow(defaultValueField);
		//else
		//removeFieldNoThrow(defaultValueField);
		switch (typesField.getValue()) {
			case VortexEyeMetadataField.VALUE_TYPE_STRING:
				addField(maxLengthField);
				break;
			case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
				break;
			case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
			case VortexEyeMetadataField.VALUE_TYPE_INT:
				addField(minValueField);
				addField(maxValueField);
				break;
			case VortexEyeMetadataField.VALUE_TYPE_ENUM:
				addField(enumsField);
				break;
		}
	}

	@Override
	protected void onUserChangedValue(FormPortletField<?> field, Map<String, String> attributes) {
		super.onUserChangedValue(field, attributes);
		if (field == this.typesField)// || field == this.requiredField)
			updateFields();
	}

	public void setMetadataFieldToEdit(VortexEyeMetadataField field) {
		setMetadataFieldToCopy(field);
		this.toEdit = field;
		this.keyCodeField.setDisabled(true);
		this.submitButton.setName("Update metadata Field");
		setIconToEdit();
	}
	public void setMetadataFieldToCopy(VortexEyeMetadataField field) {
		final long types = field.getTargetTypes();
		this.machinesCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_MACHINE));
		this.deploymentsCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_DEPLOYMENT));
		this.buildProceduresCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_BUILD_PROCEDURE));
		this.expectationsCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_EXPECTATION));
		this.dataserversCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_DB_SERVER));
		this.deploymentSetsCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_DEPLOYMENT_SET));
		this.buildResultsCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_BUILD_RESULT));
		this.backupDestinationCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_BACKUP_DESTINATION));
		this.scheduledTaskCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_SCHEDULED_TASK));
		this.backupCheckbox.setValue(MH.anyBits(types, VortexAgentEntity.MASK_TYPE_BACKUP));

		this.descriptionField.setValue(field.getDescription());
		this.titleField.setValue(field.getTitle());
		this.keyCodeField.setValue(field.getKeyCode());
		field.getEnums();
		this.typesField.setValue(field.getValueType());
		if (field.getValueType() == VortexEyeMetadataField.VALUE_TYPE_INT) {
			if (field.getMaxValue() != null)
				this.maxValueField.setValue(OH.toString(field.getMaxValue().intValue()));
			if (field.getMinValue() != null)
				this.minValueField.setValue(OH.toString(field.getMinValue().intValue()));
		} else {
			this.maxValueField.setValue(OH.toString(field.getMaxValue()));
			this.minValueField.setValue(OH.toString(field.getMinValue()));
		}
		if (field.getMaxLength() != null)
			this.maxLengthField.setValue(field.getMaxLength());
		this.enumsField.setValue(SH.isnt(field.getEnums()) ? "" : SH.joinMap('\n', '=', '\\', field.getEnums()));
		updateFields();
		setIconToCopy();
	}
	@Override
	protected void onUserPressedButton(FormPortletButton button) {
		long types = 0;
		if (machinesCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_MACHINE;
		if (deploymentsCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_DEPLOYMENT;
		if (buildProceduresCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_BUILD_PROCEDURE;
		if (expectationsCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_EXPECTATION;
		if (dataserversCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_DB_SERVER;
		if (deploymentSetsCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_DEPLOYMENT_SET;
		if (buildResultsCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_BUILD_RESULT;
		if (backupDestinationCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_BACKUP_DESTINATION;
		if (scheduledTaskCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_SCHEDULED_TASK;
		if (backupCheckbox.getBooleanValue())
			types |= VortexAgentEntity.MASK_TYPE_BACKUP;
		if (types == 0L) {
			getManager().showAlert("Must apply to at least one type of entity");
			return;
		}
		VortexEyeManageMetadataFieldRequest req = nw(VortexEyeManageMetadataFieldRequest.class);
		VortexEyeMetadataField metadataField = nw(VortexEyeMetadataField.class);
		metadataField.setTargetTypes(types);
		//metadataField.setRequired(requiredField.getBooleanValue());
		metadataField.setDescription(descriptionField.getValue());
		metadataField.setKeyCode(keyCodeField.getValue());
		metadataField.setTitle(titleField.getValue());
		metadataField.setValueType(typesField.getValue());
		switch (typesField.getValue()) {
			case VortexEyeMetadataField.VALUE_TYPE_STRING:
				if (maxLengthField.getValue() != null)
					metadataField.setMaxLength((byte) maxLengthField.getIntValue().intValue());
				break;
			case VortexEyeMetadataField.VALUE_TYPE_BOOLEAN:
				break;
			case VortexEyeMetadataField.VALUE_TYPE_DOUBLE:
				if (minValueField.getValue() != null)
					metadataField.setMinValue(Double.parseDouble(minValueField.getValue()));
				if (maxValueField.getValue() != null)
					metadataField.setMaxValue(Double.parseDouble(maxValueField.getValue()));
				break;
			case VortexEyeMetadataField.VALUE_TYPE_INT:
				if (minValueField.getValue() != null)
					metadataField.setMinValue((double) Integer.parseInt(minValueField.getValue()));
				if (maxValueField.getValue() != null)
					metadataField.setMaxValue((double) Integer.parseInt(maxValueField.getValue()));
				break;
			case VortexEyeMetadataField.VALUE_TYPE_ENUM:
				metadataField.setEnums(SH.splitToMap('\n', '=', enumsField.getValue().trim()));
				break;
		}
		//if (requiredField.getBooleanValue()) {
		//req.setDefaultValue(defaultValueField.getValue());
		//}
		if (toEdit != null) {
			metadataField.setRevision(toEdit.getRevision());
			metadataField.setId(toEdit.getId());
			metadataField.setKeyCode(toEdit.getKeyCode());
		}
		req.setMetadataField(metadataField);
		service.sendRequestToBackend(getPortletId(), req);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> result) {
		VortexEyeManageMetadataFieldResponse response = (VortexEyeManageMetadataFieldResponse) result.getAction();
		if (response.getOk())
			close();
		//else
		//getManager().showAlert(response.getMessage());
	}

}
